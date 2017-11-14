package prerna.sablecc2.reactor.masterdatabase;

import java.util.Vector;

import prerna.nameserver.utility.MasterDatabaseUtility;
import prerna.sablecc2.om.NounMetadata;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.util.Constants;

public class UpdateMetaTagReactor extends AbstractMetaDBReactor {
	/**
	 * This reactor updates tags in the concept metadata table 
	 * The inputs to the reactor are: 
	 * 1) the engine
	 * 2) the the concept
	 * 3) the tags to be updated
	 * 
	 * 	updates the following row in the concept metadata table
	 *  id, tag, tag1:::tag2:::tag3:::...
	 */
	
	@Override
	public NounMetadata execute() {
		String engine = getEngine();
		String concept = getConcept();
		Vector<String> tags = getValues();
		String tagList = "";
		for(String tag: tags) {
			tagList += tag + VALUE_DELIMITER;
		}
		boolean success = MasterDatabaseUtility.updateMetaValue(engine, concept, Constants.TAG, tagList);
		return new NounMetadata(success, PixelDataType.BOOLEAN, PixelOperationType.CODE_EXECUTION);
	}
}
