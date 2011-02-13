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
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * Provides interface to check a regular expression against input provided by the user so that they can ensure that
 * the regular expression prevents against incorrect fields in the correct way.
 *
 * @author Eamonn Maguire
 */
public class CheckRegExGUI extends JDialog implements MouseListener {
    private JTextField regExp, testCase;
    private JLabel result, test;
    private String expression;

    @InjectedResource
    private ImageIcon titleImage, closeIcon, closeIconOver, testRegEx;

    /**
     * Constructor for CheckRegExpGUI
     *
     * @param expression - Expression to be checked
     */
    public CheckRegExGUI(String expression) {
        // construct the GUI
        this.expression = expression;
        ResourceInjector.get("config-ui-package.style").inject(this);
    }

    public void createGUI() {
        setBackground(UIHelper.BG_COLOR);

        JPanel headerPanel = new JPanel(new GridLayout(1, 1));
        headerPanel.setBackground(UIHelper.BG_COLOR);

        JLabel header = new JLabel(titleImage,
                JLabel.RIGHT);
        header.setOpaque(false);

        headerPanel.add(header);

//		add(headerPanel, BorderLayout.NORTH);

        JPanel container = new JPanel();
        container.setPreferredSize(new Dimension(300, 100));
        container.setBackground(UIHelper.BG_COLOR);

        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));

        container.add(headerPanel);

        regExp = new JTextField(expression);
        regExp.setPreferredSize(new Dimension(100, 20));
        regExp.setOpaque(false);
        regExp.setEnabled(false);

        testCase = new JTextField("Enter Test Expression Here");
        testCase.setOpaque(false);
        testCase.setPreferredSize(new Dimension(100, 20));

        result = new JLabel();
        result.setOpaque(false);

        JPanel regExpCont = new JPanel(new GridLayout(1, 2));
        regExpCont.setOpaque(false);

        JLabel regExLab = UIHelper.createLabel("Regular Expression:");

        regExpCont.add(regExLab);
        regExpCont.add(regExp);
        container.add(regExpCont);

        JPanel testCaseCont = new JPanel(new GridLayout(1, 2));
        testCaseCont.setOpaque(false);

        JLabel testCaseLab = UIHelper.createLabel("Test Case:");

        testCaseCont.add(testCaseLab);
        testCaseCont.add(testCase);
        container.add(testCaseCont);

        container.add(result);

        JPanel testButtonCont = new JPanel(new BorderLayout());
        testButtonCont.setOpaque(false);

        final JLabel close = new JLabel(closeIcon,
                JLabel.LEFT);
        close.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                close.setIcon(closeIcon);
                firePropertyChange("close", "close", "");
            }

            public void mouseEntered(MouseEvent event) {
                close.setIcon(closeIconOver);
            }

            public void mouseExited(MouseEvent event) {
                close.setIcon(closeIcon);
            }
        });
        close.setOpaque(false);
        testButtonCont.add(close, BorderLayout.WEST);

        test = new JLabel(testRegEx,
                JLabel.RIGHT);
        test.addMouseListener(this);
        testButtonCont.add(test, BorderLayout.EAST);
//		container.add(testButtonCont);
        add(container, BorderLayout.NORTH);
        add(testButtonCont, BorderLayout.SOUTH);
        pack();
    }

    public void mouseClicked(MouseEvent event) {
    }

    public void mousePressed(MouseEvent event) {
        if (event.getSource() == test) {
            try {
                Pattern pattern = Pattern.compile(regExp.getText());
                Matcher matcher = pattern.matcher(testCase.getText());

                if (matcher.matches()) {
                    result.setText("Passed Regular Expression Test!");
                    result.setForeground(UIHelper.DARK_GREEN_COLOR);
                } else {
                    result.setText("Failed Regular Expression Test!");
                    result.setForeground(Color.RED);
                }
            } catch (PatternSyntaxException pse) {
                result.setText("Invalid pattern!!");
                result.setForeground(Color.RED);
            }
        }
    }

    public void mouseReleased(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
    }
}