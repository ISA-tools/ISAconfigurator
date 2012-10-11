package org.isatools.isacreatorconfigurator.configui.settings;

import org.isatools.errorreporter.ui.borders.RoundedBorder;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.settings.HTTPProxySettings;
import org.isatools.isacreator.utils.PropertyFileIO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: prs
 * Date: 04/10/2012
 * Time: 16:01
 * To change this template use File | Settings | File Templates.
 */
public class SettingsUI extends JFrame {

    static Properties settings;
    static String settingsFileName = "settings.properties";
    static {

        File settingsFile = new File("Settings" + File.separator + settingsFileName);

        settings = new Properties();
        if (settingsFile.exists()) {
            try {

                settings.load(new FileReader(settingsFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public SettingsUI() {
        createGUI();
    }

    public void createGUI() {
        setBackground(UIHelper.BG_COLOR);
        setSize(new Dimension(500, 400));
        setUndecorated(true);
        setLayout(new BorderLayout());
        ((JComponent)getContentPane()).setBorder(new RoundedBorder(UIHelper.GREY_COLOR, 4));
        createContentPanel();
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
    }

    private void createContentPanel() {
        final HTTPProxySettings proxySettingsPane = new HTTPProxySettings(settings);

        add(proxySettingsPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                closeWindow();
            }
        });

        JButton okButton = new JButton("Set Proxy");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                proxySettingsPane.updateSettings();
                closeWindow();
                PropertyFileIO.setProxy(settings);

                PropertyFileIO.saveProperties(settings, settingsFileName);
            }
        });

        buttonPanel.add(cancelButton, BorderLayout.WEST);
        buttonPanel.add(okButton, BorderLayout.EAST);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void closeWindow() {
        setVisible(false);
        dispose();
    }


    public static void main(String[] args) {
        new SettingsUI();
    }
}
