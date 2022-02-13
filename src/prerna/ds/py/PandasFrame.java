package prerna.ds.py;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.jgit.util.FileUtils;

import prerna.algorithm.api.DataFrameTypeEnum;
import prerna.algorithm.api.SemossDataType;
import prerna.auth.utils.AbstractSecurityUtils;
import prerna.cache.CachePropFileFrameObject;
import prerna.ds.OwlTemporalEngineMeta;
import prerna.ds.shared.AbstractTableDataFrame;
import prerna.ds.shared.CachedIterator;
import prerna.ds.shared.RawCachedWrapper;
import prerna.ds.util.flatfile.CsvFileIterator;
import prerna.engine.api.IHeadersDataRow;
import prerna.engine.api.IRawSelectWrapper;
import prerna.om.Insight;
import prerna.poi.main.HeadersException;
import prerna.poi.main.helper.excel.ExcelSheetFileIterator;
import prerna.query.interpreters.IQueryInterpreter;
import prerna.query.interpreters.PandasInterpreter;
import prerna.query.querystruct.CsvQueryStruct;
import prerna.query.querystruct.ExcelQueryStruct;
import prerna.query.querystruct.SelectQueryStruct;
import prerna.query.querystruct.transform.QSAliasToPhysicalConverter;
import prerna.sablecc2.reactor.imports.ImportUtility;
import prerna.ui.components.playsheets.datamakers.DataMakerComponent;
import prerna.util.Constants;
import prerna.util.DIHelper;
import prerna.util.Utility;

public class PandasFrame extends AbstractTableDataFrame {

	public static final String DATA_MAKER_NAME = "PandasFrame";
	
	public static final String PANDAS_IMPORT_VAR = "pandas_import_var";
	public static final String PANDAS_IMPORT_STRING = "import pandas as " + PANDAS_IMPORT_VAR;
	
	public static final String NUMPY_IMPORT_VAR = "np_import_var";
	public static final String NUMPY_IMPORT_STRING = "import numpy as " + NUMPY_IMPORT_VAR;
	
	static Map<String, SemossDataType> pyS = new Hashtable<String, SemossDataType>();
	static Map<String, Object> pyJ = new Hashtable<String, Object>();
	static Map<Object, String> spy = new Hashtable<Object, String>();
	
	// gets all the commands in one fell swoop 
	List <String> commands = new ArrayList<String>();
	
	private PyExecutorThread py = null;
	private String wrapperFrameName = null;
	private PyTranslator pyt = null;
	public boolean cache = true;
	
	// list of caches
	public List keyCache = new ArrayList();
	
	static {
		pyS.put("object", SemossDataType.STRING);
		pyS.put("category", SemossDataType.STRING);
		pyS.put("int64", SemossDataType.INT);
		pyS.put("float64", SemossDataType.DOUBLE);
		pyS.put("datetime64", SemossDataType.DATE);
		

		pyJ.put("object", java.lang.String.class);
		pyJ.put("category", java.lang.String.class);
		pyJ.put("int64", java.lang.Integer.class);
		pyJ.put("float64", java.lang.Double.class);
		pyJ.put("datetime64", java.util.Date.class);

		
		spy.put(SemossDataType.STRING, "'str'");
		spy.put(SemossDataType.INT, "np.int64");
		spy.put(SemossDataType.DOUBLE, "np.float64");
		spy.put(SemossDataType.DATE, "np.datetime32");
		spy.put(SemossDataType.TIMESTAMP, "np.datetime32");
		
		spy.put("float64", "np.float32");
		spy.put("int64", "np.int32");
		spy.put("datetime64", "np.datetime32");
		
		spy.put("dtype('O')", "'str'");
		spy.put("dtype('int64')", "int32");
		spy.put("dtype('float64')", "float32");
	}

	public PandasFrame() {
		this(null);
	}
	
	public PandasFrame(String tableName) {
		if(tableName == null || tableName.trim().isEmpty()) {
			tableName = "PYFRAME_" + UUID.randomUUID().toString().replace("-", "_");
		}
		this.frameName = tableName;
		this.wrapperFrameName = createFrameWrapperName(tableName);
	}
	
	@Override
	public void setName(String name) {
		if(name != null && !name.isEmpty()) {
			this.frameName = name;
			this.wrapperFrameName = createFrameWrapperName(name);
		}
	}
	
	/**
	 * Get the name of the frame wrapper object
	 * @return
	 */
	public String getWrapperName() {
		return this.wrapperFrameName;
	}
	
	public void setJep(PyExecutorThread py) {
		this.py = py;
	}

	public PyExecutorThread getJep() {
		return this.py ;
	}
	
	private String createFrameWrapperName(String frameName) {
		return frameName + "w";
	}
	
	public void addRowsViaIterator(Iterator<IHeadersDataRow> it) {
		// we really need another way to get the data types....
		Map<String, SemossDataType> rawDataTypeMap = this.metaData.getHeaderToTypeMap();
		
		// TODO: this is annoying, need to get the frame on the same page as the meta
		Map<String, SemossDataType> dataTypeMap = new HashMap<String, SemossDataType>();
		for(String rawHeader : rawDataTypeMap.keySet()) {
			dataTypeMap.put(rawHeader.split("__")[1], rawDataTypeMap.get(rawHeader));
		}
		this.addRowsViaIterator(it, this.frameName, dataTypeMap);
	}
	
	/**
	 * Generate a table from an iterator
	 * @param it
	 * @param tableName
	 * @param dataTypeMap
	 */
	public void addRowsViaIterator(Iterator<IHeadersDataRow> it, String tableName, Map<String, SemossDataType> dataTypeMap) {
		boolean loaded = false;
		if(it instanceof CsvFileIterator) {
			CsvQueryStruct csvQs = ((CsvFileIterator) it).getQs();
			if(csvQs.getLimit() == -1 || csvQs.getLimit() > 10_000) {
				addRowsViaCsvIterator((CsvFileIterator) it, tableName);
				loaded = true;
			}
		} 
		
		// just flush the excel to a grid through the iterator
		// using the below logic
		else if(it instanceof ExcelSheetFileIterator) {
			addRowsViaExcelIterator((ExcelSheetFileIterator) it, tableName);
			loaded = true;
		} 
		
		if(!loaded) {
			// default behavior is to just write this to a csv file
			// and read it back in
			String newFileLoc = DIHelper.getInstance().getProperty(Constants.INSIGHT_CACHE_DIR) + "/" + Utility.getRandomString(6) + ".csv";
			
			if(Boolean.parseBoolean(DIHelper.getInstance().getProperty(Constants.CHROOT_ENABLE)) && AbstractSecurityUtils.securityEnabled()) {
				
				Insight in = this.pyt.insight;
		
				String insightFolder = in.getInsightFolder();
			
				try {
					FileUtils.mkdirs(new File(insightFolder), true);
					if(in.getUser() != null) {
						in.getUser().getUserMountHelper().mountFolder(insightFolder,insightFolder, false);
					}
					newFileLoc = insightFolder + "/" + Utility.getRandomString(6) + ".csv";
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			File newFile = Utility.writeResultToFile(newFileLoc, it, dataTypeMap);
			
			String importPandasS = new StringBuilder(PANDAS_IMPORT_STRING).toString();
			String importNumpyS = new StringBuilder(NUMPY_IMPORT_STRING).toString();
			// generate the script
			String fileLocation = newFile.getAbsolutePath();
			String loadS = PandasSyntaxHelper.getCsvFileRead(PANDAS_IMPORT_VAR, NUMPY_IMPORT_VAR, 
					fileLocation, tableName, ",", pyt.getCurEncoding(), dataTypeMap);
			String makeWrapper = PandasSyntaxHelper.makeWrapper(createFrameWrapperName(tableName), tableName);
			// execute the script
			//pyt.runScript(importS, loadS);
			//pyt.runScript(makeWrapper);

			pyt.runEmptyPy(importPandasS, importNumpyS, loadS, makeWrapper);
			// delete the generated file
			
			// dont delete.. we probably need to test the file py
			newFile.delete();
		}
		
//		if(isEmpty(tableName)) {
//			throw new EmptyIteratorException("Unable to load data into pandas frame");
//		}
		
		syncHeaders();
		// need to get a pandas frame types and then see if this is the same as 
		if(!isEmpty(tableName)) {
			adjustDataTypes(tableName, dataTypeMap);
		}
	}
	
	/**
	 * Generate a table from a CSV file iterator
	 * @param it
	 * @param tableName
	 */
	private void addRowsViaCsvIterator(CsvFileIterator it, String tableName) {
		// generate the script
		CsvQueryStruct qs = it.getQs();
		String importPandasS = new StringBuilder(PANDAS_IMPORT_STRING).toString();
		String importNumpyS = new StringBuilder(NUMPY_IMPORT_STRING).toString();
		String fileLocation = it.getFileLocation();
		String loadS = PandasSyntaxHelper.getCsvFileRead(PANDAS_IMPORT_VAR, NUMPY_IMPORT_VAR, 
				fileLocation, tableName, qs.getDelimiter() + "", pyt.getCurEncoding(), qs.getColumnTypes());

		// need to compose a string for names
		String headerS = PandasSyntaxHelper.setColumnNames(tableName, it.getHeaders());
		// execute all 3 scripts
		//pyt.runScript(importS, loadS, headerS);
		String makeWrapper = PandasSyntaxHelper.makeWrapper(createFrameWrapperName(tableName), tableName);
		pyt.runEmptyPy(importPandasS, importNumpyS, loadS, headerS, makeWrapper);
		// need to set up the name here as well as make the frame
		//pyt.runScript(makeWrapper);
	}
	
	/**
	 * Generate a table from a CSV file iterator
	 * @param it
	 * @param tableName
	 */
	private void addRowsViaExcelIterator(ExcelSheetFileIterator it, String tableName) {
		ExcelQueryStruct qs = it.getQs();
		String sheetName = qs.getSheetName();
		String filePath = qs.getFilePath();
		String sheetRange = qs.getSheetRange();
		it.getSheet();
		// generate the script
		String importS = new StringBuilder(PANDAS_IMPORT_STRING).toString();
		String loadS = PandasSyntaxHelper.loadExcelSheet(PANDAS_IMPORT_VAR, filePath, tableName, sheetName, sheetRange);
		// need to compose a string for names
		String headerS = PandasSyntaxHelper.setColumnNames(tableName, it.getHeaders());
		// execute all 3 scripts
		// need to set up the name here as well as make the frame
		String makeWrapper = PandasSyntaxHelper.makeWrapper(createFrameWrapperName(tableName), tableName);
		//pyt.runScript(importS, loadS, headerS);
		
		//pyt.runScript(makeWrapper);
		pyt.runEmptyPy(importS, loadS, headerS, makeWrapper);
	}
	
	/**
	 * Merge the pandas frame with another frame. If a non equi join, performs a cross product and then 
	 * filters the results. For the non equi joins, if the left and right join column names are equal, changes the right column name
	 * so that it can be dropped later. 
	 * 
	 * @param returnTable
	 * @param leftTableName
	 * @param rightTableName
	 * @param joinType
	 * @param joinCols
	 */
	public void merge(String returnTable, String leftTableName, String rightTableName, String joinType, List<Map<String, String>> joinCols,
			List<String> joinComparators, boolean nonEqui) {
		String mergeString = PandasSyntaxHelper.getMergeSyntax(PANDAS_IMPORT_VAR, returnTable, leftTableName, rightTableName, 
				joinType, joinCols, nonEqui);
		
		if (!nonEqui) {
			pyt.runScript(mergeString);
		} else {
			for (int i = 0; i < joinCols.size(); i++) {
				Map<String, String> joinMap = joinCols.get(i);
				for (String lColumn : joinMap.keySet()) {
					if (lColumn.equals(joinMap.get(lColumn))) {
						String newColumn = joinMap.get(lColumn) + "_CTD";
						pyt.runScript(PandasSyntaxHelper.alterColumnName(rightTableName, joinMap.get(lColumn), newColumn));
						joinMap.replace(lColumn, newColumn);
						joinCols.set(i, joinMap);
					}
				}
			}
			String filterSyntax = PandasSyntaxHelper.getMergeFilterSyntax(returnTable, joinCols,joinComparators);
			pyt.runScript(mergeString);
			pyt.runScript(filterSyntax);
		}
	}
	
	/**
	 * Adjust the data types of the frame in case we messed up and readjust
	 * @param tableName
	 * @param dataTypeMap
	 */
	private void adjustDataTypes(String tableName, Map<String, SemossDataType> dataTypeMap) {
		String wrapperTableName = createFrameWrapperName(tableName);
		String colScript = PandasSyntaxHelper.getColumns(wrapperTableName + ".cache['data']");
		String typeScript = PandasSyntaxHelper.getTypes(wrapperTableName + ".cache['data']");
		
		List<String> headerList = (List) pyt.runScript(colScript);
		String[] headers = headerList.toArray(new String[headerList.size()]);
		List<String> types = (List<String>) pyt.runScript(typeScript);

		StringBuffer allTypes = new StringBuffer();
		// here we run and see if the types are good
		// or if we messed up, we perform a switch
		for(int colIndex = 0; colIndex < headers.length; colIndex++) {
			String colName = headers[colIndex];
			String colType = types.get(colIndex);

			if(types == null || colType == null) {
				colType = "STRING";
			}
			
			SemossDataType pysColType = (SemossDataType) pyS.get(colType);
			SemossDataType proposedType = dataTypeMap.get(frameName + "__" + colName);
			if(proposedType == null) {
				proposedType = dataTypeMap.get(colName);
			}
			String pyproposedType = colType;
			if(proposedType != null) {
				pyproposedType = spy.get(proposedType);
			} else {
				pyproposedType = spy.get(colType);
			}
			
			//if(proposedType != null && pysColType != proposedType) {
			if(proposedType != null && !pyproposedType.equalsIgnoreCase(colType)) {
				// create and execute the type
				if(proposedType == SemossDataType.DATE) {
					String typeChanger = tableName + "['" + colName + "'] = pd.to_datetime(" + tableName + "['" + colName + "'], errors='ignore').dt.date";
					allTypes.append(typeChanger).append("\n");
					//pyt.runScript(typeChanger);
				} else if(proposedType == SemossDataType.TIMESTAMP) {
					String typeChanger = tableName + "['" + colName + "'] = pd.to_datetime(" + tableName + "['" + colName + "'], errors='ignore')";
					allTypes.append(typeChanger).append("\n");
					//pyt.runScript(typeChanger);
				} else {
					String typeChanger = tableName + "['" + colName + "'] = " + tableName + "['" + colName + "'].astype(" + pyproposedType + ", errors='ignore')";
					allTypes.append(typeChanger).append("\n");
					//pyt.runScript(typeChanger);
				}
			}
			
			// execute all at once
			pyt.runEmptyPy(allTypes.toString());
			
			//if(colType.equalsIgnoreType)
			
			// reduce memory size
			// convert int32 vs float 32 vs. category
			
		}
	}
	
	// tries to see if the order in which pandas is giving is valid with the order that is being requested
	public boolean sync(String[] headers, List<String> actHeaders) {
		boolean sync = true;
		for(int headerIndex = 0;headerIndex < headers.length && sync;headerIndex++) {
			sync = sync && (headers[headerIndex].equals(actHeaders.get(headerIndex)));
		}
		return sync;
	}
	
	// get the types of headers
	public Object [] getHeaderAndTypes() {
		String colScript = PandasSyntaxHelper.getColumns(this.frameName + ".cache['data']");
		String typeScript = PandasSyntaxHelper.getTypes(this.frameName + ".cache['data']");
		
		/*
		Hashtable response = (Hashtable)pyt.runScript(colScript, typeScript);

		String [] headers = (String [])((ArrayList)response.get(colScript)).toArray();
		SemossDataType [] stypes = new SemossDataType[headers.length];
		*/
		
		String [] headers = (String [])((ArrayList)pyt.runScript(colScript)).toArray();
		SemossDataType [] stypes = new SemossDataType[headers.length];

		ArrayList <String> types = (ArrayList)pyt.runScript(typeScript);

		for(int colIndex = 0;colIndex < headers.length;colIndex++)
		{
			String colName = headers[colIndex];
			String colType = types.get(colIndex);
			
			SemossDataType pysColType = (SemossDataType)pyS.get(colType);
			stypes[colIndex] = pysColType;
		}
		Object [] retObject = new Object[2];
		retObject[1] = headers;
		
		return retObject;
	}

	@Override
	public IRawSelectWrapper query(SelectQueryStruct qs) {
		// R does not support relations in general
		// so we are going to remove any that may have been added
		// this is important for when the BE changes the frame without 
		// the FE knowing and that frame was native and had joins
		qs.getRelations().clear();
		
		// at this point try to see if the cache already has it and if so pass that iterator instead
		// the cache is sitting in the insight
		qs = QSAliasToPhysicalConverter.getPhysicalQs(qs, this.metaData);
		if(qs.getPragmap() != null && qs.getPragmap().containsKey("xCache"))
			this.cache = ((String)qs.getPragmap().get("xCache")).equalsIgnoreCase("True") ? true:false;
		
		PandasInterpreter interp = new PandasInterpreter();
		interp.setDataTableName(this.frameName, this.wrapperFrameName + ".cache['data']");
		interp.setDataTypeMap(this.metaData.getHeaderToTypeMap());
		interp.setQueryStruct(qs);
		interp.setKeyCache(keyCache);
		// I should also possibly set up pytranslator so I can run command for creating filter
		interp.setPyTranslator(pyt);
		// need to do this for subqueries where we flush the values into a filter
		interp.setPandasFrame(this);
		return processInterpreter(interp, qs);
	}
	
	@Override
	public IRawSelectWrapper query(String query) {
		//TODO: this only works if you have an interp!
		//TODO: this only works if you have an interp!
		//TODO: this only works if you have an interp!

		// need to redo this when you have a pandas script you want to run
		// need to grab the headers and types via the output object
		
		Object output = pyt.runScript(query);
		List<Object> response = null;
		
		PandasInterpreter interp = new PandasInterpreter();
		String [] headers = interp.getHeaders();
		SemossDataType [] types = interp.getTypes();
		List<String> actHeaders = null;
		
		boolean sync = true;	
		// get the types for headers also
		if(interp.isScalar()) {
			List<Object> val = new ArrayList<Object>();
			val.add(output);
			response = new ArrayList<Object>();
			response.add(val);
			
		} else if(output instanceof HashMap) {
			HashMap map = (HashMap) output;
			response = (List<Object>) map.get("data");
			
			// get the columns
			List<Object> columns = (List<Object>) map.get("columns");
			actHeaders = mapColumns(interp, columns);
			sync = sync(headers, actHeaders);
		}
		
		else if(output instanceof List) {
			response = (List) output;
			actHeaders = null;
			sync = true;
		}
		
		PandasIterator pi = new PandasIterator(headers, response, types);
		// set the actual headers so that it can be processed
		// if not in sync then transform
		pi.setTransform(actHeaders, !sync);
		RawPandasWrapper rpw = new RawPandasWrapper();
		rpw.setPandasIterator(pi);
		return rpw	;
	}
	
	// create a subframe for the purposes of variables
	@Override
	public String createVarFrame() {
		PandasInterpreter interp = new PandasInterpreter();
		SelectQueryStruct qs = getMetaData().getFlatTableQs(true);
		// add all the frame filter
		qs.setExplicitFilters(this.grf.copy());
		// convert to physical
		qs = QSAliasToPhysicalConverter.getPhysicalQs(qs, this.metaData);
		
		interp.setDataTableName(this.frameName, this.wrapperFrameName + ".cache['data']");
		interp.setDataTypeMap(this.metaData.getHeaderToTypeMap());
		interp.setQueryStruct(qs);
		interp.setKeyCache(keyCache);
		// I should also possibly set up pytranslator so I can run command for creating filter
		interp.setPyTranslator(pyt);
		// need to do this for subqueries where we flush the values into a filter
		interp.setPandasFrame(this);
		
		String query = interp.composeQuery();
		query = query.substring(0, query.indexOf(".drop_duplicates"));
		String newFrame = Utility.getRandomString(6);
		String command = newFrame  + " = " + query;
		pyt.runScript(command);
		return newFrame;
	}

	private IRawSelectWrapper processInterpreter(PandasInterpreter interp, SelectQueryStruct qs) {
		String query = interp.composeQuery();
		
		// make it into a full frame
		String frameName = Utility.getRandomString(6);
		// assign query to frame
		// the command that is coming in has the sort values and other things attached to it
		// need to get rid of it before I can get the frame
		// and then slap it back to get the values back
		//ascszng = d[['bp.1d','bp.1s','bp.2d','bp.2s','chol','Drug','frame','gender','glyhb','hdl','height','hip','id','location','ratio','stab.glu','time.ppn','waist','weight']]
		// .drop_duplicates().sort_values(['bp.1d'],ascending=[True]).iloc[0:2000].to_dict('split')
		// need to get rid of everything from drop_duplicates
		// if that is not available then sort values
		// make the query to be just the new frameName
		// query = frameName;
		
		CachedIterator ci = null;
		IRawSelectWrapper retWrapper = null;
		
		if(!queryCache.containsKey(query) || !cache)
		{			
			// run the query
			Object output = pyt.runScript(query);
			
			// need to see if this is a parquet format as well
			String format = "grid";
			if(qs.getPragmap() != null && qs.getPragmap().containsKey("format"))
				format = (String)qs.getPragmap().get("format");

			String [] headers = interp.getHeaders();
			SemossDataType [] types = interp.getTypes();

			if(format.equalsIgnoreCase("grid"))
			{
				List<Object> response = null;	
				List<String> actHeaders = null;
				
				boolean sync = true;	
				// get the types for headers also
				if(interp.isScalar()) {
					List<Object> val = new ArrayList<Object>();
					val.add(output);
					response = new ArrayList<Object>();
					response.add(val);
					
				} else if(output instanceof HashMap) {
					HashMap map = (HashMap) output;
					response = (List<Object>)map.get("data");
					
					// get the columns
					List<Object> columns = (List<Object>)map.get("columns");
					actHeaders = mapColumns(interp, columns);
					sync = sync(headers, actHeaders);
				}
				
				else if(output instanceof List) {
					response = (List) output;
					actHeaders = null;
					sync = true;
				}
				
				PandasIterator pi = new PandasIterator(headers, response, types);
				pi.setQuery(query);
				pi.setTransform(actHeaders, !sync);
				RawPandasWrapper rpw = new RawPandasWrapper();
				rpw.setPandasIterator(pi);
				retWrapper = rpw;
			}
			else // handling parquet format here
			{
				PandasParquetIterator ppi = new PandasParquetIterator(headers, output, types);
				ppi.setQuery(query);
				RawPandasParquetWrapper rpw = new RawPandasParquetWrapper();
				rpw.setPandasParquetIterator(ppi);
				retWrapper = rpw;
			}
			// clear it if it was !cache
			if(!cache) {
				// clean up the cache
				// I have to do this everytime since I keep the keys at the py level as well
				clearQueryCache();
			}
		}
		else if(cache)
		{
			ci = queryCache.get(query);
			RawCachedWrapper rcw = new RawCachedWrapper();
			rcw.setIterator(ci);
			retWrapper = rcw;
		}
		
		// set the actual headers so that it can be processed
		// if not in sync then transform
		return retWrapper;
	}

	private List<String> mapColumns(PandasInterpreter interp, List<Object> columns) {
		List<String> newHeaders = new ArrayList<String>();
		Map<String, String> funcMap = interp.functionMap();
		for(int colIndex = 0;colIndex < columns.size();colIndex++) {
			// every element here is List
			Object item = columns.get(colIndex);
			if(item instanceof List) {
				List elem = (List) columns.get(colIndex);
				String key = elem.get(1) + "" + elem.get(0);
				if(funcMap.containsKey(key)) {
					newHeaders.add(funcMap.get(key));
				} else {
					newHeaders.add(elem.get(0)+"");
				}
			} else {
				newHeaders.add(item+"");
			}
		}
		return newHeaders;
	}
	
	@Override
	public IQueryInterpreter getQueryInterpreter() {
		PandasInterpreter interp = new PandasInterpreter();
		interp.setDataTableName(this.getName(), this.wrapperFrameName);
		interp.setDataTypeMap(this.metaData.getHeaderToTypeMap());
		return interp;
	}
	
	/**
	 * Run the script
	 * By default return the first script passed in
	 * use the Executor to grab the specific code portion if running multiple
	 * @param script
	 * @return
	 */
	public Object runScript(String script) {
		/*
		py.command = script;
		Object monitor = py.getMonitor();
		Object response = null;
		synchronized(monitor) {
			try {
				monitor.notify();
				monitor.wait();
			} catch (Exception ignored) {
				
			}
			if(script.length == 1) {
				response = py.response.get(script[0]);
			} else {
				response = py.response;
			}
		}

		commands.add(script[0]);
		return response;
		*/
		//if(script.length == 1)
			return pyt.runScript(script);
		//else
		//	return pyt.runScript(script);
	}
	
	@Override
	public long size(String tableName) {
		if(isEmpty(tableName)) {
			return 0;
		}
		String command = "len(" + tableName + ")";
		Number num = (Number) pyt.runScript(command);
		return num.longValue();
	}
	
	@Override
	public void close() {
		super.close();
		// this should take the variable name and kill it
		// if the user has created others, nothing can be done
		logger.info("Removing variable " + this.frameName);
		pyt.runScript("del " + this.frameName);
		pyt.runScript("del " + this.wrapperFrameName);
		pyt.runScript("gc.collect()");
	}
	
	@Override
	public CachePropFileFrameObject save(String folderDir) throws IOException {
		CachePropFileFrameObject cf = new CachePropFileFrameObject();
		// save frame
		String frameFilePath = folderDir + DIR_SEPARATOR + this.frameName + ".pkl";
		cf.setFrameCacheLocation(frameFilePath);
		
		// trying to write the pickle instead
		frameFilePath = frameFilePath.replaceAll("\\\\", "/");
		
		/*
		
		pyt.runScript("import pickle");
		String command = PandasSyntaxHelper.getWritePandasToPickle("pickle", this.frameName, frameFilePath);
		pyt.runScript(command);
		*/
		String [] commands = new String[]{"import pickle", PandasSyntaxHelper.getWritePandasToPickle("pickle", this.frameName, frameFilePath)};
		pyt.runEmptyPy(commands);
		
		// also save the meta details
		this.saveMeta(cf, folderDir, this.frameName);
		return cf;
	}
	
	@Override
	public void open(CachePropFileFrameObject cf) {
		// open the meta details
		this.openCacheMeta(cf);
		// set the wrapper frame name once the frame name is set
		this.wrapperFrameName = getWrapperName();
				
		// load the pandas library
		/*pyt.runScript(PANDAS_IMPORT_STRING);
		// load the frame
		pyt.runScript("import pickle");
		pyt.runScript(PandasSyntaxHelper.getReadPickleToPandas(PANDAS_IMPORT_VAR, cf.getFrameCacheLocation(), this.frameName));
		pyt.runScript(PandasSyntaxHelper.makeWrapper(this.wrapperFrameName, this.frameName));

		*/
		
		String [] commands = new String[]{PANDAS_IMPORT_STRING, "import pickle", 
							PandasSyntaxHelper.getReadPickleToPandas(PANDAS_IMPORT_VAR, cf.getFrameCacheLocation(), 
							this.frameName),PandasSyntaxHelper.makeWrapper(this.wrapperFrameName, this.frameName)};

		
		pyt.runEmptyPy(commands);
		
	}

	@Override
	public boolean isEmpty() {
		return isEmpty(this.frameName);
	}
	
	public boolean isEmpty(String tableName) {
		String command = "('" + createFrameWrapperName(tableName) + "' in vars() and len(" + createFrameWrapperName(tableName) + ".cache['data']) >= 0)";
		
		Boolean notEmpty = (Boolean) pyt.runScript(command);
		return !notEmpty;
	}
	
	@Override
	public DataFrameTypeEnum getFrameType() {
		return DataFrameTypeEnum.PYTHON;
	}
	
	@Override
	public String getDataMakerName() {
		return DATA_MAKER_NAME;
	}
	
	///////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////

	@Override
	public void addRow(Object[] cleanCells, String[] headers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeColumn(String columnHeader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processDataMakerComponent(DataMakerComponent component) {
		// TODO Auto-generated method stub
		
	}

	public void setTranslator(PyTranslator pyt) {
		// TODO Auto-generated method stub
		this.pyt = pyt;
		pyt.setLogger(logger);
		
	}
	
	public PyTranslator getTranslator()
	{
		return pyt;
	}
	
	/**
	 * Recreate the metadata for this existing frame
	 */
	public void recreateMeta() {
		logger.info("Getting the columns for :" + frameName);

		String[] colNames = pyt.getStringArray(PandasSyntaxHelper.getColumns(frameName));;
		pyt.runScript(PandasSyntaxHelper.cleanFrameHeaders(frameName, colNames));
		logger.info("Getting the column types for :" + frameName);

		String[] colTypes = pyt.getStringArray(PandasSyntaxHelper.getTypes(frameName));
		//clean headers
		HeadersException headerChecker = HeadersException.getInstance();
		colNames = headerChecker.getCleanHeaders(colNames);
	
		// grab the existing metadata from the frame
		Map<String, String> additionalDataTypes = this.metaData.getHeaderToAdtlTypeMap();
		Map<String, List<String>> sources = this.metaData.getHeaderToSources();
		Map<String, String[]> complexSelectors = this.metaData.getComplexSelectorsMap();
		// close existing meta
		// make a new one
		// load into it
		this.metaData.close();
		this.metaData = new OwlTemporalEngineMeta();
		ImportUtility.parseTableColumnsAndTypesToFlatTable(this.metaData, colNames, colTypes, getName(), additionalDataTypes, sources, complexSelectors);
		
		// clear the cached info
		this.clearCachedMetrics();
		this.clearQueryCache();
	}
	
	/**
	 * Update the wrapper frame with the actual frame object
	 */
	public void replaceWrapperDataFromFrame() {
		pyt.runScript(wrapperFrameName + ".cache['data'] = "  + frameName );
	}
	
	
	
	@Override
	public Object querySQL(String sql)
	{
		// columns
		// types
		// data
		if(sql.toUpperCase().startsWith("SELECT"))
		{
			Map retMap = new HashMap();
			
			String loadsqlDF = "from pandasql import sqldf";
			String frameName = Utility.getRandomString(5);
			String newFrame = frameName + "= sqldf('" + sql + "')";
			String addColumnTypes = frameName + "_types = " + frameName + ".dtypes.to_dict()";
			String dict = frameName + "_dict = " + frameName + ".to_dict('split')";
			String dictColumns = frameName + "_dict['types'] = " + frameName + "_types";
			
			String deleteAll = "delete " + frameName + ", " + frameName + "_types, " + frameName + "_dict";
			
			pyt.runEmptyPy(loadsqlDF, newFrame, addColumnTypes, dict, dictColumns);
			
			Object retObject = pyt.runScript(frameName + "_dict"); // get the dictionary back
			
			// will delete later
			pyt.runEmptyPy(deleteAll);
			
			if(retObject instanceof Map)
			{
				System.err.println("Valid Output");
				retMap = (Map)retObject;
			}
			// convert types to java object
			Map typeMap = (Map)retMap.get("types");
			Iterator keys = typeMap.keySet().iterator();
			while(keys.hasNext())
			{
				String column = (String)keys.next();
				String value = (String)typeMap.get(column);
				
				if(pyJ.containsKey(value))
					typeMap.put(column, pyJ.get(value));
				else
					typeMap.put(column, java.lang.String.class);
			}
			
			List <String> columns = null;
			columns = (List <String>)retMap.get("columns");
			
			String [] colArray = new String[columns.size()];
			Object [] typesArray = new Object[columns.size()];
			
			for(int columnIndex = 0;columnIndex < columns.size();columnIndex++)
			{
				String thisColumn = columns.get(columnIndex);
				Object colType = typeMap.get(thisColumn);
				typesArray[columnIndex] = colType;
				colArray[columnIndex] = thisColumn;
			}
			retMap.put("columns", colArray);
			retMap.put("types", typesArray);
			return retMap;
		}
		else
		{

			Map retMap = new HashMap();

			String [] commands = sql.split("\\R");
			// execute each command and drop the result
			String [] columns = new String [] {"Command", "Output"};
			Object [] types = new Object [] {java.lang.String.class, java.lang.String.class};
			
			List <List<Object>> data = new ArrayList<List<Object>>();
			
			for(int commandIndex = 0;commandIndex < commands.length;commandIndex++)
			{
				List <Object> row = new ArrayList <Object>();
				String thisCommand = commands[commandIndex];
				Object output = pyt.runScript(thisCommand);
				
				row.add(thisCommand);
				row.add(output);
				
				data.add(row);
			}
			retMap.put("data", data);
			retMap.put("types", types);
			retMap.put("columns", columns);
			
			return retMap;
		}
		
	}
	
	public Object queryCSV(String sql)
	{
		// columns
		// types
		// data
		if(sql.toUpperCase().startsWith("SELECT"))
		{
			Map retMap = new HashMap();
			
			String loadsqlDF = "from pandasql import sqldf";
			try
			{
				String frameName = Utility.getRandomString(5);
				File fileName = new File(DIHelper.getInstance().getProperty(Constants.INSIGHT_CACHE_DIR),   frameName + ".csv");
				
				String fileNameStr = fileName.getAbsolutePath().replace("\\", "/");
				
				String newFrame = "sqldf('" + sql + "').to_csv('" + fileNameStr + "', index=False)";
				
				String deleteAll = "delete " + frameName;
			
				pyt.runEmptyPy(loadsqlDF, newFrame);
				Object retObject = "no data";
				
				if(fileName.exists())
				{
					//retObject = new String(Files.readAllBytes(fileName.toPath())); // get the dictionary back
					//fileName.delete(); // delete the generated file
					return fileName.getAbsolutePath();
				}
				pyt.runEmptyPy(deleteAll);
				//return retObject;
			}catch(Exception ex)
			{
				
			}
			// will delete later
			
		}
		return null;
	}

	// recalibrate variables
	public void recalculateVariables(String [] formulas, String oldName, String newName)
	{
		// this is a string replacement unfortunately
		// this is again why we may need better frame variable names
		String [] returnCommands = new String[formulas.length + 1];
		
		for(int varIndex = 0;varIndex < formulas.length;varIndex++)
		{
			String thisVar = formulas[varIndex];
			thisVar = thisVar.replace(oldName, newName);
			System.err.println("Running command " + thisVar);
			returnCommands[varIndex + 1] = thisVar;
			pyt.runScript(thisVar);
		}
		
	}
	
	///////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	
	/*
	 * Unused methods...
	 */
	
//	private void alignColumns(String tableName, Map<String, SemossDataType> dataTypeMap) {
//		// look at what are the column types
//		// see if they are the same as data type map
//		
//		String colScript = PandasSyntaxHelper.getColumns(tableName);
//		String typeScript = PandasSyntaxHelper.getTypes(tableName);
//		
//		Hashtable columnTypes = (Hashtable)pyt.runScript(colScript, typeScript);
//		// get the column names
//		ArrayList <String> cols = (ArrayList)columnTypes.get(colScript);
//		ArrayList <String> types = (ArrayList)columnTypes.get(typeScript);
//
//		List <String> colChanger = new ArrayList<String>();
//
//		for(int colIndex = 0;colIndex < cols.size();colIndex++)
//		{
//			String colName = cols.get(colIndex);
//			String colType = types.get(colIndex);
//			
//			SemossDataType pysColType = (SemossDataType)pyS.get(colType);
//			
//			SemossDataType sColType = dataTypeMap.get(colName);
//		
//			colChanger = checkColumn(tableName, colName, pysColType, sColType, colChanger);
//		}
//		
//		pyt.runScript((String [])colChanger.toArray());
//	}
	
//	private List<String> checkColumn(String tableName, String colName, SemossDataType curType, SemossDataType newType, List <String> colChanger) {
//		if(curType == newType) {
//			return colChanger;
//		} else {
//			// get the pytype
//			String pyType = (String)spy.get(curType);
//			
//			// get the as type
//			String asType = (String)spy.get(pyType);
//			
//			// column change
//			String script = PandasSyntaxHelper.getColumnChange(tableName, colName, asType);
//			colChanger.add(script);
//			
//			return colChanger;
//		}
//	}
	
//	public void setDataTypeMap(Map<String, SemossDataType> dataTypeMap) {
//		this.dataTypeMap = dataTypeMap;
//		interp.setDataTypeMap(dataTypeMap);
//	}
	
}
