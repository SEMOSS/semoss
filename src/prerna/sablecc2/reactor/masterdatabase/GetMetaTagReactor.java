package prerna.sablecc2.reactor.masterdatabase;

import java.util.ArrayList;
import java.util.List;

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
		List<String> tagList = MasterDatabaseUtility.getMetadataValue(engineName, concept, Constants.TAG);
		ArrayList<ArrayList<String>> history = new ArrayList<ArrayList<String>>();
		for(String tagString:tagList) {
			ArrayList<String> list = new ArrayList<String>();
			for(String tag: tagString.split(VALUE_DELIMITER)) {
				if(tag.length() > 0) {
					list.add(tag);
				}
			}
			history.add(list);
		}
		ArrayList<String> returnValues = new ArrayList<String>();
		if(history.size() > 0) {
			returnValues = history.get(history.size() -1);
		}
		return new NounMetadata(returnValues, PixelDataType.CUSTOM_DATA_STRUCTURE, PixelOperationType.CODE_EXECUTION);
	}
}
