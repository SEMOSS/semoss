package prerna.sablecc2.reactor;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import prerna.sablecc2.om.GenRowStruct;
import prerna.sablecc2.om.NounMetadata;
import prerna.sablecc2.om.NounStore;
import prerna.sablecc2.om.GenRowStruct.COLUMN_TYPE;

public class SampleReactor implements IReactor {

	String operationName = null;
	String signature = null;
	String curNoun = null;
	IReactor parentReactor = null;
	IReactor childReactor = null;
	NounStore store = null;
	IReactor.TYPE type = IReactor.TYPE.FLATMAP;
	IReactor.STATUS status = null;
	GenRowStruct curRow = null;
	
	String reactorName = "Sample";
	String [] asName = null;
	Vector <String> outputFields = null;
	
	PKSLPlanner planner = null;
	
	@Override
	public void setPKSL(String operation, String fullOperation) {
		// TODO Auto-generated method stub
		this.operationName = operation;
		this.signature = fullOperation;
	}

	@Override
	public void setParentReactor(IReactor parentReactor) {
		this.parentReactor = parentReactor;
		
	}
	
	@Override
	public String getName()
	{
		return this.reactorName;
	}
	
	@Override
	public void setName(String reactorName)
	{
		this.reactorName = reactorName;
	}
	
	@Override
	public IReactor getParentReactor()
	{
		return this.parentReactor;
	}

	@Override
	public void curNoun(String noun)
	{
		this.curNoun = noun;
		curRow = makeNoun(noun);
	}
	
	// gets the current noun
	@Override
	public GenRowStruct getCurRow()
	{
		return curRow;
	}

	@Override
	public void closeNoun(String noun)
	{
		this.curRow = null;
	}
	
	@Override
	public void setChildReactor(IReactor childReactor) {
		// TODO Auto-generated method stub
		this.childReactor = childReactor;

	}

	@Override
	public NounStore getNounStore() {
		// TODO Auto-generated method stub
		// I need to see if I have a child
		// if the child
		if(this.store == null)
			store = new NounStore(operationName);
		return store;
	}

	@Override
	public Vector<NounMetadata> getInputs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getErrorMessage() {
		// TODO Auto-generated method stub

	}

	@Override
	public STATUS getStatus() {
		// TODO Auto-generated method stub
		return this.status;
	}

	@Override
	public TYPE getType() {
		// TODO Auto-generated method stub
		return this.type;
	}

	@Override
	public void setAs(String [] asName)
	{
		this.asName = asName;
		// need to set this up on planner as well
		//if(outputFields == null)
			outputFields = new Vector();
		outputFields.addAll(Arrays.asList(asName));
		// re add this to output
		planner.addOutputs(signature, outputFields, type);
	}
	
	public void In()
	{
		// set the stores and such
		System.out.println("Calling the in of" + operationName);
        curNoun("all");
		//if(parentReactor != null && parentReactor.getName().equalsIgnoreCase("EXPR"))
	}
	
	public Object Out()
	{
		System.out.println("Calling the out of" + operationName);
		System.out.println("Calling the out of " + reactorName);
		// if the operation is fully sql-able etc
		// and it is a map operation.. in this place it should merge with the parent
		// there are 2 cases here
		// a. I can assimilate with the parent and let it continue to rip
		// b. I have to finish processing this before I give it off to parent
		
		// additionally.. if I do see a parent reactor.. I should add this as the input to the parent
		// so that we know what order to execute it
		
		updatePlan();
		
		if(this.type != IReactor.TYPE.REDUCE && this.store.isSQL())
		{
			// 2 more scenarios here
			// if parent reactor is not null
			// merge
			// if not execute it
			// if the whole thing is done through SQL, then just add the expression
			if(this.parentReactor != null)
			{
				mergeUp();
				return parentReactor;
			}
			// else assimilated with the other execute
/*			else
			{
				// execute it
			}
*/		
		}
		// the case below should not actually happen.. it should be done through the script chain
		else if(parentReactor == null)
		{
			// execute it
			//return execute();
		}
		else if(parentReactor != null) return parentReactor;
		// else all the merging has already happened
		return null;
	}
	
	// need a merge nounstore
	// this logic should sit inside the reactor not in state
	// this will be abstract eventually
	public void mergeUp()
	{
		// looks at parent and then whatever this needs to do to merge
		// for instance when encountered in an expression
		// this should just make the expression and bind it in
		// not sure how the execution works yet
		System.out.println("Call for merging..");
		
		// this is actually fairly simple to do now
		// pick each one of the genrowstruct and merge it
		// first is overall noun rows
		// should we maintain the sequence here again ?
		// or let it be ?
		// may be keep the sequence
		// should the child come first or the parent ?
		// so many questions 
		// should be the parent
		// not going to keep count for now
		Enumeration <String> curReactorKeys = store.nounRow.keys();
		// if you want to keep order.. should be the work of the reactor
		while(curReactorKeys.hasMoreElements())
		{
			String thisNoun = curReactorKeys.nextElement();
			GenRowStruct output = store.nounRow.get(thisNoun);
			parentReactor.getNounStore().addNoun(thisNoun, output);
		}
		// For expression also add the fact that this will be 
		GenRowStruct exprStruct = new GenRowStruct();

		exprStruct.addColumn(signature);
		// p is for projection
		parentReactor.getNounStore().addNoun(NounStore.projector, exprStruct);
	}
	
	// execute it
	// once again this would be abstract
	public Object execute()
	{
		System.out.println("Execute the method.. " + signature);
		System.out.println("Printing NOUN Store so far.. " + store);
		return null;
	}
	
	public void setPKSLPlanner(PKSLPlanner planner)
	{
		this.planner = planner;
	}
	
	public String[] getPKSL()
	{
		String [] output = new String[2];
		output[0] = operationName;
		output[1] = signature;
		
		return output;
	}
	
	public Vector<String> getOutputs()
	{
		return this.outputFields;
	}
	
	// get noun
	private GenRowStruct makeNoun(String noun)
	{
		GenRowStruct newRow = null;
		getNounStore();
		newRow = store.makeNoun(noun);
		return newRow;
	}
	
	public void updatePlan()
	{
		// add the inputs from the store as well as this operation
		// first is all the inputs
		Enumeration <String> keys = store.nounRow.keys();
		
		
		String reactorOutput = reactorName;
		
		while(keys.hasMoreElements())
		{
			String singleKey = keys.nextElement();
			GenRowStruct struct = store.nounRow.get(singleKey);
			Vector <String> inputs = struct.getType(COLUMN_TYPE.COLUMN);
			
			// need a better way to do it
			if(asName == null)
				reactorOutput = reactorOutput + "_" + struct.getColumns();

			planner.addInputs(signature, inputs, type);
		}
		
		// give it a variable name
		if(asName == null)
			asName = new String[]{reactorOutput};
			
		// I also need to accomodate when this happens in a chain
		// this has to happen directly through the as reactor
		// couple of things here
		// the output fields have to be specified
		// i.e. the reactor should update the output fields
		// if not, it is assumed that it is same as input field ? possibly ?
		// it can never be the same as input field
		// not sure what is the point then

		// it is very much dependent on the operation
		
		// if not the as should take care of it ?
		if(outputFields == null)
		{
			outputFields = new Vector<String>();
			outputFields.add(asName[0]); // for this reactor there is always only 1
		}
		// second is all the outputs
		// need to think about this a bit more
		// should I add it to the parent or the parent will add it by itself ?
		planner.addOutputs(signature, outputFields, type);
		// also add properties to these outputfields
		//planner.addProperty(opName, propertyName, value);
	}
}