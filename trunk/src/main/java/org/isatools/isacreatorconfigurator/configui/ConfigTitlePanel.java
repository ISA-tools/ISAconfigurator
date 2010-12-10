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

import org.isatools.isacreatorconfigurator.effects.TitlePanel;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * ConfigTitlePanel
 *
 * @author eamonnmaguire
 * @date Oct 15, 2009
 */


public class ConfigTitlePanel extends TitlePanel {

    private JButton minimiseButton;
    private JButton maximiseButton;

    @InjectedResource
    private Image grip, inactiveGrip, title, inactiveTitle, minimise, minimiseInactive,
            minimiseOver, minimisePressed, maximise, maximiseInactive, maximiseOver, maximisePressed, backgroundGradient;


    public ConfigTitlePanel() {

        ResourceInjector.get("config-ui-package.style").inject(this);
        setBackgroundGradient(backgroundGradient);
        createGUI();
    }

    protected void drawComponents(Graphics2D g2d, boolean active) {
        g2d.drawImage(active ? grip : inactiveGrip, 0, 0, null);
        g2d.drawImage(active ? title : inactiveTitle, getWidth() / 2 - title.getWidth(null) / 2, 0, null);
    }


    protected void createButtons() {
        add(Box.createHorizontalGlue(),
                new GridBagConstraints(0, 0,
                        1, 1, 1.0, 1.0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0),
                        0, 0));

        add(minimiseButton = createButton(new IconifyAction(),
                minimise, minimisePressed, minimiseOver),
                new GridBagConstraints(1, 0,
                        1, 1,
                        0.0, 1.0,
                        GridBagConstraints.NORTHEAST,
                        GridBagConstraints.NONE,
                        new Insets(1, 0, 0, 2),
                        0, 0));

        add(maximiseButton = createButton(new ResizeAction(),
                maximise, maximisePressed, maximiseOver),
                new GridBagConstraints(2, 0,
                        1, 1,
                        0.0, 1.0,
                        GridBagConstraints.NORTHEAST,
                        GridBagConstraints.NONE,
                        new Insets(1, 0, 0, 2),
                        0, 0));

    }

    private class IconifyAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            iconify();
        }
    }

    private void iconify() {
        Frame frame = (Frame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.setExtendedState(frame.getExtendedState() | Frame.ICONIFIED);
        }
    }

    private class ResizeAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            resize();
        }
    }

    private void resize() {
        Frame frame = (Frame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {

            GraphicsEnvironment env =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle screenDimensions = env.getMaximumWindowBounds();

            if (screenDimensions.width == frame.getWidth() || screenDimensions.height == frame.getHeight()) {
                frame.setSize(ISAcreatorConfigurator.WIDTH, ISAcreatorConfigurator.HEIGHT);
                frame.setLocation(screenDimensions.x, screenDimensions.y);
            } else {
                frame.setSize(screenDimensions.width, screenDimensions.height);
                frame.setLocation(screenDimensions.x, screenDimensions.y);
            }
        }
    }

    protected WindowAdapter getWindowHandler() {

        return new WindowAdapter() {

            public void windowActivated(WindowEvent ev) {
                minimiseButton.setIcon(new ImageIcon(minimise));
                maximiseButton.setIcon(new ImageIcon(maximise));
                getRootPane().repaint();
            }

            @Override
            public void windowDeactivated(WindowEvent ev) {
                minimiseButton.setIcon(new ImageIcon(minimiseInactive));
                maximiseButton.setIcon(new ImageIcon(maximiseInactive));
                getRootPane().repaint();
            }


        };
    }
}
