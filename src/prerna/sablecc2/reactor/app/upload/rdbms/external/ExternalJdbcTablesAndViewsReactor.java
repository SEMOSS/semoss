package prerna.sablecc2.reactor.app.upload.rdbms.external;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import prerna.engine.impl.rdbms.RdbmsConnectionHelper;
import prerna.sablecc2.om.GenRowStruct;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.om.execptions.SemossPixelException;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.sablecc2.reactor.AbstractReactor;
import prerna.util.Utility;
import prerna.util.sql.AbstractSqlQueryUtil;
import prerna.util.sql.RdbmsTypeEnum;
import prerna.util.sql.SqlQueryUtilFactory;

public class ExternalJdbcTablesAndViewsReactor extends AbstractReactor {
	
	private static final String CLASS_NAME = ExternalJdbcTablesAndViewsReactor.class.getName();
	
	public ExternalJdbcTablesAndViewsReactor() {
		this.keysToGet = new String[]{ ReactorKeysEnum.CONNECTION_DETAILS.getKey() };
	}

	@Override
	public NounMetadata execute() {
		Logger logger = getLogger(CLASS_NAME);
		
		Map<String, Object> connectionDetails = getConDetails();
		if(connectionDetails != null) {
			String host = (String) connectionDetails.get(AbstractSqlQueryUtil.HOSTNAME);
			if(host != null) {
				String testUpdatedHost = this.insight.getAbsoluteInsightFolderPath(host);
				if(new File(testUpdatedHost).exists()) {
					host = testUpdatedHost;
					connectionDetails.put(AbstractSqlQueryUtil.HOSTNAME, host);
				}
			}
		}
		
		String driver = (String) connectionDetails.get(AbstractSqlQueryUtil.DRIVER_NAME);
		RdbmsTypeEnum driverEnum = RdbmsTypeEnum.getEnumFromString(driver);
		AbstractSqlQueryUtil queryUtil = SqlQueryUtilFactory.initialize(driverEnum);
		
		Connection con = null;
		String connectionUrl = null;
		try {
			connectionUrl = queryUtil.buildConnectionString(connectionDetails);
		} catch (RuntimeException e) {
			throw new SemossPixelException(new NounMetadata("Unable to generation connection url with message " + e.getMessage(), PixelDataType.CONST_STRING, PixelOperationType.ERROR));
		}
		
		try {
			con = AbstractSqlQueryUtil.makeConnection(driverEnum, connectionUrl, 
					(String) connectionDetails.get(AbstractSqlQueryUtil.USERNAME), 
					(String) connectionDetails.get(AbstractSqlQueryUtil.PASSWORD));
		} catch (SQLException e) {
			e.printStackTrace();
			String driverError = e.getMessage();
			String errorMessage = "Unable to establish connection given the connection details.\nDriver produced error: \" ";
			errorMessage += driverError;
			errorMessage += " \"";
			throw new SemossPixelException(new NounMetadata(errorMessage, PixelDataType.CONST_STRING, PixelOperationType.ERROR));
		}
		
		// keep a list of tables and views
		List<String> tables = new ArrayList<String>();
		List<String> views = new ArrayList<String>();

		DatabaseMetaData meta;
		try {
			meta = con.getMetaData();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new SemossPixelException(new NounMetadata("Unable to get the database metadata", PixelDataType.CONST_STRING, PixelOperationType.ERROR));
		}
		
		String catalogFilter = null;
		try {
			catalogFilter = con.getCatalog();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String schemaFilter = RdbmsConnectionHelper.getSchema(meta, con, connectionUrl, driverEnum);
		ResultSet tablesRs;
		try {
			tablesRs = RdbmsConnectionHelper.getTables(con, meta, catalogFilter, schemaFilter, driverEnum);
		} catch (SQLException e) {
			throw new SemossPixelException(new NounMetadata("Unable to get tables and views from database metadata", PixelDataType.CONST_STRING, PixelOperationType.ERROR));
		}
		
		String[] tableKeys = RdbmsConnectionHelper.getTableKeys(driverEnum);
		final String TABLE_NAME_STR = tableKeys[0];
		final String TABLE_TYPE_STR = tableKeys[1];

		try {
			while (tablesRs.next()) {
				String table = tablesRs.getString(TABLE_NAME_STR);
				// this will be table or view
				String tableType = tablesRs.getString(TABLE_TYPE_STR).toUpperCase();
				if(tableType.toUpperCase().contains("TABLE")) {
					logger.info("Found table = " + Utility.cleanLogString(table));
					tables.add(table);
				} else {
					// there may be views built from sys or information schema
					// we want to ignore these
					logger.info("Found view = " + Utility.cleanLogString(table));
					views.add(table);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeRs(tablesRs);
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		logger.info("Done parsing database metadata");
		
		Map<String, List<String>> ret = new HashMap<String, List<String>>();
		ret.put("tables", tables);
		ret.put("views", views);
		return new NounMetadata(ret, PixelDataType.CUSTOM_DATA_STRUCTURE);
	}
	
	/**
	 * Close the result set
	 * @param rs
	 */
	private void closeRs(ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Map<String, Object> getConDetails() {
		GenRowStruct grs = this.store.getNoun(ReactorKeysEnum.CONNECTION_DETAILS.getKey());
		if(grs != null && !grs.isEmpty()) {
			List<Object> mapInput = grs.getValuesOfType(PixelDataType.MAP);
			if(mapInput != null && !mapInput.isEmpty()) {
				return (Map<String, Object>) mapInput.get(0);
			}
		}
		
		List<Object> mapInput = grs.getValuesOfType(PixelDataType.MAP);
		if(mapInput != null && !mapInput.isEmpty()) {
			return (Map<String, Object>) mapInput.get(0);
		}
		
		return null;
	}

}
