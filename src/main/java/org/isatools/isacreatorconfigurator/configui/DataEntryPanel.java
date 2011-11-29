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
import org.isatools.isacreator.common.ReOrderableJList;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.configuration.MappingObject;
import org.isatools.isacreator.effects.CustomSplitPaneDivider;
import org.isatools.isacreatorconfigurator.configui.io.Utils;
import org.isatools.isacreatorconfigurator.configui.mappingviewer.TableMappingViewer;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;


public class DataEntryPanel extends JLayeredPane {

    @InjectedResource
    private ImageIcon addTable, addTableOver, removeTable, removeTableOver, addElement,
            addElementOver, removeElement, removeElementOver, moveUp, moveUpOver, moveDown, moveDownOver,
            informationIcon, isaConfigLogo, viewMappingsIcon, warningIcon, aboutIcon, fieldListTitle, tableListTitle;

    private static final String FIELD_XML_LOC = "/config/std_isa_fields.xml";
    private static final String CUSTOM_XML_LOC = "/config/custom_isa_fields.xml";
    // Map of table to fields
    private Map<MappingObject, List<Display>> tableFields;

    private AddTableGUI atGUI;
    private AddElementGUI aeGUI;
    private AboutPanel aboutPanel;

    private JList tableList;
    private DefaultListModel tableModel;
    private JLabel tableCountInfo;
    private ReOrderableJList elementList;
    private DefaultListModel elementModel;
    private JLabel elementCountInfo;
    private JLabel removeElementButton;

    private JLayeredPane currentPage;

    private static final int WIDTH = 900;
    private static final int HEIGHT = 700;
    private static final TableElementInfo tableInfo = new TableElementInfo();

    private Fields fields;
    private static Fields customFields;

    private ISAcreatorConfigurator appCont;

    private JLabel tableInformationDisplay;
    private FieldInterface fieldInterface;
    private StructuralElementInfo structureElement = new StructuralElementInfo();
    private File sourceFile;

    public DataEntryPanel(ISAcreatorConfigurator appCont, File sourceFile) {
        this(appCont, new ListOrderedMap<MappingObject, List<Display>>(), sourceFile);
    }

    public DataEntryPanel(ISAcreatorConfigurator appCont, Map<MappingObject, List<Display>> tableFields, File sourceFile) {
        ResourceInjector.get("config-ui-package.style").inject(this);

        this.appCont = appCont;
        this.tableFields = tableFields;
        this.sourceFile = sourceFile;

    }

    public void setTableFields(Map<MappingObject, List<Display>> tableFields) {
        this.tableFields = tableFields;
    }

    public Map<MappingObject, List<Display>> getTableFields() {
        return tableFields;
    }

    public void createGUI() {
        atGUI = new AddTableGUI(this);
        aeGUI = new AddElementGUI(this);
        setupAboutPanel();
        fieldInterface = new FieldInterface(getCurrentInstance());
        fieldInterface.createGUI();
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
        setBorder(null);
        instantiateFrame();
        loadPredefinedFieldNames();
        reformTableList();
        setVisible(true);
    }


    private void setupAboutPanel() {
        aboutPanel = new AboutPanel();
        aboutPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                appCont.hideGlassPane();
            }
        });
    }

    public Fields getFields() {
        return fields;
    }

    private void loadPredefinedFieldNames() {
        ProcessStandardFieldsXML process = new ProcessStandardFieldsXML();

        fields = process.parseFile(getClass().getResource(FIELD_XML_LOC));
        customFields = process.parseFile(getClass().getResource(CUSTOM_XML_LOC));
    }

    private void instantiateFrame() {
        createMainSector();
    }

    private void showMessagePane(String message, int messageType) {
        final JOptionPane optionPane = new JOptionPane("<html>" + message + "</html>",
                JOptionPane.OK_OPTION);
        UIHelper.renderComponent(optionPane, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        if (messageType == JOptionPane.ERROR_MESSAGE) {
            optionPane.setIcon(warningIcon);
        } else {
            optionPane.setIcon(informationIcon);
        }

        optionPane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName()
                        .equals(JOptionPane.VALUE_PROPERTY)) {
                    appCont.hideSheet();
                }
            }
        });

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                appCont.showJDialogAsSheet(optionPane.createDialog(getThis(),
                        "Message"));
            }
        });

    }

    private JPanel createMenu() {
        JPanel topPanel = new JPanel(new GridLayout(1, 1));
        topPanel.setBackground(UIHelper.BG_COLOR);

        JMenuBar menu_container = new JMenuBar();

        JMenu file = new JMenu("File");


        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    updateFieldOrder();
                    saveCurrentField(true, true);
                    if (sourceFile == null) {
                        createOutput();
                    } else {
                        save();
                    }
                } catch (DataNotCompleteException dce) {
                    showMessagePane(dce.getMessage(), JOptionPane.ERROR_MESSAGE);
                } catch (Exception e1) {
                    showMessagePane(e1.getMessage(), JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JMenuItem createFile = new JMenuItem("Save As");
        createFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    saveCurrentField(true, true);
                    createOutput();
                } catch (DataNotCompleteException dce) {
                    showMessagePane(dce.getMessage(), JOptionPane.ERROR_MESSAGE);
                } catch (IOException e1) {
                    showMessagePane("IO error occurred when saving file!", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e1) {
                    showMessagePane(e1.getMessage(), JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        JMenuItem closeSession = new JMenuItem("Close session without saving");
        closeSession.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tableFields.clear();
                appCont.setCurrentPage(appCont.getMp());
            }
        });

        file.add(save);
        file.add(createFile);
        file.add(closeSession);

        JMenu mappingMenu = new JMenu("Mappings");

        JMenuItem viewMappings = new JMenuItem("View Mappings",
                viewMappingsIcon);
        viewMappings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        getApplicationContainer().showJDialogAsSheet(new TableMappingViewer(getThis(), getTableTypeMapping()));
                    }
                });

            }
        });

        mappingMenu.add(viewMappings);

        JMenu helpMenu = new JMenu("Help");

        JMenuItem about = new JMenuItem("About", aboutIcon);
        about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        appCont.setGlassPanelContents(aboutPanel);
                    }
                });

            }
        });

        helpMenu.add(about);

        menu_container.add(file);
        menu_container.add(mappingMenu);
        menu_container.add(helpMenu);

        topPanel.add(menu_container);
        return topPanel;
    }

    private DataEntryPanel getThis() {
        return this;
    }


    /**
     * Create the files necessary for loading into ISAcreator
     *
     * @throws IOException - When file cannot be found. Should never be thrown
     */
    private void createOutput() throws IOException {

        ExportConfigurationDialog exportDialog = new ExportConfigurationDialog();
        exportDialog.createGUI();

        appCont.showJDialogAsSheet(exportDialog);
        exportDialog.addPropertyChangeListener("save", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                final String saveIn = propertyChangeEvent.getNewValue().toString();

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        appCont.hideSheet();
                        try {
                            showMessagePane(Utils.createTableConfigurationXML(saveIn, tableFields), JOptionPane.INFORMATION_MESSAGE);
                            sourceFile = new File(saveIn);
                        } catch (DataNotCompleteException e) {
                            showMessagePane(e.getMessage(), JOptionPane.ERROR_MESSAGE);
                        } catch (InvalidFieldOrderException ifoe) {
                            showMessagePane(ifoe.getMessage(), JOptionPane.ERROR_MESSAGE);
                        } catch (IOException ioe) {
                            showMessagePane(ioe.getMessage(), JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
            }
        });

        exportDialog.addPropertyChangeListener("windowClosed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        appCont.hideSheet();
                    }
                });
            }
        });

        appCont.showJDialogAsSheet(exportDialog);
    }

    private void save() {
        try {
            updateFieldOrder();
            showMessagePane(Utils.createTableConfigurationXML(sourceFile.getAbsolutePath(), tableFields), JOptionPane.INFORMATION_MESSAGE);
        } catch (DataNotCompleteException e) {
            showMessagePane(e.getMessage(), JOptionPane.ERROR_MESSAGE);
        } catch (InvalidFieldOrderException ifoe) {
            showMessagePane(ifoe.getMessage(), JOptionPane.ERROR_MESSAGE);
        } catch (IOException ioe) {
            showMessagePane(ioe.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }


    private void createMainSector() {

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.PAGE_AXIS));
        topContainer.setBackground(UIHelper.BG_COLOR);

        // add logo to the top
        topContainer.add(createMenu());

        JPanel headerImagePanel = new JPanel(new GridLayout(1, 2));
        headerImagePanel.setOpaque(false);

        JLabel logo = new JLabel(isaConfigLogo, JLabel.LEFT);
        logo.setOpaque(false);

        headerImagePanel.add(logo);


        tableInformationDisplay = UIHelper.createLabel("", UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, SwingConstants.RIGHT);
        tableInformationDisplay.setVerticalAlignment(SwingConstants.TOP);
        tableInformationDisplay.setHorizontalAlignment(SwingConstants.RIGHT);

        headerImagePanel.add(UIHelper.wrapComponentInPanel(tableInformationDisplay));

        topContainer.add(headerImagePanel);
        topContainer.add(Box.createVerticalStrut(10));


        add(topContainer, BorderLayout.NORTH);

        // create central console with majority of content!
        createNavigationPanel();


        JPanel bottomContainer = new JPanel();
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.PAGE_AXIS));
        bottomContainer.setBackground(UIHelper.BG_COLOR);

        add(bottomContainer, BorderLayout.SOUTH);

    }


    private void createNavigationPanel() {

        JSplitPane tablesAndElements = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
                createTableListPanel(), createFieldListPanel());

        customiseJSplitPaneLookAndFeel(tablesAndElements);

        add(tablesAndElements, BorderLayout.WEST);

        setCurrentPage(tableInfo);

    }

    private void customiseJSplitPaneLookAndFeel(JSplitPane splitPane) {

        BasicSplitPaneUI paneUI = new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new CustomSplitPaneDivider(this);
            }
        };

        splitPane.setUI(paneUI);
        splitPane.setBackground(UIHelper.BG_COLOR);

        splitPane.setBorder(new EmptyBorder(1, 1, 10, 1));
    }

    private JPanel createTableListPanel() {

        JPanel container = new JPanel();
        container.setBackground(UIHelper.BG_COLOR);
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));

        JLabel lab = new JLabel(tableListTitle);

        container.add(UIHelper.wrapComponentInPanel(lab));
        container.add(Box.createVerticalStrut(5));

        tableModel = new DefaultListModel();
        tableList = new JList(tableModel);
        tableList.setCellRenderer(new TableListRenderer());
        tableList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tableList.setBackground(UIHelper.BG_COLOR);
        tableList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                try {
                    saveCurrentField(false, false);
                } catch (DataNotCompleteException dce) {
                    showMessagePane(dce.getMessage(), JOptionPane.ERROR_MESSAGE);
                }

                MappingObject currentlyEditedTable = getCurrentlySelectedTable();
                updateTableInfoDisplay(currentlyEditedTable);
                reformFieldList(currentlyEditedTable);
            }
        });

        JScrollPane listScroller = new JScrollPane(tableList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        listScroller.getViewport().setBackground(UIHelper.BG_COLOR);
        listScroller.setBorder(null);
        listScroller.setPreferredSize(new Dimension(((int) (WIDTH * 0.25)), ((int) (HEIGHT * 0.75))));

        IAppWidgetFactory.makeIAppScrollPane(listScroller);

        container.add(listScroller);
        container.add(Box.createVerticalStrut(5));


        tableCountInfo = UIHelper.createLabel("", UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR);
        container.add(UIHelper.wrapComponentInPanel(tableCountInfo));
        container.add(Box.createVerticalStrut(5));

        // create button panel to add and remove tables
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBackground(UIHelper.BG_COLOR);

        final JLabel addTableButton = new JLabel(addTable, JLabel.LEFT);
        UIHelper.renderComponent(addTableButton, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        addTableButton.setOpaque(false);

        addTableButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                addTableButton.setIcon(addTableOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                addTableButton.setIcon(addTable);
            }

            public void mousePressed(MouseEvent event) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        addTableButton.setIcon(addTable);
                        appCont.showJDialogAsSheet(atGUI);
                    }
                });


            }

        });

        addTableButton.setToolTipText("<html><b>Add table</b><p>Add a new table definition.</p></html>");

        final JLabel removeTableButton = new JLabel(removeTable, JLabel.LEFT);
        UIHelper.renderComponent(removeTableButton, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        removeTableButton.setOpaque(false);

        removeTableButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                removeTableButton.setIcon(removeTableOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                removeTableButton.setIcon(removeTable);
            }

            public void mousePressed(MouseEvent event) {
                removeTableButton.setIcon(removeTable);
                if (tableList.getSelectedValue() != null) {
                    String selectedTable = tableList.getSelectedValue().toString();
                    MappingObject toRemove = null;
                    for (MappingObject mo : tableFields.keySet()) {
                        if (mo.getAssayName().equals(selectedTable)) {
                            toRemove = mo;
                            break;
                        }
                    }

                    if (toRemove != null) {
                        tableFields.remove(toRemove);
                        reformTableList();
                    }
                }
            }

        });

        removeTableButton.setToolTipText("<html><b>Remove table</b><p>Remove table from definitions?</p></html>");

        buttonPanel.add(addTableButton);
        buttonPanel.add(removeTableButton);
        buttonPanel.add(Box.createHorizontalGlue());

        container.add(buttonPanel);

        container.add(Box.createVerticalGlue());

        return container;
    }

    private void updateTableInfoDisplay(MappingObject currentTable) {
        StringBuilder message = new StringBuilder();

        if (currentTable != null) {

            if (sourceFile != null) {
                message.append("<html><div align=\"right\">Currently editing <strong>").append(sourceFile.getName()).append("</strong><br/>");
            } else {
                message.append("<html><div align=\"right\">New Configuration file<strong></br>");
            }

            if (currentTable.getTableType().contains("sample")) {
                message.append("<strong>study sample</strong> table definition</html>");
            } else if (currentTable.getTableType().toLowerCase().contains("investigation")) {
                message.append("<strong>Investigation File</strong> definition</html>");
            } else {
                message.append("measuring <strong>").append(currentTable.getMeasurementEndpointType()).append("</strong>");

                String technology = currentTable.getTechnologyType();
                if (!technology.equals("")) {
                    message.append(" using <strong>").append(technology).append("</strong>");
                }

                message.append("</div></html>");
            }

            tableInformationDisplay.setText(message.toString());
        }
    }

    private String[] filterAvailableFieldsByTableType(Fields fieldList, Location type) {
        List<String> result = fieldList.getFieldsByLocation(type);
        Collections.sort(result);
        return result.toArray(new String[result.size()]);
    }

    private JPanel createFieldListPanel() {

        JPanel container = new JPanel();
        container.setBackground(UIHelper.BG_COLOR);
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));


        JPanel headerLab = new JPanel(new GridLayout(1, 1));
        headerLab.setOpaque(false);

        JLabel lab = new JLabel(fieldListTitle);
        headerLab.add(lab);

        container.add(headerLab);
        container.add(Box.createVerticalStrut(5));


        elementModel = new DefaultListModel();
        elementList = new ReOrderableJList(elementModel);
        elementList.setCellRenderer(new CustomReOrderableListCellRenderer(elementList));
        elementList.setDragEnabled(true);
        elementList.setBackground(UIHelper.BG_COLOR);

        elementList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                Display selectedNode = (Display) elementList.getSelectedValue();
                if (selectedNode != null) {
                    try {
                        saveCurrentField(false, false);
                    } catch (DataNotCompleteException dce) {
                        showMessagePane(dce.getMessage(), JOptionPane.ERROR_MESSAGE);
                    }
                    if (selectedNode instanceof FieldElement) {
                        fieldInterface.setCurrentField((FieldElement) selectedNode);
                        if (currentPage != fieldInterface) {
                            setCurrentPage(fieldInterface);
                        }
                        removeElementButton.setEnabled(!fields.isFieldRequired(selectedNode.toString()));
                    } else {
                        setCurrentPage(structureElement);
                    }
                }
            }
        });

        elementList.addPropertyChangeListener("orderChanged", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                updateFieldOrder();
            }
        });

        JScrollPane listScroller = new JScrollPane(elementList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        listScroller.getViewport().setBackground(UIHelper.BG_COLOR);
        listScroller.setBorder(new EmptyBorder(2, 2, 2, 10));
        listScroller.setPreferredSize(new Dimension(((int) (WIDTH * 0.25)), ((int) (HEIGHT * 0.75))));
        IAppWidgetFactory.makeIAppScrollPane(listScroller);

        container.add(listScroller);
        container.add(Box.createVerticalStrut(5));

        elementCountInfo = UIHelper.createLabel("", UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR);

        container.add(UIHelper.wrapComponentInPanel(elementCountInfo));
        container.add(Box.createVerticalStrut(5));

        // create button panel to add and remove tables
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBackground(UIHelper.BG_COLOR);

        final JLabel addFieldButton = new JLabel(addElement, JLabel.LEFT);
        addFieldButton.setOpaque(false);

        addFieldButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                addFieldButton.setIcon(addElementOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                addFieldButton.setIcon(addElement);
            }

            public void mousePressed(MouseEvent event) {
                addFieldButton.setIcon(addElement);
                if (getCurrentlySelectedTable() != null) {
                    aeGUI.updateFieldList(filterAvailableFieldsByTableType(fields,
                            Location.resolveLocationIdentifier(getCurrentlySelectedTable().getTableType())));

                    String tableType = getCurrentlySelectedTable().getTableType().equalsIgnoreCase("investigation")
                            ? "Investigation file"
                            : getCurrentlySelectedTable().getTableType();

                    aeGUI.updateCustomFieldList(filterAvailableFieldsByTableType(customFields,
                            Location.resolveLocationIdentifier(tableType)));


                    aeGUI.setCurrentTableType(Location.resolveLocationIdentifier(tableType));

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            appCont.showJDialogAsSheet(aeGUI);
                        }
                    });
                }
            }

        });

        addFieldButton.setToolTipText("<html><b>Add Field to table</b><p>Add a new field to currently selected table</b></p></html>");

        removeElementButton = new JLabel(removeElement, JLabel.LEFT);
        removeElementButton.setOpaque(false);

        removeElementButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                removeElementButton.setIcon(removeElementOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                removeElementButton.setIcon(removeElement);
            }

            public void mousePressed(MouseEvent event) {
                removeElementButton.setIcon(removeElement);
                if (removeElementButton.isEnabled()) {
                    if (elementList.getSelectedValue() != null) {
                        int selectedIndex = elementList.getSelectedIndex();

                        updateFieldOrder();

                        if (selectedIndex != -1) {
                            Display fd = getCurrentlySelectedField();
                            if (fd != null) {
                                if (tableFields.get(getCurrentlySelectedTable()).contains(fd)) {
                                    tableFields.get(getCurrentlySelectedTable()).remove(fd);
                                } else {
                                    Display field = findTableFieldToRemove(fd.getFieldDetails());
                                    if (field != null) {
                                        tableFields.get(getCurrentlySelectedTable()).remove(field);
                                    }
                                }
                                reformFieldList(getCurrentlySelectedTable());
                            }
                        }
                    }
                }
            }

        });

        removeElementButton.setToolTipText("<html><b>Remove Field from table</b><p>Remove this field from this list and the currently selected table.</b></p></html>");

        final JLabel moveDownButton = new JLabel(moveDown);
        moveDownButton.setOpaque(false);
        moveDownButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                moveDownButton.setIcon(moveDownOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                moveDownButton.setIcon(moveDown);
            }

            public void mousePressed(MouseEvent event) {
                moveDownButton.setIcon(moveDown);
                if (elementList.getSelectedIndex() != -1) {
                    int toMoveDown = elementList.getSelectedIndex();

                    if (toMoveDown != (elementModel.getSize() - 1)) {
                        swapElements(elementList, elementModel, toMoveDown, toMoveDown + 1);
                    }
                }
            }
        });

        moveDownButton.setToolTipText("<html><b>Move Field Down</b><p>Move the selected field down <b>one</b> position in the list.</p></html>");

        final JLabel moveUpButton = new JLabel(moveUp);
        moveUpButton.setOpaque(false);
        moveUpButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                moveUpButton.setIcon(moveUpOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                moveUpButton.setIcon(moveUp);
            }

            public void mousePressed(MouseEvent event) {
                moveUpButton.setIcon(moveUp);
                if (elementList.getSelectedIndex() != -1) {
                    int toMoveUp = elementList.getSelectedIndex();

                    if (toMoveUp != 0) {
                        swapElements(elementList, elementModel, toMoveUp, toMoveUp - 1);
                    }
                }
            }

        });

        moveUpButton.setToolTipText("<html><b>Move Field Up</b><p>Move the selected field up <b>one</b> position in the list.</p></html>");

        buttonPanel.add(addFieldButton);
        buttonPanel.add(removeElementButton);
        buttonPanel.add(moveDownButton);
        buttonPanel.add(moveUpButton);

        container.add(buttonPanel);

        container.add(Box.createVerticalGlue());

        return container;
    }

    /**
     * Saves the current field in view by calling the saveFieldObject in the FieldInterface class
     *
     * @param doFinalChecks - whether or not to check for the definition of at least one study and one assay!
     * @see org.isatools.isacreatorconfigurator.configui.FieldInterface
     *      <p/>
     *      catches a DataNotCompleteException to prevent users from not entering a description
     * @see org.isatools.isacreatorconfigurator.configui.DataNotCompleteException
     */
    private void saveCurrentField(boolean doFinalChecks, boolean doDescriptionCheck) throws DataNotCompleteException {

        if (currentPage == fieldInterface) {
            FieldInterface currentField = (FieldInterface) currentPage;
            currentField.saveFieldObject();
        }

        if (doFinalChecks) {
            checkForMinimumTablePresence();

        }
        if (doDescriptionCheck) {
            checkForSalientFieldPresence();
        }
    }

    private void checkForSalientFieldPresence() throws DataNotCompleteException {
        updateFieldOrder();

        for (MappingObject mo : tableFields.keySet()) {

            for (Display disp : tableFields.get(mo)) {
                if (disp instanceof FieldElement) {
                    FieldElement fe = (FieldElement) disp;

                    if (fe.getFieldDetails().getFieldName().equalsIgnoreCase("protocol ref")) {
                        if (fe.getFieldDetails().getDefaultVal().trim().equals("")) {
                            throw new DataNotCompleteException("<p><b>" + fe.getFieldDetails().getFieldName() +
                                    "</b> in table <b>" + mo.getAssayName() +
                                    "</p></b><p> is missing it's <b>protocol type</b>.</p>");
                        }
                    } else if (fe.getFieldDetails().getDescription().trim().equals("")) {
                        throw new DataNotCompleteException("<p><b>" + fe.getFieldDetails().getFieldName() +
                                "</b> in table <b>" + mo.getAssayName() +
                                "</p></b><p> is missing it's <b>description</b>.</p>");
                    }
                }
            }
        }
    }

    private void checkForMinimumTablePresence() throws DataNotCompleteException {
        boolean containsStudySample = false;
        boolean containsAssay = false;

        for (MappingObject mo : tableFields.keySet()) {
            if (mo.getTableType().contains("sample")) {
                containsStudySample = true;
            }
            if (mo.getTableType().equalsIgnoreCase("assay")) {
                containsAssay = true;
            }
            if (containsStudySample && containsAssay) {
                break;
            }
        }

        if (!containsAssay && !containsStudySample) {
            throw new DataNotCompleteException("<p>you need to define at least a <b>Study sample</b> and an <b>Assay</b> table</p>");
        }
        if (!containsAssay) {
            throw new DataNotCompleteException("<p>you need to define a <b>Assay</b> table</p>");
        }
        if (!containsStudySample) {
            throw new DataNotCompleteException("<p>you need to define an <b>Study sample</b> table</p>");
        }
    }

    private void swapElements(JList list, DefaultListModel model, int a, int b) {
        Object o1 = model.getElementAt(a);
        Object o2 = model.getElementAt(b);
        model.set(a, o2);
        model.set(b, o1);

        // Swap elements in list too
        List<Display> fields = tableFields.get(getCurrentlySelectedTable());

        Display e1 = fields.get(a);
        Display e2 = fields.get(b);

        fields.set(a, e2);
        fields.set(b, e1);


        list.setSelectedIndex(b);
        list.ensureIndexIsVisible(b);
    }

    private void updateFieldOrder() {
        List<Display> backup = new ArrayList<Display>();

        Set<String> sectionsAdded = new HashSet<String>();

        for (int index = 0; index < elementModel.size(); index++) {

            Object value = elementModel.get(index);
            if (!sectionsAdded.contains(value.toString())) {

                if (value instanceof FieldElement) {
                    FieldElement fieldElement = (FieldElement) value;
                    backup.add(new FieldElement(fieldElement.getFieldDetails()));
                } else if (value instanceof SectionDisplay) {
                    SectionDisplay sectionDisplay = (SectionDisplay) value;
                    backup.add(new SectionDisplay(sectionDisplay.toString()));
                    sectionsAdded.add(value.toString());
                } else if (value instanceof StructuralElementDisplay) {
                    StructuralElementDisplay structuralElementDisplay = (StructuralElementDisplay) value;
                    backup.add(new StructuralElementDisplay(structuralElementDisplay.toString()));
                }
            }
        }

        // replace with fields in list with the new ordering
        tableFields.put(getCurrentlySelectedTable(), backup);
    }

    private DataEntryPanel getCurrentInstance() {
        return DataEntryPanel.this;
    }

    public Set<MappingObject> getTableTypeMapping() {
        return tableFields.keySet();
    }

    private Display getCurrentlySelectedField() {
        MappingObject mo = getCurrentlySelectedTable();
        if (mo != null) {
            return (elementList.getSelectedValue() != null) ?
                    (Display) elementList.getSelectedValue() : null;
        }
        return null;
    }

    public boolean addTable(String tableType, String measurementType, String measurementSource, String measurementAccession,
                            String techType, String techSource, String techAccession, String tableName, List<FieldObject> initialFields,
                            String assayType, String dispatchTarget) {

        if (!checkConflictingTables(tableType, tableName, measurementType, techType)) {
            MappingObject mo = new MappingObject(tableType, measurementType, measurementSource, measurementAccession, techType, techSource, techAccession, tableName);
            mo.setAssayType(assayType);
            mo.setDispatchTarget(dispatchTarget);
            tableFields.put(mo, new ArrayList<Display>());
            // reform table list
            if (initialFields != null) {
                for (FieldObject field : initialFields) {
                    tableFields.get(mo).add(new FieldElement(field));
                }
            }
            reformTableList();

            return true;

        }
        return false;
    }

    public boolean addField(Display element) {
        MappingObject currentlySelectedTable = getCurrentlySelectedTable();
        if (currentlySelectedTable != null) {
            if (!checkPreExistingFields(currentlySelectedTable, element.toString())
                    || element.toString().equals("Unit")
                    || element.toString().equals("Protocol REF")) {
                // add field to table
                tableFields.get(currentlySelectedTable).add(element);
                reformFieldList(currentlySelectedTable);
                return true;
            }
        }
        return false;
    }

    private Display findTableFieldToRemove(FieldObject fieldToRemove) {
        for (Display d : tableFields.get(getCurrentlySelectedTable())) {
            if (d != null && d.getFieldDetails() != null) {
                if (d.getFieldDetails().getFieldName().equals(fieldToRemove.getFieldName())) {
                    if (d.getFieldDetails().getColNo() == fieldToRemove.getColNo()) {
                        return d;
                    }
                }
            }
        }
        return null;
    }

    public MappingObject getCurrentlySelectedTable() {
        String s = (tableList.getSelectedValue() != null) ? tableList.getSelectedValue().toString() : null;
        if (s != null) {
            for (MappingObject mo : tableFields.keySet()) {
                if (mo.getAssayName().equals(s)) {
                    return mo;
                }
            }
        }
        return null;
    }

    private void reformTableList() {
        tableModel.clear();
        for (MappingObject mo : tableFields.keySet()) {
            tableModel.addElement(mo);
        }
        tableList.setSelectedIndex(tableModel.getSize() - 1);
        if (tableModel.getSize() == 0) {
            setCurrentPage(tableInfo);
        }
        tableCountInfo.setText("<html><strong>" + tableModel.getSize() + "</strong> tables...</html>");
    }

    private void reformFieldList(MappingObject selectedTable) {
        // get MappingObject corresponding to index entered.

        Set<String> sectionsAdded = new HashSet<String>();

        if (selectedTable != null) {
            Iterator<Display> fields = tableFields.get(selectedTable).iterator();

            elementModel.clear();

            while (fields.hasNext()) {
                Display d = fields.next();


                if (d.getFieldDetails() != null) {
                    if (d.getFieldDetails().getSection() != null && !d.getFieldDetails().getSection().equals("")) {
                        String section = d.getFieldDetails().getSection();

                        if (!sectionsAdded.contains(section)) {
                            elementModel.addElement(new SectionDisplay(section));
                            sectionsAdded.add(section);
                        }
                    }
                }

                if (d instanceof SectionDisplay) {
                    if (!sectionsAdded.contains(d.toString())) {
                        elementModel.addElement(d);
                        sectionsAdded.add(d.toString());
                    }
                } else {
                    elementModel.addElement(d);
                }

            }
            if (elementList.getModel().getSize() > 0) {
                elementList.setSelectedIndex(0);
            }

        } else {
            elementModel.clear();
        }

        elementCountInfo.setText("<html><strong>" + elementModel.getSize() + "</strong> elements...</html>");
    }


    private boolean checkPreExistingFields(MappingObject tableUsed, String fieldName) {

        for (Display fd : tableFields.get(tableUsed)) {
            if (fd.toString().equals(fieldName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks to determine if either an existing table has the same name or if an existing tables already defines the measurement -> technology type pair.
     *
     * @param tableName   - Name of the table to be checked
     * @param measurement - measurement to be checked for e.g. Gene Expression
     * @param technology  - Technology type to be checked for e.g. DNA microarray
     * @return boolean if table exists with the sample name or if a table exists with the sample measurement <-> technology type combination.
     */
    private boolean checkConflictingTables(String tableType, String tableName, String measurement,
                                           String technology) {
        for (MappingObject mo : tableFields.keySet()) {

            if (mo.getAssayName().equals(tableName)) {
                return true;
            }

            if (mo.getMeasurementEndpointType().equals(measurement) &&
                    mo.getTechnologyType().equals(technology) && mo.getTableType().equals(tableType)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Changes JLayeredPane being shown in the center panel
     *
     * @param newPage - JLayeredPane to change to
     */
    public void setCurrentPage(JLayeredPane newPage) {
        if (currentPage == null) {
            currentPage = newPage;
        } else {
            remove(currentPage);
            currentPage = newPage;
        }

        add(currentPage, BorderLayout.CENTER);
        repaint();
        validate();
    }

    public ISAcreatorConfigurator getApplicationContainer() {
        return appCont;
    }


}
