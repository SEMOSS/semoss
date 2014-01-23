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

import prerna.om.SEMOSSEdge;


/**
 * This class is used to create a table model for edge properties.
 */
public class EdgePropertyTableModel extends AbstractTableModel {
	
	VertexFilterData data = null;
	SEMOSSEdge edge = null;

	/**
	 * Constructor for EdgePropertyTableModel.
	 * @param data VertexFilterData
	 * @param edge DBCMEdge
	 */
	public EdgePropertyTableModel(VertexFilterData data, SEMOSSEdge edge)
	{
		//super(data.getRows(), data.columnNames);
		this.data = data;
		this.edge = edge;
	}
	/**
	 * Returns the column count in the model.
	
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
	 * Gets the property name at a particular index.
	 * @param index 	Column index.
	
	 * @return String 	Column name. */
	public String getColumnName(int index)
	{
		return this.data.propertyNames[index];
	}

	/**
	 * Returns the number of rows in the model.
	
	 * @return int	Number of rows. */
	@Override
	public int getRowCount() {
		return data.getEdgeNumRows(edge);
	}

	/**
	 * Gets the value of a cell at a particular row and column index.
	 * @param row 		Row index.
	 * @param column 	Column index.
	
	 * @return Object 	Cell value. */
	@Override
	public Object getValueAt(int row, int column) {
		// get the value first
		return data.getPropValueAt(edge, row, column);
	}
	
	/**
	 * Sets the edge property value at a particular row and column.
	 * @param value 	Value to assign to cell.
	 * @param row		Row that value is assigned to.
	 * @param column 	Column that value is assigned to.
	 */
	public void setValueAt(Object val, int row, int column)
	{
		data.setPropValueAt(edge, val+"", row, column);
		fireTableDataChanged();
		// sets the value
	}
	
	/**
	 * Gets the class at a particular column.
	 * @param column 	Column index.
	
	 * @return Class	Column class. */
	public Class getColumnClass(int column)
	{
		return data.propClassNames[column];
	}
	/**
	 * Checks if the cell at a particular row and column is editable.
	 * @param row 		Row index.
	 * @param column 	Column index.
	
	 * @return boolean 	True if the cell is editable. */
	public boolean isCellEditable(int row, int column)
	{
		if(column == 1)
			return true;
		return false;
	}	
}
