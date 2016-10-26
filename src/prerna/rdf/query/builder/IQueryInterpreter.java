package prerna.rdf.query.builder;

import prerna.ds.QueryStruct;

public interface IQueryInterpreter {
	
	String SEARCH_COMPARATOR = "?like";

	void setQueryStruct(QueryStruct qs);

	String composeQuery();

	void setPerformCount(int performCount);
	
	int isPerformCount();
	
	void clear();
}
