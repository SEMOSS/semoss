package prerna.sablecc2.reactor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import prerna.sablecc2.om.NounMetadata;
import prerna.sablecc2.om.PkslDataTypes;

public class Assimilator extends AbstractReactor {

	// roles of the assimilator is simple, just assimilate an expression and then
	// plug it into the parent
	// filter is a good example of assimilator for example

	private boolean containsStringValue = false;
	private boolean allIntValue = true;

	@Override
	public void In() {
		curNoun("all");
	}

	@Override
	public Object Out() {
		// in the translation flow
		// execute will run and send back
		// the data to set into the parent
		return parentReactor;
	}

	@Override
	public NounMetadata execute() {
		modifySignatureFromLambdas();
		
		// need to see if we are dealing with any non-integer values
		if(this.curRow.getNounsOfType(PkslDataTypes.CONST_DECIMAL).size() > 0) {
			this.allIntValue = false;
		}
		
		// noun object to return
		// need to cast to get the type of the NounMetadata object
		NounMetadata noun = null;

		// get the assimilator evaluator
		// this is the class we are going to be using to execute
		// if we are running this again, we will not create the class and add
		// it to the ClassPool, but if it is new, we will
		AssimilatorEvaluator newInstance = getAssimilatorEvaluator();
		// set the values into the new instance's var map
		// this is implemented this way so we can re-use the class
		// even if a few variables are changed
		setInstanceVariables(newInstance);
		Object retVal = newInstance.execute();
		if(newInstance.containsStringValue) {
			noun = new NounMetadata(retVal.toString(), PkslDataTypes.CONST_STRING);
		} else if(allIntValue) {
			Number result = (Number) retVal;
			if(result.doubleValue() == Math.rint(result.doubleValue())) {
				noun = new NounMetadata( ((Number) retVal).intValue(), PkslDataTypes.CONST_INT);
			} else {
				// not a valid integer
				// return as a double
				noun = new NounMetadata( ((Number) retVal).doubleValue(), PkslDataTypes.CONST_DECIMAL);
			}
		} else {
			noun = new NounMetadata( ((Number) retVal).doubleValue(), PkslDataTypes.CONST_DECIMAL);
		}

		return noun;
	}

	/**
	 * 
	 * @param evaluator
	 * 
	 * This method sets the values to the evaluator through the abstract
	 */
	private void setInstanceVariables(AssimilatorEvaluator evaluator) {
		List<String> inputColumns = curRow.getAllColumns();
		// these input columns should be defined at the beginning of the expression
		// technically, someone can use the same variable multiple times
		// so need to account for this
		// ... just add them to a set and call it a day
		Set<String> uniqueInputs = new HashSet<String>();
		uniqueInputs.addAll(inputColumns);
		for(String input : uniqueInputs) {
			NounMetadata data = planner.getVariableValue(input);			
			PkslDataTypes dataType = data.getNounName();
			if(dataType == PkslDataTypes.CONST_DECIMAL) {
				this.allIntValue = false;
				evaluator.setVar(input, data.getValue());
			} else if(dataType == PkslDataTypes.CONST_INT) {
				evaluator.setVar(input, data.getValue());
			} else if(dataType == PkslDataTypes.CONST_STRING) {
				evaluator.containsStringValue = true;
				evaluator.setVar(input, data.getValue());
			} else if(dataType == PkslDataTypes.LAMBDA){
				// in case the variable points to another reactor
				// that we need to get the value from
				// evaluate the lambda
				// object better be a reactor to run
				Object rVal = data.getValue();
				if(rVal instanceof IReactor) {
					NounMetadata newNoun = ((IReactor) rVal).execute(); 
					PkslDataTypes newDataType = data.getNounName();
					if(newDataType == PkslDataTypes.CONST_DECIMAL) {
						evaluator.setVar(input, newNoun.getValue());
					} else if(newDataType == PkslDataTypes.CONST_STRING) {
						evaluator.containsStringValue = true;
						evaluator.setVar(input, newNoun.getValue());
					}
				} else {
					// this should never ever happen....
					throw new IllegalArgumentException("Assimilator cannot handle this type if input");
				}
			} else {
				throw new IllegalArgumentException("Assimilator can currently only handle outputs of scalar variables");
			}
		}
	}

	/**
	 * 
	 * method to get the run time class for the assimilator
	 */
	private AssimilatorEvaluator getAssimilatorEvaluator() {
		AssimilatorEvaluator evaluator;

		//stringified method for the evaluator
		String stringMethod = buildMethodString();
		//id for this particular assimilator
		String classId = "$Assimilator"+stringMethod.hashCode();
		//if we have an existing one, grab that
		if(this.planner.hasVariable(classId)) {
			evaluator = (AssimilatorEvaluator)this.planner.getVariable(classId).getValue();
		} 

		//otherwise build a new one
		else {
			evaluator = buildAssimilatorEvaluator(stringMethod);
			NounMetadata newEvaluator = new NounMetadata(evaluator, PkslDataTypes.CACHED_CLASS);
			this.planner.addVariable(classId, newEvaluator);
		}

		return evaluator;
	}

	/**
	 * 
	 * @param stringMethod
	 * @return
	 * 
	 * method responsible for building a new assimilator class from a stringified method
	 */
	private AssimilatorEvaluator buildAssimilatorEvaluator(String stringMethod) {
		// evaluate the assimilator as an object
		ClassMaker maker = new ClassMaker();

		// add a super so we have a base method to execute
		maker.addSuper("prerna.sablecc2.reactor.AssimilatorEvaluator");
		maker.addMethod(stringMethod);
		Class newClass = maker.toClass();

		try {
			AssimilatorEvaluator newInstance = (AssimilatorEvaluator) newClass.newInstance();
			return newInstance;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * method responsible for building the method
	 */
	private String buildMethodString() {
		// keep a string to generate the method to execute that will
		// return an object that runs the expression
		StringBuilder expressionBuilder = new StringBuilder();
		expressionBuilder.append("public Object execute(){");
		// we need to grab any variables and define them at the top of the method
		appendVariables(expressionBuilder);
		// now that the variables are defined
		// we just want to add the expression as a return
		if(this.containsStringValue) {
			expressionBuilder.append("return new String(").append(this.signature).append("));}");
		} else {
			// multiply by 1.0 to make sure everything is a double...
			// as a pksl data type
			expressionBuilder.append("return new Double(1.0 * ( ").append(this.signature).append("));}");
		}
		return expressionBuilder.toString();
	}

	/**
	 * Append the variables used within the expression
	 * @param expressionBuilder
	 */
	private void appendVariables(StringBuilder expressionBuilder) {
		List<String> inputColumns = curRow.getAllColumns();
		// these input columns should be defined at the beginning of the expression
		// technically, someone can use the same variable multiple times
		// so need to account for this
		// ... just add them to a set and call it a day
		Set<String> uniqueInputs = new HashSet<String>();
		System.out.println("reset");
		uniqueInputs.addAll(inputColumns);
		for(String input : uniqueInputs) {
			NounMetadata data = planner.getVariableValue(input);
			if(data == null) {
				// this only happens when a variable is being used but isn't defined
				throw new IllegalArgumentException("Undefined variable : " + input);
			}
			PkslDataTypes dataType = data.getNounName();
			if(dataType == PkslDataTypes.CONST_DECIMAL) {
				expressionBuilder.append("double ").append(input).append(" = ").append("((Number)super.vars.get("+"\""+input+"\")).doubleValue()").append(";");
			} else if(dataType == PkslDataTypes.CONST_INT) {
				expressionBuilder.append("int ").append(input).append(" = ").append("((Number)super.vars.get("+"\""+input+"\")).intValue()").append(";");
			} else if(dataType == PkslDataTypes.CONST_STRING) {
				this.containsStringValue = true;
				expressionBuilder.append("String ").append(input).append(" = ").append("(String)super.vars.get("+"\""+input+"\")").append(";");
			} else if(dataType == PkslDataTypes.LAMBDA){
				// in case the variable points to another reactor
				// that we need to get the value from
				// evaluate the lambda
				// object better be a reactor to run
				Object rVal = data.getValue();
				if(rVal instanceof IReactor) {
					PkslDataTypes newDataType = data.getNounName();
					if(newDataType == PkslDataTypes.CONST_DECIMAL) {
						expressionBuilder.append("double ").append(input).append(" = ").append("((Number)super.vars.get("+"\""+input+"\")).doubleValue()").append(";");
					} else if(newDataType == PkslDataTypes.CONST_INT) {
						expressionBuilder.append("int ").append(input).append(" = ").append("((Number)super.vars.get("+"\""+input+"\")).intValue()").append(";");
					} else if(newDataType == PkslDataTypes.CONST_STRING) {
						this.containsStringValue = true;
						expressionBuilder.append("String ").append(input).append(" = ").append("(String)super.vars.get("+"\""+input+"\")").append(";");
					}
				} else {
					// this should never ever happen....
					throw new IllegalArgumentException("Assimilator cannot handle this type if input");
				}
			} else {
				throw new IllegalArgumentException("Assimilator can currently only handle outputs of scalar variables");
			}
		}
	}

	@Override
	public List<NounMetadata> getOutputs() {
		List<NounMetadata> outputs = super.getOutputs();
		if(outputs != null) {
			return outputs;
		}

		outputs = new Vector<NounMetadata>();
		NounMetadata output = new NounMetadata(this.signature, PkslDataTypes.LAMBDA);
		outputs.add(output);
		return outputs;
	}
}
