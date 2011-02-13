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


import org.isatools.isacreatorconfigurator.configdefinition.Ontology;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.FileNotFoundException;

/**
 * Creates a JTree representation of OWL ontologies
 *
 * @author eamonnmaguire
 * @date Jul 25, 2009
 */


public class OWLOntologyTreeCreator implements OntologyTreeCreator {
//	private static final Logger log = Logger.getLogger(OWLOntologyTreeCreator.class.getName());
//	OWLReasoner reasoner;
//
//	public OWLOntologyTreeCreator() {
//	}
//
//	public DefaultMutableTreeNode createTree(Ontology ontology) throws FileNotFoundException {
//
//		// load the model to the reasoner
//		File toImport = null;
//		try {
//			DefaultMutableTreeNode tree = Utils.loadOntologyTree(ontology.getOntologyVersion());
//			if (tree != null) {
//				return tree;
//			} else {
//				reasoner = new OWLReasoner();
//
//				toImport = new File(BioPortalClient.DOWNLOAD_ONTOLOGY_LOC + ontology.getOntologyVersion() + ".owl");
//
//				reasoner.load("file://" + toImport.getAbsolutePath(), false);
//				Set<Resource> set = reasoner.getEquivalentClasses(OWL.Thing);
//				// getEquivalentClasses method does not include the OWL.Thing class so have to add it manually
//				set.add(OWL.Thing);
//				// create a tree starting with owl:Thing node as the root
//				DefaultMutableTreeNode newTree = createTreeBranch(set);
//				// save the tree, so that it can be loaded again!
//				Utils.saveOntologyTree(ontology.getOntologyVersion(), newTree);
//
//				// delete ontology file afterwards since it is not required!
//				if (toImport.exists()) {
//					toImport.delete();
//				}
//
//				return newTree;
//			}
//		} catch (IOException e) {
//			log.error("io exception occurred : " + e.getMessage());
//		} catch (ClassNotFoundException e) {
//			log.error("class not found exception occurred : " + e.getMessage());
//		} finally {
//			if (toImport != null) {
//				if (toImport.exists()) {
//					toImport.delete();
//				}
//			}
//		}
//
//		return null;
//	}
//
//	private DefaultMutableTreeNode createTreeBranch(Set<Resource> concepts) {
//		// don't want to add nothing to the list :o)
//		if (concepts.contains(OWL.Nothing)) {
//			return null;
//		}
//		// create a node from the concept (we use extractFirstResource to get label we want to display)
//		DefaultMutableTreeNode root = new DefaultMutableTreeNode(extractFirstResource(concepts));
//
//		if (concepts.isEmpty()) {
//			return root;
//		}
//		Resource c = concepts.iterator().next();
//		// we only get direct subclasses
//		Set<Set<Resource>> subs = reasoner.getSubClasses(c, true);
//		subs.addAll(reasoner.getInstances(c));
//		// result is a set of sets. equivalent concepts are returned inside one set
//		for (Set<Resource> subSet : subs) {
//			DefaultMutableTreeNode node = createTreeBranch(subSet);
//			// if set contains owl:Nothing tree will be null
//			if (node != null) {
//				root.add(node);
//			}
//		}
//		return root;
//	}
//
//	private OntologyBranch extractFirstResource(Set<Resource> concepts) {
//		Stack<Resource> resources = new Stack<Resource>();
//		resources.addAll(concepts);
//
//		Resource r = resources.pop();
//
//		if (r.getProperty(RDFS.label) != null) {
//			return new OntologyBranch(r.getLocalName(), r.getProperty(RDFS.label).getString());
//		} else {
//			return new OntologyBranch(r.getLocalName(), null);
//		}
//	}
//
//	public static void main(String[] args) {
//		OWLOntologyTreeCreator otc = new OWLOntologyTreeCreator();
//		try {
//			otc.createTree(new Ontology("1013", "chebi_v1.62", "CHEBI", "Chemicals of Biological Interest"));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//		}
//	}

    public DefaultMutableTreeNode createTree(Ontology ontology) throws FileNotFoundException {
        return null;
    }
}
