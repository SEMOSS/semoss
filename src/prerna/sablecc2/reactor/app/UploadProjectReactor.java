package prerna.sablecc2.reactor.app;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import prerna.auth.AuthProvider;
import prerna.auth.User;
import prerna.auth.utils.AbstractSecurityUtils;
import prerna.auth.utils.SecurityQueryUtils;
import prerna.auth.utils.SecurityUpdateUtils;
import prerna.cluster.util.ClusterUtil;
import prerna.engine.impl.SmssUtilities;
import prerna.nameserver.DeleteFromMasterDB;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.om.execptions.SemossPixelException;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.sablecc2.reactor.app.upload.UploadInputUtility;
import prerna.sablecc2.reactor.app.upload.UploadUtilities;
import prerna.sablecc2.reactor.insights.AbstractInsightReactor;
import prerna.util.Constants;
import prerna.util.DIHelper;
import prerna.util.Utility;
import prerna.util.ZipUtils;

public class UploadProjectReactor extends AbstractInsightReactor {
	
	private static final String CLASS_NAME = UploadProjectReactor.class.getName();

	public UploadProjectReactor() {
		this.keysToGet = new String[] { ReactorKeysEnum.PROJECT.getKey(), ReactorKeysEnum.FILE_PATH.getKey(), ReactorKeysEnum.SPACE.getKey() };
	}

	@Override
	public NounMetadata execute() {
		organizeKeys();
		Logger logger = this.getLogger(CLASS_NAME);
		int step = 1;
		String zipFilePath = UploadInputUtility.getFilePath(this.store, this.insight);
		// check security
		// Need to check this, will the same methods work/enhanced to check the permissions on project?
		User user = this.insight.getUser();
		boolean security = AbstractSecurityUtils.securityEnabled();
		if (security) {
			if (user == null) {
				NounMetadata noun = new NounMetadata(
						"User must be signed into an account in order to create or update an app",
						PixelDataType.CONST_STRING, PixelOperationType.ERROR, PixelOperationType.LOGGIN_REQUIRED_ERROR);
				SemossPixelException err = new SemossPixelException(noun);
				err.setContinueThreadOfExecution(false);
				throw err;
			}

			// throw error if user is anonymous
			if (AbstractSecurityUtils.anonymousUsersEnabled() && user.isAnonymous()) {
				throwAnonymousUserError();
			}

			// throw error is user doesn't have rights to publish new apps
			if (AbstractSecurityUtils.adminSetPublisher() && !SecurityQueryUtils.userIsPublisher(user)) {
				throwUserNotPublisherError();
			}
		}

		// creating a temp folder to unzip project folder and smss
		String temporaryProjectId = UUID.randomUUID().toString();
		String projectFolderPath = DIHelper.getInstance().getProperty(Constants.BASE_FOLDER) + DIR_SEPARATOR + "project";
		String tempProjectFolderPath = projectFolderPath + DIR_SEPARATOR + temporaryProjectId;
		File tempProjectFolder = new File(tempProjectFolderPath);

		// gotta keep track of the smssFile and files unzipped
		Map<String, List<String>> filesAdded = new HashMap<>();
		List<String> fileList = new Vector<>();
		String smssFileLoc = null;
		File smssFile = null;
		// unzip files to temp project folder
		boolean error = false;
		try {
			logger.info(step + ") Unzipping app");
			filesAdded = ZipUtils.unzip(zipFilePath, tempProjectFolderPath);
			logger.info(step + ") Done");
			step++;
			
			// look for smss file
			fileList = filesAdded.get("FILE");
			logger.info(step + ") Searching for smss");
			for (String filePath : fileList) {
				if (filePath.endsWith(Constants.SEMOSS_EXTENSION)) {
					smssFileLoc = tempProjectFolderPath + DIR_SEPARATOR + filePath;
					smssFile = new File(Utility.normalizePath(smssFileLoc));
					// check if the file exists
					if (!smssFile.exists()) {
						// invalid file need to delete the files unzipped
						smssFileLoc = null;
					}
					break;
				}
			}
			logger.info(step + ") Done");
			step++;


			// delete the files if we were unable to find the smss file
			if (smssFileLoc == null) {
				throw new SemossPixelException("Unable to find " + Constants.SEMOSS_EXTENSION + " file", false);
			}
		} catch (SemossPixelException e) {
			error = true;
			throw e;
		} catch (Exception e) {
			error = true;
			logger.error(Constants.STACKTRACE, e);
			throw new SemossPixelException("Error occured while unzipping the files", false);
		} finally {
			if(error) {
				cleanUpFolders(null, null, null, null, tempProjectFolder, logger);
			}
		}

		String projects = (String) DIHelper.getInstance().getProjectProperty(Constants.PROJECTS);
		String projectId = null;
		String projectName = null;
		File tempSmss = null;
		File tempEngFolder = null;
		File finalSmss = null;
		File finalEngFolder = null;
		
		try {
			logger.info(step + ") Reading smss");
			Properties prop = Utility.loadProperties(smssFileLoc);
			projectId = prop.getProperty(Constants.PROJECT);
			projectName = prop.getProperty(Constants.PROJECT_ALIAS);
			logger.info(step + ") Done");
			step++;

			// zip file has the smss and project folder on the same level
			// need to move these files around
			String oldProjectFolderPath = tempProjectFolderPath + DIR_SEPARATOR + SmssUtilities.getUniqueName(projectName, projectId);
			tempEngFolder = new File(Utility.normalizePath(oldProjectFolderPath));
			finalEngFolder = new File(Utility.normalizePath(projectFolderPath + DIR_SEPARATOR + SmssUtilities.getUniqueName(projectName, projectId)));
			finalSmss = new File(Utility.normalizePath(projectFolderPath + DIR_SEPARATOR + SmssUtilities.getUniqueName(projectName, projectId) + Constants.SEMOSS_EXTENSION));

			// need to ignore file watcher
			if (!(projects.startsWith(projectId) || projects.contains(";" + projectId + ";") || projects.endsWith(";" + projectId))) {
				String newEngines = projects + ";" + projectId;
				DIHelper.getInstance().setLocalProperty(Constants.ENGINES, newEngines);
			} else {
				SemossPixelException exception = new SemossPixelException(
						NounMetadata.getErrorNounMessage("App ID already exists"));
				exception.setContinueThreadOfExecution(false);
				throw exception;
			}
			// move project folder
			logger.info(step + ") Moving project folder");
			FileUtils.copyDirectory(tempEngFolder, finalEngFolder);
			logger.info(step + ") Done");
			step++;

			// move smss file
			logger.info(step + ") Moving smss file");
			tempSmss = new File(Utility.normalizePath(tempProjectFolder + DIR_SEPARATOR 
					+ SmssUtilities.getUniqueName(projectName, projectId) + Constants.SEMOSS_EXTENSION));
			FileUtils.copyFile(tempSmss, finalSmss);
			logger.info(step + ") Done");
			step++;
		} catch (Exception e) {
			error = true;
			logger.error(Constants.STACKTRACE, e);
			throw new SemossPixelException(e.getMessage(), false);
		} finally {
			if(error) {
				DIHelper.getInstance().setLocalProperty(Constants.ENGINES, projects);
				cleanUpFolders(tempSmss, finalSmss, tempEngFolder, finalEngFolder, tempProjectFolder, logger);
			} else {
				// just delete the temp project folder
				cleanUpFolders(null, null, null, null, tempProjectFolder, logger);
			}
		}

		try {
			DIHelper.getInstance().setProjectProperty(projectId + "_" + Constants.STORE, finalSmss.getAbsolutePath());
			logger.info(step + ") Grabbing project insights");
			SecurityUpdateUtils.addProject(projectId, !AbstractSecurityUtils.securityEnabled());
			logger.info(step + ") Done");
		} catch(Exception e) {
			error = true;
			logger.error(Constants.STACKTRACE, e);
			throw new SemossPixelException("Error occured trying to synchronize the metadata and insights for the zip file", false);
		} finally {
			if(error) {
				// delete all the resources
				cleanUpFolders(tempSmss, finalSmss, tempEngFolder, finalEngFolder, tempProjectFolder, logger);
				// remove from DIHelper
				DIHelper.getInstance().setLocalProperty(Constants.ENGINES, projects);
				// delete from local master
				DeleteFromMasterDB lmDeleter = new DeleteFromMasterDB();
				lmDeleter.deleteEngineRDBMS(projectId);
				// delete from security
				SecurityUpdateUtils.deleteDatabase(projectId);
			}
		}
		
		// even if no security, just add user as engine owner
		if (user != null) {
			List<AuthProvider> logins = user.getLogins();
			for (AuthProvider ap : logins) {
				SecurityUpdateUtils.addProjectOwner(projectId, user.getAccessToken(ap).getId());
			}
		}

		ClusterUtil.reactorPushProject(projectId);

		Map<String, Object> retMap = UploadUtilities.getProjectReturnData(this.insight.getUser(), projectId);
		return new NounMetadata(retMap, PixelDataType.UPLOAD_RETURN_MAP, PixelOperationType.MARKET_PLACE_ADDITION);	
	}
	
	/**
	 * Utility method to delete resources that have to be cleaned up
	 * @param tempSmss
	 * @param finalSmss
	 * @param tempEngDir
	 * @param finalEngDir
	 * @param tempDbDir
	 * @param logger
	 */
	private void cleanUpFolders(File tempSmss, File finalSmss, File tempEngDir, File finalEngDir, File tempDbDir, Logger logger) {
		if(tempSmss != null && tempSmss.exists()) {
			try {
				FileUtils.forceDelete(tempSmss);
			} catch (IOException e) {
				logger.error(Constants.STACKTRACE, e);
			}
		}
		if(finalSmss != null && finalSmss.exists()) {
			try {
				FileUtils.forceDelete(finalSmss);
			} catch (IOException e) {
				logger.error(Constants.STACKTRACE, e);
			}
		}
		if(tempEngDir != null && tempEngDir.exists()) {
			try {
				FileUtils.deleteDirectory(tempEngDir);
			} catch (IOException e) {
				logger.error(Constants.STACKTRACE, e);
			}
		}
		if(finalEngDir != null && finalEngDir.exists()) {
			try {
				FileUtils.deleteDirectory(finalEngDir);
			} catch (IOException e) {
				logger.error(Constants.STACKTRACE, e);
			}
		}
		if(tempDbDir != null && tempDbDir.exists()) {
			try {
				FileUtils.deleteDirectory(tempDbDir);
			} catch (IOException e) {
				logger.error(Constants.STACKTRACE, e);
			}
		}
	}

}
