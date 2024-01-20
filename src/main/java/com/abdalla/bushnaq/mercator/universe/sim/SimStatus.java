package com.abdalla.bushnaq.mercator.universe.sim;

public enum SimStatus {
	DEAD_REASON_NO_FOOD("Dead, no products"), DEAD_REASON_NO_MONEY("Dead, poor"), LIVING("Living"), /*SOCIAL_WELFARE("Social welfare"),*/ STARVING_NO_GOODS("no products"), STARVING_NO_MONEY("poor"), UNKNOWN("Unknown");

	private String name;

	SimStatus(final String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
