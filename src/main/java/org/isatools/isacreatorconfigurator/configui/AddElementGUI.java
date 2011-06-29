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

import org.isatools.isacreator.common.DropDownComponent;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.effects.components.RoundedJTextField;
import org.isatools.isacreator.ontologyselectiontool.OntologySelectionTool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Map;


public class AddElementGUI extends JDialog {

    private DataEntryPanel parentGUI;
    private JLabel status;
    private JPanel dataPanel;
    private JPanel structuralSelectPanel;
    private JPanel predefinedFieldSelectPanel;
    private JPanel customFieldSelectPanel;
    private JComboBox structuralValue;
    private JComboBox fieldValue;
    private JComboBox customFieldType;
    private JTextField customFieldQualifier;
    private JRadioButton addFieldElement;
    private JRadioButton customField;
    private JRadioButton addStructuralElement;
    private JLabel selectStructureLab;
    private JPanel fieldCont;

    private Location currentTableType;

    private static String[] structureElements;
    private static String[] sectionElements;

    static {
        try {
            SimpleXMLParser structureFileParser = new SimpleXMLParser(AddElementGUI.class.getResourceAsStream(File.separator + "config" + File.separator + "structure_fields.xml"));
            structureFileParser.parseStructureFile();
            structureElements = structureFileParser.getParsedValues();

            SimpleXMLParser sectionFileParser = new SimpleXMLParser(AddElementGUI.class.getResourceAsStream(File.separator + "config" + File.separator + "section_fields.xml"));
            sectionFileParser.parseStructureFile();
            sectionElements = sectionFileParser.getParsedValues();

        } catch (Exception e) {
            System.err.println("Problem encountered loading XML for Structural and section elements.");
            sectionElements = new String[]{"Unable to load sections"};
            structureElements = new String[]{"Unable to load structures"};
        }
    }

    public AddElementGUI(DataEntryPanel parentGUI) {

        this.parentGUI = parentGUI;
        setBackground(UIHelper.BG_COLOR);
        createGUI();
        pack();
    }

    public void createGUI() {
        // container for all elements
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
        container.setBackground(UIHelper.BG_COLOR);


        // add title header (add field)
        JPanel titleCont = new JPanel(new GridLayout(1, 1));
        titleCont.setBackground(UIHelper.BG_COLOR);

        JLabel titleHeader = new JLabel(new ImageIcon(getClass().getResource("/images/general_gui/addElement.png")), JLabel.RIGHT);
        titleHeader.setOpaque(false);

        titleCont.add(titleHeader);

        container.add(titleCont);
        container.add(Box.createVerticalStrut(5));

        // add selection for structural or field element
        JPanel elementTypeCont = new JPanel(new GridLayout(1, 2));
        elementTypeCont.setBackground(UIHelper.BG_COLOR);

        addFieldElement = new JRadioButton("field");
        UIHelper.renderComponent(addFieldElement, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);
        addFieldElement.setSelected(true);

        addFieldElement.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        dataPanel.remove(structuralSelectPanel);
                        dataPanel.add(fieldCont, BorderLayout.CENTER);
                        predefinedFieldSelectPanel.repaint();
                        dataPanel.getParent().validate();
                    }
                });
            }
        });

        addStructuralElement = new JRadioButton("structural: ");


        UIHelper.renderComponent(addStructuralElement, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);

        addStructuralElement.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        dataPanel.remove(fieldCont);
                        dataPanel.add(structuralSelectPanel, BorderLayout.CENTER);
                        structuralSelectPanel.repaint();
                        dataPanel.getParent().validate();
                    }
                });
            }
        });

        ButtonGroup bg = new ButtonGroup();
        bg.add(addFieldElement);
        bg.add(addStructuralElement);

        elementTypeCont.add(addFieldElement);
        elementTypeCont.add(addStructuralElement);

        container.add(elementTypeCont);

        // add entry panel for data
        dataPanel = new JPanel(new BorderLayout());
        dataPanel.setOpaque(false);

        // structural panel definition
        structuralSelectPanel = new JPanel(new BorderLayout());
        structuralSelectPanel.setBackground(UIHelper.BG_COLOR);
        structuralSelectPanel.setPreferredSize(new Dimension(300, 30));

        JPanel fieldContainer = new JPanel(new GridLayout(1, 2));
        fieldContainer.setOpaque(false);


        selectStructureLab = UIHelper.createLabel("select structure: ", UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR);

        structuralValue = new JComboBox(structureElements);
        UIHelper.setJComboBoxAsHeavyweight(structuralValue);
        UIHelper.renderComponent(structuralValue, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, UIHelper.BG_COLOR);

        fieldContainer.add(selectStructureLab);
        fieldContainer.add(structuralValue);

        structuralSelectPanel.add(fieldContainer, BorderLayout.NORTH);

        JPanel fieldType = new JPanel(new GridLayout(1, 2));
        fieldType.setOpaque(false);

        JRadioButton predefinedField = new JRadioButton("predefined field", true);
        predefinedField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                fieldCont.remove(customFieldSelectPanel);
                fieldCont.add(predefinedFieldSelectPanel);
                fieldCont.repaint();
                fieldCont.getParent().validate();
            }
        });

        customField = new JRadioButton("custom field", false);
        customField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                fieldCont.remove(predefinedFieldSelectPanel);
                fieldCont.add(customFieldSelectPanel);
                fieldCont.repaint();
                fieldCont.getParent().validate();
            }
        });
        UIHelper.renderComponent(predefinedField, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, UIHelper.BG_COLOR);
        UIHelper.renderComponent(customField, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, UIHelper.BG_COLOR);

        ButtonGroup group = new ButtonGroup();
        group.add(predefinedField);
        group.add(customField);

        fieldType.add(predefinedField);
        fieldType.add(customField);

        fieldCont = new JPanel(new GridLayout(2, 1));
        fieldCont.setOpaque(false);

        fieldCont.add(fieldType);


        // field panel definition
        predefinedFieldSelectPanel = new JPanel(new GridLayout(1, 2));
        predefinedFieldSelectPanel.setBackground(UIHelper.BG_COLOR);
        predefinedFieldSelectPanel.setPreferredSize(new Dimension(300, 30));

        JLabel selectFieldLab = UIHelper.createLabel("select field: ", UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR);

        fieldValue = new JComboBox();
        UIHelper.setJComboBoxAsHeavyweight(fieldValue);
        UIHelper.renderComponent(fieldValue, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, UIHelper.BG_COLOR);

        predefinedFieldSelectPanel.add(selectFieldLab);
        predefinedFieldSelectPanel.add(fieldValue);


        // custom field panel definition
        // field panel definition
        customFieldSelectPanel = new JPanel(new GridLayout(1, 2));
        customFieldSelectPanel.setBackground(UIHelper.BG_COLOR);
        customFieldSelectPanel.setPreferredSize(new Dimension(300, 30));

        customFieldType = new JComboBox();
        UIHelper.setJComboBoxAsHeavyweight(customFieldType);
        UIHelper.renderComponent(customFieldType, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);


        customFieldQualifier = new RoundedJTextField(10);
        customFieldQualifier.setText("Enter Qualifier");
        UIHelper.renderComponent(customFieldQualifier, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);

        customFieldSelectPanel.add(customFieldType);
        customFieldSelectPanel.add(createOntologyDropDown(customFieldQualifier, false, null));

        fieldCont.add(predefinedFieldSelectPanel);

        dataPanel.add(fieldCont);

        container.add(dataPanel);

        // add status panel
        JPanel statusPanel = new JPanel(new GridLayout(1, 1));
        statusPanel.setBackground(UIHelper.BG_COLOR);

        status = new JLabel("", JLabel.CENTER);
        UIHelper.renderComponent(status, UIHelper.VER_12_PLAIN, UIHelper.GREY_COLOR, false);
        status.setPreferredSize(new Dimension(300, 70));

        statusPanel.add(status);

        container.add(statusPanel);

        // add button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.setBackground(UIHelper.BG_COLOR);

        JLabel cancelButton = new JLabel(new ImageIcon(getClass().getResource("/images/general_gui/cancel.png")), JLabel.LEFT);
        cancelButton.setOpaque(false);

        cancelButton.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                status.setText("");
                parentGUI.getApplicationContainer().hideSheet();
            }
        });

        JLabel okButton = new JLabel(new ImageIcon(getClass().getResource("/images/general_gui/ok.png")), JLabel.RIGHT);
        okButton.setOpaque(false);

        okButton.addMouseListener(new MouseAdapter() {


            public void mousePressed(MouseEvent event) {
                // attempt to add field

                Display toAdd;
                if (addFieldElement.isSelected()) {
                    if (customField.isSelected()) {
                        String fieldName = customFieldQualifier.getText();
                        if (fieldName.equals("") || fieldName.equals("Enter Qualifier")) {
                            status.setForeground(UIHelper.RED_COLOR);
                            status.setText("<html><b>Problem</b>: invalid qualifier specified</html>");
                            return;
                        }
                        toAdd = new FieldElement(
                                new FieldObject(customFieldType.getSelectedItem().toString() + "[" + fieldName + "]",
                                        "", DataTypes.STRING, "", false, false, false));
                    } else {
                        toAdd = new FieldElement(new FieldObject(fieldValue.getSelectedItem().toString(),
                                "", DataTypes.STRING, "", false, false, false));
                    }
                } else {
                    if (currentTableType == Location.INVESTIGATION) {
                        toAdd = new SectionDisplay(structuralValue.getSelectedItem().toString());
                    } else {
                        toAdd = new StructuralElementDisplay(structuralValue.getSelectedItem().toString());
                    }
                }
                if (parentGUI.addField(toAdd)) {
                    parentGUI.getApplicationContainer().hideSheet();
                } else {
                    status.setText("<html><b>Problem:</b><p>This Field/Structural element is already defined for this table...</p></html>");
                }
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);

        container.add(buttonPanel);

        add(container);
    }

    /**
     * Create an DropDownComponent field.
     *
     * @param field                     - JTextField to be associated with the OntologySelectionTool.
     * @param allowsMultiple            - Should the OntologySelectionTool allow multiple terms to be selected.
     * @param recommendedOntologySource - A recommended ontology source.
     * @return DropDownComponent object.
     */
    protected DropDownComponent createOntologyDropDown(final JTextField field,
                                                       boolean allowsMultiple, Map<String, RecommendedOntology> recommendedOntologySource) {
        final OntologySelectionTool ost = new OntologySelectionTool(
                allowsMultiple, recommendedOntologySource);
        ost.createGUI();

        final DropDownComponent dropdown = new DropDownComponent(field, ost, DropDownComponent.ONTOLOGY);
        ost.addPropertyChangeListener("selectedOntology",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        dropdown.hidePopup(ost);
                        String value = evt.getNewValue().toString();
                        // for this section, we are only storing the term at the minute, not the entire unique id
                        // returned from the ontology lookup tool!
                        value = value.contains(":") ? value.substring(value.indexOf(":") + 1) : value;
                        field.setText(value);
                    }
                });

        ost.addPropertyChangeListener("noSelectedOntology",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        dropdown.hidePopup(ost);
                    }
                });

        return dropdown;
    }


    public void updateFieldList(String[] fields) {

        fieldValue.removeAllItems();

        String[] fieldsCopy = fields.clone();

        for (String fc : fieldsCopy) {
            fieldValue.addItem(fc);
        }
    }

    public void updateCustomFieldList(String[] fields) {

        customFieldType.removeAllItems();

        String[] fieldsCopy = fields.clone();

        for (String fc : fieldsCopy) {
            customFieldType.addItem(fc);
        }
    }


    public void setCurrentTableType(Location currentTableType) {
        this.currentTableType = currentTableType;


        addStructuralElement.setText((currentTableType == Location.INVESTIGATION) ? "sections" : "structural");
        selectStructureLab.setText("select " + ((currentTableType == Location.INVESTIGATION) ? "section:" : "structure: "));
        structuralValue.removeAllItems();

        for (String section : currentTableType == Location.INVESTIGATION ? sectionElements : structureElements) {
            structuralValue.addItem(section);
        }
    }

}
