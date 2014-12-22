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
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import prerna.algorithm.weka.impl.WekaClassification;
import prerna.rdf.engine.api.IEngine;
import prerna.ui.components.api.IPlaySheet;
import prerna.ui.components.playsheets.BasicProcessingPlaySheet;
import prerna.ui.components.playsheets.ClassifyClusterPlaySheet;
import prerna.ui.components.playsheets.ClusteringVizPlaySheet;
import prerna.ui.components.playsheets.LocalOutlierPlaySheet;
import prerna.ui.components.playsheets.WekaClassificationPlaySheet;
import prerna.ui.helpers.ClusteringModuleUpdateRunner;
import prerna.ui.helpers.PlaysheetCreateRunner;
import prerna.util.ArrayListUtilityMethods;
import prerna.util.Utility;

/**
 * Runs the algorithm selected on the Cluster/Classify playsheet and adds additional tabs. Tied to the button to the ClassifyClusterPlaySheet.
 */
public class RunAlgorithmListener extends AbstractListener {
	private static final Logger LOGGER = LogManager.getLogger(RunAlgorithmListener.class.getName());
	
	private ClassifyClusterPlaySheet playSheet;
	private JTabbedPane jTab;
	private JProgressBar jBar;
	private Hashtable<String, IPlaySheet> playSheetHash;
	private JComboBox<String> algorithmComboBox;
	
	//cluster
	private JComboBox<String> selectNumClustersComboBox;
	private String manuallySelectNumClustersText;
	private JTextField selectNumClustersTextField;

	//classify
	private JComboBox<String> classificationMethodComboBox;
	private JComboBox<String> classComboBox;
	
	//outlier
	private JSlider enterKNeighborsSlider;
	
	private JToggleButton showDrillDownBtn;
	private JComboBox<String> drillDownTabSelectorComboBox;
	private ArrayList<JCheckBox> columnCheckboxes;
	private String[] names;
	private ArrayList<Object[]> list;
	private IEngine engine;
	private String title;

	private double[] entropyArr;
	
	/**
	 * Method actionPerformed.
	 * @param e ActionEvent
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		//get the columns we're filtering out
		//create a new tab
		//figure out if we are clustering or classifying
		//if cluster.... figure out number of clusters to use or if automatically choosing
		//.............. pass tab, and call the clustering on it
		//if classifying... figure out the class and the method for classifying, run the classifying
		//need to put chart/grid on correct tabs, updating if necessary
		//if outlier.... figure out the kneighbors and run the outlier method
		//TODO add logger statements
		
		//filter the names array and list of data based on the independent variables selected
		//must make sure that we include the first col, even though there is no checkbox
		boolean[] includeColArr = new boolean[columnCheckboxes.size()+1];
		includeColArr[0] = true; //this is the "title" or "name"
		Boolean noIVsSelected = true;
		for(int i=0;i<columnCheckboxes.size();i++) {
			JCheckBox checkbox = columnCheckboxes.get(i);
			includeColArr[i+1] = checkbox.isSelected();
			if(checkbox.isSelected())
				noIVsSelected = false;
		}
		if(noIVsSelected) {
			Utility.showError("No variables were selected. Please select at least one and retry.");
			return;
		}
		
		String[] filteredNames = Utility.filterNames(names, includeColArr);
		ArrayList<Object[]> filteredList = ArrayListUtilityMethods.filterList(list,includeColArr);

		String algorithm = algorithmComboBox.getSelectedItem() + "";
		if(algorithm.equals("Similarity")) {
			
			ClusteringModuleUpdateRunner runner = new ClusteringModuleUpdateRunner(playSheet);
			runner.setValues(filteredNames, filteredList);
			
			Thread playThread = new Thread(runner);
			playThread.start();
			return;
		}
		
		
		BasicProcessingPlaySheet newPlaySheet;
		if(algorithm.equals("Cluster") ) {
			int numClusters = 0;
			String selectNumClustersText = (String) selectNumClustersComboBox.getSelectedItem();
			//if we are manually setting number of clusters, pull the number to use from the text field
			if(selectNumClustersText.equals(manuallySelectNumClustersText)) {
				//make sure that what you pull from text field is an integer, if not return with an error
				String numClustersText = selectNumClustersTextField.getText();
				try {
					numClusters = Integer.parseInt(numClustersText);
				} catch(NumberFormatException exception) {
					Utility.showError("Number of clusters must be an integer greater than 1. Please fix and rerun.");
					return;
				}
				if(numClusters<2) {//if error {
					Utility.showError("Number of clusters must be an integer greater than 1. Please fix and rerun.");
					return;
				}
			}

			newPlaySheet = new ClusteringVizPlaySheet();
			if(numClusters>=2) {
				((ClusteringVizPlaySheet)newPlaySheet).setInputNumClusters(numClusters);
			}
			((ClusteringVizPlaySheet)newPlaySheet).setAddAsTab(true);
			((ClusteringVizPlaySheet)newPlaySheet).drillDownData(names,filteredNames,list,filteredList);
			((ClusteringVizPlaySheet)newPlaySheet).setDrillDownTabSelectorComboBox(drillDownTabSelectorComboBox);
			((ClusteringVizPlaySheet)newPlaySheet).setPlaySheetHash(playSheetHash);
			((ClusteringVizPlaySheet)newPlaySheet).setJTab(jTab);
			((ClusteringVizPlaySheet)newPlaySheet).setJBar(jBar);
			showDrillDownBtn.setVisible(true);			
	
		} else if(algorithm.equals("Classify")) {
			//method of classification to use
			String classMethod = classificationMethodComboBox.getSelectedItem() + "";
			
			//determine the column index and name to classify on
			String classifier = classComboBox.getSelectedItem() + "";
			int classifierIndex = filteredNames.length-1;
			while(classifierIndex>-1&&!filteredNames[classifierIndex].equals(classifier)) {
				classifierIndex--;
			}
			
			if(classifierIndex<0){
				LOGGER.error("Cannot match classifier selected in drop down to list of classifiers");
				return;
			}
			
			newPlaySheet = new WekaClassificationPlaySheet();
			newPlaySheet.setList(filteredList);
			newPlaySheet.setNames(filteredNames);
			((WekaClassificationPlaySheet)newPlaySheet).setModelName(classMethod);
			((WekaClassificationPlaySheet)newPlaySheet).setClassColumn(classifierIndex);
			((WekaClassificationPlaySheet)newPlaySheet).setJTab(jTab);
			((WekaClassificationPlaySheet)newPlaySheet).setJBar(jBar);
		
		} else if(algorithm.equals("Outliers")) {
			int kneighbors = enterKNeighborsSlider.getValue();
			newPlaySheet = new LocalOutlierPlaySheet();
			newPlaySheet.setList(filteredList);
			newPlaySheet.setNames(filteredNames);
			((LocalOutlierPlaySheet)newPlaySheet).setKNeighbors(kneighbors);
			((LocalOutlierPlaySheet)newPlaySheet).setJTab(jTab);
			((LocalOutlierPlaySheet)newPlaySheet).setJBar(jBar);
		
		} else if(algorithm.equals("Predictability")) {
			
			double[] accuracyArr = new double[columnCheckboxes.size()];
			double[] precisionArr = new double[columnCheckboxes.size()];
			
			for(int i=1; i < names.length; i++) {
				if(includeColArr[i]) {
					if(entropyArr[i-1] != 0) {
						WekaClassification weka = new WekaClassification(list, names, "J48", i);
						try {
							weka.execute();
							// both stored as percents
							accuracyArr[i-1] = weka.getAccuracy(); 
							precisionArr[i-1] = weka.getPrecision(); 
						} catch (Exception ex) {
							ex.printStackTrace();
							LOGGER.error("Could not generate accuracy and precision values from WEKA classification");
						}
					} else {
						accuracyArr[i-1] = 100;
						precisionArr[i-1] = 100;
					}
				}
			}
			playSheet.fillAccuracyAndPrecision(accuracyArr, precisionArr);
			return;
			
		} else {
			LOGGER.error("Cannot find algorithm");
			return;
		}

		newPlaySheet.setRDFEngine(engine);
		newPlaySheet.setTitle(title);
		
		PlaysheetCreateRunner runner = new PlaysheetCreateRunner(newPlaySheet);
		Thread playThread = new Thread(runner);
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
		this.algorithmComboBox = playSheet.getAlgorithmComboBox();
		
		//cluster
		this.selectNumClustersComboBox = playSheet.getSelectNumClustersComboBox();
		this.manuallySelectNumClustersText = playSheet.getManuallySelectNumClustersText();
		this.selectNumClustersTextField = playSheet.getSelectNumClustersTextField();
		//classification
		this.classificationMethodComboBox = playSheet.getClassificationMethodComboBox();
		this.classComboBox = playSheet.getClassComboBox();
		//outlier
		this.enterKNeighborsSlider = playSheet.getEnterKNeighborsSlider();
		
		this.names = playSheet.getNames();
		this.list = playSheet.getList();
		this.jTab = playSheet.getJTab();
		this.jBar = playSheet.getJBar();
		this.engine = playSheet.engine;
		this.drillDownTabSelectorComboBox = playSheet.getDrillDownTabSelectorComboBox();
		this.showDrillDownBtn = playSheet.getShowDrillDownBtn();
		this.playSheetHash = playSheet.getPlaySheetHash();
		this.title = playSheet.getTitle();
	}

	public void setEntropyArr(double[] entropyArr) {
		this.entropyArr = entropyArr;
	}

}
