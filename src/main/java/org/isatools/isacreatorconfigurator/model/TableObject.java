package org.isatools.isacreatorconfigurator.model;

import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.configuration.MappingObject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Object contains tableName as well as references to the field objects and the
 * supertypes which the class should possess.
 *
 * @author Eamonn Maguire
 */
public class TableObject implements Serializable {
    private MappingObject tableDetails;
    private List<FieldObject> fields;
    private Map<Integer, String[]> tableStructure;

    public TableObject(MappingObject tableDetails, List<FieldObject> fields, Map<Integer, String[]> tableStructure) {
        this.tableDetails = tableDetails;
        this.fields = fields;
        this.tableStructure = tableStructure;
    }

    public List<FieldObject> getFields() {
        return fields;
    }

    public Map<Integer, String[]> getTableStructure() {
        return tableStructure;
    }

    public String getTableName() {
        return tableDetails.getAssayName();
    }

    public MappingObject getMappingObject() {
        return tableDetails;
    }
}