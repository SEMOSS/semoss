package prerna.sablecc2.reactor.export;

import java.util.UUID;

import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.util.Constants;
import prerna.util.DIHelper;

public class ToCsvReactor extends AbstractExportTxtReactor {

	private static final String CLASS_NAME = ToCsvReactor.class.getName();
	
	public ToCsvReactor() {
		this.keysToGet = new String[]{ReactorKeysEnum.TASK.getKey()};
	}
	
	@Override
	public NounMetadata execute() {
		this.logger = getLogger(CLASS_NAME);
		this.task = getTask();
		// set to comma separated
		this.setDelimiter(",");
		// get a random file name
		String randomKey = UUID.randomUUID().toString();
		this.fileLocation = DIHelper.getInstance().getProperty(Constants.BASE_FOLDER) + "\\" + randomKey + ".csv";
		buildTask();
		
		// store it in the insight so the FE can download it
		// only from the given insight
		this.insight.addExportFile(randomKey, this.fileLocation);
		return new NounMetadata(randomKey, PixelDataType.CONST_STRING, PixelOperationType.FILE_DOWNLOAD);
	}

}
