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
package prerna.ui.components.specific.tap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import prerna.engine.api.IEngine;
import prerna.ui.components.ParamComboBox;
import prerna.ui.helpers.EntityFiller;
import prerna.util.DIHelper;

/**
 * Combo box specific to the Transition Report.
 */
public class TransitionReportComboBox extends ParamComboBox implements Runnable {
	
	static final Logger logger = LogManager.getLogger(TransitionReportComboBox.class.getName());
	
	Object[] repos = new Object[1];
	
	/**
	 * Constructor for TransitionReportComboBox.
	 * 
	 * @param array String[]	Items populating the combobox
	 */
	public TransitionReportComboBox(String[] array) {
		super(array);
	}

	/**
	 * Inherited method - calls fillParam()
	 */
	public void run(){
		fillParam();
	}
	
	/**
	 * Sets engine as specified.
	 * 
	 * @param repo String	Name of repo to be set
	 */
	public void setEngine(String repo){
		repos[0] = repo;
	}
	
	/**
	 * Fills combobox with Systems using EntityFiller
	 */
	public void fillParam(){
		String key = "System";
		// execute the logic for filling the information here
		String entityType = "http://semoss.org/ontologies/Concept/System";
		setParamName(key);
		
		setEditable(false);
		//PlayTextField pField = new PlayTextField(field);
		
		for(int repoIndex = 0;repoIndex < repos.length;repoIndex++)
		{
			IEngine engine = (IEngine)DIHelper.getInstance().getLocalProp(repos[repoIndex]+"");
			logger.info("Repository is " + repos[repoIndex]);
			
			EntityFiller filler = new EntityFiller();
			filler.engineName = repos[repoIndex]+"";
			filler.engine = engine;
			filler.box = this;
			filler.type = entityType;
			Thread aThread = new Thread(filler);
			aThread.start();
		}
	}

}
