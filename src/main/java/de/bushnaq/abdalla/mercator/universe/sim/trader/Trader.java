/* ---------------------------------------------------------------------------
 * BEGIN_PROJECT_HEADER
 *
 *       RRRR  RRR    IIIII    CCCCC      OOOO    HHH  HHH
 *       RRRR  RRRR   IIIII   CCC  CC    OOOOOO   HHH  HHH
 *       RRRR  RRRRR  IIIII  CCCC  CCC  OOO  OOO  HHH  HHH
 *       RRRR  RRRR   IIIII  CCCC       OOO  OOO  HHH  HHH
 *       RRRR RRRR    IIIII  CCCC       OOO  OOO  HHHHHHHH
 *       RRRR  RRRR   IIIII  CCCC       OOO  OOO  HHH  HHH
 *       RRRR   RRRR  IIIII  CCCC  CCC  OOO  OOO  HHH  HHH
 *       RRRR   RRRR  IIIII   CCC  CC    OOOOOO   HHH  HHH
 *       RRRR   RRRR  IIIII    CCCCC      OOOO    HHH  HHH
 *
 *       Copyright 2005 by Ricoh Europe B.V.
 *
 *       This material contains, and is part of a computer software program
 *       which is, proprietary and confidential information owned by Ricoh
 *       Europe B.V.
 *       The program, including this material, may not be duplicated, disclosed
 *       or reproduced in whole or in part for any purpose without the express
 *       written authorization of Ricoh Europe B.V.
 *       All authorized reproductions must be marked with this legend.
 *
 *       Department : European Development and Support Center
 *       Group      : Printing & Fax Solution Group
 *       Author(s)  : bushnaq
 *       Created    : 13.02.2005
 *
 *       Project    : com.abdalla.bushnaq.mercator
 *       Product Id : <Product Key Index>
 *       Component  : <Project Component Name>
 *       Compiler   : Java/Eclipse
 *
 * END_PROJECT_HEADER
 * -------------------------------------------------------------------------*/
package de.bushnaq.abdalla.mercator.universe.sim.trader;

import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.good.GoodType;
import de.bushnaq.abdalla.mercator.universe.jumpgate.JumpGate;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.planet.PlanetList;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.util.EventLevel;
import de.bushnaq.abdalla.mercator.util.MercatorRandomGenerator;
import de.bushnaq.abdalla.mercator.util.SimEventType;
import de.bushnaq.abdalla.mercator.util.TimeUnit;
import de.bushnaq.abdalla.mercator.util.Transaction;

/**
 * @author bushnaq Created 13.02.2005
 */
public class Trader extends Sim {
	private static final float JUMP_UNIT_COST = 1f / (1000f);
	public static final int MAX_ENGINE_SPEED = 25;
	public static final int MAX_GOOD_SPACE = 100;
	public static final int MIN_ENGINE_SPEED = 2;
	public static final int MIN_GOOD_SPACE = 30;
	//	private static final String QUERY_BEST_BUY_AIT = "bestbuy AIT";
	//	private static final String QUERY_BEST_SELL_AIT = "bestsell AIT";
	public final static int TRADER_MAX_PORT_REST_TIME = 3;
	public static final float TRADER_START_CREDITS = 1000.0f;
	public final static int TRADER_STATUS_FILTER_BAD = 0xF0;
	float currentMaxEngineSpeed = 50;
	private int currentTransportedAmount;
	//	float defaultEngineSpeed = MAX_ENGINE_SPEED;
	public Planet destinationPlanet = null; // ---The planet we want to reach ultimately
	public float destinationWaypointDistance = 0;
	public float destinationWaypointDistanceProgress = 0;
	public int destinationWaypointIndex = 0; // ---The index into the WaypointList
	public Planet destinationWaypointPlanet = null; // ---The next planet we want to reach in our way to the
	boolean foundTradedGood = false;
	// destinationPlanet
	public int goodSpace = 0;
	public long lastTimeAdvancement = 0;
	public long lastTransaction = 0;
	public int lastYearTransportedAmount;
	public long portRestingTime = 0; // ---After a transaction or to wait for better prices, we recreate at a port
	public Planet sourcePlanet = null; // ---The planet origin of the good we are currently selling
	private GoodType targetGoodType = null; // ---Used to remember the index of good that we where to sell
	private long timeDelta = 0;
	public TraderStatus traderStatus = TraderStatus.TRADER_STATUS_RESTING;
	public WaypointList waypointList = new WaypointList();

	public float x;

	public float z;

	public Trader(final Planet planet, final String name, final float credits) throws Exception {
		super(planet, name, credits);
		creditsToSave = TRADER_START_CREDITS * 1000;
		set2DRenderer(new Trader2DRenderer(this));
		set3DRenderer(new Trader3DRenderer(this));
	}

	public boolean advanceInTime(final long currentTime, final MercatorRandomGenerator randomGenerator, final PlanetList planetList, final boolean selected) throws Exception {
		// we only follow our sim needs when we are in a port.
		if (destinationWaypointPlanet == null) {
			if (super.advanveInTime(currentTime, randomGenerator, planet.simList)) {
				return true;
			}
		}
		timeDelta = currentTime - lastTimeAdvancement;
		lastTimeAdvancement = currentTime;
		// ---If we are waiting in a port (after a buy) we wait until rest is over
		if (portRestingTime > 0) {
			portRestingTime -= timeDelta;
			return false;
		} else {
		}
		// are we at the start of a new year?
		if (currentTime % (TimeUnit.TICKS_PER_DAY * TimeUnit.DAYS_PER_YEAR) == 0) {
			lastYearTransportedAmount = currentTransportedAmount;
			currentTransportedAmount = 0;
		}
		// ---If we are in the middle of a flight we wait until we reach the next planet
		if (destinationWaypointPlanet != null) {
			// ---Every time we use a jump gate, we mark it. This helps to visualize much
			// used jump gates.
			markJumpGateUsage();
			destinationWaypointDistanceProgress += (getMaxEngineSpeed() * timeDelta) / TimeUnit.TICKS_PER_DAY;
			if (destinationWaypointDistanceProgress >= destinationWaypointDistance && TimeUnit.isInt(currentTime)/* ( ( currentTime - (int)currentTime ) == 0.0f ) */ ) {
				// ---Pay for the jump
				final float costOfJump = costOfDistance(destinationWaypointDistance);
				setCredits(getCredits() - costOfJump);
				// TODO pay the planet
				destinationWaypointPlanet.setCredits(destinationWaypointPlanet.getCredits() + costOfJump);
				eventManager.add(currentTime, getVolume(), SimEventType.payJump, getCredits(), String.format("and payed %5.2f to %s.", costOfJump, planet.getName()));
				// ---we reached a new port
				destinationWaypointDistance = 0;
				destinationWaypointDistanceProgress = 0;
				planet.traderList.remove(this);
				planet = destinationWaypointPlanet;
				planet.traderList.add(this);
				planet.universe.eventManager.add(EventLevel.trace, currentTime, this, String.format("arrived at %s", destinationWaypointPlanet.getName()));
				if (destinationWaypointPlanet == destinationPlanet) {
					// ---We reached our destination
					destinationPlanet = null;
					destinationWaypointPlanet = null;
					if (getGoodList().queryAmount() != 0) {
						sell(currentTime, randomGenerator);
					} else {
						buy(currentTime, randomGenerator);
					}
				} else {
					// ---Plan the next part of the trip
					destinationWaypointIndex++;
					destinationWaypointPlanet = waypointList.get(destinationWaypointIndex).planet;
					if (destinationWaypointPlanet != null) {
						destinationWaypointDistance = planet.queryDistance(destinationWaypointPlanet);
					} else {
					}
				}
			}
		} else if (TimeUnit.isInt(currentTime)/* ( ( currentTime - (int)currentTime ) == 0.0f ) */ ) {
			if (getGoodList().queryAmount() != 0) {
				// PathSeeker pathSeeker = new PathSeeker();
				final Planet bestPlanet = queryBestPlanetToSell(currentTime, planetList, /* pathSeeker, */getGoodList().getByType(targetGoodType), planet);
				// ---If we did not find a good to buy, we wait for a while
				if (bestPlanet == null) {
					portRestingTime = randomGenerator.nextInt(currentTime, this, TRADER_MAX_PORT_REST_TIME) * TimeUnit.TICKS_PER_DAY;
					traderStatus = TraderStatus.TRADER_STATUS_WAITING_TO_SELL;
					eventManager.add(currentTime, getVolume(), SimEventType.resting, getCredits(), String.format("resting %s on %s because I cannot sell.", TimeUnit.toString(portRestingTime), planet.getName()));
					return false;
				}
				// ---Now set the destination...
				destinationPlanet = bestPlanet;
				destinationWaypointDistanceProgress = 0;
				if (planet == destinationPlanet) {
					// best sell is where we are
					destinationWaypointPlanet = null;
					destinationWaypointIndex = -1;
					destinationWaypointDistance = 0;
					sell(currentTime, randomGenerator);
				} else {
					if (destinationPlanet != null) {
						extractWaypointList();
						destinationWaypointPlanet = waypointList.get(1).planet;
						// MarkJumpGateUsage();
						destinationWaypointIndex = 1;
						// DestinationWaypointPlanet = PortPlanet->QueryNextWaypoint( DestinationPlanet
						// );
						destinationWaypointDistance = planet.queryDistance(destinationWaypointPlanet);
						traderStatus = TraderStatus.TRADER_STATUS_SELLING;
						if (selected) {
							planetList.markTraderPath(this);
						} else {
						}
					} else {
						traderStatus = TraderStatus.TRADER_STATUS_CANNOT_SELL;
					}
				}
				// ---And go
			} else {
				// -------------------------------------------
				// ---Find best planet and good to buy
				// -------------------------------------------
				// planetList.markPlanetDistance( portPlanet, credits );
				// PathSeeker pathSeeker = new PathSeeker();
				final Planet bestPlanet = queryBestPlanetAndGoodToBuy(currentTime, planetList, goodSpace, getCredits());
				// ---If we did not find a good to buy, we wait for a while
				if (bestPlanet == null) {
					portRestingTime = randomGenerator.nextInt(currentTime, this, TRADER_MAX_PORT_REST_TIME) * TimeUnit.TICKS_PER_DAY;
					traderStatus = TraderStatus.TRADER_STATUS_WAITING_FOR_GOOD_PRICE_TO_BUY;
					eventManager.add(currentTime, getVolume(), SimEventType.resting, getCredits(), String.format("resting %s on %s because I cannot buy %s.", TimeUnit.toString(portRestingTime), planet.getName(), foundTradedGood ? "" : "no good is traded"));
					return false;
				}
				// targetGoodType = portPlanet.pathSeeker.destinationGoodType;
				// ---Now set the destination...
				destinationPlanet = bestPlanet;
				destinationWaypointDistanceProgress = 0;
				if (planet == destinationPlanet) {
					// best buy is where we are
					destinationWaypointPlanet = null;
					destinationWaypointIndex = -1;
					destinationWaypointDistance = 0;
					buy(currentTime, randomGenerator);
				} else {
					if (destinationPlanet != null) {
						extractWaypointList( /* portPlanet.pathSeeker */ );
						destinationWaypointPlanet = waypointList.get(1).planet;
						// MarkJumpGateUsage();
						destinationWaypointIndex = 1;
						// DestinationWaypointPlanet = PortPlanet->QueryNextWaypoint( DestinationPlanet
						// );
						destinationWaypointDistance = planet.queryDistance(destinationWaypointPlanet);
						traderStatus = TraderStatus.TRADER_STATUS_BUYING;
						if (selected) {
							planetList.markTraderPath(this);
						} else {
						}
						eventManager.add(currentTime, getVolume(), SimEventType.travel, getCredits(), String.format("%.0f from %s to %s targeting %s %s(%5.2f).", planet.pathSeeker.get(destinationPlanet).distance, planet.getName(), destinationWaypointPlanet.getName(), destinationPlanet.getName(), targetGoodType.getName(), destinationPlanet.getGoodList().getByType(targetGoodType).price));
					} else {
						traderStatus = TraderStatus.TRADER_STATUS_CANNOT_BUY;
					}
				}
			}
		}
		return false;
	}

	public void buy(final long currentTime, final MercatorRandomGenerator randomGenerator) {
		// ---Buy
		final Good portGood = planet.getGoodList().getByType(targetGoodType);
		// Good ownGood = goodList.getByType( targetGoodType );
		// ---we can only buy what we can afford, what we can store into the ship, or
		// the amount that is there. We take the minimum of all 3.
		final int amountWeCanAfford = (int) Math.floor((getCredits() - calcualteCreditBuffer()) / portGood.price);
		final int transactionAmount = Math.min(goodSpace, Math.min(amountWeCanAfford, portGood.getAmount()));
		// float transactionCost = transactionAmount * portGood.price;
		// ---In case we cannot afford any of the these prices or the planet has no
		// goods to sell
		if (transactionAmount <= 0 || portGood.price >= portGood.getAveragePrice()) {
			portRestingTime = randomGenerator.nextInt(currentTime, this, TRADER_MAX_PORT_REST_TIME) * TimeUnit.TICKS_PER_DAY;
			traderStatus = TraderStatus.TRADER_STATUS_WAITING_FOR_GOOD_PRICE_TO_BUY;
			planet.universe.eventManager.add(EventLevel.trace, currentTime, this, String.format("waiting at %s for %sf", planet.getName(), TimeUnit.toString(portRestingTime)));
			eventManager.add(currentTime, getVolume(), SimEventType.resting, getCredits(), String.format("resting %s on %s because I cannot buy good amount %d price %5.2f.", TimeUnit.toString(portRestingTime), planet.getName(), portGood.getAmount(), portGood.price));
			return;
		}
		Transaction.trade(currentTime, portGood.type, portGood.price, transactionAmount, planet, this, planet, true);
		calcualteEngineSpeed();
		lastTransaction = currentTime;
		planet.eventManager.add(currentTime, planet.getGoodList().getByType(portGood.type).getAmount(), SimEventType.sell, planet.getCredits(), String.format("%d %s(%.2f) for %5.2f from %s.", transactionAmount, portGood.type.getName(), portGood.price, portGood.price * transactionAmount, getName()));
		eventManager.add(currentTime, getVolume(), SimEventType.buy, getCredits(), String.format("%d %s(%.2f) for %5.2f from %s.", transactionAmount, portGood.type.getName(), portGood.price, portGood.price * transactionAmount, planet.getName()));
		// buy( currentTime, portGood.type, portGood.price, transactionAmount, planet,
		// planet );
		//
		sourcePlanet = planet;
		traderStatus = TraderStatus.TRADER_STATUS_WAITING_FOR_GOOD_PRICE_TO_BUY;
		// portRestingTime = randomGenerator.nextInt( currentTime, this,
		// TRADER_MAX_PORT_REST_TIME ) * TimeUnit.TICKS_PER_DAY;
		// traderStatus = TraderStatus.TRADER_STATUS_RESTING;
		// planet.universe.eventManager.add( currentTime, this, String.format( "waiting
		// at %s for %s", planet.getName(), TimeUnit.toString( portRestingTime ) ) );
		// eventManager.add( currentTime, SimEventType.resting, getCredits(),
		// String.format( "resting %s on %s after bying.", TimeUnit.toString(
		// portRestingTime ), planet.getName() ) );
	}

	/**
	 * @return credits trader should keep to be able to pay for jump cost to sell
	 *         the good
	 */
	private float calcualteCreditBuffer() {
		return planet.universe.traderCreditBuffer;
	}

	void calcualteEngineSpeed() {
		if (destinationWaypointPlanet == null) {
			currentMaxEngineSpeed = MIN_ENGINE_SPEED;
		} else {
			final float progress = clamp(destinationWaypointDistanceProgress / destinationWaypointDistance, 0.0f, 1.0f);
			float amount = 0;
			for (final Good g : getGoodList()) {
				amount += g.getAmount();
			}
			//		if (amount < 10)
			//			currentMaxEngineSpeed = MAX_ENGINE_SPEED;
			//		else
			if (progress <= 0.20f) {
				currentMaxEngineSpeed = MIN_ENGINE_SPEED + (float) Math.sin((Math.PI / 2) * progress / 0.2f) * (MAX_ENGINE_SPEED - MIN_ENGINE_SPEED) * (1 - (amount / MAX_GOOD_SPACE));
			} else if (progress >= 0.80f) {
				currentMaxEngineSpeed = MIN_ENGINE_SPEED + (float) Math.sin((Math.PI / 2) * (1.0f - (progress - 0.8f) / 0.2f)) * (MAX_ENGINE_SPEED - MIN_ENGINE_SPEED) * (1 - (amount / MAX_GOOD_SPACE));
			} else {
				currentMaxEngineSpeed = MIN_ENGINE_SPEED + (MAX_ENGINE_SPEED - MIN_ENGINE_SPEED) * (1 - (amount / MAX_GOOD_SPACE));
			}
		}
	}

	float clamp(final float value, final float min, final float max) {
		if (value > max)
			return max;
		if (value < min)
			return min;
		return value;
	}

	public float costOfDistance(final float distance) {
		return JUMP_UNIT_COST * distance;
	}

	public void create(final MercatorRandomGenerator randomGenerator) {
		goodSpace = Trader.MIN_GOOD_SPACE + randomGenerator.nextInt(0, this, Trader.MAX_GOOD_SPACE - Trader.MIN_GOOD_SPACE);
		portRestingTime = randomGenerator.nextInt(0, this, Trader.TRADER_MAX_PORT_REST_TIME) * TimeUnit.TICKS_PER_DAY;
	}

	private void extractWaypointList( /* PathSeeker pathSeeker */ ) {
		if (destinationPlanet != null) {
			waypointList.removeAllElements();
			Planet p = destinationPlanet;
			while (p != planet) {
				waypointList.add(p);
				p = planet.pathSeeker.get(p).pathSeekerNextWaypoint;
			}
			waypointList.add(p);
		}
	}

	public float getMaxEngineSpeed() {
		//max engine speed depends on how much goods we are carrying.
		return currentMaxEngineSpeed;
	}

	public void markJumpGateUsage() {
		// ---We are moving on a link
		// ---We should mark that link as beeing used by us
		if (destinationWaypointPlanet != null) {
			JumpGate jumpGate = planet.jumpGateList.queryJumpGateTo(destinationWaypointPlanet);
			if (jumpGate.usage < 16) {
				jumpGate.usage++;
			}
			jumpGate = jumpGate.targetPlanet.jumpGateList.queryJumpGateTo(planet);
			if (jumpGate.usage < 16) {
				jumpGate.usage++;
			}
		} else {
		}
	}

	private Planet queryBestPlanetAndGoodToBuy(final long currentTime, final PlanetList planetList, final int goodSpace, final float credits) throws Exception {
		// planet.universe.timeStatisticManager.start( QUERY_BEST_BUY_AIT );
		// portPlanet.pathSeeker.mapGalaxy( portPlanet, credits );
		Planet bestPlanet = null;
		// int maximumPlanetValue = 0;
		float maximumPlanetValuePerTime = 0;
		float bestPlanetValuePerTime = 0;
		final int[] anualTradingGoodVolume = planet.universe.getHistoryManager().getAnualTradingGoodVolume();
		foundTradedGood = false;
		for (final Planet p : planetList) {
			// GoodType targetGoodType = null;
			for (final Good good : p.getGoodList()) {
				// ---Prevent trader from buying good from a planet that does not produce this
				// good
				if (p.productionFacilityList.getByType(good.type) != null) {
					// ---Prevent trader to buy in bad times
					if (anualTradingGoodVolume[good.type.ordinal()] > 0 && good.price <= good.getAveragePrice()) {
						foundTradedGood = true;
						final float distance = planet.pathSeeker.get(p).distance;
						final float jumpCost = costOfDistance(distance);
						final int amountWeCanAfford = (int) Math.floor((credits - jumpCost) / good.price);
						final int transactionAmount = (Math.min(goodSpace, Math.min(amountWeCanAfford, good.getAmount())));
						// int amount = (int)Math.min( goodSpace, credits / good.price );
						// ---In case we cannot afford any of the these prices or the planet has no
						// goods to sell
						if (transactionAmount != 0) {
							// PathSeeker sellingPathSeeker = new PathSeeker();
							// sellingPathSeeker.mapGalaxy( planet, credits );
							// if ( planet.name.equals( "P-20" ) && good.type == GoodType.Food && good.price
							// < 90 )
							// System.out.println( "Found..." );
							Good goodToSell = null;
							try {
								goodToSell = good.clone();
								goodToSell.setAmount(transactionAmount);
							} catch (final CloneNotSupportedException e) {
								e.printStackTrace();
							}
							final float maxProfit = (good.averagePrice - good.price) * transactionAmount - jumpCost;
							final float planetValue = maxProfit /*- costOfDistance( planet.pathSeeker.get( p ).distance )*/;
							final float planetTime = distance / getMaxEngineSpeed();
							// assume we need to buy food at that planet to cover the whole trip
							final float catering = queryAverageFoodConsumption(planetTime) * p.queryAverageFoodPrice();
							final float planetValuePerTime = (planetValue - catering) / planetTime;
							bestPlanetValuePerTime = Math.max(bestPlanetValuePerTime, planetValuePerTime);
							if (planetValuePerTime > maximumPlanetValuePerTime) {
								targetGoodType = good.type;
								// ownGood.estimatedSellingPrice = good.price;
								maximumPlanetValuePerTime = planetValuePerTime;
								bestPlanet = p;
								// targetGoodType/*portPlanet.pathSeeker.destinationGoodType*/ = targetGoodType;
							}
							/*
							 * Planet sellingPlanet = queryBestPlanetToSell( currentTime, planetList,
							 * goodToSell, planet ); if ( sellingPlanet != null ) { Good sellingGood =
							 * sellingPlanet.goodList.getByType( good.type ); //---We found the best planet
							 * to sell this product int maxProfit = (int)( sellingGood.price - good.price )
							 * * transactionAmount; int planetValue = maxProfit - costOfDistance(
							 * portPlanet.pathSeeker.get( planet ).pathSeekerDistance +
							 * portPlanet.pathSeeker.get( sellingPlanet ).pathSeekerDistance ); if (
							 * planetValue > maximumPlanetValue ) {
							 *
							 * targetGoodType = good.type; // ownGood.estimatedSellingPrice = good.price;
							 * maximumPlanetValue = planetValue; bestPlanet = planet;
							 * portPlanet.pathSeeker.destinationGoodType = targetGoodType; } }
							 */
						}
					}
				}
			}
		}
		// planet.universe.timeStatisticManager.stop( QUERY_BEST_BUY_AIT );
		return bestPlanet;
	}

	private Planet queryBestPlanetToSell(final long currentTime, final PlanetList planetList, final Good goodToSell, final Planet portPlanet) throws Exception {
		Planet bestPlanet = null;
		// -------------------------------------------
		// ---Find best planet to sell
		// -------------------------------------------
		// ---We need to decide where to go
		// ---we need to decide what to trade
		// ---Select the planet with maximum planetValue
		// ---This value is calculated of following inputs:
		// ---1- the time needed to reach a destination
		// ---2- the difference to the average good price
		// portPlanet.pathSeeker.mapGalaxy( portPlanet, (int)( (
		// goodToSell.getMaxPrice() - goodToSell.price ) * goodToSell.amount ) );
		float maximumPlanetValuePerTime = 0;
		float bestPlanetValuePerTime = 0;
		for (final Planet p : planetList) {
			final float distance = planet.pathSeeker.get(p).distance;
			final float jumpCost = costOfDistance(distance);
			final Good good = p.getGoodList().getByType(goodToSell.type);
			// ---Prevent trader from selling good to a planet that produces this good
			if (p.productionFacilityList.getByType(good.type) == null) {
				// ---Prevent trader to sell in bad times
				if (good.isTraded(currentTime) && good.price >= good.getAveragePrice()) {
					portPlanet.pathSeeker.goodPrice = good.price;
					float profit = (good.price - goodToSell.price) * goodToSell.getAmount();
					profit = Math.min(profit, p.getCredits());
					portPlanet.pathSeeker.planetValue = profit - jumpCost;
					portPlanet.pathSeeker.time = distance / getMaxEngineSpeed();
					// assume we need to buy food at that planet to cover the whole trip
					final float catering = queryAverageFoodConsumption(portPlanet.pathSeeker.time) * p.queryAverageFoodPrice();
					final float planetValuePerTime = (portPlanet.pathSeeker.planetValue - catering) / portPlanet.pathSeeker.time;
					bestPlanetValuePerTime = Math.max(bestPlanetValuePerTime, planetValuePerTime);
					if (getName().equals("T-147") && currentTime == 72000) {
						final int a = 12;
					}
					if (planetValuePerTime > maximumPlanetValuePerTime) {
						maximumPlanetValuePerTime = planetValuePerTime;
						bestPlanet = p;
					}
				}
			}
		}
		return bestPlanet;
	}

	public void sell(final long currentTime, final MercatorRandomGenerator randomGenerator) {
		// ---Sell
		final Good portGood = planet.getGoodList().getByType(targetGoodType);
		final Good ownGood = getGoodList().getByType(targetGoodType);
		// ---we can only sell what the planet can afford, what we can store into the
		// planet, or the amount that is there. We take the minimum of all 3.
		final int amountPlanetCanAfford = (int) Math.floor(planet.getCredits() / portGood.price);
		final int transactionAmount = (Math.min(portGood.getMaxAmount() - portGood.getAmount(), Math.min(amountPlanetCanAfford, ownGood.getAmount())));
		// ---In case we cannot afford any of the these prices or the planet has no
		// goods to sell
		if (transactionAmount <= 0 || portGood.price < ownGood.price) {
			portRestingTime = randomGenerator.nextInt(currentTime, this, TRADER_MAX_PORT_REST_TIME) * TimeUnit.TICKS_PER_DAY;
			traderStatus = TraderStatus.TRADER_STATUS_WAITING_TO_SELL;
			planet.universe.eventManager.add(EventLevel.trace, currentTime, this, String.format("waiting at %s for %s", planet.getName(), TimeUnit.toString(portRestingTime)));
			eventManager.add(currentTime, getVolume(), SimEventType.resting, getCredits(), String.format("resting %s on %s because I cannot sell.", TimeUnit.toString(portRestingTime), planet.getName()));
			return;
		}
		Transaction.trade(currentTime, portGood.type, portGood.price, transactionAmount, this, planet, sourcePlanet, true);
		calcualteEngineSpeed();
		lastTransaction = currentTime;
		planet.eventManager.add(currentTime, planet.getGoodList().getByType(portGood.type).getAmount(), SimEventType.buy, planet.getCredits(), String.format("%d %s(%5.2f) for %5.2f from %s.", transactionAmount, portGood.type.getName(), portGood.price, portGood.price * transactionAmount, getName()));
		eventManager.add(currentTime, getVolume(), SimEventType.sell, getCredits(), String.format("%d %s(%5.2f) for %5.2f to %s.", transactionAmount, portGood.type.getName(), portGood.price, portGood.price * transactionAmount, planet.getName()));
		// planet.buy( currentTime, portGood.type, portGood.price, transactionAmount,
		// this, sourcePlanet );
		currentTransportedAmount += transactionAmount;
		portRestingTime = randomGenerator.nextInt(currentTime, this, TRADER_MAX_PORT_REST_TIME) * TimeUnit.TICKS_PER_DAY;
		traderStatus = TraderStatus.TRADER_STATUS_RESTING;
		planet.universe.eventManager.add(EventLevel.trace, currentTime, this, String.format("waiting at %s for %s", planet.getName(), TimeUnit.toString(portRestingTime)));
		eventManager.add(currentTime, getVolume(), SimEventType.resting, getCredits(), String.format("resting %s on %s after selling.", TimeUnit.toString(portRestingTime), planet.getName()));
	}
}
