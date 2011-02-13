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

package org.isatools.isacreatorconfigurator.configui;

import org.apache.commons.collections15.map.ListOrderedMap;
import org.isatools.isacreatorconfigurator.configdefinition.DataTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * fieldOrderChecker checks for the following:
 * -> Parameter Values appear after a Protocol REF
 * -> Unit appears after Characteristic, Factor or Parameter
 * -> If there is a Characteristic, Factor or Parameter with a Unit, the Unit must be an Ontology term and the
 * Characteristic, Factor or Parameter must not!
 */

public class FieldOrderChecker {

    private StringBuilder report;
    private String assayName;

    public FieldOrderChecker() {
        report = new StringBuilder();
    }

    public boolean haveFieldsIncorrectOrder(List<Display> fieldsToCheck, String assayName) {
        this.assayName = assayName;

        Map<Integer, Boolean> fieldsAndOntology = new ListOrderedMap<Integer, Boolean>();
        List<String> fieldNames = new ArrayList<String>();

        int count = 0;
        for (Display ed : fieldsToCheck) {
            if (ed instanceof FieldElement) {

                FieldElement fed = (FieldElement) ed;
                fieldsAndOntology.put(count, fed.getFieldDetails().getDatatype() == DataTypes.ONTOLOGY_TERM);
                fieldNames.add(fed.getFieldDetails().getFieldName());
                count++;
            }
        }

        return hasRogueColumns(fieldsAndOntology, fieldNames) || checkFieldSemantics(fieldsAndOntology, fieldNames);

    }


    /**
     * Method checks to ensure that Parameters are assigned to a Protocol REF and that Units are assigned to either a
     * Characteristic, Factor or Parameter.
     *
     * @param fieldsAndOntology - Map containing the fields to check and whether or not the said field is an ontology term.
     * @param fieldNames        - List of Field names in the table definition.
     * @return false if there no columns in the wrong position and true if everything is ok!
     */
    private boolean hasRogueColumns(Map<Integer, Boolean> fieldsAndOntology, List<String> fieldNames) {

        String previousField = "";
        for (String field : fieldNames) {
            if (field.contains("Parameter Value") || field.equals("Unit")) {
                if (previousField.equals("")) {
                    report.append("<p>").append(field).append(" appears at the start of the table definition. This is completely wrong!");
                    return true;
                } else if (!previousField.equals("Protocol REF") && !previousField.contains("Characteristics[") && !previousField.contains("Factor Value[") && !previousField.contains("Parameter Value[")) {
                    report.append("<p>").append(field).append(" is in a position where it shouldn't be!");
                    return true;
                }
            }
            previousField = field;
        }
        return false;
    }

    /**
     * By this point, we assume that fields are in the correct order since the previous check will ensure that there are
     * no Units or Parameters in the wrong place!
     *
     * @param fieldsToCheck - Map containing the fields to check and whether or not the said field is an ontology term.
     * @param fieldNames    - List of Field names in the table definition.
     * @return true if ontology order is fine, false otherwise.
     */

    private boolean checkFieldSemantics(Map<Integer, Boolean> fieldsToCheck, List<String> fieldNames) {


        for (Integer i : fieldsToCheck.keySet()) {
            String fieldName = fieldNames.get(i);

            if (fieldName.contains("Characteristics[") || fieldName.contains("Factor Value[") || fieldName.contains("Parameter Value[")) {
                if (i + 1 < fieldNames.size()) {
                    if (fieldNames.get(i + 1).equals("Unit")) {
                        if (fieldsToCheck.get(i) && fieldsToCheck.get(i + 1)) {
                            report.append("<p>both <strong>").append(fieldName).append("</strong> and it's corresponding <strong>Unit</strong> cannot be an <i>Ontology term</i>.</p><p>Only the <strong>Unit</strong> should be an <i>Ontology term</i> in this instance!</p>");
                            return true;
                        } else if (!fieldsToCheck.get(i) && fieldsToCheck.get(i + 1)) {
                            // it's ok
                        } else {
                            report.append("<p>Where you have a <strong>").append(fieldName).append("</strong> and a <strong>Unit</strong>,</p><p>the <strong>Unit</strong> must be an <i>Ontology term</i> and <strong>").append(fieldName).append("</strong> must not!</p>");

                            return true;
                        }
                    }
                }
            }
            if (fieldName.equals("Protocol REF")) {
                if (fieldsToCheck.get(i)) {
                    report.append("<p>the <strong>Protocol REF</strong> defined in position ").append(i + 1).append(" must not be an <i>Ontology term!</i></p>");
                    return true;
                }
            }
        }

        return false;

    }

    public String getReport() {
        return "<html><p>in table <strong>" + assayName + "</strong></p>" + report.toString() + "</html>";
    }

    public static void main(String[] args) {
        FieldOrderChecker foc = new FieldOrderChecker();
        Map<Integer, Boolean> fieldsToCheck = new ListOrderedMap<Integer, Boolean>();
        List<String> fieldNames = new ArrayList<String>();

        fieldsToCheck.put(0, false);
        fieldNames.add("Sample Name");
        fieldsToCheck.put(1, true);
        fieldNames.add("Protocol REF");
        fieldsToCheck.put(2, false);
        fieldNames.add("Source Name");
        fieldsToCheck.put(3, true);
        fieldNames.add("Characteristics[diet]");
        fieldsToCheck.put(4, false);
        fieldNames.add("Unit");
        fieldsToCheck.put(5, false);
        fieldNames.add("Factor Value[dose");
        fieldsToCheck.put(6, true);
        fieldNames.add("Unit");

        foc.checkFieldSemantics(fieldsToCheck, fieldNames);
        foc.hasRogueColumns(fieldsToCheck, fieldNames);

    }


}
