package org.isatools.isacreatorconfigurator.configui.notifications;

import org.isatools.isacreator.common.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 08/10/2012
 *         Time: 11:34
 */
public class NotificationWindow extends JFrame {

    private String title;
    private String notificationText;

    public NotificationWindow(String title, String notificationText) {
        this.title = title;
        this.notificationText = notificationText;
        createGUI();
        setVisible(true);
    }

    public void createGUI() {
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
        setSize(new Dimension(250, 100));
        setUndecorated(true);
        setLocationRelativeTo(null);
        ((JComponent)getContentPane()).setBorder(new EmptyBorder(2,2,2,2));
        createTopPanel();
        createContentPanel();
        createBottomPanel();
    }

    private void createTopPanel() {
        Box topPanel = Box.createHorizontalBox();
        topPanel.setBackground(UIHelper.RED_COLOR);
        topPanel.setOpaque(true);
        topPanel.add(UIHelper.createLabel(title, UIHelper.VER_11_BOLD, UIHelper.BG_COLOR));
        add(topPanel, BorderLayout.NORTH);
    }

    private void createContentPanel() {
        JLabel content = UIHelper.createLabel(notificationText, UIHelper.VER_10_PLAIN, UIHelper.GREY_COLOR);
        add(UIHelper.wrapComponentInPanel(content), BorderLayout.CENTER);
    }

    private void createBottomPanel() {
        final JLabel close = UIHelper.createLabel("Close", UIHelper.VER_10_BOLD, UIHelper.RED_COLOR);
        final Color paleGreycolor = new Color(241, 242, 241);
        close.setBackground(paleGreycolor);
        close.setSize(new Dimension(60, 20));
        close.setOpaque(true);
        
        close.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                close.setBackground(UIHelper.RED_COLOR);
                close.setForeground(UIHelper.BG_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                setCursor(Cursor.getDefaultCursor());
                close.setBackground(paleGreycolor);
                close.setForeground(UIHelper.RED_COLOR);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                setCursor(Cursor.getDefaultCursor());
                setVisible(false);
                dispose();
            }
        });

        Box closeBox = Box.createHorizontalBox();
        closeBox.add(Box.createHorizontalStrut(100));
        closeBox.add(close);
        closeBox.add(Box.createHorizontalStrut(70));

        add(closeBox, BorderLayout.SOUTH);
    }

}
