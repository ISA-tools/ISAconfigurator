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

import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.OntologyBranch;
import org.isatools.isacreator.ontologymanager.BioPortalClient;
import org.isatools.isacreator.ontologymanager.OntologyQueryAdapter;
import org.isatools.isacreator.ontologymanager.OntologyService;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * OBOOntologyTreeCreator creates a representation of an OLS ontology
 * in a tree view through queries on OLSs web services.
 *
 * @author eamonnmaguire
 * @date Feb 4, 2010
 */


public class WSOntologyTreeCreator implements OntologyTreeCreator, TreeSelectionListener, TreeModelListener, TreeExpansionListener, TreeSubject {

    private List<TreeObserver> observers;

    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;
    private Ontology ontology;

    private OntologyBrowser browser;
    private OntologyService ontologyClient;
    private JTree tree;


    public WSOntologyTreeCreator(OntologyBrowser browser, OntologyService ontologyClient, JTree tree) {
        this.browser = browser;
        this.ontologyClient = ontologyClient;
        this.tree = tree;
        observers = new ArrayList<TreeObserver>();
    }

    public DefaultMutableTreeNode createTree(Ontology ontology) throws FileNotFoundException {
        this.ontology = ontology;

        rootNode = new DefaultMutableTreeNode(ontology);
        treeModel = new DefaultTreeModel(rootNode);
        treeModel.addTreeModelListener(this);

        tree.setModel(treeModel);
        tree.addTreeExpansionListener(this);
        tree.addTreeSelectionListener(this);

        if (ontology.getOntologyAbbreviation().equals("NEWT")) {
            rootNode.add(new DefaultMutableTreeNode("NEWT is not browseable"));
        } else {
            initiateOntologyVisualization();
        }
        return rootNode;
    }

    private void initiateOntologyVisualization() {
        browser.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        System.out.println("initialising ontology visualisation for " + ontology.getOntologyDisplayLabel());
        if (ontologyClient instanceof BioPortalClient) {
            System.out.println("Using Bioportal");
        } else {
            System.out.println("Using OLS");
        }
        System.out.println("Ontology version is: " + ontology.getOntologyVersion());

        Map<String, OntologyTerm> rootTerms = ontologyClient.getOntologyRoots(new OntologyQueryAdapter(ontology).getOntologyQueryString(OntologyQueryAdapter.GET_VERSION));
        System.out.println("found " + rootTerms.size() + " roots");
        // update the tree
        for (String termId : rootTerms.keySet()) {
            addTermToTree(null, rootTerms.get(termId));
        }

        // not root terms found
        if (rootTerms.size() == 0) {
            addTermToTree(new DefaultMutableTreeNode("Something has gone wrong on when loading from " + ((ontologyClient instanceof BioPortalClient) ? "BioPortal" : "Ontology lookup service")), null);
        }
        updateTree();
        browser.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }

    public void setTree(JTree tree) {
        this.tree = tree;
    }

    public void updateTree() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        TreePath path = new TreePath(root.getPath());
        tree.collapsePath(path);
        tree.expandPath(path);
    }

    /**
     * Method to preload the next level of the ontology so that we know which nodes are leaves and which are
     * nodes at any given level.
     *
     * @param termAccession - accession of term to load children for
     * @param parentTerm    - the parent node of the term being searched for.
     */
    private void preloadNextOntologyLevel(final String termAccession, final DefaultMutableTreeNode parentTerm) {
        browser.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        Map<String, OntologyTerm> termChildren = ontologyClient.getTermChildren(termAccession, new OntologyQueryAdapter(ontology).getOntologyQueryString(OntologyQueryAdapter.GET_VERSION));

        // add the level of non visible nodes

        for (String accession : termChildren.keySet()) {
            addTermToTree(parentTerm, termChildren.get(accession));
        }


        browser.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }


    private void addTermToTree(DefaultMutableTreeNode parent, OntologyTerm term) {
        DefaultMutableTreeNode childNode =
                new DefaultMutableTreeNode(new OntologyBranch(term.getOntologySourceAccession(), term.getOntologyTermName()));

        if (parent == null) {
            parent = rootNode;
        }
        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());
    }


    public void expandTreeToReachTerm(String termAccession) {
        Enumeration treeVisitor = rootNode.breadthFirstEnumeration();
        Map<String, OntologyTerm> nodeParentsFromRoot = ontologyClient.getAllTermParents(termAccession, new OntologyQueryAdapter(ontology).getOntologyQueryString(OntologyQueryAdapter.GET_ID));
        TreePath lastPath = null;

        for (OntologyTerm node : nodeParentsFromRoot.values()) {
            while (treeVisitor.hasMoreElements()) {
                DefaultMutableTreeNode visitingNode = (DefaultMutableTreeNode) treeVisitor.nextElement();
                if (visitingNode.getUserObject() instanceof OntologyBranch) {
                    OntologyBranch termNode = (OntologyBranch) visitingNode.getUserObject();

                    if (termNode.getBranchName().toLowerCase().equalsIgnoreCase(node.getOntologyTermName()) || termNode.getBranchIdentifier().equalsIgnoreCase(node.getOntologySourceAccession())) {
                        TreePath pathToNode = new TreePath(visitingNode.getPath());
                        tree.expandPath(pathToNode);
                        tree.setSelectionPath(pathToNode);
                        lastPath = pathToNode;
                        break;
                    }
                }
            }
        }

        if (lastPath != null) {

            tree.scrollPathToVisible(lastPath);
            tree.repaint();
        }
    }

    public void treeExpanded(TreeExpansionEvent treeExpansionEvent) {
        // get selected node
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeExpansionEvent.getPath().getLastPathComponent();

        if (node == null) {
            return;
        }

        // add next level of nodes if not already added
        final Enumeration<DefaultMutableTreeNode> treeNodeEnumeration = node.children();

        while (treeNodeEnumeration.hasMoreElements()) {
            DefaultMutableTreeNode treeNode = treeNodeEnumeration.nextElement();

            if (treeNode.getUserObject() instanceof OntologyBranch) {
                if (treeNode.getChildCount() == 0) {
                    OntologyBranch currentTerm = (OntologyBranch) treeNode.getUserObject();
                    preloadNextOntologyLevel(currentTerm.getBranchIdentifier(), treeNode);
                }
            }
        }
    }

    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        // get selected node

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeSelectionEvent.getPath().getLastPathComponent();

        if (node == null) {
            return;
        }

        // get node data object
        if (node.getUserObject() instanceof OntologyBranch) {
            OntologyBranch ontologyTerm = (OntologyBranch) node.getUserObject();

            // tell the observers that an item has been selected.
            notifyObservers();
            // load the children and the meta data, unless the term is the 'no roots defined' dummy term
            if (ontologyTerm.getBranchIdentifier() != null && !ontologyTerm.getBranchIdentifier().equalsIgnoreCase("No Root Terms Defined!")) {

                // load children only for leaf nodes and those that have not been marked as processed
                if (node.isLeaf() && node.getAllowsChildren()) {

                    // load children. if no children, set allowsChildren to false
                    Map<String, OntologyTerm> termChildren = ontologyClient.getTermChildren(ontologyTerm.getBranchIdentifier(), new OntologyQueryAdapter(ontology).getOntologyQueryString(OntologyQueryAdapter.GET_VERSION));
                    if (termChildren.size() > 0) {
                        node.setAllowsChildren(true);
                    }
                }
                // olsDialog.loadMetaData(nodeInfo.getTermId(), OLSDialog.OLS_DIALOG_BROWSE_ONTOLOGY);
            } else {
                rootNode.removeAllChildren();
                treeModel.reload();
            }
        }

    }

    public void treeNodesChanged(TreeModelEvent treeModelEvent) {
    }

    public void treeNodesInserted(TreeModelEvent treeModelEvent) {
    }

    public void treeNodesRemoved(TreeModelEvent treeModelEvent) {
    }

    public void treeStructureChanged(TreeModelEvent treeModelEvent) {
    }

    public void treeCollapsed(TreeExpansionEvent treeExpansionEvent) {
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
}
