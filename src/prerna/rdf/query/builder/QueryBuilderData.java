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
package prerna.rdf.query.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import prerna.util.Utility;

public class QueryBuilderData {

	static final Logger LOGGER = LogManager.getLogger(QueryBuilderData.class.getName());

	Map<String, String> returnMapping;					// Logical name --> Physical Name
	List<List<String>> relTriples;						// List of physical name triples
	List<Map<String, String>> nodeProps;				// List of selected property objects. Each object contains SubjectVar-->Logical Name of node, uriKey-->Physical uri for property, varKey-->logical name of property
	Map<String, List<Object>> filterData;				// For each var to filter, has Logical name-->List of values to include
	List<String> returnVars;							// Specifies what variables to return and in what order
	Boolean limitReturnToVarsList = false;				// If you want to only return the vars specified in the returnVars list, set this to true. Otherwise all node and prop vars will return. THESE ARE LOGICAL NAMES

	transient private final String QUERY_DATA_KEY = "QueryData";
	transient private final String REL_TRIPLES_KEY = "relTriples";
	transient private final String FILTERS_KEY = "filter";
	transient private final String NODE_PROPS_KEY = "SelectedNodeProps";

	public QueryBuilderData(Map json){
		LOGGER.info("Instantiating QueryBuilderData with json " + json);

		if(json.containsKey(NODE_PROPS_KEY)){
			setNodeProps((List) json.get(NODE_PROPS_KEY));
			if(nodeProps != null && !nodeProps.isEmpty()) nodeProps.get(0).remove("db");
		}

		if(json.containsKey(QUERY_DATA_KEY)){
			Map queryData = (Map) json.get(QUERY_DATA_KEY);
			setRelTriples((List) queryData.get(REL_TRIPLES_KEY));
			if(queryData.get(FILTERS_KEY) != null)
				setFilterData((Map) queryData.get(FILTERS_KEY));
		} else if(json.containsKey(REL_TRIPLES_KEY)) {
			setRelTriples((List) json.get(REL_TRIPLES_KEY));
		}
	}
	
	public QueryBuilderData(){
		LOGGER.info("opening empty query builder data");
	}
	
	public void addRelTriple(List<String> trip){
		if(this.relTriples == null){
			this.relTriples = new ArrayList<List<String>>();
		}
		this.relTriples.add(trip);
	}

	public void setLimitReturnToVarsList(Boolean limitReturnToVarsList){
		LOGGER.info("Setting to limit return var list :: " + limitReturnToVarsList);
		this.limitReturnToVarsList = limitReturnToVarsList;
	}

	public Boolean getLimitReturnToVarsList(){
		return this.limitReturnToVarsList;
	}

	public void setVarReturnOrder(String var, int location){
		LOGGER.info("setting var " + var + " return order to " + location);
		if(returnVars == null){
			returnVars = new Vector<String>();
		}
		int varIdx = returnVars.indexOf(var);
		LOGGER.info(var + " is currently in position " + varIdx);
		if(varIdx != -1){
			returnVars.remove(var);
			returnVars.add(location, var);
		}
		else if (location == 0){
			returnVars.add(location, var);
		}
		LOGGER.info(var + " is now in position " + returnVars.indexOf(var));
	}

	protected List<String> getReturnVars(){
		return this.returnVars;
	}

	public Map<String, List<Object>> getFilterData() {
		return filterData;
	}

	public void setFilterData(Map<String, List<Object>> filterData) {
		this.filterData = filterData;
	}

	protected Map<String, String> getReturnMapping() {
		return returnMapping;
	}

	//	private void setReturnMapping(Map<String, String> returnMapping) {
	//		this.returnMapping = returnMapping;
	//	}

	public List<List<String>> getRelTriples() {
		return relTriples;
	}

	private void setRelTriples(List<List<String>> relTriples) {
		this.relTriples = relTriples;
	}

	public List<Map<String, String>> getNodeProps() {
		return nodeProps;
	}

	private void setNodeProps(List<Map<String, String>> nodeProps) {
		this.nodeProps = nodeProps;
	}

	protected String getLogicalNameFromPhysicalURI(String physURI) {
		String logicalName = "";
		if(returnMapping != null && !returnMapping.isEmpty()) {
			for(Entry<String, String> e : returnMapping.entrySet()) {
				if(e.getValue().equals(Utility.getInstanceName(physURI))) {
					logicalName = e.getKey();
				}
			}
		} else {
			logicalName = Utility.getInstanceName(physURI);
		}

		return logicalName;
	}

	/*
	 * This method returns how the returned results are related to one another
	 * This is needed for correctly building the results graph in TinkerFrame
	 * The structure of the return hash is returnVar -> {downstreamReturnVar, downstreamReturnVar...}
	 */
	public Map<String, Set<String>> getReturnConnectionsHash()
	{
		Map<String, Set<String>> edgeHash = new HashMap<String, Set<String>>();
		// First need to iterate through properties
		// These are the easiest to capture
		// Need to make sure valid return value

		if(this.nodeProps != null){
			for(Map<String, String> nodeProp: this.nodeProps){
				String propVarName = nodeProp.get("varKey");
				LOGGER.info("checking if we need to add property to edgeHash::: " + propVarName);
				if(!limitReturnToVarsList || this.returnVars.contains(propVarName)){
					String nodeVarName = nodeProp.get("SubjectVar");
					LOGGER.info("yes, adding now. " + propVarName + " is downstream of " + nodeVarName); // TODO: What if nodeVarName is not part of query return... ? what do we connect this property to?
					Set<String> downNodeTypes = edgeHash.get(nodeVarName);
					if(downNodeTypes == null){
						downNodeTypes = new HashSet<String>();
						edgeHash.put(nodeVarName, downNodeTypes);
					}
					downNodeTypes.add(propVarName);
				}
			}
		}
		if(this.relTriples != null){
			for(List<String> relTriple : this.relTriples){
				if(relTriple.size() > 1){ // if this is a full triple... rather than just a single node
					String physObj = relTriple.get(2);
					String logicalObj = getLogicalNameFromPhysicalURI(physObj);

					LOGGER.info("checking if need need to add node to edgeHash:::: " + logicalObj);
					if(!limitReturnToVarsList || this.returnVars.contains(logicalObj)){
						// yes it seems valid. need to get subject logical name
						String physSub = relTriple.get(0);
						String logicalSub = getLogicalNameFromPhysicalURI(physSub);
						LOGGER.info("yes, adding now. " + logicalObj + " is downstream of " + logicalSub); // TODO: What if subject is not part of query return... ? what do we connect this property to?
						Set<String> downNodeTypes = edgeHash.get(logicalSub);
						if(downNodeTypes == null){
							downNodeTypes = new HashSet<String>();
							edgeHash.put(logicalSub, downNodeTypes);
						}
						downNodeTypes.add(logicalObj);
					}
				} else {
					String physObj = relTriple.get(0);
					String logicalObj = getLogicalNameFromPhysicalURI(physObj); 
					if(!edgeHash.containsKey(logicalObj)) {
						Set<String> emptySet = new HashSet<String>();
						edgeHash.put(logicalObj, emptySet);
					}
				}

			}
		}

		return edgeHash;
	}

}
