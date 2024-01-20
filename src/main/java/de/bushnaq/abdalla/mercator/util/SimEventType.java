package de.bushnaq.abdalla.mercator.util;

public enum SimEventType {
	arive("arived"), buy("bought"), consue("consumed"), die("died"), ern("erned"), payJump("jumped"), produce("produced"), resting("resting"), sell("sold"), travel("traveled");

	public String name;

	SimEventType(final String name) {
		this.name = name;
	}
}
