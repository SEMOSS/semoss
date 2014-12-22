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
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Selects or unselects all the checkboxes in a given list
 */
public class SelectCheckboxesListener implements ActionListener {
	private static final Logger LOGGER = LogManager.getLogger(SelectCheckboxesListener.class.getName());
	
	private ArrayList<JCheckBox> checkboxes;
	
	/**
	 * Method actionPerformed.
	 * @param e ActionEvent
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		JCheckBox selectAllCheckBox = (JCheckBox)e.getSource();
		Boolean isSelected = selectAllCheckBox.isSelected();
		if(checkboxes==null) {
			LOGGER.error("Cannot select all checkboxes because arraylist is undefined");
			return;
		}
		for(JCheckBox checkbox : checkboxes) {
			if(checkbox.isEnabled())
				checkbox.setSelected(isSelected);
			else //if the checkbox is disabled, then needs to be selected
				checkbox.setSelected(true);
		}
	}

	public void setCheckboxes(ArrayList<JCheckBox> checkboxes) {
		this.checkboxes = checkboxes;
	}

}
