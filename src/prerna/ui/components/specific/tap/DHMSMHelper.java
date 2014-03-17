package prerna.ui.components.specific.tap;

import java.util.ArrayList;
import java.util.Hashtable;

import prerna.rdf.engine.api.IEngine;
import prerna.rdf.engine.impl.SesameJenaSelectStatement;
import prerna.rdf.engine.impl.SesameJenaSelectWrapper;
import prerna.util.DIHelper;

public class DHMSMHelper {

	private String GET_ALL_SYSTEM_WITH_CREATE_AND_DOWNSTREAM_QUERY = "SELECT DISTINCT ?system ?data ?crm WHERE { filter( !regex(str(?crm),\"R\")) {?system <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/ActiveSystem> }{?otherSystem <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/ActiveSystem> } {?icd <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/InterfaceControlDocument> } {?data <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/DataObject>} {?provideData <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Provide>} {?system ?provideData ?data} {?provideData <http://semoss.org/ontologies/Relation/Contains/CRM> ?crm} {?system <http://semoss.org/ontologies/Relation/Provide> ?icd}{?icd <http://semoss.org/ontologies/Relation/Consume> ?otherSystem} {?icd <http://semoss.org/ontologies/Relation/Payload> ?data} }";

	private String GET_ALL_SYSTEM_WITH_DOWNSTREAM_AND_NO_UPSTREAM = "SELECT DISTINCT ?System ?Data ?CRM WHERE { BIND(\"C\" as ?CRM) {?Data <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/DataObject>;}{?System <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/ActiveSystem> ;}{?otherSystem <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/ActiveSystem> ;}{?icd <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/InterfaceControlDocument> ;}OPTIONAL{{?icd2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/InterfaceControlDocument> ;}{?icd2 <http://semoss.org/ontologies/Relation/Consume> ?System}{?icd2 <http://semoss.org/ontologies/Relation/Payload> ?Data}}{?System <http://semoss.org/ontologies/Relation/Provide> ?icd ;}{?icd <http://semoss.org/ontologies/Relation/Consume> ?otherSystem ;} {?icd <http://semoss.org/ontologies/Relation/Payload> ?Data ;}FILTER(!BOUND(?icd2)) } ORDER BY ?System";	

	private String GET_ALL_SYSTEM_WITH_UPSTREAM = "SELECT DISTINCT ?system ?data ?crm WHERE { BIND(\"R\" as ?crm) FILTER NOT EXISTS{{?icd2 <http://semoss.org/ontologies/Relation/Consume> ?system}{?icd2 <http://semoss.org/ontologies/Relation/Payload> ?data}} {?system <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/ActiveSystem> } {?otherSystem <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/ActiveSystem> }{?icd <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/InterfaceControlDocument> } {?data <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/DataObject>} {?system <http://semoss.org/ontologies/Relation/Provide> ?data} {?system <http://semoss.org/ontologies/Relation/Provide> ?icd}{?system <http://semoss.org/ontologies/Relation/Provide> ?icd}{?icd <http://semoss.org/ontologies/Relation/Consume> ?otherSystem} {?icd <http://semoss.org/ontologies/Relation/Payload> ?data} }";

	private String GET_ALL_DHMSM_CAPABILITIES_AND_CRM = "SELECT DISTINCT ?cap ?data ?crm WHERE { BIND(<http://health.mil/ontologies/Concept/DHMSM/DHMSM> as ?dhmsm) {?cap <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Capability> } {?task <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Task>} {?data <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/DataObject>} {?dhmsm <http://semoss.org/ontologies/Relation/TaggedBy> ?cap} {?cap <http://semoss.org/ontologies/Relation/Consists> ?task} {?needs <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Needs>} {?task ?needs ?data} {?needs <http://semoss.org/ontologies/Relation/Contains/CRM> ?crm} }";
	
	private String GET_ALL_HR_CAPABILITIES_AND_CRM = "SELECT DISTINCT ?cap ?data ?crm WHERE {{?CapabilityFunctionalArea <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/CapabilityFunctionalArea>;}{?Utilizes <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Utilizes>;}{?CapabilityGroup <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/CapabilityGroup>;}{?ConsistsOfCapability <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Consists>;}{?cap <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Capability>;}{?task <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Task>} {?data <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/DataObject>}{?CapabilityFunctionalArea ?Utilizes ?CapabilityGroup;}{?CapabilityGroup ?ConsistsOfCapability ?cap;} {?cap <http://semoss.org/ontologies/Relation/Consists> ?task} {?needs <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Needs>} {?task ?needs ?data} {?needs <http://semoss.org/ontologies/Relation/Contains/CRM> ?crm}} BINDINGS ?CapabilityFunctionalArea {(<http://health.mil/ontologies/Concept/CapabilityFunctionalArea/HSD>)(<http://health.mil/ontologies/Concept/CapabilityFunctionalArea/HSS>)(<http://health.mil/ontologies/Concept/CapabilityFunctionalArea/FHP>)}";

	private Hashtable<String, Hashtable<String, String>> dataListSystems = new Hashtable<String, Hashtable<String, String>>();
	private Hashtable<String, Hashtable<String, String>> dataListCapabilities = new Hashtable<String, Hashtable<String, String>>();
	
	private boolean useDHMSMOnly = true;


	public void runData(IEngine engine)
	{
		SesameJenaSelectWrapper sjswQuery1 = processQuery(GET_ALL_SYSTEM_WITH_CREATE_AND_DOWNSTREAM_QUERY, engine);
		processAllResults(sjswQuery1, true);

		SesameJenaSelectWrapper sjswQuery2 = processQuery(GET_ALL_SYSTEM_WITH_DOWNSTREAM_AND_NO_UPSTREAM, engine);
		processAllResults(sjswQuery2, true);

		SesameJenaSelectWrapper sjswQuery3 = processQuery(GET_ALL_SYSTEM_WITH_UPSTREAM, engine);
		processAllResults(sjswQuery3, true);

		if(useDHMSMOnly)
		{
			SesameJenaSelectWrapper sjswQuery4 = processQuery(GET_ALL_DHMSM_CAPABILITIES_AND_CRM, engine);
			processAllResults(sjswQuery4, false);
		}
		else
		{
			SesameJenaSelectWrapper sjswQuery4 = processQuery(GET_ALL_HR_CAPABILITIES_AND_CRM, engine);
			processAllResults(sjswQuery4, false);
		}
		return;
	}
	public void setUseDHMSMOnly(boolean useDHMSMOnly)
	{
		this.useDHMSMOnly = useDHMSMOnly;
	}
	
	public ArrayList<Integer> getNumOfCapabilitiesSupported(String system)
	{

		String capabilityGroup = "SELECT DISTINCT ?CapabilityFunctionalArea ?Capability WHERE {{?CapabilityFunctionalArea <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/CapabilityFunctionalArea>;}{?Utilizes <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Utilizes>;}{?CapabilityGroup <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/CapabilityGroup>;}{?Capability <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Capability>;}{?ConsistsOfCapability <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Consists>;}{?CapabilityFunctionalArea ?Utilizes ?CapabilityGroup;} {?CapabilityGroup ?ConsistsOfCapability ?Capability;}}";

		SesameJenaSelectWrapper sjswQuery = processQuery(capabilityGroup, (IEngine)DIHelper.getInstance().getLocalProp("HR_Core"));
		
		ArrayList<String> allHSDCapabilities = new ArrayList<String>();
		ArrayList<String> allHSSCapabilities = new ArrayList<String>();
		ArrayList<String> allFHPCapabilities = new ArrayList<String>();
		
		String[] vars = sjswQuery.getVariables();
		while(sjswQuery.hasNext())
		{
			SesameJenaSelectStatement sjss = sjswQuery.next();
			String group = sjss.getVar(vars[0]).toString();
			String cap = sjss.getVar(vars[1]).toString();
			if(group.contains("HSD"))
				allHSDCapabilities.add(cap);
			else if(group.contains("HSS"))
				allHSSCapabilities.add(cap);
			else if(group.contains("FHP"))
				allFHPCapabilities.add(cap);
		}
		
		
		ArrayList<ArrayList<String>> sysCreateCapCreate = getCapAndData(system,"C","C");
		ArrayList<ArrayList<String>> sysCreateCapRead = getCapAndData(system, "C","R");
		
		ArrayList<String> HSDCapabilities = new ArrayList<String>();
		ArrayList<String> HSSCapabilities = new ArrayList<String>();
		ArrayList<String> FHPCapabilities = new ArrayList<String>();
		
		for(ArrayList<String> row : sysCreateCapCreate)
		{
			String capability = row.get(0);
			if(!HSDCapabilities.contains(capability)&&allHSDCapabilities.contains(capability))
				HSDCapabilities.add(capability);
			else if(!HSSCapabilities.contains(capability)&&allHSSCapabilities.contains(capability))
				HSSCapabilities.add(capability);
			else if(!FHPCapabilities.contains(capability)&&allFHPCapabilities.contains(capability))
				FHPCapabilities.add(capability);
		}
		for(ArrayList<String> row : sysCreateCapRead)
		{
			String capability = row.get(0);
			if(!HSDCapabilities.contains(capability)&&allHSDCapabilities.contains(capability))
				HSDCapabilities.add(capability);
			else if(!HSSCapabilities.contains(capability)&&allHSSCapabilities.contains(capability))
				HSSCapabilities.add(capability);
			else if(!FHPCapabilities.contains(capability)&&allFHPCapabilities.contains(capability))
				FHPCapabilities.add(capability);
		}
		
		ArrayList<Integer> retVals = new ArrayList<Integer>();
		retVals.add(HSDCapabilities.size());
		retVals.add(HSSCapabilities.size());
		retVals.add(FHPCapabilities.size());
		
		return retVals;
	}

	public ArrayList<ArrayList<String>> getSysAndData(String cap, String capCRM, String sysCRM) 
	{
		return processSysOrCapAndData(cap, capCRM, sysCRM, dataListCapabilities, dataListSystems);
	}
	
	public ArrayList<ArrayList<String>> getCapAndData(String sys, String capCRM, String sysCRM)
	{
		return processSysOrCapAndData(sys, capCRM, sysCRM, dataListSystems, dataListCapabilities);
	}
	
	private ArrayList<ArrayList<String>> processSysOrCapAndData(String capOrSys, String capCRM, String sysCRM, 
			Hashtable<String, Hashtable<String, String>> searchList, 
			Hashtable<String, Hashtable<String, String>> getList ) 
	{
		ArrayList<ArrayList<String>> resultSet = new ArrayList<ArrayList<String>>();
		ArrayList<String> capDataList = new ArrayList<String>();

		for( String data : searchList.keySet() )
		{
			Hashtable<String, String> innerHash = searchList.get(data);
			if(innerHash.containsKey(capOrSys))
			{
				String crm = innerHash.get(capOrSys);
				if(capCRM.contains("C"))
				{
					if(crm.contains(capCRM) || crm.contains("M"))
					{
						capDataList.add(data);
					}
				}
				else
				{
					if(crm.contains(capCRM))
					{
						capDataList.add(data);
					}
				}
			}
		}

		for( String data : capDataList)
		{
			Hashtable<String, String> innerHash = getList.get(data);
			if(innerHash != null)
			{
				for( String sys : innerHash.keySet())
				{
					if(sysCRM.contains("C"))
					{				
						if(innerHash.get(sys).contains(sysCRM) || innerHash.get(sys).contains("M"))
						{
							ArrayList<String> innerArray = new ArrayList<String>();
							innerArray.add(sys);
							innerArray.add(data);
							resultSet.add(innerArray);
						}
					}
					else
					{
						if(innerHash.get(sys).contains(sysCRM))
						{
							ArrayList<String> innerArray = new ArrayList<String>();
							innerArray.add(sys);
							innerArray.add(data);
							resultSet.add(innerArray);
						}
					}
				}
			}

		}

		return resultSet;
	}
	
	public ArrayList<String> getDataObjectListSupportedFromSystem(String sys, String capCRM, String sysCRM)
	{
		return processDataObjectList(sys, capCRM, sysCRM, dataListCapabilities, dataListSystems);
	}
	
	public ArrayList<String> getDataObjectListSupportedFromCapabilities(String cap, String capCRM, String sysCRM)
	{
		return processDataObjectList(cap, capCRM, sysCRM, dataListSystems, dataListCapabilities);
	}
	
	private ArrayList<String> processDataObjectList(String capOrSys, String capCRM, String sysCRM, 
			Hashtable<String, Hashtable<String, String>> searchList, Hashtable<String, Hashtable<String, String>> getList) 
	{
		ArrayList<String> resultSet = new ArrayList<String>();
		ArrayList<String> capOrSysDataList = new ArrayList<String>();

		for( String data : searchList.keySet() )
		{
			Hashtable<String, String> innerHash = searchList.get(data);
			if(innerHash.containsKey(capOrSys))
			{
				String crm = innerHash.get(capOrSys);
				if(capCRM.contains("C"))
				{
					if(crm.contains(capCRM) || crm.contains("M"))
					{
						capOrSysDataList.add(data);
					}
				}
				else
				{
					if(crm.contains(capCRM))
					{
						capOrSysDataList.add(data);
					}
				}
			}
		}

		for( String data : capOrSysDataList)
		{
			Hashtable<String, String> innerHash = getList.get(data);
			if(innerHash != null)
			{
				for( String sys : innerHash.keySet())
				{
					if(sysCRM.contains("C"))
					{				
						if(innerHash.get(sys).contains(sysCRM) || innerHash.get(sys).contains("M"))
						{
							if(!resultSet.contains(data))
								resultSet.add(data);
						}
					}
					else
					{
						if(innerHash.get(sys).contains(sysCRM))
						{
							if(!resultSet.contains(data))
								resultSet.add(data);
						}
					}
				}
			}

		}

		return resultSet;
	}
	
	public ArrayList<String> getAllDataFromSys(String sys, String crm)
	{
		return processAllDataFromSysOrCap(sys, crm, dataListSystems);
	}
	
	public ArrayList<String> getAllDataFromCap(String cap, String crm)
	{
		return processAllDataFromSysOrCap(cap, crm, dataListCapabilities);
	}
	
	
	private ArrayList<String> processAllDataFromSysOrCap(String capOrSys, String crm, Hashtable<String, Hashtable<String, String>> dataList)
	{
		ArrayList<String> resultSet = new ArrayList<String>();
		
		for( String data : dataList.keySet() )
		{
			Hashtable<String, String> innerHash = dataList.get(data);
			if(innerHash.containsKey(capOrSys))
			{			
				if(crm.contains("C"))
				{
					String dataCRM = innerHash.get(capOrSys);
					if(dataCRM.contains(crm) || dataCRM.contains("M"))
					{
						resultSet.add(data);
					}
				}
				else
				{
					String dataCRM = innerHash.get(capOrSys);
					if(dataCRM.contains(crm))
					{
						resultSet.add(data);
					}
				}
			}
		}
		
		return resultSet;
	}

	private void processAllResults(SesameJenaSelectWrapper sjsw, boolean sys)
	{
		if(sys)
		{
			processResults(sjsw, dataListSystems);
		}
		else
		{
			processResults(sjsw, dataListCapabilities);
		}
	}
	
	private void processResults(SesameJenaSelectWrapper sjsw, Hashtable<String, Hashtable<String, String>> dataList)
	{
		String[] vars = sjsw.getVariables();
		while(sjsw.hasNext())
		{
			SesameJenaSelectStatement sjss = sjsw.next();
			String sub = sjss.getVar(vars[0]).toString();
			String obj = sjss.getVar(vars[1]).toString();
			String crm = sjss.getVar(vars[2]).toString();

			if(!dataList.containsKey(obj))
			{
				Hashtable<String, String> innerHash = new Hashtable<String, String>();
				innerHash.put(sub, crm);
				dataList.put(obj, innerHash);
			}
			else if(!dataList.get(obj).containsKey(sub))
			{
				Hashtable<String, String> innerHash = dataList.get(obj);
				innerHash.put(sub,  crm);
			}
			else
			{
				Hashtable<String, String> innerHash = dataList.get(obj);
				if((crm.equals("\"C\"") || crm.equals("\"M\"")) && innerHash.get(sub).equals("\"R\""))
				{
					innerHash.put(sub, crm);
				}
			}
		}
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
}
