package prerna.sablecc2.reactor.insights;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import prerna.auth.utils.AbstractSecurityUtils;
import prerna.auth.utils.SecurityQueryUtils;
import prerna.cluster.util.AZClient;
import prerna.cluster.util.ClusterUtil;
import prerna.engine.api.IEngine;
import prerna.engine.impl.SmssUtilities;
import prerna.nameserver.utility.MasterDatabaseUtility;
import prerna.om.Insight;
import prerna.om.InsightCacheUtility;
import prerna.om.InsightPanel;
import prerna.om.InsightStore;
import prerna.om.OldInsight;
import prerna.sablecc2.PixelRunner;
import prerna.sablecc2.PixelUtility;
import prerna.sablecc2.om.GenRowStruct;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.om.execptions.SemossPixelException;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.util.Constants;
import prerna.util.DIHelper;
import prerna.util.Utility;
import prerna.util.insight.InsightUtility;
import prerna.util.usertracking.UserTrackerFactory;

public class OpenInsightReactor extends AbstractInsightReactor {
	
	private static final String CLASS_NAME = OpenInsightReactor.class.getName();
	
	private static final String DIR_SEPARATOR = java.nio.file.FileSystems.getDefault().getSeparator();
	
	public OpenInsightReactor() {
		this.keysToGet = new String[]{ReactorKeysEnum.APP.getKey(), ReactorKeysEnum.ID.getKey(), ReactorKeysEnum.PARAM_KEY.getKey(), ReactorKeysEnum.ADDITIONAL_PIXELS.getKey()};
	}

	@Override
	public NounMetadata execute() {
		Logger logger = getLogger(CLASS_NAME);
		
		// get the recipe for the insight
		// need the engine name and id that has the recipe
		String appId = getApp();
		if(appId == null) {
			throw new IllegalArgumentException("Need to input the app name");
		}
		String rdbmsId = getRdbmsId();
		if(rdbmsId == null) {
			throw new IllegalArgumentException("Need to input the id for the insight");
		}
		
		if(AbstractSecurityUtils.securityEnabled()) {
			appId = SecurityQueryUtils.testUserEngineIdForAlias(this.insight.getUser(), appId);
			if(!SecurityQueryUtils.userCanViewInsight(this.insight.getUser(), appId, rdbmsId)) {
				throw new IllegalArgumentException("User does not have access to this insight");
			}
		} else {
			appId = MasterDatabaseUtility.testEngineIdIfAlias(appId);
		}
		
		Object params = getExecutionParams();
		List<String> additionalPixels = getAdditionalPixels();

		// get the engine so i can get the new insight
		IEngine engine = Utility.getEngine(appId);
		if(engine == null) {
			throw new IllegalArgumentException("Cannot find app = " + appId);
		}
		Insight newInsight = null;
		try {
			List<Insight> in = engine.getInsight(rdbmsId + "");
			newInsight = in.get(0);
		} catch (ArrayIndexOutOfBoundsException e) {
			if (ClusterUtil.IS_CLUSTER) {
				try {
					logger.info("Pulling app from cloud storage, appid=" + appId);
					AZClient.getInstance().updateApp(appId);
				} catch (IOException | InterruptedException e1) {
					NounMetadata noun = new NounMetadata("Failed to sync this new insight from cloud storage", PixelDataType.CONST_STRING, PixelOperationType.ERROR);
					SemossPixelException err = new SemossPixelException(noun);
					err.setContinueThreadOfExecution(false);
					throw err;
				}
			}
			try {
				List<Insight> in = engine.getInsight(rdbmsId + "");
				newInsight = in.get(0);
			} catch (ArrayIndexOutOfBoundsException e2) {
				NounMetadata noun = new NounMetadata("Insight does not exist.", PixelDataType.CONST_STRING, PixelOperationType.ERROR);
				SemossPixelException err = new SemossPixelException(noun);
				err.setContinueThreadOfExecution(false);
				throw err;
			}
		}
		
		InsightUtility.transferDefaultVars(this.insight, newInsight);
		
		// OLD INSIGHT
		if(newInsight instanceof OldInsight) {
			Map<String, Object> insightMap = new HashMap<String, Object>();
			// return to the FE the recipe
			insightMap.put("name", newInsight.getInsightName());
			// keys below match those in solr
			insightMap.put("app_id", newInsight.getEngineId());
			insightMap.put("app_name", newInsight.getEngineName());
			insightMap.put("app_insight_id", newInsight.getRdbmsId());
			
			// LEGACY PARAMS
			insightMap.put("core_engine", newInsight.getEngineName());
			insightMap.put("core_engine_id", newInsight.getRdbmsId());
			
			insightMap.put("layout", ((OldInsight) newInsight).getOutput());
			return new NounMetadata(insightMap, PixelDataType.CUSTOM_DATA_STRUCTURE, PixelOperationType.OLD_INSIGHT);
		}
		
		// yay... not legacy
		boolean isParam = params != null || PixelUtility.hasParam(newInsight.getPixelRecipe());
		boolean isDashoard = PixelUtility.isDashboard(newInsight.getPixelRecipe());
		
		// if not param or dashboard, we can try to load a cache
		// do we have a cached insight we can use
		boolean hasCache = false;
		Insight cachedInsight = null;
		if(!isParam && !isDashoard) {
			try {
				cachedInsight = getCachedInsight(newInsight.getEngineId(), newInsight.getEngineName(), newInsight.getRdbmsId());
				if(cachedInsight != null) {
					hasCache = true;
					cachedInsight.setInsightName(newInsight.getInsightName());
					newInsight = cachedInsight;
					// need to transfer again
					InsightUtility.transferDefaultVars(this.insight, newInsight);
				}
			} catch (IOException e) {
				hasCache = true;
				e.printStackTrace();
			}
		}
		
		// add the insight to the insight store
		InsightStore.getInstance().put(newInsight);
		InsightStore.getInstance().addToSessionHash(getSessionId(), newInsight.getInsightId());
		// set user 
		newInsight.setUser(this.insight.getUser());
		
		// get the insight output
		PixelRunner runner = null;
		NounMetadata additionalMeta = null;
		if(hasCache && cachedInsight == null) {
			// this means we have a cache
			// but there was an error with it
			InsightCacheUtility.deleteCache(newInsight.getEngineId(), newInsight.getEngineName(), rdbmsId);
			additionalMeta = new NounMetadata("An error occured with retrieving the cache for this insight. Deleted the cache and recreated", 
					PixelDataType.CONST_STRING, PixelOperationType.WARNING);
		} else if(hasCache) {
			try {
				runner = getCachedInsightData(cachedInsight);
			} catch (IOException e) {
				InsightCacheUtility.deleteCache(newInsight.getEngineId(), newInsight.getEngineName(), rdbmsId);
				additionalMeta = new NounMetadata("An error occured with retrieving the cache for this insight. Deleted the cache and recreated", 
						PixelDataType.CONST_STRING, PixelOperationType.WARNING);
				e.printStackTrace();
			}
		} 

		if(runner == null) {
			logger.info("Running insight");
			runner = runNewInsight(newInsight, additionalPixels);
			logger.info("Done running insight");
			logger.info("Painting results");
//			now I want to cache the insight
			if(!isParam && !isDashoard) {
				try {
					InsightCacheUtility.cacheInsight(newInsight);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		// update the universal view count
		GlobalInsightCountUpdater.getInstance().addToQueue(appId, rdbmsId);

		// NEW TRACKING
		UserTrackerFactory.getInstance().trackInsightExecution(newInsight);
		
		// return the recipe steps
		Map<String, Object> runnerWraper = new HashMap<String, Object>();
		runnerWraper.put("runner", runner);
		runnerWraper.put("params", params);
		runnerWraper.put("additionalPixels", additionalPixels);
		NounMetadata noun = new NounMetadata(runnerWraper, PixelDataType.PIXEL_RUNNER, PixelOperationType.OPEN_SAVED_INSIGHT);
		if(additionalMeta != null) {
			noun.addAdditionalReturn(additionalMeta);
		}
		return noun;
	}

	protected List<String> getAdditionalPixels() {
		GenRowStruct additionalPixels = this.store.getNoun(keysToGet[3]);
		if(additionalPixels != null && !additionalPixels.isEmpty()) {
			List<String> pixels = new Vector<String>();
			int size = additionalPixels.size();
			for(int i = 0; i < size; i++) {
				pixels.add(additionalPixels.get(i).toString());
			}
			return pixels;
		}

		// no additional pixels to run
		return null;
	}
	
	/**
	 * Run an insight with the additional pixels
	 * @param insight
	 * @param additionalPixels
	 * @return
	 */
	protected PixelRunner runNewInsight(Insight insight, List<String> additionalPixels) {
		// add additional pixels if necessary
		if(additionalPixels != null && !additionalPixels.isEmpty()) {
			// just add it directly to the pixel list
			// and the reRunPiexelInsight will do its job
			insight.getPixelRecipe().addAll(additionalPixels);
		}
		
		// rerun the insight
		PixelRunner runner = insight.reRunPixelInsight();
		return runner;
	}
	
	/**
	 * Get the cached insight
	 * @param engineId
	 * @param insightId
	 * @return
	 */
	protected Insight getCachedInsight(String engineId, String engineName, String insightId) throws IOException {
		Insight insight = null;
		
		String baseFolder = DIHelper.getInstance().getProperty(Constants.BASE_FOLDER);
		String insightZipLoc = baseFolder + DIR_SEPARATOR + "db" + DIR_SEPARATOR + SmssUtilities.getUniqueName(engineName, engineId) 
					+ DIR_SEPARATOR + "version" + DIR_SEPARATOR + insightId + DIR_SEPARATOR + InsightCacheUtility.INSIGHT_ZIP;
		File insightZip = new File(insightZipLoc);
		if(!insightZip.exists()) {
			// just return null
			return insight;
		}
		
		insight = InsightCacheUtility.readInsightCache(insightZip);
		return insight;
	}
	
	/**
	 * Get cached insight view data
	 * @param cachedInsight
	 * @return
	 */
	protected PixelRunner getCachedInsightData(Insight insight) throws IOException {
		// I will create a temp insight
		// so that I can mock the output as if this was run properly
		Insight tempInsight = new Insight(insight.getEngineId(), insight.getEngineName(), insight.getRdbmsId());
		tempInsight.setInsightId(insight.getInsightId());
		tempInsight.setInsightName(insight.getInsightName());
		tempInsight.setVarStore(insight.getVarStore());
		
		PixelRunner runner = new PixelRunner();
		// i need to mock adding all the panels
		Map<String, InsightPanel> panels = insight.getInsightPanels();
		for(String panelId : panels.keySet()) {
			runner.runPixel("AddPanel(" + panelId + ");", tempInsight);
		}
		
		// i am going to run the panel view at the end again
		// because if we clone to get a new panel
		// and then switch it at another point
		Map<String, String> panelToPanelView = new HashMap<String, String>();
		// set the view to a vizual to paint the data
		for(String panelId : panels.keySet()) {
			InsightPanel panel = panels.get(panelId);
			String panelView = panel.getPanelView();
			String panelViewOptions = panel.getPanelActiveViewOptions();
			
			String pixelToRun = "";
			if(panelViewOptions != null && !panelViewOptions.isEmpty()) {
				pixelToRun = "Panel(" + panelId + ") | SetPanelView(\"" + panelView + "\", \"" + Utility.encodeURIComponent(panelViewOptions) + "\");";
			} else {
				pixelToRun = "Panel(" + panelId + ") | SetPanelView(\"" + panelView + "\");";
			}
			
			panelToPanelView.put(panelId, pixelToRun);
			runner.runPixel(pixelToRun, tempInsight);
		}
		
		// send the view data
		Map<String, Object> viewData = InsightCacheUtility.getCachedInsightViewData(insight);
		List<Object> pixelReturn = (List<Object>) viewData.get("pixelReturn");
		runner.addResult("CACHED_DATA", new NounMetadata(pixelReturn, PixelDataType.CACHED_PIXEL_RUNNER), true);
		
		// now we will set the actual panels
		for(String panelId : panels.keySet()) {
			tempInsight.addNewInsightPanel(panels.get(panelId));
		}
		
		// and finally return all the panel ornaments
		for(String panelId : panels.keySet()) {
			String userDefinedLabel = "";
			String panelLabel = panels.get(panelId).getPanelLabel();
			if(panelLabel != null) {
				userDefinedLabel = "Panel(" + panelId + ")|SetPanelLabel(\"" + panelLabel + "\");";
			}
			runner.runPixel(panelToPanelView.get(panelId)
					+ userDefinedLabel
					+ "Panel(" + panelId + ") | RetrievePanelOrnaments();"
					+ "Panel(" + panelId + ") | RetrievePanelEvents();"
					+ "Panel(" + panelId + ") | RetrievePanelComment();"
					, tempInsight);
		}
		
		// we need to reset the recipe
		tempInsight.setPixelRecipe(insight.getPixelRecipe());
		
		return runner;
	}
	
	
}