package prerna.sablecc2.reactor.imports;

import java.util.List;

import prerna.algorithm.api.ITableDataFrame;
import prerna.ds.OwlTemporalEngineMeta;
import prerna.ds.nativeframe.NativeFrame;
import prerna.ds.r.RDataTable;
import prerna.engine.api.IRawSelectWrapper;
import prerna.engine.impl.rdbms.RDBMSNativeEngine;
import prerna.query.parsers.OpaqueSqlParser;
import prerna.query.querystruct.AbstractQueryStruct.QUERY_STRUCT_TYPE;
import prerna.query.querystruct.HardSelectQueryStruct;
import prerna.query.querystruct.SelectQueryStruct;
import prerna.query.querystruct.transform.QSAliasToPhysicalConverter;
import prerna.sablecc2.om.Join;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.PixelOperationType;
import prerna.sablecc2.om.execptions.SemossPixelException;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.util.Utility;

public class NativeFrameImporter implements IImporter {

	private NativeFrame dataframe;
	private SelectQueryStruct qs;
	
	public NativeFrameImporter(NativeFrame dataframe, SelectQueryStruct qs) {
		this.dataframe = dataframe;
		this.qs = qs;
	}
	
	@Override
	public void insertData() {
		// see if we can parse the query into a valid qs object
		if(this.qs.getQsType() == QUERY_STRUCT_TYPE.RAW_ENGINE_QUERY && 
				this.qs.retrieveQueryStructEngine() instanceof RDBMSNativeEngine) {
			// lets see what happens
			OpaqueSqlParser parser = new OpaqueSqlParser();
//			SqlParser parser = new SqlParser();
			String query = ((HardSelectQueryStruct) this.qs).getQuery();
			try {
				SelectQueryStruct newQs = this.qs.getNewBaseQueryStruct();
				newQs.merge(parser.processQuery(query));
				// we were able to parse successfully
				// override the reference
				this.qs = newQs;
				this.qs.setQsType(QUERY_STRUCT_TYPE.RAW_ENGINE_QUERY);
			} catch (Exception e) {
				// we were not successful in parsing :/
				e.printStackTrace();
			}
		}
		ImportUtility.parseNativeQueryStructIntoMeta(this.dataframe, this.qs);
		this.dataframe.mergeQueryStruct(this.qs);
	}

	@Override
	public void insertData(OwlTemporalEngineMeta metaData) {
		this.dataframe.setMetaData(metaData);
		this.dataframe.mergeQueryStruct(this.qs);
	}

	@Override
	public ITableDataFrame mergeData(List<Join> joins) {
		QUERY_STRUCT_TYPE qsType = this.qs.getQsType();
		if(qsType == QUERY_STRUCT_TYPE.ENGINE && this.dataframe.getEngineName().equals(this.qs.getEngineId())) {
			// this is the case where we can do an easy merge
			ImportUtility.parseNativeQueryStructIntoMeta(this.dataframe, this.qs);
			this.dataframe.mergeQueryStruct(this.qs);
			return this.dataframe;
		} else {
			// this is the case where we are going across databases or doing some algorithm
			// need to persist the data so we can do this
			// we will flush and return a new frame to accomplish this
			return generateNewFrame(joins);
		}
	}
	
	/**
	 * Generate a new frame from the existing native query
	 * @param joins
	 * @return
	 */
	private ITableDataFrame generateNewFrame(List<Join> joins) {
		// first, load the entire native frame into rframe
		SelectQueryStruct nativeQs = this.dataframe.getQueryStruct();
		// need to convert the native QS to properly form the RDataTable
		nativeQs = QSAliasToPhysicalConverter.getPhysicalQs(nativeQs, this.dataframe.getMetaData());
		IRawSelectWrapper nativeFrameIt = this.dataframe.query(nativeQs);
		if(!ImportSizeRetrictions.sizeWithinLimit(nativeFrameIt.getNumRecords())) {
			SemossPixelException exception = new SemossPixelException(
					new NounMetadata("Frame size is too large, please limit the data size before proceeding", 
							PixelDataType.CONST_STRING, 
							PixelOperationType.FRAME_SIZE_LIMIT_EXCEEDED, PixelOperationType.ERROR));
			exception.setContinueThreadOfExecution(false);
			throw exception;
		}
		
		RDataTable rFrame = new RDataTable(Utility.getRandomString(8));
		RImporter rImporter = new RImporter(rFrame, nativeQs, nativeFrameIt);
		rImporter.insertData();
		
		// now, we want to merge this new data into it
		IRawSelectWrapper mergeFrameIt = ImportUtility.generateIterator(this.qs, this.dataframe);
		if(!ImportSizeRetrictions.mergeWithinLimit(rFrame, mergeFrameIt.getNumRecords())) {
			SemossPixelException exception = new SemossPixelException(
					new NounMetadata("Frame size is too large, please limit the data size before proceeding", 
							PixelDataType.CONST_STRING, 
							PixelOperationType.FRAME_SIZE_LIMIT_EXCEEDED, PixelOperationType.ERROR));
			exception.setContinueThreadOfExecution(false);
			throw exception;
		}
		
		rImporter = new RImporter(rFrame, this.qs, mergeFrameIt);
		rImporter.mergeData(joins);
		return rFrame;
	}
}
