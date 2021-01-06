package prerna.auth.utils.admin.reactors;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import prerna.auth.AuthProvider;
import prerna.auth.User;
import prerna.auth.utils.AbstractSecurityUtils;
import prerna.auth.utils.SecurityAdminUtils;
import prerna.auth.utils.SecurityQueryUtils;
import prerna.engine.impl.rdbms.RDBMSNativeEngine;
import prerna.poi.main.helper.excel.ExcelBlock;
import prerna.poi.main.helper.excel.ExcelRange;
import prerna.poi.main.helper.excel.ExcelSheetFileIterator;
import prerna.poi.main.helper.excel.ExcelSheetPreProcessor;
import prerna.poi.main.helper.excel.ExcelWorkbookFileHelper;
import prerna.poi.main.helper.excel.ExcelWorkbookFilePreProcessor;
import prerna.query.querystruct.ExcelQueryStruct;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.sablecc2.reactor.AbstractReactor;
import prerna.sablecc2.reactor.app.upload.UploadInputUtility;
import prerna.util.Constants;
import prerna.util.Utility;
import prerna.util.sql.AbstractSqlQueryUtil;

public class AdminUploadUsersReactor extends AbstractReactor {

	private static final String CLASS_NAME = AdminUploadUsersReactor.class.getName();
	
	static final String NAME = "NAME";
	static final String EMAIL = "EMAIL";
	static final String TYPE = "TYPE";
	static final String ID = "ID";
	static final String PASSWORD = "PASSWORD";
	static final String SALT = "SALT";
	static final String USERNAME = "USERNAME";
	static final String ADMIN = "ADMIN";
	static final String PUBLISHER = "PUBLISHER";
	
	private static String insertQuery = null;
	private static Map<String, Integer> psIndex = new HashMap<>();
	static {
		String[] headers = new String[] {NAME, EMAIL, TYPE, ID, PASSWORD, SALT,
				USERNAME, ADMIN, PUBLISHER};
		StringBuilder builder = new StringBuilder("INSERT INTO USER (");
		for(int i = 0; i < headers.length; i++) {
			if(i > 0) {
				builder.append(", ");
			}
			builder.append(headers[i]);
		}
		builder.append(") VALUES (");
		for(int i = 0; i < headers.length; i++) {
			if(i > 0) {
				builder.append(", ");
			}
			builder.append("?");
			
			// also keep track of header to index for the file uploading
			psIndex.put(headers[i], (i+1));
		}
		insertQuery = builder.append(")").toString();
	}
	
	private Logger logger = null;
	
	public AdminUploadUsersReactor() {
		this.keysToGet = new String[] {ReactorKeysEnum.FILE_PATH.getKey(), ReactorKeysEnum.SPACE.getKey()};
	}
	
	@Override
	public NounMetadata execute() {
		User user = this.insight.getUser();
		SecurityAdminUtils adminUtils = SecurityAdminUtils.getInstance(user);
		if(adminUtils == null) {
			throw new IllegalArgumentException("User must be an admin to perform this function");
		}

		String filePath = UploadInputUtility.getFilePath(this.store, this.insight);
		File uploadFile = new File(filePath);
		if(!uploadFile.exists() || !uploadFile.isFile()) {
			throw new IllegalArgumentException("Could not find the specified file");
		}

		this.logger = getLogger(CLASS_NAME);

		RDBMSNativeEngine database = (RDBMSNativeEngine) Utility.getEngine(Constants.SECURITY_DB);
		Connection conn = null;
		try {
			conn = database.getConnection();
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
		
		long start = System.currentTimeMillis();
		{
			ExcelSheetFileIterator it = null;
			try {
				it = getExcelIterator(filePath);
				loadExcelFile(conn, database.getQueryUtil(), it);
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalArgumentException("Error loading admin users : " + e.getMessage());
			} finally {
				if(it != null) {
					it.cleanUp();
				}
			}
		}

		try {
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		return new NounMetadata("Time to finish = " + (end - start) + "ms", PixelDataType.CONST_STRING);
	}

	private ExcelSheetFileIterator getExcelIterator(String fileLocation) {
		// get range
		ExcelWorkbookFilePreProcessor processor = new ExcelWorkbookFilePreProcessor();
		processor.parse(fileLocation);
		processor.determineTableRanges();
		Map<String, ExcelSheetPreProcessor> sheetProcessors = processor.getSheetProcessors();
		// get sheetName and headers
		String sheetName = processor.getSheetNames().get(0);
		String range = null;
		ExcelSheetPreProcessor sProcessor = sheetProcessors.get(sheetName);
		{
			List<ExcelBlock> blocks = sProcessor.getAllBlocks();
			// for(int i = 0; i < blocks.size(); i++) {
			ExcelBlock block = blocks.get(0);
			List<ExcelRange> blockRanges = block.getRanges();
			for (int j = 0; j < 1; j++) {
				ExcelRange r = blockRanges.get(j);
				logger.info("Found range = " + r.getRangeSyntax());
				range = r.getRangeSyntax();
			}
		}
		processor.clear();
		
		ExcelQueryStruct qs = new ExcelQueryStruct();
		qs.setSheetName(sheetName);
		qs.setSheetRange(range);
		ExcelWorkbookFileHelper helper = new ExcelWorkbookFileHelper();
		helper.parse(fileLocation);
		ExcelSheetFileIterator it = helper.getSheetIterator(qs);
		
		return it;
	}

	private void loadExcelFile(Connection conn, 
			AbstractSqlQueryUtil queryUtil, 
			ExcelSheetFileIterator helper) throws Exception {

		PreparedStatement ps = conn.prepareStatement(insertQuery);
		String[] excelHeaders = helper.getHeaders();
		List<String> excelHeadersList = Arrays.asList(excelHeaders);

		int idxName = excelHeadersList.indexOf(NAME);
		int idxEmail = excelHeadersList.indexOf(EMAIL);
		int idxType = excelHeadersList.indexOf(TYPE);
		int idxId = excelHeadersList.indexOf(ID);
		int idxPassword = excelHeadersList.indexOf(PASSWORD);
		int idxSalt = excelHeadersList.indexOf(SALT);
		int idxUsername = excelHeadersList.indexOf(USERNAME);
		int idxAdmin = excelHeadersList.indexOf(ADMIN);
		int idxPublisher = excelHeadersList.indexOf(PUBLISHER);

		if(idxName < 0 
				|| idxEmail < 0
				|| idxType < 0
				|| idxId < 0
				|| idxPassword < 0
				|| idxSalt < 0
				|| idxUsername < 0
				|| idxAdmin < 0
				|| idxPublisher < 0
				) {
			throw new IllegalArgumentException("One or more headers are missing from the excel");
		}
		
		int counter = 0;
		Object[] row = null;
		while ((helper.hasNext())) {
			row = helper.next().getRawValues();
			// id could be string or # based on the type
			Object idObj = row[idxId];
			if(idObj == null || idObj.toString().trim().isEmpty()) {
				throw new IllegalArgumentException("Must have the id for the user defined - check row " + counter);
			}
			String id = null;
			if(idObj instanceof Number) {
				id = new BigDecimal( ((Number) idObj).doubleValue() ).toPlainString();
			} else {
				id = idObj + "";
			}
			String name = (String) row[idxName];
			String email = (String) row[idxEmail];
			String type = (String) row[idxType];
			String password = (String) row[idxPassword];
			String salt = (String) row[idxSalt];
			String username = (String) row[idxUsername];
			boolean admin = Boolean.parseBoolean(row[idxAdmin] + "");
			boolean publisher = Boolean.parseBoolean(row[idxPublisher] + "");

			if(id == null || id.isEmpty()) {
				throw new IllegalArgumentException("Must have the id for the user defined - check row " + counter);
			}
			// check if the ID already exists
			if(SecurityQueryUtils.checkUserExist(id)) {
				logger.info("User id = " + id + " alraedy exists - skipping record for upload");
				continue;
			}
			
			if(type == null || type.isEmpty()) {
				throw new IllegalArgumentException("Must have the type of login for the user defined - check row " + counter);
			}
			AuthProvider provider = AuthProvider.valueOf(type.trim().toUpperCase());
			if(provider == null) {
				throw new IllegalArgumentException("Could not determine the correct provider type for " + type + " - check row " + counter);
			}
			
			// these 2 are required
			ps.setString(psIndex.get(TYPE), provider.toString());
			ps.setString(psIndex.get(ID), id.trim());
			// boolean is always true/false and defaults to false for parseBoolean logic
			ps.setBoolean(psIndex.get(ADMIN), admin);
			ps.setBoolean(psIndex.get(PUBLISHER), publisher);

			if(name == null) {
				ps.setNull(psIndex.get(NAME), java.sql.Types.VARCHAR);
			} else {
				ps.setString(psIndex.get(NAME), name.trim());
			}
			
			if(email == null) {
				ps.setNull(psIndex.get(EMAIL), java.sql.Types.VARCHAR);
			} else {
				ps.setString(psIndex.get(EMAIL), email.trim().toLowerCase());
			}
			
			if(username == null) {
				ps.setNull(psIndex.get(USERNAME), java.sql.Types.VARCHAR);
			} else {
				ps.setString(psIndex.get(USERNAME), username.trim());
			}
			
			// if password is there
			// but no salt
			// make the salt and store that
			if(password != null && !password.isEmpty() && salt != null && !salt.isEmpty()) {
				ps.setString(psIndex.get(PASSWORD), password.trim());
				ps.setString(psIndex.get(SALT), salt.trim());
			} else if(password != null && !password.isEmpty() && (salt == null || salt.isEmpty())) {
				String genSalt = AbstractSecurityUtils.generateSalt();
				String hashedPassword = (AbstractSecurityUtils.hash(password, genSalt));
				ps.setString(psIndex.get(PASSWORD), hashedPassword.trim());
				ps.setString(psIndex.get(SALT), genSalt.trim());
			} else {
				ps.setNull(psIndex.get(PASSWORD), java.sql.Types.VARCHAR);
				ps.setNull(psIndex.get(SALT), java.sql.Types.VARCHAR);
			}
			
			ps.addBatch();
			counter++;
		}
		ps.executeBatch();
		logger.info("Done with item type updates , total rows = " + counter);
	}
	
}
