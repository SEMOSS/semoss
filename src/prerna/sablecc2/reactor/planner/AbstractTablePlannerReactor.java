package prerna.sablecc2.reactor.planner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import prerna.sablecc2.SimpleTable;
import prerna.sablecc2.reactor.AbstractReactor;
import prerna.sablecc2.reactor.TablePKSLPlanner;

public abstract class AbstractTablePlannerReactor extends AbstractReactor {

	/**
	 * CREATE TABLE TESTING(OP VARCHAR(100), NOUN VARCHAR(100), DIRECTION VARCHAR(100), PROCESSED BOOLEAN);
	 * INSERT INTO TESTING (OP, NOUN, DIRECTION, PROCESSED) VALUES ('A=0', '0','IN',FALSE);
	 * INSERT INTO TESTING (OP, NOUN, DIRECTION, PROCESSED) VALUES ('A=0', 'A','OUT',FALSE);
	 * INSERT INTO TESTING (OP, NOUN, DIRECTION, PROCESSED) VALUES ('B=-A', 'A','IN',FALSE);
	 * INSERT INTO TESTING (OP, NOUN, DIRECTION, PROCESSED) VALUES ('B=-A', 'B','OUT',FALSE);
	 * INSERT INTO TESTING (OP, NOUN, DIRECTION, PROCESSED) VALUES ('C=A+B', 'A','IN',FALSE);
	 * INSERT INTO TESTING (OP, NOUN, DIRECTION, PROCESSED) VALUES ('C=A+B', 'B','IN',FALSE);
	 * INSERT INTO TESTING (OP, NOUN, DIRECTION, PROCESSED) VALUES ('C=A+B', 'C','OUT',FALSE);
	 * 
	 * -- GET ROOTS --
	 * SELECT OP, NOUN FROM TESTING WHERE DIRECTION = 'IN' AND NOUN NOT IN (SELECT NOUN FROM TESTING WHERE DIRECTION = 'OUT');
	 * 
	 * -- UPDATE FOR THOSE OPS --
	 * UPDATE TESTING SET PROCESSED=TRUE WHERE OP IN ('A=0');
	 * 
	 * -- GET NEW ROOTS --
	 * SELECT OP FROM TESTING WHERE PROCESSED=FALSE AND NOUN IN(SELECT NOUN FROM TESTING WHERE DIRECTION='OUT' AND PROCESSED='TRUE') MINUS SELECT OP FROM TESTING WHERE DIRECTION='IN' AND NOUN IN(SELECT NOUN FROM TESTING WHERE DIRECTION='OUT' AND PROCESSED=FALSE)
	 * 
	 * -- UPDATE FOR THOSE OPS --
	 * UPDATE TESTING SET PROCESSED=TRUE WHERE OP IN ('B=-A');
	 * 
 	 * -- GET NEW ROOTS --
	 * SELECT OP FROM TESTING WHERE PROCESSED=FALSE AND NOUN IN(SELECT NOUN FROM TESTING WHERE DIRECTION='OUT' AND PROCESSED='TRUE') MINUS SELECT OP FROM TESTING WHERE DIRECTION='IN' AND NOUN IN(SELECT NOUN FROM TESTING WHERE DIRECTION='OUT' AND PROCESSED=FALSE)
	 */
	
	String OPERATION_COLUMN = "OP";
	String NOUN_COLUMN = "NOUN";
	String DIRECTION_COLUMN = "DIRECTION";
	String PROCESSED_COLUMN = "PROCESSED";
	
	String inDirection = "IN";
	String outDirection = "OUT";
	
	String falseProcessed = "FALSE";
	String trueProcessed = "TRUE";
	
	String tempTableName = "TEMP_TABLE";
	
	
	public List<String> collectNextPksls(TablePKSLPlanner planner) {
		List<String> pksls = new ArrayList<>();
		SimpleTable table = planner.getSimpleTable();
		String rootsQuery = getNextQuery(table.getTableName());
		System.out.println(rootsQuery);
		ResultSet results = table.executeQuery(rootsQuery);
		try {
			while(results.next()) {
				pksls.add(results.getString(1)+";");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				results.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return pksls;
	}
	
	public List<String> collectRootPksls(TablePKSLPlanner planner) {
		List<String> pksls = new ArrayList<>();
		SimpleTable table = planner.getSimpleTable();
		String rootsQuery = getRootsQuery(table.getTableName());
		System.out.println(rootsQuery);
		ResultSet results = table.executeQuery(rootsQuery);
		try {
			while(results.next()) {
				pksls.add(results.getString(1)+";");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				results.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return pksls;
	} 
	
	public void updateTable(TablePKSLPlanner planner, List<String> pksls) {
		StringBuilder updateQuery = new StringBuilder();
		updateQuery.append("UPDATE ").append(planner.getSimpleTable().getTableName()).append(" SET PROCESSED=TRUE WHERE OP IN(");
		int size = pksls.size();
		for(int i = 0; i < size; i++) {
			updateQuery.append("'").append(pksls.get(i)).append("'");
			if(i +1 != size) {
				updateQuery.append(",");
			}
		}
		updateQuery.append(")");
		try {
			planner.getSimpleTable().runQuery(updateQuery.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	private String getNextQuery(String tableName) {
//		StringBuilder rootsQuery = new StringBuilder();
//		rootsQuery.append("SELECT ").append(OPERATION_COLUMN);  //SELECT OP
//		rootsQuery.append(" FROM ").append(tableName); //FROM TABLE
//		rootsQuery.append(" WHERE ").append(PROCESSED_COLUMN).append("=").append(falseProcessed); //WHERE PROCESSED=FALSE
//		rootsQuery.append(" AND ");
//		rootsQuery.append(NOUN_COLUMN).append("IN(");
		return "SELECT DISTINCT OP FROM " + tableName + " WHERE PROCESSED=FALSE AND NOUN IN(SELECT NOUN FROM "+tableName+" WHERE DIRECTION='OUT' AND PROCESSED=TRUE) MINUS SELECT OP FROM "+tableName+" WHERE DIRECTION='IN' AND NOUN IN(SELECT NOUN FROM "+tableName+" WHERE DIRECTION='OUT' AND PROCESSED=FALSE)";
	}
	
	private String getRootsQuery(String tableName) {
//		StringBuilder rootsQuery = new StringBuilder();
//		rootsQuery.append("SELECT ").append(OPERATION_COLUMN);  //SELECT OP
//		rootsQuery.append(" FROM ").append(tableName); //FROM TABLE
//		rootsQuery.append(" WHERE ").append(PROCESSED_COLUMN).append("=").append(falseProcessed); //WHERE PROCESSED=FALSE
//		rootsQuery.append(" AND ");
//		rootsQuery.append(NOUN_COLUMN).append("IN(");
		return "SELECT DISTINCT OP, NOUN FROM "+tableName+" WHERE DIRECTION = 'IN' AND NOUN NOT IN (SELECT NOUN FROM "+tableName+" WHERE DIRECTION = 'OUT')";
	}
	
}
