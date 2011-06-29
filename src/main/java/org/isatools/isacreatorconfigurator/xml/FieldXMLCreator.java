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

package org.isatools.isacreatorconfigurator.xml;


import org.isatools.isacreator.configuration.*;
import org.isatools.isacreatorconfigurator.configui.DataNotCompleteException;
import org.isatools.isacreatorconfigurator.configui.Display;
import org.isatools.isacreatorconfigurator.configui.FieldElement;
import org.isatools.isacreatorconfigurator.configui.SectionDisplay;

import java.util.List;

/**
 * @author eamonnmaguire
 * @date Aug 26, 2009
 */

public class FieldXMLCreator {
    private MappingObject mo;
    private List<Display> fields;


    public FieldXMLCreator(List<Display> fields, MappingObject mo) {
        this.mo = mo;
        this.fields = fields;
    }

    public StringBuffer createTableXML() throws DataNotCompleteException {
        StringBuffer entireTableDef = new StringBuffer();

        entireTableDef.append("<isatab-config-file xmlns=\"http://www.ebi.ac.uk/bii/isatab_configuration#\">");
        entireTableDef.append("<isatab-configuration table-name=\"").append(mo.getAssayName()).append("\"");


        String finalMeasurement = mo.getTableType().equalsIgnoreCase("assay") ? mo.getMeasurementEndpointType() : mo.getTableType().equalsIgnoreCase("study sample") ? "[Sample]" : "[investigation]";
        String finalTechnology = mo.getTableType().equalsIgnoreCase("assay") ? mo.getTechnologyType() : "";

        if (!finalMeasurement.equals("[Sample]")) {
            entireTableDef.append(" isatab-assay-type=\"").append(mo.getAssayType()).append("\" isatab-conversion-target=\"").append(mo.getDispatchTarget()).append("\"");
        }

        entireTableDef.append(">");

        createOntologyTypeDefinition(entireTableDef, "measurement", finalMeasurement, mo.getMeasurementAccession(), mo.getMeasurementSource());
        createOntologyTypeDefinition(entireTableDef, "technology", finalTechnology, mo.getTechnologyAccession(), mo.getTechnologySource());

        // create section defining general attributes of a field.
        String currentSection = null;

        for (Display fo : fields) {
            if (fo instanceof FieldElement) {
                FieldElement fed = (FieldElement) fo;
                FieldObject fd = fed.getFieldDetails();

                if(currentSection != null) {
                    fd.setSection(currentSection);
                }

                if (fo.toString().equalsIgnoreCase("unit")) {
                    entireTableDef.append(createUnitFieldXML(fd));
                } else if (fo.toString().equalsIgnoreCase("protocol ref")) {
                    entireTableDef.append(createProtocolFieldXML(fd));
                } else {
                    entireTableDef.append(createStandardXMLForField(fd));
                }

            } else if (fo instanceof SectionDisplay) {
                currentSection = fo.toString();
            } else {
                entireTableDef.append("<structured-field name=\"").append(fo.toString()).append("\"/>");
            }
        }

        entireTableDef.append("</isatab-configuration>");
        entireTableDef.append("</isatab-config-file>");
        return entireTableDef;
    }

    /**
     * Purpose is to output the values supplied in the values parameter. This should always contain 4 elements. Name of the
     * XML tag to be output, the term label, term accession and the ontology source reference.
     *
     * @param output Output buffer to write to.
     * @param values - XML tag to be output, the term label, term accession and the ontology source reference.
     */
    private void createOntologyTypeDefinition(StringBuffer output, String... values) {
        // some assignments made to ensure compatibility with marcos loader code...this is onyl for definition of an
        // assay which is a study sample file definition!
        output.append("<").append(values[0]).append(" term-label=\"").append(values[1]).append("\"")
                .append(" term-accession=\"").append(values[2]).append("\"")
                .append(" source-abbreviation=\"").append(values[3]).append("\"/>");
    }

    private StringBuffer createStandardXMLForField(FieldObject field) {
        StringBuffer xmlRep = new StringBuffer();

        // output initial section displaying field name and type of field in format <field header = "Sample Name" data-type = "string">
        xmlRep.append("<field header=\"").append(field.getFieldName()).
                append("\" data-type=\"").append(field.getDatatype()).
                append("\" is-file-field=\"").append(String.valueOf(field.isAcceptsFileLocations())).
                append("\" is-multiple-value=\"").append(String.valueOf(field.isAcceptsMultipleValues())).
                append("\" is-required=\"").append(String.valueOf(field.isRequired())).
                append("\" is-hidden=\"").append(String.valueOf(field.isHidden()));

        if(field.getSection() != null && !field.getSection().equals("")) {
            xmlRep.append("\" section=\"").append(String.valueOf(field.getSection())).append("\">");
        } else {
            xmlRep.append("\">");
        }

        // output field description
        xmlRep.append("<description>").append("<![CDATA[").append(StringUtils.cleanUpString(field.getDescription())).append("]]>").append("</description>");

        // output fields default value
        String defaultValue = field.getDefaultVal() == null ? "" : field.getDefaultVal();
        xmlRep.append("<default-value>").append("<![CDATA[").append(defaultValue).append("]]>").append("</default-value>");

        // output the regex formatting for the field...default is .*
        if (field.isInputFormatted()) {
            if (field.getInputFormat() != null && !field.getInputFormat().trim().equals("")) {
                xmlRep.append("<value-format>").append(field.getInputFormat()).append("</value-format>");
            }
        }

        if (field.getWizardTemplate() != null && !field.getWizardTemplate().trim().equals("")) {
            xmlRep.append("<generated-value-template>").append(field.getWizardTemplate()).append("</generated-value-template>");
        }

        if (field.getDatatype() == DataTypes.LIST) {
            if (field.getFieldList() != null) {

                String toAdd = "";
                String[] fieldList = field.getFieldList();
                for (int i = 0; i < field.getFieldList().length; i++) {
                    String s = field.getFieldList()[i];
                    toAdd += s.trim();

                    if (i < fieldList.length - 1) {
                        toAdd += ",";
                    }
                }

                xmlRep.append("<list-values>").append(toAdd).append("</list-values>");
            }
        }


        xmlRep.append(createValidationXMLForField(field));

        xmlRep.append("</field>");
        return xmlRep;
    }

    private StringBuffer createUnitFieldXML(FieldObject field) {
        StringBuffer xmlRep = new StringBuffer();

        xmlRep.append("<unit-field data-type=\"").append(field.getDatatype()).
                append("\" is-multiple-value=\"").append(String.valueOf(field.isAcceptsMultipleValues())).
                append("\" is-required=\"").append(String.valueOf(field.isRequired())).append("\">");

        // output field description
        xmlRep.append("<description>").append(StringUtils.cleanUpString(field.getDescription())).append("\"</description>");
        xmlRep.append(createValidationXMLForField(field));
        xmlRep.append("</unit-field>");

        return xmlRep;
    }

    private StringBuffer createProtocolFieldXML(FieldObject field) {
        StringBuffer xmlRep = new StringBuffer();

        xmlRep.append("<protocol-field protocol-type =\"").append(field.getDefaultVal()).append("\"/>");

        return xmlRep;
    }

    private StringBuffer createValidationXMLForField(FieldObject field) {
        StringBuffer valRep = new StringBuffer();
        if (field.getDatatype() == DataTypes.ONTOLOGY_TERM) {
            if (field.getRecommmendedOntologySource() != null && field.getRecommmendedOntologySource().size() > 0) {
                // output recommended ontology sources!
                valRep.append("<recommended-ontologies>");

                for (String ontology : field.getRecommmendedOntologySource().keySet()) {
                    RecommendedOntology ro = field.getRecommmendedOntologySource().get(ontology);

                    Ontology o = ro.getOntology();
                    valRep.append("<ontology id=\"").append(o.getOntologyID()).
                            append("\" abbreviation=\"").append(o.getOntologyAbbreviation()).
                            append("\" name=\"").append(o.getOntologyDisplayLabel()).
                            append("\" version=\"").append(o.getOntologyVersion()).append("\"");

                    if (ro.getBranchToSearchUnder() == null) {
                        valRep.append("/>");
                    } else {
                        valRep.append(">");
                        valRep.append("<branch id=\"").append(ro.getBranchToSearchUnder().getBranchIdentifier()).
                                append("\" name=\"").append(ro.getBranchToSearchUnder().getBranchName()).append("\"/>");
                        valRep.append("</ontology>");
                    }
                }

                valRep.append("</recommended-ontologies>");
            }
        }
        return valRep;
    }
}
