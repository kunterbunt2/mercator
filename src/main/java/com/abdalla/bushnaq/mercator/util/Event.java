package com.abdalla.bushnaq.mercator.util;

import com.abdalla.bushnaq.mercator.universe.UniverseGenerator;
import com.abdalla.bushnaq.mercator.universe.factory.ProductionFacility;
import com.abdalla.bushnaq.mercator.universe.good.Good;
import com.abdalla.bushnaq.mercator.universe.planet.Planet;
import com.abdalla.bushnaq.mercator.universe.sim.Sim;
import com.abdalla.bushnaq.mercator.universe.sim.trader.Trader;

public class Event {
	public EventLevel level;
	public StackTraceElement[] stackTrace;
	public String what;
	public long when;
	public Object who;

	public Event(final EventLevel level, final long when, final Object who, final String what) {
		this.level = level;
		this.when = when;
		this.who = who;
		this.what = what;
		stackTrace = Thread.currentThread().getStackTrace();
	}

	public String getWhosName() {
		if (Planet.class.isInstance(who)) {
			return ((Planet) who).getName();
		} else if (Trader.class.isInstance(who)) {
			return ((Sim) who).getName();
		} else if (Sim.class.isInstance(who)) {
			return ((Sim) who).getName();
		} else if (ProductionFacility.class.isInstance(who)) {
			return ((ProductionFacility) who).getName();
		} else if (Good.class.isInstance(who)) {
			return ((Good) who).type.getName();
		} else if (MercatorRandomGenerator.class.isInstance(who)) {
			return "MercatorRandomGenerator";
		} else if (UniverseGenerator.class.isInstance(who)) {
			return "UniverseGenerator";
		}
		return null;
	}
}
