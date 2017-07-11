package prerna.query.interpreters;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import prerna.sablecc2.om.Filter2;
import prerna.sablecc2.om.NounMetadata;
import prerna.sablecc2.om.PkslDataTypes;

public class GenRowFilters {

	/*
	 * This class is used to store filters within the QueryStruct2
	 * Idea is to allow for more complex filtering scenarios
	 */
	
	/**
	 * Right now, tracking for these types of filters within querying
	 */
	public static enum FILTER_TYPE {COL_TO_COL, COL_TO_VALUES, VALUES_TO_COL, VALUE_TO_VALUE};

	// keep the list of filter objects to execute
	private List<Filter2> filterVec = new Vector<Filter2>();
	
	// keep the list of filtered columns instead of iterating through
	private Set<String> filteredColumns = new HashSet<String>();
	
	public GenRowFilters() {
		
	}

	public List<Filter2> getFilters() {
		return this.filterVec;
	}
	
	public void addFilters(Filter2 newFilter) {
		this.filterVec.add(newFilter);
		this.filteredColumns.addAll(newFilter.getAllUsedColumns());
	}

	public boolean hasFilter(String column) {
		return this.filteredColumns.contains(column);
	}

	public boolean isEmpty() {
		return this.filterVec.isEmpty();
	}
	
	/**
	 * Overriding toString for debugging
	 */
	public String toString() {
		StringBuilder toString = new StringBuilder();
		int numFilters = filterVec.size();
		for(int i = 0; i < numFilters; i++) {
			toString.append(filterVec.get(i)).append("\t");
		}
		return toString.toString();
	}

	public void merge(GenRowFilters incomingFilters) {
		//TODO:
		//TODO:
		//TODO:
		//TODO:
		//TODO:
		// actually do a merge... right now just adding all
		this.filterVec.addAll(incomingFilters.filterVec);
		this.filteredColumns.addAll(incomingFilters.filteredColumns);
	}
	
	public static FILTER_TYPE determineFilterType(Filter2 filter) {
		NounMetadata leftComp = filter.getLComparison();
		NounMetadata rightComp = filter.getRComparison();

		// DIFFERENT PROCESSING BASED ON THE TYPE OF VALUE
		PkslDataTypes lCompType = leftComp.getNounName();
		PkslDataTypes rCompType = rightComp.getNounName();

		// col to col
		if(lCompType == PkslDataTypes.COLUMN && rCompType == PkslDataTypes.COLUMN) 
		{
			return FILTER_TYPE.COL_TO_COL;
		} 
		// col to values
		else if(lCompType == PkslDataTypes.COLUMN && (rCompType == PkslDataTypes.CONST_DECIMAL || rCompType == PkslDataTypes.CONST_INT || rCompType == PkslDataTypes.CONST_STRING) ) 
		{
			return FILTER_TYPE.COL_TO_VALUES;
		} 
		// values to col
		else if((lCompType == PkslDataTypes.CONST_DECIMAL || lCompType == PkslDataTypes.CONST_INT || lCompType == PkslDataTypes.CONST_STRING) && rCompType == PkslDataTypes.COLUMN)
		{
			return FILTER_TYPE.VALUES_TO_COL;
		} 
		// values to values
		else if((rCompType == PkslDataTypes.CONST_DECIMAL || rCompType == PkslDataTypes.CONST_INT || rCompType == PkslDataTypes.CONST_STRING) && (lCompType == PkslDataTypes.CONST_DECIMAL || lCompType == PkslDataTypes.CONST_INT || lCompType == PkslDataTypes.CONST_STRING)) 
		{
			return FILTER_TYPE.VALUE_TO_VALUE;
		}

		return null;
	}
	
}
