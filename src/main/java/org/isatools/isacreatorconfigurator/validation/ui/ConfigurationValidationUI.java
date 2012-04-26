package org.isatools.isacreatorconfigurator.validation.ui;

import com.sun.awt.AWTUtilities;
import org.isatools.errorreporter.model.ErrorLevel;
import org.isatools.errorreporter.model.ErrorMessage;
import org.isatools.errorreporter.model.FileType;
import org.isatools.errorreporter.model.ISAFileErrorReport;
import org.isatools.errorreporter.ui.ErrorReporterView;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.MappingObject;
import org.isatools.isacreator.effects.GraphicsUtils;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.isatools.isacreatorconfigurator.validation.ConfigurationValidationUtils;
import org.isatools.isacreatorconfigurator.validation.ReportType;
import org.isatools.isacreatorconfigurator.validation.ValidationReport;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 26/04/2012
 *         Time: 11:49
 */
public class ConfigurationValidationUI extends JFrame {

    static {
        ResourceInjector.get("configuration-validation-package.style").load(
                ConfigurationValidationUI.class.getResource("/dependency-injections/configuration-validation-package.properties"));
    }

    @InjectedResource
    private Image title, titleInactive;

    @InjectedResource
    private ImageIcon validationSuccess;
    
    private Set<MappingObject> mappingObjects;
    private ValidationReport report;

    public ConfigurationValidationUI(Set<MappingObject> mappingObjects, ValidationReport report) {
        this.mappingObjects = mappingObjects;
        this.report = report;
        ResourceInjector.get("configuration-validation-package.style").inject(this);
    }

    public void createGUI() {
        setTitle("Configuration Validator");
        setUndecorated(true);

        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);

        if (GraphicsUtils.isWindowTransparencySupported()) {
            AWTUtilities.setWindowOpacity(this, 0.93f);
        }

        HUDTitleBar titlePanel = new HUDTitleBar(title, titleInactive, true);

        add(titlePanel, BorderLayout.NORTH);
        titlePanel.installListeners();

        ((JComponent) getContentPane()).setBorder(new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));

        showErrorOrSuccessPanel();

        pack();
    }

    private void showErrorOrSuccessPanel() {

        List<ISAFileErrorReport> errors = new ArrayList<ISAFileErrorReport>();
        for (String fileName : report.getReports().keySet()) {

            MappingObject mappingObject = ConfigurationValidationUtils.findMappingObjectForTable(fileName, mappingObjects);

            FileType fileType = resolveFileTypeFromMappingObject(mappingObject);

            List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();

            for (ReportType reportType : report.getReports().get(fileName).keySet()) {
                for (String message : report.getReports().get(fileName).get(reportType)) {
                    errorMessages.add(new ErrorMessage(ErrorLevel.ERROR, reportType.toString() + ": " + message));
                }
            }

            if (errorMessages.size() > 0) {
                errors.add(new ISAFileErrorReport(fileName,
                        mappingObject.getTechnologyType(),
                        mappingObject.getMeasurementEndpointType(),
                        fileType, errorMessages));
            }
        }

        if (errors.size() > 0) {
            ErrorReporterView view = new ErrorReporterView(errors);
            view.setPreferredSize(new Dimension(750, 440));
            view.createGUI();
            
            add(view, BorderLayout.CENTER);
        } else {
            Container successfulValidationContainer = UIHelper.padComponentVerticalBox(70, new JLabel(validationSuccess));
            add(successfulValidationContainer, BorderLayout.CENTER);
        }
    }



    private FileType resolveFileTypeFromMappingObject(MappingObject mappingObject) {
        if (mappingObject.getTechnologyType().contains(FileType.MICROARRAY.getType())) {
            return FileType.MICROARRAY;
        } else if (mappingObject.getTechnologyType().contains(FileType.FLOW_CYTOMETRY.getType())) {
            return FileType.FLOW_CYTOMETRY;
        } else if (mappingObject.getTechnologyType().contains(FileType.MASS_SPECTROMETRY.getType()) ||
                mappingObject.getTechnologyType().contains(FileType.NMR.getType())) {
            return FileType.MASS_SPECTROMETRY;
        } else if (mappingObject.getTechnologyType().contains(FileType.SEQUENCING.getType())) {
            return FileType.SEQUENCING;
        } else if (mappingObject.getTechnologyType().contains(FileType.GEL_ELECTROPHORESIS.getType())) {
            return FileType.GEL_ELECTROPHORESIS;
        } else if (mappingObject.getTechnologyType().contains(FileType.HEMATOLOGY.getType())) {
            return FileType.HEMATOLOGY;
        } else if (mappingObject.getTechnologyType().contains(FileType.CLINICAL_CHEMISTRY.getType())) {
            return FileType.CLINICAL_CHEMISTRY;
        } else if (mappingObject.getTechnologyType().contains(FileType.HISTOLOGY.getType())) {
            return FileType.HISTOLOGY;
        } else {
            return FileType.INVESTIGATION;
        }
    }
}
