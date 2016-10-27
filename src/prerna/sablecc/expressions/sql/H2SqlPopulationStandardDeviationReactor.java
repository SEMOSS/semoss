package prerna.sablecc.expressions.sql;

public class H2SqlPopulationStandardDeviationReactor extends H2SqlBasicMathReactor {

	/*
	 * Only need to set the Math Routine
	 * Everything else is handled by inheritance
	 */
	public H2SqlPopulationStandardDeviationReactor() {
		this.setMathRoutine("STDDEV_POP");
		this.setPkqlMathRoutine("StandardDeviation");
	}
	
}
