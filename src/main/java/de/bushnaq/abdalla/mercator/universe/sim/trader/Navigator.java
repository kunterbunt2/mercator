package de.bushnaq.abdalla.mercator.universe.sim.trader;

import de.bushnaq.abdalla.mercator.universe.path.Waypoint;
import de.bushnaq.abdalla.mercator.universe.path.WaypointList;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;

/**
 * Navigator class is responsible for managing the navigation of a trader
 * through a series of waypoints towards a destination planet.
 * It keeps track of the current and next waypoints, as well as the source and destination planets.
 */
public class Navigator {
    public        Planet       destinationPlanet                   = null; // ---The planet we want to reach ultimately
    protected     float        destinationPlanetDistance;
    public        float        destinationPlanetDistanceProgress   = 0;
    public        float        destinationWaypointDistance         = 0;
    public        float        destinationWaypointDistanceProgress = 0;
    public        int          destinationWaypointIndex            = 0; // ---The index into the WaypointList
    public        Waypoint     nextWaypoint                        = null; // ---The next waypoint we want to reach in our way to the destinationPlanet
    public final  int          numberOfWaypointsBetweenTraders     = 1;
    public        Waypoint     previousWaypoint                    = null; // ---The previous waypoint we reached in our way to the destinationPlanet
    public        Planet       sourcePlanet                        = null; // ---The planet origin of the good we are currently selling
    private final Trader       trader;
    public        WaypointList waypointList                        = new WaypointList();

    Navigator(Trader trader) {
        this.trader = trader;
    }

    public void extractWaypointList() {
        if (destinationPlanet != null) {
            waypointList.removeAllElements();
            Waypoint p = destinationPlanet;
            while (p != trader.planet) {
                waypointList.add(p);
                p = trader.planet.pathSeeker.get(p).pathSeekerNextWaypoint;
            }
            waypointList.add(p);
        }
    }

    public Planet findNextPlanet(int currentWaypointIndex) {
        for (int i = currentWaypointIndex + 1; i < waypointList.size(); i++) {
            if (waypointList.get(i).waypoint.city != null) {
                return waypointList.get(i).waypoint.city;
            }
        }
        return null;
    }

    public boolean pathIsClear() {
        //next waypoint must be free
        //destinationWaypointIndex-1
        if (previousWaypoint != null && previousWaypoint.trader != null && previousWaypoint.trader != trader)
            return false;
        if (nextWaypoint.trader != null && nextWaypoint.trader != trader) return false;
        for (int i = 0; i < numberOfWaypointsBetweenTraders - 1; i++) {
            //next waypoint must be either the last one, or the one after is also free
            if ((destinationWaypointIndex + i < waypointList.size()) && waypointList.get(destinationWaypointIndex + i).waypoint.trader != null && waypointList.get(destinationWaypointIndex + i).waypoint.trader != trader)
                return false;
        }
        return true;
    }

    public void reserveNextWaypoints() {
        previousWaypoint.trader = trader;
        nextWaypoint.trader     = trader;
        for (int i = 0; i < numberOfWaypointsBetweenTraders - 2; i++) {
            //next waypoint must be either the last one, or the one after is also free
            if (destinationWaypointIndex + i < waypointList.size()) waypointList.get(destinationWaypointIndex + i).waypoint.trader = trader;
        }
    }

}
