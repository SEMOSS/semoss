package prerna.sablecc2.reactor.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

import prerna.algorithm.api.SemossDataType;
import prerna.auth.AccessToken;
import prerna.auth.AuthProvider;
import prerna.auth.User;
import prerna.engine.api.IHeadersDataRow;
import prerna.om.RemoteItem;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.sablecc2.reactor.task.TaskBuilderReactor;
import prerna.security.AbstractHttpHelper;
import prerna.util.BeanFiller;
import prerna.util.Constants;
import prerna.util.DIHelper;

public class S3UploaderReactor extends TaskBuilderReactor {

	
	private static Properties socialData = null;
	private static final String AWS_ACCOUUNT = "aws_account";
	private static final String AWS_KEY = "aws_key";

	
	public S3UploaderReactor() {
		this.keysToGet = new String[] { "fileName", "bucket", "region" };
	}

	
	private static final String CLASS_NAME = S3UploaderReactor.class.getName();
	private static final String STACKTRACE = "StackTrace: ";

	private String fileLocation = null;
	private Logger logger;
	private String objectName = "prerna.om.RemoteItem"; // it will fill this object and return the data
	private String [] beanProps = {"id", "name", "type"}; // add is done when you have a list
	private String jsonPattern = "[id, name, mimeType]";

	@Override
	public NounMetadata execute() {
		organizeKeys();
		String fileName = keyValue.get(keysToGet[0]);		

		String bucketName = this.keyValue.get(this.keysToGet[1]);
		String clientRegion = this.keyValue.get(this.keysToGet[2]);

		if (fileName == null || fileName.length() <= 0) {
			throw new IllegalArgumentException("Need to specify file name");
		}
		if (bucketName == null || bucketName.length() <= 0) {
			throw new IllegalArgumentException("Need to specify bucket");
		}
		if (clientRegion == null || clientRegion.length() <= 0) {
			throw new IllegalArgumentException("Need to specify region");
		}

		logger = getLogger(CLASS_NAME);
		this.task = getTask();

		// get a random file name
		this.fileLocation = DIHelper.getInstance().getProperty(Constants.BASE_FOLDER) + DIR_SEPARATOR + fileName + ".csv";
		//make file
		buildTask();



		File f = new File(DIHelper.getInstance().getProperty("SOCIAL"));
		FileInputStream fis = null;

		try {
			if(f.exists()) {
				socialData = new Properties();
				fis = new FileInputStream(f);
				socialData.load(fis);
			}
		} catch (FileNotFoundException fnfe) {
			logger.error(STACKTRACE, fnfe);
		} catch (IOException ioe) {
			logger.error(STACKTRACE, ioe);
		} catch (Exception e) {
			logger.error(STACKTRACE, e);
		} finally {
			if(fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.error(STACKTRACE, e);
				}
			}
		}
		
		S3Object fullObject = null;

		String account = socialData.getProperty(AWS_ACCOUUNT);
		String key = socialData.getProperty(AWS_KEY);
		
		File fileToPush = new File(this.fileLocation);

		if (account != null && account.length() > 0 && key != null && key.length() > 0) {
			BasicAWSCredentials awsCreds = new BasicAWSCredentials(account, key);
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
					.withRegion(clientRegion)
					.withCredentials(new AWSStaticCredentialsProvider(awsCreds))
					.build();
			TransferManager xferMgr = TransferManagerBuilder.standard().withS3Client(s3Client).build();
			try {
				Upload xfer = xferMgr.upload(bucketName, fileToPush.getName(), fileToPush);
				try {
					xfer.waitForCompletion();
				} catch (AmazonServiceException e) {
					logger.error("Amazon service error: " + e.getMessage());
				} catch (AmazonClientException e) {
					logger.error("Amazon client error: " + e.getMessage());
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					logger.error("Transfer interrupted: " + e.getMessage());
				}
			} catch (AmazonServiceException e) {
				logger.error(e.getErrorMessage());
				System.exit(1);
			}
			xferMgr.shutdownNow();
		}
		


		return new NounMetadata(true, PixelDataType.BOOLEAN, PixelOperationType.SUCCESS);
	}

	@Override
	protected void buildTask() {
		File f = new File(this.fileLocation);

		try {
			long start = System.currentTimeMillis();

			try {
				f.createNewFile();
			} catch (IOException e) {
				logger.error(STACKTRACE, e);
			}

			FileWriter writer = null;
			BufferedWriter bufferedWriter = null;

			try {
				writer = new FileWriter(f);
				bufferedWriter = new BufferedWriter(writer);

				// store some variables and just reset
				// should be faster than creating new ones each time
				int i = 0;
				int size = 0;
				StringBuilder builder = null;
				// create typesArr as an array for faster searching
				String[] headers = null;
				SemossDataType[] typesArr = null;

				// we need to iterate and write the headers during the first time
				if(this.task.hasNext()) {
					IHeadersDataRow row = this.task.next();
					List<Map<String, Object>> headerInfo = this.task.getHeaderInfo();

					// generate the header row
					// and define constants used throughout like size, and types
					i = 0;
					headers = row.getHeaders();
					size = headers.length;
					typesArr = new SemossDataType[size];
					builder = new StringBuilder();
					for(; i < size; i++) {
						builder.append("\"").append(headers[i]).append("\"");
						if( (i+1) != size) {
							builder.append(",");
						}
						typesArr[i] = SemossDataType.convertStringToDataType(headerInfo.get(i).get("type") + "");
					}
					// write the header to the file
					bufferedWriter.write(builder.append("\n").toString());

					// generate the data row
					Object[] dataRow = row.getValues();
					builder = new StringBuilder();
					i = 0;
					for(; i < size; i ++) {
						if(typesArr[i] == SemossDataType.STRING) {
							builder.append("\"").append(dataRow[i]).append("\"");
						} else {
							builder.append(dataRow[i]);
						}
						if( (i+1) != size) {
							builder.append(",");
						}
					}
					// write row to file
					bufferedWriter.write(builder.append("\n").toString());
				}

				int counter = 1;
				// now loop through all the data
				while(this.task.hasNext()) {
					IHeadersDataRow row = this.task.next();
					// generate the data row
					Object[] dataRow = row.getValues();
					builder = new StringBuilder();
					i = 0;
					for(; i < size; i ++) {
						if(typesArr[i] == SemossDataType.STRING) {
							builder.append("\"").append(dataRow[i]).append("\"");
						} else {
							builder.append(dataRow[i]);
						}
						if( (i+1) != size) {
							builder.append(",");
						}
					}
					// write row to file
					bufferedWriter.write(builder.append("\n").toString());

					if(counter % 10_000 == 0) {
						logger.info("Finished writing line " + counter);
					}
					counter++;
				}

			} catch (IOException e) {
				logger.error(STACKTRACE, e);
			} finally {
				try {
					if(bufferedWriter != null) {
						bufferedWriter.close();
					}
					if(writer != null) {
						writer.close();
					}
				} catch (IOException e) {
					logger.error(STACKTRACE, e);
				}
			}

			long end = System.currentTimeMillis();
			logger.info("Time to output file = " + (end-start) + " ms");
		} catch(Exception e) {
			logger.error(STACKTRACE, e);
			if(f.exists()) {
				f.delete();
			}
			throw new IllegalArgumentException("Encountered error while writing to CSV file");
		}
	}

}
