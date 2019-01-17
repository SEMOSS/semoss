package prerna.sablecc2.om;

public enum PixelOperationType {

	// JOB INFORMATION
	TASK,
	RESET_PANEL_TASKS,
	REMOVE_TASK,
	TASK_DATA,
	TASK_METADATA,
	
	// QUERY
	QUERY,
	
	// FRAME INFORMATION
	FRAME,
	FRAME_DATA_CHANGE,
	FRAME_HEADERS_CHANGE,
	// USED FOR ADDITIONAL OUTPUT
	ADD_HEADERS,
	REMOVE_HEADERS,
	MODIFY_HEADERS,
	
	FRAME_HEADERS,
	FRAME_FILTER,
	FILTER_MODEL,
	FRAME_METAMODEL,
	
	// PANEL OPERATIONS
	PANEL,
	PANEL_OPEN,
	PANEL_CLOSE,
	PANEL_CONFIG,
	PANEL_ORNAMENT,
	PANEL_VIEW,
	PANEL_LABEL,
	PANEL_HEADER,
	PANEL_CLONE,
	PANEL_COMMENT,
	PANEL_EVENT,
	PANEL_POSITION,
	PANEL_FILTER,
	PANEL_SORT,
	CACHED_PANEL,
	
	// PANEL LAYERS
	PANEL_COLOR_BY_VALUE,
	ADD_PANEL_COLOR_BY_VALUE,
	REMOVE_PANEL_COLOR_BY_VALUE,
	
	// EXTERNAL WINDOWS
	OPEN_TAB,
	
	// META DATA INFORMATION
	APP_INFO,
	APP_USERS,
	APP_INSIGHTS,
	
	// forms calls that change the db
	ALTER_DATABASE,
	
	// these are the new traverse options
	DATABASE_TABLE_STRUCTURE,
	DATABASE_TRAVERSE_OPTIONS,
	TRAVERSAL_OPTIONS,
	WIKI_LOGICAL_NAMES,
	CONNECTED_CONCEPTS,
	DATABASE_LIST,
	DATABASE_METAMODEL,
	DATABASE_DICTIONARY,
	DATABASE_CONCEPTS,
	DATABASE_CONCEPT_PROPERTIES,
	ENTITY_LOGICAL_NAMES,
	ENTITY_DESCRIPTIONS,
	
	// APP SPECIFIC WIDGETS
	APP_WIDGETS, 
	
	//Database
	DELETE_ENGINE,
	
	// INSIGHT INFORMATION
	CURRENT_INSIGHT_RECIPE,
	SAVED_INSIGHT_RECIPE,
	OPEN_SAVED_INSIGHT,
	NEW_EMPTY_INSIGHT,
	DROP_INSIGHT,
	CLEAR_INSIGHT,
	INSIGHT_HANDLE,
	SAVE_INSIGHT,
	INSIGHT_ORNAMENT,
	DELETE_INSIGHT,
	
	// DASHBAORD
	DASHBOARD_INSIGHT_CONFIGURATION,
	
	// RUNNING JAVA CODE
	CODE_EXECUTION,
	// MULTI OUTPUT
	VECTOR,
	
	// A SUBSCRIPT WITHIN YOUR SCRIPT
	// SO THE FE KNOWS TO LOOP THROUGH
	// AN ARRAY OF RESULTS
	SUB_SCRIPT,
	
	// ROUTINES THAT SEND BACK DATA TO VISUALIZE WITH A LAYOUT
	VIZ_OUTPUT,
	
	// FILE DOWNLOAD
	FILE_DOWNLOAD,
	
	// OLD INSIGHT
	OLD_INSIGHT,
	PLAYSHEET_PARAMS,
	
	// GIT_MARKET
	MARKET_PLACE, // general market routine
	MARKET_PLACE_INIT,
	MARKET_PLACE_ADDITION,
	
	// SCHEDULER INFORMATION
	SCHEDULE_JOB,
	LIST_JOB, 
	RESCHEDULE_JOB, 
	UNSCHEDULE_JOB, 
	
	// USER ANALYTICS
	VIZ_RECOMMENDATION,
	RECOMMENDATION,

	// CLOUD
	GOOGLE_SHEET_LIST,
	GOOGLE_DRIVE_LIST,
	CLOUD_FILE_LIST,
	
	// Cluster
	OPEN_APP,
	UPDATE_APP,
	CLEANUP_APPS,
	VERSION,
	
	// RECIPE COMMENTS
	RECIPE_COMMENT,
	
	// R
	CHECK_R_PACKAGES,
	
	// SOME KIND OF OPERATION THAT WE WANT TO OUTPUT
	OPERATION,
	
	// HELP
	HELP,
	
	// MESSAGES ERRORS
	SUCCESS,
	WARNING,
	ERROR,
	UNEXECUTED_PIXELS,
	FRAME_SIZE_LIMIT_EXCEEDED,
	USER_INPUT_REQUIRED,
	LOGGIN_REQUIRED_ERROR,
	INVALID_SYNTAX;
}
