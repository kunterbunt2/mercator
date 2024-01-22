package de.bushnaq.abdalla.mercator.renderer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.Deflater;

import de.bushnaq.abdalla.mercator.audio.synthesis.AudioEngine;
import de.bushnaq.abdalla.mercator.audio.synthesis.MercatorAudioEngine;
import de.bushnaq.abdalla.mercator.renderer.camera.MovingCamera;
import de.bushnaq.abdalla.mercator.renderer.camera.MyCameraInputController;
import de.bushnaq.abdalla.mercator.renderer.reports.Info;
import de.bushnaq.abdalla.mercator.universe.Universe;
import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.path.Path;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.planet.Planet3DRenderer;
import de.bushnaq.abdalla.mercator.universe.sim.trader.Trader;
import org.lwjgl.opengl.GL30C;

import de.bushnaq.abdalla.mercator.desktop.LaunchMode;
import de.bushnaq.abdalla.mercator.shader.DepthOfFieldEffect;
import de.bushnaq.abdalla.mercator.shader.MercatorShaderProvider;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer.FrameBufferBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.FxaaEffect;

import net.mgsx.gltf.scene3d.attributes.FogAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.lights.PointLightEx;
import net.mgsx.gltf.scene3d.lights.SpotLightEx;
import net.mgsx.gltf.scene3d.model.ModelInstanceHack;
import net.mgsx.gltf.scene3d.scene.SceneRenderableSorter;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.scene.Updatable;
import net.mgsx.gltf.scene3d.shaders.PBRCommon;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import net.mgsx.gltf.scene3d.utils.EnvironmentCache;
import net.mgsx.gltf.scene3d.utils.EnvironmentUtil;

public class SceneManager {
	private static final float DAY_AMBIENT_INTENSITY_B = 1.0f;
	private static final float DAY_AMBIENT_INTENSITY_G = 1.0f;
	private static final float DAY_AMBIENT_INTENSITY_R = 1.0f;
	private static final float DAY_SHADOW_INTENSITY = 3.0f;
	private static final Color FOG_VALUE = Color.BLACK;
	private static final float NIGHT_AMBIENT_INTENSITY_B = 0f;
	private static final float NIGHT_AMBIENT_INTENSITY_G = 0f;
	private static final float NIGHT_AMBIENT_INTENSITY_R = 0f;
	private static final float NIGHT_SHADOW_INTENSITY = 0.2f;
	private boolean alwaysDay = true;
	private ColorAttribute ambientLight;
	private float angle;
	private AtlasManager atlasManager;
	public AudioEngine audioEngine = new MercatorAudioEngine();
	private ModelBatch batch;
	public PolygonSpriteBatch batch2D;
	private Texture brdfLUT;
	private MyCameraInputController camController;
	MovingCamera camera;
	private final EnvironmentCache computedEnvironement = new EnvironmentCache();
	float currentDayTime;
	private SceneSkybox daySkyBox;
	//	private ModelBatch defaultBatch;
	private ModelBatch depthBatch;
	//	private ModelBatch depthOfFieldBatch;
	//	public List<ModelInstance> staticModelInstances = new ArrayList<ModelInstance>();
	//	public List<PointLight> staticPointLights = new ArrayList<PointLight>();
	//	private ModelInstance uberModel;
	private Cubemap diffuseCubemap;
	private final ModelCache dynamicCache = new ModelCache();
	public Array<GameObject> dynamicModelInstances = new Array<GameObject>();
	private boolean enableDepthOfField = false;
	public Environment environment = new Environment();
	private Cubemap environmentDayCubemap;
	private Cubemap environmentNightCubemap;
	boolean firstTime = true;
	//	private FrameBuffer depthFbo;
	private FogAttribute fogEquation = null;
	Matrix4 identityMatrix = new Matrix4();
	Info info;
	private boolean infoVisible;
	private final InputMultiplexer inputMultiplexer = new InputMultiplexer();
	private final LaunchMode launchMode;
	//	private ShaderProvider createShaderProvider() {
	//		final PBRShaderConfig config = PBRShaderProvider.createDefaultConfig();
	//		config.numBones = 0;
	//		config.numDirectionalLights = 1;
	//		config.numPointLights = 200;
	//		config.numSpotLights = 0;
	//		return PBRShaderProvider.createDefault(config);
	//	}
	MercatorShaderProvider mercatorShaderProvider;
	private SceneSkybox nightSkyBox;
	//	private GameObject ocean;
	//	private Shader oceanShader;
	//	private ModelBatch oceanBatch;
	//	public List<PointLight> dynamicPointLights = new ArrayList<PointLight>();
	private final PointLightsAttribute pointLights = new PointLightsAttribute();
	private final Vector3 position = new Vector3();
	private FrameBuffer postFbo;
	private final Ray ray = new Ray(new Vector3(), new Vector3());
	Plane reflectionClippingPlane = new Plane(new Vector3(0f, 1f, 0f), -(Planet3DRenderer.WATER_Y - 2));//render everything above the water
	//new Plane(new Vector3(0.0f, -1.0f, 0.0f), 15);//cull everything above 15
	Plane refractionClippingPlane = new Plane(new Vector3(0.0f, -1.0f, 0.0f), (Planet3DRenderer.WATER_Y - 2));//render everything below the water
	private final Array<ModelInstance> renderableProviders = new Array<ModelInstance>();
	private RenderableSorter renderableSorter;
	private final BoundingBox sceneBox = new BoundingBox(new Vector3(-2000, -2000, -1000), new Vector3(2000, 2000, 1000));
	private DirectionalShadowLight shadowLight = null;
	private final Vector3 shadowLightDirection = new Vector3();
	private Cubemap specularCubemap;
	private final int speed = 5;
	private final SpotLightsAttribute spotLights = new SpotLightsAttribute();
	private final ModelCache staticCache = new ModelCache();
	private boolean staticCacheDirty = true;
	private int staticCacheDirtyCount = 0;
	public final Array<GameObject> staticModelInstances = new Array<GameObject>();
	private float timeOfDay = 0;//24h time
	private final Universe universe;
	private final boolean useDynamicCache = false;
	private final boolean useStaticCache = true;
	DepthOfFieldEffect vfxEffect;
	private final VfxManager vfxManager;
	public int visibleDynamicGameObjectCount = 0;
	public int visibleDynamicLightCount = 0;
	private final Array<ModelInstance> visibleDynamicModelInstances = new Array<ModelInstance>();
	public int visibleStaticGameObjectCount = 0;
	public int visibleStaticLightCount = 0;
	private final Array<ModelInstance> visibleStaticModelInstances = new Array<ModelInstance>();
	private FrameBuffer waterReflectionFbo;
	private FrameBuffer waterRefractionFbo;

	public SceneManager(final Universe universe, final InputProcessor inputProcessor, final LaunchMode launchMode) throws Exception {
		this.universe = universe;
		this.launchMode = launchMode;
		createFrameBuffer();
		createEnvironment();
		createCamera();
		audioEngine.create();
		audioEngine.enableHrtf(0);
		createShader();
		createInputProcessor(inputProcessor);
		createStage();
		vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
		vfxEffect = new DepthOfFieldEffect(postFbo, camera);
		vfxManager.addEffect(vfxEffect);
		vfxManager.addEffect(new FxaaEffect());
	}

	public void add(final PointLight pointLight, final boolean dynamic) {
		if (dynamic) {
			environment.add(pointLight);
		} else {
			environment.add(pointLight);
		}
	}

	public void addDynamic(final GameObject instance) {
		dynamicModelInstances.add(instance);
	}

	public void addStatic(final GameObject instance) {
		staticModelInstances.add(instance);
		if (isVisible(instance)) {
			staticCacheDirty = true;
			staticCacheDirtyCount++;
			visibleStaticModelInstances.add(instance.instance);
		}
	}

	public void clearViewport() {
		//		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		//		Gdx.gl.glEnable(GL20.GL_BLEND);
		//		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		//		Gdx.gl.glDepthMask(true);
		//		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//		Gdx.gl.glClear(GL30.GL_ALPHA_BITS);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);//modelBatch will change this state anyway, so better enable it when you need it
		Gdx.gl.glClearColor(FOG_VALUE.r, FOG_VALUE.g, FOG_VALUE.b, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	}

	private void createCamera() throws Exception {
		camera = new MovingCamera(67f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Planet planet = universe.findBusyCenterPlanet();
		if (planet == null)
			planet = universe.planetList.get(0);

		final Vector3 lookat = new Vector3(planet.x, 0, planet.z);
		camera.position.set(lookat.x + 300f / Universe.WORLD_SCALE, lookat.y + 500f / Universe.WORLD_SCALE, lookat.z + 400f / Universe.WORLD_SCALE);
		//		camera.position.set(300f, 0f, 0f);//-270, pitch=0
		//		camera.position.set(0f, 0f, 300f);//180
		//		camera.position.set(-300f, 0f, 0f);//-90
		//		camera.position.set(0f, 0f, -300f);//180
		camera.up.set(0, 1, 0);
		camera.lookAt(lookat);
		camera.near = 8f;
		camera.far = 8000f;
		camera.update();
		camera.setDirty(true);
	}

	void createCoordinates() {

		final Vector3 position = new Vector3(0, 0, 0);
		final Vector3 xVector = new Vector3(1, 0, 0);
		final Vector3 yVector = new Vector3(0, 1, 0);
		final Vector3 zVector = new Vector3(0, 0, 1);
		final Ray rayX = new Ray(position, xVector);
		final Ray rayY = new Ray(position, yVector);
		final Ray rayZ = new Ray(position, zVector);
		createRay(rayX);
		createRay(rayY);
		createRay(rayZ);
	}

	private void createEnvironment() {
		//shadow
		shadowLight = new DirectionalShadowLight(4048 * 2, 4048 * 2);
		final Matrix4 m = new Matrix4();
		sceneBox.mul(m);
		shadowLight.setBounds(sceneBox);
		shadowLight.direction.set(-.5f, -.7f, .5f).nor();
		shadowLight.color.set(Color.WHITE);
		shadowLight.intensity = 0.1f;
		environment.add(shadowLight);
		// setup IBL (image based lighting)
		//		setupImageBasedLightingByFaceNames("ruins", "jpg", "png", "jpg", 10);
		setupImageBasedLightingByFaceNames("clouds", "jpg", "jpg", "jpg", 10);
		// setup skybox
		daySkyBox = new SceneSkybox(environmentDayCubemap);
		nightSkyBox = new SceneSkybox(environmentNightCubemap);

		environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));
		environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
		environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
		environment.set(new PBRFloatAttribute(PBRFloatAttribute.ShadowBias, 0f));
		final float lum = 0.0f;
		ambientLight = new ColorAttribute(ColorAttribute.AmbientLight, lum, lum, lum, 1.0f);
		environment.set(ambientLight);
		environment.set(new ColorAttribute(ColorAttribute.Fog, FOG_VALUE));
		environment.set(new FogAttribute(FogAttribute.FogEquation));
		fogEquation = environment.get(FogAttribute.class, FogAttribute.FogEquation);
	}

	private String createFileName(final Date date, final String append) {
		final String pattern = "yyyy-MM-dd-HH-mm-ss";
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		final String dateAsString = simpleDateFormat.format(date);
		final String fileName = "docs/pics/" + dateAsString + "-" + append + ".png";
		return fileName;
	}

	private void createFrameBuffer() {
		{
			final FrameBufferBuilder frameBufferBuilder = new FrameBufferBuilder(Gdx.graphics.getWidth() , Gdx.graphics.getHeight() );
			frameBufferBuilder.addColorTextureAttachment(GL30.GL_RGBA8, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE);
			frameBufferBuilder.addDepthTextureAttachment(GL30.GL_DEPTH_COMPONENT24, GL20.GL_UNSIGNED_BYTE);
			waterRefractionFbo = frameBufferBuilder.build();
		}
		{
			final FrameBufferBuilder frameBufferBuilder = new FrameBufferBuilder(Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
			frameBufferBuilder.addColorTextureAttachment(GL30.GL_RGBA8, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE);
			frameBufferBuilder.addDepthRenderBuffer(GL30.GL_DEPTH_COMPONENT24);
			waterReflectionFbo = frameBufferBuilder.build();
		}
		{
			final FrameBufferBuilder frameBufferBuilder = new FrameBufferBuilder(Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
			frameBufferBuilder.addColorTextureAttachment(GL30.GL_RGBA8, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE);
			frameBufferBuilder.addDepthTextureAttachment(GL30.GL_DEPTH_COMPONENT24, GL20.GL_UNSIGNED_BYTE);
			postFbo = frameBufferBuilder.build();
		}
	}

	private void createInputProcessor(final InputProcessor inputProcessor) throws Exception {
		camController = new MyCameraInputController(camera);
		camController.scrollFactor = -0.1f;
		camController.translateUnits = 1000f;
		inputMultiplexer.addProcessor(inputProcessor);
		inputMultiplexer.addProcessor(camController);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	private void createRay(final Ray ray) {

		final float length = 10000f;
		final Vector3 direction = new Vector3(ray.direction.x, ray.direction.y, ray.direction.z);
		final Vector3 position = ray.origin.cpy();
		final GameObject instance = new GameObject(new ModelInstanceHack(staticModelInstances.get(0).instance.model), null);
		//		instance.instance.materials.get(0).set(ColorAttribute.createDiffuse(Color.RED));
		addDynamic(instance);
		final Vector3 xVector = new Vector3(1, 0, 0);
		direction.nor();
		position.x += direction.x * length / 2;
		position.y += direction.y * length / 2;
		position.z += direction.z * length / 2;
		instance.instance.transform.setToTranslation(position);
		instance.instance.transform.rotate(xVector, direction);
		instance.instance.transform.scale(length, 2, 2);
		instance.update();
		//		System.out.println("created ray");
	}

	private void createShader() {
		atlasManager = new AtlasManager();
		atlasManager.init();
		renderableSorter = new SceneRenderableSorter();
		depthBatch = new ModelBatch(PBRShaderProvider.createDefaultDepth(0));
		//		oceanBatch = new ModelBatch(new OceanShaderProvider());
		//		depthOfFieldBatch = new ModelBatch(new DepthOfFieldShaderProvider());
		batch = new ModelBatch(createShaderProvider(), renderableSorter);
		//		batch = new ModelBatch(PBRShaderProvider.createDefaultDepth(0));
		batch2D = new CustomizedSpriteBatch(5460);
	}

	private ShaderProvider createShaderProvider() {
		final PBRShaderConfig config = PBRShaderProvider.createDefaultConfig();
		config.numBones = 0;
		config.numDirectionalLights = 1;
		config.numPointLights = 100;
		config.numSpotLights = 0;
		mercatorShaderProvider = MercatorShaderProvider.createDefault(config, waterRefractionFbo, waterReflectionFbo, postFbo, universe.size);
		return mercatorShaderProvider;
	}

	private void createStage() throws Exception {
		info = new Info(getAtlasManager(), batch2D, inputMultiplexer);
		info.createStage();
		//		final int height = 12;
		//		stage = new Stage();
		//		font = new BitmapFont();
		//		for (int i = 0; i < 8; i++) {
		//			final Label label = new Label(" ", new Label.LabelStyle(font, Color.WHITE));
		//			label.setPosition(0, i * height);
		//			stage.addActor(label);
		//			labels.add(label);
		//		}
		//		stringBuilder = new StringBuilder();
	}

	private void cullLights() {
		visibleDynamicLightCount = 0;
		final PointLightsAttribute pla = environment.get(PointLightsAttribute.class, PointLightsAttribute.Type);
		if (pla != null) {
			for (final PointLight light : pla.lights) {
				if (light instanceof PointLightEx) {
					final PointLightEx l = (PointLightEx) light;
					if (l.range != null && !camera.frustum.sphereInFrustum(l.position, l.range)) {
						pointLights.lights.removeValue(l, true);
					}
				} else if (light instanceof PointLight) {
					final PointLight l = light;
					if (!camera.frustum.sphereInFrustum(l.position, 50)) {
						pointLights.lights.removeValue(l, true);
					} else {
						visibleDynamicLightCount++;
					}
				}
			}
		}
		final SpotLightsAttribute sla = environment.get(SpotLightsAttribute.class, SpotLightsAttribute.Type);
		if (sla != null) {
			for (final SpotLight light : sla.lights) {
				if (light instanceof SpotLightEx) {
					final SpotLightEx l = (SpotLightEx) light;
					if (l.range != null && !camera.frustum.sphereInFrustum(l.position, l.range)) {
						spotLights.lights.removeValue(l, true);
					}
				} else if (light instanceof SpotLight) {
					final SpotLight l = light;
					if (!camera.frustum.sphereInFrustum(l.position, 50)) {
						spotLights.lights.removeValue(l, true);
					} else {
						visibleDynamicLightCount++;
					}
				}
			}
		}
	}

	public void dispose() throws Exception {
		vfxManager.dispose();
		vfxEffect.dispose();
		mercatorShaderProvider.dispose();
		postFbo.dispose();
		waterReflectionFbo.dispose();
		waterRefractionFbo.dispose();
		audioEngine.dispose();
		info.dispose();
		batch.dispose();
		batch2D.dispose();
		depthBatch.dispose();
		diffuseCubemap.dispose();
		environmentNightCubemap.dispose();
		environmentDayCubemap.dispose();
		specularCubemap.dispose();
		brdfLUT.dispose();
		//		font.dispose();
	}

	public void end() {
	}

	public AtlasManager getAtlasManager() {
		return atlasManager;
	}

	public MovingCamera getCamera() {
		return camera;
	}

	public GameObject getGameObject(final int screenX, final int screenY) {
		final Ray ray = camera.getPickRay(screenX, screenY);
		//		createRay(ray);
		GameObject result = null;
		float distance = -1;
		for (int i = 0; i < dynamicModelInstances.size; ++i) {
			final GameObject instance = dynamicModelInstances.get(i);
			if (instance.interactive != null) {
				instance.instance.transform.getTranslation(position);
				position.add(instance.center);
				final float dist2 = ray.origin.dst2(position);
				if (distance >= 0f && dist2 > distance)
					continue;
				if (Intersector.intersectRayBoundsFast(ray, instance.transformedBoundingBox)) {
					result = instance;
					distance = dist2;
				}
			}
		}
		for (int i = 0; i < staticModelInstances.size; ++i) {
			final GameObject instance = staticModelInstances.get(i);
			if (instance.interactive != null) {
				instance.instance.transform.getTranslation(position);
				position.add(instance.center);
				final float dist2 = ray.origin.dst2(position);
				if (distance >= 0f && dist2 > distance)
					continue;
				if (Intersector.intersectRayBoundsFast(ray, instance.transformedBoundingBox)) {
					result = instance;
					distance = dist2;
				}
			}
		}
		return result;
	}

	public Array<ModelInstance> getRenderableProviders() {
		return renderableProviders;
	}

	public DirectionalShadowLight getShadowLight() {
		return shadowLight;
	}

	public float getTimeOfDay() {
		return timeOfDay;
	}

	private void handleFrameBufferScreenshot(boolean takeScreenShot, final FrameBuffer frameBuffer, final String name) {
		if (takeScreenShot) {
			final Date date = new Date();
			final String fileName = createFileName(date, name);
			writeFrameBufferToDisk(fileName, frameBuffer);
			takeScreenShot = false;
		}

	}

	void handleQueuedScreenshot(final boolean takeScreenShot) {
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

	public boolean isAlwaysDay() {
		return alwaysDay;
	}

	public boolean isDay() {
		return (alwaysDay || (timeOfDay > 6 && timeOfDay <= 18));
	}

	public boolean isEnableDepthOfField() {
		return enableDepthOfField;
	}

	public boolean isInfoVisible() {
		return infoVisible;
	}

	public boolean isNight() {
		return (!alwaysDay && (timeOfDay > 19 || timeOfDay <= 5));
	}

	private boolean isVisible(final GameObject gameObject) {
		return camera.frustum.boundsInFrustum(gameObject.transformedBoundingBox);
	}

	public void postProcessRender() {
		//		clearViewport();
		//		batch.begin(info.getViewport().getCamera());
		//		batch.render(postModelInstance, computedEnvironement);
		//		batch.end();

		// Begin render to an off-screen buffer.
		//		vfxManager.beginInputCapture();

		// Here's where game render should happen.
		// For demonstration purposes we just render some simple geometry.
		//		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//		shapeRenderer.setColor(Color.PINK);
		//		shapeRenderer.rect(250f, 100f, 250f, 175f);
		//		shapeRenderer.setColor(Color.ORANGE);
		//		shapeRenderer.circle(200f, 250f, 100f);
		//		shapeRenderer.end();
		//		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		//		batch2D.disableBlending();
		//		batch2D.setProjectionMatrix(info.getViewport().getCamera().combined);
		//		batch2D.begin();
		//				batch2D.draw(waterRefractionFbo.getColorBufferTexture(), 0, 1080 - 1080 / 4, 1920 / 4, 1080 / 4, 0, 0, 1920, 1080, false, true);
		//				batch2D.draw(waterReflectionFbo.getColorBufferTexture(), 1920 - 1920 / 4, 1080 - 1080 / 4, 1920 / 4, 1080 / 4, 0, 0, 1920, 1080, false, true);
		//		batch2D.draw(postFbo.getColorBufferTexture(), 0, 0, 1920, 1080, 0, 0, 1920, 1080, false, true);
		//				batch2D.draw(postFbo.getTextureAttachments().get(1), 0, 0, 1920, 1080, 0, 0, 1920, 1080, false, true);
		//		batch2D.end();

		// End render to an off-screen buffer.
		//		vfxManager.endInputCapture();

		// Apply the effects chain to the captured frame.
		// In our case, only one effect (gaussian blur) will be applied.
		if (isEnableDepthOfField()) {
			// Clean up the screen.
			Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			// Clean up internal buffers, as we don't need any information from the last render.
			vfxManager.cleanUpBuffers();
			vfxManager.applyEffects();
			// Render result to the screen.
			vfxManager.renderToScreen();
		} else {
			//			vfxManager.setDisabled(true);
			//			vfxManager.useAsInput(postFbo.getColorBufferTexture());
			//			vfxManager.applyEffects();
			clearViewport();
			Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
			batch2D.disableBlending();
			batch2D.setProjectionMatrix(info.getViewport().getCamera().combined);
			batch2D.begin();
			//				//				batch2D.draw(waterRefractionFbo.getColorBufferTexture(), 0, 1080 - 1080 / 4, 1920 / 4, 1080 / 4, 0, 0, 1920, 1080, false, true);
			//				//				batch2D.draw(waterReflectionFbo.getColorBufferTexture(), 1920 - 1920 / 4, 1080 - 1080 / 4, 1920 / 4, 1080 / 4, 0, 0, 1920, 1080, false, true);
			batch2D.draw(postFbo.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, true);
			////								batch2D.draw(postFbo.getTextureAttachments().get(1), 0, 0, 1920, 1080, 0, 0, 1920, 1080, false, true);
			//								batch2D.draw(waterRefractionFbo.getTextureAttachments().get(1), 0, 0, 1920, 1080, 0, 0, 1920, 1080, false, true);
			batch2D.end();
			//batch2D.enableBlending();

		}
	}

	public boolean removeDynamic(final GameObject instance) {
		return dynamicModelInstances.removeValue(instance, true);
	}

	public boolean removeStatic(final GameObject instance) {
		final boolean result = staticModelInstances.removeValue(instance, true);
		if (isVisible(instance)) {
			staticCacheDirty = true;
			staticCacheDirtyCount++;
			visibleStaticModelInstances.removeValue(instance.instance, true);
		}
		return result;
	}

	public void render(final long currentTime, final float deltaTime, final boolean takeScreenShot) throws Exception {
		currentDayTime = currentTime - ((currentTime / (50000L / speed * 24)) * (50000L / speed * 24));
		currentDayTime /= 50000 / speed;
		updateEnvironment(currentDayTime);
		renderableProviders.clear();
		renderDynamicModelInstanceCache();
		renderStaticModelInstanceCache();

		updateFog();
		update(deltaTime);
		renderShadows(takeScreenShot);
		PBRCommon.enableSeamlessCubemaps();
		computedEnvironement.shadowMap = environment.shadowMap;
		//		handleFrameBufferScreenshot(takeScreenShot);

		//FBO
		{
			//waterRefractionFbo
			Gdx.gl.glEnable(GL30C.GL_CLIP_DISTANCE0);
			waterRefractionFbo.begin();
			mercatorShaderProvider.setClippingPlane(refractionClippingPlane);
			renderColors(takeScreenShot);
			waterRefractionFbo.end();
			handleFrameBufferScreenshot(takeScreenShot, waterRefractionFbo, "refractionVbo");

			//waterReflectionFbo
			mercatorShaderProvider.setClippingPlane(reflectionClippingPlane);
			final float cameraYDistance = 2 * (camera.position.y - Planet3DRenderer.WATER_Y);
			final float lookatYDistance = 2 * (camera.lookat.y - Planet3DRenderer.WATER_Y);
			camera.position.y -= cameraYDistance;
			camera.lookat.y -= lookatYDistance;
			camera.up.set(0, 1, 0);
			camera.lookAt(camera.lookat);
			camera.update();
			waterReflectionFbo.begin();
			renderColors(takeScreenShot);
			waterReflectionFbo.end();
			camera.position.y += cameraYDistance;
			camera.lookat.y += lookatYDistance;
			camera.up.set(0, 1, 0);
			camera.lookAt(camera.lookat);
			camera.update();
			handleFrameBufferScreenshot(takeScreenShot, waterReflectionFbo, "reflectionVbo");

			Gdx.gl.glDisable(GL30C.GL_CLIP_DISTANCE0);
		}
		//		if (firstTime) {
		postFbo.begin();
		renderColors(takeScreenShot);
		batch2D.begin();
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		batch2D.enableBlending();
		batch2D.setProjectionMatrix(camera.combined);
		render3DText();
		batch2D.end();
		batch2D.setTransformMatrix(identityMatrix);//fix transformMatrix
		postFbo.end();
		firstTime = false;
		//		}
		//		renderStage();
		//		mercatorShaderProvider.postShader.begin(info.getViewport().getCamera(), mercatorShaderProvider.postShader.context);
		//		mercatorShaderProvider.postShader.end();

		//				clearViewport();
		//				Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		//				batch2D.disableBlending();
		//				batch2D.setProjectionMatrix(info.getViewport().getCamera().combined);
		//				batch2D.begin();
		//				//				batch2D.draw(waterRefractionFbo.getColorBufferTexture(), 0, 1080 - 1080 / 4, 1920 / 4, 1080 / 4, 0, 0, 1920, 1080, false, true);
		//				//				batch2D.draw(waterReflectionFbo.getColorBufferTexture(), 1920 - 1920 / 4, 1080 - 1080 / 4, 1920 / 4, 1080 / 4, 0, 0, 1920, 1080, false, true);
		//				//				batch2D.draw(postFbo.getColorBufferTexture(), 0, 0, 1920, 1080, 0, 0, 1920, 1080, false, true);
		////								batch2D.draw(postFbo.getTextureAttachments().get(1), 0, 0, 1920, 1080, 0, 0, 1920, 1080, false, true);
		//								batch2D.draw(waterRefractionFbo.getTextureAttachments().get(1), 0, 0, 1920, 1080, 0, 0, 1920, 1080, false, true);
		//				batch2D.end();

		audioEngine.begin(camera);
		audioEngine.end();
		camera.setDirty(false);
		staticCacheDirtyCount = 0;
	}

	private void render3DText() {
		for (final Planet planet : universe.planetList) {
			planet.get3DRenderer().renderText(this, 0, planet == universe.selectedPlanet);
		}
		for (final Planet planet : universe.planetList) {
			int index = 0;
			for (final Good good : planet.getGoodList()) {
				good.get3DRenderer().renderText(planet.x, planet.y, planet.z, this, index++);
			}
		}
		for (final Planet planet : universe.planetList) {
			int index = 0;
			for (final Trader trader : planet.traderList) {
				trader.get3DRenderer().renderText(this, index++, trader == universe.selectedTrader);
			}

		}
		if (launchMode == LaunchMode.development)
		{
			for (final Path path : universe.pathList) {
				int index = 0;
				path.get3DRenderer().renderText(this, index++, path.selected);
			}
		}
	}

	/**
	 * Render colors only. You should call {@link #renderShadows()} before. (useful
	 * when you're using your own frame buffer to render scenes)
	 */
	private void renderColors(final boolean takeScreenShot) {
		clearViewport();

		batch.begin(camera);
		if (useStaticCache)
			batch.render(staticCache, computedEnvironement);
		//		else
		//			batch.render(visibleStaticModelInstances, computedEnvironement);
		if (useDynamicCache)
			batch.render(dynamicCache, computedEnvironement);
		else
			batch.render(visibleDynamicModelInstances, computedEnvironement);
		//		batch.render(ocean.instance, oceanShader);

		if (daySkyBox != null && isDay())
			batch.render(daySkyBox);
		else if (nightSkyBox != null && isNight())
			batch.render(nightSkyBox);
		batch.end();
	}

	/**
	 * Render only depth (packed 32 bits), usefull for post processing effects. You
	 * typically render it to a FBO with depth enabled.
	 */
	//	private void renderDepth() {
	//		renderDepth(camera);
	//	}

	private void renderDepth(final Camera camera) {
		depthBatch.begin(camera);
		if (useStaticCache)
			depthBatch.render(staticCache);
		//		else
		//			depthBatch.render(visibleStaticModelInstances);
		if (useDynamicCache)
			depthBatch.render(dynamicCache);
		else
			depthBatch.render(visibleDynamicModelInstances);
		depthBatch.end();
	}

	private void renderDynamicModelInstanceCache() {

		{
			visibleDynamicGameObjectCount = 0;
			if (useDynamicCache) {
				dynamicCache.begin(camera);
				for (final GameObject instance : dynamicModelInstances) {
					if (isVisible(instance)) {
						dynamicCache.add(instance.instance);
						visibleDynamicGameObjectCount++;
						renderableProviders.add(instance.instance);
					}
				}
				dynamicCache.end();
			} else {
				visibleDynamicModelInstances.clear();
				for (final GameObject instance : dynamicModelInstances) {
					if (isVisible(instance)) {
						visibleDynamicGameObjectCount++;
						renderableProviders.add(instance.instance);
						visibleDynamicModelInstances.add(instance.instance);
					}
				}
			}
		}
	}

	/**
	 * Render shadows only to interal frame buffers. (useful when you're using your
	 * own frame buffer to render scenes)
	 */
	public void renderShadows(final boolean takeScreenShot) {
		final DirectionalLight light = shadowLight;
		if (light instanceof DirectionalShadowLight) {
			final DirectionalShadowLight shadowLight = (DirectionalShadowLight) light;
			shadowLight.begin();
			renderDepth(shadowLight.getCamera());
			handleFrameBufferScreenshot(takeScreenShot, shadowLight.getFrameBuffer(), "shadowBuffer");
			shadowLight.end();

			environment.shadowMap = shadowLight;
		} else {
			environment.shadowMap = null;
		}
	}

	protected void renderStage() throws Exception {
		if (infoVisible) {
			info.update(universe, universe.selected, this);
			info.act(Gdx.graphics.getDeltaTime());
			info.draw();
		}
	}

	private void renderStaticModelInstanceCache() throws Exception {

		if (useStaticCache) {
			if (staticCacheDirty) {
				// there where visible instances added or removed
				visibleStaticGameObjectCount = 0;
				staticCache.begin(camera);
				for (final ModelInstance instance : visibleStaticModelInstances) {
					staticCache.add(instance);
					visibleStaticGameObjectCount++;
					renderableProviders.add(instance);
				}
				staticCache.end();
				staticCacheDirty = false;
			}
			if (camera.isDirty()) {
				//				audioEngine.setListenerPosition(camera.position);
				visibleStaticGameObjectCount = 0;
				visibleStaticModelInstances.clear();
				staticCache.begin(camera);
				for (final GameObject instance : staticModelInstances) {
					if (isVisible(instance)) {
						visibleStaticModelInstances.add(instance.instance);
						staticCache.add(instance.instance);
						visibleStaticGameObjectCount++;
						renderableProviders.add(instance.instance);
					}
				}
				staticCache.end();
				staticCacheDirty = false;
			}

		}
		//		else {
		//			if (staticCacheDirty || camera.isDirty()) {
		//				visibleStaticGameObjectCount = 0;
		//				for (GameObject instance : staticModelInstances) {
		//					if (isVisible(instance)) {
		//						visibleStaticModelInstances.add(instance.instance);
		//						visibleStaticGameObjectCount++;
		//						renderableProviders.add(instance.instance);
		//					}
		//				}
		//				staticCacheDirty = false;
		//			}
		//
		//		}
	}

	public void setAlwaysDay(final boolean alwaysDay) {
		this.alwaysDay = alwaysDay;
	}

	private void setAmbientLight(final float rLum, final float gLum, final float bLum) {
		ambientLight.color.set(rLum, gLum, bLum, 1f);
	}

	public void setCamera(final Vector3 position, final Vector3 up, final Vector3 LookAt) throws Exception {
		camera.position.set(position);
		camera.up.set(up);
		camera.lookAt(LookAt);
		camera.update();
		//		camController.notifyListener(camera);
	}

	public void setCameraTo(final float x, final float z, final boolean setDirty) throws Exception {
		camera.position.add(x - camera.lookat.x, 0, z - camera.lookat.z);
		camera.update();
		camera.setDirty(setDirty);//only set dirty if requested
		camera.lookat.x = x;
		camera.lookat.z = z;
		//		camController.notifyListener(camera);
	}

	//	@Override
	//	public void setPositionDirectionUp(final Vector3 position, final Vector3 direction, final Vector3 up) throws Exception {
	//		if (audioEngine != null) {
	//			audioEngine.setListenerPosition(position);
	//			audioEngine.setListenerOrientation(direction, up);
	//		}
	//	}

	public void setEnableDepthOfField(final boolean enableDepthOfField) {
		this.enableDepthOfField = enableDepthOfField;
	}

	public void setInfoVisible(final boolean infoVisible) {
		this.infoVisible = infoVisible;
	}

	private void setShadowLight(final float lum) {
		shadowLight.intensity = lum;
		//		shadowLight.color.set(lum, lum, lum, 1f);
	}

	private void setupImageBasedLightingByFaceNames(final String name, final String diffuseExtension, final String environmentExtension, final String specularExtension, final int specularIterations) {
		diffuseCubemap = EnvironmentUtil.createCubemap(new InternalFileHandleResolver(), "textures/" + name + "/diffuse/diffuse_", "_0." + diffuseExtension, EnvironmentUtil.FACE_NAMES_FULL);
		environmentDayCubemap = EnvironmentUtil.createCubemap(new InternalFileHandleResolver(), "textures/" + name + "/environmentDay/environment_", "_0." + environmentExtension, EnvironmentUtil.FACE_NAMES_FULL);
		environmentNightCubemap = EnvironmentUtil.createCubemap(new InternalFileHandleResolver(), "textures/" + name + "/environmentNight/environment_", "_0." + environmentExtension, EnvironmentUtil.FACE_NAMES_FULL);
		specularCubemap = EnvironmentUtil.createCubemap(new InternalFileHandleResolver(), "textures/" + name + "/specular/specular_", "_", "." + specularExtension, specularIterations, EnvironmentUtil.FACE_NAMES_FULL);
		brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

		//		// setup quick IBL (image based lighting)
		//		DirectionalLightEx light = new DirectionalLightEx();
		//		light.direction.set(1, -3, 1).nor();
		//		light.color.set(Color.WHITE);
		//		IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
		//		environmentCubemap = iblBuilder.buildEnvMap(1024);
		//		diffuseCubemap = iblBuilder.buildIrradianceMap(256);
		//		specularCubemap = iblBuilder.buildRadianceMap(10);
		//		iblBuilder.dispose();
	}
	//	private void setupImageBasedLightingByNegpos(String name) {
	//		diffuseCubemap = EnvironmentUtil.createCubemap(new InternalFileHandleResolver(),
	//				"textures/" + name + "/diffuse/diffuse_", ".jpg", EnvironmentUtil.FACE_NAMES_NEG_POS);
	//		environmentCubemap = EnvironmentUtil.createCubemap(new InternalFileHandleResolver(),
	//				"textures/" + name + "/environment/environment_", ".jpg", EnvironmentUtil.FACE_NAMES_NEG_POS);
	//		specularCubemap = EnvironmentUtil.createCubemap(new InternalFileHandleResolver(),
	//				"textures/" + name + "/specular/specular_", "_", ".jpg", 10, EnvironmentUtil.FACE_NAMES_NEG_POS);
	//		brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));
	//	}

	/**
	 * should be called in order to perform light culling, skybox update and
	 * animations.
	 *
	 * @param delta
	 */
	private void update(final float delta) {
		if (camera != null) {
			updateEnvironment();
			for (final RenderableProvider r : renderableProviders) {
				if (r instanceof Updatable) {
					((Updatable) r).update(camera, delta);
				}
			}
			if (daySkyBox != null && isDay())
				daySkyBox.update(camera, delta);
			else if (nightSkyBox != null && isNight())
				nightSkyBox.update(camera, delta);
		}
	}

	public void updateCamera(final float centerXD, final float centerYD, final float centerZD) {
		camera.translate(centerXD, 0, centerZD);
		camera.update();
	}

	private void updateEnvironment() {
		computedEnvironement.setCache(environment);
		pointLights.lights.clear();
		spotLights.lights.clear();
		if (environment != null) {
			for (final Attribute a : environment) {
				if (a instanceof PointLightsAttribute) {
					pointLights.lights.addAll(((PointLightsAttribute) a).lights);
					computedEnvironement.replaceCache(pointLights);
				} else if (a instanceof SpotLightsAttribute) {
					spotLights.lights.addAll(((SpotLightsAttribute) a).lights);
					computedEnvironement.replaceCache(spotLights);
				} else {
					computedEnvironement.set(a);
				}
			}
		}
		cullLights();
	}

	public void updateEnvironment(final float timeOfDay) {
		if (Math.abs(this.timeOfDay - timeOfDay) > 0.01) {
			angle = (float) (Math.PI * (timeOfDay - 6) / 12);
			shadowLightDirection.x = (float) Math.cos(angle);
			shadowLightDirection.z = Math.abs((float) (Math.sin(angle)));
			shadowLightDirection.y = -Math.abs((float) Math.sin(angle));
			shadowLightDirection.nor();
			shadowLight.setDirection(shadowLightDirection);

			// day break
			if (!alwaysDay && timeOfDay > 5 && timeOfDay <= 6) {
				final float intensity = (timeOfDay - 5);
				final float r = DAY_AMBIENT_INTENSITY_R * intensity;
				final float g = DAY_AMBIENT_INTENSITY_G * intensity;
				final float b = DAY_AMBIENT_INTENSITY_B * intensity;
				setShadowLight(DAY_SHADOW_INTENSITY * intensity);
				setAmbientLight(r, g, b);
			}
			// day
			else if (isDay()) {
				final float intensity = 1.0f;
				final float r = DAY_AMBIENT_INTENSITY_R;
				final float g = DAY_AMBIENT_INTENSITY_G;
				final float b = DAY_AMBIENT_INTENSITY_B;
				setShadowLight(DAY_SHADOW_INTENSITY * intensity);
				setAmbientLight(r, g, b);
			}
			// sunset
			else if (timeOfDay > 18 && timeOfDay <= 19) {
				final float intensity = 1.0f - (timeOfDay - 18);
				final float r = DAY_AMBIENT_INTENSITY_R * intensity;
				final float g = DAY_AMBIENT_INTENSITY_G * intensity;
				final float b = DAY_AMBIENT_INTENSITY_B * intensity;
				setShadowLight(DAY_SHADOW_INTENSITY * intensity);
				setAmbientLight(r, g, b);
			}
			// night
			else if (isNight()) {
				//				setShadowLight(0.01f);
				//				setAmbientLight(0.0f, 0.0f, 0.0f);
				final float intensity = (float) Math.abs(Math.abs(Math.sin(angle)));
				final float r = NIGHT_AMBIENT_INTENSITY_R * intensity;
				final float g = NIGHT_AMBIENT_INTENSITY_G * intensity;
				final float b = NIGHT_AMBIENT_INTENSITY_B * intensity;
				setShadowLight(NIGHT_SHADOW_INTENSITY * intensity);
				setAmbientLight(r, g, b);
			}
			this.timeOfDay = timeOfDay;
		}
	}

	private void updateFog() {
		if (fogEquation != null) {
			// fogEquation.x is where the fog begins
			// .y should be where it reaches 100%
			// then z is how quickly it falls off
			//			fogEquation.value.set(MathUtils.lerp(sceneManager.camera.near, sceneManager.camera.far, (FOG_X + 1f) / 2f),
			//					MathUtils.lerp(sceneManager.camera.near, sceneManager.camera.far, (FAG_Y + 1f) / 2f),
			//					1000f * (FOG_Z + 1f) / 2f);
			fogEquation.value.set(3000, 5000, 0.5f);
		}
	}

	private void writeFrameBufferToDisk(final String fileName, final FrameBuffer frameBuffer) {
		frameBuffer.bind();
		//		final FrameBuffer frameBuffer = shadowLight.getFrameBuffer();
		final Texture texture = frameBuffer.getColorBufferTexture();
		//		TextureData textureData = texture.getTextureData();
		//		if (!textureData.isPrepared()) {
		//			textureData.prepare();
		//		}
		//		Pixmap pixmap = textureData.consumePixmap();
		final Pixmap frameBufferPixmap = Pixmap.createFromFrameBuffer(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
		//		final Pixmap frameBufferPixmap = ScreenUtils.getFrameBufferPixmap(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
		PixmapIO.writePNG(Gdx.files.local(fileName), frameBufferPixmap, Deflater.DEFAULT_COMPRESSION, true);
		FrameBuffer.unbind();
	}

}
