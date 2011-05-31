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

package org.isatools.isacreatorconfigurator.ontologyconfigurationtool;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreatorconfigurator.common.UIHelper;
import org.isatools.isacreatorconfigurator.configdefinition.Ontology;
import org.isatools.isacreatorconfigurator.configdefinition.OntologyBranch;
import org.isatools.isacreatorconfigurator.informationwindow.InformationPane;
import org.isatools.isacreatorconfigurator.ontologymanager.OntologyQueryAdapter;
import org.isatools.isacreatorconfigurator.ontologymanager.OntologyService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

/**
 * ViewTermDefinitionDialog
 *
 * @author eamonnmaguire
 * @date Feb 8, 2010
 */


public class ViewTermDefinitionUI extends InformationPane {

//	private static final Image LOGO = new ImageIcon(ViewTermDefinitionUI.class.getResource("/images/ontologyconfigurationtool/term_metadata_util_header_active.png")).getImage();
//	private static final Image LOGO_INACTIVE = new ImageIcon(ViewTermDefinitionUI.class.getResource("/images/ontologyconfigurationtool/term_metadata_util_header_inactive.png")).getImage();

    private JPanel swappableContainer;

    private Map<String, String> properties;

    public ViewTermDefinitionUI() {
        swappableContainer = new JPanel(new BorderLayout());
        swappableContainer.setBackground(UIHelper.BG_COLOR);
        swappableContainer.setPreferredSize(new Dimension(240, 270));
        add(swappableContainer, BorderLayout.CENTER);
    }

    public void instantiateFrame() {

    }

    protected void createContentPane() {
        swappableContainer.add(new JLabel(LOADING));
    }

    private JPanel createOntologyInformationPane(OntologyBranch term) {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(2, 2, 2, 2));

        JEditorPane ontologyInfoPane = createOntologyInformationDisplay(term);

        JScrollPane ontologyInfoScroller = new JScrollPane(ontologyInfoPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        ontologyInfoScroller.getViewport().setBackground(UIHelper.BG_COLOR);
        ontologyInfoScroller.setBorder(new EmptyBorder(2, 2, 2, 2));

        IAppWidgetFactory.makeIAppScrollPane(ontologyInfoScroller);

        contentPane.add(ontologyInfoScroller, BorderLayout.CENTER);

        return contentPane;
    }

    private JEditorPane createOntologyInformationDisplay(OntologyBranch term) {

        JEditorPane ontologyInfo = new JEditorPane();
        ontologyInfo.setContentType("text/html");
        ontologyInfo.setEditable(false);
        ontologyInfo.setBackground(UIHelper.BG_COLOR);

        String labelContent = "<html>" + "<head>" +
                "<style type=\"text/css\">" + "<!--" + ".bodyFont {" +
                "   font-family: Verdana;" + "   font-size: 8.5px;" +
                "   color: #006838;" + "}" + "-->" + "</style>" + "</head>" +
                "<body class=\"bodyFont\">";

        labelContent += "<p><b>term name: </b>";
        labelContent += term.getBranchName() + "</p>";

        if (properties.size() > 0) {
            for (String propertyType : properties.keySet()) {
                if (propertyType != null) {
                    labelContent += ("<p><b>" + propertyType + ": </b>");
                    labelContent += (properties.get(propertyType) + "</p>");
                }
            }
        } else {
            labelContent += "connection to the ontology service failed. term definition retrieval was unsuccessful!";
        }

        labelContent += "</body></html>";

        ontologyInfo.setText(labelContent);

        return ontologyInfo;
    }


    public void setContent(OntologyBranch term, Ontology searchOntology, OntologyService ontologyService) {
        setCurrentPage(new JLabel(LOADING));
        performSearch(term, searchOntology, ontologyService);
    }

    private void performSearch(final OntologyBranch term, final Ontology searchOntology, final OntologyService ontologyService) {
        Thread performer = new Thread(new Runnable() {
            public void run() {
                try {
                    properties = ontologyService.getTermMetadata(term.getBranchIdentifier(), new OntologyQueryAdapter(searchOntology).getOntologyQueryString(OntologyQueryAdapter.GET_VERSION));
                    setCurrentPage(createOntologyInformationPane(term));
                } catch (Exception e) {
                    setCurrentPage(createOntologyInformationPane(term));
                    System.err.println("Failed to connect to ontology client: " + e.getMessage());
                } finally {
                    ViewTermDefinitionUI.this.validate();
                    ViewTermDefinitionUI.this.repaint();
                }
            }
        });

        performer.start();
    }

    protected void createButtonPane() {

    }

    /**
     * Changes Container being shown in the swappableContainer panel
     *
     * @param newContainer - Container to change to
     */
    private void setCurrentPage(Container newContainer) {
        swappableContainer.removeAll();
        swappableContainer.add(newContainer);
        swappableContainer.validate();
    }
}