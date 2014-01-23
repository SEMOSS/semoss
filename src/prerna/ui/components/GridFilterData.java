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
package prerna.ui.components;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;


/**
 * This class is the primary ones used for vertex filtering.
 */
public class GridFilterData {
	
	// need to have vertex and type information
	// everytime a vertex is added here
	// need to figure out a type so that it can show
	// the types are not needed after or may be it is
	// we need a structure which keeps types with vector
	// the vector will have all of the vertex specific to the type
	// additionally, there needs to be another structure so that when I select or deselect something
	// it marks it on the matrix
	// need to come back and solve this one
	Hashtable <String, Vector> typeHash = new Hashtable<String, Vector>();
	public String [] columnNames = null;
	
	public ArrayList<Object []> dataList = null;
	
	Logger logger = Logger.getLogger(getClass());
	
	/**
	 * Gets the value at a particular row and column index.
	 * @param row 		Row index.
	 * @param column 	Column index.
	
	 * @return Object 	Cell value. */
	public Object getValueAt(int row, int column)
	{
		Object [] val = dataList.get(row);
		Object retVal = val[column];
		if(column == 0)
			logger.debug(row + "<>" + column + "<>" + retVal);
		return retVal;
	}
	
	/**
	 * Sets the data list.
	 * @param dataList 	List of data.
	 */
	public void setDataList(ArrayList <Object []> dataList)
	{
		this.dataList = dataList;
	}
	
	/**
	 * Sets the column names.
	 * @param columnNames 	Column names.
	 */
	public void setColumnNames(String [] columnNames)
	{
		this.columnNames = columnNames;
	}
	
	/**
	 * Gets the number of rows from the data.
	
	 * @return int 	Number of rows. */
	public int getNumRows()
	{
		// use this call to convert the thing to array
		return dataList.size();
	}
	
	/**
	 * Method setValueAt.
	 //TODO: method never called
	 * @param value Object
	 * @param row int
	 * @param column int
	 */
	public void setValueAt(Object value, int row, int column)
	{
		// ignore this should never be called
	}
}
