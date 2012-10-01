package org.isatools.isacreatorconfigurator.validation;

import org.isatools.isacreator.configuration.Configuration;
import org.isatools.isacreator.configuration.TableConfiguration;
import org.isatools.isacreator.configuration.io.ConfigXMLParser;
import org.isatools.isacreator.configuration.io.ConfigurationLoadingSource;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ValidatorTest {


    private static

    Configuration validConfigurations, invalidConfigurations;

    @Before
    public void setUp() throws Exception {

        String baseDir = System.getProperty("basedir");
        String validDir = baseDir + "/target/test-classes/configurations/valid/";
        String invalidDir = baseDir + "/target/test-classes/configurations/invalid/";

        // load valid configurations
        ConfigXMLParser validParser = new ConfigXMLParser(ConfigurationLoadingSource.ISACONFIGURATOR, validDir);
        validParser.loadConfiguration();
        validConfigurations = new Configuration(validParser.getMappings());
        for (TableReferenceObject tc : validParser.getTables()) {
            validConfigurations.addTableObject(tc.getTableFields());
        }

        // load invalid configurations
        ConfigXMLParser invalidParser = new ConfigXMLParser(ConfigurationLoadingSource.ISACONFIGURATOR, invalidDir);
        invalidParser.loadConfiguration();
        invalidConfigurations = new Configuration(invalidParser.getMappings());
        for (TableReferenceObject tc : invalidParser.getTables()) {
            invalidConfigurations.addTableObject(tc.getTableFields());
        }
    }

    @Test
    public void testValidationOnValidFiles() {
        Validator validator = new Validator();
        for (TableConfiguration tco : validConfigurations.getTableData()) {
            String tableType = tco.getMappingObject().getAssayType().equals("")
                    ? tco.getMappingObject().getTableType() : tco.getMappingObject().getAssayType();

            if (tco.getTableName().equalsIgnoreCase("investigation")) {
                tableType = "investigation";
            }
            validator.validate(tco.getTableName(), tableType, tco.getFields());
        }
        System.out.println(validator.getReport().toString());

        assertTrue("Should be valid, but it isn't", validator.getReport().isValid("studySample"));
        assertTrue("Should be valid, but it isn't", validator.getReport().isValid("transcription_micro"));
        assertTrue("Should be valid, but it isn't", validator.getReport().isValid());

        System.out.println("________ ended testValidationOnValidFiles() _________");
    }

    @Test
    public void testValidationOnInValidFiles() {
        Validator validator = new Validator();

        for (TableConfiguration tco : invalidConfigurations.getTableData()) {
            String tableType = tco.getMappingObject().getAssayType().equals("")
                    ? tco.getMappingObject().getTableType() : tco.getMappingObject().getAssayType();

            if (tco.getTableName().equalsIgnoreCase("investigation")) {
                tableType = "investigation";
            }

            validator.validate(tco.getTableName(), tableType, tco.getFields());
        }

        System.out.println(validator.getReport().toString());

        assertTrue("Should be invalid, but it isn't", !validator.getReport().isValid("studySample"));
        assertTrue("Should be invalid, but it isn't", !validator.getReport().isValid("transcription_micro"));
        assertTrue("Should be invalid, but it isn't", !validator.getReport().isValid());


        System.out.println("________ ended testValidationOnInValidFiles() _________");
    }
}
