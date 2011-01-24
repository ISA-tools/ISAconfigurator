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

package org.isatools.isacreatorconfigurator.ontologymanager.bioportal.xmlresulthandlers;

import bioontology.bioportal.classBean.schema.*;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.isatools.isacreatorconfigurator.ontologymanager.bioportal.model.BioPortalOntology;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * BioPortalClassBeanResultHandler
 *
 * @author eamonnmaguire
 * @date Feb 18, 2010
 */


public class BioPortalClassBeanResultHandler {

    public SuccessDocument getDocument(String fileLocation) {
        SuccessDocument resultDocument = null;
        try {
            resultDocument = SuccessDocument.Factory.parse(new File(fileLocation));
        } catch (org.apache.xmlbeans.XmlException e) {
            System.err.println("XML Exception encountered: " + e.getMessage());
        } catch (java.io.IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
        }

        return resultDocument;
    }


    public BioPortalOntology parseMetadataFile(String fileLocation) {
        SuccessDocument resultDocument = getDocument(fileLocation);
        ClassBeanDocument.ClassBean classBean = resultDocument.getSuccess().getData().getClassBean();

        BioPortalOntology ontology = createOntologyFromClassBean(classBean);

        for (RelationsDocument.Relations relation : classBean.getRelationsArray()) {
            for (EntryDocument.Entry entry : relation.getEntryArray()) {
                if (entry.getStringArray().length > 0) {
                    String entryType = entry.getStringArray(0);
                    if (entry.getListArray().length > 0) {
                        for (ListDocument.List listItem : entry.getListArray()) {
                            for (String item : listItem.getStringArray()) {
                                ontology.addToComments(entryType, item);
                            }
                        }
                    }

                }
            }
        }

        return ontology;
    }

    public Map<String, BioPortalOntology> parseRootConceptFile(String fileLocation) {
        SuccessDocument resultDocument = getDocument(fileLocation);

        Map<String, BioPortalOntology> result = new HashMap<String, BioPortalOntology>();

        if (resultDocument == null) {
            return result;
        }

        ClassBeanDocument.ClassBean upperLevelClass = resultDocument.getSuccess().getData().getClassBean();

        for (RelationsDocument.Relations relation : upperLevelClass.getRelationsArray()) {
            for (EntryDocument.Entry entry : relation.getEntryArray()) {
                if (entry.getStringArray().length > 0) {
                    String entryType = entry.getStringArray(0);

                    if (entryType.equalsIgnoreCase("subclass")) {
                        if (entry.getListArray().length > 0) {
                            for (ListDocument.List listItem : entry.getListArray()) {
                                for (ClassBeanDocument.ClassBean classBeanItem : listItem.getClassBeanArray()) {
                                    result.put(classBeanItem.getIdArray(0), createOntologyFromClassBean(classBeanItem));
                                }
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    public Map<String, String> parseOntologyParentPathFile(String fileLocation) {
        SuccessDocument resultDocument = getDocument(fileLocation);

        Map<String, String> result = new ListOrderedMap<String, String>();

        if (resultDocument == null) {
            return result;
        }

        ClassBeanDocument.ClassBean[] definedClasses = resultDocument.getSuccess().getData().getList().getClassBeanArray();

        for (ClassBeanDocument.ClassBean upperLevelClass : definedClasses) {
            for (RelationsDocument.Relations relation : upperLevelClass.getRelationsArray()) {
                for (EntryDocument.Entry entry : relation.getEntryArray()) {
                    if (entry.getStringArray().length > 0) {
                        String entryType = entry.getStringArray(0);
                        if (entryType.equalsIgnoreCase("path")) {
                            String path = entry.getStringArray(1);

                            String[] paths = path.split("\\.");
                            for (String p : paths) {
                                BioPortalOntology ontology = new BioPortalOntology();
                                ontology.setOntologySourceAccession(p);
                                result.put(p, p);
                            }
                        }
                    }
                }
            }
        }

        System.out.println("path to resource will be: ");
        for (String accession : result.keySet()) {
            System.out.println(accession);
        }

        return result;
    }

    public Map<String, BioPortalOntology> parseParentOrChildrenConceptFile(String fileLocation) {
        SuccessDocument resultDocument = getDocument(fileLocation);


        Map<String, BioPortalOntology> result = new HashMap<String, BioPortalOntology>();

        if (!proceedWithProcessing(resultDocument)) {
            return result;
        }

        ClassBeanDocument.ClassBean[] classes = resultDocument.getSuccess().getData().getList().getClassBeanArray();
        for (ClassBeanDocument.ClassBean currentClass : classes) {
            result.put(currentClass.getIdArray(0), createOntologyFromClassBean(currentClass));
        }

        return result;
    }

    public BioPortalOntology createOntologyFromClassBean(ClassBeanDocument.ClassBean classToConvert) {
        BioPortalOntology ontology = new BioPortalOntology();

        if (classToConvert.getIdArray().length > 0) {
            ontology.setOntologySourceAccession(classToConvert.getIdArray(0));
        }
        if (classToConvert.getLabelArray().length > 0) {
            ontology.setOntologyTermName(classToConvert.getLabelArray(0));
        }
        if (classToConvert.getFullIdArray().length > 0) {
            ontology.setOntologyPurl(classToConvert.getFullIdArray(0));
        }

        return ontology;
    }

    private boolean proceedWithProcessing(SuccessDocument successDoc) {
        return successDoc != null &&
                successDoc.getSuccess() != null &&
                successDoc.getSuccess().getData() != null;

    }


}
