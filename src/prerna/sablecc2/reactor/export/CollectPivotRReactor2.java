package prerna.sablecc2.reactor.export;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import prerna.ds.r.RSyntaxHelper;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.sablecc2.om.task.ConstantDataTask;
import prerna.sablecc2.reactor.frame.r.util.AbstractRJavaTranslator;
import prerna.sablecc2.reactor.task.TaskBuilderReactor;
import prerna.util.Utility;

public class CollectPivotRReactor2 extends TaskBuilderReactor {

	/**
	 * This class is responsible for collecting data from a task and returning it
	 */

	private static Map<String, String> mathMap = new HashMap<String, String>();
	static {
		mathMap.put("Sum", "sum");
		mathMap.put("Average", "mean");
		mathMap.put("Min", "min");
		mathMap.put("Max", "max");
		mathMap.put("Median", "median");
		mathMap.put("StandardDeviation", "sd");
	};

	public CollectPivotRReactor2() {
		this.keysToGet = new String[] { ReactorKeysEnum.ROW_GROUPS.getKey(), ReactorKeysEnum.COLUMNS.getKey(), ReactorKeysEnum.VALUES.getKey() };
	}

	public NounMetadata execute() {
		this.task = getTask();

		AbstractRJavaTranslator rJavaTranslator = this.insight.getRJavaTranslator(this.getLogger(this.getClass().getName()));
		rJavaTranslator.startR(); 

		// going to do this with r datatable directly
		//  cubed <- data.table::cube(mv, .(budget=sum(MovieBudget), revenue=mean(RevenueDomestic)), by=c('Genre', 'Studio'))
		

		String fileName = Utility.getRandomString(6);
		String dir = (insight.getUserFolder() + "/Temp").replace('\\', '/');
		File tempDir = new File(dir);
		if(!tempDir.exists()) {
			tempDir.mkdir();
		}
		String outputFile = dir + "/" + fileName + ".csv";
		Utility.writeResultToFile(outputFile, this.task, ",");

		// so this is going to come in as vectors
		List<String> rowGroups = this.store.getNoun(keysToGet[0]).getAllStrValues();
		List<String> colGroups = this.store.getNoun(keysToGet[1]).getAllStrValues();
		List<String> values = this.store.getNoun(keysToGet[2]).getAllStrValues();

		// convert the inputs into a cgroup
		String rows = "by = " + RSyntaxHelper.createStringRColVec(rowGroups);
		
		rows = rows.substring(0, rows.length()-1);
		
		// we need to add this to the rows
		// that is how r data table works
		for(int colIndex = 0;colIndex < colGroups.size();colIndex++)
			rows = rows + ", \"" + colGroups.get(colIndex) + "\"";
		
		rows = rows + ")";
		

		// last piece is the calculations
		// not putting headers right now
		List<Map<String, String>> valuesList = new Vector<Map<String, String>>();

		StringBuilder calcs = new StringBuilder(".(");
		for(int calcIndex = 0; calcIndex < values.size(); calcIndex++) {
			Map<String, String> valueMap = new HashMap<String, String>();
			String curValue = values.get(calcIndex);
			
			// get the operator and selector
			//String [] composite = curValue.split("(");
			String operator = curValue.substring(0, curValue.indexOf("(")).trim();
			String operand = curValue.substring(curValue.indexOf("(") + 1, curValue.length()-1).trim();
			
			
			if(calcIndex != 0)
				calcs.append(",");
			
			for (Map.Entry<String, String> mathElement : mathMap.entrySet()) {
				String key = (String) mathElement.getKey();
				String value = (String) mathElement.getValue();

				operator = operator.replace(key, value);
			}

			//budget=sum(MovieBudget)
			calcs.append(operand).append("_").append(operator).append("=");
			calcs.append(operator).append("(as.double(").append(operand).append("))");
						
			valueMap.put("alias", operand);
			valueMap.put("math", operator);
			valuesList.add(valueMap);
		}
		calcs.append(")");

		String pivotName = "pivot" + Utility.getRandomString(5);
		String htmlName = pivotName + ".html";

		// load html
		StringBuilder pivoter = new StringBuilder("library(xtable);");
		pivoter.append(RSyntaxHelper.getFReadSyntax(fileName, outputFile, ","));
		pivoter.append(pivotName + " <- data.table::cube(" + fileName + "," + calcs + "," + rows + ");");

		// make the pivot
		rJavaTranslator.runR(pivoter.toString());
		// get the output
		String htmler = "print(xtable(" + pivotName + "), type=\"html\");";
		String htmlOutput = rJavaTranslator.runRAndReturnOutput(htmler);

		// delete the variable and pivot
		rJavaTranslator.runR("rm(" + pivotName + "," + fileName + ");");
		File outputF = new File(outputFile);
		outputF.delete();

		// need to create a pivot map for the FE
		Map<String, Object> pivotMap = new HashMap<String, Object>();
		pivotMap.put(keysToGet[0], rowGroups);
		pivotMap.put(keysToGet[1], colGroups);
		pivotMap.put(keysToGet[2], valuesList);
		
		ConstantDataTask cdt = new ConstantDataTask();
		// need to do all the sets
		cdt.setFormat("TABLE");
		cdt.setTaskOptions(task.getTaskOptions());
		cdt.setHeaderInfo(task.getHeaderInfo());
		cdt.setSortInfo(task.getSortInfo());
		cdt.setId(task.getId());

		// set the output so it can give it
		Map<String, Object> outputMap = new HashMap<String, Object>();
		outputMap.put("headers", new String[] {});
		outputMap.put("rawHeaders", new String[] {});
		outputMap.put("values", new String[] {htmlOutput});
		outputMap.put("pivotData", pivotMap);
		cdt.setOutputData(outputMap);
		return new NounMetadata(cdt, PixelDataType.FORMATTED_DATA_SET, PixelOperationType.TASK_DATA);
	}

	@Override
	public List<NounMetadata> getOutputs() {
		List<NounMetadata> outputs = super.getOutputs();
		if(outputs != null && !outputs.isEmpty()) return outputs;

		outputs = new Vector<NounMetadata>();
		NounMetadata output = new NounMetadata(this.signature, PixelDataType.FORMATTED_DATA_SET, PixelOperationType.TASK_DATA);
		outputs.add(output);
		return outputs;
	}

	@Override
	protected void buildTask() {
		// do nothing
		
	}
}
