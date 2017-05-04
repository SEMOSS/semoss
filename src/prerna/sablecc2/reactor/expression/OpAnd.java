package prerna.sablecc2.reactor.expression;

import prerna.sablecc2.om.NounMetadata;
import prerna.sablecc2.om.PkslDataTypes;

public class OpAnd extends OpBasic {

	@Override
	protected NounMetadata evaluate(Object[] values) {
		boolean result = true;
		for (Object booleanValue : values) {
			// need all values to be true
			// in order to return true
			if(! (boolean) booleanValue) {
				result = false;
				break;
			}
		}

		return new NounMetadata(result, PkslDataTypes.BOOLEAN);
	}
}
