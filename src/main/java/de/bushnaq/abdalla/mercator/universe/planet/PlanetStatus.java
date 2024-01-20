package de.bushnaq.abdalla.mercator.universe.planet;

public enum PlanetStatus {
	DEAD_REASON_NO_FOOD("Dead"), DEAD_REASON_NO_MONEY("Dead"), DEAD_REASON_NO_SIMS("Dead"), LIVING("Living"), UNKNOWN("Unknown");

	private String name;

	PlanetStatus(final String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
