/*******************************************************************************
 * Copyright 2015 SEMOSS.ORG
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
package prerna.ui.components.specific.tap;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;

import prerna.ui.components.BrowserGraphPanel;
import prerna.ui.main.listener.specific.tap.OptFunctionRadioBtnListener;
import prerna.ui.main.listener.specific.tap.SysOptBtnListener;
import prerna.ui.main.listener.specific.tap.UpdateDataBLUListListener;
import prerna.ui.swing.custom.CustomButton;
import prerna.ui.swing.custom.ToggleButton;
import aurelienribon.ui.css.Style;

/**
 * This is the playsheet used exclusively for TAP service optimization.
 */
@SuppressWarnings("serial")
public class SysOptPlaySheet extends OptPlaySheet {
	
	private SysOptCheckboxListUpdater checkboxListUpdater;
	
	public JCheckBox includeRegionalizationCheckbox;
	public JCheckBox garrTheaterCheckbox;
	
	// toggles to show the system Functionality and Capability Functionality panels
	public JToggleButton showSystemSelectBtn, showSystemCapSelectBtn, showSystemModDecomBtn;
	// panel that holds the system,data,blu and capability selectors if needed
	public JPanel systemDataBLUSelectPanel;
	public JPanel systemModDecomSelectPanel;
	// individual components of the panel
	public DHMSMSystemSelectPanel systemSelectPanel, systemModernizePanel, systemDecomissionPanel;
	public DHMSMCapabilitySelectPanel capabilitySelectPanel;
	public DHMSMDataBLUSelectPanel dataBLUSelectPanel;
	
	// toggle to show the data/blu panel (dataBLUSelectPanel) within the systemDataBLUSelectPanel
	public JToggleButton updateDataBLUPanelButton;
	
	// overall analysis tab

	public JRadioButton rdbtnProfit, rdbtnROI, rdbtnIRR;
	public JLabel solutionLbl, irrLbl, annualBudgetLbl, timeTransitionLbl;
	public BrowserGraphPanel replacementHeatMap, geoSpatialMap;
	public JPanel currentFuncPanel, futureFuncPanel, replacementHeatMapPanel, sysCapOptionsPanel, sysCapDisplayPanel,
			geoSpatialOptionsPanel;
	public static JComboBox<String> capComboBox, sysComboBox, geoCapComboBox;
	public static JButton createSysCapButton, createGeoSpatialMapButton;

	@Override
	public void createData() {
		//create all the data needed for the system, capability, data, and blu scroll lsits
		checkboxListUpdater = new SysOptCheckboxListUpdater(engine);
		
	}
	
	@Override
	protected void createAdvParamPanels() {
		super.createAdvParamPanels();
		
		includeRegionalizationCheckbox = new JCheckBox("Include Regionalization");
		GridBagConstraints gbc_includeRegionalizationCheckbox = new GridBagConstraints();
		gbc_includeRegionalizationCheckbox.gridwidth = 2;
		gbc_includeRegionalizationCheckbox.insets = new Insets(0, 0, 5, 20);
		gbc_includeRegionalizationCheckbox.gridx = 0;
		gbc_includeRegionalizationCheckbox.gridy = 5;
		advParamPanel.add(includeRegionalizationCheckbox, gbc_includeRegionalizationCheckbox);
		
		garrTheaterCheckbox = new JCheckBox("Ignore Theater/Garrison Tag");
		GridBagConstraints gbc_garrTheaterCheckbox = new GridBagConstraints();
		gbc_garrTheaterCheckbox.gridwidth = 3;
		gbc_garrTheaterCheckbox.insets = new Insets(0, 5, 5, 20);
		gbc_garrTheaterCheckbox.gridx = 2;
		gbc_garrTheaterCheckbox.gridy = 5;
		advParamPanel.add(garrTheaterCheckbox, gbc_garrTheaterCheckbox);
		
		systemDataBLUSelectPanel = new JPanel();
		systemDataBLUSelectPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		GridBagConstraints gbc_systemDataBLUSelectPanel = new GridBagConstraints();
		gbc_systemDataBLUSelectPanel.gridheight = 6;
		gbc_systemDataBLUSelectPanel.fill = GridBagConstraints.BOTH;
		gbc_systemDataBLUSelectPanel.gridx = 8;
		gbc_systemDataBLUSelectPanel.gridy = 0;
		ctlPanel.add(systemDataBLUSelectPanel, gbc_systemDataBLUSelectPanel);
		
		GridBagLayout gbl_systemDataBLUSelectPanel = new GridBagLayout();
		gbl_systemDataBLUSelectPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_systemDataBLUSelectPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl_systemDataBLUSelectPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_systemDataBLUSelectPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		systemDataBLUSelectPanel.setLayout(gbl_systemDataBLUSelectPanel);
		
		systemSelectPanel = new DHMSMSystemSelectPanel(checkboxListUpdater);
		GridBagConstraints gbc_systemSelectPanel = new GridBagConstraints();
		gbc_systemSelectPanel.gridheight = 6;
		gbc_systemSelectPanel.fill = GridBagConstraints.BOTH;
		gbc_systemSelectPanel.gridx = 0;
		gbc_systemSelectPanel.gridy = 0;
		systemDataBLUSelectPanel.add(systemSelectPanel, gbc_systemSelectPanel);
		
		capabilitySelectPanel = new DHMSMCapabilitySelectPanel(checkboxListUpdater);
		capabilitySelectPanel.setVisible(false);
		GridBagConstraints gbc_capabilitySelectPanel = new GridBagConstraints();
		gbc_capabilitySelectPanel.gridheight = 6;
		gbc_capabilitySelectPanel.fill = GridBagConstraints.BOTH;
		gbc_capabilitySelectPanel.gridx = 1;
		gbc_capabilitySelectPanel.gridy = 0;
		systemDataBLUSelectPanel.add(capabilitySelectPanel, gbc_capabilitySelectPanel);
		
		updateDataBLUPanelButton = new ToggleButton("View Data/BLU");
		updateDataBLUPanelButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		Style.registerTargetClassName(updateDataBLUPanelButton, ".toggleButton");
		
		GridBagConstraints gbc_updateDataBLUPanelButton = new GridBagConstraints();
		gbc_updateDataBLUPanelButton.anchor = GridBagConstraints.WEST;
		gbc_updateDataBLUPanelButton.gridheight = 2;
		gbc_updateDataBLUPanelButton.insets = new Insets(10, 0, 5, 5);
		gbc_updateDataBLUPanelButton.gridx = 2;
		gbc_updateDataBLUPanelButton.gridy = 0;
		systemDataBLUSelectPanel.add(updateDataBLUPanelButton, gbc_updateDataBLUPanelButton);
		
		dataBLUSelectPanel = new DHMSMDataBLUSelectPanel();
		dataBLUSelectPanel.setVisible(false);
		GridBagConstraints gbc_dataBLUSelectPanel = new GridBagConstraints();
		gbc_dataBLUSelectPanel.gridheight = 6;
		gbc_dataBLUSelectPanel.fill = GridBagConstraints.BOTH;
		gbc_dataBLUSelectPanel.gridx = 3;
		gbc_dataBLUSelectPanel.gridy = 0;
		systemDataBLUSelectPanel.add(dataBLUSelectPanel, gbc_dataBLUSelectPanel);
		dataBLUSelectPanel.addElements(systemSelectPanel);
		
		// instantiate systemModDecomSelectPanel, systemModernizePanel, systemDecomissionPanel
		systemModDecomSelectPanel = new JPanel();
		systemModDecomSelectPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		systemModDecomSelectPanel.setVisible(false);
		
		GridBagConstraints gbc_systemModDecomSelectPanel = new GridBagConstraints();
		gbc_systemModDecomSelectPanel.gridheight = 6;
		gbc_systemModDecomSelectPanel.fill = GridBagConstraints.BOTH;
		gbc_systemModDecomSelectPanel.gridx = 8;
		gbc_systemModDecomSelectPanel.gridy = 0;
		ctlPanel.add(systemModDecomSelectPanel, gbc_systemModDecomSelectPanel);
		
		GridBagLayout gbl_systemModDecomSelectPanel = new GridBagLayout();
		gbl_systemModDecomSelectPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_systemModDecomSelectPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl_systemModDecomSelectPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_systemModDecomSelectPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		systemModDecomSelectPanel.setLayout(gbl_systemModDecomSelectPanel);
		
		systemModernizePanel = new DHMSMSystemSelectPanel("Select Systems that MUST be modernized:",true,checkboxListUpdater);
		GridBagConstraints gbc_systemModernizePanel = new GridBagConstraints();
		gbc_systemModernizePanel.gridheight = 6;
		gbc_systemModernizePanel.fill = GridBagConstraints.BOTH;
		gbc_systemModernizePanel.gridx = 0;
		gbc_systemModernizePanel.gridy = 0;
		systemModDecomSelectPanel.add(systemModernizePanel, gbc_systemModernizePanel);
		
		systemDecomissionPanel = new DHMSMSystemSelectPanel("Select Systems that MUST be decommissioned:",true,checkboxListUpdater);
		GridBagConstraints gbc_systemDecomissionPanel = new GridBagConstraints();
		gbc_systemDecomissionPanel.gridheight = 6;
		gbc_systemDecomissionPanel.fill = GridBagConstraints.BOTH;
		gbc_systemDecomissionPanel.gridx = 1;
		gbc_systemDecomissionPanel.gridy = 0;
		systemModDecomSelectPanel.add(systemDecomissionPanel, gbc_systemDecomissionPanel);
		
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
				}
			}
		});
		
	}
	
	@Override
	protected void createAdvParamPanelsToggles() {
		super.createAdvParamPanelsToggles();
		
		GridBagConstraints gbc_showParamBtn = new GridBagConstraints();
		gbc_showParamBtn.anchor = GridBagConstraints.NORTHWEST;
		gbc_showParamBtn.gridwidth = 2;
		gbc_showParamBtn.insets = new Insets(0, 0, 5, 5);
		gbc_showParamBtn.gridx = 6;
		gbc_showParamBtn.gridy = 6;
		ctlPanel.add(showParamBtn, gbc_showParamBtn);
		
		showSystemSelectBtn = new ToggleButton("Select System Functionality");
		showSystemSelectBtn.setName("showSystemSelectBtn");
		showSystemSelectBtn.setFont(new Font("Tahoma", Font.BOLD, 11));
		Style.registerTargetClassName(showSystemSelectBtn, ".toggleButton");
		
		GridBagConstraints gbc_showSystemSelectBtn = new GridBagConstraints();
		gbc_showSystemSelectBtn.anchor = GridBagConstraints.NORTHWEST;
		gbc_showSystemSelectBtn.gridwidth = 2;
		gbc_showSystemSelectBtn.insets = new Insets(0, 0, 5, 5);
		gbc_showSystemSelectBtn.gridx = 6;
		gbc_showSystemSelectBtn.gridy = 3;
		ctlPanel.add(showSystemSelectBtn, gbc_showSystemSelectBtn);
		
		showSystemCapSelectBtn = new ToggleButton("Select Capability Functionality");
		showSystemCapSelectBtn.setName("showSystemCapSelectBtn");
		showSystemCapSelectBtn.setFont(new Font("Tahoma", Font.BOLD, 11));
		Style.registerTargetClassName(showSystemCapSelectBtn, ".toggleButton");
		
		GridBagConstraints gbc_showSystemCapSelectBtn = new GridBagConstraints();
		gbc_showSystemCapSelectBtn.anchor = GridBagConstraints.NORTHWEST;
		gbc_showSystemCapSelectBtn.gridwidth = 2;
		gbc_showSystemCapSelectBtn.insets = new Insets(0, 0, 5, 5);
		gbc_showSystemCapSelectBtn.gridx = 6;
		gbc_showSystemCapSelectBtn.gridy = 4;
		ctlPanel.add(showSystemCapSelectBtn, gbc_showSystemCapSelectBtn);
		
		showSystemModDecomBtn = new ToggleButton("Manually Set Systems");
		showSystemModDecomBtn.setName("showSystemModDecomBtn");
		showSystemModDecomBtn.setFont(new Font("Tahoma", Font.BOLD, 11));
		Style.registerTargetClassName(showSystemModDecomBtn, ".toggleButton");
		
		GridBagConstraints gbc_showSystemModDecomBtn = new GridBagConstraints();
		gbc_showSystemModDecomBtn.anchor = GridBagConstraints.NORTHWEST;
		gbc_showSystemModDecomBtn.gridwidth = 2;
		gbc_showSystemModDecomBtn.insets = new Insets(0, 0, 5, 5);
		gbc_showSystemModDecomBtn.gridx = 6;
		gbc_showSystemModDecomBtn.gridy = 5;
		ctlPanel.add(showSystemModDecomBtn, gbc_showSystemModDecomBtn);
	}
	
	@Override
	protected void createAdvParamPanelsToggleListeners() {
		
		showParamBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(showParamBtn.isSelected()) {
					showSystemSelectBtn.setSelected(false);
					showSystemCapSelectBtn.setSelected(false);
					showSystemModDecomBtn.setSelected(false);
					systemDataBLUSelectPanel.setVisible(false);
					systemModDecomSelectPanel.setVisible(false);
					advParamPanel.setVisible(true);
				} else
					advParamPanel.setVisible(false);
			}
		});
		
		showSystemSelectBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(showSystemSelectBtn.isSelected()) {
					showParamBtn.setSelected(false);
					showSystemCapSelectBtn.setSelected(false);
					showSystemModDecomBtn.setSelected(false);
					advParamPanel.setVisible(false);
					systemModDecomSelectPanel.setVisible(false);
					systemDataBLUSelectPanel.setVisible(true);
					capabilitySelectPanel.setVisible(false);
					capabilitySelectPanel.clearList();
					//if data or BLU was selected, show it. otherwise hide panel
					if(dataBLUSelectPanel.noneSelected()) {
						updateDataBLUPanelButton.setSelected(false);
						dataBLUSelectPanel.setVisible(false);
					} else {
						updateDataBLUPanelButton.setSelected(true);
						dataBLUSelectPanel.setVisible(true);
						dataBLUSelectPanel.setFromSystem(true);
					}
				} else {
					systemDataBLUSelectPanel.setVisible(false);
				}
			}
		});
		
		showSystemCapSelectBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {

				if(showSystemCapSelectBtn.isSelected()) {
					showParamBtn.setSelected(false);
					showSystemSelectBtn.setSelected(false);
					showSystemModDecomBtn.setSelected(false);
					advParamPanel.setVisible(false);
					systemModDecomSelectPanel.setVisible(false);
					systemDataBLUSelectPanel.setVisible(true);
					capabilitySelectPanel.setVisible(true);
					//if data or BLU was selected, show it. otherwise hide panel
					if(dataBLUSelectPanel.noneSelected()) {
						updateDataBLUPanelButton.setSelected(false);
						dataBLUSelectPanel.setVisible(false);
					} else {
						updateDataBLUPanelButton.setSelected(true);
						dataBLUSelectPanel.setVisible(true);
						dataBLUSelectPanel.setFromSystem(false);					}
				} else {
					systemDataBLUSelectPanel.setVisible(false);
				}
			}
		});
		
		showSystemModDecomBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				if(showSystemModDecomBtn.isSelected()) {
					showParamBtn.setSelected(false);
					showSystemSelectBtn.setSelected(false);
					showSystemCapSelectBtn.setSelected(false);
					advParamPanel.setVisible(false);
					systemDataBLUSelectPanel.setVisible(false);
					systemModDecomSelectPanel.setVisible(true);
				} else {
					systemModDecomSelectPanel.setVisible(false);
				}
			}
		});
		ActionListener viewDataBLUPanelListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// if the updateDataBLUPanelButton is unselected by user, hide the panel
				if (!updateDataBLUPanelButton.isSelected()) {
					dataBLUSelectPanel.setVisible(false);
					dataBLUSelectPanel.clearList();
				}
				// otherwise, if the updateDataBLUPanelButton is selected or the user clicks to update the list
				else {
					dataBLUSelectPanel.setVisible(true);
					if (showSystemSelectBtn.isSelected())
						dataBLUSelectPanel.setFromSystem(true);
					else
						dataBLUSelectPanel.setFromSystem(false);
				}
			}
		};
		updateDataBLUPanelButton.addActionListener(viewDataBLUPanelListener);
		
		UpdateDataBLUListListener updateDataBLUListener = new UpdateDataBLUListListener();
		updateDataBLUListener.setEngine(engine);
		updateDataBLUListener.setUpDHMSMHelper();
		updateDataBLUListener.setComponents(systemSelectPanel, capabilitySelectPanel, dataBLUSelectPanel, showSystemSelectBtn);
		updateDataBLUListener.setUpdateButtons(dataBLUSelectPanel.updateProvideDataBLUButton, dataBLUSelectPanel.updateConsumeDataBLUButton,
				dataBLUSelectPanel.updateComplementDataBLUButton);
		dataBLUSelectPanel.updateProvideDataBLUButton.addActionListener(updateDataBLUListener);
		dataBLUSelectPanel.updateConsumeDataBLUButton.addActionListener(updateDataBLUListener);
		dataBLUSelectPanel.updateComplementDataBLUButton.addActionListener(updateDataBLUListener);
	}
	
	@Override
	protected void createBasicParamComponents() {
		
		super.createBasicParamComponents();
		lblSoaSustainmentCost.setText("Annual Maint Exposed Data (%)");
		maxBudgetField.setText("500");
		
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.anchor = GridBagConstraints.SOUTH;
		gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBar.gridwidth = 2;
		gbc_progressBar.insets = new Insets(0, 0, 0, 5);
		gbc_progressBar.gridx = 6;
		gbc_progressBar.gridy = 0;
		ctlPanel.add(progressBar, gbc_progressBar);
		progressBar.setVisible(false);
		
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
	
	@Override
	protected void addOptimizationBtnListener(JButton btnRunOptimization) {
		SysOptBtnListener obl = new SysOptBtnListener();
		obl.setOptPlaySheet(this);
		btnRunOptimization.addActionListener(obl);
	}
	
	/**
	 * Creates the user interface of the playsheet. Calls functions to create param panel and tabbed display panel Stitches
	 * the param and display panels together.
	 */
	@Override
	protected void createOptimizationTypeComponents() {
		
		rdbtnProfit = new JRadioButton("Savings");
		rdbtnProfit.setName("rdbtnProfit");
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
		rdbtnROI.setName("rdbtnROI");
		rdbtnROI.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_rdbtnRoi = new GridBagConstraints();
		gbc_rdbtnRoi.anchor = GridBagConstraints.WEST;
		gbc_rdbtnRoi.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnRoi.gridx = 3;
		gbc_rdbtnRoi.gridy = 4;
		ctlPanel.add(rdbtnROI, gbc_rdbtnRoi);
		
		rdbtnIRR = new JRadioButton("IRR");
		rdbtnIRR.setName("rdbtnIRR");
		rdbtnIRR.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_rdbtnIRR = new GridBagConstraints();
		gbc_rdbtnIRR.anchor = GridBagConstraints.WEST;
		gbc_rdbtnIRR.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnIRR.gridx = 5;
		gbc_rdbtnIRR.gridy = 4;
		ctlPanel.add(rdbtnIRR, gbc_rdbtnIRR);
		
		OptFunctionRadioBtnListener opl = new OptFunctionRadioBtnListener();
		rdbtnROI.addActionListener(opl);
		rdbtnProfit.addActionListener(opl);
		rdbtnIRR.addActionListener(opl);
		opl.setSerOptRadioBtn(rdbtnProfit, rdbtnROI, rdbtnIRR);
	}
	
	
	
	@Override
	protected void createDisplayPanel() {
		super.createDisplayPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 2;
		chartPanel.add(tab3, gbc_panel);
		
		GridBagConstraints gbc_panel2 = new GridBagConstraints();
		gbc_panel2.insets = new Insets(0, 0, 0, 5);
		gbc_panel2.fill = GridBagConstraints.BOTH;
		gbc_panel2.gridx = 1;
		gbc_panel2.gridy = 2;
		chartPanel.add(tab4, gbc_panel2);
		
		GridBagConstraints gbc_panel3 = new GridBagConstraints();
		gbc_panel3.insets = new Insets(0, 0, 0, 5);
		gbc_panel3.fill = GridBagConstraints.BOTH;
		gbc_panel3.gridx = 0;
		gbc_panel3.gridy = 3;
		chartPanel.add(tab5, gbc_panel3);
		
		GridBagConstraints gbc_panel4 = new GridBagConstraints();
		gbc_panel4.insets = new Insets(0, 0, 0, 5);
		gbc_panel4.fill = GridBagConstraints.BOTH;
		gbc_panel4.gridx = 1;
		gbc_panel4.gridy = 3;
		chartPanel.add(tab6, gbc_panel4);
		
		solutionLbl = new JLabel("");
		solutionLbl.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_solutionLbl = new GridBagConstraints();
		gbc_solutionLbl.insets = new Insets(0, 0, 5, 5);
		gbc_solutionLbl.gridx = 0;
		gbc_solutionLbl.gridwidth = 5;
		gbc_solutionLbl.gridy = 0;
		panel_1.add(solutionLbl, gbc_solutionLbl);
		
		JLabel lblIRR = new JLabel("Internal Rate of Return:");
		GridBagConstraints gbc_lblIRR = new GridBagConstraints();
		gbc_lblIRR.insets = new Insets(0, 30, 5, 5);
		gbc_lblIRR.gridx = 4;
		gbc_lblIRR.gridy = 1;
		panel_1.add(lblIRR, gbc_lblIRR);
		
		irrLbl = new JLabel("");
		GridBagConstraints gbc_IRRLbl = new GridBagConstraints();
		gbc_IRRLbl.insets = new Insets(0, 0, 5, 5);
		gbc_IRRLbl.gridx = 5;
		gbc_IRRLbl.gridy = 1;
		panel_1.add(irrLbl, gbc_IRRLbl);
		
		JLabel lblTimeSpentTransitioning = new JLabel("Number of Years for Transition:");
		GridBagConstraints gbc_lblTimeSpentTransitioning = new GridBagConstraints();
		gbc_lblTimeSpentTransitioning.anchor = GridBagConstraints.WEST;
		gbc_lblTimeSpentTransitioning.insets = new Insets(0, 0, 0, 5);
		gbc_lblTimeSpentTransitioning.gridx = 0;
		gbc_lblTimeSpentTransitioning.gridy = 2;
		panel_1.add(lblTimeSpentTransitioning, gbc_lblTimeSpentTransitioning);
		
		timeTransitionLbl = new JLabel("");
		GridBagConstraints gbc_timeTransitionLbl = new GridBagConstraints();
		gbc_timeTransitionLbl.anchor = GridBagConstraints.WEST;
		gbc_timeTransitionLbl.insets = new Insets(0, 0, 0, 5);
		gbc_timeTransitionLbl.gridx = 1;
		gbc_timeTransitionLbl.gridy = 2;
		panel_1.add(timeTransitionLbl, gbc_timeTransitionLbl);
		
		JLabel lblAnnualBudget = new JLabel("Annual Budget During Transition:");
		GridBagConstraints gbc_lblAnnualBudget = new GridBagConstraints();
		gbc_lblAnnualBudget.insets = new Insets(0, 30, 5, 5);
		gbc_lblAnnualBudget.gridx = 4;
		gbc_lblAnnualBudget.gridy = 2;
		panel_1.add(lblAnnualBudget, gbc_lblAnnualBudget);
		
		annualBudgetLbl = new JLabel("");
		GridBagConstraints gbc_annualBudgetLbl = new GridBagConstraints();
		gbc_annualBudgetLbl.insets = new Insets(0, 0, 5, 5);
		gbc_annualBudgetLbl.gridx = 5;
		gbc_annualBudgetLbl.gridy = 2;
		panel_1.add(annualBudgetLbl, gbc_annualBudgetLbl);
		
		specificSysAlysPanel = new JPanel();
		tabbedPane.addTab("System Analysis", null, specificSysAlysPanel, null);
		GridBagLayout gbl_specificSysAlysPanel = new GridBagLayout();
		gbl_specificSysAlysPanel.columnWidths = new int[] { 0, 0 };
		gbl_specificSysAlysPanel.rowHeights = new int[] { 0, 0 };
		gbl_specificSysAlysPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_specificSysAlysPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		specificSysAlysPanel.setLayout(gbl_specificSysAlysPanel);
		
		currentFuncPanel = new JPanel();
		tabbedPane.addTab("As-Is Functionality", null, currentFuncPanel, null);
		GridBagLayout gbl_specificFuncAlysPanel = new GridBagLayout();
		gbl_specificFuncAlysPanel.columnWidths = new int[] { 0, 0 };
		gbl_specificFuncAlysPanel.rowHeights = new int[] { 0, 0 };
		gbl_specificFuncAlysPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_specificFuncAlysPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		currentFuncPanel.setLayout(gbl_specificFuncAlysPanel);
		
		futureFuncPanel = new JPanel();
		tabbedPane.addTab("Future Functionality", null, futureFuncPanel, null);
		GridBagLayout gbl_futureFuncPanel = new GridBagLayout();
		gbl_futureFuncPanel.columnWidths = new int[] { 0, 0 };
		gbl_futureFuncPanel.rowHeights = new int[] { 0, 0 };
		gbl_futureFuncPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_futureFuncPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		currentFuncPanel.setLayout(gbl_futureFuncPanel);
		
		replacementHeatMapPanel = new JPanel();
		tabbedPane.addTab("Replacement Heat Map", null, replacementHeatMapPanel, null);
		GridBagLayout gbl_replacementHeatMapPanel = new GridBagLayout();
		gbl_replacementHeatMapPanel.columnWidths = new int[] { 0, 0 };
		gbl_replacementHeatMapPanel.rowHeights = new int[] { 0, 0 };
		gbl_replacementHeatMapPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_replacementHeatMapPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		replacementHeatMapPanel.setLayout(gbl_replacementHeatMapPanel);
		
		replacementHeatMap = new BrowserGraphPanel("/html/MHS-RDFSemossCharts/app/heatmap.html");
		
		GridBagConstraints gbc_replacementHeatMap = new GridBagConstraints();
		gbc_replacementHeatMap.insets = new Insets(0, 0, 0, 5);
		gbc_replacementHeatMap.fill = GridBagConstraints.BOTH;
		gbc_replacementHeatMap.gridx = 0;
		gbc_replacementHeatMap.gridy = 0;
		replacementHeatMapPanel.add(replacementHeatMap, gbc_replacementHeatMap);
		
		// Components for Capability System Comparison Panel
		JPanel sysCapPanel = new JPanel();
		tabbedPane.addTab("Capability System Comparison", null, sysCapPanel, null);
		GridBagLayout gbl_sysCapPanel = new GridBagLayout();
		gbl_sysCapPanel.columnWidths = new int[] { 0, 0, 10 };
		gbl_sysCapPanel.rowHeights = new int[] { 0, 0, 10 };
		gbl_sysCapPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_sysCapPanel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		sysCapPanel.setLayout(gbl_sysCapPanel);
		
		sysCapOptionsPanel = new JPanel();
		sysCapOptionsPanel.setBackground(SystemColor.control);
		GridBagConstraints gbc_sysCapOptionsPanel = new GridBagConstraints();
		gbc_sysCapOptionsPanel.anchor = GridBagConstraints.NORTH;
		gbc_sysCapOptionsPanel.gridwidth = 1;
		gbc_sysCapOptionsPanel.insets = new Insets(0, 0, 5, 5);
		gbc_sysCapOptionsPanel.gridx = 0;
		gbc_sysCapOptionsPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_sysCapOptionsPanel.gridy = 0;
		sysCapPanel.add(sysCapOptionsPanel, gbc_sysCapOptionsPanel);
		
		sysCapDisplayPanel = new JPanel();
		sysCapDisplayPanel.setBackground(SystemColor.control);
		GridBagConstraints gbc_sysCapDisplayPanel = new GridBagConstraints();
		gbc_sysCapDisplayPanel.anchor = GridBagConstraints.SOUTH;
		gbc_sysCapDisplayPanel.gridwidth = 1;
		gbc_sysCapDisplayPanel.insets = new Insets(0, 0, 5, 5);
		gbc_sysCapDisplayPanel.gridx = 0;
		gbc_sysCapDisplayPanel.fill = GridBagConstraints.BOTH;
		gbc_sysCapDisplayPanel.gridy = 1;
		sysCapPanel.add(sysCapDisplayPanel, gbc_sysCapDisplayPanel);
		
		JLabel capLabel = new JLabel("Capability:");
		GridBagConstraints gbc_capLabel = new GridBagConstraints();
		gbc_capLabel.anchor = GridBagConstraints.WEST;
		gbc_capLabel.insets = new Insets(0, 0, 5, 5);
		gbc_capLabel.gridx = 0;
		gbc_capLabel.gridy = 0;
		sysCapOptionsPanel.add(capLabel, gbc_capLabel);
		
		capComboBox = new JComboBox<String>();
		GridBagConstraints gbc_capComboBox = new GridBagConstraints();
		gbc_capComboBox.gridwidth = 2;
		gbc_capComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_capComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_capComboBox.gridx = 1;
		gbc_capComboBox.gridy = 0;
		sysCapOptionsPanel.add(capComboBox, gbc_capComboBox);
		
		JLabel sysLabel = new JLabel("System:");
		GridBagConstraints gbc_sysLabel = new GridBagConstraints();
		gbc_sysLabel.anchor = GridBagConstraints.WEST;
		gbc_sysLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sysLabel.gridx = 0;
		gbc_sysLabel.gridy = 1;
		sysCapOptionsPanel.add(sysLabel, gbc_sysLabel);
		
		sysComboBox = new JComboBox<String>();
		GridBagConstraints gbc_sysComboBox = new GridBagConstraints();
		gbc_sysComboBox.gridwidth = 2;
		gbc_sysComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_sysComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_sysComboBox.gridx = 1;
		gbc_sysComboBox.gridy = 1;
		sysCapOptionsPanel.add(sysComboBox, gbc_sysComboBox);
		
		createSysCapButton = new CustomButton("Create");
		createSysCapButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_createSysCapButton = new GridBagConstraints();
		gbc_createSysCapButton.anchor = GridBagConstraints.WEST;
		gbc_createSysCapButton.insets = new Insets(0, 0, 5, 5);
		gbc_createSysCapButton.gridx = 0;
		gbc_createSysCapButton.gridy = 2;
		sysCapOptionsPanel.add(createSysCapButton, gbc_createSysCapButton);
		Style.registerTargetClassName(createSysCapButton, ".standardButton");
		
		JLabel noteLabel = new JLabel("* notifies systems that are inferred to be related to said capability");
		GridBagConstraints gbc_noteLabel = new GridBagConstraints();
		gbc_noteLabel.anchor = GridBagConstraints.SOUTH;
		gbc_noteLabel.insets = new Insets(0, 0, 5, 5);
		gbc_noteLabel.gridx = 0;
		gbc_noteLabel.gridy = 0;
		sysCapOptionsPanel.add(noteLabel, gbc_noteLabel);
		
		// Components for GeoSpatial Map
		JPanel geoSpatialPanel = new JPanel();
		tabbedPane.addTab("GeoSpatial Map", null, geoSpatialPanel, null);
		GridBagLayout gbl_geoSpatialPanel = new GridBagLayout();
		gbl_geoSpatialPanel.columnWidths = new int[] { 0, 0, 10 };
		gbl_geoSpatialPanel.rowHeights = new int[] { 0, 0, 10 };
		gbl_geoSpatialPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_geoSpatialPanel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		geoSpatialPanel.setLayout(gbl_geoSpatialPanel);
		
		geoSpatialOptionsPanel = new JPanel();
		geoSpatialOptionsPanel.setBackground(SystemColor.control);
		GridBagConstraints gbc_geoSpatialOptionsPanel = new GridBagConstraints();
		gbc_geoSpatialOptionsPanel.anchor = GridBagConstraints.NORTH;
		gbc_geoSpatialOptionsPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_geoSpatialOptionsPanel.insets = new Insets(0, 0, 5, 5);
		gbc_geoSpatialOptionsPanel.gridx = 0;
		gbc_geoSpatialOptionsPanel.gridy = 0;
		geoSpatialPanel.add(geoSpatialOptionsPanel, gbc_geoSpatialOptionsPanel);
		
		geoSpatialMap = new BrowserGraphPanel("/html/MHS-RDFSemossCharts/app/worldmap.html");
		
		GridBagConstraints gbc_geoSpatialMap = new GridBagConstraints();
		gbc_geoSpatialMap.insets = new Insets(0, 0, 0, 5);
		gbc_geoSpatialMap.fill = GridBagConstraints.BOTH;
		gbc_geoSpatialMap.gridx = 0;
		gbc_geoSpatialMap.gridy = 1;
		geoSpatialPanel.add(geoSpatialMap, gbc_geoSpatialMap);
		
		JLabel capLabel2 = new JLabel("Capability:");
		GridBagConstraints gbc_capLabel2 = new GridBagConstraints();
		gbc_capLabel2.anchor = GridBagConstraints.WEST;
		gbc_capLabel2.insets = new Insets(0, 0, 5, 5);
		gbc_capLabel2.gridx = 0;
		gbc_capLabel2.gridy = 0;
		geoSpatialOptionsPanel.add(capLabel2, gbc_capLabel2);
		
		geoCapComboBox = new JComboBox<String>();
		GridBagConstraints gbc_geoCapComboBox = new GridBagConstraints();
		gbc_geoCapComboBox.gridwidth = 2;
		gbc_geoCapComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_geoCapComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_geoCapComboBox.gridx = 1;
		gbc_geoCapComboBox.gridy = 0;
		geoSpatialOptionsPanel.add(geoCapComboBox, gbc_geoCapComboBox);
		
		createGeoSpatialMapButton = new CustomButton("Create");
		createGeoSpatialMapButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_createGeoSpatialMapButton = new GridBagConstraints();
		gbc_createGeoSpatialMapButton.anchor = GridBagConstraints.WEST;
		gbc_createGeoSpatialMapButton.insets = new Insets(0, 0, 5, 5);
		gbc_createGeoSpatialMapButton.gridx = 0;
		gbc_createGeoSpatialMapButton.gridy = 1;
		geoSpatialOptionsPanel.add(createGeoSpatialMapButton, gbc_createSysCapButton);
		Style.registerTargetClassName(createGeoSpatialMapButton, ".standardButton");
	}
	
	/**
	 * Clears panels within the playsheet
	 */
	@Override
	public void clearPanels() {
		super.clearPanels();
		currentFuncPanel.removeAll();
	}
	
	/**
	 * Sets N/A or $0 for values in optimizations. Allows for different TAP algorithms to be run as empty functions.
	 */
	@Override
	public void clearLabels() {
		super.clearLabels();
		irrLbl.setText("N/A");
		annualBudgetLbl.setText("$0");
		timeTransitionLbl.setText("N/A");
	}
	
	public SysOptCheckboxListUpdater getCheckboxListUpdater() {
		return checkboxListUpdater;
	}
	
}
