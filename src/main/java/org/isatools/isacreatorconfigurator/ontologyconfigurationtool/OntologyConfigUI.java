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
import org.apache.commons.collections15.map.ListOrderedMap;
import org.isatools.isacreator.autofilteringlist.ExtendedJList;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.OntologyFormats;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.effects.FooterPanel;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.effects.InfiniteProgressPanel;
import org.isatools.isacreator.effects.TitlePanel;
import org.isatools.isacreator.effects.borders.RoundedBorder;
import org.isatools.isacreator.ontologybrowsingutils.TreeObserver;
import org.isatools.isacreator.ontologymanager.BioPortalClient;
import org.isatools.isacreator.ontologymanager.OLSClient;
import org.isatools.isacreator.ontologymanager.OntologyService;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologymanager.bioportal.model.OntologyPortal;
import org.isatools.isacreator.ontologymanager.utils.OntologyUtils;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

/**
 * @author eamonnmaguire
 * @date Jul 17, 2009
 */


public class OntologyConfigUI extends JFrame {

    private static final BioPortalClient bioportalClient = new BioPortalClient();
    private static final OLSClient olsClient = new OLSClient();

    @InjectedResource
    private ImageIcon infoImage, confirmButton, confirmButtonOver, addOntologyButtonIcon, addOntologyButtonIconOver,
            removeOntologyButtonIcon, removeOntologyButtonIconOver, browseOntologyButtonIcon, browseOntologyButtonIconOver,
            removeRestrictionButtonIcon, removeRestrictionButtonIconOver;

    private DefaultListModel selectedOntologyListModel;
    private JList selectedOntologyList;
    private JPanel ontologyViewContainer;

    private OntologyBrowser currentlyActiveBrowser;
    private SearchAndDefinitionUI searchAndTermDefinitionViewer;

    // map from ontology display label to the recommended ontology record
    private Map<String, RecommendedOntology> selectedOntologies = new ListOrderedMap<String, RecommendedOntology>();

    private JLabel removeRestrictionButton, viewOntologyButton, removeOntologyButton;

    private List<Ontology> ontologiesToBrowseOn;
    // only want to create one instance in memory of the glass pane.
    private static InfiniteProgressPanel glassPane;

    public OntologyConfigUI() {
        this(null);
    }

    public OntologyConfigUI(List<Ontology> ontologiesToBrowseOn) {
        this(ontologiesToBrowseOn, null);
    }

    public OntologyConfigUI(List<Ontology> ontologiesToBrowseOn, Map<String, RecommendedOntology> currentlySelectedOntologies) {
        this.ontologiesToBrowseOn = ontologiesToBrowseOn;

        ResourceInjector.get("ontologyconfigtool-package.style").inject(this);

        if (currentlySelectedOntologies != null) {
            for (String ontologyLabel : currentlySelectedOntologies.keySet()) {
                if (currentlySelectedOntologies.get(ontologyLabel) != null) {
                    selectedOntologies.put(ontologyLabel, currentlySelectedOntologies.get(ontologyLabel));
                }
            }
        }
        setUndecorated(true);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(900, 450));
        setAlwaysOnTop(true);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGUI();
                updateSelectedOntologies();
                ((JComponent) getContentPane()).setBorder(new LineBorder(UIHelper.LIGHT_GREEN_COLOR, 2));
            }
        });
    }

    public void createGUI() {
        HUDTitleBar titlePanel = new HUDTitleBar(null, null, true);
        add(titlePanel, BorderLayout.NORTH);
        titlePanel.installListeners();
        titlePanel.addPropertyChangeListener("windowClosed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                firePropertyChange("ontologyClosed", "", "closed");
            }
        });
        // create left panel with list of selected ontology terms and Expandable panel
        createOntologySelectionPanel();
        // create right panel containing tree showing the entirety of the ontology selected from the left pane.
        JPanel ontologySelectionPanel = new JPanel(new BorderLayout());

        ontologyViewContainer = new JPanel(new BorderLayout());
        ontologyViewContainer.setPreferredSize(new Dimension(500, 300));
        // add placeholder panel by default with some image describing what to do
        setOntologySelectionPanelPlaceholder(infoImage);
        ontologyViewContainer.setBorder(new TitledBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 7), "browse ontology",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR));

        ontologySelectionPanel.add(ontologyViewContainer, BorderLayout.CENTER);
        ontologySelectionPanel.add(Box.createVerticalStrut(20), BorderLayout.SOUTH);
        add(ontologySelectionPanel, BorderLayout.CENTER);

        JPanel functionWrapper = new JPanel(new BorderLayout());

        searchAndTermDefinitionViewer = new SearchAndDefinitionUI();
        searchAndTermDefinitionViewer.setBorder(new TitledBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 7), "functions",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR));

        functionWrapper.add(searchAndTermDefinitionViewer);
        functionWrapper.add(createButtonPanel(), BorderLayout.SOUTH);

        add(functionWrapper, BorderLayout.EAST);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                if (currentlyActiveBrowser != null && currentlyActiveBrowser.isShowing()) {
                    ontologyViewContainer.setPreferredSize(getMaxBrowserSize());
                    currentlyActiveBrowser.setBrowserSize(getMaxBrowserSize());
                    ontologyViewContainer.repaint();
                    currentlyActiveBrowser.repaint();
                }
            }
        });

        FooterPanel footer = new FooterPanel(this);
        add(footer, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    private void setOntologySelectionPanelPlaceholder(ImageIcon image) {
        JPanel placeHolder = new JPanel(new GridLayout(1, 1));
        placeHolder.add(new JLabel(image, SwingConstants.CENTER));
        placeHolder.setPreferredSize(new Dimension(500, 300));
        ontologyViewContainer.removeAll();
        ontologyViewContainer.add(placeHolder);
        ontologyViewContainer.revalidate();
        ontologyViewContainer.repaint();
    }

    private JPanel createButtonPanel() {
        // contains the button to confirm selection of ontology plus filter part (if applicable!)
        JPanel buttonPanel = new JPanel(new BorderLayout());

        final JLabel button = new JLabel(confirmButton);
        button.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                button.setIcon(confirmButtonOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                button.setIcon(confirmButton);
            }

            public void mousePressed(MouseEvent mouseEvent) {

                Set<String> ontLabelsToRemove = new HashSet<String>();
                for (String ontLabel : selectedOntologies.keySet()) {
                    if (getOntologyByLabel(ontLabel) != null) {
                        selectedOntologies.get(ontLabel)
                                .setBranchToSearchUnder(getOntologyByLabel(ontLabel)
                                        .getSubsectionToQuery());
                    } else {

                        ontLabelsToRemove.add(ontLabel);
                    }
                }

                // remove deprecated ontologies :)
                for (String ontology : ontLabelsToRemove) {
                    if (selectedOntologies.containsKey(ontology)) {
                        selectedOntologies.remove(ontology);
                    }
                }

                firePropertyChange("ontologySelected", "", selectedOntologies);
                setVisible(false);
                dispose();
            }
        });

        buttonPanel.add(button, BorderLayout.EAST);
        return buttonPanel;
    }

    /**
     * Allows for retrieval of an ontology object for the Map of ontologies to browse on given a display label for
     * the ontology.
     *
     * @param label - Display label for ontology of interest, e.g. Ontology for Biomedical Investigations.
     * @return Ontology representing that searched for if it exists, or null if it doesn't.
     */
    private Ontology getOntologyByLabel(String label) {
        for (Ontology o : ontologiesToBrowseOn) {
            if (o.toString().equals(label)) {
                return o;
            }
        }
        return null;
    }

    private void createOntologySelectionPanel() {

        OntologyListRenderer listRenderer = new OntologyListRenderer();

        JPanel westPanel = new JPanel(new BorderLayout());


        JPanel selectedOntologiesContainer = new JPanel(new BorderLayout());
        selectedOntologiesContainer.setOpaque(false);

        // create List containing selected ontologies
        selectedOntologyListModel = new DefaultListModel();
        selectedOntologyList = new JList(selectedOntologyListModel);
        selectedOntologyList.setCellRenderer(new SelectedOntologyListRenderer());
        selectedOntologyList.setBackground(UIHelper.BG_COLOR);

        selectedOntologyList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                setOntologySelectionPanelPlaceholder(infoImage);

                setSelectedOntologyButtonVisibility(selectedOntologyList.isSelectionEmpty());
            }
        });

        JScrollPane selectedOntologiesScroller = new JScrollPane(selectedOntologyList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        selectedOntologiesScroller.setPreferredSize(new Dimension(200, 255));
        selectedOntologiesScroller.setBackground(UIHelper.BG_COLOR);
        selectedOntologiesScroller.getViewport().setBackground(UIHelper.BG_COLOR);

        IAppWidgetFactory.makeIAppScrollPane(selectedOntologiesScroller);

        selectedOntologiesContainer.setBorder(new TitledBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 7), "selected ontologies", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR));

        selectedOntologiesContainer.add(selectedOntologiesScroller, BorderLayout.CENTER);

        // ADD BUTTONS
        removeOntologyButton = new JLabel(removeOntologyButtonIcon);
        removeOntologyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {

                if (!selectedOntologyList.isSelectionEmpty()) {
                    String ontologyToRemove = selectedOntologyList.getSelectedValue().toString();
                    System.out.println("Removing  " + ontologyToRemove);
                    selectedOntologies.remove(ontologyToRemove);
                    setOntologySelectionPanelPlaceholder(infoImage);

                    updateSelectedOntologies();
                }

                removeOntologyButton.setIcon(removeOntologyButtonIcon);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                removeOntologyButton.setIcon(removeOntologyButtonIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                removeOntologyButton.setIcon(removeOntologyButtonIcon);
            }
        });

        removeOntologyButton.setVisible(false);

        viewOntologyButton = new JLabel(browseOntologyButtonIcon);
        viewOntologyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                performTransition();
                viewOntologyButton.setIcon(browseOntologyButtonIcon);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                viewOntologyButton.setIcon(browseOntologyButtonIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                viewOntologyButton.setIcon(browseOntologyButtonIcon);
            }
        });

        viewOntologyButton.setVisible(false);

        removeRestrictionButton = new JLabel(removeRestrictionButtonIcon);
        removeRestrictionButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (!selectedOntologyList.isSelectionEmpty()) {
                    ((RecommendedOntology) selectedOntologyList.getSelectedValue()).setBranchToSearchUnder(null);
                    removeRestrictionButton.setVisible(false);
                    selectedOntologyList.repaint();
                }

                removeRestrictionButton.setIcon(removeRestrictionButtonIcon);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                removeRestrictionButton.setIcon(removeRestrictionButtonIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                removeRestrictionButton.setIcon(removeRestrictionButtonIcon);
            }
        });

        removeRestrictionButton.setVisible(false);

        Box selectedOntologiesOptionContainer = Box.createHorizontalBox();
        selectedOntologiesOptionContainer.setOpaque(false);

        selectedOntologiesOptionContainer.add(removeOntologyButton);
        selectedOntologiesOptionContainer.add(viewOntologyButton);
        selectedOntologiesOptionContainer.add(removeRestrictionButton);

        selectedOntologiesContainer.add(selectedOntologiesOptionContainer, BorderLayout.SOUTH);


        // create panel populated with all available ontologies inside a filterable list!
        JPanel availableOntologiesListContainer = new JPanel(new BorderLayout());
        availableOntologiesListContainer.setBorder(new TitledBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 7), "available ontologies", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR));

        final ExtendedJList availableOntologies = new ExtendedJList(listRenderer);

        final JLabel addOntologyButton = new JLabel(addOntologyButtonIcon);
        addOntologyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (!availableOntologies.isSelectionEmpty()) {
                    Ontology ontology = (Ontology) availableOntologies.getSelectedValue();

                    selectedOntologies.put(ontology.getOntologyDisplayLabel(),
                            new RecommendedOntology(ontology));
                    updateSelectedOntologies();

                    setOntologySelectionPanelPlaceholder(infoImage);
                }

                addOntologyButton.setIcon(addOntologyButtonIcon);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                addOntologyButton.setIcon(addOntologyButtonIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                addOntologyButton.setIcon(addOntologyButtonIcon);
            }
        });

        final JLabel info = UIHelper.createLabel("", UIHelper.VER_10_PLAIN, UIHelper.DARK_GREEN_COLOR);

        availableOntologies.addPropertyChangeListener("update", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                info.setText("<html>viewing <b>" + availableOntologies.getFilteredItems().size() + "</b> ontologies</html>");
            }
        });

        Box optionsBox = Box.createVerticalBox();

        optionsBox.add(UIHelper.wrapComponentInPanel(info));

        Box availableOntologiesOptionBox = Box.createHorizontalBox();
        availableOntologiesOptionBox.add(addOntologyButton);
        availableOntologiesOptionBox.add(Box.createHorizontalGlue());

        optionsBox.add(availableOntologiesOptionBox);

        availableOntologiesListContainer.add(optionsBox, BorderLayout.SOUTH);

        if (ontologiesToBrowseOn == null) {
            ontologiesToBrowseOn = new ArrayList<Ontology>();
            List<Ontology> bioportalQueryResult = bioportalClient.getAllOntologies();
            if (bioportalQueryResult != null) {
                ontologiesToBrowseOn.addAll(bioportalQueryResult);
            }
            ontologiesToBrowseOn.addAll(olsClient.getAllOntologies());
        }

        // precautionary check in case of having no ontologies available to search on.
        if (ontologiesToBrowseOn != null) {
            for (Ontology o : ontologiesToBrowseOn) {
                availableOntologies.addItem(o);
            }
        }

        info.setText("<html>viewing <b>" + availableOntologies.getFilteredItems().size() + "</b> ontologies</html>");

        // need to get ontologies available from bioportal and add them here.
        JScrollPane availableOntologiesScroller = new JScrollPane(availableOntologies,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        availableOntologiesScroller.getViewport().setBackground(UIHelper.BG_COLOR);
        availableOntologiesScroller.setPreferredSize(new Dimension(200, 125));
        availableOntologiesScroller.setBorder(new EmptyBorder(0, 0, 0, 0));

        IAppWidgetFactory.makeIAppScrollPane(availableOntologiesScroller);

        availableOntologiesListContainer.add(availableOntologiesScroller);
        availableOntologiesListContainer.add(availableOntologies.getFilterField(), BorderLayout.NORTH);


        westPanel.add(selectedOntologiesContainer, BorderLayout.CENTER);
        westPanel.add(availableOntologiesListContainer, BorderLayout.SOUTH);

        add(westPanel, BorderLayout.WEST);
    }

    private void setSelectedOntologyButtonVisibility(boolean selectionEmpty) {
        removeOntologyButton.setVisible(!selectionEmpty);
        viewOntologyButton.setVisible(!selectionEmpty);

        removeRestrictionButton.setVisible(showRemoveRestrictionButton(selectionEmpty));
    }

    private boolean showRemoveRestrictionButton(boolean selectionEmpty) {
        if(selectionEmpty) {
            return !selectionEmpty;
        }

        RecommendedOntology selectedOntology = (RecommendedOntology) selectedOntologyList.getSelectedValue();

        return selectedOntology.getBranchToSearchUnder() != null;
    }

    private void updateSelectedOntologies() {
        selectedOntologyListModel.removeAllElements();
        for (String ro : selectedOntologies.keySet()) {
            if (selectedOntologies.get(ro) != null) {
                selectedOntologyListModel.addElement(selectedOntologies.get(ro));
            }
        }
        repaint();
    }

    private void performTransition() {

        Thread performer = new Thread(new Runnable() {
            public void run() {
                try {
                    RecommendedOntology recommendedOntology = (RecommendedOntology) selectedOntologyList.getSelectedValue();

                    Ontology ontology = recommendedOntology.getOntology();

                    if (ontology != null) {
                        System.err.println("Got ontology: " + ontology.getOntologyAbbreviation());
                        String ontLabel = ontology.getOntologyAbbreviation() == null ? "ontology" : ontology.getOntologyAbbreviation();
                        glassPane = new InfiniteProgressPanel(
                                "loading " + ontLabel + " for display...");
                        glassPane.setSize(new Dimension(
                                getWidth(),
                                getHeight()));
                        setGlassPane(glassPane);
                        glassPane.start();
                        validate();

                        ontologyViewContainer.removeAll();
                        if (selectedOntologies.containsKey(ontology.getOntologyDisplayLabel())) {
                            ontology.setSubsectionToQuery(selectedOntologies.get(ontology.getOntologyDisplayLabel()).getBranchToSearchUnder());
                        }

                        if (OntologyUtils.getSourceOntologyPortal(ontology) == OntologyPortal.BIOPORTAL) {
                            currentlyActiveBrowser = new OntologyBrowser(ontology, bioportalClient, getMaxBrowserSize());
                            configureSearchAndTermDefinitionPanel(ontology, bioportalClient);
                            ontologyViewContainer.add(currentlyActiveBrowser);
                        } else {
                            currentlyActiveBrowser = new OntologyBrowser(ontology, olsClient, getMaxBrowserSize());
                            configureSearchAndTermDefinitionPanel(ontology, olsClient);
                            ontologyViewContainer.add(currentlyActiveBrowser);
                        }

                        currentlyActiveBrowser.setPreferredSize(getMaxBrowserSize());
                        glassPane.stop();
                        OntologyConfigUI.this.validate();
                        OntologyConfigUI.this.repaint();
                    } else {
                        System.err.println("ontology is null");
                    }
                } catch (OutOfMemoryError oome) {
                    System.err.println("We ran out of memory when trying to load the ontology");
                } finally {
                    if (glassPane != null) {
                        if (glassPane.isStarted()) {
                            EventQueue.invokeLater(new Runnable() {
                                public void run() {
                                    glassPane.stop();
                                }
                            });
                        }
                    }
                }
            }
        });

        performer.start();
    }

    private Dimension getMaxBrowserSize() {
        return new Dimension(OntologyConfigUI.this.getWidth() - 450, (int) (OntologyConfigUI.this.getHeight() * 0.75));
    }

    private void configureSearchAndTermDefinitionPanel(Ontology ontologyToQuery, OntologyService ontologyClient) {
        searchAndTermDefinitionViewer.setOntologyToQuery(ontologyToQuery);
        searchAndTermDefinitionViewer.setOntologyTree(currentlyActiveBrowser.getOntologyTree());
        searchAndTermDefinitionViewer.setOntologyTreeCreator(currentlyActiveBrowser.getOntologyTreeCreator());
        searchAndTermDefinitionViewer.setOntologyClient(ontologyClient);
        searchAndTermDefinitionViewer.updateView();

        currentlyActiveBrowser.registerObserver(searchAndTermDefinitionViewer);
        currentlyActiveBrowser.registerObserver(new TreeObserver() {
            public void notifyOfSelection() {

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        Ontology currentOntology = currentlyActiveBrowser.getOntologyToQuery();
                        RecommendedOntology selectedOntologyItem = (RecommendedOntology) selectedOntologyList.getSelectedValue();

                        if (selectedOntologyItem.getOntology() == currentOntology) {
                            selectedOntologyItem.setBranchToSearchUnder(currentlyActiveBrowser.getSelectedTreePart());
                            removeRestrictionButton.setVisible(true);
                            repaint();
                        }
                    }
                });
            }
        });
    }

    public static void main(String[] args) {
        OntologyConfigUI ui = new OntologyConfigUI();
        ui.createGUI();
    }

}
