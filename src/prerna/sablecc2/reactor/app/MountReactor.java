package prerna.sablecc2.reactor.app;

import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.sablecc2.reactor.AbstractReactor;

public class MountReactor extends AbstractReactor {
	
	private static final String CLASS_NAME = MountReactor.class.getName();
	
	// takes in a the name and engine and mounts the engine assets as that variable name in both python and R
	// I need to accomodate for when I should over ride
	// for instance a user could have saved a recipe with some mapping and then later, they would like to use a different mapping

	public MountReactor() {
		this.keysToGet = new String[] {ReactorKeysEnum.PROJECT.getKey(), ReactorKeysEnum.VARIABLE.getKey()};
		this.keyRequired = new int[]{1,1};
	}
	
	@Override
	public NounMetadata execute() {
		organizeKeys();
		
		String projectName = keyValue.get(keysToGet[0]);
		String varName = keyValue.get(keysToGet[1]);

		if(varName == null || projectName == null) {
			throw new IllegalArgumentException("Var or App Name cannot be null ");			
		}
		
		boolean success = insight.getUser().addVarMap(varName, projectName);
		//String varString = insight.getUser().getVarString(false);
		if(!success) {
			throw new IllegalArgumentException("No app with " + projectName + " available ");			
		}
		return new NounMetadata("Successfully mounted '" + varName + "' to app '" + projectName + "'", PixelDataType.CONST_STRING, PixelOperationType.OPERATION);
	}

}
