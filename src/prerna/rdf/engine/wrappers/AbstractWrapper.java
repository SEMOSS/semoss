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
package prerna.rdf.engine.wrappers;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import prerna.engine.api.IEngine;
import prerna.engine.api.IEngineWrapper;
import prerna.engine.api.IRemoteQueryable;

public abstract class AbstractWrapper implements IRemoteQueryable, IEngineWrapper {

	private static final Logger LOGGER = LogManager.getLogger(AbstractWrapper.class.getName());
	
	protected transient IEngine engine = null;
	protected transient String query = null;
//	protected transient QueryStruct qs;

	protected String ID = null;
	protected String api = null;
	protected boolean remote = false;
	protected String[] var = null;
	protected String[] displayVar = null;

	public abstract void execute();

	public abstract boolean hasNext();
	
	@Override
	public void setRemoteID(String id) {
		this.ID = id;
	}

	@Override
	public Map<String, Object> getResponseMeta() {
		Map<String, Object> resp = new HashMap<String, Object>();
		if(engine != null) {
			resp.put("engine", engine.getEngineName());
		}
		if(query != null){
			// if super long... only show first 500 values
			if(query.length() > 500) {
				resp.put("query", query.substring(0, 500) + " ...");
			} else {
				resp.put("query", query);
			}
		}
		return resp;
	}

	@Override
	public String getRemoteID() {
		return this.ID;
	}

	@Override
	public void setRemoteAPI(String engine) {
		this.api = engine;
	}

	@Override
	public String getRemoteAPI() {
		return this.api;
	}
	
	public void setRemote(boolean remote) {
		this.remote = remote;
	}

	@Override
	public void setQuery(String query) {
		LOGGER.debug("Setting the query " + query);
		this.query = query;
	}
	
//	@Override
//	public void setQueryStruct(QueryStruct qs) {
//		this.qs = qs;
//	}

	@Override
	public void setEngine(IEngine engine) {
		LOGGER.debug("Set the engine to " + engine.getEngineName());
		this.engine = engine;
	}

}
