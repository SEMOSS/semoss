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

import javax.swing.table.AbstractTableModel;

import prerna.om.SEMOSSVertex;


/**
 * This class is used to create a table model for vertex properties.
 */
public class VertexPropertyTableModel extends AbstractTableModel {
	
	VertexFilterData data = null;
	SEMOSSVertex vertex = null;

	/**
	 * Constructor for VertexPropertyTableModel.
	 * @param data VertexFilterData
	 * @param vertex DBCMVertex
	 */
	public VertexPropertyTableModel(VertexFilterData data, SEMOSSVertex vertex)
	{
		//super(data.getRows(), data.columnNames);
		this.data = data;
		this.vertex = vertex;
	}
	/**
	 * Returns the column count.
	
	 * @return int 	Column count. */
	@Override
	public int getColumnCount() {
		return this.data.propertyNames.length;
	}
	
	/**
	 * Sets the vertex filter data.
	 * @param data VertexFilterData
	 */
	public void setVertexFilterData(VertexFilterData data)
	{
		this.data = data;
	}

	/**
	 * Gets the column name at a particular index.
	 * @param index 	Column index.
	
	 * @return String 	Column name. */
	public String getColumnName(int index)
	{
		return this.data.propertyNames[index];
	}

	/**
	 * Returns the row count.
	
	 * @return int 	Row count. */
	@Override
	public int getRowCount() {
		return data.getPropertyNumRows(vertex);
	}

	/**
	 * Gets the cell value at a particular row and column index.
	 * @param arg0 		Row index.
	 * @param arg1 		Column index.
	
	 * @return Object 	Cell value. */
	@Override
	public Object getValueAt(int row, int column) {
		// get the value first
		return data.getPropValueAt(vertex, row, column);
	}
	
	/**
	 * Sets the cell value at a particular row and column index.
	 * @param val 		Cell value.
	 * @param row 		Row index.
	 * @param column 	Column index.
	 */
	public void setValueAt(Object val, int row, int column)
	{
		data.setPropValueAt(vertex, val+"", row, column);
		fireTableDataChanged();
		// sets the value
	}
	
	/**
	 * Gets the column class at a particular index.
	 * @param column 	Column index.
	
	 * @return Class 	Column class. */
	public Class getColumnClass(int column)
	{
		return data.propClassNames[column];
	}
	/**
	 * Checks whether the cell at a particular row and column index is editable.
	 * @param row 		Row index.
	 * @param column 	Column index.
	
	 * @return boolean 	True if cell is editable. */
	public boolean isCellEditable(int row, int column)
	{
		if(column == 1)
			return true;
		return false;
	}	
}
