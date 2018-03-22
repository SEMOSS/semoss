package prerna.sablecc2.reactor.qs.filter;

import java.util.List;

import prerna.query.querystruct.QueryStruct2;
import prerna.query.querystruct.filters.IQueryFilter;
import prerna.query.querystruct.filters.SimpleQueryFilter;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.om.nounmeta.NounMetadata;

public class QueryHavingFilterReactor extends QueryFilterReactor {
	
	public QueryHavingFilterReactor() {
		this.keysToGet = new String[]{ReactorKeysEnum.FILTERS.getKey()};
	}

	protected QueryStruct2 createQueryStruct() {
		List<Object> filters = this.curRow.getValuesOfType(PixelDataType.FILTER);
		if(filters.isEmpty()) {
			throw new IllegalArgumentException("No filter founds to append into the query");
		}
		for(int i = 0; i < filters.size(); i++) {
			IQueryFilter nextFilter = (IQueryFilter)filters.get(i);
			if(nextFilter != null) {
				if(nextFilter.getQueryFilterType() == IQueryFilter.QUERY_FILTER_TYPE.SIMPLE) {
					if(isValidFilter((SimpleQueryFilter) nextFilter)) {
						qs.addHavingFilter(processSimpleFilter( (SimpleQueryFilter) nextFilter));
					}
				} else {
					qs.addHavingFilter(nextFilter);
				}
			}
		}
		return qs;
	}
	
	/**
	 * Shift the query struct if it is just an aggregation
	 * @param filter
	 * @return
	 */
	private SimpleQueryFilter processSimpleFilter(SimpleQueryFilter filter) {
		NounMetadata lComp = filter.getLComparison();
		NounMetadata rComp = filter.getRComparison();
		
		NounMetadata newL = null;
		if(lComp.getValue() instanceof QueryStruct2) {
			QueryStruct2 query = (QueryStruct2) lComp.getValue();
			if(query.getCombinedFilters().isEmpty() && query.getRelations().isEmpty() && query.getSelectors().size() == 1) {
				newL = new NounMetadata(query.getSelectors().get(0), PixelDataType.COLUMN);
			} else {
				newL = lComp;
			}
		} else {
			newL = lComp;
		}
		
		NounMetadata newR = null;
		if(rComp.getValue() instanceof QueryStruct2) {
			QueryStruct2 query = (QueryStruct2) rComp.getValue();
			if(query.getCombinedFilters().isEmpty() && query.getRelations().isEmpty() && query.getSelectors().size() == 1) {
				newR = new NounMetadata(query.getSelectors().get(0), PixelDataType.COLUMN);
			} else {
				newR = rComp;
			}
		} else {
			newR = rComp;
		}

		return new SimpleQueryFilter(newL, filter.getComparator(), newR);
	}
}
