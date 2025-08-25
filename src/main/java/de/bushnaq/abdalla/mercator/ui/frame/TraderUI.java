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
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

/**
 * UI component for managing trader-related interface elements
 */
public class TraderUI {
    private       boolean    enableEventFiring = true;
    private       JPanel     jPanelTraders;
    private       JTable     jTableTraderEvents;
    private       JTable     jTableTraderGoods;
    private       JTable     jTableTraderNeeds;
    private       JTable     jTableTraderSimEvents;
    private       JTable     jTableTraderWaypoints;
    private       JTable     jTableTraders;
    private       JTextField traderCredits;
    private       JTextField traderDestinationPlanet;
    private       JTextField traderFilterField;
    private       JTextField traderName;
    private       JTextField traderNextWaypoint;
    private       JTextField traderPreviousWaypoint;
    private       JTextField traderSourcePlanet;
    private final Universe   universe;

    public TraderUI(Universe universe) {
        this.universe = universe;
    }

    private void createTraderInfo(JTabbedPane jTraderTabbedPane) {
        // Info tab - move the trader details here
        {
            final JPanel jPanelTraderInfo = new JPanel();
            jPanelTraderInfo.setLayout(new BorderLayout());
            {
                final JPanel jPanel = new JPanel();
                jPanel.setLayout(new GridLayout(0, 2, 5, 5)); // Added spacing between components
                {
                    final JLabel jLabel = new JLabel();
                    jLabel.setText("Name");
                    jPanel.add(jLabel);
                }
                {
                    traderName = new JTextField();
                    traderName.setColumns(10);
                    traderName.setEditable(false);
                    jPanel.add(traderName);
                }
                {
                    final JLabel jLabel = new JLabel();
                    jLabel.setText("Credits");
                    jPanel.add(jLabel);
                }
                {
                    traderCredits = new JTextField();
                    traderCredits.setColumns(10);
                    traderCredits.setEditable(false);
                    jPanel.add(traderCredits);
                }
                {
                    final JLabel jLabel = new JLabel();
                    jLabel.setText("Destination Planet");
                    jPanel.add(jLabel);
                }
                {
                    traderDestinationPlanet = new JTextField();
                    traderDestinationPlanet.setColumns(10);
                    traderDestinationPlanet.setEditable(false);
                    jPanel.add(traderDestinationPlanet);
                }
                {
                    final JLabel jLabel = new JLabel();
                    jLabel.setText("Next Waypoint");
                    jPanel.add(jLabel);
                }
                {
                    traderNextWaypoint = new JTextField();
                    traderNextWaypoint.setColumns(10);
                    traderNextWaypoint.setEditable(false);
                    jPanel.add(traderNextWaypoint);
                }
                {
                    final JLabel jLabel = new JLabel();
                    jLabel.setText("Previous Waypoint");
                    jPanel.add(jLabel);
                }
                {
                    traderPreviousWaypoint = new JTextField();
                    traderPreviousWaypoint.setColumns(10);
                    traderPreviousWaypoint.setEditable(false);
                    jPanel.add(traderPreviousWaypoint);
                }
                {
                    final JLabel jLabel = new JLabel();
                    jLabel.setText("Source Planet");
                    jPanel.add(jLabel);
                }
                {
                    traderSourcePlanet = new JTextField();
                    traderSourcePlanet.setColumns(10);
                    traderSourcePlanet.setEditable(false);
                    jPanel.add(traderSourcePlanet);
                }
                jPanelTraderInfo.add(jPanel, BorderLayout.NORTH);
            }
            jTraderTabbedPane.addTab("Info", null, jPanelTraderInfo, "Trader's basic information");
        }
    }

    public JPanel getJPanelTraders() {
        if (jPanelTraders == null) {
            jPanelTraders = new JPanel();
            jPanelTraders.setLayout(new BorderLayout());

            {
                final JPanel jPanelTradersList = new JPanel();
                jPanelTradersList.setLayout(new BorderLayout());
                // Add filter text field
                {
                    final JPanel filterPanel = new JPanel();
                    filterPanel.setLayout(new BorderLayout());
                    final JLabel filterLabel = new JLabel("Filter by name: ");
                    traderFilterField = new JTextField();
                    filterPanel.add(filterLabel, BorderLayout.WEST);
                    filterPanel.add(traderFilterField, BorderLayout.CENTER);
                    jPanelTradersList.add(filterPanel, BorderLayout.NORTH);
                }
                {
                    jTableTraders = new JTable(new TradersTableModel(universe));
                    jTableTraders.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    jTableTraders.setAutoCreateRowSorter(true);
                    // Add filter functionality
                    final TableRowSorter<TradersTableModel> sorter = new TableRowSorter<>((TradersTableModel) jTableTraders.getModel());
                    jTableTraders.setRowSorter(sorter);

                    traderFilterField.getDocument().addDocumentListener(new DocumentListener() {
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
                            String text = traderFilterField.getText().trim();
                            if (text.length() == 0) {
                                sorter.setRowFilter(null);
                            } else {
                                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0)); // Assuming name is in column 0
                            }
                        }
                    });
                    jTableTraders.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                        @Override
                        public void valueChanged(final ListSelectionEvent e) {
                            if (enableEventFiring) {
                                if (!e.getValueIsAdjusting()) {
                                    int rowIndex = jTableTraders.getSelectedRow();
                                    if (rowIndex > -1) {
                                        rowIndex = jTableTraders.convertRowIndexToModel(rowIndex);
                                        try {
                                            universe.setSelected(universe.traderList.get(rowIndex), true);
                                        } catch (final Exception e1) {
                                            // TODO Auto-generated catch block
                                            e1.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    });
                    jTableTraders.setFillsViewportHeight(true);
                    final JScrollPane jScroll = new JScrollPane(jTableTraders);
                    jPanelTradersList.add(jScroll, BorderLayout.CENTER);
                }

                final JPanel jTraderSelected = new JPanel();
                jTraderSelected.setLayout(new BorderLayout());
                {
                    final JLabel jLabel = new JLabel();
                    jLabel.setText("Selected Trader");
                    final JPanel jPanel = new JPanel();
                    jPanel.setLayout(new GridBagLayout());
                    jPanel.add(jLabel, new GridBagConstraints());
                    jTraderSelected.add(jPanel, BorderLayout.NORTH);
                }
                // Create tabbed pane for trader details
                {
                    final JTabbedPane jTraderTabbedPane = new JTabbedPane();

                    createTraderInfo(jTraderTabbedPane);

                    // Needs tab
                    {
                        final JPanel jPanelTraderNeeds = new JPanel();
                        jPanelTraderNeeds.setLayout(new BorderLayout());
                        {
                            jTableTraderNeeds = new JTable(new TraderNeedsTableModel(universe));
                            jTableTraderNeeds.setFillsViewportHeight(true);
                            final JScrollPane jScroll = new JScrollPane(jTableTraderNeeds);
                            jPanelTraderNeeds.add(jScroll, BorderLayout.CENTER);
                        }
                        jTraderTabbedPane.addTab("Needs", null, jPanelTraderNeeds, "Trader's needs");
                    }

                    // Goods tab
                    {
                        final JPanel jPanelGoods = new JPanel();
                        jPanelGoods.setLayout(new BorderLayout());
                        {
                            jTableTraderGoods = new JTable(new TraderGoodsTableModel(universe));
                            jTableTraderGoods.setFillsViewportHeight(true);
                            final JScrollPane jScroll = new JScrollPane(jTableTraderGoods);
                            jPanelGoods.add(jScroll, BorderLayout.CENTER);
                        }
                        jTraderTabbedPane.addTab("Goods", null, jPanelGoods, "Trader's goods inventory");
                    }

                    // Events tab
                    {
                        final JPanel jPanelEvents = new JPanel();
                        jPanelEvents.setLayout(new BorderLayout());
                        {
                            jTableTraderEvents = new JTable(new TraderEventsTableModel(universe));
                            jTableTraderEvents.setFillsViewportHeight(true);
                            jTableTraderEvents.setAutoCreateRowSorter(true);
                            jTableTraderEvents.getColumnModel().getColumn(0).setPreferredWidth(80);
                            jTableTraderEvents.getColumnModel().getColumn(1).setPreferredWidth(80);
                            jTableTraderEvents.getColumnModel().getColumn(2).setPreferredWidth(60);
                            jTableTraderEvents.getColumnModel().getColumn(3).setPreferredWidth(80);
                            jTableTraderEvents.getColumnModel().getColumn(4).setPreferredWidth(400);
                            final JScrollPane jScroll = new JScrollPane(jTableTraderEvents);
                            jPanelEvents.add(jScroll, BorderLayout.CENTER);
                        }
                        jTraderTabbedPane.addTab("Events", null, jPanelEvents, "Trader's general event history");
                    }

                    // SimEvents tab (only SimEvents)
                    {
                        final JPanel jPanelSimEvents = new JPanel();
                        jPanelSimEvents.setLayout(new BorderLayout());
                        {
                            jTableTraderSimEvents = new JTable(new TraderSimEventsTableModel(universe));
                            jTableTraderSimEvents.setFillsViewportHeight(true);
                            jTableTraderSimEvents.setAutoCreateRowSorter(true);
                            jTableTraderSimEvents.getColumnModel().getColumn(0).setPreferredWidth(80);
                            jTableTraderSimEvents.getColumnModel().getColumn(1).setPreferredWidth(80);
                            jTableTraderSimEvents.getColumnModel().getColumn(2).setPreferredWidth(60);
                            jTableTraderSimEvents.getColumnModel().getColumn(3).setPreferredWidth(80);
                            jTableTraderSimEvents.getColumnModel().getColumn(4).setPreferredWidth(400);
                            final JScrollPane jScroll = new JScrollPane(jTableTraderSimEvents);
                            jPanelSimEvents.add(jScroll, BorderLayout.CENTER);
                        }
                        jTraderTabbedPane.addTab("SimEvents", null, jPanelSimEvents, "Trader's simulation event history");
                    }

                    // Waypoints tab
                    {
                        final JPanel jPanelWaypoints = new JPanel();
                        jPanelWaypoints.setLayout(new BorderLayout());
                        {
                            jTableTraderWaypoints = new JTable(new TraderWaypointTableModel(universe));
                            jTableTraderWaypoints.setFillsViewportHeight(true);
                            jTableTraderWaypoints.setAutoCreateRowSorter(true);
                            jTableTraderWaypoints.getColumnModel().getColumn(0).setPreferredWidth(100); // City
                            jTableTraderWaypoints.getColumnModel().getColumn(1).setPreferredWidth(120); // Name
                            jTableTraderWaypoints.getColumnModel().getColumn(2).setPreferredWidth(100); // Sector
                            jTableTraderWaypoints.getColumnModel().getColumn(3).setPreferredWidth(100); // Trader
                            final JScrollPane jScroll = new JScrollPane(jTableTraderWaypoints);
                            jPanelWaypoints.add(jScroll, BorderLayout.CENTER);
                        }
                        jTraderTabbedPane.addTab("Waypoints", null, jPanelWaypoints, "Trader's navigation waypoints");
                    }

                    jTraderSelected.add(jTraderTabbedPane, BorderLayout.CENTER);
                }

                // Use JSplitPane to create 50/50 split that responds to window resizing
                final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jPanelTradersList, jTraderSelected);
                splitPane.setResizeWeight(0.5); // 50/50 split
                splitPane.setOneTouchExpandable(true);
                splitPane.setContinuousLayout(true);

                jPanelTraders.add(splitPane, BorderLayout.CENTER);
            }
        }
        return jPanelTraders;
    }

    public void setEnableEventFiring(boolean enableEventFiring) {
        this.enableEventFiring = enableEventFiring;
    }

    public void updateTraderInfo() {
        Trader trader = universe.selectedTrader;
        if (trader != null) {
            if (traderName != null && !trader.getName().equals(traderName.getText())) {
                traderName.setText(trader.getName());
            }
            if (traderCredits != null && !String.valueOf(trader.getCredits()).equals(traderCredits.getText())) {
                traderCredits.setText(String.valueOf(trader.getCredits()));
            }
            if (traderDestinationPlanet != null && trader.navigator.destinationPlanet != null && !trader.navigator.destinationPlanet.getName().equals(traderDestinationPlanet.getText())) {
                traderDestinationPlanet.setText(trader.navigator.destinationPlanet.getName());
            }
            if (traderNextWaypoint != null && trader.navigator.nextWaypoint != null && !trader.navigator.nextWaypoint.getName().equals(traderNextWaypoint.getText())) {
                traderNextWaypoint.setText(trader.navigator.nextWaypoint.getName());
            }
            if (traderPreviousWaypoint != null && trader.navigator.previousWaypoint != null && !trader.navigator.previousWaypoint.getName().equals(traderPreviousWaypoint.getText())) {
                traderPreviousWaypoint.setText(trader.navigator.previousWaypoint.getName());
            }
            if (traderSourcePlanet != null && trader.navigator.sourcePlanet != null && !trader.navigator.sourcePlanet.getName().equals(traderSourcePlanet.getText())) {
                traderSourcePlanet.setText(trader.navigator.sourcePlanet.getName());
            }
        }
    }

    public void updateTraderTableData() {
        if (jTableTraders != null) {
            int rowIndex = -1;
            if (universe.selectedTrader != null)
                rowIndex = universe.traderList.indexOf(universe.selectedTrader);
            else
                rowIndex = jTableTraders.getSelectedRow();
            ((AbstractTableModel) jTableTraders.getModel()).fireTableDataChanged();
            if (rowIndex > -1) {
                // Convert model index to view index for filtered table
                int viewIndex = jTableTraders.convertRowIndexToView(rowIndex);
                if (viewIndex > -1) {
                    jTableTraders.setRowSelectionInterval(viewIndex, viewIndex);
                }
            }
        }

        if (jTableTraderGoods != null) {
            ((AbstractTableModel) jTableTraderGoods.getModel()).fireTableDataChanged();
        }
        if (jTableTraderNeeds != null) {
            ((AbstractTableModel) jTableTraderNeeds.getModel()).fireTableDataChanged();
        }
        if (jTableTraderEvents != null) {
            ((AbstractTableModel) jTableTraderEvents.getModel()).fireTableDataChanged();
        }
        if (jTableTraderSimEvents != null) {
            ((AbstractTableModel) jTableTraderSimEvents.getModel()).fireTableDataChanged();
        }
        if (jTableTraderWaypoints != null) {
            ((AbstractTableModel) jTableTraderWaypoints.getModel()).fireTableDataChanged();
        }
    }
}
