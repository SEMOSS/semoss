package prerna.auth.utils;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import prerna.auth.AccessToken;
import prerna.auth.EnginePermission;
import prerna.auth.User;
import prerna.ds.util.RdbmsQueryBuilder;
import prerna.engine.api.IRawSelectWrapper;
import prerna.engine.impl.SmssUtilities;
import prerna.engine.impl.rdbms.RDBMSNativeEngine;
import prerna.rdf.engine.wrappers.WrapperManager;
import prerna.util.Constants;
import prerna.util.DIHelper;
import prerna.util.Utility;

public class SecurityUpdateUtils extends AbstractSecurityUtils {

	private static final Logger LOGGER = Logger.getLogger(SecurityUpdateUtils.class);
	
	/**
	 * Only used for static references
	 */
	private SecurityUpdateUtils() {
		
	}
	
	/**
	 * Add an entire engine into the security db
	 * @param appId
	 */
	public static void addApp(String appId) {
		if(ignoreEngine(appId)) {
			// dont add local master or security db to security db
			return;
		}
		String smssFile = DIHelper.getInstance().getCoreProp().getProperty(appId + "_" + Constants.STORE);
		Properties prop = Utility.loadProperties(smssFile);
		
		boolean global = true;
		if(prop.containsKey(Constants.HIDDEN_DATABASE) && "true".equalsIgnoreCase(prop.get(Constants.HIDDEN_DATABASE).toString().trim()) ) {
			global = false;
		}
		
		addApp(appId, global);
	}
	
	/**
	 * Add an entire engine into the security db
	 * @param appId
	 */
	public static void addApp(String appId, boolean global) {
		if(ignoreEngine(appId)) {
			// dont add local master or security db to security db
			return;
		}
		String smssFile = DIHelper.getInstance().getCoreProp().getProperty(appId + "_" + Constants.STORE);
		Properties prop = Utility.loadProperties(smssFile);

		String appName = prop.getProperty(Constants.ENGINE_ALIAS);
		if(appName == null) {
			appName = appId;
		}
		
		boolean reloadInsights = false;
		if(prop.containsKey(Constants.RELOAD_INSIGHTS)) {
			String booleanStr = prop.get(Constants.RELOAD_INSIGHTS).toString();
			reloadInsights = Boolean.parseBoolean(booleanStr);
		}
		
		String[] typeAndCost = getAppTypeAndCost(prop);
		boolean engineExists = containsEngineId(appId);
		if(engineExists && !reloadInsights) {
			LOGGER.info("Security database already contains app with alias = " + appName);
			// update engine properties anyway ... in case global was shifted for example
//			updateEngine(appId, appName, typeAndCost[0], typeAndCost[1], global);
			return;
		} else if(!engineExists) {
			addEngine(appId, appName, typeAndCost[0], typeAndCost[1], global);
		} else if(engineExists) {
			// delete values if currently present
			deleteInsightsForRecreation(appId);
			// update engine properties anyway ... in case global was shifted for example
			updateEngine(appId, appName, typeAndCost[0], typeAndCost[1], global);
		}
		
		File dbfile = SmssUtilities.getInsightsRdbmsFile(prop);
		String dbLocation = dbfile.getAbsolutePath();
		String jdbcURL = "jdbc:h2:" + dbLocation.replace(".mv.db", "") + ";query_timeout=180000;early_filter=true;query_cache_size=24;cache_size=32768";
		String userName = "sa";
		String password = "";
		RDBMSNativeEngine rne = new RDBMSNativeEngine();
		rne.setEngineId(appId + "_InsightsRDBMS");
		rne.makeConnection(jdbcURL, userName, password);
		
		// i need to delete any current insights for the app
		// before i start to insert new insights
		String deleteQuery = "DELETE FROM INSIGHT WHERE ENGINEID='" + appId + "'";
		try {
			securityDb.removeData(deleteQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// make a prepared statement
		PreparedStatement ps = securityDb.bulkInsertPreparedStatement(
				new String[]{"INSIGHT","ENGINEID","INSIGHTID","INSIGHTNAME","GLOBAL","EXECUTIONCOUNT","CREATEDON","LASTMODIFIEDON","LAYOUT"});
		// keep a batch size so we dont get heapspace
		final int batchSize = 5000;
		int count = 0;
		
		LocalDateTime now = LocalDateTime.now();
		
		String query = "SELECT DISTINCT ID, QUESTION_NAME, QUESTION_LAYOUT, HIDDEN_INSIGHT FROM QUESTION_ID WHERE HIDDEN_INSIGHT=false";
		IRawSelectWrapper wrapper = WrapperManager.getInstance().getRawWrapper(rne, query);
		while(wrapper.hasNext()) {
			Object[] row = wrapper.next().getValues();
			try {
				ps.setString(1, appId);
				ps.setString(2, row[0].toString());
				ps.setString(3, row[1].toString());
				ps.setBoolean(4, !((boolean) row[3]));
				ps.setLong(5, 0);
				ps.setTimestamp(6, java.sql.Timestamp.valueOf(now));
				ps.setTimestamp(7, java.sql.Timestamp.valueOf(now));
				ps.setString(8, row[2].toString());
				ps.addBatch();
				
				// batch commit based on size
				if (++count % batchSize == 0) {
					LOGGER.info("Executing batch .... row num = " + count);
					ps.executeBatch();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		// well, we are done looping through now
		LOGGER.info("Executing final batch .... row num = " + count);
		try {
			ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		} // insert any remaining records
		try {
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(reloadInsights) {
			LOGGER.info("Modifying force reload to false");
			Utility.changePropMapFileValue(smssFile, Constants.RELOAD_INSIGHTS, "false");	
		}
		
		LOGGER.info("Finished adding engine = " + appId);
	}
	
	/**
	 * Utility method to get the engine type and cost for storage
	 * @param prop
	 * @return
	 */
	public static String[] getAppTypeAndCost(Properties prop) {
		String app_type = null;
		String app_cost = null;
		// the whole app cost stuff is completely made up...
		// but it will look cool so we are doing it
		String eType = prop.getProperty(Constants.ENGINE_TYPE);
		if(eType.equals("prerna.engine.impl.rdbms.RDBMSNativeEngine")) {
			String rdbmsType = prop.getProperty(Constants.RDBMS_TYPE);
			if(rdbmsType == null) {
				rdbmsType = "H2_DB";
			}
			rdbmsType = rdbmsType.toUpperCase();
			app_type = rdbmsType;
			if(rdbmsType.equals("TERADATA") || rdbmsType.equals("DB2")) {
				app_cost = "$$";
			} else {
				app_cost = "";
			}
		} else if(eType.equals("prerna.engine.impl.rdbms.ImpalaEngine")) {
			app_type = "IMPALA";
			app_cost = "$$$";
		} else if(eType.equals("prerna.engine.impl.rdf.BigDataEngine")) {
			app_type = "RDF";
			app_cost = "";
		} else if(eType.equals("prerna.engine.impl.rdf.RDFFileSesameEngine")) {
			app_type = "RDF";
			app_cost = "";
		} else if(eType.equals("prerna.ds.datastax.DataStaxGraphEngine")) {
			app_type = "DATASTAX";
			app_cost = "$$$";
		} else if(eType.equals("prerna.engine.impl.solr.SolrEngine")) {
			app_type = "SOLR";
			app_cost = "$$";
		} else if(eType.equals("prerna.engine.impl.tinker.TinkerEngine")) {
			String tinkerDriver = prop.getProperty(Constants.TINKER_DRIVER);
			if(tinkerDriver.equalsIgnoreCase("neo4j")) {
				app_type = "NEO4J";
				app_cost = "";
			} else {
				app_type = "TINKER";
				app_cost = "";
			}
		} else if(eType.equals("prerna.engine.impl.json.JsonAPIEngine") || eType.equals("prerna.engine.impl.json.JsonAPIEngine2")) {
			app_type = "JSON";
			app_cost = "";
		} else if(eType.equals("prerna.engine.impl.app.AppEngine")) {
			app_type = "APP";
			app_cost = "$";
		}
		
		return new String[]{app_type, app_cost};
	}
	
	/**
	 * Delete just the insights for an engine
	 * @param appId
	 */
	public static void deleteInsightsForRecreation(String appId) {
		String deleteQuery = "DELETE FROM INSIGHT WHERE ENGINEID='" + appId + "'";
		try {
			securityDb.removeData(deleteQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Delete all values
	 * @param appId
	 */
	public static void deleteApp(String appId) {
		if(ignoreEngine(appId)) {
			// dont add local master or security db to security db
			return;
		}
		String deleteQuery = "DELETE FROM ENGINE WHERE ENGINEID='" + appId + "'";
		try {
			securityDb.removeData(deleteQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		deleteQuery = "DELETE FROM INSIGHT WHERE ENGINEID='" + appId + "'";
		try {
			securityDb.removeData(deleteQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		deleteQuery = "DELETE FROM ENGINEPERMISSION WHERE ENGINEID='" + appId + "'";
		try {
			securityDb.removeData(deleteQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		deleteQuery = "DELETE FROM ENGINEMETA WHERE ENGINEID='" + appId + "'";
		try {
			securityDb.removeData(deleteQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}

//		//TODO: add the other tables...
		boolean securityEnabled = Boolean.parseBoolean(DIHelper.getInstance().getLocalProp(Constants.SECURITY_ENABLED).toString());
		if(securityEnabled){
			removeDb(appId);
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////

	/*
	 * Adding engine
	 */
	
	/**
	 * Add an engine into the security database
	 * Default to set as not global
	 */
	public static void addEngine(String engineId, String engineName, String engineType, String engineCost) {
		addEngine(engineId, engineName, engineType, engineCost, !securityEnabled);
	}
	
	public static void addEngine(String engineId, String engineName, String engineType, String engineCost, boolean global) {
		String query = "INSERT INTO ENGINE (ENGINENAME, ENGINEID, TYPE, COST, GLOBAL) "
				+ "VALUES ('" + RdbmsQueryBuilder.escapeForSQLStatement(engineName) + "', '" + engineId + "', '" + engineType + "', '" + engineCost + "', " + global + ")";
		try {
			securityDb.insertData(query);
			securityDb.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateEngine(String engineId, String engineName, String engineType, String engineCost, boolean global) {
		String query = "UPDATE ENGINE SET "
				+ "ENGINENAME='" + RdbmsQueryBuilder.escapeForSQLStatement(engineName) 
				+ "', TYPE='" + engineType 
				+ "', COST='" + engineCost 
				+ "', GLOBAL=" + global
				+ " WHERE ENGINEID='" + engineId + "'";
		try {
			securityDb.insertData(query);
			securityDb.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void addEngineOwner(String engineId, String userId) {
		String query = "INSERT INTO ENGINEPERMISSION (USERID, PERMISSION, ENGINEID, VISIBILITY) VALUES ('" + RdbmsQueryBuilder.escapeForSQLStatement(userId) + "', " + EnginePermission.OWNER.getId() + ", '" + engineId + "', TRUE);";
		try {
			securityDb.insertData(query);
			securityDb.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set an engine and all its insights to be global
	 * @param engineId
	 */
	public static void setEngineCompletelyGlobal(String engineId) {
		String update1 = "UPDATE ENGINE SET GLOBAL=TRUE WHERE ENGINEID='" + engineId + "'";
		try {
			securityDb.insertData(update1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String update2 = "UPDATE INSIGHT SET GLOBAL=TRUE WHERE ENGINEID='" + engineId + "'";
		try {
			securityDb.insertData(update2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////

	/*
	 * Adding Insight
	 */
	
	/**
	 * 
	 * @param engineId
	 * @param insightId
	 * @param insightName
	 * @param global
	 * @param exCount
	 * @param createdOn
	 * @param lastModified
	 * @param layout
	 */
	public static void addInsight(String engineId, String insightId, String insightName, boolean global, String layout) {
		LocalDateTime now = LocalDateTime.now();
		String nowString = java.sql.Timestamp.valueOf(now).toString();
		String insightQuery = "INSERT INTO INSIGHT (ENGINEID, INSIGHTID, INSIGHTNAME, GLOBAL, EXECUTIONCOUNT, CREATEDON, LASTMODIFIEDON, LAYOUT) "
				+ "VALUES ('" + engineId + "', '" + insightId + "', '" + RdbmsQueryBuilder.escapeForSQLStatement(insightName) + "', " + global + " ," + 0 + " ,'" + nowString + "' ,'" + nowString + "','" + layout + "')";
		try {
			securityDb.insertData(insightQuery);
			securityDb.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param engineId
	 * @param insightId
	 * @param insightName
	 * @param global
	 * @param exCount
	 * @param createdOn
	 * @param lastModified
	 * @param layout
	 */
	public static void addUserInsightCreator(String userId, String engineId, String insightId) {
//		String checkQ = "SELECT DISTINCT USERINSIGHTPERMISSION.USERID, USERINSIGHTPERMISSION.ENGINEID, USERINSIGHTPERMISSION.INSIGHTID FROM USERINSIGHTPERMISSION WHERE "
//				+ "USERINSIGHTPERMISSION.USERID='" + userId + "' AND USERINSIGHTPERMISSION.ENGINEID='" + engineId + "' "
//				+ "AND USERINSIGHTPERMISSION.INSIGHTID='" + insightId + "'";
//		IRawSelectWrapper wrapper = WrapperManager.getInstance().getRawWrapper(securityDb, checkQ);
//		if(wrapper.hasNext()) {
//			wrapper.cleanUp();
//		} else {
			String insightQuery = "INSERT INTO USERINSIGHTPERMISSION (USERID, ENGINEID, INSIGHTID, PERMISSION) "
					+ "VALUES ('" + userId + "', '" + engineId + "', '" + insightId + "', " + 1 + ");";
			try {
				securityDb.insertData(insightQuery);
				securityDb.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
//		}
	}
	
	// TODO >>>timb: push app here on create/update
	/**
	 * 
 	 * @param engineId
	 * @param insightId
	 * @param insightName
	 * @param global
	 * @param exCount
	 * @param lastModified
	 * @param layout
	 */
	public static void updateInsight(String engineId, String insightId, String insightName, boolean global, String layout) {
		LocalDateTime now = LocalDateTime.now();
		String nowString = java.sql.Timestamp.valueOf(now).toString();
		String query = "UPDATE INSIGHT SET INSIGHTNAME='" + insightName + "', GLOBAL=" + global + ", LASTMODIFIEDON='" + nowString 
				+ "', LAYOUT='" + layout + "'  WHERE INSIGHTID = '" + insightId + "' AND ENGINEID='" + engineId + "'"; 
		try {
			securityDb.insertData(query);
			securityDb.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
 	 * @param engineId
	 * @param insightId
	 * @param insightName
	 * @param global
	 * @param exCount
	 * @param lastModified
	 * @param layout
	 */
	public static void updateInsightName(String engineId, String insightId, String insightName) {
		LocalDateTime now = LocalDateTime.now();
		String nowString = java.sql.Timestamp.valueOf(now).toString();
		String query = "UPDATE INSIGHT SET INSIGHTNAME='" + insightName + "', LASTMODIFIEDON='" + nowString + "' "
				+ "WHERE INSIGHTID = '" + insightId + "' AND ENGINEID='" + engineId + "'"; 
		try {
			securityDb.insertData(query);
			securityDb.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param engineId
	 * @param insightId
	 */
	public static void deleteInsight(String engineId, String insightId) {
		String query = "DELETE FROM INSIGHT WHERE INSIGHTID ='" + insightId + "' AND ENGINEID='" + engineId + "'";
		try {
			securityDb.insertData(query);
			securityDb.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		query = "DELETE FROM USERINSIGHTPERMISSION  WHERE INSIGHTID ='" + insightId + "' AND ENGINEID='" + engineId + "'";
		try {
			securityDb.insertData(query);
			securityDb.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param engineId
	 * @param insightId
	 */
	public static void deleteInsight(String engineId, String... insightId) {
		String insightFilter = createFilter(insightId);
		String query = "DELETE FROM INSIGHT WHERE INSIGHTID " + insightFilter + " AND ENGINEID='" + engineId + "'";
		try {
			securityDb.insertData(query);
			securityDb.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		query = "DELETE FROM USERINSIGHTPERMISSION WHERE INSIGHTID " + insightFilter + " AND ENGINEID='" + engineId + "'";
		try {
			securityDb.insertData(query);
			securityDb.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////

	/*
	 * Adding engine meta
	 */
	
	/**
	 * 
	 * @param appId
	 * @param metaValues
	 * @param metaType
	 * @throws SQLException
	 */
	public static void setEngineMeta(String engineId, String metaType, List<String> metaValues) {
		metaType = metaType.toLowerCase();
		// first delete existing values
		String deleteQuery = "DELETE FROM ENGINEMETA WHERE ENGINEID='" + engineId + "' AND KEY='" + metaType + "'";
		try {
			securityDb.removeData(deleteQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// second set new values
		try {
			PreparedStatement ps = securityDb.bulkInsertPreparedStatement(new Object[]{"ENGINEMETA", "ENGINEID", "KEY", "VALUE"});
			boolean added = false;
			for (String val : metaValues) {
				if(val == null || val.isEmpty()) {
					continue;
				}
				ps.setString(1, engineId);
				ps.setString(2, metaType);
				ps.setString(3, val);
				ps.addBatch();
				added = true;
			}
			if(added) {
				ps.executeBatch();
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update the total execution count
	 * @param engineId
	 * @param insightId
	 */
	public static void updateExecutionCount(String engineId, String insightId) {
		String updateQuery = "UPDATE INSIGHT SET EXECUTIONCOUNT = EXECUTIONCOUNT + 1 "
				+ "WHERE ENGINEID='" + engineId + "' AND INSIGHTID='" + insightId + "'";
		try {
			securityDb.insertData(updateQuery);
			securityDb.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	
	/*
	 * Groups
	 */
	
	/**
	 * Add a new group
	 * @param userId
	 * @param groupName
	 * @param users
	 * @return
	 */
	public static Boolean addGroup(String userId, String groupName, List<String> users) {
		String query = "INSERT INTO USERGROUP(GROUPID, NAME, OWNER) VALUES (NULL, '" + groupName + "', '" + userId + "');";
		Statement stmt = securityDb.execUpdateAndRetrieveStatement(query, false);
		int id = -1;
		ResultSet rs = null;
		try {
			rs = stmt.getGeneratedKeys();
			while (rs.next()) {
			   id = rs.getInt(1);
			   if(id < 1) {
				   return false;
			   }
			}
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		for(String user : users) {
			query = "INSERT INTO GROUPMEMBERS(GROUPMEMBERSID, GROUPID, USERID) VALUES (NULL, " + id + ", '" + user + "');";
			try {
				securityDb.insertData(query);
				securityDb.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		//ADD VISIBILITY
		query = "INSERT INTO ENGINEGROUPMEMBERVISIBILITY (ID, GROUPENGINEPERMISSIONID, GROUPMEMBERSID, VISIBILITY) "
				+ "SELECT NULL AS ID, GROUPENGINEPERMISSION.GROUPENGINEPERMISSIONID AS GROUPENGINEPERMISSIONID, GROUPMEMBERS.GROUPMEMBERSID AS GROUPMEMBERSID, TRUE AS VISIBILITY "
				+ "FROM GROUPMEMBERS JOIN GROUPENGINEPERMISSION ON(GROUPMEMBERS.GROUPID = GROUPENGINEPERMISSION.GROUPID) "
				+ "WHERE GROUPMEMBERS.GROUPID = ?1 AND GROUPMEMBERS.USERID ?2 ";
		query = query.replace("?1", id + "");
		query = query.replace("?2", createFilter(users));
		try {
			securityDb.insertData(query);
			securityDb.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * Remove a group
	 * @param userId
	 * @param groupId
	 * @return
	 */
	public static Boolean removeGroup(String userId, String groupId) {
		String query;
		if(SecurityQueryUtils.userIsAdmin(userId)){
			query = "DELETE FROM GROUPENGINEPERMISSION WHERE GROUPENGINEPERMISSION.GROUPID IN (SELECT USERGROUP.GROUPID FROM USERGROUP WHERE USERGROUP.GROUPID='" + groupId + "'); ";
			query += "DELETE FROM GROUPMEMBERS WHERE GROUPMEMBERS.GROUPID IN (SELECT USERGROUP.GROUPID FROM USERGROUP WHERE USERGROUP.GROUPID='" + groupId + "'); ";
			query += "DELETE FROM USERGROUP WHERE USERGROUP.GROUPID='" + groupId + "';";
		} else {
			query = "DELETE FROM GROUPENGINEPERMISSION WHERE GROUPENGINEPERMISSION.GROUPID IN (SELECT USERGROUP.GROUPID FROM USERGROUP WHERE USERGROUP.GROUPID='" + groupId + "' AND USERGROUP.OWNER='" + userId + "'); ";
			query += "DELETE FROM GROUPMEMBERS WHERE GROUPMEMBERS.GROUPID IN (SELECT USERGROUP.GROUPID FROM USERGROUP WHERE USERGROUP.GROUPID='" + groupId + "' AND USERGROUP.OWNER='" + userId + "'); ";
			query += "DELETE FROM USERGROUP WHERE USERGROUP.GROUPID='" + groupId + "' AND USERGROUP.OWNER='" + userId + "';";
		}
		
		securityDb.execUpdateAndRetrieveStatement(query, true);
		securityDb.commit();
		return true;
	}
	
	public static Boolean addUserToGroup(String userId, String groupId, String userIdToAdd) {
		String query;
		
		if(SecurityQueryUtils.userIsAdmin(userId)){
			query = "INSERT INTO GROUPMEMBERS (GROUPMEMBERSID, GROUPID, USERID) VALUES (NULL, (SELECT USERGROUP.GROUPID FROM USERGROUP WHERE USERGROUP.GROUPID='" + groupId + "'), '" + userIdToAdd + "');";
		} else {
			query = "INSERT INTO GROUPMEMBERS (GROUPMEMBERSID, GROUPID, USERID) VALUES (NULL, (SELECT USERGROUP.GROUPID FROM USERGROUP WHERE USERGROUP.GROUPID='" + groupId + "' AND USERGROUP.OWNER='" + userId + "'), '" + userIdToAdd + "');";
		}
		
		try {
			securityDb.insertData(query);
			securityDb.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//ADD VISIBILITY
		query = "INSERT INTO ENGINEGROUPMEMBERVISIBILITY (ID, GROUPENGINEPERMISSIONID, GROUPMEMBERSID, VISIBILITY) "
				+ "SELECT NULL AS ID, GROUPENGINEPERMISSION.GROUPENGINEPERMISSIONID AS GROUPENGINEPERMISSIONID, GROUPMEMBERS.GROUPMEMBERSID AS GROUPMEMBERSID, TRUE AS VISIBILITY "
				+ "FROM GROUPMEMBERS GROUPMEMBERS JOIN GROUPENGINEPERMISSION ON(GROUPMEMBERS.GROUPID = GROUPENGINEPERMISSION.GROUPID) "
				+ "WHERE GROUPMEMBERS.GROUPID = '?1' AND GROUPMEMBERS.USERID = '?2'";
		query = query.replace("?1", groupId);
		query = query.replace("?2", userIdToAdd);
		
		try {
			securityDb.insertData(query);
			securityDb.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * Remove a user from a group. Only owner of a group can do it. 
	 * @param userId
	 * @param groupId
	 * @param userToRemove
	 * @return
	 */
	public static Boolean removeUserFromGroup(String userId, String groupId, String userToRemove) {
        String query;
        
        /*if(isUserAdmin(userId)){
            query = "DELETE FROM GroupMembers WHERE GROUPMEMBERS.USERID='" + userToRemove + "' AND GroupMembers.GROUPID = '" + groupId + "';";
        } else {*/
        query = "DELETE FROM GROUPMEMBERS WHERE GROUPMEMBERS.USERID='" + userToRemove + "' AND GROUPMEMBERS.GROUPID IN "
                    + "(SELECT DISTINCT USERGROUP.GROUPID AS GROUPID FROM USERGROUP WHERE USERGROUP.OWNER='" + userId + "' AND USERGROUP.GROUPID = '" + groupId + "');";
        //}
        
        securityDb.execUpdateAndRetrieveStatement(query, true);
        securityDb.commit();
        
        return true;
    }
	
	/**
	 * Adds a new user to the database. Does not create any relations, simply the node.
	 * @param userName	String representing the name of the user to add
	 */
	public static boolean addOAuthUser(AccessToken newUser) throws IllegalArgumentException{
		boolean isNewUser = SecurityQueryUtils.checkUserExist(newUser.getId());
		if(!isNewUser) {			
			String query = "INSERT INTO USER (ID, NAME, USERNAME, EMAIL, TYPE, ADMIN) VALUES ('" + 
					RdbmsQueryBuilder.escapeForSQLStatement(newUser.getId()) + "', '" + 
					RdbmsQueryBuilder.escapeForSQLStatement(newUser.getName()) + "', '" + 
					RdbmsQueryBuilder.escapeForSQLStatement(newUser.getUsername()) + "', '" + 
					RdbmsQueryBuilder.escapeForSQLStatement(newUser.getEmail()) + "', '" + 
					newUser.getProvider() + "', 'FALSE');";
			try {
				securityDb.insertData(query);
				securityDb.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			String query = "SELECT NAME FROM USER WHERE "
					+ "NAME='" + ADMIN_ADDED_USER + "' AND "
					// this matching the ID field to the email because admin added user only sets the id field
					+ "(ID='" + RdbmsQueryBuilder.escapeForSQLStatement(newUser.getId()) + "' OR ID='" + RdbmsQueryBuilder.escapeForSQLStatement(newUser.getEmail()) + "')";
			IRawSelectWrapper wrapper = WrapperManager.getInstance().getRawWrapper(securityDb, query);
			try {
				if(wrapper.hasNext()) {
					// this user was added by the user
					// and we need to update
					String updateQuery = "UPDATE USER SET "
							+ "NAME='"+ RdbmsQueryBuilder.escapeForSQLStatement(newUser.getName()) + "', "
							+ "USERNAME='" + RdbmsQueryBuilder.escapeForSQLStatement(newUser.getUsername()) + "', "
							+ "EMAIL='" + RdbmsQueryBuilder.escapeForSQLStatement(newUser.getEmail()) + "', "
							+ "TYPE='" + newUser.getProvider() + "' "
							+ "WHERE ID='" + RdbmsQueryBuilder.escapeForSQLStatement(newUser.getId()) + "';";
					try {
						securityDb.insertData(updateQuery);
						securityDb.commit();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}finally {
				wrapper.cleanUp();
			}
			String name = flushToString(wrapper);
			if(ADMIN_ADDED_USER.equals(name)) {
				
			}
			return false;
		}
	}
	
	/**
	 * Adds a new user to the database. Does not create any relations, simply the node.
	 * @param userName	String representing the name of the user to add
	 */
	public static boolean registerUser(String id, boolean admin) throws IllegalArgumentException{
		boolean isNewUser = SecurityQueryUtils.checkUserExist(id);
		if(!isNewUser) {			
			String query = "INSERT INTO USER (ID, NAME, ADMIN) VALUES ('" + RdbmsQueryBuilder.escapeForSQLStatement(id) + "', '" + ADMIN_ADDED_USER + "', " + admin + ");";
			try {
				securityDb.insertData(query);
				securityDb.commit();
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * Permissions
	 */
	
	/**
	 * Remove all permissions from a group with a database.
	 * @param groupId
	 * @param engineId
	 * @return
	 */
	public static Boolean removeAllPermissionsForGroup(String groupId, String engineId) {
		String query = "DELETE FROM GROUPENGINEPERMISSION WHERE ENGINE = '?1' AND GROUPID = '?2'";
		query = query.replace("?1", engineId);
		query = query.replace("?2", groupId);
		
		System.out.println("Executing security query: " + query);
		if(securityDb.execUpdateAndRetrieveStatement(query, true) != null){
			securityDb.commit();
		} else {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Remove all permission a user has over a database.
	 * @param userRemove
	 * @param engineId
	 * @return true if action was performed otherwise false
	 */
	public static boolean removeUserPermissionsbyDbId(String userRemove, String engineId){
		String query = "DELETE FROM ENGINEPERMISSION WHERE ENGINEID = '?2' AND USERID = '?1' AND PERMISSION != 1; "
				+ "DELETE FROM GROUPMEMBERS WHERE  GROUPID = (SELECT TOP 1 GROUPENGINEPERMISSION.GROUPID AS GROUPID FROM GROUPMEMBERS GROUPMEMBERS "
				+ "JOIN GROUPENGINEPERMISSION ON (GROUPMEMBERS.GROUPID = GROUPENGINEPERMISSION.GROUPID) WHERE GROUPMEMBERS.USERID = '?1' "
				+ "AND GROUPENGINEPERMISSION.ENGINE = '?2' AND GROUPENGINEPERMISSION.PERMISSION != 1) AND USERID = '?1'";
		
		query = query.replace("?1", userRemove);
		query = query.replace("?2", engineId);
		System.out.println("Executing security query: " + query);
		if(securityDb.execUpdateAndRetrieveStatement(query, true) != null){
			securityDb.commit();
		} else {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Remove all direct permissions of user with a database. 
	 * @param userId
	 * @param engineId
	 * @return
	 */
	public static Boolean removeAllPermissionsForUser(String userId, String engineId) {
		String query = "DELETE FROM ENGINEPERMISSION WHERE ENGINEID = '?1' AND USERID = '?2'";
		query = query.replace("?1", engineId);
		query = query.replace("?2", userId);
		
		if(securityDb.execUpdateAndRetrieveStatement(query, true) != null){
			securityDb.commit();
		} else {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Build the relationship between a group and a database.
	 * @param groupId
	 * @param engineId
	 * @param permission
	 * @return true
	 */
	public static Boolean setPermissionsForGroup(String groupId, String engineId, EnginePermission permission) {
		String query = "INSERT INTO GROUPENGINEPERMISSION(GROUPENGINEPERMISSIONID, ENGINE, GROUPID, PERMISSION) VALUES (NULL,'?1', '?2', '?3')";
		query = query.replace("?1", engineId);
		query = query.replace("?2", groupId);
		query = query.replace("?3", permission.getId() + "");
		try {
			securityDb.insertData(query);
			securityDb.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//ADD VISIBILITY
		query = "INSERT INTO ENGINEGROUPMEMBERVISIBILITY (ID, GROUPENGINEPERMISSIONID, GROUPMEMBERSID, VISIBILITY)  "
				+ "SELECT NULL AS ID, GROUPENGINEPERMISSION.GROUPENGINEPERMISSIONID AS GROUPENGINEPERMISSIONID, GROUPMEMBERS.GROUPMEMBERSID AS GROUPMEMBERS, TRUE AS VISIBILITY "
				+ "FROM GROUPENGINEPERMISSION JOIN GROUPMEMBERS GROUPMEMBERS ON (GROUPENGINEPERMISSION.GROUPID = GROUPMEMBERS.GROUPID) "
				+ "WHERE GROUPENGINEPERMISSION.GROUPID = '?1' AND GROUPENGINEPERMISSION.ENGINE = '?2'";
		query = query.replace("?1", groupId);
		query = query.replace("?2", engineId);
		try {
			securityDb.insertData(query);
			securityDb.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public static Boolean setPermissionsForUser(String engineId, String userToAdd, EnginePermission permission) {
		String query = "INSERT INTO ENGINEPERMISSION (ENGINEID, USERID, PERMISSION) VALUES ('?1', '?2', '?3')";
		query = query.replace("?1", engineId);
		query = query.replace("?2", userToAdd);
		query = query.replace("?3", permission.getId() + "");
		try {
			securityDb.insertData(query);
			securityDb.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * Adds or Remove permission for users and groups to a
	 * certain database.
	 * @param userId
	 * @param isAdmin
	 * @param engineId
	 * @param groups
	 * @param users
	 */
	public static void savePermissions(String userId, boolean isAdmin, String engineId, Map<String, List<Map<String, String>>> groups, Map<String, List<Map<String, String>>> users){
		
		List<Map<String, String>> groupsToAdd = groups.get("add");
		List<Map<String, String>> groupsToRemove = groups.get("remove");
		
		if(isAdmin && !SecurityQueryUtils.userIsAdmin(userId)){
			throw new IllegalArgumentException("The user doesn't have the permissions to access this resource.");
		}
		
		if(!isAdmin && !SecurityQueryUtils.isUserDatabaseOwner(userId, engineId)){
			throw new IllegalArgumentException("The user is not an owner of this database.");
		}
		
		for(Map<String, String> map : groupsToRemove) {
			removeAllPermissionsForGroup(map.get("id"), engineId);
		}
		
		for(Map<String, String> map : groupsToAdd) {
			String perm = map.get("permission");
			setPermissionsForGroup(map.get("id"), engineId, EnginePermission.getPermissionByValue(perm));
		}
		
		List<Map<String, String>> usersToAdd = users.get("add");
		List<Map<String, String>> usersToRemove = users.get("remove");
		
		for(Map<String, String> map : usersToRemove) {
			removeAllPermissionsForUser(map.get("id"), engineId);
		}
		
		for(Map<String, String> map : usersToAdd) {
			String perm = map.get("permission");
			setPermissionsForUser(engineId, map.get("id"), EnginePermission.getPermissionByValue(perm));
		}
		
	}
	
	/*
	 * Engines
	 */
	
	/**
	 * Set if the database is public to all users on this instance
	 * @param user
	 * @param engineId
	 * @param isPublic
	 * @return
	 */
	public static boolean setDbGlobal(User user, String engineId, boolean isPublic) {
		if(!SecurityQueryUtils.userIsOwner(user, engineId)) {
			throw new IllegalArgumentException("The user doesn't have the permission to set this database as global. Only the owner or an admin can perform this action.");
		}
		
		String query = "UPDATE ENGINE SET GLOBAL = " + isPublic + " WHERE ENGINEID ='" + engineId + "';";
		securityDb.execUpdateAndRetrieveStatement(query, true);
		securityDb.commit();
		return true;
	}
	
	/**
	 * Change the user visibility (show/hide) for a database. Without removing its permissions.
	 * @param userId
	 * @param engineId
	 * @param visibility
	 */
	public static void setDbVisibility(String userId, String engineId, String visibility){
		String query = "SELECT ENGINEID FROM ENGINEPERMISSION WHERE USERID = '?1' AND ENGINEID = '?2'";
		query = query.replace("?1", userId);
		query = query.replace("?2", engineId);
		IRawSelectWrapper sjsw = WrapperManager.getInstance().getRawWrapper(securityDb, query);
		if(sjsw.hasNext()){
			query = "UPDATE ENGINEPERMISSION SET VISIBILITY = '?3' WHERE USERID = '?1' AND ENGINEID = '?2'";
			query = query.replace("?1", userId);
			query = query.replace("?2", engineId);
			query = query.replace("?3", visibility);
			securityDb.execUpdateAndRetrieveStatement(query, true);
			securityDb.commit();
			return;
		}
		
		// TODO: WHAT IS THIS CODE FOR???
		// TODO: WHAT IS THIS CODE FOR???
		// TODO: WHAT IS THIS CODE FOR???
		// TODO: WHAT IS THIS CODE FOR???
		// TODO: WHAT IS THIS CODE FOR???
		
//		query = "SELECT GROUPENGINEPERMISSION.GROUPENGINEPERMISSIONID, TEMP.AID "
//				+ "FROM GROUPENGINEPERMISSION JOIN (SELECT GROUPMEMBERSID AS AID, GROUPID FROM GROUPMEMBERS WHERE USERID = '?2') TEMP ON (GROUPENGINEPERMISSION.GROUPID = TEMP.GROUPID) "
//				+ "WHERE ENGINE = '?1'";
//		query = query.replace("?1", engineId);
//		query = query.replace("?2", userId);
//		
//		IRawSelectWrapper wrapper = WrapperManager.getInstance().getRawWrapper(securityDb, query);
//		List<Object[]> rows = flushRsToMatrix(wrapper);
//		
//		for(Object[] row : rows){
//			query = "UPDATE ENGINEGROUPMEMBERVISIBILITY SET VISIBILITY = '"+ visibility + 
//					"' WHERE GROUPENGINEPERMISSIONID = '"+ row[0].toString() +"' AND GROUPMEMBERSID = '" + row[1].toString() +"'";
//			securityDb.execUpdateAndRetrieveStatement(query, true);
//			securityDb.commit();
//			return;
//		}
		
		// if we do not update
		// we need to insert
		String insertQuery = "INSERT INTO ENGINEPERMISSION (USERID, ENGINEID, VISIBILITY) VALUES ('" + userId + "', '" + engineId + "', " + visibility + ")";
		securityDb.execUpdateAndRetrieveStatement(insertQuery, true);
		securityDb.commit();
	}
	
	/**
	 * Remove a database and all the permissions related to it.
	 * @param engineName
	 */
	public static void removeDb(String engineId) {
		//DELETE USERPERMISSIONS
		String query = "DELETE FROM ENGINEPERMISSION WHERE ENGINEID = '?1'; DELETE FROM GROUPENGINEPERMISSION WHERE ENGINE = '?1'; DELETE FROM ENGINE WHERE ENGINEID = '?1'";
		query = query.replace("?1", engineId);
		
		System.out.println("Executing security query: " + query);
		securityDb.execUpdateAndRetrieveStatement(query, true);
		securityDb.commit();
	}
}
