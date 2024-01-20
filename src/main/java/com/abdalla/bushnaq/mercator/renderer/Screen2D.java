package com.abdalla.bushnaq.mercator.renderer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abdalla.bushnaq.mercator.renderer.reports.Info;
import com.abdalla.bushnaq.mercator.universe.Universe;
import com.abdalla.bushnaq.mercator.universe.factory.ProductionFacility;
import com.abdalla.bushnaq.mercator.universe.good.Good;
import com.abdalla.bushnaq.mercator.universe.jumpgate.JumpGate;
import com.abdalla.bushnaq.mercator.universe.planet.Planet;
import com.abdalla.bushnaq.mercator.universe.sim.Sim;
import com.abdalla.bushnaq.mercator.universe.sim.trader.Trader;
import com.abdalla.bushnaq.mercator.util.Message;
import com.abdalla.bushnaq.mercator.util.TimeAccuracy;
import com.abdalla.bushnaq.mercator.util.TimeStatistic;
import com.abdalla.bushnaq.mercator.util.TimeUnit;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.profiling.GLErrorListener;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;

public class Screen2D implements ScreenListener, ApplicationListener, InputProcessor, Message {
	static final Color BACKGROUND_COLOR = new Color(35.0f / 255, 135.0f / 255, 159.5f / 255, 1.0f);
	//	private static final String BATCH_END_DURATION = "batch.end()";
	public static final int CHART_FONT_SIZE = 10;
	public static final Color DARK_RED_COLOR = new Color(0.475f, 0.035f, 0.027f, 1.0f);
	public static final Color DEAD_COLOR = Color.GRAY;
	static final Color DEBUG_GRID_BORDER_COLOR = new Color(1f, 1f, 1f, 0.2f);
	static final Color DEBUG_GRID_COLOR = new Color(.0f, .0f, .0f, 0.2f);
	//	private static final String DRAW_DURATION = "draw()";
	public static final int FONT_SIZE = 14;
	public static final int MENU_FONT_SIZE = 12;
	public static final float PLANET_DISTANCE = 512;
	private static final float SCROLL_SPEED = 100f;
	//	private static final String RENDER_DURATION = "render()";
	public static final Color SELECTED_COLOR = Color.GOLDENROD;
	// private static final float SCROLL_SPEED = 16.0f;
	// static final float SPACE_BETWEEN_OBJECTS = 1 * 4;
	public static final float SOOM_SPEED = 8.0f;
	public static final int SOOMIN_FONT_SIZE = 10;
	public static final Color TEXT_COLOR = Color.WHITE; // 0xffffffff;
	private static final Color TIME_MACHINE_BACKGROUND_COLOR = new Color(0.0f, 0.0f, 0.0f, 0.9f);
	public static final int TIME_MACHINE_FONT_SIZE = 10;
	private static final Color TIME_MACHINE_SUB_MARKER_COLOR = new Color(0.7f, 0.7f, 0.7f, 1.0f);
	//	private static final Color trafficEndColor = new Color(0xffff0000);
	//	private static final Color trafficStartColor = new Color(0xff55ff55);
	// static final int WORLD_HEIGHT = 100;
	// static final int WORLD_WIDTH = 100;
	// private float centerXD;
	// private float centerYD;
	private final TimeStatistic debugTimer;
	private BitmapFont font;
	//	private Environment environment;
	private Info info;
	private boolean infoVisible;
	private final InputMultiplexer inputMultiplexer = new InputMultiplexer();
	private final List<Label> labels = new ArrayList<>();
	private int lastDragX = -1;
	private int lastDragY = -1;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private int maxFramesPerSecond;
	private final List<MercatorMessage> messageQueue = new LinkedList<MercatorMessage>();
	private GLProfiler profiler;
	//	private MyCanvas myCanvas;
	public Render2DMaster render2DMaster;

	private Stage stage;

	private StringBuilder stringBuilder;

	private boolean takeScreenShot;

	private final Universe universe;

	//	private void drawBackground() {
	//		float d = 10;
	//		float z = 10;
	//		float tx1 = -universe.size * UniverseGenerator.PLANET_DISTANCE / d;
	//		float ty1 = -universe.size * UniverseGenerator.PLANET_DISTANCE / d;
	//		float tx2 = universe.size * UniverseGenerator.PLANET_DISTANCE / d;
	//		float ty2 = universe.size * UniverseGenerator.PLANET_DISTANCE / d;
	//		render2DMaster.bar(render2DMaster.atlasManager.background, tx1, ty1, tx2, ty2, z, BACKGROUND_COLOR);
	//	}

	private boolean vsyncEnabled = true;

	public Screen2D(final Universe universe/* , LwjglApplicationConfiguration config */) throws Exception {
		this.universe = universe;
		universe.setScreenListener(this);
		//		this.desktopLauncher = desktopLauncher;
		render2DMaster = new Render2DMaster(universe);
		debugTimer = new TimeStatistic();
		//		myCanvas = new MyCanvas(this, config);
	}

	@Override
	public void create() {
		try {
			render2DMaster.create();
			info = new Info(universe, render2DMaster.atlasManager, render2DMaster.batch, inputMultiplexer);
			//		info = new Info(render2DMaster, inputMultiplexer);
			info.createStage();
			inputMultiplexer.addProcessor(this);
			Gdx.input.setInputProcessor(inputMultiplexer);
			profiler = new GLProfiler(Gdx.graphics);
			profiler.setListener(GLErrorListener.THROWING_LISTENER);// ---enable exception throwing in case of error
			profiler.enable();
			createStage();
		} catch (final Exception e) {
			Gdx.app.log(this.getClass().getSimpleName(), e.getMessage());
			System.exit(1);
		}
	}

	private String createFileName(final Date date, final String append) {
		final String pattern = "yyyy-MM-dd-HH-mm-ss";
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		final String dateAsString = simpleDateFormat.format(date);
		final String fileName = "docs/pics/" + dateAsString + "-" + append + ".png";
		return fileName;
	}

	private void createStage() throws Exception {
		info = new Info(universe, render2DMaster.atlasManager, render2DMaster.batch, inputMultiplexer);
		info.createStage();
		final int height = 12;
		stage = new Stage();
		font = new BitmapFont();
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
		render2DMaster.dispose();
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
					render2DMaster.bar(render2DMaster.atlasManager.systemTextureRegion, tx1, ty1, tx2, ty2, DEBUG_GRID_BORDER_COLOR);
				}
				{
					final float tx1 = x * Planet.PLANET_DISTANCE/* + Planet3DRenderer.PLANET_BORDER*/;
					final float ty1 = y * Planet.PLANET_DISTANCE/* + Planet3DRenderer.PLANET_BORDER*/;
					final float tx2 = x * Planet.PLANET_DISTANCE + Planet.PLANET_DISTANCE/* - Planet3DRenderer.PLANET_BORDER*/ - 1;
					final float ty2 = y * Planet.PLANET_DISTANCE + Planet.PLANET_DISTANCE/* - Planet3DRenderer.PLANET_BORDER*/ - 1;
					render2DMaster.bar(render2DMaster.atlasManager.systemTextureRegion, tx1, ty1, tx2, ty2, DEBUG_GRID_COLOR);
				}
			}
		}
	}

	//	private void enableProfiler() {
	//		//		GLProfiler.enable();
	//	}

	private void exit() {
		Gdx.app.exit();
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

	private void handleQueuedScreenshot(final boolean takeScreenShot) {
		if (takeScreenShot) {
			final Date date = new Date();
			final String fileName = createFileName(date, "mercator");
			final byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);
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
			render2DMaster.centerX -= SCROLL_SPEED;
			return true;
		case Input.Keys.D:
		case Input.Keys.RIGHT:
			render2DMaster.centerX += SCROLL_SPEED;
			return true;
		case Input.Keys.W:
		case Input.Keys.UP:
			render2DMaster.centerY -= SCROLL_SPEED;
			return true;
		case Input.Keys.S:
		case Input.Keys.DOWN:
			render2DMaster.centerY += SCROLL_SPEED;
			return true;
		case Input.Keys.Q:
			exit();
			return true;
		case Input.Keys.P:
			render2DMaster.universe.setEnableTime(!render2DMaster.universe.isEnableTime());
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

	@Override
	public void pause() {
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
	public void post(final MercatorMessage message) {
		messageQueue.add(message);
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
		render2DMaster.batch.setProjectionMatrix(render2DMaster.camera.combined);
		render2DMaster.batch.begin();
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
		render2DMaster.batch.end();
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

	private void renderGoods() {
		for (final Planet planet : universe.planetList) {
			int index = 0;
			for (final Good good : planet.getGoodList()) {
				good.get2DRenderer().render(planet.x, planet.y, render2DMaster, index++, universe.selectedGood == good);
			}
		}
	}

	private void renderJumpGates() {
		for (final Planet planet : universe.planetList) {
			for (final JumpGate jumpGate : planet.jumpGateList) {
				jumpGate.get2DRenderer().render(planet.x, planet.y, render2DMaster, 0, false);
			}
		}
	}

	private void renderPlanets() {
		for (final Planet planet : universe.planetList) {
			planet.get2DRenderer().render(0, 0, render2DMaster, 0, planet == universe.selectedPlanet);
		}
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
				trader.get2DRenderer().render(0, 0, render2DMaster, index++, trader == universe.selectedTrader);
			}
		}
	}

	private void renderUniverse() {
		{
			final float x1 = render2DMaster.untransformX(0);
			final float y1 = render2DMaster.untransformY(render2DMaster.height - Screen2D.FONT_SIZE - 2);
			render2DMaster.batch.setColor(TIME_MACHINE_BACKGROUND_COLOR);
			render2DMaster.batch.draw(render2DMaster.atlasManager.factoryTextureRegion, x1, y1, render2DMaster.width * render2DMaster.camera.zoom, render2DMaster.height * render2DMaster.camera.zoom);
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
			render2DMaster.atlasManager.defaultFont.setColor(TEXT_COLOR);
			final String universeAge = String.format("%s", TimeUnit.toString(universe.currentTime, TimeAccuracy.DAY_ACCURACY));
			final float x = render2DMaster.untransformX(render2DMaster.width - 50);
			final float y = render2DMaster.untransformY(render2DMaster.height - 2);
			render2DMaster.atlasManager.defaultFont.draw(render2DMaster.batch, universeAge, x, y);
		}
		{
			final float start = 0;
			final float end = universe.currentTime;
			final float totalDays = end - start;
			final int digits = (int) Math.log10(totalDays);
			if (digits > 1) {
				final long daysDelta = (long) Math.pow(10, digits - 1);
				float lastPosition = 0;
				float lastSubPosition = 0;
				for (int i = 0; i * (daysDelta / totalDays) * render2DMaster.width < render2DMaster.width - 70; i++) {
					final float yearPosition = i * (daysDelta / totalDays) * render2DMaster.width;
					String yearName;
					if (yearPosition - lastPosition > 256) {
						// String yearName = String.format( "%.0f", i * daysDelta / 100 );
						if (i * daysDelta < TimeUnit.TICKS_PER_YEAR)
							yearName = TimeUnit.toString(i * daysDelta);
						else
							yearName = TimeUnit.toString(i * daysDelta, TimeAccuracy.YEAR_ACCURACY);
						final float yX = render2DMaster.untransformX(yearPosition);
						final float yY = render2DMaster.untransformY(render2DMaster.height - 2);
						render2DMaster.atlasManager.defaultFont.setColor(TEXT_COLOR);
						render2DMaster.atlasManager.defaultFont.draw(render2DMaster.batch, yearName, yX, yY);
						lastPosition = yearPosition;
						lastSubPosition = yearPosition;
					} else if (yearPosition - lastSubPosition > 51.2f) {
						// String yearName = String.format( "%.0f", i * daysDelta / 100 );
						if (i * daysDelta < TimeUnit.TICKS_PER_YEAR)
							yearName = TimeUnit.toString(i * daysDelta);
						else
							yearName = TimeUnit.toString(i * daysDelta, TimeAccuracy.YEAR_ACCURACY);
						final float yX = render2DMaster.untransformX(yearPosition);
						final float yY = render2DMaster.untransformY(render2DMaster.height - 2 - (render2DMaster.defaultFontSize - render2DMaster.timeMachineFontSize) / (2 * render2DMaster.camera.zoom));
						render2DMaster.atlasManager.timeMachineFont.setColor(TIME_MACHINE_SUB_MARKER_COLOR);
						render2DMaster.atlasManager.timeMachineFont.draw(render2DMaster.batch, yearName, yX, yY);
						lastSubPosition = yearPosition;
					}
				}
			}
		}
	}

	@Override
	public void resize(final int width, final int height) {
		render2DMaster.width = width;
		render2DMaster.height = height;
		render2DMaster.camera.setToOrtho(false, width, height);
		render2DMaster.camera.update();
		render2DMaster.batch.setProjectionMatrix(render2DMaster.camera.combined);
		info.resize(width, height);
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean scrolled(final float amountX, final float amountY) {
		if (amountY > 0) {
			render2DMaster.soomIn(this, Gdx.input.getX(), Gdx.input.getY());
		} else {
			render2DMaster.soomOut(this, Gdx.input.getX(), Gdx.input.getY());
		}
		return false;
	}

	//	private void setMaxFramesPerSecond(int maxFramesPerSecond) {
	//		this.maxFramesPerSecond = maxFramesPerSecond;
	//	}

	@Override
	public void setCamera(final float x, final float z, final boolean setDirty) {
		// TODO Auto-generated method stub
	}

	public void setInfoVisible(final boolean infoVisible) {
		this.infoVisible = infoVisible;
	}

	@Override
	public void setShowGood(final ShowGood name) {
		render2DMaster.showGood = name;
	}

	@Override
	public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
		final float x = render2DMaster.centerX + (screenX - render2DMaster.width / 2) * render2DMaster.camera.zoom;
		final float y = render2DMaster.centerY + (screenY - render2DMaster.height / 2) * render2DMaster.camera.zoom;
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
				render2DMaster.centerX = render2DMaster.centerX - (screenX - lastDragX) * render2DMaster.camera.zoom;
				render2DMaster.centerY = render2DMaster.centerY - (screenY - lastDragY) * render2DMaster.camera.zoom;
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
}
