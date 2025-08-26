package de.bushnaq.abdalla.mercator.universe.sim.trader;

import de.bushnaq.abdalla.engine.event.EventLevel;
import de.bushnaq.abdalla.mercator.universe.event.SimEventType;
import de.bushnaq.abdalla.mercator.universe.path.WaypointProxy;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.util.Debug;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnTraderEvent {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Trader trader;

    public OnTraderEvent(Trader trader) {
        this.trader = trader;
    }

    public void aligned() {
        //TODO move to Navigator.reachedWaypoint?
        if (trader.navigator.reachedDestination()) {
            //case 3
            if (Debug.isFilterTrader(trader.getName()))
                System.out.printf("reached destination %s port %s\n", trader.navigator.nextWaypoint.city.getName(), trader.navigator.destinationPlanet.getName());
            //dock
            trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_REQUESTING_DOCKING);
            trader.communicationPartner.requestDocking(trader.navigator.nextWaypoint.city);
        } else if (trader.navigator.reachedTransit()) {
            //case 2
            if (Debug.isFilterTrader(trader.getName()))
                System.out.printf("reached transit %s port %s\n", trader.navigator.nextWaypoint.city.getName(), trader.navigator.destinationPlanet.getName());
            //transition
            trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_REQUESTING_TRANSITION);
            trader.communicationPartner.requestTransition(trader.navigator.nextWaypoint.city);
        } else {
            //wait
            if (Debug.isFilterTrader(trader.getName()))
                System.out.printf("reached waypoint %s port %s\n", trader.navigator.nextWaypoint.getName(), trader.navigator.destinationPlanet.getName());
            trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_WAITING_FOR_WAYPOINT);
        }
//            if (trader.navigator.nextWaypoint.city != null) {
//                if (trader.navigator.reachedDestination()) {
//                    //dock
//                    trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_REQUESTING_DOCKING);
//                    trader.communicationPartner.requestDocking(trader.navigator.nextWaypoint.city);
//                } else if (trader.navigator.reachedTransit()) {
//                    //transition
//                    trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_REQUESTING_TRANSITION);
//                    trader.communicationPartner.requestTransition(trader.navigator.nextWaypoint.city);
//                }
//            } else {
//                trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_WAITING_FOR_WAYPOINT);
//            }
    }

    public void docked() {
        trader.clearDock(trader.navigator.destinationPlanet);//finished docking
        trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_DOCKED);
        trader.eventManager.add(EventLevel.trace, trader.currentTime, trader, String.format("Docked at '%s'", trader.navigator.nextWaypoint.city.getName()));
    }

    private void reachedDestination() {
        trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_DOCKING_ACC);
        if (Debug.isFilterTrader(trader.getName()))
            trader.eventManager.add(EventLevel.trace, trader.currentTime, this, String.format("reached target port '%s' [%s-%s}", trader.navigator.nextWaypoint.name, trader.getTraderStatus().getDisplayName(), trader.getTraderSubStatus().getDisplayName()));
//                                logger.info(String.format("reached city %s %s %s", navigator.nextWaypoint.name, traderStatus.getDisplayName(), traderSubStatus.getDisplayName()));
    }

    private void reachedPort() {
        //we reached a city
        if (trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_DECELERATING) {
            if (Debug.isFilterTrader(trader.getName()))
                trader.eventManager.add(EventLevel.trace, trader.currentTime, this, String.format("reached port '%s' [%s-%s}", trader.navigator.nextWaypoint.name, trader.getTraderStatus().getDisplayName(), trader.getTraderSubStatus().getDisplayName()));
//                            logger.info(String.format("reached city %s %s %s", navigator.nextWaypoint.name, traderStatus.getDisplayName(), traderSubStatus.getDisplayName()));
            if (trader.navigator.previousWaypoint != null) trader.navigator.freePrevWaypoints();
            // ---Pay for the jump
            final float costOfJump = trader.costOfDistance(trader.navigator.destinationWaypointDistance);
            trader.setCredits(trader.getCredits() - costOfJump);
            // TODO pay the planet
            //					targetWaypoint = waypointList.get(destinationWaypointIndex).waypoint;
            trader.navigator.nextWaypoint.city.setCredits(trader.navigator.nextWaypoint.city.getCredits() + costOfJump);
            trader.eventManager.add(trader.currentTime, trader.getVolume(), SimEventType.payJump, trader.getCredits(), String.format("and payed %5.2f to %s for jump cost.", costOfJump, trader.planet.getName()));
            // ---we reached a new port
            trader.navigator.destinationWaypointDistance         = 0;
            trader.navigator.destinationWaypointDistanceProgress = 0;
            trader.planet.traderList.remove(this);
            trader.planet = (Planet) trader.navigator.nextWaypoint;
            trader.planet.traderList.add(trader);
//                        eventManager.add(EventLevel.trace, currentTime, this, String.format("arrived at '%s'", navigator.nextWaypoint.city.getName()));
            if (trader.navigator.nextWaypoint == trader.navigator.destinationPlanet) {
                reachedDestination();
            } else {
                //we reached some city on the way
                // ---this is a multi city trip. Plan the next part of the trip
                //						destinationWaypointIndex++;
                trader.navigator.previousWaypoint = trader.navigator.nextWaypoint;
                //						destinationWaypointIndex++;
//                            if (Debug.isFilterTrader(getName()))
//                                System.out.printf("nextWaypoint = %s\n", navigator.nextWaypoint.name);
                trader.navigator.destinationWaypointIndex++;
                trader.navigator.nextWaypoint = trader.navigator.waypointList.get(trader.navigator.destinationWaypointIndex).waypoint;
//                            if (Debug.isFilterTrader(getName()))
//                                System.out.printf("nextWaypoint = %s\n", navigator.nextWaypoint.name);
                trader.navigator.reserveNextWaypoints();
                trader.navigator.destinationWaypointDistance = trader.navigator.previousWaypoint.queryDistance(trader.navigator.nextWaypoint);
                if (trader.navigator.pathIsClear()) {
                    trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_ALIGNING);
                    if (Debug.isFilterTrader(trader.getName()))
                        logger.info("startRotation");
                    trader.getManeuveringSystem().startRotation();
                    //							if (sourceWaypoint.getName().equals(targetWaypoint.getName())) {
                    //								System.out.println(sourceWaypoint.getName());
                    //							}
                    //							if (targetWaypoint != null) {
                    //							} else {
                    //							}
                } else {
                    trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_WAITING_FOR_WAYPOINT);
                }
            }
        }
    }

    public void reachedWaypoint() {
        //we reached a waypoint
        //rotate towards next waypoint
        //if next waypoint is still not a city?
        if (trader.getTraderSubStatus() != TraderSubStatus.TRADER_STATUS_WAITING_FOR_WAYPOINT) {
            //					if (sourceWaypoint.getName().equals(targetWaypoint.getName())) {
            //						System.out.println(sourceWaypoint.getName());
            //					}
            float d = 0;
            for (int i = 1; i < trader.navigator.destinationWaypointIndex - 1; i++) {
                final WaypointProxy sw = trader.navigator.waypointList.get(i - 1);
                final WaypointProxy tw = trader.navigator.waypointList.get(i);
                d += sw.waypoint.queryDistance(tw.waypoint);
            }
            trader.eventManager.add(EventLevel.trace, trader.currentTime, this, String.format("reached waypoint %s %f %f %f", trader.navigator.nextWaypoint.getName(), d + trader.navigator.destinationWaypointDistanceProgress, trader.navigator.destinationPlanetDistanceProgress, trader.navigator.destinationPlanetDistance));
        } else {
            trader.eventManager.add(EventLevel.trace, trader.currentTime, this, String.format("waiting for waypoint %s to become clear", trader.navigator.nextWaypoint.getName()));
        }
        if (trader.navigator.nextWaypoint.city == null) {
//                    if (Debug.isFilterTrader(getName()))
//                        logger.info(String.format("reached waypoint %s %s %s", nextWaypoint.name, traderStatus.getDisplayName(), traderSubStatus.getDisplayName()));
            // we reached the next waypoint
            if (trader.navigator.previousWaypoint != null) {
                trader.navigator.previousWaypoint.trader = null;//free
            }
            trader.navigator.previousWaypoint = trader.navigator.nextWaypoint;
            trader.navigator.reserveNextWaypoints();
            //					if (sourceWaypoint.getName().equals(targetWaypoint.getName())) {
            //						System.out.println(sourceWaypoint.getName());
            //					}
            trader.navigator.destinationWaypointIndex++;
            trader.navigator.nextWaypoint                        = trader.navigator.waypointList.get(trader.navigator.destinationWaypointIndex).waypoint;
            trader.navigator.destinationWaypointDistanceProgress = trader.navigator.destinationWaypointDistanceProgress - trader.navigator.destinationWaypointDistance;
            trader.navigator.destinationWaypointDistance         = trader.navigator.previousWaypoint.queryDistance(trader.navigator.nextWaypoint);
            trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_ALIGNING);
            if (Debug.isFilterTrader(trader.getName()))
                trader.eventManager.add(EventLevel.trace, trader.currentTime, this, String.format("aligning to %s", trader.navigator.nextWaypoint.getName()));
            trader.getManeuveringSystem().startRotation();
        } else {
            reachedPort();
        }
    }

    public void waypointCleared() {
        if (Debug.isFilterTrader(trader.getName()))
            trader.eventManager.add(EventLevel.trace, trader.currentTime, this, String.format("we are cleared for next waypoint %s", trader.navigator.nextWaypoint.name));
        if (trader.navigator.destinationWaypointIndex >= 2 && trader.navigator.waypointList.get(trader.navigator.destinationWaypointIndex - 2).waypoint.city != null) {//TODO we only release the first dock
            trader.clearDock(trader.navigator.waypointList.get(trader.navigator.destinationWaypointIndex - 2).waypoint.city);//finished undocking
        }
        trader.setTraderSubStatus(TraderSubStatus.TRADER_STATUS_ACCELERATING);
    }
}
