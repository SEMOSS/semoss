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
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;

import prerna.ui.components.GridFilterData;

/**
 * This class is used to calculate TAP system specific costs.
 */
public class PfTapSystemSpecificCalculator {
	
	Logger logger = Logger.getLogger(getClass());
	
	GridFilterData gfd = new GridFilterData();
	public Date startDate = null;
	public Date lastDate = null;
	Hashtable yearIdxTable = new Hashtable();
	Hashtable systemPhaseEstimate = new Hashtable();
	Hashtable systemPhaseDate = new Hashtable();
	Double semanticRate = 0.4;
	ArrayList runTypes = new ArrayList();
	boolean semantics = false;
	Boolean serviceBoolean;
	
	/**
	 * Constructor for PfTapSystemSpecificCalculator.
	 */
	public PfTapSystemSpecificCalculator()
	{
		
	}
	
	/**
	 * Sets the types.
	 * @param types 	List of types.
	 */
	public void setTypes(ArrayList types){
		runTypes = types;
	}

	/**
	 * Sets the semantics boolean.
	 * @param sem 	True if it is a semantic.
	 */
	public void setSemantics(boolean sem){
		semantics = sem;
	}
	/**
	 * Sets service boolean.
	 * @param ser 	True if it is a service.
	 */
	public void setServiceBoolean(boolean ser){
		serviceBoolean = ser;
	}
	
	/**
	 * Processes data given a list of systems.
	 * Calculates the system phase estimate and system phase date given the phase index, LOE, start date, end date, and data federation indices.
	 * @param systemV String[]	List of systems
	 */
	public void processData(String[] systemV)
	{
		PfTapFinancialOrganizerFinal tapOrg = new PfTapFinancialOrganizerFinal();
		for (int sysIdx = 0; sysIdx < systemV.length ; sysIdx++){
			String system = systemV[sysIdx];
				
			Vector phaseV = new Vector();
			phaseV.addElement("Requirements");
			phaseV.addElement("Design");
			phaseV.addElement("Develop");
			phaseV.addElement("Test");
			phaseV.addElement("Deploy");
			Double sdlcTotal = 0.0;
			Double dataFedTotal = 0.0;
			EstimationCalculationFunctions pfCalc= new EstimationCalculationFunctions();
			
			int nullCount = 0;
			
			pfCalc.setHourlyRate(tapOrg.hourlyRate);
			pfCalc.setTypes(runTypes);
			pfCalc.setServiceBoolean(serviceBoolean);
			ArrayList <Object[]> phaseReturnList = pfCalc.processPhaseData(system);
			int phaseIdx = 0;
			int highestLOESetIdx = 1;
			int startDateIdx = 2;
			int endDateIdx = 3;
			int totalLOEIdx = 4;
			int dataFedIdx = 5;
			for (int i = 0; i<phaseReturnList.size(); i++)
			{
				Object[] phaseReturnArray = phaseReturnList.get(i);

				Double phaseLoeTotal = (Double) phaseReturnArray[totalLOEIdx];
				Date phaseStartDate = (Date) phaseReturnArray[startDateIdx];
				Double dataFedPhaseTotal = (Double) phaseReturnArray[dataFedIdx];
				//if loeTotal is not null, add loe for phase and fiscal year for phase
				if(phaseLoeTotal!=null){
					sdlcTotal = sdlcTotal+phaseLoeTotal;
					systemPhaseEstimate.put((String)phaseV.get(i), phaseLoeTotal);
					systemPhaseDate.put((String)phaseV.get(i), pfCalc.retFiscalYear(phaseStartDate));
				}
				//if loeTotal is null, add loe total as 0 and end date as start date.
				else{

					phaseLoeTotal = 0.0;
					systemPhaseEstimate.put((String)phaseV.get(i), 0.0);
					systemPhaseDate.put((String)phaseV.get(i), 2014);
				}
				if(dataFedPhaseTotal!=null)
				{
					dataFedTotal = dataFedTotal+dataFedPhaseTotal;
				}
			}
			
			Object[] semString = pfCalc.getSysSemData(system);
			Object[] phaseReturnDevelopArray = phaseReturnList.get(2);
			Date semDate = (Date) phaseReturnDevelopArray[startDateIdx];
			Double semLOE = dataFedTotal*semanticRate;
			systemPhaseDate.put("Semantics", 2014);
			if (semDate != null)
			{
				systemPhaseDate.put("Semantics", pfCalc.retFiscalYear(semDate));
			}
			systemPhaseEstimate.put("Semantics", semLOE);

			
			Object[] trString = pfCalc.getSysTrainingData(system);
			systemPhaseEstimate.put("Training", new Double(0.0));
			systemPhaseDate.put("Training", 2014);
			if (trString != null)
			{
				//get date of develop
				Object[] phaseReturnArray = phaseReturnList.get(4);
				Date trDate = (Date) phaseReturnArray[startDateIdx];
				Double trainingDouble = sdlcTotal*0.15;
				systemPhaseEstimate.put("Training", trainingDouble);
				if(trDate != null)
				{
					systemPhaseDate.put("Training", pfCalc.retFiscalYear(trDate));
				}
			}
			//if all 5 phases are null and training and semantics are null, display null error.
			/*if(nullCount == 7) {
				showError("The selected RDF store does not contain financial information for this system. " +
						"\nPlease select a different system or RDF store and try again.");
			}*/
			//logger.info(systemPhaseEstimate);
			//logger.info(systemPhaseDate);
			tapOrg.preparePhaseTasks(systemPhaseEstimate, systemPhaseDate, system);
		}
		tapOrg.createGrid();
		
	}
	
}
