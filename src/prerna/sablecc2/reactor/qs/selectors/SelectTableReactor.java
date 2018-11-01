package prerna.sablecc2.reactor.qs.selectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import prerna.ds.QueryStruct;
import prerna.nameserver.utility.MasterDatabaseUtility;
import prerna.query.querystruct.AbstractQueryStruct;
import prerna.query.querystruct.SelectQueryStruct;
import prerna.query.querystruct.selectors.QueryColumnSelector;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.reactor.qs.AbstractQueryStructReactor;

public class SelectTableReactor extends AbstractQueryStructReactor {	
	
	public SelectTableReactor() {
		this.keysToGet = new String[]{ReactorKeysEnum.TABLE.getKey()};
	}
	
	protected AbstractQueryStruct createQueryStruct() {
		organizeKeys();
		String appId = qs.getEngineId();
		String table  = this.keyValue.get(ReactorKeysEnum.TABLE.getKey());
		List<String> tables = new ArrayList();
		tables.add(table);
		// TODO need to clean this method to use id
		Map<String, HashMap> props = MasterDatabaseUtility.getConceptProperties(tables, appId);
		List<String> properties = (List<String>) props.get(MasterDatabaseUtility.getEngineAliasForId(appId)).get(table);
		if (properties != null && !properties.isEmpty()) {
			for (String column : properties) {
				QueryColumnSelector qsSelector = new QueryColumnSelector();
				qsSelector.setTable(table);
				qsSelector.setColumn(column);
				qsSelector.setAlias(column);
				qs.addSelector(qsSelector);
			}
			// add prim key
			System.out.println("TEST");
			QueryColumnSelector qsSelector = new QueryColumnSelector();
			qsSelector.setTable(table);
			qsSelector.setColumn(SelectQueryStruct.PRIM_KEY_PLACEHOLDER);
			qsSelector.setAlias(table);
			qs.addSelector(qsSelector);

		}
		
		return qs;
	}
}
