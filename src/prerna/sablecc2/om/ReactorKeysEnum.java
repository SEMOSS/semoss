package prerna.sablecc2.om;

public enum ReactorKeysEnum {

	ADDITIONAL_PIXELS("additionalPixels",					"Additional pixels to be executed in addition to the pixel steps saved within the insight"),
	ALL_NUMERIC_KEY("allNumeric", 							"Indicates if only numeric headers should be returned"),
	ALIAS("alias", 											"An alias to assign for an operation or output"),
	APP("app", 												"Name of the app on the local SEMOSS instance"),
	ARRAY("array", 											"An array of input values"),
	ATTRIBUTES("attributes", 								"List of columns used as properties/characteristics to describe an instance/object"),
	BREAKS("breaks", 										"Number of divisions"),
	CLONE_PANEL_KEY("cloneId", 								"Id to assign the new clone panel"),
	CLUSTER_KEY("numClusters", 								"Number of clusters"),
	COLLABORATOR("collaborator",							"Username for an individual to collaborate with on an app"),
	COLUMN("column", 										"Name of the column header"),
	COLUMNS("columns", 										"List of column headers"),
	COMMENT_KEY("comment", 									"This key can represent a string comment, a map containing the data for a given comment, or the id for an existing comment"),
	COMMENT_ID_KEY("commentId", 							"Unique ID for the insight comment"),
	CONCEPT("concept", 										"Concept name within an engine"),
	CONCEPTS("concepts", 									"List of concept names within an engine"),
	CONFIG_FILE("configFile",                               "The configuration file."),
	CONNECTION_STRING_KEY("connectionString", 				"JDBC connection string to connect to an external database"),
	CRITERIA("criteria", 									"Criteria to be evaluated"),
	CRON_EXPRESSION("cronExpression",                       "The cron expression"),
	DB_DRIVER_KEY("dbDriver",								"Name of the JDBC driver.  Not all JDBC drivers are open source so make sure you include the driver within the classpath of SEMOSS"),
	DATA_TYPE("dataType", 									"Data type of the column (STRING, NUMBER, DATE)"),
	DATA_TYPE_MAP("dataTypeMap", 							"Map of column name to the column data types"),
	DEFAULT_VALUE_KEY("defaultValue", 						"A default value to use for null columns"),
	DELIMITER("delimiter", 									"Delimiter to be used"),
	ENGINE("engine", 										"Name of the datasource"),
	EVENTS_KEY("events", 									"Events map input"),
	FILE_NAME("fileName", 									"Name of the file"),
	FILE_PATH("filePath", 									"Absolute file path location"),
	FILTERS("filters", 										"Filters automatically persisted on queries affecting this frame or panel"),
	FILTER_WORD("filterWord", 								"Regex to apply for searches"),
	FRAME("frame", 											"The frame"),
	FRAME_TYPE("frameType", 								"Type of frame to generate - grid (sql based frame), graph (frame based on tinkerpop), r (data sits within r, must have r installed to use), native (leverages the database to execute queries)"),
	HOST("host",                                            "The host."),
	IMAGE_URL("imageUrl", 									"Image URL"),
	INCLUDE_META_KEY("meta", 								"Boolean indication (true or false) of whether to retrieve metadata"),
	INDEX("index", 											"A specified index for an object (e.g., the frame filter)"),
	INSIGHT_ID("id", 										"This key can represent the unique id of the insight instance or the unique id of the saved insight"),
	INSIGHT_NAME("insightName", 							"Name of the insight"),
	INSIGHT_ORNAMENT("insightOrnament", 					"Insight ornament map"),
	INSTANCE_KEY("instance", 								"Column representing the objects being used to perform the operation"),
	JOB_GROUP("jobGroup",                                   "The name of the job group"),
	JOB_ID("jobId", 										"Id of the job"),
	JOB_NAME("jobName",                                     "The name of the job"),
	JOINS("joins", 											"Joins on the frame"),
	LAYOUT_KEY("layout", 									"The layout of the insight, used as a tag"),
	LIMIT("limit", 											"Limit to add for the query results"),
	LOGICAL_NAME("logicalNames", 							"Column alias to be added to the master database"),
	NEW_COLUMN("newCol", 									"Name of the new column being created"),
	NEW_HEADER_NAMES("newHeaders", 							"New header names for a file"),
	NEW_VALUE("newValue", 									"New value used to replace an existing value"),
	NUM_DISPLAY("display",                                  "The number of results to display"),
	NUMERIC_VALUE("numValue", 								"Numeric value to be used in the operation"),
	NUMERIC_VALUES("numValues", 							"Numeric values to be used in the operation"),
	OFFSET("offset", 										"Offset to add for the query results"),
	OPTIONS("options", 										"Map of option values"),
	ORNAMENTS_KEY("ornaments", 								"Panel ornaments"),
	PANEL("panel", 											"Id of the panel"),
	PANEL_COLOR_RULE_ID("panelCbv",							"Id of the color by value rule in the panel"), 								
	PANEL_LABEL_KEY("panelLabel", 							"Label for the panel"),
	PANEL_POSITION_KEY("position", 							"Panel position map"),
	PANEL_VIEW_KEY("panelView", 							"Text that specifies what is displayed in the panel (e.g., \"visualization\")"),
	PANEL_VIEW_OPTIONS_KEY("panelViewOptions", 				"The panel view options"),
	PARAM_KEY("params", 									"The parameters for the insight map"),
	PASSWORD("password", 									"Password used in conjunction with the username for access to a service"),
	PORT("port",                                            "The connection port."),
	PLANNER("planner", 										"The planner"),
	RANDOM_VALS("randomVals",                               "The number of random values to use for the routine"),
	REACTOR("reactor", 										"Reactor name"),
	RECIPE("recipe", 										"Recipe that shows the sequence of pixels resulting in the insight"),
	REGEX("regex", 											"Regular expression (sequence of characters)"),
	REPOSITORY("repository", 								"Name of the repository. Usually namespaced to be username\\appname"),
	SCHEMA("schema",                                        "The database schema."),
	SESSION_ID("sessionId", 								"Id of the session"),
	SHEET_NAME("sheetName",                                 "The name of the excel sheet"),
	SORT("sort", 											"Sort direction: ascending (\"asc\") or decending (\"desc\")"),
	STATEMENT("statement", 									"Statement to be evaluated"),
	SUM_RANGE("sumRange", 									"Range that values to sum over"),
	SYNC_PULL("dual", 										"True/False value to determine if the sync should also pull the latest updates from the repository"),
	SYNC_DATABASE("syncDatabase", 							"True/False value to detetermine if the database should be published with the app"),
	TASK("task", 											"Task object (can retrieve the object by using Task(taskId) where taskId is the unique id for the task)"),
	TASK_ID("taskId", 										"Unique id of the task within the insight"),
	TRAVERSAL("traversal", 									"The traversal path within the map"),
	USE_FRAME_FILTERS("useFrameFilters", 					"A boolean indication (true or false) to use frame filters"), 								
	USERNAME("username", 									"Unique identifier for the user to access a service"),
	QUERY_KEY("query", 										"Query string to be executed on the database"),
	QUERY_STRUCT("qs", 										"QueryStruct object that contains selectors, filters, and joins"),
	VALUE("value", 											"The instance value in a column, or the numeric or string value used in a operation"),
	VALUES("values", 										"Numeric or string values used as input text or inputs to an operation"),
	VARIABLE("variable", 									"Pixel variable consisting of only alphanumeric characters and underscores");
	
	private String key;
	private String description;
	
	private ReactorKeysEnum(String key, String description) {
		this.key = key;
		this.description = description;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public static String getDescriptionFromKey(String key) {
		for(ReactorKeysEnum e : ReactorKeysEnum.values()) {
			if(e.key.equals(key)) {
				return e.description;
			}
		}
		// if we cannot find the description above
		// it is not a standardized key
		// so just return null
		return null;
	}
}
