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
package prerna.rdf.query.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import prerna.rdf.query.util.SEMOSSQueryHelper;
import prerna.rdf.query.util.SPARQLConstants;
import prerna.rdf.query.util.TriplePart;

public class CustomVizGraphBuilder extends AbstractBaseMetaModelQueryBuilder {
	static final Logger logger = LogManager.getLogger(CustomVizGraphBuilder.class.getName());

	protected void addRelationshipTriples (ArrayList<Hashtable<String,String>> predV) {
		for(Hashtable<String, String> predHash : predV){
			String predName = predHash.get(varKey);
			String predURI = predHash.get(uriKey);

			SEMOSSQueryHelper.addRelationTypeTripleToQuery(predName, predURI, semossQuery);
			SEMOSSQueryHelper.addRelationshipVarTripleToQuery(predHash.get("SubjectVar"), predName, predHash.get("ObjectVar"), semossQuery);
		}
	}

	@Override
	public void buildQuery() 
	{
		semossQuery.setQueryType(SPARQLConstants.CONSTRUCT);
		semossQuery.setDisctinct(false);
		semossQuery.setReturnTripleArray((ArrayList<ArrayList<String>>) allJSONHash.get(relArrayKey));
		parsePath();
		// we are assuming properties are passed in now based on user selection
//		parsePropertiesFromPath(); 
		configureQuery();
	}

	@Override
	protected void addReturnVariables(
			ArrayList<Hashtable<String, String>> predV2) {
		// TODO Auto-generated method stub
		
	}
}
