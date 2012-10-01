package org.isatools.isacreatorconfigurator.configui;

import org.isatools.isacreator.configuration.MappingObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 26/04/2012
 *         Time: 15:58
 */
public class ApplicationManager {
    
    private static MappingObject currentMappingObject;
    
    private static Map<MappingObject, Map<String, Set<String>>> fileErrors
            = new HashMap<MappingObject, Map<String, Set<String>>>();
    
    public static void setCurrentMappingObject(MappingObject mappingObject) {
        ApplicationManager.currentMappingObject = mappingObject;
    }

    public static MappingObject getCurrentMappingObject() {
        return currentMappingObject;
    }


    public static void setFileErrors(Map<MappingObject, Map<String, Set<String>>> fileErrors) {
        ApplicationManager.fileErrors = fileErrors;
    }

    public static Map<MappingObject, Map<String, Set<String>>> getFileErrors() {
        return fileErrors;
    }
}
