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

package org.isatools.isacreatorconfigurator.effects;

import org.isatools.isacreatorconfigurator.common.UIHelper;

import javax.swing.*;
import java.awt.*;

/**
 * @author: eamonnmaguire
 * @date May 26, 2009
 */


public class ColorPanel extends JComponent {
    private String title;
    private Color backgroundColor;
    private Color foregroundColor;
    private int width;

    public ColorPanel(String title, Color backgroundColor, Color foregroundColor, int width) {
        this.title = title;
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
        this.width = width;
        setBackground(UIHelper.BG_COLOR);
    }


    @Override
    public void paint(Graphics graphics) {
        int height = calculateHeight();

        setSize(width + 5, height + 5);

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setFont(UIHelper.VER_12_PLAIN);
        g2d.setColor(backgroundColor);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.fillRoundRect(5, 5, width, height, 10, 10);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
        int titleWidth = fm.stringWidth(title);

        g2d.setColor(UIHelper.BG_COLOR);
        g2d.fillRoundRect((width - titleWidth) - 2, 0, width, fm.getHeight(), 5, 5);
        g2d.setColor(UIHelper.GREY_COLOR);
        g2d.drawString(title, (width - titleWidth), fm.getHeight() - 1);
        super.paint(graphics);
    }

    private int calculateHeight() {
        int height = 0;
        for (Component c : getComponents()) {
            height += c.getHeight();
        }
        // return height plus a value to compensate for the space required for the header and footer.
        return height;
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("test frame");
        f.setBackground(UIHelper.BG_COLOR);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setPreferredSize(new Dimension(350, 125));

        ColorPanel cp = new ColorPanel("Parameter", new Color(51, 51, 51, 100), new Color(255, 255, 255), 350);
        cp.setLayout(new BoxLayout(cp, BoxLayout.PAGE_AXIS));

        JPanel testEntryCont = new JPanel();
        testEntryCont.setOpaque(false);
        testEntryCont.setLayout(new BoxLayout(testEntryCont, BoxLayout.PAGE_AXIS));
        testEntryCont.add(Box.createVerticalStrut(15));
        testEntryCont.add(UIHelper.createLabel("test1", UIHelper.VER_12_PLAIN, UIHelper.BG_COLOR));
        testEntryCont.add(UIHelper.createLabel("test2", UIHelper.VER_12_PLAIN, UIHelper.BG_COLOR));

        testEntryCont.setBounds(new Rectangle(10, 20, testEntryCont.getWidth(), testEntryCont.getHeight()));
        cp.add(testEntryCont);

        f.add(cp, BorderLayout.CENTER);
        f.pack();
        f.setVisible(true);
    }
}
