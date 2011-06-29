package org.isatools.isacreatorconfigurator.configui;

import org.isatools.isacreator.configuration.FieldObject;

import java.io.Serializable;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 29/03/2011
 *         Time: 17:50
 */
public class SectionDisplay implements Display, Serializable {

    private String fieldName;

    public SectionDisplay(String fieldName) {

        this.fieldName = fieldName;
    }

    public FieldObject getFieldDetails() {
        return null;
    }

    @Override
    public String toString() {
        return fieldName;
    }

}
