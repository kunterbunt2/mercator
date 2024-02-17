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

package de.bushnaq.abdalla.mercator.renderer;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import de.bushnaq.abdalla.engine.GameObject;
import de.bushnaq.abdalla.engine.IContextFactory;
import de.bushnaq.abdalla.engine.RenderEngine3D;
import de.bushnaq.abdalla.engine.audio.AudioEngine;
import de.bushnaq.abdalla.engine.audio.Mp3Player;
import de.bushnaq.abdalla.engine.audio.OpenAlException;
import de.bushnaq.abdalla.engine.camera.MovingCamera;
import de.bushnaq.abdalla.mercator.audio.synthesis.MercatorAudioEngine;
import de.bushnaq.abdalla.mercator.desktop.Context;
import de.bushnaq.abdalla.mercator.desktop.LaunchMode;
import de.bushnaq.abdalla.mercator.renderer.camera.MyCameraInputController;
import de.bushnaq.abdalla.mercator.renderer.reports.Info;
import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.land.Land;
import de.bushnaq.abdalla.mercator.universe.path.Path;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.planet.Planet3DRenderer;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;
import de.bushnaq.abdalla.mercator.util.TimeAccuracy;
import de.bushnaq.abdalla.mercator.util.TimeUnit;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.model.ModelInstanceHack;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.EnvironmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GameEngine3D implements ScreenListener, ApplicationListener, InputProcessor {
    public static final  float                        CAMERA_OFFSET_X               = 300f;
    public static final  float                        CAMERA_OFFSET_Y               = 500f;
    public static final  float                        CAMERA_OFFSET_Z               = 400f;
    //	private static final String BATCH_END_DURATION = "batch.end()";
    //	private static final String DRAW_DURATION = "draw()";
    public static final  Color                        FACTORY_COLOR                 = Color.DARK_GRAY; // 0xff000000;
    public static final  float                        FACTORY_HEIGHT                = 1.2f;
    public static final  float                        FACTORY_WIDTH                 = 2.4f;
    public static final  int                          FONT_SIZE                     = 9;
    // private static final float MAX_VOXEL_DIMENSION = 20;
    public static final  Color                        NOT_PRODUCING_FACTORY_COLOR   = Color.RED; // 0xffFF0000;
    public static final  int                          RAYS_NUM                      = 128;
    public static final  Color                        SELECTED_PLANET_COLOR         = Color.BLUE;
    public static final  Color                        SELECTED_TRADER_COLOR         = Color.RED; // 0xffff0000;
    public static final  float                        SIM_HEIGHT                    = 0.3f;
    public static final  float                        SIM_WIDTH                     = 0.3f;
    public static final  float                        SOOM_SPEED                    = 8.0f * 10;
    public static final  float                        SPACE_BETWEEN_OBJECTS         = 0.1f / Universe.WORLD_SCALE;
    public static final  Color                        TEXT_COLOR                    = Color.WHITE; // 0xffffffff;
    public static final  int                          TIME_MACHINE_FONT_SIZE        = 10;
    //	private static final String RENDER_DURATION = "render()";
    //	private static final String RENDER_LIGHT = "light";
    private static final float                        SCROLL_SPEED                  = 100f;
    //	private static final Color trafficEndColor = new Color(0xffff0000);
    //	private static final Color trafficStartColor = new Color(0xff55ff55);
    //	private static final float VOXEL_SIZE = 0.5f;
    private static final Color                        TIME_MACHINE_BACKGROUND_COLOR = new Color(0.0f, 0.0f, 0.0f, 0.9f);
    private static final Color                        TIME_MACHINE_SUB_MARKER_COLOR = new Color(0.7f, 0.7f, 0.7f, 1.0f);
    //	private ModelInstance uberModelInstance;
    public final         Universe                     universe;
    private final        IContextFactory              contextFactory;
    private final        List<DemoString>             demoText                      = new ArrayList<>();
    private final        InputMultiplexer             inputMultiplexer              = new InputMultiplexer();
    private final        List<Label>                  labels                        = new ArrayList<>();
    private final        Logger                       logger                        = LoggerFactory.getLogger(this.getClass());
    //    private final        GameObject<GameEngine3D>     ocean                         = null;
    public               AudioEngine                  audioEngine                   = new MercatorAudioEngine();
    public               LaunchMode                   launchMode;
    public               RenderEngine3D<GameEngine3D> renderEngine;
    public               AssetManager                 renderMaster;
    private              AtlasManager                 atlasManager;
    private              Texture                      brdfLUT;
    private              MyCameraInputController      camController;
    private              MovingCamera                 camera;
    private              OrthographicCamera           camera2D;
    private              float                        centerXD;
    private              float                        centerYD;
    private              Context                      context;
    private              float                        demoTextX                     = 100;
    private              float                        demoTextY                     = 0;
    private              Cubemap                      diffuseCubemap;
    //	private boolean end = false;
    //	private MercatorFrame frame;
    //	private LwjglApplicationConfiguration config;
    private              Cubemap                      environmentDayCubemap;
    private              Cubemap                      environmentNightCubemap;
    private              boolean                      followMode;
    //    private              BitmapFont                   font;
    private              boolean                      hrtfEnabled                   = true;
    private              Info                         info;
    private              boolean                      infoVisible;
    private              GameObject<GameEngine3D>     instance;//TODO
    private              long                         lastCameraDirty               = 0;
    private              Mp3Player                    mp3Player;
    //    private              Render2DMaster               render2DMaster;
    private              boolean                      showFps;
    private              Cubemap                      specularCubemap;
    private              Stage                        stage;
    private              StringBuilder                stringBuilder;
    private              boolean                      takeScreenShot;
    private              boolean                      vsyncEnabled                  = true;

    public GameEngine3D(final IContextFactory contextFactory, final Universe universe, final LaunchMode launchMode) throws Exception {
        this.contextFactory = contextFactory;
        this.universe       = universe;
        this.launchMode     = launchMode;
        universe.setScreenListener(this);
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
            createInputProcessor(this);
            atlasManager = new AtlasManager();
            atlasManager.init();
            renderEngine = new RenderEngine3D<GameEngine3D>(context, this, camera, camera2D, getAtlasManager().menuFont, getAtlasManager().systemTextureRegion);
            renderEngine.getWater().setTiling(universe.size * 2 * 4 * 2 / Universe.WORLD_SCALE);
            renderEngine.getWater().setPresent(false);
            renderEngine.getWater().setWaveStrength(0.01f / Universe.WORLD_SCALE);
            renderEngine.getWater().setWaveSpeed(0.03f);
            renderEngine.getWater().setRefractiveMultiplicator(1f);
            renderEngine.setReflectionClippingPlane(-(context.getWaterLevel() - 2));
            renderEngine.setRefractionClippingPlane((context.getWaterLevel() - 2));
            renderEngine.setShadowEnabled(true);
            renderEngine.getFog().setColor(Color.BLACK);
            renderEngine.getFog().setBeginDistance(3000f);
            renderEngine.getFog().setFullDistance(5000f);
            renderEngine.getFog().setEnabled(true);
            renderEngine.setDynamicDayTime(true);
            renderEngine.setSceneBoxMin(new Vector3(-1000, -1000, -1000));
            renderEngine.setSceneBoxMax(new Vector3(1000, 1000, 1000));
            renderEngine.setDayAmbientLight(.01f, .01f, .01f, 10f);
            renderEngine.setNightAmbientLight(.01f, .01f, .01f, 10f);
            renderEngine.setSkyBox(false);
            try {
                context.setSelected(renderEngine.getProfiler(), false);
            } catch (final Exception e) {
                logger.error(e.getMessage(), e);
            }

            renderMaster = new AssetManager(universe);
//            render2DMaster = new Render2DMaster(universe);
            renderMaster.create();
//            render2DMaster.create(atlasManager);
            createEnvironment();
            createStage();
            audioEngine.create();
            audioEngine.enableHrtf(0);

//            createStone();
            createTraders();
//			createRing();
//            createWater();
            createPlanets();
//			createLand();
            createJumpGates();

            if (launchMode == LaunchMode.demo) {
                startDemoMode();
            }
            if (universe.selected != null) {
                universe.setSelected(universe.selected, true);
                followMode = true;
            }
        } catch (final Exception e) {
            Gdx.app.log(this.getClass().getSimpleName(), e.getMessage(), e);
            System.exit(1);
        }
    }

    @Override
    public void resize(final int width, final int height) {
        renderEngine.renderEngine2D.width  = width;
        renderEngine.renderEngine2D.height = height;
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
            //			batch.setProjectionMatrix(renderMaster.sceneClusterManager.camera.combined);
            //			batch.begin();
            //			batch.end();
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            System.exit(0);
        }
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        try {
            if (mp3Player != null) mp3Player.dispose();
            audioEngine.dispose();
            //		myCanvas.stop();
            renderMaster.dispose();
//            font.dispose();
            //		synchronized (desktopLauncher) {
            //			desktopLauncher.notify();
            //		}
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
        }
    }

    //	private void createCamera() {
//		camera = new MovingCamera(67f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//		final Vector3 lookat = new Vector3(0, 0, 0);
//		camera.position.set(lookat.x + 0f / 2, lookat.y + 0f / 2, lookat.z + 8);
//		camera.up.set(0, 1, 0);
//		camera.lookAt(lookat);
//		camera.near = 2f;
//		camera.far = 100f;
//		camera.update();
//		camera.setDirty(true);
//
//	}
    private void createCamera() throws Exception {
        camera = new MovingCamera(67f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Planet planet = universe.findBusyCenterPlanet();
        if (planet == null && !universe.planetList.isEmpty()) planet = universe.planetList.get(0);
        Vector3 lookat;
        if (planet != null) lookat = new Vector3(planet.x, 0, planet.z);
        else lookat = new Vector3(0, 0, 0);
        camera.position.set(lookat.x + CAMERA_OFFSET_X / Universe.WORLD_SCALE, lookat.y + CAMERA_OFFSET_Y / Universe.WORLD_SCALE, lookat.z + CAMERA_OFFSET_Z / Universe.WORLD_SCALE);
        camera.up.set(0, 1, 0);
        camera.lookAt(lookat);
        camera.near = 8f;
        camera.far  = 8000f;
        camera.update();
        camera.setDirty(true);
        camera2D = new OrthographicCamera();
    }

    private void createEnvironment() {
        // setup IBL (image based lighting)
        if (renderEngine.isPbr()) {
//			setupImageBasedLightingByFaceNames("ruins", "jpg", "png", "jpg", 10);
            setupImageBasedLightingByFaceNames("clouds", "jpg", "jpg", "jpg", 10);
//			setupImageBasedLightingByFaceNames("moonless_golf_2k", "jpg", "jpg", "jpg", 10);
            // setup skybox
            if (renderEngine.isSkyBox()) {
                renderEngine.setDaySkyBox(new SceneSkybox(environmentDayCubemap));
//            renderEngine.setSkyBox(true);
                renderEngine.setNightSkyBox(new SceneSkybox(environmentNightCubemap));
            }
            renderEngine.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));
            renderEngine.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
            renderEngine.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
            renderEngine.environment.set(new PBRFloatAttribute(PBRFloatAttribute.ShadowBias, 0f));
        } else {
        }
    }

    private void createInputProcessor(final InputProcessor inputProcessor) throws Exception {
        camController                = new MyCameraInputController(camera);
        camController.scrollFactor   = -0.1f;
        camController.translateUnits = 1000f;
        inputMultiplexer.addProcessor(inputProcessor);
        inputMultiplexer.addProcessor(camController);
        Gdx.input.setInputProcessor(inputMultiplexer);
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
        final int height = 12;
        stage = new Stage();
//        font  = new BitmapFont();
        for (int i = 0; i < 8; i++) {
            final Label label = new Label(" ", new Label.LabelStyle(getAtlasManager().menuFont, Color.WHITE));

            label.setPosition(0, i * height);
            stage.addActor(label);
            labels.add(label);
        }
        stringBuilder = new StringBuilder();
    }

    private void createStone() {
        {
            instance = new GameObject<>(new ModelInstanceHack(renderEngine.getGameEngine().renderMaster.cube.scene.model), null);
            instance.instance.transform.setToTranslationAndScaling(0, 0, 0, 16, 16, 16);
            instance.update();
            renderEngine.addStatic(instance);
        }
        {
            instance = new GameObject<>(new ModelInstanceHack(renderEngine.getGameEngine().renderMaster.cube.scene.model), null);
            instance.instance.transform.setToTranslationAndScaling(0, 0, 0, 32, 1, 32);
            instance.update();
            renderEngine.addStatic(instance);
        }
    }

    private void createTraders() {
        for (final Planet planet : renderMaster.universe.planetList) {
            for (final Trader trader : planet.traderList) {
                trader.get3DRenderer().create(renderEngine);
            }
        }
    }

    private void createWater() {
        final float delta = (universe.size + 1) * Planet.PLANET_DISTANCE * 2;
        //sector
        {
            //			final Color sectorColor = renderMaster.getDistinctiveColor(planet.sector.type);
            final GameObject<GameEngine3D> sectorInstance = new GameObject<>(new ModelInstanceHack(renderMaster.sector), null);
            sectorInstance.instance.transform.setToTranslationAndScaling(0, Planet3DRenderer.SECTOR_Y, 0, delta, 8, delta);
            sectorInstance.update();
            renderEngine.addStatic(sectorInstance);

        }
        //water
        {
            final GameObject<GameEngine3D> sectorInstance = new GameObject<>(new ModelInstanceHack(renderMaster.water), null);
            sectorInstance.instance.transform.setToTranslationAndScaling(0, Planet3DRenderer.WATER_Y, 0, delta, 1, delta);
            sectorInstance.update();
            renderEngine.addStatic(sectorInstance);
        }
    }

    private void exit() {
        Gdx.app.exit();
    }

    private void export(final String fileName, final List<DemoString> Strings) throws IOException {
        final FileWriter  fileWriter  = new FileWriter(fileName);
        final PrintWriter printWriter = new PrintWriter(fileWriter);
        for (final DemoString demoString : Strings) {
            printWriter.println(demoString.text);
        }
        printWriter.close();
    }

    public AtlasManager getAtlasManager() {
        return atlasManager;
    }

//    public int getMaxFramesPerSecond() {
//        return maxFramesPerSecond;
//    }

    public boolean isInfoVisible() {
        return infoVisible;
    }

    @Override
    public boolean keyDown(final int keycode) {
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.LEFT:
                centerXD = -SCROLL_SPEED;
                return true;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                centerXD = SCROLL_SPEED;
                return true;
            case Input.Keys.W:
            case Input.Keys.UP:
                centerYD = -SCROLL_SPEED;
                return true;
            case Input.Keys.S:
            case Input.Keys.DOWN:
                centerYD = SCROLL_SPEED;
                return true;
            case Input.Keys.Q:
                exit();
                return true;
            case Input.Keys.P:
                renderMaster.universe.setEnableTime(!renderMaster.universe.isEnableTime());
                return true;
            case Input.Keys.PRINT_SCREEN:
                queueScreenshot();
                return true;
            case Input.Keys.NUM_1:
                renderEngine.setAlwaysDay(!renderEngine.isAlwaysDay());
                return true;
            case Input.Keys.NUM_3:
                renderEngine.setDepthOfField(!renderEngine.isDepthOfField());
                return true;
            case Input.Keys.V:
                vsyncEnabled = !vsyncEnabled;
                Gdx.graphics.setVSync(vsyncEnabled);
                return true;
            case Input.Keys.I:
                setInfoVisible(!isInfoVisible());
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
            case Input.Keys.TAB:
                try {
                    universe.setSelected(renderEngine.getProfiler(), false);
                } catch (final Exception e) {
                    logger.error(e.getMessage(), e);
                }
                return true;
            case Input.Keys.NUM_2:
                if (launchMode == LaunchMode.demo) launchMode = LaunchMode.normal;
                else launchMode = LaunchMode.demo;
                try {
                    startDemoMode();
                } catch (OpenAlException e) {
                    throw new RuntimeException(e);
                }
                return true;
            case Input.Keys.F:
                followMode = !followMode;
                return true;
        }
        return false;

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

    @Override
    public boolean keyUp(final int keycode) {
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.D:
            case Input.Keys.LEFT:
            case Input.Keys.RIGHT:
                centerXD = 0;
                return true;
            case Input.Keys.W:
            case Input.Keys.S:
            case Input.Keys.UP:
            case Input.Keys.DOWN:
                centerYD = 0;
                return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(final char character) {
        return false;
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

    @Override
    public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
        switch (button) {
            case Input.Buttons.LEFT:
                //did we select an object?
                //			renderMaster.sceneClusterManager.createCoordinates();
                final GameObject<GameEngine3D> selected = renderEngine.getGameObject(screenX, screenY);
                System.out.println("selected " + selected);
                if (selected != null) try {
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

    @Override
    public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(final int screenX, final int screenY) {
        return false;
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

    public void queueScreenshot() {
        takeScreenShot = true;
    }

    private void render(final long currentTime) throws Exception {
        final float deltaTime = Gdx.graphics.getDeltaTime();
        renderEngine.updateCamera(centerXD, 0f, centerYD);
        if (followMode && universe.selected != null) {
            if (System.currentTimeMillis() - lastCameraDirty > 1000) {
                universe.setSelected(universe.selected, true);
                lastCameraDirty = System.currentTimeMillis();
            } else {
                universe.setSelected(universe.selected, false);
            }
        }

        renderJumpGates(currentTime);
        renderPlanets(currentTime);
        renderGoods(currentTime);
        renderTraders(currentTime);
        audioEngine.begin(camera);
        audioEngine.end();
        renderEngine.cpuGraph.end();

        renderEngine.gpuGraph.begin();
        renderEngine.render(currentTime, deltaTime, takeScreenShot);
//        renderEngine.postProcessRender();
//        renderEngine.renderGraphs();
        renderEngine.renderEngine2D.batch.begin();
        renderUniverse();
        renderDemo();
        renderEngine.renderEngine2D.batch.end();
        renderEngine.gpuGraph.end();
        renderStage();
        renderEngine.handleQueuedScreenshot(takeScreenShot);
        takeScreenShot = false;

    }

    private void renderDemo() throws IOException {
        if (launchMode == LaunchMode.demo) {
            final float lineHeightFactor = 2f;
            if (demoText.isEmpty()) {
                demoText.add(new DemoString("Mercator", getAtlasManager().demoBigFont));
                demoText.add(new DemoString("A computer game implementation of a closed economical simulation.", getAtlasManager().demoMidFont));
                demoText.add(new DemoString(String.format("The current world is generated proceduraly and includes %d cities, %d factories, %d traders and %d sims.", universe.planetList.size(), universe.planetList.size() * 2, universe.traderList.size(), universe.simList.size()), getAtlasManager().demoMidFont));
                demoText.add(new DemoString("The amount of wealth in the system, including products and money is constant at all times. ", getAtlasManager().demoMidFont));
                demoText.add(new DemoString("Factories pay wages to sims to produce goods that are sold on a free market.", getAtlasManager().demoMidFont));
                demoText.add(new DemoString("Some sims are traders that buy products in one city and sell them with profit in another city.", getAtlasManager().demoMidFont));
                demoText.add(new DemoString("All sims have needs that they need to fulfill else they die.", getAtlasManager().demoMidFont));
                demoText.add(new DemoString("All sims have cravings that they need to fulfill to keep their satisfaction level up.", getAtlasManager().demoMidFont));
                demoText.add(new DemoString("All sounds are generated by a openal based audio render engine for libgdx supporting procedurally generated audio using HRTF.", getAtlasManager().demoMidFont));
                //				demoText.add(new DemoString("Demo song is 'abyss' by Abdalla Bushnaq.", render2DMaster.atlasManager.demoMidFont));
                demoText.add(new DemoString("Work in progress...", getAtlasManager().demoMidFont));
                demoText.add(new DemoString("Developed using libgdx and gdx-gltf open source frameworks.", getAtlasManager().demoMidFont));
                export("target/demo.txt", demoText);
            }

            Color demoTextColor;
            if (renderEngine.isNight()) demoTextColor = new Color(1f, 1f, 1f, 0.2f);
            else demoTextColor = new Color(0f, 0f, 0f, 0.6f);
            float deltaY = 0;

            final GlyphLayout layout = new GlyphLayout();
            layout.setText(demoText.get(0).font, demoText.get(0).text);
            final float width = layout.width;// contains the width of the current set text
            //		final float height = layout.height; // contains the height of the current set text

            final float topMargine    = 50f;
            final float buttomMargine = 200f;
            for (int i = 0; i < demoText.size(); i++) {
                final DemoString ds = demoText.get(i);
                ds.font.setColor(demoTextColor);
                final float y = demoTextY - deltaY;
                if (y < buttomMargine) {
                    ds.font.setColor(demoTextColor);
                    ds.font.getColor().a = 0f;
                } else if (y < buttomMargine * 2) {
                    ds.font.setColor(demoTextColor);
                    ds.font.getColor().a = demoTextColor.a * (y - buttomMargine) / buttomMargine;
                } else if (y > renderEngine.renderEngine2D.height - topMargine) {
                    ds.font.setColor(demoTextColor);
                    ds.font.getColor().a = 0;
                } else if (y > renderEngine.renderEngine2D.height - topMargine * 2) {
                    ds.font.setColor(demoTextColor);
                    ds.font.getColor().a = demoTextColor.a * (1 - (y - renderEngine.renderEngine2D.height + topMargine * 2) / topMargine);
                } else {
                    ds.font.setColor(demoTextColor);
                }
                final GlyphLayout lastLayout = ds.font.draw(renderEngine.renderEngine2D.batch, ds.text, demoTextX, y, width, Align.left, true);
                deltaY += lastLayout.height * lineHeightFactor;
            }
            demoTextY += 1;
            if (demoTextY - deltaY > renderEngine.renderEngine2D.height * lineHeightFactor) demoTextY = 0;
        }
    }

    private void renderGoods(final long currentTime) throws Exception {
        for (final Planet planet : universe.planetList) {
            int index = 0;
            for (final Good good : planet.getGoodList()) {
                good.get3DRenderer().update(planet.x, planet.y, planet.z, renderEngine, currentTime, renderEngine.getTimeOfDay(), index++, false);
//				good.get3DRenderer().renderText(planet.x, planet.y, planet.z, renderEngine, index++);
            }
        }
    }

    private void renderJumpGates(final long currentTime) throws Exception {
        for (final Path path : universe.pathList) {
            path.get3DRenderer().update(path.source.x, path.source.y, path.source.z, renderEngine, currentTime, renderEngine.getTimeOfDay(), 0, path.selected);
        }
        //		for (final Planet planet : universe.planetList) {
        //			for (final Path jumpGate : planet.pathList) {
        //				jumpGate.get3DRenderer().update(planet.x, planet.y, planet.z, renderMaster, currentTime, renderMaster.sceneManager.getTimeOfDay(), 0, jumpGate.selected);
        //			}
        //		}
    }

    private void renderPlanets(final long currentTime) throws Exception {
        for (final Planet planet : universe.planetList) {
            planet.get3DRenderer().update(renderEngine, currentTime, renderEngine.getTimeOfDay(), 0, planet == renderMaster.universe.selectedPlanet);
        }
    }

    private void renderStage() throws Exception {
        if (infoVisible) {
            info.update(universe, universe.selected, renderEngine);
            info.act(Gdx.graphics.getDeltaTime());
            info.draw();
        }
        int labelIndex = 0;
        // fps
        if (showFps) {
            stringBuilder.setLength(0);
            stringBuilder.append(" FPS ").append(Gdx.graphics.getFramesPerSecond());
            labels.get(labelIndex).setText(stringBuilder);
            labelIndex++;
        }
        //audio sources
        {
            stringBuilder.setLength(0);
            stringBuilder.append(" audio sources: ").append(audioEngine.getEnabledAudioSourceCount() + " / " + audioEngine.getDisabledAudioSourceCount());
            labels.get(labelIndex++).setText(stringBuilder);
        }
        //time
        {
            stringBuilder.setLength(0);

            final float time    = renderEngine.getCurrentDayTime();
            final int   hours   = (int) time;
            final int   minutes = (int) (60 * ((time - (int) time) * 100) / 100);
            stringBuilder.append(" time ").append(hours).append(":").append(minutes);
            labels.get(labelIndex++).setText(stringBuilder);
        }
        stage.draw();
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

    private void renderTraders(final long currentTime) throws Exception {
        for (final Planet planet : renderMaster.universe.planetList) {
            int index = 0;
            for (final Trader trader : planet.traderList) {
                trader.get3DRenderer().update(renderEngine, currentTime, renderEngine.getTimeOfDay(), index++, trader == renderMaster.universe.selectedTrader);
            }
        }
    }

    //	private void setMaxFramesPerSecond(int maxFramesPerSecond) {
    //		this.maxFramesPerSecond = maxFramesPerSecond;
    //	}

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
    public void setCamera(final float x, final float z, final boolean setDirty) throws Exception {
        renderEngine.setCameraTo(x, z, setDirty);
    }

    @Override
    public void setShowGood(final ShowGood name) {
        renderMaster.showGood = name;
    }

    public void setInfoVisible(final boolean infoVisible) {
        this.infoVisible = infoVisible;
    }

    private void setupImageBasedLightingByFaceNames(final String name, final String diffuseExtension, final String environmentExtension, final String specularExtension, final int specularIterations) {
        diffuseCubemap          = EnvironmentUtil.createCubemap(new InternalFileHandleResolver(), AtlasManager.getAssetsFolderName() + "/textures/" + name + "/diffuse/diffuse_", "_0." + diffuseExtension, EnvironmentUtil.FACE_NAMES_FULL);
        environmentDayCubemap   = EnvironmentUtil.createCubemap(new InternalFileHandleResolver(), AtlasManager.getAssetsFolderName() + "/textures/" + name + "/environmentDay/environment_", "_0." + environmentExtension, EnvironmentUtil.FACE_NAMES_FULL);
        environmentNightCubemap = EnvironmentUtil.createCubemap(new InternalFileHandleResolver(), AtlasManager.getAssetsFolderName() + "/textures/" + name + "/environmentNight/environment_", "_0." + environmentExtension, EnvironmentUtil.FACE_NAMES_FULL);
        specularCubemap         = EnvironmentUtil.createCubemap(new InternalFileHandleResolver(), AtlasManager.getAssetsFolderName() + "/textures/" + name + "/specular/specular_", "_", "." + specularExtension, specularIterations, EnvironmentUtil.FACE_NAMES_FULL);
        brdfLUT                 = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        // // setup quick IBL (image based lighting)
        // DirectionalLightEx light = new DirectionalLightEx();
        // light.direction.set(1, -3, 1).nor();
        // light.color.set(Color.WHITE);
        // IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        // environmentCubemap = iblBuilder.buildEnvMap(1024);
        // diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        // specularCubemap = iblBuilder.buildRadianceMap(10);
        // iblBuilder.dispose();
    }

    private void startDemoMode() throws OpenAlException {
        renderEngine.setDepthOfField(true);
        renderEngine.setAlwaysDay(false);
        mp3Player = audioEngine.createAudioProducer(Mp3Player.class);
        mp3Player.setFile(Gdx.files.internal(AtlasManager.getAssetsFolderName() + "/audio/02-methodica.ogg"));
        mp3Player.setGain(1.0f);
        mp3Player.play();
        AudioEngine.checkAlError("Failed to set listener orientation with error #");
    }

    class DemoString {
        BitmapFont font;

        String text;

        public DemoString(final String text, final BitmapFont font) {
            this.text = text;
            this.font = font;
        }
    }

}
