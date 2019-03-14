package prerna.sablecc2.reactor.algorithms;

import java.util.List;

import prerna.auth.utils.AbstractSecurityUtils;
import prerna.auth.utils.SecurityQueryUtils;
import prerna.ds.r.RSyntaxHelper;
import prerna.nameserver.utility.MasterDatabaseUtility;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.sablecc2.reactor.frame.r.AbstractRFrameReactor;
import prerna.util.DIHelper;
import prerna.util.Utility;

public class UpdateNLPHistoryReactor extends AbstractRFrameReactor {
	
	/**
	 * Returns predicted next word of an NLP Search as String array
	 */
	
	protected static final String CLASS_NAME = NaturalLanguageSearchReactor.class.getName();
	private static final String DIR_SEPARATOR = java.nio.file.FileSystems.getDefault().getSeparator();

	public UpdateNLPHistoryReactor() {
		this.keysToGet = new String[] { };
	}

	@Override
	public NounMetadata execute() {
		init();
		organizeKeys();
		
		// otherwise, proceed with the reactor
		String[] packages = new String[] { "data.table", "stringr" , "stringdist" ,"udpipe", "tokenizers" };
		this.rJavaTranslator.checkPackages(packages);
		
		// Generate string to initialize R console
		StringBuilder sb = new StringBuilder();
		String baseFolder = DIHelper.getInstance().getProperty("BaseFolder");
		String wd = "wd" + Utility.getRandomString(5);
		sb.append(wd + "<- getwd();");
		sb.append(("setwd(\"" + baseFolder + DIR_SEPARATOR + "R" + DIR_SEPARATOR + "AnalyticsRoutineScripts\");").replace("\\", "/"));
		sb.append("source(\"nli_db.R\");");
		sb.append("source(\"refine_nli.R\");");
		sb.append(RSyntaxHelper.loadPackages(packages));
		this.rJavaTranslator.runR(sb.toString());
		
		// get all id's of a user
		List<String> allIds = null;
		if (AbstractSecurityUtils.securityEnabled()) {
			allIds = SecurityQueryUtils.getUserEngineIds(this.insight.getUser());
		}else {
			allIds = MasterDatabaseUtility.getAllEngineIds();
		}
		
		// get matrix of data from local master
		List<Object[]> allTableCols = MasterDatabaseUtility.getAllTablesAndColumns(allIds);
		
		// run script
		generateAndRunScript(allTableCols);
		
		// return data to the front end
		String message = "NLP History successfully updated";
		return new NounMetadata(message, PixelDataType.CONST_STRING);
	}
	
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	
	/**
	 * Generate and run the script, returning array of strings
	 * @param query
	 * @param allTableCols
	 * @return
	 */
	private void generateAndRunScript(List<Object[]> allTableCols) {
		// Getting database info
		String rTempTable = "NaturalLangTable" + Utility.getRandomString(8);
		StringBuilder rsb = new StringBuilder();
		
		String rAppIds = "c(";
		String rTableNames = "c(";
		String rColNames = "c(";
		
		int colCount = allTableCols.size();
		// create R vector of appid, tables, and columns
		for(int i = 0; i < colCount; i++) {
			Object[] entry = allTableCols.get(i);
			String appId = entry[0].toString();
			String table = entry[1].toString();
			String column = entry[2].toString();
			if(i == 0) {
				rAppIds += "'" + appId + "'" ;
				rTableNames += "'" + table + "'" ;
				rColNames += "'" + column + "'" ;
			} else {
				rAppIds += ",'" + appId + "'" ;
				rTableNames += ",'" + table + "'" ;
				rColNames += ",'" + column + "'" ;
			}
		}
		
		rAppIds += ")" ;
		rTableNames += ")";
		rColNames += ")";
		
		rsb.append(rTempTable + " <- data.frame(Column = " + rColNames + " , Table = " + rTableNames + " , AppID = " + rAppIds + ", stringsAsFactors = FALSE);");
		rsb.append("nli_history_mgr(\"nlq_history.txt\"," + rTempTable + ")");
		
		this.rJavaTranslator.runR(rsb.toString());

		// variable cleanup
		this.rJavaTranslator.executeEmptyR("rm( " + rTempTable + " ); gc();");
	}
}