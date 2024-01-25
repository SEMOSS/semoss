package prerna.engine.impl.vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;

import prerna.ds.py.TCPPyTranslator;
import prerna.reactor.frame.gaas.processors.DocProcessor;
import prerna.reactor.frame.gaas.processors.PDFProcessor;
import prerna.reactor.frame.gaas.processors.PPTProcessor;
import prerna.reactor.frame.gaas.processors.TextFileProcessor;
import prerna.util.Constants;

public class FaissDatabaseUtils {
	private static final Logger classLogger = LogManager.getLogger(FaissDatabaseUtils.class);
	
	public static int convertFilesToCSV(String csvFileName, int contentLength, int contentOverlap, File file, String faissDbVarName, TCPPyTranslator vectorPyt) throws IOException {
		VectorDatabaseCSVWriter writer = new VectorDatabaseCSVWriter(csvFileName);
		writer.setTokenLength(contentLength);
		writer.overlapLength(contentOverlap);
		writer.setFaissDbVarName(faissDbVarName);
		writer.setPyTranslator(vectorPyt);

		classLogger.info("Starting file conversions ");
		List <String> processedList = new ArrayList<String>();

		// pick up the files and convert them to CSV
		

		classLogger.info("Processing file : " + file.getName());
		
		Path filePath = Paths.get(file.getAbsolutePath());
		// process this file
		String mimeType = null;
		
		//using tika for mime type check since it is more consistent across env + rhel OS and macOS
		Tika tika = new Tika();

		try (FileInputStream inputstream = new FileInputStream(file)) {
			mimeType = tika.detect(inputstream, new Metadata());
		} catch (IOException e) {
			classLogger.error(Constants.ERROR_MESSAGE, e);
        }
		
		if(mimeType != null) {
			classLogger.info("Processing file : " + file.getName() + " mime type: " + mimeType);
			if(mimeType.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
			{
				// document
				DocProcessor dp = new DocProcessor(file.getAbsolutePath(), writer);
				dp.process();
				processedList.add(file.getAbsolutePath());
			}
			else if(mimeType.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.presentationml.presentation"))
			{
				// powerpoint
				PPTProcessor pp = new PPTProcessor(file.getAbsolutePath(), writer);
				pp.process();
				processedList.add(file.getAbsolutePath());
			}
			else if(mimeType.equalsIgnoreCase("application/pdf"))
			{
				PDFProcessor pdf = new PDFProcessor(file.getAbsolutePath(), writer);
				pdf.process();
				processedList.add(file.getAbsolutePath());
			}
			else if(mimeType.equalsIgnoreCase("text/plain"))
			{
				TextFileProcessor text = new TextFileProcessor(file.getAbsolutePath(), writer);
				text.process();
				processedList.add(file.getAbsolutePath());
			}
			else
			{
				classLogger.warn("We Currently do not support mime-type " + mimeType);
			}
			classLogger.info("Completed Processing file : " + file.getAbsolutePath());
		
		}
		writer.close();
		
		return writer.getRowsInCsv();
	}
	
	public static boolean verifyFileTypes(List<String> newFilesPaths, List<String> filesInDocumentsFolder) {
		
		/*
		 * First Check
		 * Make sure the csv files aren't sent with non csv files
		 * TODO refine checks here
		*/
		Set<String> newFileTypes = extractFileTypesFromPaths(newFilesPaths);
        boolean newFilesAreCsv;
        if (newFileTypes.contains("csv") && newFileTypes.size() == 1) {
        	newFilesAreCsv = true;
        } else {
        	newFilesAreCsv = false;
        }
		
        // TODO update this to do a headers check. Maybe the can do the pre-processing beforehand
        // cant process csvs unless the have the same headers
        if (newFileTypes.contains("csv") && !newFilesAreCsv) {
        	return false;
        }
        
		/*
		 * Second Check
		 * Make sure we arent trying to add csv to a mixed file type class 
		 * TODO refine checks here once they above is addressed
		*/
        Set<String> currentFileTypes = extractFileTypesFromPaths(filesInDocumentsFolder);
        
        if (currentFileTypes.size() == 0) {
        	return true;
        }
        
        
        if (newFilesAreCsv && !currentFileTypes.contains("csv")) {
        	return false;
        }
        
		/*
		 * Third Check
		 * Make sure we are not adding mixed files to just csv class
		 * TODO refine checks here once they above is addressed
		*/
        if (!newFilesAreCsv && currentFileTypes.contains("csv") && currentFileTypes.size() == 1) {
        	return false;
        }
		
		return true;
	}
	
	public static Set<String> extractFileTypesFromPaths(List<String> filePaths) {
        Set<String> fileTypes = new HashSet<>();
        for (String filePath : filePaths) {
            // Find the last dot (.) in the file path
            int lastDotIndex = filePath.lastIndexOf(".");
            
            if (lastDotIndex >= 0) {
                // Extract the file extension
                String fileType = filePath.substring(lastDotIndex + 1).toLowerCase();
                fileTypes.add(fileType);
            }
        }
        return fileTypes;
    }
	
	public static Set<String> createKeywordsFromChunks(List<String> filePaths, TCPPyTranslator vectorPyt) {
        Set<String> fileTypes = new HashSet<>();
        for (String filePath : filePaths) {
            // Find the last dot (.) in the file path
            int lastDotIndex = filePath.lastIndexOf(".");
            
            if (lastDotIndex >= 0) {
                // Extract the file extension
                String fileType = filePath.substring(lastDotIndex + 1).toLowerCase();
                fileTypes.add(fileType);
            }
        }
        return fileTypes;
    }
}
