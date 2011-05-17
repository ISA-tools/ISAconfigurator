/**
 ISAconfigurator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAconfigurator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 �The contents of this file are subject to the CPAL version 1.0 (the �License�); you may not use this file except
 in compliance with the License. You may obtain a copy of the License at http://isa-tools.org/licenses/ISAconfigurator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections 14 and 15 have been added to cover use of software over
 a computer network and provide for limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 consistent with Exhibit B.

 Software distributed under the License is distributed on an �AS IS� basis, WITHOUT WARRANTY OF ANY KIND, either express
 or implied. See the License for the specific language governing rights and limitations under the License.

 The Original Code is ISAconfigurator.
 The Original Developer is the Initial Developer. The Initial Developer of the Original Code is the ISA Team
 (Eamonn Maguire, eamonnmag@gmail.com; Philippe Rocca-Serra, proccaserra@gmail.com; Susanna-Assunta Sansone,
 sa.sanson@gmail.com; http://www.isa-tools.org). All portions of the code written by the ISA Team are
 Copyright (c) 2007-2011 ISA Team. All Rights Reserved.

 EXHIBIT B. Attribution Information
 Attribution Copyright Notice: Copyright (c) 2008-2011 ISA Team
 Attribution Phrase: Developed by the ISA Team
 Attribution URL: http://www.isa-tools.org
 Graphic Image provided in the Covered Code as file: http://isa-tools.org/licenses/icons/poweredByISAtools.png
 Display of Attribution Information is required in Larger Works which are defined in the CPAL as a work which combines
 Covered Code or portions thereof with code not governed by the terms of the CPAL.

 Sponsors:
 The ISA Team and the ISA software suite have been funded by the EU Carcinogenomics project (http://www.carcinogenomics.eu),
 the UK BBSRC (http://www.bbsrc.ac.uk), the UK NERC-NEBC (http://nebc.nerc.ac.uk) and in part by the EU NuGO consortium
 (http://www.nugo.org/everyone).
 */

package org.isatools.isacreatorconfigurator.ontologymanager;

import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.log4j.Logger;
import org.isatools.isacreatorconfigurator.configdefinition.Ontology;
import org.isatools.isacreatorconfigurator.configdefinition.RecommendedOntology;
import org.isatools.isacreatorconfigurator.ontologymanager.bioportal.model.BioPortalOntology;
import org.isatools.isacreatorconfigurator.ontologymanager.bioportal.utils.BioPortalXMLModifier;
import org.isatools.isacreatorconfigurator.ontologymanager.bioportal.utils.Modifier;
import org.isatools.isacreatorconfigurator.ontologymanager.bioportal.utils.NCIThesaurusModifier;
import org.isatools.isacreatorconfigurator.ontologymanager.bioportal.xmlresulthandlers.AcceptedOntologies;
import org.isatools.isacreatorconfigurator.ontologymanager.bioportal.xmlresulthandlers.BioPortalClassBeanResultHandler;
import org.isatools.isacreatorconfigurator.ontologymanager.bioportal.xmlresulthandlers.BioPortalOntologyListResultHandler;
import org.isatools.isacreatorconfigurator.ontologymanager.bioportal.xmlresulthandlers.BioPortalSearchBeanResultHandler;
import org.isatools.isacreatorconfigurator.ontologyselectiontool.OntologySourceManager;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;


public class BioPortalClient implements OntologyService {
    private static final Logger log = Logger.getLogger(BioPortalClient.class.getName());

    public static final String DOWNLOAD_ONTOLOGY_LOC = "Data" + File.separator + "ontologies" + File.separator;
    public static final int PARENTS = 0;
    public static final int CHILDREN = 1;

    public static final String REST_URL = "http://rest.bioontology.org/bioportal/";

    public static final String DOWNLOAD_FILE_LOC = "Data" + File.separator + "ontologies_matching_";

    private static final String EXT = ".xml";

    private boolean doneOntologyCheck = false;
    private boolean allowRetry = true;

    private Map<String, String> ontologySources;
    private Map<String, String> ontologyVersions;

    private Map<String, BioPortalOntology> searchResults;
    private static Map<String, Map<String, String>> cachedNodeChildrenQueries;

    private Set<String> noChildren;

    public BioPortalClient() {
        ontologySources = new HashMap<String, String>();
        ontologyVersions = new HashMap<String, String>();
        searchResults = new HashMap<String, BioPortalOntology>();
        cachedNodeChildrenQueries = new HashMap<String, Map<String, String>>();
        noChildren = new HashSet<String>();
    }

    public List<Ontology> getAllOntologies() {
        String searchString = REST_URL + "ontologies";

        resetRetryFlag();
        downloadFile(searchString, DOWNLOAD_FILE_LOC + "ontologies" + EXT);

        BioPortalOntologyListResultHandler parser = new BioPortalOntologyListResultHandler();

        List<Ontology> ontologies = parser.parseFile(DOWNLOAD_FILE_LOC + "ontologies" + EXT);

        deleteFile(DOWNLOAD_FILE_LOC + "ontologies" + EXT);

        return ontologies;
    }

    public Ontology getOntologyById(String ontologyId) {
        String searchString = REST_URL + "virtual/ontology/" + ontologyId;
        String downloadLocation = DOWNLOAD_FILE_LOC + "ontology-info-" + ontologyId + EXT;

        downloadFile(searchString, downloadLocation);

        BioPortalOntologyListResultHandler parser = new BioPortalOntologyListResultHandler();

        List<Ontology> ontologies = parser.parseFile(downloadLocation);

        deleteFile(downloadLocation);

        if (!ontologies.isEmpty()) {
            return ontologies.get(0);
        }

        return null;
    }

    public String getLatestOntologyVersion(String ontologyId) {

        Ontology o = getOntologyById(ontologyId);

        if (o == null) {
            return null;
        } else {
            return o.getOntologyVersion();
        }
    }

    public Map<String, String> getOntologyNames() {
        return ontologySources;
    }

    public Map<String, String> getOntologyVersions() {
        return ontologyVersions;
    }

    public Map<String, String> getTermMetadata(String termAccession, String ontology) {

        BioPortalOntology ontologyResult = getTermInformation(termAccession, ontology);
        Map<String, String> result = new ListOrderedMap<String, String>();
        result.put("accession", termAccession);

        result.putAll(ontologyResult.getComments());

        return result;
    }

    public BioPortalOntology getTermInformation(String termAccession, String ontology) {
        BioPortalOntology bpo;
        if (searchResults.containsKey(ontology + "-" + termAccession)) {
            bpo = searchResults.get(ontology + "-" + termAccession);
            if (bpo != null) {
                if (bpo.getOntologyPurl() == null ||
                        bpo.getOntologyPurl().trim().equals("")) {
                    bpo = performMetadataQuery(termAccession, ontology);
                    searchResults.put(ontology + "-" + termAccession, bpo);
                } else {
                    return bpo;
                }
            }
        } else {
            bpo = performMetadataQuery(termAccession, ontology);
            searchResults.put(ontology + "-" + termAccession,
                    bpo);
        }
        return bpo;
    }

    private BioPortalOntology performMetadataQuery(String termAccession, String ontology) {
        String searchString = REST_URL + "concepts/" + ontology + "/" + termAccession;

        System.out.println("GETTING term information for " + termAccession + " -> " + searchString);

        String downloadLocation = DOWNLOAD_FILE_LOC + ontology + "-" + termAccession + EXT;

        resetRetryFlag();
        downloadFile(searchString, downloadLocation);

        File fileWithNameSpace = BioPortalXMLModifier.addNameSpaceToFile(new File(downloadLocation), "http://bioontology.org/bioportal/classBeanSchema#", "<success>");

        if (fileWithNameSpace != null) {

            BioPortalClassBeanResultHandler handler = new BioPortalClassBeanResultHandler();

            BioPortalOntology result = handler.parseMetadataFile(fileWithNameSpace.getAbsolutePath());

            deleteFile(downloadLocation);
            deleteFile(fileWithNameSpace.getAbsolutePath());
            return result;
        } else {
            return null;
        }
    }

    public Map<String, String> getTermByAccessionId(String id) {
        String searchString = REST_URL + "ontologies";

        resetRetryFlag();
        downloadFile(searchString, "Data/ontology_" + id + EXT);

        return null;
    }

    public Map<String, String> getTermsByPartialNameFromSource(String term, List<RecommendedOntology> recommendedOntologies) {

        term = correctTermForHTTPTransport(term);

        // need to accommodate more complicated search strings in the case where the recommended source contains the branch
        // to search under as well!
        Map<String, String> result = new HashMap<String, String>();

        // need to do a loop over all branches and do a single query on those recommended ontologies only defined
        // by the source, not the branch.
        for (RecommendedOntology ro : recommendedOntologies) {

            if (ro.getOntology() != null) {

                StringBuilder searchString = new StringBuilder();

                searchString.append(REST_URL);

                searchString.append("search/").append(term).append("/?ontologyids=").append(ro.getOntology().getOntologyID());


                if (ro.getBranchToSearchUnder() != null && !ro.getBranchToSearchUnder().getBranchIdentifier().equals("")) {
                    String branch = ro.getBranchToSearchUnder().getBranchIdentifier();

                    if (ro.getOntology().getOntologyID().equals(AcceptedOntologies.NCI_THESAURUS.getOntologyID())
                            || ro.getOntology().getOntologyID().equals(AcceptedOntologies.NPO.getOntologyID())) {
                        Modifier modifier = new NCIThesaurusModifier();
                        branch = modifier.modifySearch(branch);

                    }

                    searchString.append("&subtreerootconceptid=").append(branch);
                }

                System.out.println("sending query: " + searchString);

                Map<String, String> searchResult = downloadAndProcessBranch(term, searchString.toString());

                if (searchResult != null) {
                    result.putAll(searchResult);
                }
            }
        }


        return result;
    }


    private Map<String, String> downloadAndProcessBranch(String term, String searchString) {
        String downloadLocation = DOWNLOAD_FILE_LOC + term + EXT;

        resetRetryFlag();
        downloadFile(searchString, downloadLocation);

        BioPortalSearchBeanResultHandler handler = new BioPortalSearchBeanResultHandler();

        File fileWithNameSpace = BioPortalXMLModifier.addNameSpaceToFile(new File(downloadLocation), "http://bioontology.org/bioportal/resultBeanSchema#", "<success>");

        Map<String, BioPortalOntology> result = handler.getSearchResults(fileWithNameSpace.getAbsolutePath());

        deleteFile(downloadLocation);
        deleteFile(fileWithNameSpace.getAbsolutePath());

        updateOntologyManagerWithOntologyInformation();

        return processBioPortalOntology(result);
    }

    public Map<String, String> getTermsByPartialNameFromSource(String term, String source, boolean reverseOrder) {


        term = correctTermForHTTPTransport(term);
        String searchString = REST_URL + "search/" + term + "/?ontologyids=" + (((source == null) || source.trim().equalsIgnoreCase("all")) ? constructSourceStringFromAllowedOntologies() : source);
        System.out.println("search string " + searchString);

        Map<String, String> searchResult = downloadAndProcessBranch(term, searchString);

        return searchResult == null ? new HashMap<String, String>() : searchResult;
    }

    private String constructSourceStringFromAllowedOntologies() {
        String allowedOntologies = "";

        int count = 0;
        for (AcceptedOntologies ao : AcceptedOntologies.values()) {
            allowedOntologies += ao.getOntologyID();
            if (count < AcceptedOntologies.values().length - 1) {
                allowedOntologies += ",";
            }
            count++;
        }

        return allowedOntologies;
    }

    private void updateOntologyManagerWithOntologyInformation() {
        if (!doneOntologyCheck) {
            for (AcceptedOntologies ao : AcceptedOntologies.values()) {
                Ontology o = getOntologyById(ao.getOntologyID());
                if (o != null) {
                    OntologySourceManager.appendOntologyDescriptions(Collections.singletonMap(o.getOntologyAbbreviation(), o.getOntologyDisplayLabel()));
                    OntologySourceManager.appendOntologyVersions(Collections.singletonMap(o.getOntologyAbbreviation(), o.getOntologyVersion()));
                }
            }
            doneOntologyCheck = true;
        }
    }


    /**
     * Finds the root in an ontology
     *
     * @param ontology - ontology to search in as it's version ID e.g. 39002 for BRO
     * @return Map<String,String> representing ontology term accession to term label mappings.
     */
    public Map<String, String> getOntologyRoots(String ontology) {

        System.out.println("getting ontology roots");
        if (!cachedNodeChildrenQueries.containsKey(ontology)) {

            String searchString = REST_URL + "concepts/" + ontology + "/root";

            String downloadLocation = DOWNLOAD_FILE_LOC + ontology + "-roots" + EXT;

            resetRetryFlag();
            downloadFile(searchString, downloadLocation);

            File fileWithNameSpace = BioPortalXMLModifier.addNameSpaceToFile(new File(downloadLocation), "http://bioontology.org/bioportal/classBeanSchema#", "<success>");

            BioPortalClassBeanResultHandler handler = new BioPortalClassBeanResultHandler();

            Map<String, BioPortalOntology> result = handler.parseRootConceptFile(fileWithNameSpace.getAbsolutePath(), noChildren);

            Map<String, String> processedResult = new HashMap<String, String>();

            if (result != null) {
                searchResults.putAll(result);
                processedResult.putAll(processBioPortalOntology(result));
                cachedNodeChildrenQueries.put(ontology, processedResult);
            }

            deleteFile(downloadLocation);
            deleteFile(fileWithNameSpace.getAbsolutePath());

            return processedResult;
        } else {
            System.out.println("using cached version for display.");
            return cachedNodeChildrenQueries.get(ontology);
        }
    }

    public Map<String, String> getTermParent(String termAccession, String ontology) {
        return getTermChildOrParent(termAccession, ontology, PARENTS);
    }


    /**
     * Return the children of a term with gathering extra metadata information about the term.
     * This makes creating tree views quicker, although it's primarily for BioPortal WS queries since
     * it requires an extra query per child term to resolve the PURLs etc.
     *
     * @param termAccession - term to search under
     * @param ontology      - ontology term is located in
     * @return Map<String,String> with mappings from term accession to the term label
     */
    public Map<String, String> getTermChildren(String termAccession, String ontology) {
        return getTermChildOrParent(termAccession, ontology, CHILDREN);
    }

    /**
     * Gets the children or parents of a term
     *
     * @param termAccession - accession of term e.g. snap:Continuant
     * @param ontology      - ontology version id e.g. 40832 for OBI
     * @param type          - @see PARENTS, CHILDREN - what type of search to make
     * @return Map<String, String> from term accession -> term label
     */
    public Map<String, String> getTermChildOrParent(String termAccession, String ontology, int type) {
        if (!noChildren.contains(termAccession)) {
            if (!cachedNodeChildrenQueries.containsKey(ontology + "-" + termAccession)) {

                String searchString = REST_URL + "concepts/" + ((type == PARENTS) ? "parents/" : "") + "" + ontology + "?conceptid=" + termAccession;

                System.out.printf("Search string is %s\n", searchString);

                String downloadLocation = DOWNLOAD_FILE_LOC + ontology + "-" + termAccession + EXT;
                resetRetryFlag();
                downloadFile(searchString, downloadLocation);

                BioPortalClassBeanResultHandler handler = new BioPortalClassBeanResultHandler();

                File fileWithNameSpace = BioPortalXMLModifier.addNameSpaceToFile(new File(downloadLocation),
                        "http://bioontology.org/bioportal/classBeanSchema#", "<success>");

                if (fileWithNameSpace != null) {

                    Map<String, BioPortalOntology> result = handler.parseRootConceptFile(fileWithNameSpace.getAbsolutePath(), noChildren);

                    Map<String, String> processedResult = new HashMap<String, String>();

                    if (result != null) {
                        searchResults.putAll(result);
                        processedResult.putAll(processBioPortalOntology(result));
                        cachedNodeChildrenQueries.put(ontology + "-" + termAccession, processedResult);
                    }

                    deleteFile(downloadLocation);
                    deleteFile(fileWithNameSpace.getAbsolutePath());
                    return processedResult;
                } else {
                    return new HashMap<String, String>();
                }
            } else {
                return cachedNodeChildrenQueries.get(ontology + "-" + termAccession);
            }
        } else {
            return new HashMap<String, String>();
        }

    }

    /**
     * Method will search the Ontology space to determine the parents of a given term. This can then be used in searches to make
     * location of a term within an Ontology much quicker.
     *
     * @param termAccession - the accession of the term being searched on e.g. ENVO:00003073
     * @param ontology      - version access for the ontology you wish to query e.g. 40832 for OBI
     * @return Map<String, String> representing the parents of the Term
     */
    public Map<String, String> getAllTermParents(String termAccession, String ontology) {
//        http://rest.bioontology.org/bioportal/path/45155/?source=efo:EFO_0000428&target=root
//        /virtual/[rootpath|leafpath]/{ontologyId}/{conceptId}
        String searchString = REST_URL + "virtual/rootpath/" + ontology + "/" + termAccession;

        String downloadLocation = DOWNLOAD_FILE_LOC + ontology + "-all-parents-" + termAccession + EXT;

        resetRetryFlag();
        downloadFile(searchString, downloadLocation);

        File fileWithNameSpace = BioPortalXMLModifier.addNameSpaceToFile(new File(downloadLocation), "http://bioontology.org/bioportal/classBeanSchema#", "<success>");

        BioPortalClassBeanResultHandler handler = new BioPortalClassBeanResultHandler();

        Map<String, String> result = handler.parseOntologyParentPathFile(fileWithNameSpace.getAbsolutePath());

        deleteFile(downloadLocation);
        deleteFile(fileWithNameSpace.getAbsolutePath());

        return result;
    }

    public Map<String, BioPortalOntology> getSearchResults() {
        return searchResults;
    }

    private String correctTermForHTTPTransport(String term) {
        return term.replaceAll("[\\s]+", "%20");
    }

    private boolean downloadFile(String fileLocation, String downloadLocation) {
        URL url;
        OutputStream os = null;
        InputStream is = null;

        try {
            url = new URL(fileLocation);

            URLConnection urlConn = url.openConnection();
            urlConn.setReadTimeout(10000);
            urlConn.setUseCaches(true);

            is = urlConn.getInputStream();

            os = new BufferedOutputStream(new FileOutputStream(downloadLocation));

            byte[] inputBuffer = new byte[1024];
            int numBytesRead;

            while ((numBytesRead = is.read(inputBuffer)) != -1) {
                os.write(inputBuffer, 0, numBytesRead);
            }

            return true;
        } catch (MalformedURLException e) {
            log.error("url malformed: " + e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            log.error("file not found" + e.getMessage());
            return false;
        } catch (IOException e) {
            log.error("io exception caught" + e.getMessage());
            // we allow one retry attempt due to problems with BioPortal not always serving
            // back results on the first attempt!
            // NO LONGER RETRYING. NEW SERVICE APPEARS TO BE MUCH MORE RELIABLE
//            if (allowRetry) {
//                log.info("attempting retry");
//                allowRetry = false;
//                downloadFile(fileLocation, downloadLocation);
//            }
            return false;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }

                if (is != null) {
                    is.close();
                }
            } catch (IOException ioe) {
                log.error("io exception caught: " + ioe.getMessage());

            }
        }
    }

    private void deleteFile(String file) {
        File f = new File(file);
        if (f.exists()) {
            f.delete();
        }
    }


    public void downloadOntology(Ontology ontology) {
        String searchString = REST_URL + "ontologies/download/" + ontology.getOntologyVersion();

        File downloadDir = new File(DOWNLOAD_ONTOLOGY_LOC);
        if (!downloadDir.exists()) {
            downloadDir.mkdir();
        }

        String fileDownload = downloadDir.getAbsolutePath() + File.separator + ontology.getOntologyVersion() + "." + ontology.getFormat();
        // only download an ontology file if it doesn't already exist!
        if (!new File(fileDownload).exists()) {
            resetRetryFlag();
            downloadFile(searchString, fileDownload);
        }

    }

    private void resetRetryFlag() {
        allowRetry = true;
    }

    /**
     * Processes Map of ID -> SearchBeans to return the information in a way readable into the OntologySelectionTool
     * and stores the ontology source details for provision of source information...
     *
     * @param result - Map<String, SearchBean> returned from ProcessBioportalXML classes parseFile method.
     * @return - Map of Source:Accession to Term for entry into the tree view in the ontology lookup tool.
     */
    private Map<String, String> processResult(Map<String, BioPortalOntology> result) {

        Map<String, String> finalResult = new HashMap<String, String>();

        if (result != null) {
            for (String key : result.keySet()) {

                String tempKey = key.substring(key.indexOf(":") + 1);
                boolean validSourceAccession = key.contains(":") || key.contains("_");

                if (validSourceAccession) {

                    finalResult.put(tempKey, result.get(tempKey).getOntologyTermName());
                    String separator = tempKey.contains(":") ? ":" : "_";
                    tempKey = tempKey.substring(0, tempKey.indexOf(separator));
                    ontologySources.put(tempKey, result.get(tempKey).getOntologySource());
                    ontologyVersions.put(tempKey, result.get(tempKey).getOntologyVersionId());
                }

                ontologySources.put("IAO", "Information Artifact Ontology");
            }
        }

        return finalResult;
    }

    private Map<String, String> processBioPortalOntology(Map<String, BioPortalOntology> toConvert) {
        Map<String, String> convertedMap = new HashMap<String, String>();
        for (String ontologyAccession : toConvert.keySet()) {
            BioPortalOntology bpo = toConvert.get(ontologyAccession);

            if (bpo != null) {
                convertedMap.put(bpo.getOntologySourceAccession(), bpo.getOntologyTermName());
            }
        }
        return convertedMap;
    }

    public static void main(String[] args) {
        BioPortalClient bpc = new BioPortalClient();

        System.out.println("Sending query to NCBO...");

        Map<String, String> result = bpc.getOntologyRoots("42280");

        System.out.println("Got result from NCBO, now processing...");

        for (String accession : result.keySet()) {
            System.out.println(result.get(accession) + " (" + accession + ")");
        }
    }


}
