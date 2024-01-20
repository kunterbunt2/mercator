package de.bushnaq.abdalla.mercator.gui.frame;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;

public class SelectedPlanetSimsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 4803847753013026463L;
	private final String[] columnNames = { "Name", "Credits", "Satisfaction", "Profession", "Factory", "Status" };
	private final Vector<Sim> simList = new Vector<Sim>();
	private final Universe universe;

	public SelectedPlanetSimsTableModel(final Universe universe) {
		this.universe = universe;
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
		if (universe.selectedPlanet != null) {
			return universe.selectedPlanet.simList.size();
		}
		return 0;
	}

	@Override
	public Object getValueAt(final int row, final int col) {
		if (universe.selectedPlanet != null) {
			simList.clear();
			simList.addAll(universe.selectedPlanet.simList);
			simList.addAll(universe.selectedPlanet.deadSimList);
			final Sim sim = simList.get(row);
			switch (col) {
			case 0:
				return sim.getName();
			case 1:
				return sim.getCredits();
			case 2:
				return sim.getSatisfactionFactor(universe.currentTime);
			case 3:
				return sim.profession.name();
			case 4:
				if (sim.productionFacility != null) {
					return sim.productionFacility.getName();
				} else {
					return "-";
				}
			case 5:
				return sim.status.getName();
			}
		}
		return null;
	}

}
