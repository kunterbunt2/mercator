package de.bushnaq.abdalla.mercator.gui.frame;

import javax.swing.table.AbstractTableModel;

import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.sim.trader.TraderList;

public class TradersTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 4257926461207710123L;
	private final String[] columnNames = { "Name", "Max load", "Credits", "Status", "Sim Status", "Satisfaction", "Waiting", "Loading", "Home port", "Bought at", "Over port", "Selling to" };
	private final TraderList traderList;
	private final Universe universe;

	public TradersTableModel(final Universe universe) {
		this.universe = universe;
		this.traderList = universe.traderList;
	}

	@Override
	public Class getColumnClass(final int c) {
		return getValueAt(0, c).getClass();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(final int col) {
		return columnNames[col];
	}

	@Override
	public int getRowCount() {
		return traderList.size();
	}

	@Override
	public Object getValueAt(final int row, final int col) {
		if (traderList.size() > 0) {
			switch (col) {
			case 0:
				return traderList.get(row).getName();
			case 1:
				return traderList.get(row).goodSpace;
			case 2:
				return traderList.get(row).getCredits();
			case 3:
				return traderList.get(row).traderStatus.getName();
			case 4:
				return traderList.get(row).status.getName();
			case 5:
				return traderList.get(row).getSatisfactionFactor(universe.currentTime);
			case 6:
				return traderList.get(row).portRestingTime;
			case 7:
				return traderList.get(row).getGoodList().queryFirstGoodName();
			case 8:
				return traderList.get(row).planet.getName();
			case 9:
				if (traderList.get(row).sourcePlanet != null) {
					return traderList.get(row).sourcePlanet.getName();
				} else {
					return "-";
				}
			case 10:
				if (traderList.get(row).targetWaypoint != null && traderList.get(row).targetWaypoint.city != null) {
					return traderList.get(row).targetWaypoint.city.getName();
				} else {
					return "-";
				}
			case 11:
				if (traderList.get(row).sourcePlanet != null && traderList.get(row).destinationPlanet != null && traderList.get(row).sourcePlanet != traderList.get(row).destinationPlanet) {
					return traderList.get(row).destinationPlanet.getName();
				} else {
					return "-";
				}
			}
		} else {
			return "-";
			//			switch (col) {
			//			case 0:
			//				return "";
			//			case 1:
			//				return 0;
			//			case 2:
			//				return 0;
			//			case 3:
			//				return "";
			//			case 4:
			//				return "";
			//			case 5:
			//				return 0f;
			//			case 6:
			//				return 0L;
			//			case 7:
			//				return "";
			//			case 8:
			//				return "";
			//			case 9:
			//				return "";
			//			case 10:
			//				if (traderList.get(row).destinationWaypointPlanet != null) {
			//					return traderList.get(row).destinationWaypointPlanet.getName();
			//				} else {
			//					return "-";
			//				}
			//			case 11:
			//				if ( traderList.get(row).sourcePlanet != null && traderList.get(row).destinationPlanet != null && traderList.get(row).sourcePlanet != traderList.get(row).destinationPlanet) {
			//					return traderList.get(row).destinationPlanet.getName();
			//				} else {
			//					return "-";
			//				}
			//			}
		}
		return null;
	}

}
