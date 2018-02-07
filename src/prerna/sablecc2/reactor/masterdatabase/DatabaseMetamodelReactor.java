package prerna.sablecc2.reactor.masterdatabase;

import java.util.Map;
import prerna.nameserver.utility.MasterDatabaseUtility;
import prerna.sablecc2.om.GenRowStruct;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.sablecc2.reactor.AbstractReactor;

public class DatabaseMetamodelReactor extends AbstractReactor {
	
	public DatabaseMetamodelReactor() {
		this.keysToGet = new String[]{ReactorKeysEnum.DATABASE.getKey()};
	}

	@Override
	public NounMetadata execute() {
		GenRowStruct eGrs = this.store.getNoun(keysToGet[0]);
		if(eGrs == null) {
			throw new IllegalArgumentException("Need to define the database to get the concepts from");
		}
		if(eGrs.isEmpty()) {
			throw new IllegalArgumentException("Must define a single database");
		}
		if(eGrs.size() > 1) {
			throw new IllegalArgumentException("Can only define one database within this call");
		}
		String engineName = eGrs.get(0).toString();
		Map<String, Object[]> metamodelObject = MasterDatabaseUtility.getMetamodelRDBMS(engineName);
		return new NounMetadata(metamodelObject, PixelDataType.CUSTOM_DATA_STRUCTURE, PixelOperationType.DATABASE_METAMODEL);
	}

}
