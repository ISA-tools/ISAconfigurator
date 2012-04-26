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
    private Map<String, Map<ReportType, Set<String>>> reports;


    public ValidationReport() {
        reports= new HashMap<String, Map<ReportType, Set<String>>>();
        
    }

    public void addToReport(ReportType reportType, String fileName, String message) {
        addFileRecord(reportType, fileName);
        
        reports.get(fileName).get(reportType).add(message);
    }

    public void addToReport(ReportType reportType, String fileName, Set<String> messages) {
        addFileRecord(reportType, fileName);

        reports.get(fileName).get(reportType).addAll(messages);
    }

    private void addFileRecord(ReportType reportType, String fileName) {
        if(!reports.containsKey(fileName)) {
            reports.put(fileName, new HashMap<ReportType, Set<String>>());
        }
        
        if(!reports.get(fileName).containsKey(reportType)) {
            reports.get(fileName).put(reportType, new HashSet<String>());
        }
    }
    
    public boolean isValid() {

        return reports.isEmpty();
    }
    
    public boolean isValid(String fileName) {
        return !reports.containsKey(fileName);
    }

    public Map<String, Map<ReportType, Set<String>>> getReports() {
        return reports;
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        for(String fileName : reports.keySet()) {
            output.append(fileName).append("\n");
            
            for(ReportType reportType : reports.get(fileName).keySet()) {
                output.append("\t").append(reportType).append("\n");
                
                for(String message : reports.get(fileName).get(reportType)) {
                    output.append("\t\t").append(message).append("\n");
                }
            }
        }
        return output.toString();
    }
     
}
