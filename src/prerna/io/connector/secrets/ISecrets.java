package prerna.io.connector.secrets;

import java.util.Map;

import prerna.engine.api.IEngine;

public interface ISecrets {

	String HASHICORP_VAULT = "VAULT";
	String IV = "iv";
	String SECRET = "secret";
	String SALT = "salt";
	
	/**
	 * 
	 * @param eType
	 * @param engineId
	 * @param engineName
	 * @return
	 */
	Map<String, String> getEngineSecrets(IEngine.CATALOG_TYPE eType, String engineId, String engineName);
	
	/**
	 * Get the secrets associated with an insight
	 * @param projectId
	 * @param projectName
	 * @param insightId
	 * @return
	 */
	Map<String, String> getInsightSecrets(String projectId, String projectName, String insightId);

	/**
	 * Get the insight encryption key
	 * @param projectId
	 * @param projectName
	 * @param insightId
	 * @return
	 */
	Map<String, Object> getInsightEncryptionSecrets(String projectId, String projectName, String insightId);
	
	/**
	 * Write a secret key-value pair for an engine
	 * @param eType
	 * @param engineId
	 * @param engineName
	 * @param key
	 * @param value
	 * @return
	 */
	boolean writeEngineSecret(IEngine.CATALOG_TYPE eType, String engineId, String engineName, String key, Object value);

	/**
	 * Write a set of secret key-value pairs for an engine
	 * @param eType
	 * @param engineId
	 * @param engineName
	 * @param nameValuePairs
	 * @return
	 */
	boolean writeEngineSecrets(IEngine.CATALOG_TYPE eType, String engineId, String engineName, Map<String, Object> nameValuePairs);

	/**
	 * Write a secret key-value pair for a insight
	 * @param projectId
	 * @param projectName
	 * @param insightId
	 * @param key
	 * @param value
	 * @return
	 */
	boolean writeInsightSecret(String projectId, String projectName, String insightId, String key, Object value);

	/**
	 * Write a set of secret key-value pairs for a insight
	 * @param projectId
	 * @param projectName
	 * @param insightId
	 * @param nameValuePairs
	 * @return
	 */
	boolean writeInsightSecrets(String projectId, String projectName, String insightId, Map<String, Object> nameValuePairs);

	/**
	 * Write the secret for the insight encryption
	 * @param projectId
	 * @param projectName
	 * @param insightId
	 * @param nameValuePairs
	 * @return
	 */
	boolean writeInsightEncryptionSecrets(String projectId, String projectName, String insightId, Map<String, Object> nameValuePairs);

}