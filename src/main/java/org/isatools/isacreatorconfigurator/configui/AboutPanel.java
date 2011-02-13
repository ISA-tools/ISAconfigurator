package org.isatools.isacreatorconfigurator.configui;

import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: Dec 14, 2010
 *         Time: 9:47:59 PM
 */
public class AboutPanel extends JPanel {

    @InjectedResource
    private ImageIcon aboutImage;

    public AboutPanel() {
        ResourceInjector.get("config-ui-package.style").inject(this);

        setPreferredSize(new Dimension(463, 301));
        setLayout(new BorderLayout());
        setOpaque(false);
        createGUI();
    }

    private void createGUI() {
        add(new JLabel(aboutImage));
    }
}
