package org.isatools.isacreatorconfigurator.validation;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 25/04/2012
 *         Time: 12:57
 */
public enum ReportType {

    CARDINALITY("Cardinality checking"),ORDER("Order checking"),POSITIONING("Positioning checking");
    private String value;

    private ReportType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
