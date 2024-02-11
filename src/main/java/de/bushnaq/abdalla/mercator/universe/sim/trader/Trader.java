/*
 * Copyright (C) 2024 Abdalla Bushnaq
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.bushnaq.abdalla.mercator.universe.sim.trader;

import de.bushnaq.abdalla.mercator.universe.event.EventLevel;
import de.bushnaq.abdalla.mercator.universe.event.SimEventType;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.good.GoodType;
import de.bushnaq.abdalla.mercator.universe.path.Path;
import de.bushnaq.abdalla.mercator.universe.path.Waypoint;
import de.bushnaq.abdalla.mercator.universe.path.WaypointList;
import de.bushnaq.abdalla.mercator.universe.path.WaypointProxy;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.planet.PlanetList;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.util.MercatorRandomGenerator;
import de.bushnaq.abdalla.mercator.util.TimeUnit;
import de.bushnaq.abdalla.mercator.util.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bushnaq Created 13.02.2005
 */
public class Trader extends Sim {
    public static final  int          MAX_ENGINE_SPEED                    = 20;
    public static final  int          MAX_GOOD_SPACE                      = 100;
    public static final  int          MIN_ENGINE_SPEED                    = 2;
    public static final  int          MIN_GOOD_SPACE                      = 30;
    public final static  int          TRADER_MAX_PORT_REST_TIME           = 3;
    public static final  float        TRADER_START_CREDITS                = 1000.0f;
    public final static  int          TRADER_STATUS_FILTER_BAD            = 0xF0;
    private static final float        ACCELLERATION_DISTANCE              = Trader3DRenderer.TRADER_SIZE_Z * 2;
    private static final float        JUMP_UNIT_COST                      = 1f / (1000f);
    static               float        lastSpeed                           = 0f;
    final                int          numberOfWaypointsBetweenTraders     = 4;
    private final        Logger       logger                              = LoggerFactory.getLogger(this.getClass());
    public               Planet       destinationPlanet                   = null; // ---The planet we want to reach ultimately
    public               float        destinationPlanetDistanceProgress   = 0;
    public               float        destinationWaypointDistance         = 0;
    public               float        destinationWaypointDistanceProgress = 0;
    public               int          destinationWaypointIndex            = 0; // ---The index into the WaypointList
    public               int          goodSpace                           = 0;
    public               long         lastTimeAdvancement                 = 0;
    public               int          lastYearTransportedAmount;
    public               long         portRestingTime                     = 0; // ---After a transaction or to wait for better prices, we recreate at a port
    public               Planet       sourcePlanet                        = null; // ---The planet origin of the good we are currently selling
    public               Waypoint     sourceWaypoint                      = null; // ---The previous waypoint we reach in our way to the destinationPlanet
    public               Waypoint     targetWaypoint                      = null; // ---The next waypoint we want to reach in our way to the destinationPlanet
    public               TraderStatus traderStatus                        = TraderStatus.TRADER_STATUS_RESTING;
    public               WaypointList waypointList                        = new WaypointList();
    public               float        x;
    public               float        y;
    public               float        z;
    private              float        currentMaxEngineSpeed               = MIN_ENGINE_SPEED;
    private              int          currentTransportedAmount;
    private              float        destinationPlanetDistance;
    private              boolean      foundTradedGood                     = false;
    private              GoodType     targetGoodType                      = null; // ---Used to remember the index of good that we where to sell
    private              long         timeDelta                           = 0;

    public Trader(final Planet planet, final String name, final float credits) throws Exception {
        super(planet, name, credits);
        creditsToSave = TRADER_START_CREDITS * 1000;
        set2DRenderer(new Trader2DRenderer(this));
        set3DRenderer(new Trader3DRenderer(this));
        calculateEngineSpeed();
    }

    public boolean advanceInTime(final long currentTime, final MercatorRandomGenerator randomGenerator, final PlanetList planetList, final boolean selected) throws Exception {
        // we only follow our sim needs when we are in a port.
        if (targetWaypoint == null) {
            if (super.advanveInTime(currentTime, randomGenerator, planet.simList)) {
                return true;
            }
        }
        timeDelta           = currentTime - lastTimeAdvancement;
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
            currentTransportedAmount  = 0;
        }
        // ---If we are in the middle of a flight we wait until we reach the next planet
        if (targetWaypoint != null) {
            reachNextPlanet(currentTime, randomGenerator);
        } else if (TimeUnit.isInt(currentTime)/* ( ( currentTime - (int)currentTime ) == 0.0f ) */) {
            //we are currently not traveling,
            //either because we just started,
            //or because we where resting at the port,
            //or because we just sold something at this port
            if (getGoodList().queryAmount() != 0) {
                // -------------------------------------------
                // ---Find best planet to sell our good
                // -------------------------------------------
                final Planet bestPlanet = queryBestPlanetToSell(currentTime, planetList, getGoodList().getByType(targetGoodType), planet);
                // ---If we did not find a good to buy, we wait for a while
                if (bestPlanet == null) {
                    portRestingTime = randomGenerator.nextInt(currentTime, this, TRADER_MAX_PORT_REST_TIME) * TimeUnit.TICKS_PER_DAY;
                    traderStatus    = TraderStatus.TRADER_STATUS_WAITING_TO_SELL;
                    eventManager.add(currentTime, getVolume(), SimEventType.resting, getCredits(), String.format("resting %s on %s because I cannot sell.", TimeUnit.toString(portRestingTime), planet.getName()));
                    return false;
                }
                // ---Now set the destination...
                destinationPlanet                   = bestPlanet;
                destinationWaypointDistanceProgress = 0;
                destinationPlanetDistanceProgress   = 0;
                if (planet == destinationPlanet) {
                    // best sell is where we are
                    targetWaypoint              = null;
                    destinationWaypointIndex    = -1;
                    destinationWaypointDistance = 0;

                    sell(currentTime, randomGenerator);
                } else {
                    if (destinationPlanet != null) {
                        //we need to travel somewhere to actually sell
                        extractWaypointList();
                        sourceWaypoint              = null;
                        targetWaypoint              = waypointList.get(0).waypoint;
                        destinationWaypointDistance = 0;
                        destinationPlanetDistance   = planet.queryDistance(waypointList);
                        destinationWaypointIndex    = 1;
                        traderStatus                = TraderStatus.TRADER_STATUS_WAITING_FOR_WAYPOINT;
                        planet.universe.eventManager.add(EventLevel.trace, currentTime, this, String.format("departing %s to reach %s", planet.getName(), destinationPlanet.city.getName()));
                        //						if (pathIsClear()) {
                        //							sourceWaypoint.trader = this;
                        //							//							if (sourceWaypoint.getName().equals(targetWaypoint.getName())) {
                        //							//								System.out.println(sourceWaypoint.getName());
                        //							//							}
                        //							planet.universe.eventManager.add(EventLevel.trace, currentTime, this, String.format("departing %s to reach %s", sourceWaypoint.getName(), destinationPlanet.city.getName()));
                        //							// MarkJumpGateUsage();
                        //							// DestinationWaypointPlanet = PortPlanet->QueryNextWaypoint( DestinationPlanet
                        //							// );
                        //							traderStatus = TraderStatus.TRADER_STATUS_SELLING;
                        //							if (selected) {
                        //								planetList.markTraderPath(this);
                        //							} else {
                        //							}
                        //						} else {
                        //							traderStatus = TraderStatus.TRADER_STATUS_WAITING_FOR_WAYPOINT;
                        //						}
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
                    traderStatus    = TraderStatus.TRADER_STATUS_WAITING_FOR_GOOD_PRICE_TO_BUY;
                    eventManager.add(currentTime, getVolume(), SimEventType.resting, getCredits(), String.format("resting %s on %s because I cannot buy %s.", TimeUnit.toString(portRestingTime), planet.getName(), foundTradedGood ? "" : "no good is traded"));
                    return false;
                }
                // targetGoodType = portPlanet.pathSeeker.destinationGoodType;
                // ---Now set the destination...
                destinationPlanet                   = bestPlanet;
                destinationWaypointDistanceProgress = 0;
                destinationPlanetDistanceProgress   = 0;
                if (planet == destinationPlanet) {
                    // best buy is where we are
                    targetWaypoint              = null;
                    destinationWaypointIndex    = -1;
                    destinationWaypointDistance = 0;
                    buy(currentTime, randomGenerator);
                } else {
                    if (destinationPlanet != null) {
                        extractWaypointList();
                        sourceWaypoint              = null;
                        targetWaypoint              = waypointList.get(0).waypoint;
                        destinationWaypointIndex    = 1;
                        destinationWaypointDistance = 0;
                        destinationPlanetDistance   = planet.queryDistance(waypointList);
                        traderStatus                = TraderStatus.TRADER_STATUS_WAITING_FOR_WAYPOINT;
                        planet.universe.eventManager.add(EventLevel.trace, currentTime, this, String.format("departing %s to reach %s", planet.getName(), destinationPlanet.city.getName()));
                        //						if (pathIsClear()) {
                        //							sourceWaypoint.trader = this;
                        //							//							if (sourceWaypoint.getName().equals(targetWaypoint.getName())) {
                        //							//								System.out.println(sourceWaypoint.getName());
                        //							//							}
                        //							// MarkJumpGateUsage();
                        //							// DestinationWaypointPlanet = PortPlanet->QueryNextWaypoint( DestinationPlanet
                        //							// );
                        //							traderStatus = TraderStatus.TRADER_STATUS_BUYING;
                        //							if (selected) {
                        //								planetList.markTraderPath(this);
                        //							} else {
                        //							}
                        //							//						eventManager.add(currentTime, getVolume(), SimEventType.travel, getCredits(), String.format("%.0f from %s to %s targeting %s %s(%5.2f).", planet.pathSeeker.get(destinationPlanet).distance, planet.getName(), destinationWaypointPlanet.getName(), destinationPlanet.getName(), targetGoodType.getName(), destinationPlanet.getGoodList().getByType(targetGoodType).price));
                        //							//TODO!!!
                        //
                        //						} else {
                        //							traderStatus = TraderStatus.TRADER_STATUS_CANNOT_SELL;
                        //						}
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
            traderStatus    = TraderStatus.TRADER_STATUS_WAITING_FOR_GOOD_PRICE_TO_BUY;
            planet.universe.eventManager.add(EventLevel.trace, currentTime, this, String.format("waiting at %s for %sf", planet.getName(), TimeUnit.toString(portRestingTime)));
            eventManager.add(currentTime, getVolume(), SimEventType.resting, getCredits(), String.format("resting %s on %s because I cannot buy good amount %d price %5.2f.", TimeUnit.toString(portRestingTime), planet.getName(), portGood.getAmount(), portGood.price));
            return;
        }
        Transaction.trade(currentTime, portGood.type, portGood.price, transactionAmount, planet, this, planet, true);
        calculateEngineSpeed();
        //		lastTransaction = currentTime;
        planet.universe.eventManager.add(EventLevel.trace, currentTime, this, String.format("buys %d %s(%.2f) for %5.2f from %s.", transactionAmount, portGood.type.getName(), portGood.price, portGood.price * transactionAmount, getName()));
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
     * the good
     */
    private float calcualteCreditBuffer() {
        return planet.universe.traderCreditBuffer;
    }

    void calculateEngineSpeed() {

        if (targetWaypoint == null) {
            currentMaxEngineSpeed = MIN_ENGINE_SPEED;
        } else {
//			final float overAllProgress = clamp(destinationPlanetDistanceProgress / destinationPlanetDistance, 0.0f, 1.0f);
            float amount = 0;
            for (final Good g : getGoodList()) {
                amount += g.getAmount();
            }
            if (destinationPlanetDistanceProgress <= ACCELLERATION_DISTANCE) {
                currentMaxEngineSpeed = MIN_ENGINE_SPEED + (float) Math.sin((Math.PI / 2) * destinationPlanetDistanceProgress / ACCELLERATION_DISTANCE) * (MAX_ENGINE_SPEED - MIN_ENGINE_SPEED) * (1 - (amount / MAX_GOOD_SPACE));
            } else if (destinationPlanetDistance - destinationPlanetDistanceProgress <= ACCELLERATION_DISTANCE) {
                currentMaxEngineSpeed = MIN_ENGINE_SPEED + (float) Math.sin((Math.PI / 2) * (1.0f - (destinationPlanetDistance - destinationPlanetDistanceProgress) / ACCELLERATION_DISTANCE)) * (MAX_ENGINE_SPEED - MIN_ENGINE_SPEED) * (1 - (amount / MAX_GOOD_SPACE));
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
        goodSpace       = Trader.MIN_GOOD_SPACE + randomGenerator.nextInt(0, this, Trader.MAX_GOOD_SPACE - Trader.MIN_GOOD_SPACE);
        portRestingTime = randomGenerator.nextInt(0, this, Trader.TRADER_MAX_PORT_REST_TIME) * TimeUnit.TICKS_PER_DAY;
    }

    private void extractWaypointList() {
        if (destinationPlanet != null) {
            waypointList.removeAllElements();
            Waypoint p = destinationPlanet;
            while (p != planet) {
                waypointList.add(p);
                p = planet.pathSeeker.get(p).pathSeekerNextWaypoint;
            }
            waypointList.add(p);
        }
    }

    private void freePrevWaypoints() {
        sourceWaypoint.trader = null;//free
    }

    public float getMaxEngineSpeed() {
        //max engine speed depends on how much goods we are carrying.
        return currentMaxEngineSpeed;
    }

    public void markJumpGateUsage() {
        // ---We are moving on a link
        // ---We should mark that link as beeing used by us
        if (targetWaypoint != null) {
            final Path pathTo = planet.pathList.queryJumpGateTo(targetWaypoint);
            if (pathTo.usage < 16) {
                pathTo.usage++;
            }
            //			Path pathFrom = pathTo.target.pathList.queryJumpGateTo(planet);
            //			if (pathFrom.usage < 16) {
            //				pathFrom.usage++;
            //			}
        } else {
        }
    }

    private boolean pathIsClear() {
        //next waypoint must be free
        //destinationWaypointIndex-1
        if (sourceWaypoint != null && sourceWaypoint.trader != null && sourceWaypoint.trader != this)
            return false;
        if (targetWaypoint.trader != null && targetWaypoint.trader != this)
            return false;
        for (int i = 0; i < numberOfWaypointsBetweenTraders - 1; i++) {
            //next waypoint must be either the last one, or the one after is also free
            if ((destinationWaypointIndex + i < waypointList.size()) && waypointList.get(destinationWaypointIndex + i).waypoint.trader != null && waypointList.get(destinationWaypointIndex + i).waypoint.trader != this)
                return false;
        }
        return true;
    }

    private Planet queryBestPlanetAndGoodToBuy(final long currentTime, final PlanetList planetList, final int goodSpace, final float credits) throws Exception {
        // planet.universe.timeStatisticManager.start( QUERY_BEST_BUY_AIT );
        // portPlanet.pathSeeker.mapGalaxy( portPlanet, credits );
        Planet bestPlanet = null;
        // int maximumPlanetValue = 0;
        float       maximumPlanetValuePerTime = 0;
        float       bestPlanetValuePerTime    = 0;
        final int[] anualTradingGoodVolume    = planet.universe.getHistoryManager().getAnualTradingGoodVolume();
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
                        final float distance          = planet.pathSeeker.get(p).distance;
                        final float jumpCost          = costOfDistance(distance);
                        final int   amountWeCanAfford = (int) Math.floor((credits - jumpCost) / good.price);
                        final int   transactionAmount = (Math.min(goodSpace, Math.min(amountWeCanAfford, good.getAmount())));
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
                            final float maxProfit   = (good.averagePrice - good.price) * transactionAmount - jumpCost;
                            final float planetValue = maxProfit /*- costOfDistance( planet.pathSeeker.get( p ).distance )*/;
                            final float planetTime  = distance / getMaxEngineSpeed();
                            // assume we need to buy food at that planet to cover the whole trip
                            final float catering           = queryAverageFoodConsumption(planetTime) * p.queryAverageFoodPrice();
                            final float planetValuePerTime = (planetValue - catering) / planetTime;
                            bestPlanetValuePerTime = Math.max(bestPlanetValuePerTime, planetValuePerTime);
                            if (planetValuePerTime > maximumPlanetValuePerTime) {
                                targetGoodType = good.type;
                                // ownGood.estimatedSellingPrice = good.price;
                                maximumPlanetValuePerTime = planetValuePerTime;
                                bestPlanet                = p;
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
        float bestPlanetValuePerTime    = 0;
        for (final Planet p : planetList) {
            final float distance = planet.pathSeeker.get(p).distance;
            final float jumpCost = costOfDistance(distance);
            final Good  good     = p.getGoodList().getByType(goodToSell.type);
            // ---Prevent trader from selling good to a planet that produces this good
            if (p.productionFacilityList.getByType(good.type) == null) {
                // ---Prevent trader to sell in bad times
                if (good.isTraded(currentTime) && good.price >= good.getAveragePrice()) {
                    portPlanet.pathSeeker.goodPrice = good.price;
                    float profit = (good.price - goodToSell.price) * goodToSell.getAmount();
                    profit                            = Math.min(profit, p.getCredits());
                    portPlanet.pathSeeker.planetValue = profit - jumpCost;
                    portPlanet.pathSeeker.time        = distance / getMaxEngineSpeed();
                    // assume we need to buy food at that planet to cover the whole trip
                    final float catering           = queryAverageFoodConsumption(portPlanet.pathSeeker.time) * p.queryAverageFoodPrice();
                    final float planetValuePerTime = (portPlanet.pathSeeker.planetValue - catering) / portPlanet.pathSeeker.time;
                    bestPlanetValuePerTime = Math.max(bestPlanetValuePerTime, planetValuePerTime);
                    if (getName().equals("T-147") && currentTime == 72000) {
                        final int a = 12;
                    }
                    if (planetValuePerTime > maximumPlanetValuePerTime) {
                        maximumPlanetValuePerTime = planetValuePerTime;
                        bestPlanet                = p;
                    }
                }
            }
        }
        return bestPlanet;
    }

    private void reachNextPlanet(final long currentTime, final MercatorRandomGenerator randomGenerator) {
        // ---Every time we use a jump gate, we mark it. This helps to visualize much
        // used jump gates.
        //		markJumpGateUsage();//TODO!!!
        final float delta = (getMaxEngineSpeed() * timeDelta) / TimeUnit.TICKS_PER_DAY;
        if (pathIsClear())
            traderStatus = TraderStatus.TRADER_STATUS_BUYING;
        if (traderStatus != TraderStatus.TRADER_STATUS_WAITING_FOR_WAYPOINT) {
            destinationWaypointDistanceProgress += delta;
            destinationPlanetDistanceProgress += delta;
        }
        if (destinationWaypointDistanceProgress >= destinationWaypointDistance && TimeUnit.isInt(currentTime)/* ( ( currentTime - (int)currentTime ) == 0.0f ) */) {
            //if next waypoint is still not a city?
            if (pathIsClear()) {
                if (traderStatus != TraderStatus.TRADER_STATUS_WAITING_FOR_WAYPOINT) {
                    //					if (sourceWaypoint.getName().equals(targetWaypoint.getName())) {
                    //						System.out.println(sourceWaypoint.getName());
                    //					}
                    float d = 0;
                    for (int i = 1; i < destinationWaypointIndex - 1; i++) {
                        final WaypointProxy sw = waypointList.get(i - 1);
                        final WaypointProxy tw = waypointList.get(i);
                        d += sw.waypoint.queryDistance(tw.waypoint);
                    }
                    planet.universe.eventManager.add(EventLevel.trace, currentTime, this, String.format("reached waypoint %s %f %f %f", targetWaypoint.getName(), d + destinationWaypointDistanceProgress, destinationPlanetDistanceProgress, destinationPlanetDistance));
                } else {
                    planet.universe.eventManager.add(EventLevel.trace, currentTime, this, String.format("waiting for waypoint %s to become clear", targetWaypoint.getName()));
                }
                if (targetWaypoint.city == null) {
                    if (sourceWaypoint != null)
                        sourceWaypoint.trader = null;//free
                    sourceWaypoint = targetWaypoint;
                    reserveNextWaypoints();
                    targetWaypoint = waypointList.get(destinationWaypointIndex).waypoint;
                    //					if (sourceWaypoint.getName().equals(targetWaypoint.getName())) {
                    //						System.out.println(sourceWaypoint.getName());
                    //					}
                    destinationWaypointIndex++;
                    destinationWaypointDistanceProgress = destinationWaypointDistanceProgress - destinationWaypointDistance;
                    destinationWaypointDistance         = sourceWaypoint.queryDistance(targetWaypoint);
                    traderStatus                        = TraderStatus.TRADER_STATUS_BUYING;
                } else {
                    //we reached a city
                    if (sourceWaypoint != null)
                        freePrevWaypoints();
                    // ---Pay for the jump
                    final float costOfJump = costOfDistance(destinationWaypointDistance);
                    setCredits(getCredits() - costOfJump);
                    // TODO pay the planet
                    //					targetWaypoint = waypointList.get(destinationWaypointIndex).waypoint;
                    targetWaypoint.city.setCredits(targetWaypoint.city.getCredits() + costOfJump);
                    eventManager.add(currentTime, getVolume(), SimEventType.payJump, getCredits(), String.format("and payed %5.2f to %s.", costOfJump, planet.getName()));
                    // ---we reached a new port
                    destinationWaypointDistance         = 0;
                    destinationWaypointDistanceProgress = 0;
                    planet.traderList.remove(this);
                    planet = (Planet) targetWaypoint;
                    planet.traderList.add(this);
                    planet.universe.eventManager.add(EventLevel.trace, currentTime, this, String.format("arrived at %s", targetWaypoint.city.getName()));
                    if (targetWaypoint == destinationPlanet) {
                        destinationPlanetDistanceProgress = 0;
                        destinationWaypointIndex          = 0;
                        // ---We reached our destination
                        destinationPlanet = null;
                        targetWaypoint    = null;
                        if (getGoodList().queryAmount() != 0) {
                            sell(currentTime, randomGenerator);
                        } else {
                            buy(currentTime, randomGenerator);
                        }
                    } else {
                        //we reached some city on the way
                        // ---this is a multi city trip. Plan the next part of the trip
                        //						destinationWaypointIndex++;
                        sourceWaypoint = targetWaypoint;
                        //						destinationWaypointIndex++;
                        targetWaypoint = waypointList.get(destinationWaypointIndex).waypoint;
                        destinationWaypointIndex++;
                        reserveNextWaypoints();
                        destinationWaypointDistance = sourceWaypoint.queryDistance(targetWaypoint);
                        if (pathIsClear()) {
                            //							if (sourceWaypoint.getName().equals(targetWaypoint.getName())) {
                            //								System.out.println(sourceWaypoint.getName());
                            //							}
                            //							if (targetWaypoint != null) {
                            //							} else {
                            //							}
                        } else {
                            traderStatus = TraderStatus.TRADER_STATUS_WAITING_FOR_WAYPOINT;
                        }
                    }
                }
            } else {
                traderStatus = TraderStatus.TRADER_STATUS_WAITING_FOR_WAYPOINT;
            }
        }
    }

    private void reserveNextWaypoints() {
        sourceWaypoint.trader = this;
        targetWaypoint.trader = this;
        for (int i = 0; i < numberOfWaypointsBetweenTraders - 2; i++) {
            //next waypoint must be either the last one, or the one after is also free
            if (destinationWaypointIndex + i < waypointList.size())
                waypointList.get(destinationWaypointIndex + i).waypoint.trader = this;
        }
    }

    public void sell(final long currentTime, final MercatorRandomGenerator randomGenerator) {
        // ---Sell
        final Good portGood = planet.getGoodList().getByType(targetGoodType);
        final Good ownGood  = getGoodList().getByType(targetGoodType);
        // ---we can only sell what the planet can afford, what we can store into the
        // planet, or the amount that is there. We take the minimum of all 3.
        final int amountPlanetCanAfford = (int) Math.floor(planet.getCredits() / portGood.price);
        final int transactionAmount     = (Math.min(portGood.getMaxAmount() - portGood.getAmount(), Math.min(amountPlanetCanAfford, ownGood.getAmount())));
        // ---In case we cannot afford any of the these prices or the planet has no
        // goods to sell
        if (transactionAmount <= 0 || portGood.price < ownGood.price) {
            portRestingTime = randomGenerator.nextInt(currentTime, this, TRADER_MAX_PORT_REST_TIME) * TimeUnit.TICKS_PER_DAY;
            traderStatus    = TraderStatus.TRADER_STATUS_WAITING_TO_SELL;
            planet.universe.eventManager.add(EventLevel.trace, currentTime, this, String.format("waiting at %s for %s", planet.getName(), TimeUnit.toString(portRestingTime)));
            eventManager.add(currentTime, getVolume(), SimEventType.resting, getCredits(), String.format("resting %s on %s because I cannot sell.", TimeUnit.toString(portRestingTime), planet.getName()));
            return;
        }
        Transaction.trade(currentTime, portGood.type, portGood.price, transactionAmount, this, planet, sourcePlanet, true);
        calculateEngineSpeed();
        //		lastTransaction = currentTime;
        planet.universe.eventManager.add(EventLevel.trace, currentTime, this, String.format("sells %d %s(%5.2f) for %5.2f from %s.", transactionAmount, portGood.type.getName(), portGood.price, portGood.price * transactionAmount, getName()));
        planet.eventManager.add(currentTime, planet.getGoodList().getByType(portGood.type).getAmount(), SimEventType.buy, planet.getCredits(), String.format("%d %s(%5.2f) for %5.2f from %s.", transactionAmount, portGood.type.getName(), portGood.price, portGood.price * transactionAmount, getName()));
        eventManager.add(currentTime, getVolume(), SimEventType.sell, getCredits(), String.format("%d %s(%5.2f) for %5.2f to %s.", transactionAmount, portGood.type.getName(), portGood.price, portGood.price * transactionAmount, planet.getName()));
        // planet.buy( currentTime, portGood.type, portGood.price, transactionAmount,
        // this, sourcePlanet );
        currentTransportedAmount += transactionAmount;
        portRestingTime = randomGenerator.nextInt(currentTime, this, TRADER_MAX_PORT_REST_TIME) * TimeUnit.TICKS_PER_DAY;
        traderStatus    = TraderStatus.TRADER_STATUS_RESTING;
        planet.universe.eventManager.add(EventLevel.trace, currentTime, this, String.format("waiting at %s for %s", planet.getName(), TimeUnit.toString(portRestingTime)));
        eventManager.add(currentTime, getVolume(), SimEventType.resting, getCredits(), String.format("resting %s on %s after selling.", TimeUnit.toString(portRestingTime), planet.getName()));
    }
}
