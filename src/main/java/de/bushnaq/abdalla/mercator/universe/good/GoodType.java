package de.bushnaq.abdalla.mercator.universe.good;

public enum GoodType {
	FOOD(0, "Food"), G02(0, "Medecine"), G03(0, "Media"), G04(0, "Weapons")/*, G11(1, "G-11"), G12(1, "G-12"), G13(1, "G-13"), G14(1, "G-14")*/;

	private String name;
	private int technicalLevel;

	GoodType(final int technicalLevel, final String name) {
		this.technicalLevel = technicalLevel;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getTechnicalLevel() {
		return technicalLevel;
	}
}
