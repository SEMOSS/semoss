package prerna.sablecc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class VizReactor extends AbstractReactor {


	Hashtable <String, String[]> values2SyncHash = new Hashtable <String, String[]>();
	
	public VizReactor()
	{
		String [] thisReacts = {PKQLEnum.WORD_OR_NUM, "TERM"};
		super.whatIReactTo = thisReacts;
		super.whoAmI = PKQLEnum.VIZ;
	}
	
	@Override
	public Iterator process() {
		Object termObject = myStore.get("VizTableData");
		
		List<String> columns = new ArrayList<>();
		Map<Map<String, Object>, Object> mainMap = new HashMap<>();
		List<Object[]> grid = new ArrayList<>(1);
		
		if(termObject instanceof List) {
			List<Object> listObject = (List<Object>)termObject;
			int counter = 0;
			for(Object nextObject : listObject) {
				if(nextObject instanceof Map) {
					String newColName = "newCol"+counter++;
					columns.add(newColName);
					mainMap = mergeMap(mainMap, (Map<Map<String, Object>, Object>)nextObject);
//					grid = convertMapToGrid((Map<Map<String, Object>, Object>)nextObject);
				} else if(nextObject instanceof String) {
					columns.add(nextObject.toString());
				} else {
					//formulas which are not group bys and not columns should be taken care of here
				}
			}
		} else {
			
		}
		
		grid = convertMapToGrid(mainMap);
		myStore.put("VizTableKeys", columns);
		myStore.put("VizTableValues", grid);
		return null;
	}
	
	private Map<Map<String, Object>, Object> mergeMap(Map<Map<String, Object>, Object> firstMap, Map<Map<String, Object>, Object> secondMap) {
		
		if(firstMap == null || firstMap.isEmpty()) return secondMap;
		if(secondMap == null || secondMap.isEmpty()) return firstMap;
		
		Map<Map<String, Object>, Object> mergedMap = new HashMap<>();
		
		for(Map<String, Object> key : firstMap.keySet()) {
			mergedMap.put(key, firstMap.get(key));
		}
		
		for(Map<String, Object> key : secondMap.keySet()) {
			if(mergedMap.containsKey(key)) {
				Object obj = mergedMap.get(key);
				if(obj instanceof List) {
					((List)obj).add(secondMap.get(key));
				} else if(obj != null) {
					List<Object> newList = new ArrayList<>();
					newList.add(obj);
					newList.add(secondMap.get(key));
					mergedMap.put(key, newList);
				} else {
					List<Object> newList = new ArrayList<>();
					newList.add("");
					newList.add(secondMap.get(key));
					mergedMap.put(key, newList);
				}
			} else {
				List<Object> valList = new ArrayList<Object>();
				valList.add("");
				valList.add(secondMap.get(key));
				mergedMap.put(key, valList);
			}
		}
		
		return mergedMap;
	}
	
    private List<Object[]> convertMapToGrid(Map<Map<String, Object>, Object> mapData) {
        List<Object[]> grid = new Vector<Object[]>();
        
        String[] headers = null;
        int numHeaders = 0;
        
        // iterate through each unique group
        Set<Map<String, Object>> unqiueGroupSet = mapData.keySet();
        for(Map<String, Object> group : unqiueGroupSet) {
              // get the headers from the keyset
              if(headers == null) {
                    headers = group.keySet().toArray(new String[]{});
                    numHeaders = headers.length;
              }
              
//              Object[] row = new Object[numHeaders + 1];
              List<Object> row = new ArrayList<>();
              // store each value of the group by
              for(int colIdx = 0; colIdx < numHeaders; colIdx++) {
                    row.add(group.get(headers[colIdx]));
              }
              // store the value for the group by result
//              row[numHeaders] = mapData.get(group);
              Object val = mapData.get(group);
              if(val instanceof List) {
            	  row.addAll((List)val);
              } else {
            	  row.add(val);
              }
              
              grid.add(row.toArray());
        }
        
//        String[] dataHeaders = new String[numHeaders+1];
//        for(int i = 0; i < dataHeaders.length; i++) {
//        	if(i==dataHeaders.length-1) {
//        		dataHeaders[i] = newColName;
//        	} else {
//        		dataHeaders[i] = headers[i];
//        	}
//        }
//        Map map = new HashMap();
//        map.put("data", grid);
//        map.put("headers", dataHeaders);
//        dataMap.put("data", grid);
        return grid;
    }
}
