package org.isatools.isacreatorconfigurator.validation;

import org.apache.log4j.Logger;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isatab_v1.ISATABLoader;
import org.isatools.tablib.exceptions.TabStructureError;
import org.isatools.tablib.mapping.FormatSetTabMapper;
import org.isatools.tablib.schema.*;
import org.isatools.tablib.schema.constraints.FieldCardinalityConstraint;
import org.isatools.tablib.schema.constraints.FieldConstraint;
import org.isatools.tablib.schema.constraints.FollowsConstraint;
import org.isatools.tablib.schema.constraints.PrecedesConstraint;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 24/04/2012
 *         Time: 18:27
 */
public class Validator {

    private static Logger log = Logger.getLogger(Validator.class.getName());
    private static FormatSet isatabSchema;

    private ValidationReport report;

    static {
        InputStream input = new BufferedInputStream(FormatSetTabMapper.class.getResourceAsStream(ISATABLoader.ISATAB_SCHEMA_PATH));
        isatabSchema = SchemaBuilder.loadFormatSetFromXML(input);
    }

    public Validator() {
        report = new ValidationReport();
    }

    public ValidationReport validate(String tableName, String tableType, List<FieldObject> fieldHeaders) {
        tableType = convertInconsistentTableTypes(tableType);

        for (Format format : isatabSchema.getFormats()) {
            for (Section section : format.getSections()) {
                if (format.getId().equalsIgnoreCase(tableType)) {
                    Set<String> cardinalityErrors = validateCardinality(section, fieldHeaders);

                    if (cardinalityErrors.size() > 0) {
                        report.addToReport(ReportType.CARDINALITY, tableName, cardinalityErrors);
                    }

                    Set<String> orderErrors = validateOrder(section, fieldHeaders);
                    if (orderErrors.size() > 0) {
                        report.addToReport(ReportType.ORDER, tableName, orderErrors);
                    }

                    if (!format.getId().equalsIgnoreCase("investigation")) {

                        Set<String> positioningErrors = validatePositioning(fieldHeaders);
                        if (positioningErrors.size() > 0) {
                            report.addToReport(ReportType.POSITIONING, tableName, positioningErrors);
                        }

                        return report;
                    }
                }
            }
        }
        return report;
    }

    /**
     * Will check that the number of fields matches that as expected in the 'isatab schema'
     *
     * @param section - Section matching that of the incoming file type
     * @param headers - column headers as expressed using FieldObjects
     * @return Set<String> representing error messages encountered during validation.
     */
    private Set<String> validateCardinality(Section section, List<FieldObject> headers) {
        Set<String> messages = new HashSet<String>();

        for (Field nodeField : section.getFields()) {

            for (FieldConstraint constraint : nodeField.getConstraints()) {
                if (!(constraint instanceof FieldCardinalityConstraint)) {
                    continue;
                }
                FieldCardinalityConstraint cardCons = (FieldCardinalityConstraint) constraint;

                int fieldCount = 0;
                for (FieldObject fieldObject : headers) {

                    if (fieldObject.getFieldName().equalsIgnoreCase(nodeField.getId())) {
                        fieldCount++;
                    }
                }

                try {
                    cardCons.validateCardinality(fieldCount, nodeField.getId(), log);
                } catch (TabStructureError error) {
                    messages.add(error.getMessage());
                }
            }
        }

        return messages;
    }

    private Set<String> validateOrder(Section section, List<FieldObject> headers) {
        Set<String> messages = new HashSet<String>();

        for (Field nodeField : section.getFields()) {
            for (FieldConstraint constraint : nodeField.getConstraints()) {
                if (constraint instanceof FollowsConstraint) {
                    FollowsConstraint followsConstraint = (FollowsConstraint) constraint;
                    String consField = followsConstraint.getFieldName();

                    boolean meetsConstraint = checkIfFieldAFollowsFieldB(nodeField.getId(), consField, headers);
                    if (!meetsConstraint) {
                        if (fieldExists(nodeField.getId(), headers)) {
                            messages.add(nodeField.getId() + " should follow " + consField);
                        } else {
                            messages.add(nodeField.getId() + " should be present in this file.");
                        }
                    }

                } else if (constraint instanceof PrecedesConstraint) {
                    PrecedesConstraint precedesConstraint = (PrecedesConstraint) constraint;
                    String consField = precedesConstraint.getFieldName();

                    boolean meetsConstraint = checkIfFieldAFollowsFieldB(consField, nodeField.getId(), headers);
                    if (!meetsConstraint) {
                        if (fieldExists(nodeField.getId(), headers)) {
                            messages.add(nodeField.getId() + " should precede" + consField);
                        } else {
                            messages.add(nodeField.getId() + " should be present in this file.");
                        }
                    }
                }
            }
        }

        return messages;
    }

    private boolean fieldExists(String fieldName, List<FieldObject> headers) {

        for (FieldObject field : headers) {
            if (field.getFieldName().equalsIgnoreCase(fieldName)) {
                return true;
            }
        }

        return false;
    }

    private Set<String> validatePositioning(List<FieldObject> headers) {

        Set<String> messages = new HashSet<String>();

        // check that first field is Sample Name or Source Name

        messages.addAll(checkFirstFieldIsSampleNameOrSourceName(headers));

        // check for orphan unit and/ookr parameter values
        messages.addAll(checkForOrphanFields(headers));

        return messages;
    }

    private Set<String> checkFirstFieldIsSampleNameOrSourceName(List<FieldObject> headers) {
        Set<String> messages = new HashSet<String>();
        String firstField = headers.get(0).getFieldName();
        if (!firstField.equalsIgnoreCase("Sample Name") && !firstField.equalsIgnoreCase("Source Name")) {
            messages.add("The first field should be either Source Name or Sample Name");
        }

        return messages;
    }

    /**
     * Checks for orphan fields. By this we mean Parameter Values, Date, etc. not attached to a Protocol or Unit
     * fields not attached to Character
     *
     * @param headers
     * @return
     */
    private Set<String> checkForOrphanFields(List<FieldObject> headers) {
        Set<String> messages = new HashSet<String>();

        Set<FieldObject> invalidFieldPositions = new HashSet<FieldObject>();
        // now we need to check for patterns.
        int currentIndex = 0;

        int lastProtocol = -1;
        int lastCharacteristicFactorParameterValue = -1;

        for (FieldObject field : headers) {
            if (field.getFieldName().equalsIgnoreCase("Protocol REF")) {
                lastProtocol = currentIndex;
            } else if (field.getFieldName().matches("(Parameter Value\\[.+\\]|Factor Value\\[.+\\]|Characteristics\\[.+\\])")) {
                lastCharacteristicFactorParameterValue = currentIndex;
                if (field.getFieldName().contains("Parameter")) {
                    if (lastProtocol == -1) {
                        messages.add(field.getFieldName() + " in position " + currentIndex + " is on its own. It should be beside a Protocol REF.");
                        invalidFieldPositions.add(field);
                    }
                }
            } else if (field.getFieldName().equalsIgnoreCase("unit")) {
                if (lastCharacteristicFactorParameterValue == -1) {
                    messages.add("Unit field in position " + currentIndex + " is on its own. " +
                            "It should be beside a Characteristic, Factor Value or Parameter Value.");
                    invalidFieldPositions.add(field);
                }
            } else if (field.getFieldName().matches("(Date|Performer|Term Source REF|Term Accession Number)")) {
                lastProtocol = currentIndex;
            } else {
                lastProtocol = -1;
                lastCharacteristicFactorParameterValue = -1;
            }
            currentIndex++;
        }

        return messages;
    }

    /**
     * Checks if Field A comes after Field B in the headers.
     *
     * @param fieldA  - Field that should come after Field B
     * @param fieldB  - Field that should come before Field A
     * @param headers - list<FieldObject> representing the column order
     * @return boolean - true if A follows B, false otherwise.
     */
    private boolean checkIfFieldAFollowsFieldB(String fieldA, String fieldB, List<FieldObject> headers) {
        int fieldAIndex = -1;
        int fieldBIndex = -1;

        int index = 0;
        for (FieldObject field : headers) {
            if (field.getFieldName().equalsIgnoreCase(fieldA)) {
                fieldAIndex = index;
            }
            if (field.getFieldName().equalsIgnoreCase(fieldB)) {
                fieldBIndex = index;
            }

            if (fieldAIndex != -1 && fieldBIndex != -1) {
                break;
            }

            index++;
        }
        return fieldAIndex > fieldBIndex;
    }


    public ValidationReport getReport() {
        return report;
    }

    private String convertInconsistentTableTypes(String name) {
        if (name.equalsIgnoreCase("study sample")) {
            return "study_samples";
        }
        return name;
    }

}
