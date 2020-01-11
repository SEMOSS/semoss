package prerna.sablecc2.reactor.export;

import org.jclouds.scriptbuilder.functions.InitAdminAccess;

import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.om.ReactorKeysEnum;
import prerna.sablecc2.om.nounmeta.NounMetadata;
import prerna.sablecc2.reactor.AbstractReactor;

public class FrameCacheReactor extends AbstractReactor {
	
	public FrameCacheReactor() {
		this.keysToGet = new String[] { ReactorKeysEnum.FRAME_CACHE.getKey() };
	}
	
	public NounMetadata execute() {
		// default this to use Python
		// if Python not present
		// try in R
		// default is R
		// reset it ?
		
		if(insight.getPragmap() == null)
			insight.setPragmap(new java.util.HashMap());
		this.insight.getPragmap().put("xCache", this.curRow.vector.get(0).getValue());
		
		return new NounMetadata("Cache is now set to : " + insight.getPragmap().get("xCache") + "", PixelDataType.CONST_STRING);
	}
}
