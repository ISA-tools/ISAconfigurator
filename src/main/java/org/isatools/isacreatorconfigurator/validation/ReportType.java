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


    CARDINALITY("Cardinality error"),
    ORDER("Order error"),
    POSITIONING("Positioning error");

    private String value;

    private ReportType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public String getSalientFieldOrIndex(String message) {
        if (message != null) {
            if (this == CARDINALITY) {
                return message.substring(message.indexOf("field") + 5, message.indexOf("must")).replaceAll("\"", "").trim();
            } else if (this == ORDER) {
                return message.substring(0, message.lastIndexOf("should")).replaceAll("\"", "").trim();
            } else if (this == POSITIONING) {
                System.err.println("message: " + message);
                if (message.contains("position")) {
                    return message.substring(message.lastIndexOf("position") + 8, message.lastIndexOf("is on")).replaceAll("\"", "").trim();
                } else {
                    return message;
                }
            }
        }

        return null;
    }

    public static void main(String[] args) {
        String index = ReportType.ORDER.getSalientFieldOrIndex("\"Sample Name\" should follow \"Source Name\"");
        System.out.println("Index is " + index);

        String index2 = ReportType.CARDINALITY.getSalientFieldOrIndex("ERROR: The ISA-TAB field \"Sample Name\" must appear at least 1 time(s) in current table");
        System.out.println("Index is: " + index2);

        String index3 = ReportType.POSITIONING.getSalientFieldOrIndex("Unit field in position 4 is on its own. It should be beside a Characteristic, Factor Value or Parameter Value.");
        System.out.println("Index is: " + index3);
    }
}
