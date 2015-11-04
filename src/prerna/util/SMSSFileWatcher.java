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
package prerna.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;

import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;

import prerna.engine.api.IEngine;
import prerna.engine.api.ISelectStatement;
import prerna.engine.api.ISelectWrapper;
import prerna.engine.impl.AbstractEngine;
import prerna.nameserver.AddToMasterDB;
import prerna.nameserver.DeleteFromMasterDB;
import prerna.nameserver.MasterDBHelper;
import prerna.poi.main.BaseDatabaseCreator;
import prerna.rdf.engine.wrappers.WrapperManager;

/**
 * This class opens a thread and watches a specific SMSS file.
 */
public class SMSSFileWatcher extends AbstractFileWatcher {

	/**
	 * Processes SMSS files.
	 * 
	 * @param Name
	 *            of the file.
	 */
	@Override
	public void process(String fileName) {
		loadNewDB(fileName, false);
	}

	/**
	 * Returns an array of strings naming the files in the directory. Goes through list and loads an existing database.
	 */
	public void loadExistingDB(String fileName, boolean isLocal) {
		loadNewDB(fileName, isLocal);
	}

	/**
	 * Loads a new database by setting a specific engine with associated properties.
	 * 
	 * @param Specifies
	 *            properties to load
	 */
	public void loadNewDB(String newFile, boolean isLocal) {
		FileInputStream fileIn = null;
		try {
			Properties prop = new Properties();
			fileIn = new FileInputStream(folderToWatch + "/" + newFile);
			prop.load(fileIn);

			Utility.loadEngine(folderToWatch + "/" + newFile, prop);
			String engineName = prop.getProperty(Constants.ENGINE);
			// check if hidden database
			boolean hidden = (prop.getProperty(Constants.HIDDEN_DATABASE) != null && Boolean.parseBoolean(prop.getProperty(Constants.HIDDEN_DATABASE)));
			if(isLocal && !hidden) {
				JList list = (JList) DIHelper.getInstance().getLocalProp(Constants.REPO_LIST);
				DefaultListModel listModel = (DefaultListModel) list.getModel();
				listModel.addElement(engineName);
				// list.setModel(listModel);
				list.setSelectedIndex(0);
				list.repaint();
			} else if(!isLocal && !hidden) {
				JList list = (JList) DIHelper.getInstance().getLocalProp(Constants.REPO_LIST);
				DefaultListModel listModel = (DefaultListModel) list.getModel();
				listModel.addElement(engineName);
				// list.setModel(listModel);
				list.setSelectedIndex(0);
				list.repaint();
				addToLocalMaster(engineName);
			} else if(!isLocal){ // never add local master to itself...
				DeleteFromMasterDB deleter = new DeleteFromMasterDB(Constants.LOCAL_MASTER_DB_NAME);
				deleter.deleteEngine(engineName);
			}

			// initialize combo box for cost db update
			JComboBox changedDBComboBox = (JComboBox) DIHelper.getInstance().getLocalProp(Constants.CHANGED_DB_COMBOBOX);
			DefaultComboBoxModel changedDBComboBoxModel = (DefaultComboBoxModel) changedDBComboBox.getModel();
			changedDBComboBoxModel.addElement(engineName);
			changedDBComboBox.repaint();

			JComboBox costDBComboBox = (JComboBox) DIHelper.getInstance().getLocalProp(Constants.COST_DB_COMBOBOX);
			DefaultComboBoxModel costDBComboBoxModel = (DefaultComboBoxModel) costDBComboBox.getModel();
			costDBComboBoxModel.addElement(engineName);
			costDBComboBox.repaint();

			// initialize combo box for aggregating tap services into tap cost
			JComboBox selectTapCoreForAggregationComboBox = (JComboBox) DIHelper.getInstance().getLocalProp(
					ConstantsTAP.TAP_SERVICES_AGGREGATION_CORE_COMBO_BOX);
			DefaultComboBoxModel selectTapCoreForAggregationComboBoxModel = (DefaultComboBoxModel) selectTapCoreForAggregationComboBox.getModel();
			selectTapCoreForAggregationComboBoxModel.addElement(engineName);
			selectTapCoreForAggregationComboBox.repaint();

			JComboBox selectTapServicesComboBox = (JComboBox) DIHelper.getInstance().getLocalProp(
					ConstantsTAP.TAP_SERVICES_AGGREGATION_SERVICE_COMBO_BOX);
			DefaultComboBoxModel selectTapServicesComboBoxModel = (DefaultComboBoxModel) selectTapServicesComboBox.getModel();
			selectTapServicesComboBoxModel.addElement(engineName);
			selectTapServicesComboBox.repaint();

			// initialize combo box for creating future interface db
			JComboBox selectHRCoreForFutureInterfaceComboBox = (JComboBox) DIHelper.getInstance().getLocalProp(
					ConstantsTAP.HR_CORE_FUTURE_INTERFACE_DATABASE_CORE_COMBO_BOX);
			DefaultComboBoxModel selectHRCoreForFutureInterfaceComboBoxModel = (DefaultComboBoxModel) selectHRCoreForFutureInterfaceComboBox
					.getModel();
			selectHRCoreForFutureInterfaceComboBoxModel.addElement(engineName);
			selectHRCoreForFutureInterfaceComboBox.repaint();

			JComboBox selectTapFutureInterfaceComboBox = (JComboBox) DIHelper.getInstance().getLocalProp(
					ConstantsTAP.TAP_FUTURE_INTERFACE_DATABASE_COMBO_BOX);
			DefaultComboBoxModel selectTapFutureInterfaceComboBoxModel = (DefaultComboBoxModel) selectTapFutureInterfaceComboBox.getModel();
			selectTapFutureInterfaceComboBoxModel.addElement(engineName);
			selectTapServicesComboBox.repaint();

			JComboBox selectTapFutureCostInterfaceComboBox = (JComboBox) DIHelper.getInstance().getLocalProp(
					ConstantsTAP.TAP_FUTURE_COST_INTERFACE_DATABASE_COMBO_BOX);
			DefaultComboBoxModel selectTapFutureCostInterfaceComboBoxModel = (DefaultComboBoxModel) selectTapFutureCostInterfaceComboBox.getModel();
			selectTapFutureCostInterfaceComboBoxModel.addElement(engineName);
			selectTapFutureCostInterfaceComboBox.repaint();

			// initialize combo box for db comparison
			JComboBox selectNewDBComboBox = (JComboBox) DIHelper.getInstance().getLocalProp(Constants.NEW_DB_COMBOBOX);
			DefaultComboBoxModel selectNewDBComboBoxModel = (DefaultComboBoxModel) selectNewDBComboBox.getModel();
			selectNewDBComboBoxModel.addElement(engineName);
			selectNewDBComboBox.repaint();

			JComboBox selectOldDBComboBox = (JComboBox) DIHelper.getInstance().getLocalProp(Constants.OLD_DB_COMBOBOX);
			DefaultComboBoxModel selectOldDBComboBoxModel = (DefaultComboBoxModel) selectOldDBComboBox.getModel();
			selectOldDBComboBoxModel.addElement(engineName);
			selectOldDBComboBox.repaint();

			// initialize combo box for export db information
			JComboBox exportDataDBComboBox = (JComboBox) DIHelper.getInstance().getLocalProp(Constants.EXPORT_LOAD_SHEET_SOURCE_COMBOBOX);
			DefaultComboBoxModel exportDataDBComboBoxModel = (DefaultComboBoxModel) exportDataDBComboBox.getModel();
			exportDataDBComboBoxModel.addElement(engineName);
			exportDataDBComboBox.repaint();

			// initialize combo box for common subgraph
			JComboBox commonSubgraphComboBox0 = (JComboBox) DIHelper.getInstance().getLocalProp(Constants.COMMON_SUBGRAPH_COMBO_BOX_0);
			DefaultComboBoxModel commonSubgraphComboBox0Model = (DefaultComboBoxModel) commonSubgraphComboBox0.getModel();
			commonSubgraphComboBox0Model.addElement(engineName);
			commonSubgraphComboBox0.repaint();

			JComboBox commonSubgraphComboBox1 = (JComboBox) DIHelper.getInstance().getLocalProp(Constants.COMMON_SUBGRAPH_COMBO_BOX_1);
			DefaultComboBoxModel commonSubgraphComboBox1Model = (DefaultComboBoxModel) commonSubgraphComboBox1.getModel();
			commonSubgraphComboBox1Model.addElement(engineName);
			commonSubgraphComboBox1.repaint();

			// initialize combo box for question modification
			JComboBox questionDatabaseSelector = (JComboBox) DIHelper.getInstance().getLocalProp(Constants.QUESTION_DB_SELECTOR);
			DefaultComboBoxModel questionDatabaseSelectorModel = (DefaultComboBoxModel) questionDatabaseSelector.getModel();
			questionDatabaseSelectorModel.addElement(engineName);
			questionDatabaseSelector.repaint();

			JComboBox autoGenerateQueriesForEngineCombobox = (JComboBox) DIHelper.getInstance().getLocalProp(Constants.AUTO_GENERATE_INSIGHTS_FOR_ENGINE_COMBOBOX);
			DefaultComboBoxModel autoGenerateQueriesForEngineComboboxModel = (DefaultComboBoxModel) autoGenerateQueriesForEngineCombobox.getModel();
			autoGenerateQueriesForEngineComboboxModel.addElement(engineName);
			autoGenerateQueriesForEngineCombobox.repaint();

			JFrame frame2 = (JFrame) DIHelper.getInstance().getLocalProp(Constants.MAIN_FRAME);
			frame2.repaint();

		} catch (IOException ex) {
			// TODO: Specify exception
			ex.printStackTrace();
		} finally {
			try {
				if (fileIn != null)
					fileIn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

//	private void clearLocalDB() {
//		BigDataEngine localDB = (BigDataEngine) DIHelper.getInstance().getLocalProp(Constants.LOCAL_MASTER_DB_NAME);
//		String query = "SELECT DISTINCT ?S ?P ?O WHERE {?S ?P ?O}";
//		List<Object[]> results = new ArrayList<Object[]>();
//		ISelectWrapper wrap = WrapperManager.getInstance().getSWrapper(localDB, query);
//		String[] names = wrap.getVariables();
//		while(wrap.hasNext()) {
//			ISelectStatement ss = wrap.next();
//			boolean isConcept = false;
//			if(ss.getRawVar(names[2]).toString().startsWith("http://")) {
//				isConcept = true;
//			}
//			results.add(new Object[]{ss.getRawVar(names[0]).toString(), ss.getRawVar(names[1]).toString(), ss.getRawVar(names[2]).toString(), isConcept});
//		}
//		
//		for(Object[] row : results) {
//			localDB.doAction(ACTION_TYPE.REMOVE_STATEMENT, row);
//		}
//	}

	private void addToLocalMaster(String engineName) {
		IEngine localMaster = (IEngine) DIHelper.getInstance().getLocalProp(Constants.LOCAL_MASTER_DB_NAME);
		if(localMaster == null) {
			throw new NullPointerException("Unable to find local master database in DIHelper.");
		}
		IEngine engineToAdd = (IEngine) DIHelper.getInstance().getLocalProp(engineName);
		if(engineToAdd == null) {
			throw new NullPointerException("Unable to find engine " + engineName + " in DIHelper.");
		}

		String engineURL = "http://semoss.org/ontologies/Concept/Engine/" + Utility.cleanString(engineName, true);
		String localDbQuery = "SELECT DISTINCT ?TIMESTAMP WHERE {<" + engineURL + "> <" + BaseDatabaseCreator.TIME_KEY + "> ?TIMESTAMP}";
		String engineQuery = "SELECT DISTINCT ?TIMESTAMP WHERE {<" + BaseDatabaseCreator.TIME_URL + "> <" + BaseDatabaseCreator.TIME_KEY + "> ?TIMESTAMP}";
		
		String localDbTimeForEngine = null;
		ISelectWrapper wrapper = WrapperManager.getInstance().getSWrapper(localMaster, localDbQuery);
		String[] names = wrapper.getVariables();
		while(wrapper.hasNext()) {
			ISelectStatement ss = wrapper.next();
			localDbTimeForEngine = ss.getVar(names[0]) + "";
		}
		
		String engineDbTime = null;
		ISelectWrapper wrapper2 = WrapperManager.getInstance().getSWrapper( ((AbstractEngine)engineToAdd).getBaseDataEngine(), engineQuery);
		String[] names2 = wrapper2.getVariables();
		while(wrapper2.hasNext()) {
			ISelectStatement ss = wrapper2.next();
			engineDbTime = ss.getVar(names2[0]) + "";
		}
		
		if(engineDbTime == null) {
			DateFormat dateFormat = BaseDatabaseCreator.getFormatter();
			Calendar cal = Calendar.getInstance();
			engineDbTime = dateFormat.format(cal.getTime());
			((AbstractEngine)engineToAdd).getBaseDataEngine().doAction(IEngine.ACTION_TYPE.ADD_STATEMENT, new Object[]{BaseDatabaseCreator.TIME_URL, BaseDatabaseCreator.TIME_KEY, engineDbTime, false});
			((AbstractEngine)engineToAdd).getBaseDataEngine().commit();
			try {
				((AbstractEngine)engineToAdd).getBaseDataEngine().exportDB();
			} catch (RepositoryException e) {
				e.printStackTrace();
			} catch (RDFHandlerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(localDbTimeForEngine == null) {
			localMaster.doAction(IEngine.ACTION_TYPE.ADD_STATEMENT, new Object[]{engineURL, BaseDatabaseCreator.TIME_KEY, engineDbTime, false});
			
			AddToMasterDB adder = new AddToMasterDB(Constants.LOCAL_MASTER_DB_NAME);
			adder.registerEngineLocal(engineToAdd);
			localMaster.commit();
		} else if(!localDbTimeForEngine.equals(engineDbTime)) {
			localMaster.doAction(IEngine.ACTION_TYPE.REMOVE_STATEMENT, new Object[]{engineURL, BaseDatabaseCreator.TIME_KEY, localDbTimeForEngine, false});
			localMaster.doAction(IEngine.ACTION_TYPE.ADD_STATEMENT, new Object[]{engineURL, BaseDatabaseCreator.TIME_KEY, engineDbTime, false});
			
			//if it has a time stamp, it means it was previously in local master
			//need to delete current information and readd
			DeleteFromMasterDB remover = new DeleteFromMasterDB(Constants.LOCAL_MASTER_DB_NAME);
			remover.deleteEngine(engineName);
			
			AddToMasterDB adder = new AddToMasterDB(Constants.LOCAL_MASTER_DB_NAME);
			adder.registerEngineLocal(engineToAdd);
			localMaster.commit();
		}
	}
	
	/**
	 * Used in the starter class for processing SMSS files.
	 */
	@Override
	public void loadFirst() {
		File dir = new File(folderToWatch);
		String[] fileNames = dir.list(this);
		String localMasterDBName = Constants.LOCAL_MASTER_DB_NAME + ".smss";
		int localMasterIndex = ArrayUtilityMethods.calculateIndexOfArray(fileNames, localMasterDBName);
		if(localMasterIndex != -1) {
			String temp = fileNames[0];
			fileNames[0] = localMasterDBName;
			fileNames[localMasterIndex] = temp;
			localMasterIndex = 0;
		}
		boolean isLocal = false;
		for (int fileIdx = 0; fileIdx < fileNames.length; fileIdx++) {
			if(fileIdx == localMasterIndex) {
				isLocal = true;
			} else {
				isLocal = false;
			}
			try {
				loadExistingDB(fileNames[fileIdx], isLocal);
			} catch (RuntimeException ex) {
				ex.printStackTrace();
				logger.fatal("Engine Failed " + folderToWatch + "/" + fileNames[fileIdx]);
			}
		}

		if(localMasterIndex == 0) {
			// remove unused databases
			IEngine localMaster = (IEngine) DIHelper.getInstance().getLocalProp(Constants.LOCAL_MASTER_DB_NAME);
			List<String> engines = MasterDBHelper.getAllEngines(localMaster);
			DeleteFromMasterDB remover = new DeleteFromMasterDB(Constants.LOCAL_MASTER_DB_NAME);
			for(String engine : engines) {
				if(!ArrayUtilityMethods.arrayContainsValue(fileNames, engine + ".smss")) {
					remover.deleteEngine(engine);
				}
			}
		}
	}

	/**
	 * Processes new SMSS files.
	 */
	@Override
	public void run() {
		logger.info("Starting thread");
		synchronized (monitor) {
			super.run();
		}
	}

}
