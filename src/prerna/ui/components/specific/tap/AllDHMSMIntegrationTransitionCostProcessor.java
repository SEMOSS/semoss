/*******************************************************************************
 * Copyright 2014 SEMOSS.ORG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package prerna.ui.components.specific.tap;

import java.util.HashSet;

import prerna.error.EngineException;
import prerna.error.FileReaderException;
import prerna.rdf.engine.api.IEngine;
import prerna.util.DHMSMTransitionUtility;
import prerna.util.DIHelper;

public class AllDHMSMIntegrationTransitionCostProcessor {

	public void runAllReports() throws EngineException, FileReaderException{
		IEngine hrCore = (IEngine) DIHelper.getInstance().getLocalProp("HR_Core");
		if(hrCore==null) {
				throw new EngineException("Database not found");
		}
		
		HashSet<String> lpiSystemList = DHMSMTransitionUtility.runRawVarListQuery(hrCore, DHMSMTransitionUtility.LPI_SYS_QUERY);
		DHMSMIntegrationTransitionCostWriter writer = new DHMSMIntegrationTransitionCostWriter();
		for(String sysURI: lpiSystemList) {
			writer.setSysURI(sysURI);
			writer.calculateValuesForReport();
			writer.writeToExcel();
		}
	}
}
