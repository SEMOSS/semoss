package prerna.om;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import prerna.algorithm.api.ITableDataFrame;
import prerna.cache.CacheFactory;
import prerna.ds.H2.H2Frame;
import prerna.ds.H2.H2Joiner;
import prerna.ds.H2.H2Builder.Comparator;
import prerna.sablecc.PKQLEnum;
import prerna.sablecc.PKQLEnum.PKQLReactor;
import prerna.ui.components.playsheets.datamakers.DataMakerComponent;
import prerna.ui.components.playsheets.datamakers.IDataMaker;
import prerna.ui.components.playsheets.datamakers.ISEMOSSAction;
import prerna.ui.components.playsheets.datamakers.ISEMOSSTransformation;
import prerna.ui.helpers.InsightCreateRunner;

public class Dashboard implements IDataMaker {

	private static final Logger LOGGER = LogManager.getLogger(Dashboard.class.getName());
	Map<String, Insight> attachedInsights = new LinkedHashMap<>(4);
	private static String delimiter = ":::";
	private String insightID;
	private H2Joiner joiner;
		
	//mapping from 2 frames to joinColumns
	//InsightID1 + delimiter + InsightID2 ->[joinCols1[], joinCols2[]]}
	private Map<String, List<String[]>> joinColsMap;
	
	private String viewTableName;
	
	//this maps the insightID to the translation map for that insight
	private Map<String, Map<String, String>> translationMap;
	
	//will need this when same insight can be on two different dashboards
	private Map<String, Map<Comparator, Set<Object>>> filterHash = new HashMap<>();
	
	public Dashboard() {
		joiner = new H2Joiner(this);
	}
	
	@Override
	public void processDataMakerComponent(DataMakerComponent component) {
		
		Insight insight = null;
		String insightID = component.getRdbmsId();
		String engine = component.getEngine().getEngineName();
		
		String insightKey = engine+delimiter+insightID;
		if(insightID != null) {
//			Insight insight = InsightStore.getInstance().get(insightID);
//			String engine = insight.getEngineName();
//			String id = insight.getRdbmsId();
//			String insightKey = engine+delimiter+id;
		
			//i know this is duplicative to the above code but we want to ensure the insight we are running on is in fact joined to this dashboard
			insight = this.attachedInsights.get(insightKey);
			insight.getDataMaker().processDataMakerComponent(component);
		} else {
			//if join then need to add open insight and param pkql
			processPreTransformations(component, component.getPreTrans());
			processPostTransformations(component, component.getPostTrans());
			processActions(component, component.getActions());
			
		}
	}

	@Override
	public void processPreTransformations(DataMakerComponent dmc, List<ISEMOSSTransformation> transforms) {
//		LOGGER.info("We are processing " + transforms.size() + " pre transformations");
//		for(ISEMOSSTransformation transform : transforms) { 
//			transform.setDataMakers(this);
//			transform.setDataMakerComponent(dmc);
//			transform.runMethod();
//		}
	}
	
	@Override
	public void processPostTransformations(DataMakerComponent dmc, List<ISEMOSSTransformation> transforms, IDataMaker... dataFrame) {
		LOGGER.info("We are processing " + transforms.size() + " post transformations");
		// if other data frames present, create new array with this at position 0
		IDataMaker[] extendedArray = new IDataMaker[]{this};
		if(dataFrame.length > 0) {
			extendedArray = new IDataMaker[dataFrame.length + 1];
			extendedArray[0] =  this;
			for(int i = 0; i < dataFrame.length; i++) {
				extendedArray[i+1] = dataFrame[i];
			}
		}
		for(ISEMOSSTransformation transform : transforms){
			//get the id from the pkql transformation and run in on that join
			transform.setDataMakers(extendedArray);
			transform.setDataMakerComponent(dmc);
			transform.runMethod();
		}
		
		for(String key : attachedInsights.keySet()) {
			Insight insight = attachedInsights.get(key);
			insight.setParentInsight(InsightStore.getInstance().get(this.insightID));
//			insight.getDataMaker().processPostTransformations(dmc, transforms, dataFrame);
		}
	}


	@Override
	/**
	 * Return the data maker output for each attached insight
	 * 
	 * TODO : Take into account selectors
	 */
	public Map<String, Object> getDataMakerOutput(String... selectors) {
		Map<String, Object> returnHash = new HashMap<>();
		for(String insightID : attachedInsights.keySet()) {
			Insight insight = attachedInsights.get(insightID);
			ITableDataFrame frame = (ITableDataFrame)insight.getDataMaker();
			Object output = insight.getDataMaker().getDataMakerOutput(frame.getColumnHeaders());
			returnHash.put(insight.getInsightID(), output);
		}
		return returnHash;
	}
	
	public List<Object> getInsightData() {
		
		
		
		return null;
	}

	@Override
	public List<Object> processActions(DataMakerComponent dmc, List<ISEMOSSAction> actions, IDataMaker... dataMaker) {
		return null;
	}

	@Override
	public List<Object> getActionOutput() {
		return null;
	}

	@Override
	public Map<String, String> getScriptReactors() {
		Map<String, String> reactorNames = new HashMap<>();
		reactorNames.put(PKQLEnum.DASHBOARD_JOIN, "prerna.sablecc.DashboardJoinReactor");
		reactorNames.put(PKQLEnum.OPEN_DATA, "prerna.sablecc.OpenDataReactor");
		reactorNames.put(PKQLReactor.VAR.toString(), "prerna.sablecc.VarReactor");
		reactorNames.put(PKQLReactor.INPUT.toString(), "prerna.sablecc.InputReactor");
		reactorNames.put(PKQLEnum.COL_CSV, "prerna.sablecc.ColCsvReactor");
		return reactorNames;
	}

	@Override
	public void updateDataId() {
		
	}

	@Override
	public int getDataId() {
		return 0;
	}

	@Override
	public void setUserId(String userId) {
		
	}

	@Override
	public String getUserId() {
		return null;
	}

	@Override
	public String getDataMakerName() {
		return "DashBoard";
	}
	
	private void addInsight(Insight insight) {
		this.attachedInsights.put(insight.getInsightID(), insight);
	}
	
	public List<Insight> getInsights() {
		List<Insight> insightList = new ArrayList<>();
		for(String key : attachedInsights.keySet()) {
			insightList.add(attachedInsights.get(key));
		}
		return insightList;
	}
	
	public void setInsightID(String insightID) {
		this.insightID = insightID;
	}

	@Override
	public void resetDataId() {
		
	}
	
	/************************************* JOINING LOGIC **************************************/
	
	/**
	 * 
	 * @param insights
	 * @param joinColumns
	 * 
	 * first doing the case where we have all unjoined insights
	 */
	public void joinInsights(List<Insight> insights, List<List<String>> joinColumns) {
		H2Frame[] frames = new H2Frame[insights.size()];
		for(int i = 0; i < insights.size(); i++) {
			Insight insight = insights.get(i);
			IDataMaker dm = insight.getDataMaker();
			
			if(dm instanceof H2Frame) {
				frames[i] = (H2Frame)dm;
			} else {
				throw new IllegalArgumentException("Cannot join Insight "+insight.getInsightID()+": Does not have H2Frame as datamaker");
			}
		}
		
		try {
			joiner.joinFrames(joinColumns, frames);
			for(Insight insight : insights) {
				insight.getDataMaker().updateDataId();
				addInsight(insight);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void unJoinInsights(Insight... insights) {
		H2Frame[] frames = new H2Frame[insights.length];
		for(int i = 0; i < insights.length; i++) {
			Insight insight = insights[i];
			frames[i] = (H2Frame)insight.getDataMaker();
			String insightID = insight.getInsightID();
			this.attachedInsights.remove(insightID);
		}
		joiner.unJoinFrame(frames);
	}
	
	public List<String> getSaveRecipe() {
		
		List<String> saveRecipe = new ArrayList<>();
		Map<String, String> varHash = new HashMap<>();
		
		int i = 1; 
		for(String insightId : attachedInsights.keySet()) {
			
			Insight insight = attachedInsights.get(insightId);
			String nextVar = "insightVar"+i;
			String nextPkql = createVarPkql(nextVar, insight.getEngineName(), insight.getDatabaseID());
			
			varHash.put(nextVar, insightId);
			i++;
			saveRecipe.add(nextPkql);
		}
		
		return saveRecipe;
	}
	
	private String createVarPkql(String varName, String engine, String id) {
		varName = "v:"+varName;
		String varPkql = varName+" = data.open('"+engine+"', '"+id+"');";
		return varPkql;
	}
	
	/************************************* JOINING LOGIC **************************************/
}
