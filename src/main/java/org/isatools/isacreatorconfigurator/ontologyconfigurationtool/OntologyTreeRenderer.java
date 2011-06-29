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

package org.isatools.isacreatorconfigurator.ontologyconfigurationtool;

import org.isatools.isacreator.common.UIHelper;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import java.awt.*;

/**
 * OntologyTreeRenderer
 *
 * @author eamonnmaguire
 * @date Feb 4, 2010
 */


public class OntologyTreeRenderer implements TreeCellRenderer {

    @InjectedResource
    private ImageIcon node, nodeSelected, branchOpen, branchClosed, rootOpen, rootClosed;

    private JPanel contents;
    private JLabel icon;
    private JLabel text;

    public OntologyTreeRenderer() {

        ResourceInjector.get("ontologyconfigtool-package.style").inject(this);

        contents = new JPanel();
        contents.setLayout(new BorderLayout());
        contents.setOpaque(false);

        icon = new JLabel();
        text = UIHelper.createLabel("", UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR);

        contents.add(icon, BorderLayout.WEST);
        contents.add(text, BorderLayout.CENTER);
    }

    /**
     * Sets all list values to have a white background and green foreground if not selected, and
     * a green background and white foregroud if selected.
     *
     * @param tree     - List to render
     * @param val      - value of list item being rendered.
     * @param index    - list index for value to be renderered.
     * @param selected - is the value selected?
     * @param hasFocus - has the cell got focus?
     * @return - The CustomListCellRendered Component.
     */
    public Component getTreeCellRendererComponent(JTree tree, Object val, boolean selected, boolean expanded, boolean leaf, int index, boolean hasFocus) {

        if (leaf) {
            icon.setIcon(selected ? nodeSelected : node);
        } else if (((TreeNode) val).getParent() == null) {
            icon.setIcon(expanded ? rootOpen : rootClosed);
        } else {
            icon.setIcon(expanded ? branchOpen : branchClosed);
        }

        String toDisplay = val.toString();
        if (toDisplay.contains("#")) {
            toDisplay = toDisplay.substring(toDisplay.lastIndexOf("#") + 1, toDisplay.length() - 1);
        }
        text.setText(toDisplay);

        // change text colour depending on selection
        text.setFont(!leaf ? UIHelper.VER_11_BOLD : UIHelper.VER_11_PLAIN);


        text.setForeground(selected ? UIHelper.LIGHT_GREEN_COLOR : UIHelper.DARK_GREEN_COLOR);
        text.revalidate();

        return contents;
    }
}

