package prerna.sablecc2.reactor.frame.r;

import prerna.algorithm.api.IMetaData;
import prerna.algorithm.api.IMetaData.DATA_TYPES;
import prerna.ds.r.RDataTable;
import prerna.sablecc2.om.GenRowStruct;
import prerna.sablecc2.om.NounMetadata;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.util.Utility;

public class ChangeColumnTypeReactor extends AbstractRFrameReactor {
	@Override
	public NounMetadata execute() {
		init();
		// get frame
		RDataTable frame = (RDataTable) getFrame();

		// get inputs
		String column = getColumn();
		String newType = getColumnType();
		String table = frame.getTableName();

		if (column.contains("__")) {
			String[] split = column.split("__");
			column = split[1];
			table = split[0];
		}
		// define the r script to execute
		// script depends on the new data type
		String script = null;
		if (newType.equalsIgnoreCase("string") || newType.equalsIgnoreCase("character")) {
			script = table + " <- " + table + "[, " + column + " := as.character(" + column + ")]";
			frame.executeRScript(script);
		} else if (newType.equalsIgnoreCase("factor")) {
			script = table + " <- " + table + "[, " + column + " := as.factor(" + column + ")]";
			frame.executeRScript(script);
		} else if (newType.equalsIgnoreCase("number") || newType.equalsIgnoreCase("numeric")) {
			script = table + " <- " + table + "[, " + column + " := as.numeric(" + column + ")]";
			frame.executeRScript(script);
		} else if (newType.equalsIgnoreCase("date")) {
			// we have a different script to run if it is a str to date
			// conversion
			// define date format
			String dateFormat = "%Y/%m/%d";
			// get the column type of the existing column
			String type = getColumnType(table, column);
			String tempTable = Utility.getRandomString(6);
			if (type.equalsIgnoreCase("date")) {
				String formatString = ", format = '" + dateFormat + "'";
				script = tempTable + " <- format(" + table + "$" + column + formatString + ")";
				frame.executeRScript(script);
				script = table + "$" + column + " <- " + "as.Date(" + tempTable + formatString + ")";
				frame.executeRScript(script);
			} else {
				script = tempTable + " <- as.Date(" + table + "$" + column + ", format='" + dateFormat + "')";
				frame.executeRScript(script);
				script = table + "$" + column + " <- " + tempTable;
				frame.executeRScript(script);
			}
			// perform variable cleanup
			frame.executeRScript("rm(" + tempTable + ");");
			frame.executeRScript("gc();");
		}
		// update the metadata
		this.getFrame().getMetaData().modifyDataTypeToProperty(table + "__" + column, table, newType);
		return new NounMetadata(frame, PixelDataType.FRAME, PixelOperationType.FRAME_DATA_CHANGE);
	}

	private String getColumn() {
		GenRowStruct inputsGRS = this.getCurRow();
		if (inputsGRS != null && !inputsGRS.isEmpty()) {
			String colName = inputsGRS.getNoun(0).getValue() + "";
			if (colName.length() == 0) {
				throw new IllegalArgumentException("Need to define the new column name");
			}
			return colName;
		}
		throw new IllegalArgumentException("Need to define the new column name");
	}

	private String getColumnType() {
		GenRowStruct inputsGRS = this.getCurRow();
		NounMetadata input2 = inputsGRS.getNoun(1);
		String columnType = input2.getValue() + "";
		// default to string
		if (columnType.length() == 0) {
			columnType = "STRING";
		}
		// check if data type is supported
		DATA_TYPES dt = IMetaData.convertToDataTypeEnum(columnType);
		if (dt == null) {
			dt = DATA_TYPES.STRING;
		}
		return columnType;
	}
}
