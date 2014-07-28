package prerna.ui.components.specific.tap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import prerna.error.EngineException;
import prerna.error.FileReaderException;
import prerna.poi.specific.TAPLegacySystemDispositionReportWriter;
import prerna.ui.components.BooleanProcessor;
import prerna.ui.components.UpdateProcessor;
import prerna.ui.components.playsheets.BasicProcessingPlaySheet;
import prerna.util.Constants;
import prerna.util.DIHelper;
import prerna.util.Utility;

@SuppressWarnings("serial")
public class TAPLegacySystemDispositionPlaySheet extends BasicProcessingPlaySheet{

	private String checkModPropQuery = "ASK WHERE { {?system <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/System> ;} BIND(<http://semoss.org/ontologies/Relation/Contains/InterfaceModernizationCost> AS ?contains) {?p ?contains ?prop ;} }";
	private String modPropDeleteQuery = "DELETE { ?system ?contains ?prop } WHERE { {?system <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/System> ;} BIND(<http://semoss.org/ontologies/Relation/Contains/InterfaceModernizationCost> AS ?contains) {?p ?contains ?prop ;} }";

	@Override
	public void createView() {
		Utility.showMessage("Success!");
	}
	
	@Override
	public void createData() {
		boolean modernizationPropExists = checkModernizationProp();
		if(!modernizationPropExists) 
		{
			// show continue popup
			JFrame playPane = (JFrame) DIHelper.getInstance().getLocalProp(Constants.MAIN_FRAME);
			Object[] buttons = {"Cancel", "Continue With Calculation"};
			int response = JOptionPane.showOptionDialog(playPane, "The selected RDF store does not " +
					"contain necessary calculated values.  Would you like to calculate now?", 
					"Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, buttons, buttons[1]);
			
			// if user chooses to run insert, run the thing
			if(response == 1)
			{
				try {
					runModernizationPropInsert();
				} catch (EngineException e) {
					Utility.showError(e.getMessage());
					e.printStackTrace();
				}
			}
			else{
				return;
			}
		}
		else
		{
			// show override popup
			JFrame playPane = (JFrame) DIHelper.getInstance().getLocalProp(Constants.MAIN_FRAME);
			Object[] buttons = {"Continue with stored values", "Recalculate"};
			int response = JOptionPane.showOptionDialog(playPane, "The selected RDF store already " +
					"contains calculated values.  Would you like to recalculate?", 
					"Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, buttons, buttons[1]);
			
			// if user chooses to overwrite, delete then insert
			if(response == 1)
			{
				deleteModernizationProp();
				try {
					runModernizationPropInsert();
				} catch (EngineException e) {
					Utility.showError(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		
		String systemURI = query;
		TAPLegacySystemDispositionReportWriter writer;
		try {
			writer = new TAPLegacySystemDispositionReportWriter(systemURI);
			writer.writeToExcel();
		} catch (EngineException e) {
			e.printStackTrace();
			Utility.showError(e.getMessage());
		} catch (FileReaderException e) {
			e.printStackTrace();
			Utility.showError(e.getMessage());
		}
	}
	
	private void runModernizationPropInsert() throws EngineException {
		InsertInterfaceModernizationProperty inserter = new InsertInterfaceModernizationProperty();
		inserter.insert();
	}
	
	private boolean checkModernizationProp(){
		logger.info("Checking modernization prop");
		boolean exists = false;

		BooleanProcessor proc = new BooleanProcessor();
		proc.setEngine(this.engine);
		proc.setQuery(checkModPropQuery);
		exists = proc.processQuery();
		logger.info("Modernization prop exists: " + exists);
		return exists;
	}
	
	private void deleteModernizationProp() {
		logger.info("Deleting modernization prop");
		UpdateProcessor upProc = new UpdateProcessor();
		upProc.setEngine(this.engine);
		upProc.setQuery(modPropDeleteQuery);
		upProc.processQuery();
	}
	
}
