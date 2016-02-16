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
package prerna.ui.main.listener.impl;

import java.util.Hashtable;

import com.google.gson.Gson;
import com.teamdev.jxbrowser.chromium.BrowserFunction;
import com.teamdev.jxbrowser.chromium.JSValue;

import prerna.ui.components.playsheets.AbstractGraphPlaySheet;

/**
 * Refreshes the view of the graph play sheet.
 */
public class RefreshPlaysheetFunction implements BrowserFunction {
	
	AbstractGraphPlaySheet gps;
	
	/**
	 * Method setGps.  Sets the graph play sheet that the listener will access.
	 * @param gps GraphPlaySheet
	 */
	public void setGps(AbstractGraphPlaySheet gps) {
		this.gps = gps;
	}

	/**
	 * Method invoke.
	 * @param arg0 Object[]
	
	 * @return Object */
	@Override
	public JSValue invoke(JSValue... arg0) {
		gps.refineView();

		Hashtable retHash = new Hashtable();
		retHash.put("success", true);

		Gson gson = new Gson();
        
		return JSValue.create(gson.toJson(retHash));
	}
	
}
