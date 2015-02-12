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
package prerna.ui.components.playsheets;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;

import prerna.ui.components.playsheets.BrowserPlaySheet;
import prerna.util.Constants;
import prerna.util.DIHelper;

/**
 * The Play Sheet for creating a Parallel Sets diagram.
 */
public class ParallelSetsPlaySheet extends BrowserPlaySheet {
	
	/**
	 * Constructor for ParallelSetsPlaySheet.
	 */
	public ParallelSetsPlaySheet() {
		super();
		this.setPreferredSize(new Dimension(800,600));
		String workingDir = DIHelper.getInstance().getProperty(Constants.BASE_FOLDER);
		fileName = "file://" + workingDir + "/html/MHS-RDFSemossCharts/app/parsets.html";
	}
	
	/**
	 * Method processQueryData.  Processes the data from the SPARQL query into an appropriate format for the specific play sheet.
	
	 * @return Hashtable - Processed text and numerical data accordingly for the parallel sets visualization.*/
	public Hashtable processQueryData()
	{
		ArrayList dataArrayList = new ArrayList();
		String[] var = wrapper.getVariables(); 		
		for (int i=0; i<list.size(); i++)
		{	
			LinkedHashMap elementHash = new LinkedHashMap();
			Object[] listElement = list.get(i);
			String colName;
			Double value;
			for (int j = 0; j < var.length; j++) 
			{
				colName = var[j];
				if (listElement[j] instanceof String)
				{
					String text = (String) listElement[j];
					text = text.replaceAll("^\"|\"$", "");
					if (text.length() >= 30) {
					text = text.substring(0, Math.min(text.length(), 30));  //temporary
					text = text + "...";
					}
					elementHash.put(colName, text);
				}
				else 
				{
					value = (Double) listElement[j];	
					elementHash.put(colName, value);
				}
			}
				dataArrayList.add(elementHash);			
		}
		Hashtable allHash = new Hashtable();
		allHash.put("dataSeries", dataArrayList);
		allHash.put("headers", var);
		
		return allHash;
	}
	@Override
	public Hashtable<String, String> getDataTableAlign() {
		Hashtable<String, String> alignHash = new Hashtable<String, String>();
		for(int namesIdx = 0; namesIdx<names.length; namesIdx++){
			alignHash.put("dim " + namesIdx, names[namesIdx]);
		}
		return alignHash;
	}
}
