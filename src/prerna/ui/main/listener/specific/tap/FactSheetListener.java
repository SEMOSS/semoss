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
package prerna.ui.main.listener.specific.tap;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.apache.log4j.Logger;

import prerna.poi.specific.FactSheetProcessor;
import prerna.ui.components.ParamComboBox;
import prerna.ui.components.api.IChakraListener;
import prerna.util.Constants;
import prerna.util.DIHelper;

/**
 * Listener for btnFactSheetReport on the MHS TAP tab
 * Results in the creation of fact sheets for either all systems or a specified system
 */
public class FactSheetListener implements IChakraListener {

	FactSheetProcessor processor = new FactSheetProcessor();
	Logger logger = Logger.getLogger(getClass());
	ArrayList queryArray = new ArrayList();

	/**
	 * This is executed when the btnFactSheetReport is pressed by the user
	 * Calls FactSheetProcessor to generate all the information from the queries to write onto the fact sheet
	 * @param arg0 ActionEvent
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		JComboBox reportType = (JComboBox) DIHelper.getInstance().getLocalProp(Constants.FACT_SHEET_REPORT_TYPE_COMBO_BOX);
		String type = (String) reportType.getSelectedItem();
		String system = null;
		if(type.contains("Specific")){
			ParamComboBox systemComboBox = (ParamComboBox) DIHelper.getInstance().getLocalProp(Constants.FACT_SHEET_SYSTEM_SELECT_COMBO_BOX);
			system = (String) systemComboBox.getSelectedItem();		

			processor.generateSystemReport(system);
		}
		else if (type.contains("All Systems")) {
			processor.generateReports();	
		}
	}

	/**
	 * Override method from IChakraListener
	 * @param view
	 */
	@Override
	public void setView(JComponent view) {

	}

}
