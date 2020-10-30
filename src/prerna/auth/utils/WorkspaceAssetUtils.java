package prerna.auth.utils;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import prerna.auth.AccessToken;
import prerna.auth.AuthProvider;
import prerna.auth.User;
import prerna.cluster.util.CloudClient;
import prerna.cluster.util.ClusterUtil;
import prerna.ds.util.RdbmsQueryBuilder;
import prerna.engine.api.IEngine;
import prerna.engine.api.IRawSelectWrapper;
import prerna.engine.impl.SmssUtilities;
import prerna.engine.impl.app.AppEngine;
import prerna.engine.impl.rdbms.RDBMSNativeEngine;
import prerna.query.querystruct.SelectQueryStruct;
import prerna.query.querystruct.filters.SimpleQueryFilter;
import prerna.query.querystruct.selectors.QueryColumnSelector;
import prerna.rdf.engine.wrappers.WrapperManager;
import prerna.sablecc2.reactor.app.upload.UploadUtilities;
import prerna.util.Constants;
import prerna.util.DIHelper;
import prerna.util.Utility;
import prerna.util.git.GitRepoUtils;

public class WorkspaceAssetUtils extends AbstractSecurityUtils {
	
	private static final String FS = java.nio.file.FileSystems.getDefault().getSeparator();
	
	public static final String WORKSPACE_APP_NAME = "Workspace";
	public static final String ASSET_APP_NAME = "Asset";
	public static final String HIDDEN_FILE = ".semoss";
	
	WorkspaceAssetUtils() {
		super();
	}
	
	
	//////////////////////////////////////////////////////////////////////
	// Creating workspace and asset metadata 
	//////////////////////////////////////////////////////////////////////
	
	/**
	 * Create the user workspace app for the provided access token
	 * @param token
	 * @return
	 * @throws Exception 
	 */
	public static String createUserWorkspaceApp(AccessToken token) throws Exception {
		String appId = createEmptyApp(token, WORKSPACE_APP_NAME, false);
		registerUserWorkspaceApp(token, appId);
		return appId;
	}
	
	/**
	 * Create the user workspace app for the provided user and auth token
	 * @param user
	 * @param token
	 * @return
	 * @throws Exception 
	 */
	public static String createUserWorkspaceApp(User user, AuthProvider token) throws Exception {
		return createUserWorkspaceApp(user.getAccessToken(token));
	}
	
	/**
	 * Create the user asset app for the provided access token
	 * @param token
	 * @return
	 * @throws Exception 
	 */
	public static String createUserAssetApp(AccessToken token) throws Exception {
		String appId = createEmptyApp(token, ASSET_APP_NAME, true);
		registerUserAssetApp(token, appId);
		return appId;
	}
	
	/**
	 * Create the user asset app for the provided user and auth token
	 * @param user
	 * @param token
	 * @return
	 * @throws Exception 
	 */
	public static String createUserAssetApp(User user, AuthProvider token) throws Exception {
		return createUserAssetApp(user.getAccessToken(token));
	}
	
	// TODO >>>timb: WORKSPACE - DONE - look at GenerateEmptyAppReactor, use AppEngine
	/**
	 * Generate 
	 * @param token
	 * @param appName
	 * @param ignoreAsset
	 * @return
	 * @throws Exception
	 */
	private static String createEmptyApp(AccessToken token, String appName, boolean ignoreAsset) throws Exception {
		// Create a new app id
		String appId = UUID.randomUUID().toString();

		// Create the app folder
		String baseFolder = DIHelper.getInstance().getProperty("BaseFolder");
		String appLocation = baseFolder + FS + "db" + FS + SmssUtilities.getUniqueName(appName, appId);
		File appFolder = new File(appLocation);
		appFolder.mkdirs();
		
		// Create the insights database
		RDBMSNativeEngine insightDb = UploadUtilities.generateInsightsDatabase(appId, appName);

		// Add database into DIHelper so that the web watcher doesn't try to load as well
		File tempSmss = UploadUtilities.createTemporaryAppSmss(appId, appName, ignoreAsset);
		DIHelper.getInstance().getCoreProp().setProperty(appId + "_" + Constants.STORE, tempSmss.getAbsolutePath());
		
		// Add the app to security db
		if(!ignoreAsset) {
			SecurityUpdateUtils.addApp(appId, false);
			SecurityUpdateUtils.addEngineOwner(appId, token.getId());
		}
		
		// Create the app engine
		AppEngine appEng = new AppEngine();
		appEng.setEngineId(appId);
		appEng.setEngineName(appName);
		appEng.setInsightDatabase(insightDb);
		
		// Only at end do we add to DIHelper
		DIHelper.getInstance().setLocalProperty(appId, appEng);
		String appNames = (String) DIHelper.getInstance().getLocalProp(Constants.ENGINES);
		appNames = appNames + ";" + appId;
		DIHelper.getInstance().setLocalProperty(Constants.ENGINES, appNames);
		
		// adding all the git here
		String versionFolder = appFolder.getAbsolutePath() + FS + "version";
		File file = new File(versionFolder);
		if (!file.exists()) {
			file.mkdir();
		}
		// I will assume the directory is there now
		GitRepoUtils.init(versionFolder);
		
		// Rename .temp to .smss
		File smssFile = new File(tempSmss.getAbsolutePath().replace(".temp", ".smss"));
		FileUtils.copyFile(tempSmss, smssFile);
		tempSmss.delete();
		
		// Update engine smss file location
		appEng.setPropFile(smssFile.getAbsolutePath());
		
		if (ClusterUtil.IS_CLUSTER) {
			CloudClient.getClient().pushApp(appId);
		}
		
		DIHelper.getInstance().getCoreProp().setProperty(appId + "_" + Constants.STORE, smssFile.getAbsolutePath());
		DIHelper.getInstance().setLocalProperty(appId, appEng);
		
		return appId;
	}
	
	
	//////////////////////////////////////////////////////////////////////
	// Updating workspace and asset metadata 
	//////////////////////////////////////////////////////////////////////
	// TODO >>>timb: WORKSPACE - DONE - register workspace

	/**
	 * Register the user workspace app for the provided access token and app id
	 * @param token
	 * @param appId
	 * @throws SQLException 
	 */
	public static void registerUserWorkspaceApp(AccessToken token, String appId) throws SQLException {
		String[] colNames = new String[] {"TYPE", "USERID", "ENGINEID"};
		String[] types = new String[] {"varchar(255)", "varchar(255)", "varchar(255)"};
		String insertQuery = RdbmsQueryBuilder.makeInsert("WORKSPACEENGINE", colNames, types, 
				new String[] {	token.getProvider().name(), 
								token.getId(), 
								appId});
		securityDb.insertData(insertQuery);
		securityDb.commit();
	}
	
	/**
	 * Register the user workspace app for the provided user, auth provider, and app id
	 * @param user
	 * @param provider
	 * @param appId
	 * @throws SQLException 
	 */
	public static void registerUserWorkspaceApp(User user, AuthProvider provider, String appId) throws SQLException {
		registerUserWorkspaceApp(user.getAccessToken(provider), appId);
	}
	
	/**
	 * Register the user asset app for the provided access token and app id
	 * @param token
	 * @param appId
	 * @throws SQLException 
	 */
	public static void registerUserAssetApp(AccessToken token, String appId) throws SQLException {
		String[] colNames = new String[] {"TYPE", "USERID", "ENGINEID"};
		String[] types = new String[] {"varchar(255)", "varchar(255)", "varchar(255)"};
		String insertQuery = RdbmsQueryBuilder.makeInsert("ASSETENGINE", colNames, types, 
				new String[] {	token.getProvider().name(), 
								token.getId(), 
								appId});
		securityDb.insertData(insertQuery);
		securityDb.commit();
	}
	
	/**
	 * Register the user asset app for the provided user, auth provider, and app id
	 * @param user
	 * @param provider
	 * @param appId
	 * @throws SQLException 
	 */
	public static void registerUserAssetApp(User user, AuthProvider provider, String appId) throws SQLException {
		registerUserAssetApp(user.getAccessToken(provider), appId);
	}

	
	//////////////////////////////////////////////////////////////////////
	// Querying workspace and asset metadata 
	//////////////////////////////////////////////////////////////////////
	
	/**
	 * Get the user workspace app for the provided access token; returns null if there is none
	 * @param token
	 * @return
	 */
	public static String getUserWorkspaceApp(AccessToken token) {
//		String query = "SELECT ENGINEID FROM WORKSPACEENGINE WHERE "
//				+ "TYPE = '" + token.getProvider().name() + "' AND "
//				+ "USERID = '" + token.getId() + "'"
//				;
//		IRawSelectWrapper wrapper = WrapperManager.getInstance().getRawWrapper(securityDb, query);
		
		SelectQueryStruct qs = new SelectQueryStruct();
		qs.addSelector(new QueryColumnSelector("WORKSPACEENGINE__ENGINEID"));
		qs.addExplicitFilter(SimpleQueryFilter.makeColToValFilter("WORKSPACEENGINE__TYPE", "==", token.getProvider().name()));
		qs.addExplicitFilter(SimpleQueryFilter.makeColToValFilter("WORKSPACEENGINE__USERID", "==", token.getId()));
		IRawSelectWrapper wrapper = null;
		try {
			wrapper = WrapperManager.getInstance().getRawWrapper(securityDb, qs);
			if (wrapper.hasNext()) {
				 Object rs = wrapper.next().getValues()[0];
				 if (rs == null){
					 return null;
				 }
				return rs.toString();
				//return wrapper.next().getValues()[0].toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(wrapper != null) {
				wrapper.cleanUp();
			}
		}
		
		return null;
	}
	
	/**
	 * Get the user workspace app for the provided user and auth provider; returns null if is there is none
	 * @param user
	 * @param provider
	 * @return
	 */
	public static String getUserWorkspaceApp(User user, AuthProvider provider) {
		return getUserWorkspaceApp(user.getAccessToken(provider));
	}
	
	/**
	 * Get the user asset app for the provided access token; returns null if there is none
	 * @param user
	 * @param token
	 * @return
	 */
	public static String getUserAssetApp(AccessToken token) {
//		String query = "SELECT ENGINEID FROM ASSETENGINE WHERE "
//				+ "TYPE = '" + token.getProvider().name() + "' AND "
//				+ "USERID = '" + token.getId() + "'"
//				;
//		IRawSelectWrapper wrapper = WrapperManager.getInstance().getRawWrapper(securityDb, query);
		
		SelectQueryStruct qs = new SelectQueryStruct();
		qs.addSelector(new QueryColumnSelector("ASSETENGINE__ENGINEID"));
		qs.addExplicitFilter(SimpleQueryFilter.makeColToValFilter("ASSETENGINE__TYPE", "==", token.getProvider().name()));
		qs.addExplicitFilter(SimpleQueryFilter.makeColToValFilter("ASSETENGINE__USERID", "==", token.getId()));
		IRawSelectWrapper wrapper = null;
		try {
			wrapper = WrapperManager.getInstance().getRawWrapper(securityDb, qs);
			if (wrapper.hasNext()) {
				 Object rs = wrapper.next().getValues()[0];
				 if (rs == null){
					 return null;
				 }
				return rs.toString();
				//return wrapper.next().getValues()[0].toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(wrapper != null) {
				wrapper.cleanUp();
			}
		}
		
		return null;
	}
	
	/**
	 * Get the user asset app for the provided user and auth provider; returns null if there is none
	 * @param user
	 * @param provider
	 * @return
	 */
	public static String getUserAssetApp(User user, AuthProvider provider) {
		return getUserAssetApp(user.getAccessToken(provider));
	}
	
	/**
	 * See if the app is a workspace or asset app
	 * @param appId
	 * @return
	 */
	public static boolean isAssetOrWorkspaceApp(String appId) {
		SelectQueryStruct qs = new SelectQueryStruct();
		qs.addSelector(new QueryColumnSelector("WORKSPACEENGINE__ENGINEID"));
		qs.addExplicitFilter(SimpleQueryFilter.makeColToValFilter("WORKSPACEENGINE__ENGINEID", "==", appId));
		IRawSelectWrapper wrapper = null;
		try {
			wrapper = WrapperManager.getInstance().getRawWrapper(securityDb, qs);
			if (wrapper.hasNext()) {
				return true;
			} else {
				return isAssetApp(appId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(wrapper != null) {
				wrapper.cleanUp();
			}
		}
		
		return false;
	}
	
	/**
	 * Is the app an asset
	 * @param appId
	 * @return
	 */
	public static boolean isAssetApp(String appId) {
		SelectQueryStruct qs = new SelectQueryStruct();
		qs.addSelector(new QueryColumnSelector("ASSETENGINE__ENGINEID"));
		qs.addExplicitFilter(SimpleQueryFilter.makeColToValFilter("ASSETENGINE__ENGINEID", "==", appId));
		IRawSelectWrapper wrapper = null;
		try {
			wrapper = WrapperManager.getInstance().getRawWrapper(securityDb, qs);
			return wrapper.hasNext();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(wrapper != null) {
				wrapper.cleanUp();
			}
		}

		return false;
	}
	
	
	//////////////////////////////////////////////////////////////////////
	// Asset folder locations
	//////////////////////////////////////////////////////////////////////
	public static String getUserAssetRootDirectory(User user, AuthProvider provider) {
		String assetAppId = user.getAssetEngineId(provider);
		if (assetAppId != null) {
			IEngine assetEngine = Utility.getEngine(assetAppId);
			if (assetEngine != null) {
				String assetAppName = assetEngine.getEngineName();
				if (assetAppName != null) {
					return DIHelper.getInstance().getProperty(Constants.BASE_FOLDER) + FS + "db" + FS + assetAppName + "__" + assetAppId + FS + "version";
				}
			}
		}
		return null;
	}

}
