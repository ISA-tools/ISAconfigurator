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

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.apache.log4j.Logger;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.OntologyBranch;
import org.isatools.isacreator.configuration.OntologyFormats;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.ontologybrowsingutils.*;
import org.isatools.isacreator.ontologymanager.BioPortalClient;
import org.isatools.isacreator.ontologymanager.OntologyService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * OntologyBrowser contains the general environment for browsing ontologies. Depending on the type of the Ontology,
 * different mechanisms are used to generate ontology trees. For instance, for OBO files we use the OLS web service queries
 * to generate a view of the ontology and for OWL files, we download the files from BioPortal, reason over them and build the
 * JTree representation of the Ontology through recursive access of the subclasses.
 *
 */

public class OntologyBrowser extends JPanel implements TreeObserver, TreeSubject {

    private static Logger log = Logger.getLogger(OntologyBrowser.class.getName());

    private JTree ontologyTree;
    private OntologyBranch selectedTreePart;

    private JLabel selectedTreePartInfo;
    private Ontology ontologyToQuery;
    private OntologyTreeCreator ontologyTreeCreator;
    private List<TreeObserver> observers;
    private Dimension browserSize;

    /**
     * Creates a new Ontology Browser which will provide the interface to allow for selection of subtree elements
     * within an ontology!
     *
     * @param ontologyToQuery - the Ontology object representing the Ontology to be queried
     */
    public OntologyBrowser(Ontology ontologyToQuery, Dimension browserSize) {
        this.ontologyToQuery = ontologyToQuery;
        this.browserSize = browserSize;
        this.observers = new ArrayList<TreeObserver>();
        instantiate();
    }

    private void instantiate() {
        ontologyTree = new JTree();
        // add event change listener to detect when nodes are being selected in the tree. we can then update the definition in
        // the ViewTermDefinitionUI to show the currently selected term.

        ontologyTreeCreator = new WSOntologyTreeCreator(this, ontologyTree);

        ((WSOntologyTreeCreator) ontologyTreeCreator).registerObserver(this);

        this.selectedTreePart = ontologyToQuery.getSubsectionToQuery();
        createGUI();
        updateSelectedTreePartText(selectedTreePart);
    }


    private void createGUI() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        DefaultMutableTreeNode root;

        try {
            System.out.println("Querying... ");
            System.out.println(ontologyToQuery.getOntologyAbbreviation());
            System.out.println(ontologyToQuery.getOntologyID());

            System.out.println();
            ontologyToQuery.setOntologyVersion(ontologyToQuery.getOntologyAbbreviation());
            root = ontologyTreeCreator.createTree(Collections.singletonMap(ontologyToQuery.getOntologyAbbreviation(), new RecommendedOntology(ontologyToQuery, null)));
            ontologyTree.setModel(new DefaultTreeModel(root));
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        } catch (RuntimeException re) {
            log.error(re.getMessage());
        }

        ontologyTree.setCellRenderer(new OntologyTreeRenderer());
        ontologyTree.setShowsRootHandles(false);

        // remove standard icon used to indicate that node is open.
        BasicTreeUI ui = new BasicTreeUI() {
            public Icon getCollapsedIcon() {
                return null;
            }

            public Icon getExpandedIcon() {
                return null;
            }
        };

        ontologyTree.setUI(ui);
        ontologyTree.setFont(UIHelper.VER_11_PLAIN);

        ontologyTree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                if (ontologyTree.getSelectionCount() > 0) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) ontologyTree.getLastSelectedPathComponent();
                    if (!selectedNode.isLeaf()) {

                        System.out.println("Class: " + selectedNode.getUserObject().getClass());

                        if (selectedNode.getUserObject() instanceof OntologyTreeItem) {

                            OntologyTreeItem treeItem = (OntologyTreeItem) selectedNode.getUserObject();
                            selectedTreePart = treeItem.getBranch();

                            System.out.println("Setting subsection to query as " + selectedTreePart.getBranchName());
                            ontologyToQuery.setSubsectionToQuery(selectedTreePart);
                            updateSelectedTreePartText(selectedTreePart);
                        } else {
                            System.out.println("Object is not of type ontology branch");
                        }
                    } else {
                        updateSelectedTreePartText(null);
                    }
                }
            }
        });

        JScrollPane ontologyTreeScroller = new JScrollPane(ontologyTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        ontologyTreeScroller.getViewport().setOpaque(false);
        ontologyTreeScroller.setBorder(new EmptyBorder(0, 0, 0, 0));
        add(createInfoPanel(), BorderLayout.NORTH);
        add(ontologyTreeScroller, BorderLayout.CENTER);

        IAppWidgetFactory.makeIAppScrollPane(ontologyTreeScroller);

        JPanel selectedTreePartInfoCont = new JPanel(new GridLayout(1, 1));

        selectedTreePartInfo = UIHelper.createLabel("", UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, SwingConstants.LEFT);
        selectedTreePartInfoCont.add(selectedTreePartInfo);
        add(selectedTreePartInfoCont, BorderLayout.SOUTH);
    }

    private JPanel createInfoPanel() {

        JPanel labelContainer = new JPanel(new GridLayout(1, 1));
        labelContainer.setPreferredSize(new Dimension(275, 40));

        labelContainer.add(UIHelper.createLabel("<html>viewing version <strong>" + ontologyToQuery.getOntologyVersion() +
                "</strong> of <strong>" + ontologyToQuery.getOntologyDisplayLabel() +
                "</strong> <i>(" + ontologyToQuery.getOntologyAbbreviation() + ")</i></html>",
                UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR));

        return labelContainer;
    }

    private void updateSelectedTreePartText(OntologyBranch part) {
        if (part == null) {
            selectedTreePartInfo.setText("");
        } else {
            selectedTreePartInfo.setText("<html>all queries on this ontology will be restricted to terms below <strong><font color=\"#8DC63F\">" + part + "</font></strong></html>");
        }
        OntologyBrowser.this.repaint();
    }

    public OntologyBranch getSelectedTreePart() {
        return selectedTreePart;
    }

    public void notifyOfSelection() {
        notifyObservers();
    }

    public void notifyObservers() {
        for (TreeObserver observer : observers) {
            observer.notifyOfSelection();
        }
    }


    public void registerObserver(TreeObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    public void unregisterObserver(TreeObserver observer) {
        if (observer != null) {
            if (observers.contains(observer)) {
                observers.remove(observer);
            }
        }
    }

    public JTree getOntologyTree() {
        return ontologyTree;
    }

    public OntologyTreeCreator getOntologyTreeCreator() {
        return ontologyTreeCreator;
    }

    public Dimension getBrowserSize() {
        return browserSize;
    }

    public void setBrowserSize(Dimension browserSize) {
        this.browserSize = browserSize;
    }

    public Ontology getOntologyToQuery() {
        return ontologyToQuery;
    }
}
