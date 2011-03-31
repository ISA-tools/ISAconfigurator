/**
 ISAconfigurator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAconfigurator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 ÒThe contents of this file are subject to the CPAL version 1.0 (the ÒLicenseÓ); you may not use this file except
 in compliance with the License. You may obtain a copy of the License at http://isa-tools.org/licenses/ISAconfigurator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections 14 and 15 have been added to cover use of software over
 a computer network and provide for limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 consistent with Exhibit B.

 Software distributed under the License is distributed on an ÒAS ISÓ basis, WITHOUT WARRANTY OF ANY KIND, either express
 or implied. See the License for the specific language governing rights and limitations under the License.

 The Original Code is ISAconfigurator.
 The Original Developer is the Initial Developer. The Initial Developer of the Original Code is the ISA Team
 (Eamonn Maguire, eamonnmag@gmail.com; Philippe Rocca-Serra, proccaserra@gmail.com; Susanna-Assunta Sansone,
 sa.sanson@gmail.com; http://www.isa-tools.org). All portions of the code written by the ISA Team are
 Copyright (c) 2007-2011 ISA Team. All Rights Reserved.

 EXHIBIT B. Attribution Information
 Attribution Copyright Notice: Copyright (c) 2008-2011 ISA Team
 Attribution Phrase: Developed by the ISA Team
 Attribution URL: http://www.isa-tools.org
 Graphic Image provided in the Covered Code as file: http://isa-tools.org/licenses/icons/poweredByISAtools.png
 Display of Attribution Information is required in Larger Works which are defined in the CPAL as a work which combines
 Covered Code or portions thereof with code not governed by the terms of the CPAL.

 Sponsors:
 The ISA Team and the ISA software suite have been funded by the EU Carcinogenomics project (http://www.carcinogenomics.eu),
 the UK BBSRC (http://www.bbsrc.ac.uk), the UK NERC-NEBC (http://nebc.nerc.ac.uk) and in part by the EU NuGO consortium
 (http://www.nugo.org/everyone).
 */

package org.isatools.isacreatorconfigurator.common;

import org.apache.log4j.Logger;
import org.isatools.isacreatorconfigurator.configui.SectionDisplay;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import javax.swing.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class ReOrderableJList extends JList
        implements DragSourceListener, DropTargetListener, DragGestureListener {

    private static Logger log = Logger.getLogger(ReOrderableJList.class.getName());

    static DataFlavor localObjectFlavor;

    static {
        try {
            localObjectFlavor =
                    new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType);
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    static DataFlavor[] supportedFlavors = {localObjectFlavor};
    DragSource dragSource;
    DropTarget dropTarget;
    Object dropTargetCell;
    int draggedIndex = -1;

    public ReOrderableJList(DefaultListModel model) {
        super();

        setCellRenderer(new ReorderableListCellRenderer());
        setModel(model);
        dragSource = new DragSource();
        DragGestureRecognizer dgr =
                dragSource.createDefaultDragGestureRecognizer(this,
                        DnDConstants.ACTION_MOVE,
                        this);
        dropTarget = new DropTarget(this, this);
    }

    // DragGestureListener
    public void dragGestureRecognized(DragGestureEvent dge) {
        try {

            // find object at this x,y
            Point clickPoint = dge.getDragOrigin();
            int index = locationToIndex(clickPoint);
            if (index == -1)
                return;

            Transferable trans;

            if (getSelectedValues().length > 1) {
                List<Object> target = new ArrayList<Object>();

                target.addAll(Arrays.asList(getSelectedValues()));
                trans = new RJLTransferable(target);


            } else {
                Object target = getModel().getElementAt(index);
                trans = new RJLTransferable(target);
            }


            draggedIndex = index;
            dragSource.startDrag(dge, Cursor.getDefaultCursor(),
                    trans, this);
        } catch (InvalidDnDOperationException e) {
            log.info(e.getMessage());
        }
    }

    // DragSourceListener events
    public void dragDropEnd(DragSourceDropEvent dsde) {

        dropTargetCell = null;
        draggedIndex = -1;
        repaint();
    }

    public void dragEnter(DragSourceDragEvent dsde) {
    }

    public void dragExit(DragSourceEvent dse) {
    }

    public void dragOver(DragSourceDragEvent dsde) {
    }

    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

    // DropTargetListener events
    public void dragEnter(DropTargetDragEvent dtde) {
        if (dtde.getSource() != dropTarget)
            dtde.rejectDrag();
        else {
            dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
        }

    }

    public void dragExit(DropTargetEvent dte) {
    }

    public void dragOver(DropTargetDragEvent dtde) {
        // figure out which cell it's over, no drag to self
        if (dtde.getSource() != dropTarget)
            dtde.rejectDrag();
        Point dragPoint = dtde.getLocation();
        int index = locationToIndex(dragPoint);
        if (index == -1)
            dropTargetCell = null;
        else
            dropTargetCell = getModel().getElementAt(index);
        repaint();
    }

    public void drop(DropTargetDropEvent dtde) {

        if (dtde.getSource() != dropTarget) {
            dtde.rejectDrop();
            return;
        }
        Point dropPoint = dtde.getLocation();
        int index = locationToIndex(dropPoint);

        boolean dropped = false;
        try {
            if ((index == -1) || (index == draggedIndex)) {
                //dropped onto self
                dtde.rejectDrop();
                return;
            }
            dtde.acceptDrop(DnDConstants.ACTION_MOVE);

            Object dragged =
                    dtde.getTransferable().getTransferData(localObjectFlavor);


            // move items - note that indicies for insert will
            // change if [removed] source was before target
            boolean sourceBeforeTarget = (draggedIndex < index);

            DefaultListModel mod = (DefaultListModel) getModel();

            if (dragged instanceof List) {
                // todo check if dragged is of Instance ArrayList/List. If so, we need to iterate through them, adding the items sequentially to their new position

                List<Object> toInsert = (ArrayList<Object>) dragged;

                int startIndex = draggedIndex;
                int dropIndex = index;

                if (sourceBeforeTarget) {
                    // we need to perform some specialised behaviour

                    // need to remove the elements from the list from the last element selected in the drag to the first.
                    // First to last would cause a conflict in indexes.
                    for (int toRemove = startIndex + toInsert.size() - 1; toRemove >= startIndex; toRemove--) {
                        mod.remove(toRemove);
                    }

                    // we have just removed items from the list, so we need to compensate for this removal by
                    // modifying the dropIndex through subtraction of the items to be entered in their new positions.
                    dropIndex = dropIndex - toInsert.size();

                    // now add all the elements to be added in their rightful place.
                    for (Object draggedObject : toInsert) {
                        mod.add(dropIndex, draggedObject);
                        dropIndex++;
                    }
                } else {

                    for (Object draggedObject : toInsert) {
                        mod.remove(startIndex);
                        mod.add((sourceBeforeTarget ? dropIndex - 1 : dropIndex), draggedObject);
                        startIndex++;
                        dropIndex++;
                    }
                }

            } else {
                mod.remove(draggedIndex);
                mod.add((sourceBeforeTarget ? index - 1 : index), dragged);
            }
            dropped = true;
            firePropertyChange("orderChanged", "", "changedOrder");
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        dtde.dropComplete(dropped);
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    class RJLTransferable implements Transferable {
        Object object;

        public RJLTransferable(Object o) {
            object = o;
        }

        public Object getTransferData(DataFlavor df)
                throws UnsupportedFlavorException, IOException {
            if (isDataFlavorSupported(df))
                return object;
            else
                throw new UnsupportedFlavorException(df);
        }

        public boolean isDataFlavorSupported(DataFlavor df) {
            return (df.equals(localObjectFlavor));
        }

        public DataFlavor[] getTransferDataFlavors() {
            return supportedFlavors;
        }
    }

    // custom renderer
    class ReorderableListCellRenderer
            extends JLabel implements ListCellRenderer {
        boolean isTargetCell;
        boolean isLastItem;

        public ReorderableListCellRenderer() {
            setFont(UIHelper.VER_11_BOLD);
            setOpaque(true);
        }

        public Component getListCellRendererComponent(JList list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean hasFocus) {
            isTargetCell = (value == dropTargetCell);
            isLastItem = (index == list.getModel().getSize() - 1);

            if (value instanceof SectionDisplay) {
                setBackground(isSelected ? UIHelper.LIGHT_GREEN_COLOR : UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR);
                setForeground(isSelected ? UIHelper.BG_COLOR : UIHelper.LIGHT_GREEN_COLOR);
            } else {
                setBackground(isSelected ? UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR : UIHelper.BG_COLOR);
                setForeground(UIHelper.GREY_COLOR);

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
                g2d.drawLine (0, 0, getSize().width, 0);
            }
        }
    }
}
