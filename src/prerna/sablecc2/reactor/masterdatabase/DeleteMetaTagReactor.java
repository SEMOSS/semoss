package prerna.sablecc2.reactor.masterdatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import prerna.nameserver.utility.MasterDatabaseUtility;
import prerna.sablecc2.om.NounMetadata;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.util.Constants;

public class DeleteMetaTagReactor extends AbstractMetaDBReactor {
	
	/**
	 * This reactor deletes tags in the metadata
	 * The inputs to the reactor are: 
	 * 1) the engine
	 * 2) the the concept
	 * 3) tags to delete
	 */
	
	@Override
	public NounMetadata execute() {
		// retrieve the inputs
		String engine = getEngine();
		String concept = getConcept();
		List<String> valuesToDelete = getValues();

		// get current tags from concept metadata table
		String tagString = MasterDatabaseUtility.getMetadataValue(engine, concept, Constants.TAG);
		ArrayList<String> tagList = new ArrayList<String>(Arrays.asList(tagString.split(VALUE_DELIMITER)));

		// remove input values
		for (String tag : valuesToDelete) {
			tagList.remove(tag);
		}
		// update tag string and update table
		String updatedTags = "";
		for (String tag : tagList) {
			updatedTags += tag + VALUE_DELIMITER;
		}
		boolean deleteUpdate = MasterDatabaseUtility.updateMetaValue(engine, concept, Constants.TAG, updatedTags);
		return new NounMetadata(deleteUpdate, PixelDataType.BOOLEAN, PixelOperationType.CODE_EXECUTION);
	}
}
