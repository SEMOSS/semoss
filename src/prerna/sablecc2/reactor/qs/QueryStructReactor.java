package prerna.sablecc2.reactor.qs;

import java.util.List;
import java.util.Vector;

import prerna.query.interpreters.QueryStruct2;
import prerna.query.interpreters.QueryStructSelector;
import prerna.sablecc2.om.GenRowStruct;
import prerna.sablecc2.om.NounMetadata;
import prerna.sablecc2.om.PkslDataTypes;
import prerna.sablecc2.reactor.AbstractReactor;

/**
 * This is the base class for any reactor responsible for building a querystruct
 * 
 * The design of this class is as such:
 * 		This class holds a query struct, responsible for grabbing input and passing output
 * 		any subclass is only responsible for building the query struct through method createQueryStruct()
 * 		Ex:
 * 			SelectReactor overrides createQueryStruct() build on top of the existing query struct with only Selectors
 * 			JoinReactor overrides createQueryStruct() builds on top of the existing query struct with only joins
 * 
 * 			QueryStructReactor is responsible for grabbing any input querystructs and passing the output
 */
public abstract class QueryStructReactor extends AbstractReactor {

	protected QueryStruct2 qs;
	protected String[] selectorAlias;
	
	// method to override in the specific qs classes
	abstract QueryStruct2 createQueryStruct();

	@Override
	public Object Out() {
		init();
		return this.parentReactor;
	}

	@Override
	public NounMetadata execute() {
		//build the query struct
		QueryStruct2 qs = createQueryStruct();
		setAlias(selectorAlias);
		//create the output and return
		NounMetadata noun = new NounMetadata(qs, PkslDataTypes.QUERY_STRUCT);
		return noun;
	}
	
	public void setAs(String[] asName) {
		this.selectorAlias = asName;
	}
	
	//initialize the reactor with its necessary inputs
	private void init() {
		// this will happen when we have an explicit querystruct
		// or one result piped a query struct to the current reactor
		GenRowStruct qsInputParams = getNounStore().getNoun(PkslDataTypes.QUERY_STRUCT.toString());
		if(qsInputParams != null) {
			int numInputs = qsInputParams.size();
			for(int inputIdx = 0; inputIdx < numInputs; inputIdx++) {
				NounMetadata qsNoun = (NounMetadata)qsInputParams.getNoun(inputIdx);
				QueryStruct2 qs = (QueryStruct2) qsNoun.getValue();
				mergeQueryStruct(qs);
			}
		}
		
		// if it is not piped
		// but there is a query struct within a query struct
		// the specific instance of the reactor will handle those types of merges
		// example
		// selector ( studio , sum(mb) ) 
		// the selector reactor will handle putting the studio and the sum(mb)
		
		if(this.qs == null) {
			this.qs = new QueryStruct2();
		}
	}
	
	//method to merge an outside query struct with this query struct
	protected void mergeQueryStruct(QueryStruct2 queryStruct) {
		if(this.qs == null) {
			this.qs = queryStruct;
		} else {
			this.qs.merge(queryStruct);
		}
	}
	
	private void setAlias(String[] selectorAlias2) {
		/*
		 * Since multiple select reactors can each have an alias
		 * we need to go back and add alias to the last selectors that were added
		 * based on the index of the alias we are adding
		 */
		if(selectorAlias != null) {
			List<QueryStructSelector> selectors = this.qs.selectors;
			int numSelectors = selectors.size();
			int numAlias = selectorAlias.length;
			int startingPoint = numSelectors-numAlias;
			int counter = 0;
			for(int i = startingPoint; i < numSelectors; i++) {
				selectors.get(i).setAlias(selectorAlias[counter]);
				counter++;
			}
		}
	}
	
	@Override
	public List<NounMetadata> getOutputs() {
		// all of the classes return the same thing
		// which is a QS
		// this works because even if execute hasn't occured yet
		// because the same preference exists for the qs
		// and since out is called prior to update the planner
		// the qs cannot be null
		List<NounMetadata> outputs = new Vector<NounMetadata>();
		NounMetadata output = new NounMetadata(this.qs, PkslDataTypes.QUERY_STRUCT);
		outputs.add(output);
		return outputs;
	}
}
