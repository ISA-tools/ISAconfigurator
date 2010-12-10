/**
 ISAconfigurator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAconfigurator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 “The contents of this file are subject to the CPAL version 1.0 (the “License”); you may not use this file except
 in compliance with the License. You may obtain a copy of the License at http://isa-tools.org/licenses/ISAconfigurator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections 14 and 15 have been added to cover use of software over
 a computer network and provide for limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 consistent with Exhibit B.

 Software distributed under the License is distributed on an “AS IS” basis, WITHOUT WARRANTY OF ANY KIND, either express
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

package org.isatools.isacreatorconfigurator.configui;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: eamonnmaguire
 * @date May 5, 2009
 */


public class ProcessStandardFieldsXML extends DefaultHandler {

    private static final Logger log = Logger.getLogger(ProcessStandardFieldsXML.class.getName());

    private Fields fields;

    private int currentReadStatus = -1;

    private Field currentField = null;
    private List<Location> currentFieldLocations;

    private static final int READING_FIELD = 1;
    private static final int READING_NAME = 2;
    private static final int READING_APPEARS_IN = 3;
    private static final int LOCATION = 4;
    private static final int READING_IS_STUDY_DEFAULT = 5;
    private static final int READING_IS_ASSAY_DEFAULT = 6;
    private static final int READING_DEFAULT_WIZARD_TEMPLATE = 7;
    private static final int IGNORE_READ = 7;

    public Fields parseFile(URL fileLoc) {
        fields = new Fields();
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser sp = spf.newSAXParser();
            sp.parse(fileLoc.toString(), this);
            return fields;
        } catch (SAXException se) {
            log.error("SAX Exception Caught: \n Details are: \n" +
                    se.getMessage());
        } catch (ParserConfigurationException pce) {
            log.error(
                    "Parser Configuration Exception Caught: \n Details are: \n" +
                            pce.getMessage());
        } catch (IOException ioe) {
            log.error("File not found: \n Details are: \n" +
                    ioe.getMessage());
        }
        return null;
    }


    /**
     * Process start element to determine if information about tables or mappings is being read in.
     *
     * @param namespaceURI *
     * @param localName    *
     * @param qName        - qualifier name for XML tag.
     * @param atts         - Attributes of element.
     */
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes atts) {
        if (qName.equalsIgnoreCase("field")) {
            currentReadStatus = READING_FIELD;
            currentField = new Field();
        } else if (qName.equalsIgnoreCase("name")) {
            currentReadStatus = READING_NAME;
        } else if (qName.equalsIgnoreCase("appearsIn")) {
            currentFieldLocations = new ArrayList<Location>();
            currentReadStatus = READING_APPEARS_IN;
        } else if (qName.equalsIgnoreCase("location")) {
            currentReadStatus = LOCATION;
        } else if (qName.equalsIgnoreCase("studyDefault")) {
            currentReadStatus = READING_IS_STUDY_DEFAULT;
        } else if (qName.equalsIgnoreCase("assayDefault")) {
            currentReadStatus = READING_IS_ASSAY_DEFAULT;
        } else if (qName.equalsIgnoreCase("defaultWizardTemplate")) {
            currentReadStatus = READING_DEFAULT_WIZARD_TEMPLATE;
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName,
                           String qName) throws SAXException {
        if (qName.equalsIgnoreCase("field")) {
            fields.addField(currentField);
            currentReadStatus = IGNORE_READ;
        } else if (qName.equalsIgnoreCase("appearsIn")) {
            currentField.setAppearsIn(currentFieldLocations);
        }
    }

    public void characters(char[] ch, int start, int length) {
        String data = new String(ch, start, length).trim();
        if (!data.equals("")) {
            if (currentReadStatus == READING_NAME) {
                currentField.setName(data);
            } else if (currentReadStatus == LOCATION) {
                currentFieldLocations.add(Location.resolveLocationIdentifier(data));
            } else if (currentReadStatus == READING_IS_STUDY_DEFAULT) {
                currentField.setStudyDefault(Boolean.valueOf(data));
            } else if (currentReadStatus == READING_IS_ASSAY_DEFAULT) {
                currentField.setAssayDefault(Boolean.valueOf(data));
            } else if (currentReadStatus == READING_DEFAULT_WIZARD_TEMPLATE) {
                currentField.setDefaultWizardTemplate(data);
            }
            currentReadStatus = IGNORE_READ;
        }
    }
}
