/*******************************************************************************
 * Copyright 2015 Defense Health Agency (DHA)
 *
 * If your use of this software does not include any GPLv2 components:
 * 	Licensed under the Apache License, Version 2.0 (the "License");
 * 	you may not use this file except in compliance with the License.
 * 	You may obtain a copy of the License at
 *
 * 	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * 	Unless required by applicable law or agreed to in writing, software
 * 	distributed under the License is distributed on an "AS IS" BASIS,
 * 	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 	See the License for the specific language governing permissions and
 * 	limitations under the License.
 * ----------------------------------------------------------------------------
 * If your use of this software includes any GPLv2 components:
 * 	This program is free software; you can redistribute it and/or
 * 	modify it under the terms of the GNU General Public License
 * 	as published by the Free Software Foundation; either version 2
 * 	of the License, or (at your option) any later version.
 *
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 *******************************************************************************/

package prerna.ds;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import prerna.algorithm.impl.ExactStringMatcher;
import prerna.util.ArrayUtilityMethods;
import prerna.util.Utility;

public class OrderedBTreeDataFrame extends BTreeDataFrame {
	
	private static final Logger LOGGER = LogManager.getLogger(OrderedBTreeDataFrame.class.getName());
	List<SimpleTreeNode> orderedLeafs = new ArrayList<SimpleTreeNode>();

	public OrderedBTreeDataFrame(String[] levelNames) {
		super(levelNames);
	}

	public OrderedBTreeDataFrame(String[] levelNames, String[] uriLevelNames) {
		super(levelNames, uriLevelNames);
	}
	
	protected void storeRowInTree(ISEMOSSNode[] row){
		SimpleTreeNode leaf = simpleTree.addNodeArray(row);
		orderedLeafs.add(leaf);
	}
	
	@Override
	public Iterator<Object[]> iterator(boolean getRawData) {
		return new OrderedBTreeIterator(this.orderedLeafs, getRawData, columnsToSkip);
	}

	//includes filtered values
	public List<Object[]> getAllData() {
		List<Object[]> table = new ArrayList<Object[]>();
		Iterator<Object[]> it = new OrderedBTreeIterator(this.orderedLeafs, false, columnsToSkip, true);
		while(it.hasNext()) {
			table.add(it.next());
		}
		return table;
	}
}