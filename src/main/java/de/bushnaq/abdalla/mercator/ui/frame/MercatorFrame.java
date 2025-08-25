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
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;

/**
 * @author bushnaq Created 13.02.2005
 */
public class MercatorFrame extends JFrame {
    private static final int                FORM_WIDTH                      = 1400;
    private static final int                UNIVERSE_GENERATION_RANDOM_SEED = 0;
    private              boolean            enableEventFiring               = true;
    private              JTextField         fieldUniverseCredits;
    private              JTextField         fieldUniverseTime;
    private              JButton            jButtonExit;
    private              JButton            jButtonUniverseStart;
    private              JButton            jButtonUniverseStep;
    private              JButton            jButtonUniverseStop;
    private              javax.swing.JPanel jContentPane;
    private              JPanel             jPanelEvents;
    private              JPanel             jPanelGui;
    private              JPanel             jPanelGuiNoMenu;
    private              JPanel             jPanelPlanets;
    private              JPanel             jPanelSystems;
    private              JPanel             jPanelTraders;
    private              JPanel             jPanelUniverse;
    private              JPanel             jPanelUniverseCreate;
    private              JPanel             jPanelUniverseFrameRate;
    private              JPanel             jPanelUniverseGuiConfig;
    private              JPanel             jPanelUniverseProperties;
    private              JPanel             jPanelUniverseStep;
    private              JTabbedPane        jTabbedPane;
    private              JTable             jTableEvents;
    private              JTable             jTablePlanets;
    private              JTable             jTableSelectedPlanetEvents;
    private              JTable             jTableSelectedPlanetFactories;
    private              JTable             jTableSelectedPlanetGoods;
    private              JTable             jTableSelectedPlanetSimEvents;
    private              JTable             jTableSelectedPlanetSims;
    private              JTable             jTableSelectedTraderEvents;
    private              JTable             jTableSelectedTraderGoods;
    private              JTable             jTableSelectedTraderNeeds;
    private              JTable             jTableSelectedTraderSimEvents;
    private              JTable             jTableSystems;
    private              JTable             jTableTraders;
    private              JToolBar           jToolBarMain;
    private              int                lastTablePlanetsRowIndex        = -1;
    private              JTextField         planetCredits;
    private              JTextField         planetName;
    private              JTextField         traderCredits;
    private              JTextField         traderDestinationPlanet;
    private              JTextField         traderFilterField;
    private              JTextField         traderName;
    private              JTextField         traderNextWaypoint;
    private              JTextField         traderPreviousWaypoint;
    private              JTextField         traderSourcePlanet;
    //	private ScreenListener screen;
    private final        Universe           universe;

    public MercatorFrame(final Universe universe) throws Exception {
        super();
        this.universe = universe;
        initialize();

    }

    private void createPlanetInfo(JTabbedPane jPlanetTabbedPane) {
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
                jPanelPlanetInfo.add(jPanel, BorderLayout.NORTH);
            }
            jPlanetTabbedPane.addTab("Info", null, jPanelPlanetInfo, "Planet's basic information");
        }
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

    protected void drawUniverse() {
        enableEventFiring = false;
        // screen.draw( image );
        // jPanelMap.repaint();
        {
            int rowIndex = -1;
            if (universe.selectedTrader != null)
                rowIndex = universe.traderList.indexOf(universe.selectedTrader);
            else
                rowIndex = jTableTraders.getSelectedRow();
            ((AbstractTableModel) jTableTraders.getModel()).fireTableDataChanged();
            if (rowIndex > -1) {
                // rowIndex = jTablePlanets.convertRowIndexToModel( rowIndex );
//                jTableTraders.setRowSelectionInterval(rowIndex, rowIndex);
                // Convert model index to view index for filtered table
                int viewIndex = jTableTraders.convertRowIndexToView(rowIndex);
                if (viewIndex > -1) {
                    jTableTraders.setRowSelectionInterval(viewIndex, viewIndex);
                }
            }

        }
        {
            final int rowIndex = jTableEvents.getSelectedRow();
            ((AbstractTableModel) jTableEvents.getModel()).fireTableDataChanged();
            if (rowIndex > -1) {
                jTableEvents.setRowSelectionInterval(rowIndex, rowIndex);
            }
        }
        ((AbstractTableModel) jTableSelectedTraderGoods.getModel()).fireTableDataChanged();
        ((AbstractTableModel) jTableSelectedTraderNeeds.getModel()).fireTableDataChanged();
        ((AbstractTableModel) jTableSelectedTraderEvents.getModel()).fireTableDataChanged();
        ((AbstractTableModel) jTableSelectedTraderSimEvents.getModel()).fireTableDataChanged();
        ((AbstractTableModel) jTableSystems.getModel()).fireTableDataChanged();
        {
            final int oldIndex = jTablePlanets.getSelectedRow();
            int       newIndex = -2;
            if (universe.selectedPlanet != null) {
                if (oldIndex > -1) {
                    final Planet selectedPlanet = universe.planetList.get(oldIndex);
                    //					System.out.println("found " + selectedPlanet.getName());
                    if (selectedPlanet != universe.selectedPlanet) {
                        newIndex = universe.planetList.getIndex(universe.selectedPlanet);
                    } else
                        newIndex = oldIndex;
                } else {
                    newIndex = universe.planetList.getIndex(universe.selectedPlanet);
                }
            }
            ((AbstractTableModel) jTablePlanets.getModel()).fireTableDataChanged();
            enableEventFiring = false;
            if (newIndex > -1) {
                jTablePlanets.setRowSelectionInterval(newIndex, newIndex);
                //				System.out.println("selecting " + newIndex);
            } else {
                //				System.out.println("selecting -1");

            }
        }
        ((AbstractTableModel) jTableSelectedPlanetGoods.getModel()).fireTableDataChanged();
        ((AbstractTableModel) jTableSelectedPlanetFactories.getModel()).fireTableDataChanged();
        ((AbstractTableModel) jTableSelectedPlanetSims.getModel()).fireTableDataChanged();
        ((AbstractTableModel) jTableSelectedPlanetEvents.getModel()).fireTableDataChanged();
        ((AbstractTableModel) jTableSelectedPlanetSimEvents.getModel()).fireTableDataChanged();
        updatePlanetInfo();
        updateTraderInfo();
        fieldUniverseTime.setText(String.valueOf(universe.currentTime));
        fieldUniverseCredits.setText(String.valueOf(universe.queryCredits(false)));
        enableEventFiring = true;
    }

    // -------------------------------------------------------------------------
    // ---EVENTS
    // -------------------------------------------------------------------------
    private JPanel getEventPanel() {
        if (jPanelEvents == null) {
            jPanelEvents = new JPanel();
            jPanelEvents.setLayout(new BorderLayout());
            {
                jTableEvents = new JTable(new EventTableModel(universe));
                jTableEvents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                jTableEvents.setAutoCreateRowSorter(true);
                jTableEvents.getColumnModel().getColumn(0).setPreferredWidth(50);
                jTableEvents.getColumnModel().getColumn(1).setPreferredWidth(100);
                jTableEvents.getColumnModel().getColumn(2).setPreferredWidth(500);
                jTableEvents.getColumnModel().getColumn(3).setPreferredWidth(100);
                jTableEvents.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                    @Override
                    public void valueChanged(final ListSelectionEvent e) {
                        if (enableEventFiring) {
                            if (!e.getValueIsAdjusting()) {
                                int rowIndex = jTableEvents.getSelectedRow();
                                if (rowIndex > -1) {
                                    rowIndex = jTableEvents.convertRowIndexToModel(rowIndex);
                                    universe.selectEvent(universe.eventManager.eventList.get(rowIndex));
                                }
                            }
                        }
                    }
                });
                jTableEvents.setFillsViewportHeight(true);
                final JScrollPane jScroll = new JScrollPane(jTableEvents);
                jPanelEvents.add(jScroll, BorderLayout.CENTER);
            }
        }
        return jPanelEvents;
    }

    private javax.swing.JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new javax.swing.JPanel();
            jContentPane.setBackground(Color.GREEN);
            jContentPane.setBorder(new LineBorder(new Color(0, 0, 0)));
            jContentPane.setLayout(new java.awt.BorderLayout());
            // Remove the event panel from the east side to give tables more space
            jContentPane.add(getJPanelGui(), BorderLayout.CENTER);
        }
        return jContentPane;
    }

    private JPanel getJPanelGui() {
        if (jPanelGui == null) {
            jPanelGui = new JPanel();
            jPanelGui.setLayout(new BorderLayout());
            {
                final JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.add(getJToolBarMain(), null/* BorderLayout.NORTH */);
                panel.add(getJPanelUniverseStep(), null);
                jPanelGui.add(panel, BorderLayout.NORTH);
            }
            jPanelGui.add(getJPanelGuiNoMenu(), BorderLayout.CENTER);
        }
        return jPanelGui;
    }

    private JPanel getJPanelGuiNoMenu() {
        if (jPanelGuiNoMenu == null) {
            jPanelGuiNoMenu = new JPanel();
            jPanelGuiNoMenu.setLayout(new BorderLayout());
            //			jPanelGuiNoMenu.setPreferredSize(new Dimension(500, 112));
            jPanelGuiNoMenu.add(getJTabbedPane(), BorderLayout.CENTER);
        }
        return jPanelGuiNoMenu;
    }

    private JPanel getJPanelPlanets() {
        if (jPanelPlanets == null) {
            jPanelPlanets = new JPanel();
            jPanelPlanets.setLayout(new BorderLayout());

            {
                final JPanel jPanelGoup1 = new JPanel();
                jPanelGoup1.setLayout(new BorderLayout());
                {
                    final JLabel jLabel = new JLabel();
                    jLabel.setText("Planets");
                    final JPanel jPanel = new JPanel();
                    jPanel.setLayout(new GridBagLayout());
                    jPanel.add(jLabel, new GridBagConstraints());
                    jPanelGoup1.add(jPanel, BorderLayout.NORTH);
                }
                {
                    jTablePlanets = new JTable(new PlanetsTableModel(universe));
                    jTablePlanets.setAutoCreateRowSorter(true);
                    jTablePlanets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
                    jPanelGoup1.add(jScroll, BorderLayout.CENTER);
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

                    createPlanetInfo(jPlanetTabbedPane);

                    // Goods tab
                    {
                        final JPanel jPanelGoods = new JPanel();
                        jPanelGoods.setLayout(new BorderLayout());
                        {
                            jTableSelectedPlanetGoods = new JTable(new SelectedPlanetGoodsTableModel(universe));
                            jTableSelectedPlanetGoods.setFillsViewportHeight(true);
                            final JScrollPane jScroll = new JScrollPane(jTableSelectedPlanetGoods);
                            jScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                            jPanelGoods.add(jScroll, BorderLayout.CENTER);
                        }
                        jPlanetTabbedPane.addTab("Goods", null, jPanelGoods, "Planet's goods inventory");
                    }

                    // Factories tab
                    {
                        final JPanel jPanelFactories = new JPanel();
                        jPanelFactories.setLayout(new BorderLayout());
                        {
                            jTableSelectedPlanetFactories = new JTable(new SelectedPlanetFactoriesTableModel(universe));
                            jTableSelectedPlanetFactories.setFillsViewportHeight(true);
                            final JScrollPane jScroll = new JScrollPane(jTableSelectedPlanetFactories);
                            jPanelFactories.add(jScroll, BorderLayout.CENTER);
                        }
                        jPlanetTabbedPane.addTab("Factories", null, jPanelFactories, "Planet's factories");
                    }

                    // Sims tab
                    {
                        final JPanel jPanelSims = new JPanel();
                        jPanelSims.setLayout(new BorderLayout());
                        {
                            jTableSelectedPlanetSims = new JTable(new SelectedPlanetSimsTableModel(universe));
                            jTableSelectedPlanetSims.setFillsViewportHeight(true);
                            final JScrollPane jScroll = new JScrollPane(jTableSelectedPlanetSims);
                            jScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                            jPanelSims.add(jScroll, BorderLayout.CENTER);
                        }
                        jPlanetTabbedPane.addTab("Sims", null, jPanelSims, "Planet's sims (inhabitants)");
                    }

                    // Events tab (now only non-SimEvents)
                    {
                        final JPanel jPanelEvents = new JPanel();
                        jPanelEvents.setLayout(new BorderLayout());
                        {
                            jTableSelectedPlanetEvents = new JTable(new SelectedPlanetEventsTableModel(universe));
                            jTableSelectedPlanetEvents.setFillsViewportHeight(true);
                            jTableSelectedPlanetEvents.setAutoCreateRowSorter(true);
                            jTableSelectedPlanetEvents.getColumnModel().getColumn(0).setPreferredWidth(80);
                            jTableSelectedPlanetEvents.getColumnModel().getColumn(1).setPreferredWidth(80);
                            jTableSelectedPlanetEvents.getColumnModel().getColumn(2).setPreferredWidth(60);
                            jTableSelectedPlanetEvents.getColumnModel().getColumn(3).setPreferredWidth(80);
                            jTableSelectedPlanetEvents.getColumnModel().getColumn(4).setPreferredWidth(400);
                            final JScrollPane jScroll = new JScrollPane(jTableSelectedPlanetEvents);
                            jScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                            jPanelEvents.add(jScroll, BorderLayout.CENTER);
                        }
                        jPlanetTabbedPane.addTab("Events", null, jPanelEvents, "Planet's general event history");
                    }

                    // SimEvents tab (only SimEvents)
                    {
                        final JPanel jPanelSimEvents = new JPanel();
                        jPanelSimEvents.setLayout(new BorderLayout());
                        {
                            jTableSelectedPlanetSimEvents = new JTable(new SelectedPlanetSimEventsTableModel(universe));
                            jTableSelectedPlanetSimEvents.setFillsViewportHeight(true);
                            jTableSelectedPlanetSimEvents.setAutoCreateRowSorter(true);
                            jTableSelectedPlanetSimEvents.getColumnModel().getColumn(0).setPreferredWidth(80);
                            jTableSelectedPlanetSimEvents.getColumnModel().getColumn(1).setPreferredWidth(80);
                            jTableSelectedPlanetSimEvents.getColumnModel().getColumn(2).setPreferredWidth(60);
                            jTableSelectedPlanetSimEvents.getColumnModel().getColumn(3).setPreferredWidth(80);
                            jTableSelectedPlanetSimEvents.getColumnModel().getColumn(4).setPreferredWidth(400);
                            final JScrollPane jScroll = new JScrollPane(jTableSelectedPlanetSimEvents);
                            jScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                            jPanelSimEvents.add(jScroll, BorderLayout.CENTER);
                        }
                        jPlanetTabbedPane.addTab("SimEvents", null, jPanelSimEvents, "Planet's simulation event history");
                    }

                    jPanelSelected.add(jPlanetTabbedPane, BorderLayout.CENTER);
                }

                // Use JSplitPane to create 50/50 split that responds to window resizing
                final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jPanelGoup1, jPanelSelected);
                splitPane.setResizeWeight(0.5); // 50/50 split
                splitPane.setOneTouchExpandable(true);
                splitPane.setContinuousLayout(true);

                jPanelPlanets.add(splitPane, BorderLayout.CENTER);
            }
        }
        return jPanelPlanets;
    }

    // -------------------------------------------------------------------------
    // ---Systems
    // -------------------------------------------------------------------------
    private JPanel getJPanelSystems() {
        if (jPanelSystems == null) {
            jPanelSystems = new JPanel();
            jPanelSystems.setLayout(new BorderLayout());

            {
                final JLabel jLabel = new JLabel();
                jLabel.setText("Systems");
                final JPanel jPanel = new JPanel();
                jPanel.setLayout(new GridBagLayout());
                jPanel.add(jLabel, new GridBagConstraints());
                jPanelSystems.add(jLabel, BorderLayout.NORTH);
            }

            {
                jTableSystems = new JTable(new SectorsTableModel(universe.sectorList));
                jTableSystems.setFillsViewportHeight(true);
                final JScrollPane jScroll = new JScrollPane(jTableSystems);
                jPanelSystems.add(jScroll, BorderLayout.CENTER);
            }
        }
        return jPanelSystems;
    }

    // -------------------------------------------------------------------------
    // ---Traders
    // -------------------------------------------------------------------------
    private JPanel getJPanelTraders() {
        if (jPanelTraders == null) {
            jPanelTraders = new JPanel();
            jPanelTraders.setLayout(new BorderLayout());

            {
                final JPanel jPanelTradersList = new JPanel();
                jPanelTradersList.setLayout(new BorderLayout());
                {
                    final JLabel jLabel = new JLabel();
                    jLabel.setText("Traders");
                    final JPanel jPanel = new JPanel();
                    jPanel.setLayout(new GridBagLayout());
                    jPanel.add(jLabel, new GridBagConstraints());
                    jPanelTradersList.add(jPanel, BorderLayout.NORTH);
                }

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

                    // Get reference to filter field from the panel above

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
                            jTableSelectedTraderNeeds = new JTable(new SelectedTraderNeedsTableModel(universe));
                            jTableSelectedTraderNeeds.setFillsViewportHeight(true);
                            final JScrollPane jScroll = new JScrollPane(jTableSelectedTraderNeeds);
                            jPanelTraderNeeds.add(jScroll, BorderLayout.CENTER);
                        }
                        jTraderTabbedPane.addTab("Needs", null, jPanelTraderNeeds, "Trader's needs");
                    }

                    // Goods tab
                    {
                        final JPanel jPanelGoods = new JPanel();
                        jPanelGoods.setLayout(new BorderLayout());
                        {
                            jTableSelectedTraderGoods = new JTable(new SelectedTraderGoodsTableModel(universe));
                            jTableSelectedTraderGoods.setFillsViewportHeight(true);
                            final JScrollPane jScroll = new JScrollPane(jTableSelectedTraderGoods);
                            jPanelGoods.add(jScroll, BorderLayout.CENTER);
                        }
                        jTraderTabbedPane.addTab("Goods", null, jPanelGoods, "Trader's goods inventory");
                    }

                    // Events tab
                    {
                        final JPanel jPanelEvents = new JPanel();
                        jPanelEvents.setLayout(new BorderLayout());
                        {
                            jTableSelectedTraderEvents = new JTable(new SelectedTraderEventsTableModel(universe));
                            jTableSelectedTraderEvents.setFillsViewportHeight(true);
                            jTableSelectedTraderEvents.setAutoCreateRowSorter(true);
                            jTableSelectedTraderEvents.getColumnModel().getColumn(0).setPreferredWidth(80);
                            jTableSelectedTraderEvents.getColumnModel().getColumn(1).setPreferredWidth(80);
                            jTableSelectedTraderEvents.getColumnModel().getColumn(2).setPreferredWidth(60);
                            jTableSelectedTraderEvents.getColumnModel().getColumn(3).setPreferredWidth(80);
                            jTableSelectedTraderEvents.getColumnModel().getColumn(4).setPreferredWidth(400);
                            final JScrollPane jScroll = new JScrollPane(jTableSelectedTraderEvents);
                            jPanelEvents.add(jScroll, BorderLayout.CENTER);
                        }
                        jTraderTabbedPane.addTab("Events", null, jPanelEvents, "Trader's general event history");
                    }

                    // SimEvents tab (only SimEvents)
                    {
                        final JPanel jPanelSimEvents = new JPanel();
                        jPanelSimEvents.setLayout(new BorderLayout());
                        {
                            jTableSelectedTraderSimEvents = new JTable(new SelectedTraderSimEventsTableModel(universe));
                            jTableSelectedTraderSimEvents.setFillsViewportHeight(true);
                            jTableSelectedTraderSimEvents.setAutoCreateRowSorter(true);
                            jTableSelectedTraderSimEvents.getColumnModel().getColumn(0).setPreferredWidth(80);
                            jTableSelectedTraderSimEvents.getColumnModel().getColumn(1).setPreferredWidth(80);
                            jTableSelectedTraderSimEvents.getColumnModel().getColumn(2).setPreferredWidth(60);
                            jTableSelectedTraderSimEvents.getColumnModel().getColumn(3).setPreferredWidth(80);
                            jTableSelectedTraderSimEvents.getColumnModel().getColumn(4).setPreferredWidth(400);
                            final JScrollPane jScroll = new JScrollPane(jTableSelectedTraderSimEvents);
                            jPanelSimEvents.add(jScroll, BorderLayout.CENTER);
                        }
                        jTraderTabbedPane.addTab("SimEvents", null, jPanelSimEvents, "Trader's simulation event history");
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

    // -------------------------------------------------------------------------
    // ---GUI-UNIVERSE TAB
    // -------------------------------------------------------------------------
    private JPanel getJPanelUniverse() {
        if (jPanelUniverse == null) {
            jPanelUniverse = new JPanel();
            jPanelUniverse.setLayout(new BorderLayout());

            final JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(getJPanelUniverseCreate(), null);
            panel.add(getJPanelUniverseFrameRate(), null);
            panel.add(getJPanelUniverseGuiConfig(), null);
            panel.add(getJPanelUniverseProperties(), null);

            jPanelUniverse.add(panel, BorderLayout.NORTH);

            // Add the event panel to the lower part of the Universe tab
            {
                final JPanel jPanelEventsWrapper = new JPanel();
                jPanelEventsWrapper.setLayout(new BorderLayout());
                {
                    final JLabel jLabel = new JLabel();
                    jLabel.setText("Universe Events");
                    final JPanel jPanel = new JPanel();
                    jPanel.setLayout(new GridBagLayout());
                    jPanel.add(jLabel, new GridBagConstraints());
                    jPanelEventsWrapper.add(jPanel, BorderLayout.NORTH);
                }
                jPanelEventsWrapper.add(getEventPanel(), BorderLayout.CENTER);
                jPanelUniverse.add(jPanelEventsWrapper, BorderLayout.CENTER);
            }
        }
        return jPanelUniverse;
    }

    private JPanel getJPanelUniverseCreate() {
        if (jPanelUniverseCreate == null) {
            final JLabel jLabelCaption = new JLabel();
            jLabelCaption.setText("Create universe");
            final JPanel jPanelCaption = new JPanel();
            jPanelCaption.setLayout(new GridBagLayout());
            jPanelCaption.add(jLabelCaption, new GridBagConstraints());
//            jPanelCaption.setBackground(new Color(198, 242, 255));

            final JButton jButtonCreate = new JButton();
            jButtonCreate.setText("Create");
            jButtonCreate.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(final java.awt.event.ActionEvent e) {
                    try {
//                        universe.create(UNIVERSE_GENERATION_RANDOM_SEED, 10, 10L * TimeUnit.TICKS_PER_DAY, gameEngine);//TODO fix generating a new universe.
                    } catch (final Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    drawUniverse();
                }
            });

            final JToolBar jToolBar = new JToolBar();
            jToolBar.add(jButtonCreate);

            jPanelUniverseCreate = new JPanel();
            jPanelUniverseCreate.setLayout(new BorderLayout());
            jPanelUniverseCreate.add(jPanelCaption, BorderLayout.NORTH);
            jPanelUniverseCreate.add(jToolBar, BorderLayout.CENTER);
        }
        return jPanelUniverseCreate;
    }

    private JPanel getJPanelUniverseFrameRate() {
        if (jPanelUniverseFrameRate == null) {
            final JLabel jLabelCaption = new JLabel();
            jLabelCaption.setText("Frame rate");
            final JPanel jPanelCaption = new JPanel();
            jPanelCaption.setLayout(new GridBagLayout());
            jPanelCaption.add(jLabelCaption, new GridBagConstraints());

            final JSlider jSliderFrameRate = new JSlider();
            jSliderFrameRate.setPreferredSize(new Dimension(100, 16));
            final JToolBar jToolBar = new JToolBar();
            jToolBar.add(jSliderFrameRate);

            jPanelUniverseFrameRate = new JPanel();
            jPanelUniverseFrameRate.setLayout(new BorderLayout());
            jPanelUniverseFrameRate.add(jPanelCaption, BorderLayout.NORTH);
            jPanelUniverseFrameRate.add(jToolBar, java.awt.BorderLayout.CENTER);
        }
        return jPanelUniverseFrameRate;
    }

    private JPanel getJPanelUniverseGuiConfig() {
        if (jPanelUniverseGuiConfig == null) {
            final JLabel jLabelCaption = new JLabel();
            jLabelCaption.setText("GUI config");
            final JPanel jPanelCaption = new JPanel();
            jPanelCaption.setLayout(new GridBagLayout());
            jPanelCaption.add(jLabelCaption, new GridBagConstraints());
//            jPanelCaption.setBackground(new Color(198, 242, 255));

            final JToolBar    jToolBar    = new JToolBar();
            final ButtonGroup buttonGroup = new ButtonGroup();

            {
                final JRadioButton rdbtn = new JRadioButton("Show good price");
                rdbtn.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(final ItemEvent e) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            //							screen.setShowGood(ShowGood.Price);
                        } else {
                        }
                    }
                });
                jToolBar.add(rdbtn);
                buttonGroup.add(rdbtn);
            }
            {
                final JRadioButton rdbtn = new JRadioButton("Show good name");
                rdbtn.setSelected(true);
                rdbtn.addItemListener(new ItemListener() {

                    @Override
                    public void itemStateChanged(final ItemEvent e) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            //							screen.setShowGood(ShowGood.Name);
                        } else {
                        }
                    }
                });
                jToolBar.add(rdbtn);
                buttonGroup.add(rdbtn);
            }
            {
                final JRadioButton rdbtn = new JRadioButton("Show good volume");
                rdbtn.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(final ItemEvent e) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            //							screen.setShowGood(ShowGood.Volume);
                        } else {
                        }
                    }
                });
                jToolBar.add(rdbtn);
                buttonGroup.add(rdbtn);
            }
            jPanelUniverseGuiConfig = new JPanel();
            jPanelUniverseGuiConfig.setLayout(new BorderLayout());
            jPanelUniverseGuiConfig.add(jPanelCaption, BorderLayout.NORTH);
            jPanelUniverseGuiConfig.add(jToolBar, BorderLayout.CENTER);
        }
        return jPanelUniverseGuiConfig;
    }

    private JPanel getJPanelUniverseProperties() {
        if (jPanelUniverseProperties == null) {
            jPanelUniverseProperties = new JPanel();
            jPanelUniverseProperties.setLayout(new BorderLayout());
            {
                final JLabel jLabel = new JLabel();
                jLabel.setText("Universe properties");
                final JPanel jPanel = new JPanel();
                jPanel.setLayout(new GridBagLayout());
//                jPanel.setBackground(new Color(198, 242, 255));
                jPanel.add(jLabel, new GridBagConstraints());
                jPanelUniverseProperties.add(jPanel, BorderLayout.NORTH);
            }
            {
                final JPanel jPanel = new JPanel();
                jPanel.setLayout(new GridLayout(0, 2, 0, 0));
                {
                    final JLabel jLabel = new JLabel();
                    jLabel.setText("Time");
                    jPanel.add(jLabel);
                }
                {
                    fieldUniverseTime = new JTextField();
                    fieldUniverseTime.setColumns(10);
                    jPanel.add(fieldUniverseTime);
                }
                {
                    final JLabel jLabel = new JLabel();
                    jLabel.setText("Credit");
                    jPanel.add(jLabel);
                }
                {
                    fieldUniverseCredits = new JTextField();
                    fieldUniverseCredits.setColumns(10);
                    jPanel.add(fieldUniverseCredits);
                }
                jPanelUniverseProperties.add(jPanel, BorderLayout.SOUTH);
            }
        }
        return jPanelUniverseProperties;
    }

    private JPanel getJPanelUniverseStep() {
        if (jPanelUniverseStep == null) {
            jPanelUniverseStep = new JPanel();
            jPanelUniverseStep.setLayout(new BorderLayout());
            {
                final JLabel jLabel = new JLabel();
                jLabel.setText("Time control");
                final JPanel jPanel = new JPanel();
                jPanel.setLayout(new GridBagLayout());
                jPanel.add(jLabel, new GridBagConstraints());
//                jPanel.setBackground(new Color(198, 242, 255));
                jPanelUniverseStep.add(jPanel, BorderLayout.NORTH);
            }
            {
                final JToolBar jToolBar = new JToolBar();
                {
                    jButtonUniverseStart = new JButton();
                    jButtonUniverseStart.setText("Start");
                    jButtonUniverseStart.addActionListener(new java.awt.event.ActionListener() {
                        @Override
                        public void actionPerformed(final java.awt.event.ActionEvent e) {
                            universe.setEnableTime(true);
                            jButtonUniverseStart.setEnabled(false);
                            jButtonUniverseStep.setEnabled(false);
                            jButtonUniverseStop.setEnabled(true);
                        }
                    });
                    jToolBar.add(jButtonUniverseStart);
                }
                {
                    jButtonUniverseStep = new JButton();
                    jButtonUniverseStep.setText("Step");
                    jButtonUniverseStep.addActionListener(new java.awt.event.ActionListener() {

                        @Override
                        public void actionPerformed(final java.awt.event.ActionEvent e) {
                            jButtonUniverseStart.setEnabled(false);
                            jButtonUniverseStep.setEnabled(false);
                            try {
                                universe.advanceInTime(true);
                            } catch (final Exception e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                            drawUniverse();
                            jButtonUniverseStart.setEnabled(true);
                            jButtonUniverseStep.setEnabled(true);
                        }
                    });
                    jToolBar.add(jButtonUniverseStep);
                }
                {
                    jButtonUniverseStop = new JButton();
                    jButtonUniverseStop.setText("Pause");
                    jButtonUniverseStop.addActionListener(new java.awt.event.ActionListener() {
                        @Override
                        public void actionPerformed(final java.awt.event.ActionEvent e) {
                            universe.setEnableTime(false);
                            jButtonUniverseStart.setEnabled(true);
                            jButtonUniverseStep.setEnabled(true);
                            jButtonUniverseStop.setEnabled(false);
                        }
                    });
                    jToolBar.add(jButtonUniverseStop);
                }
                jPanelUniverseStep.add(jToolBar, BorderLayout.CENTER);

            }
        }
        return jPanelUniverseStep;
    }

    private JTabbedPane getJTabbedPane() {
        if (jTabbedPane == null) {
            jTabbedPane = new JTabbedPane();
            jTabbedPane.addTab("Universe", null, getJPanelUniverse(), null);
            jTabbedPane.addTab("Systems", null, getJPanelSystems(), null);
            jTabbedPane.addTab("Planets", null, getJPanelPlanets(), null);
            jTabbedPane.addTab("Traders", null, getJPanelTraders(), null);
            jTabbedPane.setSelectedComponent(getJPanelTraders());
        }
        return jTabbedPane;
    }

    // -------------------------------------------------------------------------
    // ---GUI-MAIN-MENU
    // -------------------------------------------------------------------------
    private JToolBar getJToolBarMain() {
        if (jToolBarMain == null) {
            jToolBarMain = new JToolBar();

            {
                jButtonExit = new JButton();
                jButtonExit.setText("Exit");
                jButtonExit.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(final java.awt.event.ActionEvent e) {
                        dispatchEvent(new WindowEvent(MercatorFrame.this, WindowEvent.WINDOW_CLOSING));
                    }
                });
                jToolBarMain.add(jButtonExit);
            }
        }
        return jToolBarMain;
    }

    private void initialize() throws Exception {
        // Enable live resizing
        System.setProperty("sun.awt.noerasebackground", "true");
        Toolkit.getDefaultToolkit().setDynamicLayout(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(new Dimension(FORM_WIDTH, 1000));
//        this.setBackground(Color.RED);
        this.setContentPane(getJContentPane());
        this.setTitle("Mercator 1.0");
        this.setExtendedState(Frame.MAXIMIZED_VERT);
        this.setUndecorated(false);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(final WindowEvent e) {
                //				screen.dispose();
                dispose();
                //				System.exit(0);
            }
        });

        final ActionListener taskPerformer = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                drawUniverse();
                //				if (universe.isEnableTime()) {
                //				} else {
                //				}
            }
        };
        new Timer(500, taskPerformer).start();
    }

    private void updatePlanetInfo() {
        Planet planet = universe.selectedPlanet;
        if (planet != null && !universe.selectedPlanet.getName().equals(planetName.getText())) {
            planetName.setText(planet.getName());
        }
        if (planet != null && !String.valueOf(planet.getCredits()).equals(planetCredits.getText())) {
            planetCredits.setText(String.valueOf(planet.getCredits()));
        }
    }

    private void updateTraderInfo() {
        Trader trader = universe.selectedTrader;
        if (trader != null) {
            if (!trader.getName().equals(traderName.getText())) {
                traderName.setText(trader.getName());
            }
            if (!String.valueOf(trader.getCredits()).equals(traderCredits.getText())) {
                traderCredits.setText(String.valueOf(trader.getCredits()));
            }
            if (trader.navigator.destinationPlanet != null && !trader.navigator.destinationPlanet.getName().equals(traderDestinationPlanet.getText())) {
                traderDestinationPlanet.setText(trader.navigator.destinationPlanet.getName());
            }
            if (trader.navigator.nextWaypoint != null && !trader.navigator.nextWaypoint.getName().equals(traderNextWaypoint.getText())) {
                traderNextWaypoint.setText(trader.navigator.nextWaypoint.getName());
            }
            if (trader.navigator.previousWaypoint != null && !trader.navigator.previousWaypoint.getName().equals(traderPreviousWaypoint.getText())) {
                traderPreviousWaypoint.setText(trader.navigator.previousWaypoint.getName());
            }
            if (trader.navigator.sourcePlanet != null && !trader.navigator.sourcePlanet.getName().equals(traderSourcePlanet.getText())) {
                traderSourcePlanet.setText(trader.navigator.sourcePlanet.getName());
            }

        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
