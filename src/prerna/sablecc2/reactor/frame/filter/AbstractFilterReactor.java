package prerna.sablecc2.reactor.frame.filter;

import java.util.List;

import prerna.om.InsightPanel;
import prerna.query.querystruct.filters.GenRowFilters;
import prerna.query.querystruct.filters.IQueryFilter;
import prerna.sablecc2.om.GenRowStruct;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.reactor.frame.AbstractFrameReactor;

public abstract class AbstractFilterReactor extends AbstractFrameReactor {

	protected String DYNAMIC_KEY = "dynamic";
	
	/**
	 * Get the filters passed into the reactor
	 * 
	 * @return
	 */
	protected GenRowFilters getFilters() {
		// generate a grf with the wanted filters
		GenRowFilters grf = new GenRowFilters();
		int size = this.curRow.size();
		for (int i = 0; i < size; i++) {
			IQueryFilter nextFilter = (IQueryFilter) this.curRow.get(i);
			if (nextFilter != null) {
				grf.addFilters(nextFilter);
			}
		}
		return grf;
	}

	protected InsightPanel getInsightPanel() {
		InsightPanel panel = null;

		// see if panel was passed via generic reactor
		GenRowStruct genericGrs = this.store.getNoun(ReactorKeysEnum.PANEL.getKey());
		if (genericGrs != null && !genericGrs.isEmpty()) {
			String panelId = genericGrs.get(0).toString();
			panel = this.insight.getInsightPanel(panelId);
		}

		// or was passed in via a "|"
		GenRowStruct pipedGrs = this.store.getNoun(PixelDataType.PANEL.toString());
		if (pipedGrs != null && !pipedGrs.isEmpty()) {
			panel = (InsightPanel) pipedGrs.get(0);
		}

		if (panel == null) {
			// if not, see if it was passed in the grs
			List<Object> panelNouns = this.curRow.getValuesOfType(PixelDataType.PANEL);
			if (panelNouns != null && !panelNouns.isEmpty()) {
				panel = (InsightPanel) panelNouns.get(0);
			}
		}

		if (panel == null) {
			throw new IllegalArgumentException("Invalid panel id passed into With reactor");
		}

		return panel;
	}
	
}