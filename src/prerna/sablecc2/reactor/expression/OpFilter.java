package prerna.sablecc2.reactor.expression;

import java.util.List;

import prerna.sablecc2.om.Filter2;
import prerna.sablecc2.om.NounMetadata;
import prerna.sablecc2.om.PkslDataTypes;
import prerna.sablecc2.reactor.JavaExecutable;
import prerna.sablecc2.reactor.qs.QueryFilterReactor;

public class OpFilter extends OpBasic {

	@Override
	protected NounMetadata evaluate(Object[] values) {
		if(this.parentReactor instanceof QueryFilterReactor) {
			// we want to return a filter object
			// so it can be integrated with the query struct
			Filter2 filter = new Filter2(this.nouns[0], values[1].toString().trim(), this.nouns[2]);
			return new NounMetadata(filter, PkslDataTypes.FILTER);
		}
		
		// there are 3 things being passed
		// index 0 is left term
		// index 1 is comparator
		// index 2 is right term
		Object left = values[0];
		Object right = values[2];
		String comparator = values[1].toString().trim();
		boolean evaluation = false;
		if(comparator.equals("==")) {
			if(left instanceof Number && right instanceof Number) {
				evaluation = ((Number)left).doubleValue() == ((Number)right).doubleValue();
			} else if(left instanceof String && right instanceof String){
				evaluation = left.toString().equals(right.toString());
			} else {
				evaluation = left == right;
			}
		} else if(comparator.equals("!=") || comparator.equals("<>")) {
			if(left instanceof Number && right instanceof Number) {
				evaluation = ((Number)left).doubleValue() != ((Number)right).doubleValue();
			} else if(left instanceof String && right instanceof String){
				evaluation = !left.toString().equals(right.toString());
			} else {
				evaluation = left != right;
			}
		}
		// we have some numerical stuff
		// everything needs to be a valid number
		else if(comparator.equals(">=")) {
			evaluation = ((Number)left).doubleValue() >= ((Number)right).doubleValue();
		} else if(comparator.equals(">")) {
			evaluation = ((Number)left).doubleValue() > ((Number)right).doubleValue();
		} else if(comparator.equals("<=")) {
			evaluation = ((Number)left).doubleValue() <= ((Number)right).doubleValue();
		} else if(comparator.equals("<")) {
			evaluation = ((Number)left).doubleValue() < ((Number)right).doubleValue();
		} else {
			throw new IllegalArgumentException("Cannot handle comparator " + comparator);
		}
		
		return new NounMetadata(evaluation, PkslDataTypes.BOOLEAN);
	}
	
	public String getJavaSignature() {
		List<NounMetadata> inputs = this.getJavaInputs();
		
		NounMetadata leftSide = inputs.get(0);
		Object leftSideValue = leftSide.getValue();
		String leftString;
		if(leftSideValue instanceof JavaExecutable) {
			leftString = ((JavaExecutable)leftSideValue).getJavaSignature();
		} else if(leftSide.getNounName() == PkslDataTypes.CONST_STRING) {
			leftString = "\""+leftSideValue.toString()+"\"";
		} else {
			leftString = leftSideValue.toString();
		}
		
		String comparator = inputs.get(1).getValue().toString();
		
		NounMetadata rightSide = inputs.get(2);
		Object rightSideValue = rightSide.getValue();
		String rightString;
		if(rightSideValue instanceof JavaExecutable) {
			rightString = ((JavaExecutable)rightSideValue).getJavaSignature();
		} else if(rightSide.getNounName() == PkslDataTypes.CONST_STRING) {
			rightString = "\""+rightSideValue.toString()+"\"";
		} else {
			rightString = rightSideValue.toString();
		}
		
		
		return leftString + " " +comparator + " " + rightString;

	}

	@Override
	public String getReturnType() {
		// TODO Auto-generated method stub
		return "boolean";
	}
}
