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

import org.apache.log4j.Logger;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.AnimatableJFrame;
import org.isatools.isacreator.effects.FooterPanel;
import org.isatools.isacreator.effects.TitlePanel;
import org.isatools.isacreator.gui.ISAcreatorTitlePanel;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ISAcreatorConfigurator extends AnimatableJFrame {
    private static Logger log = Logger.getLogger(ISAcreatorConfigurator.class.getName());

    public static final int APP_WIDTH = 999;
    public static final int APP_HEIGHT = 668;

    @InjectedResource
    private Image isaConfigLogo, grip, inactiveGrip, title, inactiveTitle;

    private JPanel glass;
    private GridBagConstraints c;
    private MenuPanel mp;

    // Map of table to fields
    private JLayeredPane currentPage;

    static {

        UIManager.put("Panel.background", UIHelper.BG_COLOR);
        UIManager.put("ToolTip.foreground", UIHelper.DARK_GREEN_COLOR);
        UIManager.put("ToolTip.background", UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR);
        UIManager.put("Container.background", UIHelper.BG_COLOR);

        UIManager.put("Menu.selectionBackground", UIHelper.LIGHT_GREEN_COLOR);
        UIManager.put("MenuItem.selectionBackground", UIHelper.LIGHT_GREEN_COLOR);

        UIManager.put("PopupMenuUI", "org.isatools.isacreator.common.CustomPopupMenuUI");
        UIManager.put("MenuItemUI", "org.isatools.isacreator.common.CustomMenuItemUI");
        UIManager.put("MenuUI", "org.isatools.isacreator.common.CustomMenuUI");
        UIManager.put("SeparatorUI", "org.isatools.isacreator.common.CustomSeparatorUI");
        UIManager.put("MenuBarUI", "org.isatools.isacreator.common.CustomMenuBarUI");

        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");
        ResourceInjector.get("config-ui-package.style").load(
                ISAcreatorConfigurator.class.getResource("/dependency-injections/config-ui-package.properties"));
        ResourceInjector.get("ontologyconfigtool-package.style").load(
                ISAcreatorConfigurator.class.getResource("/dependency-injections/ontologyconfigtool-package.properties"));
    }

    public ISAcreatorConfigurator() {
        ResourceInjector.get("config-ui-package.style").inject(this);

        mp = new MenuPanel(this);
        mp.createGUI();

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.gridwidth = 2;
        c.gridheight = 2;
        c.gridx = 1;
        c.gridy = 2;
    }

    public void hideGlassPane() {
        glass.setVisible(false);
    }

    public void showGlassPane() {
        glass.setVisible(true);
    }

    public void setGlassPanelContents(Container panel) {
        if (glass != null) {
            glass.removeAll();
        }
        glass = (JPanel) getGlassPane();
        glass.setLayout(new GridBagLayout());
        glass.add(panel, c);
        glass.setBackground(UIHelper.BG_COLOR);
        glass.setVisible(true);
        glass.revalidate();
        glass.repaint();
    }

    public void creatGUI() {
        setTitle("ISAcreator Configurator 1.6");
        setIconImage(isaConfigLogo);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(APP_WIDTH, APP_HEIGHT));
        setBackground(UIHelper.BG_COLOR);
        setResizable(false);
        setUndecorated(true);

        TitlePanel titlePanel = new ConfigTitlePanel(grip, inactiveGrip, title, inactiveTitle);
        titlePanel.addPropertyChangeListener(ISAcreatorTitlePanel.CLOSE_EVENT, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                log.info("Exiting system");
                System.exit(0);
            }
        });

        add(titlePanel, BorderLayout.NORTH);
        titlePanel.installListeners();
        getRootPane().setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setCurrentPage(mp);

        FooterPanel fp = new FooterPanel(this);
        add(fp, BorderLayout.SOUTH);

        ((JComponent) getContentPane()).setBorder(new LineBorder(UIHelper.LIGHT_GREEN_COLOR, 1));

        pack();
        setVisible(true);
    }

    public MenuPanel getMp() {
        return mp;
    }


    public static void main(String[] args) {
        final ISAcreatorConfigurator main = new ISAcreatorConfigurator();
        ISAcreatorProperties.setProperty("java.version", System.getProperty("java.version"));
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                main.creatGUI();
            }
        });
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
            getContentPane().remove(currentPage);
            currentPage = newPage;
        }

        getContentPane().add(currentPage, BorderLayout.CENTER);

        if (currentPage instanceof MenuPanel) {
            mp.setGlassPaneToMenu();
        }
        validate();
    }


}
