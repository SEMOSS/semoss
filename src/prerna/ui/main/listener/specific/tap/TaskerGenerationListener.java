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

import javax.swing.JComponent;
import javax.swing.JList;

import org.apache.log4j.Logger;

import prerna.ui.components.ParamComboBox;
import prerna.ui.components.api.IChakraListener;
import prerna.ui.components.specific.tap.TaskerGenerationProcessor;
import prerna.util.Constants;
import prerna.util.ConstantsTAP;
import prerna.util.DIHelper;

/**
 * Listener for btnTaskerGeneration on the MHS TAP tab
 * Results in the creation of a Tasker for either all systems or a specified system
 */
public class TaskerGenerationListener implements IChakraListener {

	TaskerGenerationProcessor processor = new TaskerGenerationProcessor();
	Logger logger = Logger.getLogger(getClass());
	ArrayList queryArray = new ArrayList();

	/**
	 * This is executed when the btnFactSheetReport is pressed by the user
	 * Calls FactSheetProcessor to generate all the information from the queries to write onto the fact sheet
	 * @param arg0 ActionEvent
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		JList list = (JList) DIHelper.getInstance().getLocalProp(Constants.REPO_LIST);
		processor.coreDB = (String)list.getSelectedValue();
		ParamComboBox systemComboBox = (ParamComboBox) DIHelper.getInstance().getLocalProp(ConstantsTAP.TASKER_GENERATION_SYSTEM_COMBO_BOX);
		String system = (String)systemComboBox.getSelectedItem();
		processor.generateSystemTasker(system);

	}

	/**
	 * Override method from IChakraListener
	 * @param view
	 */
	@Override
	public void setView(JComponent view) {

	}

}
