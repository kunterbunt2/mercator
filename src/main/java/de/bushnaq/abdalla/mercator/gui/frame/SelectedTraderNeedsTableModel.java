package de.bushnaq.abdalla.mercator.gui.frame;

import javax.swing.table.AbstractTableModel;

import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.sim.SimNeedList;

public class SelectedTraderNeedsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 4803847753013026463L;
	private final String[] columnNames = { "Good", "consumeEvery", "lastConsumed", "dieIfNotConsumedWithin", "totalConsumed" };
	private final Universe universe;

	public SelectedTraderNeedsTableModel(final Universe universe) {
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
		if (universe.selectedTrader != null) {
			return universe.selectedTrader.getGoodList().size();
		}
		return 0;
	}

	@Override
	public Object getValueAt(final int row, final int col) {
		if (universe.selectedTrader != null) {
			final SimNeedList simNeedList = universe.selectedTrader.simNeedsList;
			switch (col) {
			case 0:
				return simNeedList.get(row).type.getName();
			case 1:
				return simNeedList.get(row).consumeEvery;
			case 2:
				return simNeedList.get(row).lastConsumed;
			case 3:
				return simNeedList.get(row).lastConsumed + simNeedList.get(row).dieIfNotConsumedWithin;
			case 4:
				return simNeedList.get(row).totalConsumed;
			}
		}
		return null;
	}

}
