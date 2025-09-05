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

package de.bushnaq.abdalla.mercator.engine;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import de.bushnaq.abdalla.engine.*;
import de.bushnaq.abdalla.engine.audio.AudioEngine;
import de.bushnaq.abdalla.engine.audio.OggPlayer;
import de.bushnaq.abdalla.engine.audio.OpenAlException;
import de.bushnaq.abdalla.engine.audio.radio.Radio;
import de.bushnaq.abdalla.engine.camera.MovingCamera;
import de.bushnaq.abdalla.mercator.desktop.Context;
import de.bushnaq.abdalla.mercator.desktop.LaunchMode;
import de.bushnaq.abdalla.mercator.engine.ai.MercatorSystemPrompts;
import de.bushnaq.abdalla.mercator.engine.audio.synthesis.MercatorAudioEngine;
import de.bushnaq.abdalla.mercator.engine.camera.CameraProperties;
import de.bushnaq.abdalla.mercator.engine.camera.ZoomingCameraInputController;
import de.bushnaq.abdalla.mercator.engine.demo.Demo1;
import de.bushnaq.abdalla.mercator.engine.demo.Demo2;
import de.bushnaq.abdalla.mercator.renderer.ScreenListener;
import de.bushnaq.abdalla.mercator.renderer.ShowGood;
import de.bushnaq.abdalla.mercator.ui.Info;
import de.bushnaq.abdalla.mercator.ui.PauseScreen;
import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.land.Land;
import de.bushnaq.abdalla.mercator.universe.path.Path;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;
import de.bushnaq.abdalla.mercator.util.Debug;
import de.bushnaq.abdalla.mercator.util.TimeAccuracy;
import de.bushnaq.abdalla.mercator.util.TimeUnit;
import de.bushnaq.abdalla.mercator.util.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.model.ModelInstanceHack;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameEngine3D implements ScreenListener, ApplicationListener, InputProcessor, IGameEngine {
    public static final  float                             CAMERA_OFFSET_X                 = 10f;
    public static final  float                             CAMERA_OFFSET_Y                 = 30f;
    public static final  float                             CAMERA_OFFSET_Z                 = 50f;
    static final         Color                             DEBUG_GRID_BORDER_COLOR         = new Color(1f, 1f, 1f, 0.1f);
    static final         Color                             DEBUG_GRID_COLOR                = new Color(.0f, .0f, .0f, 0.2f);
    //	private static final String BATCH_END_DURATION = "batch.end()";
    //	private static final String DRAW_DURATION = "draw()";
//    public static final  Color                        FACTORY_COLOR                 = Color.DARK_GRAY; // 0xff000000;
//    public static final  float                        FACTORY_HEIGHT                = 1.2f;
//    public static final  float                        FACTORY_WIDTH                 = 2.4f;
    public static final  float                             FIELD_OF_VIEW_Y                 = 46f;
    public static final  int                               FONT_SIZE                       = 9;
    private static final float                             MAX_TIME_DELTA                  = 0.1f;//everything above will be ignored as a glitch
    // private static final float MAX_VOXEL_DIMENSION = 20;
//    public static final  Color                        NOT_PRODUCING_FACTORY_COLOR   = Color.RED; // 0xffFF0000;
    public static final  int                               NUMBER_OF_CELESTIAL_BODIES      = 100000;//TODO should be 100000
    private static final float                             ROTATION_SPEED                  = 1f;//degrees
    //    public static final  int                          RAYS_NUM                      = 128;
//    private static final float                        RENDER_2D_UNTIL               = 1500;
//    private static final float                        RENDER_3D_UNTIL               = 2000;
    //	private static final String RENDER_DURATION = "render()";
    //	private static final String RENDER_LIGHT = "light";
    private static final float                             SCROLL_SPEED                    = 40f;
    //    public static final  Color                        SELECTED_PLANET_COLOR         = Color.BLUE;
//    public static final  Color                        SELECTED_TRADER_COLOR         = Color.RED; // 0xffff0000;
//    public static final  float                        SIM_HEIGHT                    = 0.3f;
//    public static final  float                        SIM_WIDTH                     = 0.3f;
//    public static final  float                        SOOM_SPEED                    = 8.0f * 10;
    public static final  float                             SPACE_BETWEEN_OBJECTS           = 0.03f / Universe.WORLD_SCALE;
    public static final  Color                             TEXT_COLOR                      = Color.WHITE; // 0xffffffff;
    //	private static final Color trafficEndColor = new Color(0xffff0000);
    //	private static final Color trafficStartColor = new Color(0xff55ff55);
    //	private static final float VOXEL_SIZE = 0.5f;
    private static final Color                             TIME_MACHINE_BACKGROUND_COLOR   = new Color(0.0f, 0.0f, 0.0f, 0.9f);
    public static final  int                               TIME_MACHINE_FONT_SIZE          = 10;
    private static final Color                             TIME_MACHINE_SUB_MARKER_COLOR   = new Color(0.7f, 0.7f, 0.7f, 1.0f);
    private              float                             angle                           = -1;
    public               AssetManager                      assetManager;
    private              AtlasManager                      atlasManager;
    //    private final        GameObject<GameEngine3D>     ocean                         = null;
    public               AudioEngine                       audioEngine                     = new MercatorAudioEngine();
    private              Texture                           brdfLUT;
    @Getter
    private              ZoomingCameraInputController      camController;
    @Getter
    private              MovingCamera                      camera;
    private              OrthographicCamera                camera2D;
    private final        List<CelestialBody>               celestialBodyList               = new ArrayList<>();
    private              float                             centerRD;//camera rotation
    public               float                             centerXD;
    private              float                             centerZD;
    private              Context                           context;
    private final        IContextFactory                   contextFactory;
    private              Demo1                             demo1;
    private              Demo2                             demo2;
    //    private       float            dayAmbientIntensityB            = 1f;
//    private       float            dayAmbientIntensityG            = 1f;
//    private       float            dayAmbientIntensityR            = 1f;
//    private       float            dayShadowIntensity              = 5f;
    private              Cubemap                           diffuseCubemap;
    public               List<Color>                       distinctiveColorlist            = new ArrayList<Color>();
    public               List<Color>                       distinctiveTransparentColorlist = new ArrayList<Color>();
    //	private boolean end = false;
    //	private MercatorFrame frame;
    //	private LwjglApplicationConfiguration config;
    private              Cubemap                           environmentDayCubemap;
    private              Cubemap                           environmentNightCubemap;
    private final        Map<Integer, EnvironmentSnapshot> environmentSnapshotMap          = new HashMap<>();
    private              boolean                           followMode;
    //    private              BitmapFont                   font;
    private              boolean                           hrtfEnabled                     = true;
    private              Info                              info;
    private final        InputMultiplexer                  inputMultiplexer                = new InputMultiplexer();
    private              GameObject<GameEngine3D>          instance;//TODO
    private final        List<Label>                       labels                          = new ArrayList<>();
    private              long                              lastCameraDirty                 = 0;
    public               LaunchMode                        launchMode;
    private final        Logger                            logger                          = LoggerFactory.getLogger(this.getClass());
    public               OggPlayer                         oggPlayer;
    private final        boolean                           old                             = true;
    private              PauseScreen                       pauseScreen;
    public               RenderEngine3D<GameEngine3D>      renderEngine;
    @Getter
    @Setter
    private              boolean                           showAudioSources                = false;
    @Getter
    @Setter
    private              boolean                           showCameraInfo                  = true;
    @Getter
    @Setter
    private              boolean                           showDemo2Info                   = false;
    @Getter
    @Setter
    private              boolean                           showDepthOfFieldInfo            = true;
    @Getter
    @Setter
    private              boolean                           showFps                         = false;
    public               ShowGood                          showGood                        = ShowGood.Name;
    @Getter
    @Setter
    private              boolean                           showInfo                        = false;
    @Getter
    @Setter
    private              boolean                           showTime                        = true;
    @Getter
    @Setter
    private              boolean                           showUniverseTime                = false;
    private              Cubemap                           specularCubemap;
    private              Stage                             stage;
    private              StringBuilder                     stringBuilder;
    private              Subtitles                         subtitles;
    //    Vector3 sunPosition = new Vector3();
    private              boolean                           takeScreenShot;
    private              float                             timeOfDay;
    //	private ModelInstance uberModelInstance;
    @Getter
    public final         Universe                          universe;
    private              boolean                           vsyncEnabled                    = true;

    public GameEngine3D(final IContextFactory contextFactory, final Universe universe, final LaunchMode launchMode) throws Exception {
        this.contextFactory = contextFactory;
        this.universe       = universe;
        this.launchMode     = launchMode;
        universe.setScreenListener(this);
    }

    public Color amountColor(final Good good) {
        return availabilityColor(good.getAmount(), good.getMaxAmount());
    }

    public Color availabilityColor(final float amount, final float maxAmount) {
        if (amount >= 0.5 * maxAmount) {
            return Color.GREEN;
        } else if (amount >= 0.3 * maxAmount) {
            return Color.ORANGE;
        } else {
            return GameEngine2D.DARK_RED_COLOR;
        }
    }

    @Override
    public void create() {
        try {
            if (context == null)// ios
            {
                context = (Context) contextFactory.create();
            }
            showFps = context.getShowFpsProperty();

            createCamera();
            atlasManager = new AtlasManager();
            atlasManager.init();
            initColors();
            renderEngine = new RenderEngine3D<GameEngine3D>(context, this, camera, camera2D, getAtlasManager().menuFont, getAtlasManager().menuBoldFont, getAtlasManager().systemTextureRegion);
            renderEngine.setSceneBoxMin(new Vector3(-500, -500, -500));
            renderEngine.setSceneBoxMax(new Vector3(1000, 0, 1000));
            renderEngine.getWater().setPresent(false);
//            renderEngine.getWater().setWaterLevel(-10f);
//            renderEngine.getWater().setTiling(universe.size * 2 * 4 * 2 * 4 / Universe.WORLD_SCALE);
//            renderEngine.getWater().setWaveStrength(0.01f / Universe.WORLD_SCALE);
//            renderEngine.getWater().setWaveSpeed(0.01f);
//            renderEngine.getWater().setRefractiveMultiplicator(1f);
//            renderEngine.getMirror().setPresent(false);
//            renderEngine.getMirror().setMirrorLevel(-12f);
//            renderEngine.getMirror().setReflectivity(0.2f);
//            renderEngine.setReflectionClippingPlane(-(context.getWaterLevel() - 2));
//            renderEngine.setRefractionClippingPlane((context.getWaterLevel() - 2));
            renderEngine.getFog().setEnabled(true);
            renderEngine.getFog().setColor(Color.BLACK);
            renderEngine.getFog().setBeginDistance(2000f);
            renderEngine.getFog().setFullDistance(3000f);

            renderEngine.setSkyBox(true);
            renderEngine.setDayAmbientLight(1f, 1f, 1f, 3f);
            renderEngine.setNightAmbientLight(.8f, .8f, .8f, 10f);
            renderEngine.setAlwaysDay(true);
            renderEngine.setDynamicDayTime(false);
            renderEngine.setShadowEnabled(true);
            renderEngine.getShadowLight().setColor(Color.WHITE);
            renderEngine.setFixedShadowDirection(true);
            renderEngine.getDepthOfFieldEffect().setEnabled(true);
//            renderEngine.getSsaoEffect().setEnabled(false);
//            renderEngine.getSsaoCombineEffect().setEnabled(false);
            renderEngine.setGammaCorrected(true);

            createInputProcessor(this, this);
            createLogo();

            try {
                context.setSelected(renderEngine.getProfiler(), false);
            } catch (final Exception e) {
                logger.error(e.getMessage(), e);
            }

            assetManager = new AssetManager(universe);
            assetManager.create();
            createEnvironment();
            createStage();
            pauseScreen = new PauseScreen(this, atlasManager);

            audioEngine.create(AtlasManager.getAssetsFolderName());
            audioEngine.enableHrtf(0);
            audioEngine.radio.loadResource(this.getClass());
            MercatorSystemPrompts.register(getRadio());//register all ai prompts to the radioTTS
//            renderAllTTSStrings();
//            audioEngine.radioTTS.loadAudio();
//            audioEngine.radioTTS.test();


//            createStone();
            createTraders();
//			createRing();
//            createWater();
            createPlanets();
//			createLand();
            createJumpGates();

            demo1     = new Demo1(this, launchMode);
            demo2     = new Demo2(this, launchMode);
            subtitles = new Subtitles(this);

            if (universe.selected != null) {
                universe.setSelected(universe.selected, true);
                followMode = true;
            }
            universe.updateSelectedPlanet();
        } catch (final Exception e) {
            Gdx.app.log(this.getClass().getSimpleName(), e.getMessage(), e);
            System.exit(1);
        }
    }

    private void createCamera() throws Exception {
        camera = new MovingCamera(FIELD_OF_VIEW_Y, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        Planet planet = universe.planetList.findBusyCenterPlanet();

        Planet planet = universe.planetList.findByName(Debug.getFilterPlanet());

//        Planet planet = universe.traderList.findByName(Debug.getFilterTrader()).planet;
//        Planet planet = universe.planetList.findByName(Debug.getFilterPlanet());
        if (planet == null && !universe.planetList.isEmpty()) planet = universe.planetList.get(0);
        Vector3 lookat;
        if (planet != null) lookat = new Vector3(planet.x, 0, planet.z);
        else lookat = new Vector3(0, 0, 0);
        camera.position.set(lookat.x + CAMERA_OFFSET_X / Universe.WORLD_SCALE, lookat.y + CAMERA_OFFSET_Y / Universe.WORLD_SCALE, lookat.z + CAMERA_OFFSET_Z / Universe.WORLD_SCALE);
        camera.up.set(0, 1, 0);
        camera.lookAt(lookat);
        camera.near = 2f;
        camera.far  = 8000f;
        camera.update();
        camera.setDirty(true);
        camera2D = new OrthographicCamera();
    }

    private void createEnvironment() {
        // setup IBL (image based lighting)
        if (renderEngine.isPbr()) {
            updateEnvironment(timeOfDay);
//            setupImageBasedLightingByFaceNames("clouds", "jpg", "jpg", "jpg", 10);
//            if (renderEngine.isSkyBox()) {
//                renderEngine.setDaySkyBox(new SceneSkybox(environmentNightCubemap));
//                renderEngine.setNightSkyBox(new SceneSkybox(environmentNightCubemap));
//            }
//
//            renderEngine.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));
//            renderEngine.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
//            renderEngine.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
//            renderEngine.environment.set(new PBRFloatAttribute(PBRFloatAttribute.ShadowBias, .001f));
//            setupImageBasedLighting(timeOfDay);
//            updateEnvironment(timeOfDay);
        } else {
        }
//        if (renderEngine.isPbr()) {
//            setupImageBasedLighting(0f);
//            renderEngine.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));
//            renderEngine.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
//            renderEngine.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
//            renderEngine.environment.set(new PBRFloatAttribute(PBRFloatAttribute.ShadowBias, 0f));
//        } else {
//        }
    }

    private void createImageBasedLighting(float timeOfDay) {
        if (old) {
            if (brdfLUT == null)
            // setup quick IBL (image based lighting)
            {
                brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));
                Integer             index               = (int) (angle);
                EnvironmentSnapshot environmentSnapshot = environmentSnapshotMap.get(index);
                if (environmentSnapshot == null) {
                    long time = System.currentTimeMillis();
                    logger.info("----------------------------------------------------------------------------------");
                    logger.info(String.format("setupImageBasedLighting = %d", index));
                    DirectionalLightEx sun = new DirectionalLightEx();
                    sun.direction.set(renderEngine.getShadowLight().direction.x, renderEngine.getShadowLight().direction.y, renderEngine.getShadowLight().direction.z).nor();
                    sun.color.set(Color.WHITE);
                    myIBLBuilder ibl = new myIBLBuilder("app/assets/textures/space/");
                    {
                        celestialBodyList.clear();
                        int numberOfBodies;
                        if (index == -1) numberOfBodies = 10000;
                        else numberOfBodies = NUMBER_OF_CELESTIAL_BODIES;
                        logger.info(String.format("numberOfBodies=%dk", numberOfBodies / 1000));
                        CelestialBody star = new CelestialBody(sun.direction, sun.color, 10000f);
                        celestialBodyList.add(star);
                        for (int i = 0; i < numberOfBodies; i++) {
                            celestialBodyList.add(new CelestialBody());
                        }
                    }

                    celestialBodyList.getFirst().getDirection().set(sun.direction);
                    for (CelestialBody cb : celestialBodyList) {
                        myIBLBuilder.Light light = new myIBLBuilder.Light();
                        light.direction.set(cb.getDirection());
                        light.color.set(cb.getColor());
                        light.exponent = cb.getExponent();
                        ibl.lights.add(light);
                    }

                    float tint = 0.0f;
                    ibl.nearGroundColor.set(tint, tint, tint, 1.0F);
                    ibl.farGroundColor.set(tint, tint, tint, 1.0F);
                    ibl.nearSkyColor.set(tint, tint, tint, 1.0F);
                    ibl.farSkyColor.set(tint, tint, tint, 1.0F);
                    Cubemap environmentCubemap = ibl.buildEnvMap(1024 * 4, renderEngine.batch2D, atlasManager.bold256Font);
                    tint = 0.3f;//ambience
                    ibl.nearGroundColor.set(tint, tint, tint, 1.0F);
                    ibl.farGroundColor.set(tint, tint, tint, 1.0F);
                    ibl.nearSkyColor.set(tint, tint, tint, 1.0F);
                    ibl.farSkyColor.set(tint, tint, tint, 1.0F);
                    Cubemap irradianceMap = ibl.buildIrradianceMap(256 * 4, renderEngine.batch2D, atlasManager.bold256Font);
                    tint                       = 0.0f;//metallic reflection
                    ibl.lights.get(0).exponent = 10;
                    ibl.nearGroundColor.set(tint, tint, tint, 1.0F);
                    ibl.farGroundColor.set(tint, tint, tint, 1.0F);
                    ibl.nearSkyColor.set(tint, tint, tint, 1.0F);
                    ibl.farSkyColor.set(tint, tint, tint, 1.0F);
                    Cubemap radianceMap = ibl.buildRadianceMap(12, renderEngine.batch2D, atlasManager.bold256Font);
                    environmentSnapshot = new EnvironmentSnapshot(environmentCubemap, irradianceMap, radianceMap);
                    environmentSnapshotMap.put(index, environmentSnapshot);
                    ibl.dispose();
                    logger.info("----------------------------------------------------------------------------------");

                    long   durationMs = System.currentTimeMillis() - time;
                    String formatted  = DurationFormatUtils.formatDuration(durationMs, "H'h' m'm' s's' SSS'ms'");
                    logger.info(String.format("setupImageBasedLighting done in %s", formatted));
                }
                renderEngine.setDaySkyBox(environmentSnapshot.getEnvironmentCubemap());
                renderEngine.setNightSkyBox(environmentSnapshot.getEnvironmentCubemap());
                diffuseCubemap  = environmentSnapshot.getIrradianceMap();
                specularCubemap = environmentSnapshot.getRadianceMap();
            }
        } else {
            brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

            DirectionalLightEx light = new DirectionalLightEx();
            light.direction.set(1, -1, 1).nor();
            light.color.set(Color.WHITE);
            IBLBuilder iblBuilder         = IBLBuilder.createOutdoor(light);
            Cubemap    environmentCubemap = iblBuilder.buildEnvMap(1024);
            renderEngine.setDaySkyBox(new SceneSkybox(environmentCubemap));
            renderEngine.setNightSkyBox(new SceneSkybox(environmentCubemap));
            diffuseCubemap  = iblBuilder.buildIrradianceMap(256);
            specularCubemap = iblBuilder.buildRadianceMap(10);
            iblBuilder.dispose();

        }
        renderEngine.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));
        renderEngine.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        renderEngine.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        renderEngine.environment.set(new PBRFloatAttribute(PBRFloatAttribute.ShadowBias, .002f));
    }

    private void createInputProcessor(final InputProcessor inputProcessor, GameEngine3D gameEngine) throws Exception {
        camController                = new ZoomingCameraInputController(camera, gameEngine);
        camController.scrollFactor   = -0.1f;
        camController.translateUnits = 1000f;
        inputMultiplexer.addProcessor(inputProcessor);
        inputMultiplexer.addProcessor(camController);
        Gdx.input.setInputProcessor(inputMultiplexer);
        camController.setTargetZoomIndex(2);
        camController.setZoomIndex(2);
        camController.update(true);
    }

    private void createJumpGates() {
//        if (launchMode == LaunchMode.development)
        {
            for (final Path path : universe.pathList) {
                path.get3DRenderer().create(path.source.x, path.source.y, path.source.z, renderEngine);
            }
        }
    }

    private void createLand() {
        for (final Land land : universe.landList) {
            land.get3DRenderer().create(renderEngine);
        }
    }

    private void createLogo() {
        String applicationName = "Mercator";
        int    x               = 100;
        int    y               = Gdx.graphics.getHeight() - 100;
        Text2D logo            = new Text2D(applicationName, x, y, new Color(1f, 1f, 1f, .4f), renderEngine.getGameEngine().getAtlasManager().logoFont);
        renderEngine.add(logo);
        try {
            String            v      = renderEngine.getGameEngine().context.getAppVersion();
            final GlyphLayout layout = new GlyphLayout();
            layout.setText(renderEngine.getGameEngine().getAtlasManager().logoFont, applicationName);
            float h1 = layout.height;
            float w1 = layout.width;
            layout.setText(renderEngine.getGameEngine().getAtlasManager().versionFont, v);
            float  h2      = layout.height;
            Text2D version = new Text2D(v, (int) (x + w1), y - (int) (h1 - h2), Color.WHITE, renderEngine.getGameEngine().getAtlasManager().versionFont);
            renderEngine.add(version);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void createPlanets() {
        for (final Planet planet : universe.planetList) {
            planet.get3DRenderer().create(renderEngine);
        }
    }

    private void createRing() {
        universe.ring.get3DRenderer().create(renderEngine);
    }

    private void createStage() throws Exception {
        info = new Info(renderEngine, getAtlasManager(), camera2D, renderEngine.renderEngine2D.batch, inputMultiplexer);
        info.createStage();
        final int height = 15;
        stage = new Stage();
        for (int i = 0; i < 8; i++) {
            final RichLabel label = new RichLabel(" ", new Label.LabelStyle(getAtlasManager().menuFont, Color.WHITE), getAtlasManager().systemTextureRegion);
            label.setBackgroundColor(new Color(0f, 0f, 0f, 0.7f)); // Semi-transparent black
            label.setPadding(6f, 3f); // Horizontal and vertical padding
            label.setPosition(0, Gdx.graphics.getHeight() - (i + 1) * height);
            stage.addActor(label);
            labels.add(label);
        }
        stringBuilder = new StringBuilder();
    }

    private void createStone() {
        {
            instance = new GameObject<>(new ModelInstanceHack(renderEngine.getGameEngine().assetManager.cubeModel), null);
            instance.instance.transform.setToTranslationAndScaling(0, 0, 0, 16, 16, 16);
            instance.update();
            renderEngine.addStatic(instance);
        }
        {
            instance = new GameObject<>(new ModelInstanceHack(renderEngine.getGameEngine().assetManager.cubeModel), null);
            instance.instance.transform.setToTranslationAndScaling(0, 0, 0, 128, 1, 128);
            instance.update();
            renderEngine.addStatic(instance);
        }
    }

    private void createTraders() {
        for (final Planet planet : assetManager.universe.planetList) {
            for (final Trader trader : planet.traderList) {
                trader.get3DRenderer().create(renderEngine);
            }
        }
    }

//    private void createWater() {
//        final float delta = (universe.size + 1) * Planet.PLANET_DISTANCE * 2;
//        //water
//        if (renderEngine.getWater().isPresent()) {
//            //bottom
//            {
//                final GameObject<GameEngine3D> sectorInstance = new GameObject<>(new ModelInstanceHack(assetManager.sector), null);
//                sectorInstance.instance.transform.setToTranslationAndScaling(0, Planet3DRenderer.SECTOR_Y, 0, delta, 8, delta);
//                sectorInstance.update();
//                renderEngine.addStatic(sectorInstance);
//
//            }
//            {
//                final GameObject<GameEngine3D> sectorInstance = new GameObject<>(new ModelInstanceHack(assetManager.waterModel), null);
//                sectorInstance.instance.transform.setToTranslationAndScaling(0, Planet3DRenderer.WATER_Y, 0, delta, 1, delta);
//                sectorInstance.update();
//                renderEngine.addStatic(sectorInstance);
//            }
//        }
//        if (renderEngine.getMirror().isPresent()) {
//            final GameObject<GameEngine3D> sectorInstance = new GameObject<>(new ModelInstanceHack(assetManager.mirrorModel), null);
//            sectorInstance.instance.transform.setToTranslationAndScaling(0, Planet3DRenderer.MIRROR_Y, 0, delta, 1, delta);
//            sectorInstance.update();
//            renderEngine.addStatic(sectorInstance);
//        }
//    }

    @Override
    public void dispose() {
        try {
            audioEngine.dispose();
            //		myCanvas.stop();
            assetManager.dispose();
//            font.dispose();
            //		synchronized (desktopLauncher) {
            //			desktopLauncher.notify();
            //		}
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
        }
    }

    private void drawDebugGrid() {
        for (int y = -universe.size; y < universe.size; y++) {
            for (int x = -universe.size; x < universe.size; x++) {
                {
                    final float tx1 = x * Planet.PLANET_DISTANCE - Planet.PLANET_DISTANCE / 2 + 1;
                    final float tz1 = y * Planet.PLANET_DISTANCE - Planet.PLANET_DISTANCE / 2 + 1;
                    final float tx2 = x * Planet.PLANET_DISTANCE + Planet.PLANET_DISTANCE - Planet.PLANET_DISTANCE / 2 - 2;
                    final float tz2 = y * Planet.PLANET_DISTANCE + Planet.PLANET_DISTANCE - Planet.PLANET_DISTANCE / 2 - 2;
                    renderEngine.renderutils2Dxz.bar(atlasManager.systemTextureRegion, tx1, 0, tz1, tx2, 0, tz2, DEBUG_GRID_BORDER_COLOR);
                }
//                {
//                    final float tx1 = x * Planet.PLANET_DISTANCE - Planet.PLANET_DISTANCE / 2/* + Planet3DRenderer.PLANET_BORDER*/;
//                    final float tz1 = y * Planet.PLANET_DISTANCE - Planet.PLANET_DISTANCE / 2/* + Planet3DRenderer.PLANET_BORDER*/;
//                    final float tx2 = x * Planet.PLANET_DISTANCE + Planet.PLANET_DISTANCE - Planet.PLANET_DISTANCE / 2/* - Planet3DRenderer.PLANET_BORDER*/ - 1;
//                    final float tz2 = y * Planet.PLANET_DISTANCE + Planet.PLANET_DISTANCE - Planet.PLANET_DISTANCE / 2/* - Planet3DRenderer.PLANET_BORDER*/ - 1;
//                    renderEngine.renderutils2Dxz.bar(atlasManager.systemTextureRegion, tx1, 0, tz1, tx2, 0, tz2, DEBUG_GRID_COLOR);
//                }
            }
        }
    }

    private void exit() {
        Gdx.app.exit();
    }


//    public int getMaxFramesPerSecond() {
//        return maxFramesPerSecond;
//    }

    public float getAngle() {
        return angle;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    //	Sector3DRenderer sector3DRenderer = new Sector3DRenderer();
    //
    //	private void drawSectors() {
    //		for (int y = 0; y < universe.size * 2; y++) {
    //			for (int x = 0; x < universe.size * 2; x++) {
    //				Sector sector = universe.sectorList.sectorMap[x][y];
    //				if (sector != null) {
    //					sector3DRenderer.render(x - renderMaster.universe.size, y - renderMaster.universe.size, sector,
    //							renderMaster, 0, false);
    //				}
    //			}
    //		}
    //	}

    //	private void drawStatistics() {
    //		float x = universe.size * Planet3DRenderer.PLANET_DISTANCE;
    //		float y = universe.size * Planet3DRenderer.PLANET_DISTANCE;
    //		int delta = FONT_SIZE;
    //		for (String statisticName : universe.timeStatisticManager.getSet()) {
    //			TimeStatistic statistic = universe.timeStatisticManager.getStatistic(statisticName);
    //			renderMaster.text(x, y, Color.WHITE, TEXT_COLOR, String.format("%s %dms %dms %dms %dms", statisticName,
    //					statistic.lastTime, statistic.minTime, statistic.averageTime, statistic.maxTime));
    //			y += delta;
    //		}
    //		renderMaster.text(x, y, Color.WHITE, TEXT_COLOR, String.format("%dps", myCanvas.getGraphics().getFramesPerSecond()));
    //	}

    //	private void drawGoodTraffic() {
    //		shapeRenderer.setProjectionMatrix(camera.combined);
    //		shapeRenderer.begin(ShapeType.Filled);
    //		for (Planet planet : universe.planetList) {
    //			drawGoodTraffic(planet);
    //		}
    //		shapeRenderer.end();
    //	}

    //	private void drawGoodTraffic( Planet planet ) { int planetX = planet.x *
    //	  PLANET_DISTANCE; int planetY = planet.y * PLANET_DISTANCE; int color =
    //	  Color.white.getRGB(); // ---Find max int maxAmount = 32; for ( Planet
    //	  fromPlanet : universe.planetList ) { int amount =
    //	  planet.statisticManager.getAmount( fromPlanet.name ); if ( amount > maxAmount
    //	  ) maxAmount = amount; }
    //
    //	  Stroke defaultStroke = graphics.getStroke(); float dash[] = { 1.0f };
    //	 BasicStroke stroke = new BasicStroke( 1.7f, BasicStroke.CAP_SQUARE,
    //	  BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f ); graphics.setStroke( stroke );
    //
    //	  for ( Planet fromPlanet : universe.planetList ) { if ( planet != fromPlanet )
    //	  { if ( maxAmount != 0 ) { int thickness = (
    //	  planet.statisticManager.getAmount( fromPlanet.name ) * 32 ) / maxAmount; if (
    //	  thickness != 0 ) { int fromPlanetX = fromPlanet.x * PLANET_DISTANCE; int
    //	  fromPlanetY = fromPlanet.y * PLANET_DISTANCE; if ( universe.selectedPlanet ==
    //	  planet || universe.selectedPlanet == fromPlanet ) { lineBar( fromPlanetX,
    //	  fromPlanetY, planetX, planetY, trafficStartColor, trafficEndColor, thickness
    //	  ); } else {
    //
    //	  // lineBar( fromPlanetX, fromPlanetY, planetX, // planetY,
    //	  RcColor.colorMerger( Color.green, // Color.white, 0.7f ),
    //	  RcColor.colorMerger( // Color.red, Color.white, 0.7f ), thickness ); } } } }
    //	  }
    //
    //	  graphics.setStroke( defaultStroke ); }

    public AtlasManager getAtlasManager() {
        return atlasManager;
    }

    @Override
    public AudioEngine getAudioEngine() {
        return audioEngine;
    }

    /*
     * private void drawPlanetGraph( Planet planet ) { int pd = PLANET_DISTANCE - 2;
     * int hpd = PLANET_DISTANCE / 2; int qpd = PLANET_DISTANCE / 4 - 2; int planetX
     * = planet.x * PLANET_DISTANCE - hpd; int planetY = planet.y * PLANET_DISTANCE
     * - hpd; int x[] = new int[planet.creditHistory.size()]; int y[] = new
     * int[planet.creditHistory.size()]; boolean draw = true; // ---Find max int
     * maxY = Planet.PLANET_START_CREDITS; for ( int i = 0; i <
     * planet.creditHistory.size(); i++ ) { int ty = planet.creditHistory.get( i );
     * if ( ty > maxY ) maxY = ty; } for ( int i = 0; i <
     * planet.creditHistory.size(); i++ ) { x[i] = transformX( planetX + ( i * pd )
     * / Planet.CREDIT_HISTORY_SIZE ); y[i] = transformY( planetY + pd - ( (
     * planet.creditHistory.get( i ) * qpd ) / maxY ) ); } graphics.setColor(
     * queryCreditColor( planet ) ); if ( draw ) { Stroke defaultStroke =
     * graphics.getStroke(); float dash[] = { 1.0f }; BasicStroke stroke = new
     * BasicStroke( 1.7f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f,
     * dash, 0.0f ); graphics.setStroke( stroke ); graphics.drawPolyline( x, y,
     * planet.creditHistory.size() ); graphics.setStroke( defaultStroke ); } }
     */

    //	public Canvas getCanvas() {
    //		return myCanvas.getCanvas();
    //	}

    public int getCameraZoomIndex() {
        return camController.getZoomIndex();
    }

    public int getCameraZoomThreshold() {
        return camController.getCameraZoomThreshold();
    }

    public Radio getRadio() {
        return audioEngine.radio;
    }

    @Override
    public RenderEngine3D<?> getRenderEngine() {
        return renderEngine;
    }

    @Override
    public ISubtitles getSubtitles() {
        return subtitles;
    }

    private void initColors() {
        {
            distinctiveColorlist.add(new Color(0.2f, 0.2f, 0.2f, 0.5f));
            final float factor = (universe.sectorList.size() / 8) / 2.0f;
            final int   c      = (int) Math.ceil(universe.sectorList.size() / 6.0);
            for (float i = 0; i < Math.ceil(universe.sectorList.size() / 6.0); i++) {
                final float low = 1.0f - (i + 1) * factor;
                //				System.out.println(low * 255);
                final float high  = 1.0f - i * factor;
                final float alpha = 1.f;
                // distinctiveColorlist.add( new Color( high, high, high, alpha ) );
                distinctiveColorlist.add(new Color(high, high, low, alpha));
                distinctiveColorlist.add(new Color(high, low, high, alpha));
                distinctiveColorlist.add(new Color(low, high, high, alpha));
                distinctiveColorlist.add(new Color(high, low, low, alpha));
                distinctiveColorlist.add(new Color(low, low, high, alpha));
                distinctiveColorlist.add(new Color(low, high, low, alpha));
                // distinctiveColorlist.add( new Color( low, high, low, alpha ) );
                // distinctiveColorlist.add( new Color( low, low, high, alpha ) );
                // distinctiveColorlist.add( new Color( low, 1.0f, 1.0f, alpha ) );
                // distinctiveColorlist.add( new Color( 1.0f, low, 1.0f, alpha ) );
                // distinctiveColorlist.add( new Color( 1.0f, 1.0f, low, alpha ) );
                // distinctiveColorlist.add( new Color( low, low, low, alpha ) );
            }
            // distinctiveColorArray = distinctiveColorlist.toArray( new Color[0] );
        }
        distinctiveTransparentColorlist.add(new Color(0.2f, 0.2f, 0.2f, 0.5f));
        final float factor = (universe.sectorList.size() / 8) / 2.0f;
        final int   c      = (int) Math.ceil(universe.sectorList.size() / 6.0);
        for (float i = 0; i < Math.ceil(universe.sectorList.size() / 6.0); i++) {
            final float low = 1.0f - (i + 1) * factor;
            //			System.out.println(low * 255);
            final float high  = 1.0f - i * factor;
            final float alpha = 0.4f;
            // distinctiveColorlist.add( new Color( high, high, high, alpha ) );
            distinctiveTransparentColorlist.add(new Color(high, high, low, alpha));
            distinctiveTransparentColorlist.add(new Color(high, low, high, alpha));
            distinctiveTransparentColorlist.add(new Color(low, high, high, alpha));
            distinctiveTransparentColorlist.add(new Color(high, low, low, alpha));
            distinctiveTransparentColorlist.add(new Color(low, low, high, alpha));
            distinctiveTransparentColorlist.add(new Color(low, high, low, alpha));
            // distinctiveColorlist.add( new Color( low, high, low, alpha ) );
            // distinctiveColorlist.add( new Color( low, low, high, alpha ) );
            // distinctiveColorlist.add( new Color( low, 1.0f, 1.0f, alpha ) );
            // distinctiveColorlist.add( new Color( 1.0f, low, 1.0f, alpha ) );
            // distinctiveColorlist.add( new Color( 1.0f, 1.0f, low, alpha ) );
            // distinctiveColorlist.add( new Color( low, low, low, alpha ) );
        }
        // distinctiveColorArray = distinctiveColorlist.toArray( new Color[0] );
    }

    @Override
    public boolean keyDown(final int keycode) {
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.LEFT:
                centerXD = -SCROLL_SPEED * camera.position.y / 1000f; // reduce speed with distance to center
                return true;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                centerXD = SCROLL_SPEED * camera.position.y / 1000f;
                return true;
            case Input.Keys.W:
            case Input.Keys.UP:
                centerZD = SCROLL_SPEED * camera.position.y / 1000f;
                return true;
            case Input.Keys.S:
            case Input.Keys.DOWN:
                centerZD = -SCROLL_SPEED * camera.position.y / 1000f;
                return true;
            case Input.Keys.Q:
                centerRD = ROTATION_SPEED;
                return true;
            case Input.Keys.E:
                centerRD = -ROTATION_SPEED;
                return true;
            case Input.Keys.ESCAPE:
                exit();
                return true;
            case Input.Keys.PAUSE:
            case Input.Keys.SPACE:
                assetManager.universe.setEnableTime(!assetManager.universe.isEnableTime());
                return true;
            case Input.Keys.PRINT_SCREEN:
                queueScreenshot();
                return true;
            case Input.Keys.TAB:
                try {
                    universe.setSelected(renderEngine.getProfiler(), false);
                } catch (final Exception e) {
                    logger.error(e.getMessage(), e);
                }
                return true;

            case Input.Keys.F1:
                renderEngine.setGammaCorrected(!renderEngine.isGammaCorrected());
                if (renderEngine.isGammaCorrected()) logger.info("gamma correction on");
                else logger.info("gamma correction off");
                return true;
            case Input.Keys.F2:
                renderEngine.getDepthOfFieldEffect().setEnabled(!renderEngine.getDepthOfFieldEffect().isEnabled());
                if (renderEngine.getDepthOfFieldEffect().isEnabled()) logger.info("depth of field on");
                else logger.info("depth of field off");
                return true;
            case Input.Keys.F3:
                renderEngine.setRenderBokeh(!renderEngine.isRenderBokeh());
                if (renderEngine.isRenderBokeh()) logger.info("render bokeh on");
                else logger.info("render bokeh off");
                return true;
            case Input.Keys.F4:
                renderEngine.getSsaoEffect().setEnabled(!renderEngine.getSsaoEffect().isEnabled());
                renderEngine.getSsaoCombineEffect().setEnabled(!renderEngine.getSsaoCombineEffect().isEnabled());
                if (renderEngine.getSsaoEffect().isEnabled()) logger.info("ssao on");
                else logger.info("ssao off");
                return true;

            case Input.Keys.F5:
                renderEngine.setAlwaysDay(!renderEngine.isAlwaysDay());
                return true;
            case Input.Keys.F6:
                switch (launchMode) {
                    case normal:
                    case development:
                        launchMode = LaunchMode.demo1;
                        try {
                            demo1.startDemoMode();
                        } catch (OpenAlException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case demo1:
                        launchMode = LaunchMode.demo2;
                        try {
                            demo2.startDemoMode();
                        } catch (OpenAlException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case demo2:
                        launchMode = LaunchMode.normal;
                        break;
                }
                return true;

            case Input.Keys.F9:
                renderEngine.setShowGraphs(!renderEngine.isShowGraphs());
                if (renderEngine.isShowGraphs()) logger.info("graphs are on");
                else logger.info("graphs are off");
                return true;
            case Input.Keys.F10:
                renderEngine.setDebugMode(!renderEngine.isDebugMode());
                if (renderEngine.isDebugMode()) logger.info("debug mode on");
                else logger.info("debug mode off");
                return true;

            case Input.Keys.F:
                followMode = !followMode;
                return true;
            case Input.Keys.H:
                try {
                    if (hrtfEnabled) {
                        audioEngine.disableHrtf(0);
                        hrtfEnabled = false;
                    } else {
                        audioEngine.enableHrtf(0);
                        hrtfEnabled = true;
                    }
                } catch (final OpenAlException e) {
                    logger.error(e.getMessage(), e);
                }
                return true;
            case Input.Keys.I:
                setShowInfo(!isShowInfo());
                return true;
            case Input.Keys.V:
                vsyncEnabled = !vsyncEnabled;
                Gdx.graphics.setVSync(vsyncEnabled);
                return true;
        }
        return false;

    }

    @Override
    public boolean keyTyped(final char character) {
        return false;
    }

    @Override
    public boolean keyUp(final int keycode) {
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.D:
            case Input.Keys.LEFT:
            case Input.Keys.RIGHT:
                centerXD = 0;
                universe.updateSelectedPlanet();
                return true;
            case Input.Keys.W:
            case Input.Keys.S:
            case Input.Keys.UP:
            case Input.Keys.DOWN:
                centerZD = 0;
                universe.updateSelectedPlanet();
                return true;
            case Input.Keys.Q:
            case Input.Keys.E:
                centerRD = 0;
                return true;
            case Input.Keys.C:
                getCamera().setDirty(true);
                break;

        }
        return false;
    }

    @Override
    public boolean mouseMoved(final int screenX, final int screenY) {
        return false;
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    public Color priceColor(final Good good) {
        return availabilityColor(good.price, good.getMaxPrice());
    }

    public Color queryCreditColor(final float creadits, final float startCredits) {
        if (creadits < startCredits / 2) {
            return Color.RED;
        } else if (creadits < startCredits) {
            return Color.ORANGE;
        } else if (creadits > startCredits * 2) {
            return Color.GREEN;
        } else {
            return Color.WHITE;
        }
    }

    public void queueScreenshot() {
        takeScreenShot = true;
    }

    @Override
    public void render() {
        try {
            renderEngine.cpuGraph.begin();
            universe.advanceInTime();
            if (renderEngine.getProfiler().isEnabled()) {
                renderEngine.getProfiler().reset();// reset on each frame
            }
            render(universe.currentTime);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            System.exit(0);
        }
    }

    private void render(final long currentTime) throws Exception {
        final float deltaTime = Gdx.graphics.getDeltaTime();


        if (followMode && universe.selected instanceof Trader) {
            if (System.currentTimeMillis() - lastCameraDirty > 100) {
                universe.setSelected(universe.selected, true);
                lastCameraDirty = System.currentTimeMillis();
            } else {
                universe.setSelected(universe.selected, false);
            }
        }
        // must be called after moving the camera
        camController.update();
        renderEngine.updateCameraXZ(centerXD, 0f, centerZD);
        renderEngine.updateCameraRotationY(centerRD);
        renderEngine.render2D = getCameraZoomIndex() > getCameraZoomThreshold();
        renderEngine.render3D = getCameraZoomIndex() <= getCameraZoomThreshold();
//        centerXD = 0;
        updateDepthOfFieldFocusDistance();
//        if (camera.position.y > 1000) {
//            renderEngine.getFog().setBeginDistance(camera.position.y + 100);
//            renderEngine.getFog().setBeginDistance(camera.position.y + 1000);
//        } else {
//            renderEngine.getFog().setBeginDistance(3000);
//            renderEngine.getFog().setFullDistance(5000);
//        }

//        {
//            final float[] positionArray = new float[]{1f, 1f, 1f};
//            final float[] velocityArray = new float[]{0f, 0f, 0f};
//            oggPlayer.setPositionAndVelocity(positionArray, velocityArray);
//            if ((currentTime / 1000) * 1000 == currentTime)
//                logger.info(String.format("%f %f %f", oggPlayer.getPosition().x, oggPlayer.getPosition().y, oggPlayer.getPosition().z));
//        }
        if (deltaTime < MAX_TIME_DELTA) {
            updateJumpGates(currentTime);
            updatePlanets(currentTime);
            updateGoods(currentTime);
            updateTraders(currentTime);
            audioEngine.begin(camera, universe.isEnableTime());
            audioEngine.end();
        }
        renderEngine.cpuGraph.end();

        renderEngine.gpuGraph.begin();
        renderEngine.render(currentTime, deltaTime, takeScreenShot);
//        if (renderEngine.testCase == 1) {
//            Gdx.gl20.glClearColor(0, 0, 0, 1);
//            Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
//
//            renderEngine.renderutils2Dxz.batch.begin();
//            renderEngine.renderutils2Dxz.batch.setColor(new Color(.5f, .2f, .9f, 0.45f));
//            renderEngine.renderutils2Dxz.batch.fillCircle(getAtlasManager().planetTextureRegion, 0, 0, 16, 32);
//            renderJumpGates();
//            renderPlanets();
//            renderTraders();
//            renderEngine.renderutils2Dxz.batch.end();
//            camera.setDirty(false);
//        }
//        if (renderEngine.testCase == 2) {
//            Gdx.gl20.glClearColor(0, 0, 0, 1);
//            Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
//
//            renderEngine.renderutils2Dxz.batch.setProjectionMatrix(camera2D.combined);
//            renderEngine.renderutils2Dxz.batch.begin();
//            renderEngine.renderutils2Dxz.batch.setColor(new Color(.5f, .2f, .9f, 0.45f));
//            renderEngine.renderutils2Dxz.batch.fillCircle(getAtlasManager().planetTextureRegion, 0, 0, 16, 32);
//            renderJumpGates();
//            renderPlanets();
//            renderTraders();
//            renderEngine.renderutils2Dxz.batch.end();
//            camera.setDirty(false);
//        }
//        if (renderEngine.testCase == 3) {
//            Gdx.gl20.glClearColor(0, 0, 0, 1);
//            Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
//
//            renderEngine.renderEngine2D.batch.setProjectionMatrix(camera2D.combined);
//            renderEngine.renderEngine2D.batch.begin();
//            renderEngine.renderEngine2D.batch.setColor(new Color(.5f, .2f, .9f, 0.45f));
//            renderEngine.renderEngine2D.batch.fillCircle(getAtlasManager().planetTextureRegion, 0, 0, 16, 32);
//            renderJumpGates();
//            renderPlanets();
//            renderTraders();
//            renderEngine.renderEngine2D.batch.end();
//            camera.setDirty(false);
//        }
//        renderEngine.postProcessRender();

        renderEngine.renderEngine2D.batch.begin();
        if (showUniverseTime)
            renderUniverse();
        demo1.renderDemo(deltaTime);
        demo2.renderDemo(deltaTime);
        subtitles.render(deltaTime);
        pauseScreen.render(deltaTime);
        renderEngine.renderEngine2D.batch.end();

        renderEngine.gpuGraph.end();

        renderStage();
        renderEngine.handleQueuedScreenshot(takeScreenShot);
        takeScreenShot = false;

    }

    @Override
    public void render2Dxz() {
        renderJumpGates();
        renderPlanets();
        renderGoods();
        renderTraders();
        drawDebugGrid();
    }

    private void renderGoods() {
        for (final Planet planet : universe.planetList) {
            int index = 0;
            for (final Good good : planet.getGoodList()) {
                good.get3DRenderer().render2D(planet.x, planet.z, renderEngine, index++, universe.selectedGood == good);
            }
        }
    }

//    private void renderAllTTSStrings() {
//        List<String> names = new ArrayList<>();
//        for (Trader trader : universe.traderList) {
//            names.add(trader.getName());
//        }
//        for (Planet planet : universe.planetList) {
//            names.add(planet.getName());
//        }
//        audioEngine.radioTTS.renderAllTTSStrings(names);
//    }

    private void renderJumpGates() {
        for (final Path path : universe.pathList) {
            path.get3DRenderer().render2D(renderEngine, 0, false);
        }
    }

    private void renderPlanets() {
        for (final Planet planet : universe.planetList) {
            planet.get3DRenderer().render2D(renderEngine, 0, false);
        }
    }

    private void renderStage() throws Exception {
        if (showInfo) {
            info.update(universe, universe.selected, renderEngine);
            info.act(Gdx.graphics.getDeltaTime());
            info.draw();
        }
        int labelIndex = 0;
        // fps
        if (showFps) {
            stringBuilder.setLength(0);
            stringBuilder.append(" FPS ").append(Gdx.graphics.getFramesPerSecond());
            labels.get(labelIndex).getStyle().fontColor = Color.ROYAL;
            labels.get(labelIndex).setText(stringBuilder);
            labelIndex++;
        }
        //demo1 mode
        if (launchMode == LaunchMode.demo1) {
            stringBuilder.setLength(0);
            stringBuilder.append(String.format(" demo1 time=%s, demo1 index = %d, ambient music=%s", TimeUtil.create24hDurationString(System.currentTimeMillis() - demo1.startTime, true, false, true, true, false), demo1.index, demo1.files[demo1.index]));
            labels.get(labelIndex).getStyle().fontColor = Color.PINK;
            labels.get(labelIndex++).setText(stringBuilder);
        }

        if (launchMode == LaunchMode.demo2 && showDemo2Info) {
            stringBuilder.setLength(0);
            stringBuilder.append(String.format(" demo1 time=%s, demo1 index = %d, ambient music=%s", TimeUtil.create24hDurationString(System.currentTimeMillis() - demo2.startTime, true, false, true, true, false), demo2.index, demo2.files[demo2.index]));
            labels.get(labelIndex).getStyle().fontColor = Color.PINK;
            labels.get(labelIndex++).setText(stringBuilder);
        }
        //camera properties
        if (showCameraInfo) {
            stringBuilder.setLength(0);
            stringBuilder.append(String.format(" camera: zoomIndex(%d), position(%+.0f,%+.0f,%+.0f), lookAt(%+.0f, %+.0f, %+.0f)", camController.getZoomIndex(), camera.position.x, camera.position.y, camera.position.z, camera.lookat.x, camera.lookat.y, camera.lookat.z));
            labels.get(labelIndex).getStyle().fontColor = Color.ORANGE;
            labels.get(labelIndex++).setText(stringBuilder);
        }
        //depth of field
        if (showDepthOfFieldInfo) {
            stringBuilder.setLength(0);
            stringBuilder.append(String.format(" focal depth = %.0f, FarDofStart = %.0f, FarDofDist = %.0f", renderEngine.getDepthOfFieldEffect().getFocalDepth(), renderEngine.getDepthOfFieldEffect().getFarDofStart(), renderEngine.getDepthOfFieldEffect().getFarDofDist()));
            labels.get(labelIndex++).setText(stringBuilder);
        }
        //audio sources
        if (showAudioSources) {
            stringBuilder.setLength(0);
            stringBuilder.append(" audio sources enabled(").append(audioEngine.getEnabledAudioSourceCount()).append(") + disabled(").append(audioEngine.getDisabledAudioSourceCount()).append(") = total(").append(audioEngine.getNumberOfSources()).append(")");
            labels.get(labelIndex).getStyle().fontColor = Color.GREEN;
            labels.get(labelIndex++).setText(stringBuilder);
        }
        //time
        if (showTime) {
            stringBuilder.setLength(0);

            final float time    = renderEngine.getCurrentDayTime();
            final int   hours   = (int) time;
            final int   minutes = (int) (60 * ((time - (int) time) * 100) / 100);
            stringBuilder.append(" time = ").append(String.format("%2d", hours)).append(":").append(String.format("%2d", minutes)).append(", sun = ").append(String.format("%.0f", angle));
            labels.get(labelIndex++).setText(stringBuilder);
        }
        stage.draw();
    }

    private void renderTraders() {
        for (final Planet planet : universe.planetList) {
            int index = 0;
            for (final Sim trader : planet.traderList) {
                trader.get3DRenderer().render2D(renderEngine, 0, false);
            }
        }
    }

    private void renderUniverse() {
        {
            final float x1 = renderEngine.renderEngine2D.untransformX(0);
            final float y1 = renderEngine.renderEngine2D.untransformY(renderEngine.renderEngine2D.height - GameEngine2D.FONT_SIZE - 2);
            renderEngine.renderEngine2D.batch.setColor(TIME_MACHINE_BACKGROUND_COLOR);
            renderEngine.renderEngine2D.batch.draw(getAtlasManager().factoryTextureRegion, x1, y1, renderEngine.renderEngine2D.width * renderEngine.renderEngine2D.camera.zoom, renderEngine.renderEngine2D.height * renderEngine.renderEngine2D.camera.zoom);
        }
        // for ( int i = 0; i < renderMaster.distinctiveColorlist.size(); i++ )
        // {
        // float x1 = renderMaster.untransformX( i * 64 );
        // float y1 = renderMaster.untransformY( renderMaster.height - Screen.FONT_SIZE
        // - 2 );
        // renderMaster.batch.setColor( renderMaster.distinctiveColorlist.get( i ) );
        // renderMaster.batch.draw( renderMaster.factoryTexture, x1, y1, 63 *
        // renderMaster.camera.zoom, renderMaster.height * renderMaster.camera.zoom );
        // }
        {
            getAtlasManager().defaultFont.setColor(TEXT_COLOR);
            final String universeAge = String.format("%s", TimeUnit.toString(universe.currentTime, TimeAccuracy.DAY_ACCURACY));
            final float  x           = renderEngine.renderEngine2D.untransformX(renderEngine.renderEngine2D.width - 50);
            final float  y           = renderEngine.renderEngine2D.untransformY(renderEngine.renderEngine2D.height - 2);
            getAtlasManager().defaultFont.draw(renderEngine.renderEngine2D.batch, universeAge, x, y);
        }
        {
            final float start     = 0;
            final float end       = universe.currentTime;
            final float totalDays = end - start;
            final int   digits    = (int) Math.log10(totalDays);
            if (digits > 1) {
                final long daysDelta       = (long) Math.pow(10, digits - 1);
                float      lastPosition    = 0;
                float      lastSubPosition = 0;
                for (int i = 0; i * (daysDelta / totalDays) * renderEngine.renderEngine2D.width < renderEngine.renderEngine2D.width - 70; i++) {
                    final float yearPosition = i * (daysDelta / totalDays) * renderEngine.renderEngine2D.width;
                    String      yearName;
                    if (yearPosition - lastPosition > 256) {
                        // String yearName = String.format( "%.0f", i * daysDelta / 100 );
                        if (i * daysDelta < TimeUnit.TICKS_PER_YEAR) yearName = TimeUnit.toString(i * daysDelta);
                        else yearName = TimeUnit.toString(i * daysDelta, TimeAccuracy.YEAR_ACCURACY);
                        final float yX = renderEngine.renderEngine2D.untransformX(yearPosition);
                        final float yY = renderEngine.renderEngine2D.untransformY(renderEngine.renderEngine2D.height - 2);
                        getAtlasManager().defaultFont.setColor(TEXT_COLOR);
                        getAtlasManager().defaultFont.draw(renderEngine.renderEngine2D.batch, yearName, yX, yY);
                        lastPosition    = yearPosition;
                        lastSubPosition = yearPosition;
                    } else if (yearPosition - lastSubPosition > 51.2f) {
                        // String yearName = String.format( "%.0f", i * daysDelta / 100 );
                        if (i * daysDelta < TimeUnit.TICKS_PER_YEAR) yearName = TimeUnit.toString(i * daysDelta);
                        else yearName = TimeUnit.toString(i * daysDelta, TimeAccuracy.YEAR_ACCURACY);
                        final float yX = renderEngine.renderEngine2D.untransformX(yearPosition);
                        final float yY = renderEngine.renderEngine2D.untransformY(renderEngine.renderEngine2D.height - 2 - (FONT_SIZE - TIME_MACHINE_FONT_SIZE) / (2 * renderEngine.renderEngine2D.camera.zoom));
                        getAtlasManager().timeMachineFont.setColor(TIME_MACHINE_SUB_MARKER_COLOR);
                        getAtlasManager().timeMachineFont.draw(renderEngine.renderEngine2D.batch, yearName, yX, yY);
                        lastSubPosition = yearPosition;
                    }
                }
            }
        }
    }

    @Override
    public void resize(final int width, final int height) {
        info.resize(width, height);
        renderEngine.renderEngine2D.width  = width;
        renderEngine.renderEngine2D.height = height;
    }

//    public void setDayAmbientLight(float r, float g, float b, float shadowIntensity) {
//        dayAmbientIntensityR = r;
//        dayAmbientIntensityG = g;
//        dayAmbientIntensityB = b;
//        dayShadowIntensity   = shadowIntensity;
//    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean scrolled(final float amountX, final float amountY) {

        //		if (amountY > 0) {
        //			renderMaster.sceneClusterManager.soomOut(this, Gdx.input.getX(), Gdx.input.getY());
        //		} else {
        //			renderMaster.sceneClusterManager.soomIn(this, Gdx.input.getX(), Gdx.input.getY());
        //		}
        return false;
    }

    @Override
    public void setCamera(final float x, final float z, final boolean setDirty) throws Exception {
        renderEngine.setCameraTo(x, z, setDirty);
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

//    @Override
//    public void setShowGood(final ShowGood name) {
//        assetManager.showGood = name;
//    }

    @Override
    public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
        switch (button) {
            case Input.Buttons.LEFT:
                //did we select an object?
                //			renderMaster.sceneClusterManager.createCoordinates();
                final GameObject<GameEngine3D> selected = renderEngine.getGameObject(screenX, screenY);
                if (selected != null) try {
                    System.out.println("selected " + selected.interactive);
                    universe.setSelected(selected.interactive, true);
                } catch (final Exception e) {
                    // TODO Auto-generated catch block
                    logger.error(e.getMessage(), e);
                }
                else try {
                    universe.setSelected(selected, true);
                } catch (final Exception e) {
                    // TODO Auto-generated catch block
                    logger.error(e.getMessage(), e);
                }
                return true;
        }
        return false;
    }

    //	public Planet select(int virtualX, int virtualY) throws Exception {
    //		float xx = virtualX + (Planet3DRenderer.PLANET_DISTANCE / 2) * (int) Math.signum(virtualX);
    //		float yy = virtualY + (Planet3DRenderer.PLANET_DISTANCE / 2) * (int) Math.signum(virtualY);
    //		Planet planet = universe.planetList.queryPlanetByLocation(xx / Planet3DRenderer.PLANET_DISTANCE,
    //				yy / Planet3DRenderer.PLANET_DISTANCE);
    //		if (planet != null) {
    //			universe.selectedPlanet = planet;
    //			float dx = virtualX - (planet.x * Planet3DRenderer.PLANET_DISTANCE - Planet3DRenderer.PLANET_DISTANCE / 2);
    //			if (dx < TRADER_X_SIZE) {
    //				float dy = virtualY
    //						- (planet.y * Planet3DRenderer.PLANET_DISTANCE - Planet3DRenderer.PLANET_DISTANCE / 2);
    //				int index = (int) (dy / TRADER_Y_SIZE);
    //				if (index < universe.selectedPlanet.traderList.size()) {
    //					Trader trader = universe.selectedPlanet.traderList.get(index);
    //					if (trader != null) {
    //						universe.selectTrader(trader);
    //					}
    //				}
    //			}
    //			draw( currentTime);
    //		}
    //		return planet;
    //	}

    @Override
    public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
        return false;
    }

    //	private void setMaxFramesPerSecond(int maxFramesPerSecond) {
    //		this.maxFramesPerSecond = maxFramesPerSecond;
    //	}

    @Override
    public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
        return false;
    }

    private void updateDepthOfFieldFocusDistance() {
        if (camera.isDirty()) {
            CameraProperties zoomFactor = camController.getZoomFactors()[camController.getZoomIndex()];
            if (zoomFactor.focalDistance() != 0) {
                //camera has fixed focal distance
                renderEngine.getDepthOfFieldEffect().setFocalDepth(zoomFactor.focalDistance());
            } else {
                //calculate focal distance as the distance of the camera to the xz plane that we are looking at
                Ray     pickRay      = camera.getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
                Plane   plane        = new Plane(new Vector3(0, 1, 0), Vector3.Zero);
                Vector3 intersection = new Vector3();
                if (Intersector.intersectRayPlane(pickRay, plane, intersection)) {
                    //calculate distance to camera
                    intersection.sub(camera.position);
                    float focus = intersection.len();
                    float max   = focus * 2;
                    float min   = focus / 2;
                    renderEngine.getDepthOfFieldEffect().setFocalDepth(focus);
//                System.out.printf("depth of field min=%f max=%f%n", min, max);
                } else {
                    // Not hit
                }
            }
        }
    }

    @Override
    public boolean updateEnvironment(float timeOfDay) {
        if (Math.abs(this.timeOfDay - timeOfDay) > 0.01) {
            final Vector3 shadowLightDirection = new Vector3();
            final Matrix4 m                    = new Matrix4();
            if (renderEngine.isFixedShadowDirection()) {
                angle = 360 * 8f / 24;//0=pz,1=-pz,6=px,12=nz,18=nx
            } else {
                angle = 360 * timeOfDay / 24;
            }

            m.rotate(Vector3.X, 30);
            m.rotate(Vector3.Y, angle);
            m.translate(0, 0, -1);
            m.getTranslation(shadowLightDirection);
            shadowLightDirection.nor();
            renderEngine.getShadowLight().setDirection(shadowLightDirection);
            createImageBasedLighting(timeOfDay);
            {
                final float intensity = 1.0f;
                final float r         = renderEngine.getDayAmbientIntensityR();
                final float g         = renderEngine.getDayAmbientIntensityG();
                final float b         = renderEngine.getDayAmbientIntensityB();
                renderEngine.setShadowLight(renderEngine.getDayShadowIntensity() * intensity);
                renderEngine.setAmbientLight(r, g, b);
            }
            this.timeOfDay = timeOfDay;
        }
        return true;
//        return false;
    }

    private void updateGoods(final long currentTime) throws Exception {
        for (final Planet planet : universe.planetList) {
            int index = 0;
            for (final Good good : planet.getGoodList()) {
                good.get3DRenderer().update(planet.x, planet.y, planet.z, renderEngine, currentTime, renderEngine.getTimeOfDay(), index++, false);
//				good.get3DRenderer().renderText(planet.x, planet.y, planet.z, renderEngine, index++);
            }
        }
    }

    private void updateJumpGates(final long currentTime) throws Exception {
        for (final Path path : universe.pathList) {
            path.get3DRenderer().update(path.source.x, path.source.y, path.source.z, renderEngine, currentTime, renderEngine.getTimeOfDay(), 0, path.selected);
        }
    }

    private void updatePlanets(final long currentTime) throws Exception {
        if (universe.isEnableTime()) {
            for (final Planet planet : universe.planetList) {
                planet.get3DRenderer().update(renderEngine, currentTime, renderEngine.getTimeOfDay(), 0, planet == assetManager.universe.selectedPlanet);
            }
        }
    }


    private void updateTraders(final long currentTime) throws Exception {
        if (universe.isEnableTime()) {
            for (final Planet planet : assetManager.universe.planetList) {
                int index = 0;
                for (final Trader trader : planet.traderList) {
                    trader.get3DRenderer().update(renderEngine, currentTime, renderEngine.getTimeOfDay(), index++, trader == assetManager.universe.selectedTrader);
                }
            }
        }
    }


}
