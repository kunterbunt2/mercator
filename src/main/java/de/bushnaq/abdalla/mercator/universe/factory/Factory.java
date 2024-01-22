package de.bushnaq.abdalla.mercator.universe.factory;

import de.bushnaq.abdalla.mercator.universe.event.EventLevel;
import de.bushnaq.abdalla.mercator.universe.event.SimEventType;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.good.GoodList;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.universe.tools.Tools;
import de.bushnaq.abdalla.mercator.util.TimeUnit;
import de.bushnaq.abdalla.mercator.util.Transaction;

/**
 * @author bushnaq
 * Created 13.02.2005
 */
/**
 * A factory belongs to a planet. A factory consumes labor and a list of goods
 * of technicalLevel N from the planet to produce one good of technicalLevel N+1
 * for the planet. The cost of production is equal to the cost of labor, as the
 * input goods are already belonging to the planet.
 *
 *
 * @author abdalla bushnaq
 *
 */
public class Factory extends ProductionFacility {
	public GoodList inputGood;

	public Factory(final Planet planet, final GoodList inputGood, final Good producedGood) {
		super(planet, producedGood);
		if (inputGood != null) {
			this.inputGood = inputGood;
		} else {
			this.inputGood = new GoodList();
		}
	}

	@Override
	public void advanceInTime(final long currentTime) {
		// are we at the start of a new year?
		if (currentTime % (TimeUnit.TICKS_PER_DAY * TimeUnit.DAYS_PER_YEAR) == 0) {
			lastYearProducedAmount = currentProducedAmount;
			currentProducedAmount = 0;
		}
		if (((lastProductionTime + TIME_NEEDED_TO_PRODUCE) <= currentTime) && TimeUnit.isInt(currentTime)/* ( ( currentTime - (int)currentTime ) == 0.0f ) */ ) {
			// --Do we have enough engineers?
			if (engineers.size() <= 0) {
				status = ProductionFacilityStatus.NO_ENGINEERS;
				return;
			}
			// ---Can the planet afford this production?
			{
				float engineeringCost = 0;
				engineeringCost += engineers.getTotalCost();
				if (planet.getCredits() < engineeringCost) {
					status = ProductionFacilityStatus.CANNOT_AFFORD_ENGINEERS;
					return;
				}
				// ---We do not need to pay for our own goods, but we have paid for them and
				// need to make sure we get the money back
				float goodCost = 0;
				for (final Good good : inputGood) {
					goodCost += good.price;
				}
				// ---Do we still make a profit in average?
				final float profit = (producedGood.getAveragePrice() - engineeringCost - goodCost);
				if (profit < 0) {
					status = ProductionFacilityStatus.NO_PROFIT;
					Tools.print("Factory %s on planet %s cannot afford to produce.\n", getName(), planet.getName());
					//					Tools.speak(String.format("Factory %s on planet %s cannot afford to produce.\n", getName(), planet.getName()));
					return;
				}
			}
			// ---Can we store it?
			if (producedGood.getAmount() >= producedGood.getMaxAmount()) {
				status = ProductionFacilityStatus.NO_STORAGE;
				return;
			}
			// ---Indicate buy interest
			for (final Good good : inputGood) {
				good.indicateBuyInterest(currentTime);
			}
			// --Do we have enough goods to produce?
			for (final Good good : inputGood) {
				if (good.getAmount() <= 0) {
					status = ProductionFacilityStatus.NO_INPUT_GOODS;
					return;
				}
			}
			// ---Produce
			// ---Consume the input good
			for (final Good good : inputGood) {
				good.consume(1);
			}
			// ---Pay the engineers
			for (final Sim sim : engineers) {
				Transaction.pay(currentTime, sim.cost, 1, planet, sim);
			}
			productionProgress += engineers.size();
			while (productionProgress > queryEngineersNeeded()) {
				productionProgress -= queryEngineersNeeded();
				producedGood.produce(1, planet);
				currentProducedAmount += 1;
				planet.eventManager.add(currentTime, planet.getGoodList().getByType(producedGood.type).getAmount(), SimEventType.produce, planet.getCredits(), String.format("%s in %s.", producedGood.type.getName(), getName()));
				if (planet.universe.eventManager.isEnabled())
					planet.universe.eventManager.add(EventLevel.trace, currentTime, this, String.format("%s on %s produces %s", getName(), planet.getName(), producedGood.type.getName()));
				// TODO BUG. This can lead to producing more than one good out of one input
			}
			status = ProductionFacilityStatus.PRODUCING;
			lastProductionTime = currentTime;
		}
	}

	@Override
	public String getName() {
		return "F-" + producedGood.type.getName();
	}

	@Override
	public float queryAverageProfit() {
		return queryAverageRevenue() - queryCost();
	}

	public float queryAverageRevenue() {
		return producedGood.getAveragePrice();
	}

	public float queryCost() {
		int cost = 0;
		// ---Input goods cost
		for (final Good good : inputGood) {
			cost += good.price;
		}
		// ---Pay the engineers
		cost += engineers.getTotalCost();
		return cost;
	}

	/**
	 * @return Engineer time units needed to produce one unit of Good
	 */
	public int queryEngineersNeeded() {
		return producedGood.type.getTechnicalLevel() * 10 + 2;
	}

	@Override
	public float queryProfit() {
		return queryRevenue() - queryCost();
	}

	public float queryRevenue() {
		return producedGood.price;
	}
}
