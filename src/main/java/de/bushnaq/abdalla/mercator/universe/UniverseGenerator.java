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

import de.bushnaq.abdalla.engine.audio.OpenAlException;
import de.bushnaq.abdalla.mercator.engine.GameEngine;
import de.bushnaq.abdalla.mercator.universe.good.GoodList;
import de.bushnaq.abdalla.mercator.universe.land.Land;
import de.bushnaq.abdalla.mercator.universe.land.LandList;
import de.bushnaq.abdalla.mercator.universe.path.Path;
import de.bushnaq.abdalla.mercator.universe.path.PathList;
import de.bushnaq.abdalla.mercator.universe.path.Waypoint;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.planet.Planet3DRenderer;
import de.bushnaq.abdalla.mercator.universe.planet.PlanetList;
import de.bushnaq.abdalla.mercator.universe.ring.Ring;
import de.bushnaq.abdalla.mercator.universe.sector.Sector;
import de.bushnaq.abdalla.mercator.universe.sector.SectorList;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;
import de.bushnaq.abdalla.mercator.universe.sim.trader.TraderList;
import de.bushnaq.abdalla.mercator.util.MercatorRandomGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bushnaq Created 13.02.2005
 */
public class UniverseGenerator {
    public static final int                     PLANET_CHANCE_DICE_PORLTION = 6;
    //	public static final float PLANET_MAX_JUMP_GATE_DISTANCE = 1610;// TODO adapt to 2D 1636
    public static final int                     PLANET_CHANCE_DICE_SIZE     = 10;
    public static final int                     PLANET_MAX_HIGHT            = 256;
    public static final int                     PLANET_MAX_SHIFT            = Planet.PLANET_DISTANCE / 2;
    public static final float                   PLANET_MAX_SHIFT_JUMP_GATE  = (float) Planet.PLANET_DISTANCE + PLANET_MAX_SHIFT + 10;
    final               int                     MAX_NUMBER_OF_TRADERS       = 1;
    private final       Logger                  logger                      = LoggerFactory.getLogger(this.getClass());
    public              GoodList                goodList;
    public              PathList                pathList;
    public              MercatorRandomGenerator randomGenerator;
    public              TraderList              traderList;
    LandList   landList;
    PlanetList planetList;
    // public int randomGeneratorSeed = 5;
    SectorList sectorList;
    private int size;

    public UniverseGenerator() {
    }

    private void addPath(final PathList pathList, final Waypoint w, final Object seed) {
        if (w.seed == null) {
            w.seed = seed;
            for (final Path path : w.pathList) {
                pathList.add(path);
                if (path.target.seed == null) {
                    addPath(pathList, path.target, seed);
                } else {
                    //already seeded
                }
            }
        }
    }

    private void assignSectors() {
        // ---Assign to sectors
        {
            // ---insert the seed
            // ---for every sector, pick one planet
            for (int i = 1; i < sectorList.size(); i++) {
                final Sector sector = sectorList.get(i);
                Planet       planet = null;
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
                // propagate sector info from planet to its jumpgates (path)
                for (final Planet planet : planetList) {
                    if (planet.sector != null) {
                        for (final Path jumpGate : planet.pathList) {
                            if (jumpGate.target.sector == null) {
                                jumpGate.target.sector = planet.sector;
                                propergateSector(jumpGate.target);
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
            } while (changesExist);
            // ---Destroy disconnected planets
            {
                for (int i = 0; i < planetList.size(); ) {
                    final Planet planet = planetList.get(i);
                    if (planet.sector == null) {
                        planetList.remove(i);
                    } else {
                        i++;
                    }
                }
            }
        }
        logger.info(String.format("%d planets left after assigning sectors.", planetList.size()));
    }

    private void connectPlanets(final Planet sourcePlanet, final Planet targetPlanet, final float zSign, final float xSign, final String name) {
        final Waypoint w0 = new Waypoint(sourcePlanet.getName() + "-0" + name, sourcePlanet.x + zSign * Planet.CHANNEL_SIZE / 2 - xSign * (Planet3DRenderer.PLANET_3D_SIZE /*/ 2*/ + Planet.CHANNEL_SIZE), sourcePlanet.y, sourcePlanet.z - zSign * (Planet3DRenderer.PLANET_3D_SIZE /*/ 2*/ + Planet.CHANNEL_SIZE) - xSign * Planet.CHANNEL_SIZE / 2);
        final Path     p0 = new Path(sourcePlanet, w0);
        sourcePlanet.pathList.add(p0);

//        final Waypoint w1 = new Waypoint(sourcePlanet.getName() + "-1*" + name, sourcePlanet.x + zSign * Planet.CHANNEL_SIZE / 2 - xSign * (Planet3DRenderer.PLANET_3D_SIZE / 2 + Planet.CHANNEL_SIZE), sourcePlanet.y, sourcePlanet.z - zSign * (Planet3DRenderer.PLANET_3D_SIZE / 2 + Planet.CHANNEL_SIZE) - xSign * Planet.CHANNEL_SIZE / 2);
//
//        final Waypoint w2 = new Waypoint(sourcePlanet.getName() + "-2*" + name, targetPlanet.x + zSign * Planet.CHANNEL_SIZE / 2 + xSign * (Planet3DRenderer.PLANET_3D_SIZE / 2 + Planet.CHANNEL_SIZE), targetPlanet.y, targetPlanet.z + zSign * (Planet3DRenderer.PLANET_3D_SIZE / 2 + Planet.CHANNEL_SIZE) - xSign * Planet.CHANNEL_SIZE / 2);
//        final Path     p  = new Path(w0, w2);
//        sourcePlanet.pathList.add(p0);
//        w0.pathList.add(p);

        {
//            final float p0X = w1.x;
//            final float p0Z = w1.z;
//
//            final float p1X = w1.x - xSign * (Planet3DRenderer.PLANET_3D_SIZE);
//            final float p1Z = w1.z - zSign * (Planet3DRenderer.PLANET_3D_SIZE);
//
//            final float p2X = w2.x + xSign * (Planet3DRenderer.PLANET_3D_SIZE);
//            final float p2Z = w2.z + zSign * (Planet3DRenderer.PLANET_3D_SIZE);
//
//            final float p3X = w2.x;
//            final float p3Z = w2.z;
//
//            Waypoint lastW = w0;
//            Waypoint w     = null;
//            int      n     = 1;
//            //use smaller incrementation to increase number of spline elements
//            for (float t = 0.0f; t <= 1.0f; t += 0.05f) {
//                final float x = ((1.0f - t) * (1.0f - t) * (1.0f - t) * p0X + 3 * (1.0f - t) * (1.0f - t) * t * p1X + 3 * (1.0f - t) * t * t * p2X + t * t * t * p3X);
//                final float z = ((1.0f - t) * (1.0f - t) * (1.0f - t) * p0Z + 3 * (1.0f - t) * (1.0f - t) * t * p1Z + 3 * (1.0f - t) * t * t * p2Z + t * t * t * p3Z);
//                final float y = sourcePlanet.y;
//                w = new Waypoint(sourcePlanet.getName() + "-" + n++ + name, x, y, z);
//                final Path p = new Path(lastW, w);
//                lastW.pathList.add(p);
//                lastW = w;
//            }


            final Waypoint w3 = new Waypoint(sourcePlanet.getName() + "-" + 3 + name, targetPlanet.x + zSign * Planet.CHANNEL_SIZE / 2 + xSign * (Planet3DRenderer.PLANET_3D_SIZE /*/ 2*/ + Planet.CHANNEL_SIZE), targetPlanet.y, targetPlanet.z + zSign * (Planet3DRenderer.PLANET_3D_SIZE /*/ 2*/ + Planet.CHANNEL_SIZE) - xSign * Planet.CHANNEL_SIZE / 2);
            final Path     p3 = new Path(w0, w3);
            w0.pathList.add(p3);

            final Path p4 = new Path(w3, targetPlanet);
            w3.pathList.add(p4);

        }

    }
//    private void connectPlanets(final Planet sourcePlanet, final Planet targetPlanet, final float zSign, final float xSign, final String name) {
//        final Waypoint w0 = new Waypoint(sourcePlanet.getName() + "-0" + name, sourcePlanet.x + zSign * Planet.CHANNEL_SIZE / 2 - xSign * (Planet3DRenderer.PLANET_3D_SIZE / 4), sourcePlanet.y, sourcePlanet.z - zSign * (Planet3DRenderer.PLANET_3D_SIZE / 4) - xSign * Planet.CHANNEL_SIZE / 2);
//        final Path     p0 = new Path(sourcePlanet, w0);
//        sourcePlanet.pathList.add(p0);
//
//        final Waypoint w1 = new Waypoint(sourcePlanet.getName() + "-1*" + name, sourcePlanet.x + zSign * Planet.CHANNEL_SIZE / 2 - xSign * (Planet3DRenderer.PLANET_3D_SIZE / 2 + Planet.CHANNEL_SIZE), sourcePlanet.y, sourcePlanet.z - zSign * (Planet3DRenderer.PLANET_3D_SIZE / 2 + Planet.CHANNEL_SIZE) - xSign * Planet.CHANNEL_SIZE / 2);
//
//        final Waypoint w2 = new Waypoint(sourcePlanet.getName() + "-2*" + name, targetPlanet.x + zSign * Planet.CHANNEL_SIZE / 2 + xSign * (Planet3DRenderer.PLANET_3D_SIZE / 2 + Planet.CHANNEL_SIZE), targetPlanet.y, targetPlanet.z + zSign * (Planet3DRenderer.PLANET_3D_SIZE / 2 + Planet.CHANNEL_SIZE) - xSign * Planet.CHANNEL_SIZE / 2);
//
//        {
//            final float p0X = w1.x;
//            final float p0Z = w1.z;
//
//            final float p1X = w1.x - xSign * (Planet3DRenderer.PLANET_3D_SIZE);
//            final float p1Z = w1.z - zSign * (Planet3DRenderer.PLANET_3D_SIZE);
//
//            final float p2X = w2.x + xSign * (Planet3DRenderer.PLANET_3D_SIZE);
//            final float p2Z = w2.z + zSign * (Planet3DRenderer.PLANET_3D_SIZE);
//
//            final float p3X = w2.x;
//            final float p3Z = w2.z;
//
//            Waypoint lastW = w0;
//            Waypoint w     = null;
//            int      n     = 1;
//            //use smaller incrementation to increase number of spline elements
//            for (float t = 0.0f; t <= 1.0f; t += 0.05f) {
//                final float x = ((1.0f - t) * (1.0f - t) * (1.0f - t) * p0X + 3 * (1.0f - t) * (1.0f - t) * t * p1X + 3 * (1.0f - t) * t * t * p2X + t * t * t * p3X);
//                final float z = ((1.0f - t) * (1.0f - t) * (1.0f - t) * p0Z + 3 * (1.0f - t) * (1.0f - t) * t * p1Z + 3 * (1.0f - t) * t * t * p2Z + t * t * t * p3Z);
//                final float y = sourcePlanet.y;
//                w = new Waypoint(sourcePlanet.getName() + "-" + n++ + name, x, y, z);
//                final Path p = new Path(lastW, w);
//                lastW.pathList.add(p);
//                lastW = w;
//            }
//
//
//            final Waypoint w3 = new Waypoint(sourcePlanet.getName() + "-" + n + name, targetPlanet.x + zSign * Planet.CHANNEL_SIZE / 2 + xSign * (Planet3DRenderer.PLANET_3D_SIZE / 4), targetPlanet.y, targetPlanet.z + zSign * (Planet3DRenderer.PLANET_3D_SIZE / 4) - xSign * Planet.CHANNEL_SIZE / 2);
//            final Path     p3 = new Path(w, w3);
//            w.pathList.add(p3);
//
//            final Path p4 = new Path(w3, targetPlanet);
//            w3.pathList.add(p4);
//
//        }
//
//    }

    public void generate(GameEngine gameEngine, final Universe universe) throws Exception {
        logger.info("----------------------------------------------------------------------------------");
        logger.info(String.format("creating universe of %d light years across.", universe.size));
        final long time = System.currentTimeMillis();
        size            = universe.size;
        universe.ring   = new Ring(universe);
        randomGenerator = universe.universeRG;
        sectorList      = generateSectorList();
        planetList      = generatePlanetList(gameEngine, universe);
        generatePaths();
        assignSectors();
        removeUnconnectedSectors();
        removeUnusedSectors();
        mapGalaxy();
        pathList   = generatePathList();
        landList   = generateLand(planetList, universe);
        traderList = generateTraders(gameEngine);
        // goodList = generateGoods();
//        System.out.printf(" in %dms.\n", System.currentTimeMillis() - time);
        universe.sectorList = sectorList;
        universe.planetList = planetList;
        universe.traderList = traderList;
        universe.landList   = landList;
        universe.pathList   = pathList;
        // universe.goodList = goodList;
        //		for (final Planet planet : planetList) {
        //			logger.info(planet.getName());
        //		}
        logger.info(String.format("generated universe of %d light years across within %dms.", universe.size, System.currentTimeMillis() - time));
        logger.info("----------------------------------------------------------------------------------");
    }

    private LandList generateLand(final PlanetList planetList, final Universe universe) {
        final LandList landList = new LandList();
        for (int z = -size; z <= size; z++) {
            for (int x = -size; x <= size; x++) {
                final float lx    = x * Planet.PLANET_DISTANCE;
                final float lz    = z * Planet.PLANET_DISTANCE;
                boolean     found = false;
                for (final Planet planet : planetList) {
                    if (planet.x >= lx && planet.x < lx + Planet.PLANET_DISTANCE && planet.z >= lz && planet.z < lz + Planet.PLANET_DISTANCE) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    final float ly   = 0;
                    final Land  land = new Land(lx + PLANET_MAX_SHIFT / 2, ly, lz + PLANET_MAX_SHIFT / 2, universe);
                    landList.add(land);
                }
            }
        }
        final int border = size + 1;
        for (int z = -border; z <= border; z++) {
            for (int x = -border; x <= border; x++) {
                if (z == border || z == -border || x == border || x == -border) {
                    final float lx   = x * Planet.PLANET_DISTANCE;
                    final float lz   = z * Planet.PLANET_DISTANCE;
                    final float ly   = 0;
                    final Land  land = new Land(lx + PLANET_MAX_SHIFT / 2, ly, lz + PLANET_MAX_SHIFT / 2, universe);
                    landList.add(land);
                }
            }
        }
        logger.info(String.format("generated %d lands.", landList.size()));
        return landList;
    }

    private PathList generatePathList() {
        final PathList pathList = new PathList();
        planetList.clearSeed();
        final Planet planet = planetList.get(0);
        //		planet.seed = new Object();
        addPath(pathList, planet, new Object());
        logger.info(String.format("generated %d paths.", pathList.size()));
        return pathList;
    }

    private void generatePaths() throws Exception {
        // ---Create the jump gates
        int count = 0;
        for (int i = 0; i < planetList.size(); i++) {
            final Planet sourcePlanet = planetList.get(i);
            for (int j = 0; j < planetList.size(); j++) {
                if (i != j) {
                    final Planet targetPlanet = planetList.get(j);
                    final float  distance     = sourcePlanet.queryDistance(targetPlanet);
                    if (distance < PLANET_MAX_SHIFT_JUMP_GATE) {
                        final float xDelta = (float) Math.floor(sourcePlanet.x / Planet.PLANET_DISTANCE) - (float) Math.floor(targetPlanet.x / Planet.PLANET_DISTANCE);
                        final float zDelta = (float) Math.floor(sourcePlanet.z / Planet.PLANET_DISTANCE) - (float) Math.floor(targetPlanet.z / Planet.PLANET_DISTANCE);
                        //target lies to one of the cardinal directions
                        String name;
                        if (xDelta == 0f) {
                            final float zSign = Math.signum(zDelta);
                            final float xSign = 0;
                            if (zSign > 0)
                                name = "N";
                            else
                                name = "S";
                            connectPlanets(sourcePlanet, targetPlanet, zSign, xSign, name);
                        } else if (zDelta == 0f) {
                            final float xSign = Math.signum(xDelta);
                            final float zSign = 0;
                            if (xSign > 0)
                                name = "W";
                            else
                                name = "E";
                            connectPlanets(sourcePlanet, targetPlanet, zSign, xSign, name);
                        } else {
                            //not a direct neighbor
                        }

                        count++;
                        if (sourcePlanet.pathList.size() > 4)
                            throw new Exception("unexpected number of paths");
                    }
                }
            }
        }
        logger.info(String.format("generated %d jumpgates.", count));
        // ---Destroy disconnected planets
        for (int i = 0; i < planetList.size(); ) {
            final Planet planet = planetList.get(i);
            if (planet.pathList.size() == 0) {
                planetList.remove(i);
            } else {
                i++;
            }
        }
        logger.info(String.format("%d planets left.", planetList.size()));
    }

    // ---Create the planets
    private PlanetList generatePlanetList(GameEngine gameEngine, final Universe universe) throws OpenAlException {
        final PlanetList planetList = new PlanetList();
        int              count      = 0;
        do {
            planetList.clear();
            int index = 0;
            count = 0;
            for (int z = -size; z <= size; z++) {
                for (int x = -size; x <= size; x++) {
                    if (randomGenerator.nextInt(0, this, PLANET_CHANCE_DICE_SIZE) < PLANET_CHANCE_DICE_PORLTION) {
                        // ---Create planet
                        index++;
                        final String name   = generatePlanetName(index, x, z);
                        final float  tx     = x * Planet.PLANET_DISTANCE + randomGenerator.nextInt(0, this, PLANET_MAX_SHIFT) / 2;
                        final float  ty     = 0/*randomGenerator.nextInt(0, this, PLANET_MAX_HIGHT)*/;
                        final float  tz     = z * Planet.PLANET_DISTANCE + randomGenerator.nextInt(0, this, PLANET_MAX_SHIFT) / 2;
                        final Planet planet = new Planet(name, tx, ty, tz, universe);
                        planet.create(gameEngine, randomGenerator);
                        count++;
                        planetList.add(planet);

                    }
                }
            }
        } while (planetList.size() < ((size * 2 + 1) * (size * 2 + 1)) / 2);
        logger.info(String.format("generated %d planets.", count));
//        for (Planet planet : planetList) {
//            logger.info(String.format("%s.", planet.getName()));
//        }

        return planetList;
    }

    private String generatePlanetName(final int index, final int x, final int z) {
//        return String.format("P-%02d%02d", x + size, z + size);
        return String.format("P-%d", index);
    }

    private SectorList generateSectorList() {
        final SectorList sectorList = new SectorList();
        sectorList.createSectors(size);
        return sectorList;
    }

    private TraderList generateTraders(GameEngine gameEngine) throws Exception {
        final TraderList traderList = new TraderList();
        int              count      = 0;
        for (final Planet planet : planetList) {
            planet.traderList.clear();
            final int number = 1 + randomGenerator.nextInt(0, this, MAX_NUMBER_OF_TRADERS);
            for (int i = 0; i < number; i++) {
                final Trader trader = new Trader(planet, "T-" + count++, Trader.TRADER_START_CREDITS);
                trader.create(gameEngine, randomGenerator);
                planet.traderList.add(trader);
                traderList.add(trader);
            }
        }
        logger.info(String.format("generated %d traders.", traderList.size()));
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

    private void propergateSector(Waypoint start) {
//        logger.info(String.format("Waypoint=%s",start.name));
        for (Path path : start.pathList) {
            if (path.target.sector == null) {
                if (path.target.city != null)
                    path.target.city.sector = start.sector;
                path.target.sector = start.sector;
                propergateSector(path.target);
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
                break;
            }
        }
        for (final Planet planet : planetList) {
            if (planet.seed != null) {
                seed(planet);
            }
        }
        // ---let the seed distribute
        //		boolean changesExist = false;
        //		do {
        //			changesExist = false;
        //			for (final Planet planet : planetList) {
        //				if (planet.seed != null) {
        //					for (final Path jumpGate : planet.pathList) {
        //						if (jumpGate.target.seed == null) {
        //							jumpGate.target.seed = planet.seed;
        //							changesExist = true;
        //						} else {
        //						}
        //					}
        //				} else {
        //				}
        //			}
        //		} while (changesExist);
        // ---Destroy disconnected sectors
        {
            for (int i = 0; i < planetList.size(); ) {
                final Planet planet = planetList.get(i);
                if (planet.seed == null) {
                    planetList.remove(i);
                } else {
                    i++;
                }
            }
        }
        logger.info(String.format("%d planets left after removeing unconnected sectors.", planetList.size()));
    }

    private void removeUnusedSectors() {
        // ---find biggest sector
        Sector biggestSector = sectorList.firstElement();
        for (int i = 1; i < sectorList.size(); ) {
            final Sector sector = sectorList.get(i);
            if (sector.numberOfPlanets == 0) {
                sectorList.remove(i);
                logger.info("Removed unused sector " + sector.name);
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
        for (final Planet planet : planetList) {
            if (planet.seed != null) {
                seed(planet);
            }
        }
        // ---let the seed distribute
        //		boolean changesExist = false;
        //		do {
        //			changesExist = false;
        //			for (final Planet planet : planetList) {
        //				if (planet.seed != null) {
        //					for (final Path jumpGate : planet.pathList) {
        //						if (jumpGate.target.seed == null) {
        //							jumpGate.target.seed = planet.seed;
        //							changesExist = true;
        //						} else {
        //						}
        //					}
        //				} else {
        //				}
        //			}
        //		} while (changesExist);
        // ---Destroy disconnected sectors

        //		{
        //			for (int i = 0; i < planetList.size();) {
        //				final Planet planet = planetList.get(i);
        //				if (planet.seed == null) {
        //					planetList.remove(i);
        //				} else {
        //					i++;
        //				}
        //			}
        //		}
        logger.info(String.format("%d planets left after removing unused sectors.", planetList.size()));
    }

    private void seed(final Waypoint w) {
        for (final Path path : w.pathList) {
            if (path.target.seed == null) {
                path.target.seed = w.seed;
                seed(path.target);
            } else {
                //already seeded
            }
        }
    }

}
