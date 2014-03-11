package org.isatools.isacreatorconfigurator.configui;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.launch.ISAcreatorGUIProperties;
import org.isatools.isacreator.settings.ISAcreatorProperties;
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

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);

        Graphics2D g2d = (Graphics2D) graphics;

        g2d.setFont(UIHelper.VER_10_PLAIN);
        g2d.setColor(UIHelper.BG_COLOR);
        g2d.drawString("version ", 90, 130);
        g2d.setColor(UIHelper.LIGHT_GREEN_COLOR);
        g2d.drawString(ISAcreatorConfigurator.VERSION, 130, 130);
    }
}
