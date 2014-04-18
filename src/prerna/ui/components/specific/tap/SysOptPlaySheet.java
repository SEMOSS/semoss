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
package prerna.ui.components.specific.tap;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;

import aurelienribon.ui.css.Style;
import prerna.ui.helpers.EntityFiller;
import prerna.ui.main.listener.specific.tap.AdvParamListener;
import prerna.ui.main.listener.specific.tap.CapCheckBoxSelectorListener;
import prerna.ui.main.listener.specific.tap.CapSpecComboBoxListener;
import prerna.ui.main.listener.specific.tap.CheckBoxSelectorListener;
import prerna.ui.main.listener.specific.tap.OptFunctionRadioBtnListener;
import prerna.ui.main.listener.specific.tap.SysOptBtnListener;
import prerna.ui.main.listener.specific.tap.SysOptTypeSelectorListener;
import prerna.ui.swing.custom.ButtonMenuDropDown;
import prerna.ui.swing.custom.SelectScrollList;
import prerna.ui.swing.custom.ToggleButton;


/**
 * This is the playsheet used exclusively for TAP service optimization.
 */
public class SysOptPlaySheet extends SerOptPlaySheet{

	//system, data, blu select panel and toggle button
	public JToggleButton showSystemSelectBtn;
	public JPanel systemSelectPanel;

	 //overall analysis tab
	public JLabel annualBudgetLbl, timeTransitionLbl;
	
	//system and capability selects
//	public Panel sysTheaterGarrisonSelectPanel, sysProbSelectPanel;
	public JCheckBox theaterSysButton, garrisonSysButton, allSysButton;
	public JCheckBox lowProbButton, medProbButton, highProbButton;
//	public Panel sysAllCapSelectPanel, sysHSDHSSFHPCapSelectPanel;
	public JCheckBox allCapButton, dhmsmCapButton;
	public JCheckBox hsdCapButton, hssCapButton, fhpCapButton;

	public JComboBox sysSelectComboBox, capSelectComboBox;
	public SelectScrollList sysSelectDropDown, capSelectDropDown;

	//old system, data, blu selects
	public JTextField sysSelectQueryField, dataSelectQueryField, bluSelectQueryField;



	
	/**
	 * Constructor for SysOptPlaySheet.
	 */
	public SysOptPlaySheet()
	{
		super();
	}
	public String[] makeListFromQuery(String type, String query)
	{
		//"SELECT DISTINCT ?entity WHERE {{?CapabilityTag <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/CapabilityTag>;}{?TaggedBy <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/TaggedBy>;}{?entity <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://semoss.org/ontologies/Concept/Capability> ;}{?CapabilityTag ?TaggedBy ?entity}}"
		EntityFiller filler = new EntityFiller();
		filler.engineName = engine.getEngineName();
		filler.type = "Capability";
		filler.setExternalQuery(query);
		filler.run();
		Vector names = filler.nameVector;
		String[] listArray=new String[names.size()];
		for (int i = 0;i<names.size();i++)
		{
			listArray[i]=(String) names.get(i);
		}
		return listArray;
	}
	@Override
	public void createAdvParamPanels()
	{
		super.createAdvParamPanels();
		systemSelectPanel = new JPanel();
		systemSelectPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		systemSelectPanel.setVisible(false);
		
		GridBagConstraints gbc_systemSelectPanel = new GridBagConstraints();
		gbc_systemSelectPanel.gridheight = 6;
		gbc_systemSelectPanel.fill = GridBagConstraints.BOTH;
		gbc_systemSelectPanel.gridx = 8;
		gbc_systemSelectPanel.gridy = 0;
		ctlPanel.add(systemSelectPanel, gbc_systemSelectPanel);
		
		GridBagLayout gbl_systemSelectPanel = new GridBagLayout();
		gbl_systemSelectPanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_systemSelectPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_systemSelectPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_systemSelectPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		systemSelectPanel.setLayout(gbl_systemSelectPanel);
		
		JLabel lblSystemSelectHeader = new JLabel("Select Systems:");
		lblSystemSelectHeader.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblSystemSelectHeader = new GridBagConstraints();
		gbc_lblSystemSelectHeader.gridwidth = 3;
		gbc_lblSystemSelectHeader.anchor = GridBagConstraints.WEST;
		gbc_lblSystemSelectHeader.insets = new Insets(10, 0, 5, 5);
		gbc_lblSystemSelectHeader.gridx = 0;
		gbc_lblSystemSelectHeader.gridy = 0;
		systemSelectPanel.add(lblSystemSelectHeader, gbc_lblSystemSelectHeader);
		
		JLabel lblCapSelectHeader = new JLabel("Select Capabilities:");
		lblCapSelectHeader.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblCapSelectHeader = new GridBagConstraints();
		gbc_lblCapSelectHeader.gridwidth = 3;
		gbc_lblCapSelectHeader.anchor = GridBagConstraints.WEST;
		gbc_lblCapSelectHeader.insets = new Insets(10, 0, 5, 5);
		gbc_lblCapSelectHeader.gridx = 5;
		gbc_lblCapSelectHeader.gridy = 0;
		systemSelectPanel.add(lblCapSelectHeader, gbc_lblCapSelectHeader);
		

//		sysTheaterGarrisonSelectPanel = new Panel();
//		GridBagConstraints gbc_sysTheaterGarrisonSelectPanel = new GridBagConstraints();
//		gbc_sysTheaterGarrisonSelectPanel.anchor = GridBagConstraints.WEST;
//		gbc_sysTheaterGarrisonSelectPanel.gridx = 0;
//		gbc_sysTheaterGarrisonSelectPanel.gridy = 1;
//		systemSelectPanel.add(sysTheaterGarrisonSelectPanel, gbc_sysTheaterGarrisonSelectPanel);
//		sysTheaterGarrisonSelectPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));
		
		allSysButton = new JCheckBox("All Systems");
		GridBagConstraints gbc_allSysButton = new GridBagConstraints();
		gbc_allSysButton.anchor = GridBagConstraints.WEST;
		gbc_allSysButton.gridx = 0;
		gbc_allSysButton.gridy = 1;
		systemSelectPanel.add(allSysButton, gbc_allSysButton);
//		allSysButton.addActionListener(sysTypeList);
		
		theaterSysButton = new JCheckBox("Theater");
		GridBagConstraints gbc_theaterSysButton = new GridBagConstraints();
		gbc_theaterSysButton.anchor = GridBagConstraints.WEST;
		gbc_theaterSysButton.gridx = 1;
		gbc_theaterSysButton.gridy = 1;
		systemSelectPanel.add(theaterSysButton, gbc_theaterSysButton);
//		theaterSysButton.addActionListener(sysTypeList);

		garrisonSysButton = new JCheckBox("Garrison");
		GridBagConstraints gbc_garrisonSysButton = new GridBagConstraints();
		gbc_garrisonSysButton.anchor = GridBagConstraints.WEST;
		gbc_garrisonSysButton.gridx = 2;
		gbc_garrisonSysButton.gridy = 1;
		systemSelectPanel.add(garrisonSysButton, gbc_garrisonSysButton);
//		garrisonSysButton.addActionListener(sysTypeList);
		
		lowProbButton = new JCheckBox("Low Prob");
		GridBagConstraints gbc_lowProbButton = new GridBagConstraints();
		gbc_lowProbButton.anchor = GridBagConstraints.WEST;
		gbc_lowProbButton.gridx = 0;
		gbc_lowProbButton.gridy = 2;
		systemSelectPanel.add(lowProbButton, gbc_lowProbButton);
//		lowProbButton.addActionListener(sysTypeList);
		
		medProbButton = new JCheckBox("Med Prob");
		GridBagConstraints gbc_medProbButton = new GridBagConstraints();
		gbc_medProbButton.anchor = GridBagConstraints.WEST;
		gbc_medProbButton.gridx = 1;
		gbc_medProbButton.gridy = 2;
		systemSelectPanel.add(medProbButton, gbc_medProbButton);
//		medProbButton.addActionListener(sysTypeList);
		
		highProbButton = new JCheckBox("High Prob");
		GridBagConstraints gbc_highProbButton = new GridBagConstraints();
		gbc_highProbButton.anchor = GridBagConstraints.WEST;
		gbc_highProbButton.gridx = 2;
		gbc_highProbButton.gridy = 2;
		systemSelectPanel.add(highProbButton, gbc_highProbButton);
//		highProbButton.addActionListener(sysTypeList);
		

		allCapButton = new JCheckBox("All Cap");
		GridBagConstraints gbc_allCapButton = new GridBagConstraints();
		gbc_allCapButton.anchor = GridBagConstraints.WEST;
		gbc_allCapButton.gridx = 5;
		gbc_allCapButton.gridy = 1;
		systemSelectPanel.add(allCapButton, gbc_allCapButton);
//		lowProbButton.addActionListener(sysTypeList);
		
		dhmsmCapButton = new JCheckBox("DHMSM");
		GridBagConstraints gbc_dhmsmCapButton = new GridBagConstraints();
		gbc_dhmsmCapButton.anchor = GridBagConstraints.WEST;
		gbc_dhmsmCapButton.gridx = 6;
		gbc_dhmsmCapButton.gridy = 1;
		systemSelectPanel.add(dhmsmCapButton, gbc_dhmsmCapButton);
//		medProbButton.addActionListener(sysTypeList);

		hsdCapButton = new JCheckBox("HSD Cap");
		GridBagConstraints gbc_hsdCapButton = new GridBagConstraints();
		gbc_hsdCapButton.anchor = GridBagConstraints.WEST;
		gbc_hsdCapButton.gridx = 5;
		gbc_hsdCapButton.gridy = 2;
		systemSelectPanel.add(hsdCapButton, gbc_hsdCapButton);
		
		hssCapButton = new JCheckBox("HSS Cap");
		GridBagConstraints gbc_hssCapButton = new GridBagConstraints();
		gbc_hssCapButton.anchor = GridBagConstraints.WEST;
		gbc_hssCapButton.gridx = 6;
		gbc_hssCapButton.gridy = 2;
		systemSelectPanel.add(hssCapButton, gbc_hssCapButton);
//		lowProbButton.addActionListener(sysTypeList);
		
		fhpCapButton = new JCheckBox("FHP Cap");
		GridBagConstraints gbc_fhpCapButton = new GridBagConstraints();
		gbc_fhpCapButton.anchor = GridBagConstraints.WEST;
		gbc_fhpCapButton.gridx = 7;
		gbc_fhpCapButton.gridy = 2;
		systemSelectPanel.add(fhpCapButton, gbc_fhpCapButton);
//		medProbButton.addActionListener(sysTypeList);
		
		
		
		sysSelectDropDown = new SelectScrollList("Select Individual Systems");
		sysSelectDropDown.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		GridBagConstraints gbc_sysSelectDropDown = new GridBagConstraints();
		gbc_sysSelectDropDown.gridwidth = 3;
		gbc_sysSelectDropDown.insets = new Insets(0, 0, 0, 5);
		gbc_sysSelectDropDown.fill = GridBagConstraints.HORIZONTAL;
		gbc_sysSelectDropDown.gridx = 0;
		gbc_sysSelectDropDown.gridy = 3;
		systemSelectPanel.add(sysSelectDropDown.pane, gbc_sysSelectDropDown);
//		final JPopupMenu sysSelectPopupMenu = sysSelectDropDown.popupMenu;
		
		String[] sysArray = makeListFromQuery("System","SELECT DISTINCT ?entity WHERE {{?entity <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://semoss.org/ontologies/Concept/ActiveSystem> ;}}");
		sysSelectDropDown.setupButton(sysArray,40,120); //need to give list of all systems

		CheckBoxSelectorListener sysCheckBoxListener = new CheckBoxSelectorListener();
		sysCheckBoxListener.setEngine(engine);
		sysCheckBoxListener.setScrollList(sysSelectDropDown);
		sysCheckBoxListener.setCheckBox(allSysButton,theaterSysButton, garrisonSysButton,lowProbButton, medProbButton, highProbButton);
		allSysButton.addActionListener(sysCheckBoxListener);
		theaterSysButton.addActionListener(sysCheckBoxListener);
		garrisonSysButton.addActionListener(sysCheckBoxListener);
		lowProbButton.addActionListener(sysCheckBoxListener);
		medProbButton.addActionListener(sysCheckBoxListener);
		highProbButton.addActionListener(sysCheckBoxListener);
		
		capSelectDropDown = new SelectScrollList("Select Individual Capabilities");
		capSelectDropDown.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		GridBagConstraints gbc_capSelectDropDown = new GridBagConstraints();
		gbc_capSelectDropDown.gridwidth = 3;
		gbc_capSelectDropDown.fill = GridBagConstraints.HORIZONTAL;
		gbc_capSelectDropDown.insets = new Insets(0, 0, 0, 5);
		gbc_capSelectDropDown.gridx = 5;
		gbc_capSelectDropDown.gridy = 3;
		systemSelectPanel.add(capSelectDropDown.pane, gbc_capSelectDropDown);
//		final JPopupMenu capSelectPopupMenu = capSelectDropDown.popupMenu;
		
		String[] capArray = makeListFromQuery("Capability","SELECT DISTINCT ?entity WHERE {{?CapabilityTag <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/CapabilityTag>;}{?TaggedBy <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/TaggedBy>;}{?entity <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://semoss.org/ontologies/Concept/Capability> ;}{?CapabilityTag ?TaggedBy ?entity}}");
		capSelectDropDown.setupButton(capArray,40,120); //need to give list of all systems
		
		CapCheckBoxSelectorListener capCheckBoxListener = new CapCheckBoxSelectorListener();
		capCheckBoxListener.setEngine(engine);
		capCheckBoxListener.setScrollList(capSelectDropDown);
		capCheckBoxListener.setCheckBox(allCapButton,dhmsmCapButton, hsdCapButton,hssCapButton, fhpCapButton);
		allCapButton.addActionListener(capCheckBoxListener);
		dhmsmCapButton.addActionListener(capCheckBoxListener);
		hsdCapButton.addActionListener(capCheckBoxListener);
		hssCapButton.addActionListener(capCheckBoxListener);
		fhpCapButton.addActionListener(capCheckBoxListener);
		
//		///adding in capability selector to allow selection of data and blu from a list of capabilities specified in the param panel
//		capSelectComboBox = new JComboBox();
//		if(engine.getEngineName().contains("HR"))
//			capSelectComboBox.setModel(new DefaultComboBoxModel(new String[] {"All Capabilities", "All HSD Capabilities", "All HSS Capabilities", "All FHP Capabilities", "All DHMSM Capabilities", "Select Individual Capabilities"}));
//		else//Tap
//			capSelectComboBox.setModel(new DefaultComboBoxModel(new String[] {"All Capabilities", "Select Individual Capabilities"}));
//		GridBagConstraints gbc_sysSpecComboBox = new GridBagConstraints();
//		gbc_sysSpecComboBox.gridwidth = 4;
//		gbc_sysSpecComboBox.insets = new Insets(0, 0, 0, 5);
//		gbc_sysSpecComboBox.fill = GridBagConstraints.HORIZONTAL;
//		gbc_sysSpecComboBox.gridx = 0;
//		gbc_sysSpecComboBox.gridy = 5;
//		systemSelectPanel.add(capSelectComboBox, gbc_sysSpecComboBox);
//
//		String [] fetching = {"Fetching"};
//		EntityFiller filler = new EntityFiller();
//		filler.engineName = engine.getEngineName();
//		filler.type = "Capability";
//		filler.setExternalQuery("SELECT DISTINCT ?entity WHERE {{?CapabilityTag <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/CapabilityTag>;}{?TaggedBy <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/TaggedBy>;}{?entity <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://semoss.org/ontologies/Concept/Capability> ;}{?CapabilityTag ?TaggedBy ?entity}}");
//		filler.run();
//		Vector names = filler.nameVector;
//		String[] listArray=new String[names.size()];
//		for (int i = 0;i<names.size();i++)
//		{
//			listArray[i]=(String) names.get(i);
//		}
//		capSelectDropDown = new ButtonMenuDropDown("Select Individual Capabilities");
//		capSelectDropDown.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//		CapSpecComboBoxListener sscb = new CapSpecComboBoxListener();
//		sscb.setShowItem(capSelectDropDown);
//		sscb.setEngineName(engine.getEngineName());
//		sscb.setQueryTextFields(dataSelectQueryField,bluSelectQueryField);
//		capSelectComboBox.addActionListener(sscb);
//		capSelectDropDown.setVisible(false);
//		GridBagConstraints gbc_capSelect = new GridBagConstraints();
//		gbc_capSelect.anchor = GridBagConstraints.WEST;
//		gbc_capSelect.gridwidth = 2;
//		gbc_capSelect.gridx = 4;
//		gbc_capSelect.gridy = 5;
//		systemSelectPanel.add(capSelectDropDown, gbc_capSelect);
//		capSelectDropDown.setupButton(listArray);
//		final JPopupMenu popupMenu = capSelectDropDown.popupMenu;

		final JComponent contentPane = (JComponent) this.getContentPane();
		contentPane.addMouseListener(new MouseAdapter() {  

			@Override  
			public void mouseClicked(MouseEvent e) {  
				maybeShowPopup(e);  
			}  

			@Override  
			public void mousePressed(MouseEvent e) {  
				maybeShowPopup(e);  
			}  

			@Override  
			public void mouseReleased(MouseEvent e) {  
				maybeShowPopup(e);  
			}  

			private void maybeShowPopup(MouseEvent e) {  
				if (e.isPopupTrigger()) {
//					sysSelectPopupMenu.show(contentPane, e.getX(), e.getY());  
//					capSelectPopupMenu.show(contentPane, e.getX(), e.getY());  
				}  
			}  
		});

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//		JLabel lblSystemSelect = new JLabel("System Select Query");
//		lblSystemSelect.setFont(new Font("Tahoma", Font.PLAIN, 12));
//		GridBagConstraints gbc_lblSystemSelect = new GridBagConstraints();
//		gbc_lblSystemSelect.anchor = GridBagConstraints.WEST;
//		gbc_lblSystemSelect.insets = new Insets(0, 0, 5, 5);
//		gbc_lblSystemSelect.gridx = 6;
//		gbc_lblSystemSelect.gridy = 1;
//		systemSelectPanel.add(lblSystemSelect, gbc_lblSystemSelect);
//		
//		sysSelectQueryField = new JTextField();
//		sysSelectQueryField.setFont(new Font("Tahoma", Font.PLAIN, 11));
//		GridBagConstraints gbc_sysSelectQueryField = new GridBagConstraints();
//		gbc_sysSelectQueryField.anchor = GridBagConstraints.WEST;
//		gbc_sysSelectQueryField.insets = new Insets(0, 0, 5, 5);
//		gbc_sysSelectQueryField.gridwidth = 1;
//		gbc_sysSelectQueryField.gridx = 7;
//		gbc_sysSelectQueryField.gridy = 1;
//		systemSelectPanel.add(sysSelectQueryField, gbc_sysSelectQueryField);
//		sysSelectQueryField.setText("SELECT DISTINCT ?System WHERE { {?System <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/ActiveSystem> ;}}");
//		sysSelectQueryField.setColumns(20);
//		
//		sysTheaterGarrisonSelectPanel = new Panel();
//		GridBagConstraints gbc_sysSelectPanel = new GridBagConstraints();
//		gbc_sysSelectPanel.anchor = GridBagConstraints.WEST;
//		gbc_sysSelectPanel.gridx = 8;
//		gbc_sysSelectPanel.gridy = 1;
//		sysTheaterGarrisonSelectPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));
//		systemSelectPanel.add(sysTheaterGarrisonSelectPanel, gbc_sysSelectPanel);
//
//		SysOptTypeSelectorListener sysTypeList = new SysOptTypeSelectorListener();
//
//		ButtonGroup sysTypeButtonGroup = new ButtonGroup();
//		theaterSysButton = new JRadioButton("Theater");
//		sysTypeButtonGroup.add(theaterSysButton);
//		sysTheaterGarrisonSelectPanel.add(theaterSysButton);
//		theaterSysButton.addActionListener(sysTypeList);
//
//		garrisonSysButton = new JRadioButton("Garrison");
//		sysTypeButtonGroup.add(garrisonSysButton);
//		sysTheaterGarrisonSelectPanel.add(garrisonSysButton);
//		garrisonSysButton.addActionListener(sysTypeList);
//		
//		allSysButton = new JRadioButton("All Systems");
//		sysTypeButtonGroup.add(allSysButton);
//		sysTheaterGarrisonSelectPanel.add(allSysButton);
//		allSysButton.addActionListener(sysTypeList);
//		allSysButton.setSelected(true);
//		
//		sysProbSelectPanel = new Panel();
//		GridBagConstraints gbc_sysProbSelectPanel = new GridBagConstraints();
//		gbc_sysProbSelectPanel.anchor = GridBagConstraints.WEST;
//		gbc_sysProbSelectPanel.gridx = 8;
//		gbc_sysProbSelectPanel.gridy = 2;
//		sysProbSelectPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));
//		systemSelectPanel.add(sysProbSelectPanel, gbc_sysProbSelectPanel);
//
//		ButtonGroup sysProbButtonGroup = new ButtonGroup();
//		lowProbButton = new JRadioButton("Low Prob");
//		sysProbButtonGroup.add(lowProbButton);
//		sysProbSelectPanel.add(lowProbButton);
//		lowProbButton.addActionListener(sysTypeList);
//		
//		medProbButton = new JRadioButton("Med Prob");
//		sysProbButtonGroup.add(medProbButton);
//		sysProbSelectPanel.add(medProbButton);
//		medProbButton.addActionListener(sysTypeList);
//		
//		highProbButton = new JRadioButton("High Prob");
//		sysProbButtonGroup.add(highProbButton);
//		sysProbSelectPanel.add(highProbButton);
//		highProbButton.addActionListener(sysTypeList);
//		
//		allProbButton = new JRadioButton("All Prob");
//		sysProbButtonGroup.add(allProbButton);
//		sysProbSelectPanel.add(allProbButton);
//		allProbButton.addActionListener(sysTypeList);
//		allProbButton.setSelected(true);
//		
//		sysTypeList.setTheaterGarrisonBtn(theaterSysButton,garrisonSysButton,allSysButton);
//		sysTypeList.setProbBtn(lowProbButton,medProbButton,highProbButton,allProbButton);
//		sysTypeList.setQueryTextField(sysSelectQueryField);
//		
//		JLabel lblDataSelect = new JLabel("Data Select Query");
//		lblDataSelect.setFont(new Font("Tahoma", Font.PLAIN, 12));
//		GridBagConstraints gbc_lblDataSelect = new GridBagConstraints();
//		gbc_lblDataSelect.anchor = GridBagConstraints.WEST;
//		gbc_lblDataSelect.insets = new Insets(0, 0, 5, 5);
//		gbc_lblDataSelect.gridx = 6;
//		gbc_lblDataSelect.gridy = 2;
//		systemSelectPanel.add(lblDataSelect, gbc_lblDataSelect);
//		
//		dataSelectQueryField = new JTextField();
//		dataSelectQueryField.setFont(new Font("Tahoma", Font.PLAIN, 11));
//		GridBagConstraints gbc_dataSelectQueryField = new GridBagConstraints();
//		gbc_dataSelectQueryField.insets = new Insets(0, 0, 5, 5);
//		gbc_dataSelectQueryField.anchor = GridBagConstraints.WEST;
//		gbc_dataSelectQueryField.gridwidth = 1;
//		gbc_dataSelectQueryField.gridx = 7;
//		gbc_dataSelectQueryField.gridy = 2;
//		systemSelectPanel.add(dataSelectQueryField, gbc_dataSelectQueryField);
//		if(engine.getEngineName().contains("HR"))
//			dataSelectQueryField.setText("SELECT DISTINCT ?Data WHERE {{?Capability <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Capability>;}{?Consists <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Consists>;}{?Task <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Task>;}{?Capability ?Consists ?Task.}{?Needs <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Needs>;}{?Needs <http://semoss.org/ontologies/Relation/Contains/CRM> 'C'}{?Data <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/DataObject>;}{?Task ?Needs ?Data.} }");
//		else //if TAP Core
//			dataSelectQueryField.setText("SELECT DISTINCT ?Data WHERE {{?Capability <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Capability> ;}{?supports <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Supports> ;}{?BusinessProcess <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/BusinessProcess> ;}{ ?Capability ?supports ?BusinessProcess.} {?consists <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Consists>;} {?Activity <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Activity> ;}{?BusinessProcess ?consists ?Activity.}{?needs <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Needs>;} {?Data <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/DataObject> ;}{?Activity ?needs ?Data.}}");			
//		dataSelectQueryField.setColumns(20);
//		
//		JLabel lblBLUSelect = new JLabel("BLU Select Query");
//		lblBLUSelect.setFont(new Font("Tahoma", Font.PLAIN, 12));
//		GridBagConstraints gbc_lblBLUSelect = new GridBagConstraints();
//		gbc_lblBLUSelect.anchor = GridBagConstraints.WEST;
//		gbc_lblBLUSelect.insets = new Insets(0, 0, 5, 5);
//		gbc_lblBLUSelect.gridx = 6;
//		gbc_lblBLUSelect.gridy = 3;
//		systemSelectPanel.add(lblBLUSelect, gbc_lblBLUSelect);
//		
//		bluSelectQueryField = new JTextField();
//		bluSelectQueryField.setFont(new Font("Tahoma", Font.PLAIN, 11));
//		GridBagConstraints gbc_bluSelectQueryField = new GridBagConstraints();
//		gbc_bluSelectQueryField.insets = new Insets(0, 0, 5, 5);
//		gbc_bluSelectQueryField.anchor = GridBagConstraints.WEST;
//		gbc_bluSelectQueryField.gridwidth = 1;
//		gbc_bluSelectQueryField.gridx = 7;
//		gbc_bluSelectQueryField.gridy = 3;
//		systemSelectPanel.add(bluSelectQueryField, gbc_bluSelectQueryField);
//		if(engine.getEngineName().contains("HR"))
//			bluSelectQueryField.setText("SELECT DISTINCT ?BLU WHERE { {?Capability <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Capability>;}{?Consists <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Consists>;}{?Task <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Task>;}{?BLU <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/BusinessLogicUnit>} {?Task_Needs_BusinessLogicUnit <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Needs>}{?Capability ?Consists ?Task.}{?Task ?Task_Needs_BusinessLogicUnit ?BLU}}");
//		else
//			bluSelectQueryField.setText("SELECT DISTINCT ?BLU WHERE {{?Capability <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Capability>;}{?supports <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Supports> ;}{?BusinessProcess <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/BusinessProcess> ;}{ ?Capability ?supports ?BusinessProcess.} {?consists <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Consists>;} {?Activity <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/Activity> ;}{?BusinessProcess ?consists ?Activity.}{?needs <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Needs>;} {?BLU <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept/BusinessLogicUnit> ;}{?Activity ?needs ?BLU.}}");
//		bluSelectQueryField.setColumns(20);

	}
	@Override
	public void createAdvParamPanelsToggles()
	{
		super.createAdvParamPanelsToggles();

		showSystemSelectBtn = new ToggleButton("Select System Functionality");
		showSystemSelectBtn.setFont(new Font("Tahoma", Font.BOLD, 11));
		Style.registerTargetClassName(showSystemSelectBtn,  ".toggleButton");
		
		GridBagConstraints gbc_showSystemSelectBtn = new GridBagConstraints();
		gbc_showSystemSelectBtn.anchor = GridBagConstraints.WEST;
		gbc_showSystemSelectBtn.gridwidth = 2;
		gbc_showSystemSelectBtn.insets = new Insets(0, 0, 5, 5);
		gbc_showSystemSelectBtn.gridx = 6;
		gbc_showSystemSelectBtn.gridy = 3;
		ctlPanel.add(showSystemSelectBtn, gbc_showSystemSelectBtn);

	}
	@Override
	public void createAdvParamPanelsToggleListeners()
	{
		AdvParamListener saLis = new AdvParamListener();
		saLis.setPlaySheet(this);
		saLis.setParamButtons(showParamBtn,showSystemSelectBtn);
		showParamBtn.addActionListener(saLis);
		showSystemSelectBtn.addActionListener(saLis);
	}
	@Override
	public void createSpecificParamComponents()
	{
		yearField.setText("20");
		lblSoaSustainmentCost.setText("Annual Maint Exposed Data (%)");
		maxBudgetField.setText("500");
		
		hourlyRateField.setColumns(4);
		GridBagConstraints gbc_hourlyRateField = new GridBagConstraints();
		gbc_hourlyRateField.anchor = GridBagConstraints.NORTHWEST;
		gbc_hourlyRateField.insets = new Insets(0, 0, 5, 5);
		gbc_hourlyRateField.gridx = 1;
		gbc_hourlyRateField.gridy = 3;
		ctlPanel.add(hourlyRateField, gbc_hourlyRateField);

		JLabel lblHourlyRate = new JLabel("Hourly Build Cost Rate ($)");
		lblHourlyRate.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_lblHourlyRate = new GridBagConstraints();
		gbc_lblHourlyRate.anchor = GridBagConstraints.WEST;
		gbc_lblHourlyRate.gridwidth = 4;
		gbc_lblHourlyRate.insets = new Insets(0, 0, 5, 5);
		gbc_lblHourlyRate.gridx = 2;
		gbc_lblHourlyRate.gridy = 3;
		ctlPanel.add(lblHourlyRate, gbc_lblHourlyRate);

	}
	
	//must add in new button listener for sysopt
	public void addOptimizationBtnListener(JButton btnRunOptimization)
	{
		SysOptBtnListener obl = new SysOptBtnListener();
		obl.setOptPlaySheet(this);
		btnRunOptimization.addActionListener(obl);
	}
	
	@Override
	public void createSpecificDisplayComponents()
	{
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		chartPanel.add(tab3,  gbc_panel);
		
		GridBagConstraints gbc_panel2 = new GridBagConstraints();
		gbc_panel2.insets = new Insets(0, 0, 0, 5);
		gbc_panel2.fill = GridBagConstraints.BOTH;
		gbc_panel2.gridx = 1;
		gbc_panel2.gridy = 1;
		chartPanel.add(tab4,  gbc_panel2);

		GridBagConstraints gbc_panel3 = new GridBagConstraints();
		gbc_panel3.insets = new Insets(0, 0, 0, 5);
		gbc_panel3.fill = GridBagConstraints.BOTH;
		gbc_panel3.gridx = 0;
		gbc_panel3.gridy = 2;
		chartPanel.add(tab5,  gbc_panel3);

		GridBagConstraints gbc_panel4 = new GridBagConstraints();
		gbc_panel4.insets = new Insets(0, 0, 0, 5);
		gbc_panel4.fill = GridBagConstraints.BOTH;
		gbc_panel4.gridx = 1;
		gbc_panel4.gridy = 2;
		chartPanel.add(tab6,  gbc_panel4);
		
		JLabel lblAnnualBudget = new JLabel("Annual Budget During Transition:");
		GridBagConstraints gbc_lblAnnualBudget = new GridBagConstraints();
		gbc_lblAnnualBudget.insets = new Insets(0, 0, 5, 5);
		gbc_lblAnnualBudget.gridx = 4;
		gbc_lblAnnualBudget.gridy = 0;
		panel_1.add(lblAnnualBudget, gbc_lblAnnualBudget);
		
		annualBudgetLbl = new JLabel("");
		GridBagConstraints gbc_annualBudgetLbl = new GridBagConstraints();
		gbc_annualBudgetLbl.insets = new Insets(0, 0, 5, 5);
		gbc_annualBudgetLbl.gridx = 5;
		gbc_annualBudgetLbl.gridy = 0;
		panel_1.add(annualBudgetLbl, gbc_annualBudgetLbl);
		
		JLabel lblTimeSpentTransitioning = new JLabel("Number of Years for Transition:");
		GridBagConstraints gbc_lblTimeSpentTransitioning = new GridBagConstraints();
		gbc_lblTimeSpentTransitioning.anchor = GridBagConstraints.WEST;
		gbc_lblTimeSpentTransitioning.insets = new Insets(0, 0, 0, 5);
		gbc_lblTimeSpentTransitioning.gridx = 0;
		gbc_lblTimeSpentTransitioning.gridy = 1;
		panel_1.add(lblTimeSpentTransitioning, gbc_lblTimeSpentTransitioning);

		timeTransitionLbl = new JLabel("");
		GridBagConstraints gbc_timeTransitionLbl = new GridBagConstraints();
		gbc_timeTransitionLbl.anchor = GridBagConstraints.WEST;
		gbc_timeTransitionLbl.insets = new Insets(0, 0, 0, 5);
		gbc_timeTransitionLbl.gridx = 1;
		gbc_timeTransitionLbl.gridy = 1;
		panel_1.add(timeTransitionLbl, gbc_timeTransitionLbl);
	}
	
	/**
	 * Creates the user interface of the playsheet.
	 * Calls functions to create param panel and tabbed display panel
	 * Stitches the param and display panels together.
	 */
	public void createOptimizationTypeComponents()
	{

		rdbtnProfit = new JRadioButton("Savings");
		rdbtnProfit.setFont(new Font("Tahoma", Font.PLAIN, 12));
		rdbtnProfit.setSelected(true);
		GridBagConstraints gbc_rdbtnProfit = new GridBagConstraints();
		gbc_rdbtnProfit.gridwidth = 2;
		gbc_rdbtnProfit.anchor = GridBagConstraints.WEST;
		gbc_rdbtnProfit.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnProfit.gridx = 1;
		gbc_rdbtnProfit.gridy = 4;
		ctlPanel.add(rdbtnProfit, gbc_rdbtnProfit);

		rdbtnROI = new JRadioButton("ROI");
		rdbtnROI.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_rdbtnRoi = new GridBagConstraints();
		gbc_rdbtnRoi.anchor = GridBagConstraints.WEST;
		gbc_rdbtnRoi.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnRoi.gridx = 3;
		gbc_rdbtnRoi.gridy = 4;
		ctlPanel.add(rdbtnROI, gbc_rdbtnRoi);

		OptFunctionRadioBtnListener opl = new OptFunctionRadioBtnListener();
		rdbtnROI.addActionListener(opl);
		rdbtnProfit.addActionListener(opl);
		opl.setRadioBtn(rdbtnProfit, rdbtnROI);
	}
	
}
