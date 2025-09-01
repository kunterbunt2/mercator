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

import com.badlogic.gdx.math.Vector3;
import de.bushnaq.abdalla.engine.IGameEngine;
import de.bushnaq.abdalla.engine.audio.OpenAlException;
import de.bushnaq.abdalla.engine.event.EventLevel;
import de.bushnaq.abdalla.mercator.universe.event.SimEventType;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.good.GoodType;
import de.bushnaq.abdalla.mercator.universe.path.Path;
import de.bushnaq.abdalla.mercator.universe.path.WaypointProxy;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.planet.PlanetList;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.util.MercatorRandomGenerator;
import de.bushnaq.abdalla.mercator.util.TimeUnit;
import de.bushnaq.abdalla.mercator.util.Transaction;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.bushnaq.abdalla.mercator.universe.sim.trader.Trader3DRenderer.TRADER_DOCKING_HEIGHT;

/**
 * @author bushnaq Created 13.02.2005
 */
public class Trader extends Sim {
    private static final float              JUMP_UNIT_COST            = 1f / (1000f);
    public static final  int                MAX_GOOD_SPACE            = 100;
    public static final  int                MIN_GOOD_SPACE            = 25;
    public final static  int                TRADER_MAX_PORT_REST_TIME = 3;
    public static final  float              TRADER_START_CREDITS      = 1000.0f;
    public               TraderRadioChannel communicationPartner;
    public               long               currentTime               = 0;
    private              int                currentTransportedAmount;
    @Getter
    private final        Engine             engine                    = new Engine(this);
    private              boolean            foundTradedGood           = false;
    public               int                goodSpace                 = 0;
    @Getter
    private final        int                id;
    public               int                lastYearTransportedAmount;
    private final        Logger             logger                    = LoggerFactory.getLogger(this.getClass());
    @Getter
    private final        ManeuveringSystem  maneuveringSystem         = new ManeuveringSystem(this);
    public               Navigator          navigator                 = new Navigator(this);
    public               OnTraderEvent      onEvent                   = new OnTraderEvent(this);
    public               long               portRestingTime           = 0; // ---After a transaction or to wait for better prices, we recreate at a port
    protected            boolean            selected                  = false;
    public final         Vector3            speed                     = new Vector3(0, 0, 0);
    private              GoodType           targetGoodType            = null; // ---Used to remember the index of good that we where to sell
    private              long               timeDelta                 = 0;
    @Getter
    private              TraderStatus       traderStatus              = TraderStatus.TRADER_STATUS_RESTING;
    @Getter
    private              TraderSubStatus    traderSubStatus           = TraderSubStatus.TRADER_STATUS_DOCKED;
    public               float              x;
    public               float              y                         = TRADER_DOCKING_HEIGHT;
    public               float              z;

    public Trader(int id, final Planet planet, final String name, final float credits) throws Exception {
        super(planet, name, credits);
        eventManager.add(EventLevel.trace, 0, this, String.format("We are docked at '%s'", planet.getName()));
        this.id       = id;
        creditsToSave = TRADER_START_CREDITS * 1000;
        set2DRenderer(new Trader2DRenderer(this));
        set3DRenderer(new Trader3DRenderer(this));
//        calculateEngineSpeed();
    }

    public boolean advanceInTime(final long currentTime, final MercatorRandomGenerator randomGenerator, final PlanetList planetList, final boolean selected) throws Exception {
//        realTimeDelta = Gdx.graphics.getDeltaTime();
        // we only follow our sim needs when we are in a port.
        if (navigator.nextWaypoint == null) {
            if (super.advanveInTime(currentTime, randomGenerator, planet.simList)) {
                return true;
            }
        }
        timeDelta        = currentTime - this.currentTime;
        this.currentTime = currentTime;
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

        checkStateEngine(currentTime, randomGenerator, planetList);
        return false;
    }

    public void buy(final long currentTime, final MercatorRandomGenerator randomGenerator) {
        // ---Buy
        final Good portGood = planet.getGoodList().getByType(targetGoodType);
        // Good ownGood = goodList.getByType( targetGoodType );
        // ---we can only buy what we can afford, what we can store into the ship, or
        // the amount that is there. We take the minimum of all 3.
        final int amountWeCanAfford = (int) Math.floor((getCredits() - calculateCreditBuffer()) / portGood.price);
        final int transactionAmount = Math.min(goodSpace, Math.min(amountWeCanAfford, portGood.getAmount()));
        // float transactionCost = transactionAmount * portGood.price;
        // ---In case we cannot afford any of the these prices or the planet has no
        // goods to sell
        if (transactionAmount <= 0 || portGood.price >= portGood.getAveragePrice()) {
            portRestingTime = randomGenerator.nextInt(currentTime, this, TRADER_MAX_PORT_REST_TIME) * TimeUnit.TICKS_PER_DAY;
            setTraderStatus(TraderStatus.TRADER_STATUS_WAITING_FOR_GOOD_PRICE_TO_BUY);
            eventManager.add(EventLevel.trace, currentTime, this, String.format("waiting at %s for %sf", planet.getName(), TimeUnit.toString(portRestingTime)));
            eventManager.add(currentTime, getVolume(), SimEventType.resting, getCredits(), String.format("resting %s on %s because I cannot buy good amount %d price %5.2f.", TimeUnit.toString(portRestingTime), planet.getName(), portGood.getAmount(), portGood.price));
            return;
        }
        Transaction.trade(currentTime, portGood.type, portGood.price, transactionAmount, planet, this, planet, true);
//        calculateEngineSpeed();
        //		lastTransaction = currentTime;
        eventManager.add(EventLevel.trace, currentTime, this, String.format("buys %d %s(%.2f) for %5.2f from %s.", transactionAmount, portGood.type.getName(), portGood.price, portGood.price * transactionAmount, getName()));
        planet.eventManager.add(currentTime, planet.getGoodList().getByType(portGood.type).getAmount(), SimEventType.sell, planet.getCredits(), String.format("%d %s(%.2f) for %5.2f from %s.", transactionAmount, portGood.type.getName(), portGood.price, portGood.price * transactionAmount, getName()));
        eventManager.add(currentTime, getVolume(), SimEventType.buy, getCredits(), String.format("%d %s(%.2f) for %5.2f from %s.", transactionAmount, portGood.type.getName(), portGood.price, portGood.price * transactionAmount, planet.getName()));
        // buy( currentTime, portGood.type, portGood.price, transactionAmount, planet,
        // planet );
        //
        navigator.sourcePlanet = planet;
        setTraderStatus(TraderStatus.TRADER_STATUS_WAITING_FOR_GOOD_PRICE_TO_BUY);
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
    private float calculateCreditBuffer() {
        return planet.universe.traderCreditBuffer;
    }

    private void checkArriving(long currentTime, MercatorRandomGenerator randomGenerator) {
        if (navigator.destinationWaypointDistanceProgress >= navigator.destinationWaypointDistance && TimeUnit.isInt(currentTime)/* ( ( currentTime - (int)currentTime ) == 0.0f ) */) {
            onEvent.reachedWaypoint();
        }
    }

    private void checkStateEngine(final long currentTime, final MercatorRandomGenerator randomGenerator, PlanetList planetList) throws Exception {
        // ---Every time we use a jump gate, we mark it. This helps to visualize much
        // used jump gates.
        //		markJumpGateUsage();//TODO!!!
        if (timeDelta == 0) return;
        switch (getTraderSubStatus()) {
            case TRADER_STATUS_ALIGNING:
                //wait until we have aligned ourselves
                break;
            case TRADER_STATUS_WAITING_FOR_WAYPOINT:
                if (navigator.nextWaypoint != null)
                    if (navigator.pathIsClear()) {
                        onEvent.waypointCleared();
                    }
                break;
            case TRADER_STATUS_ACCELERATING:
                //wait until we start decelerating
                break;
            case TRADER_STATUS_DECELERATING: {
                if (navigator.nextWaypoint != null)
                    checkArriving(currentTime, randomGenerator);
            }
            break;
            case TRADER_STATUS_DOCKING_ACC: {
            }
            break;
            case TRADER_STATUS_DOCKING_DEC: {
            }
            case TRADER_STATUS_REQUESTING_UNDOCKING: {
                //wait for approval
            }
            case TRADER_STATUS_REQUESTING_DOCKING: {
                //wait for approval
            }
            break;
            case TRADER_STATUS_DOCKED: {
                if (navigator.nextWaypoint != null)
                    tradeInPort(currentTime, randomGenerator);
                else
                    planJourney(currentTime, randomGenerator, planetList);
            }
            break;
        }
    }

    float clamp(final float value, final float min, final float max) {
        if (value > max) return max;
        if (value < min) return min;
        return value;
    }

    public void clearDock(Planet planet) {
        planet.clearDock(communicationPartner);
    }

    public float costOfDistance(final float distance) {
        return JUMP_UNIT_COST * distance;
    }

    public void create(IGameEngine gameEngine, final MercatorRandomGenerator randomGenerator) throws OpenAlException {
        goodSpace            = Trader.MIN_GOOD_SPACE + randomGenerator.nextInt(0, this, Trader.MAX_GOOD_SPACE - Trader.MIN_GOOD_SPACE);
        portRestingTime      = randomGenerator.nextInt(0, this, Trader.TRADER_MAX_PORT_REST_TIME) * TimeUnit.TICKS_PER_DAY;
        communicationPartner = new TraderRadioChannel(gameEngine, this);
    }

    public void markJumpGateUsage() {
        // ---We are moving on a link
        // ---We should mark that link as beeing used by us
        if (navigator.nextWaypoint != null) {
            final Path pathTo = planet.pathList.queryJumpGateTo(navigator.nextWaypoint);
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

    private boolean planJourney(long currentTime, MercatorRandomGenerator randomGenerator, PlanetList planetList) throws Exception {
        if (traderSubStatus == TraderSubStatus.TRADER_STATUS_DOCKED && TimeUnit.isInt(currentTime)) {
            //we are currently not traveling,
            //either because we just started,
            //or because we were resting at the port,
            //or because we just sold something at this port
            //or because we just bought something at this port
            if (getGoodList().queryAmount() != 0) {
                // -------------------------------------------
                // ---Find best planet to sell our good
                // -------------------------------------------
                setTraderStatus(TraderStatus.TRADER_STATUS_SELLING);
                final Planet bestPlanet = queryBestPlanetToSell(currentTime, planetList, getGoodList().getByType(targetGoodType), planet);
                // ---If we did not find a good to buy, we wait for a while
                if (bestPlanet == null) {
                    portRestingTime = randomGenerator.nextInt(currentTime, this, TRADER_MAX_PORT_REST_TIME) * TimeUnit.TICKS_PER_DAY;
                    setTraderStatus(TraderStatus.TRADER_STATUS_WAITING_TO_SELL);
                    eventManager.add(currentTime, getVolume(), SimEventType.resting, getCredits(), String.format("resting %s on %s because I cannot sell.", TimeUnit.toString(portRestingTime), planet.getName()));
                    return true;
                }
                // ---Now set the destination...
                navigator.destinationPlanet                   = bestPlanet;
                navigator.destinationWaypointDistanceProgress = 0;
                navigator.destinationPlanetDistanceProgress   = 0;
                if (planet == navigator.destinationPlanet) {
                    //- best sell is where we are
                    navigator.nextWaypoint                = null;
                    navigator.destinationWaypointIndex    = -1;
                    navigator.destinationWaypointDistance = 0;
                    sell(currentTime, randomGenerator);
                } else {
                    //- we need to travel somewhere to actually sell
                    //- as for permission to undock
                    setTraderSubStatus(TraderSubStatus.TRADER_STATUS_REQUESTING_UNDOCKING);
                    //- need to wait for permission to undock
                    navigator.extractWaypointList();
                    navigator.previousWaypoint            = navigator.sourcePlanet;
                    navigator.nextWaypoint                = navigator.waypointList.get(1).waypoint;
                    navigator.destinationWaypointDistance = navigator.previousWaypoint.queryDistance(navigator.nextWaypoint);
                    navigator.destinationPlanetDistance   = planet.queryDistance(navigator.waypointList);
                    navigator.destinationWaypointIndex    = 1;
                    communicationPartner.requestUndocking(planet);
                    eventManager.add(EventLevel.trace, currentTime, this, String.format("departing '%s' to reach '%s' %s", planet.getName(), navigator.destinationPlanet.city.getName(), navigator.WaypointPortsAsString()));
                    planet.eventManager.add(EventLevel.trace, currentTime, this, String.format("'%s' undocking", getName()));//for planet logs
                }
                // ---And go
            } else {
                // -------------------------------------------
                // ---Find best planet and good to buy
                // -------------------------------------------
                // planetList.markPlanetDistance( portPlanet, credits );
                // PathSeeker pathSeeker = new PathSeeker();
                setTraderStatus(TraderStatus.TRADER_STATUS_BUYING);
                final Planet bestPlanet = queryBestPlanetAndGoodToBuy(currentTime, planetList, goodSpace, getCredits());
                // ---If we did not find a good to buy, we wait for a while
                if (bestPlanet == null) {
                    portRestingTime = randomGenerator.nextInt(currentTime, this, TRADER_MAX_PORT_REST_TIME) * TimeUnit.TICKS_PER_DAY;
                    setTraderStatus(TraderStatus.TRADER_STATUS_WAITING_FOR_GOOD_PRICE_TO_BUY);
                    eventManager.add(currentTime, getVolume(), SimEventType.resting, getCredits(), String.format("resting %s on %s because I cannot buy %s.", TimeUnit.toString(portRestingTime), planet.getName(), foundTradedGood ? "" : "no good is traded"));
                    return true;
                }
                // ---Now set the destination...
                navigator.destinationPlanet                   = bestPlanet;
                navigator.destinationWaypointDistanceProgress = 0;
                navigator.destinationPlanetDistanceProgress   = 0;
                if (planet == navigator.destinationPlanet) {
                    //- best buy is where we are
                    navigator.nextWaypoint                = null;
                    navigator.destinationWaypointIndex    = -1;
                    navigator.destinationWaypointDistance = 0;
                    buy(currentTime, randomGenerator);
                } else {
                    //- we need to travel somewhere to actually buy
                    //- as for permission to undock
                    setTraderSubStatus(TraderSubStatus.TRADER_STATUS_REQUESTING_UNDOCKING);
                    navigator.extractWaypointList();
                    navigator.previousWaypoint            = null;
                    navigator.nextWaypoint                = navigator.waypointList.get(1).waypoint;
                    navigator.destinationWaypointDistance = 0;
                    navigator.destinationPlanetDistance   = planet.queryDistance(navigator.waypointList);
                    navigator.destinationWaypointIndex    = 1;
//                    setTraderSubStatus(TraderSubStatus.TRADER_STATUS_ALIGNING);
//                    maneuveringSystem.startRotation();
                    communicationPartner.requestUndocking(planet);
                    eventManager.add(EventLevel.trace, currentTime, this, String.format("departing '%s' to reach '%s' %s", planet.getName(), navigator.destinationPlanet.city.getName(), navigator.WaypointPortsAsString()));
                    planet.eventManager.add(EventLevel.trace, currentTime, this, String.format("'%s' undocking", getName()));//for planet logs
                }
            }
        }
        return false;
    }

    private Planet queryBestPlanetAndGoodToBuy(final long currentTime, final PlanetList planetList, final int goodSpace, final float credits) throws Exception {
        Planet      bestPlanet                = null;
        float       maximumPlanetValuePerTime = 0;
        float       bestPlanetValuePerTime    = 0;
        final int[] anualTradingGoodVolume    = planet.universe.getHistoryManager().getAnualTradingGoodVolume();
        foundTradedGood = false;
        for (final Planet p : planetList) {
            // GoodType targetGoodType = null;
            for (final Good good : p.getGoodList()) {
                // -Prevent trader from buying good from a planet that does not produce this good
                if (p.productionFacilityList.getByType(good.type) != null) {
                    // ---Prevent trader to buy in bad times
                    if (anualTradingGoodVolume[good.type.ordinal()] > 0 && good.price <= good.getAveragePrice()) {
                        foundTradedGood = true;
                        final float distance          = planet.pathSeeker.get(p).distance;
                        final float jumpCost          = costOfDistance(distance);
                        final int   amountWeCanAfford = (int) Math.floor((credits - jumpCost) / good.price);
                        final int   transactionAmount = (Math.min(goodSpace, Math.min(amountWeCanAfford, good.getAmount())));
                        // -In case we cannot afford any of the these prices or the planet has no
                        // -goods to sell
                        if (transactionAmount != 0) {
                            Good goodToSell = null;
                            try {
                                goodToSell = good.clone();
                                goodToSell.setAmount(transactionAmount);
                            } catch (final CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                            final float maxProfit   = (good.averagePrice - good.price) * transactionAmount - jumpCost;
                            final float planetValue = maxProfit /*- costOfDistance( planet.pathSeeker.get( p ).distance )*/;
                            final float planetTime  = distance / engine.getEngineSpeed();
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
        float maximumPlanetValuePerTime = 0;
        float bestPlanetValuePerTime    = 0;
        for (final Planet p : planetList) {
            final float distance = planet.pathSeeker.get(p).distance;
            final float jumpCost = costOfDistance(distance);
            final Good  good     = p.getGoodList().getByType(goodToSell.type);
            // -Prevent trader from selling good to a planet that produces this good
            if (p.productionFacilityList.getByType(good.type) == null) {
                // -Prevent trader to sell in bad times
                if (good.isTraded(currentTime) && good.price >= good.getAveragePrice()) {
                    portPlanet.pathSeeker.goodPrice = good.price;
                    float profit = (good.price - goodToSell.price) * goodToSell.getAmount();
                    profit                            = Math.min(profit, p.getCredits());
                    portPlanet.pathSeeker.planetValue = profit - jumpCost;
                    portPlanet.pathSeeker.time        = distance / engine.getEngineSpeed();
                    //- assume we need to buy food at that planet to cover the whole trip
                    final float catering           = queryAverageFoodConsumption(portPlanet.pathSeeker.time) * p.queryAverageFoodPrice();
                    final float planetValuePerTime = (portPlanet.pathSeeker.planetValue - catering) / portPlanet.pathSeeker.time;
                    bestPlanetValuePerTime = Math.max(bestPlanetValuePerTime, planetValuePerTime);
                    if (planetValuePerTime > maximumPlanetValuePerTime) {
                        maximumPlanetValuePerTime = planetValuePerTime;
                        bestPlanet                = p;
                    }
                }
            }
        }
        return bestPlanet;
    }

    public void select() throws OpenAlException {
        selected = true;
        for (int i = 0; i < navigator.waypointList.size() - 1; i++) {
            WaypointProxy waypointProxy     = navigator.waypointList.get(i);
            WaypointProxy nextWaypointProxy = navigator.waypointList.get(i + 1);
            for (Path p : waypointProxy.waypoint.pathList) {
                if (p.target.equals(nextWaypointProxy.waypoint)) p.selected = true;
            }
        }
        communicationPartner.select();
    }

    public void sell(final long currentTime, final MercatorRandomGenerator randomGenerator) {
        // ---Sell
        final Good portGood = planet.getGoodList().getByType(targetGoodType);
        final Good ownGood  = getGoodList().getByType(targetGoodType);
        // -we can only sell what the planet can afford, what we can store into the planet, or the amount that is there. We take the minimum of all 3.
        final int amountPlanetCanAfford = (int) Math.floor(planet.getCredits() / portGood.price);
        final int transactionAmount     = (Math.min(portGood.getMaxAmount() - portGood.getAmount(), Math.min(amountPlanetCanAfford, ownGood.getAmount())));
        // -In case we cannot afford any of the these prices or the planet has no goods to sell
        if (transactionAmount <= 0 || portGood.price < ownGood.price) {
            portRestingTime = randomGenerator.nextInt(currentTime, this, TRADER_MAX_PORT_REST_TIME) * TimeUnit.TICKS_PER_DAY;
            setTraderStatus(TraderStatus.TRADER_STATUS_WAITING_TO_SELL);
            eventManager.add(EventLevel.trace, currentTime, this, String.format("waiting at %s for %s", planet.getName(), TimeUnit.toString(portRestingTime)));
            eventManager.add(currentTime, getVolume(), SimEventType.resting, getCredits(), String.format("resting %s on %s because I cannot sell.", TimeUnit.toString(portRestingTime), planet.getName()));
            return;
        }
        Transaction.trade(currentTime, portGood.type, portGood.price, transactionAmount, this, planet, navigator.sourcePlanet, true);
        eventManager.add(EventLevel.trace, currentTime, this, String.format("sells %d %s(%5.2f) for %5.2f from %s.", transactionAmount, portGood.type.getName(), portGood.price, portGood.price * transactionAmount, getName()));
        planet.eventManager.add(currentTime, planet.getGoodList().getByType(portGood.type).getAmount(), SimEventType.buy, planet.getCredits(), String.format("%d %s(%5.2f) for %5.2f from %s.", transactionAmount, portGood.type.getName(), portGood.price, portGood.price * transactionAmount, getName()));
        eventManager.add(currentTime, getVolume(), SimEventType.sell, getCredits(), String.format("%d %s(%5.2f) for %5.2f to %s.", transactionAmount, portGood.type.getName(), portGood.price, portGood.price * transactionAmount, planet.getName()));
        // planet.buy( currentTime, portGood.type, portGood.price, transactionAmount,
        // this, sourcePlanet );
        currentTransportedAmount += transactionAmount;
        portRestingTime = randomGenerator.nextInt(currentTime, this, TRADER_MAX_PORT_REST_TIME) * TimeUnit.TICKS_PER_DAY;
        setTraderStatus(TraderStatus.TRADER_STATUS_RESTING);
        eventManager.add(EventLevel.trace, currentTime, this, String.format("waiting at %s for %s", planet.getName(), TimeUnit.toString(portRestingTime)));
        eventManager.add(currentTime, getVolume(), SimEventType.resting, getCredits(), String.format("resting %s on %s after selling.", TimeUnit.toString(portRestingTime), planet.getName()));
    }

    public void setTraderStatus(TraderStatus traderStatus) {
        eventManager.add(EventLevel.trace, currentTime, this, String.format("change status to [%s-%s]", traderStatus.getDisplayName(), traderSubStatus.getDisplayName()));
        this.traderStatus = traderStatus;
    }

    public void setTraderSubStatus(TraderSubStatus traderSubStatus) {
        eventManager.add(EventLevel.trace, currentTime, this, String.format("change status to [%s-%s]", traderStatus.getDisplayName(), traderSubStatus.getDisplayName()));
        this.traderSubStatus = traderSubStatus;
    }

    public String toString() {
        return getName();
    }

    private void tradeInPort(long currentTime, MercatorRandomGenerator randomGenerator) {
        navigator.destinationPlanetDistanceProgress = 0;
        navigator.destinationWaypointIndex          = 0;
        // -We reached our destination
        navigator.destinationPlanet = null;
        navigator.nextWaypoint      = null;
        if (getGoodList().queryAmount() != 0) {
            sell(currentTime, randomGenerator);
        } else {
            buy(currentTime, randomGenerator);
        }
    }

    public void unselect() throws OpenAlException {
        selected = false;
        navigator.unselect();
        communicationPartner.unselect();
    }
}
