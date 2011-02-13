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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


/**
 * Represents a panel which contains a label to tell the user what we're expecting,
 * a jtextfield to hold the selected file (uneditable), and a button to invoke the appearance
 * of the file chooser!
 */

public class FileSelectionPanel extends JPanel {

    public static final int OPEN = 0;
    public static final int SAVE = 1;

    private ImageIcon fileSelect = new ImageIcon(getClass().getResource("/images/common/select_file.png"));
    private ImageIcon fileSelectOver = new ImageIcon(getClass().getResource("/images/common/select_file_over.png"));

    private JTextField fileToUse;
    private String text;
    private JFileChooser fileChooser;
    private Font textFont;
    private Color textColor;

    public FileSelectionPanel(String text) {
        this(text, null);
    }

    public FileSelectionPanel(String text, JFileChooser fileChooser) {
        this(text, fileChooser, UIHelper.VER_12_BOLD, UIHelper.GREY_COLOR);
    }

    public FileSelectionPanel(String text, JFileChooser fileChooser, Font textFont, Color textColor) {
        this.text = text;

        if (fileChooser == null) {
            this.fileChooser = new JFileChooser();
        } else {
            this.fileChooser = fileChooser;
        }

        this.textFont = textFont;
        this.textColor = textColor;

        setLayout(new GridLayout(1, 2));
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGUI();
            }
        });
    }

    private void createGUI() {

        add(UIHelper.createLabel(text, textFont, textColor));

        // create field for viewing the file location selected (uneditable) and the label representing a file selection action
        JPanel fileSelectionUtil = new JPanel();
        fileSelectionUtil.setLayout(new BoxLayout(fileSelectionUtil, BoxLayout.LINE_AXIS));

        fileToUse = new JTextField();
        fileToUse.setEditable(false);
        UIHelper.renderComponent(fileToUse, textFont, textColor, false);

        fileSelectionUtil.add(fileToUse);

        final JLabel selectFileButton = new JLabel(fileSelect);
        selectFileButton.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent event) {

            }

            public void mouseEntered(MouseEvent event) {
                selectFileButton.setIcon(fileSelectOver);
            }

            public void mouseExited(MouseEvent event) {
                selectFileButton.setIcon(fileSelect);
            }

            public void mousePressed(MouseEvent event) {
                selectFileButton.setIcon(fileSelect);

                if (fileChooser.showOpenDialog(getInstance()) == JFileChooser.APPROVE_OPTION) {
                    if (fileChooser.getSelectedFile() != null) {
                        fileToUse.setText(fileChooser.getSelectedFile().getPath());
                    }
                }
            }

            public void mouseReleased(MouseEvent event) {

            }
        });
        fileSelectionUtil.add(selectFileButton);
        add(fileSelectionUtil);
    }

    public String getSelectedFilePath() {
        return fileToUse.getText();
    }

    private JPanel getInstance() {
        return this;
    }
}
