package com.abdalla.bushnaq.mercator.gui.frame;

import javax.swing.table.AbstractTableModel;

import com.abdalla.bushnaq.mercator.universe.sector.SectorList;

public class SectorsTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 4803847753013026463L;
	private final String[] columnNames = { "Name", "Credits", "NumberOfPlanets" };
	private final SectorList systemList;

	public SectorsTableModel(final SectorList systemList) {
		this.systemList = systemList;
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
		return systemList.size();
	}

	@Override
	public Object getValueAt(final int row, final int col) {
		switch (col) {
		case 0:
			return systemList.get(row).name;
		case 1:
			return systemList.get(row).credits;
		case 2:
			return systemList.get(row).numberOfPlanets;
		}
		return null;
	}

}
