package org.isatools.isacreatorconfigurator.configui.mappingviewer;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreatorconfigurator.common.MappingObject;
import org.isatools.isacreatorconfigurator.common.UIHelper;
import org.isatools.isacreatorconfigurator.configui.DataEntryPanel;
import org.isatools.isacreatorconfigurator.effects.RoundedBorder;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;


/**
 * TableMappingViewer provides the GUI to visualise the mappings of measurement and technology
 * types to a table definition.
 *
 * @author Eamonn Maguire
 */
public class TableMappingViewer extends JDialog {
    private Set<MappingObject> mappingList;
    private DefaultTableModel subTM;
    private JLabel statusInfo;
    private JTable mappings;
    private DataEntryPanel dep;
    private Object[] columnNames;

    @InjectedResource
    private ImageIcon cancelIcon, updateIcon, headerIcon;

    public TableMappingViewer(DataEntryPanel dep,
                              Set<MappingObject> mappingList) {
        ResourceInjector.get("config-ui-package.style").inject(this);

        this.dep = dep;
        this.mappingList = mappingList;


        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
        statusInfo = new JLabel();
        createGUI();
        pack();

    }

    /**
     * Create the GUI
     */
    private void createGUI() {
        columnNames = new Object[]{
                "table type", "measurement type", "technology type",
                "table"
        };

        subTM = new DefaultTableModel() {
            public boolean isCellEditable(int row, int col) {
                return !getValueAt(row, 0).toString().equals("study sample") && !(col == 0) && !(col == 3);
            }
        };

        subTM.setColumnIdentifiers(columnNames);
        subTM.setDataVector(updateItems(convertSetToList(mappingList)), columnNames);

        mappings = new JTable(subTM);
        mappings.setGridColor(UIHelper.LIGHT_GREEN_COLOR);

        try {
            mappings.setDefaultRenderer(Class.forName("java.lang.Object"),
                    new MappingTableCellRenderer(UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, null));
        } catch (ClassNotFoundException e) {
            // ignore
        }

        mappings.setShowGrid(true);

        JTableHeader header = mappings.getTableHeader();
        setHeaderProperties(header, new MappingTableHeaderRenderer());

        JScrollPane tableScroll = new JScrollPane(mappings,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        tableScroll.setPreferredSize(new Dimension(500, 150));
        tableScroll.setBorder(new TitledBorder(
                new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 4),
                "table mappings", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR));

        IAppWidgetFactory.makeIAppScrollPane(tableScroll);

        Box southPanel = Box.createVerticalBox();

        // create button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.setBackground(UIHelper.BG_COLOR);

        JLabel cancel = new JLabel(cancelIcon, JLabel.LEFT);
        cancel.setOpaque(false);
        cancel.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                dep.getApplicationContainer().hideSheet();
            }

        });

        buttonPanel.add(cancel);

        JLabel update = new JLabel(updateIcon, JLabel.RIGHT);
        update.setOpaque(false);
        update.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                saveUpdates();
                dep.getApplicationContainer().hideSheet();
            }
        });

        buttonPanel.add(update);

        southPanel.add(buttonPanel);
        southPanel.add(statusInfo);

        JPanel headerPanel = new JPanel(new GridLayout(1, 1));
        headerPanel.setBackground(UIHelper.BG_COLOR);

        JLabel topHeader = new JLabel(headerIcon, JLabel.RIGHT);
        topHeader.setOpaque(false);

        headerPanel.add(topHeader);

        add(headerPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    public List<MappingObject> convertSetToList(Set<MappingObject> toConvert) {
        List<MappingObject> converted = new ArrayList<MappingObject>();

        for (MappingObject mo : toConvert) {
            converted.add(mo);
        }

        return converted;
    }

    /**
     * Refresh the mapping list with a new set of mappings.
     *
     * @param mappingList - the List of mappings to use for the update.
     */
    public void refreshList(List<MappingObject> mappingList) {
        subTM.setDataVector(updateItems(mappingList), columnNames);
        subTM.fireTableDataChanged();
    }

    /**
     * Save the updates applied to the Mapping objects. Called whenever the update button is clicked
     * in the GUI.
     */
    private void saveUpdates() {
        for (int row = 0; row < mappings.getRowCount(); row++) {
            String[] newVals = new String[3];

            for (int col = 1; col < mappings.getColumnCount(); col++) {
                newVals[col - 1] = subTM.getValueAt(row, col).toString();
            }

            for (MappingObject mo : dep.getTableTypeMapping()) {
                if (mo.getAssayName().equals(newVals[2])) {
                    if (!newVals[0].equals("") && !newVals[1].equals("")) {
                        mo.setMeasurementEndpointType(newVals[0]);
                        mo.setTechnologyType(newVals[1]);
                    }

                    break;
                }
            }
        }
    }

    private void setHeaderProperties(JTableHeader tableHeader, TableCellRenderer renderer) {
        tableHeader.setReorderingAllowed(false);

        Enumeration<TableColumn> columns = mappings.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {

            TableColumn tc = columns.nextElement();
            tc.setHeaderRenderer(renderer);


        }
    }

    /**
     * Updates the Mappings whenever the user clicks on the update button.
     */
    private Object[][] updateItems(List<MappingObject> mappingList) {
        Object[][] data = new Object[mappingList.size()][4];

        for (int i = 0; i < mappingList.size(); i++) {
            data[i][0] = mappingList.get(i).getTableType();
            data[i][1] = mappingList.get(i).getMeasurementEndpointType();
            data[i][2] = mappingList.get(i).getTechnologyType();
            data[i][3] = mappingList.get(i).getAssayName();
        }

        return data;
    }
}
