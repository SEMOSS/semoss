package prerna.ui.components.specific.ousd;

import prerna.engine.api.IEngine;

public interface ITimelineGenerator {

	public void createTimeline();
	
	public void createTimeline(IEngine engine);
	
	public void createTimeline(IEngine engine, String owner);
	
	public OUSDTimeline getTimeline();
	
}
