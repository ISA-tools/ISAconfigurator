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

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.isatools.isacreator.common.*;
import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.effects.borders.RoundedBorder;
import org.isatools.isacreator.effects.components.RoundedFormattedTextField;
import org.isatools.isacreator.effects.components.RoundedJTextArea;
import org.isatools.isacreator.effects.components.RoundedJTextField;
import org.isatools.isacreator.ontologymanager.BioPortalClient;
import org.isatools.isacreator.ontologymanager.OLSClient;
import org.isatools.isacreator.ontologymanager.OntologyService;
import org.isatools.isacreator.ontologyselectiontool.OntologySelectionTool;
import org.isatools.isacreatorconfigurator.ontologyconfigurationtool.OntologyConfigUI;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.*;
import java.util.List;


public class FieldInterface extends JLayeredPane implements ActionListener,
        Serializable {

    private static final String UNIT_STR = "Unit";
    private static final String PROTOCOL_STR = "Protocol REF";
    private static final String DEFAULT_VAL_STR = "Default Value:";
    private static final String PROTOCOL_TYPE_STR = "Protocol Type:";

    @InjectedResource
    private ImageIcon ontologyConfigIcon, ontologyConfigIconOver, fieldDefinitionHeader, preferredOntologiesSidePanelIcon, checkRegExIcon;

    private static String[] ontologyColumnHeaders;
    // Holds assigned datatype, so that a comparison can be made to see if it's been changed between user interactions.
    private static List<Ontology> ontologiesToQuery;

    private JCheckBox acceptsFileLocations;
    private JCheckBox acceptsMultipleValues;


    //private JCheckBox isFixedLength;
    private JCheckBox isInputFormatted, usesTemplateForWizard, required, recommendOntologySource, hidden;
    private JLabel defaultValLabStd;
    private JComboBox datatype;
    private JComboBox defaultValBool;

    private JPanel defaultValContBool;
    private JPanel defaultValContStd;
    private JPanel inputFormatCont;
    private JPanel listDataSourceCont;
    private JPanel preferredOntologySource;
    private JPanel sourceEntryPanel;
    private JPanel wizardTemplatePanel;

    // these panels are globally visible since they need their visibility to be set depending on whether or not validation
    // is required and whether or not multiple values need to be entered
    private Box defaultValCont;
    private JTextArea description;
    private JTextArea listValues;


    // fields for entry
    private RoundedFormattedTextField defaultValStd;
    private JTextField fieldName;
    private JTextField inputFormat;
    private JTextArea wizardTemplate;
    private DataEntryPanel main;
    private JTable ontologiesToUse;
    private DefaultTableModel ontologiesToUseModel;
    private static OntologyConfigUI ontologyConfig;
    private Map<String, RecommendedOntology> selectedOntologies = new ListOrderedMap<String, RecommendedOntology>();
    private FieldElement field;

    static {
        OntologyService bpc = new BioPortalClient();
        ontologiesToQuery = new ArrayList<Ontology>();
        if (bpc.getAllOntologies() != null) {
            ontologiesToQuery.addAll(bpc.getAllOntologies());
        }

        OntologyService olsClient = new OLSClient();

        ontologiesToQuery.addAll(olsClient.getAllOntologies());
        ontologyColumnHeaders = new String[]{"ontology", "search under"};
    }

    public FieldInterface(DataEntryPanel main) {
        this.main = main;
    }

    public void createGUI() {
        ResourceInjector.get("config-ui-package.style").inject(this);
        instantiateFrame("");
    }

    private void instantiateFields(String initFieldName) {
        // OVERALL CONTAINER
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
        container.setBackground(UIHelper.BG_COLOR);

        JLabel fieldDefinitionLab = new JLabel(fieldDefinitionHeader, JLabel.CENTER);
        container.add(fieldDefinitionLab);

        container.add(Box.createVerticalStrut(15));

        // FIELD LABEL & INPUT BOX CONTAINER
        JPanel fieldCont = new JPanel(new GridLayout(1, 2));
        fieldCont.setBackground(UIHelper.BG_COLOR);

        fieldName = new RoundedJTextField(15);
        fieldName.setText(initFieldName);
        fieldName.setEditable(false);
        UIHelper.renderComponent(fieldName, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        JLabel fieldNameLab = UIHelper.createLabel("Field Name: ", UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR);
        fieldCont.add(fieldNameLab);
        fieldCont.add(fieldName);
        container.add(fieldCont);

        JPanel descCont = new JPanel(new GridLayout(1, 2));
        descCont.setBackground(UIHelper.BG_COLOR);
        description = new RoundedJTextArea();
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        UIHelper.renderComponent(description, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        JScrollPane descScroll = new JScrollPane(description,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        descScroll.setBackground(UIHelper.BG_COLOR);
        descScroll.setPreferredSize(new Dimension(150, 65));
        descScroll.getViewport().setBackground(UIHelper.BG_COLOR);

        IAppWidgetFactory.makeIAppScrollPane(descScroll);

        JLabel descLab = UIHelper.createLabel("Description: ", UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR);
        descLab.setVerticalAlignment(JLabel.TOP);
        descCont.add(descLab);
        descCont.add(descScroll);
        container.add(descCont);

        // add datatype information
        JPanel datatypeCont = new JPanel(new GridLayout(1, 2));
        datatypeCont.setBackground(UIHelper.BG_COLOR);

        DataTypes[] allowedDataTypes = initFieldName.equals(UNIT_STR) ? new DataTypes[]{DataTypes.ONTOLOGY_TERM} : DataTypes.values();

        datatype = new JComboBox(allowedDataTypes);
        datatype.addActionListener(this);
        UIHelper.renderComponent(datatype, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, UIHelper.BG_COLOR);

        JLabel dataTypeLab = UIHelper.createLabel("Datatype:", UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR);
        datatypeCont.add(dataTypeLab);
        datatypeCont.add(datatype);
        container.add(datatypeCont);

        defaultValContStd = new JPanel(new GridLayout(1, 2));
        defaultValContStd.setBackground(UIHelper.BG_COLOR);

        defaultValCont = Box.createHorizontalBox();
        defaultValCont.setPreferredSize(new Dimension(150, 25));

        defaultValStd = new RoundedFormattedTextField(null, UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR, UIHelper.DARK_GREEN_COLOR);
        defaultValCont.setPreferredSize(new Dimension(120, 25));
        defaultValStd.setFormatterFactory(new DefaultFormatterFactory(
                new RegExFormatter(".*", defaultValStd, false, UIHelper.DARK_GREEN_COLOR, UIHelper.RED_COLOR, UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR, UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR)));
        defaultValStd.setForeground(UIHelper.DARK_GREEN_COLOR);
        defaultValStd.setFont(UIHelper.VER_11_PLAIN);

        defaultValCont.add(defaultValStd);

        defaultValLabStd = UIHelper.createLabel(DEFAULT_VAL_STR, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR);

        defaultValContStd.add(defaultValLabStd);
        defaultValContStd.add(defaultValCont);
        container.add(defaultValContStd);

        defaultValContBool = new JPanel(new GridLayout(1, 2));
        defaultValContBool.setBackground(UIHelper.BG_COLOR);
        defaultValContBool.setVisible(false);

        listDataSourceCont = new JPanel(new GridLayout(2, 1));
        listDataSourceCont.setBackground(UIHelper.BG_COLOR);
        listDataSourceCont.setVisible(false);

        JLabel listValLab = UIHelper.createLabel(
                "Please enter comma separated list of values:", UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR);
        listDataSourceCont.add(listValLab);

        listValues = new RoundedJTextArea("SampleVal1, SampleVal2, SampleVal3", 3, 5);
        listValues.setLineWrap(true);
        listValues.setWrapStyleWord(true);
        UIHelper.renderComponent(listValues, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        JScrollPane listScroll = new JScrollPane(listValues,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        listScroll.setBackground(UIHelper.BG_COLOR);
        listScroll.getViewport().setBackground(UIHelper.BG_COLOR);

        listDataSourceCont.add(listScroll);
        container.add(listDataSourceCont);

        IAppWidgetFactory.makeIAppScrollPane(listScroll);

        sourceEntryPanel = new JPanel(new BorderLayout());
        sourceEntryPanel.setSize(new Dimension(125, 190));
        sourceEntryPanel.setOpaque(false);
        sourceEntryPanel.setVisible(false);

        preferredOntologySource = new JPanel();
        preferredOntologySource.setLayout(new BoxLayout(preferredOntologySource, BoxLayout.PAGE_AXIS));
        preferredOntologySource.setVisible(false);
        recommendOntologySource = new JCheckBox("Use recommended ontology source?", false);

        UIHelper.renderComponent(recommendOntologySource, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        recommendOntologySource.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (recommendOntologySource.isSelected()) {
                    sourceEntryPanel.setVisible(true);
                } else {
                    sourceEntryPanel.setVisible(false);
                }
            }
        });

        JPanel useOntologySourceCont = new JPanel(new GridLayout(1, 1));
        useOntologySourceCont.add(recommendOntologySource);

        preferredOntologySource.add(useOntologySourceCont);


        JPanel infoCont = new JPanel(new GridLayout(1, 1));
        JLabel infoLab = UIHelper.createLabel("<html>click on the <strong>configure ontologies</strong> button to open the ontology configurator to edit the list of ontologies and search areas within an ontology</html>", UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR);
        infoLab.setPreferredSize(new Dimension(100, 40));
        infoCont.add(infoLab);

        sourceEntryPanel.add(infoCont, BorderLayout.NORTH);

        JLabel preferredOntologiesLab = new JLabel(preferredOntologiesSidePanelIcon);
        preferredOntologiesLab.setVerticalAlignment(SwingConstants.TOP);

        sourceEntryPanel.add(preferredOntologiesLab, BorderLayout.WEST);

        ontologiesToUseModel = new DefaultTableModel(new String[0][2], ontologyColumnHeaders) {
            @Override
            public boolean isCellEditable(int i, int i1) {
                return false;
            }
        };
        ontologiesToUse = new JTable(ontologiesToUseModel);
        ontologiesToUse.getTableHeader().setBackground(UIHelper.BG_COLOR);

        try {
            ontologiesToUse.setDefaultRenderer(Class.forName("java.lang.Object"), new CustomSpreadsheetCellRenderer());
        } catch (ClassNotFoundException e) {
            // empty
        }

        renderTableHeader();

        JScrollPane ontologiesToUseScroller = new JScrollPane(ontologiesToUse, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ontologiesToUseScroller.setBorder(new EmptyBorder(0, 0, 0, 0));
        ontologiesToUseScroller.setPreferredSize(new Dimension(100, 80));
        ontologiesToUseScroller.setBackground(UIHelper.BG_COLOR);
        ontologiesToUseScroller.getViewport().setBackground(UIHelper.BG_COLOR);

        IAppWidgetFactory.makeIAppScrollPane(ontologiesToUseScroller);

        sourceEntryPanel.add(ontologiesToUseScroller);

        JPanel buttonCont = new JPanel(new BorderLayout());
        final JLabel openConfigButton = new JLabel(ontologyConfigIcon);
        openConfigButton.setVerticalAlignment(SwingConstants.TOP);
        openConfigButton.setHorizontalAlignment(SwingConstants.RIGHT);

        MouseAdapter showOntologyConfigurator = new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                openConfigButton.setIcon(ontologyConfigIconOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                openConfigButton.setIcon(ontologyConfigIcon);
            }

            public void mousePressed(MouseEvent mouseEvent) {

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (openConfigButton.isEnabled()) {
                            openConfigButton.setIcon(ontologyConfigIcon);
                            ontologyConfig = new OntologyConfigUI(ontologiesToQuery, selectedOntologies);
                            ontologyConfig.addPropertyChangeListener("ontologySelected", new PropertyChangeListener() {
                                public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                                    selectedOntologies = (ListOrderedMap<String, RecommendedOntology>) propertyChangeEvent.getNewValue();
                                    updateTable();
                                }
                            });
                            showPopupInCenter(ontologyConfig);
                        }
                    }
                });
            }

        };

        openConfigButton.addMouseListener(showOntologyConfigurator);

        buttonCont.add(openConfigButton, BorderLayout.EAST);

        sourceEntryPanel.add(buttonCont, BorderLayout.SOUTH);
        preferredOntologySource.add(sourceEntryPanel);
        container.add(preferredOntologySource);

        String[] contents = new String[]{"true", "false"};
        defaultValBool = new

                JComboBox(contents);

        UIHelper.renderComponent(defaultValBool, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, UIHelper.BG_COLOR);

        JLabel defaultValLabBool = UIHelper.createLabel(DEFAULT_VAL_STR, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR);

        defaultValContBool.add(defaultValLabBool);
        defaultValContBool.add(defaultValBool);
        container.add(defaultValContBool);

        // RegExp data entry
        isInputFormatted = new JCheckBox("Is the input formatted?", false);
        isInputFormatted.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        isInputFormatted.setHorizontalAlignment(SwingConstants.LEFT);

        UIHelper.renderComponent(isInputFormatted, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        isInputFormatted.addActionListener(this);
        container.add(UIHelper.wrapComponentInPanel(isInputFormatted));

        inputFormatCont = new JPanel();

        inputFormatCont.setLayout(new BoxLayout(inputFormatCont, BoxLayout.LINE_AXIS));
        inputFormatCont.setVisible(false);
        inputFormatCont.setBackground(UIHelper.BG_COLOR);
        inputFormat = new RoundedJTextField(10);
        inputFormat.setText(".*");
        inputFormat.setSize(new Dimension(150, 19));

        inputFormat.setPreferredSize(new Dimension(160, 25));
        inputFormat.setToolTipText(
                "Field expects a regular expression describing the input format.");
        UIHelper.renderComponent(inputFormat, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        JLabel inputFormatLab = UIHelper.createLabel("Input format:", UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR);
        inputFormatLab.setVerticalAlignment(SwingConstants.TOP);

        inputFormatCont.add(inputFormatLab);
        inputFormatCont.add(inputFormat);
        JLabel checkRegExp = new JLabel(checkRegExIcon,
                JLabel.RIGHT);
        checkRegExp.setOpaque(false);
        checkRegExp.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                String regexToCheck = inputFormat.getText();

                final CheckRegExGUI regexChecker = new CheckRegExGUI(regexToCheck);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        regexChecker.createGUI();
                    }
                });
                regexChecker.addPropertyChangeListener("close",
                        new PropertyChangeListener() {
                            public void propertyChange(PropertyChangeEvent evt) {
                                main.getApplicationContainer().hideSheet();
                            }
                        });
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        main.getApplicationContainer().showJDialogAsSheet(regexChecker);
                    }
                });
            }

        }

        );
        inputFormatCont.add(checkRegExp);
        container.add(inputFormatCont);

        usesTemplateForWizard = new JCheckBox("Requires template for wizard?", false);
        usesTemplateForWizard.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        usesTemplateForWizard.setHorizontalAlignment(SwingConstants.LEFT);

        UIHelper.renderComponent(usesTemplateForWizard, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        usesTemplateForWizard.addActionListener(this);
        container.add(UIHelper.wrapComponentInPanel(usesTemplateForWizard));

        wizardTemplatePanel = new JPanel(new GridLayout(1, 2));
        wizardTemplatePanel.setVisible(false);
        wizardTemplatePanel.setBackground(UIHelper.BG_COLOR);

        wizardTemplate = new RoundedJTextArea();
        wizardTemplate.setToolTipText(
                "A template for the wizard to auto-create the data...");
        wizardTemplate.setLineWrap(true);
        wizardTemplate.setWrapStyleWord(true);
        UIHelper.renderComponent(wizardTemplate, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        JScrollPane wizScroll = new JScrollPane(wizardTemplate,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        wizScroll.getViewport().setBackground(UIHelper.BG_COLOR);
        wizScroll.setPreferredSize(new Dimension(70, 60));

        IAppWidgetFactory.makeIAppScrollPane(wizScroll);

        JLabel wizardTemplateLab = UIHelper.createLabel("Template definition:", UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR);
        wizardTemplateLab.setVerticalAlignment(JLabel.TOP);

        wizardTemplatePanel.add(wizardTemplateLab);
        wizardTemplatePanel.add(wizScroll);

        container.add(wizardTemplatePanel);

        JPanel checkCont = new JPanel(new GridLayout(2, 2));
        checkCont.setBackground(UIHelper.BG_COLOR);
        checkCont.setBorder(new TitledBorder(new RoundedBorder(UIHelper.DARK_GREEN_COLOR, 3),
                "Behavioural Attributes", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, UIHelper.VER_11_BOLD,
                UIHelper.DARK_GREEN_COLOR));

        required = new JCheckBox("Required ", true);

        UIHelper.renderComponent(required, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        checkCont.add(required);

        acceptsMultipleValues = new JCheckBox("Allow multiple instances", false);

        UIHelper.renderComponent(acceptsMultipleValues, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        checkCont.add(acceptsMultipleValues);
        acceptsFileLocations = new JCheckBox("Accepts file locations", false);

        acceptsFileLocations.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                acceptsMultipleValues.setSelected(false);
                acceptsMultipleValues.setEnabled(!acceptsFileLocations.isSelected());
            }
        });

        UIHelper.renderComponent(acceptsFileLocations, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        checkCont.add(acceptsFileLocations);

        hidden = new JCheckBox("hidden?", false);

        UIHelper.renderComponent(hidden, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        checkCont.add(hidden);

        container.add(checkCont);
        container.add(Box.createGlue());

        JScrollPane contScroll = new JScrollPane(container,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contScroll.setBorder(null);

        IAppWidgetFactory.makeIAppScrollPane(contScroll);

        add(contScroll, BorderLayout.NORTH);
    }

    private void updateTable() {

        String[][] data = new String[selectedOntologies.size()][2];

        int count = 0;
        for (String ontology : selectedOntologies.keySet()) {
            data[count][0] = ontology;
            data[count][1] = selectedOntologies.get(ontology).getBranchToSearchUnder() == null ? "" : selectedOntologies.get(ontology).getBranchToSearchUnder().toString();
            count++;
        }
        ontologiesToUseModel.setDataVector(data, ontologyColumnHeaders);
        renderTableHeader();
    }

    private void renderTableHeader() {
        Enumeration<TableColumn> columns = ontologiesToUse.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn tc = columns.nextElement();

            tc.setHeaderRenderer(new CustomTableHeaderRenderer());
        }
    }

    private void showPopupInCenter(Window container) {
        Container parent = (Container) main;
        Point parentLocation = parent.getLocationOnScreen();

        Dimension parentSize = parent.getSize();

        int calcedXLoc = (parentLocation.x) + ((parentSize.width) / 2) - (container.getWidth() / 2);
        int calcedYLoc = (parentLocation.y) + ((parentSize.height) / 2) - (container.getHeight() / 2);

        container.setVisible(true);
        container.setLocation(calcedXLoc, calcedYLoc);
        container.toFront();
        container.requestFocusInWindow();
    }

    private void instantiateFrame(String initFieldName) {
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
        instantiateFields(initFieldName);
        setBorder(new EmptyBorder(2, 10, 2, 2));
        setVisible(true);
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
        final OntologySelectionTool ost = new OntologySelectionTool(allowsMultiple, recommendedOntologySource);
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

    public void setCurrentField(FieldElement field) {
        this.selectedOntologies.clear();
        this.field = field;
        populateFields();
    }

    /**
     * Populates fields with info contained in FieldObject
     */
    private void populateFields() {
        try {
            FieldObject tfo = field.getFieldDetails();
            if (tfo != null) {

                fieldName.setText(tfo.getFieldName());
                description.setText(tfo.getDescription());

                // set the data type variable!
                if (tfo.getFieldName().equals(UNIT_STR)) {
                    // set data type to ontology term since this is all it should be!
                    datatype.removeAllItems();
                    datatype.addItem(DataTypes.ONTOLOGY_TERM);

                } else {
                    if (datatype.getItemCount() == 1) {
                        datatype.removeAllItems();
                        for (DataTypes d : DataTypes.values()) {
                            datatype.addItem(d);
                        }
                    }

                    datatype.setSelectedItem(tfo.getDatatype());
                }

                if (tfo.getFieldName().equals(PROTOCOL_STR)) {
                    defaultValLabStd.setText(PROTOCOL_TYPE_STR);
                    // create a separate panel for the default value which can be switched
                    // with ontology lookup when the field is a protocol ref!
                    defaultValCont.removeAll();
                    JComponent ontLookup = createOntologyDropDown(defaultValStd, false, null);

                    defaultValCont.add(ontLookup);
                    defaultValStd.setText(tfo.getDefaultVal());


                } else {
                    defaultValLabStd.setText(DEFAULT_VAL_STR);
                    defaultValCont.removeAll();
                    defaultValCont.add(defaultValStd);
                }


                if (tfo.getDatatype() == DataTypes.BOOLEAN) {
                    defaultValBool.setSelectedItem(tfo.getDefaultVal());
                } else {
                    defaultValStd.setText(tfo.getDefaultVal());
                }

                if (tfo.getDatatype() == DataTypes.ONTOLOGY_TERM) {
                    if (tfo.getRecommmendedOntologySource() != null) {
                        recommendOntologySource.setSelected(true);
                        sourceEntryPanel.setVisible(true);
                        usesTemplateForWizard.setVisible(false);

                        selectedOntologies = tfo.getRecommmendedOntologySource();
                        updateTable();
                    }
                }

                if (tfo.getInputFormat() != null) {
                    if (!tfo.getInputFormat().equals("") && !tfo.getInputFormat().equals(".*")) {
                        isInputFormatted.setSelected(true);
                        inputFormatCont.setVisible(true);
                        inputFormat.setText(tfo.getInputFormat());
                    } else {
                        isInputFormatted.setSelected(false);
                        inputFormatCont.setVisible(false);
                    }
                }

                if (tfo.getWizardTemplate() != null && !tfo.getWizardTemplate().trim().equals("")
                        && datatype.getSelectedItem() == DataTypes.STRING) {
                    usesTemplateForWizard.setSelected(true);
                    wizardTemplatePanel.setVisible(true);
                    wizardTemplate.setText(tfo.getWizardTemplate());
                } else {
                    usesTemplateForWizard.setSelected(false);
                    wizardTemplatePanel.setVisible(false);
                    wizardTemplate.setText("");
                }

                required.setSelected(tfo.isRequired());
                acceptsMultipleValues.setSelected(tfo.isAcceptsMultipleValues());
                acceptsFileLocations.setSelected(tfo.isAcceptsFileLocations());
                hidden.setSelected(tfo.isHidden());

                if (tfo.getFieldList() != null) {
                    String s = "";

                    for (String val : tfo.getFieldList()) {
                        s += (val + ", ");
                    }

                    s = s.length() > 2 ? s.substring(0, s.length() - 2) : s;
                    listValues.setText(s);
                }
            }

            revalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return fieldName.getText();
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == datatype) {
            DataTypes selected;
            if (datatype.getSelectedItem() == null) {
                selected = DataTypes.ONTOLOGY_TERM;
            } else {
                selected = DataTypes.resolveDataType(datatype.getSelectedItem().toString());
            }

            // Need to remove dependencies if a change has occurred which results in differing datatypes
            if (selected == DataTypes.STRING || selected == DataTypes.LONG_STRING) {
                preferredOntologySource.setVisible(false);
                // initialise the default value field to be specific for String values
                defaultValStd.setText("");
                defaultValStd.setEditable(true);
                defaultValStd.setEnabled(true);

                //isFixedLength.setVisible(true);
                isInputFormatted.setVisible(true);
                //fixedLengthCont.setVisible(false);
                inputFormatCont.setVisible(false);

                usesTemplateForWizard.setVisible(true);

                // show default value field for standard inputs and hide default value combo for boolean values.
                defaultValContStd.setVisible(true);
                defaultValContBool.setVisible(false);

                // hide list data source fields. no use here!
                listDataSourceCont.setVisible(false);

                acceptsFileLocations.setEnabled(true);


                // set regex for any valid character.
                defaultValStd.setFormatterFactory(new DefaultFormatterFactory(
                        new RegExFormatter(".*", defaultValStd, true, UIHelper.DARK_GREEN_COLOR, UIHelper.RED_COLOR, UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR, UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR)));
            }

            if (selected == DataTypes.INTEGER) {
                preferredOntologySource.setVisible(false);
                // initialise the default value field to be specific for integer values
                defaultValStd.setText("0");
                defaultValStd.setEditable(true);
                defaultValStd.setEnabled(true);

                // hide all options for length and regular expressions
                isInputFormatted.setVisible(false);
                isInputFormatted.setSelected(false);

                usesTemplateForWizard.setVisible(false);
                wizardTemplatePanel.setVisible(false);

                inputFormatCont.setVisible(false);
                acceptsFileLocations.setEnabled(false);

                defaultValContStd.setVisible(true);
                defaultValContBool.setVisible(false);

                // hide list data source fields. no use here!
                listDataSourceCont.setVisible(false);

                // set regex for default value to only accept integers.
                defaultValStd.setFormatterFactory(new DefaultFormatterFactory(
                        new RegExFormatter("[0-9]+", defaultValStd, true, UIHelper.DARK_GREEN_COLOR, UIHelper.RED_COLOR, UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR, UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR)));
            }

            if (selected == DataTypes.DOUBLE) {
                preferredOntologySource.setVisible(false);
                // initialise the default value field to be specific for double values
                defaultValStd.setText("0.0");
                defaultValStd.setEditable(true);
                defaultValStd.setEnabled(true);

                // hide all options for length and regular expressions
                isInputFormatted.setVisible(false);
                isInputFormatted.setSelected(false);
                usesTemplateForWizard.setVisible(false);
                wizardTemplatePanel.setVisible(false);

                inputFormatCont.setVisible(false);

                // show the standard default value for text etc. and hide default value showing boolean options
                defaultValContStd.setVisible(true);
                defaultValContBool.setVisible(false);

                // hide list data source fields. no use here!
                listDataSourceCont.setVisible(false);

                acceptsFileLocations.setEnabled(false);

                // set the regular expression checker to handle double values.
                defaultValStd.setFormatterFactory(new DefaultFormatterFactory(
                        new RegExFormatter("[0-9]+\\.[0-9]+", defaultValStd, true,
                                UIHelper.DARK_GREEN_COLOR, UIHelper.RED_COLOR, UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR, UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR)));
            }

            if (selected == DataTypes.DATE) {
                preferredOntologySource.setVisible(false);
                // initialise the default value field to be specific for date values
                defaultValStd.setText("dd-MM-yyyy");
                defaultValStd.setEnabled(false);
                defaultValStd.setEditable(true);

                isInputFormatted.setVisible(false);
                isInputFormatted.setSelected(false);
                inputFormatCont.setVisible(false);
                usesTemplateForWizard.setVisible(false);
                wizardTemplatePanel.setVisible(false);

                // show the standard default value for text etc. and hide default value showing boolean options
                defaultValContStd.setVisible(true);
                defaultValContBool.setVisible(false);

                acceptsFileLocations.setEnabled(false);

                // hide list data source fields. no use here!
                listDataSourceCont.setVisible(false);
            }

            if (selected == DataTypes.BOOLEAN) {
                preferredOntologySource.setVisible(false);

                // hide all options for length and regular expressions
                isInputFormatted.setVisible(false);
                isInputFormatted.setSelected(false);
                inputFormatCont.setVisible(false);
                usesTemplateForWizard.setVisible(false);
                wizardTemplatePanel.setVisible(false);

                // show default value field for boolean types and hide those for all other types.
                defaultValContStd.setVisible(false);
                defaultValContBool.setVisible(true);

                acceptsFileLocations.setEnabled(false);

                // hide list data source fields. no use here!
                listDataSourceCont.setVisible(false);
            }

            if (selected == DataTypes.LIST) {
                preferredOntologySource.setVisible(false);
                listDataSourceCont.setVisible(true);

                // hide default value input
                defaultValContStd.setVisible(false);
                defaultValContBool.setVisible(false);

                // hide all options for length and regular expressions
                isInputFormatted.setVisible(false);
                isInputFormatted.setSelected(false);
                inputFormatCont.setVisible(false);
                usesTemplateForWizard.setVisible(false);
                wizardTemplatePanel.setVisible(false);
                acceptsFileLocations.setEnabled(false);
            }

            if (selected == DataTypes.ONTOLOGY_TERM) {
                // show preferred ontology source box
                preferredOntologySource.setVisible(true);
                listDataSourceCont.setVisible(false);

                // hide default value input
                defaultValContStd.setVisible(false);
                defaultValContBool.setVisible(false);
                usesTemplateForWizard.setVisible(false);
                wizardTemplatePanel.setVisible(false);


                // hide all options for length and regular expressions
                isInputFormatted.setVisible(false);
                isInputFormatted.setSelected(false);
                inputFormatCont.setVisible(false);
                acceptsFileLocations.setEnabled(false);
            }
        }

        if (event.getSource() == isInputFormatted) {
            inputFormatCont.setVisible(isInputFormatted.isSelected());
        }

        if (event.getSource() == usesTemplateForWizard) {
            wizardTemplatePanel.setVisible(usesTemplateForWizard.isSelected());
        }
    }

    /**
     * Saves the currently acquired Field object. Will be used to export field information.
     *
     * @return FieldObject holding data contained entered using this interface
     * @throws DataNotCompleteException - if data is not complete, this exception is thrown
     */
    public void saveFieldObject(boolean checkDesc) throws DataNotCompleteException {

        if (field != null) {

            String defaultValAsString = defaultValStd.getText();

            if (datatype.getSelectedItem() == DataTypes.BOOLEAN) {
                defaultValAsString = defaultValBool.getSelectedItem().toString();
            }

            FieldObject tfo = new FieldObject(field.getFieldDetails().getColNo(), fieldName.getText(), description.getText(),
                    DataTypes.resolveDataType(datatype.getSelectedItem().toString()), defaultValAsString, "",
                    required.isSelected(),
                    acceptsMultipleValues.isSelected(),
                    acceptsFileLocations.isSelected(), hidden.isSelected());

            if (usesTemplateForWizard.isSelected()) {
                tfo.setWizardTemplate(wizardTemplate.getText());
            } else {
                tfo.setWizardTemplate("");
            }

            if(field.getFieldDetails().getSection() != null && !field.getFieldDetails().getSection().equals("")) {
            tfo.setSection(field.getFieldDetails().getSection());
            }

            tfo.setInputFormatted(isInputFormatted.isSelected());

            String finalInputFormat = "";

            if (isInputFormatted.isSelected()) {
                String s = inputFormat.getText();

                if (!s.equals("Input Format as RegExp")) {
                    tfo.setInputFormat(inputFormat.getText());
                    finalInputFormat += s;
                }
            } else if (datatype.getSelectedItem() == DataTypes.STRING) {
                // allow any character
                finalInputFormat += ".*";
            }

            if (!finalInputFormat.equals("")) {
                tfo.setInputFormat(finalInputFormat);
            }

            if (datatype.getSelectedItem() == DataTypes.LIST) {

                String[] fields = listValues.getText().split(",");

                for (int i = 0; i < fields.length; i++) {
                    fields[i] = fields[i].trim();
                }
                tfo.setFieldList(fields);
            }

            if (datatype.getSelectedItem() == DataTypes.ONTOLOGY_TERM) {
                if (recommendOntologySource.isSelected()) {
                    if (ontologiesToUseModel.getRowCount() > 0) {
                        Map<String, RecommendedOntology> toBeUpdated = new HashMap<String, RecommendedOntology>();
                        toBeUpdated.putAll(selectedOntologies);
                        tfo.setRecommmendedOntologySource(toBeUpdated);
                    }
                }
            }

            field.setFieldObject(tfo);

            if (checkDesc) {
                if (description.getText().equals("")) {
                    throw new DataNotCompleteException(
                            "Description not entered for field " + tfo.getFieldName());
                }
            }
        }
    }

}