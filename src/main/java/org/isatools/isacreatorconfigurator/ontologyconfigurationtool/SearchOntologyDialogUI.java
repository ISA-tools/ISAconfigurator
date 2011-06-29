/**
 ISAconfigurator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAconfigurator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 “The contents of this file are subject to the CPAL version 1.0 (the “License”); you may not use this file except
 in compliance with the License. You may obtain a copy of the License at http://isa-tools.org/licenses/ISAconfigurator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections 14 and 15 have been added to cover use of software over
 a computer network and provide for limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 consistent with Exhibit B.

 Software distributed under the License is distributed on an “AS IS” basis, WITHOUT WARRANTY OF ANY KIND, either express
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

package org.isatools.isacreatorconfigurator.ontologyconfigurationtool;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.effects.SingleSelectionListCellRenderer;
import org.isatools.isacreator.ontologymanager.OntologyQueryAdapter;
import org.isatools.isacreator.ontologymanager.OntologyService;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.isatools.isacreatorconfigurator.informationwindow.InformationPane;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.List;

/**
 * SearchOntologyDialog
 *
 * @author eamonnmaguire
 * @date Feb 8, 2010
 */


public class SearchOntologyDialogUI extends InformationPane implements ListSelectionListener {


    @InjectedResource
    private ImageIcon searchIcon, searchIconOver, locateIcon, locateIconOver, searchInfo, searchFailed, noTermsFound, locatingNotSupported;

    private JList ontologyResultList;
    private JScrollPane resultScroller;
    private JTextField searchField;
    private JLabel locateButton;
    private JPanel swappableContainer;

    private Ontology searchOntology;
    private OntologyService ontologyService;

    public SearchOntologyDialogUI(Ontology searchOntology, OntologyService ontologyService) {
        ResourceInjector.get("ontologyconfigtool-package.style").inject(this);

        this.searchOntology = searchOntology;
        this.ontologyService = ontologyService;
    }

    public void instantiateFrame() {
        ontologyResultList = new JList();
        ontologyResultList.setCellRenderer(new SingleSelectionListCellRenderer());
        ontologyResultList.addListSelectionListener(this);

        resultScroller = new JScrollPane(ontologyResultList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        resultScroller.setBorder(new EmptyBorder(1, 1, 1, 1));
        resultScroller.getViewport().setBackground(UIHelper.BG_COLOR);

        IAppWidgetFactory.makeIAppScrollPane(resultScroller);

        swappableContainer = new JPanel(new BorderLayout());

        swappableContainer.add(isLocatingSupported() ? new JLabel(searchInfo) : new JLabel(locatingNotSupported));
        swappableContainer.setBackground(UIHelper.BG_COLOR);
    }

    protected void createContentPane() {

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIHelper.BG_COLOR);
        contentPanel.add(createSearchPanel(), BorderLayout.NORTH);
        contentPanel.add(swappableContainer, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
    }


    protected void createButtonPane() {
        JPanel buttonPanel = new JPanel(new BorderLayout());

        locateButton = new JLabel(locateIcon);
        UIHelper.renderComponent(locateButton, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        locateButton.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                locateButton.setIcon(locateIconOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                locateButton.setIcon(locateIcon);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                Thread performer = new Thread(new Runnable() {
                    public void run() {
                        if (isLocatingSupported() && !ontologyResultList.isSelectionEmpty()) {
                            try {
                                setCurrentPage(new JLabel(LOADING));
                                SearchOntologyDialogUI.this.validate();
                                firePropertyChange("termSelected", "", ontologyResultList.getSelectedValue().toString());

                            } catch (Exception e) {
                                System.err.println("Failed to resolve tree: " + e.getMessage());
                            } finally {
                                setCurrentPage(resultScroller);
                                SearchOntologyDialogUI.this.validate();
                                SearchOntologyDialogUI.this.repaint();
                            }
                        }
                    }
                });

                performer.start();
            }
        });
        locateButton.setVisible(false);
        buttonPanel.add(locateButton, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private Container createSearchPanel() {
        Box verticalBox = Box.createVerticalBox();
        verticalBox.setBackground(UIHelper.BG_COLOR);

        Box informationPanel = Box.createHorizontalBox();
        informationPanel.setBackground(UIHelper.BG_COLOR);

        JLabel informationLabel = UIHelper.createLabel("<html>please enter the <b>term</b>:</html>", UIHelper.VER_10_PLAIN, UIHelper.DARK_GREEN_COLOR,
                SwingConstants.LEFT);

        informationPanel.add(Box.createHorizontalStrut(2));
        informationPanel.add(informationLabel);

        verticalBox.add(informationPanel);

        Box horBox = Box.createHorizontalBox();


        searchField = new JTextField();
        UIHelper.renderComponent(searchField, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);


        searchField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "SEARCH");
        searchField.getActionMap().put("SEARCH", new SearchAction());

        horBox.add(searchField);
        horBox.add(Box.createVerticalStrut(10));

        final JLabel searchOntologies = new JLabel(searchIcon);
        searchOntologies.setBackground(UIHelper.BG_COLOR);
        searchOntologies.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                searchOntologies.setIcon(searchIcon);
                performSearch();
            }

            public void mouseEntered(MouseEvent event) {
                searchOntologies.setIcon(searchIconOver);
            }

            public void mouseExited(MouseEvent event) {
                searchOntologies.setIcon(searchIcon);
            }
        });

        horBox.add(searchOntologies);

        verticalBox.add(horBox);

        return verticalBox;
    }


    private void createOntologyResultList(Map<OntologySourceRefObject, List<OntologyTerm>> termsFound) {
        DefaultListModel dlm = new DefaultListModel();
        for (OntologySourceRefObject ontologySourceRefObject : termsFound.keySet()) {
            for (OntologyTerm term : termsFound.get(ontologySourceRefObject)) {
                dlm.addElement(term);
            }
        }
        ontologyResultList.setModel(dlm);
    }

    private void performSearch() {

        Thread performer = new Thread(new Runnable() {
            public void run() {
                if (!searchField.getText().equals("")) {
                    try {
                        setCurrentPage(new JLabel(LOADING));
                        SearchOntologyDialogUI.this.validate();
                        Map<OntologySourceRefObject, List<OntologyTerm>> result;

                        System.out.println("Search ontology is " + searchOntology.getOntologyID() + " version " + searchOntology.getOntologyVersion());
                        result = ontologyService.getTermsByPartialNameFromSource(searchField.getText(), new OntologyQueryAdapter(searchOntology).getOntologyQueryString(OntologyQueryAdapter.GET_ID), false);

                        if (result.size() == 0) {
                            setCurrentPage(UIHelper.wrapComponentInPanel(new JLabel(noTermsFound)));
                        } else {
                            createOntologyResultList(result);
                            setCurrentPage(resultScroller);
                        }
                    } catch (Exception e) {
                        setCurrentPage(UIHelper.wrapComponentInPanel(new JLabel(searchFailed)));
                        System.err.println("Failed to connect to ontology client: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });

        performer.start();


    }


    public void setSearchOntology(Ontology searchOntology) {
        this.searchOntology = searchOntology;
    }

    public void setOntologyService(OntologyService ontologyService) {
        this.ontologyService = ontologyService;
    }

    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        if (ontologyResultList != null && isLocatingSupported()) {
            locateButton.setVisible(!ontologyResultList.isSelectionEmpty());
        }
    }

    public class SearchAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {

            performSearch();
        }
    }

    private boolean isLocatingSupported() {
        if (searchOntology != null) {
            if (searchOntology.getOntologyAbbreviation() != null) {
                return !searchOntology.getOntologyAbbreviation().equalsIgnoreCase("chebi");
            }
        }

        return true;

    }

    /**
     * Changes Container being shown in the swappableContainer panel
     *
     * @param newContainer - Container to change to
     */
    public void setCurrentPage(Container newContainer) {
        swappableContainer.removeAll();
        swappableContainer.add(newContainer);

        swappableContainer.validate();
        swappableContainer.repaint();
    }


//	public static void main(String[] args) {
//		new SearchOntologyDialogUI(new Ontology("1013", "chebi_v1.62", "CHEBI", "Chemicals of Biological Interest"), new OLSClient());
//	}
}
