package de.bushnaq.abdalla.mercator.renderer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import de.bushnaq.abdalla.mercator.audio.synthesis.Mp3Player;
import de.bushnaq.abdalla.mercator.audio.synthesis.OpenAlException;
import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.land.Land;
import de.bushnaq.abdalla.mercator.universe.path.Path;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.planet.Planet3DRenderer;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;
import net.mgsx.gltf.scene3d.model.ModelInstanceHack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bushnaq.abdalla.mercator.desktop.LaunchMode;
import de.bushnaq.abdalla.mercator.util.TimeAccuracy;
import de.bushnaq.abdalla.mercator.util.TimeUnit;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.profiling.GLErrorListener;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

public class Screen3D implements ScreenListener, ApplicationListener, InputProcessor {
	class DemoString {
		BitmapFont font;

		String text;

		public DemoString(final String text, final BitmapFont font) {
			this.text = text;
			this.font = font;
		}
	}

	//	private static final String BATCH_END_DURATION = "batch.end()";
	//	private static final String DRAW_DURATION = "draw()";
	public static final Color FACTORY_COLOR = Color.DARK_GRAY; // 0xff000000;

	public static final float FACTORY_HEIGHT = 1.2f;
	public static final float FACTORY_WIDTH = 2.4f;
	public static final int FONT_SIZE = 9;
	// private static final float MAX_VOXEL_DIMENSION = 20;
	public static final Color NOT_PRODUCING_FACTORY_COLOR = Color.RED; // 0xffFF0000;
	public static final int RAYS_NUM = 128;
	//	private static final String RENDER_DURATION = "render()";
	//	private static final String RENDER_LIGHT = "light";
	private static final float SCROLL_SPEED = 100f;
	public static final Color SELECTED_PLANET_COLOR = Color.BLUE;
	public static final Color SELECTED_TRADER_COLOR = Color.RED; // 0xffff0000;
	public static final float SIM_HEIGHT = 0.3f;
	public static final float SIM_WIDTH = 0.3f;
	public static final float SOOM_SPEED = 8.0f * 10;
	public static final float SPACE_BETWEEN_OBJECTS = 0.1f / Universe.WORLD_SCALE;
	public static final Color TEXT_COLOR = Color.WHITE; // 0xffffffff;
	//	private static final Color trafficEndColor = new Color(0xffff0000);
	//	private static final Color trafficStartColor = new Color(0xff55ff55);
	//	private static final float VOXEL_SIZE = 0.5f;
	private static final Color TIME_MACHINE_BACKGROUND_COLOR = new Color(0.0f, 0.0f, 0.0f, 0.9f);
	private static final Color TIME_MACHINE_SUB_MARKER_COLOR = new Color(0.7f, 0.7f, 0.7f, 1.0f);
	private float centerXD;
	private float centerYD;
	List<DemoString> demoText = new ArrayList<>();
	float demoTextX = 100;
	float demoTextY = 0;
	//	private boolean end = false;
	//	private MercatorFrame frame;
	//	private LwjglApplicationConfiguration config;
	boolean enableProfiling = true;
	private boolean followMode;
	private BitmapFont font;
	private boolean hrtfEnabled = true;
	private final List<Label> labels = new ArrayList<>();
	private long lastCameraDirty = 0;
	public LaunchMode launchMode;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	//	private FPSLogger fPSLogger = new FPSLogger();
	//	private float time;
	private int maxFramesPerSecond;
	Mp3Player mp3Player;
	GameObject ocean = null;
	//	private MyCanvas myCanvas;
	//	private PerformanceLogger performanceLogger = new PerformanceLogger();
	private GLProfiler profiler;
	private final Render2DMaster render2DMaster;

	public Render3DMaster renderMaster;
	private Stage stage;
	private StringBuilder stringBuilder;
	private boolean takeScreenShot;
	//	private ModelInstance uberModelInstance;
	private final Universe universe;
	private boolean vsyncEnabled = true;

	public Screen3D(final Universe universe, final LaunchMode launchMode) throws Exception {
		this.universe = universe;
		this.launchMode = launchMode;
		universe.setScreenListener(this);
		//		this.config = config;
		//		this.frame = frame;
		renderMaster = new Render3DMaster(universe, this, launchMode);
		render2DMaster = new Render2DMaster(universe);
		//		renderMaster.centerX = 0;
		//		renderMaster.centerY = 0;
		//		myCanvas = new MyCanvas(this, config);
	}

	@Override
	public void create() {
		try {
			renderMaster.create();
			render2DMaster.create();
			profiler = new GLProfiler(Gdx.graphics);
			profiler.setListener(GLErrorListener.LOGGING_LISTENER);// ---enable exception throwing in case of error
			profiler.setListener(new MyGLErrorListener());
			if (enableProfiling) {
				profiler.enable();
			}
			createStage();
			createTraders();
//			createRing();
			createWater();
			createPlanets();
//			createLand();
			createJumpGates();

			if (launchMode == LaunchMode.demo) {
				//				renderMaster.sceneManager.setEnableDepthOfField(true);
				//				renderMaster.sceneManager.setAlwaysDay(false);
				//				mp3Player = renderMaster.sceneClusterManager.audioEngine.createAudioProducer(Mp3Player.class);
				//				mp3Player.setFile(Gdx.files.internal("02-methodica.ogg"));
				//				mp3Player.setGain(0.1f);
				//				mp3Player.play();
				//				AudioEngine.checkAlError("Failed to set listener orientation with error #");
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
	private void createWater() {
		final float delta = (universe.size + 1) * Planet.PLANET_DISTANCE * 2;
		//sector
		{
			//			final Color sectorColor = renderMaster.getDistinctiveColor(planet.sector.type);
			final GameObject sectorInstance = new GameObject(new ModelInstanceHack(renderMaster.sector), null);
			sectorInstance.instance.transform.setToTranslationAndScaling(0, Planet3DRenderer.SECTOR_Y, 0, delta, 8, delta);
			sectorInstance.update();
			renderMaster.sceneManager.addStatic(sectorInstance);

		}
		//water
		{
			final GameObject sectorInstance = new GameObject(new ModelInstanceHack(renderMaster.water), null);
			sectorInstance.instance.transform.setToTranslationAndScaling(0, Planet3DRenderer.WATER_Y, 0, delta, 1, delta);
			sectorInstance.update();
			renderMaster.sceneManager.addStatic(sectorInstance);
		}
	}

	private void createJumpGates() {
//		if (launchMode == LaunchMode.development)
		{
			for (final Path path : universe.pathList) {
				path.get3DRenderer().create(path.source.x, path.source.y, path.source.z, renderMaster);
			}
		}
	}

	private void createLand() {
		for (final Land land : universe.landList) {
			land.get3DRenderer().create(renderMaster);
		}
	}

	private void createPlanets() {
		for (final Planet planet : universe.planetList) {
			planet.get3DRenderer().create(renderMaster);
		}
	}

	private void createRing() {
		universe.ring.get3DRenderer().create(renderMaster);
	}

	private void createStage() throws Exception {
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

	private void createTraders() {
		for (final Planet planet : renderMaster.universe.planetList) {
			for (final Trader trader : planet.traderList) {
				trader.get3DRenderer().create(renderMaster);
			}

		}
	}

	@Override
	public void dispose() {
		try {
			//			if (mp3Player != null)
			//				mp3Player.dispose();
			if (profiler.isEnabled()) {
				profiler.disable();
			}
			//		myCanvas.stop();
			renderMaster.dispose();
			font.dispose();
			//		synchronized (desktopLauncher) {
			//			desktopLauncher.notify();
			//		}
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
	}

	private void exit() {
		Gdx.app.exit();
	}

	private void export(final String fileName, final List<DemoString> Strings) throws IOException {
		final FileWriter fileWriter = new FileWriter(fileName);
		final PrintWriter printWriter = new PrintWriter(fileWriter);
		for (final DemoString demoString : Strings) {
			printWriter.println(demoString.text);
		}
		printWriter.close();
	}

	public int getMaxFramesPerSecond() {
		return maxFramesPerSecond;
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
			renderMaster.sceneManager.setAlwaysDay(!renderMaster.sceneManager.isAlwaysDay());
			return true;
		case Input.Keys.V:
			vsyncEnabled = !vsyncEnabled;
			Gdx.graphics.setVSync(vsyncEnabled);
			return true;
		case Input.Keys.M:
			renderMaster.sceneManager.setInfoVisible(!renderMaster.sceneManager.isInfoVisible());
			return true;
		case Input.Keys.H:
			try {
				if (hrtfEnabled) {
					renderMaster.sceneManager.audioEngine.disableHrtf(0);
					hrtfEnabled = false;
				} else {
					renderMaster.sceneManager.audioEngine.enableHrtf(0);
					hrtfEnabled = true;
				}
			} catch (final OpenAlException e) {
				logger.error(e.getMessage(), e);
			}
			return true;
		case Input.Keys.TAB:
			try {
				universe.setSelected(profiler, false);
			} catch (final Exception e) {
				logger.error(e.getMessage(), e);
			}
			return true;
		case Input.Keys.NUM_2:
			if (launchMode == LaunchMode.demo)
				launchMode = LaunchMode.normal;
			else
				launchMode = LaunchMode.demo;
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
	public boolean mouseMoved(final int screenX, final int screenY) {
		return false;
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	public void queueScreenshot() {
		takeScreenShot = true;
	}

	@Override
	public void render() {
		try {
			universe.advanceInTime();
			if (profiler.isEnabled()) {
				profiler.reset();// reset on each frame
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

	//	private void printStatistics() throws Exception {
	//		if (profiler.isEnabled()) {
	//			//			setMaxFramesPerSecond(Math.max(getMaxFramesPerSecond(), Gdx.graphics.getFramesPerSecond()));
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
	//				System.out.printf("profiler.textureBindings %d\n", profiler.getTextureBindings());// expensive, minimize
	//																									// with atlas
	//				System.out.printf("profiler.drawCalls %d\n", profiler.getDrawCalls());
	//				System.out.printf("profiler.shaderSwitches %d\n", profiler.getShaderSwitches());
	//				System.out.printf("profiler.vertexCount.min %.0f\n", profiler.getVertexCount().min);
	//				System.out.printf("profiler.vertexCount.average %.0f\n", profiler.getVertexCount().average);
	//				System.out.printf("profiler.vertexCount.max %.0f\n", profiler.getVertexCount().max);
	//				System.out.printf("profiler.calls %d\n", profiler.getCalls());
	//				System.out.printf("Texture.getNumManagedTextures() %d\n", Texture.getNumManagedTextures());
	//				System.out.printf("Gdx.graphics.getDeltaTime() %f\n", Gdx.graphics.getDeltaTime());
	//				//				System.out.printf("batch.renderCalls %d\n", renderMaster.sceneClusterManager.modelBatch.);
	//				System.out.printf(Gdx.graphics.getFramesPerSecond() + " fps\n");
	//				System.out.printf("----------------------------------------------------\n");
	//			}
	//		}
	//	}

	private void render(final long currentTime) throws Exception {
		final float deltaTime = Gdx.graphics.getDeltaTime();
		renderMaster.sceneManager.updateCamera(centerXD, 0f, centerYD);
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

		renderMaster.sceneManager.render(currentTime, deltaTime, takeScreenShot);
		renderMaster.sceneManager.postProcessRender();
		renderMaster.sceneManager.renderStage();
		render2DMaster.batch.begin();
		renderUniverse();
		renderDemo();
		render2DMaster.batch.end();
		renderStage();
		renderMaster.sceneManager.handleQueuedScreenshot(takeScreenShot);
		takeScreenShot = false;
	}

	private void renderDemo() throws IOException {
		if (launchMode == LaunchMode.demo) {
			final float lineHeightFactor = 2f;
			if (demoText.isEmpty()) {
				demoText.add(new DemoString("Mercator", render2DMaster.atlasManager.demoBigFont));
				demoText.add(new DemoString("A computer game implementation of a closed economical simulation.", render2DMaster.atlasManager.demoMidFont));
				demoText.add(new DemoString(String.format("The current world is generated proceduraly and includes %d cities, %d factories, %d traders and %d sims.", universe.planetList.size(), universe.planetList.size() * 2, universe.traderList.size(), universe.simList.size()), render2DMaster.atlasManager.demoMidFont));
				demoText.add(new DemoString("The amount of wealth in the system, including products and money is constant at all times. ", render2DMaster.atlasManager.demoMidFont));
				demoText.add(new DemoString("Factories pay wages to sims to produce goods that are sold on a free market.", render2DMaster.atlasManager.demoMidFont));
				demoText.add(new DemoString("Some sims are traders that buy products in one city and sell them with profit in another city.", render2DMaster.atlasManager.demoMidFont));
				demoText.add(new DemoString("All sims have needs that they need to fulfill else they die.", render2DMaster.atlasManager.demoMidFont));
				demoText.add(new DemoString("All sims have cravings that they need to fulfill to keep their satisfaction level up.", render2DMaster.atlasManager.demoMidFont));
				demoText.add(new DemoString("All sounds are generated by a openal based audio render engine for libgdx supporting procedurally generated audio using HRTF.", render2DMaster.atlasManager.demoMidFont));
				//				demoText.add(new DemoString("Demo song is 'abyss' by Abdalla Bushnaq.", render2DMaster.atlasManager.demoMidFont));
				demoText.add(new DemoString("Work in progress...", render2DMaster.atlasManager.demoMidFont));
				demoText.add(new DemoString("Developed using libgdx and gdx-gltf open source frameworks.", render2DMaster.atlasManager.demoMidFont));
				export("target/demo.txt", demoText);
			}

			Color demoTextColor;
			if (renderMaster.sceneManager.isNight())
				demoTextColor = new Color(1f, 1f, 1f, 0.2f);
			else
				demoTextColor = new Color(0f, 0f, 0f, 0.6f);
			float deltaY = 0;

			final GlyphLayout layout = new GlyphLayout();
			layout.setText(demoText.get(0).font, demoText.get(0).text);
			final float width = layout.width;// contains the width of the current set text
			//		final float height = layout.height; // contains the height of the current set text

			final float topMargine = 50f;
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
				} else if (y > render2DMaster.height - topMargine) {
					ds.font.setColor(demoTextColor);
					ds.font.getColor().a = 0;
				} else if (y > render2DMaster.height - topMargine * 2) {
					ds.font.setColor(demoTextColor);
					ds.font.getColor().a = demoTextColor.a * (1 - (y - render2DMaster.height + topMargine * 2) / topMargine);
				} else {
					ds.font.setColor(demoTextColor);
				}
				final GlyphLayout lastLayout = ds.font.draw(render2DMaster.batch, ds.text, demoTextX, y, width, Align.left, true);
				deltaY += lastLayout.height * lineHeightFactor;
			}
			demoTextY += 1;
			if (demoTextY - deltaY > render2DMaster.height * lineHeightFactor)
				demoTextY = 0;
		}
	}

	private void renderGoods(final long currentTime) throws Exception {
		for (final Planet planet : universe.planetList) {
			int index = 0;
			for (final Good good : planet.getGoodList()) {
				good.get3DRenderer().update(planet.x, planet.y, planet.z, renderMaster, currentTime, renderMaster.sceneManager.getTimeOfDay(), index++, false);
			}
		}
	}

	private void renderJumpGates(final long currentTime) throws Exception {
		for (final Path path : universe.pathList) {
			path.get3DRenderer().update(path.source.x, path.source.y, path.source.z, renderMaster, currentTime, renderMaster.sceneManager.getTimeOfDay(), 0, path.selected);
		}
		//		for (final Planet planet : universe.planetList) {
		//			for (final Path jumpGate : planet.pathList) {
		//				jumpGate.get3DRenderer().update(planet.x, planet.y, planet.z, renderMaster, currentTime, renderMaster.sceneManager.getTimeOfDay(), 0, jumpGate.selected);
		//			}
		//		}
	}

	private void renderPlanets(final long currentTime) throws Exception {
		for (final Planet planet : universe.planetList) {
			planet.get3DRenderer().update(renderMaster, currentTime, renderMaster.sceneManager.getTimeOfDay(), 0, planet == renderMaster.universe.selectedPlanet);
		}
	}

	private void renderStage() throws Exception {
		int labelIndex = 0;
		// fps
		{
			stringBuilder.setLength(0);
			stringBuilder.append(" FPS ").append(Gdx.graphics.getFramesPerSecond());
			labels.get(labelIndex++).setText(stringBuilder);
		}
		//audio sources
		//		{
		//			stringBuilder.setLength(0);
		//			stringBuilder.append(" audio sources: ").append(renderMaster.sceneManager.audioEngine.getEnabledAudioSourceCount() + " / " + renderMaster.sceneManager.audioEngine.getDisabledAudioSourceCount());
		//			labels.get(labelIndex++).setText(stringBuilder);
		//		}
		//time
		{
			stringBuilder.setLength(0);

			final float time = renderMaster.sceneManager.currentDayTime;
			final int hours = (int) time;
			final int minutes = (int) (60 * ((time - (int) time) * 100) / 100);
			stringBuilder.append(" time ").append(hours).append(":").append(minutes);
			labels.get(labelIndex++).setText(stringBuilder);
		}
		stage.draw();
	}

	private void renderTraders(final long currentTime) throws Exception {
		for (final Planet planet : renderMaster.universe.planetList) {
			int index = 0;
			for (final Trader trader : planet.traderList) {
				trader.get3DRenderer().update(renderMaster, currentTime, renderMaster.sceneManager.getTimeOfDay(), index++, trader == renderMaster.universe.selectedTrader);
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
		//		renderMaster.width = width;
		//		renderMaster.height = height;
		//		renderMaster.sceneClusterManager.sceneManager.camera.viewportWidth = width;
		//		renderMaster.sceneClusterManager.sceneManager.camera.viewportHeight = height;
		//		renderMaster.sceneClusterManager.sceneManager.camera.update();

		//		System.out.println("width = " + width + " height = " + height);
		//		renderMaster.sceneClusterManager.info.resize(width, height);
		render2DMaster.width = width;
		render2DMaster.height = height;

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
	public void resume() {
		// TODO Auto-generated method stub

	}

	//	private void setMaxFramesPerSecond(int maxFramesPerSecond) {
	//		this.maxFramesPerSecond = maxFramesPerSecond;
	//	}

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
		renderMaster.sceneManager.setCameraTo(x, z, setDirty);
	}

	@Override
	public void setShowGood(final ShowGood name) {
		renderMaster.showGood = name;
	}

	@Override
	public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
		switch (button) {
		case Input.Buttons.LEFT:
			//did we select an object?
			//			renderMaster.sceneClusterManager.createCoordinates();
			final GameObject selected = renderMaster.sceneManager.getGameObject(screenX, screenY);
			System.out.println("selected " + selected);
			if (selected != null)
				try {
					universe.setSelected(selected.interactive, true);
				} catch (final Exception e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(), e);
				}
			else
				try {
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
	public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
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

}
