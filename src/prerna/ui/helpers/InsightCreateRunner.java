/*******************************************************************************
 * Copyright 2015 Defense Health Agency (DHA)
 *
 * If your use of this software does not include any GPLv2 components:
 * 	Licensed under the Apache License, Version 2.0 (the "License");
 * 	you may not use this file except in compliance with the License.
 * 	You may obtain a copy of the License at
 *
 * 	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * 	Unless required by applicable law or agreed to in writing, software
 * 	distributed under the License is distributed on an "AS IS" BASIS,
 * 	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 	See the License for the specific language governing permissions and
 * 	limitations under the License.
 * ----------------------------------------------------------------------------
 * If your use of this software includes any GPLv2 components:
 * 	This program is free software; you can redistribute it and/or
 * 	modify it under the terms of the GNU General Public License
 * 	as published by the Free Software Foundation; either version 2
 * 	of the License, or (at your option) any later version.
 *
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 *******************************************************************************/
package prerna.ui.helpers;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JDesktopPane;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import prerna.om.Insight;
import prerna.om.InsightStore;
import prerna.rdf.query.builder.QueryBuilderData;
import prerna.ui.components.api.IPlaySheet;
import prerna.ui.components.playsheets.AbstractPlaySheet;
import prerna.ui.components.playsheets.datamakers.DataMakerComponent;
import prerna.ui.components.playsheets.datamakers.IDataMaker;
import prerna.ui.components.playsheets.datamakers.ISEMOSSAction;
import prerna.ui.components.playsheets.datamakers.ISEMOSSTransformation;
import prerna.ui.components.playsheets.datamakers.JoinTransformation;
import prerna.util.Constants;
import prerna.util.DIHelper;
import prerna.util.Utility;

/**
 * This class handles everything involved with creating an insight
 * The creating of an insight currently varies significantly between web and thick
 * Web: use data maker and components to create data, then send to front end
 * Thick: use data maker and components to create data, then set in playsheet and call necessary methods there
 */
public class InsightCreateRunner implements Runnable{

	private static final Logger LOGGER = LogManager.getLogger(InsightCreateRunner.class.getName());
	
	Insight insight= null;
	
	/**
	 * Constructor for PlaysheetCreateRunner.
	 * @param playSheet IPlaySheet
	 */
	public InsightCreateRunner(Insight insight)
	{
		this.insight = insight;
	}
	
	/**
	 * Method run.  Calls the create view method on the local play sheet.
	 */
	@Override
	public void run() {
		IDataMaker dm = createData();
		IPlaySheet playSheet = insight.getPlaySheet();
//		playSheet.setDataMaker(dm);
		Map<String, String> tableDataAlign = insight.getDataTableAlign();
		if(playSheet instanceof AbstractPlaySheet && !(tableDataAlign == null || tableDataAlign.isEmpty())) {
			((AbstractPlaySheet)playSheet).setTableDataAlign(tableDataAlign);
		}
		preparePlaySheet(playSheet, insight);

		if(!insight.getAppend()){
			playSheet.runAnalytics();
			playSheet.processQueryData();
			playSheet.createView();
		}
		else {
			playSheet.runAnalytics();
			playSheet.overlayView();
		}
		
	}
	
	/** 
	 * This creates the data needed for the insight
	 * Get the data maker from the insight
	 * Feed each component one by one into data maker
	 */
	private IDataMaker createData(){
		IDataMaker dm = insight.getDataMaker();
		
		// get the list of data maker components from the insight
		List<DataMakerComponent> dmComps = insight.getDataMakerComponents();
		// logic to append the parameter information that was selected on view into the data maker components
		// this either fills the query if the parameter was saved as a string using @INPUT_NAME@ taxonomy or
		// it fills in the values in a Filtering PreTransformation which appends the metamodel
		insight.appendParamsToDataMakerComponents();
		for(DataMakerComponent dmComp : dmComps){
//			// NOTE: this runs the data maker components directly onto the dm, not through the insight
			if(dmComp.isProcessed()) {
				continue;
			}
			DataMakerComponent copyDmc = dmComp.copy();
			if(copyDmc.getBuilderData() != null) {
				optimizeDataMakerComponents(copyDmc, dmComps);
	    	}
			dm.processDataMakerComponent(copyDmc);
		}
		return dm;
	}
	
	public void optimizeDataMakerComponents(DataMakerComponent copyDmc, List<DataMakerComponent> dmcList) {
		// if this dmc is processed, do nothing
		if(copyDmc.isProcessed()) {
			return;
		}
		
		// note, the dmcList does not contain copies
		int dmcIndex = -1;
		for(int i = 0 ; i < dmcList.size(); i++) {
			if(dmcList.get(i).getId().equals(copyDmc.getId())) {
				dmcIndex = i;
				break;
			}
		}
		// for debugging
		if(dmcIndex == -1) {
			System.out.println("ERROR!!!! dmc is not found. dmc copy did not properly get the index");
		}
		
		// could be empty if it is the first dmc
		if(copyDmc.getPreTrans() == null || copyDmc.getPreTrans().isEmpty() ) {
			// this is the first dmc
		} else {
			List<ISEMOSSTransformation> preTrans = copyDmc.getPreTrans();
			// need to check if this dmc is a join trans that is not inner, if so, need to process by itself
			for(int i = 0; i < preTrans.size(); i++) {
				ISEMOSSTransformation trans = preTrans.get(i);
				if(trans instanceof JoinTransformation) {
					JoinTransformation joinTrans = (JoinTransformation) trans;
					String joinType = (String) joinTrans.getProperties().get(JoinTransformation.JOIN_TYPE);
					if(JoinTransformation.PARTIAL.equals(joinType) || JoinTransformation.OUTER.equals(joinType)) {
						return;
					}
				}
			}
		}
		
		int startIndex = dmcIndex + 1;
		DMC_LOOP : for(int i = startIndex; i < dmcList.size(); i++) {
			// grab the next dmc
			DataMakerComponent nextDmc = dmcList.get(i);
			// when we hit the first dmc that is not the same engine, break
			if(nextDmc.getEngine() != copyDmc.getEngine()) {
				break DMC_LOOP;
			}
			// for every dmc (except the first one which is fine since startIndex always larger than 1) assume first preTrans is a join
			List<ISEMOSSTransformation> nextDmcPreTrans = nextDmc.getPreTrans();
			int joinTransIndex = -1;
			JoinTransformation joinTrans = null;
			for(int j = 0; j < nextDmcPreTrans.size(); j++) {
				ISEMOSSTransformation trans = nextDmcPreTrans.get(j);
				if(trans instanceof JoinTransformation) {
					joinTransIndex = j;
					joinTrans = (JoinTransformation) trans;
				}
			}
			
			Map<String, Object> joinProps = joinTrans.getProperties();
			String joinType = (String) joinProps.get(JoinTransformation.JOIN_TYPE);
			if(joinType == null || JoinTransformation.INNER.equals(joinType)) {
				LOGGER.info("Combining dmc # " + i + " into dmc # " + dmcIndex);
				
				// we need to combine all the query data
				QueryBuilderData thisDmcBuilderData = copyDmc.getBuilderData();
				QueryBuilderData nextDmcBuilderData = nextDmc.getBuilderData();

				// this method should be moved to the builder data itself
				boolean isEligable = thisDmcBuilderData.determineEligibleForCombining(nextDmcBuilderData, joinProps);
				if(!isEligable) {
					break DMC_LOOP;
				}
				thisDmcBuilderData.combineBuilderData(nextDmcBuilderData);
				
				// we need to combine all the preTrans - but need copies
				for(int k = 0; k < nextDmcPreTrans.size(); k++) {
					if(k == joinTransIndex) {
						continue;
					}
					copyDmc.getPreTrans().add(nextDmcPreTrans.get(k).copy());
				}
				
				// we need to combine all the postTrans - but need copies
				List<ISEMOSSTransformation> nextDmcPostTrans = nextDmc.getPostTrans();
				for(int k = 0; k < nextDmcPostTrans.size(); k++) {
					copyDmc.getPostTrans().add(nextDmcPostTrans.get(k).copy());
				}
				
				// we need to combine all the actions - but need copies
				List<ISEMOSSAction> nextDmcActions = nextDmc.getActions();
				for(int k = 0; k < nextDmcActions.size(); k++) {
					copyDmc.getActions().add(nextDmcActions.get(k).copy());
				}
				
				// this sets the boolean in the non-copy dmc list, which gets passed into the copy-dmc
				nextDmc.setProcessed(true);
			} else {
				break DMC_LOOP;
			}
		}
	}
	
	/**
	 * Runs the insight and returns the data table align for FE to view
	 */
	public Map<String, Object> runWeb()
	{
		createData();
		Map<String, String> tableDataAlign = insight.getDataTableAlign();
		
		// previous insights did not save the table data align
		// if it is not present, we get the table data align by setting the data maker in the playsheet and grabbing it
		if(tableDataAlign == null || tableDataAlign.isEmpty()) {
			IPlaySheet playSheet = insight.getPlaySheet();
			tableDataAlign = (Map<String, String>) (((AbstractPlaySheet) playSheet).getDataTableAlign());
			insight.setDataTableAlign(tableDataAlign);
		}
		return insight.getWebData();
	}
	
	private void preparePlaySheet(IPlaySheet playSheet, Insight insight){
		// SET THE DESKTOP PANE
		playSheet.setJDesktopPane((JDesktopPane) DIHelper.getInstance().getLocalProp(Constants.DESKTOP_PANE));
		
		// CREATE INSIGHT ID AND STORE IT
		String insightID = InsightStore.getInstance().put(insight);
		playSheet.setQuestionID(insightID);
		
		// CREATE THE INSIGHT TITLE
		String playSheetTitle = "";
		Map<String, List<Object>> paramHash = insight.getParamHash();
		if(paramHash != null && !paramHash.isEmpty())
		{
			// loops through and appends the selected parameters in the play sheet title
			Iterator<String> enumKey = paramHash.keySet().iterator();
			while (enumKey.hasNext())
			{
				String key = (String) enumKey.next();
				List<Object> value = paramHash.get(key);
				for(int i = 0; i < value.size(); i++) {
					Object val = value.get(i);
					if(val instanceof String || val instanceof Double ) {
						playSheetTitle = playSheetTitle + Utility.getInstanceName(val+"") + " - ";
					}
				}
			}
		}
		String name = insight.getInsightName();
		if (name == null){
			name = "Custom";
		}
		System.out.println("Param Hash is " + paramHash);
		playSheetTitle = playSheetTitle+name.trim();
		playSheet.setTitle(playSheetTitle);
	}
}
