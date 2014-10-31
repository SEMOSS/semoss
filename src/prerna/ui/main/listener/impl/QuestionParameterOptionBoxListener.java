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
package prerna.ui.main.listener.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;
import javax.swing.JTextPane;

import prerna.ui.components.api.IChakraListener;
import prerna.util.Constants;
import prerna.util.DIHelper;

/**
 */
public class QuestionParameterOptionBoxListener implements FocusListener, IChakraListener {
	// when focused in on Parameter Prop text pane, set all text to plain font and black
	/**
	 * Method focusGained.  When focused
	 * @param e FocusEvent
	 */
	@Override 
	public void focusGained(FocusEvent e) {
		JTextPane parameterOptionTextPane = (JTextPane) DIHelper.getInstance().getLocalProp(Constants.PARAMETER_OPTION_TEXT_PANE);
		// if example is in the textpane, delete it when focus is gained
		if(parameterOptionTextPane.getText().contains("Example:")){
			parameterOptionTextPane.setText("");
		}
		
		parameterOptionTextPane.setFont(new Font("Tahoma", Font.PLAIN, 11));
		parameterOptionTextPane.setForeground(Color.BLACK);
	}
	
	// when not focused on sparql area and the area is filled with a play sheet hint
	// set all text to italics and gray
	/**
	 * Method focusLost.
	 * @param e FocusEvent
	 */
	@Override
	public void focusLost(FocusEvent e) {
		JTextPane parameterOptionTextPane = (JTextPane) DIHelper.getInstance().getLocalProp(Constants.PARAMETER_OPTION_TEXT_PANE);
		//sets the hint
		if(parameterOptionTextPane.getText().equals("")){
			parameterOptionTextPane.setText("Example:" + "\r" + "OverrideMonth_OPTION" + "\t" + "1;2;3;4;5;6;7;8;9;10;11;12");
			parameterOptionTextPane.setFont(new Font("Tahoma", Font.ITALIC, 11));
			parameterOptionTextPane.setForeground(Color.GRAY);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setView(JComponent view) {
		// TODO Auto-generated method stub
		
	}
}
