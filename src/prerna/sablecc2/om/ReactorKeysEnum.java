package prerna.sablecc2.om;

public enum ReactorKeysEnum {
	
	ACCESS("access",											"Boolean to check if access is required."),
	ADDITIONAL_DATA_TYPE("additionalDataType",					"Additional data types defining specific format"),
	ADDITIONAL_DATA_TYPES("additionalDataTypes",				"Additional data types defining specific formats"),
	ADDITIONAL_PIXELS("additionalPixels",						"Additional pixels to be executed in addition to the pixel steps saved within the insight"),
	ALL("all",                                              	"Boolean to use all the values"),
	ALL_NUMERIC_KEY("allNumeric", 								"Indicates if only numeric headers should be returned"),
	ALIAS("alias", 												"An alias to assign for an operation or output - use .as([\"aliasName\"])"), 
//	APP("app", 													"Name of the app on the local SEMOSS instance"),
	ANIMATE("animate", 											"Specifies if the ggplot needs to be animated"),
	API_KEY("apikey", 									    	"API key being used for the specific insight / project"),
	APP_TYPE("appType", 										"The type of app"),
	ARRAY("array", 												"An array of input values"),
	ATTRIBUTES("attributes", 									"List of columns used as properties/characteristics to describe an instance/object"),
	BASE_URL("baseUrl",                                     	"The base SEMOSS url"),
	BREAKS("breaks", 											"Number of divisions"),
	CLEAN("clean", 												"Boolean if we should clean up the strings before insertion. (default is true)"),
	CLEAN_UP_CLOUD_STORAGE("cleanUpCloudStorage",				"Boolean whether to clean up the cloud storage account used to persist databases in the cluster (defaults to false)"),
	CLONE_PANEL_KEY("cloneId", 									"Id to assign the new clone panel"),
	CLUSTER_KEY("numClusters", 									"Number of clusters"),
	COLLABORATOR("collaborator",								"Username for an individual to collaborate with on an app"),
	COLUMN("column", 											"Name of the column header"),
	COLUMNS("columns", 											"List of column headers"),
	COLUMN_GUTTER("column_gutter",	 							"Gutter / Padding for columns"),
	COMMAND ("command",											"The shell command to execute. Commands allowed cd, dir, ls, copy, cp, mv, move, del <specific file>, rm <specific file>, git"),
	COMMENT_KEY("comment", 										"This key can represent a string comment, a map containing the data for a given comment, or the id for an existing comment"),
	COMMENT_ID_KEY("commentId", 								"Unique ID for the insight comment"),
	CONCEPTUAL_NAMES("conceptualNames", 						"Conceptual names that are defined for the app tables and columns"),
	CONCEPT("concept", 											"Concept name within an engine"),
	CONCEPTS("concepts", 										"List of concept names within an engine"),
	CONFIG("config",                       	        			"The configuration settings."),
	CONNECTION_DETAILS("conDetails", 							"Map containing the necessary details to establish a JDBC connection to an external database"),
	CONNECTION_STRING_KEY("connectionString", 					"JDBC connection string to connect to an external database"),
	CONSUMER_ID("consumer", 									"Email / ID of the consumer to be added for API"),
	CONTENT("content", 										    "The actual content to be persisted on the file. Tab and Newline Separated"),
	CONTEXT("context", 									    	"THe workspace to be set command line. This is the base directory."),
	CUSTOM_SUCCESS_MESSAGE("customSuccessMessage",				"Defines for certain reactors a custom success message after running"),
	ADDITIONAL_CONNECTION_PARAMS_KEY("connParams",	 			"Additional JDBC connection params to perform connection"),
	CREATE_INDEX("createIndex", 								"Boolean if indicies should be generated."),
	CRITERIA("criteria", 										"Criteria to be evaluated"),
	CRON_EXPRESSION("cronExpression",							"The cron expression"),
	DATABASE("database",										"Name of the datasource"),
	DEDUPLICATE("deduplicate",								    "Boolean if we should remove duplicate."),
	DATA_TYPE("dataType", 										"Data type of the column (STRING, NUMBER, DATE)"),
	DATA_TYPE_MAP("dataTypeMap", 								"Map of column name to the column data types"),
	DATE_TIME_FIELD("dateTimeField", 							"Data time field (SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, YEAR)"),
	DB_DRIVER_KEY("dbDriver",									"Name of the JDBC driver.  Not all JDBC drivers are open source so make sure you include the driver within the classpath of SEMOSS"),
	DEFAULT_VALUE_KEY("defaultValue", 							"A default value to use for null columns"),
	DELIMITER("delimiter", 										"Delimiter to be used"),
	DESCRIPTION("description", 									"A description (could be for an app, table/concept, insight, etc.)"),
	DRY_RUN("dryRun",											"Boolean whether to perform a dry run of the operation without impacting application state (default is true)"),
	EMAIL_SESSION("emailSession",								"The javax.mail email session object"),
	ENABLE("enable",											"Boolean whether to enable"),
	EVENTS_KEY("events", 										"Events map input"),
	EXISTING("existing", 										"Add to exisitng app"),
	END("end",	 									            "Ending value for a between reactor"),
	END_ROW("endRow",	 									    "The ending row number to import from a file."),
	EXPIRES_ON("expires",	 									"The date when the api expires"),
	EXPORT_AUDIT("exportAudit",	                             	"Boolean to include an audit sheet for parameters to excel exports"),
	EXPORT_TEMPLATE("export_template",					    	"Template file to use for the export of this data"),
	EXPRESSION("expression",                                	"R / Python expression that needs to be dynamically calculated for this variable"),
	FILE_NAME("fileName", 										"Name of the file"),
	FILE_PATH("filePath", 										"Absolute file path location"),
	FILTERS("filters", 											"Filters automatically persisted on queries affecting this frame or panel"),
	FILTER_WORD("filterWord", 									"Regex to apply for searches"),
	FORMAT("format", 											"The format to save the information as Jpeg, Gif, PNG"),
	FOOTER("footer", 											"Footer for the export template if there is any"),
	FRAME("frame", 												"The frame"),
	FRAME_CACHE("frameCache", 									"Enable or disable the frame cache. Boolean (True / False)"),
	FRAME_TYPE("frameType", 									"Type of frame to generate - grid (sql based frame), graph (frame based on tinkerpop), r (data sits within r, must have r installed to use), native (leverages the database to execute queries)"),
	GGPLOT("ggplot",                	             			"The GGPlot expression used to create the graph"),
	GRAPH_LAYOUT("graphLayout",         	                    "The layout of the igraph"),
	GRAPH_NAME("graphName",                 	                "The name of the graph"),
	GRAPH_NAME_ID("graphNameId",                	            "The name identifier of the graph nodes"),
	GRAPH_METAMODEL("graphMetamodel",               	        "The metamodel of the graph"),
	GRAPH_TYPE_ID("graphTypeId",                        	    "The type identifier of the graph nodes"),
	HEADERS("headers",                                      	"All the headers strings we want to push as part of the header on the excel / powerpoint export"),
	HEIGHT("height", 											"The height to use for screenshot capture."),
	HOST("host",                    	                        "The host."),
	HTML("html",                        	                    "The html."),
	ID("id", 													"This key can represent the unique id of the insight instance or the unique id of the saved insight relative to the app"),
	ID_TYPE("id_type", 											"Type of the id for setting param. colum / column_table / column_table_operator"),
	IMAGE("image",		 										"The location of the image file or the encoding of the image as a png"),
	INCLUDE_META_KEY("meta", 									"Boolean indication (true or false) of whether to retrieve metadata"),
	INDEX("index", 												"A specified index for an object (e.g., the frame filter)"),
	INSIGHT_NAME("insightName", 								"Name of the insight"),
	INSIGHT_ORNAMENT("insightOrnament", 						"Insight ornament map"),
	INSTANCE_KEY("instance", 									"Column representing the objects being used to perform the operation"),
	JOB_GROUP("jobGroup",										"The name of the job group"),
	JOB_ID("jobId",		 										"Id of the job"),
	JOB_NAME("jobName", 	                                    "The name of the job"),
	JOB_TAGS("jobTags",     	                              	"List of job tags to use for filtering"),
	JOINS("joins", 												"Joins on the frame"),
	LAYER("layer", 												"The id for the layer of this visualization"),				
	LAMBDA("lambda", 											"Name of the lambda transformtion to perform"),				
	LANGUAGE("language",                    	                "Language in which this expression needs to be interpreted"),
	LAYOUT_KEY("layout", 										"The layout of the insight, used as a tag"),
	LIMIT("limit", 												"Limit to add for the query results"),
	LOGICAL_NAME("logicalNames", 								"Column alias to be added to the master database"),
	MAINTAIN_COLUMNS("maintainCols", 							"Any additional columns to keep in the frame"),
	MAP("map", 													"Map of project properties that need to be set for a project"),
	MESSAGE("message", 											"Message to display for logging"),									
	METAMODEL("metamodel", 										"The metamodel map for a database."),
	MERGE_CELLS("merge", 										"When the export is generated should the rowspans, colspans be merged"),
	MUSTACHE("mustache", 										"Boolean true/false if the html passed in is a mustache template"),
	MUSTACHE_VARMAP("mustacheVars", 							"Map containing the replacement values for a mustache tempalte. Most reactors will require mustache=true for this to be utilized"),
	MVN_GOALS("mvn_goals", 										"The maven goals to execute"),							
	NAME("name",												"Unique identifier"), 								
	NEW_COLUMN("newCol", 										"Name of the new column being created"),
	NEW_HEADER_NAMES("newHeaders", 								"New header names for a file"),
	NEW_VALUE("newValue", 										"New value used to replace an existing value"),
	NUM_DISPLAY("display",										"The number of results to display"),
	NUMERIC_VALUE("numValue", 									"Numeric value to be used in the operation"),
	NUMERIC_VALUES("numValues", 								"Numeric values to be used in the operation"),
	OFFSET("offset", 											"Offset to add for the query results"),
	ONLY_FAVORITES("onlyFavorites", 							"Get apps/insights which are favorited by the user"),
	OPERATOR("operator", 										"The operator to use for identifying this filter such as > , < = != etc. "),
	OPERATORU("operatoru", 										"Unique operator to use for identifying this filter such as > , < = != etc. For instance this can be a = 1 AND a = 2 in which case the first = would be and.left.="),
	OPTIONS("options", 											"Map of option values"),
	ORNAMENTS_KEY("ornaments", 									"Panel ornaments"),
	OUTPUT_FILE_PATH("outputFilePath", 							"Output file path to be used to destinguish if a reactor already has filePath passed in for an input file"),
	OVERRIDE("override", 										"Indicates if the existing value will be overridden or if a new value will be created"),
	PANEL("panel", 												"Id of the panel"),
	PANEL_ORDER_IDS("panelOrderIds",							"Order of the panels in insight"),
	PANEL_LABEL_KEY("panelLabel", 								"Label for the panel"),
	PANEL_POSITION_KEY("position", 								"Panel position map"),
	PANEL_VIEW_KEY("panelView", 								"Text that specifies what is displayed in the panel (e.g., \"visualization\")"),
	PANEL_VIEW_OPTIONS_KEY("panelViewOptions", 					"The panel view options"),
	PARAM_KEY("params", 										"The parameters for the insight map"),
	PARAM_NAME("paramName", 									"The name of the parameter"),
	PARAM_STRUCT("paramStruct",									"Map containing the details for the parameter"),
	PARAM_VALUES_MAP("paramValues",								"Map containing the param name to param values"),
	PARALLEL_WORKER("parallel_worker", 							"Class that will run as a thread"),
	PASSWORD("password", 										"Password used in conjunction with the username for access to a service"),
	PDF_PAGE_NUMBERS("pdfPageNumbers",							"Boolean to add the page numbers to the pdf"),
	PDF_PAGE_NUMBERS_IGNORE_FIRST("pdfPageNumbersIgnoreFirst",	"Boolean to include the page numbers on teh first page of the pdf (defaults to true)"),
	PDF_START_PAGE_NUM("pdfStartPageNumbers",               	"Integer containing the start value for the page number (default to 1)"),
	PDF_SIGNATURE_BLOCK("pdfSignatureBlock",					"Boolean to add digital signature block in exisitng pdf file"),
	PDF_SIGNATURE_LABEL("pdfSignatureLabel",                	"String containing text to add above the signature block to specify signature label"),
	PERMISSION("permission", 									"Permission level"),
	PIXEL("pixel", 												"Pixel script as string"),
	PIXEL_ID("pixelId", 										"The pixel id for this pixel step"),
	PLACE_HOLDER_DATA("placeHolderData",						"Updated place holder information of the template"),
	PLANNER("planner", 											"The planner"),
	PORT("port",												"The connection port."),
	POSITION_MAP("positionMap", 								"Map containing positions for elements"),
	PRAGMA("pragma",											"Directives that can be provided with the query like cache"),
	PREDEFINED_PARAM_STRUCT("preDefinedParamStruct",			"Map containing the details for a predefined paramter"),	
	PROJECT("project", 											"Name of the project"),
	PROJECT_PROPERTIES_MAP("propMap", 							"Map containing the values for the project properties"),
	PROP_FILE("propFile", 									    "The path to the properties file."),
	RANDOM_VALS("randomVals",									"The number of random values to use for the routine"),
	QUERY_PART("querypart",										"Specify the query part that is being replaced. This will help toward REPL"),	
	REACTOR("reactor", 											"Reactor name"),
	RECIPE("recipe", 											"Recipe that shows the sequence of pixels resulting in the insight"),
	RECIPE_PARAMETERS("recipeParameters", 						"Parameters for the recipe to execute on the insight recipe"),
	REGEX("regex", 												"Regular expression (sequence of characters)"),
	RELATIVE_PATH("relativePath", 								"Relative file path for user assets"),
	RELOAD("reload", 											"Reload an object or connection"),
	REPLACE("replace",											"Replace data when adding to existing app"),
	REPOSITORY("repository", 									"Name of the repository. Usually namespaced to be username\\appname"),
	ROUTINE("routine",											"The name of the routine"),
	ROW_COUNT("rowCount",										"Boolean get the number of rows"),
	ROW_GROUPS("rowGroups",										"Row Grouping that needs to be done for pivot"),
	ROW_GUTTER("rowgutter", 								    "Number of rows to pad between subsequent tables"),
	RULES_MAP("rulesMap",										"The map of rules for validation, including information such as the name of the rule, the rule definition, the columns, and the description"),
	SCALE("scale", 												"How much to scale the graph, default value is set at 20 based on screen size"),
	SCHEMA("schema",                	                        "The database schema."),
	SEARCH("search",											"The search term."),
	SESSION_ID("sessionId", 									"Id of the session"),
	SHAPE_INDEX("shapeIndex",                 		            "Which particular shape to replace on the powerpoint"),
	SHEET("sheet",												"Id for the sheet"),
	SHEET_CONFIG_KEY("sheetConfig", 							"Configuration map for the sheet"),
	SHEET_LABEL_KEY("sheetLabel", 								"Label for the sheet"),
	SHEET_NAME("sheetName",										"The name of the excel sheet"),
	SLIDE_LAYOUT("slideLayout",									"Name of the slide layout name to use for the capture"),
	SORT("sort", 												"Sort direction: ascending (\"asc\") or decending (\"desc\")"),
	SPACE("space", 												"The space to work with assets (user, insight, app)."),
	START("start",												"Start value for a between reactor"),
	START_ROW("startRow",										"The starting row number to import from a file."),
	STATEMENT("statement", 										"Statement to be evaluated"),
	SUBTOTALS("subtotals",										"All the columns in a pivot that you need subtotal for. Default is all. "),	
	SUM_RANGE("sumRange", 										"Range that values to sum over"),
	SYNC_PULL("dual", 											"True/False value to determine if the sync should also pull the latest updates from the repository"),
	SYNC_DATABASE("syncDatabase", 								"True/False value to detetermine if the database should be published with the app"),
	SPLOT("splot", 								                "Seaborn plot expression"),
	TABLE("table", 												"The name of the table"),
	TABLES("tables",											"List of table names"),
	TABLE_HEADER("theader", 									"Header specific to this table if any / title"),
	TABLE_FOOTER("tfooter", 									"Footer specific to this table if any "),
	TAGS("tags",												"Metadata tags for an app or insight"),		
	TASK("task", 												"Task object (can retrieve the object by using Task(taskId) where taskId is the unique id for the task)"),
	TASK_ID("taskId", 											"Unique id of the task within the insight"),
	TRAVERSAL("traversal", 										"The traversal path within the map"),
	TEMPLATE_NAME("template_name",              	            "Name of the template which has been uploaded "),
    TEMPLATE_FILE("template_file",								"File Name of the template to be used for export"),
    TEMPLATE("template",										"Template folder name"),
	UNIQUE_COLUMN("uniqueColumn", 								"Unique column identifier for csv/excel table uploads"),
	USE_FRAME_FILTERS("useFrameFilters", 						"A boolean indication (true or false) to use frame filters"), 	
	USERNAME("username", 										"Unique identifier for the user to access a service"),
	QUERY_KEY("query", 											"Query string to be executed on the database"),
	QUERY_STRUCT("qs", 											"QueryStruct object that contains selectors, filters, and joins"),
	URL("url",													"The url"),
	USE_PANEL("usePanel",                   	                "Boolean to export each panel as a separate sheet on exports instead of the entire dashboard"),
	USE_LABEL("useLabel",										"Boolean to use the label to query a graph database"),
	VALUE("value", 												"The instance value in a column, or the numeric or string value used in a operation"),
	VALUES("values", 											"Numeric or string values used as input text or inputs to an operation"),
	VARIABLE("variable", 										"Pixel variable consisting of only alphanumeric characters and underscores"),
	VERSION("version", 											"The version"),
	WIDTH("width", 											    "The width to use for screenshot capture.");

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
