package org.isatools.isacreatorconfigurator.configui;

import org.isatools.isacreator.common.ReOrderableJList;
import org.isatools.isacreator.common.UIHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 23/06/2011
 *         Time: 18:41
 */
public class CustomReOrderableListCellRenderer extends JLabel implements ListCellRenderer {
    boolean isTargetCell;
    boolean isLastItem;

    private ReOrderableJList list;

    public CustomReOrderableListCellRenderer(ReOrderableJList list) {
        this.list = list;
        setFont(UIHelper.VER_11_BOLD);
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean hasFocus) {
        isTargetCell = (value == this.list.dropTargetCell);
        isLastItem = (index == list.getModel().getSize() - 1);

        if (value instanceof SectionDisplay) {
            setBackground(isSelected ? UIHelper.LIGHT_GREEN_COLOR : UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR);
            setForeground(isSelected ? UIHelper.BG_COLOR : UIHelper.LIGHT_GREEN_COLOR);
        } else {
        setBackground(isSelected ? UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR : UIHelper.BG_COLOR);
        setForeground(UIHelper.DARK_GREEN_COLOR);

        }

        setText(value.toString());
        return this;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isTargetCell) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(UIHelper.LIGHT_GREEN_COLOR);
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(0, 0, getSize().width, 0);
        }
    }
}
