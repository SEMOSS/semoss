package prerna.sablecc2.reactor.insights;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrServerException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import prerna.engine.api.IEngine;
import prerna.om.Insight;
import prerna.om.InsightStore;
import prerna.om.OldInsight;
import prerna.sablecc2.om.GenRowStruct;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.solr.SolrIndexEngine;
import prerna.util.Constants;
import prerna.util.DIHelper;
import prerna.util.Utility;
import prerna.util.ga.GATracker;

public class OpenInsightReactor extends AbstractInsightReactor {
	
	public OpenInsightReactor() {
		this.keysToGet = new String[]{ReactorKeysEnum.APP.getKey(), ReactorKeysEnum.ID.getKey(), ReactorKeysEnum.PARAM_KEY.getKey(), ReactorKeysEnum.ADDITIONAL_PIXELS.getKey()};
	}

	@Override
	public NounMetadata execute() {
		// set the existing insight to the saved insight

		// get the recipe for the insight
		// need the engine name and id that has the recipe
		String appName = getApp();
		if(appName == null) {
			throw new IllegalArgumentException("Need to input the app name");
		}
		String rdbmsId = getRdbmsId();
		if(rdbmsId == null) {
			throw new IllegalArgumentException("Need to input the id for the insight");
		}
		Object params = getParams();
		List<String> additionalPixels = getAdditionalPixels();

		// get the engine so i can get the new insight
		IEngine engine = Utility.getEngine(appName);
		if(engine == null) {
			throw new IllegalArgumentException("Cannot find app = " + appName);
		}
		List<Insight> in = engine.getInsight(rdbmsId + "");
		Insight newInsight = in.get(0);

		// OLD INSIGHT
		if(newInsight instanceof OldInsight) {
			Map<String, Object> insightMap = new HashMap<String, Object>();
			// return to the FE the recipe
			insightMap.put("name", newInsight.getInsightName());
			// keys below match those in solr
			insightMap.put("core_engine", newInsight.getEngineName());
			insightMap.put("core_engine_id", newInsight.getRdbmsId());
			insightMap.put("layout", ((OldInsight) newInsight).getOutput());
			return new NounMetadata(insightMap, PixelDataType.CUSTOM_DATA_STRUCTURE, PixelOperationType.OLD_INSIGHT);
		}
		
		// yay... not legacy
		// add the insight to the insight store
		InsightStore.getInstance().put(newInsight);

		// get the insight output
		Map<String, Object> insightData = null;
		// add additional pixels if necessary
		if(additionalPixels != null && !additionalPixels.isEmpty()) {
			// just add it directly to the pixel list
			// and the reRunPiexelInsight will do its job
			newInsight.getPixelRecipe().addAll(additionalPixels);
			// get the insight data
			// note, we do not cache when there are the additional pixels
			insightData = newInsight.reRunPixelInsight();
		} else {
//			insightData = getCachedData(appName, rdbmsId);
//			if(insightData == null) {
				insightData = newInsight.reRunPixelInsight();
//				cacheInsightData(appName, rdbmsId, insightData);
//			}
		}
		
		Map<String, Object> insightMap = new HashMap<String, Object>();
		// return to the FE the recipe
		insightMap.put("name", newInsight.getInsightName());
		// keys below match those in solr
		insightMap.put("core_engine", newInsight.getEngineName());
		insightMap.put("core_engine_id", newInsight.getRdbmsId());
		insightMap.put("insightData", insightData);
		insightMap.put("params", params);

		// update the solr universal view count
		try {
			SolrIndexEngine.getInstance().updateViewedInsight(appName + "_" + rdbmsId);
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | SolrServerException
				| IOException e) {
			e.printStackTrace();
		}

		// track GA data
		GATracker.getInstance().trackInsightExecution(this.insight, "openinsight", appName, rdbmsId, newInsight.getInsightName());
		
		// return the recipe steps
		return new NounMetadata(insightMap, PixelDataType.CUSTOM_DATA_STRUCTURE, PixelOperationType.OPEN_SAVED_INSIGHT);
	}

	private List<String> getAdditionalPixels() {
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
	
//	/**
//	 * Cache the insight data in the version folder location
//	 * @param appName
//	 * @param rdbmsId
//	 * @param insightData
//	 */
//	private void cacheInsightData(String appName, String rdbmsId, Map<String, Object> insightData) {
//		// get the file location
//		String cacheLoc = DIHelper.getInstance().getProperty(Constants.BASE_FOLDER);
//		cacheLoc += "\\" + Constants.DB + "\\" + appName + "\\version\\" + rdbmsId + "\\response.json";
//		
//		File cacheFile = new File(cacheLoc);
//		if(cacheFile.exists()) {
//			// if it exists, delete it
//			cacheFile.delete();
//		}
//		
//		// write the data to the file
//		Gson gson = new Gson();
//		try {
//			FileUtils.writeStringToFile(cacheFile, gson.toJson(insightData));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * Get the cached insight data
//	 * Return null if there is none
//	 * @param appName
//	 * @param rdbmsId
//	 * @return
//	 */
//	private Map<String, Object> getCachedData(String appName, String rdbmsId) {
//		String cacheLoc = DIHelper.getInstance().getProperty(Constants.BASE_FOLDER);
//		cacheLoc += "\\" + Constants.DB + "\\" + appName + "\\version\\" + rdbmsId + "\\response.json";
//		
//		File cacheFile = new File(cacheLoc);
//		if(!cacheFile.exists()) {
//			return null;
//		}
//		
//		
//		Gson gson = new Gson();
//		try {
//			String responseStr = FileUtils.readFileToString(cacheFile);
//			return gson.fromJson(responseStr, new TypeToken<Map<String, Object>>() {}.getType());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		return null;
//	}
	
	
}