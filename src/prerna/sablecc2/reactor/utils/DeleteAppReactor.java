package prerna.sablecc2.reactor.utils;

import java.util.List;
import java.util.Vector;

import prerna.auth.AbstractSecurityUtils;
import prerna.auth.SecurityQueryUtils;
import prerna.auth.SecurityUpdateUtils;
import prerna.auth.User;
import prerna.engine.api.IEngine;
import prerna.nameserver.DeleteFromMasterDB;
import prerna.nameserver.utility.MasterDatabaseUtility;
import prerna.sablecc2.om.GenRowStruct;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.sablecc2.reactor.AbstractReactor;
import prerna.util.Constants;
import prerna.util.DIHelper;
import prerna.util.Utility;

public class DeleteAppReactor extends AbstractReactor {

	public DeleteAppReactor() {
		this.keysToGet = new String[] { ReactorKeysEnum.APP.getKey() };
	}

	@Override
	public NounMetadata execute() {
		List<String> appIds = getAppIds();
		for (String appId : appIds) {
			User user = this.insight.getUser();
			String userId = user.getAccessToken(user.getLogins().get(0)).getId();
			
			// we may have the alias
			if(AbstractSecurityUtils.securityEnabled()) {
				appId = SecurityQueryUtils.testUserEngineIdForAlias(this.insight.getUser(), appId);
				if(!SecurityQueryUtils.isUserAdmin(userId) || !SecurityQueryUtils.isUserDatabaseOwner(userId, appId)) {
					throw new IllegalArgumentException("App " + appId + " does not exist or user does not have permissions to database");
				}
			} else {
				appId = MasterDatabaseUtility.testEngineIdIfAlias(appId);
				if(!MasterDatabaseUtility.getAllEngineIds().contains(appId)) {
					throw new IllegalArgumentException("App " + appId + " does not exist");
				}
			}

			IEngine engine = Utility.getEngine(appId);
			deleteEngine(engine);
		}

		return new NounMetadata(true, PixelDataType.BOOLEAN, PixelOperationType.DELETE_ENGINE);
	}

	/**
	 * 
	 * @param coreEngine
	 * @return
	 */
	private boolean deleteEngine(IEngine coreEngine) {
		String engineId = coreEngine.getEngineId();
		coreEngine.deleteDB();

		// remove from dihelper... this is absurd
		String engineNames = (String) DIHelper.getInstance().getLocalProp(Constants.ENGINES);
		engineNames = engineNames.replace(";" + engineId, "");
		// in case it was the first engine loaded
		engineNames = engineNames.replace(engineId + ";", "");
		DIHelper.getInstance().setLocalProperty(Constants.ENGINES, engineNames);

		DeleteFromMasterDB remover = new DeleteFromMasterDB();
		remover.deleteEngineRDBMS(engineId);
		SecurityUpdateUtils.deleteApp(engineId);
		return true;
	}

	/**
	 * Get inputs
	 * @return list of engines to delete
	 */
	public List<String> getAppIds() {
		List<String> appIds = new Vector<String>();

		// see if added as key
		GenRowStruct grs = this.store.getNoun(this.keysToGet[0]);
		if (grs != null && !grs.isEmpty()) {
			int size = grs.size();
			for (int i = 0; i < size; i++) {
				appIds.add(grs.get(i).toString());
			}
			return appIds;
		}

		// no key is added, grab all inputs
		int size = this.curRow.size();
		for (int i = 0; i < size; i++) {
			appIds.add(this.curRow.get(i).toString());
		}
		return appIds;
	}
}
