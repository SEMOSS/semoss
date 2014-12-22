/*******************************************************************************
 * Copyright 2014 SEMOSS.ORG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package prerna.ui.components;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


/**
 * This class is used to create a table model for shapes and colors of vertices.
 */
public class ShapeColorTableModel extends AbstractTableModel {
	
	VertexColorShapeData data = null;
	static final Logger logger = LogManager.getLogger(ShapeColorTableModel.class.getName());

	/**
	 * Constructor for ShapeColorTableModel.
	 * @param data VertexColorShapeData
	 */
	public ShapeColorTableModel(VertexColorShapeData data)
	{
		this.data = data;
	}
	
	/**
	 * Returns the column count.
	
	 * @return int 	Column count. */
	@Override
	public int getColumnCount() {
		return data.scColumnNames.length;
	}
	
	/**
	 * Gets the column name at a particular index.
	 * @param index 	Column index.
	
	 * @return String 	Column name. */
	public String getColumnName(int index)
	{
		return data.scColumnNames[index] + "";
	}

	/**
	 * Returns the row count.
	
	 * @return int 	Row count. */
	@Override
	public int getRowCount() {
		return data.count;
	}

	/**
	 * Gets the cell value at a particular row and column index.
	 * @param arg0 		Row index.
	 * @param arg1 		Column index.
	
	 * @return Object 	Cell value. */
	@Override
	public Object getValueAt(int row, int column) {
		
		return data.getValueAt(row, column);
	}
	
	/**
	 * Gets the column class at a particular index.
	 * @param column 	Column index.
	
	 * @return Class 	Column class. */
	public Class getColumnClass(int column)
	{
		return data.shapeColorRows[0][column].getClass();
	}

	/**
	 * Checks whether the cell at a particular row and column index is editable.
	 * @param row 		Row index.
	 * @param column 	Column index.
	
	 * @return boolean 	True if cell is editable. */
	public boolean isCellEditable(int row, int column)
	{
		if(column == 2 || column == 3)
			return true;
		else
			return false;
	}
		
	/**
	 * Sets the label value at a particular row and column index.
	 * @param value 	Label value.
	 * @param row 		Row index.
	 * @param column 	Column index.
	 */
	public void setValueAt(Object value, int row, int column)
	{
		logger.debug("Calling the shape color value set");
		data.setValueAt(value, row, column);
	}
	
}
