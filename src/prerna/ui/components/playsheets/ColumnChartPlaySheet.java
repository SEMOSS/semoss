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
package prerna.ui.components.playsheets;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import prerna.engine.api.IHeadersDataRow;
import prerna.ui.components.ChartControlPanel;
import prerna.ui.main.listener.impl.ColumnChartGroupedStackedListener;
import prerna.util.Constants;
import prerna.util.DIHelper;
import prerna.util.Utility;

public class ColumnChartPlaySheet extends BrowserPlaySheet{

	private static final Logger logger = LogManager.getLogger(ColumnChartPlaySheet.class.getName());
	
	private static final String labelString = "label";
	private static final String valueString = "value ";
	
	public ColumnChartPlaySheet() 
	{
		super();
		this.setPreferredSize(new Dimension(800,600));
		String workingDir = DIHelper.getInstance().getProperty(Constants.BASE_FOLDER);
		fileName = "file://" + workingDir + "/html/MHS-RDFSemossCharts/app/columnchart.html";
	}
	
	@Override
	public void createControlPanel(){
		controlPanel = new ChartControlPanel();
		controlPanel.addExportButton(1);
		
		ChartControlPanel ctrlPanel = this.getControlPanel();
		GridBagConstraints gbc_btnGroupedStacked = new GridBagConstraints();
		gbc_btnGroupedStacked.insets = new Insets(10, 0, 0, 5);
		gbc_btnGroupedStacked.anchor = GridBagConstraints.EAST;
		gbc_btnGroupedStacked.gridx = 0;
		gbc_btnGroupedStacked.gridy = 0;
		JButton transitionGroupedStacked = new JButton("Transition Grouped");
		ColumnChartGroupedStackedListener gsListener = new ColumnChartGroupedStackedListener();
//		gsListener.setBrowser(this.browser);
		transitionGroupedStacked.addActionListener(gsListener);
		ctrlPanel.add(transitionGroupedStacked, gbc_btnGroupedStacked);

		this.controlPanel.setPlaySheet(this);
	}
	
	public void processQueryData()
	{		
		ArrayList< ArrayList<Hashtable<String, Object>>> dataObj = new ArrayList< ArrayList<Hashtable<String, Object>>>();
		String[] names = dataFrame.getColumnHeaders();
		Map<String, String> align = getDataTableAlign();
		int labelIdx = -1;
		Integer[] seriesIndices = new Integer[align.size()-1];
		for(int i = 0; i < names.length; i++){
			String name = names[i];
			if(align.containsValue(name)){
				String key = Utility.getKeyFromValue(align, name);
				if(key.contains(labelString)){
					labelIdx = i;
				}
				else if(key.contains(valueString)){
					int seriesIdx = Integer.parseInt(key.replace(valueString, ""));
					seriesIndices[seriesIdx - 1] = i;
				}
			}
		}

		//series name - all objects in that series (x : ... , y : ...)
		Iterator<IHeadersDataRow> it = dataFrame.iterator();
		while(it.hasNext())
		{
			Object[] elemValues = it.next().getValues();
			
			for (int j = 0; j < seriesIndices.length; j++){
				ArrayList<Hashtable<String,Object>> seriesArray = new ArrayList<Hashtable<String,Object>>();
				if(dataObj.size() > j)
					seriesArray = dataObj.get(j);
				else
					dataObj.add(j, seriesArray);
				Hashtable<String, Object> elementHash = new Hashtable();
				elementHash.put("x", elemValues[labelIdx].toString());
				elementHash.put("y", elemValues[seriesIndices[j]]);
				elementHash.put("seriesName", names[seriesIndices[j]]);
				seriesArray.add(elementHash);
			}
			
//			for( int j = 1; j < elemValues.length; j++)
//			{
//				ArrayList<Hashtable<String,Object>> seriesArray = new ArrayList<Hashtable<String,Object>>();
//				if(dataObj.size() >= j)
//					seriesArray = dataObj.get(j-1);
//				else
//					dataObj.add(j-1, seriesArray);
//				Hashtable<String, Object> elementHash = new Hashtable();
//				elementHash.put("x", elemValues[0].toString());
//				elementHash.put("y", elemValues[j]);
//				elementHash.put("seriesName", names[j]);
//				seriesArray.add(elementHash);
//			}
		}
		
		Hashtable<String, Object> columnChartHash = new Hashtable<String, Object>();
		columnChartHash.put("names", names);
		columnChartHash.put("dataSeries", dataObj);
		
		this.dataHash = columnChartHash;
	}
	
	@Override
	public Map<String, String> getDataTableAlign() {
		if(this.tableDataAlign == null){
			this.tableDataAlign = getAlignHash();
		}
		return this.tableDataAlign;
	}
	
	public Hashtable<String, String> getAlignHash() {
		Hashtable<String, String> alignHash = new Hashtable<String, String>();
		String[] names = dataFrame.getColumnHeaders();
		alignHash.put(labelString, names[0]);
		for(int namesIdx = 1; namesIdx<names.length; namesIdx++){
			alignHash.put(valueString + namesIdx, names[namesIdx]);
		}
		return alignHash;
	}
}
