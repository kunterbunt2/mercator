package com.abdalla.bushnaq.mercator.gui.frame;

import javax.swing.table.AbstractTableModel;

import com.abdalla.bushnaq.mercator.universe.Universe;
import com.abdalla.bushnaq.mercator.universe.factory.Factory;
import com.abdalla.bushnaq.mercator.universe.factory.ProductionFacility;
import com.abdalla.bushnaq.mercator.universe.factory.ProductionFacilityList;

public class SelectedPlanetFactoriesTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 4803847753013026463L;
	private final String[] columnNames = { "Name", "Needs", "Produces", "Status" };
	private final Universe universe;

	public SelectedPlanetFactoriesTableModel(final Universe universe) {
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
			return universe.selectedPlanet.productionFacilityList.size();
		}
		return 0;
	}

	@Override
	public Object getValueAt(final int row, final int col) {
		if (universe.selectedPlanet != null) {
			final ProductionFacilityList productionFacilityList = universe.selectedPlanet.productionFacilityList;
			final ProductionFacility productionFacility = productionFacilityList.get(row);
			switch (col) {
			case 0:
				return productionFacility.getName();
			case 1:
				if (Factory.class.isInstance(productionFacility)) {
					final Factory factory = (Factory) productionFacility;
					if (factory.inputGood.size() != 0) {
						return factory.inputGood.get(0).type.getName();
					} else {
						return "N/A";
					}
				}
			case 2:
				return productionFacility.producedGood.type.getName();
			case 3:
				return productionFacility.getStatusName();
			}
		}
		return null;
	}

}
