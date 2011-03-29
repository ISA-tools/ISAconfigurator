package org.isatools.isacreatorconfigurator.effects.borders;

import org.isatools.isacreatorconfigurator.common.UIHelper;

import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 29/03/2011
 *         Time: 11:05
 */
public class RoundedBorderFilled extends AbstractBorder {

    private Color borderColor;
    private int curveRadius;

    public RoundedBorderFilled() {
        this(UIHelper.GREY_COLOR, 8);
    }

    public RoundedBorderFilled(Color borderColor, int curveRadius) {
        this.borderColor = borderColor;
        this.curveRadius = curveRadius;
    }

    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = insets.right = insets.bottom = 12;
        return insets;
    }

    public void paintBorder(Component c, Graphics g, int x, int y,
                            int width, int height) {

        Graphics2D g2d =  (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.translate(x, y);
        g2d.setColor(borderColor);
//        g2d.drawRoundRect(0, 0, width - 2, height - 2, curveRadius, curveRadius);
        g2d.fillRoundRect(0, 0, width - curveRadius, height - curveRadius, curveRadius, curveRadius-1);
        g2d.translate(-x, -y);
    }

    public boolean isBorderOpaque() {
        return true;
    }
}
