package de.bushnaq.abdalla.mercator.gui.frame;

import javax.swing.table.AbstractTableModel;

import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.good.GoodList;

public class SelectedPlanetGoodsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 4803847753013026463L;
	private final String[] columnNames = { "Name", "Price", "Traded", "Average price", "Amount", "Average amount", "produced", "Consumed", "sold", "bought" };
	private final Universe universe;

	public SelectedPlanetGoodsTableModel(final Universe universe) {
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
			return universe.selectedPlanet.getGoodList().size();
		}
		return 0;
	}

	@Override
	public Object getValueAt(final int row, final int col) {
		if (universe.selectedPlanet != null) {
			final GoodList goodList = universe.selectedPlanet.getGoodList();
			switch (col) {
			case 0:
				return goodList.get(row).type.getName();
			case 1:
				return goodList.get(row).price;
			case 2:
				return goodList.get(row).isTraded(universe.currentTime);
			case 3:
				return goodList.get(row).getAveragePrice();
			case 4:
				return goodList.get(row).getAmount();
			case 5:
				return goodList.get(row).getAverageAmount();
			case 6:
				return goodList.get(row).statistic.produced;
			case 7:
				return goodList.get(row).statistic.consumed;
			case 8:
				return goodList.get(row).statistic.sold;
			case 9:
				return goodList.get(row).statistic.bought;
			}
		}
		return null;
	}

}
