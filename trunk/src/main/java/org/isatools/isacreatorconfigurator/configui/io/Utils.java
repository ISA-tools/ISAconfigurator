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

package org.isatools.isacreatorconfigurator.configui.io;

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.isatools.isacreatorconfigurator.common.MappingObject;
import org.isatools.isacreatorconfigurator.configdefinition.Configuration;
import org.isatools.isacreatorconfigurator.configdefinition.TableConfiguration;
import org.isatools.isacreatorconfigurator.configdefinition.TableFieldObject;
import org.isatools.isacreatorconfigurator.configui.*;
import org.isatools.isacreatorconfigurator.xml.FieldXMLCreator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: eamonnmaguire
 * @date Aug 27, 2009
 */


public class Utils {

    /**
     * Saves current session to a user defined location.
     * throws IOException - When problem occures on save file attempt
     *
     * @throws DataNotCompleteException - When fields are missing.
     * @throws java.io.IOException      /
     */
    public static String saveSession(Map<MappingObject, List<Display>> tableFields, File toSave) throws IOException, DataNotCompleteException, InvalidFieldOrderException {


        String path = toSave.getPath();

        if (!ConfigurationFileFilter.getExtension(toSave).equals("tcf")) {
            if (path.contains(".")) {
                path = path.substring(0, path.lastIndexOf("."));
            }

            path += ".tcf";
            toSave = new File(path);
        }

        FileOutputStream fos = new FileOutputStream(toSave);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(Utils.createTableConfigurationObject(tableFields));
        oos.close();
        fos.close();

        String message = "Files have been saved in ";

        if (path.equals("")) {
            message += "this programs directory";
        } else {
            message += path;
        }

        return message;

    }


    public static Configuration createTableConfigurationObject(Map<MappingObject, List<Display>> tableFields)
            throws DataNotCompleteException, InvalidFieldOrderException {

        List<MappingObject> mappings = new ArrayList<MappingObject>();
        mappings.addAll(tableFields.keySet());
        Configuration tco = new Configuration(mappings);

        for (MappingObject mo : tableFields.keySet()) {

            List<Display> elements = tableFields.get(mo);

            List<TableFieldObject> fields = new ArrayList<TableFieldObject>();

            int count = 0;
            for (Display ed : elements) {
                if (ed instanceof FieldElement) {
                    FieldElement fed = (FieldElement) ed;
                    TableFieldObject tfo = fed.getFieldDetails();
                    tfo.setColNo(count);
                    fields.add(fed.getFieldDetails());

                }
                count++;
            }

            TableConfiguration to = new TableConfiguration(mo, fields, createTableStructure(tableFields, mo));
            tco.addTableObject(to);
        }
        return tco;
    }

    public static String createTableConfigurationXML(String outputDir, Map<MappingObject, List<Display>> tableFields)
            throws DataNotCompleteException, InvalidFieldOrderException, IOException {

        FieldXMLCreator fieldXMLcreator;

        for (MappingObject mo : tableFields.keySet()) {

            List<Display> elements = tableFields.get(mo);
            for (int fieldIndex = 0; fieldIndex < elements.size(); fieldIndex++) {
                fieldXMLcreator = new FieldXMLCreator(elements, mo);
                StringBuffer xmlToWrite = fieldXMLcreator.createTableXML();

                FileOutputStream fos = new FileOutputStream(outputDir + File.separator + mo.getAssayName().replace("\\s", "") + ".xml");
                PrintStream ps = new PrintStream(fos);
                ps.print(xmlToWrite);
                ps.close();
                fos.close();
            }
        }

        String message = "Files have been saved in ";

        if (outputDir.equals("")) {
            message += "this programs directory";
        } else {
            message += outputDir;
        }

        return message;
    }

    public static Map<Integer, String[]> createTableStructure(Map<MappingObject, List<Display>> tableFields, MappingObject mo) throws InvalidFieldOrderException {
        if (mo != null) {
            Map<Integer, String[]> orderedMap = new ListOrderedMap<Integer, String[]>();

            List<Display> elements = tableFields.get(mo);

            int count = 0;
            for (Display ed : elements) {
                if (ed instanceof FieldElement) {
                    FieldElement fed = (FieldElement) ed;
                    String template = (fed.getFieldDetails().getWizardTemplate() == null) ? "" : fed.getFieldDetails().getWizardTemplate();
                    orderedMap.put(count, new String[]{fed.toString(), template});
                } else {
                    orderedMap.put(count, new String[]{ed.toString(), ""});
                }
                count++;
            }

            FieldOrderChecker foc = new FieldOrderChecker();

            if (foc.haveFieldsIncorrectOrder(elements, mo.getAssayName())) {
                throw new InvalidFieldOrderException(foc.getReport());
            }

            return orderedMap;
        }

        return null;
    }
}
