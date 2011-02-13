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

/**
 * TableListRenderer
 *
 * @author eamonnmaguire
 * @date May 11, 2010
 */


public class ElementListRenderer implements ListCellRenderer {

    private JPanel contents;
    private JLabel icon;
    private JLabel text;

    private Color unselectedBG = UIHelper.BG_COLOR;
    private Color selectedBG =  UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR;

    @InjectedResource
    private ImageIcon protocolNode, protocolNodeNS, characteristicNode, characteristicNodeNS, factorNode, factorNodeNS,
            commentNode, commentNodeNS, parameterNode, parameterNodeNS, separator,
            genericNode, genericNodeNS;


    public ElementListRenderer() {

        ResourceInjector.get("config-ui-package.style").inject(this);

        contents = new JPanel(new BorderLayout());
        contents.setOpaque(true);

        icon = new JLabel();
        text = UIHelper.createLabel("", UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR);


        JPanel iconContainer = new JPanel();
        iconContainer.setLayout(new BoxLayout(iconContainer, BoxLayout.LINE_AXIS));

        iconContainer.add(icon);
        iconContainer.add(new JLabel(separator));

        contents.add(iconContainer, BorderLayout.WEST);
        contents.add(text, BorderLayout.CENTER);
    }


    public Component getListCellRendererComponent(JList jList, Object value, int index, boolean selected, boolean hasFocus) {

        String valueStr = value.toString().toLowerCase().trim();
        if (valueStr.contains("protocol ref")) {
            icon.setIcon(selected ? protocolNode : protocolNodeNS);
        } else if (valueStr.contains("characteristics[")) {
            icon.setIcon(selected ? characteristicNode : characteristicNodeNS);
        } else if (valueStr.contains("comment[")) {
            icon.setIcon(selected ? commentNode : commentNodeNS);
        } else if (valueStr.contains("parameter value[")) {
            icon.setIcon(selected ? parameterNode : parameterNodeNS);
        } else if (valueStr.contains("factor value")) {
            icon.setIcon(selected ? factorNode : factorNodeNS);
        } else if (valueStr.equalsIgnoreCase("factors")) {
            icon.setIcon(selected ? factorNode : factorNodeNS);
        } else if (valueStr.equalsIgnoreCase("characteristics")) {
            icon.setIcon(selected ? characteristicNode : characteristicNodeNS);
        } else {
            icon.setIcon(selected ? genericNode : genericNodeNS);
        }

        contents.setBackground(selected ? selectedBG : unselectedBG);

        text.setForeground(UIHelper.GREY_COLOR);

        text.setText(value.toString());

        return contents;
    }
}