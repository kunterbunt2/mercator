package de.bushnaq.abdalla.mercator.util;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.bushnaq.abdalla.mercator.universe.event.SimEvent;
import de.bushnaq.abdalla.mercator.universe.event.SimEventType;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;

public class PlanetEventManager {
	public List<SimEvent> eventList = new ArrayList<SimEvent>();
	Planet planet;

	public PlanetEventManager(final Planet planet) {
		this.planet = planet;
	}

	public void add(final long when, final int volume, final SimEventType eventType, final float credits, final String what) {
		eventList.add(new SimEvent(when, volume, eventType, credits, what));
	}

	public void print() {
		try (PrintStream out = new PrintStream(planet.getName() + ".txt", "UTF-8")) {
			print(out);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void print(final PrintStream out) {
		out.printf("%s\n", planet.getName());
		out.printf("%3s %4s %4s %7s %8s %s\n", "-ID", "TIME", "-VOL", "CREDITS", "---EVENT", "DESCRIPTION");
		for (final SimEvent simEvent : eventList) {
			out.printf("%s %s %4d %7.2f %8s %s\n", planet.getName(), TimeUnit.toString(simEvent.when), simEvent.volume, simEvent.credits, simEvent.eventType.name, simEvent.what);
		}
	}
}
