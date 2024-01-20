package com.abdalla.bushnaq.mercator.universe.sim.trader;

public enum TraderStatus {
	TRADER_STATUS_BUYING("Buy", true), TRADER_STATUS_CANNOT_BUY("X buy", false), TRADER_STATUS_CANNOT_SELL("X sell", false), TRADER_STATUS_RESTING("Rest", true), TRADER_STATUS_SELLING("Sell", true), TRADER_STATUS_UNKNOWN("Unknown", false), TRADER_STATUS_WAITING_FOR_GOOD_PRICE_TO_BUY("Waiting for a good price to buy...", true), TRADER_STATUS_WAITING_TO_SELL("Waiting to sell...", true);

	private boolean good;
	private String name;

	TraderStatus(final String name, final boolean good) {
		this.setName(name);
		this.setGood(good);
	}

	public String getName() {
		return name;
	}

	public boolean isGood() {
		return good;
	}

	public void setGood(final boolean good) {
		this.good = good;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
