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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import de.bushnaq.abdalla.engine.GameObject;
import de.bushnaq.abdalla.engine.ObjectRenderer;
import de.bushnaq.abdalla.engine.RenderEngine2D;
import de.bushnaq.abdalla.engine.RenderEngine3D;
import de.bushnaq.abdalla.mercator.audio.synthesis.MercatorSynthesizer;
import de.bushnaq.abdalla.mercator.renderer.GameEngine2D;
import de.bushnaq.abdalla.mercator.renderer.GameEngine3D;
import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.good.Good3DRenderer;
import de.bushnaq.abdalla.mercator.universe.good.GoodType;
import de.bushnaq.abdalla.mercator.universe.planet.Planet3DRenderer;
import net.mgsx.gltf.scene3d.model.ModelInstanceHack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trader3DRenderer extends ObjectRenderer<GameEngine3D> {

    public static final  Color                                         TRADER_COLOR            = new Color(.7f, .7f, .7f, 0.45f); // 0xffcc5555;
    public static final  Color                                         TRADER_COLOR_IS_GOOD    = Color.LIGHT_GRAY; // 0xaaaaaa
    public static final  float                                         TRADER_SIZE_Z           = (8 + 80 + 8)/*16*/ / Universe.WORLD_SCALE;
    public static final  float                                         TRADER_WIDTH            = 16f;
    private static final float                                         ANTENNA_LENGTH          = 8f;
    //    private static final float                                         LIGHT_DISTANCE          = 0.1f;
    private static final int                                           NUMBER_OF_LIGHTS        = 4;
    private static final float                                         TRADER_ANTENNA_MARGINE  = 1f;
    private static final float                                         TRADER_COCKPIT_SIZE_Z   = 16f;
    private static final float                                         TRADER_ENGINE_SIZE_Z    = 16f;
    private static final Color                                         TRADER_NAME_COLOR       = Color.ORANGE;
    private static final float                                         TRADER_SIZE_X           = 16 / Universe.WORLD_SCALE;
    private static final float                                         TRADER_SIZE_Y           = 16 / Universe.WORLD_SCALE;
    private static final float                                         TRADER_TRAVELING_HEIGHT = -TRADER_SIZE_Y / 2 + Planet3DRenderer.WATER_Y;
    private final        Vector3                                       direction               = new Vector3();//intermediate value
    private final        List<GameObject<GameEngine3D>>                goodInstances           = new ArrayList<>();
    private final        float[]                                       lastVelocity            = new float[3];
    //    private final        List<GameObject<GameEngine3D>>                lightGameObjects             = new ArrayList<>();
    private final        Logger                                        logger                  = LoggerFactory.getLogger(this.getClass());
    //    private final        List<PointLight>                              pointLights                  = new ArrayList<>();
    private final        float[]                                       position                = new float[3];
    private final        Vector3                                       scaling                 = new Vector3();//intermediate value
    private final        Vector3                                       shift                   = new Vector3();//intermediate value
    private final        Vector3                                       speed                   = new Vector3(0, 0, 0);
    private final        List<StrobeLight>                             strobeLights            = new ArrayList<>();
    private final        Vector3                                       target                  = new Vector3();//intermediate value
    private final        Trader                                        trader;
    private final        Vector3                                       translation             = new Vector3();//intermediate value
    private final        Map<GoodType, List<GameObject<GameEngine3D>>> unusedMls               = new HashMap<>();
    private final        Map<GoodType, List<GameObject<GameEngine3D>>> usedMls                 = new HashMap<>();
    private final        float[]                                       velocity                = new float[3];
    private              int                                           TraderColorIndex        = -1;
    private              GameObject<GameEngine3D>                      instance;
    private              GameObject<GameEngine3D>                      instance1;
    private              boolean                                       lastSelected            = false;
    private              long                                          lastTransaction         = 0;
    //    private              int                                           lightMode                    = 0;
//    private              float                                         lightTimer                   = 0;
    private              MercatorSynthesizer                           synth;

    public Trader3DRenderer(final Trader trader) {
        this.trader = trader;
    }

    @Override
    public void create(final RenderEngine3D<GameEngine3D> renderEngine) {
        try {
            createTrader(renderEngine);
            createLights(renderEngine);
            createGoods(renderEngine);
            synth = renderEngine.getGameEngine().audioEngine.createAudioProducer(MercatorSynthesizer.class);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public void render2D(final RenderEngine3D<GameEngine3D> renderEngine, final int index, final boolean selected) {
        final float hps = TRADER_WIDTH / 2;
        Color       color;
        if (selected) {
            color = GameEngine2D.SELECTED_COLOR;
        } else {
            color = TRADER_COLOR;
        }
        if (!trader.traderStatus.isGood()) {
            color = TRADER_COLOR_IS_GOOD;
        }
        final float x = translation.x;
        final float y = translation.y;
        final float z = translation.z;

        renderEngine.renderutils2Dxz.fillCircle(renderEngine.getGameEngine().getAtlasManager().planetTextureRegion, x, 0, z, hps + 1, 32, color);
        if (renderEngine.getCamera().position.y < 3000) renderEngine.renderutils2Dxz.label(renderEngine.getGameEngine().getAtlasManager().dottedLineTextureRegion, trader.x - hps, 0, (trader.z - hps), Trader2DRenderer.TRADER_WIDTH, Trader2DRenderer.TRADER_HEIGHT, TRADER_WIDTH * 1, TRADER_WIDTH * 3, renderEngine.getGameEngine().getAtlasManager().demoMidFont, color, trader.getName(), color, String.format("%.0f", trader.getCredits()), renderEngine.getGameEngine().queryCreditColor(trader.getCredits(), Trader.TRADER_START_CREDITS));

    }

    @Override
    public void renderText(final RenderEngine3D<GameEngine3D> renderEngine, final int index, final boolean selected) {
        renderTextOnTop(renderEngine, 0, 0, trader.getName().substring(2), 16);
        renderTextOnTop(renderEngine, -6, -7, "" + (int) velocity[0], 3);//x speed
        renderTextOnTop(renderEngine, 6, -7, "" + (int) velocity[2], 3);//z speed
        renderTextOnTop(renderEngine, 0, -7, "" + Float.toString(toOneDigitPrecision(synth.getGain())), 3);//bass gain
    }

    @Override
    public void update(final RenderEngine3D<GameEngine3D> renderEngine, final long currentTime, final float timeOfDay, final int index, final boolean selected) throws Exception {
        update(renderEngine, currentTime, index, selected);
    }

    private void createGoods(final RenderEngine3D<GameEngine3D> renderEngine) {
        goodInstances.clear();
        for (final Good g : trader.getGoodList()) {
            final List<GameObject<GameEngine3D>> unused    = getUnusedGoodList(g.type);
            final List<GameObject<GameEngine3D>> used      = getUsedGoodList(g.type);
            final int                            usedDelta = used.size() - g.getAmount() / 5;
            if (usedDelta > 0) {
                for (int i = 0; i < usedDelta; i++) {
                    final GameObject<GameEngine3D> go = used.remove(used.size() - 1);
                    unused.add(go);
                    if (!renderEngine.removeDynamic(go)) logger.error("Game engine logic error: Expected dynamic GameObject to exist.");
                }
            } else if (usedDelta < 0) {
                TraderColorIndex = g.type.ordinal();
                final int addNr    = -usedDelta;
                final int reuseNr  = Math.min(addNr, unused.size());// reuse from unused
                final int createNr = addNr - reuseNr;// create the rest
                for (int i = 0; i < reuseNr; i++) {
                    final GameObject<GameEngine3D> go = unused.remove(unused.size() - 1);
                    used.add(go);
                    //					goodInstances.add(go);
                    renderEngine.addDynamic(go);
                }
                for (int i = 0; i < createNr; i++) {
                    final GameObject<GameEngine3D> go = Good3DRenderer.instanciateGoodGameObject(g, renderEngine);
                    used.add(go);
                    //					goodInstances.add(go);
                    renderEngine.addDynamic(go);
                }
            }
            for (final GameObject<GameEngine3D> go : used) {
                goodInstances.add(go);
            }
        }
    }

    private void createLights(final RenderEngine3D<GameEngine3D> renderEngine) {
        final Vector3[] delta = {
                //20.1 - 33,7
                new Vector3(TRADER_SIZE_X / 2 + ANTENNA_LENGTH + StrobeLight.LIGHT_SIZE / 2, TRADER_SIZE_Y / 2 - TRADER_ANTENNA_MARGINE, TRADER_SIZE_Z / 2 - TRADER_ENGINE_SIZE_Z + TRADER_ANTENNA_MARGINE),//back/right/top
                new Vector3(TRADER_SIZE_X / 2 - TRADER_ANTENNA_MARGINE, TRADER_SIZE_Y / 2 - TRADER_ANTENNA_MARGINE, -TRADER_SIZE_Z / 2 - ANTENNA_LENGTH - StrobeLight.LIGHT_SIZE / 2),//front/right/top
                new Vector3(-TRADER_SIZE_X / 2 - ANTENNA_LENGTH - StrobeLight.LIGHT_SIZE / 2, -TRADER_SIZE_Y / 2 + TRADER_ANTENNA_MARGINE, -TRADER_SIZE_Z / 2 + +TRADER_COCKPIT_SIZE_Z - TRADER_ANTENNA_MARGINE),//front/left/bottom
                new Vector3(-TRADER_SIZE_X / 2 + TRADER_ANTENNA_MARGINE, -TRADER_SIZE_Y / 2 - ANTENNA_LENGTH - StrobeLight.LIGHT_SIZE / 2, TRADER_SIZE_Z / 2 - TRADER_ENGINE_SIZE_Z / 2 + 2),//back/left/bottom
        };
        for (int i = 0; i < NUMBER_OF_LIGHTS; i++) {
            strobeLights.add(new StrobeLight(renderEngine, delta[i], new GameObject<GameEngine3D>(new ModelInstanceHack(renderEngine.getGameEngine().assetManager.redEmissiveModel), trader)));
        }
    }

    private void createTrader(final RenderEngine3D<GameEngine3D> renderEngine) {
        instance = new GameObject<GameEngine3D>(new ModelInstanceHack(renderEngine.getGameEngine().assetManager.trader.scene.model), trader, this);
        renderEngine.addDynamic(instance);
        instance1 = new GameObject<GameEngine3D>(new ModelInstanceHack(renderEngine.getGameEngine().assetManager.cubeTrans1), trader, this);
        renderEngine.addDynamic(instance1);
    }

    private int getColorIndex() {
        //		return trader.getName().hashCode() % NUMBER_OF_LIGHT_COLORS;
        return TraderColorIndex;
    }

    private List<GameObject<GameEngine3D>> getUnusedGoodList(final GoodType type) {
        if (unusedMls.get(type) == null) unusedMls.put(type, new ArrayList<GameObject<GameEngine3D>>());
        return unusedMls.get(type);
    }

    private List<GameObject<GameEngine3D>> getUsedGoodList(final GoodType type) {
        if (usedMls.get(type) == null) usedMls.put(type, new ArrayList<GameObject<GameEngine3D>>());
        return usedMls.get(type);
    }

    private boolean nearListener(final RenderEngine3D<GameEngine3D> renderEngine) {
        final Vector3 v = renderEngine.getCamera().position;
        if ((trader.x - v.x) + (trader.z - v.z) > 1000) return false;
        else return true;
    }

    public void render2Da(final RenderEngine2D<GameEngine3D> renderEngine, final int index, final boolean selected) {
        final float hps = TRADER_WIDTH / 2;
        Color       color;
        if (selected) {
            color = GameEngine2D.SELECTED_COLOR;
        } else {
            color = TRADER_COLOR;
        }
        if (!trader.traderStatus.isGood()) {
            color = TRADER_COLOR_IS_GOOD;
        }
        final float x = translation.x;
        final float y = translation.y;
        final float z = translation.z;

        renderEngine.fillCircle(renderEngine.getGameEngine().getAtlasManager().planetTextureRegion, x, z, hps + 1, 32, color);
        if (renderEngine.camera.position.y < 3000) renderEngine.lable(renderEngine.getGameEngine().getAtlasManager().dottedLineTextureRegion, trader.x - hps, (trader.z - hps), Trader2DRenderer.TRADER_WIDTH, Trader2DRenderer.TRADER_HEIGHT, TRADER_WIDTH * 1, TRADER_WIDTH * 3, renderEngine.getGameEngine().getAtlasManager().demoMidFont, color, trader.getName(), color, String.format("%.0f", trader.getCredits()), renderEngine.getGameEngine().queryCreditColor(trader.getCredits(), Trader.TRADER_START_CREDITS));

    }

    private void renderTextOnTop(final RenderEngine3D<GameEngine3D> renderEngine, final float dx, final float dy, final String text, final float size) {
        final float x = translation.x;
        final float y = translation.y;
        final float z = translation.z;
        //draw text
        final PolygonSpriteBatch batch = renderEngine.renderEngine2D.batch;
        final BitmapFont         font  = renderEngine.getGameEngine().getAtlasManager().modelFont;
        {
            final Matrix4     m        = new Matrix4();
            final float       fontSize = font.getLineHeight();
            final float       scaling  = size / fontSize;
            final GlyphLayout layout   = new GlyphLayout();
            layout.setText(font, text);
            final float width  = layout.width;// contains the width of the current set text
            final float height = layout.height; // contains the height of the current set text
            //on top
            {
                final Vector3 xVector = new Vector3(1, 0, 0);
                final Vector3 yVector = new Vector3(0, 1, 0);
                m.setToTranslation(x - height * scaling / 2.0f - dy, y + TRADER_SIZE_Y / 2.0f + 0.2f, z + width * scaling / 2.0f - dx);
                m.rotateTowardDirection(direction, Vector3.Y);
                m.translate(/*-8*/0, 0, +TRADER_SIZE_Z / 2 + 8);
                m.rotate(yVector, 90);
                m.rotate(xVector, -90);
                m.scale(scaling, scaling, 1f);

            }
            batch.setTransformMatrix(m);
            font.setColor(TRADER_NAME_COLOR);
            font.draw(batch, text, 0, 0);
        }
    }

    private float toOneDigitPrecision(final float value) {
        return ((float) ((int) (value * 10))) / 10;
    }

    private void update(final RenderEngine3D<GameEngine3D> renderEngine, final long currentTime, final int index, final boolean selected) throws Exception {
        updateTrader(renderEngine, index, selected);
        updateLights(renderEngine, currentTime);
        if (lastTransaction != trader.lastTransaction) {
            createGoods(renderEngine);
//            updateLightColor(renderEngine);
            lastTransaction = trader.lastTransaction;
        }
        updateGoods(renderEngine);
    }

    private void updateGoods(final RenderEngine3D<GameEngine3D> renderEngine) {

        for (int i = 0; i < goodInstances.size(); i++) {
//            final GameObject go         = goodInstances.get(i);
//            final int        xEdgeSize  = (int) (TRADER_SIZE_X / Good3DRenderer.GOOD_X);
//            final int        zEdgeSize  = (int) (TRADER_SIZE_Z / Good3DRenderer.GOOD_Y);
//            final int        xContainer = i % xEdgeSize;
//            final int        zContainer = (int) Math.floor(i / xEdgeSize) % zEdgeSize;
//            final int        yContainer = (int) Math.floor(i / (xEdgeSize * zEdgeSize));
//            final float      x          = translation.x - TRADER_SIZE_X / 2 + Good3DRenderer.GOOD_X / 2 + xContainer * (Good3DRenderer.GOOD_X);
//            final float      z          = translation.z - TRADER_SIZE_Z / 2 + Good3DRenderer.GOOD_Y / 2 + zContainer * (Good3DRenderer.GOOD_Z);
//            final float      y          = translation.y - TRADER_SIZE_Y / 2 - Good3DRenderer.GOOD_Z / 2 - yContainer * (Good3DRenderer.GOOD_Y);
//            go.instance.transform.setToTranslationAndScaling(x, y, z, Good3DRenderer.GOOD_X - Good3DRenderer.SPACE_BETWEEN_GOOD, Good3DRenderer.GOOD_Y - Good3DRenderer.SPACE_BETWEEN_GOOD, Good3DRenderer.GOOD_Z - Good3DRenderer.SPACE_BETWEEN_GOOD);
//            go.update();
            final GameObject<GameEngine3D> go = goodInstances.get(i);
            go.instance.transform.setToTranslation(translation.x, translation.y, translation.z);

            go.instance.transform.rotateTowardDirection(direction, Vector3.Y);

            final int   xEdgeSize  = (int) (TRADER_SIZE_X / Good3DRenderer.GOOD_X);
            final int   yEdgeSize  = (int) (TRADER_SIZE_Y / Good3DRenderer.GOOD_Y);
            final int   xContainer = i % xEdgeSize;
            final int   yContainer = (int) Math.floor(i / xEdgeSize) % yEdgeSize;
            final int   zContainer = (int) Math.floor(i / (xEdgeSize * yEdgeSize));
            final float x          = -TRADER_SIZE_X / 2 + Good3DRenderer.GOOD_X / 2 + xContainer * (Good3DRenderer.GOOD_X + 2);
            final float z          = /*-40*/ +16 - TRADER_SIZE_Z / 2 + Good3DRenderer.GOOD_Y / 2 + zContainer * (Good3DRenderer.GOOD_Z + 2);
            final float y          = +TRADER_SIZE_Y / 2 - Good3DRenderer.GOOD_Z / 2 - yContainer * (Good3DRenderer.GOOD_Y + 2);


            go.instance.transform.translate(x, y, z);
            go.instance.transform.scale(Good3DRenderer.GOOD_X - Good3DRenderer.SPACE_BETWEEN_GOOD, Good3DRenderer.GOOD_Y - Good3DRenderer.SPACE_BETWEEN_GOOD, Good3DRenderer.GOOD_Z - Good3DRenderer.SPACE_BETWEEN_GOOD);
            go.update();
        }
    }

//    private void updateLightColor(final RenderEngine3D<GameEngine3D> renderEngine) {
//        //TODO reuse instances
//        final ColorAttribute emissiveAttribute = ColorAttribute.createEmissive(Good3DRenderer.getColor(getColorIndex()));
//        for (final GameObject go : lightGameObjects) {
//            go.instance.materials.get(0).set(emissiveAttribute);
//        }
//    }

    private void updateLights(final RenderEngine3D<GameEngine3D> renderEngine, final long currentTime) {
        //		lightTranslation.set(translation);
//        if (trader.targetWaypoint != null) {
//            final float intensitiy = LIGHT_INTENSITY;
//            for (int i = 0; i < NUMBER_OF_LIGHTS; i++) {
//                lightTranslation.x = dx[i];
//                lightTranslation.z = dz[i];
//                //todo lights must be rotated with trader
//                lightGameObjects.get(i).instance.transform.setToTranslationAndScaling(lightTranslation, lightScaling);
//                lightGameObjects.get(i).update();
//                pointLights.get(i).set(Good3DRenderer.getColor(getColorIndex()), lightTranslation, intensitiy);
//            }
//        } else {
//            // in port
//            final float intensitiy = (float) Math.abs(Math.sin((currentTime) / (2000f)) * 500f);
//            //			lightTranslation.y = translation.y - TRADER_Y_SIZE / 2 + Screen3D.SPACE_BETWEEN_OBJECTS * 10;
//            for (int i = 0; i < NUMBER_OF_LIGHTS; i++) {
//                lightTranslation.x = dx[i];
//                lightTranslation.z = dz[i];
//                //				lightTranslation.y = lightTranslation.y - TRADER_Y_SIZE / 2 + Trader3DRenderer.TRADER_Z_SIZE + Screen3D.SPACE_BETWEEN_OBJECTS;
//                pointLights.get(i).set(Color.RED, lightTranslation, intensitiy);
//            }
//
//        }
        {
            // in port
//            final float intensity = (float) Math.abs(Math.sin((currentTime) / (2000f)) * LIGHT_MAX_INTENSITY);
//            final float intensity = (float) Math.abs(Math.sin(lightTimer / LIGHT_ON_DURATION) * LIGHT_MAX_INTENSITY);
            //			lightTranslation.y = translation.y - TRADER_Y_SIZE / 2 + Screen3D.SPACE_BETWEEN_OBJECTS * 10;

        }
        for (StrobeLight strobeLight : strobeLights) {
            strobeLight.update(renderEngine, translation, direction);
        }
    }

    private void updateTrader(final RenderEngine3D<GameEngine3D> renderEngine, final int index, final boolean selected) throws Exception {
        if (trader.targetWaypoint != null) {

            position[0] = translation.x;
            position[1] = translation.y;
            position[2] = translation.z;
            trader.calculateEngineSpeed();
            if (trader.sourceWaypoint != null) speed.set(trader.targetWaypoint.x - trader.sourceWaypoint.x, 0, trader.targetWaypoint.z - trader.sourceWaypoint.z);
            else speed.set(1, 0, 1);

            speed.nor();
            //			final float engineSpeed = trader.getMaxEngineSpeed();
            speed.scl(trader.getMaxEngineSpeed());
            velocity[0] = speed.x;
            velocity[1] = 0;
            velocity[2] = speed.z;

            boolean update = false;
            for (int i = 0; i < 3; i++) {
                if (Math.abs(lastVelocity[i] - velocity[i]) > 0.001f) {
                    update = true;
                }
            }
            if (update) {
                synth.setPositionAndVelocity(position, velocity);
                //				if (trader.getName().equals("T-6"))
                //					logger.info(String.format("%f %f  %f %f  %f %f", lastVelocity[0], velocity[0], lastVelocity[1], velocity[1], lastVelocity[2], velocity[2]));
                for (int i = 0; i < 3; i++)
                     lastVelocity[i] = velocity[i];
            }

            //			if (trader.getName().equals("T-6"))
            synth.play();
            translation.y = Good3DRenderer.GOOD_HEIGHT * 8 + GameEngine3D.SPACE_BETWEEN_OBJECTS;
            // ---Traveling
            if (trader.destinationWaypointDistance != 0) {
                final float scalex = (trader.targetWaypoint.x - trader.sourceWaypoint.x);
                final float scaley = (trader.targetWaypoint.y - trader.sourceWaypoint.y);
                final float scalez = (trader.targetWaypoint.z - trader.sourceWaypoint.z);
                direction.set(scalex, scaley, scalez);
                //				shift.set(-direction.z, direction.y, direction.x);
                //				shift.nor();
                //				shift.scl(Planet.CHANNEL_SIZE / 2);
                translation.x = (trader.sourceWaypoint.x + (trader.targetWaypoint.x - trader.sourceWaypoint.x) * trader.destinationWaypointDistanceProgress / trader.destinationWaypointDistance) /*+ shift.x*/;
//				translation.y = (trader.sourceWaypoint.y + (trader.targetWaypoint.y - trader.sourceWaypoint.y) * trader.destinationWaypointDistanceProgress / trader.destinationWaypointDistance) /*+ shift.y*/ + TRADER_TRAVELING_HEIGHT;
                translation.z = (trader.sourceWaypoint.z + (trader.targetWaypoint.z - trader.sourceWaypoint.z) * trader.destinationWaypointDistanceProgress / trader.destinationWaypointDistance) /*+ shift.z*/;
            } else {
                translation.x = trader.planet.x /*- Planet3DRenderer.PLANET_ATMOSPHARE_SIZE / 2*/;
//				translation.y = trader.planet.y + TRADER_TRAVELING_HEIGHT;
                translation.z = trader.planet.z /*- Planet3DRenderer.PLANET_ATMOSPHARE_SIZE / 2 + index * TRADER_SIZE_Z*/;
            }
        } else {
            synth.pause();
            // in port
            translation.x = trader.planet.x /*- Planet3DRenderer.PLANET_ATMOSPHARE_SIZE / 2*/;
//			translation.y = trader.planet.y + TRADER_TRAVELING_HEIGHT;
            translation.z = trader.planet.z /*- Planet3DRenderer.PLANET_ATMOSPHARE_SIZE / 2 + index * TRADER_SIZE_Z*/;
        }
        //translation.add(0, TRADER_SIZE_Y / 2, 0);
//        scaling.set(TRADER_SIZE_X, TRADER_SIZE_Y, TRADER_SIZE_Z);
        scaling.set(1, 1, 1);
        instance.instance.transform.setToTranslation(translation);
        instance1.instance.transform.setToTranslation(translation);

        //		pole.instance.transform.setToTranslation(translation);

        if (trader.targetWaypoint != null) {
            target.set(trader.targetWaypoint.x/* + shift.x*/, Planet3DRenderer.WATER_Y, trader.targetWaypoint.z/* + shift.z*/);
            //			instance.instance.transform.rotateTowardTarget(target, Vector3.Y);
            instance.instance.transform.rotateTowardDirection(direction, Vector3.Y);
            instance.instance.transform.scale(scaling.x, scaling.y, scaling.z);
//            instance.instance.transform.scale(scaling.x * 17, scaling.y * 17, scaling.z * (96));
            instance1.instance.transform.rotateTowardDirection(direction, Vector3.Y);
            instance1.instance.transform.scale(scaling.x * 17, scaling.y * 17, scaling.z * (96 - 32));
        }

        //		if (trader.getName().equals("T-50")) {
        //						System.out.println("x=" + position[0] + " y=" + position[1] + " z=" + position[2]);
        //		}

        trader.x = translation.x;
        trader.y = translation.y;
        trader.z = translation.z;
        instance.update();
        instance1.update();
        //		pole.update();
        if (selected != lastSelected) {
            if (selected) {
//                instance.instance.materials.get(0).set(new PBRColorAttribute(ColorAttribute.Emissive, Color.YELLOW));
//                instance.instance.materials.get(0).remove(PBRColorAttribute.BaseColorFactor);
//                instance.instance.materials.get(0).set(new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, Color.DARK_GRAY));
//                instance1.instance.materials.get(0).set(new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, Color.DARK_GRAY));
            } else {
                //				instance.instance.materials.get(0).remove(ColorAttribute.Emissive);
                //				final PBRColorAttribute ca = (PBRColorAttribute) renderMaster.trader.materials.get(0).get(PBRColorAttribute.BaseColorFactor);
                //				instance.instance.materials.get(0).set(new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, ca.color));
            }
            lastSelected = selected;
        }
    }

    //	void bar(Render3DMaster renderMaster, TextureRegion image, float aX1, float aY1, float aX2, float aY2, Color color) {
    //		PolygonSpriteBatch batch = renderMaster.sceneClusterManager.batch2D;
    //		float x1 = aX1;
    //		float y1 = aY1;
    //		float x2 = aX2;
    //		float y2 = aY2;
    //		float width = x2 - x1 + 1;
    //		float height = y2 - y1 - 1;
    //		Vector3 p1 = new Vector3(x1, y1, 0);
    //		Vector3 p2 = new Vector3(x2, y2, 0);
    //		BoundingBox bb = new BoundingBox(p2, p1);
    //		// Vector3[] v3 = camera.frustum.planePoints;
    //		batch.setColor(color);
    //		batch.draw(image, x1, y1, width, height);
    //	}

}
