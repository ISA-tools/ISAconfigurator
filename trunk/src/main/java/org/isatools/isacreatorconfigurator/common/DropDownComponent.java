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

package org.isatools.isacreatorconfigurator.common;

import org.isatools.isacreatorconfigurator.ontologyselectiontool.OntologySelector;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.*;


/**
 * DropDownComponent is used to provide an intuitive way for components such as the Calendar component
 * and OntologySelectionTool to be displayed.
 *
 * @author Eamonn Maguire
 * @date May 21, 2008
 */
public class DropDownComponent extends JComponent implements ActionListener,
        AncestorListener {
    protected JComponent visibleComponent;
    protected JFrame dropDownFrameComponent;
    protected JLabel arrow;
    protected JWindow dropDownWindowComponent;

    /**
     * Creates a DropDownComponent for a JWindow type component.
     *
     * @param visibleComponent  - visible component (e.g. a JTextField)
     * @param dropDownComponent - e.g. FileChooser, etc.
     */
    public DropDownComponent(JComponent visibleComponent,
                             JWindow dropDownComponent) {
        this.visibleComponent = visibleComponent;
        this.visibleComponent.setPreferredSize(new Dimension(visibleComponent.getWidth(), 20));
        this.dropDownWindowComponent = dropDownComponent;

        arrow = new JLabel(new ImageIcon(getClass()
                .getResource("/images/gui/dropDownImage.png")));

        arrow.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent event) {
            }

            public void mousePressed(MouseEvent event) {
                addListener(dropDownWindowComponent);
                showPopup(dropDownWindowComponent);
            }

            public void mouseReleased(MouseEvent event) {
            }

            public void mouseEntered(MouseEvent event) {
            }

            public void mouseExited(MouseEvent event) {
            }
        });
        addAncestorListener(this);

        setupLayout();
    }

    /**
     * Creates a DropDownComponent for a JFrame type component.
     *
     * @param visibleComponent  - visible component (e.g. a JTextField)
     * @param dropDownComponent - e.g. OntologySelectionTool, etc.
     */
    public DropDownComponent(JComponent visibleComponent,
                             JFrame dropDownComponent) {
        this(visibleComponent, dropDownComponent, new ImageIcon(DropDownComponent.class
                .getResource("/images/general_gui/dropDownImage.png")));
    }

    /**
     * Creates a DropDownComponent for a JFrame type component.
     *
     * @param visibleComponent  - visible component (e.g. a JTextField)
     * @param dropDownComponent - e.g. OntologySelectionTool, etc.
     * @param image             - image to use for the dropdown image
     */
    public DropDownComponent(JComponent visibleComponent,
                             JFrame dropDownComponent, ImageIcon image) {
        this.visibleComponent = visibleComponent;
        this.dropDownFrameComponent = dropDownComponent;

        arrow = new JLabel(image);

        arrow.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent event) {
            }

            public void mousePressed(MouseEvent event) {
                if (dropDownFrameComponent instanceof OntologySelector) {
                    OntologySelector ost = (OntologySelector) dropDownFrameComponent;
                    ost.updatehistory();
                }

                addListener(dropDownFrameComponent);
                showPopup(dropDownFrameComponent);
            }

            public void mouseReleased(MouseEvent event) {
            }

            public void mouseEntered(MouseEvent event) {
            }

            public void mouseExited(MouseEvent event) {
            }
        });

        addAncestorListener(this);

        setupLayout();
    }

    public void actionPerformed(ActionEvent evt) {
        // build pop-up window
        if (dropDownFrameComponent != null) {
            if (dropDownFrameComponent instanceof OntologySelector) {
                OntologySelector ost = (OntologySelector) dropDownFrameComponent;
                ost.updatehistory();
            }

            addListener(dropDownFrameComponent);
            showPopup(dropDownFrameComponent);
        } else {
            addListener(dropDownWindowComponent);
            showPopup(dropDownWindowComponent);
        }
    }

    private void addListener(final Window container) {
        container.addWindowFocusListener(new WindowAdapter() {
            public void windowLostFocus(WindowEvent evt) {
                container.setVisible(false);
            }
        });
        container.pack();
    }

    public void ancestorAdded(AncestorEvent event) {
        if (dropDownFrameComponent != null) {
            hidePopup(dropDownFrameComponent);
        } else {
            hidePopup(dropDownWindowComponent);
        }
    }

    public void ancestorMoved(AncestorEvent event) {
        if (dropDownFrameComponent != null) {
            if (event.getSource() != dropDownFrameComponent) {
                hidePopup(dropDownFrameComponent);
            }
        } else {
            if (event.getSource() != dropDownWindowComponent) {
                hidePopup(dropDownWindowComponent);
            }
        }
    }

    public void ancestorRemoved(AncestorEvent event) {
        if (dropDownFrameComponent != null) {
            hidePopup(dropDownFrameComponent);
        } else {
            hidePopup(dropDownWindowComponent);
        }
    }

    public void disableElements() {
        visibleComponent.setEnabled(false);
        arrow.setEnabled(false);
    }

    public void enableElements() {
        visibleComponent.setEnabled(true);
        arrow.setEnabled(true);
    }

    protected Frame getFrame(Component comp) {
        if (comp == null) {
            comp = this;
        }

        if (comp.getParent() instanceof Frame) {
            return (Frame) comp.getParent();
        }

        return getFrame(comp.getParent());
    }

    /**
     * Hide the Window from view
     *
     * @param container - Window to hide.
     */
    public void hidePopup(Window container) {
        if ((container != null) && container.isVisible()) {
            container.setVisible(false);
        }
    }

    protected void setupLayout() {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gbl);

        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(visibleComponent, c);
        add(visibleComponent);

        c.weightx = 0;
        c.gridx++;
        gbl.setConstraints(arrow, c);
        add(arrow);
    }

    /**
     * Show the window.
     */
    private void showPopup(Window container) {
        Point pt = visibleComponent.getLocationOnScreen();
        pt.translate(0, visibleComponent.getHeight());
        container.setLocation(pt);
        container.toFront();
        container.setVisible(true);
        container.requestFocusInWindow();
    }
}
