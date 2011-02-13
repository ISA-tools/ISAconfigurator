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

import org.isatools.isacreatorconfigurator.common.FileSelectionPanel;
import org.isatools.isacreatorconfigurator.common.UIHelper;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * ExportConfigurationDialog provides interface to allow user to select which directory to save the configurations in and to
 * say what to call the configuration.
 *
 * @author eamonnmaguire
 * @date Nov 10, 2009
 */


public class ExportConfigurationDialog extends JDialog {

    @InjectedResource
    private ImageIcon headerImage, closeIcon, closeIconOver, okIcon, okIconOver;

    private JLabel status;

    public ExportConfigurationDialog() {
        ResourceInjector.get("config-ui-package.style").inject(this);
    }

    public void createGUI() {
        setBackground(UIHelper.BG_COLOR);
        instantiatePanel();
        pack();
    }

    private void instantiatePanel() {

        JLabel headerLab = new JLabel(headerImage, SwingConstants.RIGHT);
        headerLab.setBackground(UIHelper.BG_COLOR);

        add(UIHelper.wrapComponentInPanel(headerLab), BorderLayout.NORTH);

        // setup center panel to contain data entry facility for user.
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
        centerPanel.setBackground(UIHelper.BG_COLOR);

        JPanel fileNamePanel = new JPanel(new GridLayout(1, 2));
        fileNamePanel.setOpaque(false);

        JLabel fileNameLab = new JLabel("Please enter configuration name...");
        UIHelper.renderComponent(fileNameLab, UIHelper.VER_12_BOLD, UIHelper.GREY_COLOR, false);

        final JTextField fileNameTxt = new JTextField(
                "isaconfig-" + getDateString());
        fileNameTxt.setBackground(UIHelper.BG_COLOR);
        UIHelper.renderComponent(fileNameTxt, UIHelper.VER_12_BOLD, UIHelper.GREY_COLOR, false);

        fileNamePanel.add(fileNameLab);
        fileNamePanel.add(fileNameTxt);
        centerPanel.add(fileNamePanel);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Where do you want to save the Configuration");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setApproveButtonText("Export here...");

        final FileSelectionPanel configDirLocation = new FileSelectionPanel("Choose where to save configuration...", fileChooser);

        centerPanel.add(configDirLocation);

        JPanel statusPanel = new JPanel(new GridLayout(1, 1));
        statusPanel.setPreferredSize(new Dimension(300, 30));
        statusPanel.setBackground(UIHelper.BG_COLOR);

        status = new JLabel();
        UIHelper.renderComponent(status, UIHelper.VER_12_BOLD, UIHelper.RED_COLOR, false);

        statusPanel.add(status);

        centerPanel.add(statusPanel);

        add(centerPanel, BorderLayout.CENTER);

        // setup south panel with buttons and so forth :o)
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(UIHelper.BG_COLOR);

        final JLabel close = new JLabel(closeIcon,
                JLabel.LEFT);
        close.setOpaque(false);
        close.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                firePropertyChange("windowClosed", "", "none");
            }

            public void mouseEntered(MouseEvent event) {
                close.setIcon(closeIconOver);
            }

            public void mouseExited(MouseEvent event) {
                close.setIcon(closeIcon);
            }
        });

        Action saveAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                save(configDirLocation.getSelectedFilePath().trim(), fileNameTxt.getText().trim());
            }
        };

        fileNameTxt.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "SAVE");
        fileNameTxt.getActionMap().put("SAVE", saveAction);

        final JLabel save = new JLabel(okIcon, JLabel.RIGHT);
        save.setOpaque(false);
        save.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                save(configDirLocation.getSelectedFilePath().trim(), fileNameTxt.getText().trim());
            }

            public void mouseEntered(MouseEvent event) {
                save.setIcon(okIconOver);
            }

            public void mouseExited(MouseEvent event) {
                save.setIcon(okIcon);
            }
        });

        southPanel.add(close, BorderLayout.WEST);
        southPanel.add(save, BorderLayout.EAST);

        add(southPanel, BorderLayout.SOUTH);
    }

    public void save(String saveDir, String configName) {
        if (!saveDir.equals("")) {
            if (!configName.equals("") &&
                    !configName.contains("Please enter a filename...")) {

                // take directory to save in and prepend to file name.

                File f = new File(saveDir + File.separator +
                        configName);

                if (!f.exists()) {
                    f.mkdir();
                    firePropertyChange("save", "", f.getAbsolutePath());
                } else {
                    status.setText(
                            "<html>Configuration with same name already exists in <b>\"" + saveDir + "\"</b> folder</html>");
                }
            } else {
                status.setText("<html><b>Invalid configuration name</b></html>");
            }
        } else {
            status.setText("<htmL>Please select a directory to output configuration to...</html>");
        }
    }

    private String getDateString() {
        Calendar c = new GregorianCalendar();
        return c.get(Calendar.YEAR) + "" + (c.get(Calendar.MONTH) + 1) + "" + c.get(Calendar.DAY_OF_MONTH);
    }
}
