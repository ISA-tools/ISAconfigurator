package org.isatools.isacreatorconfigurator.validation;

import java.util.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 25/04/2012
 *         Time: 12:51
 */
public class ValidationReport {

    // maps from the file name to the error messages received
    private Map<ReportType, Map<String, Set<String>>> reports;


    public ValidationReport() {
        reports= new HashMap<ReportType, Map<String, Set<String>>>();
        for(ReportType type : ReportType.values()) {
            reports.put(type, new HashMap<String, Set<String>>());
        }
    }

    public Map<String, Set<String>> getReport(ReportType reportType) {
        return reports.get(reportType);
    }

    public void addToReport(ReportType reportType, String fileName, String message) {
        addFileRecord(reportType, fileName);

        reports.get(reportType).get(fileName).add(message);
    }

    public void addToReport(ReportType reportType, String fileName, Set<String> messages) {
        addFileRecord(reportType, fileName);

        reports.get(reportType).get(fileName).addAll(messages);
    }

    private void addFileRecord(ReportType reportType, String fileName) {
        if(!reports.get(reportType).containsKey(fileName)) {
            reports.get(reportType).put(fileName, new HashSet<String>());
        }
    }
    
    public boolean isValid() {
        boolean valid = true;
        for(ReportType reportType : reports.keySet()) {
            valid = reports.get(reportType).isEmpty();
            if (!valid) {
                return valid;
            }
        }
        return valid;
    }
    
    public boolean isValid(String fileName) {
        boolean valid = true;
        for(ReportType reportType : reports.keySet()) {
            valid = !reports.get(reportType).containsKey(fileName);

            if (!valid) {
                return valid;
            }
        }
        return valid;
    }
    
    public String toString() {
        StringBuilder output = new StringBuilder();
        for(ReportType reportType : reports.keySet()) {
            output.append(reportType).append("\n");
            
            for(String fileName : reports.get(reportType).keySet()) {
                output.append("\t").append(fileName).append("\n");
                
                for(String message : reports.get(reportType).get(fileName)) {
                    output.append("\t\t").append(message).append("\n");
                }
            }
        }
        return output.toString();
    }
}
