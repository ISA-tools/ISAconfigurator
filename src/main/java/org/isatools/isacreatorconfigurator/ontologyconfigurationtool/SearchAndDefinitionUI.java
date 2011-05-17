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

import org.isatools.isacreatorconfigurator.common.UIHelper;
import org.isatools.isacreatorconfigurator.configdefinition.Ontology;
import org.isatools.isacreatorconfigurator.configdefinition.OntologyBranch;
import org.isatools.isacreatorconfigurator.ontologymanager.BioPortalClient;
import org.isatools.isacreatorconfigurator.ontologymanager.OLSClient;
import org.isatools.isacreatorconfigurator.ontologymanager.OntologyService;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * SearchAndDefinitionUI
 *
 * @author eamonnmaguire
 * @date Feb 17, 2010
 */


public class SearchAndDefinitionUI extends JPanel implements TreeObserver {
    private static final ImageIcon ADDITIONAL_FUNCTION_SIDE_LEFT = new ImageIcon(SearchAndDefinitionUI.class.getResource("/images/ontologyconfigurationtool/additionalFunctions.png"));
    private static final ImageIcon ADDITIONAL_FUNCTION_SIDE_RIGHT = new ImageIcon(SearchAndDefinitionUI.class.getResource("/images/ontologyconfigurationtool/additionFunctions_end.png"));
    private static final ImageIcon FIND_TERM = new ImageIcon(SearchAndDefinitionUI.class.getResource("/images/ontologyconfigurationtool/use_search_function.png"));
    private static final ImageIcon FIND_TERM_OVER = new ImageIcon(SearchAndDefinitionUI.class.getResource("/images/ontologyconfigurationtool/use_search_function_over.png"));
    private static final ImageIcon VIEW_META = new ImageIcon(SearchAndDefinitionUI.class.getResource("/images/ontologyconfigurationtool/view_term_definition.png"));
    private static final ImageIcon VIEW_META_OVER = new ImageIcon(SearchAndDefinitionUI.class.getResource("/images/ontologyconfigurationtool/view_term_definition_over.png"));
    private static final ImageIcon PLACEHOLDER = new ImageIcon(SearchAndDefinitionUI.class.getResource("/images/ontologyconfigurationtool/search_box_placeholder.gif"));

    private ViewTermDefinitionUI viewTerm;
    private SearchOntologyDialogUI searchOntologyDialogUI;

    private Ontology ontologyToQuery;

    private OntologyTreeCreator ontologyTreeCreator;
    private JTree ontologyTree;
    private OntologyService ontologyClient;

    private JPanel swappableContainer;

    public SearchAndDefinitionUI() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGUI();
            }
        });
    }

    public void createGUI() {
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
        setPreferredSize(new Dimension(240, 300));
        createPlaceholderPanel();
    }

    public void createPlaceholderPanel() {
        add(new JLabel(PLACEHOLDER), BorderLayout.CENTER);
    }

    public void createFunctionalView() {
        removeAll();
        setLayout(new BorderLayout());
        createMenuPanel();

        swappableContainer = new JPanel(new BorderLayout());
        swappableContainer.setBackground(UIHelper.BG_COLOR);

        createSearchOntologyPane();
        swappableContainer.add(searchOntologyDialogUI);
        add(swappableContainer, BorderLayout.CENTER);
    }

    public void createMenuPanel() {
        JPanel functionPanel = new JPanel();
        functionPanel.setLayout(new BoxLayout(functionPanel, BoxLayout.LINE_AXIS));
        functionPanel.setBackground(UIHelper.BG_COLOR);

        functionPanel.add(new JLabel(ADDITIONAL_FUNCTION_SIDE_LEFT));

        final JLabel findTerm = new JLabel(FIND_TERM);
        findTerm.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                findTerm.setIcon(FIND_TERM_OVER);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                findTerm.setIcon(FIND_TERM);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                findTerm.setIcon(FIND_TERM);
                if (searchOntologyDialogUI != null) {
                    setCurrentPage(searchOntologyDialogUI);
                } else {
                    createSearchOntologyPane();
                    setCurrentPage(searchOntologyDialogUI);
                }
            }
        });

        functionPanel.add(findTerm);

        final JLabel viewTermDefinition = new JLabel(VIEW_META);
        viewTermDefinition.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                viewTermDefinition.setIcon(VIEW_META_OVER);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                viewTermDefinition.setIcon(VIEW_META);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                viewTermDefinition.setIcon(VIEW_META);
                if (!ontologyTree.isSelectionEmpty()) {
                    Object treeObject = ((DefaultMutableTreeNode) ontologyTree.getSelectionPath().getLastPathComponent()).getUserObject();

                    if (viewTerm == null) {
                        createViewDefinitionPane();
                    }

                    if (treeObject instanceof OntologyBranch) {

                        setCurrentPage(viewTerm);
                        viewTerm.setContent((OntologyBranch) treeObject,
                                ontologyToQuery, ontologyClient);
                    }
                }
            }
        });

        functionPanel.add(viewTermDefinition);

        functionPanel.add(new JLabel(ADDITIONAL_FUNCTION_SIDE_RIGHT));

        add(functionPanel, BorderLayout.NORTH);
    }

    private void createViewDefinitionPane() {
        viewTerm = new ViewTermDefinitionUI();
    }

    private void createSearchOntologyPane() {
        searchOntologyDialogUI = new SearchOntologyDialogUI(ontologyToQuery, getOntologyClient());
        searchOntologyDialogUI.addPropertyChangeListener("termSelected", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String termAccession = propertyChangeEvent.getNewValue().toString();
                termAccession = termAccession.substring(termAccession.indexOf("(") + 1, termAccession.lastIndexOf(")"));
                ((WSOntologyTreeCreator) ontologyTreeCreator).expandTreeToReachTerm(termAccession);
            }
        });
    }

    public void setOntologyToQuery(Ontology ontologyToQuery) {
        this.ontologyToQuery = ontologyToQuery;
    }

    public void notifyOfSelection() {
        if (viewTerm != null) {
            if (viewTerm.isShowing()) {
                viewTerm.setContent((OntologyBranch) ((DefaultMutableTreeNode) ontologyTree.getSelectionPath().getLastPathComponent()).getUserObject(),
                        ontologyToQuery, getOntologyClient());
            }
        }
    }


    public void setOntologyTree(JTree ontologyTree) {
        this.ontologyTree = ontologyTree;
    }

    public void setOntologyTreeCreator(OntologyTreeCreator ontologyTreeCreator) {
        this.ontologyTreeCreator = ontologyTreeCreator;
    }

    public void setOntologyClient(OntologyService ontologyClient) {
        this.ontologyClient = ontologyClient;
    }

    public OntologyService getOntologyClient() {
        if (ontologyClient == null) {
            ontologyClient = ontologyTreeCreator instanceof WSOntologyTreeCreator ? new OLSClient() : new BioPortalClient();
        }
        return ontologyClient;
    }

    public void updateView() {
        createFunctionalView();
        SearchAndDefinitionUI.this.revalidate();
    }

    /**
     * Changes Container being shown in the swappableContainer panel
     *
     * @param newContainer - Container to change to
     */
    public void setCurrentPage(Container newContainer) {
        swappableContainer.removeAll();
        swappableContainer.add(newContainer);
        SearchAndDefinitionUI.this.revalidate();
        SearchAndDefinitionUI.this.repaint();
    }

}
