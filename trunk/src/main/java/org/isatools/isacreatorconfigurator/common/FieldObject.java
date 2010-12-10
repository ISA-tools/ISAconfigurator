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

package org.isatools.isacreatorconfigurator.common;

import java.io.Serializable;


/**
 * FieldObject contains information from the FieldData interface.
 * Both should be edited in Tandem during modification as one will depend on the other
 *
 * @author Eamonn Maguire
 */
public class FieldObject implements Serializable {
    private String datatype;
    private String defaultVal;
    private String description;
    private String errorMessageText = null;
    private String errorMessageTitle = null;
    private String fieldName;

    // contains inputformat + checks on size if required.
    private String finalInputFormat = null;
    private String inputFormat = null;
    private String recommmendedOntologySource = null;
    private String val1 = null;
    private String val2 = null;
    private String validationType = null;
    private String[] fieldList = null;
    private boolean acceptsFileLocations;
    private boolean acceptsMultipleValues;
    private boolean isEditable;
    private boolean isInputFormatted;
    private boolean required;
    private String wizardTemplate = "";
    private int colNo;
    private int fieldLength = 0;

    /**
     * @param colNo                 - Column No for field
     * @param fieldName             - Name of Field
     * @param description           - Description of field. Will be used when displaying tool tips/help
     * @param datatype              - The type of data being entered e.g. String
     * @param defaultVal            - The default value for the field
     * @param required              - Is the field required?
     * @param editable              - Is the field editable?
     * @param acceptsMultipleValues - Does the field accept multiple values separated by comma's (,)
     * @param acceptsFileLocations  - Does the field Accept a file Location
     */
    public FieldObject(int colNo, String fieldName, String description,
                       String datatype, String defaultVal, boolean required, boolean editable,
                       boolean acceptsMultipleValues, boolean acceptsFileLocations) {
        this.colNo = colNo;
        this.fieldName = fieldName;
        this.description = description;
        this.datatype = datatype;
        this.defaultVal = defaultVal;
        this.required = required;
        isEditable = editable;
        this.acceptsMultipleValues = acceptsMultipleValues;
        this.acceptsFileLocations = acceptsFileLocations;
    }

    public FieldObject(String fieldName, String description,
                       String datatype, String defaultVal, boolean required, boolean editable,
                       boolean acceptsMultipleValues, boolean acceptsFileLocations) {
        this.fieldName = fieldName;
        this.description = description;
        this.datatype = datatype;
        this.defaultVal = defaultVal;
        this.required = required;
        isEditable = editable;
        this.acceptsMultipleValues = acceptsMultipleValues;
        this.acceptsFileLocations = acceptsFileLocations;
    }

    public int getColNo() {
        return colNo;
    }

    public String getDatatype() {
        return datatype;
    }

    public String getDefaultVal() {
        return defaultVal;
    }

    public String getDescription() {
        return description;
    }

    public String getErrorMessageText() {
        return errorMessageText;
    }

    public String getErrorMessageTitle() {
        return errorMessageTitle;
    }

    public int getFieldLength() {
        return fieldLength;
    }

    public String[] getFieldList() {
        return fieldList;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFinalInputFormat() {
        return finalInputFormat;
    }

    public String getInputFormat() {
        return inputFormat;
    }

    public String getRecommmendedOntologySource() {
        return recommmendedOntologySource;
    }

    public String getVal1() {
        return val1;
    }

    public String getVal2() {
        return val2;
    }

    public String getValidationType() {
        return validationType;
    }

    public boolean isAcceptsFileLocations() {
        return acceptsFileLocations;
    }

    public boolean isAcceptsMultipleValues() {
        return acceptsMultipleValues;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public boolean isInputFormatted() {
        return isInputFormatted;
    }

    public boolean isRequired() {
        return required;
    }

    public void setColNo(int colNo) {
        this.colNo = colNo;
    }

    public void setErrorMessageText(String errorMessageText) {
        this.errorMessageText = errorMessageText;
    }

    public void setErrorMessageTitle(String errorMessageTitle) {
        this.errorMessageTitle = errorMessageTitle;
    }

    public void setFieldLength(int fieldLength) {
        this.fieldLength = fieldLength;
    }

    public void setFieldList(String[] fieldList) {
        this.fieldList = fieldList;
    }

    public void setFinalInputFormat(String finalInputFormat) {
        this.finalInputFormat = finalInputFormat;
    }

    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    public void setInputFormatted(boolean inputFormatted) {
        isInputFormatted = inputFormatted;
    }

    public void setRecommmendedOntologySource(String recommmendedOntologySource) {
        this.recommmendedOntologySource = recommmendedOntologySource;
    }

    public void setVal1(String val1) {
        this.val1 = val1;
    }

    public void setVal2(String val2) {
        this.val2 = val2;
    }

    public void setValidationType(String validationType) {
        this.validationType = validationType;
    }

    public String getWizardTemplate() {
        return wizardTemplate;
    }

    public void setWizardTemplate(String wizardTemplate) {
        this.wizardTemplate = wizardTemplate;
    }
}