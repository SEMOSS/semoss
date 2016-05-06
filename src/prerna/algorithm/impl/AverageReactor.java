package prerna.algorithm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import prerna.util.ArrayUtilityMethods;

public class AverageReactor extends BaseReducerReactor {
	
	@Override
	public Object reduce() {
		double output = 0.0;
		int count = 0;
		while(inputIterator.hasNext() && !errored)
		{
			ArrayList dec = (ArrayList)getNextValue();
				if(dec.get(0) instanceof Number) {
					output += ((Number)dec.get(0)).doubleValue();
					count++;
				}
		}
			output = output/count;
		System.out.println(output);
		return output;
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
			HashMap<String,Object> paramMap = (HashMap<String,Object>)groupByHash.get(key);
			if(paramMap == null) {
				paramMap = new HashMap<String,Object>();
				groupByHash.put(key, paramMap);
				paramMap.put("SUM", 0.0);
				paramMap.put("COUNT", 0);
			}
			paramMap.put("SUM", (Double)paramMap.get("SUM")+(Double)value);
			paramMap.put("COUNT", (Integer)paramMap.get("COUNT")+1);
		}
		for(HashMap<String,String> key: groupByHash.keySet()) {
			HashMap<String,Object> paramMap = (HashMap<String,Object>)groupByHash.get(key);
			groupByHash.put(key, (Double)paramMap.get("SUM")/(Integer)paramMap.get("COUNT"));
		}
		
		return groupByHash;
	}
	
}
