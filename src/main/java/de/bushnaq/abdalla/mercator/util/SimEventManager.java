package de.bushnaq.abdalla.mercator.util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import de.bushnaq.abdalla.mercator.universe.sim.Sim;

public class SimEventManager {
	public boolean enabled = false;
	public List<SimEvent> eventList = new ArrayList<SimEvent>();
	Sim sim;

	public SimEventManager(final Sim sim, final boolean enabled) {
		this.sim = sim;
		this.enabled = enabled;
	}

	public void add(final long when, final int volume, final SimEventType eventType, final float credits, final String what) {
		if (enabled)
			eventList.add(new SimEvent(when, volume, eventType, credits, what));
	}

	//		public void print() {
	//			try (PrintStream out = new PrintStream(sim.planet.getName() + "-" + sim.getName() + ".txt", "UTF-8")) {
	//				print(out);
	//			} catch (FileNotFoundException e) {
	//				e.printStackTrace();
	//			} catch (UnsupportedEncodingException e) {
	//				e.printStackTrace();
	//			}
	//		}

	public void print(final PrintStream out) {
		out.printf("%s on %s\n", sim.getName(), sim.planet.getName());
		out.printf("%3s %4s %4s %7s %8s %s\n", "-ID", "TIME", "-VOL", "CREDITS", "---EVENT", "DESCRIPTION");
		for (final SimEvent simEvent : eventList) {
			out.printf("%s %s %4d %7.2f %8s %s\n", sim.getName(), TimeUnit.toString(simEvent.when), simEvent.volume, simEvent.credits, simEvent.eventType.name, simEvent.what);
		}
	}
}
