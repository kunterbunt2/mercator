package de.bushnaq.abdalla.mercator.universe;

import de.bushnaq.abdalla.mercator.universe.good.GoodList;
import de.bushnaq.abdalla.mercator.universe.jumpgate.JumpGate;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.planet.Planet3DRenderer;
import de.bushnaq.abdalla.mercator.universe.planet.PlanetList;
import de.bushnaq.abdalla.mercator.universe.sector.Sector;
import de.bushnaq.abdalla.mercator.universe.sector.SectorList;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;
import de.bushnaq.abdalla.mercator.universe.sim.trader.TraderList;
import de.bushnaq.abdalla.mercator.universe.tools.Tools;
import de.bushnaq.abdalla.mercator.util.MercatorRandomGenerator;

/**
 * @author bushnaq Created 13.02.2005
 */
public class UniverseGenerator {
	public static final int PLANET_CHANCE_DICE_PORLTION = 6;
	//	public static final float PLANET_MAX_JUMP_GATE_DISTANCE = 1610;// TODO adapt to 2D 1636
	public static final int PLANET_CHANCE_DICE_SIZE = 10;
	public GoodList goodList;
	final int MAX_NUMBER_OF_TRADERS = 1;
	PlanetList planetList;
	public MercatorRandomGenerator randomGenerator;
	// public int randomGeneratorSeed = 5;
	SectorList sectorList;
	private int size;
	public TraderList traderList;

	public UniverseGenerator() {
	}

	private void assignSectors() {
		// ---Assign to sectors
		{
			// ---insert the seed
			// ---for every sector, pick one planet
			for (int i = 1; i < sectorList.size(); i++) {
				final Sector sector = sectorList.get(i);
				Planet planet = null;
				do {
					planet = planetList.get(randomGenerator.nextInt(0, this, planetList.size()));
				} while (planet.sector != null);
				planet.sector = sector;
				// sectorList.sectorMap[(int)( planet.x + size )][(int)( planet.y + size )] =
				// sector;
				sector.numberOfPlanets++;
			}
			// ---Spread the sector seed through the planets
			boolean changesExist = false;
			do {
				changesExist = false;
				for (final Planet planet : planetList) {
					if (planet.sector != null) {
						for (final JumpGate jumpGate : planet.jumpGateList) {
							if (jumpGate.targetPlanet.sector == null) {
								jumpGate.targetPlanet.sector = planet.sector;
								// universe.sectorList.sectorMap[(int)( jumpGate.planet.x + universe.size
								// )][(int)( jumpGate.planet.y + universe.size )] = planet.sector;
								planet.sector.numberOfPlanets++;
								changesExist = true;
							} else {
							}
						}
					} else {
					}
				}
				// {
				// for ( int y = 0; y < size * 2; y++ )
				// {
				// for ( int x = 0; x < size * 2; x++ )
				// {
				// Sector sector = sectorList.sectorMap[x][y];
				// SectorManager sectorManager = new SectorManager();
				// if ( sector == null )
				// {
				// //---west
				// if ( x > 0 && sectorList.sectorMap[x - 1][y] != null )
				// {
				// sectorManager.add( sectorList.sectorMap[x - 1][y] );
				// }
				// //---north
				// if ( y > 0 && sectorList.sectorMap[x][y - 1] != null )
				// {
				// sectorManager.add( sectorList.sectorMap[x][y - 1] );
				// }
				// //---east
				// if ( x < size * 2 - 1 && sectorList.sectorMap[x + 1][y] != null )
				// {
				// sectorManager.add( sectorList.sectorMap[x + 1][y] );
				// }
				// //---South
				// if ( y < size * 2 - 1 && sectorList.sectorMap[x][y + 1] != null )
				// {
				// sectorManager.add( sectorList.sectorMap[x][y + 1] );
				// }
				// // sectorMap[x][y] = sectorManager.getSector();
				// if ( sectorList.sectorMap[x][y] != null )
				// changesExist = true;
				// }
				// }
				// }
				// }
			} while (changesExist);
			// ---Destroy disconnected planets
			{
				for (int i = 0; i < planetList.size();) {
					final Planet planet = planetList.get(i);
					if (planet.sector == null) {
						planetList.remove(i);
					} else {
						i++;
					}
				}
			}
		}
	}

	public void generate(final Universe universe) throws Exception {
		final long time = System.currentTimeMillis();
		System.out.printf("creating universe of " + universe.size + " light years across\n", System.currentTimeMillis() - time);
		size = universe.size;
		randomGenerator = universe.universeRG;
		sectorList = generateSectorList();
		planetList = generatePlanetList(universe);
		generateJumpGates();
		assignSectors();
		removeUnconnectedSectors();
		removeUnusedSectors();
		mapGalaxy();
		traderList = generateTraders();
		// goodList = generateGoods();
		System.out.printf(" in %dms.\n", System.currentTimeMillis() - time);
		universe.sectorList = sectorList;
		universe.planetList = planetList;
		universe.traderList = traderList;
		// universe.goodList = goodList;
	}

	// private GoodList generateGoods()
	// {
	// GoodList goodList = new GoodList();
	// goodList.createGoodList();
	// {
	// for ( Trader trader : traderList )
	// {
	// trader.getGoodList().createEmptyGoodList();
	// }
	// }
	// return goodList;
	// }
	private void generateJumpGates() throws Exception {
		// ---Create the jump gates
		int count = 0;
		for (int i = 0; i < planetList.size(); i++) {
			final Planet planet = planetList.get(i);
			for (int j = 0; j < planetList.size(); j++) {
				if (i != j) {
					final Planet targetPlanet = planetList.get(j);
					final float distance = planet.queryDistance(targetPlanet);
					if (distance < Planet.PLANET_MAX_JUMP_GATE_DISTANCE) {
						count++;
						planet.jumpGateList.add(new JumpGate(planet, targetPlanet));
						if (planet.jumpGateList.size() > 8)
							throw new Exception("unexpected number of jump gates");
					}
				}
			}
		}
		Tools.print(String.format("generated %d jumpgates.\n", count));
		// ---Destroy disconnected planets
		for (int i = 0; i < planetList.size();) {
			final Planet planet = planetList.get(i);
			if (planet.jumpGateList.size() == 0) {
				planetList.remove(i);
			} else {
				i++;
			}
		}
		Tools.print(String.format("%d planets left.\n", planetList.size()));
	}

	// ---Create the planets
	private PlanetList generatePlanetList(final Universe universe) {
		final PlanetList planetList = new PlanetList();
		int count = 0;
		do {
			planetList.clear();
			int index = 0;
			count = 0;
			for (int y = -size; y <= size; y++) {
				for (int x = -size; x <= size; x++) {
					if (randomGenerator.nextInt(0, this, PLANET_CHANCE_DICE_SIZE) < PLANET_CHANCE_DICE_PORLTION) {
						// ---Create planet
						index++;
						final String name = generatePlanetName(index, x, y);
						final float tx = x * Planet.PLANET_DISTANCE /*+ Planet3DRenderer.PLANET_BORDER*/ + randomGenerator.nextInt(0, this, Planet3DRenderer.PLANET_MAX_SHIFT);
						final float ty = y * Planet.PLANET_DISTANCE /*+ Planet3DRenderer.PLANET_BORDER*/ + randomGenerator.nextInt(0, this, Planet3DRenderer.PLANET_MAX_SHIFT);
						final Planet planet = new Planet(name, tx, ty, universe);
						planet.create(randomGenerator);
						count++;
						planetList.add(planet);

					}
				}
			}
		} while (planetList.size() < ((size * 2 + 1) * (size * 2 + 1)) / 2);
		Tools.print(String.format("generated %d planets.\n", count));
		return planetList;
	}

	private String generatePlanetName(final int index, final int x, final int y) {
		//		return "P-" + index;
		return String.format("P-%02d%02d", x + size, y + size);
	}

	private SectorList generateSectorList() {
		final SectorList sectorList = new SectorList();
		sectorList.createSectors(size);
		return sectorList;
	}

	/**
	 * @param aUniverseSize
	 * @throws Exception
	 */
	// private void generatePlanets_old( int aUniverseSize )
	// {
	// universe.planetList.clear();
	// //---Create the planets
	// {
	// for ( int y = -aUniverseSize; y < aUniverseSize; y++ )
	// {
	// for ( int x = -aUniverseSize; x < aUniverseSize; x++ )
	// {
	// if ( randomGenerator.nextInt( 4 ) < 2 )
	// {
	// //---Create planet
	// String name = "P-" + "" + ( ( x + aUniverseSize ) + ( y + aUniverseSize ) * 8
	// );
	// Planet planet = new Planet( name, x, y, universe );
	// planet.create( randomGenerator );
	// universe.planetList.add( planet );
	// }
	// }
	// }
	// }
	// //---Create the jump gates
	// {
	// for ( Planet planet : universe.planetList )
	// {
	// //---For each of the 4 immediate neighbors (west, south, east, north) we try
	// to find a planet sitting there
	// {
	// int dx[] = { -1, 0, 1, 0 };
	// int dy[] = { 0, -1, 0, 1 };
	// for ( int i = 0; i < 4; i++ )
	// {
	// Planet p = universe.planetList.queryPlanetByLocation( planet.x + dx[i],
	// planet.y + dy[i] );
	// if ( p != null )
	// {
	// planet.jumpGateList.add( new JumpGate( p ) );
	// }
	// }
	// }
	// //---For each of the 4 more further neighbors (south-west, south-east,
	// north-east, north-west) we try to find a planet sitting there
	// {
	// int dx[] = { -1, 1, 1, -1 };
	// int dy[] = { -1, -1, 1, 1 };
	// for ( int i = 0; i < 4; i++ )
	// {
	// if ( planet.jumpGateList.size() < 2 )
	// {
	// Planet p = universe.planetList.queryPlanetByLocation( planet.x + dx[i],
	// planet.y + dy[i] );
	// if ( p != null )
	// {
	// planet.jumpGateList.add( new JumpGate( p ) );
	// p.jumpGateList.add( new JumpGate( planet ) );//---This could lead to double
	// connections
	// }
	// }
	// }
	// }
	// //---For each of the 4 more even further neighbors (2xwest, 2xsouth, 2xeast,
	// 2xnorth) we try to find a planet sitting there
	// //---Make sure no planet sits in between
	// {
	// int dx1[] = { -1, 0, 1, 0 };
	// int dy1[] = { 0, -1, 0, 1 };
	// int dx2[] = { -2, 0, 2, 0 };
	// int dy2[] = { 0, -2, 0, 2 };
	// for ( int i = 0; i < 4; i++ )
	// {
	// if ( planet.jumpGateList.size() < 2 )
	// {
	// Planet p = universe.planetList.queryPlanetByLocation( planet.x + dx2[i],
	// planet.y + dy2[i] );
	// if ( ( p != null ) && ( universe.planetList.queryPlanetByLocation( planet.x +
	// dx1[i], planet.y + dy1[i] ) == null ) )
	// {
	// planet.jumpGateList.add( new JumpGate( p ) );
	// p.jumpGateList.add( new JumpGate( planet ) );//---This could lead to double
	// connections
	// }
	// }
	// }
	// }
	// }
	// }
	// //---Destroy disconnected planets
	// {
	// for ( int i = 0; i < universe.planetList.size(); )
	// {
	// Planet planet = universe.planetList.get( i );
	// if ( planet.jumpGateList.size() == 0 )
	// {
	// universe.planetList.remove( i );
	// }
	// else
	// {
	// i++;
	// }
	// }
	// }
	// //---Assign to sectors
	// {
	// //---insert the seed
	// //---for every sector, pick one planet
	// for ( int i = 0; i < universe.sectorList.size(); i++ )
	// {
	// Sector sector = universe.sectorList.get( i );
	// Planet planet = null;
	// do
	// {
	// planet = universe.planetList.get( randomGenerator.nextInt(
	// universe.planetList.size() ) );
	// }
	// while ( planet.sector != null );
	// planet.sector = sector;
	// universe.sectorList.sectorMap[(int)( planet.x + universe.size )][(int)(
	// planet.y + universe.size )] = sector;
	// sector.numberOfPlanets++;
	// }
	// //---Spread the sector seed through the planets
	// boolean changesExist = false;
	// do
	// {
	// changesExist = false;
	// for ( Planet planet : universe.planetList )
	// {
	// if ( planet.sector != null )
	// {
	// for ( JumpGate jumpGate : planet.jumpGateList )
	// {
	// if ( jumpGate.planet.sector == null )
	// {
	// jumpGate.planet.sector = planet.sector;
	// universe.sectorList.sectorMap[(int)( jumpGate.planet.x + universe.size
	// )][(int)( jumpGate.planet.y + universe.size )] = planet.sector;
	// planet.sector.numberOfPlanets++;
	// changesExist = true;
	// }
	// else
	// {
	// }
	// }
	// }
	// else
	// {
	// }
	// }
	// {
	// for ( int y = 0; y < aUniverseSize * 2; y++ )
	// {
	// for ( int x = 0; x < aUniverseSize * 2; x++ )
	// {
	// Sector sector = universe.sectorList.sectorMap[x][y];
	// SectorManager sectorManager = new SectorManager();
	// if ( sector == null )
	// {
	// //---west
	// if ( x > 0 && universe.sectorList.sectorMap[x - 1][y] != null )
	// {
	// sectorManager.add( universe.sectorList.sectorMap[x - 1][y] );
	// }
	// //---north
	// if ( y > 0 && universe.sectorList.sectorMap[x][y - 1] != null )
	// {
	// sectorManager.add( universe.sectorList.sectorMap[x][y - 1] );
	// }
	// //---east
	// if ( x < aUniverseSize * 2 - 1 && universe.sectorList.sectorMap[x + 1][y] !=
	// null )
	// {
	// sectorManager.add( universe.sectorList.sectorMap[x + 1][y] );
	// }
	// //---South
	// if ( y < aUniverseSize * 2 - 1 && universe.sectorList.sectorMap[x][y + 1] !=
	// null )
	// {
	// sectorManager.add( universe.sectorList.sectorMap[x][y + 1] );
	// }
	// // sectorMap[x][y] = sectorManager.getSector();
	// if ( universe.sectorList.sectorMap[x][y] != null )
	// changesExist = true;
	// }
	// }
	// }
	// }
	// }
	// while ( changesExist );
	// //---Destroy disconnected planets
	// {
	// for ( int i = 0; i < universe.planetList.size(); )
	// {
	// Planet planet = universe.planetList.get( i );
	// if ( planet.sector == null )
	// {
	// universe.planetList.remove( i );
	// }
	// else
	// {
	// i++;
	// }
	// }
	// }
	// }
	// for ( Planet planet : universe.planetList )
	// {
	// //---Map and memorize the distance to any other planet
	// planet.pathSeeker.mapGalaxy( planet, 999999 );
	// if ( planet.sector == null )
	// {
	// int a = 0;
	// }
	// }
	// }
	private TraderList generateTraders() throws Exception {
		final TraderList traderList = new TraderList();
		int count = 0;
		for (final Planet planet : planetList) {
			planet.traderList.clear();
			final int number = 1 + randomGenerator.nextInt(0, this, MAX_NUMBER_OF_TRADERS);
			for (int i = 0; i < number; i++) {
				final Trader trader = new Trader(planet, "T-" + count++, Trader.TRADER_START_CREDITS);
				trader.create(randomGenerator);
				planet.traderList.add(trader);
				traderList.add(trader);
			}
		}
		Tools.print(String.format("generated %d traders.\n", count));
		return traderList;
	}

	private void mapGalaxy() {
		// ---Map and memorize the distance to any other planet
		for (final Planet planet : planetList) {
			planet.pathSeeker.mapGalaxy(planet, 999999);
			if (planet.sector == null) {
				final int a = 0;
			}
		}
	}

	private void removeUnconnectedSectors() {
		// ---find biggest sector
		Sector biggestSector = sectorList.firstElement();
		for (final Sector sector : sectorList) {
			if (sector.numberOfPlanets > biggestSector.numberOfPlanets)
				biggestSector = sector;
		}
		planetList.clearSeed();
		final Object seed = new Object();
		for (final Planet planet : planetList) {
			if (planet.sector == biggestSector) {
				planet.seed = seed;
			}
		}
		// ---let the seed distribute
		boolean changesExist = false;
		do {
			changesExist = false;
			for (final Planet planet : planetList) {
				if (planet.seed != null) {
					for (final JumpGate jumpGate : planet.jumpGateList) {
						if (jumpGate.targetPlanet.seed == null) {
							jumpGate.targetPlanet.seed = planet.seed;
							changesExist = true;
						} else {
						}
					}
				} else {
				}
			}
		} while (changesExist);
		// ---Destroy disconnected sectors
		{
			for (int i = 0; i < planetList.size();) {
				final Planet planet = planetList.get(i);
				if (planet.seed == null) {
					planetList.remove(i);
				} else {
					i++;
				}
			}
		}
	}

	private void removeUnusedSectors() {
		// ---find biggest sector
		Sector biggestSector = sectorList.firstElement();
		for (int i = 1; i < sectorList.size();) {
			final Sector sector = sectorList.get(i);
			if (sector.numberOfPlanets == 0) {
				sectorList.remove(i);
				System.out.println("Removed unused sector " + sector.name);
			} else {
				i++;
			}
		}
		for (final Sector sector : sectorList) {
			if (sector.numberOfPlanets > biggestSector.numberOfPlanets)
				biggestSector = sector;
		}
		planetList.clearSeed();
		final Object seed = new Object();
		for (final Planet planet : planetList) {
			if (planet.sector == biggestSector) {
				planet.seed = seed;
			}
		}
		// ---let the seed distribute
		boolean changesExist = false;
		do {
			changesExist = false;
			for (final Planet planet : planetList) {
				if (planet.seed != null) {
					for (final JumpGate jumpGate : planet.jumpGateList) {
						if (jumpGate.targetPlanet.seed == null) {
							jumpGate.targetPlanet.seed = planet.seed;
							changesExist = true;
						} else {
						}
					}
				} else {
				}
			}
		} while (changesExist);
		// ---Destroy disconnected sectors
		{
			for (int i = 0; i < planetList.size();) {
				final Planet planet = planetList.get(i);
				if (planet.seed == null) {
					planetList.remove(i);
				} else {
					i++;
				}
			}
		}
	}
}
