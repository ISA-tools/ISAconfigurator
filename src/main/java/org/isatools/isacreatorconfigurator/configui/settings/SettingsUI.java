package org.isatools.isacreatorconfigurator.configui.settings;

import org.isatools.errorreporter.ui.borders.RoundedBorder;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.settings.HTTPProxySettings;
import org.isatools.isacreator.utils.PropertyFileIO;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    @InjectedResource
    private ImageIcon closeIcon, closeIconOver, okIcon, okIconOver;

    public SettingsUI() {
        ResourceInjector.get("config-ui-package.style").inject(this);
        createGUI();
    }

    public void createGUI() {
        setBackground(UIHelper.BG_COLOR);
        setSize(new Dimension(500, 400));
        setUndecorated(true);
        setLayout(new BorderLayout());
        ((JComponent) getContentPane()).setBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 4));
        createContentPanel();
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
    }

    private void createContentPanel() {
        final HTTPProxySettings proxySettingsPane = new HTTPProxySettings(settings);

        HUDTitleBar titleBar = new HUDTitleBar(null, null);
        add(titleBar, BorderLayout.NORTH);


        titleBar.installListeners();

        add(proxySettingsPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        final JLabel cancelButton = new JLabel(closeIcon);
        cancelButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                cancelButton.setIcon(closeIcon);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                cancelButton.setIcon(closeIconOver);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                cancelButton.setIcon(closeIcon);
                closeWindow();
            }
        });

        final JLabel okButton = new JLabel(okIcon);
        okButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                okButton.setIcon(okIcon);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                okButton.setIcon(okIconOver);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                okButton.setIcon(okIcon);
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
