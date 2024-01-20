package com.abdalla.bushnaq.mercator.gui.frame;

import javax.swing.table.AbstractTableModel;

import com.abdalla.bushnaq.mercator.universe.Universe;
import com.abdalla.bushnaq.mercator.universe.planet.PlanetList;

public class PlanetsTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -4333102673911118458L;
	private final String[] columnNames = { "Name", "Credits", "Satisfaction", "Sector" };
	private final PlanetList planetList;
	private final Universe universe;

	public PlanetsTableModel(final Universe universe) {
		this.universe = universe;
		this.planetList = universe.planetList;
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
		return planetList.size();
	}

	@Override
	public Object getValueAt(final int row, final int col) {
		switch (col) {
		case 0:
			return planetList.get(row).getName();
		case 1:
			return planetList.get(row).getCredits();
		case 2:
			return planetList.get(row).getSatisfactionFactor(universe.currentTime);
		case 3:
			return planetList.get(row).sector.name;
		}
		return null;
	}

}
