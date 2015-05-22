/*******************************************************************************
 * Copyright 2015 SEMOSS.ORG
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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import prerna.util.Utility;

public abstract class SQLQueryUtil {
	
	public enum DB_TYPE {H2_DB,MARIA_DB}
	
	public static final String USE_OUTER_JOINS_NO = "NO";
	public static final String USE_OUTER_JOINS_YES = "YES";
	
	private String defaultDbUserName = "";
	private String defaultDbPassword = "";
	
	// generic db info
	private String dialectAllTables = "";
	private String resultAllTablesTableName = "";
	private String dialectAllColumns = " SHOW COLUMNS FROM ";
	private String resultAllColumnsColumnName = "FIELD";
	private String resultAllColumnsColumnType = "TYPE";
	
	public final String dialectSelectAllFrom = " SELECT * FROM ";
	
	private String dialectSelectRowCountFrom = " SELECT COUNT(*) as ROW_COUNT FROM ";  
	private String resultSelectRowCountFromRowCount = "ROW_COUNT";

	//create
	public final String dialectCreateTable = " CREATE TABLE "; 
	public final String dialectAlterTable = " ALTER TABLE ";
	public final String dialectDropTable = " DROP TABLE ";
	
	//update
	
	//index
	private String dialectAllIndexesInDB = "";
	private String dialectIndexInfo = "";
	private String resultAllIndexesInDBIndexName = "INDEX_NAME";
	private String resultAllIndexesInDBColumnName = "COLUMN_NAME";
	private String resultAllIndexesInDBTableName = "TABLE_NAME";
	private String dialectCreateIndex = "";
	private String dialectDropIndex = "DROP INDEX ";
	public final String dialectSelectDistinct = "SELECT DISTINCT ";
	
	
	//"full outer join"
	private String dialectOuterJoinLeft = "";
	private String dialectOuterJoinRight = "";
	

	public static SQLQueryUtil initialize(SQLQueryUtil.DB_TYPE dbtype){
		if(dbtype == SQLQueryUtil.DB_TYPE.MARIA_DB){
			return new MariaDbQueryUtil();
		} else {
			return new H2QueryUtil();
		}
	}
	
	public abstract SQLQueryUtil.DB_TYPE getDatabaseType();
	
	public abstract String getDefaultOuterJoins();
	
	public abstract String getConnectionURL(String baseFolder,String dbname);
	
	public abstract String getDatabaseDriverClassName();
	
	public abstract String getDialectIndexInfo(String indexName, String dbName);
	
	//"full outer join"
	//this is definitely going to be db specific
	public abstract String getDialectDistinctFullOuterJoinQuery(String selectors, ArrayList<String> rightJoinsArr, 
			ArrayList<String> leftJoinsArr, ArrayList<String> joinsArr, String filters, int limit);
	
	public String getDefaultDBUserName(){
		return this.defaultDbUserName;
	}
	
	public String getDefaultDBPassword(){
		return this.defaultDbPassword;
	}
	
	// generic db info getters 
	public String getDialectAllTables(){
		return this.dialectAllTables;
	}
	public String getResultAllTablesTableName(){
		return this.resultAllTablesTableName;
	}
	public String getDialectAllColumns(){
		return this.dialectAllColumns;
	}
	public String getDialectAllColumns(String tableName){
		return this.dialectAllColumns + tableName ;
	}
	public String getAllColumnsResultColumnName(){
		return this.resultAllColumnsColumnName;
	}
	public String getResultAllColumnsColumnType(){
		return this.resultAllColumnsColumnType;
	}
	public String getDialectSelectRowCountFrom(String tableName, String whereClause){
		String query = this.dialectSelectRowCountFrom + tableName;
		if(whereClause.length()>0){
			query += " WHERE " + whereClause;
		}
		return query;
	}
	public String getResultSelectRowCountFromRowCount(){
		return this.resultSelectRowCountFromRowCount;
	}
	
	//index
	public String getDialectAllIndexesInDB(){
		return this.dialectAllIndexesInDB;
	}
	public String getDialectAllIndexesInDB(String schema){
		return this.dialectAllIndexesInDB + "'" + schema + "'"; //use bind bariables?
	}
	public String getDialectIndexInfo(){
		return this.dialectIndexInfo;
	}
	public String getResultAllIndexesInDBIndexName(){
		return this.resultAllIndexesInDBIndexName;
	}
	public String getResultAllIndexesInDBColumnName(){
		return this.resultAllIndexesInDBColumnName;
	}
	public String getResultAllIndexesInDBTableName(){
		return this.resultAllIndexesInDBTableName;
	}
	public String getDialectDropIndex(){
		return this.dialectDropIndex;
	}
	public String getDialectDropIndex(String indexName){
		return getDialectDropIndex(indexName,"");
	}
	public String getDialectDropIndex(String indexName, String tableName){
		return this.dialectDropIndex + indexName;
	}
	public String getDialectCreateIndex(String indexName, String tablename, String columnInIndex){
		ArrayList<String> columnsInIndexArr = new ArrayList();
		columnsInIndexArr.add(columnInIndex);
		return getDialectCreateIndex(indexName, tablename, columnsInIndexArr);
	}
	public String getDialectCreateIndex(String indexName, String tablename, ArrayList<String> columnsInIndex){
		String createIndexText = "";
		for(String columnName: columnsInIndex){
			if(createIndexText.length() == 0){
				createIndexText = "CREATE INDEX " + indexName + " ON " + tablename  + "(";
			} else {
				createIndexText += ",";
			}
			createIndexText += columnName;
		}
		createIndexText += ")";
		return createIndexText;
	}
	
	//full outer join is abstract (see above...)
	
	//inner join
	public String getDialectDistinctInnerJoinQuery(String selectors, String froms, String joins){
		return getDialectDistinctInnerJoinQuery(selectors, froms, joins, -1);
	}
	public String getDialectDistinctInnerJoinQuery(String selectors, String froms, String joins, int limit){
		String query = this.dialectSelectDistinct + selectors + "  FROM  " + froms + joins;
		if(limit != -1){
			query += " LIMIT " + limit;
		}
		return query;
	}
	//just get the right and left outer join syntax
	public String getDialectOuterJoinLeft(String fromTable){
		return dialectOuterJoinLeft + fromTable;
	}
	public String getDialectOuterJoinRight(String fromTable){
		return dialectOuterJoinRight + fromTable;
	}
	
	//select from dual
	public String getDialectDistinctFromDual(String selectors){
		return getDialectDistinctFromDual(selectors, -1);
	}
	public String getDialectDistinctFromDual(String selectors, int limit){
		String query = this.dialectSelectDistinct + selectors + " FROM DUAL ";
		if(limit != -1){
			query += " LIMIT " + limit;
		}
		return query;
	}
	
	public String getDialectRemoveDuplicates(String tableName, String fullColumnNameList){
		String createTable = "CREATE TABLE " + tableName + "_TEMP AS ";
		String subQuery = "(SELECT DISTINCT " + fullColumnNameList
			+ " FROM " + tableName+" WHERE " + tableName 
			+ " IS NOT NULL AND TRIM(" + tableName + ") <> '' )";
		return createTable + subQuery;
	}
	
	public String dialectVerifyTableExists(String tableName){
		String query = getDialectSelectRowCountFrom("INFORMATION_SCHEMA.TABLES","TABLE_NAME = '" + tableName +"'");
		return query;
	}
	
	public String getDialectDropTable(String tableName){
		return this.dialectDropTable + tableName;
	}
	
	public String getDialectAlterTableName(String fromName, String toName){
		return this.dialectAlterTable + fromName + " RENAME TO " + toName; 
	}

	public String getDialectMergeStatement(String tableKey, String insertIntoClause, String insertValuesClause, String whereClause){
		String query = "INSERT INTO " + tableKey + " ("+ insertIntoClause + ") SELECT DISTINCT " + insertValuesClause +
				   " FROM " + tableKey + " WHERE " + whereClause ;
		return query;
	}

	//SETTERS
	public void setDefaultDbUserName(String defaultDbUserName){
		this.defaultDbUserName = defaultDbUserName;
	}
	public void setDefaultDbPassword(String defaultDbPassword){
		this.defaultDbPassword = defaultDbPassword;
	}
	public void setDialectAllTables(String dialectAllTables){
		this.dialectAllTables = dialectAllTables;
	}
	public void setResultAllTablesTableName(String resultAllTablesTableName){
		this.resultAllTablesTableName = resultAllTablesTableName;
	}
	public void setDialectAllColumns(String dialectAllColumns){
		this.dialectAllColumns = dialectAllColumns;
	}
	public void setResultAllColumnsColumnName(String resultAllColumnsColumnName){
		this.resultAllColumnsColumnName = resultAllColumnsColumnName;
	}
	public void setResultAllColumnsColumnType(String resultAllColumnsColumnType){
		this.resultAllColumnsColumnType = resultAllColumnsColumnType;
	}
	public void setdialectSelectRowCountFrom(String dialectSelectRowCountFrom){
		this.dialectSelectRowCountFrom = dialectSelectRowCountFrom;
	}
	public void setResultSelectRowCountFromRowCount(String resultSelectRowCountFromRowCount){
		this.resultSelectRowCountFromRowCount = resultSelectRowCountFromRowCount;
	}
	public void setDialectAllIndexesInDB(String dialectAllIndexesInDB){
		this.dialectAllIndexesInDB = dialectAllIndexesInDB;
	}
	public void setDialectIndexInfo(String dialectIndexInfo){
		this.dialectIndexInfo = dialectIndexInfo;
	}
	public void setResultAllIndexesInDBIndexName(String resultAllIndexesInDBIndexName){
		this.resultAllIndexesInDBIndexName = resultAllIndexesInDBIndexName;
	}
	public void setResultAllIndexesInDBColumnName(String resultAllIndexesInDBColumnName){
		this.resultAllIndexesInDBColumnName = resultAllIndexesInDBColumnName;
	}
	public void setResultAllIndexesInDBTableName(String resultAllIndexesInDBTableName){
		this.resultAllIndexesInDBTableName = resultAllIndexesInDBTableName;
	}
	public void setDialectCreateIndex(String dialectCreateIndex){
		this.dialectCreateIndex = dialectCreateIndex;
	}
	public void setdialectDropIndex(String dialectDropIndex){
		this.dialectDropIndex = dialectDropIndex;
	}
	public void setDialectOuterJoinLeft(String dialectOuterJoinLeft){
		this.dialectOuterJoinLeft = dialectOuterJoinLeft;
	}
	public void setDialectOuterJoinRight(String dialectOuterJoinRight){
		this.dialectOuterJoinRight = dialectOuterJoinRight;
	}
	
}