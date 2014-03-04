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
package prerna.ui.components.specific.tap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * This class is used to generate the system duplication calculations for the fact sheet report.
 */
public class CapabilityFactSheetCapDupeCalculator {
	
	Logger logger = Logger.getLogger(getClass());

	ArrayList<String> capList = new ArrayList<String>();
	String hrCoreDB = "HR_Core";
	Hashtable<String, Hashtable<String, ArrayList<Object>>> allDataHash = new Hashtable<String, Hashtable<String, ArrayList<Object>>>();
	public ArrayList<String> criteriaList = new ArrayList<String>();
	public Hashtable<String, ArrayList<ArrayList<Object>>> priorityAllDataHash = new Hashtable<String, ArrayList<ArrayList<Object>>>();
	public Hashtable<String, ArrayList<String>> priorityCapHash = new Hashtable<String, ArrayList<String>>();
	public Hashtable<String, ArrayList<Double>> priorityValueHash = new Hashtable<String, ArrayList<Double>>();
	int comparisonSysNum = 5;
	/**
	 * Constructor for FactSheetSysDupeCalculator.
	 */
	public CapabilityFactSheetCapDupeCalculator()
	{
		performAnalysis();
		prioritizeValues();
		organizeFinalPriorityHash();
		printValues2();
	}
	
	/**
	 * Performs analysis on the TAP Core database for system duplication calculations.
	 * Takes into business processes, data, BLU, theater/garrison, transactions, data warehouse, users, sites, and user interface.
	 */
	public void performAnalysis() 
	{
		//declare 
		SysDupeFunctions sdf = new SysDupeFunctions();
		//get list of systems first
		
		String query = "SELECT DISTINCT ?Capability WHERE {{?Capability <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Capability>}}";
		capList = sdf.createSystemList(hrCoreDB, query);
		sdf.setSysList(capList);
		
		//Data and BLU 2
		criteriaList.add("Data/BLU");
		String dataQuery = "SELECT DISTINCT ?Capability ?Data ?CRM WHERE {{?Capability <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Capability>;}{?Consists <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Consists>;}{?Task <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Task>;}{?Needs <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Needs>;}{?Needs <http://semoss.org/ontologies/Relation/Contains/CRM> ?CRM;}{?Data <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/DataObject>;}{?Capability ?Consists ?Task.}{?Task ?Needs ?Data.} }";
		String bluQuery = "SELECT DISTINCT ?Capability ?BusinessLogicUnit WHERE {{?Capability <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Capability>;}{?Consists <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Consists>;}{?Task <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Task>;}{?BusinessLogicUnit <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/BusinessLogicUnit>} {?Task_Needs_BusinessLogicUnit <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Needs>}{?Capability ?Consists ?Task.}{?Task ?Task_Needs_BusinessLogicUnit ?BusinessLogicUnit}}";
		Hashtable<String, Hashtable<String,Double>> dataBLUHash = sdf.getDataBLUDataSet(hrCoreDB, dataQuery, bluQuery, SysDupeFunctions.VALUE);
		processHashForScoring(dataBLUHash, 0);
		
		//BP
		criteriaList.add("BusinessProcess");
		String bpQuery ="SELECT DISTINCT ?Capability ?BusinessProcess WHERE {{?Capability <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Capability>;} {?Supports <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Supports>;} {?BusinessProcess <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/BusinessProcess>;} {?Capability ?Supports ?BusinessProcess.} }";
		Hashtable bpHash = sdf.compareSystemParameterScore(hrCoreDB, bpQuery, SysDupeFunctions.VALUE);
		processHashForScoring(bpHash,1);
	
		//Attribute
		criteriaList.add("Attribute");
		String attributeQuery ="SELECT DISTINCT ?Capability ?Attribute WHERE {{?Capability <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Capability>;}{?Consists <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Consists>;}{?Task <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Task>;}{?Has <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Has>;}{?Attribute <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Attribute>;}{?Capability ?Consists ?Task.}{?Task ?Has ?Attribute.}}";
		Hashtable attributeHash = sdf.compareSystemParameterScore(hrCoreDB, attributeQuery, SysDupeFunctions.VALUE);
		processHashForScoring(attributeHash,2);
		
		//Participant
		criteriaList.add("Participant");
		String participantQuery ="SELECT DISTINCT ?Capability ?Participant WHERE {{?Capability <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Capability> ;}{?Consists <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Consists>;}{?Task <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Task>;} {?Requires <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Requires>;}{?Participant <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Participant>;}{?Capability ?Consists ?Task.}{?Task ?Requires ?Participant.}}";
		Hashtable participantHash = sdf.compareSystemParameterScore(hrCoreDB, participantQuery, SysDupeFunctions.VALUE);
		processHashForScoring(participantHash,3);
		
	}	
	

	/**
	 * Processes the hashtable that is used for system duplication storing.
	 * Creates hashtable of arraylists with system as the key and corresponding data/BLU as the values.
	 * @param dataHash 	Hashtable<String,Hashtable<String,Double>>
	 * @param idx 		Index for the system value hash (int).
	 */
	public void processHashForScoring(Hashtable<String, Hashtable<String,Double>>dataHash, int idx)
	{

		for(Entry<String, Hashtable<String, Double>> sysEntry : dataHash.entrySet()) 
		{
			String sysName = sysEntry.getKey();
		    Hashtable<String,Double> sysDataHash = sysEntry.getValue();
		    Hashtable<String, ArrayList<Object>> dataSysHash;
		    //get the right system data hash from allDataHash
		    if(!allDataHash.containsKey(sysName))
		    {
		    	dataSysHash = new Hashtable<String, ArrayList<Object>>();
		    	allDataHash.put(sysName, dataSysHash);
		    }
		    else
		    {
		    	dataSysHash = allDataHash.get(sysName);
		    }
		    	
		    for(Entry<String, Double> sysCompEntry : sysDataHash.entrySet()) 
			{
				String sysName2 = sysCompEntry.getKey();
			    Double value = sysCompEntry.getValue();
			    if(!dataSysHash.containsKey(sysName2))
			    {
			    	ArrayList<Object> sysValueList = new ArrayList<Object>(){{
			    		  add("N/A");
			    		  add("N/A");
			    		  add("N/A");
			    		  add("N/A");
			    		  add("N/A");
			    		  add("N/A");
			    		  add("N/A");
			    		}};;
			    	sysValueList.remove(idx);
			    	sysValueList.add(idx, value);
			    	dataSysHash.put(sysName2, sysValueList);
			    }
			    else
			    {
			    	ArrayList<Object> sysValueList  = dataSysHash.get(sysName2);
			    	sysValueList.remove(idx);
			    	sysValueList.add(idx, value);
			    }
			}
		}
	}
	
	
	/**
	 * This method prioritizes the top systems compared to a given system.
	 */
	public void prioritizeValues()
	{
		
		//go through all the data for one system at a time
		for(Entry<String, Hashtable<String, ArrayList<Object>>> e : allDataHash.entrySet()) 
		{
			String sysName = e.getKey();
			Hashtable<String, ArrayList<Object>> valueHash = e.getValue();
			ArrayList<Double> priorityList = new ArrayList<Double>();
			ArrayList<String> priorityNameList = new ArrayList<String>();
			
			//for a given system go through all criteria
			for(Entry<String, ArrayList<Object>> e1 : valueHash.entrySet()) 
			{
				String sysName2 = e1.getKey();
				ArrayList<Object> valueList = e1.getValue();
				double totalValue = 0.0;
				//do not need to evaluate system against itself
				//if theater or data warehouse ones both dont work
				if(( valueList.get(1) instanceof Double && (Double)valueList.get(1)==0 ) || (valueList.get(2) instanceof Double && (Double)valueList.get(2) ==0)||sysName.equals(sysName2))
				{
					continue;
				}
				for(int i=0;i<criteriaList.size();i++)
				{
					if(valueList.get(i) instanceof Double)
					{
						totalValue = totalValue+ (Double)valueList.get(i);
					}
				}
				
				//When a new total value is determined for a sys-sys comparison, compare to see if it is top 5 or n 
				for (int i=0; i<comparisonSysNum;i++)
				{
					//if element doesn't even exist, just add it in
					if (priorityList.size()<=i)
					{
						priorityList.add(i, totalValue);
						priorityNameList.add(i, sysName2);
						break;
					}
					//if it does exist, you ened to insert it and ensure it doesn't overpopulate the total number
					else if (totalValue>priorityList.get(i))
					{
						//get % estimate
						priorityList.add(i, totalValue);
						priorityNameList.add(i, sysName2);
						//always remove nth element because we want to keep the size of this arraylist to that number
						if(priorityList.size()>5)
						{
							priorityList.remove(comparisonSysNum);
							priorityNameList.remove(comparisonSysNum);
						}
						break;
					}
				}
			}
			//finally put everything into those two hashes
			
			for (int i=0; i<priorityList.size();i++)
			{
				double curValue = priorityList.get(i);
				curValue =(double) curValue/criteriaList.size();
				priorityList.remove(i);
				priorityList.add(i, curValue);
			}
			priorityCapHash.put(sysName, priorityNameList);
			priorityValueHash.put(sysName, priorityList);
		}
	}
	/**
	 * Organizes the final priority hash by looping through the list of priority systems.
	 */
	public void organizeFinalPriorityHash()
	{
		for(Entry<String, ArrayList<String>> e : priorityCapHash.entrySet()) 
		{
			String sysName = e.getKey();
			ArrayList<String> capList = e.getValue();
			Hashtable<String, ArrayList<Object>> sysSpecAllDataHash = allDataHash.get(sysName);
			ArrayList<ArrayList<Object>> sysSpecPriorityList = new ArrayList<ArrayList<Object>>();
			for(int i=0;i<capList.size();i++) 
			{
				String sysName2 = capList.get(i);
				ArrayList<Object> allSpecData = sysSpecAllDataHash.get(sysName2);
				sysSpecPriorityList.add(allSpecData);
			}
			priorityAllDataHash.put(sysName, sysSpecPriorityList);
		}
	}
	
	/**
	 * Prints values for system duplication, looping through the all data hash.
	 */
	public void printValues1()
	{
		for(Entry<String, Hashtable<String, ArrayList<Object>>> e : allDataHash.entrySet()) 
		{
			String sysName = e.getKey();
			Hashtable<String, ArrayList<Object>> valueHash = e.getValue();
			for(Entry<String, ArrayList<Object>> e1 : valueHash.entrySet()) 
			{
				String sysName2 = e1.getKey();
				ArrayList<Object> valueList = e1.getValue();
				String displayString = sysName+" Fulfillment by "+sysName2+ " through: ";
				for(int i=0;i<criteriaList.size();i++)
				{
					displayString = displayString+" "+criteriaList.get(i)+ "-" +valueList.get(i)+";";
				}
				logger.debug(displayString);
			}
		}
	}
	
	/**
	 * Prints values for system duplication, looping through the priority all data hash.
	 */
	public void printValues2()
	{
		for(Entry<String, ArrayList<ArrayList<Object>>> e : priorityAllDataHash.entrySet()) 
		{
			String sysName = e.getKey();
			ArrayList<ArrayList<Object>> valueList = e.getValue();
			ArrayList<String> sysNameList = priorityCapHash.get(sysName);
			for(int i=0;i<valueList.size();i++) 
			{
				String sysName2 = sysNameList.get(i);
				ArrayList<Object> printList = valueList.get(i);
				String displayString = sysName+" Fulfillment by "+sysName2+ " through: ";
				for(int j=0;j<criteriaList.size();j++)
				{
					displayString = displayString+" "+criteriaList.get(j)+ "-" +printList.get(j)+";";
				}
				logger.debug(displayString);
			}
		}
	}
	
}
