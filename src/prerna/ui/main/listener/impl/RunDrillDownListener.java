/*******************************************************************************
 * Copyright 2014 SEMOSS.ORG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package prerna.ui.main.listener.impl;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import prerna.ui.components.api.IPlaySheet;
import prerna.ui.components.playsheets.ClassifyClusterPlaySheet;
import prerna.ui.components.playsheets.ClusteringVizPlaySheet;
import prerna.ui.helpers.PlaysheetCreateRunner;
import prerna.util.Constants;
import prerna.util.DIHelper;
import prerna.util.QuestionPlaySheetStore;
import prerna.util.Utility;

/**
 * Runs the algorithm selected on the Cluster/Classify playsheet and adds additional tabs. Tied to the button to the ClassifyClusterPlaySheet.
 */
public class RunDrillDownListener extends AbstractListener {
	private static final Logger LOGGER = LogManager.getLogger(RunDrillDownListener.class.getName());
	
	private ClassifyClusterPlaySheet playSheet;
	private Hashtable<String, IPlaySheet> playSheetHash;
	private JComboBox<String> drillDownTabSelectorComboBox;
	private ArrayList<JCheckBox> columnCheckboxes;
	
	/**
	 * Method actionPerformed.
	 * @param e ActionEvent
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		//get the tabName selected
		//use tabName to get the playSheetSelected
		String tabName = drillDownTabSelectorComboBox.getSelectedItem() + "";
		ClusteringVizPlaySheet clusteringPlaySheet = (ClusteringVizPlaySheet) playSheetHash.get(tabName);
		String[] masterNames = clusteringPlaySheet.getMasterNames();
		ArrayList<Object []> masterList = clusteringPlaySheet.getMasterList();
		
		int i;
		Integer numberSelected = 0;
		for(i = 0; i < columnCheckboxes.size(); i++) {
			if(columnCheckboxes.get(i).isSelected())
				numberSelected++;
		}
		
		String[] filteredNames = new String[numberSelected+1];
		filteredNames[0] = masterNames[0];
		int colInd=1;
		for(i = 0; i < columnCheckboxes.size(); i++) {
			if(columnCheckboxes.get(i).isSelected()) {
				filteredNames[colInd] = masterNames[1+i];
				colInd++;
			}
		}
		
		ArrayList<JCheckBox> clusterCheckboxes = playSheet.getClusterCheckboxes();
		ArrayList<Integer> clustersToInclude = new ArrayList<Integer>();
		for(i = 0; i < clusterCheckboxes.size(); i++) {
			JCheckBox checkbox = clusterCheckboxes.get(i);
			if(checkbox.isSelected()) {
				clustersToInclude.add(Integer.parseInt(checkbox.getText()));
			}
		}
		if(clustersToInclude.isEmpty()) {
			Utility.showError("No clusters were selected to drill down on. Please select at least one and retry.");
			return;
		}
		
		int[] clusterAssigned  = clusteringPlaySheet.getClusterAssignment();
		ArrayList<Object[]> newList = new ArrayList<Object[]>();
		for(i = 0; i < clusterAssigned.length; i++) {
			Object[] instanceRow = masterList.get(i);
			int cluster = clusterAssigned[i];
			//check if cluster is to be included
			if(clustersToInclude.contains(cluster)) {
				newList.add(instanceRow);
			}
		}
		
		//take out the clusters we dont care about from the master list...
		//then do the filtering after that.
		ArrayList<Object[]> filteredList = new ArrayList<Object []>();
		for(Object[] row : newList) {
			Object[] filteredRow = new Object[numberSelected+1];
			filteredRow[0] = row[0];//whatever object name we're clustering on
			colInd=1;
			for(i = 0; i < columnCheckboxes.size(); i++) {
				if(columnCheckboxes.get(i).isSelected()) {
					filteredRow[colInd] = row[1+i];
					colInd++;
				}
			}
			filteredList.add(filteredRow);
		}
		
		ClassifyClusterPlaySheet drillDownPlaySheet = new ClassifyClusterPlaySheet();
		String insightID = QuestionPlaySheetStore.getInstance().getIDCount()+". "+playSheet.getTitle();
		QuestionPlaySheetStore.getInstance().put(insightID,  drillDownPlaySheet);
		drillDownPlaySheet.setQuery(playSheet.getQuery());
		drillDownPlaySheet.setRDFEngine(playSheet.engine);
		drillDownPlaySheet.setQuestionID(insightID);
		drillDownPlaySheet.setTitle(playSheet.getTitle());
		drillDownPlaySheet.setData(masterNames, newList);
		
		JDesktopPane pane = (JDesktopPane) DIHelper.getInstance().getLocalProp(Constants.DESKTOP_PANE);
		drillDownPlaySheet.setJDesktopPane(pane);
		
		PlaysheetCreateRunner playRunner = new PlaysheetCreateRunner(drillDownPlaySheet);
		Thread playThread = new Thread(playRunner);
		playThread.start();
		
	}

	/**
	 * Method setView. Sets a JComponent that the listener will access and/or modify when an action event occurs.  
	 * @param view the component that the listener will access
	 */
	@Override
	public void setView(JComponent view) {
		this.playSheet = (ClassifyClusterPlaySheet)view;
		this.columnCheckboxes = playSheet.getColumnCheckboxes();
		this.drillDownTabSelectorComboBox = playSheet.getDrillDownTabSelectorComboBox();
		this.playSheetHash = playSheet.getPlaySheetHash();
	}

}
