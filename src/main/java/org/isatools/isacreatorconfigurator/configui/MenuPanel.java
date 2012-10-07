/**
 ISAconfigurator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAconfigurator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 �The contents of this file are subject to the CPAL version 1.0 (the �License�); you may not use this file except
 in compliance with the License. You may obtain a copy of the License at http://isa-tools.org/licenses/ISAconfigurator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections 14 and 15 have been added to cover use of software over
 a computer network and provide for limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 consistent with Exhibit B.

 Software distributed under the License is distributed on an �AS IS� basis, WITHOUT WARRANTY OF ANY KIND, either express
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

import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.Configuration;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.configuration.MappingObject;
import org.isatools.isacreator.configuration.TableConfiguration;
import org.isatools.isacreator.configuration.io.ConfigXMLParser;
import org.isatools.isacreator.configuration.io.ConfigurationLoadingSource;
import org.isatools.isacreator.effects.GenericPanel;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MenuPanel extends JLayeredPane {
    private static final Logger log = Logger.getLogger(MenuPanel.class.getName());

    private GenericPanel generic;

    private ISAcreatorConfigurator appCont;
    private CreateMenu menu;
    private JLabel status;
    private File toLoad;

    public static final ImageIcon CONFIG_LOAD = new ImageIcon(MenuPanel.class.getResource("/images/effects/load_config.gif"));

    @InjectedResource
    private ImageIcon createNewICOIcon, createNewICOIconOver, openExistingIcon, openExistingOverIcon, exitIcon, exitOverIcon;

    // Map of table to fields
    private Map<MappingObject, List<Display>> tableFields;
    private JFileChooser jfc;

    public MenuPanel(ISAcreatorConfigurator appCont) {

        this.appCont = appCont;
        jfc = new JFileChooser();
        tableFields = new ListOrderedMap<MappingObject, List<Display>>();
    }

    public void createGUI() {
         ResourceInjector.get("config-ui-package.style").inject(this);

        setLayout(new OverlayLayout(this));
        setOpaque(false);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                generic = new BackgroundPanel();
                add(generic, JLayeredPane.DEFAULT_LAYER);
                startAnimation();
                menu = new CreateMenu();
                menu.createGUI();
                setGlassPaneToMenu();
                setVisible(true);
            }
        });
    }

    private void startAnimation() {
        Timer timer = new Timer(75, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generic.animate();
                generic.repaint();
            }
        });
        timer.start();
    }

    public void setGlassPaneToMenu() {
        appCont.setGlassPanelContents(menu);
    }


    /**
     * CreateISATABMenu provides the menu to allow users to either create an ISATAB submission
     * manually, or to create a new submission using the data creation wizard.
     */
    class CreateMenu extends JPanel implements MouseListener {
        public CreateMenu() {
            setPreferredSize(new Dimension(400, 400));
            setLayout(new BorderLayout());
            setOpaque(false);
            addMouseListener(this);

        }

        public void createGUI() {

            Box menuItems = Box.createHorizontalBox();
            menuItems.setOpaque(false);

            final JLabel createNewConfiguration = new JLabel(createNewICOIcon);

            createNewConfiguration.addMouseListener(new MouseAdapter() {

                public void mousePressed(MouseEvent event) {
                    createNewConfiguration.setIcon(createNewICOIcon);

                    status.setForeground(UIHelper.DARK_GREEN_COLOR);
                    status.setText("<html><p align=\"center\"><strong>starting</strong> application...</p></html>");
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            status.setIcon(CONFIG_LOAD);
                            DataEntryPanel dep = new DataEntryPanel(appCont, null);
                            dep.createGUI();
                            appCont.hideGlassPane();
                            appCont.setCurrentPage(dep);
                            status.setIcon(null);
                            status.setText("");
                        }
                    });
                }

                public void mouseEntered(MouseEvent event) {
                    createNewConfiguration.setIcon(createNewICOIconOver);
                }

                public void mouseExited(MouseEvent event) {
                    createNewConfiguration.setIcon(createNewICOIcon);
                }
            });
            menuItems.add(createNewConfiguration);
            menuItems.add(Box.createHorizontalStrut(10));

            final JLabel openExistingConfiguration = new JLabel(openExistingIcon);
            openExistingConfiguration.addMouseListener(new MouseAdapter() {

                public void mousePressed(MouseEvent event) {
                    try {
                        openExistingConfiguration.setIcon(openExistingIcon);
                        Thread performer = new Thread(new Runnable() {
                            public void run() {

                                try {
                                    loadSession();
                                } catch (IOException e) {
                                    showErrorInStatus("<html>Invalid selection made. Please choose a valid ISA configuration directory...</html>");
                                    e.printStackTrace();
                                } catch (ClassNotFoundException e) {
                                    showErrorInStatus("<html>The table configuration file you have loaded is incorrectly formed!</html>");
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    showErrorInStatus("<html>Invalid selection made. Please choose a valid ISA configuration directory...</html>");
                                }
                            }
                        });
                        performer.start();

                    } catch (Exception e) {
                        log.error("e " + e.getMessage());
                    }
                }

                public void mouseEntered(MouseEvent event) {
                    openExistingConfiguration.setIcon(openExistingOverIcon);
                }

                public void mouseExited(MouseEvent event) {
                    openExistingConfiguration.setIcon(openExistingIcon);
                }
            });
            menuItems.add(openExistingConfiguration);
            menuItems.add(Box.createHorizontalStrut(15));

            final JLabel exit = new JLabel(exitIcon,
                    JLabel.LEFT);
            exit.addMouseListener(new MouseAdapter() {

                public void mousePressed(MouseEvent event) {

                }

                public void mouseEntered(MouseEvent event) {
                    exit.setIcon(exitOverIcon);
                }

                public void mouseExited(MouseEvent event) {
                    exit.setIcon(exitIcon);
                }
            });
            menuItems.add(exit);

            JPanel menuItemContainer = new JPanel();
            menuItemContainer.setLayout(new BoxLayout(menuItemContainer, BoxLayout.PAGE_AXIS));
            menuItemContainer.setOpaque(false);

            menuItemContainer.add(menuItems);

            JPanel northPanel = new JPanel();
            northPanel.setLayout(new GridLayout(3, 3));

            northPanel.add(Box.createVerticalStrut(100));
            northPanel.add(menuItemContainer);

            status = UIHelper.createLabel("", UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR);
            status.setSize(new Dimension(300, 80));
            status.setHorizontalTextPosition(JLabel.CENTER);
            status.setVerticalTextPosition(JLabel.BOTTOM);
            status.setHorizontalAlignment(JLabel.CENTER);
            northPanel.add(UIHelper.wrapComponentInPanel(status));
            northPanel.setOpaque(false);
            add(northPanel, BorderLayout.CENTER);
        }

        public void mouseClicked(MouseEvent mouseEvent) {

        }

        public void mousePressed(MouseEvent mouseEvent) {

        }

        public void mouseReleased(MouseEvent mouseEvent) {

        }

        public void mouseEntered(MouseEvent mouseEvent) {

        }

        public void mouseExited(MouseEvent mouseEvent) {

        }
    }

    /**
     * Loads a session to allow for editing
     *
     * @throws java.io.IOException    - When file not found
     * @throws ClassNotFoundException - When object being read in from input stream doesn't read in properly
     */
    private boolean loadSession() throws IOException, ClassNotFoundException, XmlException {
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setFileFilter(new ConfigurationFileFilter());
        jfc.setDialogTitle("Select configuration file directory");
        jfc.setApproveButtonText("load configuration");
        jfc.showOpenDialog(this);

        if (jfc.getSelectedFile() != null) {

            status.setIcon(CONFIG_LOAD);

            toLoad = jfc.getSelectedFile();

            if (toLoad != null && checkDirectoryContentIsOk(toLoad)) {
                DataEntryPanel dep = new DataEntryPanel(appCont, toLoad);


                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        status.setForeground(UIHelper.DARK_GREEN_COLOR);
                        status.setText("<html>attempting to load <i>" + toLoad.getName() + "</i></html>");
                        status.setText("<html>processing files...</html>");
                    }
                });

                // we want to attempt to parse the XML since this directory should contain XML describing configurations
                if (toLoad.isDirectory()) {

                    ConfigXMLParser parser = new ConfigXMLParser(ConfigurationLoadingSource.ISACONFIGURATOR, toLoad.getAbsolutePath());
                    parser.loadConfiguration();
                    Configuration c = new Configuration(parser.getMappings());
                    for (TableReferenceObject tc : parser.getTables()) {
                        c.addTableObject(tc.getTableFields());
                    }

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            status.setText("<html>connecting to ontology resources...</html>");
                        }
                    });

                    loadTFO(c, dep);

                }
                // otherwise, this is a legacy TFO or TCO file.
                else {

                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                            toLoad));

                    if (ConfigurationFileFilter.getExtension(toLoad).equalsIgnoreCase("tfo")) {
                        Configuration c = (Configuration) ois.readObject();
                        ois.close();
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                status.setText("<html>connecting to ontology resources...</html>");
                            }
                        });
                        loadTFO(c, dep);
                    } else {
                        log.info("This format is not supported...");
                        showErrorInStatus("<html>This file format is not supported...</html>");

                    }
                }

                dep.createGUI();
                status.setText("");
                status.setIcon(null);
                appCont.hideGlassPane();
                appCont.setCurrentPage(dep);
                return true;

            } else {
                showErrorInStatus("<html>Invalid selection made. Please choose a valid ISA configuration directory...</html>");
            }
        }
        showErrorInStatus("<html>Invalid selection made. Please choose a valid ISA configuration directory...</html>");

        return false;
    }

    private void showErrorInStatus(String message) {
        status.setForeground(UIHelper.RED_COLOR);
        status.setIcon(null);
        status.setText(message);
    }


    private void loadTFO(Configuration tfo, DataEntryPanel dep) {
        for (TableConfiguration to : tfo.getTableData()) {

            List<Display> fields = new ArrayList<Display>();

            for (int i = 0; i < to.getTableStructure().keySet().size(); i++) {
                String elementName = to.getTableStructure().get(i)[0];

                Display ed = null;

                boolean set = false;
                for (FieldObject fo : to.getFields()) {
                    if (fo.getColNo() == i) {
                        ed = new FieldElement(fo);
                        set = true;
                        break;
                    }
                }

                if (!set) {
                    ed = new StructuralElementDisplay(elementName);
                }

                fields.add(ed);
            }

            tableFields.put(to.getMappingObject(), fields);
        }


        dep.setTableFields(tableFields);
    }

    private boolean checkDirectoryContentIsOk(File directory) {
        File[] files = directory.listFiles();

        for(File file : files) {
            if(ConfigurationFileFilter.getExtension(file).equalsIgnoreCase("xml")) {
                return true;
            }
        }

        return false;
    }

}
