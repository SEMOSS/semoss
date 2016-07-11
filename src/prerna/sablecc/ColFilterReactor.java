package prerna.sablecc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import prerna.algorithm.api.ITableDataFrame;
import prerna.util.Utility;

public class ColFilterReactor extends AbstractReactor{

//	Hashtable <String, String[]> values2SyncHash = new Hashtable <String, String[]>();
	
	public ColFilterReactor() {
		String [] thisReacts = {PKQLEnum.FILTER}; // these are the input columns - there is also expr Term which I will come to shortly
		super.whatIReactTo = thisReacts;
		super.whoAmI = PKQLEnum.FILTER_DATA;
	}
	
	@Override
	public Iterator process() {
		// I need to take the col_def
		// and put it into who am I
		modExpression();
		String nodeStr = (String)myStore.get(whoAmI);
		System.out.println("My Store on COL CSV " + myStore);
		
		ITableDataFrame frame = (ITableDataFrame) myStore.get("G");
		
		Vector<Hashtable> filters = (Vector<Hashtable>) myStore.get(PKQLEnum.FILTER);
		this.processFilters(frame, filters);
		
		// update the data id so FE knows data has been changed
		frame.updateDataId();
		
		return null;
	}
	
	private Map<String, Map<String, List<Object>>> getFilters(Vector<Hashtable> filters) {
		Map<String, Map<String, List<Object>>> fdata = new HashMap<>();
		
		for(int filterIndex = 0;filterIndex < filters.size();filterIndex++) {
			Hashtable thisFilter = (Hashtable)filters.get(filterIndex);
			String fromCol = (String)thisFilter.get("FROM_COL");
			Vector filterData = new Vector();
			
			if(!thisFilter.containsKey("TO_COL")){
				filterData = (Vector)thisFilter.get("TO_DATA");
				List<Object> cleanedFilterData = new ArrayList<>(filterData.size());
				for(Object data : filterData) {
					String inputData = data.toString().trim();
					Object cleanData = null;
					String type = Utility.findTypes(inputData)[0] + "";
					if(type.equalsIgnoreCase("Date")) {
						cleanData = Utility.getDate(inputData);
					} else if(type.equalsIgnoreCase("Double")) {
						cleanData = Utility.getDouble(inputData);
					} else {
						cleanData = Utility.cleanString(inputData, true, true, false);
					}
					cleanedFilterData.add(cleanData);
				}
				
				String comparator = (String)thisFilter.get("COMPARATOR");
				if(fdata.containsKey(fromCol)) {
					Map<String, List<Object>> innerMap = fdata.get(fromCol);
					if(innerMap.containsKey(comparator)) {
						List<Object> existingFilterData = innerMap.get(comparator);
						existingFilterData.addAll(cleanedFilterData);
						innerMap.put(comparator, existingFilterData);
					} else {
						innerMap.put(comparator, cleanedFilterData);
					}
				} else {
					Map<String, List<Object>> innerMap = new HashMap<>();
					innerMap.put(comparator, cleanedFilterData);
					fdata.put(fromCol, innerMap);
				}
				
			}
		}
		return fdata;
	}
	
	private void processFilters(ITableDataFrame frame, Vector<Hashtable> filters) {
		Map<String, Map<String, List<Object>>> processedFilters = getFilters(filters);
		for(String columnHeader : processedFilters.keySet())
		{
			try {
				frame.filter(columnHeader, processedFilters.get(columnHeader));
				myStore.put("STATUS", PKQLRunner.STATUS.SUCCESS);
				myStore.put("FILTER_RESPONSE", "Filtered Column: " + columnHeader);
			} catch(IllegalArgumentException e) {
				myStore.put("STATUS", PKQLRunner.STATUS.ERROR);
				myStore.put("FILTER_RESPONSE", e.getMessage());
			}
		}
	}
//	private void processFilters(ITableDataFrame frame, Vector<Hashtable> filters) {
//	
//		for(int filterIndex = 0;filterIndex < filters.size();filterIndex++)
//		{
//			Object thisObject = filters.get(filterIndex);
//			
//			Hashtable thisFilter = (Hashtable)filters.get(filterIndex);
//			String fromCol = (String)thisFilter.get("FROM_COL");
//			String toCol = null;
//			Vector filterData = new Vector();
//			if(thisFilter.containsKey("TO_COL")) {
//				toCol = (String)thisFilter.get("TO_COL");
//			}
//			else {
//				filterData = (Vector)thisFilter.get("TO_DATA");
//				List<Object> cleanedFilterData = new ArrayList<>(filterData.size());
//				for(Object data : filterData) {
//					String inputData = data.toString().trim();
//					Object cleanData = null;
//					// grammar change should no longer allow for quotes to be added due to outAWord change
////					if((inputData.startsWith("\"") && inputData.endsWith("\"")) || (inputData.startsWith("'") && inputData.endsWith("'"))) {
////						// this is logic that input is a string
////						inputData = inputData.substring(1, inputData.length() - 1);
////						cleanData = Utility.cleanString(inputData, true, true, false);
////					} else {
//						String type = Utility.findTypes(inputData)[0] + "";
//						if(type.equalsIgnoreCase("Date")) {
//							cleanData = Utility.getDate(inputData);
//						} else if(type.equalsIgnoreCase("Double")) {
//							cleanData = Utility.getDouble(inputData);
//						} else {
//							cleanData = Utility.cleanString(inputData, true, true, false);
//						}
////					}
//					cleanedFilterData.add(cleanData);
//				}
//				String comparator = (String)thisFilter.get("COMPARATOR");
//				try {
//					frame.filter(fromCol, cleanedFilterData, comparator);
//					myStore.put("STATUS", PKQLRunner.STATUS.SUCCESS);
//					myStore.put("FILTER_RESPONSE", "Filtered Column: " + fromCol);
//				} catch(IllegalArgumentException e) {
//					myStore.put("STATUS", PKQLRunner.STATUS.ERROR);
//					myStore.put("FILTER_RESPONSE", e.getMessage());
//				}
//			}
//		}
//	}

}
