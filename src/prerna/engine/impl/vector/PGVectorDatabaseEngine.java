package prerna.engine.impl.vector;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pgvector.PGvector;

import au.com.bytecode.opencsv.CSVReader;
import prerna.cluster.util.ClusterUtil;
import prerna.cluster.util.DeleteFilesFromEngineRunner;
import prerna.ds.py.PyUtils;
import prerna.ds.py.TCPPyTranslator;
import prerna.engine.api.IEngine;
import prerna.engine.api.IModelEngine;
import prerna.engine.api.IVectorDatabaseEngine;
import prerna.engine.api.VectorDatabaseTypeEnum;
import prerna.engine.impl.model.ModelEngineConstants;
import prerna.engine.impl.model.responses.EmbeddingsModelEngineResponse;
import prerna.engine.impl.rdbms.RDBMSNativeEngine;
import prerna.engine.impl.vector.PGVectorDatabaseEngine.PgVectorTable.PgVectorRow;
import prerna.om.ClientProcessWrapper;
import prerna.om.Insight;
import prerna.om.InsightStore;
import prerna.query.querystruct.AbstractQueryStruct.QUERY_STRUCT_TYPE;
import prerna.query.querystruct.HardSelectQueryStruct;
import prerna.reactor.vector.VectorDatabaseParamOptionsEnum;
import prerna.util.ConnectionUtils;
import prerna.util.Constants;
import prerna.util.QueryExecutionUtility;
import prerna.util.Settings;
import prerna.util.Utility;
import prerna.util.sql.PGVectorQueryUtil;
import prerna.util.sql.RDBMSUtility;

public class PGVectorDatabaseEngine extends RDBMSNativeEngine implements IVectorDatabaseEngine {

	private static final Logger classLogger = LogManager.getLogger(PGVectorDatabaseEngine.class);

	public static final String PGVECTOR_TABLE_NAME = "PGVECTOR_TABLE_NAME";

	protected static final String DIR_SEPARATOR = "/";
	protected static final String FILE_SEPARATOR = java.nio.file.FileSystems.getDefault().getSeparator();
	
	private static final String tokenizerInitScript = "from genai_client import get_tokenizer;cfg_tokenizer = get_tokenizer(tokenizer_name = '${MODEL}', max_tokens = ${MAX_TOKENS}, tokenizer_type = '${MODEL_TYPE}');import vector_database;";

	protected String engineDirectoryPath = null;
	protected int contentLength = 512;
	protected int contentOverlap = 0;
	protected String defaultChunkUnit;
	protected String defaultExtractionMethod;
	protected String defaultIndexClass;
	
	protected String embedderEngineId = null;
	protected String keywordGeneratorEngineId = null;
	
	File schemaFolder;

	List<String> indexClasses;

	// python server
	private String pyDirectoryBasePath;
	protected TCPPyTranslator pyt = null;
	private File cacheFolder;
	private ClientProcessWrapper cpw = null;
	
	String vectorTableName = null;
	
	private boolean modelPropsLoaded = false;
	
	// string substitute vars
	Map<String, String> vars = new HashMap<>();

	@Override
	public void open(Properties smssProp) throws Exception {
		super.open(smssProp);

		this.vectorTableName = smssProp.getProperty(PGVECTOR_TABLE_NAME);

		this.engineDirectoryPath = RDBMSUtility.fillParameterizedFileConnectionUrl("@BaseFolder@/vector/@ENGINE@/", this.engineId, this.engineName);

		
		this.pyDirectoryBasePath = this.engineDirectoryPath + "py" + DIR_SEPARATOR;
		this.cacheFolder = new File(this.pyDirectoryBasePath);

		// second layer - This holds all the different "tables". The reason we want this is to easily and quickly grab the sub folders
		this.schemaFolder = new File(this.engineDirectoryPath, "schema");
		if(!this.schemaFolder.exists()) {
			this.schemaFolder.mkdirs();
		}
		
		// third layer - All the separate tables,classes, or searchers that can be added to this db
		this.indexClasses = new ArrayList<>();
		
		Connection conn = null;
		try {
			conn = getConnection();
			PGvector.addVectorType(conn);
		} catch(SQLException e) {
			classLogger.error(Constants.STACKTRACE, e);
			throw e;
		} finally {
			ConnectionUtils.closeAllConnectionsIfPooling(this, conn, null, null);
		}
		
		this.defaultChunkUnit = "tokens";
		if (this.smssProp.containsKey(Constants.DEFAULT_CHUNK_UNIT)) {
			this.defaultChunkUnit = this.smssProp.getProperty(Constants.DEFAULT_CHUNK_UNIT).toLowerCase().trim();
			if (!this.defaultChunkUnit.equals("tokens") && !this.defaultChunkUnit.equals("characters")){
	            throw new IllegalArgumentException("DEFAULT_CHUNK_UNIT should be either 'tokens' or 'characters'");
			}
		}
		
		this.defaultExtractionMethod = this.smssProp.getProperty(Constants.EXTRACTION_METHOD, "None");
		
		this.defaultIndexClass = "default";
		if (this.smssProp.containsKey(Constants.INDEX_CLASSES)) {
			this.defaultIndexClass = this.smssProp.getProperty(Constants.INDEX_CLASSES);
		}
	}
	
	protected void verifyModelProps() {
		// This could get moved depending on other vector db needs
		// This is to get the Model Name and Max Token for an encoder -- we need this to verify chunks aren't getting truncated
		this.embedderEngineId = this.smssProp.getProperty(Constants.EMBEDDER_ENGINE_ID);
		if (embedderEngineId == null) {
			embedderEngineId = this.smssProp.getProperty("ENCODER_ID");
			if (embedderEngineId == null) {
				throw new IllegalArgumentException("Embedder Engine ID is not provided.");
			}
			
			this.smssProp.put(Constants.EMBEDDER_ENGINE_ID, embedderEngineId);
		}
		
		IModelEngine modelEngine = Utility.getModel(embedderEngineId);
		if (modelEngine == null) {
			throw new IllegalArgumentException("Model Engine must be created and contain MODEL");
		}
		
		Properties modelProperties = modelEngine.getSmssProp();
		if (modelProperties.isEmpty() || !modelProperties.containsKey(Constants.MODEL)) {
			throw new IllegalArgumentException("Model Engine must be created and contain MODEL");
		}
		
		this.smssProp.put(Constants.MODEL, modelProperties.getProperty(Constants.MODEL));
		this.smssProp.put(IModelEngine.MODEL_TYPE, modelProperties.getProperty(IModelEngine.MODEL_TYPE));
		if (!modelProperties.containsKey(Constants.MAX_TOKENS)) {
			this.smssProp.put(Constants.MAX_TOKENS, "None");
		} else {
			this.smssProp.put(Constants.MAX_TOKENS, modelProperties.getProperty(Constants.MAX_TOKENS));
		}

		// model engine responsible for creating keywords
		this.keywordGeneratorEngineId = this.smssProp.getProperty(AbstractVectorDatabaseEngine.KEYWORD_ENGINE_ID);
				
		for (Object smssKey : this.smssProp.keySet()) {
			String key = smssKey.toString();
			this.vars.put(key, this.smssProp.getProperty(key));
		}
					
		modelPropsLoaded = true;
	}

	public void startServer(int port) {
		if (!modelPropsLoaded) {
			verifyModelProps();
		}
		// spin the server
		// start the client
		// get the startup command and parameters - at some point we need a better way than the command

		// execute all the basic commands		

		// break the commands seperated by ;
		String [] commands = (tokenizerInitScript).split(ModelEngineConstants.PY_COMMAND_SEPARATOR);

		// need to iterate through and potential spin up tables themselves
		if (this.indexClasses.size() > 0) {
			ArrayList<String> modifiedCommands = new ArrayList<>(Arrays.asList(commands));
			commands = modifiedCommands.stream().toArray(String[]::new);
		}

		// replace the Vars
		StringSubstitutor substitutor = new StringSubstitutor(this.vars);
		for(int commandIndex = 0; commandIndex < commands.length;commandIndex++) {
			String resolvedString = substitutor.replace(commands[commandIndex]);
			commands[commandIndex] = resolvedString;
		}
		
		if(!this.cacheFolder.exists()) {
			this.cacheFolder.mkdirs();
		}
		
		// check if we have already created a process wrapper
		if(this.cpw == null) {
			this.cpw = new ClientProcessWrapper();
		}
		
		String timeout = "30";
		if(this.smssProp.containsKey(Constants.IDLE_TIMEOUT)) {
			timeout = this.smssProp.getProperty(Constants.IDLE_TIMEOUT);
		}
		
		if(this.cpw.getSocketClient() == null) {
			boolean debug = false;
			
			// pull the relevant values from the smss
			String forcePort = this.smssProp.getProperty(Settings.FORCE_PORT);
			String customClassPath = this.smssProp.getProperty("TCP_WORKER_CP");
			String loggerLevel = this.smssProp.getProperty(Settings.LOGGER_LEVEL, "WARNING");
			String venvEngineId = this.smssProp.getProperty(Constants.VIRTUAL_ENV_ENGINE, null);
			String venvPath = venvEngineId != null ? Utility.getVenvEngine(venvEngineId).pathToExecutable() : null;
			
			if(port < 0) {
				// port has not been forced
				if(forcePort != null && !(forcePort=forcePort.trim()).isEmpty()) {
					try {
						port = Integer.parseInt(forcePort);
						debug = true;
					} catch(NumberFormatException e) {
						// ignore
						classLogger.warn("Vector Database " + this.engineName + " has an invalid FORCE_PORT value");
					}
				}
			}
			
			String serverDirectory = this.cacheFolder.getAbsolutePath();
			boolean nativePyServer = true; // it has to be -- don't change this unless you can send engine calls from python
			try {
				this.cpw.createProcessAndClient(nativePyServer, null, port, venvPath, serverDirectory, customClassPath, debug, timeout, loggerLevel);
			} catch (Exception e) {
				classLogger.error(Constants.STACKTRACE, e);
				throw new IllegalArgumentException("Unable to connect to server for faiss databse.");
			}
		} else if (!this.cpw.getSocketClient().isConnected()) {
			this.cpw.shutdown(false);
			try {
				this.cpw.reconnect();
			} catch (Exception e) {
				classLogger.error(Constants.STACKTRACE, e);
				throw new IllegalArgumentException("Failed to start TCP Server for Faiss Database = " + this.engineName);
			}
		}

		// create the py translator
		pyt = new TCPPyTranslator();
		pyt.setSocketClient(this.cpw.getSocketClient());
		
		pyt.runEmptyPy(commands);
	}

	public void initSQL(Connection con, String schema, String table) throws SQLException {
		//creating embeddings table
		Statement statement = con.createStatement();
		PGVectorQueryUtil pgVectorQueryUtil = new PGVectorQueryUtil();
		String sqlQueryString = pgVectorQueryUtil.createEmbeddingsTable(schema,table);
		classLogger.info(">>>>> " + sqlQueryString);
		statement.execute(sqlQueryString);
	}

	@Override
	public void addDocument(List<String> filePaths, Map<String, Object> parameters) {		
		this.removeDocument(filePaths, parameters);

		String indexClass = this.defaultIndexClass;
		if (parameters.containsKey("indexClass")) {
			indexClass = (String) parameters.get("indexClass");
		}

		int chunkMaxTokenLength = this.contentLength;
		if (parameters.containsKey(VectorDatabaseParamOptionsEnum.CONTENT_LENGTH.getKey())) {
			chunkMaxTokenLength = (int) parameters.get(VectorDatabaseParamOptionsEnum.CONTENT_LENGTH.getKey());
		}

		int tokenOverlapBetweenChunks = this.contentOverlap;
		if (parameters.containsKey(VectorDatabaseParamOptionsEnum.CONTENT_OVERLAP.getKey())) {
			tokenOverlapBetweenChunks = (int) parameters.get(VectorDatabaseParamOptionsEnum.CONTENT_OVERLAP.getKey());
		}

		String chunkUnit = this.defaultChunkUnit;
		if (parameters.containsKey(VectorDatabaseParamOptionsEnum.CHUNK_UNIT.getKey())) {
			chunkUnit = (String) parameters.get(VectorDatabaseParamOptionsEnum.CHUNK_UNIT.getKey());
		}

		String extractionMethod = this.defaultExtractionMethod;
		if (parameters.containsKey(VectorDatabaseParamOptionsEnum.EXTRACTION_METHOD.getKey())) {
			chunkUnit = (String) parameters.get(VectorDatabaseParamOptionsEnum.EXTRACTION_METHOD.getKey());
		}
		
		Insight insight = getInsight(parameters.get(AbstractVectorDatabaseEngine.INSIGHT));
		if (insight == null) {
			throw new IllegalArgumentException("Insight must be provided to run Model Engine Encoder");
		}
		
		File tableIndexFolder = new File(this.schemaFolder + DIR_SEPARATOR + indexClass, "indexed_files");
		try {
			// first we need to extract the text from the document
			// TODO change this to json so we never have an encoding issue
			checkSocketStatus();

			File indexDirectory = new File(this.schemaFolder, indexClass);
			File documentDir = new File(indexDirectory, "documents");
			if(!documentDir.exists()) {
				documentDir.mkdirs();
			}

			boolean filesAppoved = VectorDatabaseUtils.verifyFileTypes(filePaths, new ArrayList<>(Arrays.asList(documentDir.list())));
			if (!filesAppoved) {
				throw new IllegalArgumentException("Currently unable to mix csv with non-csv file types.");
			}

			if (!tableIndexFolder.exists()) {
				tableIndexFolder.mkdirs();
			}
			if (!this.indexClasses.contains(indexClass)) {
				this.indexClasses.add(indexClass);
			}

			String columnsToIndex = "";
			List<File> extractedFiles = new ArrayList<File>();
			List<String> filesToCopyToCloud = new ArrayList<String>(); // create a list to store all the net new files so we can push them to the cloud
			String chunkingStrategy = PyUtils.determineStringType(parameters.getOrDefault("chunkingStrategy", "ALL"));

			// move the documents from insight into documents folder
			HashSet<File> fileToExtractFrom = new HashSet<File>();
			for (String fileName : filePaths) {
				File fileInInsightFolder = new File(fileName);

				// Double check that they are files and not directories
				if (!fileInInsightFolder.isFile()) {
					continue;
				}

				File destinationFile = new File(documentDir, fileInInsightFolder.getName());

				// Check if the destination file exists, and if so, delete it
				try {
					if (destinationFile.exists()) {
						FileUtils.forceDelete(destinationFile);
					}
					FileUtils.moveFileToDirectory(fileInInsightFolder, documentDir, true);
				} catch (IOException e) {
					classLogger.error(Constants.STACKTRACE, e);
					throw new IllegalArgumentException("Unable to remove previously created file for " + destinationFile.getName() + " or move it to the document directory");
				}

				// add it to the list of files we need to extract text from
				fileToExtractFrom.add(destinationFile);
			}
			
			// loop through each document and attempt to extract text
			for (File document : fileToExtractFrom) {
				String documentName = document.getName().split("\\.")[0];
				File extractedFile = new File(tableIndexFolder.getAbsolutePath() + DIR_SEPARATOR + documentName + ".csv");
				String extractedFileName = extractedFile.getAbsolutePath().replace(FILE_SEPARATOR, DIR_SEPARATOR);
				try {
					if (extractedFile.exists()) {
						FileUtils.forceDelete(extractedFile);
					}
					if (!document.getName().toLowerCase().endsWith(".csv")) {

						classLogger.info("Extracting text from document " + documentName);
						// determine which text extraction method to use
						int rowsCreated;
						if (extractionMethod.equals("fitz") && document.getName().toLowerCase().endsWith(".pdf")) {
							StringBuilder extractTextFromDocScript = new StringBuilder();
							extractTextFromDocScript.append("vector_database.extract_text(source_file_name = '")
							.append(document.getAbsolutePath().replace(FILE_SEPARATOR, DIR_SEPARATOR))
							.append("', target_folder = '")
							.append(this.schemaFolder.getAbsolutePath().replace(FILE_SEPARATOR, DIR_SEPARATOR) + DIR_SEPARATOR + indexClass + DIR_SEPARATOR + "extraction_files")
							.append("', output_file_name = '")
							.append(extractedFileName)
							.append("')");
							Number rows = (Number) pyt.runScript(extractTextFromDocScript.toString());

							rowsCreated = rows.intValue();
						} else {
							rowsCreated = VectorDatabaseUtils.convertFilesToCSV(extractedFile.getAbsolutePath(), document);
						}

						// check to see if the file data was extracted
						if (rowsCreated <= 1) {
							// no text was extracted so delete the file
							FileUtils.forceDelete(extractedFile); // delete the csv
							FileUtils.forceDelete(document); // delete the input file e.g pdf
							continue;
						}

						classLogger.info("Creating chunks from extracted text for " + documentName);

						StringBuilder splitTextCommand = new StringBuilder();
						splitTextCommand.append("vector_database.split_text(csv_file_location = '")
						.append(extractedFileName)
						.append("', chunk_unit = '")
						.append(chunkUnit)
						.append("', chunk_size = ")
						.append(chunkMaxTokenLength)
						.append(", chunk_overlap = ")
						.append(tokenOverlapBetweenChunks)
						.append(", chunking_strategy = ")
						.append(chunkingStrategy)
						.append(", cfg_tokenizer = cfg_tokenizer)");
						
						pyt.runScript(splitTextCommand.toString());

						// this needs to match the column created in the new CSV
						columnsToIndex = "['Content']"; 
					} else {
						// copy csv over
						FileUtils.copyFileToDirectory(document, tableIndexFolder);
						if (parameters.containsKey(VectorDatabaseParamOptionsEnum.COLUMNS_TO_INDEX.getKey())) {
							columnsToIndex = PyUtils.determineStringType(parameters.get(VectorDatabaseParamOptionsEnum.COLUMNS_TO_INDEX.getKey()));
						} else {
							columnsToIndex = "[]"; // this is so we pass an empty list
						}
					}
					
					// add it to the list of files that need to be pushed to the cloud in a new thread
					filesToCopyToCloud.add(document.getAbsolutePath());
					extractedFiles.add(extractedFile);
				} catch (IOException e) {
					classLogger.error(Constants.STACKTRACE, e);
					throw new IllegalArgumentException("Unable to remove old or create new text extraction file for " + documentName);
				}
			}
			
			// ( embedding, source, modality, divider, part, tokens, content, engineid, keywords, taggigng)
			// TODO s3 holds documents
			
			if (extractedFiles.size() > 0) {
				
				// if we were able to extract files, begin embeddings process
				IModelEngine embeddingsEngine = Utility.getModel(this.embedderEngineId);
				String psString = "INSERT INTO " + vectorTableName +" ( embedding, source, modality, divider, part, tokens, content, engineid, keywords) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
				
				Connection conn = null;
				try {
					conn = this.getConnection();
					PreparedStatement ps = conn.prepareStatement(psString);
					for(int i = 0; i < extractedFiles.size(); i++) {
						File extractedFile = extractedFiles.get(i);
						PgVectorTable dataForTable = readCsv(extractedFile);
						
						if (parameters.containsKey(VectorDatabaseParamOptionsEnum.KEYWORD_SEARCH_PARAM.getKey())) {
							IModelEngine keywordEngine = Utility.getModel(this.keywordGeneratorEngineId);
							dataForTable.setKeywordEngine(keywordEngine);
						}
						
						dataForTable.generateAndAssignEmbeddings(embeddingsEngine, insight);
						
						for (PgVectorRow row: dataForTable.getRows()) {
							int index = 1;
							ps.setObject(index++, row.getEmbeddings());
							ps.setString(index++, row.getSource());
							ps.setString(index++, row.getModality());
							ps.setString(index++, row.getDivider());
							ps.setString(index++, row.getPart());
							ps.setInt(index++, row.getTokens());
							ps.setString(index++, row.getContent());
							ps.setString(index++, this.engineId);
							ps.setString(index++, row.getKeywords());
							ps.addBatch();
						}
						
						int[] results = ps.executeBatch();
			            for(int j=0; j<results.length; j++) {
			                if(results[j] == PreparedStatement.EXECUTE_FAILED) {
			                    throw new SQLException("Error inserting data for row " + j);
			                }
			            }
			            
						if (!conn.getAutoCommit()) {
							conn.commit();
						}
						
						ps.close();
						FileUtils.forceDelete(extractedFile);
					}
				} catch (IOException | SQLException e) {
					classLogger.error(Constants.STACKTRACE, e);					
				} finally {
					ConnectionUtils.closeAllConnectionsIfPooling(this, conn, null, null);
				}
			}
		} finally {
			try {
				FileUtils.forceDelete(tableIndexFolder);
			} catch (IOException e) {
				classLogger.error(Constants.STACKTRACE, e);
			}
		}
	}

	protected PgVectorTable readCsv(File file) throws IOException {
		PgVectorTable pgVectorTable = new PgVectorTable();
		try (Reader reader = Files.newBufferedReader(Paths.get(file.getAbsolutePath()))) {
			try (CSVReader csvReader = new CSVReader(reader)) {
				String[] line;
				boolean start = true;
				Map<String,Integer> headersMap = new HashMap<String, Integer>();
				while ((line = csvReader.readNext()) != null) {
					if(start) {
						for(int i=0;i<line.length;i++) {
							headersMap.put(line[i],i);
						}
						start = false;
					} else {
						pgVectorTable.addRow(line[headersMap.get("Source")], line[headersMap.get("Modality")], line[headersMap.get("Divider")], line[headersMap.get("Part")], line[headersMap.get("Tokens")], line[headersMap.get("Content")]);
					}
				}
			}
		}

		return pgVectorTable;
	}

	@Override
	public void removeDocument(List<String> filePaths, Map<String, Object> parameters) {
		String indexClass = this.defaultIndexClass;
		if (parameters.containsKey("indexClass")) {
			indexClass = (String) parameters.get("indexClass");
		}

		List<String> filesToRemoveFromCloud = new ArrayList<String>();
		
		String deleteQuery = "DELETE from " + this.vectorTableName + " WHERE \"source\"= ? AND ENGINEID = ?";

		Connection conn = null;
		try {
			conn = this.getConnection();
			PreparedStatement ps = conn.prepareStatement(deleteQuery);
			for (String document : filePaths) {
				
				String documentName = Paths.get(document).getFileName().toString();
				// remove the physical documents
				File documentFile = new File(this.schemaFolder.getAbsolutePath() + DIR_SEPARATOR + indexClass + DIR_SEPARATOR + "documents", documentName);
				if (documentFile.exists()) {
					FileUtils.forceDelete(documentFile);
					filesToRemoveFromCloud.add(documentFile.getAbsolutePath());
				}
				
				// remove the results from the db
				int parameterIndex = 1;
				ps.setString(parameterIndex++, documentName);
				ps.setString(parameterIndex++, this.engineId);
				ps.addBatch();
				
			}
			
			int[] results = ps.executeBatch();
            for(int j=0; j<results.length; j++) {
                if(results[j] == PreparedStatement.EXECUTE_FAILED) {
                    throw new SQLException("Error inserting data for row " + j);
                }
            }
            
			if (!conn.getAutoCommit()) {
				conn.commit();
			}
	
			ps.close();
		} catch (IOException | SQLException e) {
			classLogger.error(Constants.STACKTRACE, e);
			
			// TODO add error throwing
		} finally {
			ConnectionUtils.closeAllConnectionsIfPooling(this, conn, null, null);
		}

		if (ClusterUtil.IS_CLUSTER) {
			Thread deleteFilesFromCloudThread = new Thread(new DeleteFilesFromEngineRunner(engineId, this.getCatalogType(), filesToRemoveFromCloud.stream().toArray(String[]::new)));
			deleteFilesFromCloudThread.start();
		}
	}

	@Override
	public Object nearestNeighbor(String question, Number limit, Map<String, Object> parameters) {
		if (!this.modelPropsLoaded) {
			verifyModelProps();
		}
		
		if (this.embedderEngineId == null) {
			throw new IllegalArgumentException("Please fedine the embeding model in the database smss");
		}
		
		Insight insight = getInsight(parameters.remove(AbstractVectorDatabaseEngine.INSIGHT));
		if (insight == null) {
			throw new IllegalArgumentException("Insight must be provided to run Model Engine Encoder");
		}
		
		String searchFilters = "None";
		if (parameters.containsKey("filters")) {}

		if (parameters.containsKey(VectorDatabaseParamOptionsEnum.COLUMNS_TO_RETURN.getKey())) {}

		if (parameters.containsKey(VectorDatabaseParamOptionsEnum.RETURN_THRESHOLD.getKey())) {}

		if (parameters.containsKey(VectorDatabaseParamOptionsEnum.ASCENDING.getKey())) {}

		
		IModelEngine engine = Utility.getModel(this.embedderEngineId);
		EmbeddingsModelEngineResponse embeddingsResponse = engine.embeddings(Arrays.asList(new String[] {question}), insight, null);

		StringBuilder searchQueryBuilder = new StringBuilder("SELECT ");
		searchQueryBuilder.append("\"source\" AS \"Source\",")
						  .append("\"modality\" AS \"Modality\",")
						  .append("\"divider\" AS \"Divider\",")
						  .append("\"part\" AS \"Part\",")
						  .append("\"tokens\" AS \"Tokens\",")
						  .append("\"content\" AS \"Content\",")
						  .append("1-(\"embedding\" <=> '"+embeddingsResponse.getResponse().get(0)+"') AS Score")
						  .append(" FROM ")
						  .append(this.vectorTableName)
						  .append(" WHERE ENGINEID = '")
						  .append(this.engineId)
						  .append("' ORDER BY Score desc LIMIT ")
						  .append(limit);
		
		HardSelectQueryStruct qs = new HardSelectQueryStruct();
		qs.setEngine(this);
		qs.setEngineId(this.engineId);
		qs.setQsType(QUERY_STRUCT_TYPE.RAW_ENGINE_QUERY);		
		qs.setQuery(searchQueryBuilder.toString());
		
		return QueryExecutionUtility.flushRsToMap(this, qs);
	}

	@Override
	public List<Map<String, Object>> listDocuments(Map<String, Object> parameters) {
		String indexClass = this.defaultIndexClass;
		if (parameters.containsKey("indexClass")) {
			indexClass = (String) parameters.get("indexClass");
		}

		File documentsDir = new File(this.schemaFolder.getAbsolutePath() + DIR_SEPARATOR + indexClass + DIR_SEPARATOR + "documents");

		List<Map<String, Object>> fileList = new ArrayList<>();

		File[] files = documentsDir.listFiles();
		if (files != null) {
			for (File file : files) {
				String fileName = file.getName();
				long fileSizeInBytes = file.length();
				double fileSizeInMB = (double) fileSizeInBytes / (1024);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String lastModified = dateFormat.format(new Date(file.lastModified()));

				Map<String, Object> fileInfo = new HashMap<>();
				fileInfo.put("fileName", fileName);
				fileInfo.put("fileSize", fileSizeInMB);
				fileInfo.put("lastModified", lastModified);
				fileList.add(fileInfo);
			}
		} 

		return fileList;
	}
	
	@Override
	public IEngine.CATALOG_TYPE getCatalogType() {
		return IEngine.CATALOG_TYPE.VECTOR;
	}
	
	@Override
	public VectorDatabaseTypeEnum getVectorDatabaseType() {
		return VectorDatabaseTypeEnum.PGVECTOR;
	}

	@Override
	public void close() throws IOException {
		if(this.cpw != null) {
			this.cpw.shutdown(true);
		}
		
		super.close();
	}

	protected void checkSocketStatus() {
		if(this.cpw == null || this.cpw.getSocketClient() == null || !this.cpw.getSocketClient().isConnected()) {
			this.startServer(-1);
		}
	}

	protected Insight getInsight(Object insightObj) {
		if (insightObj instanceof String) {
			return InsightStore.getInstance().get((String) insightObj);
		} else {
			return (Insight) insightObj;
		}
	}
	
	public class PgVectorTable {
		
		public class PgVectorRow {
			
			private PGvector embeddings = null; // This could be a placeholder or identifier for actual embeddings
			private String source;
			private String modality;
			private String divider;
			private String part;
			private Integer tokens;
			private String content;
			private String keywords = "";

	        public PgVectorRow(String source, String modality, String divider, String part, int tokens, String content) {
	            // Initially, embeddings might not be set
	            this.source = source;
	            this.modality = modality;
	            this.divider = divider;
	            this.part = part;
	            this.tokens = tokens;
	            this.content = content;
	        }

	        // Method to update the embeddings for a row
	        public void setEmbeddings(PGvector embeddings) {
	            this.embeddings = embeddings;
	        }
	        
	        public PGvector getEmbeddings() {
	            return this.embeddings;
	        }
	        
	        public String getSource() {
	        	return this.source;
	        }
	        
	        public String getModality() {
	        	return this.modality;
	        }
	        
	        public String getDivider() {
	        	return this.divider;
	        }
	        
	        public String getPart() {
	        	return this.part;
	        }

	        public Integer getTokens() {
	        	return this.tokens;
	        }
	        
	        public String getContent() {
	        	return this.content;
	        }
	        
	        public void setKeywords(String keywords) {
	            this.keywords = keywords;
	        }
	        
	        public String getKeywords() {
	            return this.keywords;
	        }
	    }

	    protected List<PgVectorRow> rows;
	    private IModelEngine keywordEngine = null;
		private int maxKeywords = 12;
		private int percentile = 0;
		
	    public PgVectorTable() {
	        this.rows = new ArrayList<>();
	    }

	    public void addRow(String source, String modality, String divider, String part, int tokens, String content) {
	    	PgVectorRow newRow = new PgVectorRow(source, modality, divider, part, tokens, content);
	        this.rows.add(newRow);
	    }
	    
	    public void addRow(String source, String modality, String divider, String part, String tokens, String content) {
	    	PgVectorRow newRow = new PgVectorRow(source, modality, divider, part, Double.valueOf(tokens).intValue(), content);
	        this.rows.add(newRow);
	    }
	            
	    public List<String> getAllContent() {
	        List<String> contents = new ArrayList<>();
	        for (PgVectorRow row : rows) {
	            contents.add(row.content);
	        }
	        return contents;
	    }
	    
	    public List<PgVectorRow> getRows() {
	    	return this.rows;
	    }
	    
	    public void setKeywordEngine(IModelEngine keywordEngine) {
            this.keywordEngine = keywordEngine;
        }
        
        public IModelEngine getKeywordEngine() {
            return this.keywordEngine;
        }
	    
	    public void generateAndAssignEmbeddings(IModelEngine modelEngine, Insight insight) {
	    	List<String> stringsToEmbed = this.getAllContent();
	    	
	    	if (this.keywordEngine != null) {
	    		
	    		Map<String, Object> keywordEngineParams = new HashMap<>();
	    		keywordEngineParams.put("max_keywords", maxKeywords);
	    		keywordEngineParams.put("percentile", percentile);
	    		
	    		@SuppressWarnings({"unchecked" })
				List<String> keywordsFromChunks = (List<String>) this.keywordEngine.model(stringsToEmbed, insight, keywordEngineParams); 		
	    		
	    		for (int i = 0; i < this.rows.size(); i++) {
	    			String keywordChunk = keywordsFromChunks.get(i);
	    			
	    			if (keywordChunk != null && !(keywordChunk=keywordChunk.trim()).isEmpty()) {
	    				this.rows.get(i).setKeywords(keywordChunk);
	    				stringsToEmbed.add(i, keywordChunk);
	    			}
	    		}
	    	}
	    	
			EmbeddingsModelEngineResponse output = modelEngine.embeddings(stringsToEmbed, insight, null);
	    	
			List<List<Double>> vectors = output.getResponse();

			for (int i = 0; i < this.rows.size(); i++) {
				this.rows.get(i).setEmbeddings(new PGvector(vectors.get(i)));
			}
	    }
	}
}
