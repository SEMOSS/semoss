package prerna.sablecc.expressions.sql;

import prerna.ds.H2.H2Frame;
import prerna.sablecc.expressions.IExpressionSelector;
import prerna.sablecc.expressions.sql.builder.SqlExpressionBuilder;
import prerna.sablecc.expressions.sql.builder.SqlMathSelector;

public abstract class H2SqlBasicMathReactor extends AbstractH2SqlBaseReducer {

	public SqlExpressionBuilder process(H2Frame frame, SqlExpressionBuilder builder) {
		IExpressionSelector previousSelector = builder.getLastSelector();
		SqlMathSelector newSelector = new SqlMathSelector(previousSelector, this.mathRoutine, this.pkqlMathRoutine);
		builder.replaceSelector(previousSelector, newSelector);
		return builder;
	}
	
}