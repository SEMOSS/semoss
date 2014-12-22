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

import javax.swing.JComponent;
import javax.swing.JTextPane;

import org.apache.log4j.Logger;

import prerna.ui.components.ParamComboBox;
import prerna.util.Constants;
import prerna.util.DIHelper;

public class AddParameterBtnListener extends AbstractListener {
	Logger logger = Logger.getLogger(getClass());
	/**
	 * Method actionPerformed.
	 * @param e ActionEvent
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		JTextPane questionSparql = (JTextPane) DIHelper.getInstance().getLocalProp(
				Constants.QUESTION_SPARQL_TEXT_PANE);
		ParamComboBox addParameterComboBox = (ParamComboBox) DIHelper.getInstance().getLocalProp(Constants.QUESTION_ADD_PARAMETER_COMBO_BOX);
		
		String currentQuery = questionSparql.getText();
		String parameter = (String) addParameterComboBox.getSelectedItem();
		
		//adds the sparql for parameters
		questionSparql.setText(currentQuery+" BIND(<@"+ parameter + "-http://semoss.org/ontologies/" + parameter + "@> AS ?" + parameter + ") ");
	}
	
	/**
	 * Method setView.
	 * @param view JComponent
	 */
	@Override
	public void setView(JComponent view) {
		// TODO Auto-generated method stub
		
	}

}
