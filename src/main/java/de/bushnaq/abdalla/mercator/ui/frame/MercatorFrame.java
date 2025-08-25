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

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
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
    private              JPanel             jPanelSystems;
    private              JPanel             jPanelUniverse;
    private              JPanel             jPanelUniverseCreate;
    private              JPanel             jPanelUniverseFrameRate;
    private              JPanel             jPanelUniverseGuiConfig;
    private              JPanel             jPanelUniverseProperties;
    private              JPanel             jPanelUniverseStep;
    private              JTabbedPane        jTabbedPane;
    private              JTable             jTableEvents;
    private              JTable             jTableSystems;
    private              JToolBar           jToolBarMain;
    private final        PlanetUI           planetUI;
    private final        TraderUI           traderUI;
    private final        Universe           universe;

    public MercatorFrame(final Universe universe) throws Exception {
        super();
        this.universe = universe;
        this.traderUI = new TraderUI(universe);
        this.planetUI = new PlanetUI(universe);
        initialize();
    }

    protected void drawUniverse() {
        enableEventFiring = false;
        // Set event firing state for UI components
        traderUI.setEnableEventFiring(enableEventFiring);
        planetUI.setEnableEventFiring(enableEventFiring);

        {
            final int rowIndex = jTableEvents.getSelectedRow();
            ((AbstractTableModel) jTableEvents.getModel()).fireTableDataChanged();
            if (rowIndex > -1) {
                jTableEvents.setRowSelectionInterval(rowIndex, rowIndex);
            }
        }

        // Update trader UI components
        traderUI.updateTraderTableData();

        // Update planet UI components
        planetUI.updatePlanetTableData();

        ((AbstractTableModel) jTableSystems.getModel()).fireTableDataChanged();

        // Update info panels
        planetUI.updatePlanetInfo();
        traderUI.updateTraderInfo();

        fieldUniverseTime.setText(String.valueOf(universe.currentTime));
        fieldUniverseCredits.setText(String.valueOf(universe.queryCredits(false)));
        enableEventFiring = true;

        // Re-enable event firing for UI components
        traderUI.setEnableEventFiring(enableEventFiring);
        planetUI.setEnableEventFiring(enableEventFiring);
    }

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

    // -------------------------------------------------------------------------
    // ---Systems
    // -------------------------------------------------------------------------
    private JPanel getJPanelSystems() {
        if (jPanelSystems == null) {
            jPanelSystems = new JPanel();
            jPanelSystems.setLayout(new BorderLayout());

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
            jTabbedPane.addTab("Planets", null, planetUI.getJPanelPlanets(), null);
            jTabbedPane.addTab("Traders", null, traderUI.getJPanelTraders(), null);
            jTabbedPane.setSelectedComponent(traderUI.getJPanelTraders());
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


} // @jve:decl-index=0:visual-constraint="10,10"
