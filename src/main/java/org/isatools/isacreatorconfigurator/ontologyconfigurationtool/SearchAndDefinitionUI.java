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

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.OntologyBranch;
import org.isatools.isacreator.ontologybrowsingutils.OntologyTreeCreator;
import org.isatools.isacreator.ontologybrowsingutils.TreeObserver;
import org.isatools.isacreator.ontologybrowsingutils.WSOntologyTreeCreator;
import org.isatools.isacreator.ontologymanager.BioPortalClient;
import org.isatools.isacreator.ontologymanager.OLSClient;
import org.isatools.isacreator.ontologymanager.OntologyService;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

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

    @InjectedResource
    private ImageIcon findTermIcon, findTermIconOver, viewMetadataIcon, viewMetadataIconOver, placeholder;

    private static final int SEARCH = 0;
    private static final int DEFINITION = 1;

    private int mode = SEARCH;

    private JLabel viewTermDefinition, findTerm;

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
        ResourceInjector.get("ontologyconfigtool-package.style").inject(this);

        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
        setPreferredSize(new Dimension(240, 300));
        createPlaceholderPanel();
    }

    public void createPlaceholderPanel() {
        add(new JLabel(placeholder), BorderLayout.CENTER);
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

        findTerm = new JLabel(findTermIcon);
        findTerm.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                findTerm.setIcon(findTermIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                findTerm.setIcon(mode == SEARCH ? findTermIconOver : findTermIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

                resetButtons();
                mode = SEARCH;
                findTerm.setIcon(findTermIconOver);

                if (searchOntologyDialogUI != null) {
                    setCurrentPage(searchOntologyDialogUI);
                } else {
                    createSearchOntologyPane();
                    setCurrentPage(searchOntologyDialogUI);
                }
            }
        });

        functionPanel.add(findTerm);

        viewTermDefinition = new JLabel(viewMetadataIcon);
        viewTermDefinition.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                viewTermDefinition.setIcon(viewMetadataIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                viewTermDefinition.setIcon(mode == DEFINITION ? viewMetadataIconOver : viewMetadataIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {


                if (!ontologyTree.isSelectionEmpty()) {

                    Object treeObject = ((DefaultMutableTreeNode) ontologyTree.getSelectionPath().getLastPathComponent()).getUserObject();

                    if (viewTerm == null) {
                        createViewDefinitionPane();
                    }

                    if (treeObject instanceof OntologyBranch) {
                        resetButtons();
                        mode = DEFINITION;
                        viewTermDefinition.setIcon(viewMetadataIconOver);

                        setCurrentPage(viewTerm);
                        viewTerm.setContent((OntologyBranch) treeObject,
                                ontologyToQuery, ontologyClient);
                    }
                }
            }
        });

        functionPanel.add(viewTermDefinition);

        add(functionPanel, BorderLayout.NORTH);
    }

    private void resetButtons() {
        findTerm.setIcon(findTermIcon);
        viewTermDefinition.setIcon(viewMetadataIcon);
    }


    private void createViewDefinitionPane() {
        viewTerm = new ViewTermDefinitionUI();
    }

    private void createSearchOntologyPane() {
        searchOntologyDialogUI = new SearchOntologyDialogUI(ontologyToQuery, getOntologyClient());
        searchOntologyDialogUI.addPropertyChangeListener("termSelected", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                if (propertyChangeEvent.getNewValue() instanceof OntologyTerm) {
                    OntologyTerm term = (OntologyTerm) propertyChangeEvent.getNewValue();
                    ((WSOntologyTreeCreator) ontologyTreeCreator).expandTreeToReachTerm(term);
                }
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
