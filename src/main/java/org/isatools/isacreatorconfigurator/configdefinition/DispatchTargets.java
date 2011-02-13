package org.isatools.isacreatorconfigurator.configdefinition;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 13/02/2011
 *         Time: 17:59
 */
public enum DispatchTargets {

    GENERIC("generic"), MAGETAB("magetab"), MEDA("meda"), PRIDEML("prideml"), SRA("sra");
    private String target;

    DispatchTargets(String target) {

        this.target = target;
    }

    @Override
    public String toString() {
        return target;
    }

    public static String[] asStringArray() {
        String[] values = new String[values().length];
        for(int valueIndex = 0; valueIndex < values().length; valueIndex++) {
            values[valueIndex] = values()[valueIndex].toString();
        }

        return values;
    }
}
