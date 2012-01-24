package org.isatools.isacreatorconfigurator.configui.xml;

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

            for (int fieldIndex = 0; fieldIndex < fields.getLength(); fieldIndex++) {
                Field newField = new Field();

                String fieldName = (String) reader.read("/fields/field[" + fieldIndex + "]/name", XPathConstants.STRING);
                newField.setName(fieldName);

                NodeList appearsIn = (NodeList) reader.read("/fields/field[" + fieldIndex + "]/appearsIn/location/text()",XPathConstants.NODESET);

                for (int locationIndex = 0; locationIndex < appearsIn.getLength(); locationIndex++) {

                    //System.out.println("found at rank: " + locationIndex);

                    //String location = (String) reader.read("/fields/field[" + fieldIndex + "]/appearsIn/location[" + locationIndex + "]",XPathConstants.STRING);

                    String location = appearsIn.item(locationIndex).getNodeValue().toString();

                    //System.out.println("location is: " + appearsIn.item(locationIndex).getNodeValue());

                    Location newLocation = Location.resolveLocationIdentifier(location);


                    if (newLocation != null) {
                       // System.out.println(newLocation);
                        newField.addAppearsIn(newLocation);
                    }
                }

                if (fieldName != null) {
                    fieldsList.addField(newField);
                }

            }

        }

        return fieldsList;

    }

}
