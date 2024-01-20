package de.bushnaq.abdalla.mercator.universe.sector;

/**
 * @author bushnaq Created 13.02.2005
 */
public class Sector {
	public int credits = 0;
	public String name = null;
	public int numberOfPlanets = 0;
	public int type = 0;

	public Sector() {
	}

	public Sector(final int type, final String name) {
		credits = 0;
		this.name = name;
		numberOfPlanets = 0;
		this.type = type;
	}

}
