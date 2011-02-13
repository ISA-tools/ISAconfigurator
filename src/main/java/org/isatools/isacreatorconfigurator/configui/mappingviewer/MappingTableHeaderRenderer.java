package org.isatools.isacreatorconfigurator.configui.mappingviewer;

import org.isatools.isacreatorconfigurator.common.UIHelper;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * MappingTableHeaderRenderer
 *
 * @author eamonnmaguire
 * @date Apr 9, 2010
 */


public class MappingTableHeaderRenderer extends JPanel implements TableCellRenderer {

	@InjectedResource
	private ImageIcon columnIcon, endColumnIcon;

	private JLabel text;

	public MappingTableHeaderRenderer() {
		ResourceInjector.get("config-ui-package.style").inject(this);

		setLayout(new BorderLayout());
		setBackground(UIHelper.BG_COLOR);

		instantiatePanel();
	}

	private void instantiatePanel() {
		text = UIHelper.createLabel("", UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, JLabel.LEFT);
		add(text, BorderLayout.CENTER);

		add(UIHelper.wrapComponentInPanel(new JLabel(columnIcon)), BorderLayout.WEST);
		add(UIHelper.wrapComponentInPanel(new JLabel(endColumnIcon)), BorderLayout.EAST);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
												   boolean hasFocus, int rowIndex, int vColIndex) {

		text.setFont(isSelected ? UIHelper.VER_11_BOLD : UIHelper.VER_11_PLAIN);

		text.setText(value.toString());

		return this;
	}
}