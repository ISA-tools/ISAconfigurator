package org.isatools.isacreatorconfigurator.configui.io.xml;

import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreatorconfigurator.configui.Field;
import org.isatools.isacreatorconfigurator.configui.Fields;
import org.isatools.isacreatorconfigurator.configui.Location;
import org.w3c.dom.NodeList;
import uk.ac.ebi.utils.xml.XPathReader;

import javax.xml.xpath.XPathConstants;

/**
 * Created by IntelliJ IDEA.
 * User: prs
 * Date: 18/01/2012
 * Time: 18:57
 * To change this template use File | Settings | File Templates.
 */
public class ProcessStandardFieldsXML {

    public static final String STANDARD_FIELDS_XML = "/config/std_isa_fields.xml";
    public static final String CUSTOM_FIELDS_XML = "/config/custom_isa_fields.xml";

    public Fields loadFieldsFromFile(String fileName) {

        XPathReader reader = new XPathReader(getClass().getResourceAsStream(fileName));

        NodeList fields = (NodeList) reader.read("/fields/field", XPathConstants.NODESET);

        Fields fieldsList = new Fields();

        if (fields.getLength() > 0) {
            // carry on

            for (int fieldIndex = 1; fieldIndex < fields.getLength() + 1; fieldIndex++) {
                Field newField = new Field();

                String fieldName = (String) reader.read("/fields/field[" + fieldIndex + "]/name", XPathConstants.STRING);
                newField.setName(fieldName);

                NodeList appearsIn = (NodeList) reader.read("/fields/field[" + fieldIndex + "]/appearsIn/location", XPathConstants.NODESET);

                for (int locationIndex = 1; locationIndex < appearsIn.getLength() + 1; locationIndex++) {

                    String location = (String) reader.read("/fields/field[" + fieldIndex + "]/appearsIn/location[" + locationIndex + "]", XPathConstants.STRING);
                    Location newLocation = Location.resolveLocationIdentifier(location);

                    if (newLocation != null) {
                        // System.out.println(newLocation);
                        newField.addAppearsIn(newLocation);
                    }
                }

                boolean assayDefault = (Boolean) reader.read("/fields/field[" + fieldIndex + "]/assayDefault", XPathConstants.BOOLEAN);
                boolean studyDefault = (Boolean) reader.read("/fields/field[" + fieldIndex + "]/studyDefault", XPathConstants.BOOLEAN);
                boolean invDefault = (Boolean) reader.read("/fields/field[" + fieldIndex + "]/invDefault", XPathConstants.BOOLEAN);
                String fixedType = (String) reader.read("/fields/field[" + fieldIndex + "]/fixedDataType", XPathConstants.STRING);

                if (fixedType != null && !fixedType.isEmpty()) {
                    DataTypes dataType = DataTypes.resolveDataType(fixedType);
                    if (dataType != null) {
                        newField.setFixedDataType(dataType);
                    }
                }

                newField.setAssayDefault(assayDefault);
                newField.setStudyDefault(studyDefault);
                newField.setInvDefault(invDefault);

                if (fieldName != null) {
                    fieldsList.addField(newField);
                }

            }

        }

        return fieldsList;

    }

}
