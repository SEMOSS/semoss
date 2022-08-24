package prerna.solr.reactor;

import java.util.List;
import java.util.Map;

import prerna.auth.utils.AbstractSecurityUtils;
import prerna.auth.utils.SecurityDatabaseUtils;
import prerna.auth.utils.SecurityQueryUtils;
import prerna.nameserver.utility.MasterDatabaseUtility;
import prerna.sablecc2.om.GenRowStruct;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.sablecc2.reactor.AbstractReactor;

public class DatabaseInfoReactor extends AbstractReactor {
	
	private static final String META_KEYS = "metakey";
	
	public DatabaseInfoReactor() {
		this.keysToGet = new String[]{ReactorKeysEnum.DATABASE.getKey(), META_KEYS};
	}

	@Override
	public NounMetadata execute() {
		organizeKeys();
		String databaseId = this.keyValue.get(this.keysToGet[0]);
		
		if(databaseId == null || databaseId.isEmpty()) {
			throw new IllegalArgumentException("Must input an database id");
		}
		
		List<Map<String, Object>> baseInfo = null;
		if(AbstractSecurityUtils.securityEnabled()) {
			// make sure valid id for user
			databaseId = SecurityQueryUtils.testUserDatabaseIdForAlias(this.insight.getUser(), databaseId);
			if(SecurityDatabaseUtils.userCanViewDatabase(this.insight.getUser(), databaseId)) {
				// user has access!
				baseInfo = SecurityDatabaseUtils.getUserDatabaseList(this.insight.getUser(), databaseId);
			} else if(SecurityDatabaseUtils.databaseIsDiscoverable(databaseId)) {
				baseInfo = SecurityDatabaseUtils.getDiscoverableDatabaseList(databaseId);
			} else {
				// you dont have access
				throw new IllegalArgumentException("Database does not exist or user does not have access to the database");
			}
		} else {
			databaseId = MasterDatabaseUtility.testDatabaseIdIfAlias(databaseId);
			// just grab the info
			baseInfo = SecurityDatabaseUtils.getAllDatabaseList(databaseId);
		}
		
		if(baseInfo == null || baseInfo.isEmpty()) {
			throw new IllegalArgumentException("Could not find any database data");
		}
		
		// we filtered to a single database
		Map<String, Object> databaseInfo = baseInfo.get(0);
		databaseInfo.putAll(SecurityDatabaseUtils.getAggregateDatabaseMetadata(databaseId, getMetaKeys(), true));
		return new NounMetadata(databaseInfo, PixelDataType.CUSTOM_DATA_STRUCTURE, PixelOperationType.DATABASE_INFO);
	}
	
	private List<String> getMetaKeys() {
		GenRowStruct grs = this.store.getNoun(META_KEYS);
		if(grs != null && !grs.isEmpty()) {
			return grs.getAllStrValues();
		}
		
		return null;
	}

}