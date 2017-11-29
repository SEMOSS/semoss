package prerna.sablecc;

import java.util.Iterator;

import prerna.sablecc.meta.IPkqlMetadata;

public class RawQueryApiReactor extends AbstractReactor {

	public static final String ENGINE_KEY = "ENGINE";
	public static final String QUERY_KEY = "QUERY";

	public RawQueryApiReactor() {
		String [] thisReacts = {};//{PKQLEnum.COL_CSV, PKQLEnum.FILTER, PKQLEnum.MAP_OBJ, PKQLEnum.JOINS, "KEY_VALUE", PKQLEnum.TABLE_JOINS};
		super.whatIReactTo = thisReacts;
		super.whoAmI = PKQLEnum.RAW_API;
	}
	
	@Override
	public Iterator process() {
//		String engineName = (String) myStore.get(ENGINE_KEY);
//		IEngine engine = Utility.getEngine(engineName);
//		
//		String query = (String) myStore.get(QUERY_KEY);
//		// run the query on the engine
//		IRawSelectWrapper it = WrapperManager.getInstance().getRawWrapper(engine, query);
//		
//		// determine the edge hash
//		// testing -> create a prim key
//		String[] headers = it.getDisplayVariables();
//		Map<String, Set<String>> edgeHash = TinkerMetaHelper.createPrimKeyEdgeHash(headers);
//		this.put("EDGE_HASH", edgeHash);
//
//		if(it instanceof RawRDBMSSelectWrapper) {
//			RawRDBMSSelectWrapper rdbmsIt = (RawRDBMSSelectWrapper) it;
//			try {
//				ResultSetMetaData meta = rdbmsIt.getMetaData();
//				int numCols = meta.getColumnCount();
//				
//				Map<String, String> dataTypeMap = new HashMap<String, String>();
//				Map<String, String> logicalToValueMap = new HashMap<String, String>();
//				for(int index = 0; index < numCols; index++) {
//					dataTypeMap.put(meta.getColumnLabel(index+1), meta.getColumnTypeName(index+1));
//					logicalToValueMap.put(meta.getColumnLabel(index+1), meta.getColumnLabel(index+1));
//				}
//				
//				this.put("DATA_TYPE_MAP", dataTypeMap);
//				this.put("LOGICAL_TO_VALUE", logicalToValueMap);
//			} catch (SQLException e) {
//				e.printStackTrace();
//				throw new IllegalArgumentException("ERROR WITH EXECUTION OF SQL QUERY");
//			}
//			
//		} else {
//			// HOW DO I DETERMINE THE DATA TYPES PROPERLY??? :(
//			int numCols = headers.length;
//			Map<String, String> dataTypeMap = new HashMap<String, String>();
//			Map<String, String> logicalToValueMap = new HashMap<String, String>();
//			
//			for(int index = 0; index < numCols; index++) {
//				dataTypeMap.put(headers[index], "STRING");
//				logicalToValueMap.put(headers[index], headers[index]);
//			}
//			this.put("DATA_TYPE_MAP", dataTypeMap);
//			this.put("LOGICAL_TO_VALUE", logicalToValueMap);
//		}
//		
//		this.put((String) getValue(PKQLEnum.RAW_API), it);
//		this.put("RESPONSE", "success");
//		this.put("STATUS", PKQLRunner.STATUS.SUCCESS);
		
		return null;
	}

	@Override
	public IPkqlMetadata getPkqlMetadata() {
		// TODO Auto-generated method stub
		return null;
	}
}
