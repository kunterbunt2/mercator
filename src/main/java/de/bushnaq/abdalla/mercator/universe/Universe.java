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

package de.bushnaq.abdalla.mercator.universe;

import com.badlogic.gdx.graphics.Color;
import de.bushnaq.abdalla.mercator.desktop.GraphicsDimentions;
import de.bushnaq.abdalla.mercator.renderer.GameEngine;
import de.bushnaq.abdalla.mercator.renderer.ScreenListener;
import de.bushnaq.abdalla.mercator.renderer.reports.GraphChartData;
import de.bushnaq.abdalla.mercator.renderer.reports.PieChartData;
import de.bushnaq.abdalla.mercator.universe.event.Event;
import de.bushnaq.abdalla.mercator.universe.event.EventLevel;
import de.bushnaq.abdalla.mercator.universe.event.EventManager;
import de.bushnaq.abdalla.mercator.universe.factory.ProductionFacility;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.land.LandList;
import de.bushnaq.abdalla.mercator.universe.path.PathList;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.planet.PlanetList;
import de.bushnaq.abdalla.mercator.universe.ring.Ring;
import de.bushnaq.abdalla.mercator.universe.sector.SectorList;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.universe.sim.SimList;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;
import de.bushnaq.abdalla.mercator.universe.sim.trader.TraderList;
import de.bushnaq.abdalla.mercator.universe.tools.Tools;
import de.bushnaq.abdalla.mercator.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bushnaq Created 13.02.2005
 */
public class Universe {
    public static final  float                   WORLD_SCALE                       = 1.0f;
    private static final String                  ADVANCE_IN_TIME_PLANET_DURATION   = "planet   AIT";
    private static final String                  ADVANCE_IN_TIME_TRADER_DURATION   = "trader   AIT";
    private static final String                  ADVANCE_IN_TIME_UNIVERSE_DURATION = "All      AIT";
    private static final long                    SIMULATION_DELTA                  = 20L;//ms
    private final        GraphicsDimentions      graphicsDimentions;
    private final        Logger                  logger                            = LoggerFactory.getLogger(this.getClass());
    private final        TimeStatisticManager    timeStatisticManager              = new TimeStatisticManager();
    //	private static final String APPLICATION_VERSION_STRING = "0.1.0.0";
    public               PieChartData            amountPieChart                    = new PieChartData("volumen");
    public               PieChartData            creditPieChart                    = new PieChartData("credits");
    public               long                    currentTime                       = 0L;//simulation time in milliseconds
    public               PlanetList              deadPlanetList                    = new PlanetList();
    public               SimList                 deadSimList                       = new SimList(null);
    public               GraphChartData          deadSimStatistics                 = new GraphChartData("dead sims", Color.RED);
    public               TraderList              deadTraderList                    = new TraderList();
    public               GraphChartData          deadTraderStatistics              = new GraphChartData("dead traders", Color.RED);
    public               EventManager            eventManager;
    public               LandList                landList                          = new LandList();
    public               PathList                pathList                          = new PathList();
    public               PieChartData            planetDeadReasonPieChart          = new PieChartData("planet death");
    public               PlanetList              planetList                        = new PlanetList();
    public               Ring                    ring;
    public               PieChartData            satisfactionPieChart              = new PieChartData("satisfaction");
    public               SectorList              sectorList                        = new SectorList();
    public               Object                  selected                          = null;
    public               Good                    selectedGood                      = null;
    public               Planet                  selectedPlanet                    = null;
    public               ProductionFacility      selectedProductionFacility        = null;
    public               Sim                     selectedSim;
    public               Trader                  selectedTrader                    = null;
    public               PieChartData            simDeadReasonPieChart             = new PieChartData("sim death");
    public               SimList                 simList                           = new SimList(null);
    public               int                     size;
    public               PieChartData            statisticsPieChart                = new PieChartData("statistics");
    public               long                    timeDelta                         = 0L;
    public               float                   traderCreditBuffer                = 0;
    public               PieChartData            traderDeadReasonPieChart          = new PieChartData("trader death");
    public               TraderList              traderList                        = new TraderList();
    public               MercatorRandomGenerator universeRG;
    //    Voice   helloVoice;
    boolean useFixedDelta = false;
    //    VoiceManager voiceManager;
    private ScreenListener ScreenListener;
    private float          credits; // ---credits at creation time, should never change
    private boolean        enableTime  = true;
    private HistoryManager historyManager;
    private long           lastTime    = 0;
    private String         name;
    private Event          selectedEvent;
    private int            selectedGoodIndex;
    private long           universeAge = 100L * TimeUnit.TICKS_PER_DAY;


    public Universe(final String name, final GraphicsDimentions graphicsDimentions, final EventLevel level, final Class<?> eventFilter) {
        setName(name);
        this.graphicsDimentions = graphicsDimentions;
        eventManager            = new EventManager(level, eventFilter);
        setHistoryManager(new HistoryManager());

    }


    public void advanceInTime() throws Exception {
        advanceInTime(enableTime);
        // {
        // float anualExportAmountTotal = 0;
        // float anualImportAmountTotal = 0;
        // float anualLocalCreditsEarnedTotal = 0;
        // float anualLocalCreditsSpentTotal = 0;
        // boolean oneSimIsNotStarving = false;
        // for ( Planet planet : planetList )
        // {
        // anualExportAmountTotal +=
        // planet.historyManager.getAnualExportAmountOfGoods();
        // anualImportAmountTotal +=
        // planet.historyManager.getAnualImportAmountOfGoods();
        // anualLocalCreditsEarnedTotal +=
        // planet.historyManager.getAnualLocalCreditsEarned();
        // anualLocalCreditsSpentTotal +=
        // planet.historyManager.getAnualLocalCreditsSpent();
        // for ( Sim sim : planet.simList )
        // {
        // if ( sim.status != SimStatus.STARVING )
        // oneSimIsNotStarving = true;
        // }
        // }
        // System.out.printf( "%.2f %.0f %.0f\n", currentTime, anualImportAmountTotal,
        // anualExportAmountTotal );
        // }
    }

    public void advanceInTime(final boolean enable) throws Exception {
        if (enable) {
            if (useFixedDelta)
                timeDelta = SIMULATION_DELTA;
            else
                timeDelta += System.currentTimeMillis() - lastTime;
            lastTime = System.currentTimeMillis();
            if (timeDelta > 1000)
                timeDelta -= 1000;//reset every second
            if (timeDelta >= SIMULATION_DELTA)//we update the simulation only every 20ms
            //			float[] before = queryDetailedCredits(false);
            {
                timeDelta -= SIMULATION_DELTA;
                currentTime += SIMULATION_DELTA;
                timeStatisticManager.start(ADVANCE_IN_TIME_UNIVERSE_DURATION);
                {
                    timeStatisticManager.start(ADVANCE_IN_TIME_PLANET_DURATION);
                    for (final Planet planet : planetList) {
                        planet.advanceInTime(currentTime, universeRG);
                    }
                    timeStatisticManager.stop(ADVANCE_IN_TIME_PLANET_DURATION);
                }
                {
                    timeStatisticManager.start(ADVANCE_IN_TIME_TRADER_DURATION);
                    int i = 0;
                    while (i < traderList.size()) {
                        final Trader trader = traderList.get(i);
                        // for ( Trader trader : traderList )
                        // {
                        if (trader.advanceInTime(currentTime, universeRG, planetList, selectedTrader == trader)) {
                            if (trader.getName().equals("T-179")) {
                                final int a = 23;
                            }
                            trader.planet.setCredits(trader.planet.getCredits() + trader.getCredits());
                            trader.setCredits(0);
                            traderList.kill(trader);
                            Tools.print(String.format("%s.%s is dead.\n", trader.planet.getName(), trader.getName()));
                            deadTraderList.add(trader);
                            deadTraderStatistics.add(currentTime);
                            // trader.eventManager.print();
                            // System.exit(1);
                        } else {
                            i++;
                        }
                    }
                    timeStatisticManager.stop(ADVANCE_IN_TIME_TRADER_DURATION);
                }
                getHistoryManager().get(currentTime);
                calculateSectorValue();
                deadSimStatistics.add(currentTime, 0);
                deadTraderStatistics.add(currentTime, 0);
                // are we at the start of a new year?
                boolean newYear = currentTime % (TimeUnit.TICKS_PER_DAY * TimeUnit.DAYS_PER_YEAR) == 0;
                if (newYear) {
                    queryStatistics();
                    queryAmounts();
                    querySatisfaction(currentTime);
                    queryDeathReason();
                    newYear = true;
                }
                final float newCredits = queryCredits(newYear);
                if (Math.abs(credits - newCredits) > 1000.0f) {
                    // TODO why are we loosing so much money?
                    Tools.error("Universe %s at %s credits has changed, expecting %f but is %f", getName(), TimeUnit.toString(currentTime), credits, newCredits);
                    final float[] after = queryDetailedCredits(false);
                    throw new Exception("credit discrepancy found");
                }
                timeStatisticManager.stop(ADVANCE_IN_TIME_UNIVERSE_DURATION);
            }
        }
    }

    public void advanceInTime(final long age) throws Exception {
        eventManager.eventList.clear();
        {
            final long time  = System.currentTimeMillis();
            final long start = currentTime;
            while (currentTime < start + age) {
                advanceInTime();
            }
            if (age != 0) {
                System.out.printf("aged universe %s at %s for %s years in %dms.\n", getName(), TimeUnit.toString(start), TimeUnit.toString(age), System.currentTimeMillis() - time);
            }
        }
    }

    private void calculateSectorValue() {
        //		for (final Sector sector : sectorList) {
        //			sector.credits = 0;
        //		}
        //		for (final Planet planet : planetList) {
        //			planet.sector.credits += planet.getCredits();
        //		}
    }

    public void create(GameEngine gameEngine, final int randomGeneratorSeed, final int aUniverseSize, final long age) throws Exception {
        universeAge = age;
        {
            size = aUniverseSize;
            // EventLogManager.Clear();
            // eventLogManager.add( "UNN", Level.FINE, "<<<<<<< Starting Merkator v" +
            // APPLICATION_VERSION_STRING + " >>>>>>>" );
            selectedGoodIndex = 0;
            selectedPlanet    = null;
            selectedTrader    = null;
            universeRG        = new MercatorRandomGenerator(randomGeneratorSeed, eventManager);
            final UniverseGenerator generator = new UniverseGenerator();
            generator.generate(gameEngine, this);
            credits = queryCredits(false);
        }
        // ---Reset all the lastTimeAdvancement values
        // advanceInTime( true );
        // currentTime = 0;
        useFixedDelta = true;
        advanceInTime(age);
    }

    public void dispose() {
    }


    public Planet findBusyCenterPlanet() {
        Planet planet      = null;
        float  minDistance = Float.MAX_VALUE;
        for (final Planet p : planetList) {
            if (p.pathList.size() > 3) {
                final float distance = p.x * p.x + p.z * p.z;
                if (distance < minDistance) {
                    minDistance = distance;
                    planet      = p;
                }
            }
        }
        return planet;
    }


    public GraphicsDimentions getGraphicsDimentions() {
        return graphicsDimentions;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public String getName() {
        return name;
    }

    public boolean isEnableTime() {
        return enableTime;
    }

    public void kill(final Sim sim) {
        simList.remove(sim);
        deadSimList.add(sim);
    }

    private void queryAmounts() {
        float         total = 0;
        final float[] delta = new float[3];
        for (final Planet planet : planetList) {
            for (final ProductionFacility productionFacility : planet.productionFacilityList) {
                delta[0] += productionFacility.lastYearProducedAmount;
            }
        }
        for (final Trader trader : traderList) {
            delta[1] += trader.lastYearTransportedAmount;
        }
        for (final Sim sim : simList) {
            delta[2] += sim.lastYearConsumedAmount;
        }
        for (int i = 0; i < delta.length; i++) {
            total += delta[i];
        }
        if (total != 0) {
            amountPieChart.pices.clear();
            amountPieChart.add("produced", VolumeUnit.toString(delta[0]), delta[0] * 100 / total, Color.GREEN);
            amountPieChart.add("transported", VolumeUnit.toString(delta[1]), delta[1] * 100 / total, Color.BLUE);
            amountPieChart.add("consumed", VolumeUnit.toString(delta[2]), delta[2] * 100 / total, Color.RED);
        }
    }

    public float queryCredits(final boolean updatePieChart) {
        float         total = 0;
        final float[] delta = new float[3];
        for (final Planet planet : planetList) {
            delta[0] += planet.getCredits();
        }
        for (final Sim trader : traderList) {
            delta[1] += trader.getCredits();
        }
        for (final Sim sim : simList) {
            delta[2] += sim.getCredits();
        }
        for (int i = 0; i < delta.length; i++) {
            total += delta[i];
        }
        if (total != 0 && updatePieChart) {
            creditPieChart.pices.clear();
            creditPieChart.add("planets", CreditUnit.toString(delta[0]), delta[0] * 100 / this.credits, Color.BLUE);
            creditPieChart.add("traders", CreditUnit.toString(delta[1]), delta[1] * 100 / this.credits, Color.RED);
            creditPieChart.add("sims", CreditUnit.toString(delta[2]), delta[2] * 100 / this.credits, Color.GREEN);
        }
        return total;
    }

    private void queryDeathReason() {
        {
            float         amount = 0;
            final float[] delta  = new float[2];
            for (final Sim trader : deadTraderList) {
                switch (trader.status) {
                    case DEAD_REASON_NO_FOOD:
                        delta[0]++;
                        break;
                    case DEAD_REASON_NO_MONEY:
                        delta[1]++;
                        break;
                }
            }
            for (int i = 0; i < delta.length; i++) {
                amount += delta[i];
            }
            if (amount != 0) {
                traderDeadReasonPieChart.pices.clear();
                traderDeadReasonPieChart.add("no food", String.format("%.0f", delta[0]), delta[0] * 100 / amount, Color.RED);
                traderDeadReasonPieChart.add("no money", String.format("%.0f", delta[1]), delta[1] * 100 / amount, Color.FIREBRICK);
            }
        }
        {
            float         amount = 0;
            final float[] delta  = new float[2];
            for (final Sim sim : deadSimList) {
                switch (sim.status) {
                    case DEAD_REASON_NO_FOOD:
                        delta[0]++;
                        break;
                    case DEAD_REASON_NO_MONEY:
                        delta[1]++;
                        break;
                }
            }
            for (int i = 0; i < delta.length; i++) {
                amount += delta[i];
            }
            if (amount != 0) {
                simDeadReasonPieChart.pices.clear();
                simDeadReasonPieChart.add("no food", String.format("%.0f", delta[0]), delta[0] * 100 / amount, Color.RED);
                simDeadReasonPieChart.add("no money", String.format("%.0f", delta[1]), delta[1] * 100 / amount, Color.FIREBRICK);
            }
        }
        {
            float         amount = 0;
            final float[] delta  = new float[3];
            for (final Planet planet : planetList) {
                switch (planet.status) {
                    case DEAD_REASON_NO_FOOD:
                        delta[0]++;
                        break;
                    case DEAD_REASON_NO_MONEY:
                        delta[1]++;
                        break;
                    case DEAD_REASON_NO_SIMS:
                        delta[2]++;
                        break;
                }
            }
            for (int i = 0; i < delta.length; i++) {
                amount += delta[i];
            }
            if (amount != 0) {
                planetDeadReasonPieChart.pices.clear();
                planetDeadReasonPieChart.add("no food", String.format("%.0f", delta[0]), delta[0] * 100 / amount, Color.RED);
                planetDeadReasonPieChart.add("no money", String.format("%.0f", delta[1]), delta[1] * 100 / amount, Color.FIREBRICK);
                planetDeadReasonPieChart.add("no sim", String.format("%.0f", delta[2]), delta[2] * 100 / amount, Color.PINK);
            }
        }
    }

    private float[] queryDetailedCredits(final boolean print) {
        final float[] delta = new float[3];
        for (final Planet planet : planetList) {
            if (print)
                System.out.printf("planet %s %f\n", planet.getName(), planet.getCredits());
            delta[0] += planet.getCredits();
        }
        for (final Sim trader : traderList) {
            if (print)
                System.out.printf("trader %s %f\n", trader.getName(), trader.getCredits());
            delta[1] += trader.getCredits();
        }
        for (final Sim sim : simList) {
            if (print)
                System.out.printf("sim %s %f\n", sim.getName(), sim.getCredits());
            delta[2] += sim.getCredits();
        }
        return delta;
    }

    private void querySatisfaction(final long currentTime) {
        float         amount = 0;
        final float[] delta  = new float[2];
        {
            for (final Sim trader : traderList) {
                delta[0] += trader.getSatisfactionFactor(currentTime);
            }
        }
        {
            for (final Sim sim : simList) {
                delta[1] += sim.getSatisfactionFactor(currentTime);
            }
        }
        for (int i = 0; i < delta.length; i++) {
            amount += delta[i];
        }
        if (amount != 0) {
            satisfactionPieChart.pices.clear();
            satisfactionPieChart.add("traders", SatisfactionUnit.toString(delta[0]), delta[0] * 100 / amount, Color.GREEN);
            satisfactionPieChart.add("sims", SatisfactionUnit.toString(delta[1]), delta[1] * 100 / amount, Color.BLUE);
        }
    }

    private void queryStatistics() {
        float         total = 0;
        final float[] delta = new float[3];
        delta[0] = planetList.size();
        delta[1] = simList.size();
        delta[2] = traderList.size();
        for (int i = 0; i < delta.length; i++) {
            total += delta[i];
        }
        if (total != 0) {
            statisticsPieChart.pices.clear();
            statisticsPieChart.add("planets", NumberUnit.toString(delta[0]), delta[0] * 100 / total, Color.GREEN);
            statisticsPieChart.add("sims", NumberUnit.toString(delta[1]), delta[1] * 100 / total, Color.BLUE);
            statisticsPieChart.add("traders", NumberUnit.toString(delta[2]), delta[2] * 100 / total, Color.RED);
        }
    }


    public void selectEvent(final Event event) {
        selectedEvent = event;
    }

    //	public void selectTrader(final Trader aTrader) {
    //		setSelected(aTrader);
    //		planetList.markTraderPath(selectedTrader);
    //	}

    public void setEnableTime(final boolean enableTime) {
        this.enableTime = enableTime;
    }

    private void setHistoryManager(final HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private void setName(final String name) {
        this.name = name;
    }

    public void setScreenListener(final ScreenListener ScreenListener) {
        this.ScreenListener = ScreenListener;
    }

    public void setSelected(final Object selected, final boolean setDirty) throws Exception {
        if (this.selected instanceof Trader) {
            selectedTrader = (Trader) this.selected;
            selectedTrader.unselect();
        }
        this.selected              = selected;
        selectedPlanet             = null;
        selectedTrader             = null;
        selectedSim                = null;
        selectedProductionFacility = null;
        selectedGood               = null;
        if (selected != null) {
            if (selected instanceof Planet) {
                selectedPlanet = (Planet) selected;
                if (ScreenListener != null)
                    ScreenListener.setCamera(selectedPlanet.x, selectedPlanet.z, setDirty);
            } else if (selected instanceof Trader) {
                selectedTrader = (Trader) selected;
                if (ScreenListener != null)
                    ScreenListener.setCamera(selectedTrader.x, selectedTrader.z, setDirty);
                selectedTrader.select();
            } else if (selected instanceof Sim) {
                selectedSim = (Sim) selected;
                if (ScreenListener != null)
                    ScreenListener.setCamera(selectedSim.planet.x, selectedSim.planet.z, setDirty);
            } else if (selected instanceof ProductionFacility) {
                selectedProductionFacility = (ProductionFacility) selected;
                if (ScreenListener != null)
                    ScreenListener.setCamera(selectedProductionFacility.planet.x, selectedProductionFacility.planet.z, setDirty);
            } else if (selected instanceof Good) {
                selectedGood = (Good) selected;
            }
        } else {
        }
    }

//    private void windowstts() {
//        SpeechEngine speechEngine = null;
//        try {
//            speechEngine = SpeechEngineNative.getInstance();
//            List<Voice> voices = speechEngine.getAvailableVoices();
//
//            //            System.out.println("For now the following voices are supported:\n");
//            String text = "The answer to the ultimate question of life, the universe, and everything is 42";
//            for (Voice voice : voices) {
//                System.out.printf("%s%n", voice);
//            }
//            // We want to find a voice according our preferences
//            VoicePreferences voicePreferences = new VoicePreferences();
//            voicePreferences.setLanguage("en"); //  ISO-639-1
//            voicePreferences.setCountry("GB"); // ISO 3166-1 Alpha-2 code
//            voicePreferences.setGender(VoicePreferences.Gender.FEMALE);
//            Voice voice = speechEngine.findVoiceByPreferences(voicePreferences);
//
//            // simple fallback just in case our preferences didn't match any voice
//            if (voice == null) {
//                System.out.printf("Warning: Voice has not been found by the voice preferences %s%n", voicePreferences);
//                voice = voices.get(0); // it is guaranteed that the speechEngine supports at least one voice
//                System.out.printf("Using \"%s\" instead.%n", voice);
//            }
//
//            speechEngine.setVoice(voice.getName());
//            speechEngine.say(text);
//        } catch (SpeechEngineCreationException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
