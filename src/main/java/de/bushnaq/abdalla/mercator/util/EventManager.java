package de.bushnaq.abdalla.mercator.util;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
	private final Class<?> classFilter;
	public List<Event> eventList = new ArrayList<Event>();
	List<Event> filteredList = new ArrayList<Event>();
	EventLevel level;
	private Object objectFilter;

	/**
	 * @param level set to a specific EventLevel
	 * @param filter set to a specific class or to null to disable filtering
	 */
	public EventManager(final EventLevel level, final Class<?> filter) {
		this.level = level;
		this.classFilter = filter;
	}

	public void add(final EventLevel level, final long when, final Object who, final String what) {
		if (level.ordinal() >= this.level.ordinal() && (classFilter == null || classFilter.isAssignableFrom(who.getClass()))) {
			final Event e = new Event(level, when, who, what);
			eventList.add(e);
			if (e.who == objectFilter) {
				filteredList.add(e);
			}
		}
	}

	public List<Event> filter(final Object objectFilter) {
		filteredList.clear();
		for (final Event e : eventList) {
			if (e.who == objectFilter) {
				filteredList.add(e);
			}
		}
		this.objectFilter = objectFilter;
		return filteredList;
	}

	public boolean isEnabled() {
		return !level.equals(EventLevel.none);
	}
}
