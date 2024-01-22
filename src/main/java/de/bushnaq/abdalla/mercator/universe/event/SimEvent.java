package de.bushnaq.abdalla.mercator.universe.event;

public class SimEvent {
	public float credits;
	public SimEventType eventType;
	public int volume;
	public String what;
	public long when;

	public SimEvent(final long when, final int volume, final SimEventType eventType, final float credits, final String what) {
		this.when = when;
		this.volume = volume;
		this.eventType = eventType;
		this.credits = credits;
		this.what = what;
	}
}
