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

import java.util.Collections;
import java.util.Vector;

import org.apache.commons.collections15.Transformer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import prerna.om.SEMOSSVertex;
import prerna.ui.components.ControlData;
import prerna.util.Constants;
import prerna.util.PropComparator;

/**
 * Transforms the property label on a node vertex in the graph.
 */
public class VertexLabelTransformer implements Transformer <SEMOSSVertex, String> {
	
	static final Logger logger = LogManager.getLogger(VertexLabelTransformer.class.getName());
	
	ControlData data = null;
	
	/**
	 * Constructor for VertexLabelTransformer.
	 * @param data ControlData
	 */
	public VertexLabelTransformer(ControlData data)
	{
		this.data = data;
	}

	/**
	 * Method transform.  Transforms the label on a node vertex in the graph
	 * @param arg0 DBCMVertex - the vertex to be transformed
	
	 * @return String - the property name of the vertex*/
	@Override
	public String transform(SEMOSSVertex arg0) {
		// get the DI Helper to find what is the property we need to get for vertex
		// based on that get that property and return it		
		String propName = "";//(String)arg0.getProperty(Constants.VERTEX_NAME);

		Vector props = this.data.getSelectedProperties(arg0.getProperty(Constants.VERTEX_TYPE)+"");
		if(props != null && props.size() > 0)
		{
			propName = "<html>";
			propName = propName + "<!--"+arg0.getURI()+"-->";//Need this stupid comment to keep each html comment different. For some reason the transformer cannot handle text size changes if two labels are the same
			propName = propName + "<font size=\"1\"><br>";//need these font tags so that when you increase font through font transformer, the label doesn't get really far away fromt the vertex
			propName = propName + "<br>";
			propName = propName + "<br></font>";
			
			//want to order the props so that it is always in the order name, type, uri, then the other properties
			Collections.sort(props, new PropComparator());
			for(int propIndex=0;propIndex < props.size();propIndex++){
				if(propIndex!=0)propName = propName + "<font size=\"1\"><br></font>";
				propName = propName + arg0.getProperty(props.elementAt(propIndex)+"");
			}
			propName = propName + "</html>";
		}
		//logger.debug("Prop Name " + propName);

		return propName;
	}

}
