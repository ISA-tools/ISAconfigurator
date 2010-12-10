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

package org.isatools.isacreatorconfigurator.ontologyselectiontool;

import org.isatools.isacreatorconfigurator.common.UIHelper;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * OntologyResultTree provides filtering functionality on a JTree
 *
 * @author Eamonn Maguire
 * @date Mar 2, 2010
 */


public class OntologyResultTree extends JTree {
    private FilterField filterField;

    public OntologyResultTree(TreeNode rootNode) {
        super();
        setBackground(UIHelper.BG_COLOR);
        setModel(new TreeFilterModel<String, Set<String>>(rootNode));
        filterField = new FilterField();

        filterField.addPropertyChangeListener("filterEvent", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                fireUpdateToListeners();
            }
        });
    }

    private void fireUpdateToListeners() {
        firePropertyChange("update", "", "none");
    }

    public void addItem(String key, String value) {
        ((TreeFilterModel<String, String>) getModel()).addElement(key, value);
    }

    public void setItems(Map<String, Set<String>> values) {
        ((TreeFilterModel<String, String>) getModel()).setContents(values);
    }

    public void clearItems() {
        ((TreeFilterModel) getModel()).clearItems();
    }

    public FilterField getFilterField() {
        return filterField;
    }

    private Set<String> getExpandedTreePaths() {
        Set<String> expandedPaths = new HashSet<String>();

        TreeNode rootNode = (TreeNode) getModel().getRoot();

        Enumeration children = rootNode.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) children.nextElement();
            TreePath path = new TreePath(childNode.getPath());

            if (isExpanded(path)) {
                expandedPaths.add(childNode.toString());
            }
        }

        return expandedPaths;
    }

    private void showExpandedKeys(Set<String> expandedKeys) {
        TreeNode rootNode = (TreeNode) getModel().getRoot();
        Enumeration children = rootNode.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) children.nextElement();
            if (expandedKeys.contains(childNode.toString())) {
                expandPath(new TreePath(childNode.getPath()));
            }
        }
    }


    /**
     * The FilterFields which implements the DocumentListener class. Calls updates on the JList as and
     * when modifications occur in the textfield as a result of user insertion, deletion, or update.
     */
    class FilterField extends JTextField implements DocumentListener {
        public FilterField() {
            super();
            getDocument().addDocumentListener(this);
        }

        public void changedUpdate(DocumentEvent event) {
            ((TreeFilterModel) getModel()).refilter();
            firePropertyChange("filterEvent", "", "uyt");
        }

        public void insertUpdate(DocumentEvent event) {
            ((TreeFilterModel) getModel()).refilterOnFilteredList();
            firePropertyChange("filterEvent", "", "sdf");
        }

        public void removeUpdate(DocumentEvent event) {
            ((TreeFilterModel) getModel()).refilter();

            firePropertyChange("filterEvent", "", "hgf");
        }
    }

    /**
     * The ListFilterModel provides the logic to filter the list given the values entered in FilterField
     * textfield.
     */
    class TreeFilterModel<T, V> extends DefaultTreeModel {
        Map<T, Set<V>> filterItems;
        Map<T, Set<V>> items;

        public TreeFilterModel(TreeNode treeNode) {
            super(treeNode);
            items = new HashMap<T, Set<V>>();
            filterItems = new HashMap<T, Set<V>>();
        }

        /**
         * Add an element to the Map of items, and then refilter the list in case the new item is being shown when it
         * shouldn't be.
         *
         * @param key  - where the item should be added
         * @param item - the item to be added
         */
        public void addElement(T key, V item) {
            if (!items.containsKey(key)) {
                items.put(key, new HashSet<V>());
            }
            items.get(key).add(item);
            refilter();
        }

        public void removeElement(T key) {
            items.remove(key);
            refilter();
        }

        public void setContents(Map<T, Set<V>> contents) {
            clearItems();
            items.putAll(contents);
            refilter();
            rebuildTree();
        }

        public Map<T, Set<V>> getFilterItems() {
            return filterItems;
        }

        public Map<T, Set<V>> getItems() {
            return items;
        }

        /**
         * Clear the items in the list
         */
        public void clearItems() {
            items.clear();
        }

        /**
         * Get the size of the filterItems list.
         *
         * @return Integer - the size of the list.
         */
        public int getSize() {
            return filterItems.size();
        }

        public Object getElementAt(int i) {
            return null;
        }

        /**
         * Refilter method clears the previously filtered items and then using the value typed into
         * the FilterField JTextField, items which contain the value typed into the field are added to
         * the filterItems list of terms.
         */
        public void refilter() {
            filterItems.clear();

            String term = getFilterField().getText().toLowerCase();

            for (T key : items.keySet()) {

                for (V value : items.get(key)) {
                    if (value.toString().toLowerCase().contains(term)) {
                        if (!filterItems.containsKey(key)) {
                            filterItems.put(key, new HashSet<V>());
                        }
                        filterItems.get(key).add(value);
                    }
                }
                clearSelection();
            }
            rebuildTree();

        }

        /**
         * Slight performance enhancement is instead of performing a complete refilter everytime, perform
         * a filter on the filtered items, whose size is inevitable going to be less, but at worse equal to the
         * the already filtered list.
         */
        public void refilterOnFilteredList() {
            String term = getFilterField().getText().toLowerCase();
            Map<T, List<V>> toRemove = new HashMap<T, List<V>>();

            for (T key : filterItems.keySet()) {
                for (V value : items.get(key)) {
                    if (!value.toString().toLowerCase().contains(term)) {
                        if (!toRemove.containsKey(key)) {
                            toRemove.put(key, new ArrayList<V>());
                        }
                        toRemove.get(key).add(value);
                    }
                }

                // items are removed after. otherwise there is concurrent access on the ArrayList, which is
                // not Thread safe.
                for (T toRemoveKey : toRemove.keySet()) {
                    for (V value : toRemove.get(toRemoveKey)) {
                        filterItems.get(toRemoveKey).remove(value);
                    }
                }
                clearSelection();
            }
            rebuildTree();
        }

        private void rebuildTree() {
            Set<String> expandedPaths = getExpandedTreePaths();

            DefaultMutableTreeNode root = new DefaultMutableTreeNode(countTerms() + " terms from " + countUsefulOntologies() + " ontologies");
            for (T key : filterItems.keySet()) {
                if (filterItems.get(key).size() > 0) {
                    DefaultMutableTreeNode nodeForKey = new DefaultMutableTreeNode(key);
                    for (V value : filterItems.get(key)) {
                        nodeForKey.add(new DefaultMutableTreeNode(value));
                    }
                    root.add(nodeForKey);
                }
            }
            setRoot(root);
            showExpandedKeys(expandedPaths);
        }

        private int countTerms() {
            int termCount = 0;

            for (T key : filterItems.keySet()) {
                termCount += filterItems.get(key).size();
            }
            return termCount;
        }

        private int countUsefulOntologies() {
            int ontologyCount = 0;

            for (T key : filterItems.keySet()) {
                if (filterItems.get(key).size() > 0) {
                    ontologyCount++;
                }
            }
            return ontologyCount;
        }

    }
}
