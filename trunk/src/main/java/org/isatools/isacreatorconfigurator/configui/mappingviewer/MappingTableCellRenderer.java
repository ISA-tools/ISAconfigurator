package org.isatools.isacreatorconfigurator.configui.mappingviewer;

import org.isatools.isacreatorconfigurator.common.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * MappingTableCellRenderer
 *
 * @author eamonnmaguire
 * @date Apr 9, 2010
 */


public class MappingTableCellRenderer extends DefaultTableCellRenderer {
	private Font defaultFont;
	private Color defaultFontColor;
	private Color bgColor;


	/**
	 * Creates a SharpCellRenderer.
	 */
	public MappingTableCellRenderer(Font defaultFont, Color defaultFontColor, Color bgColor) {
		super();
		this.defaultFont = defaultFont;
		this.defaultFontColor = defaultFontColor;
		this.bgColor = bgColor;
	}


	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		if (bgColor != null) {
			super.setBackground(bgColor);
			super.setForeground(defaultFontColor);
		} else {
			if (hasFocus && table.isCellEditable(row, column)) {
				super.setForeground(UIHelper.BG_COLOR);
				super.setBackground(new Color(0, 104, 56, 175));
			} else {
				super.setForeground(defaultFontColor);
				super.setBackground(row % 2 == 1 ? new Color(141, 198, 63, 40) : UIHelper.BG_COLOR);
			}
		}

		setFont(defaultFont);
		setText(value == null ? "" : value.toString());

		return this;
	}
}