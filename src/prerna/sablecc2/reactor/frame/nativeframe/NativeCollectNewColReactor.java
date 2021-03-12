package prerna.sablecc2.reactor.frame.nativeframe;

import java.util.List;
import java.util.Vector;

import prerna.ds.OwlTemporalEngineMeta;
import prerna.ds.nativeframe.NativeFrame;
import prerna.query.querystruct.SelectQueryStruct;
import prerna.query.querystruct.selectors.IQuerySelector;
import prerna.query.querystruct.transform.QSAliasToPhysicalConverter;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.sablecc2.om.task.BasicIteratorTask;
import prerna.sablecc2.reactor.imports.NativeImporter;
import prerna.sablecc2.reactor.task.TaskBuilderReactor;

public class NativeCollectNewColReactor extends TaskBuilderReactor {

	public NativeCollectNewColReactor() {
		this.keysToGet = new String[] { ReactorKeysEnum.QUERY_STRUCT.getKey() };
	}
	
	public NounMetadata execute() {
		String warning = null;
		
		if(! ((this.task=getTask()) instanceof BasicIteratorTask)) {
			throw new IllegalArgumentException("Can only add a new column using a basic query on a frame");
		}
		
		// get the query struct
		SelectQueryStruct sqs = ((BasicIteratorTask) this.task).getQueryStruct();
		NativeFrame frame = (NativeFrame) sqs.getFrame();
		
		OwlTemporalEngineMeta metadata = frame.getMetaData();
		SelectQueryStruct pqs = null;

		try {
			// convert to to the physical structure
			pqs = QSAliasToPhysicalConverter.getPhysicalQs(sqs, metadata);
		} catch(Exception ex) {
			return getWarning("Calculation is using columns that do not exist in the frame. Cannot perform this operation");
		}

		if(pqs.getCombinedFilters().getFilters() != null && pqs.getCombinedFilters().getFilters().size() > 0 ) {
			warning = "You are applying a calculation while there are filters, this is not recommended and can lead to unpredictable results";
			pqs.ignoreFilters = true;
		}

		List<NounMetadata> outputs = new Vector<NounMetadata>();
		// there should be only one selector
		List <IQuerySelector> allSelectors = sqs.getSelectors();
		if(allSelectors.size() > 0) {
			NativeImporter importer;
			try {
				// set the engine id for the sqs to be that of the native frame
				pqs.setEngineId(frame.getEngineId());
				// now we can import without the importer needed to be modified
				importer = new NativeImporter(frame, pqs, ((BasicIteratorTask) task).getIterator());
				importer.insertData();
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalArgumentException(e.getMessage());
			}
			
			outputs.add(new NounMetadata("Added Col " + allSelectors.get(0).getAlias(), PixelDataType.CONST_STRING));
			outputs.add(new NounMetadata(frame, PixelDataType.FRAME, PixelOperationType.FRAME_HEADERS_CHANGE));
			if(warning != null) {
				outputs.add(getWarning(warning));
			}
		} else {
			outputs.add(new NounMetadata("No New Columns to add", PixelDataType.CONST_STRING));
		}

		return new NounMetadata(outputs, PixelDataType.VECTOR, PixelOperationType.VECTOR);
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
