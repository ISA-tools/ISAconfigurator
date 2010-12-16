package org.isatools.isacreatorconfigurator.xml;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: Dec 16, 2010
 *         Time: 10:45:52 AM
 */
public class StringUtils {

    /**
     * Cleans up the String to remove quotes and incorrect spaces
     * @param toClean - String to be cleaned
     * @return Clean String
     */
    public static String cleanUpString(String toClean) {
        // replace " with nothing
        toClean = toClean.replaceAll("\"", "");

        //replace one or more spaces with just one space
        toClean = toClean.replaceAll("[\\s]+", " ");

        // remove all trailing spaces
        toClean = toClean.trim();

        return toClean;
    }
}
