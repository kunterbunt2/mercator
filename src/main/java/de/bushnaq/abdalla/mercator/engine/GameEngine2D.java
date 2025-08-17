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
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.profiling.GLErrorListener;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import de.bushnaq.abdalla.engine.IContextFactory;
import de.bushnaq.abdalla.engine.IGameEngine;
import de.bushnaq.abdalla.engine.RenderEngine2D;
import de.bushnaq.abdalla.engine.RenderEngine3D;
import de.bushnaq.abdalla.engine.audio.AudioEngine;
import de.bushnaq.abdalla.engine.audio.RadioTTS;
import de.bushnaq.abdalla.engine.camera.MovingCamera;
import de.bushnaq.abdalla.mercator.desktop.Context;
import de.bushnaq.abdalla.mercator.desktop.LaunchMode;
import de.bushnaq.abdalla.mercator.engine.audio.synthesis.MercatorAudioEngine;
import de.bushnaq.abdalla.mercator.renderer.ScreenListener;
import de.bushnaq.abdalla.mercator.renderer.ShowGood;
import de.bushnaq.abdalla.mercator.ui.Info;
import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.factory.ProductionFacility;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.path.Path;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.sim.Sim;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;
import de.bushnaq.abdalla.mercator.util.Message;
import de.bushnaq.abdalla.mercator.util.TimeAccuracy;
import de.bushnaq.abdalla.mercator.util.TimeStatistic;
import de.bushnaq.abdalla.mercator.util.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class GameEngine2D implements ScreenListener, ApplicationListener, InputProcessor, Message, IGameEngine {
    //	private static final String BATCH_END_DURATION = "batch.end()";
    public static final  int                          CHART_FONT_SIZE                 = 10;
    public static final  Color                        DARK_RED_COLOR                  = new Color(0.475f, 0.035f, 0.027f, 1.0f);
    public static final  Color                        DEAD_COLOR                      = Color.GRAY;
    //    static final         Color                    BACKGROUND_COLOR                = new Color(35.0f / 255, 135.0f / 255, 159.5f / 255, 1.0f);
    static final         Color                        DEBUG_GRID_BORDER_COLOR         = new Color(1f, 1f, 1f, 0.2f);
    static final         Color                        DEBUG_GRID_COLOR                = new Color(.0f, .0f, .0f, 0.2f);
    //	private static final String DRAW_DURATION = "draw()";
    public static final  int                          FONT_SIZE                       = 14;
    public static final  int                          MENU_FONT_SIZE                  = 12;
    private static final float                        SCROLL_SPEED                    = 100f;
    //    public static final  float                    PLANET_DISTANCE                 = 512;
    //	private static final String RENDER_DURATION = "render()";
    public static final  Color                        SELECTED_COLOR                  = Color.GOLDENROD;
    //    public static final  int                      SOOMIN_FONT_SIZE                = 10;
    // private static final float SCROLL_SPEED = 16.0f;
    // static final float SPACE_BETWEEN_OBJECTS = 1 * 4;
//    public static final  float                    SOOM_SPEED                      = 8.0f;
    public static final  Color                        TEXT_COLOR                      = Color.WHITE; // 0xffffffff;
    private static final Color                        TIME_MACHINE_BACKGROUND_COLOR   = new Color(0.0f, 0.0f, 0.0f, 0.9f);
    public static final  int                          TIME_MACHINE_FONT_SIZE          = 10;
    private static final Color                        TIME_MACHINE_SUB_MARKER_COLOR   = new Color(0.7f, 0.7f, 0.7f, 1.0f);
    public               AtlasManager                 atlasManager;
    public               AudioEngine                  audioEngine                     = new MercatorAudioEngine();
    public               OrthographicCamera           camera;
    private              Context                      context;
    private final        IContextFactory              contextFactory;
    //	private static final Color trafficEndColor = new Color(0xffff0000);
    //	private static final Color trafficStartColor = new Color(0xff55ff55);
    // static final int WORLD_HEIGHT = 100;
    // static final int WORLD_WIDTH = 100;
    // private float centerXD;
    // private float centerYD;
    private final        TimeStatistic                debugTimer;
    //	private MyCanvas myCanvas;
//	public Render2DMaster render2DMaster;
    private final        int                          defaultFontSize                 = GameEngine2D.FONT_SIZE;
    public               List<Color>                  distinctiveColorlist            = new ArrayList<Color>();
    public               List<Color>                  distinctiveTransparentColorlist = new ArrayList<Color>();
    private              BitmapFont                   font;
    //	private Environment environment;
    private              Info                         info;
    private              boolean                      infoVisible;
    private final        InputMultiplexer             inputMultiplexer                = new InputMultiplexer();
    private final        List<Label>                  labels                          = new ArrayList<>();
    //	private void drawBackground() {
    //		float d = 10;
    //		float z = 10;
    //		float tx1 = -universe.size * UniverseGenerator.PLANET_DISTANCE / d;
    //		float ty1 = -universe.size * UniverseGenerator.PLANET_DISTANCE / d;
    //		float tx2 = universe.size * UniverseGenerator.PLANET_DISTANCE / d;
    //		float ty2 = universe.size * UniverseGenerator.PLANET_DISTANCE / d;
    //		render2DMaster.bar(render2DMaster.atlasManager.background, tx1, ty1, tx2, ty2, z, BACKGROUND_COLOR);
    //	}
    private              int                          lastDragX                       = -1;
    private              int                          lastDragY                       = -1;
    private final        LaunchMode                   launchMode;
    private final        Logger                       logger                          = LoggerFactory.getLogger(this.getClass());
    private              int                          maxFramesPerSecond;
    private final        List<MercatorMessage>        messageQueue                    = new LinkedList<MercatorMessage>();
    private              GLProfiler                   profiler;
    public               RenderEngine2D<GameEngine2D> renderEngine;
    public               ShowGood                     showGood                        = ShowGood.Name;
    private              Stage                        stage;
    private              StringBuilder                stringBuilder;
    private              boolean                      takeScreenShot;
    private final        int                          timeMachineFontSize             = GameEngine2D.TIME_MACHINE_FONT_SIZE;
    public final         Universe                     universe;
    private              boolean                      vsyncEnabled                    = true;

    public GameEngine2D(final IContextFactory contextFactory, final Universe universe, final LaunchMode launchMode) throws Exception {
        this.contextFactory = contextFactory;
        this.universe       = universe;
        this.launchMode     = launchMode;
        universe.setScreenListener(this);
        //		this.desktopLauncher = desktopLauncher;
//		render2DMaster = new Render2DMaster(universe);
        debugTimer = new TimeStatistic();
        //		myCanvas = new MyCanvas(this, config);
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
            createCamera();
            atlasManager = new AtlasManager();
            atlasManager.init();
            initColors();
//			render2DMaster.create(null);
            renderEngine = new RenderEngine2D<GameEngine2D>(this, camera);
//            info = new Info(null, atlasManager, renderEngine.batch, camera, inputMultiplexer);
            //		info = new Info(render2DMaster, inputMultiplexer);
//			info.createStage();
            createInputProcessor(this);


            profiler = new GLProfiler(Gdx.graphics);
            profiler.setListener(GLErrorListener.THROWING_LISTENER);// ---enable exception throwing in case of error
            profiler.enable();
            createStage();
        } catch (final Exception e) {
            Gdx.app.log(this.getClass().getSimpleName(), e.getMessage());
            System.exit(1);
        }
    }

    public void createCamera() {
        camera = new OrthographicCamera(300, 0);
        Planet planet = universe.planetList.findBusyCenterPlanet();
        if (planet == null) planet = universe.planetList.get(0);
        camera.position.set(planet.x, planet.z, 0);
        camera.zoom = 1.0f;
        camera.update();
    }

    private String createFileName(final Date date, final String append) {
        final String           pattern          = "yyyy-MM-dd-HH-mm-ss";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        final String           dateAsString     = simpleDateFormat.format(date);
        final String           fileName         = "docs/pics/" + dateAsString + "-" + append + ".png";
        return fileName;
    }

    //	private void enableProfiler() {
    //		//		GLProfiler.enable();
    //	}

    private void createInputProcessor(final InputProcessor inputProcessor) throws Exception {
        inputMultiplexer.addProcessor(inputProcessor);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    //	private int getMaxFramesPerSecond() {
    //		return maxFramesPerSecond;
    //	}

    //	private void handleMessages() {
    //		synchronized (this) {
    //			while (!messageQueue.isEmpty()) {
    //				MercatorMessage message = messageQueue.remove(0);
    //				switch (message) {
    //				case MERCATOR_MESSAGE_PAUSE:
    //					pauseTime();
    //					break;
    //				case MERCATOR_MESSAGE_ENABLE_PROFILING:
    //					enableProfiler();
    //					break;
    //				case MERCATOR_MESSAGE_EXIT:
    //					exit();
    //					break;
    //				}
    //			}
    //		}
    //	}

    private void createStage() throws Exception {
        info = new Info(null, atlasManager, camera, renderEngine.batch, inputMultiplexer);
        info.createStage();
        final int height = 12;
        stage = new Stage();
        font  = new BitmapFont();
        for (int i = 0; i < 8; i++) {
            final Label label = new Label(" ", new Label.LabelStyle(font, Color.WHITE));
            label.setPosition(0, i * height);
            stage.addActor(label);
            labels.add(label);
        }
        stringBuilder = new StringBuilder();
    }

    @Override
    public void dispose() {
        System.out.println("dispose() called");
        profiler.disable();
        // postProcessor.dispose();
        renderEngine.dispose();
        info.dispose();
        //		synchronized (desktopLauncher) {
        //			desktopLauncher.notify();
        //		}
    }

    private void drawDebugGrid() {
        for (int y = -universe.size; y < universe.size; y++) {
            for (int x = -universe.size; x < universe.size; x++) {
                {
                    final float tx1 = x * Planet.PLANET_DISTANCE + 1;
                    final float ty1 = y * Planet.PLANET_DISTANCE + 1;
                    final float tx2 = x * Planet.PLANET_DISTANCE + Planet.PLANET_DISTANCE - 2;
                    final float ty2 = y * Planet.PLANET_DISTANCE + Planet.PLANET_DISTANCE - 2;
                    renderEngine.bar(atlasManager.systemTextureRegion, tx1, ty1, tx2, ty2, DEBUG_GRID_BORDER_COLOR);
                }
                {
                    final float tx1 = x * Planet.PLANET_DISTANCE/* + Planet3DRenderer.PLANET_BORDER*/;
                    final float ty1 = y * Planet.PLANET_DISTANCE/* + Planet3DRenderer.PLANET_BORDER*/;
                    final float tx2 = x * Planet.PLANET_DISTANCE + Planet.PLANET_DISTANCE/* - Planet3DRenderer.PLANET_BORDER*/ - 1;
                    final float ty2 = y * Planet.PLANET_DISTANCE + Planet.PLANET_DISTANCE/* - Planet3DRenderer.PLANET_BORDER*/ - 1;
                    renderEngine.bar(atlasManager.systemTextureRegion, tx1, ty1, tx2, ty2, DEBUG_GRID_COLOR);
                }
            }
        }
    }

    private void exit() {
        Gdx.app.exit();
    }

    @Override
    public AudioEngine getAudioEngine() {
        return audioEngine;
    }

    @Override
    public CameraInputController getCamController() {
        return null;
    }

    @Override
    public MovingCamera getCamera() {
        return null;
    }

    @Override
    public RadioTTS getRadioTTS() {
        return audioEngine.radioTTS;
    }

    private Object getRendablePosition(final float x, final float y) {
        for (final Planet planet : universe.planetList) {
            if (planet.get2DRenderer().withinBounds(x, y)) {
                return planet;
                //				universe.selectedPlanet = planet;
                //				universe.selected = planet;
                //					System.out.println(planet.getName());
            }
            for (final ProductionFacility production : planet.productionFacilityList) {
                if (production.get2DRenderer().withinBounds(x, y)) {
                    return production;
                    //					universe.selectedProductionFacility = production;
                    //					universe.selected = production;
                    //						System.out.println(production.getName());
                }
            }
            for (final Good good : planet.getGoodList()) {
                if (good.get2DRenderer().withinBounds(x, y)) {
                    return good;
                    //					universe.selectedGood = good;
                    //					universe.selected = good;
                    //						System.out.println(good.type.getName());
                }
            }
            for (final Sim sim : planet.simList) {
                if (sim.get2DRenderer().withinBounds(x, y)) {
                    return sim;
                    //					universe.selectedSim = sim;
                    //					universe.selected = sim;
                    //					System.out.println(sim.getName());
                    // simInfo.update( sim );
                }
            }
        }
        for (final Planet planet : universe.planetList) {
            for (final Trader trader : planet.traderList) {
                if (trader.get2DRenderer().withinBounds(x, y)) {
                    return trader;
                    //					universe.selected = trader;
                    //					universe.selectTrader(trader);
                    //						System.out.println(trader.getName());
                }
            }
        }
        return null;
    }

    //	private void printStatistics() throws Exception {
    //		if (profiler.isEnabled()) {
    //			setMaxFramesPerSecond(Math.max(getMaxFramesPerSecond(), Gdx.graphics.getFramesPerSecond()));
    //			// once a second
    //			if (debugTimer.getTime() > 1000) {
    //				// for ( String statisticName : universe.timeStatisticManager.getSet() )
    //				// {
    //				// TimeStatistic statistic = universe.timeStatisticManager.getStatistic(
    //				// statisticName );
    //				// System.out.println( String.format( "%s %dms %dms %dms %dms", statisticName,
    //				// statistic.lastTime, statistic.minTime, statistic.averageTime,
    //				// statistic.maxTime ) );
    //				// }
    //				System.out.printf("----------------------------------------------------\n");
    //				System.out.printf("profiler.textureBindings %d\n", profiler.getTextureBindings());
    //				System.out.printf("GLProfiler.drawCalls %d\n", profiler.getDrawCalls());
    //				System.out.printf("GLProfiler.shaderSwitches %d\n", profiler.getShaderSwitches());
    //				System.out.printf("GLProfiler.vertexCount.min %.0f\n", profiler.getVertexCount().min);
    //				System.out.printf("GLProfiler.vertexCount.average %.0f\n", profiler.getVertexCount().average);
    //				System.out.printf("GLProfiler.vertexCount.max %.0f\n", profiler.getVertexCount().max);
    //				System.out.printf("GLProfiler.calls %d\n", profiler.getCalls());
    //				System.out.printf("Texture.getNumManagedTextures() %d\n", Texture.getNumManagedTextures());
    //				System.out.printf("Gdx.graphics.getDeltaTime() %f\n", Gdx.graphics.getDeltaTime());
    //				System.out.printf("batch.renderCalls %d\n", render2DMaster.batch.renderCalls);
    //				System.out.printf(Gdx.graphics.getFramesPerSecond() + " fps\n");
    //				System.out.printf("----------------------------------------------------\n");
    //			}
    //		}
    //	}

    @Override
    public RenderEngine3D<?> getRenderEngine() {
        return null;
    }

    private void handleQueuedScreenshot(final boolean takeScreenShot) {
        if (takeScreenShot) {
            final Date   date     = new Date();
            final String fileName = createFileName(date, "mercator");
            final byte[] pixels   = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);
            // This loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing
            for (int i = 4; i < pixels.length; i += 4) {
                pixels[i - 1] = (byte) 255;
            }
            final Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
            BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
            final FileHandle handle = Gdx.files.local(fileName);
            PixmapIO.writePNG(handle, pixmap);
            pixmap.dispose();
        }

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

    public boolean isInfoVisible() {
        return infoVisible;
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
    @Override
    public boolean keyDown(final int keycode) {
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.LEFT:
                renderEngine.centerX -= SCROLL_SPEED;
                return true;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                renderEngine.centerX += SCROLL_SPEED;
                return true;
            case Input.Keys.W:
            case Input.Keys.UP:
                renderEngine.centerY -= SCROLL_SPEED;
                return true;
            case Input.Keys.S:
            case Input.Keys.DOWN:
                renderEngine.centerY += SCROLL_SPEED;
                return true;
            case Input.Keys.Q:
                exit();
                return true;
            case Input.Keys.P:
                universe.setEnableTime(!universe.isEnableTime());
                return true;
            case Input.Keys.PRINT_SCREEN:
                queueScreenshot();
                return true;
            case Input.Keys.V:
                vsyncEnabled = !vsyncEnabled;
                Gdx.graphics.setVSync(vsyncEnabled);
                return true;
            case Input.Keys.M:
                setInfoVisible(!isInfoVisible());
                return true;
            case Input.Keys.TAB:
                try {
                    universe.setSelected(profiler, false);
                } catch (final Exception e) {
                    // TODO Auto-generated catch block
                    logger.error(e.getMessage(), e);
                }
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
        //		switch (keycode) {
        //		case Input.Keys.A:
        //		case Input.Keys.D:
        //		case Input.Keys.LEFT:
        //		case Input.Keys.RIGHT:
        //			render2DMaster.centerX = 0;
        //			return true;
        //		case Input.Keys.W:
        //		case Input.Keys.S:
        //		case Input.Keys.UP:
        //		case Input.Keys.DOWN:
        //			render2DMaster.centerY = 0;
        //			return true;
        //		}
        return false;
    }

    @Override
    public boolean mouseMoved(final int screenX, final int screenY) {
        return false;
    }
    // private void drawSectors()
    // {
    // for ( int y = 0; y < universe.size * 2; y++ )
    // {
    // for ( int x = 0; x < universe.size * 2; x++ )
    // {
    // Sector sector = renderMaster.universe.sectorList.sectorMap[x][y];
    // if ( sector != null )
    // {
    // sector.getRenderer().render( x - renderMaster.universe.size, y -
    // renderMaster.universe.size, renderMaster, 0, false );
    // }
    // }
    // }
    // }

    @Override
    public void pause() {
    }

    @Override
    public void post(final MercatorMessage message) {
        messageQueue.add(message);
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

    private void queueScreenshot() {
        takeScreenShot = true;
    }

    @Override
    public void render() {
        try {
            universe.advanceInTime();
            if (profiler.isEnabled()) {
                profiler.reset();// reset on each frame
            }
            // universe.timeStatisticManager.start( RENDER_DURATION );
            // postProcessor.capture();
            render(universe.currentTime);
            // postProcessor.render();
            // universe.timeStatisticManager.stop( RENDER_LIGHT );
            //			GLProfiler.reset();
            // universe.timeStatisticManager.stop( RENDER_DURATION );
            // System.out.printf( "advance in time %d\n",
            // universe.timeStatisticManager.getStatistic(
            // Universe.ADVANCE_IN_TIME_UNIVERSE_DURATION ).lastTime );
            // System.out.printf( "render time %d\n\n",
            // universe.timeStatisticManager.getStatistic( RENDER_DURATION ).lastTime );
            renderStage();
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            System.exit(0);
        }
    }

    private void render(final long currentTime) throws Exception {
        // universe.timeStatisticManager.start( DRAW_DURATION );
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // universe.timeStatisticManager.start( BATCH_END_DURATION );
        // renderMaster.centerX += centerXD * renderMaster.camera.zoom;
        // renderMaster.centerY += centerYD * renderMaster.camera.zoom;

        renderEngine.batch.setProjectionMatrix(renderEngine.camera.combined);
        renderEngine.batch.begin();
        // universe.timeStatisticManager.pause( BATCH_END_DURATION );
        // drawSectors();
        // drawGoodTraffic();
        drawDebugGrid();
        // drawBackground();
        renderJumpGates();
        renderPlanets();
        renderGoods();
        renderTraders();
        renderUniverse();
        // for ( Planet planet : universe.planetList )
        // {
        // drawPlanetGraph( planet );
        // }
        // universe.timeStatisticManager.resume( BATCH_END_DURATION );
        if (infoVisible) {
            info.update(universe, universe.selected, null);
            info.act(Gdx.graphics.getDeltaTime());
            info.draw();
        }
        renderEngine.batch.end();
        //		handleMessages();
        // universe.timeStatisticManager.stop( BATCH_END_DURATION );
        // universe.timeStatisticManager.stop( DRAW_DURATION );
        //		printStatistics();
        if (debugTimer.getTime() > 1000) {
            debugTimer.restart();
        }
        handleQueuedScreenshot(takeScreenShot);
        takeScreenShot = false;
    }

    //	private void setMaxFramesPerSecond(int maxFramesPerSecond) {
    //		this.maxFramesPerSecond = maxFramesPerSecond;
    //	}

    @Override
    public void render2Dxz() {

    }

    private void renderGoods() {
        for (final Planet planet : universe.planetList) {
            int index = 0;
            for (final Good good : planet.getGoodList()) {
                good.get2DRenderer().render(planet.x, planet.z, renderEngine, index++, universe.selectedGood == good);
            }
        }
    }

    private void renderJumpGates() {
        for (final Path path : universe.pathList) {
            path.get2DRenderer().render(path.source.x, path.source.z, renderEngine, 0, path.selected);
        }
//		for (final Planet planet : universe.planetList) {
//			for (final Path jumpGate : planet.pathList) {
//				jumpGate.get2DRenderer().render(planet.x, planet.z, render2DMaster, 0, false);
//			}
//		}
    }

    private void renderPlanets() {
        for (final Planet planet : universe.planetList) {
            planet.get2DRenderer().render(0, 0, renderEngine, 0, planet == universe.selectedPlanet);
        }
    }

    private void renderStage() throws Exception {
        int labelIndex = 0;
        {
            stringBuilder.setLength(0);
            stringBuilder.append(" FPS: ").append(Gdx.graphics.getFramesPerSecond());
            labels.get(labelIndex++).setText(stringBuilder);
        }
        stage.draw();
        if (infoVisible) {
            info.update(universe, universe.selected, null);
            info.act(Gdx.graphics.getDeltaTime());
            info.draw();
        }
    }

    /*
     * private void drawGoodTraffic() { shapeRenderer.setProjectionMatrix(
     * camera.combined ); shapeRenderer.begin( ShapeType.Filled ); for ( Planet
     * planet : universe.planetList ) { drawGoodTraffic( planet ); }
     * shapeRenderer.end(); }
     *
     * private void drawGoodTraffic( Planet planet ) { int planetX = planet.x *
     * PLANET_DISTANCE; int planetY = planet.y * PLANET_DISTANCE; int color =
     * Color.white.getRGB(); // ---Find max int maxAmount = 32; for ( Planet
     * fromPlanet : universe.planetList ) { int amount =
     * planet.statisticManager.getAmount( fromPlanet.name ); if ( amount > maxAmount
     * ) maxAmount = amount; }
     *
     * Stroke defaultStroke = graphics.getStroke(); float dash[] = { 1.0f };
     * BasicStroke stroke = new BasicStroke( 1.7f, BasicStroke.CAP_SQUARE,
     * BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f ); graphics.setStroke( stroke );
     *
     * for ( Planet fromPlanet : universe.planetList ) { if ( planet != fromPlanet )
     * { if ( maxAmount != 0 ) { int thickness = (
     * planet.statisticManager.getAmount( fromPlanet.name ) * 32 ) / maxAmount; if (
     * thickness != 0 ) { int fromPlanetX = fromPlanet.x * PLANET_DISTANCE; int
     * fromPlanetY = fromPlanet.y * PLANET_DISTANCE; if ( universe.selectedPlanet ==
     * planet || universe.selectedPlanet == fromPlanet ) { lineBar( fromPlanetX,
     * fromPlanetY, planetX, planetY, trafficStartColor, trafficEndColor, thickness
     * ); } else {
     *
     * // lineBar( fromPlanetX, fromPlanetY, planetX, // planetY,
     * RcColor.colorMerger( Color.green, // Color.white, 0.7f ),
     * RcColor.colorMerger( // Color.red, Color.white, 0.7f ), thickness ); } } } }
     * }
     *
     * graphics.setStroke( defaultStroke ); }
     */
    private void renderTraders() {
        for (final Planet planet : universe.planetList) {
            int index = 0;
            for (final Sim trader : planet.traderList) {
                trader.get2DRenderer().render(0, 0, renderEngine, index++, trader == universe.selectedTrader);
            }
        }
    }

    private void renderUniverse() {
        {
            final float x1 = renderEngine.untransformX(0);
            final float y1 = renderEngine.untransformY(renderEngine.height - GameEngine2D.FONT_SIZE - 2);
            renderEngine.batch.setColor(TIME_MACHINE_BACKGROUND_COLOR);
            renderEngine.batch.draw(atlasManager.factoryTextureRegion, x1, y1, renderEngine.width * renderEngine.camera.zoom, renderEngine.height * renderEngine.camera.zoom);
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
            atlasManager.defaultFont.setColor(TEXT_COLOR);
            final String universeAge = String.format("%s", TimeUnit.toString(universe.currentTime, TimeAccuracy.DAY_ACCURACY));
            final float  x           = renderEngine.untransformX(renderEngine.width - 50);
            final float  y           = renderEngine.untransformY(renderEngine.height - 2);
            atlasManager.defaultFont.draw(renderEngine.batch, universeAge, x, y);
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
                for (int i = 0; i * (daysDelta / totalDays) * renderEngine.width < renderEngine.width - 70; i++) {
                    final float yearPosition = i * (daysDelta / totalDays) * renderEngine.width;
                    String      yearName;
                    if (yearPosition - lastPosition > 256) {
                        // String yearName = String.format( "%.0f", i * daysDelta / 100 );
                        if (i * daysDelta < TimeUnit.TICKS_PER_YEAR) yearName = TimeUnit.toString(i * daysDelta);
                        else yearName = TimeUnit.toString(i * daysDelta, TimeAccuracy.YEAR_ACCURACY);
                        final float yX = renderEngine.untransformX(yearPosition);
                        final float yY = renderEngine.untransformY(renderEngine.height - 2);
                        atlasManager.defaultFont.setColor(TEXT_COLOR);
                        atlasManager.defaultFont.draw(renderEngine.batch, yearName, yX, yY);
                        lastPosition    = yearPosition;
                        lastSubPosition = yearPosition;
                    } else if (yearPosition - lastSubPosition > 51.2f) {
                        // String yearName = String.format( "%.0f", i * daysDelta / 100 );
                        if (i * daysDelta < TimeUnit.TICKS_PER_YEAR) yearName = TimeUnit.toString(i * daysDelta);
                        else yearName = TimeUnit.toString(i * daysDelta, TimeAccuracy.YEAR_ACCURACY);
                        final float yX = renderEngine.untransformX(yearPosition);
                        final float yY = renderEngine.untransformY(renderEngine.height - 2 - (defaultFontSize - timeMachineFontSize) / (2 * renderEngine.camera.zoom));
                        atlasManager.timeMachineFont.setColor(TIME_MACHINE_SUB_MARKER_COLOR);
                        atlasManager.timeMachineFont.draw(renderEngine.batch, yearName, yX, yY);
                        lastSubPosition = yearPosition;
                    }
                }
            }
        }
    }

    @Override
    public void resize(final int width, final int height) {
        renderEngine.width  = width;
        renderEngine.height = height;
        renderEngine.camera.setToOrtho(false, width, height);
        renderEngine.camera.update();
        renderEngine.batch.setProjectionMatrix(renderEngine.camera.combined);
        info.resize(width, height);
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
    }

    Color satesfactionColor(final float satisfactionFactor) {
        if (satisfactionFactor >= 50) {
            return Color.GREEN;
        } else if (satisfactionFactor >= 30) {
            return Color.ORANGE;
        } else {
            return GameEngine2D.DARK_RED_COLOR;
        }
    }

    @Override
    public boolean scrolled(final float amountX, final float amountY) {
        if (amountY < 0) {
            renderEngine.soomIn(Gdx.input.getX(), Gdx.input.getY());
        } else {
            renderEngine.soomOut(Gdx.input.getX(), Gdx.input.getY());
        }
        return false;
    }

    @Override
    public void setCamera(final float x, final float z, final boolean setDirty) {
        // TODO Auto-generated method stub
    }

    public void setInfoVisible(final boolean infoVisible) {
        this.infoVisible = infoVisible;
    }

//    @Override
//    public void setShowGood(final ShowGood name) {
//        showGood = name;
//    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
        final float x = renderEngine.centerX + (screenX - renderEngine.width / 2) * renderEngine.camera.zoom;
        final float y = renderEngine.centerY + (screenY - renderEngine.height / 2) * renderEngine.camera.zoom;
        // ---What did we select?
        //		Object selected = null;
        if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
            final Object selected = getRendablePosition(x, y);
            try {
                universe.setSelected(selected, true);
            } catch (final Exception e) {
                // TODO Auto-generated catch block
                logger.error(e.getMessage(), e);
            }
        }
        return false;
    }

    @Override
    public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
        if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
            if ((lastDragX != screenX || lastDragY != screenY) && lastDragX != -1) {
                renderEngine.centerX = renderEngine.centerX - (screenX - lastDragX) * renderEngine.camera.zoom;
                renderEngine.centerY = renderEngine.centerY - (screenY - lastDragY) * renderEngine.camera.zoom;
            }
            lastDragX = screenX;
            lastDragY = screenY;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
        lastDragX = -1;
        lastDragY = -1;
        return true;
    }

    @Override
    public boolean updateEnvironment(float timeOfDay) {
        return false;
    }
}
