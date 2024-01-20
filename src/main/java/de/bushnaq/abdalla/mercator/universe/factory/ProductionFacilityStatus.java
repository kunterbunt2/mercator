package de.bushnaq.abdalla.mercator.universe.factory;

public enum ProductionFacilityStatus {
	CANNOT_AFFORD_ENGINEERS("cannot afford engineers"), NO_ENGINEERS("No engineers"), NO_INPUT_GOODS("Not enough input goods"), NO_PROFIT("No expected profit"), NO_STORAGE("Not enough storage"), OFFLINE("Offline"), PRODUCING("Producing"), RESEARCHING("Researching");

	private String name;

	ProductionFacilityStatus(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
