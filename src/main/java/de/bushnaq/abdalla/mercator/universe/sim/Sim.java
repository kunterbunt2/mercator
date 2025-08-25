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

package de.bushnaq.abdalla.mercator.universe.sim;

import de.bushnaq.abdalla.engine.event.EventLevel;
import de.bushnaq.abdalla.mercator.renderer.Renderable;
import de.bushnaq.abdalla.mercator.universe.event.SimEventManager;
import de.bushnaq.abdalla.mercator.universe.event.SimEventType;
import de.bushnaq.abdalla.mercator.universe.factory.ProductionFacility;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.good.GoodList;
import de.bushnaq.abdalla.mercator.universe.good.GoodType;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.util.*;

import java.util.Collections;

public class Sim extends Renderable implements TradingPartner {
    public static final long               BUYS_GOODS_EVERY            = 10 * TimeUnit.TICKS_PER_DAY;//if we can afford it, we consume that often
    public static final long               DIES_IF_NOT_CONSUMED_WITHIN = BUYS_GOODS_EVERY * 100;//minimum consumption
    // private static final int CAN_REPRODUCE_UP_TO = 10;
    // private static final int CAN_SURVIVE_UP_TO = 10;
    // private static final float CREDITS_NEEDED_TO_REPRODUCE = 500f;
    // static final long NEEDS_GOODS_EVERY = 100 * TimeUnit.TICKS_PER_DAY;
    public static final float              SIM_START_CREDITS           = 1000f;
    public              float              cost                        = 1.0f;
    private             float              credits                     = 0;
    public              float              creditsToSave               = SIM_START_CREDITS;
    private             int                currentConsumedAmount;
    public              SimEventManager    eventManager;
    private             GoodList           goodList                    = new GoodList();
    private             HistoryManager     historyManager;
    public              long               lastTimeAdvancement         = 0;
    public              long               lastTransaction             = 0;
    public              int                lastYearConsumedAmount;
    private             String             name                        = null;
    public              Planet             planet                      = null;//last planet we reached on our trip as a trader
    public              ProductionFacility productionFacility          = null;
    public              SimProfession      profession                  = SimProfession.UNIMPLOYED;
    public              SimNeedList        simNeedsList                = new SimNeedList();
    public              SimStatus          status                      = SimStatus.LIVING;
    private             long               timeDelta                   = 0;

    public Sim(final Planet planet, final String name, final float credits) {
        this.planet = planet;
        this.setName(name);
        this.setCredits(credits);
        getGoodList().createEmptyGoodList();
        setHistoryManager(new HistoryManager());
        simNeedsList.createGoodList();
        eventManager = new SimEventManager(this, EventLevel.all, null);
        set2DRenderer(new Sim2DRenderer(this));
        set3DRenderer(new Sim3DRenderer(this));
    }

    public boolean advanveInTime(final long currentTime, final MercatorRandomGenerator randomGenerator, final SimList simList) {
        if (TimeUnit.isInt(currentTime)/* ( currentTime - (int)currentTime ) == 0.0f */) {
            timeDelta           = currentTime - lastTimeAdvancement;
            lastTimeAdvancement = currentTime;
            // are we at the start of a new year?
            if (currentTime % (TimeUnit.TICKS_PER_DAY * TimeUnit.DAYS_PER_YEAR) == 0) {
                lastYearConsumedAmount = currentConsumedAmount;
                currentConsumedAmount  = 0;
            }
            final SimNeedList sortedSimNeedsList = sortGoodList();
            Collections.sort(sortedSimNeedsList);
            final SimNeed needs = picGoodToBuy(currentTime);
            if (needs != null) {
                final Good good = planet.getGoodList().getByType(needs.type);
                Transaction.trade(currentTime, good.type, good.price, 1, planet, this, planet, false);
                // buy( currentTime, food.type, food.price, 1, planet, planet );
                // needs.amount = (int)NEEDS_GOODS_EVERY;
                if (eventManager.isEnabled()) eventManager.add(currentTime, getVolume(), SimEventType.buy, credits, String.format("%d %s for %5.2f from %s.", 1, good.type.getName(), good.price * 1, planet.getName()));
                currentConsumedAmount += 1;
                needs.consume(currentTime);
                goodList.consume(good.type, 1);
                if (eventManager.isEnabled()) eventManager.add(currentTime, getVolume(), SimEventType.consue, credits, String.format("%d %s.", 1, good.type.getName()));
                status = SimStatus.LIVING;
            }
            if (calculateStatus(currentTime)) {
                if (eventManager.isEnabled()) eventManager.add(currentTime, getVolume(), SimEventType.die, credits, "because no food.");
                //				planet.universe.eventManager.add(EventLevel.error, currentTime, this, String.format("%s: died because no '%s' available at '%s'.", getName(), good.type.getName(), planet.getName()));
                return true;
            }

            //			for (SimNeed needs : sortedSimNeedsList) {
            //				if (currentTime - needs.lastConsumed > needs.consumeEvery) {
            //					Good good = planet.getGoodList().getByType(needs.type);
            //					good.indicateBuyInterest(currentTime);
            //					if (getCredits() >= good.price) {
            //						if (getCredits() > needs.creditLimit) {
            //							if (good.getAmount() > 0) {
            //								// Transaction.pay( planet, this, food.price );
            //								Transaction.trade(currentTime, good.type, good.price, 1, planet, this, planet, false);
            //								// buy( currentTime, food.type, food.price, 1, planet, planet );
            //								// needs.amount = (int)NEEDS_GOODS_EVERY;
            //								eventManager.add(currentTime, getVolume(), SimEventType.buy, credits, String.format("%d %s for %5.2f from %s.", 1, good.type.getName(), good.price * 1, planet.getName()));
            //								currentConsumedAmount += 1;
            //								needs.consume(currentTime);
            //								goodList.consume(good.type, 1);
            //								eventManager.add(currentTime, getVolume(), SimEventType.consue, credits, String.format("%d %s.", 1, good.type.getName()));
            //								status = SimStatus.LIVING;
            //							} else {
            //								// ---No food available on planet
            //								planet.universe.eventManager.add(EventLevel.warning, currentTime, this, String.format("%s: no '%s' available at '%s'.", getName(), good.type.getName(), planet.getName()));
            //								// Tools.speak( "no food" );
            //								if (calculateStatus(currentTime, needs, SimStatus.DEAD_REASON_NO_FOOD)) {
            //									eventManager.add(currentTime, getVolume(), SimEventType.die, credits, String.format("because no food."));
            //									planet.universe.eventManager.add(EventLevel.error, currentTime, this, String.format("%s: died because no '%s' available at '%s'.", getName(), good.type.getName(), planet.getName()));
            //									return true;
            //								}
            //							}
            //						} else {
            //							// ---We are not rich enough for this luxury
            //						}
            //					} else {
            //						if (good.getAmount() > 0) {
            //							// ---Sim cannot afford this good
            //							//							Tools.print(String.format("%s: cannot afford any food on planet %s.\n", getName(), planet.getName()));
            //							planet.universe.eventManager.add(EventLevel.warning, currentTime, this, String.format("%s: cannot afford any '%s' at %s.", getName(), good.type.getName(), planet.getName()));
            //							if (calculateStatus(currentTime, needs, SimStatus.DEAD_REASON_NO_MONEY)) {
            //								eventManager.add(currentTime, getVolume(), SimEventType.die, credits, String.format("because no money to buy %s(%5.2f).", good.type.getName(), good.price));
            //								planet.universe.eventManager.add(EventLevel.error, currentTime, this, String.format("%s: died because no money to buy '%s'(%5.2f) at '%s'.", getName(), good.type.getName(), good.price, planet.getName()));
            //								return true;
            //							}
            //						} else {
            //							// ---No good available on planet
            //							//							Tools.print(String.format("%s: no '%s' available at '%s'.\n", getName(), good.type.getName(), planet.getName()));
            //							planet.universe.eventManager.add(EventLevel.warning, currentTime, this, String.format("%s: no '%s' available at '%s'.", getName(), good.type.getName(), planet.getName()));
            //							if (calculateStatus(currentTime, needs, SimStatus.DEAD_REASON_NO_FOOD)) {
            //								eventManager.add(currentTime, getVolume(), SimEventType.die, credits, String.format("because no food."));
            //								planet.universe.eventManager.add(EventLevel.error, currentTime, this, String.format("%s: died because no '%s' available at '%s'.", getName(), good.type.getName(), planet.getName()));
            //								return true;
            //							}
            //						}
            //					}
            //				}
            //			}
            // }
            // ---Reproduce
            // if ( queryReproductionReady() )
            // {
            // reproduce( simList );
            // }
        }
        return false;
    }

    private boolean calculateStatus(final long currentTime) {
        boolean starving  = true;
        boolean dying     = true;
        boolean breakLoop = false;
        status = SimStatus.LIVING;

        //if we are missing all of the goods for consumeEvery, then we are starving
        for (final SimNeed needs : simNeedsList) {
            if (currentTime - needs.lastConsumed < needs.consumeEvery) {
                starving = false;
            }
            if (currentTime - needs.lastConsumed < needs.dieIfNotConsumedWithin) {
                dying = false;
            }
        }
        if (dying) {
            status    = SimStatus.DEAD_REASON_NO_FOOD;
            breakLoop = true;
        } else if (starving) status = SimStatus.STARVING_NO_GOODS;
        else status = SimStatus.LIVING;

        return breakLoop;
    }
    // private boolean queryDyingReady()
    // {
    // return ( Universe.randomGenerator.nextInt( CAN_SURVIVE_UP_TO ) == 1 );
    // }

    @Override
    public void ern(final long currentTime, final float credits) {
        this.credits += credits;
        if (eventManager.isEnabled()) eventManager.add(currentTime, getVolume(), SimEventType.ern, getCredits(), String.format("%5.2f credits.", credits));
    }

    // @Override
    // public void buy( long currentTime, GoodType goodType, float price, float
    // transactionAmount, Transaction from, Transaction producer )
    // {
    // Good ownGood = goodList.getByType( goodType );
    // ownGood.buy( transactionAmount );
    // ownGood.price = price;//---remember the price we bought this good
    // setCredits( getCredits() - transactionAmount * price );
    // planet.universe.eventManager.add( currentTime, this, String.format( "buys %s
    // from %s produced at %s", goodType.getName(), from.getName(),
    // producer.getName() ) );
    // from.sell( currentTime, goodType, price, transactionAmount, this );
    // }
    // private void die( SimList simList )
    // {
    // planet.setCredits( planet.getCredits() + getCredits() );
    // setCredits( 0 );
    // simList.kill( this );
    // }
    @Override
    public float getCredits() {
        return credits;
    }

    @Override
    public GoodList getGoodList() {
        return goodList;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Planet getPlanet() {
        return planet;
    }

    // @Override
    // public void sell( long currentTime, GoodType goodType, float price, float
    // transactionAmount, Transaction to )
    // {
    // Good ownGood = goodList.getByType( goodType );
    // ownGood.sell( transactionAmount );
    // setCredits( getCredits() + transactionAmount * price );
    // planet.universe.eventManager.add( currentTime, this, String.format( "%s sells
    // %s to %s", getName(), goodType.getName(), to.getName() ) );
    // }
    public float getSatisfactionFactor(final long currentTime) {
        float satisfaction = 0;
        for (final SimNeed needs : simNeedsList) {
            if (currentTime - needs.lastConsumed < needs.consumeEvery) satisfaction += 100 / simNeedsList.size();
        }
        return satisfaction;
    }

    // public String getProfessionName()
    // {
    // switch ( profession )
    // {
    // case ENGINEERING:
    // return "Engineering";
    // case SERVICE:
    // return "Service";
    // case SCIENCE:
    // return "Science";
    // case UNIMPLOYED:
    // return "Unemployed";
    // default:
    // return "Unknown";
    // }
    // }
    //	public ObjectRenderer getRenderer() {
    //		return renderer;
    //	}

    protected int getVolume() {
        int volume = 0;
        for (final Good good : goodList) {
            volume += good.getAmount();
        }
        return volume;
    }

    /**
     * A sim pics a good to buy according to following factors
     * Good availability, good price, sim craving for the good, amount of money the sim has
     *
     * @return the good to buy, null if he is not willing to buy anything (no good available, no need, too expensive)
     */
    private SimNeed picGoodToBuy(final long currentTime) {
        //every good gets a
        float   maxGoodPoints = 0;
        SimNeed selected      = null;
        for (final SimNeed needs : simNeedsList) {
            final Good good = planet.getGoodList().getByType(needs.type);
            if (good.getAmount() > 0) {
                //available
                if (getCredits() >= good.price) {
                    //we could afford it
                    final float craving    = (currentTime - needs.lastConsumed) / needs.consumeEvery;//0 - time*
                    final float cheapness  = good.getAveragePrice() / good.price;//5 - 5/250
                    final float goodPoints = craving * cheapness;
                    if (goodPoints > maxGoodPoints) {
                        selected      = needs;
                        maxGoodPoints = goodPoints;
                    }
                } else {
                    //cannot afford
                }
            } else {
                //not available
            }
        }
        return selected;
    }

    protected float queryAverageFoodConsumption(final float planetTime) {
        return planetTime / simNeedsList.getByType(GoodType.FOOD).dieIfNotConsumedWithin;
    }

    // private boolean queryReproductionReady()
    // {
    // return ( credits > CREDITS_NEEDED_TO_REPRODUCE ) && (
    // Universe.randomGenerator.nextInt( CAN_REPRODUCE_UP_TO ) == 1 );
    // }
    // private void reproduce( SimList simList )
    // {
    // simList.create( planet, getCredits() / 2, 1 );
    // setCredits( getCredits() - getCredits() / 2 );
    // }
    @Override
    public void setCredits(final float credits) {
        this.credits = credits;
    }

    public void setGoodList(final GoodList goodList) {
        this.goodList = goodList;
    }

    public void setHistoryManager(final HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void setLastTransaction(final long currentTime) {
        lastTransaction = currentTime;
    }

    public void setName(final String name) {
        this.name = name;
    }

    private SimNeedList sortGoodList() {
        final SimNeedList sortedSimNeedsList = simNeedsList.clone();
        return sortedSimNeedsList;
    }
}
