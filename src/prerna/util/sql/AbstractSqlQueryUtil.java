/*******************************************************************************
 * Copyright 2015 Defense Health Agency (DHA)
 *
 * If your use of this software does not include any GPLv2 components:
 * 	Licensed under the Apache License, Version 2.0 (the "License");
 * 	you may not use this file except in compliance with the License.
 * 	You may obtain a copy of the License at
 *
 * 	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * 	Unless required by applicable law or agreed to in writing, software
 * 	distributed under the License is distributed on an "AS IS" BASIS,
 * 	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 	See the License for the specific language governing permissions and
 * 	limitations under the License.
 * ----------------------------------------------------------------------------
 * If your use of this software includes any GPLv2 components:
 * 	This program is free software; you can redistribute it and/or
 * 	modify it under the terms of the GNU General Public License
 * 	as published by the Free Software Foundation; either version 2
 * 	of the License, or (at your option) any later version.
 *
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 *******************************************************************************/
package prerna.util.sql;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import prerna.algorithm.api.ITableDataFrame;
import prerna.algorithm.api.SemossDataType;
import prerna.engine.api.IEngine;
import prerna.engine.api.IRawSelectWrapper;
import prerna.engine.impl.rdbms.RDBMSNativeEngine;
import prerna.query.interpreters.IQueryInterpreter;
import prerna.query.interpreters.sql.SqlInterpreter;
import prerna.rdf.engine.wrappers.WrapperManager;
import prerna.sablecc2.om.Join;
import prerna.test.TestUtilityMethods;
import prerna.util.Constants;
import prerna.util.Utility;

public abstract class AbstractSqlQueryUtil {
	
	// special key when not required
	public static final String NO_KEY_REQUIRED = "NO_KEY_REQUIRED";
	
	// inputs for connection string builder
	public static final String CONNECTION_STRING = Constants.CONNECTION_URL;
	public static final String DRIVER_NAME = "dbDriver";

	public static final String HOSTNAME = "hostname";
	public static final String PORT = "port";
	public static final String DATABASE = "database";
	public static final String SCHEMA = "schema";
	public static final String USERNAME = Constants.USERNAME;
	public static final String PASSWORD = Constants.PASSWORD;
	public static final String ADDITIONAL = "additional";
	
	// relatively specific inputs
	// athena
	public static final String SERVICE = "service";
	public static final String REGION = "region";
	public static final String ACCESS_KEY = "accessKey";
	public static final String SECRET_KEY = "secretKey";
	public static final String OUTPUT = "output";
	// bigquery
	public static final String PROJECT_ID = "projectId";
	public static final String OAUTH_TYPE = "oauthType";
	public static final String OAUTH_SERVICE_ACCT_EMAIL = "oauthServiceAcctEmail";
	public static final String OAUTH_PRIVATE_KEY_PATH = "oauthPvtKeyPath";
	public static final String OAUTH_ACCESS_TOKEN = "oauthAccessToken";
	public static final String OAUTH_REFRESH_TOKEN = "oauthRefreshToken";
	public static final String OAUTH_CLIENT_ID = "oauthClientId";
	public static final String OAUTH_CLIENT_SECRET = "oauthClientSecret";

	public static final String DEFAULT_DATASET = "defaultDataSet";
	// snowflake
	public static final String WAREHOUSE = "warehouse";
	public static final String ROLE = "role";
	
	private static final Logger logger = LogManager.getLogger(AbstractSqlQueryUtil.class);

	protected RdbmsTypeEnum dbType = null;
	// there are 2 differnet ways of providing the inputs
	// properties - primarily for grabbing from SMSS files
	// map - primarily for getting input details from FE / JSON
	protected Properties properites;
	protected Map<String, Object> conDetails;

	protected String connectionUrl;
	protected String username;
	protected String password;
	
	// these should be replaced and use the properties / conDetails
	protected String hostname;
	protected String port;
	protected String schema;

	// reserved words
	protected List<String> reservedWords = null;
	// type conversions
	protected Map<String, String> typeConversionMap = new HashMap<>();

	AbstractSqlQueryUtil() {
		initTypeConverstionMap();
	}
	
	AbstractSqlQueryUtil(String connectionURL, String username, String password) {
		this.connectionUrl = connectionURL;
		this.username = username;
		this.password = password;
		initTypeConverstionMap();
	}

	/**
	 * Build the connection string from a JSON map
	 * @param configMap
	 * @return
	 * @throws SQLException 
	 */
	public abstract String buildConnectionString(Map<String, Object> configMap) throws RuntimeException;
	
	/**
	 * Build the connection string from a properties file (SMSS file)
	 * @param prop
	 * @return
	 */
	public abstract String buildConnectionString(Properties prop) throws RuntimeException;
	
	/**
	 * Method to get a connection to an existing RDBMS engine
	 * @param driverEnum
	 * @param connectionUrl
	 * @param connectionDetails
	 * @return
	 * @throws SQLException 
	 */
	public static Connection makeConnection(AbstractSqlQueryUtil util, String connectionUrl, Map<String, Object> connectionDetails) throws SQLException {
		return AbstractSqlQueryUtil.makeConnection(util.getDbType(), 
				connectionUrl, 
				(String) connectionDetails.get(util.getConnectionUserKey()), 
				(String) connectionDetails.get(util.getConnectionPasswordKey()));
	}
	
	/**
	 * Method to get a connection to an existing RDBMS engine
	 * If the username or password are null, we will assume the information is already provided within the connectionUrl
	 * @param connectionUrl
	 * @param userName
	 * @param password
	 * @param driver
	 * @return
	 * @throws SQLException 
	 */
	public static Connection makeConnection(RdbmsTypeEnum type, String connectionUrl, String userName, String password) throws SQLException {
		try {
			Class.forName(type.getDriver());
		} catch (ClassNotFoundException e) {
			logger.error(Constants.STACKTRACE, e);
			throw new SQLException("Unable to find driver for engine type");
		}

		// create the iterator
		Connection conn;
		try {
			if (userName == null || password == null) {
				conn = DriverManager.getConnection(connectionUrl);
			} else {
				conn = DriverManager.getConnection(connectionUrl, userName, password);
			}
		} catch (SQLException e) {
			logger.error(Constants.STACKTRACE, e);
			throw new SQLException(e.getMessage());
		}

		return conn;
	}
	
	
	/**
	 * Use this when we need to make any modifications to the connection object for
	 * proper usage Example ::: Adding user defined functions for RDBMS types that
	 * allow it
	 * 
	 * @param con
	 */
	public abstract void enhanceConnection(Connection con);

	/**
	 * Initialize the type conversion map to account for sql discrepancies in type
	 * names
	 */
	public abstract void initTypeConverstionMap();

	/////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////

	/*
	 * All connection details the setters and getters
	 */

	public RdbmsTypeEnum getDbType() {
		return dbType;
	}

	void setDbType(RdbmsTypeEnum dbType) {
		this.dbType = dbType;
	}

	public String getDriver() {
		return this.dbType.getDriver();
	}

	public String getHostname() {
		return hostname;
	}

	public String getPort() {
		return port;
	}

	public String getSchema() {
		return schema;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getConnectionUrl() {
		return connectionUrl;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	
	public String getConnectionUserKey() {
		return AbstractSqlQueryUtil.USERNAME;
	}
	
	public String getConnectionPasswordKey() {
		return AbstractSqlQueryUtil.PASSWORD;
	}

	public IQueryInterpreter getInterpreter(IEngine engine) {
		return new SqlInterpreter(engine);
	}

	public IQueryInterpreter getInterpreter(ITableDataFrame frame) {
		return new SqlInterpreter(frame);
	}

	/////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Set the list of reserved words
	 * 
	 * @param reservedWords
	 */
	public void setReservedWords(List<String> reservedWords) {
		this.reservedWords = reservedWords;
	}

	/**
	 * Check if the selector is in fact a reserved word
	 * 
	 * @param selector
	 * @return
	 */
	public boolean isSelectorKeyword(String selector) {
		return this.reservedWords != null && this.reservedWords.contains(selector.toUpperCase());
	}

	/**
	 * Get the escaped keyword Default is to wrap the selector in quotes
	 * 
	 * @param selector
	 * @return
	 */
	public String getEscapeKeyword(String selector) {
		return "\"" + selector + "\"";
	}

	public String escapeReferencedAlias(String alias) {
		return alias;
	}

	/**
	 * Escape sql statement literals
	 * 
	 * @param s
	 * @return
	 */
	public static String escapeForSQLStatement(String s) {
		if (s == null) {
			return s;
		}
		return s.replace("'", "''");
	}

	/**
	 * Escape regex searching
	 * 
	 * @param s
	 * @return
	 */
	public static String escapeRegexCharacters(String s) {
		s = s.trim();
		s = s.replace("(", "\\(");
		s = s.replace(")", "\\)");
		return s;
	}

	/**
	 * Flush clob to string
	 * 
	 * @param inputClob
	 * @return
	 */
	public static String flushClobToString(java.sql.Clob inputClob) {
		InputStream inputstream = null;
		if (inputClob != null) {
			try {
				inputstream = inputClob.getAsciiStream();
				return IOUtils.toString(inputstream);
			} catch (SQLException sqe) {
				logger.error(Constants.STACKTRACE, sqe);
			} catch (IOException e) {
				logger.error(Constants.STACKTRACE, e);
			}
		}
		return null;
	}
	
	/**
	 * Clean the table name so it is valid for SQL
	 * @param tableName
	 * @return
	 */
	public static String cleanTableName(String tableName) {
		tableName = Utility.makeAlphaNumeric(tableName);
		if(Character.isDigit(tableName.charAt(0))) {
			tableName = "_" + tableName;
		}
		return tableName;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/*
	 * Methods to clean the sql type
	 */

	/**
	 * Clean the types to account for sql naming differences
	 * 
	 * @param type
	 * @return
	 */
	public String cleanType(String type) {
		if (type == null) {
			type = "VARCHAR(800)";
		}
		type = type.toUpperCase();
		if (typeConversionMap.containsKey(type)) {
			type = typeConversionMap.get(type);
		} else {
			if (typeConversionMap.containsValue(type)) {
				return type;
			}
			type = "VARCHAR(800)";
		}
		return type;
	}

	/**
	 * Clean the types to account for sql naming differences
	 * 
	 * @param types
	 * @return
	 */
	public String[] cleanTypes(String[] types) {
		String[] cleanTypes = new String[types.length];
		for (int i = 0; i < types.length; i++) {
			cleanTypes[i] = cleanType(types[i]);
		}

		return cleanTypes;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/*
	 * This section is so I can properly convert the intended function names
	 */

	/**
	 * Get the sql function string
	 * 
	 * @param inputFunction
	 * @return
	 */

	// there are all the specific functions
	// the {@link #getSqlFunctionSyntax(String) getSqlFunctionSyntax}
	// only needs to be implemented in the AnsiSqlQueryUtil
	// where it loops through everything and the specifics can be
	// implemented in the query util implementations

	public abstract String getSqlFunctionSyntax(String inputFunction);

	public abstract String getMinFunctionSyntax();

	public abstract String getMaxFunctionSyntax();

	public abstract String getAvgFunctionSyntax();

	public abstract String getMedianFunctionSyntax();

	public abstract String getSumFunctionSyntax();

	public abstract String getStdevFunctionSyntax();

	public abstract String getCountFunctionSyntax();

	public abstract String getConcatFunctionSyntax();

	public abstract String getGroupConcatFunctionSyntax();

	public abstract String getLowerFunctionSyntax();

	public abstract String getCoalesceFunctionSyntax();

	public abstract String getRegexLikeFunctionSyntax();

	public abstract String getMonthNameFunctionSyntax();
	
	public abstract String getDayNameFunctionSyntax();
	
	public abstract String getQuarterFunctionSyntax();
	
	public abstract String getWeekFunctionSyntax();
	
	public abstract String getYearFunctionSyntax();
	
		// TODO Auto-generated method stub
		
	
	
	// date functions - require more complex inputs
	public abstract String getCurrentDate();
	
	public abstract String getDateAddFunctionSyntax(String timeUnit, int value, String  dateTimeField);
	
	/////////////////////////////////////////////////////////////////////////////////////

	/*
	 * This section is intended for modifications to select queries to pull data
	 */

	/**
	 * Add the limit and offset to a query
	 * 
	 * @param query
	 * @param limit
	 * @param offset
	 * @return
	 */
	public abstract StringBuilder addLimitOffsetToQuery(StringBuilder query, long limit, long offset);

	/**
	 * Remove duplicates that exist from an existing table by creating a new temp
	 * intermediary table
	 * 
	 * @param tableName
	 * @param fullColumnNameList
	 * @return
	 */
	public abstract String removeDuplicatesFromTable(String tableName, String fullColumnNameList);

	/**
	 * Create an insert prepared statement
	 * 
	 * @param tableName
	 * @param columns
	 */
	public abstract String createInsertPreparedStatementString(String tableName, String[] columns);

	/**
	 * Create an update prepared statement
	 * 
	 * @param tableName
	 * @param columnsToUpdate
	 * @param whereColumns
	 * @return
	 */
	public abstract String createUpdatePreparedStatementString(String tableName, String[] columnsToUpdate,
			String[] whereColumns);

	/**
	 * Create the syntax to merge 2 tables together
	 * 
	 * @param returnTableName  The return table name
	 * @param returnTableTypes
	 * @param leftTableName    The left table
	 * @param leftTableTypes   The {header -> type} of the left table
	 * @param rightTableName   The right table name
	 * @param rightTableTypes  The {header -> type} of the right table
	 * @param joins            The joins between the right and left table
	 * @return
	 */
	public abstract String createNewTableFromJoiningTables(String returnTableName, String leftTableName,
			Map<String, SemossDataType> leftTableTypes, String rightTableName,
			Map<String, SemossDataType> rightTableTypes, List<Join> joins, Map<String, String> leftTableAlias,
			Map<String, String> rightTableAlias);

	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Does the RDBMS type support array data types
	 * 
	 * @return
	 */
	public abstract boolean allowArrayDatatype();

	/**
	 * Does the RDBMS type support boolean types
	 * @return
	 */
	public abstract boolean allowBooleanDataType();
	
	/**
	 * Get the date time data type used by the RDBMS
	 * @return
	 */
	public abstract String getDateWithTimeDataType();
	
	/**
	 * Does the RDBMS type support blob data type
	 * @return
	 */
	public abstract boolean allowBlobDataType();
	
	/**
	 * Get the RDBMS type equivalent for blobs
	 * @return
	 */
	public abstract String getBlobReplacementDataType();
	
	/**
	 * Does the RDBMS type support blob java object storage
	 * i.e. - connection.createBlob();
	 * @return
	 */
	public abstract boolean allowBlobJavaObject();
	
	/**
	 * Does the engine allow you to add a column to an existing table
	 * 
	 * @return
	 */
	public abstract boolean allowAddColumn();

	/**
	 * Does the engine allow you to add multiple columns in a single statement
	 * 
	 * @return
	 */
	public abstract boolean allowMultiAddColumn();

	/**
	 * Does the engine allow you to rename a column in an existing table
	 * 
	 * @return
	 */
	public abstract boolean allowRedefineColumn();

	/**
	 * Does the engine allow you to drop a column in an existing table
	 * 
	 * @return
	 */
	public abstract boolean allowDropColumn();

	/**
	 * Does the engine allow "CREATE TABLE IF NOT EXISTS " syntax
	 * 
	 * @return
	 */
	public abstract boolean allowsIfExistsTableSyntax();

	/**
	 * Does the engine allow "CREATE INDEX IF NOT EXISTS " syntax
	 * 
	 * @return
	 */
	public abstract boolean allowIfExistsIndexSyntax();

	/**
	 * Does the engine allow "ALTER TABLE xxx ADD COLUMN IF NOT EXISTS" and "ALTER
	 * TABLE xxx DROP COLUMN IF EXISTS" syntax
	 * 
	 * @return
	 */
	public abstract boolean allowIfExistsModifyColumnSyntax();
	
	/**
	 * Does the engine allow " ADD CONSTRAINT IF NOT EXISTS " syntax
	 * 
	 * @return
	 */
	public abstract boolean allowIfExistsAddConstraint();

	/////////////////////////////////////////////////////////////////////////

	/*
	 * Create table scripts
	 */

	/**
	 * Create a new table with passed in columns + types + default values
	 * 
	 * @param tableName
	 * @param colNames
	 * @param types
	 * @return
	 */
	public abstract String createTable(String tableName, String[] colNames, String[] types);

	/**
	 * Create a new table with passed in columns + types + default values
	 * 
	 * @param tableName
	 * @param colNames
	 * @param types
	 * @param defaultValues
	 * @return
	 */
	public abstract String createTableWithDefaults(String tableName, String[] colNames, String[] types, Object[] defaultValues);

	/**
	 * Create a new table with custom constraints
	 * 
	 * @param tableName
	 * @param colNames
	 * @param types
	 * @param customConstraints
	 * @return
	 */
	public abstract String createTableWithCustomConstraints(String tableName, String[] colNames, String[] types, Object[] customConstraints);

	/**
	 * Create a new table if it does not exist with passed in columns + types +
	 * default values
	 * 
	 * @param tableName
	 * @param colNames
	 * @param types
	 * @return
	 */
	public abstract String createTableIfNotExists(String tableName, String[] colNames, String[] types);

	/**
	 * Create a new table if it does not exist with passed in columns + types +
	 * default values
	 * 
	 * @param tableName
	 * @param colNames
	 * @param types
	 * @param defaultValues
	 * @return
	 */
	public abstract String createTableIfNotExistsWithDefaults(String tableName, String[] colNames, String[] types, Object[] defaultValues);

	/**
	 * Create a new table if it does not exist with custom constraints
	 * 
	 * @param tableName
	 * @param colNames
	 * @param types
	 * @param customConstraints
	 * @return
	 */
	public abstract String createTableIfNotExistsWithCustomConstraints(String tableName, String[] colNames, String[] types, Object[] customConstraints);
	
	/*
	 * Drop table scripts
	 */

	/**
	 * Drop a table
	 * 
	 * @param tableName
	 * @return
	 */
	public abstract String dropTable(String tableName);

	/**
	 * Drop a table if it exists
	 * 
	 * @param tableName
	 * @return
	 */
	public abstract String dropTableIfExists(String tableName);

	/*
	 * Alter table scripts
	 */

	/**
	 * Rename a table
	 * 
	 * @param tableName
	 * @param newName
	 * @return
	 */
	public abstract String alterTableName(String tableName, String newTableName);

	/**
	 * Add a new column to an existing table
	 * 
	 * @param tableName
	 * @param newColumn
	 * @param newColType
	 * @return
	 */
	public abstract String alterTableAddColumn(String tableName, String newColumn, String newColType);

	/**
	 * Add a new column to an existing table with default value
	 * 
	 * @param tableName
	 * @param newColumn
	 * @param newColType
	 * @param defaultValue
	 * @return
	 */
	public abstract String alterTableAddColumnWithDefault(String tableName, String newColumn, String newColType,
			Object defualtValue);

	/**
	 * Add a new column to an existing table if the column does not exist
	 * 
	 * @param tableName
	 * @param newColumn
	 * @param newColType
	 * @return
	 */
	public abstract String alterTableAddColumnIfNotExists(String tableName, String newColumn, String newColType);

	/**
	 * Add a new column to an existing table if the column does not exist with
	 * default value
	 * 
	 * @param tableName
	 * @param newColumn
	 * @param newColType
	 * @param defaultValue
	 * @return
	 */
	public abstract String alterTableAddColumnIfNotExistsWithDefault(String tableName, String newColumn,
			String newColType, Object defualtValue);

	/**
	 * Add new columns to an existing table
	 * 
	 * @param tableName
	 * @param newColumns
	 * @param newColTypes
	 * @return
	 */
	public abstract String alterTableAddColumns(String tableName, String[] newColumns, String[] newColTypes);

	/**
	 * Add new columns to an existing table with default value
	 * 
	 * @param tableName
	 * @param newColumns
	 * @param newColTypes
	 * @param defaultValue
	 * @return
	 */
	public abstract String alterTableAddColumnsWithDefaults(String tableName, String[] newColumns, String[] newColTypes,
			Object[] defaultValues);

	/**
	 * Drop a column from an existing table
	 * 
	 * @param tableName
	 * @param columnName
	 * @return
	 */
	public abstract String alterTableDropColumn(String tableName, String columnName);

	/**
	 * Drop a column from an existing table if it exists
	 * 
	 * @param tableName
	 * @param columnName
	 * @return
	 */
	public abstract String alterTableDropColumnIfExists(String tableName, String columnName);

	/**
	 * Modify a column definition
	 * 
	 * @param tableName
	 * @param columnName
	 * @param dataType
	 * @return
	 */
	public abstract String modColumnType(String tableName, String columnName, String dataType);

	/**
	 * Modify a column definition with default value
	 * 
	 * @param tableName
	 * @param columnName
	 * @param dataType
	 * @param defaultValue
	 * @return
	 */
	public abstract String modColumnTypeWithDefault(String tableName, String columnName, String dataType,
			Object defualtValue);

	/**
	 * Modify a column definition if it exists
	 * 
	 * @param tableName
	 * @param columnName
	 * @param dataType
	 * @return
	 */
	public abstract String modColumnTypeIfExists(String tableName, String columnName, String dataType);

	/**
	 * Modify a column definition with a default value if it exists
	 * 
	 * @param tableName
	 * @param columnName
	 * @param dataType
	 * @param defaultValue
	 * @return
	 */
	public abstract String modColumnTypeIfExistsWithDefault(String tableName, String columnName, String dataType,
			Object defualtValue);

	/*
	 * Index
	 */

	/**
	 * Create an index on a table for a given column
	 * 
	 * @param indexName
	 * @param tableName
	 * @param column
	 * @return
	 */
	public abstract String createIndex(String indexName, String tableName, String columnName);

	/**
	 * Create an index on a table with a set of columns
	 * 
	 * @param indexName
	 * @param tableName
	 * @param columns
	 * @return
	 */
	public abstract String createIndex(String indexName, String tableName, Collection<String> columns);

	/**
	 * Create an index on a table for a given column
	 * 
	 * @param indexName
	 * @param tableName
	 * @param column
	 * @return
	 */
	public abstract String createIndexIfNotExists(String indexName, String tableName, String columnName);

	/**
	 * Create an index on a table with a set of columns
	 * 
	 * @param indexName
	 * @param tableName
	 * @param columns
	 * @return
	 */
	public abstract String createIndexIfNotExists(String indexName, String tableName, Collection<String> columns);

	/**
	 * Drop an existing index
	 * 
	 * @param indexName
	 * @param tableName
	 * @return
	 */
	public abstract String dropIndex(String indexName, String tableName);

	/**
	 * Drop an index if it exists
	 * 
	 * @param indexName
	 * @param tableName
	 * @return
	 */
	public abstract String dropIndexIfExists(String indexName, String tableName);

	/**
	 * Insert a row into a table
	 * 
	 * @param tableName
	 * @param columnNames
	 * @param types
	 * @param values
	 * @return
	 */
	public abstract String insertIntoTable(String tableName, String[] columnNames, String[] types, Object[] values);

	/**
	 * Drop all rows from a table
	 * 
	 * @param tableName
	 * @return
	 */
	public abstract String deleteAllRowsFromTable(String tableName);

	/////////////////////////////////////////////////////////////////////////////////////

	/*
	 * Query database scripts
	 */

	/**
	 * Query to execute If has next, the table exists The schema input is optional
	 * and only required by certain engines
	 * 
	 * @param tableName
	 * @param schema
	 * @return
	 */
	public abstract String tableExistsQuery(String tableName, String schema);

	/**
	 * Query to get the list of column names for a table The schema input is
	 * optional and only required by certain engines Returns the column name and
	 * column type
	 * 
	 * @param tableName
	 * @param schema
	 * @return
	 */
	public abstract String getAllColumnDetails(String tableName, String schema);

	/**
	 * Query to execute to get the column details Can also imply if the query
	 * returns that the column exists
	 * 
	 * @param tableName
	 * @param columnName
	 * @param schema
	 * @return
	 */
	public abstract String columnDetailsQuery(String tableName, String columnName, String schema);

	/**
	 * Query to get a list of all the indexes in the schema Since indexes are not
	 * unique across tables, this must return (index based) 1) INDEX_NAME 2)
	 * TABLE_NAME The schema input is optional and only required by certain engines
	 * 
	 * @param schema
	 * @return
	 */
	public abstract String getIndexList(String schema);

	/**
	 * Query to get the index details Must return data in the following order (index
	 * based) 1) TABLE_NAME 2) COLUMN_NAME The schema input is optional and only
	 * required by certain engines
	 * 
	 * @param indexName
	 * @return
	 */
	public abstract String getIndexDetails(String indexName, String tableName, String schema);

	/**
	 * Query to get all the indexes on a given table Must return the data in the
	 * following order (index based) 1) INDEX NAME 2) COLUMN_NAME The schema input
	 * is optional and only required by certain engines
	 * 
	 * @param tableName
	 * @param schema
	 * @return
	 */
	public abstract String allIndexForTableQuery(String tableName, String schema);

	/**
	 * Query to get if a constraint exists
	 * @param constraintName
	 * @return
	 */
	public abstract String constraintExistsQuery(String constraintName);
	
	/////////////////////////////////////////////////////////////////////////////////////

	/*
	 * Utility methods
	 */

	/**
	 * Test on the connection if a table exists Assumption that the conn and sql
	 * util are of same type
	 * 
	 * @param conn
	 * @param tableName
	 * @param schema
	 * @return
	 */
	public boolean tableExists(Connection conn, String tableName, String schema) {
		String query = this.tableExistsQuery(tableName, schema);
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			return rs.next();
		} catch (SQLException e) {
			return false;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error(Constants.STACKTRACE, e);
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					logger.error(Constants.STACKTRACE, e);
				}
			}
		}
	}

	/**
	 * Test on the connection if a table exists Assumption that the conn and sql
	 * util are of same type
	 * 
	 * @param engine
	 * @param tableName
	 * @param schema
	 * @return
	 */
	public boolean tableExists(IEngine engine, String tableName, String schema) {
		String query = this.tableExistsQuery(tableName, schema);
		IRawSelectWrapper wrapper = null;
		try {
			wrapper = WrapperManager.getInstance().getRawWrapper(engine, query);
			if (wrapper.hasNext()) {
				return true;
			}
		} catch (Exception e) {
			logger.error(Constants.STACKTRACE, e);
		} finally {
			if (wrapper != null) {
				wrapper.cleanUp();
			}
		}

		return false;
	}

	/**
	 * Helper method to see if an index exists based on Query Utility class
	 * 
	 * @param queryUtil
	 * @param indexName
	 * @param tableName
	 * @param schema
	 * @return
	 */
	public boolean indexExists(IEngine engine, String indexName, String tableName, String schema) {
		String indexCheckQ = this.getIndexDetails(indexName, tableName, schema);
		IRawSelectWrapper wrapper = null;
		try {
			wrapper = WrapperManager.getInstance().getRawWrapper(engine, indexCheckQ);
			if (wrapper.hasNext()) {
				return true;
			}
		} catch (Exception e) {
			logger.error(Constants.STACKTRACE, e);
		} finally {
			if (wrapper != null) {
				wrapper.cleanUp();
			}
		}

		return false;
	}
	
	/**
	 * Test on the connection if a constraint exists
	 * 
	 * @param conn
	 * @param constraintName
	 * @return
	 */
	public boolean constraintExists(Connection conn, String constraintName) {
		String query = this.constraintExistsQuery(constraintName);
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			return rs.next();
		} catch (SQLException e) {
			return false;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error(Constants.STACKTRACE, e);
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					logger.error(Constants.STACKTRACE, e);
				}
			}
		}
	}
	
	/**
	 * Test on the engine if a constraint exists
	 * 
	 * @param engine
	 * @param tableName
	 * @param schema
	 * @return
	 */
	public boolean constraintExists(IEngine engine, String constraintName) {
		String query = this.constraintExistsQuery(constraintName);
		IRawSelectWrapper wrapper = null;
		try {
			wrapper = WrapperManager.getInstance().getRawWrapper(engine, query);
			if (wrapper.hasNext()) {
				return true;
			}
		} catch (Exception e) {
			logger.error(Constants.STACKTRACE, e);
		} finally {
			if (wrapper != null) {
				wrapper.cleanUp();
			}
		}

		return false;
	}

	/**
	 * Get all the table columns Will return them all upper cased
	 * 
	 * @param conn
	 * @param tableName
	 * @param schema
	 * @return
	 */
	public List<String> getTableColumns(Connection conn, String tableName, String schema) {
		List<String> tableColumns = new Vector<>();
		String query = this.getAllColumnDetails(tableName, schema);
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				tableColumns.add(rs.getString(1).toUpperCase());
			}
		} catch (SQLException e) {
			logger.error(Constants.STACKTRACE, e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error(Constants.STACKTRACE, e);
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					logger.error(Constants.STACKTRACE, e);
				}
			}
		}

		return tableColumns;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/*
	 * These are older methods
	 * Need to come back and see where to 
	 * utilize these/clean up
	 */

	public String getDialectSelectRowCountFrom(String tableName, String whereClause) {
		String query = "SELECT COUNT(*) as ROW_COUNT FROM " + tableName;
		if (whereClause.length() > 0) {
			query += " WHERE " + whereClause;
		}
		return query;
	}

	public String getDialectMergeStatement(String tableKey, String insertIntoClause, List<String> columnList,
			HashMap<String, String> whereValues, String fkVal, String whereClause) {
		ArrayList<String> subqueries = new ArrayList<>();
		String query = "INSERT INTO " + tableKey + " (" + insertIntoClause + ") SELECT DISTINCT ";
		for (String column : columnList) {
			String tempColumnName = column + "TEMP";
			String subquery = "(SELECT DISTINCT " + column + " FROM " + tableKey + " WHERE " + whereClause;
			String tempquery = subquery + " union select null where not exists" + subquery + ")) AS " + tempColumnName;
			subqueries.add(tempquery);
			query += tempColumnName + "." + column + " AS " + column + ",";
		}
		for (String whereKey : whereValues.keySet()) {
			query += whereValues.get(whereKey) + " AS " + whereKey + ", ";
		}
		query += fkVal + " FROM " + tableKey;
		for (int i = 0; i < subqueries.size(); i++) {
			query += ", " + subqueries.get(i);
		}
		return query;
	}

	public String hashColumn(String tableName, String[] columns){
		throw new UnsupportedOperationException();
	}

	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////

	public static void main(String[] args) throws Exception {
		TestUtilityMethods.loadAll("C:\\workspace2\\Semoss_Dev\\RDF_Map.prop");

		RDBMSNativeEngine security = (RDBMSNativeEngine) Utility.getEngine("security");
		AbstractSqlQueryUtil util = security.getQueryUtil();
		IRawSelectWrapper wrapper = WrapperManager.getInstance().getRawWrapper(security,
				"SELECT * FROM PRAGMA_TABLE_INFO('USER') WHERE NAME='email'");
		while (wrapper.hasNext()) {
			logger.debug(wrapper.next());
		}
	}
}