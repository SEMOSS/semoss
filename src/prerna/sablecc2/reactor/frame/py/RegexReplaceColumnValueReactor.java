package prerna.sablecc2.reactor.frame.py;

import java.util.List;
import java.util.Vector;

import prerna.ds.py.PandasFrame;
import prerna.sablecc2.om.GenRowStruct;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.sablecc2.reactor.frame.AbstractFrameReactor;
import prerna.util.usertracking.AnalyticsTrackerHelper;
import prerna.util.usertracking.UserTrackerFactory;

public class RegexReplaceColumnValueReactor extends AbstractFrameReactor {

	/**
	 * This reactor updates row values based on a regex It replaces all portions
	 * of the current cell value that is an exact match to the input value The
	 * inputs to the reactor are: 1) the column to update 2) the regex to look
	 * for 3) value to replace the regex with
	 */

	public RegexReplaceColumnValueReactor() {
		this.keysToGet = new String[] { ReactorKeysEnum.COLUMN.getKey(), ReactorKeysEnum.VALUE.getKey(),
				ReactorKeysEnum.NEW_VALUE.getKey() };
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

		// iterate through all passed columns
		for (String column : columnNames) {
			String neededQuote = (boolean) frame.runScript(wrapperFrameName + ".is_numeric('" + column + "')") ? "" : "'";
			frame.runScript(wrapperFrameName + ".regex_replace_val('" + column + "', '" + regex + "' , " + neededQuote + newValue + neededQuote + ")");
		}

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
