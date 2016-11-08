package prerna.sablecc.expressions.sql;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import prerna.sablecc.PKQLEnum;
import prerna.sablecc.PKQLRunner.STATUS;
import prerna.sablecc.expressions.IExpressionSelector;
import prerna.sablecc.expressions.sql.builder.SqlMathSelector;
import prerna.sablecc.expressions.sql.builder.SqlPowerSelector;

public class SqlPowerReactor extends AbstractSqlExpression {

	@Override
	public Iterator process() {
		super.process();
		IExpressionSelector previousSelector = this.builder.getLastSelector();
		Map<String, Object> options = (Map<String, Object>) myStore.get(PKQLEnum.MATH_PARAM);
		double power = Double.parseDouble(options.get(PKQLEnum.POWER) + "");
		IExpressionSelector base = this.builder.getSelector(0);
		SqlPowerSelector newSelector = new SqlPowerSelector(base, power);
		this.builder.replaceSelector(previousSelector, newSelector);
		
		myStore.put(myStore.get(whoAmI).toString(), this.builder);
		myStore.put("STATUS", STATUS.SUCCESS);

		return null;
	}
}
