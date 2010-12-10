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
import org.isatools.isacreatorconfigurator.effects.FooterPanel;
import org.isatools.isacreatorconfigurator.effects.MACTypeDialogFrame;
import org.isatools.isacreatorconfigurator.effects.TitlePanel;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;

public class ISAcreatorConfigurator extends MACTypeDialogFrame {

    public static final int WIDTH = 999;
    public static final int HEIGHT = 668;

    @InjectedResource
    private Image isaConfigLogo;

    private JPanel glass;
    private GridBagConstraints c;
    private MenuPanel mp;

    // Map of table to fields
    private JLayeredPane currentPage;

    static {
        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");

        ResourceInjector.get("config-ui-package.style").load(
                ISAcreatorConfigurator.class.getResource("/dependency-injections/config-ui-package.properties"));
        ResourceInjector.get("ontologyselectiontool-package.style").load(
                ISAcreatorConfigurator.class.getResource("/dependency-injections/ontologyselectiontool-package.properties"));
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
        setTitle("ISAcreator Configurator");
        setIconImage(isaConfigLogo);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(UIHelper.BG_COLOR);
        setResizable(false);
        setUndecorated(true);

        TitlePanel titlePan = new ConfigTitlePanel();
        add(titlePan, BorderLayout.NORTH);
        titlePan.installListeners();
        getRootPane().setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setCurrentPage(mp);

        FooterPanel fp = new FooterPanel(this);
        add(fp, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    public MenuPanel getMp() {
        return mp;
    }


    public static void main(String[] args) {
        UIManager.put("Panel.background", UIHelper.BG_COLOR);
        UIManager.put("ToolTip.foreground", Color.white);
        UIManager.put("ToolTip.background", UIHelper.DARK_GREEN_COLOR);
        UIManager.put("Container.background", UIHelper.BG_COLOR);
        UIManager.put("PopupMenuUI", "org.isatools.isacreatorconfigurator.common.CustomPopupMenuUI");
        UIManager.put("MenuItemUI", "org.isatools.isacreatorconfigurator.common.CustomMenuItemUI");
        UIManager.put("MenuUI", "org.isatools.isacreatorconfigurator.common.CustomMenuUI");
        UIManager.put("SeparatorUI", "org.isatools.isacreatorconfigurator.common.CustomSeparatorUI");
        UIManager.put("MenuBarUI", "org.isatools.isacreatorconfigurator.common.CustomMenuBarUI");

        final ISAcreatorConfigurator main = new ISAcreatorConfigurator();
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
