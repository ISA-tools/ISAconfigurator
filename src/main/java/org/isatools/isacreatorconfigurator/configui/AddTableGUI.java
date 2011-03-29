/**
 ISAconfigurator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAconfigurator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 “The contents of this file are subject to the CPAL version 1.0 (the “License”); you may not use this file except
 in compliance with the License. You may obtain a copy of the License at http://isa-tools.org/licenses/ISAconfigurator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections 14 and 15 have been added to cover use of software over
 a computer network and provide for limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 consistent with Exhibit B.

 Software distributed under the License is distributed on an “AS IS” basis, WITHOUT WARRANTY OF ANY KIND, either express
 or implied. See the License for the specific language governing rights and limitations under the License.

 The Original Code is ISAconfigurator.
 The Original Developer is the Initial Developer. The Initial Developer of the Original Code is the ISA Team
 (Eamonn Maguire, eamonnmag@gmail.com; Philippe Rocca-Serra, proccaserra@gmail.com; Susanna-Assunta Sansone,
 sa.sanson@gmail.com; http://www.isa-tools.org). All portions of the code written by the ISA Team are
 Copyright (c) 2007-2011 ISA Team. All Rights Reserved.

 EXHIBIT B. Attribution Information
 Attribution Copyright Notice: Copyright (c) 2008-2011 ISA Team
 Attribution Phrase: Developed by the ISA Team
 Attribution URL: http://www.isa-tools.org
 Graphic Image provided in the Covered Code as file: http://isa-tools.org/licenses/icons/poweredByISAtools.png
 Display of Attribution Information is required in Larger Works which are defined in the CPAL as a work which combines
 Covered Code or portions thereof with code not governed by the terms of the CPAL.

 Sponsors:
 The ISA Team and the ISA software suite have been funded by the EU Carcinogenomics project (http://www.carcinogenomics.eu),
 the UK BBSRC (http://www.bbsrc.ac.uk), the UK NERC-NEBC (http://nebc.nerc.ac.uk) and in part by the EU NuGO consortium
 (http://www.nugo.org/everyone).
 */

package org.isatools.isacreatorconfigurator.configui;

import org.isatools.isacreatorconfigurator.common.UIHelper;
import org.isatools.isacreatorconfigurator.configdefinition.AssayTypes;
import org.isatools.isacreatorconfigurator.configdefinition.DataTypes;
import org.isatools.isacreatorconfigurator.configdefinition.DispatchTargets;
import org.isatools.isacreatorconfigurator.configdefinition.FieldObject;
import org.isatools.isacreatorconfigurator.effects.components.RoundedJTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;


/**
 * AddTableGUI provides the interface for users to use when adding a new table to the
 * ISAcreators definitions.
 *
 * @author Eamonn Maguire
 */
public class AddTableGUI extends JDialog {
    private static final String MEASEND_DEFAULT = "e.g. Gene Expression";
    private static final String TECHTYPE_DEFAULT = "e.g. DNA microarray";
    private static final String SOURCE_DEFAULT = "e.g. OBI";
    private static final String ACCESSION_DEFAULT = "e.g. 12345";

    private DataEntryPanel parentGUI;

    /**
     * AddTableGUI
     * GUI required to add a table to the ISAcreator configuration.
     *
     * @param parentGUI - the MainMenu component this utility will appear in.
     */
    public AddTableGUI(DataEntryPanel parentGUI) {
        this.parentGUI = parentGUI;
        setBackground(UIHelper.BG_COLOR);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                createGUI();
                pack();
            }
        });
    }

    /**
     * createGUI method creates the GUI for the the AddTableGUI component.
     */
    private void createGUI() {
        JPanel headerPanel = new JPanel(new GridLayout(1, 1));
        headerPanel.setBackground(UIHelper.BG_COLOR);

        JLabel header = new JLabel(new ImageIcon(getClass()
                .getResource("/images/general_gui/defineTable.png")),
                JLabel.RIGHT);
        header.setOpaque(false);
        headerPanel.add(header);
        add(headerPanel, BorderLayout.NORTH);

        JPanel container = new JPanel();
        container.setBackground(UIHelper.BG_COLOR);

        final JLabel status = UIHelper.createLabel("", UIHelper.VER_12_PLAIN, UIHelper.RED_COLOR);
        status.setHorizontalAlignment(SwingConstants.LEFT);
        status.setVerticalAlignment(SwingConstants.TOP);
        status.setPreferredSize(new Dimension(150, 50));

        final JPanel assayDefPanel = new JPanel();
        assayDefPanel.setLayout(new BoxLayout(assayDefPanel, BoxLayout.PAGE_AXIS));
        assayDefPanel.setEnabled(false);
        assayDefPanel.setBackground(UIHelper.BG_COLOR);

        final JLabel measEndLab = UIHelper.createLabel("measurement type :",
                UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR);
        measEndLab.setEnabled(false);

        final JTextField measEndText = new RoundedJTextField(10);
        measEndText.setText(MEASEND_DEFAULT);
        UIHelper.renderComponent(measEndText, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);
        measEndText.setEnabled(false);

        final JLabel measEndSourceLab = UIHelper.createLabel("term source :",
                UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR);
        measEndSourceLab.setEnabled(false);

        final JTextField measEndSourceText = new RoundedJTextField(10);
        measEndSourceText.setText(SOURCE_DEFAULT);
        UIHelper.renderComponent(measEndSourceText, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);
        measEndSourceText.setEnabled(false);

        final JLabel measEndAccessionLab = UIHelper.createLabel("term accession :",
                UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR);
        measEndAccessionLab.setEnabled(false);

        final JTextField measEndAccessionText = new RoundedJTextField(10);
        measEndAccessionText.setText(ACCESSION_DEFAULT);
        UIHelper.renderComponent(measEndAccessionText, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);
        measEndAccessionText.setEnabled(false);

        final JLabel techTypeLab = UIHelper.createLabel("technology type :",
                UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR);
        techTypeLab.setEnabled(false);

        final JTextField techTypeText = new RoundedJTextField(10);
        techTypeText.setText(TECHTYPE_DEFAULT);
        UIHelper.renderComponent(techTypeText, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);
        techTypeText.setEnabled(false);

        final JLabel techSourceLab = UIHelper.createLabel("term source ref :",
                UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR);
        techSourceLab.setEnabled(false);

        final JTextField techSourceText = new RoundedJTextField(10);
        techSourceText.setText(SOURCE_DEFAULT);
        UIHelper.renderComponent(techSourceText, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);
        techSourceText.setEnabled(false);

        final JLabel techAccessionLab = UIHelper.createLabel("term accession :",
                UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR);
        techAccessionLab.setEnabled(false);

        final JTextField techAccessionText = new RoundedJTextField(10);
        UIHelper.renderComponent(techAccessionText, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);
        techAccessionText.setText(ACCESSION_DEFAULT);
        techAccessionText.setEnabled(false);

        final JLabel assayTypeLab = UIHelper.createLabel("assay type :",
                UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR);
        assayTypeLab.setEnabled(false);

        final JComboBox assayType = new JComboBox(AssayTypes.asStringArray());
        UIHelper.renderComponent(assayType, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);
        assayType.setEnabled(false);

        final JLabel targetDispatchLab = UIHelper.createLabel("dispatch target :",
                UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR);
        targetDispatchLab.setEnabled(false);

        final JComboBox targetDispatch = new JComboBox(DispatchTargets.asStringArray());
        UIHelper.renderComponent(targetDispatch, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);
        targetDispatch.setEnabled(false);

        JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.PAGE_AXIS));
        fields.setOpaque(false);

        JPanel tableType = new JPanel(new GridLayout(1, 2));
        tableType.setOpaque(false);

        JLabel selectTypeLab = UIHelper.createLabel("select type of table:", UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR);

        tableType.add(selectTypeLab);

        final JComboBox typeCombo = new JComboBox(Location.values());
        UIHelper.setJComboBoxAsHeavyweight(typeCombo);
        typeCombo.setOpaque(false);
        typeCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {

                if (typeCombo.getSelectedIndex() == -1) {
                    typeCombo.setSelectedIndex(0);
                }

                boolean enabledFields = (typeCombo.getSelectedItem() == Location.ASSAY);

                measEndLab.setEnabled(enabledFields);
                measEndText.setEnabled(enabledFields);
                measEndSourceLab.setEnabled(enabledFields);
                measEndSourceText.setEnabled(enabledFields);
                measEndAccessionLab.setEnabled(enabledFields);
                measEndAccessionText.setEnabled(enabledFields);

                techTypeLab.setEnabled(enabledFields);
                techTypeText.setEnabled(enabledFields);
                techSourceLab.setEnabled(enabledFields);
                techSourceText.setEnabled(enabledFields);
                techAccessionLab.setEnabled(enabledFields);
                techAccessionText.setEnabled(enabledFields);

                assayTypeLab.setEnabled(enabledFields);
                assayType.setEnabled(enabledFields);
                targetDispatchLab.setEnabled(enabledFields);
                targetDispatch.setEnabled(enabledFields);
            }
        });
        UIHelper.renderComponent(typeCombo, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, UIHelper.BG_COLOR);
        tableType.add(typeCombo);
        typeCombo.setSelectedIndex(0);

        fields.add(tableType);
        fields.add(Box.createVerticalStrut(5));

        // define measurement/endpoint panel
        JPanel measEndPanel = new JPanel(new GridLayout(3, 2));
        measEndPanel.setBackground(UIHelper.BG_COLOR);
        measEndPanel.setOpaque(false);
        measEndPanel.add(measEndLab);
        measEndPanel.add(measEndText);
        measEndPanel.add(measEndSourceLab);
        measEndPanel.add(measEndSourceText);
        measEndPanel.add(measEndAccessionLab);
        measEndPanel.add(measEndAccessionText);
        assayDefPanel.add(measEndPanel);
        assayDefPanel.add(Box.createVerticalStrut(5));

        // define technologytype panel
        JPanel techTypePanel = new JPanel(new GridLayout(3, 2));
        techTypePanel.setOpaque(false);
        techTypePanel.add(techTypeLab);
        techTypePanel.add(techTypeText);
        techTypePanel.add(techSourceLab);
        techTypePanel.add(techSourceText);
        techTypePanel.add(techAccessionLab);
        techTypePanel.add(techAccessionText);
        assayDefPanel.add(techTypePanel);

        JPanel assaySettingsPanel = new JPanel(new GridLayout(2, 2));
        assaySettingsPanel.setBackground(UIHelper.BG_COLOR);
        assaySettingsPanel.setOpaque(false);
        assaySettingsPanel.add(targetDispatchLab);
        assaySettingsPanel.add(targetDispatch);
        assaySettingsPanel.add(assayTypeLab);
        assaySettingsPanel.add(assayType);
        assayDefPanel.add(assaySettingsPanel);

        fields.add(assayDefPanel);
        fields.add(Box.createVerticalStrut(5));

        JPanel refNamePanel = new JPanel(new GridLayout(1, 2));
        refNamePanel.setBackground(UIHelper.BG_COLOR);

        JLabel refNameLab = UIHelper.createLabel("table name:", UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR);
        refNamePanel.add(refNameLab);

        final JTextField refNameText = new RoundedJTextField(10);
        UIHelper.renderComponent(refNameText, UIHelper.VER_12_PLAIN, UIHelper.GREY_COLOR, false);

        refNamePanel.add(refNameText);

        fields.add(refNamePanel);
        fields.add(Box.createVerticalStrut(5));

        container.add(fields, BorderLayout.CENTER);

        add(container, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.PAGE_AXIS));
        southPanel.setBackground(UIHelper.BG_COLOR);
        southPanel.add(UIHelper.wrapComponentInPanel(status));
        southPanel.add(Box.createVerticalStrut(5));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.setBackground(UIHelper.BG_COLOR);

        JLabel cancel = new JLabel(new ImageIcon(getClass()
                .getResource("/images/general_gui/cancel.png")),
                JLabel.LEFT);
        cancel.setOpaque(false);
        cancel.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                parentGUI.getApplicationContainer().hideSheet();
                measEndText.setText(MEASEND_DEFAULT);
                techTypeText.setText(TECHTYPE_DEFAULT);
                refNameText.setText("");
                status.setText("<html><p></p><p></p></html>");
            }
        });

        JLabel add = new JLabel(new ImageIcon(getClass()
                .getResource("/images/general_gui/addtablebutton.png")),
                JLabel.RIGHT);
        add.setOpaque(false);
        add.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                String measType = "n/a";
                String techType = "n/a";

                if (typeCombo.getSelectedItem() != Location.STUDY_SAMPLE && typeCombo.getSelectedItem() != Location.INVESTIGATION) {
                    measType = (measEndText.getText().equals(MEASEND_DEFAULT))
                            ? "" : measEndText.getText();
                    techType = (techTypeText.getText()
                            .equals(TECHTYPE_DEFAULT)) ? ""
                            : techTypeText.getText();
                }

                if (measType.equals("")) {
                    status.setText(
                            "<html><b>Table not added!</b> <p> Required fields not entered!</p></html>");
                    status.setForeground(UIHelper.RED_COLOR);

                    return;
                }

                if (refNameText.getText().trim().equals("")) {
                    status.setText(
                            "<html><b>Invalid table name!</b> <p> Table name is blank!</p></html>");
                    status.setForeground(UIHelper.RED_COLOR);

                    return;
                }

                List<FieldObject> initialFieldsList = null;

                // populate initial fields with the default fields
                Fields fieldList = parentGUI.getFields();

                List<String> defaultFields = fieldList.getDefaultFieldsByLocation(Location.resolveLocationIdentifier(typeCombo.getSelectedItem().toString()));

                if (defaultFields.size() > 0) {
                    initialFieldsList = new ArrayList<FieldObject>();

                    for (String fieldName : defaultFields) {
                        FieldObject tfo = new FieldObject(fieldName, "", DataTypes.STRING, "", true, false, false);
                        tfo.setWizardTemplate(fieldList.getDefaultWizardTemplateForField(fieldName));
                        initialFieldsList.add(tfo);
                    }
                }

                String assayTypeValue = (typeCombo.getSelectedItem() == Location.STUDY_SAMPLE || typeCombo.getSelectedItem() == Location.INVESTIGATION)
                        ? "" : assayType.getSelectedItem().toString();

                String dispatchTargetValue = (typeCombo.getSelectedItem() == Location.STUDY_SAMPLE || typeCombo.getSelectedItem() == Location.INVESTIGATION)
                        ? "" : targetDispatch.getSelectedItem().toString();

                boolean added = parentGUI.addTable(typeCombo.getSelectedItem().toString(),
                        measType, measEndSourceText.getText(), measEndAccessionText.getText(), techType, techSourceText.getText(), techAccessionText.getText(),
                        refNameText.getText(), initialFieldsList, assayTypeValue, dispatchTargetValue);

                if (!added) {
                    status.setText(
                            "<html><b>Table not added!</b> <p> Check name and properties!</p></html>");
                    status.setForeground(UIHelper.RED_COLOR);

                } else {
                    status.setText("");
                    status.setForeground(UIHelper.DARK_GREEN_COLOR);
                    parentGUI.getApplicationContainer().hideSheet();
                }
            }
        });

        buttonPanel.add(cancel);
        buttonPanel.add(add);

        southPanel.add(buttonPanel);
        southPanel.add(Box.createVerticalGlue());

        add(southPanel, BorderLayout.SOUTH);
    }

}