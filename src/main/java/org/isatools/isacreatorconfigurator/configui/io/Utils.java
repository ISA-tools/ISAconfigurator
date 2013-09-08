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

package org.isatools.isacreatorconfigurator.configui.io;

import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.isatools.isacreator.configuration.*;
import org.isatools.isacreatorconfigurator.configui.*;
import org.isatools.isacreatorconfigurator.xml.FieldXMLCreator;
import org.isatools.isatab.configurator.schema.UnitFieldType;
import org.isatools.isatab.configurator.schema.impl.UnitFieldTypeImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import static org.apache.poi.ss.util.CellReference.convertNumToColString;


public class Utils {

    public static String utf8CharacterEncoding = "utf-8";

    public static Configuration createTableConfigurationObject(Map<MappingObject, List<Display>> tableFields)
            throws DataNotCompleteException, InvalidFieldOrderException {

        List<MappingObject> mappings = new ArrayList<MappingObject>();

        UnitFieldType unit = (UnitFieldTypeImpl)new Object();
        unit.getDescription();
        mappings.addAll(tableFields.keySet());
        Configuration tco = new Configuration(mappings);

        for (MappingObject mo : tableFields.keySet()) {

            List<Display> elements = tableFields.get(mo);

            List<FieldObject> fields = new ArrayList<FieldObject>();

            int count = 0;
            for (Display ed : elements) {
                if (ed instanceof FieldElement) {
                    FieldElement fed = (FieldElement) ed;
                    FieldObject tfo = fed.getFieldDetails();
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

                PrintStream ps = new PrintStream(fos, true, utf8CharacterEncoding);
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


    public static String createTableConfigurationEXL(String outputDir, Map<MappingObject, List<Display>> tableFields)
            throws DataNotCompleteException, InvalidFieldOrderException, IOException {

        String excelFileName = "ISA-config-template.xlsx";
        FileOutputStream fos = new FileOutputStream(outputDir + File.separator + excelFileName);

        String tableName = "";

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet hiddenSheet = workbook.createSheet("hiddenCV");

        Map<String, List<String>> nodups = new HashMap<String, List<String>>();
        XSSFSheet ontologyRestriction = workbook.createSheet("Restrictions");
        XSSFRow ontorow0 = ontologyRestriction.createRow((short) 0);

        ontorow0.createCell(0).setCellValue("Column Name");
        ontorow0.createCell(1).setCellValue("Ontology");
        ontorow0.createCell(2).setCellValue("Branch");
        ontorow0.createCell(3).setCellValue("Version");


        CreationHelper factory = workbook.getCreationHelper();

        //  int counting=0;
        //  int ontocounter=0;
        int lastposition = 0;


        for (MappingObject mo : tableFields.keySet()) {

            tableName = mo.getAssayName().replace("\\s", "");

            List<Display> elements = tableFields.get(mo);

            System.out.println("creating worksheet: " + tableName);

            //we create a table with 50 records by default for anything that is not an investigation file
            if (!tableName.contains("investigation")) {

                XSSFSheet tableSheet = workbook.createSheet(tableName);
                Drawing drawing = tableSheet.createDrawingPatriarch();
                CellStyle style = workbook.createCellStyle();
                XSSFRow rowAtIndex;

                //we create 51 rows by default for each table
                for (int index = 0; index <= 50; index++) {
                    rowAtIndex = tableSheet.createRow((short) index);
                }

                //the first row is the header we need to build from the configuration declaration
                XSSFRow header = tableSheet.getRow(0);

                //we now iterated through the element found in the xml table configuration
                for (int fieldIndex = 0; fieldIndex < elements.size(); fieldIndex++) {

                    if (elements.get(fieldIndex).getFieldDetails() != null) {

                        if (elements.get(fieldIndex).getFieldDetails().isRequired()) {

                            XSSFCell cell = header.createCell(fieldIndex);
                            Font font = workbook.createFont();
                            font.setBoldweight(Font.BOLDWEIGHT_BOLD);

                            style.setFont(font);
                            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
                            style.setFillPattern(CellStyle.SOLID_FOREGROUND);
                            font.setColor(IndexedColors.RED.index);
                            cell.setCellStyle(style);
                            //create the header field by setting to FieldName as Cell name
                            cell.setCellValue(elements.get(fieldIndex).getFieldDetails().getFieldName());

                            //using the ISA field description to create a Comment attached to the set
                            ClientAnchor anchor = factory.createClientAnchor();
                            Comment comment = drawing.createCellComment(anchor);
                            RichTextString rts = factory.createRichTextString(elements.get(fieldIndex).getFieldDetails().getDescription());
                            comment.setString(rts);
                            cell.setCellComment(comment);
                            tableSheet.autoSizeColumn(fieldIndex);

                        } else {
                            XSSFCell cell = header.createCell(fieldIndex);
                            Font font = workbook.createFont();
                            font.setBoldweight(Font.BOLDWEIGHT_BOLD);

                            style.setFont(font);
                            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
                            style.setFillPattern(CellStyle.SOLID_FOREGROUND);
                            font.setColor(IndexedColors.BLACK.index);
                            cell.setCellStyle(style);
                            //create the header field by setting to FieldName as Cell name
                            cell.setCellValue(elements.get(fieldIndex).getFieldDetails().getFieldName());
                            //using the ISA field description to create a Comment attached to the set
                            ClientAnchor anchor = factory.createClientAnchor();
                            Comment comment = drawing.createCellComment(anchor);
                            RichTextString rts = factory.createRichTextString(elements.get(fieldIndex).getFieldDetails().getDescription());
                            comment.setString(rts);
                            cell.setCellComment(comment);
                            tableSheet.autoSizeColumn(fieldIndex);
                        }

                        //checking if the field requires controled values, i.e ISA datatype is List
                        if (elements.get(fieldIndex).getFieldDetails().getDatatype() == DataTypes.LIST) {

                            //create a hidden spreadsheet and named range with the list of val
                            //counting++; //incrementing the counter defining the position where to start the new namedrange in the hidden spreadsheet

                            //obtain the name of the ISA fields and extracting key information needed to create a unique name for the Named Range to be
                            String rangeName = elements.get(fieldIndex).getFieldDetails().getFieldName().replace("'", "").replace(" ", "").replace("Comment[", "").replace("ParameterValue[", "").replace("Characteristics[", "").replace("]", "").replace("(", "").replace(")", "");

                            //getting all the values allowed by the List Field
                            String[] fieldValues = elements.get(fieldIndex).getFieldDetails().getFieldList();

                            //System.out.println("CV : "+elements.get(fieldIndex).getFieldDetails().getFieldName()+ " values: "  + Arrays.asList(fieldValues).toString()+ "size :" +fieldValues.length);

                            //iterating through the values and creating a cell for each
                            for (int j = 0; j < fieldValues.length; j++) {
                                hiddenSheet.createRow(lastposition + j).createCell(0).setCellValue(fieldValues[j]);
                            }

                            Name namedCell = workbook.createName();

                            workbook.getNumberOfNames();

                            int k = 0;
                            int position = 0;

                            //this is to handle ISA Fields sharing the same name (in different assays)
                            //namedRanges in Excel must be unique

                            while (k < workbook.getNumberOfNames()) {  //we can the total number of field to type list we have found so far.

                                //something already exists...
                                if (workbook.getNameAt(k).equals(rangeName)) {
                                    // namedCell.setNameName(workbook.getNameAt(k).toString());
                                    //no need to go further, we exit here and set the parameter position to use the value
                                    position = k;
                                    k = -1;
                                } else {
                                    k++;
                                }
                            }

                            if (k > 0) {   //this means this field already existed list of that type
                                //we name the new cell after it
                                namedCell.setNameName(rangeName + k);
                                System.out.println("Name Name: " + namedCell.getNameName());
                            } else {      //there is already one, so we just point back to it using the position parameter
                                namedCell.setNameName(workbook.getNameAt(k).toString());       //workbook.getNameAt(position).toString()
                                System.out.println("Name Name: " + namedCell.getNameName());
                            }

                            int start = 0;
                            int end = 0;
                            start = lastposition + 1;
                            System.out.println("start: + " + start);
                            end = lastposition + fieldValues.length;
                            System.out.println("end: + " + end);

//                                    String reference ="hiddenCV"+"!"+convertNumToColString(0)+start+":"+ convertNumToColString(0)+end;
                            String reference = "hiddenCV" + "!$" + convertNumToColString(0) + "$" + start + ":$" + convertNumToColString(0) + "$" + end;
                            namedCell.setRefersToFormula(reference);

                            start = 0;
                            end = 0;
                            DataValidationHelper validationHelper = new XSSFDataValidationHelper(tableSheet);
                            DataValidationConstraint constraint = validationHelper.createFormulaListConstraint(reference);
                            CellRangeAddressList addressList = new CellRangeAddressList(1, 50, fieldIndex, fieldIndex);

                            System.out.println("field index: " + fieldIndex);
                            DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);

                            tableSheet.addValidationData(dataValidation);

                            lastposition = lastposition + fieldValues.length;
                            System.out.println("lastposition: + " + lastposition);
                            System.out.println("reference: " + reference);
                        }

//                                //TODO: reformat date but this is pain in Excel
//                                if (elements.get(fieldIndex).getFieldDetails().getDatatype()== DataTypes.DATE) {
//                                    //do something
//                                }

                        //  If a default value has been specified in the ISAconfiguration, we set it in the Excel spreadsheet
                        if (elements.get(fieldIndex).getFieldDetails().getDefaultVal() != null) {
                            for (int i=1; i<51; i++) {
                            rowAtIndex = tableSheet.getRow(i);
                            XSSFCell cellThere = rowAtIndex.createCell(fieldIndex);
                            cellThere.setCellValue(elements.get(fieldIndex).getFieldDetails().getDefaultVal());
                            }
                        }


                        if (elements.get(fieldIndex).getFieldDetails().getDatatype() == DataTypes.ONTOLOGY_TERM) {
                            int count = elements.get(fieldIndex).getFieldDetails().getRecommmendedOntologySource().values().size();
                            Collection<RecommendedOntology> myList = elements.get(fieldIndex).getFieldDetails().getRecommmendedOntologySource().values();
                            for (RecommendedOntology recommendedOntology : myList) {
                                System.out.println("ONTOLOGY :" + recommendedOntology.getOntology());
                                try {
                                    if (recommendedOntology.getOntology() != null) {
                                        ArrayList<String> ontoAttributes = new ArrayList<String>();
                                        ontoAttributes.add(recommendedOntology.getOntology().getOntologyID());
                                        ontoAttributes.add(recommendedOntology.getOntology().getOntologyVersion());
                                        //  ontocounter++;
//                                              XSSFRow ontoRowj = ontologyRestriction.createRow(ontocounter);
//                                              ontoRowj.createCell(0).setCellValue(elements.get(fieldIndex).getFieldDetails().getFieldName());
//                                              ontoRowj.createCell(1).setCellValue(recommendedOntology.getOntology().getOntologyID());
//                                              ontoRowj.createCell(3).setCellValue(recommendedOntology.getOntology().getOntologyVersion());

                                        if (recommendedOntology.getBranchToSearchUnder() != null) {
                                            System.out.println("ONTOLOGY BRANCH :" + recommendedOntology.getBranchToSearchUnder());
//                                                  ontoRowj.createCell(2).setCellValue(recommendedOntology.getBranchToSearchUnder().toString());
                                            ontoAttributes.add(recommendedOntology.getBranchToSearchUnder().toString());
                                        } else {
                                            ontoAttributes.add("");
                                        }

                                        nodups.put(elements.get(fieldIndex).getFieldDetails().getFieldName(), ontoAttributes);
                                    }
                                } catch (NullPointerException npe) {
                                    System.out.println(npe);
                                }
                            }
                        }
                    }
                }
            } else {

                //we now create with the Investigation Sheet
                XSSFSheet tableSheet = workbook.createSheet(tableName);

                Drawing drawing = tableSheet.createDrawingPatriarch();

                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();

                font.setBoldweight(Font.BOLDWEIGHT_BOLD);
                style.setFont(font);

                for (int fieldIndex = 0; fieldIndex < elements.size(); fieldIndex++) {
                    XSSFRow row = tableSheet.createRow((short) fieldIndex);
                    if (elements.get(fieldIndex).getFieldDetails() != null) {
                        XSSFCell cell = row.createCell(0);
                        //create the header field by setting to FieldName as Cell name
                        cell.setCellValue(elements.get(fieldIndex).getFieldDetails().getFieldName());

                        //using the ISA field description to create a Comment attached to the set
                        ClientAnchor anchor = factory.createClientAnchor();
                        Comment comment = drawing.createCellComment(anchor);
                        RichTextString rts = factory.createRichTextString(elements.get(fieldIndex).getFieldDetails().getDescription());
                        comment.setString(rts);
                        cell.setCellComment(comment);
                        cell.setCellStyle(style);
                        tableSheet.autoSizeColumn(fieldIndex);

                        SheetConditionalFormatting sheetCF = tableSheet.getSheetConditionalFormatting();

                        //condition: if the output of the FIND function is equal to 1, then, set cell to a blue font
                        ConditionalFormattingRule rule = sheetCF.createConditionalFormattingRule("FIND(Investigation,$A$1:$A$21)>1");
                        //ConditionalFormattingRule rule = sheetCF.createConditionalFormattingRule(ComparisonOperator.) ;
                        FontFormatting font1 = rule.createFontFormatting();
                        font1.setFontStyle(false, true);
                        font1.setFontColorIndex(IndexedColors.BLUE.index);

                        CellRangeAddress[] regions = {
                                CellRangeAddress.valueOf("A1:A21")
                        };

                        sheetCF.addConditionalFormatting(regions, rule);
                    }
                }
                tableSheet.setSelected(true);
                workbook.setSheetOrder(tableName, 0);

            }
        }

        //writes the values of ontology resources used to restrict selection in ISA fields
        int compteur = 1;

        for (Map.Entry<String, List<String>> entry : nodups.entrySet()) {
            String key = entry.getKey();
            // Object value = entry.getValue();

            System.out.println("UNIQUE RESOURCE: " + key);
            XSSFRow ontoRowj = ontologyRestriction.createRow(compteur);
            ontoRowj.createCell(0).setCellValue(key);
            ontoRowj.createCell(1).setCellValue(entry.getValue().get(0));
            ontoRowj.createCell(2).setCellValue(entry.getValue().get(2));
            ontoRowj.createCell(3).setCellValue(entry.getValue().get(1));

            compteur++;

        }

        //moving support worksheet to be the rightmost sheets in the workbook.
        //if the table corresponds to the study sample table, we move it to first position
        if (tableName.toLowerCase().contains("studysample")) {
            workbook.setSheetOrder(tableName, 1);
        }
        workbook.setSheetOrder("hiddenCV", tableFields.keySet().size() + 1);
        workbook.setSheetOrder("Restrictions", tableFields.keySet().size() + 1);
        workbook.write(fos);
        fos.close();

        String message = "Files have been saved in ";

        if (outputDir.equals("")) {
            message += "this programs directory";
        } else {
            message += outputDir;
        }

        return message;
    }

}
