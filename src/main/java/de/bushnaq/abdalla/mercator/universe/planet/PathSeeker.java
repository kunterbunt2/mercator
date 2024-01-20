package de.bushnaq.abdalla.mercator.universe.planet;

import java.util.HashMap;
import java.util.Map;

import de.bushnaq.abdalla.mercator.universe.good.GoodType;
import de.bushnaq.abdalla.mercator.universe.jumpgate.JumpGate;

public class PathSeeker {
	GoodType destinationGoodType = null;
	public float goodPrice; // ---used by queryBestPlanetToSell
	Map<Planet, Path> pathMap = new HashMap<Planet, Path>();
	public float planetValue; // ---used by queryBestPlanetToSell
	public float time; // ---used by queryBestPlanetToSell

	public PathSeeker() {
	}

	void findDestination(final Planet sourcePlanet, final Planet aDestinationPlanet, final int aMaxDistance) {
		for (final JumpGate jumpGate : sourcePlanet.jumpGateList) {
			final float distance = get(sourcePlanet).distance + sourcePlanet.queryDistance(jumpGate.targetPlanet);
			// if( distance < aMaxDistance )
			{
				if (distance < get(jumpGate.targetPlanet).distance) {
					get(jumpGate.targetPlanet).distance = distance;
					get(jumpGate.targetPlanet).pathSeekerNextWaypoint = sourcePlanet;
					/*
					 * if( jumpGate->Planet == aDestinationPlanet ) { } else
					 */
					{
						findDestination(jumpGate.targetPlanet, aDestinationPlanet, aMaxDistance);
					}
				} else {
					// ---Someone else was already here
				}
			}
		}
	}

	public Path get(final Planet planet) {
		Path path = pathMap.get(planet);
		if (path == null) {
			path = new Path(planet);
			pathMap.put(planet, path);
		}
		return path;
	}

	/**
	 * This is a very simple path seeker that calculates the minimum distance of
	 * every planet in the universe to the portPlanet.
	 *
	 * @param portPlanet
	 * @param aMaxDistance
	 */
	public void mapGalaxy(final Planet portPlanet, final int aMaxDistance) {
		put(portPlanet, 0);
		markNeighborDistance(portPlanet, aMaxDistance);
	}

	void markNeighborDistance(final Planet planet, final int aMaxDistance) {
		// ---For every jumpgate
		for (final JumpGate jumpGate : planet.jumpGateList) {
			final float distance = get(planet).distance + planet.queryDistance(jumpGate.targetPlanet);
			if (distance < aMaxDistance) {
				if (distance < get(jumpGate.targetPlanet).distance) {
					get(jumpGate.targetPlanet).distance = distance;
					get(jumpGate.targetPlanet).pathSeekerNextWaypoint = planet;
					markNeighborDistance(jumpGate.targetPlanet, aMaxDistance);
				} else {
					// ---Someone else was already here
				}
			}
		}
	}

	private void put(final Planet portPlanet, final int i) {
		pathMap.put(portPlanet, new Path(portPlanet, i, null));
	}

	float queryDistance(final Planet aSourcePlanet, final Planet aDestinationPlanet, final int aMaxDistance) {
		queryFirstWaypoint(aSourcePlanet, aDestinationPlanet, aMaxDistance);
		return get(aDestinationPlanet).distance;
	}

	Planet queryFirstWaypoint(final Planet sourcePlanet, final Planet destinationPlanet, final int maxDistance) {
		// ---We start from the source
		// ---Reset the distance
		// Sleep(1000);
		// for ( Planet planet : planetList )
		// {
		// get( planet ).pathSeekerDistance = 999992;
		// get( planet ).pathSeekerNextWaypoint = null;
		// }
		if ((sourcePlanet == destinationPlanet) || (destinationPlanet == null)) {
			get(sourcePlanet).distance = 0;
			return null;
		} else {
			get(sourcePlanet).distance = 0;
			findDestination(sourcePlanet, destinationPlanet, maxDistance);
			// ---Mark the path
			/*
			 * BcPlanet* planet = aDestinationPlanet; { while( planet->NextWaypointToStart
			 * != aSourcePlanet ) { planet->Selected = true; planet =
			 * planet->NextWaypointToStart; } //planet->Selected = true;
			 * planet->NextWaypointToStart->Selected = true; }
			 */
			return queryNextWaypoint(sourcePlanet, destinationPlanet);
		}
	}

	Planet queryNextWaypoint(final Planet sourcePlanet, Planet aDestinationPlanet) {
		while (get(aDestinationPlanet).pathSeekerNextWaypoint != sourcePlanet) {
			aDestinationPlanet = get(aDestinationPlanet).pathSeekerNextWaypoint;
		}
		return aDestinationPlanet;
	}
}
