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

package de.bushnaq.abdalla.mercator.universe.planet;

import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.factory.Factory;
import de.bushnaq.abdalla.mercator.universe.factory.ProductionFacility;
import de.bushnaq.abdalla.mercator.universe.factory.ProductionFacilityList;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.good.GoodList;
import de.bushnaq.abdalla.mercator.universe.good.GoodType;
import de.bushnaq.abdalla.mercator.universe.path.PathSeeker;
import de.bushnaq.abdalla.mercator.universe.path.Waypoint;
import de.bushnaq.abdalla.mercator.universe.path.WaypointList;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.universe.sim.SimList;
import de.bushnaq.abdalla.mercator.universe.sim.trader.TraderList;
import de.bushnaq.abdalla.mercator.util.*;

/**
 * @author bushnaq Created 13.02.2005
 */
public class Planet extends Waypoint implements TradingPartner {
    public static final float                  CHANNEL_SIZE           = 32 / Universe.WORLD_SCALE;
    public final static float                  MIN_PLANET_DISTANCE    = 30;
    public static final int                    PLANET_DISTANCE        = 2048;
    public final static int                    PLANET_MAX_SIMS        = 10;
    public final static float                  PLANET_START_CREDITS   = 20000;
    public              SimList                deadSimList            = new SimList(this);
    public              PlanetEventManager     eventManager;
    public              long                   lastTimeAdvancement    = 0;
    public              long                   lastTransaction        = 0;
    //	private String name = null;
    public              float                  orbitAngle             = 0.0f;
    public              PathSeeker             pathSeeker             = new PathSeeker();
    public              ProductionFacilityList productionFacilityList = new ProductionFacilityList();
    public              SimList                simList                = new SimList(this);
    public              PlanetStatisticManager statisticManager       = new PlanetStatisticManager();
    public              PlanetStatus           status                 = PlanetStatus.LIVING;
    public              long                   timeDelta              = 0;
    public              TraderList             traderList             = new TraderList();
    public              Universe               universe;
    private             float                  credits                = PLANET_START_CREDITS;
    private             GoodList               goodList               = new GoodList();
    private             HistoryManager         historyManager;

    public Planet(final String name, final float x, final float y, final float z, final Universe universe) {
        super(name, x, y, z);
        //		this.setName(name);
        this.universe = universe;
        set2DRenderer(new Planet2DRenderer(this));
        set3DRenderer(new Planet3DRenderer(this));
        eventManager = new PlanetEventManager(this);
        city         = this;
    }

    public void advanceInTime(final long currentTime, final MercatorRandomGenerator randomGenerator)// OK
    {
        timeDelta = currentTime - lastTimeAdvancement;
        // timeDelta = currentTime - lastTimeAdvancement;
        lastTimeAdvancement = currentTime;
        orbitAngle -= (Math.PI * ((float) timeDelta / TimeUnit.TICKS_PER_DAY)) / 360f;
        if (TimeUnit.isInt(currentTime)/* ( currentTime - (int)currentTime ) == 0.0f */) {
            pathList.reduceUsage();
            getGoodList().calculatePrice(currentTime);
            distributeEnigneers();
            productionFacilityList.advanceInTime(currentTime);
            simList.advanveInTime(currentTime, randomGenerator, this);
            getGoodList().calculatePrice(currentTime);
            if (simList.size() == 0) {
                if (goodList.getByType(GoodType.FOOD).getAmount() == 0)
                    status = PlanetStatus.DEAD_REASON_NO_FOOD;
                else if (credits == 0)
                    status = PlanetStatus.DEAD_REASON_NO_MONEY;
                else
                    status = PlanetStatus.DEAD_REASON_NO_SIMS;
                sector = universe.sectorList.getAbandonedSector();
                universe.deadPlanetList.add(this);
                //				Tools.speak(String.format("PLanet %s is abandoned.\n", getName()));
            }
            // ---Ensure that this time is recorded in the history master
            getHistoryManager().get(currentTime);
        }
    }

    // @Override
    // public void buy( long currentTime, GoodType goodType, float price, float
    // transactionAmount, Transaction from, Transaction producer )
    // {
    // Good good = goodList.getByType( goodType );
    // good.buy( transactionAmount );
    // setCredits( getCredits() - price * transactionAmount );
    // historyManager.get( currentTime ).buy( good, price * transactionAmount,
    // transactionAmount, from );
    // transported( producer.getPlanet(), transactionAmount );
    // from.sell( currentTime, goodType, price, transactionAmount, this );
    // }
    public void create(final MercatorRandomGenerator randomGenerator) {
        setHistoryManager(new HistoryManager());
        getGoodList().createGoodList(this);
        simList.create(this, Sim.SIM_START_CREDITS, /* Universe.randomGenerator.nextInt( 10 ) + 10 */5);
        universe.simList.addAll(simList);
        // ---Mines

        for (int factoryIndex = 0; factoryIndex < 2; factoryIndex++) {
            int  outputIndex = randomGenerator.nextInt(0, this, getGoodList().size());
            Good outputGood;
            do {
                //ensure we do not create two factories of the same type
                outputIndex++;
                outputIndex %= getGoodList().size();
                outputGood = getGoodList().get(outputIndex);
            } while (productionFacilityList.getByType(outputGood.type) != null);
            // ---TODO Universe needs to get into this status by it self
            // outputGood.produce( outputGood.getAverageAmount() - 500 );
            final Factory factory = new Factory(this, null, outputGood);
            productionFacilityList.addProductionFacility(factory);
        }
        // ---Labs
        {
            // ---TODO Universe needs to get into this status by it self
            // Lab lab = new Lab( this, new Good( GoodType.G11, 15, 10, 0, null ) );
            // productionFacilityList.add( lab );
        }
        getGoodList().calculatePrice(0);
        distributeEnigneers();
        // ---and factories that only need one input good
        // {
        // //---The first half of the goods can be produced without any input, they have
        // technical level 0.
        // //---The second half of the goods need one input good, they have technical
        // level 1.
        // //---The n'th technical level 1 good needs the n'th technical level 0 good as
        // input.
        // int goodIndex = Universe.randomGenerator.nextInt( ( goodList.size() - 1 ) / 2
        // );
        // int outputIndex = goodList.size() / 2 + goodIndex + 1;
        // int inputIndex = goodIndex + 1;
        // //---Make sure the input does not exist on the same planet
        // //---This is necessary to make sure we only buy or sell a good
        //
        // GoodList inputList = new GoodList();
        // inputList.add( goodList.get( inputIndex ) );
        // Good outputGood = goodList.get( outputIndex );
        // //---TODO Universe needs to get into this status by it self
        // outputGood.produce( outputGood.getAverageAmount() - 500 );
        // Factory factory = new Factory( this, inputList, outputGood );
        // //---employ engineers to work the factory
        // int availableEngineers = Math.min( factory.queryEngineersNeeded(),
        // simList.getNumberOfUnenployed() );
        // for ( int i = 0; i < availableEngineers; i++ )
        // {
        // Sim sim = simList.getUnemployed();
        // sim.profession = SimProfession.ENGINEERING;
        // sim.factory = factory;
        // factory.addEngineer( sim );
        // }
        // factoryList.add( factory );
        // }
    }

    private void distributeEnigneers() {
        // if ( getName().equals( "P-3" ) )
        // {
        // for ( Sim sim : simList )
        // {
        // System.out.printf( "%s %s %s\n", sim.getName(), sim.profession.name(),
        // sim.productionFacility != null ? sim.productionFacility.getName() : "-" );
        // }
        // int a = 12;
        // for ( ProductionFacility productionFacility : productionFacilityList )
        // {
        // for ( int engineerIndex = 0; engineerIndex <
        // productionFacility.engineers.size(); engineerIndex++ )
        // {
        // productionFacility.unemploy( productionFacility.engineers.get( engineerIndex
        // ) );
        // }
        // }
        // }
        // ---distribute engineers
        for (int i = 0; i < simList.getNumberOfUnenployed(); i++) {
            // ---Find the best factory to put this engineer in
            ProductionFacility bestProductionFacility = null;
            float              profit                 = 0;
            for (final ProductionFacility productionFacility : productionFacilityList) {
                if (profit < productionFacility.queryAverageProfit()) {
                    profit                 = productionFacility.queryAverageProfit();
                    bestProductionFacility = productionFacility;
                }
            }
            if (bestProductionFacility != null) {
                final Sim engineer = simList.getFirstUnemployed();
                bestProductionFacility.employ(engineer);
            } else {
                // ---IF we add more engineers, we are no longer profitable
                break;
            }
        }
    }

    @Override
    public void ern(final long currentTime, final float credits) {
        this.credits += credits;
    }

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
    public Planet getPlanet() {
        return this;
    }

    // @Override
    // public void pay( long currentTime, float price, float transactionAmount,
    // TradingPartner transaction )
    // {
    // setCredits( getCredits() - price * transactionAmount );
    // transaction.setCredits( transaction.getCredits() + price * transactionAmount
    // );
    // getHistoryManager().get( currentTime ).buy( null, price * transactionAmount,
    // transactionAmount, transaction.getPlanet() );
    // }

    @Override
    public void setCredits(final float credits) {
        this.credits = credits;
    }

    @Override
    public void setLastTransaction(final long currentTime) {
        lastTransaction = currentTime;
    }

    // @Override
    // public void sell( long currentTime, GoodType goodType, float price, float
    // transactionAmount, Transaction to )
    // {
    // Good good = goodList.getByType( goodType );
    // good.sell( transactionAmount );
    // good.indicateBuyInterest( currentTime );
    // setCredits( getCredits() + price * transactionAmount );
    // historyManager.get( currentTime ).sell( good, price * transactionAmount,
    // transactionAmount, to );
    // }
    public int getSatisfactionFactor(final float currentTime) {
        int satisfaction = 0;
        for (final Sim sim : simList) {
            satisfaction += sim.getSatisfactionFactor((int) currentTime);
        }
        if (simList.size() == 0)
            return 0;
        else
            return satisfaction / simList.size();
    }

    public float queryAverageFoodPrice() {
        return goodList.getByType(GoodType.FOOD).getAveragePrice();
    }

    public float queryDistance(final WaypointList waypointList) {
        //ignore first and last waypoint, as they only mark a city, but not an actual waypoint
        float    distance = 0.0f;
        Waypoint source   = waypointList.get(0).waypoint;
        for (int i = 1; i < waypointList.size(); i++) {
            final Waypoint target = waypointList.get(i).waypoint;
            distance += source.queryDistance(target);
            source = target;
        }
        return distance;
    }

    public void remove(final Sim sim) {
        for (final ProductionFacility ProductionFacility : productionFacilityList) {
            ProductionFacility.removeEngineer(sim);
        }
        deadSimList.add(sim);
        universe.kill(sim);
    }

    //	public void setName(final String name) {
    //		this.name = name;
    //	}

    public void setGoodList(final GoodList goodList) {
        this.goodList = goodList;
    }

    public void setHistoryManager(final HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public void transported(final Planet from, final int amount) {
        // ---First lets check if we have transported things to that planet
        final int amount2 = from.statisticManager.getAmount(getName());
        if (amount2 > amount) {
            from.statisticManager.transported(this, -amount);
        } else {
            from.statisticManager.transported(this, -amount2);
            statisticManager.transported(from, amount - amount2);
        }
    }
}
