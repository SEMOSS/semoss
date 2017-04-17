package prerna.sablecc2.reactor;

import java.util.List;
import java.util.Vector;

import prerna.sablecc2.om.NounMetadata;
import prerna.sablecc2.om.PkslDataTypes;

/**
 * This reactor is responsible for taking the output of an execution and assigning the result as a variable
 */
public class AssignmentReactor extends AbstractReactor {

	@Override
	public void In() {
		curNoun("all");
	}

	@Override
	public Object Out() {
		NounMetadata result = planner.getVariable("$RESULT");
		if(result != null) {
			planner.addVariable(operationName.toUpperCase(), result);
			planner.removeVariable("$RESULT");
		} else {
			// if we have a constant value
			// it is just set within the curRow
			// this is because it doesn't produce a result
			// as it doesn't go through a reactor
			// and just adds to the cur row
			
			// we use position 1 because position 0 is a constant 
			// which is stored in curRow and matches operationName
			Object constant = this.curRow.get(1);
			PkslDataTypes constantType = this.curRow.getMeta(1);
			result = new NounMetadata(constant, constantType);
			planner.addVariable(operationName.toUpperCase(), result);
		}
		return null;
	}

	private boolean checkVariable(String variableName) {
		//use this method to make sure the variable name doesn't not interfere with frame's headers
		return true;
	}

	@Override
	public List<NounMetadata> getOutputs() {
		// output is the variable name to be referenced
		List<NounMetadata> outputs = new Vector<NounMetadata>();
		NounMetadata output = new NounMetadata(operationName, PkslDataTypes.CONST_STRING);
		outputs.add(output);
		return outputs;
	}
}
