/* ---------------------------------------------------------------------------
 * BEGIN_PROJECT_HEADER
 *
 *       RRRR  RRR    IIIII    CCCCC      OOOO    HHH  HHH
 *       RRRR  RRRR   IIIII   CCC  CC    OOOOOO   HHH  HHH
 *       RRRR  RRRRR  IIIII  CCCC  CCC  OOO  OOO  HHH  HHH
 *       RRRR  RRRR   IIIII  CCCC       OOO  OOO  HHH  HHH
 *       RRRR RRRR    IIIII  CCCC       OOO  OOO  HHHHHHHH
 *       RRRR  RRRR   IIIII  CCCC       OOO  OOO  HHH  HHH
 *       RRRR   RRRR  IIIII  CCCC  CCC  OOO  OOO  HHH  HHH
 *       RRRR   RRRR  IIIII   CCC  CC    OOOOOO   HHH  HHH
 *       RRRR   RRRR  IIIII    CCCCC      OOOO    HHH  HHH
 *
 *       Copyright 2005 by Ricoh Europe B.V.
 *
 *       This material contains, and is part of a computer software program
 *       which is, proprietary and confidential information owned by Ricoh
 *       Europe B.V.
 *       The program, including this material, may not be duplicated, disclosed
 *       or reproduced in whole or in part for any purpose without the express
 *       written authorization of Ricoh Europe B.V.
 *       All authorized reproductions must be marked with this legend.
 *
 *       Department : European Development and Support Center
 *       Group      : Printing & Fax Solution Group
 *       Author(s)  : bushnaq
 *       Created    : 13.02.2005
 *
 *       Project    : com.abdalla.bushnaq.mercator
 *       Product Id : <Product Key Index>
 *       Component  : <Project Component Name>
 *       Compiler   : Java/Eclipse
 *
 * END_PROJECT_HEADER
 * -------------------------------------------------------------------------*/
package de.bushnaq.abdalla.mercator.gui.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.util.TimeUnit;

/**
 * @author bushnaq Created 13.02.2005
 */
public class MercatorFrame extends JFrame {
	private static final int FORM_WIDTH = 1400;
	private static final long serialVersionUID = -614297318910428464L;
	private static final int UNIVERSE_GENERATION_RANDOM_SEED = 0;
	private boolean enableEventFiring = true;
	private JTextField fieldSelectedPlanetCredits;
	private JTextField fieldSelectedPlanetName;
	private JTextField fieldSelectedTraderCredits;
	private JTextField fieldSelectedTraderName;
	private JTextField fieldUniverseCredits;
	private JTextField fieldUniverseTime;
	private JButton jButtonExit;
	private JButton jButtonUniverseStart;
	private JButton jButtonUniverseStep;
	private JButton jButtonUniverseStop;
	private javax.swing.JPanel jContentPane;
	private JPanel jPanelEvents;
	private JPanel jPanelGui;
	private JPanel jPanelGuiNoMenu;
	private JPanel jPanelPlanets;
	private JPanel jPanelSystems;
	private JPanel jPanelTraders;

	private JPanel jPanelUniverse;
	private JPanel jPanelUniverseCreate;

	private JPanel jPanelUniverseFrameRate;
	private JPanel jPanelUniverseGuiConfig;
	private JPanel jPanelUniverseProperties;
	private JPanel jPanelUniverseStep;
	private JTabbedPane jTabbedPane;
	private JTable jTableEvents;
	private JTable jTablePlanets;

	private JTable jTableSelectedPlanetFactories;
	private JTable jTableSelectedPlanetGoods;
	private JTable jTableSelectedPlanetSims;
	private JTable jTableSelectedTraderGoods;
	private JTable jTableSelectedTraderNeeds;
	private JTable jTableSystems;
	private JTable jTableTraders;

	private JToolBar jToolBarMain;
	// -------------------------------------------------------------------------
	// ---Planets
	// -------------------------------------------------------------------------
	int lastTablePlanetsRowIndex = -1;

	//	private ScreenListener screen;
	protected Universe universe;

	public MercatorFrame(final Universe universe) throws Exception {
		super();
		this.universe = universe;
		initialize();

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
				jTableTraders.setRowSelectionInterval(rowIndex, rowIndex);
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
		((AbstractTableModel) jTableSystems.getModel()).fireTableDataChanged();
		{
			final int oldIndex = jTablePlanets.getSelectedRow();
			int newIndex = -2;
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
		if (universe.selectedPlanet != null && !universe.selectedPlanet.getName().equals(fieldSelectedPlanetName.getText())) {
			fieldSelectedPlanetName.setText(universe.selectedPlanet.getName());
		}
		if (universe.selectedPlanet != null && !String.valueOf(universe.selectedPlanet.getCredits()).equals(fieldSelectedPlanetCredits.getText())) {
			fieldSelectedPlanetCredits.setText(String.valueOf(universe.selectedPlanet.getCredits()));
		}
		if (universe.selectedTrader != null) {
			if (!universe.selectedTrader.getName().equals(fieldSelectedTraderName.getText())) {
				fieldSelectedTraderName.setText(universe.selectedTrader.getName());
			}
			if (!String.valueOf(universe.selectedTrader.getCredits()).equals(fieldSelectedTraderCredits.getText())) {
				fieldSelectedTraderCredits.setText(String.valueOf(universe.selectedTrader.getCredits()));
			}
		}
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
			jContentPane.add(getEventPanel(), BorderLayout.EAST);
			jContentPane.add(getJPanelGui(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanelMap
	 *
	 * @return javax.swing.JPanel
	 */
	/*
	 * private RcImagePanel getJPanelMap() { if ( jPanelMap == null ) { jPanelMap =
	 * new RcImagePanel(); jPanelMap.setLayout( new GridBagLayout() );
	 * jPanelMap.setBackground( Color.white ); jPanelMap.setPreferredSize( new
	 * Dimension( 30, 30 ) ); jPanelMap.addComponentListener( new
	 * java.awt.event.ComponentAdapter() {
	 *
	 * @Override public void componentResized( final java.awt.event.ComponentEvent e
	 * ) { drawUniverse( jPanelMap.getImage() ); } } ); jPanelMap.addMouseListener(
	 * new java.awt.event.MouseAdapter() { public void mouseClicked(
	 * java.awt.event.MouseEvent e ) { int virtualX = screen.transformIntoVirtualX(
	 * e.getX() ); int virtualY = screen.transformIntoVirtualY( e.getY() ); if ( (
	 * e.getModifiers() & InputEvent.BUTTON1_MASK ) != 0 ) { if ( e.isShiftDown() )
	 * { screen.moveCenter( e.getX(), e.getY() ); drawUniverse( jPanelMap.getImage()
	 * ); } else { Planet planet = screen.select( virtualX, virtualY ); if ( planet
	 * != null ) { int rowIndex = universe.planetList.indexOf( planet ); rowIndex =
	 * jTablePlanets.getRowSorter().convertRowIndexToView( rowIndex );
	 * jTablePlanets.setRowSelectionInterval( rowIndex, rowIndex ); } drawUniverse(
	 * jPanelMap.getImage() ); }
	 *
	 *
	 * } else if ( ( e.getModifiers() & InputEvent.BUTTON3_MASK ) != 0 ) { if (
	 * e.isShiftDown() ) { screen.moveCenter( e.getX(), e.getY() ); drawUniverse(
	 * jPanelMap.getImage() ); } } }
	 *
	 * } );
	 *
	 * } return jPanelMap; }
	 */

	// -------------------------------------------------------------------------
	// ---GUI
	// -------------------------------------------------------------------------
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
			jPanelGuiNoMenu.add(getJTabbedPane(), BorderLayout.NORTH);
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
					jPanel.setBackground(new Color(198, 242, 255));
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
											rowIndex = jTablePlanets.convertRowIndexToModel(rowIndex);
											try {
												universe.setSelected(universe.planetList.get(rowIndex), true);
											} catch (final Exception e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											}
										} /*else {
											lastTablePlanetsRowIndex = -1;
											jTablePlanets.clearSelection();
											universe.setSelected(null);
											}*/
									}
								}
							}
						}
					});
					jTablePlanets.setFillsViewportHeight(true);
					final JScrollPane jScroll = new JScrollPane(jTablePlanets);
					jScroll.setPreferredSize(new Dimension(-1, 200));
					jScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
					jPanelGoup1.add(jScroll, BorderLayout.SOUTH);
				}
				jPanelPlanets.add(jPanelGoup1, BorderLayout.NORTH);
			}
			{
				final JPanel jPanelSelected = new JPanel();
				jPanelSelected.setLayout(new BorderLayout());
				{
					final JPanel jPanelSelectedName = new JPanel();
					jPanelSelectedName.setLayout(new BorderLayout());
					{
						final JLabel jLabel = new JLabel();
						jLabel.setText("Selected Planet");
						final JPanel jPanel = new JPanel();
						jPanel.setLayout(new GridBagLayout());
						jPanel.setBackground(new Color(198, 242, 255));
						jPanel.add(jLabel, new GridBagConstraints());
						jPanelSelectedName.add(jPanel, BorderLayout.NORTH);
					}
					{
						final JPanel jPanel = new JPanel();
						jPanel.setLayout(new GridLayout(0, 2, 0, 0));
						{
							final JLabel jLabel = new JLabel();
							jLabel.setText("Name");
							jPanel.add(jLabel);
						}
						{
							fieldSelectedPlanetName = new JTextField();
							fieldSelectedPlanetName.setColumns(10);
							jPanel.add(fieldSelectedPlanetName);
						}
						{
							final JLabel jLabel = new JLabel();
							jLabel.setText("Credits");
							jPanel.add(jLabel);
						}
						{
							fieldSelectedPlanetCredits = new JTextField();
							fieldSelectedPlanetCredits.setColumns(10);
							jPanel.add(fieldSelectedPlanetCredits);
						}
						jPanelSelectedName.add(jPanel, BorderLayout.SOUTH);
					}
					jPanelSelected.add(jPanelSelectedName, BorderLayout.NORTH);
					{
						final JPanel jPanelGoods = new JPanel();
						jPanelGoods.setLayout(new BorderLayout());
						{
							final JLabel jLabel = new JLabel();
							jLabel.setText("Goods");
							final JPanel jPanel = new JPanel();
							jPanel.setLayout(new GridBagLayout());
							jPanel.setBackground(new Color(198, 242, 255));
							jPanel.add(jLabel, new GridBagConstraints());
							jPanelGoods.add(jPanel, BorderLayout.NORTH);
						}
						{
							jTableSelectedPlanetGoods = new JTable(new SelectedPlanetGoodsTableModel(universe));
							jTableSelectedPlanetGoods.setFillsViewportHeight(true);
							final JScrollPane jScroll = new JScrollPane(jTableSelectedPlanetGoods);
							jScroll.setPreferredSize(new Dimension(-1, 200));
							jScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
							jPanelGoods.add(jScroll, BorderLayout.SOUTH);
						}
						jPanelSelected.add(jPanelGoods, BorderLayout.CENTER);
					}
					{
						final JPanel jPanelFactories = new JPanel();
						jPanelFactories.setLayout(new BorderLayout());
						{
							final JLabel jLabel = new JLabel();
							jLabel.setText("Factories");
							final JPanel jPanel = new JPanel();
							jPanel.setLayout(new GridBagLayout());
							jPanel.setBackground(new Color(198, 242, 255));
							jPanel.add(jLabel, new GridBagConstraints());
							jPanelFactories.add(jPanel, BorderLayout.NORTH);
						}
						{
							jTableSelectedPlanetFactories = new JTable(new SelectedPlanetFactoriesTableModel(universe));
							jTableSelectedPlanetFactories.setFillsViewportHeight(true);
							final JScrollPane jScroll = new JScrollPane(jTableSelectedPlanetFactories);
							jScroll.setPreferredSize(new Dimension(-1, 200));
							jPanelFactories.add(jScroll, BorderLayout.CENTER);
						}
						{
							final JPanel jPanelSims = new JPanel();
							jPanelSims.setLayout(new BorderLayout());
							{
								final JLabel jLabel = new JLabel();
								jLabel.setText("Sims");
								final JPanel jPanel = new JPanel();
								jPanel.setLayout(new GridBagLayout());
								jPanel.setBackground(new Color(198, 242, 255));
								jPanel.add(jLabel, new GridBagConstraints());
								jPanelSims.add(jPanel, BorderLayout.NORTH);
							}
							{
								jTableSelectedPlanetSims = new JTable(new SelectedPlanetSimsTableModel(universe));
								jTableSelectedPlanetSims.setFillsViewportHeight(true);
								final JScrollPane jScroll = new JScrollPane(jTableSelectedPlanetSims);
								jScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
								jScroll.setPreferredSize(new Dimension(-1, 200));
								jPanelSims.add(jScroll, BorderLayout.SOUTH);
							}
							jPanelFactories.add(jPanelSims, BorderLayout.SOUTH);
						}
						jPanelSelected.add(jPanelFactories, BorderLayout.SOUTH);
					}
				}
				jPanelPlanets.add(jPanelSelected, BorderLayout.SOUTH);
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
				final JLabel jLabel = new JLabel();
				jLabel.setText("Traders");
				final JPanel jPanel = new JPanel();
				jPanel.setLayout(new GridBagLayout());
				jPanel.add(jLabel, new GridBagConstraints());
				jPanelTraders.add(jLabel, BorderLayout.NORTH);
			}
			{
				jTableTraders = new JTable(new TradersTableModel(universe));
				jTableTraders.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				jTableTraders.setAutoCreateRowSorter(true);
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
				jPanelTraders.add(jScroll, BorderLayout.CENTER);
			}
			{
				final JPanel jTraderSelected = new JPanel();
				jTraderSelected.setLayout(new BorderLayout());
				{
					final JPanel jPanelSelectedName = new JPanel();
					jPanelSelectedName.setLayout(new BorderLayout());
					{
						final JLabel jLabel = new JLabel();
						jLabel.setText("Selected Trader");
						final JPanel jPanel = new JPanel();
						jPanel.setLayout(new GridBagLayout());
						jPanel.setBackground(new Color(198, 242, 255));
						jPanel.add(jLabel, new GridBagConstraints());
						jPanelSelectedName.add(jPanel, BorderLayout.NORTH);
					}
					{
						final JPanel jPanel = new JPanel();
						jPanel.setLayout(new GridLayout(0, 2, 0, 0));
						{
							final JLabel jLabel = new JLabel();
							jLabel.setText("Name");
							jPanel.add(jLabel);
						}
						{
							fieldSelectedTraderName = new JTextField();
							fieldSelectedTraderName.setColumns(10);
							jPanel.add(fieldSelectedTraderName);
						}
						{
							final JLabel jLabel = new JLabel();
							jLabel.setText("Credit");
							jPanel.add(jLabel);
						}
						{
							fieldSelectedTraderCredits = new JTextField();
							fieldSelectedTraderCredits.setColumns(10);
							jPanel.add(fieldSelectedTraderCredits);
						}
						jPanelSelectedName.add(jPanel, BorderLayout.SOUTH);
						jTraderSelected.add(jPanelSelectedName, BorderLayout.NORTH);
					}
					//needs panel
					{
						final JPanel jPanelTraderNeeds = new JPanel();
						jPanelTraderNeeds.setLayout(new BorderLayout());
						jPanelTraderNeeds.setPreferredSize(new Dimension(-1, 150));
						{
							final JLabel jLabel = new JLabel();
							jLabel.setText("Needs");
							final JPanel jPanel = new JPanel();
							jPanel.setLayout(new GridBagLayout());
							jPanel.setBackground(new Color(198, 242, 255));
							jPanel.add(jLabel, new GridBagConstraints());
							jPanelTraderNeeds.add(jPanel, BorderLayout.NORTH);
						}
						{
							jTableSelectedTraderNeeds = new JTable(new SelectedTraderNeedsTableModel(universe));
							jTableSelectedTraderNeeds.setFillsViewportHeight(true);
							final JScrollPane jScroll = new JScrollPane(jTableSelectedTraderNeeds);
							jPanelTraderNeeds.add(jScroll, BorderLayout.CENTER);
						}
						jTraderSelected.add(jPanelTraderNeeds, BorderLayout.CENTER);
					}
					//goods panel
					{
						final JPanel jPanelGoods = new JPanel();
						jPanelGoods.setLayout(new BorderLayout());
						jPanelGoods.setPreferredSize(new Dimension(-1, 150));
						{
							final JLabel jLabel = new JLabel();
							jLabel.setText("Goods");
							final JPanel jPanel = new JPanel();
							jPanel.setLayout(new GridBagLayout());
							jPanel.setBackground(new Color(198, 242, 255));
							jPanel.add(jLabel, new GridBagConstraints());
							jPanelGoods.add(jPanel, BorderLayout.NORTH);
						}
						{
							jTableSelectedTraderGoods = new JTable(new SelectedTraderGoodsTableModel(universe));
							jTableSelectedTraderGoods.setFillsViewportHeight(true);
							final JScrollPane jScroll = new JScrollPane(jTableSelectedTraderGoods);
							jPanelGoods.add(jScroll, BorderLayout.CENTER);
						}
						jTraderSelected.add(jPanelGoods, BorderLayout.SOUTH);
					}
					// {
					// JPanel jPanelFactories = new JPanel();
					// jPanelFactories.setLayout( new BorderLayout() );
					// {
					// JLabel jLabel = new JLabel();
					// jLabel.setText( "Factories" );
					// JPanel jPanel = new JPanel();
					// jPanel.setLayout( new GridBagLayout() );
					// jPanel.setBackground( new Color( 198, 242, 255 ) );
					// jPanel.add( jLabel, new GridBagConstraints() );
					// jPanelFactories.add( jPanel, BorderLayout.NORTH );
					// }
					// {
					// jTableSelectedPlanetFactories = new JTable(
					// new SelectedPlanetFactoriesTableModel( universe ) );
					// jTableSelectedPlanetFactories.setFillsViewportHeight( true );
					// jScroll = new JScrollPane( jTableSelectedPlanetFactories );
					// jPanelFactories.add( jScroll, BorderLayout.CENTER );
					// }
					// jTraderSelected.add( jPanelFactories, BorderLayout.SOUTH );
					// }
				}
				jPanelTraders.add(jTraderSelected, BorderLayout.SOUTH);
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
			jPanelCaption.setBackground(new Color(198, 242, 255));

			final JButton jButtonCreate = new JButton();
			jButtonCreate.setText("Create");
			jButtonCreate.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(final java.awt.event.ActionEvent e) {
					try {
						universe.create(UNIVERSE_GENERATION_RANDOM_SEED, 10, 10L * TimeUnit.TICKS_PER_DAY);
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
			jPanelCaption.setBackground(new Color(198, 242, 255));

			final JToolBar jToolBar = new JToolBar();
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
				jPanel.setBackground(new Color(198, 242, 255));
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
				jPanel.setBackground(new Color(198, 242, 255));
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
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setSize(new Dimension(FORM_WIDTH, 1000));
		this.setBackground(Color.RED);
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
