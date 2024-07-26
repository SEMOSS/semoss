package prerna.io.connector.secrets.hashicorp.vault;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bettercloud.vault.SslConfig;
import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.json.JsonArray;
import com.bettercloud.vault.json.JsonValue;
import com.google.common.primitives.Bytes;
import com.google.gson.JsonObject;

import prerna.engine.api.IEngine;
import prerna.engine.impl.SmssUtilities;
import prerna.io.connector.secrets.ISecrets;
import prerna.security.HttpHelperUtility;
import prerna.test.TestUtilityMethods;
import prerna.util.Constants;
import prerna.util.DIHelper;
import prerna.util.Utility;

public class HashiCorpVaultUtil implements ISecrets {

	private static final Logger classLogger = LogManager.getLogger(HashiCorpVaultUtil.class);

	private static final String VAULT_ADDR = "VAULT_ADDR";
	private static final String VAULT_TOKEN = "VAULT_TOKEN";
	private static final String VAULT_TOKEN_HEADER_KEY = "X-Vault-Token";
	
	// root url for db and projects
	private static final String VAULT_DB_PATH = "VAULT_DB_PATH";
	private static final String VAULT_STORAGE_PATH = "VAULT_STORAGE_PATH";
	private static final String VAULT_MODEL_PATH = "VAULT_MODEL_PATH";
	private static final String VAULT_VECTOR_PATH = "VAULT_VECTOR_PATH";
	private static final String VAULT_FUNCTION_PATH = "VAULT_FUNCTION_PATH";
	private static final String VAULT_PROJECT_PATH = "VAULT_PROJECT_PATH";
	private static final String VAULT_VENV_PATH = "VAULT_VENV_PATH";
	
	private static final String INSIGHT_ENCRYPTION_PATH = "/encrypt";
	
	private static HashiCorpVaultUtil instance;
	
	private Vault vault;
	private VaultConfig config;

	private HashiCorpVaultUtil() throws VaultException {
		createVault();
	}
	
	private void createVault() throws VaultException {
		this.config = new VaultConfig()
				.address(getInput(VAULT_ADDR))			// Defaults to "VAULT_ADDR" environment variable
				.token(getInput(VAULT_TOKEN))			// Defaults to "VAULT_TOKEN" environment variable
				.openTimeout(5)							// Defaults to "VAULT_OPEN_TIMEOUT" environment variable
				.readTimeout(30)						// Defaults to "VAULT_READ_TIMEOUT" environment variable
				.sslConfig(new SslConfig().build())
				.build();
		this.vault = new Vault(this.config);
	}

	public static HashiCorpVaultUtil getInstance() {
		if(instance != null) {
			return instance;
		}

		if(instance == null) {
			synchronized(HashiCorpVaultUtil.class) {
				if(instance == null) {
					try {
						instance = new HashiCorpVaultUtil();
					} catch (VaultException e) {
						classLogger.error(Constants.STACKTRACE, e);
					}
				}
			}
		}

		return instance;
	}

	/**
	 * 
	 * @param eType
	 * @param engineNameAndId
	 * @return
	 */
	private String getPathForEngine(IEngine.CATALOG_TYPE eType, String engineNameAndId) {
		return getBaseForEngine(eType) + "/" + engineNameAndId;
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	private String getBaseForEngine(IEngine.CATALOG_TYPE type) {
		String inputName = getInputNameForEngine(type);
		return getInput(inputName);
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	private String getInputNameForEngine(IEngine.CATALOG_TYPE type) {
		if(IEngine.CATALOG_TYPE.DATABASE == type) {
			return VAULT_DB_PATH;
		} else if(IEngine.CATALOG_TYPE.STORAGE == type) {
			return VAULT_STORAGE_PATH;
		} else if(IEngine.CATALOG_TYPE.MODEL == type) {
			return VAULT_MODEL_PATH;
		} else if(IEngine.CATALOG_TYPE.VECTOR == type) {
			return VAULT_VECTOR_PATH;
		} else if(IEngine.CATALOG_TYPE.FUNCTION == type) {
			return VAULT_FUNCTION_PATH;
		} else if(IEngine.CATALOG_TYPE.VENV == type) {
			return VAULT_VENV_PATH;
		} else if(IEngine.CATALOG_TYPE.PROJECT == type) {
			return VAULT_PROJECT_PATH;
		}
		
		throw new IllegalArgumentException("Unhandled engine type = " + type);

	}
	
	/**
	 * Get the full path for the insight secrets
	 * @param projectPath
	 * @param insightId
	 * @return
	 */
	private String getInsightPath(String projectPath, String insightId) {
		return getPathForEngine(IEngine.CATALOG_TYPE.PROJECT, projectPath) + "/" + insightId;
	}
	
	/**
	 * General method to grab input from environment variable or RDF_Map
	 * @param key
	 * @return
	 */
	private String getInput(String key) {
		String value = System.getenv(key);
		if(value == null || value.isEmpty()) {
			value = DIHelper.getInstance().getProperty(key);
		}

		return value;
	}
	
	@Override
	public Map<String, String> getEngineSecrets(IEngine.CATALOG_TYPE eType, String engineId, String engineName) {
		String secretPath = SmssUtilities.getUniqueName(engineName, engineId);
		secretPath = Utility.encodeURIComponent(secretPath);
		try {
			return this.vault.logical().read(getPathForEngine(eType, secretPath)).getData();
		} catch (VaultException e) {
			classLogger.error(Constants.STACKTRACE, e);
		}

		return null;
	}
	
	@Override
	public Map<String, String> getInsightSecrets(String projectId, String projectName, String insightId) {
		String secretPath = SmssUtilities.getUniqueName(projectName, projectId);
		secretPath = Utility.encodeURIComponent(secretPath);
		try {
			return this.vault.logical().read(getInsightPath(secretPath, insightId)).getData();
		} catch (VaultException e) {
			classLogger.error(Constants.STACKTRACE, e);
		}

		return null;
	}
	
	@Override
	public Map<String, Object> getInsightEncryptionSecrets(String projectId, String projectName, String insightId) {
		String secretPath = SmssUtilities.getUniqueName(projectName, projectId);
		secretPath = Utility.encodeURIComponent(secretPath);
		try {
			com.bettercloud.vault.json.JsonObject jsonObject = this.vault.logical().read(getInsightPath(secretPath, insightId + INSIGHT_ENCRYPTION_PATH)).getDataObject();
			String secret = jsonObject.getString(ISecrets.SECRET);
			String salt = jsonObject.getString(ISecrets.SALT);
			Iterator<JsonValue> ivIterator = jsonObject.get(ISecrets.IV).asArray().iterator();
			List<Byte> iv = new ArrayList<>();
			while(ivIterator.hasNext()) {
				iv.add( (byte) ivIterator.next().asInt());
			}
			Map<String, Object> cacheData = new HashMap<>();
			cacheData.put(ISecrets.SECRET, secret);
			cacheData.put(ISecrets.SALT, salt);
			cacheData.put(ISecrets.IV, Bytes.toArray(iv));
			return cacheData;
		} catch (VaultException e) {
			classLogger.error(Constants.STACKTRACE, e);
		}

		return null;
	}
	
	@Override
	public boolean writeEngineSecret(IEngine.CATALOG_TYPE eType, String engineId, String engineName, String key, Object value) {
		Map<String, Object> nameValuePairs = new HashMap<>();
		nameValuePairs.put(key, value);
		return writeEngineSecrets(eType, engineId, engineName, nameValuePairs);
	}
	
	@Override
	public boolean writeEngineSecrets(IEngine.CATALOG_TYPE eType, String engineId, String engineName, Map<String, Object> nameValuePairs) {
		String secretPath = SmssUtilities.getUniqueName(engineName, engineId);
		secretPath = Utility.encodeURIComponent(secretPath);
		try {
			this.vault.logical().write(getPathForEngine(eType, secretPath), nameValuePairs);
			return true;
		} catch (VaultException e) {
			classLogger.error(Constants.STACKTRACE, e);
			return false;
		}
	}

	@Override
	public boolean writeInsightSecret(String projectId, String projectName, String insightId, String key, Object value) {
		Map<String, Object> nameValuePairs = new HashMap<>();
		nameValuePairs.put(key, value);
		return writeInsightSecrets(insightId, projectName, projectId, nameValuePairs);
	}

	@Override
	public boolean writeInsightSecrets(String projectId, String projectName, String insightId, Map<String, Object> nameValuePairs) {
		String secretPath = SmssUtilities.getUniqueName(projectName, projectId);
		secretPath = Utility.encodeURIComponent(secretPath);
		
		try {
			this.vault.logical().write(getInsightPath(secretPath, insightId), nameValuePairs);
			return true;
		} catch (VaultException e) {
			classLogger.error(Constants.STACKTRACE, e);
			return false;
		}
	}

	@Override
	public boolean writeInsightEncryptionSecrets(String projectId, String projectName, String insightId, Map<String, Object> nameValuePairs) {
		String secretPath = SmssUtilities.getUniqueName(projectName, projectId);
		secretPath = Utility.encodeURIComponent(secretPath);
		byte[] iv = (byte[]) nameValuePairs.get(ISecrets.IV);
		com.bettercloud.vault.json.JsonArray jsonArray = new JsonArray();
		for(byte i : iv) {
			jsonArray.add(i);
		}
		nameValuePairs.put(ISecrets.IV, jsonArray);
		try {
			this.vault.logical().write(getInsightPath(secretPath, insightId + INSIGHT_ENCRYPTION_PATH), nameValuePairs);
			return true;
		} catch (VaultException e) {
			classLogger.error(Constants.STACKTRACE, e);
			return false;
		}
	}
	
	/**
	 * Create an engine KV engine
	 * @throws ParseException
	 * @throws IOException
	 */
	public void createEngineSecretEngine(IEngine.CATALOG_TYPE eType) {
		String lookup = getInputNameForEngine(eType);
		String name = getInput(lookup);
		
		JsonObject json = new JsonObject();
		json.addProperty("type", "kv");
		json.addProperty("description", "Secrets for " + eType + " smss files");
		JsonObject version = new JsonObject();
		json.addProperty("version", "2");
		json.add("options", version);
		
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put(VAULT_TOKEN_HEADER_KEY, getInput(VAULT_TOKEN));
		
		String response = HttpHelperUtility.postRequestStringBody(getInput(VAULT_ADDR) + "/v1/sys/mounts/"+name,
				headerMap,
				json.toString(), 
				ContentType.APPLICATION_JSON, 
				null, null, null);
		
		classLogger.info("Response for creating " + eType + " = " + response);
	}

	private void unwrapToken(String wrappingToken) throws ClientProtocolException, IOException {
		HttpPost post = new HttpPost(getInput(VAULT_ADDR) + "/v1/sys/wrapping/unwrap");
		post.setHeader(VAULT_TOKEN_HEADER_KEY, wrappingToken);

		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpResponse response = client.execute(post);

		String responseBody = null;
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			responseBody = EntityUtils.toString(entity);
		}

		StatusLine statusLine = response.getStatusLine();
		System.out.println("status line = " + statusLine.getStatusCode());
		System.out.println("response body = " + responseBody);
	}
	
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////

	
	public static void main(String[] args) throws VaultException, ParseException, IOException {
		TestUtilityMethods.loadDIHelper();
		
		HashiCorpVaultUtil instance = HashiCorpVaultUtil.getInstance();
		instance.createEngineSecretEngine(IEngine.CATALOG_TYPE.DATABASE);
		
		Map<String, Object> nameValuePairs = new HashMap<>();
		nameValuePairs.put("PASSWORD","password");
		instance.writeEngineSecrets(IEngine.CATALOG_TYPE.DATABASE, "fe5e2c23-59e6-42ae-939d-b2ca9699f38c", "test-name", nameValuePairs);
		Map<String, String> dbSecrets = instance.getEngineSecrets(IEngine.CATALOG_TYPE.DATABASE, "fe5e2c23-59e6-42ae-939d-b2ca9699f38c", "test-name");
		System.out.println(dbSecrets);
	}

}
