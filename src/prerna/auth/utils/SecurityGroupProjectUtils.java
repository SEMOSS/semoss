package prerna.auth.utils;

import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import prerna.auth.AccessPermission;
import prerna.auth.AuthProvider;
import prerna.auth.User;
import prerna.ds.util.RdbmsQueryBuilder;
import prerna.engine.api.IRawSelectWrapper;
import prerna.query.querystruct.SelectQueryStruct;
import prerna.query.querystruct.filters.AndQueryFilter;
import prerna.query.querystruct.filters.OrQueryFilter;
import prerna.query.querystruct.filters.SimpleQueryFilter;
import prerna.query.querystruct.selectors.QueryColumnOrderBySelector;
import prerna.query.querystruct.selectors.QueryColumnSelector;
import prerna.rdf.engine.wrappers.WrapperManager;
import prerna.util.Constants;

public class SecurityGroupProjectUtils extends AbstractSecurityUtils {
	
	private static final Logger logger = LogManager.getLogger(SecurityGroupProjectUtils.class);
	
	/**
	 * Determine if a user can view a project including group permissions
	 * @param user
	 * @param projectId
	 * @return
	 */
	public static boolean userCanViewProject(User user, String projectId) {
		Integer bestUserProjectPermission = getBestProjectPermission(user, projectId);
		return bestUserProjectPermission != null;
	}
	
	/**
	 * Determine if the user can modify the project including group permissions
	 * @param projectId
	 * @param userId
	 * @return
	 */
	public static boolean userCanEditProject(User user, String projectId) {
		Integer bestUserProjectPermission = getBestProjectPermission(user, projectId);
		return bestUserProjectPermission != null && AccessPermission.isEditor(bestUserProjectPermission);
	}
	
	/**
	 * Determine if the user is the owner of an project including group permissions
	 * @param userFilters
	 * @param projectId
	 * @return
	 */
	public static boolean userIsOwner(User user, String projectId) {
		Integer bestUserProjectPermission = getBestProjectPermission(user, projectId);
		return bestUserProjectPermission != null && AccessPermission.isOwner(bestUserProjectPermission);
	}
	
	/**
	 * Determine the strongest project permission for the user/group
	 * @param userId
	 * @param projectId
	 * @return
	 */
	public static Integer getBestProjectPermission(User user, String projectId) {
		// get best permission from user
		Integer bestUserProjectPermission = null;
		
		SelectQueryStruct qs = new SelectQueryStruct();
		qs.addSelector(new QueryColumnSelector("PROJECTPERMISSION__PERMISSION"));
		qs.addExplicitFilter(SimpleQueryFilter.makeColToValFilter("PROJECTPERMISSION__PROJECTID", "==", projectId));
		qs.addExplicitFilter(SimpleQueryFilter.makeColToValFilter("PROJECTPERMISSION__USERID", "==", getUserFiltersQs(user)));
		qs.addOrderBy(new QueryColumnOrderBySelector("PROJECTPERMISSION__PERMISSION"));
		IRawSelectWrapper wrapper = null;
		try {
			wrapper = WrapperManager.getInstance().getRawWrapper(securityDb, qs);
			if(wrapper.hasNext()) {
				Object val = wrapper.next().getValues()[0];
				if(val != null) {
					bestUserProjectPermission = ((Number) val).intValue();
				}
			}
		} catch (Exception e) {
			logger.error(Constants.STACKTRACE, e);
			throw new IllegalArgumentException("Failed to retrieve existing project permissions for user", e);
		} finally {
			if(wrapper != null) {
				wrapper.cleanUp();
			}
		}		
		
		// if they are the owner based on user, then skip the group check
		if(bestUserProjectPermission != null && AccessPermission.isOwner(bestUserProjectPermission)) {
			return bestUserProjectPermission;
		}
		
		// get best group permission
		Integer bestGroupProjectPermission = null;
		
		qs = new SelectQueryStruct();
		qs.addSelector(new QueryColumnSelector("GROUPPROJECTPERMISSION__PERMISSION"));
		qs.addExplicitFilter(SimpleQueryFilter.makeColToValFilter("GROUPPROJECTPERMISSION__ENGINEID", "==", projectId));
		OrQueryFilter orFilter = new OrQueryFilter();
		List<AuthProvider> logins = user.getLogins();
		for(AuthProvider login : logins) {
			AndQueryFilter andFilter = new AndQueryFilter();
			andFilter.addFilter(SimpleQueryFilter.makeColToValFilter("GROUPPROJECTPERMISSION__TYPE", "==", user.getAccessToken(login).getUserGroupType()));
			andFilter.addFilter(SimpleQueryFilter.makeColToValFilter("GROUPPROJECTPERMISSION__ID", "==", user.getAccessToken(login).getUserGroups()));
			orFilter.addFilter(andFilter);
		}
		qs.addExplicitFilter(orFilter);
		qs.addOrderBy(new QueryColumnOrderBySelector("GROUPPROJECTPERMISSION__PERMISSION"));
		wrapper = null;
		try {
			wrapper = WrapperManager.getInstance().getRawWrapper(securityDb, qs);
			if(wrapper.hasNext()) {
				Object val = wrapper.next().getValues()[0];
				if(val != null) {
					bestGroupProjectPermission = ((Number) val).intValue();
				}
			}
		} catch (Exception e) {
			logger.error(Constants.STACKTRACE, e);
			throw new IllegalArgumentException("Failed to retrieve existing project permissions for user", e);
		} finally {
			if(wrapper != null) {
				wrapper.cleanUp();
			}
		}
		
		if(bestGroupProjectPermission == null && bestUserProjectPermission == null) {
			if(SecurityProjectUtils.projectIsGlobal(projectId)) {
				return AccessPermission.READ_ONLY.getId();
			}
			return null;
		} else if(bestGroupProjectPermission == null || bestGroupProjectPermission.compareTo(bestUserProjectPermission) >= 0) {
			return bestUserProjectPermission;
		} else {
			return bestGroupProjectPermission;
		}
	}
	
	/**
	 * Create a project group permission
	 * @param user
	 * @param groupId
	 * @param groupType
	 * @param projectId
	 * @param permission
	 * @return
	 * @throws IllegalAccessException 
	 */
	public static void addProjectGroupPermission(User user, String groupId, String groupType, String projectId, String permission) throws IllegalAccessException {
		if(!userCanEditProject(user, projectId)) {
			throw new IllegalAccessException("Insufficient privileges to modify this project's permissions.");
		}
		
		if(getGroupProjectPermission(groupId, groupType, projectId) != null) {
			throw new IllegalArgumentException("This group already has access to this project. Please edit the existing permission level.");
		}
		
		String query = "INSERT INTO GROUPPROJECTPERMISSION (ID, TYPE, PROJECTID, PERMISSION) VALUES('"
				+ RdbmsQueryBuilder.escapeForSQLStatement(groupId) + "', '"
				+ RdbmsQueryBuilder.escapeForSQLStatement(groupType) + "', '"
				+ RdbmsQueryBuilder.escapeForSQLStatement(projectId) + "', "
				+ AccessPermission.getIdByPermission(permission) + ");";
		
		try {
			securityDb.insertData(query);
		} catch (SQLException e) {
			logger.error(Constants.STACKTRACE, e);
			throw new IllegalArgumentException("An error occured adding group permissions for this project", e);
		}
	}
	
	/**
	 * Get the project permission for a specific group
	 * @param groupId
	 * @param groupType
	 * @param projectId
	 * @return
	 */
	public static Integer getGroupProjectPermission(String groupId, String groupType, String projectId) {
		SelectQueryStruct qs = new SelectQueryStruct();
		qs.addSelector(new QueryColumnSelector("GROUPPROJECTPERMISSION__PERMISSION"));
		qs.addExplicitFilter(SimpleQueryFilter.makeColToValFilter("GROUPPROJECTPERMISSION__PROJECTID", "==", projectId));
		qs.addExplicitFilter(SimpleQueryFilter.makeColToValFilter("GROUPPROJECTPERMISSION__ID", "==", groupId));
		qs.addExplicitFilter(SimpleQueryFilter.makeColToValFilter("GROUPPROJECTPERMISSION__TYPE", "==", groupType));
		IRawSelectWrapper wrapper = null;
		try {
			wrapper = WrapperManager.getInstance().getRawWrapper(securityDb, qs);
			if(wrapper.hasNext()) {
				Object val = wrapper.next().getValues()[0];
				if(val != null && val instanceof Number) {
					return ((Number) val).intValue();
				}
			}
		} catch (Exception e) {
			logger.error(Constants.STACKTRACE, e);
		} finally {
			if(wrapper != null) {
				wrapper.cleanUp();
			}
		}
		
		return null;
	}
	
	/**
	 * Modify a group project permission
	 * @param user
	 * @param groupId
	 * @param groupType
	 * @param projectId
	 * @param newPermission
	 * @return
	 * @throws IllegalAccessException 
	 */
	public static void editProjectGroupPermission(User user, String groupId, String groupType, String projectId, String newPermission) throws IllegalAccessException {
		// make sure user can edit the project
		Integer userPermissionLvl = getBestProjectPermission(user, projectId);
		if(userPermissionLvl == null || !AccessPermission.isEditor(userPermissionLvl)) {
			throw new IllegalAccessException("Insufficient privileges to modify this project's permissions.");
		}
		
		// make sure we are trying to edit a permission that exists
		Integer existingGroupPermission = getGroupProjectPermission(groupId, groupType, projectId);
		if(existingGroupPermission == null) {
			throw new IllegalArgumentException("Attempting to modify project permission for a group who does not currently have access to the project");
		}
		
		int newPermissionLvl = AccessPermission.getIdByPermission(newPermission);
		
		// if i am not an owner
		// then i need to check if i can edit this group permission
		if(!AccessPermission.isOwner(userPermissionLvl)) {
			// not an owner, check if trying to edit an owner or an editor/reader
			// get the current permission
			if(AccessPermission.OWNER.getId() == existingGroupPermission) {
				throw new IllegalAccessException("The user doesn't have the high enough permissions to modify this group project permission.");
			}
			
			// also, cannot give some owner permission if i am just an editor
			if(AccessPermission.OWNER.getId() == newPermissionLvl) {
				throw new IllegalAccessException("Cannot give owner level access to this project since you are not currently an owner.");
			}
		}
		
		String query = "UPDATE GROUPPROJECTPERMISSION SET PERMISSION=" + newPermissionLvl
				+ " WHERE ID='" + RdbmsQueryBuilder.escapeForSQLStatement(groupId) + "' "
				+ "AND TYPE='" + RdbmsQueryBuilder.escapeForSQLStatement(groupType) + "' "
				+ "AND PROJECTID='"	+ RdbmsQueryBuilder.escapeForSQLStatement(projectId) + "';";
		try {
			securityDb.insertData(query);
		} catch (SQLException e) {
			logger.error(Constants.STACKTRACE, e);
			throw new IllegalArgumentException("An error occured updating the group permissions for this project", e);
		}
	}
	
	/**
	 * Delete a group project permission
	 * @param user
	 * @param groupId
	 * @param groupType
	 * @param projectId
	 * @return
	 * @throws IllegalAccessException 
	 */
	public static void removeProjectGroupPermission(User user, String groupId, String groupType, String projectId) throws IllegalAccessException {
		// make sure user can edit the project
		Integer userPermissionLvl = getBestProjectPermission(user, projectId);
		if(userPermissionLvl == null || !AccessPermission.isEditor(userPermissionLvl)) {
			throw new IllegalAccessException("Insufficient privileges to modify this project's permissions.");
		}
		
		// make sure we are trying to edit a permission that exists
		Integer existingGroupPermission = getGroupProjectPermission(groupId, groupType, projectId);
		if(existingGroupPermission == null) {
			throw new IllegalArgumentException("Attempting to modify group permission for a user who does not currently have access to the project");
		}
		
		// if i am not an owner
		// then i need to check if i can remove this group permission
		if(!AccessPermission.isOwner(userPermissionLvl)) {
			// not an owner, check if trying to edit an owner or an editor/reader
			// get the current permission
			if(AccessPermission.OWNER.getId() == existingGroupPermission) {
				throw new IllegalAccessException("The user doesn't have the high enough permissions to modify this group project permission.");
			}
		}
		
		String query = "DELETE FROM GROUPPROJECTPERMISSION WHERE ID='" 
				+ RdbmsQueryBuilder.escapeForSQLStatement(groupId) + "' "
				+ "AND TYPE='" + RdbmsQueryBuilder.escapeForSQLStatement(groupType) + "' "
				+ "AND PROJECTID='"	+ RdbmsQueryBuilder.escapeForSQLStatement(projectId) + "';";
		try {
			securityDb.insertData(query);
		} catch (SQLException e) {
			logger.error(Constants.STACKTRACE, e);
			throw new IllegalArgumentException("An error occured removing the user permissions for this project", e);
		}
	}

}
