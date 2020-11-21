package prerna.ds.py;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import prerna.algorithm.api.SemossDataType;
import prerna.poi.main.HeadersException;
import prerna.poi.main.helper.CSVFileHelper;
import prerna.poi.main.helper.excel.ExcelRange;
import prerna.sablecc2.om.Join;

public class PandasSyntaxHelper {

	private PandasSyntaxHelper() {
		
	}

	/**
	 * Get the syntax to construct get the wrapper for a pandas frame
	 * @param wrapper
	 * @param tableName
	 * @return
	 */
	public static String makeWrapper(String wrapper, String tableName) {
		return wrapper + " = PyFrame.makefm(" + tableName +")";
	}
	
	/**
	 * Execute a .py file
	 * @param fileLocation
	 * @return
	 */
	public static String execFile(String fileLocation) {
		return "execfile('" + fileLocation.replaceAll("\\\\+", "/") + "')";
	}
	
	/**
	 * Get the syntax to load a csv file
	 * Defaults the encoding to utf-8
	 * @param pandasImportVar
	 * @param fileLocation
	 * @param tableName
	 * @return
	 */
	public static String getCsvFileRead(String pandasImportVar, String fileLocation, String tableName) {
		return getCsvFileRead(pandasImportVar, fileLocation, tableName, null);
	}
	
	/**
	 * Get the syntax to load a csv file
	 * @param pandas import var
	 * @param fileLocation
	 * @param tableName
	 * @param sep
	 */
	public static String getCsvFileRead(String pandasImportVar, String fileLocation, String tableName, String sep) {
		if(sep == null || sep.isEmpty()) {
			sep = ",";
		}
		return getCsvFileRead(pandasImportVar, fileLocation, tableName, sep, null);
	}
	
	/**
	 * Get the syntax to load a csv file
	 * @param pandas import var
	 * @param fileLocation
	 * @param tableName
	 * @param sep
	 * @param encoding
	 */
	public static String getCsvFileRead(String pandasImportVar, String fileLocation, String tableName, String sep, String encoding) {
		if(encoding == null || encoding.isEmpty()) {
			encoding = "utf-8";
		}
		// this is to account for leading zeros in columns that get converted to numbers when we want to maintain strings
		String converters = "converters={i : lambda x: str(x) "
				+ "if (isinstance(x,int) and len(str(int(x))) != len(str(x))) "
				+ "else x for i in range(" + numberOfColumns(fileLocation) + ")}";
		String readCsv = tableName + " = " + pandasImportVar + ".read_csv('" + fileLocation.replaceAll("\\\\+", "/") + "', sep='" + sep + "', encoding='" + encoding + "', " + converters + ")";//.replace(np.nan, '', regex=True)";
		return readCsv;
	}
	/**
	 * Returns the number of columns in a csv file. 
	 * @param filePath
	 * @return
	 */
	public static Number numberOfColumns(String filePath) {
		CSVFileHelper csv = new CSVFileHelper();
		csv.parse(filePath);
		return csv.getHeaders().length;
	}
	
	/**
	 * 
	 * @param tableName
	 * @param fileLocation
	 * @return
	 */
	public static String getWriteCsvFile(String tableName, String fileLocation) {
		return getWriteCsvFile(tableName, fileLocation, null);
	}
	
	/**
	 * 
	 * @param tableName
	 * @param fileLocation
	 * @param sep
	 * @return
	 */
	public static String getWriteCsvFile(String tableName, String fileLocation, String sep) {
		if(sep == null || sep.isEmpty()) {
			sep = ",";
		}
		return getWriteCsvFile(tableName, fileLocation, sep, null);
	}
	
	/**
	 * 
	 * @param tableName
	 * @param fileLocation
	 * @param sep
	 * @param encoding
	 * @return
	 */
	public static String getWriteCsvFile(String tableName, String fileLocation, String sep, String encoding) {
		if(encoding == null || encoding.isEmpty()) {
			encoding = "utf-8";
		}
		String readCsv = tableName + ".to_csv('" + fileLocation.replaceAll("\\\\+", "/") + "', sep='" + sep + "', encoding='" + encoding + "', index=False)";
		return readCsv;
	}
	
	public static String loadExcelSheet(String pandasImportVar, String fileLocation, String tableName, String sheetName, String sheetRange) {
		int[] rangeIndicies = ExcelRange.getSheetRangeIndex(sheetRange);
		int startCol = rangeIndicies[0];
		int startRow = rangeIndicies[1];
		int endCol = rangeIndicies[2];
		int endRow = rangeIndicies[3];
		fileLocation = fileLocation.replace("\\", "/");
		StringBuilder sb = new StringBuilder();
		sb.append(tableName + " = " + pandasImportVar + ".read_excel('" + fileLocation + "',");
		// add column range
		sb.append("sheet_name='" + sheetName + "', usecols=range(" + (startCol - 1) + ", " + endCol + "), ");
		// add row range
		int rowNum = endRow - startRow;
		sb.append("skiprows = " + (startRow - 1) + ", nrows=" + rowNum + ")");
		return sb.toString();
	}
	
	public static String getWritePandasToPickle(String pickleVarName, String tableName, String fileLocation) {
		return pickleVarName + ".dump(" + tableName + ", open(\"" + fileLocation.replaceAll("\\\\+", "/") + "\", \"wb\"))";
	}
	
	public static String getReadPickleToPandas(String pandasImportVar, String fileLocation, String tableName) {
		return tableName + " = " + pandasImportVar + ".read_pickle(\"" + fileLocation.replaceAll("\\\\+", "/") + "\")";
//		return tableName + " = " + pickleVarName + ".load(open(\"" + fileLocation.replaceAll("\\\\+", "/") + "\", \"wb\"))";
	}
	
	/**
	 * 
	 * @param leftTableName
	 * @param rightTableName
	 * @param joinType
	 * @param joinCols
	 * @return
	 */
	public static String getMergeSyntax(String pandasFrameVar, String returnTable, String leftTableName, String rightTableName, String joinType, List<Map<String, String>> joinCols) {
		/*
		 * joinCols = [ {leftTable.Title -> rightTable.Movie} , {leftTable.Genre -> rightTable.Genre}  ]
		 */

		StringBuilder builder = new StringBuilder();
		builder.append(returnTable).append(" = ").append(pandasFrameVar).append(".merge(").append(leftTableName).append(", ")
			.append(rightTableName).append(", left_on=[");
		getMergeColsSyntax(builder, joinCols, true);
		builder.append("], right_on=[");
		getMergeColsSyntax(builder, joinCols, false);
		
		if (joinType.equals("inner.join")) {
			builder.append("], how=\"inner\")");
		} else if (joinType.equals("left.outer.join")) {
			builder.append("], how=\"left\")");
		} else if (joinType.equals("right.outer.join")) {
			builder.append("], how=\"right\")");
		} else if (joinType.equals("outer.join")) {
			builder.append("], how=\"outer\")");
		}

		return builder.toString();
	}

	/**
	 * Get the correct py syntax for the table join columns
	 * This uses the join information keys since it is {leftCol -> rightCol}
	 * @param builder
	 * @param colNames
	 */
	public static void getMergeColsSyntax(StringBuilder builder, List<Map<String, String>> colNames, boolean grabKeys){
		// iterate through the map
		boolean firstLoop = true;
		int numJoins = colNames.size();
		for(int i = 0; i < numJoins; i++) {
			Map<String, String> joinMap = colNames.get(i);
			// just in case an empty map is passed
			// we do not want to modify the firstLoop boolean
			if(joinMap.isEmpty()) {
				continue;
			}
			if(!firstLoop) {
				builder.append(",");
			}
			// this should really be 1
			// since each join between 2 columns is its own map
			Collection<String> tableCols = null;
			if(grabKeys) {
				tableCols = joinMap.keySet();
			} else {
				tableCols = joinMap.values();
			}
			// keep track of where to add a ","
			int counter = 0;
			int numCols = tableCols.size();
			for(String colName : tableCols){
				builder.append("\"").append(colName).append("\"");
				if(counter+1 != numCols) {
					builder.append(",");
				}
				counter++;
			}
			firstLoop = false;
		}
	}
	
	public static String alterColumnName(String tableName, String oldHeader, String newHeader) {
		return tableName + ".rename(columns={'" + oldHeader + "':'" + newHeader + "'}, inplace=True)";
	}
	
	/**
	 * Generate an alter statement to add new columns, taking into consideration joins
	 * and new column alias's
	 * @param tableName
	 * @param existingColumns
	 * @param newColumns
	 * @param joins
	 * @param newColumnAlias
	 * @return
	 */
	public static String alterMissingColumns(String tableName, String[] curHeaders, Map<String, SemossDataType> newColumnsToTypeMap, List<Join> joins, Map<String, String> newColumnAlias) {
		List<String> newColumnsToAdd = new Vector<String>();
		List<SemossDataType> newColumnsToAddTypes = new Vector<SemossDataType>();
		
		// get all the join columns
		List<String> joinColumns = new Vector<String>();
		for(Join j : joins) {
			String columnName = j.getRColumn();
			if(columnName.contains("__")) {
				columnName = columnName.split("__")[1];
			}
			joinColumns.add(columnName);
		}
		
		for(String newColumn : newColumnsToTypeMap.keySet()) {
			SemossDataType newColumnType = newColumnsToTypeMap.get(newColumn);
			// modify the header
			if(newColumn.contains("__")) {
				newColumn = newColumn.split("__")[1];
			}
			// if its a join column, ignore it
			if(joinColumns.contains(newColumn)) {
				continue;
			}
			// not a join column
			// check if it has an alias
			// and then add
			if(newColumnAlias.containsKey(newColumn)) {
				newColumnsToAdd.add(newColumnAlias.get(newColumn));
			} else {
				newColumnsToAdd.add(newColumn);
			}
			// and store the type at the same index 
			// in its list
			newColumnsToAddTypes.add(newColumnType);
		}
		
		//TODO: account for column types
		
		StringBuilder command = new StringBuilder(tableName).append(".reindex(columns=[");
		// add current headers
		for(int i = 0; i < curHeaders.length; i++) {
			command.append("\"").append(curHeaders[i]).append("\",");
		}
		
		// add the new headers
		int numNew = newColumnsToAdd.size();
		for(int i = 0; i < numNew; i++) {
			String newCol = newColumnsToAdd.get(i);
			SemossDataType newColType = newColumnsToAddTypes.get(i);
			
			command.append("\"").append(newCol).append("\"");
			if( (i+1) < numNew) {
				command.append(",");
			}
			
//			if(newColType == SemossDataType.DOUBLE) {
//				command.append(newColSyntax).append(" <- as.numeric(").append(newColSyntax).append(");");
//			} else if(newColType == SemossDataType.INT) {
//				command.append(newColSyntax).append(" <- as.integer(").append(newColSyntax).append(");");
//			} else {
//				command.append(newColSyntax).append(" <- as.character(").append(newColSyntax).append(");");
//			}
		}
		
		command.append("])");
		return command.toString();
	}
	
	// gets the number of rows in a given data frame
	public static String getDFLength(String tableName)
	{
		String dfLength = "len(" + tableName + ".index)";
		return dfLength;
	}
	
	// gets all the columns as an array
	public static String getColumns(String tableName)
	{
		String cols = "list(" + tableName + ")";
		return cols;
	}
	
	// get all types
	// this needs to be used in combination with get columns
	public static String getTypes(String tableName)
	{
		String types = tableName + ".dtypes.tolist()";
		return types;
	}
	
	public static String getColumnType(String tableName, String column) {
		String types = tableName + "['" + column + "'].dtype.name";
		return types;
	}
	
	// gets the record
	public static String getColumnChange(String tableName, String colName, String type)
	{
		String typeChanger = tableName + "['" + colName + "'] = " + tableName + "['" + colName + "'].astype('" + type + "')";
		return typeChanger;
	}
	
	/**
	 * Remove duplicate columns in a frame based on column names
	 * 
	 * @param tableName the frame with the duplicate column names
	 * @param newTable the new frame to set
	 * @return Python syntax to remove duplicate columns
	 */
	public static String removeDuplicateColumns(String tableName, String newTable) {
		String script = newTable + " = " + tableName + ".loc[:,~" + tableName + ".columns.duplicated()]";
		return script;
	}
	
	/**
	 * Create a pandas vector from a java vector
	 * @param row				The object[] to convert
	 * @param dataType			The data type for each entry in the object[]
	 * @return					String containing the equivalent r column vector
	 */
	public static String createPandasColVec(List<Object> row, SemossDataType dataType) {
		StringBuilder str = new StringBuilder("([");
		int i = 0;
		int size = row.size();
		for(; i < size; i++) {
			if(SemossDataType.STRING == dataType) {
				String escaper = row.get(i) + "";
				escaper = escaper.replace("'", "\\'");
				str.append("'").append(escaper).append("'");
			} else if(SemossDataType.INT == dataType || SemossDataType.DOUBLE == dataType) {
				str.append(row.get(i).toString());
			} else if(SemossDataType.DATE == dataType) {
				str.append("np.datetime64(\"" + row.get(i).toString() + "\", format='%Y-%m-%d')");
			} else if(SemossDataType.TIMESTAMP == dataType) {
				str.append("np.datetime64(\"" + row.get(i).toString() + "\", format='%Y-%m-%d %H:%M:%S')");
			} else {
				// just in case this is not defined yet...
				// see the type of the value and add it in based on that
				if(dataType == null) {
					if(row.get(i) instanceof String) {
						String escaper = row.get(i) + "";
						escaper = escaper.replace("'", "\\'");
						str.append("'").append(escaper).append("'");
					} else {
						str.append(row.get(i));
					}
				} else {
					str.append(row.get(i));
				}
			}
			// if not the last entry, append a "," to separate entries
			if( (i+1) != size) {
				str.append(",");
			}
		}
		str.append("])");
		return str.toString();
	}

	public static String formatFilterValue(Object value, SemossDataType dataType) {
		if(SemossDataType.STRING == dataType) {
			return "'" + value + "'";
		} else if(SemossDataType.INT == dataType || SemossDataType.DOUBLE == dataType) {
			return value.toString();
		} else if(SemossDataType.DATE == dataType) {
			return "np.datetime64(\"" + value.toString() + "\", format='%Y-%m-%d')";
		} else if(SemossDataType.TIMESTAMP == dataType) {
			return "np.datetime64(\"" + value.toString() + "\", format='%Y-%m-%d %H:%M:%S')";
		} else {
			// just in case this is not defined yet...
			// see the type of the value and add it in based on that
			if(dataType == null) {
				if(value instanceof String) {
					return "'" + value + "'";
				} else {
					return value + "";
				}
			} else {
				return value + "";
			}
		}
	}

	public static String cleanFrameHeaders(String frameName, String[] colNames) {
		HeadersException headerChecker = HeadersException.getInstance();
		String [] pyColNames = headerChecker.getCleanHeaders(colNames);
		// update frame header names in R
		StringBuilder colRen = new StringBuilder("{");
		for (int i = 0; i < colNames.length; i++) {
			colRen.append("'").append(colNames[i]).append("':'").append(pyColNames[i]).append("'");
			if (i < colNames.length - 1) {
				colRen.append(", ");
			}
		}
		colRen.append("}");
		String script = frameName + ".rename(columns=" + colRen + ", inplace=True)" ;
		return script;
	}

	/**
	 * Set the column names for a frame
	 * @param tableName
	 * @param headers the new column names to use
	 * @return
	 */
	public static String setColumnNames(String tableName, String[] headers) {
		StringBuilder sb = new StringBuilder();
		for (int headerIndex = 0; headerIndex < headers.length; headerIndex++) {
			if (sb.length() == 0) {
				sb.append("[");
			} else {
				sb.append(",");
			}
			sb.append("'").append(headers[headerIndex]).append("'");
		}
		sb.append("]");
		String headerS = tableName + ".columns=" + sb.toString();
		return headerS;
	}
}
