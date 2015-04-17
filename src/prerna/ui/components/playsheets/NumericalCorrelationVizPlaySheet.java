/*******************************************************************************
 * Copyright 2015 SEMOSS.ORG
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
package prerna.ui.components.playsheets;

import java.awt.Dimension;
import java.util.Hashtable;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import prerna.util.CSSApplication;
import prerna.util.Constants;
import prerna.util.DIHelper;

public class NumericalCorrelationVizPlaySheet extends BrowserPlaySheet{

	private static final Logger LOGGER = LogManager.getLogger(NumericalCorrelationVizPlaySheet.class.getName());		
	private double[][] correlationArray;

	private boolean includesInstance = true;
	
	
	/**
	 * Constructor for MatrixRegressionVizPlaySheet.
	 */
	public NumericalCorrelationVizPlaySheet() {
		super();
		this.setPreferredSize(new Dimension(800,600));
		String workingDir = DIHelper.getInstance().getProperty(Constants.BASE_FOLDER);
		fileName = "file://" + workingDir + "/html/MHS-RDFSemossCharts/app/scatter-plot-matrix.html";
	}
	
	@Override
	public void createData() {
		if(list==null)
			super.createData();
		else
			dataHash = processQueryData();
	}
	
	public void runAlgorithm() {

		int numCols = names.length;
		
		//create the b and A arrays which are used in matrix regression to determine coefficients
		double[][] dataArr;
		if(includesInstance)
			dataArr = MatrixRegressionHelper.createA(list, 1, numCols);
		else
			dataArr = MatrixRegressionHelper.createA(list, 0, numCols);

		//run covariance 
//		Covariance covariance = new Covariance(dataArr);
//		double[][] covarianceArray = covariance.getCovarianceMatrix().getData();
		//run correlation
		//TODO this can be simplified to only get correlation for the params we need
		PearsonsCorrelation correlation = new PearsonsCorrelation(dataArr);
		correlationArray = correlation.getCorrelationMatrix().getData();		
	}
	
	@Override
	public Hashtable processQueryData()
	{
		runAlgorithm();
		int i;
		int j;
		int listNumRows = list.size();
		int numVariables;
		int variableStartCol;
		String id;
		
		if(includesInstance) {
			numVariables = names.length - 1;
			variableStartCol = 1;
			id = names[0];
		}else {
			numVariables = names.length;
			variableStartCol = 0;
			id = "";
		}
		
		//for each element/instance
		//add its values for all independent variables to the dataSeriesHash
		Object[][] dataSeries = new Object[listNumRows][numVariables + 1];
		
		//set the first value to either be the instance, or null
		if(includesInstance) {
			for(i=0;i<listNumRows;i++) {
				dataSeries[i][0] = list.get(i)[0];
			}
		}//not sure if i need the below?
//		else {
//			for(i=0;i<listNumRows;i++) {
//				dataSeries[i][0] = "";
//			}
//		}

		for(i=0;i<listNumRows;i++) {
			for(j=0; j<numVariables; j++) {
				dataSeries[i][j+1] = list.get(i)[j+variableStartCol];
			}
		}

		// reversing values since it is being painted by JS in reverse order
		double[][] correlations = new double[numVariables][numVariables];
		for(i = 0; i<numVariables; i++) {
			for(j = 0; j<numVariables; j++) {
				correlations[numVariables-i-1][numVariables-j-1] = correlationArray[i][j];
			}
		}

		dataHash = new Hashtable();
		dataHash.put("one-row",false);
		dataHash.put("id",id);
		dataHash.put("names", names);
		dataHash.put("dataSeries", dataSeries);
		dataHash.put("correlations", correlations);
		
//		Gson gson = new Gson();
//		System.out.println(gson.toJson(dataHash));
		
		return dataHash;
	}
	
	/**
	 * Method addPanel. Creates a panel and adds the table to the panel.
	 */
	@Override
	public void addPanel()
	{
		//if this is to be a separate playsheet, create the tab in a new window
		//otherwise, if this is to be just a new tab in an existing playsheet,
		
		if(jTab==null) {
			super.addPanel();
		} else {
			String lastTabName = jTab.getTitleAt(jTab.getTabCount()-1);
			LOGGER.info("Parsing integer out of last tab name");
			int count = 1;
			if(jTab.getTabCount()>1)
				count = Integer.parseInt(lastTabName.substring(0,lastTabName.indexOf(".")))+1;
			addPanelAsTab(count+". Correlation");
		}
		new CSSApplication(getContentPane());
	}
	
	public void setIncludesInstance(boolean includesInstance) {
		this.includesInstance = includesInstance;
	}

}
