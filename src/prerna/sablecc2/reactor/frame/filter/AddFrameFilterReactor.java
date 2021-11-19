package prerna.sablecc2.reactor.frame.filter;

import prerna.algorithm.api.ITableDataFrame;
import prerna.query.querystruct.filters.BooleanValMetadata;
import prerna.query.querystruct.filters.GenRowFilters;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.util.insight.InsightUtility;

public class AddFrameFilterReactor extends AbstractFilterReactor {

	public AddFrameFilterReactor() {
		this.keysToGet = new String[] { ReactorKeysEnum.FRAME.getKey(), ReactorKeysEnum.FILTERS.getKey() };
	}

	@Override
	public NounMetadata execute() {
		ITableDataFrame frame = getFrame();

		// get the filters to add
		GenRowFilters newFiltersToAdd = getFilters();
		if (newFiltersToAdd.isEmpty()) {
			throw new IllegalArgumentException("No filter found to add to frame");
		}

		// get existing filters
		GenRowFilters existingFilters = frame.getFrameFilters();
		
		// add the new filters by merging into the existing state
		mergeFilters(newFiltersToAdd, existingFilters);

		// clear panel temp filter model state
		InsightUtility.clearPanelTempFilterModel(this.insight, frame);
		
		BooleanValMetadata fFilterVal = BooleanValMetadata.getFrameVal();
		fFilterVal.setName(frame.getName());
		fFilterVal.setFilterVal(true);
		NounMetadata noun = new NounMetadata(fFilterVal, PixelDataType.BOOLEAN_METADATA, PixelOperationType.FRAME_FILTER_CHANGE);
		return noun;
	}
}
