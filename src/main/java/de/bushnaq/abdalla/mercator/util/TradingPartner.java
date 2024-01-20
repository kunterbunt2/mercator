package de.bushnaq.abdalla.mercator.util;

import de.bushnaq.abdalla.mercator.universe.good.GoodList;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;

/**
 * @author abdalla Defines an interface to allow the exchange of goods and
 *         credits between two TradingPartner. All transactions are handled
 *         through static methods of the Transaction class.
 */
public interface TradingPartner {
	void ern(long currentTime, float f);

	float getCredits();

	GoodList getGoodList();

	HistoryManager getHistoryManager();

	String getName();

	Planet getPlanet();

	void setCredits(float credits);
}
