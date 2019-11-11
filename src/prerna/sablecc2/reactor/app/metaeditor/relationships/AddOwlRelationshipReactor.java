package prerna.sablecc2.reactor.app.metaeditor.relationships;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import prerna.engine.api.IEngine;
import prerna.engine.api.impl.util.Owler;
import prerna.sablecc2.om.GenRowStruct;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.sablecc2.reactor.app.metaeditor.AbstractMetaEditorReactor;
import prerna.util.Utility;

public class AddOwlRelationshipReactor extends AbstractMetaEditorReactor {

	private static final String CLASS_NAME = AddOwlRelationshipReactor.class.getName();
	
	/*
	 * This class assumes that the start table, start column, end table, and end column have already been defined
	 */
	
	public AddOwlRelationshipReactor() {
		this.keysToGet = new String[]{ReactorKeysEnum.APP.getKey(), "startT", "endT", "startC", "endC", STORE_VALUES_FRAME};
	}
	
	@Override
	public NounMetadata execute() {
		String appId = getAppInput();
		// we may have the alias
		appId = testAppId(appId, true);
		
		// get tables
		List<String> startTList = getValues(this.keysToGet[1], 1);
		List<String> endTList = getValues(this.keysToGet[2], 2);

		// get columns if exist
		List<String> startCList = getValues(this.keysToGet[3], 3);
		List<String> endCList = getValues(this.keysToGet[4], 4);

		int size = startTList.size();
		if(size != endTList.size() && size != startCList.size() && size != endCList.size()) {
			throw new IllegalArgumentException("Input values are not the same size");
		}
		
		Owler owler = getOWLER(appId);
		// set all the existing values into the OWLER
		// so that its state is updated
		IEngine engine = Utility.getEngine(appId);
		setOwlerValues(engine, owler);
		
		for(int i = 0; i < size; i++) {
			String startT = startTList.get(i);
			String endT = endTList.get(i);
			String startC = startCList.get(i);
			String endC = endCList.get(i);
			// define the rel
			String rel = startT + "." + startC + "." + endT + "." + endC;
			// add the relationship
			owler.addRelation(startT, endT, rel);
		}
		owler.commit();
		
		try {
			owler.export();
		} catch (IOException e) {
			e.printStackTrace();
			NounMetadata noun = new NounMetadata(false, PixelDataType.BOOLEAN);
			noun.addAdditionalReturn(new NounMetadata("An error occured attempting to add the relationships defined", 
					PixelDataType.CONST_STRING, PixelOperationType.ERROR));
			return noun;
		}
		
		// store user inputed values
		storeUserInputs(getLogger(CLASS_NAME), startTList, startCList, endTList, endCList, "added");
		
		NounMetadata noun = new NounMetadata(true, PixelDataType.BOOLEAN);
		noun.addAdditionalReturn(new NounMetadata("Successfully adding relationships", 
				PixelDataType.CONST_STRING, PixelOperationType.SUCCESS));
		return noun;
	}
	
	///////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////
	///////////// GRAB INPUTS FROM PIXEL REACTOR //////////////
	///////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////
	
	public String getAppInput() {
		GenRowStruct grs = this.store.getNoun(this.keysToGet[0]);
		if (grs != null && !grs.isEmpty()) {	
			return grs.get(0).toString();
		}
		
		// if we got to this point
		// it better be single values
		organizeKeys();
		return this.keyValue.get(this.keysToGet[0]);
	}
	
	public List<String> getValues(String keyToGet, int index) {
		List<String> values = new Vector<String>();
		GenRowStruct grs = this.store.getNoun(keyToGet);
		if (grs != null && !grs.isEmpty()) {
			for(int i = 0; i < grs.size(); i++) {
				values.add(grs.get(i).toString());
			}
			return values;
		}
		
		// if we got to this point
		// it better be single values
		organizeKeys();
		values.add(this.keyValue.get(keyToGet));
		return values;
	}
}
