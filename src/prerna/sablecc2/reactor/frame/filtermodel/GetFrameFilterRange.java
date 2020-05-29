package prerna.sablecc2.reactor.frame.filtermodel;

import java.util.HashMap;
import java.util.Map;

import prerna.algorithm.api.ITableDataFrame;
import prerna.algorithm.api.SemossDataType;
import prerna.engine.api.IRawSelectWrapper;
import prerna.om.InsightPanel;
import prerna.query.querystruct.SelectQueryStruct;
import prerna.query.querystruct.filters.GenRowFilters;
import prerna.query.querystruct.selectors.QueryColumnSelector;
import prerna.query.querystruct.selectors.QueryFunctionHelper;
import prerna.query.querystruct.selectors.QueryFunctionSelector;
import prerna.sablecc2.om.GenRowStruct;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.sablecc2.reactor.frame.filter.AbstractFilterReactor;

public class GetFrameFilterRange extends AbstractFilterReactor {

	/**
	 * Get the absolute min/max for the column and the relative min/max based on
	 * the filters
	 */

	public GetFrameFilterRange() {
		this.keysToGet = new String[] { ReactorKeysEnum.COLUMN.getKey(), ReactorKeysEnum.PANEL.getKey(), DYNAMIC_KEY };
	}

	@Override
	public NounMetadata execute() {
		ITableDataFrame dataframe = getFrame();

		GenRowStruct colGrs = this.store.getNoun(keysToGet[0]);
		if (colGrs == null || colGrs.isEmpty()) {
			throw new IllegalArgumentException("Need to set the column for the filter model");
		}
		String tableCol = colGrs.get(0).toString();

		InsightPanel panel = null;
		GenRowStruct panelGrs = this.store.getNoun(keysToGet[1]);
		if (panelGrs != null && !panelGrs.isEmpty()) {
			String panelId = panelGrs.get(0) + "";
			panel = this.insight.getInsightPanel(panelId);
		}
		
		boolean dynamic = false;
		GenRowStruct dynamicGrs = this.store.getNoun(keysToGet[2]);
		if (dynamicGrs != null && !dynamicGrs.isEmpty()) {
			dynamic = Boolean.parseBoolean(dynamicGrs.get(0) + "");
		}

		return getFilterModel(dataframe, tableCol, dynamic, panel);
	}

	public NounMetadata getFilterModel(ITableDataFrame dataframe, String tableCol, boolean dynamic, InsightPanel panel) {
		// store results in this map
		Map<String, Object> retMap = new HashMap<String, Object>();
		// first just return the info that was passed in
		retMap.put("column", tableCol);

		// create the selector
		QueryColumnSelector selector = new QueryColumnSelector(tableCol);
		// get the base filters that are being applied that we are concerned
		GenRowFilters baseFilters = dataframe.getFrameFilters().copy();
		GenRowFilters baseFiltersExcludeCol = dataframe.getFrameFilters().copy();
		if (panel != null) {
			baseFilters.merge(panel.getPanelFilters().copy());
			baseFiltersExcludeCol.merge(panel.getPanelFilters().copy());
		}
		baseFiltersExcludeCol.removeColumnFilter(tableCol);

		// for numerical, also add the min/max
		String alias = selector.getAlias();
		String metaName = dataframe.getMetaData().getUniqueNameFromAlias(alias);
		if (metaName == null) {
			metaName = alias;
		}
		// check it is in fact numeric
		SemossDataType columnType = dataframe.getMetaData().getHeaderTypeAsEnum(metaName);
		if (SemossDataType.INT == columnType || SemossDataType.DOUBLE == columnType || SemossDataType.DATE == columnType
				|| SemossDataType.TIMESTAMP == columnType) {
			QueryColumnSelector innerSelector = new QueryColumnSelector(tableCol);

			SelectQueryStruct mathQS = new SelectQueryStruct();
			if(dynamic) {
				mathQS.setImplicitFilters(baseFiltersExcludeCol);
			}
			
			QueryFunctionSelector mathSelector = new QueryFunctionSelector();
			mathSelector.addInnerSelector(innerSelector);
			mathSelector.setFunction(QueryFunctionHelper.MIN);
			mathQS.addSelector(mathSelector);
			// get the absolute min when no filters are present
			Map<String, Object> minMaxMap = new HashMap<String, Object>();
			IRawSelectWrapper it = null;
			try {
				it = dataframe.query(mathQS);
				minMaxMap.put("absMin", it.next().getValues()[0]);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(it != null) {
					it.cleanUp();
				}
			}
			// get the abs max when no filters are present
			mathSelector.setFunction(QueryFunctionHelper.MAX);
			try {
				it = dataframe.query(mathQS);
				minMaxMap.put("absMax", it.next().getValues()[0]);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(it != null) {
					it.cleanUp();
				}
			}

			// add in the filters now and repeat
			mathQS.setImplicitFilters(baseFilters);
			// run for actual max
			try {
				it = dataframe.query(mathQS);
				minMaxMap.put("max", it.next().getValues()[0]);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(it != null) {
					it.cleanUp();
				}
			}
			// run for actual min
			mathSelector.setFunction(QueryFunctionHelper.MIN);
			try {
				it = dataframe.query(mathQS);
				minMaxMap.put("min", it.next().getValues()[0]);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(it != null) {
					it.cleanUp();
				}
			}

			retMap.put("minMax", minMaxMap);
		}

		return new NounMetadata(retMap, PixelDataType.CUSTOM_DATA_STRUCTURE, PixelOperationType.FILTER_MODEL);
	}
}