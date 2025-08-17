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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import de.bushnaq.abdalla.engine.*;
import de.bushnaq.abdalla.mercator.desktop.LaunchMode;
import de.bushnaq.abdalla.mercator.engine.GameEngine2D;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;
import de.bushnaq.abdalla.mercator.engine.audio.synthesis.MercatorSynthesizer;
import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.good.Good3DRenderer;
import de.bushnaq.abdalla.mercator.universe.good.GoodType;
import de.bushnaq.abdalla.mercator.universe.planet.Planet3DRenderer;
import net.mgsx.gltf.scene3d.lights.SpotLightEx;
import net.mgsx.gltf.scene3d.model.ModelInstanceHack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trader3DRenderer extends ObjectRenderer<GameEngine3D> {

    private static final float                                         ANTENNA_LENGTH          = 8f;
    private static final int                                           DRAW_GOOD_FACTOR        = 5;// we only draw a portion of the actual good containers
    private static final int                                           NUMBER_OF_LIGHTS        = 4;
    private static final int                                           NUMBER_OF_THRUSTERS     = 4;
    private static final float                                         PORT_HIGHT              = -128f;
    private static final float                                         TRADER_ANTENNA_MARGINE  = 1f;
    private static final float                                         TRADER_COCKPIT_SIZE_Z   = 16f;
    public static final  Color                                         TRADER_COLOR            = new Color(.7f, .7f, .7f, 0.45f); // 0xffcc5555;
    public static final  Color                                         TRADER_COLOR_IS_GOOD    = Color.LIGHT_GRAY; // 0xaaaaaa
    public static final  float                                         TRADER_DOCKING_HEIGHT   = -128f;
    private static final float                                         TRADER_ENGINE_SIZE_Z    = 16f;
    private static final float                                         TRADER_EXTERNAL_SIZE_X  = 32 / Universe.WORLD_SCALE;
    public static final  float                                         TRADER_EXTERNAL_SIZE_Y  = 8 / Universe.WORLD_SCALE;
    public static final  float                                         TRADER_FLIGHT_HEIGHT    = 24f;
    private static final Color                                         TRADER_NAME_COLOR       = new Color(0xffa500ff);
    private static final float                                         TRADER_SIZE_X           = 16 / Universe.WORLD_SCALE;
    public static final  float                                         TRADER_SIZE_Y           = 16 / Universe.WORLD_SCALE;
    public static final  float                                         TRADER_SIZE_Z           = (16 + 64 + 16)/*16*/ / Universe.WORLD_SCALE;
    private static final float                                         TRADER_TRAVELING_HEIGHT = -TRADER_SIZE_Y / 2 + Planet3DRenderer.WATER_Y;
    public static final  float                                         TRADER_WIDTH            = 16f;
    final static         Vector3                                       xVectorNeg              = new Vector3(-1, 0, 0);
    final static         Vector3                                       yVectorNeg              = new Vector3(0, -1, 0);
    static               float                                         TRADER_THRUSTER_MARGIN  = 2f;
    private final        Vector3                                       direction               = new Vector3();//intermediate value
    private final        List<GameObject<GameEngine3D>>                goodInstances           = new ArrayList<>();
    //    private              GameObject<GameEngine3D> instance1;
    private final        boolean                                       lastSelected            = false;
    private              long                                          lastTransaction         = 0;
    private final        float[]                                       lastVelocity            = new float[3];
    private final        Logger                                        logger                  = LoggerFactory.getLogger(this.getClass());
    private final        float[]                                       position                = new float[3];
    private              SpotLightEx                                   spotLight;
    //    private final        Vector3                                       scaling                 = new Vector3();//intermediate value
    //    private final        Vector3                                       shift                   = new Vector3();//intermediate value
    private final        List<StrobeLight>                             strobeLights            = new ArrayList<>();
    private              MercatorSynthesizer                           synth;
    private final        Vector3                                       target                  = new Vector3();//intermediate value
    private final        Trader                                        trader;
    private              int                                           traderColorIndex        = -1;
    private              GameObject<GameEngine3D>                      traderGameObject;
    private final        Vector3                                       translation             = new Vector3();//intermediate value
    private final        Map<GoodType, List<GameObject<GameEngine3D>>> unusedMls               = new HashMap<>();
    private final        Map<GoodType, List<GameObject<GameEngine3D>>> usedMls                 = new HashMap<>();
    private final        float[]                                       velocity                = new float[3];
    private final        VelocityVector                                velocityVector          = new VelocityVector();

    public Trader3DRenderer(final Trader trader) {
        this.trader = trader;
    }

    @Override
    public void create(final RenderEngine3D<GameEngine3D> renderEngine) {
        try {
            createTrader(renderEngine);
            if (renderEngine.getGameEngine().launchMode == LaunchMode.development) velocityVector.create(renderEngine);
            createLights(renderEngine);
            createThrusters(renderEngine);
            createEngine(renderEngine);
            createGoods(renderEngine);
            synth = renderEngine.getGameEngine().audioEngine.createAudioProducer(MercatorSynthesizer.class);
            synth.setGain(100);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void createEngine(RenderEngine3D<GameEngine3D> renderEngine) {
        trader.getEngine().create(renderEngine);
    }

    private void createGoods(final RenderEngine3D<GameEngine3D> renderEngine) {
        goodInstances.clear();
        for (final Good g : trader.getGoodList()) {
            final List<GameObject<GameEngine3D>> unused    = getUnusedGoodList(g.type);
            final List<GameObject<GameEngine3D>> used      = getUsedGoodList(g.type);
            final int                            usedDelta = used.size() - g.getAmount() / DRAW_GOOD_FACTOR;
            if (usedDelta > 0) {
                for (int i = 0; i < usedDelta; i++) {
                    final GameObject<GameEngine3D> go = used.remove(used.size() - 1);
                    unused.add(go);
                    if (!renderEngine.removeDynamic(go)) logger.error("Game engine logic error: Expected dynamic GameObject to exist.");
                }
            } else if (usedDelta < 0) {
                traderColorIndex = g.type.ordinal();
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
        final Vector3[] delta = {//
                new Vector3(TRADER_EXTERNAL_SIZE_X / 2 + ANTENNA_LENGTH + StrobeLight.LIGHT_SIZE / 2, 0, TRADER_SIZE_Z / 2 - TRADER_ENGINE_SIZE_Z + TRADER_ANTENNA_MARGINE),//back/right/top
                new Vector3(TRADER_SIZE_X / 2 - TRADER_ANTENNA_MARGINE, 0, -TRADER_SIZE_Z / 2 - ANTENNA_LENGTH - StrobeLight.LIGHT_SIZE / 2),//front/right/top
                new Vector3(-TRADER_EXTERNAL_SIZE_X / 2 - ANTENNA_LENGTH - StrobeLight.LIGHT_SIZE / 2, 0, -TRADER_SIZE_Z / 2 + +TRADER_COCKPIT_SIZE_Z - TRADER_ANTENNA_MARGINE),//front/left/bottom
                new Vector3(-TRADER_SIZE_X / 2 + TRADER_ANTENNA_MARGINE, -TRADER_EXTERNAL_SIZE_Y / 2 - ANTENNA_LENGTH - StrobeLight.LIGHT_SIZE / 2, TRADER_SIZE_Z / 2 - TRADER_ENGINE_SIZE_Z / 2 + 2),//back/left/bottom
        };
        for (int i = 0; i < NUMBER_OF_LIGHTS; i++) {
            strobeLights.add(new StrobeLight(renderEngine, delta[i], new GameObject<GameEngine3D>(new ModelInstanceHack(renderEngine.getGameEngine().assetManager.redEmissiveModel), null), new GameObject<GameEngine3D>(new ModelInstanceHack(renderEngine.getGameEngine().assetManager.redEmissiveBohkeyModel), null)));
        }
    }

    private void createThrusters(final RenderEngine3D<GameEngine3D> renderEngine) {
        final Vector3[] delta = {//
                new Vector3(-TRADER_EXTERNAL_SIZE_X / 2 - TRADER_THRUSTER_MARGIN, 0, -TRADER_SIZE_Z / 2),//left, top, front
                new Vector3(TRADER_EXTERNAL_SIZE_X / 2 + TRADER_THRUSTER_MARGIN, 0, TRADER_SIZE_Z / 2),//right, top, back
                new Vector3(TRADER_EXTERNAL_SIZE_X / 2 + TRADER_THRUSTER_MARGIN, 0, -TRADER_SIZE_Z / 2),//right, top, front
                new Vector3(-TRADER_EXTERNAL_SIZE_X / 2 - TRADER_THRUSTER_MARGIN, 0, TRADER_SIZE_Z / 2),//left, top, back
        };
        final Vector3[] direction = {//
                new Vector3(xVectorNeg).scl(5f),//front/left/bottom
                new Vector3(Vector3.X).scl(5f),//back/right/top
                new Vector3(Vector3.X).scl(5f),//front/right/top
                new Vector3(xVectorNeg).scl(5f),//back/left/bottom
        };
        final RotationDirection[] rotationDirection = {//
                RotationDirection.CLOCKWISE,//front/left/bottom
                RotationDirection.CLOCKWISE,//back/right/top
                RotationDirection.COUNTER_CLOCKWISE,//front/right/top
                RotationDirection.COUNTER_CLOCKWISE,//back/left/bottom
        };
        final float[] rotation = {//
                180f,//front/left/bottom
                0f,//back/right/top
                0f,//front/right/top
                180f,//back/left/bottom
        };
        trader.getThrusters().create(renderEngine.getGameEngine().getAudioEngine());
        for (int i = 0; i < NUMBER_OF_THRUSTERS; i++) {
            trader.getManeuveringSystem().getThrusters().add(new Thruster(renderEngine, delta[i], direction[i], rotationDirection[i], rotation[i], new GameObject<GameEngine3D>(new ModelInstanceHack(renderEngine.getGameEngine().assetManager.flame.scene.model), trader)));
        }
    }

    private void createTrader(final RenderEngine3D<GameEngine3D> renderEngine) {
        traderGameObject = new GameObject<GameEngine3D>(new ModelInstanceHack(renderEngine.getGameEngine().assetManager.trader.scene.model), trader, this);
        renderEngine.addDynamic(traderGameObject);
//        spotLight = new SpotLightEx();
//        renderEngine.add(spotLight);
//        instance1 = new GameObject<GameEngine3D>(new ModelInstanceHack(renderEngine.getGameEngine().assetManager.cubeTrans1), trader, this);
//        renderEngine.addDynamic(instance1);
    }

    private int getColorIndex() {
        //		return trader.getName().hashCode() % NUMBER_OF_LIGHT_COLORS;
        return traderColorIndex;
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
        return !((trader.x - v.x) + (trader.z - v.z) > 1000);
    }

    public void render2D(final RenderEngine3D<GameEngine3D> renderEngine, final int index, final boolean selected) {
        final float hps = TRADER_WIDTH / 2;
        Color       color;
        if (selected) {
            color = GameEngine2D.SELECTED_COLOR;
        } else {
            color = TRADER_COLOR;
        }
        if (!trader.getTraderStatus().isGood()) {
            color = TRADER_COLOR_IS_GOOD;
        }
        final float x = translation.x;
        final float y = translation.y;
        final float z = translation.z;

        renderEngine.renderutils2Dxz.fillCircle(renderEngine.getGameEngine().getAtlasManager().planetTextureRegion, x, 0, z, hps + 1, 32, color);
        if (renderEngine.getCamera().position.y < 3000) renderEngine.renderutils2Dxz.label(renderEngine.getGameEngine().getAtlasManager().dottedLineTextureRegion, trader.x - hps, 0, (trader.z - hps), Trader2DRenderer.TRADER_WIDTH, Trader2DRenderer.TRADER_HEIGHT, TRADER_WIDTH * 1, TRADER_WIDTH * 3, renderEngine.getGameEngine().getAtlasManager().demoMidFont, color, trader.getName(), color, String.format("%.0f", trader.getCredits()), renderEngine.getGameEngine().queryCreditColor(trader.getCredits(), Trader.TRADER_START_CREDITS));

    }

    private void renderDetails(RenderEngine3D<GameEngine3D> renderEngine) {
        if (renderEngine.getCamera().frustum.pointInFrustum(translation.x, translation.y, translation.z)) {
            float rotation = trader.getManeuveringSystem().rotation;
            {
                final Matrix4 m = new Matrix4();
                //move center of text to center of trader
                m.setToTranslation(translation.x, translation.y, translation.z);
                m.rotate(Vector3.Y, rotation);
                //move to the top and back on engine
                m.translate(0, -TRADER_SIZE_Y, 0);
                //rotate into the xz layer
                m.rotate(Vector3.X, -90);
                renderEngine.renderEngine25D.setTransformMatrix(m);
            }

            TextureAtlas.AtlasRegion systemTextureRegion = renderEngine.getGameEngine().getAtlasManager().systemTextureRegion;
//            renderEngine.renderEngine25D.fillCircle(systemTextureRegion, 0, 0, TRADER_SIZE_Z, 128, new Color(.2f, .2f, .4f, 0.2f));
            renderEngine.renderEngine25D.circle(renderEngine.getGameEngine().getAtlasManager().patternCircle24, 0, 0, TRADER_SIZE_Z - .5f, 1f, new Color(.9f, .9f, .9f, .5f), 128);
            if (renderEngine.getGameEngine().getCameraZoomIndex() < 3) renderEngine.renderEngine25D.renderRose(systemTextureRegion, renderEngine.getGameEngine().getAtlasManager().modelFont, translation, TRADER_SIZE_Z / 2, -TRADER_SIZE_Y);
            BitmapFont modelFont = renderEngine.getGameEngine().getAtlasManager().modelFont;
            if (trader.destinationPlanet != null) {
                String name  = trader.getName();
                String value = String.format("%.0f credits", trader.getCredits());
                renderEngine.renderEngine25D.label(translation, rotation, systemTextureRegion, 0, TRADER_EXTERNAL_SIZE_Y / 2, -TRADER_SIZE_Z / 2, TRADER_SIZE_Z * .75f, HAlignment.RIGHT, VAlignment.TOP, 0.2f, modelFont, Color.WHITE, name, TRADER_NAME_COLOR, value, Color.YELLOW);
            }
            if (trader.destinationPlanet != null) {
                if (trader.getTraderStatus() == TraderStatus.TRADER_STATUS_SELLING) {
                    String name  = "Selling";
                    String value = String.format("from %s to %s", trader.planet.getName(), trader.destinationPlanet.getName());
                    renderEngine.renderEngine25D.label(translation, rotation, systemTextureRegion, 0, TRADER_EXTERNAL_SIZE_Y / 2, -TRADER_SIZE_Z / 2, TRADER_SIZE_Z * .75f, HAlignment.LEFT, VAlignment.TOP, 0.2f, modelFont, Color.WHITE, name, TRADER_NAME_COLOR, value, Color.YELLOW);
                } else if (trader.getTraderStatus() == TraderStatus.TRADER_STATUS_BUYING) {
                    String name  = "Buying";
                    String value = String.format("from %s", trader.destinationPlanet.getName());
                    renderEngine.renderEngine25D.label(translation, rotation, systemTextureRegion, 0, TRADER_EXTERNAL_SIZE_Y / 2, -TRADER_SIZE_Z / 2, TRADER_SIZE_Z * .75f, HAlignment.RIGHT, VAlignment.TOP, 0.2f, modelFont, Color.WHITE, name, TRADER_NAME_COLOR, value, Color.YELLOW);
                }
            }
            if (!trader.getGoodList().isEmpty()) {
                for (Good good : trader.getGoodList()) {
                    if (good.getAmount() > 0) {
                        String name  = good.type.getName();
                        String value = String.format("%d kt", good.getAmount());
                        renderEngine.renderEngine25D.label(translation, rotation, systemTextureRegion, 0, TRADER_EXTERNAL_SIZE_Y / 2, 0, TRADER_SIZE_Z * .75f, HAlignment.RIGHT, VAlignment.TOP, 0.2f, modelFont, Color.WHITE, name, TRADER_NAME_COLOR, value, Color.YELLOW);
                    }
                }
            }
            {
                String value = String.format("%.1f °", trader.getManeuveringSystem().rotation);
                renderEngine.renderEngine25D.label(translation, rotation, systemTextureRegion, 0, TRADER_EXTERNAL_SIZE_Y / 2, 0, TRADER_SIZE_Z * .75f, HAlignment.LEFT, VAlignment.TOP, 0.2f, modelFont, Color.WHITE, "Heading", TRADER_NAME_COLOR, value, Color.YELLOW);
            }
            if (trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_ALIGNING) {
                String value = String.format("%.1f °/s", trader.getThrusters().rotationSpeed);
                renderEngine.renderEngine25D.label(translation, rotation, systemTextureRegion, 0, TRADER_EXTERNAL_SIZE_Y / 2, TRADER_SIZE_Z / 2, TRADER_SIZE_Z * .75f, HAlignment.LEFT, VAlignment.BOTTOM, 0.2f, modelFont, Color.WHITE, "Aligning", TRADER_NAME_COLOR, value, Color.YELLOW);
            } else if (trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_ACCELERATING) {
                String value = String.format("%.1f m/s", trader.getEngine().getEngineSpeed() * Engine.ENGINE_TO_REALITY_FACTOR);
                renderEngine.renderEngine25D.label(translation, rotation, systemTextureRegion, 0, TRADER_EXTERNAL_SIZE_Y / 2, TRADER_SIZE_Z / 2, TRADER_SIZE_Z * .75f, HAlignment.RIGHT, VAlignment.BOTTOM, 0.2f, modelFont, Color.WHITE, "Accelerating", TRADER_NAME_COLOR, value, Color.YELLOW);
            } else if (trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_DECELERATING) {
                String value = String.format("%.1f m/s", trader.getEngine().getEngineSpeed() * Engine.ENGINE_TO_REALITY_FACTOR);
                renderEngine.renderEngine25D.label(translation, rotation, systemTextureRegion, 0, TRADER_EXTERNAL_SIZE_Y / 2, TRADER_SIZE_Z / 2, TRADER_SIZE_Z * .75f, HAlignment.RIGHT, VAlignment.BOTTOM, 0.2f, modelFont, Color.WHITE, "Decelerating", TRADER_NAME_COLOR, value, Color.YELLOW);
            }
        }
    }

    @Override
    public void renderText(final RenderEngine3D<GameEngine3D> renderEngine, final int index, final boolean selected) {
        renderTextOnTop(renderEngine, 0, 0, trader.getName().substring(2), TRADER_ENGINE_SIZE_Z);
        if (renderEngine.isDebugMode()) {
            renderTextOnTop(renderEngine, -6, -5f, "" + (int) velocity[0], 3);//x speed
            renderTextOnTop(renderEngine, 6, -5f, "" + (int) velocity[2], 3);//z speed
            renderTextOnTop(renderEngine, 0, -5f, String.valueOf(toOneDigitPrecision(synth.getGain())), 3);//bass gain
        }
        renderTextOnTop(renderEngine, -6, 6.5f, String.format("%.1f°", trader.getManeuveringSystem().rotationSpeed), 3);
        renderTextOnTop(renderEngine, 6, 6.5f, String.format("%.1f", trader.getEngine().getEngineSpeed()), 3);
        renderTextOnTop(renderEngine, 0, 6.5f, String.format("%.1f", trader.getCredits()), 3);
        renderTextOnTop(renderEngine, 0, -6.5f, trader.getTraderSubStatus().getName(), 3);
        if (trader.selected) {
            renderDetails(renderEngine);
        }
    }

//    public void render2Da(final RenderEngine2D<GameEngine3D> renderEngine, final int index, final boolean selected) {
//        final float hps = TRADER_WIDTH / 2;
//        Color       color;
//        if (selected) {
//            color = GameEngine2D.SELECTED_COLOR;
//        } else {
//            color = TRADER_COLOR;
//        }
//        if (!trader.traderStatus.isGood()) {
//            color = TRADER_COLOR_IS_GOOD;
//        }
//        final float x = translation.x;
//        final float y = translation.y;
//        final float z = translation.z;
//
//        renderEngine.fillCircle(renderEngine.getGameEngine().getAtlasManager().planetTextureRegion, x, z, hps + 1, 32, color);
//        if (renderEngine.camera.position.y < 3000) renderEngine.lable(renderEngine.getGameEngine().getAtlasManager().dottedLineTextureRegion, trader.x - hps, (trader.z - hps), Trader2DRenderer.TRADER_WIDTH, Trader2DRenderer.TRADER_HEIGHT, TRADER_WIDTH * 1, TRADER_WIDTH * 3, renderEngine.getGameEngine().getAtlasManager().demoMidFont, color, trader.getName(), color, String.format("%.0f", trader.getCredits()), renderEngine.getGameEngine().queryCreditColor(trader.getCredits(), Trader.TRADER_START_CREDITS));
//
//    }

    private void renderTextOnTop(final RenderEngine3D<GameEngine3D> renderEngine, final float dx, final float dy, final String text, final float size) {
        final BitmapFont font = renderEngine.getGameEngine().getAtlasManager().bold256Font;
        renderEngine.renderEngine25D.renderTextCenterOnTop(translation, trader.getManeuveringSystem().rotation, dx, TRADER_EXTERNAL_SIZE_Y / 2.0f + 0.2f, dy - (+TRADER_SIZE_Z / 2 - TRADER_ENGINE_SIZE_Z + (TRADER_ENGINE_SIZE_Z / 2)), font, Color.BLACK, TRADER_NAME_COLOR, text, size);
    }

    private float toOneDigitPrecision(final float value) {
        return ((float) ((int) (value * 10))) / 10;
    }

    public String toString() {
        return trader.getName();
    }

    @Override
    public void update(final RenderEngine3D<GameEngine3D> renderEngine, final long currentTime, final float timeOfDay, final int index, final boolean selected) throws Exception {
//        update(renderEngine, currentTime, index, selected);
        float realTimeDelta = Gdx.graphics.getDeltaTime();
        trader.getEngine().advanceInTime(realTimeDelta);
        trader.getManeuveringSystem().advanceInTime(realTimeDelta);
        updateTrader(renderEngine, index, selected);
        if (renderEngine.getGameEngine().launchMode == LaunchMode.development) velocityVector.update(renderEngine, trader);
        updateLights(renderEngine, currentTime);
        trader.getManeuveringSystem().updateThrusters(renderEngine, translation);
        trader.getEngine().update(renderEngine, translation);
        if (lastTransaction != trader.lastTransaction) {
            createGoods(renderEngine);
//            updateLightColor(renderEngine);
            lastTransaction = trader.lastTransaction;
        }
        updateGoods(renderEngine);
    }

//    private void updateLightColor(final RenderEngine3D<GameEngine3D> renderEngine) {
//        //TODO reuse instances
//        final ColorAttribute emissiveAttribute = ColorAttribute.createEmissive(Good3DRenderer.getColor(getColorIndex()));
//        for (final GameObject go : lightGameObjects) {
//            go.instance.materials.get(0).set(emissiveAttribute);
//        }
//    }

//    private void update(final RenderEngine3D<GameEngine3D> renderEngine, final long currentTime, final int index, final boolean selected) throws Exception {
//    }

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
//            go.instance.transform.rotateTowardDirection(direction, Vector3.Y);
            go.instance.transform.rotate(Vector3.Y, trader.getManeuveringSystem().rotation);

            final int   xEdgeSize  = (int) (TRADER_SIZE_X / Good3DRenderer.GOOD_X);
            final int   yEdgeSize  = (int) (TRADER_SIZE_Y / Good3DRenderer.GOOD_Y);
            final int   xContainer = i % xEdgeSize;
            final int   yContainer = (int) Math.floor(i / xEdgeSize) % yEdgeSize;
            final int   zContainer = (int) Math.floor(i / (xEdgeSize * yEdgeSize));
            final float x          = -TRADER_SIZE_X / 2 + Good3DRenderer.GOOD_X / 2 - .5f + xContainer * (Good3DRenderer.GOOD_X + 1);
            final float z          = /*-40*/ +16 - TRADER_SIZE_Z / 2 + Good3DRenderer.GOOD_Y / 2 + zContainer * (Good3DRenderer.GOOD_Z + 1);
            final float y          = +0.5f + TRADER_SIZE_Y / 2 - Good3DRenderer.GOOD_Z / 2 - yContainer * (Good3DRenderer.GOOD_Y + 1);


            go.instance.transform.translate(x, y, z);
            go.instance.transform.scale(Good3DRenderer.GOOD_X, Good3DRenderer.GOOD_Y, Good3DRenderer.GOOD_Z);
            go.update();
        }
    }

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
            strobeLight.update(renderEngine, translation, trader.getManeuveringSystem().rotation);
        }
    }


    private void updateTrader(final RenderEngine3D<GameEngine3D> renderEngine, final int index, final boolean selected) throws Exception {

//        if (trader.targetWaypoint != null)
        if (trader.getTraderSubStatus().isTraveling()) {
            position[0] = translation.x;
            position[1] = translation.y;
            position[2] = translation.z;
            if (trader.sourceWaypoint != null) trader.speed.set(trader.targetWaypoint.x - trader.sourceWaypoint.x, 0, trader.targetWaypoint.z - trader.sourceWaypoint.z);
            else {
                trader.speed.set(0, 0, 1);
            }

            trader.speed.nor();
            trader.speed.scl(trader.getEngine().getEngineSpeed());
            velocity[0] = trader.speed.x;
            velocity[1] = 0;
            velocity[2] = trader.speed.z;

            boolean update = false;
            for (int i = 0; i < 3; i++) {
                if (Math.abs(lastVelocity[i] - velocity[i]) > 0.001f) {
                    update = true;
                    break;
                }
            }
            if (update) {
                synth.setPositionAndVelocity(position, velocity);
                trader.communicationPartner.ttsPlayer.setPositionAndVelocity(position, velocity);
                System.arraycopy(velocity, 0, lastVelocity, 0, 3);
            }
            synth.play();
            trader.communicationPartner.ttsPlayer.play();
            translation.y = TRADER_FLIGHT_HEIGHT;
            // ---Traveling to next waypoint
            if (trader.destinationWaypointDistance != 0) {
                final float scalex = (trader.targetWaypoint.x - trader.sourceWaypoint.x);
                final float scaley = (trader.targetWaypoint.y - trader.sourceWaypoint.y);
                final float scalez = (trader.targetWaypoint.z - trader.sourceWaypoint.z);
                direction.set(scalex, scaley, scalez);
                translation.x = (trader.sourceWaypoint.x + (trader.targetWaypoint.x - trader.sourceWaypoint.x) * trader.destinationWaypointDistanceProgress / trader.destinationWaypointDistance) /*+ shift.x*/;
                translation.z = (trader.sourceWaypoint.z + (trader.targetWaypoint.z - trader.sourceWaypoint.z) * trader.destinationWaypointDistanceProgress / trader.destinationWaypointDistance) /*+ shift.z*/;
            } else {
                translation.x = trader.sourceWaypoint.x;
                translation.z = trader.sourceWaypoint.z;
            }
        } else if (trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_DOCKING_ACC || trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_DOCKING_DEC || trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_UNDOCKING_ACC || trader.getTraderSubStatus() == TraderSubStatus.TRADER_STATUS_UNDOCKING_DEC) {
            synth.play();
            trader.communicationPartner.ttsPlayer.play();
            translation.x = trader.planet.x;
            translation.y = trader.y;
            translation.z = trader.planet.z;

        } else {
            // in port
            synth.pause();
            translation.x = trader.planet.x /*- Planet3DRenderer.PLANET_ATMOSPHARE_SIZE / 2*/;
//			translation.y = trader.planet.y + TRADER_TRAVELING_HEIGHT;
            translation.y = PORT_HIGHT;
            translation.z = trader.planet.z /*- Planet3DRenderer.PLANET_ATMOSPHARE_SIZE / 2 + index * TRADER_SIZE_Z*/;
        }
        //translation.add(0, TRADER_SIZE_Y / 2, 0);
//        scaling.set(TRADER_SIZE_X, TRADER_SIZE_Y, TRADER_SIZE_Z);
//        scaling.set(1, 1, 1);
        traderGameObject.instance.transform.setToTranslation(translation);
//        spotLight.setDeg(Color.WHITE, translation, direction, 10f, 50f, 10f, null);
//        instance1.instance.transform.setToTranslation(translation);
//        instance2.instance.transform.setToTranslation(trader.sourceWaypoint.x, trader.sourceWaypoint.y, trader.sourceWaypoint.z);

        //		pole.instance.transform.setToTranslation(translation);

        if (trader.targetWaypoint != null) {
            target.set(trader.targetWaypoint.x, Planet3DRenderer.WATER_Y, trader.targetWaypoint.z);
            //			instance.instance.transform.rotateTowardTarget(target, Vector3.Y);
            traderGameObject.instance.transform.rotate(Vector3.Y, trader.getManeuveringSystem().rotation);
//            traderGameObject.instance.transform.scale(scaling.x, scaling.y, scaling.z);
//            instance1.instance.transform.rotate(yVector, trader.getManeuveringSystem().rotation);
//            instance1.instance.transform.scale(scaling.x * 17, scaling.y * 17, scaling.z * (TRADER_SIZE_Z - 32));
//            instance2.instance.transform.rotate(yVector, trader.getManeuveringSystem().rotation);
//            instance2.instance.transform.scale(1f, 1f, 4000f);
        }

//        trader.x = translation.x;
//        trader.y = translation.y;
//        trader.z = translation.z;
        traderGameObject.update();
//        instance1.update();
//        instance2.update();
        //		pole.update();
    }

}
