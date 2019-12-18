package prerna.sablecc2.reactor.frame.py;

import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import prerna.algorithm.api.SemossDataType;
import prerna.ds.py.PandasFrame;
import prerna.sablecc2.om.GenRowStruct;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.util.usertracking.AnalyticsTrackerHelper;
import prerna.util.usertracking.UserTrackerFactory;

public class RegexReplaceColumnValueReactor extends AbstractPyFrameReactor {

	/**
	 * This reactor updates row values based on a regex It replaces all portions
	 * of the current cell value that is an exact match to the input value The
	 * inputs to the reactor are: 1) the column to update 2) the regex to look
	 * for 3) value to replace the regex with
	 */

	private static final Pattern NUMERIC_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");

	public RegexReplaceColumnValueReactor() {
		this.keysToGet = new String[] { ReactorKeysEnum.COLUMN.getKey(), ReactorKeysEnum.VALUE.getKey(), ReactorKeysEnum.NEW_VALUE.getKey() };
	}

	@Override
	public NounMetadata execute() {
		organizeKeys();
		// get frame
		PandasFrame frame = (PandasFrame) getFrame();

		// get wrapper name
		String wrapperFrameName = frame.getWrapperName();

		// get inputs
		// first input is the column that we are updating
		List<String> columnNames = getColumns();

		// get regular expression
		String regex = this.keyValue.get(this.keysToGet[1]);
		if (regex == null) {
			throw new IllegalArgumentException("Need to define " + this.keysToGet[1]);
		}

		// get new value
		String newValue = this.keyValue.get(this.keysToGet[2]);
		if (newValue == null) {
			throw new IllegalArgumentException("Need to define " + this.keysToGet[2]);
		}

		int numColumns = columnNames.size();
		String[] scripts = new String[columnNames.size()];
		
		// iterate through all passed columns
		for(int i = 0; i < numColumns; i++) {
			String column = columnNames.get(i);
		
			SemossDataType columnDataType = SemossDataType.convertStringToDataType(getColumnType(frame, column));

			if (columnDataType == SemossDataType.INT || columnDataType == SemossDataType.DOUBLE) {
				if(newValue.isEmpty() || newValue.equalsIgnoreCase("null") || newValue.equalsIgnoreCase("na") || newValue.equalsIgnoreCase("nan")) {
					newValue = "NaN";
				} else if(!NUMERIC_PATTERN.matcher(newValue).matches()) {
					throw new IllegalArgumentException("Cannot update a numeric field with string value = " + newValue);
				}
				
				// TODO: See why this is not executing properly in python!
				scripts[i] = wrapperFrameName + ".regex_replace_val('" + column + "', " + regex + ", " + newValue + ")";
			} else if (columnDataType == SemossDataType.DATE) {
				// what is this doing?
				
//				// TODO: should grab value of whole column with matched regex, not values past in
//				SemossDate oldD = SemossDate.genDateObj(regex);
//				SemossDate newD = SemossDate.genDateObj(newValue);
//				String error = "";
//				if(oldD == null) {
//					error = "Unable to parse old date value = " + regex;
//				}
//				if(newD == null) {
//					error += "Unable to parse new date value = " + newValue;
//				}
//				if(!error.isEmpty()) {
//					throw new IllegalArgumentException(error);
//				}
//				script.append(wrapperFrameName).append(".regex_replace_val('").append(column).append("',")
//				.append("pd.to_datetime('").append(oldD).append("')").append(" , ").append("pd.to_datetime('")
//				.append(newD).append("')").append(")");
//				frame.runScript(script.toString());
			} else if (columnDataType == SemossDataType.STRING) {
				scripts[i] = wrapperFrameName + ".regex_replace_val('" + column +"', '" + regex + "' , '" + newValue + "')";
			}
		}

		// execute all of the routines after we have done our validation
		frame.runScript(scripts);
		
		// NEW TRACKING
		UserTrackerFactory.getInstance().trackAnalyticsWidget(this.insight, frame, "RegexReplaceColumnValue",
				AnalyticsTrackerHelper.getHashInputs(this.store, this.keysToGet));

		return new NounMetadata(frame, PixelDataType.FRAME, PixelOperationType.FRAME_DATA_CHANGE);
	}

	private List<String> getColumns() {
		List<String> cols = new Vector<String>();

		GenRowStruct grs = this.store.getNoun(this.keysToGet[0]);
		if (grs != null && !grs.isEmpty()) {
			for (int i = 0; i < grs.size(); i++) {
				String column = grs.get(i).toString();
				if (column.contains("__")) {
					column = column.split("__")[1];
				}
				cols.add(column);
			}
			return cols;
		}

		return cols;
	}

}
