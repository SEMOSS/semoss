/*******************************************************************************
 * Copyright 2013 SEMOSS.ORG
 * 
 * This file is part of SEMOSS.
 * 
 * SEMOSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SEMOSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SEMOSS.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package prerna.ui.components.specific.tap;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JDesktopPane;

import prerna.error.EngineException;
import prerna.rdf.engine.api.IEngine;
import prerna.ui.components.playsheets.GridPlaySheet;
import prerna.util.DHMSMTransitionUtility;
import prerna.util.DIHelper;

@SuppressWarnings("serial")
public class LPInterfaceReportGenerator extends GridPlaySheet {

	private LPInterfaceProcessor processor;
	
	public void setProcessor(LPInterfaceProcessor processor) {
		this.processor = processor;
	}
	
	/**
	 * This is the function that is used to create the first view 
	 * of any play sheet.  It often uses a lot of the variables previously set on the play sheet, such as {@link #setQuery(String)},
	 * {@link #setJDesktopPane(JDesktopPane)}, {@link #setRDFEngine(IEngine)}, and {@link #setTitle(String)} so that the play 
	 * sheet is displayed correctly when the view is first created.  It generally creates the model for visualization from 
	 * the specified engine, then creates the visualization, and finally displays it on the specified desktop pane
	 * 
	 * <p>This is the function called by the PlaysheetCreateRunner.  PlaysheetCreateRunner is the runner used whenever a play 
	 * sheet is to first be created, most notably in ProcessQueryListener.
	 */
	@Override
	public void createData() {

		list = new ArrayList<Object[]>();
		
		if(processor == null) {
			processor = new LPInterfaceProcessor();
		}
		processor.setQuery(query);
		processor.setEngine(engine);
		
		list = processor.generateReport();
		names = processor.getNames();
	}
	
	// requires the cost information to already be created and set in the processor
	public HashMap<String, Object> getSysInterfaceWithCostData(String systemName, String reportType) throws EngineException {
		HashMap<String, Object> sysLPIInterfaceHash = getSysInterfaceData(systemName);
		
		HashMap<String, Object> dataHash = new HashMap<String, Object>();
		String[] oldHeaders = (String[]) sysLPIInterfaceHash.get(DHMSMTransitionUtility.HEADER_KEY);

		String[] newHeaders = new String[oldHeaders.length + 3];
		for(int i = 0; i < oldHeaders.length; i++)
		{
			if(i < oldHeaders.length - 1) {
				newHeaders[i] = oldHeaders[i];
			} else {
				newHeaders[i] = "Services";
				newHeaders[i+1] = "Recommendation";
				newHeaders[i+2] = "Direct Cost";
				newHeaders[i+3] = "Indirect Cost";
			}
		}
		
		ArrayList<Object[]> newData;
		if(reportType.equals("LPI") || reportType.equals("HPI")) {
			newData = processor.createLPIInterfaceWithCostHash(systemName, (ArrayList<Object[]>) sysLPIInterfaceHash.get(DHMSMTransitionUtility.DATA_KEY));
		} else {
			processor.getLPNIInfo(engine);
			newData = processor.createLPNIInterfaceWithCostHash(systemName, (ArrayList<Object[]>) sysLPIInterfaceHash.get(DHMSMTransitionUtility.DATA_KEY));
		}
		
		dataHash.put(DHMSMTransitionUtility.DATA_KEY, newData);
		dataHash.put(DHMSMTransitionUtility.HEADER_KEY, newHeaders);
		dataHash.put(DHMSMTransitionUtility.TOTAL_DIRECT_COST_KEY, processor.getTotalDirectCost());
		dataHash.put(DHMSMTransitionUtility.TOTAL_INDIRECT_COST_KEY, processor.getTotalIndirectCost());
		
		return dataHash;
	}

	public HashMap<String, Object> getSysInterfaceData(String systemName) throws EngineException 
	{
		HashMap<String, Object> sysLPIInterfaceHash = new HashMap<String, Object>();
		systemName = systemName.replaceAll("\\(", "\\\\\\\\\\(").replaceAll("\\)", "\\\\\\\\\\)");
		String lpSystemInterfacesQuery = DHMSMTransitionUtility.lpSystemInterfacesQuery.replace("@SYSTEMNAME@", systemName);
		this.query = lpSystemInterfacesQuery;
		this.engine = (IEngine) DIHelper.getInstance().getLocalProp("HR_Core");
		if(engine == null) {
			throw new EngineException("HR_Core Database is not available");
		}
		createData();			
		sysLPIInterfaceHash.put(DHMSMTransitionUtility.HEADER_KEY, DHMSMTransitionUtility.removeSystemFromStringArray(getNames()));
		sysLPIInterfaceHash.put(DHMSMTransitionUtility.DATA_KEY, DHMSMTransitionUtility.removeSystemFromArrayList(getList()));
		return sysLPIInterfaceHash;
	}
}
