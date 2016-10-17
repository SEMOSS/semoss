package prerna.algorithm.impl;

public class SqlMaxReactor extends SqlBasicMathReactor {

	/*
	 * Only need to set the Math Routine
	 * Everything else is handled by inheritance
	 */
	public SqlMaxReactor() {
		this.setMathRoutine("MAX");
	}
}
