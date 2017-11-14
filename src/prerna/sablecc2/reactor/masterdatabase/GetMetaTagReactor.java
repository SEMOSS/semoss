package prerna.sablecc2.reactor.masterdatabase;

import prerna.nameserver.utility.MasterDatabaseUtility;
import prerna.sablecc2.om.NounMetadata;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.util.Constants;

public class GetMetaTagReactor extends AbstractMetaDBReactor {
	/**
	 * This reactor gets the tags from the concept metadata table
	 * The inputs to the reactor are: 
	 * 1) the engine
	 * 2) the the concept
	 */
	
	@Override
	public NounMetadata execute() {
		String engineName = getEngine();
		String concept = getConcept();
		String tagList = MasterDatabaseUtility.getMetadataValue(engineName, concept, Constants.TAG);
		return new NounMetadata(tagList.split(VALUE_DELIMITER), PixelDataType.CUSTOM_DATA_STRUCTURE, PixelOperationType.CODE_EXECUTION);
	}
}
