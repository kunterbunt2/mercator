/*
 * Copyright (C) 2024 Abdalla Bushnaq
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.bushnaq.abdalla.mercator.ui.frame;

import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

/**
 * UI component for managing planet-related interface elements
 */
public class PlanetUI {
    private       boolean    enableEventFiring        = true;
    private       JPanel     jPanelPlanets;
    private       JTable     jTablePlanetEvents;
    private       JTable     jTablePlanetFactories;
    private       JTable     jTablePlanetGoods;
    private       JTable     jTablePlanetSimEvents;
    private       JTable     jTablePlanetSims;
    private       JTable     jTablePlanets;
    private       int        lastTablePlanetsRowIndex = -1;
    private       JTextField planetCredits;
    private       JTextField planetDockingDoorsState;
    private       JTextField planetFilterField;
    private       JTextField planetInDock;
    private       JTextField planetName;
    private       JTextField planetStatus;
    private final Universe   universe;

    public PlanetUI(Universe universe) {
        this.universe = universe;
    }

    private void createEventsTab(JTabbedPane jPlanetTabbedPane) {
        // Events tab (now only non-SimEvents)
        {
            final JPanel jPanelEvents = new JPanel();
            jPanelEvents.setLayout(new BorderLayout());
            {
                jTablePlanetEvents = new JTable(new PlanetEventsTableModel(universe));
                jTablePlanetEvents.setFillsViewportHeight(true);
                jTablePlanetEvents.setAutoCreateRowSorter(true);
                jTablePlanetEvents.getColumnModel().getColumn(0).setPreferredWidth(80);
                jTablePlanetEvents.getColumnModel().getColumn(1).setPreferredWidth(80);
                jTablePlanetEvents.getColumnModel().getColumn(2).setPreferredWidth(60);
                jTablePlanetEvents.getColumnModel().getColumn(3).setPreferredWidth(80);
                jTablePlanetEvents.getColumnModel().getColumn(4).setPreferredWidth(400);
                final JScrollPane jScroll = new JScrollPane(jTablePlanetEvents);
                jScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                jPanelEvents.add(jScroll, BorderLayout.CENTER);
            }
            jPlanetTabbedPane.addTab("Events", null, jPanelEvents, "Planet's general event history");
        }
    }

    private void createFactoriesTab(JTabbedPane jPlanetTabbedPane) {
        // Factories tab
        {
            final JPanel jPanelFactories = new JPanel();
            jPanelFactories.setLayout(new BorderLayout());
            {
                jTablePlanetFactories = new JTable(new PlanetFactoriesTableModel(universe));
                jTablePlanetFactories.setFillsViewportHeight(true);
                final JScrollPane jScroll = new JScrollPane(jTablePlanetFactories);
                jPanelFactories.add(jScroll, BorderLayout.CENTER);
            }
            jPlanetTabbedPane.addTab("Factories", null, jPanelFactories, "Planet's factories");
        }
    }

    private void createGoodsTab(JTabbedPane jPlanetTabbedPane) {
        // Goods tab
        {
            final JPanel jPanelGoods = new JPanel();
            jPanelGoods.setLayout(new BorderLayout());
            {
                jTablePlanetGoods = new JTable(new PlanetGoodsTableModel(universe));
                jTablePlanetGoods.setFillsViewportHeight(true);
                final JScrollPane jScroll = new JScrollPane(jTablePlanetGoods);
                jScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                jPanelGoods.add(jScroll, BorderLayout.CENTER);
            }
            jPlanetTabbedPane.addTab("Goods", null, jPanelGoods, "Planet's goods inventory");
        }
    }

    private void createInfoTab(JTabbedPane jPlanetTabbedPane) {
        // Info tab - move the planet details here
        {
            final JPanel jPanelPlanetInfo = new JPanel();
            jPanelPlanetInfo.setLayout(new BorderLayout());
            {
                final JPanel jPanel = new JPanel();
                jPanel.setLayout(new GridLayout(0, 2, 0, 0));
                {
                    final JLabel jLabel = new JLabel();
                    jLabel.setText("Name");
                    jPanel.add(jLabel);
                }
                {
                    planetName = new JTextField();
                    planetName.setColumns(10);
                    jPanel.add(planetName);
                }
                {
                    final JLabel jLabel = new JLabel();
                    jLabel.setText("Credits");
                    jPanel.add(jLabel);
                }
                {
                    planetCredits = new JTextField();
                    planetCredits.setColumns(10);
                    jPanel.add(planetCredits);
                }
                {
                    final JLabel jLabel = new JLabel();
                    jLabel.setText("Docking Doors State");
                    jPanel.add(jLabel);
                }
                {
                    planetDockingDoorsState = new JTextField();
                    planetDockingDoorsState.setColumns(10);
                    planetDockingDoorsState.setEditable(false);
                    jPanel.add(planetDockingDoorsState);
                }
                {
                    final JLabel jLabel = new JLabel();
                    jLabel.setText("In Dock");
                    jPanel.add(jLabel);
                }
                {
                    planetInDock = new JTextField();
                    planetInDock.setColumns(10);
                    planetInDock.setEditable(false);
                    jPanel.add(planetInDock);
                }
                {
                    final JLabel jLabel = new JLabel();
                    jLabel.setText("Status");
                    jPanel.add(jLabel);
                }
                {
                    planetStatus = new JTextField();
                    planetStatus.setColumns(10);
                    planetStatus.setEditable(false);
                    jPanel.add(planetStatus);
                }
                jPanelPlanetInfo.add(jPanel, BorderLayout.NORTH);
            }
            jPlanetTabbedPane.addTab("Info", null, jPanelPlanetInfo, "Planet's basic information");
        }
    }

    private void createSimEventsTab(JTabbedPane jPlanetTabbedPane) {
        // SimEvents tab (only SimEvents)
        {
            final JPanel jPanelSimEvents = new JPanel();
            jPanelSimEvents.setLayout(new BorderLayout());
            {
                jTablePlanetSimEvents = new JTable(new PlanetSimEventsTableModel(universe));
                jTablePlanetSimEvents.setFillsViewportHeight(true);
                jTablePlanetSimEvents.setAutoCreateRowSorter(true);
                jTablePlanetSimEvents.getColumnModel().getColumn(0).setPreferredWidth(80);
                jTablePlanetSimEvents.getColumnModel().getColumn(1).setPreferredWidth(80);
                jTablePlanetSimEvents.getColumnModel().getColumn(2).setPreferredWidth(60);
                jTablePlanetSimEvents.getColumnModel().getColumn(3).setPreferredWidth(80);
                jTablePlanetSimEvents.getColumnModel().getColumn(4).setPreferredWidth(400);
                final JScrollPane jScroll = new JScrollPane(jTablePlanetSimEvents);
                jScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                jPanelSimEvents.add(jScroll, BorderLayout.CENTER);
            }
            jPlanetTabbedPane.addTab("SimEvents", null, jPanelSimEvents, "Planet's simulation event history");
        }
    }

    private void createSimsTab(JTabbedPane jPlanetTabbedPane) {
        // Sims tab
        {
            final JPanel jPanelSims = new JPanel();
            jPanelSims.setLayout(new BorderLayout());
            {
                jTablePlanetSims = new JTable(new PlanetSimsTableModel(universe));
                jTablePlanetSims.setFillsViewportHeight(true);
                final JScrollPane jScroll = new JScrollPane(jTablePlanetSims);
                jScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                jPanelSims.add(jScroll, BorderLayout.CENTER);
            }
            jPlanetTabbedPane.addTab("Sims", null, jPanelSims, "Planet's sims (inhabitants)");
        }
    }

    public JPanel getJPanelPlanets() {
        if (jPanelPlanets == null) {
            jPanelPlanets = new JPanel();
            jPanelPlanets.setLayout(new BorderLayout());

            {
                final JPanel jPanelPlanetList = new JPanel();
                jPanelPlanetList.setLayout(new BorderLayout());

                // Filter text field
                {
                    final JPanel filterPanel = new JPanel();
                    filterPanel.setLayout(new BorderLayout());
                    final JLabel filterLabel = new JLabel("Filter by name: ");
                    planetFilterField = new JTextField();
                    filterPanel.add(filterLabel, BorderLayout.WEST);
                    filterPanel.add(planetFilterField, BorderLayout.CENTER);
                    jPanelPlanetList.add(filterPanel, BorderLayout.NORTH);
                }

                {
                    jTablePlanets = new JTable(new PlanetsTableModel(universe));
                    jTablePlanets.setAutoCreateRowSorter(true);
                    jTablePlanets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    // Add filter functionality
                    final TableRowSorter<PlanetsTableModel> sorter = new TableRowSorter<>((PlanetsTableModel) jTablePlanets.getModel());
                    jTablePlanets.setRowSorter(sorter);

                    planetFilterField.getDocument().addDocumentListener(new DocumentListener() {
                        @Override
                        public void changedUpdate(DocumentEvent e) {
                            updateFilter();
                        }

                        @Override
                        public void insertUpdate(DocumentEvent e) {
                            updateFilter();
                        }

                        @Override
                        public void removeUpdate(DocumentEvent e) {
                            updateFilter();
                        }

                        private void updateFilter() {
                            String text = planetFilterField.getText().trim();
                            if (text.length() == 0) {
                                sorter.setRowFilter(null);
                            } else {
                                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0)); // Assuming name is in column 0
                            }
                        }
                    });
                    jTablePlanets.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                        @Override
                        public void valueChanged(final ListSelectionEvent e) {
                            if (enableEventFiring) {
                                if (!e.getValueIsAdjusting()) {
                                    int rowIndex = jTablePlanets.getSelectedRow();
                                    if (rowIndex > -1) {
                                        if (lastTablePlanetsRowIndex != rowIndex) {
                                            lastTablePlanetsRowIndex = rowIndex;
                                            rowIndex                 = jTablePlanets.convertRowIndexToModel(rowIndex);
                                            try {
                                                universe.setSelected(universe.planetList.get(rowIndex), true);
                                            } catch (final Exception e1) {
                                                // TODO Auto-generated catch block
                                                e1.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                    jTablePlanets.setFillsViewportHeight(true);
                    final JScrollPane jScroll = new JScrollPane(jTablePlanets);
                    jScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                    jPanelPlanetList.add(jScroll, BorderLayout.CENTER);
                }

                final JPanel jPanelSelected = new JPanel();
                jPanelSelected.setLayout(new BorderLayout());
                {
                    final JLabel jLabel = new JLabel();
                    jLabel.setText("Selected Planet");
                    final JPanel jPanel = new JPanel();
                    jPanel.setLayout(new GridBagLayout());
                    jPanel.add(jLabel, new GridBagConstraints());
                    jPanelSelected.add(jPanel, BorderLayout.NORTH);
                }
                // Create tabbed pane for planet details
                {
                    final JTabbedPane jPlanetTabbedPane = new JTabbedPane();

                    createInfoTab(jPlanetTabbedPane);
                    createGoodsTab(jPlanetTabbedPane);
                    createFactoriesTab(jPlanetTabbedPane);
                    createSimsTab(jPlanetTabbedPane);
                    createEventsTab(jPlanetTabbedPane);
                    createSimEventsTab(jPlanetTabbedPane);

                    jPanelSelected.add(jPlanetTabbedPane, BorderLayout.CENTER);
                }

                // Use JSplitPane to create 50/50 split that responds to window resizing
                final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jPanelPlanetList, jPanelSelected);
                splitPane.setResizeWeight(0.5); // 50/50 split
                splitPane.setOneTouchExpandable(true);
                splitPane.setContinuousLayout(true);

                jPanelPlanets.add(splitPane, BorderLayout.CENTER);
            }
        }
        return jPanelPlanets;
    }

    public void setEnableEventFiring(boolean enableEventFiring) {
        this.enableEventFiring = enableEventFiring;
    }

    public void updatePlanetInfo() {
        Planet planet = universe.selectedPlanet;
        if (planet != null && planetName != null && !universe.selectedPlanet.getName().equals(planetName.getText())) {
            planetName.setText(planet.getName());
        }
        if (planet != null && planetCredits != null && !String.valueOf(planet.getCredits()).equals(planetCredits.getText())) {
            planetCredits.setText(String.valueOf(planet.getCredits()));
        }
        if (planet != null && planet.dockingDoors != null && planetDockingDoorsState != null) {
            String dockingDoorsState = planet.dockingDoors.getDockingDoorStatus().name();
            if (!dockingDoorsState.equals(planetDockingDoorsState.getText())) {
                planetDockingDoorsState.setText(dockingDoorsState);
            }
        } else {
            if (planetDockingDoorsState != null && !"".equals(planetDockingDoorsState.getText())) {
                planetDockingDoorsState.setText("");
            }
        }
        if (planet != null && planet.inDock != null && planetInDock != null) {
            String inDockName = planet.inDock.getName();
            if (!inDockName.equals(planetInDock.getText())) {
                planetInDock.setText(inDockName);
            }
        } else {
            if (planetInDock != null && !"".equals(planetInDock.getText())) {
                planetInDock.setText("");
            }
        }
        if (planet != null && planet.status != null && planetStatus != null) {
            String statusName = planet.status.name();
            if (!statusName.equals(planetStatus.getText())) {
                planetStatus.setText(statusName);
            }
        } else {
            if (planetStatus != null && !"".equals(planetStatus.getText())) {
                planetStatus.setText("");
            }
        }
    }

    public void updatePlanetTableData() {
        if (jTablePlanets != null) {
            int rowIndex = -1;
            if (universe.selectedPlanet != null)
                rowIndex = universe.planetList.indexOf(universe.selectedPlanet);
            else
                rowIndex = jTablePlanets.getSelectedRow();
            ((AbstractTableModel) jTablePlanets.getModel()).fireTableDataChanged();
            if (rowIndex > -1) {
                // Convert model index to view index for filtered table
                int viewIndex = jTablePlanets.convertRowIndexToView(rowIndex);
                if (viewIndex > -1) {
                    jTablePlanets.setRowSelectionInterval(viewIndex, viewIndex);
                }
            }
        }

        ((AbstractTableModel) jTablePlanetGoods.getModel()).fireTableDataChanged();
        ((AbstractTableModel) jTablePlanetFactories.getModel()).fireTableDataChanged();
        ((AbstractTableModel) jTablePlanetSims.getModel()).fireTableDataChanged();
        ((AbstractTableModel) jTablePlanetEvents.getModel()).fireTableDataChanged();
        ((AbstractTableModel) jTablePlanetSimEvents.getModel()).fireTableDataChanged();
    }
}
