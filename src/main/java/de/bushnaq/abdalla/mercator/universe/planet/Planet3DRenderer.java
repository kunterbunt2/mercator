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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import de.bushnaq.abdalla.engine.GameObject;
import de.bushnaq.abdalla.engine.ObjectRenderer;
import de.bushnaq.abdalla.engine.RenderEngine3D;
import de.bushnaq.abdalla.mercator.renderer.GameEngine3D;
import de.bushnaq.abdalla.mercator.universe.factory.ProductionFacility;
import de.bushnaq.abdalla.mercator.universe.factory.ProductionFacilityStatus;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.good.Good3DRenderer;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import net.mgsx.gltf.scene3d.animation.AnimationControllerHack;
import net.mgsx.gltf.scene3d.model.ModelInstanceHack;

import java.util.ArrayList;
import java.util.List;

public class Planet3DRenderer extends ObjectRenderer<GameEngine3D> {

    public static final  float                          CIRCLE_SIZE            = 512;
    public static final  float                          CITY_SIZE              = 360;
    public static final  float                          LIGHT_HIGHT            = 128;
    public static final  float                          PLANET_ATMOSPHARE_SIZE = 9.6f;
    public static final  int                            PLANET_BORDER          = 256;
    public static final  Color                          PLANET_COLOR           = new Color(0.5f, 0.5f, 0.8f, 1.0f); // 0xff8888cc;
    public static final  float                          PLANET_CORE_SIZE       = Good3DRenderer.GOOD_X * 2;
    //	public static final float PLANET_DISTANCE = 512/* PLANET_SIZE * 2 */;
    public static final  float                          PLANET_HIGHT           = 1000;
    public static final  int                            PLANET_MAX_SHIFT       = Planet.PLANET_DISTANCE / 4;
    public static final  float                          PLANET_SIZE            = 512;
    public static final  int                            PLANET_SPAN_SPACE      = Planet.PLANET_DISTANCE + PLANET_BORDER;
    public static final  float                          SECTOR_HIGHT           = 8;
    public static final  float                          SECTOR_SIZE            = Planet.PLANET_DISTANCE - GameEngine3D.SPACE_BETWEEN_OBJECTS;
    public static final  float                          SECTOR_Y               = -500;
    public static final  float                          WATER_HIGHT            = 1;
    public static final  float                          WATER_SIZE             = SECTOR_SIZE;
    public static final  float                          WATER_Y                = -10;//TODO should use context.getWaterLevel()
    private static final Color                          BRIGHT_WHITE           = new Color(0xfefefeff);
    private static final int                            BUILDING_BLOCK_SIZE    = 13;
    private static final int                            BUILDING_BLOCK_SIZE_2  = BUILDING_BLOCK_SIZE / 2;
    private static final Color                          DEEP_YELLOW            = new Color(0xf29917ff);
    private static final Color                          FOUNDRY_RED            = new Color(0xf71212ff);
    private static final Color                          HAWAIIAN_BLUE          = new Color(0x7892cdff);
    private static final float                          LIGHT_DAY_INTENSITY    = 10000f;
    private static final float                          LIGHT_NIGHT_INTENSITY  = 10000f;
    private static final Color                          ORANGE_YELLOW          = new Color(0xf07e02ff);
    private static final Color                          PEACOCK_BLUE           = new Color(0x092f5cff);
    private static final Color                          PLANET_NAME_COLOR      = new Color(0.08f, 0.247f, 0.596f, 0.8f);
    private static final Color                          SKY_BLUE               = new Color(0x3980c2ff);
    private static final float                          TURBINE_SIZE           = 4;
    private final        List<GameObject<GameEngine3D>> animatedObjects        = new ArrayList<>();
    //	private GameObject ganeObject;
    private final        Planet                         planet;
    private final        List<PointLight>               pointLight             = new ArrayList<>();
    private final        List<GameObject<GameEngine3D>> pointLightObjects      = new ArrayList<>();
    int index = 0;
    private boolean                  dayMode = true;
    //	Scene scene;
    private GameObject<GameEngine3D> gameObject;

    public Planet3DRenderer(final Planet planet) {
        this.planet = planet;
    }

    public static Color getBuildingColor(final int index) {
        switch (index % 6) {
            case 0:
                return PEACOCK_BLUE;
            case 1:
                return FOUNDRY_RED;
            case 2:
                return ORANGE_YELLOW;
            case 3:
                return BRIGHT_WHITE;
            case 4:
                return SKY_BLUE;
            case 5:
                return HAWAIIAN_BLUE;
            default:
                return DEEP_YELLOW;
        }
    }

    @Override
    public void create(final RenderEngine3D<GameEngine3D> renderEngine) {
        createPlanet(renderEngine);
    }

    //	private void renderFactory(Planet planet, Render3DMaster renderMaster) {
    //		{
    //			int index = 0;
    //			for (ProductionFacility productionFacility : planet.productionFacilityList) {
    //				//				productionFacility.getRenderer().render(planet.x, planet.y, renderMaster, index++, false);
    //				// TODO uncomment
    //			}
    //
    //		}
    //	}
    @Override
    public void renderText(final RenderEngine3D<GameEngine3D> renderEngine, final int index, final boolean selected) {
        final float size = 32;
        final float x    = planet.x;
        final float z    = planet.z;
        //draw text
        final PolygonSpriteBatch batch = renderEngine.renderEngine2D.batch;
        final BitmapFont         font  = renderEngine.getGameEngine().getAtlasManager().modelFont;
        {
            final Matrix4 m        = new Matrix4();
            final float   fontSize = font.getLineHeight();
            final float   scaling  = size / fontSize;
            //				m.setToTranslationAndScaling(x, 1, z, scaling, 1f, scaling);
            final Vector3 xVector = new Vector3(1, 0, 0);
            final Vector3 yVector = new Vector3(0, 1, 0);
            final Vector3 zVector = new Vector3(0, 0, 1);
            m.setToTranslation(x + PLANET_SIZE / 2 - size, 1, z + PLANET_SIZE / 2 - size / 5);
            m.rotate(yVector, 90);
            m.rotate(xVector, -90);
            m.scale(scaling, scaling, 1f);
            //				m.setToTranslationAndScaling(x + PLANET_SIZE / 2 - size, 1, z + PLANET_SIZE / 2 - size / 5, scaling, scaling, 1f);
            batch.setTransformMatrix(m);
            font.setColor(PLANET_NAME_COLOR);
            font.draw(batch, planet.getName(), 0, 0);
        }
        int i = 0;
        for (final Good good : planet.getGoodList()) {
            good.get3DRenderer().renderText(planet.x, planet.y, planet.z, renderEngine, i++);
        }
    }

    @Override
    public void update(final RenderEngine3D<GameEngine3D> renderEngine, final long currentTime, final float timeOfDay, final int index, final boolean selected) {
        renderPlanet(renderEngine, currentTime, timeOfDay, planet == renderEngine.getGameEngine().universe.selectedPlanet);
    }

    private void createCity(final RenderEngine3D<GameEngine3D> renderEngine, final float x, final float z) {
        final int   iteration           = 20;
        final float scale               = (CITY_SIZE / 2) / (iteration - 1);
        final float averageBuildingHigh = 16f;
        //		if (planet.getName().equals("P-0202"))
        createCity(renderEngine, x, z, iteration, scale, averageBuildingHigh);
    }

    private void createCity(final RenderEngine3D<GameEngine3D> renderEngine, final float x, final float z, int iteration, final float scale, float averrageBuildingHight) {
        //we are responsible for the 4 corners
        final float streetSize = 6;
        iteration /= 2;
        //		System.out.println(String.format("iteration=%d scale=%f x=%f z=%f", iteration, scale, x, z));
        //		int i = 0;
        averrageBuildingHight = averrageBuildingHight + averrageBuildingHight * (0.5f - renderEngine.getGameEngine().renderMaster.createRG.nextFloat());
        final TwinBuilding twinChances[][] = {{new TwinBuilding(0.3f, 0.0f, 1, 1)/*0,0*/, new TwinBuilding(0.0f, 0.3f, 1, -1)}/*0,1*/, {new TwinBuilding(0.0f, 0.3f, -1, 1)/*1,0*/, new TwinBuilding(0.3f, 0.0f, -1, -1)}/*1,1*/};
        //		z=1, x=1, x=0
        //		0,0
        //		0,1
        //		1,0
        //		1,1

        for (int xi = 0; xi < 2; xi++) {
            for (int zi = 0; zi < 2; zi++) {
                final TwinBuilding twinChance = twinChances[xi][zi];
                if (!twinChance.occupided) {
                    twinChance.occupided = true;
                    float xx = x + (xi * 2 - 1) * iteration * scale;
                    float zz = z + (zi * 2 - 1) * iteration * scale;
                    //the bigger the block, the lower the change for it to be one building
                    final float changceOfOneBuilding = 0.5f / iteration;
                    final float changceOfNoBuilding  = 0.0f / iteration;
                    if (renderEngine.getGameEngine().renderMaster.createRG.nextFloat() < changceOfNoBuilding) {

                    } else if (iteration > 1f && renderEngine.getGameEngine().renderMaster.createRG.nextFloat() > changceOfOneBuilding)
                        //create smaller buildings
                        createCity(renderEngine, xx, zz, iteration, scale, averrageBuildingHight);
                    else {
                        float twinFactorXs = 1f;
                        float twinFactorZs = 1f;
                        if (renderEngine.getGameEngine().renderMaster.createRG.nextFloat() < twinChance.chanceHorizontal) {
                            final TwinBuilding twin = twinChances[xi + twinChance.deltaX][zi];
                            if (!twin.occupided) {
                                twinFactorXs   = 2f;
                                xx             = x;
                                twin.occupided = true;
                            }
                        }
                        if (renderEngine.getGameEngine().renderMaster.createRG.nextFloat() < twinChance.chanceVertical) {
                            final TwinBuilding twin = twinChances[xi][zi + twinChance.deltaZ];
                            if (!twin.occupided) {
                                twinFactorZs   = 2f;
                                zz             = z;
                                twin.occupided = true;
                            }
                        }

                        final GameObject<GameEngine3D> inst = instanciateBuilding(renderEngine, index++);
                        final float                    xs   = iteration * 2 * scale * twinFactorXs - streetSize;
                        //the bigger the building, the lower the change for it to get big
                        final float ys = /*(5 - iteration) * 16;//*/averrageBuildingHight * (0.1f + 3 * renderEngine.getGameEngine().renderMaster.createRG.nextFloat());
                        final float zs = iteration * 2 * scale * twinFactorZs - streetSize;
                        //					System.out.println(String.format("  xx=%f zz=%f xs=%f", xx, zz, xs));
                        inst.instance.transform.setToTranslationAndScaling(xx, ys / 2, zz, xs, ys, zs);
                        inst.update();
                        renderEngine.addStatic(inst);
                    }
                }
                //				i++;
            }
        }
    }

    private void createFactories(final RenderEngine3D<GameEngine3D> renderEngine) {
        final float x = planet.x;
        final float z = planet.z;
        //turbine
        final ColorAttribute emissiveAttribute = ColorAttribute.createEmissive(Color.RED);
        final int            edgeSize          = Good3DRenderer.CONTAINER_EDGE_SIZE;
        final float          fx                = x - Planet3DRenderer.PLANET_SIZE / 2 - 4;
        for (final ProductionFacility productionFacility : planet.productionFacilityList) {
            final int                index = productionFacility.producedGood.type.ordinal();
            final float              fz    = z + Planet3DRenderer.PLANET_SIZE / 2 - edgeSize / 2 * (Good3DRenderer.GOOD_Y) - index * (edgeSize + 1) * (Good3DRenderer.GOOD_Y);
            GameObject<GameEngine3D> go    = new GameObject<>(new ModelInstanceHack(renderEngine.getGameEngine().renderMaster.turbine.scene.model), productionFacility);
            go.instance.transform.setToTranslationAndScaling(fx, 0, fz, TURBINE_SIZE, TURBINE_SIZE, TURBINE_SIZE);
            go.instance.transform.rotate(Vector3.Y, 90);
            go.update();
            go.controller = new AnimationControllerHack(go.instance);
            go.controller.setAnimation(go.instance.getAnimation("rotate"), -1);
            animatedObjects.add(go);
            renderEngine.addDynamic(go);
            go = new GameObject<>(new ModelInstanceHack(renderEngine.getGameEngine().renderMaster.cubeEmissive), planet);
            go.instance.materials.get(0).set(emissiveAttribute);
            go.instance.transform.setToTranslationAndScaling(fx, 0f, fz, 1.0f, 1.0f, 1.0f);
            pointLightObjects.add(go);
            renderEngine.addStatic(go);
        }
    }

    private void createPlanet(final RenderEngine3D<GameEngine3D> renderEngine) {
        final float x = planet.x;
        final float z = planet.z;
        //planet
        {
            gameObject = new GameObject<GameEngine3D>(new ModelInstanceHack(renderEngine.getGameEngine().renderMaster.planet), null, this);
            gameObject.instance.transform.setToTranslationAndScaling(x, -PLANET_HIGHT / 2, z, PLANET_SIZE, PLANET_HIGHT, PLANET_SIZE);
            gameObject.update();
            renderEngine.addStatic(gameObject);

            final PointLight light = new PointLight().set(Color.WHITE, x, LIGHT_HIGHT, z, LIGHT_DAY_INTENSITY);
            pointLight.add(light);
            //			renderMaster.sceneClusterManager.add(light, false);
        }
        //wheel
        //		{
        //			final GameObject ganeObject = new GameObject(new ModelInstanceHack(renderMaster.wheel.scene.model), null);
        //			ganeObject.instance.transform.setToTranslationAndScaling(x, 0, z, CIRCLE_SIZE, 16, CIRCLE_SIZE);
        //			ganeObject.update();
        //			renderMaster.sceneClusterManager.addStatic(ganeObject);
        //		}
        //cube
        //								{
        //									ModelInstanceHack instance = new ModelInstanceHack(renderMaster.animatedCube.scene.model);
        //									ganeObject = new GameObject(instance);
        //									//					scene = new Scene(instance);
        //									ganeObject.instance.transform.setToTranslationAndScaling(x, 128, z, 32, 32, 32);
        //									ganeObject.update();
        //									//					scene.animations.playAll(true);
        //									controller = new AnimationControllerHack(ganeObject.instance);
        //									controller.setAnimation(ganeObject.instance.getAnimation("animation_AnimatedCube"), -1);
        //									renderMaster.sceneClusterManager.addDynamic(ganeObject);
        //								}
        //																{
        //																	ModelInstanceHack instance = new ModelInstanceHack(renderMaster.cube.scene.model);
        //																	ganeObject = new GameObject(instance);
        //																	//					scene = new Scene(instance);
        //																	ganeObject.instance.transform.setToTranslationAndScaling(x, 64, z, 32, 32, 32);
        //																	ganeObject.update();
        //																	//					scene.animations.playAll(true);
        //																	controller = new AnimationControllerHack(ganeObject.instance);
        //																	controller.setAnimation(ganeObject.instance.getAnimation("CubeAction"), -1);
        //																	renderMaster.sceneClusterManager.addDynamic(ganeObject);
        //																}
        createFactories(renderEngine);
        //city
        createCity(renderEngine, x, z);
        //sector
        //		{
        //			//			final Color sectorColor = renderMaster.getDistinctiveColor(planet.sector.type);
        //			final GameObject sectorInstance = new GameObject(new ModelInstanceHack(renderMaster.sector), planet);
        //			sectorInstance.instance.transform.setToTranslationAndScaling(x, SECTOR_Y, z, SECTOR_SIZE, SECTOR_HIGHT, SECTOR_SIZE);
        //			sectorInstance.update();
        //			renderMaster.sceneManager.addStatic(sectorInstance);
        //
        //		}
        //		//water
        //		{
        //			final GameObject sectorInstance = new GameObject(new ModelInstanceHack(renderMaster.water), planet);
        //			sectorInstance.instance.transform.setToTranslationAndScaling(x, WATER_Y, z, WATER_SIZE, WATER_HIGHT, WATER_SIZE);
        //			sectorInstance.update();
        //			renderMaster.sceneManager.addStatic(sectorInstance);
        //		}
    }

    private GameObject<GameEngine3D> instanciateBuilding(final RenderEngine3D<GameEngine3D> renderEngine, final int index) {
        final GameObject<GameEngine3D> go = new GameObject<>(new ModelInstanceHack(renderEngine.getGameEngine().renderMaster.buildingCube), planet);
        //		final Material material = go.instance.materials.get(0);
        //		material.set(new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, getBuildingColor(index)));
        return go;
    }

    private void renderPlanet(final RenderEngine3D<GameEngine3D> renderEngine, final long currentTime, final float timeOfDay, final boolean selected) {
        //		float x = planet.x;
        //		float z = planet.y;
        //		float hps = PLANET_SIZE / 2;
        //		Color color;

        // ---Planet color
        if (selected) {
            //			color = Screen3D.SELECTED_PLANET_COLOR;
        } else {
            //			color = Planet3DRenderer.PLANET_COLOR;
        }
        updatePlanet(renderEngine);
        //		renderMaster.text(x - hps, y - hps, color, Screen3D.TEXT_COLOR, planet.getName());
        //		float height = y - hps + Trader3DRenderer.TRADER_Y_SIZE;
        // {
        // text( x - s, height, color, TEXT_COLOR, String.valueOf(
        // planet.jumpGateList.size() ) );
        // height += TRADER_HEIGHT;
        // }
        //		if (planet.sector != null) {
        //			renderMaster.text(x - hps, height, color, Screen3D.TEXT_COLOR, planet.sector.name);
        //			height += Trader3DRenderer.TRADER_Y_SIZE;
        //		}
        {
            // int textColor = TEXT_COLOR;
            // if ( planet.credits < Planet.PLANET_START_CREDITS )
            // {
            // textColor = Color.red.getRGB();
            // }
            // else
            // {
            // }
            //			renderMaster.text(x - hps, height, color, renderMaster.queryCreditColor(planet),
            //					String.valueOf(planet.getCredits()));
            //			height += Trader3DRenderer.TRADER_Y_SIZE;
        }
        {
            // Text( x-s, height, color, TEXT_COLOR, Printf( "%d-%d",
            // aPlanet.QueryAgrecultureLevel(), aPlanet.QueryTechnologyLevel() )
            // );
            // height += TRADER_HEIGHT;
        }
        {
            // Text( x-s, height, color, TEXT_COLOR, Printf( "%d %d %d",
            // aPlanet.PathSeekerDistance, aPlanet.X, aPlanet.Y ) );
            // height += TRADER_HEIGHT;
        }

        // { int index = 0; for( BcConsumer* consumer =
        // aPlanet.ConsumerList.First(); consumer; consumer = consumer->Next() )
        // { DrawConsumer( aPlanet.X, aPlanet.Y, *consumer, index++, false ); }
        // }

        //		renderFactory(planet, renderMaster);
    }

    private void renderSims(final Planet planet, final RenderEngine3D<GameEngine3D> renderEngine, final float x, final float y, final float hps) {
        {
            final int simIndex = 0;
            for (final Sim sim : planet.simList) {
                //				sim.getRenderer().render(x - hps, y + hps - Screen3D.SIM_HEIGHT, renderMaster, simIndex++, false);
                // TODO uncomment
            }
        }
    }

    private void updatePlanet(final RenderEngine3D<GameEngine3D> renderEngine) {
        for (final GameObject<GameEngine3D> go : animatedObjects) {
            if (ProductionFacility.class.isInstance(go.interactive)) {
                final ProductionFacility pf = (ProductionFacility) go.interactive;
                if (pf.status == ProductionFacilityStatus.PRODUCING)
                    go.controller.update(Gdx.graphics.getDeltaTime());
            }
        }
        if (renderEngine.getGameEngine().renderEngine.isNight() && dayMode) {
            for (final PointLight l : pointLight) {
                l.intensity = LIGHT_NIGHT_INTENSITY;
            }
            dayMode = false;
        } else if (renderEngine.getGameEngine().renderEngine.isDay() && !dayMode) {
            for (final PointLight l : pointLight) {
                l.intensity = LIGHT_DAY_INTENSITY;
            }
            dayMode = true;
        }
    }

}
