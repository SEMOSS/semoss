package prerna.ui.components.specific.tap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.apache.log4j.Logger;

import prerna.rdf.engine.api.IEngine;
import prerna.rdf.engine.impl.AbstractEngine;
import prerna.rdf.engine.impl.SesameJenaSelectStatement;
import prerna.rdf.engine.impl.SesameJenaSelectWrapper;
import prerna.ui.components.UpdateProcessor;
import prerna.util.Utility;

public class ServicesAggregationProcessor {

	Logger logger = Logger.getLogger(getClass());
	private IEngine servicesDB;
	private IEngine coreDB;
	//private String baseURI = "http://www.health.mil/ontologies";
	private String semossBaseURI = "http://semoss.org/ontologies/Concept/";
	private String semossRelBaseURI = "http://semoss.org/ontologies/Relation/";
	private String propURI = "http://semoss.org/ontologies/Relation/Contains/";


	private Hashtable<String, Set<String>> allRelations = new Hashtable<String, Set<String>>();
	private Hashtable<String, Set<String>> allConcepts = new Hashtable<String, Set<String>>();

	private String TAP_SYSTEM_SERVICES_PROPERTY_AGGREGATION_QUERY = "SELECT DISTINCT ?system (GROUP_CONCAT(DISTINCT ?description ; SEPARATOR = \";\") AS ?fullDescription) "
			+ "(SUM(?numUsers) AS ?totalUsers) (SUM(?userConsoles) AS ?totalUserConsoles) (MAX(?availabilityReq) AS ?maxAvailability) (MIN(?availabilityAct) AS ?actualAvailability)"
			+ " (SUM(?transactions) AS ?totalTransactions) (MIN(?ato) AS ?earliestATODate) (GROUP_CONCAT(DISTINCT ?gt ; SEPARATOR = \"****\") AS ?GarrisonTheater) (MAX(?supportDate)"
			+ " AS ?latestEndOfSupportDate) (GROUP_CONCAT(DISTINCT ?POC ;  SEPARATOR = \",\") AS ?allPOCs) (GROUP_CONCAT(DISTINCT ?transactional ;  SEPARATOR = \"****\") AS ?Transactional)"
			+ " WHERE { {?system a <http://semoss.org/ontologies/Concept/System>} {?systemService a <http://semoss.org/ontologies/Concept/SystemService>}"
			+ " {?consists <http://www.w3.org/2000/01/rdf-schema#subPropertyOf>  <http://semoss.org/ontologies/Relation/ConsistsOf>} {?system ?consists ?systemService}"
			+ " OPTIONAL { BIND(<http://semoss.org/ontologies/Relation/Contains/Description> AS ?prop1) {?systemService ?prop1 ?description} }"
			+ " OPTIONAL { BIND(<http://semoss.org/ontologies/Relation/Contains/Number_of_Users> as ?prop2) {?systemService ?prop2 ?numUsers} FILTER( datatype(?numUsers) = xsd:double || datatype(?numUsers) = xsd:integer) }"
			+ " OPTIONAL { BIND(<http://semoss.org/ontologies/Relation/Contains/User_Consoles> as ?prop3) {?systemService ?prop3 ?userConsoles} FILTER(  datatype(?userConsoles) = xsd:double || datatype(?userConsoles) = xsd:integer) }"
			+ " OPTIONAL { BIND(<http://semoss.org/ontologies/Relation/Contains/Availability-Required> as ?prop4) {?systemService ?prop4 ?availabilityReq} FILTER(  datatype(?availabilityReq) = xsd:double || datatype(?availabilityReq) = xsd:integer) }"
			+ " OPTIONAL{ BIND(<http://semoss.org/ontologies/Relation/Contains/Availability-Actual> as ?prop5) {?systemService ?prop5 ?availabilityAct} FILTER(  datatype(?availabilityAct) = xsd:double || datatype(?availabilityAct) = xsd:integer) }"
			+ " OPTIONAL { BIND(<http://semoss.org/ontologies/Relation/Contains/Transaction_Count> as ?prop6) {?systemService ?prop6 ?transactions} FILTER(  datatype(?transactions) = xsd:double || datatype(?transactions) = xsd:integer) }"
			+ " OPTIONAL { BIND(<http://semoss.org/ontologies/Relation/Contains/ATO_Date> as ?prop7) {?systemService ?prop7 ?ato} FILTER( datatype(?ato) = xsd:dateTime ) }"
			+ " OPTIONAL { BIND(<http://semoss.org/ontologies/Relation/Contains/GarrisonTheater> as ?prop8) {?systemService ?prop8 ?gt} }"
			+ " OPTIONAL { BIND(<http://semoss.org/ontologies/Relation/Contains/End_of_Support_Date> as ?prop9) {?systemService ?prop9 ?supportDate} FILTER(  datatype(?supportDate) = xsd:dateTime ) }"
			+ " OPTIONAL { BIND(<http://semoss.org/ontologies/Relation/Contains/POC> as ?prop10) {?systemService ?prop10 ?POC} }"
			+ " OPTIONAL { BIND(<http://semoss.org/ontologies/Relation/Contains/Transactional> as ?prop11) {?systemService ?prop11 ?transactional} } } "
			+ "GROUP BY ?system";

	private String TAP_SERVICES_AGGREGATE_HARDWARE_QUERY = "SELECT DISTINCT ?hardwareVersion ?has ?hardware WHERE { {?hardwareVersion a <http://semoss.org/ontologies/Concept/HardwareVersion>} "
			+ "{?has <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Has>} {?hardware a <http://semoss.org/ontologies/Concept/Hardware>} {?hardware ?has ?hardwareVersion} }";

	private String TAP_SERVICES_AGGREGATE_PERSONNEL_QUERY = "SELECT DISTINCT ?system ?usedBy ?personnel WHERE{{?system <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/System>}"
			+ "{?consistsOf <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/ConsistsOf>}"
			+ "{?systemService <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/SystemService>}"
			+ "{?system ?consistsOf ?SystemService} {?usedBy <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/UsedBy>} "
			+ "{?personnel <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Personnel>} {?systemService ?usedBy ?personnel}}";

	private String TAP_SERVICES_AGGREGATE_USER_INTERFACE_QUERY = "SELECT DISTINCT ?system ?utilizes ?userInterface "
			+ "WHERE{ {?system <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/System>} "
			+ "{?consistsOf <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/ConsistsOf>} "
			+ "{?systemService <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/SystemService>} "
			+ "{?system ?consistsOf ?systemService} {?utilizes <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Utilizes>} "
			+ "{?userInterface <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/UserInterface>} {?systemService ?utilizes ?userInterface}}";

	private String TAP_SERVICES_AGGREGATE_BP_QUERY = "SELECT DISTINCT ?system ?supports ?bp WHERE{{?system <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/System>}"
			+ "{?consistsOf <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/ConsistsOf>} "
			+ "{?systemService <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/SystemService>} "
			+ "{?system ?consistsOf ?systemService} {?supports <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Supports>} "
			+ "{?bp <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/BusinessProcess>} {?systemService ?supports ?bp}}";

	private String TAP_SERVICES_AGGREGATE_ACTIVITY_QUERY = "SELECT DISTINCT ?system ?supports ?activity WHERE{{?system <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/System>}"
			+ "{?consistsOf <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/ConsistsOf>} "
			+ "{?systemService <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/SystemService>} "
			+ "{?system ?consistsOf ?systemService} {?supports <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Supports>} "
			+ "{?activity <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Activity>} {?systemService ?supports ?activity}}";

	private String TAP_SERVICES_AGGREGATE_BLU_QUERY = "SELECT DISTINCT ?system ?provide ?BLU WHERE{{?system <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/System>} "
			+ "{?consistsOf <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/ConsistsOf>} "
			+ "{?systemService <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/SystemService>} "
			+ "{?system ?consistsOf ?systemService} {?provide <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Provide>} "
			+ "{?BLU <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/BusinessLogicUnit>} {?systemService ?provide ?BLU}}";

	private String TAP_SERVICES_AGGREGATE_DATA_OBJECT_QUERY = "SELECT DISTINCT ?system ?provide ?data ?crm WHERE{{?system <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/System>} "
			+ "{?consistsOf <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/ConsistsOf>} "
			+ "{?SystemService <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/SystemService>} "
			+ "{?system ?consistsOf ?SystemService} {?provide <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Provide>} "
			+ "{?data <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/DataObject>} "
			+ "{?SystemService ?provide ?data} {?provide <http://semoss.org/ontologies/Relation/Contains/CRM> ?crm}}";

	private String TAP_SERVICES_AGGREGATE_TERROR_QUERY = "SELECT DISTINCT ?system ?has ?TError ?weight WHERE {{?system <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/System>} "
			+ "{?consistsOf <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/ConsistsOf>} "
			+ "{?systemService <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/SystemService>} "
			+ "{?system ?consistsOf ?systemService} {?has <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Has>} "
			+ "{?TError <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/TError>} "
			+ "{?systemService ?has ?TError} {?has <http://semoss.org/ontologies/Relation/Contains/weight> ?weight}}";

	private String TAP_SERVICES_AGGREGATE_ICD_QUERY = "SELECT DISTINCT ?ICD ?prop ?value WHERE {{?ICD <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/InterfaceControlDocument>} "
			+ "{?Payload <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Payload>} "
			+ "{?Data <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/DataObject>} "
			+ "{?ICD ?Payload ?Data} {?prop <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Relation/Contains>} {?Payload ?prop ?value}}";
	
	public ServicesAggregationProcessor(IEngine servicesDB, IEngine coreDB){
		this.servicesDB = servicesDB;
		this.coreDB = coreDB;
	}

	public void runFullAggregation(){
		runSystemServicePropertyAggregation(TAP_SYSTEM_SERVICES_PROPERTY_AGGREGATION_QUERY);
		runGenericRelationshipWithOnePropAggregation(TAP_SERVICES_AGGREGATE_PERSONNEL_QUERY, "", "");
		runGenericRelationshipWithOnePropAggregation(TAP_SERVICES_AGGREGATE_USER_INTERFACE_QUERY, "", "");
		runGenericRelationshipWithOnePropAggregation(TAP_SERVICES_AGGREGATE_BP_QUERY, "", "");
		runGenericRelationshipWithOnePropAggregation(TAP_SERVICES_AGGREGATE_ACTIVITY_QUERY, "", "");
		runGenericRelationshipWithOnePropAggregation(TAP_SERVICES_AGGREGATE_BLU_QUERY, "", "");
		runGenericRelationshipWithOnePropAggregation(TAP_SERVICES_AGGREGATE_DATA_OBJECT_QUERY, "CRM", "RELATION");
		runGenericRelationshipWithOnePropAggregation(TAP_SERVICES_AGGREGATE_TERROR_QUERY, "weight", "RELATION");
		runICDAggregation(TAP_SERVICES_AGGREGATE_ICD_QUERY);
		
		System.out.println("success");
	}

	/*
	 * -Description  Concatenate service entries if different
		-Number of Users - Add
		-User Consoles  Add
		-Availability-Required  Use highest
		-Availability-Actual  Use lowest
		-Transaction Count - Add
		-ATO Date  Use earliest
		-GarrisonTheater  If both Garrison and Theater, label, Both
		-End of Support Date  Take latest
		-POC - Concatenate
		-Transactional  Flag and check with POCs
	 */
	private void runSystemServicePropertyAggregation(String propQuery)
	{
		Hashtable<String, Hashtable<String, String>> propHash = new Hashtable<String, Hashtable<String, String>>();

		SesameJenaSelectWrapper sjsw = processQuery(propQuery, servicesDB);
		while(sjsw.hasNext())
		{
			SesameJenaSelectStatement sjss = sjsw.next();
			// get the next row and see how it must be added to the insert query
			String System = sjss.getRawVar("system") + "";
			Hashtable<String, String> properties = new Hashtable<String, String>();
			properties.put(propURI + "Description", sjss.getRawVar("fullDescription") + "");
			properties.put(propURI + "Number_of_Users", sjss.getRawVar("totalUsers") + "");
			properties.put(propURI + "User_Consoles", sjss.getRawVar("totalUserConsoles") + "");
			properties.put(propURI + "Availability-Required", sjss.getRawVar("maxAvailability") + "");
			properties.put(propURI + "Availability-Actual", sjss.getRawVar("actualAvailability") + "");
			properties.put(propURI + "Transaction_Count", sjss.getRawVar("totalTransactions") + "");
			properties.put(propURI + "ATO_Date", sjss.getRawVar("earliestATODate") + "");
			properties.put(propURI + "GarrisonTheater", sjss.getRawVar("GarrisonTheater") + "");
			properties.put(propURI + "End_of_Support_Date", sjss.getRawVar("latestEndOfSupportDate") + "");
			properties.put(propURI + "POC", sjss.getRawVar("allPOCs") + "");
			properties.put(propURI + "Transactional", sjss.getRawVar("Transactional") + "");

			propHash = addToHashtable(System, properties, propHash);

			// add instance system to list
			addToAllConcepts(System);
		}

		//String insertSystemServicePropertiesQuery = prepareInsertQuery(propHash);
		//runInsert(insertQuery, coreDB);
	}

	private Hashtable<String, Hashtable<String, String>> addToHashtable(String system, Hashtable<String, String> properties, Hashtable<String, Hashtable<String, String>> propHash) 
	{
		Hashtable<String, String> innerHash = new Hashtable<String, String>();
		//sysPropHash.put(system, innerHash);
		if(!propHash.containsKey(system))
		{
			propHash.put(system, innerHash);
			for(String key : properties.keySet())
			{
				// properties are optional so null can be returned
				if(properties.get(key) != null)
				{
					innerHash.put(key, properties.get(key).toString());
				}
			}
			propHash.get(system).putAll(innerHash);
		}
		else
		{
			for(String key : properties.keySet())
			{
				// properties are optional so null can be returned
				if(properties.get(key) != null)
				{
					propHash.get(system).put(key, properties.get(key));
				}
			}
		}	

		return propHash;
	}

//	private void runGenericRelationshipAggregation(String query)
//	{
//		Hashtable<String, Hashtable<String, String>> dataHash = new Hashtable<String, Hashtable<String, String>>();
//		//Hashtable<String, String> baseFilterHash = ((AbstractEngine) servicesDB).getBaseHash();
//		SesameJenaSelectWrapper sjsw = processQuery(query, servicesDB);
//		String[] vars = sjsw.getVariables();
//		while(sjsw.hasNext())
//		{
//			SesameJenaSelectStatement sjss = sjsw.next();
//			// get the next row and see how it must be added to the insert query
//			String subject = sjss.getRawVar(vars[0]) + "";
//			String pred = sjss.getRawVar(vars[1]) + "";
//			String object = sjss.getRawVar(vars[2]) + "";
//			//if(!baseFilterHash.containsKey(pred))
//			{
//				pred = pred.substring(0, pred.lastIndexOf("/")) + "/" + getTextAfterFinalDelimeter(subject) +":" + getTextAfterFinalDelimeter(object);
//				dataHash = addToHashtable(subject, pred, object, dataHash);
//
//				addToAllConcepts(subject);
//				addToAllConcepts(object);
//				addToAllRelationships(pred);
//			}
//		}
//	}

	/*
	 * -DataObject  Aggregate, and C takes precedence over M or "R"
	 */
	private void runGenericRelationshipWithOnePropAggregation(String query, String propType, String type)
	{
		Hashtable<String, Hashtable<String, String>> dataHash = new Hashtable<String, Hashtable<String, String>>();
		//Hashtable<String, String> baseFilterHash = ((AbstractEngine) servicesDB).getBaseHash();
		SesameJenaSelectWrapper sjsw = processQuery(query, servicesDB);
		String[] vars = sjsw.getVariables();
		while(sjsw.hasNext())
		{
			SesameJenaSelectStatement sjss = sjsw.next();
			// get the next row and see how it must be added to the insert query
			String subject = sjss.getRawVar(vars[0]) + "";
			String pred = sjss.getRawVar(vars[1]) + "";
			String object = sjss.getRawVar(vars[2]) + "";
			String prop = "";
			// check in case property is null
			if(vars.length > 3)
			{
				prop = sjss.getRawVar(vars[3]) + "";
			}
			
			System.out.println("ADDING RELATIONSHIP");
			pred = pred.substring(0, pred.lastIndexOf("/")) + "/" + getTextAfterFinalDelimeter(subject) +":" + getTextAfterFinalDelimeter(object);
			dataHash = addToHashtable(subject, pred, object, dataHash);
			if(!prop.equals(""))
			{
				String propertyURI = propURI + propType;
				if(type.equals("RELATION"))
				{
					System.out.println("ADDING PROPERTY TO RELATIONSHIP");
					dataHash = addToHashtable(pred, propertyURI, prop, dataHash);
				}
				if(type.equals("NODE-SUBJECT"))
				{
					System.out.println("ADDING PROPERTY TO SUBJECT NODE");
					dataHash = addToHashtable(subject, propertyURI, prop, dataHash);
				}
				if(type.equals("NODE-OBJECT"))
				{
					System.out.println("ADDING PROPERTY TO OBJECT NODE");
					dataHash = addToHashtable(subject, propertyURI, prop, dataHash);
				}
			}

			addToAllConcepts(subject);
			addToAllConcepts(object);
			addToAllRelationships(pred);

		}
	}
	
	/*
	 * -InterfaceControlDocument  Copy over to TAP Core
		-DFreq  Keep the highest frequency (e.g. daily takes precedence over weekly, if we receive both)
		-DProt, DForm  Concatenate
		-Concatenate all properties
	 */
	private void runICDAggregation(String query)
	{
		Hashtable<String, Hashtable<String, String>> dataHash = new Hashtable<String, Hashtable<String, String>>();
		//Hashtable<String, String> baseFilterHash = ((AbstractEngine) servicesDB).getBaseHash();
		SesameJenaSelectWrapper sjsw = processQuery(query, servicesDB);
		String[] vars = sjsw.getVariables();
		while(sjsw.hasNext())
		{
			SesameJenaSelectStatement sjss = sjsw.next();
			// get the next row and see how it must be added to the insert query
			String subject = sjss.getRawVar(vars[0]) + "";
			String prop = sjss.getRawVar(vars[1]) + "";
			String value = sjss.getRawVar(vars[2]) + "";
		
			System.out.println("ADDING NODE PROPERTY");
			dataHash = addToHashtable(subject, prop, value, dataHash);
		
			addToAllConcepts(subject);
		}
	}

	//this function will add a value to a hashtable in format {subject : {predicate : object}} which is what will be needed for the prepareInsertQuery function
	//need to think through how the aggregation will work if the s p o is already present in the hash... separate functions?
	private Hashtable<String, Hashtable<String, String>> addToHashtable(String subject, String predicate, String object, Hashtable<String, Hashtable<String, String>> dataHash)
	{
		Hashtable<String, String> innerHash = new Hashtable<String, String>();
		if(!dataHash.containsKey(subject))
		{
			System.out.println("ADDING:     " + subject + " -----> {" + predicate + " --- " + object + "}");
			innerHash.put(predicate, object);
			dataHash.put(subject, innerHash);
		}
		else
		{
			System.out.println("ADDING:     " + subject + " -----> {" + predicate + " --- " + object + "}");
			innerHash = dataHash.get(subject);
			innerHash.put(predicate, object);
			dataHash.get(subject).put(predicate, object);
		}

		return dataHash;
	}
	

	private String getTextAfterFinalDelimeter(String uri)
	{
		uri = uri.substring(uri.lastIndexOf("/")+1);
		return uri;
	}

	private void addToAllConcepts(String uri)
	{
		String conceptBaseURI = semossBaseURI + Utility.getClassName(uri);
		if(allConcepts.containsKey(conceptBaseURI))
		{
			allConcepts.get(conceptBaseURI).add(uri);
		}
		else
		{
			allConcepts.put(conceptBaseURI, new HashSet<String>());
			allConcepts.get(conceptBaseURI).add(uri);
		}		
	}

	private void addToAllRelationships(String uri)
	{
		String relationBaseURI = semossRelBaseURI + Utility.getClassName(uri);
		if(allRelations.containsKey(relationBaseURI))
		{
			allRelations.get(relationBaseURI).add(uri);
		}
		else
		{
			allRelations.put(relationBaseURI, new HashSet<String>());
			allRelations.get(relationBaseURI).add(uri);
		}
	}

	/*
	 * -HardwareModule, HardwareVersion, Hardware  Roll up from service to enterprise level
		-Add quantities
	 */
	private void runHardwareAggregation()
	{

	}

	/*
	 * -SoftwareModule, SoftwareVersion, Software  Roll up from service to enterprise level
		-Add quantities
	 */
	private void runSoftwareAggregation(){

	}




	//process the query
	private SesameJenaSelectWrapper processQuery(String query, IEngine engine){
		SesameJenaSelectWrapper sjsw = new SesameJenaSelectWrapper();
		//run the query against the engine provided
		sjsw.setEngine(engine);
		sjsw.setQuery(query);
		sjsw.executeQuery();		
		sjsw.getVariables();
		return sjsw;
	}

	//this function will take a hashtable in the format {subject : {predicate : object}} to create an insert query
	private String prepareInsertQuery(Hashtable<String, Hashtable<String, String>> table){
		String insertQuery = "INSERT DATA { " ;

		return insertQuery;
	}

	// simply run the insert query
	private void runInsert(String query, IEngine engine){
		logger.info("Running update query into " + engine.getEngineName() + "::: " + query);
		UpdateProcessor upProc = new UpdateProcessor();
		upProc.setEngine(engine);
		upProc.setQuery(query);
		upProc.processQuery();
	}
}
