package prerna.algorithm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import prerna.sablecc.PKQLEnum;
import prerna.util.ArrayUtilityMethods;

public class CountIfReactor extends BaseReducerReactor {

	@Override
	public Object reduce() {
		double count = 0;
		Map<String, Object> options = (Map<String, Object>) myStore.get(PKQLEnum.MATH_PARAM);
		String condition = options.get("CONDITION1") + "";
		double value =0;
		if(condition.contains("<=")){	
			value = Double.parseDouble( condition.trim().substring(condition.indexOf("<=")+2));					
			while(inputIterator.hasNext()) {
				ArrayList dec = (ArrayList)getNextValue();
				if(Double.parseDouble( dec.get(0).toString().trim())<= value) {
					count++;
				}
			}
		}
		else if(condition.contains(">=")){		
			value = Double.parseDouble( condition.trim().substring(condition.indexOf(">=")+2));
			while(inputIterator.hasNext()) {
				ArrayList dec = (ArrayList)getNextValue();
				if(Double.parseDouble( dec.get(0).toString().trim())>= value) {
					count++;
				}
			}
		}
		else if(condition.contains("<")){		
			value = Double.parseDouble( condition.trim().substring(condition.indexOf("<")+1));
			while(inputIterator.hasNext()) {
				ArrayList dec = (ArrayList)getNextValue();
				if(Double.parseDouble( dec.get(0).toString().trim())< value) {
					count++;
				}
			}
		}
		else if(condition.contains(">")){		
			value = Double.parseDouble( condition.trim().substring(condition.indexOf(">")+1));
			while(inputIterator.hasNext()) {
				ArrayList dec = (ArrayList)getNextValue();
				if(Double.parseDouble( dec.get(0).toString().trim())> value) {
					count++;
				}
			}
		}
		else if(condition.contains("<>")){		
			value = Double.parseDouble( condition.trim().substring(condition.indexOf("<>"))+2);
			while(inputIterator.hasNext()) {
				ArrayList dec = (ArrayList)getNextValue();
				if(Double.parseDouble( dec.get(0).toString().trim()) != value) {
					count++;
				}
			}
		}
		else {	
			try{
			value = Double.parseDouble( condition.trim());
			while(inputIterator.hasNext()) {
				ArrayList dec = (ArrayList)getNextValue();
				if(Double.parseDouble( dec.get(0).toString().trim()) == value) {
					count++;
				}
			}}
			catch(Exception e)
			{	
				String val= condition.trim();
				while(inputIterator.hasNext()) {
					ArrayList dec = (ArrayList)getNextValue();
					if(dec.get(0).toString().trim().equals( val)) {
						count++;
					}
				}
				
			}
		}
		
		System.out.println(count);
		return count;
	}
	
	@Override
	public HashMap<HashMap<Object,Object>,Object> reduceGroupBy(Vector<String> groupBys, Vector<String> processedColumns, String[] columnsArray, Iterator it) {
		HashMap<HashMap<Object,Object>, Object> groupByHash = new HashMap<HashMap<Object,Object>,Object>();
		
		//TODO
		return groupByHash;
	}
	
}
