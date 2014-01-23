/*******************************************************************************
 * Copyright 2013 SEMOSS.ORG
 * 
 * This file is part of SEMOSS.
 * 
 * SEMOSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SEMOSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SEMOSS.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package prerna.ui.transformer;

import java.util.Vector;

import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;

import prerna.om.SEMOSSVertex;
import prerna.ui.components.ControlData;
import prerna.util.Constants;

/**
 * Transforms what is displayed on the tooltip when a vertex/node is selected on a graph.
 */
public class VertexTooltipTransformer implements Transformer <SEMOSSVertex, String> {
	
	Logger logger = Logger.getLogger(getClass());	
	ControlData data = null;
	
	/**
	 * Constructor for VertexTooltipTransformer.
	 * @param data ControlData
	 */
	public VertexTooltipTransformer(ControlData data)
	{
		this.data = data;
	}
	
	
	/**
	 * Method transform.  Get the DI Helper to find what is needed to get for vertex
	 * @param arg0 DBCMVertex - The edge of which this returns the properties.
	
	 * @return String - The name of the property. */
	@Override
	public String transform(SEMOSSVertex arg0) {	
		String propName = "";//(String)arg0.getProperty(Constants.VERTEX_NAME);

		Vector props = this.data.getSelectedPropertiesTT(arg0.getProperty(Constants.VERTEX_TYPE)+"");
		if(props != null && props.size() > 0)
		{
			propName = propName + arg0.getProperty(props.elementAt(0)+"");
			for(int propIndex=1;propIndex < props.size();propIndex++){
				String prop = props.elementAt(propIndex)+"";
				propName = propName + "<br>";
				//only add the label on the property if it is not one of the main three
				if(!prop.equals(Constants.VERTEX_NAME)&&!prop.equals(Constants.EDGE_NAME)&&!prop.equals(Constants.VERTEX_TYPE)&&!prop.equals(Constants.EDGE_TYPE)&&!prop.equals(Constants.URI))
					propName = propName + prop+": ";
				propName = propName + arg0.getProperty(props.elementAt(propIndex)+"");
			}
		}
		//logger.debug("Prop Name " + propName);
		
		if(propName.equals(""))
			return null;		
		

		propName = "<html><body style=\"border:0px solid white; box-shadow:1px 1px 1px #000; padding:2px; background-color:white;\">" +
				"<font size=\"3\" color=\"black\"><i>" + propName + "</i></font></body></html>";
		return propName;
	}
}
