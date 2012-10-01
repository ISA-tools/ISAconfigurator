package org.isatools.isacreatorconfigurator.configui;

import org.isatools.isacreator.common.ReOrderableJList;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.MappingObject;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 23/06/2011
 *         Time: 18:41
 */
public class CustomReOrderableListCellRenderer extends JLabel implements ListCellRenderer {

    static {
        ResourceInjector.get("configuration-validation-package.style").load(
                CustomReOrderableListCellRenderer.class.getResource("/dependency-injections/configuration-validation-package.properties"));
    }

    boolean isTargetCell;
    boolean isLastItem;

    @InjectedResource
    private ImageIcon valid, invalid;

    private ReOrderableJList list;

    public CustomReOrderableListCellRenderer(ReOrderableJList list) {
        ResourceInjector.get("configuration-validation-package.style").inject(this);

        this.list = list;
        setFont(UIHelper.VER_11_BOLD);
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
        isTargetCell = (value == this.list.dropTargetCell);
        isLastItem = (index == list.getModel().getSize() - 1);

        if (value instanceof SectionDisplay) {
            setBackground(isSelected ? UIHelper.LIGHT_GREEN_COLOR : UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR);
            setForeground(isSelected ? UIHelper.BG_COLOR : UIHelper.LIGHT_GREEN_COLOR);
        } else {
            setBackground(isSelected ? UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR : UIHelper.BG_COLOR);
            setForeground(UIHelper.DARK_GREEN_COLOR);
        }

        setIcon(valid);

        MappingObject currentMappingObject = ApplicationManager.getCurrentMappingObject();

        if (ApplicationManager.getFileErrors().containsKey(currentMappingObject)) {

            String indexAsString = String.valueOf(index);
            String valueAsString = value.toString();

            boolean indexContained = ApplicationManager.getFileErrors().get(currentMappingObject).containsKey(indexAsString);
            boolean fieldContained = ApplicationManager.getFileErrors().get(currentMappingObject).containsKey(valueAsString);

            if (indexContained || fieldContained) {
                setIcon(invalid);
                String key = indexContained ? indexAsString : valueAsString;
                createToolTip(key, ApplicationManager.getFileErrors().get(currentMappingObject));
            } else {
                // we are probably missing the fields which have thrown errors.
                setToolTipText("");
            }
        } else {
            setToolTipText("");
        }

        setText(value.toString());
        return this;
    }

    private void createToolTip(String key, Map<String, Set<String>> errors) {
        Set<String> messages = errors.get(key);

        StringBuilder fieldErrors = new StringBuilder();
        for (String message : messages) {
            fieldErrors.append(message).append("</br>");
        }

        setToolTipText("<html>" + fieldErrors.toString() + "</html>");
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
