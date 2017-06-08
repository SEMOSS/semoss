package prerna.sablecc2.reactor.expression;

import prerna.sablecc2.om.NounMetadata;
import prerna.sablecc2.om.PkslDataTypes;

public class OpOr extends OpBasic {

	@Override
	protected NounMetadata evaluate(Object[] values) {
		boolean result = eval(values);
		return new NounMetadata(result, PkslDataTypes.BOOLEAN);
	}
	
	public boolean eval(Object...values) {
		boolean result = false;
		for (Object booleanValue : values) {
			// need only 1 value to be true
			// in order to return true
			if((boolean) booleanValue) {
				result = true;
				break;
			}
		}
		return result;
	}

	@Override
	public String getReturnType() {
		// TODO Auto-generated method stub
		return "boolean";
	}
}
