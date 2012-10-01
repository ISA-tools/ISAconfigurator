package org.isatools.isacreatorconfigurator.validation;

import org.isatools.isacreator.configuration.MappingObject;

import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 26/04/2012
 *         Time: 16:37
 */
public class ConfigurationValidationUtils {

    public static MappingObject findMappingObjectForTable(String tableName, Set<MappingObject> mappingObjects) {
        for (MappingObject mappingObject : mappingObjects) {
            if (mappingObject.getAssayName().equals(tableName)) {
                return mappingObject;
            }
        }

        return null;
    }
}
