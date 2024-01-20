package de.bushnaq.abdalla.mercator.gui.frame;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.util.Event;
import de.bushnaq.abdalla.mercator.util.TimeUnit;

public class EventTableModel extends AbstractTableModel {

	private static final String[] columnNames = { "#", "Time", "Event", "Who" };
	private static final long serialVersionUID = 4803847753013026463L;
	private List<Event> filteredEventList = null;
	private Object lastFiltered = null;
	private final Universe universe;

	public EventTableModel(final Universe universe) {
		this.universe = universe;
		filteredEventList = universe.eventManager.eventList;
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
		return filteredEventList.size();
	}

	@Override
	public Object getValueAt(final int row, final int col) {
		if (universe.selected != lastFiltered) {
			filteredEventList = universe.eventManager.filter(universe.selected);
			lastFiltered = universe.selected;
		} else {
			//use list
		}

		if (filteredEventList.size() > 0) {
			final int index = filteredEventList.size() - 1 - row;
			switch (col) {
			case 0:
				return index;
			case 1:
				return TimeUnit.toString(filteredEventList.get(index).when);
			case 2:
				return filteredEventList.get(index).what;
			case 3:
				return filteredEventList.get(index).who;
			}
		} else {
			return "-";
		}
		return null;
	}

}
