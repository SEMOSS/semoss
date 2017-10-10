package prerna.query.querystruct;

import java.util.List;
import java.util.Vector;

public class QueryArithmeticSelector extends AbstractQuerySelector {

	private IQuerySelector leftSelector;
	private String mathExpr;
	private IQuerySelector rightSelector;
	
	public QueryArithmeticSelector() {
		this.mathExpr = "";
	}

	@Override
	public SELECTOR_TYPE getSelectorType() {
		return SELECTOR_TYPE.ARITHMETIC;
	}

	@Override
	public String getAlias() {
		if(this.alias.equals("")) {
			return this.leftSelector.getAlias()+ "_" + getEnglishForMath() + "_" + this.rightSelector.getAlias();
		}
		return this.alias;
	}

	@Override
	public boolean isDerived() {
		return true;
	}

	@Override
	public String getQueryStructName() {
		return this.leftSelector.getQueryStructName() + this.mathExpr + this.rightSelector.getQueryStructName();
	}
	
	@Override
	public String getDataType() {
		return "NUMBER";
	}
	
	public IQuerySelector getLeftSelector() {
		return leftSelector;
	}

	public void setLeftSelector(IQuerySelector leftSelector) {
		this.leftSelector = leftSelector;
	}

	public String getMathExpr() {
		return mathExpr;
	}

	public void setMathExpr(String mathExpr) {
		this.mathExpr = mathExpr;
	}

	public IQuerySelector getRightSelector() {
		return rightSelector;
	}

	public void setRightSelector(IQuerySelector rightSelector) {
		this.rightSelector = rightSelector;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof QueryArithmeticSelector) {
			QueryArithmeticSelector selector = (QueryArithmeticSelector)obj;
			if(this.leftSelector.equals(selector.leftSelector) &&
					this.rightSelector.equals(selector.rightSelector) &&
					this.mathExpr.equals(selector.mathExpr) &&
					this.alias.equals(selector.alias)) {
					return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		String allString = leftSelector+":::"+this.mathExpr+":::"+this.rightSelector+":::"+alias;
		return allString.hashCode();
	}

	/**
	 * Used for the default alias since most languages will not support
	 * the string version of the math expression (for obvious reasons)
	 * @return
	 */
	private String getEnglishForMath() {
		if(this.mathExpr.equals("+")) {
			return "Plus";
		} else if(this.mathExpr.equals("-")) {
			return "Minus";
		} else if(this.mathExpr.equals("*")) {
			return "MultipiedBy";
		} else if(this.mathExpr.equals("/")) {
			return "DividedBy";
		}
		return "";
	}

	@Override
	public List<QueryColumnSelector> getAllQueryColumns() {
		// grab all the columns from the left selector and the right selector
		List<QueryColumnSelector> usedCols = new Vector<QueryColumnSelector>();
		usedCols.addAll(this.leftSelector.getAllQueryColumns());
		usedCols.addAll(this.rightSelector.getAllQueryColumns());
		return usedCols;
	}
}
