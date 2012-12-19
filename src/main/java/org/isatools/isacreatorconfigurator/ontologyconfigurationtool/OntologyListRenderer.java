/**
 ISAconfigurator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAconfigurator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 ÒThe contents of this file are subject to the CPAL version 1.0 (the ÒLicenseÓ); you may not use this file except
 in compliance with the License. You may obtain a copy of the License at http://isa-tools.org/licenses/ISAconfigurator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections 14 and 15 have been added to cover use of software over
 a computer network and provide for limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 consistent with Exhibit B.

 Software distributed under the License is distributed on an ÒAS ISÓ basis, WITHOUT WARRANTY OF ANY KIND, either express
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
import org.isatools.isacreator.configuration.Ontology;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * OntologyListRenderer
 *
 * @author eamonnmaguire
 * @date Mar 30, 2010
 */


public class OntologyListRenderer extends JPanel
        implements ListCellRenderer {
    static Color listForeground = UIHelper.DARK_GREEN_COLOR;

    @InjectedResource
    private ImageIcon olsIcon, bioPortalIcon;


    private JLabel icon;
    private JLabel text;

    /**
     * CustomListCellRenderer Constructor
     */
    public OntologyListRenderer() {
        ResourceInjector.get("ontologyconfigtool-package.style").inject(this);

        setLayout(new BorderLayout());

        icon = new JLabel(olsIcon);
        add(icon, BorderLayout.WEST);

        text = UIHelper.createLabel("", UIHelper.VER_10_PLAIN, listForeground);

        add(text, BorderLayout.CENTER);
        setBorder(new EmptyBorder(1, 1, 1, 1));
    }

    /**
     * Sets all list values to have a white background and green foreground.
     *
     * @param jList        - List to render
     * @param val          - value of list item being rendered.
     * @param index        - list index for value to be renderered.
     * @param selected     - is the value selected?
     * @param cellGotFocus - has the cell got focus?
     * @return - The CustomListCellRendered Component.
     */
    public Component getListCellRendererComponent(JList jList, Object val,
                                                  int index, boolean selected, boolean cellGotFocus) {
        String stringRepresentation = val.toString();
        if (val instanceof Ontology) {

            Ontology o = (Ontology) val;

            // ols versions are now like Jun 2010 rather than their previous version format 1.26
            if (o.getOntologyVersion().length() > 5) {
                icon.setIcon(olsIcon);
            } else {
                icon.setIcon(bioPortalIcon);
            }

            stringRepresentation = o.getOntologyAbbreviation() + " - " + o.getOntologyDisplayLabel();
        }

        text.setText(stringRepresentation);
        text.setFont(selected ? UIHelper.VER_10_BOLD : UIHelper.VER_10_PLAIN);
        return this;
    }
}
