package prerna.algorithm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import prerna.util.ArrayUtilityMethods;

public class CountReactor extends BaseReducerReactor {

	@Override
	public Object reduce() {
		double count = 0;
		Set<String> values = new TreeSet<String>();
		while(inputIterator.hasNext() && !errored)
		{
			ArrayList dec = (ArrayList)getNextValue();
			if (!values.contains(dec.get(0).toString())) {
				count++;
				values.add(dec.get(0).toString());
			}
		}
		System.out.println(count);
		return count;
	}
	
	@Override
	public HashMap<HashMap<String,String>,Object> reduceGroupBy(Vector<String> groupBys, Vector<String> processedColumns, String[] columnsArray, Iterator it) {
		HashMap<HashMap<String,String>, Object> groupByHash = new HashMap<HashMap<String,String>,Object>();

		while(it.hasNext()){
			Object[] row = (Object[]) it.next();
			HashMap<String, String> key = new HashMap<String,String>();
			for(String groupBy : groupBys) {
				int groupByIndex = ArrayUtilityMethods.arrayContainsValueAtIndexIgnoreCase(columnsArray, groupBy);
				String instance = (String)row[groupByIndex];
				key.put(groupBy, instance);
			}
			int processedIndex = ArrayUtilityMethods.arrayContainsValueAtIndexIgnoreCase(columnsArray, processedColumns.get(0));
			Object value = row[processedIndex];
			Set<Object> values = (TreeSet<Object>)groupByHash.get(key);
			if(values == null) {
				values = new TreeSet<Object>();
				groupByHash.put(key, values);
			}
			if (!values.contains(value)) {
				values.add(value);
			}
		}
		for(HashMap<String,String> key: groupByHash.keySet()) {
			groupByHash.put(key, ((TreeSet<Object>)groupByHash.get(key)).size());
		}
		
		return groupByHash;
	}
	
}
