
package prerna.sablecc2.reactor.qs.source;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;

import prerna.auth.AccessToken;
import prerna.auth.AuthProvider;
import prerna.auth.User2;
import prerna.poi.main.MetaModelCreator;
import prerna.poi.main.helper.CSVFileHelper;
import prerna.query.querystruct.CsvQueryStruct;
import prerna.query.querystruct.SelectQueryStruct;
import prerna.sablecc2.reactor.qs.AbstractQueryStructReactor;
import prerna.security.AbstractHttpHelper;
import prerna.util.Constants;
import prerna.util.DIHelper;
import prerna.util.Utility;

public class OneDriveFileRetrieverReactor extends AbstractQueryStructReactor{

	private static final String CLASS_NAME = OneDriveFileRetrieverReactor.class.getName();

	public OneDriveFileRetrieverReactor() {
		this.keysToGet = new String[] { "id" };
	}



	@Override
	protected SelectQueryStruct createQueryStruct() {
		//get keys
		Logger logger = getLogger(CLASS_NAME);
		organizeKeys();
		String msID = this.keyValue.get(this.keysToGet[0]);
		if (msID == null || msID.length() <= 0) {
			throw new IllegalArgumentException("Need to specify file id");
		}

		//get access token
		String accessToken=null;
		User2 user = this.insight.getUser2();

		try{
			if(user==null){
				Map<String, Object> retMap = new HashMap<String, Object>();
				retMap.put("type", "microsoft");
				retMap.put("message", "Please login to your Microsoft account");
				throwLoginError(retMap);
			}
			else if (user != null) {
				AccessToken msToken = user.getAccessToken(AuthProvider.AZURE_GRAPH.name());
				accessToken=msToken.getAccess_token();
			}
		}
		catch (Exception e) {
			Map<String, Object> retMap = new HashMap<String, Object>();
			retMap.put("type", "microsoft");
			retMap.put("message", "Please login to your Microsoft account");
			throwLoginError(retMap);
		}


		Hashtable params = new Hashtable();
		CsvQueryStruct qs = new CsvQueryStruct();

		try {
			String url_str = "https://graph.microsoft.com/v1.0/me/drive/items/"+msID+"/content";
			BufferedReader br = AbstractHttpHelper.getHttpStream(url_str, accessToken, params, true);

			// create a file
			String filePath = DIHelper.getInstance().getProperty(Constants.INSIGHT_CACHE_DIR) + "\\"
					+ DIHelper.getInstance().getProperty(Constants.CSV_INSIGHT_CACHE_FOLDER);
			filePath += "\\" + Utility.getRandomString(10) + ".csv";
			filePath = filePath.replace("\\", "/");
			File outputFile = new File(filePath);

			BufferedWriter target = new BufferedWriter(new FileWriter(outputFile));
			String data = null;


			while((data = br.readLine()) != null)
			{
				target.write(data);
				target.write("\n");
				target.flush();
			}

			// get datatypes
			CSVFileHelper helper = new CSVFileHelper();
			helper.setDelimiter(',');
			helper.parse(filePath);
			MetaModelCreator predictor = new MetaModelCreator(helper, null);
			Map<String, String> dataTypes = predictor.getDataTypeMap();
			for (String key : dataTypes.keySet()) {
				qs.addSelector("DND", key);
			}
			helper.clear();
			qs.merge(this.qs);
			qs.setCsvFilePath(filePath);
			qs.setDelimiter(',');
			qs.setColumnTypes(dataTypes);
			return qs;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return qs;
	}




}