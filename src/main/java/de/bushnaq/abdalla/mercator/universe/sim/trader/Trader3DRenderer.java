package de.bushnaq.abdalla.mercator.universe.sim.trader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bushnaq.abdalla.mercator.universe.good.Good;
import de.bushnaq.abdalla.mercator.universe.good.Good3DRenderer;
import de.bushnaq.abdalla.mercator.universe.good.GoodType;
import de.bushnaq.abdalla.mercator.universe.jumpgate.JumpGate3DRenderer;
import de.bushnaq.abdalla.mercator.universe.planet.Planet3DRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bushnaq.abdalla.mercator.audio.synthesis.MercatorSynthesizer;
import de.bushnaq.abdalla.mercator.renderer.GameObject;
import de.bushnaq.abdalla.mercator.renderer.ObjectRenderer;
import de.bushnaq.abdalla.mercator.renderer.Render3DMaster;
import de.bushnaq.abdalla.mercator.renderer.SceneManager;
import de.bushnaq.abdalla.mercator.renderer.Screen3D;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.model.ModelInstanceHack;

public class Trader3DRenderer extends ObjectRenderer {

	private static final float LIGHT_DISTANCE = 10f;
	private static final float LIGHT_INTENSITY = 10000f;
	private static int NUMBER_OF_LIGHTS = 1;
	private static final Color TRADER_NAME_COLOR = Color.WHITE;
	private static final float TRADER_X_SIZE = 16/* 2.4f */;
	private static final float TRADER_Y_SIZE = 8/* 1.2f */;
	private static final float TRADER_Z_SIZE = 16/* 1.2f */;
	private final Vector3 direction = new Vector3();//intermediate value
	private final List<GameObject> goodInstances = new ArrayList<>();
	private GameObject instance;
	private boolean lastSelected = false;
	private long lastTransaction = 0;
	private final List<GameObject> lightGameObjects = new ArrayList<>();
	private final Vector3 lightScaling = new Vector3(2.0f, 2.0f, 2.0f);
	private final Vector3 lightTranslation = new Vector3();
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final List<PointLight> pointLights = new ArrayList<>();
	float[] position = new float[3];
	private final Vector3 scaling = new Vector3();//intermediate value
	private final Vector3 shift = new Vector3();//intermediate value
	Vector3 speed = new Vector3(0, 0, 0);
	private MercatorSynthesizer synth;
	private final Trader trader;
	private int TraderColorIndex = -1;
	private final Vector3 translation = new Vector3();//intermediate value
	private final Map<GoodType, List<GameObject>> unusedMls = new HashMap<>();
	private final Map<GoodType, List<GameObject>> usedMls = new HashMap<>();

	float[] velocity = new float[3];

	public Trader3DRenderer(final Trader trader) {
		this.trader = trader;
	}

	@Override
	public void create(final float x, final float y, final Render3DMaster renderMaster) {
		try {
			createTader(renderMaster);
			createLights(renderMaster);
			createGoods(renderMaster);
			synth = renderMaster.sceneManager.audioEngine.createAudioProducer(MercatorSynthesizer.class);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void createGoods(final Render3DMaster renderMaster) {
		goodInstances.clear();
		for (final Good g : trader.getGoodList()) {
			final List<GameObject> unused = getUnusedGoodList(g.type);
			final List<GameObject> used = getUsedGoodList(g.type);
			final int usedDelta = used.size() - g.getAmount() / 5;
			if (usedDelta > 0) {
				for (int i = 0; i < usedDelta; i++) {
					final GameObject go = used.remove(used.size() - 1);
					unused.add(go);
					if (!renderMaster.sceneManager.removeDynamic(go))
						logger.error("Game engine logic error: Expected dynamic GameObject to exist.");
				}
			} else if (usedDelta < 0) {
				TraderColorIndex = g.type.ordinal();
				final int addNr = -usedDelta;
				final int reuseNr = Math.min(addNr, unused.size());// reuse from unused
				final int createNr = addNr - reuseNr;// create the rest
				for (int i = 0; i < reuseNr; i++) {
					final GameObject go = unused.remove(unused.size() - 1);
					used.add(go);
					//					goodInstances.add(go);
					renderMaster.sceneManager.addDynamic(go);
				}
				for (int i = 0; i < createNr; i++) {
					final GameObject go = Good3DRenderer.instanciateGoodGameObject(g, renderMaster);
					used.add(go);
					//					goodInstances.add(go);
					renderMaster.sceneManager.addDynamic(go);
				}
			}
			for (final GameObject go : used) {
				goodInstances.add(go);
			}
		}
	}

	private void createLights(final Render3DMaster renderMaster) {
		//TODO reuse instances
		final ColorAttribute emissiveAttribute = ColorAttribute.createEmissive(Good3DRenderer.getColor(getColorIndex()));
		for (int i = 0; i < NUMBER_OF_LIGHTS; i++) {
			pointLights.add(new PointLight());
			renderMaster.sceneManager.add(pointLights.get(i), true);
			final GameObject go = new GameObject(new ModelInstanceHack(renderMaster.cubeEmissive), trader);
			go.instance.materials.get(0).set(emissiveAttribute);
			lightGameObjects.add(go);
			renderMaster.sceneManager.addDynamic(lightGameObjects.get(i));
		}

	}

	private void createTader(final Render3DMaster renderMaster) {
		instance = new GameObject(new ModelInstanceHack(renderMaster.trader), trader);
		renderMaster.sceneManager.addDynamic(instance);
	}

	private int getColorIndex() {
		//		return trader.getName().hashCode() % NUMBER_OF_LIGHT_COLORS;
		return TraderColorIndex;
	}

	private List<GameObject> getUnusedGoodList(final GoodType type) {
		if (unusedMls.get(type) == null)
			unusedMls.put(type, new ArrayList<GameObject>());
		return unusedMls.get(type);
	}

	private List<GameObject> getUsedGoodList(final GoodType type) {
		if (usedMls.get(type) == null)
			usedMls.put(type, new ArrayList<GameObject>());
		return usedMls.get(type);
	}

	private boolean nearListener(final Render3DMaster renderMaster) {
		final Vector3 v = renderMaster.sceneManager.getCamera().position;
		if ((trader.x - v.x) + (trader.z - v.z) > 1000)
			return false;
		else
			return true;
	}

	@Override
	public void renderText(final float aX, final float aY, final SceneManager sceneManager, final int index) {
		renderTextOnTop(sceneManager, 0, 0, trader.getName().substring(2), 16);
		renderTextOnTop(sceneManager, -6, -7, "" + (int) velocity[0], 3);//x speed
		renderTextOnTop(sceneManager, 6, -7, "" + (int) velocity[2], 3);//z speed
		renderTextOnTop(sceneManager, 0, -7, "" + Float.toString(toOneDigitPrecision(synth.getGain())), 3);//bass gain
	}

	private void renderTextOnTop(final SceneManager sceneManager, final float dx, final float dy, final String text, final float size) {
		final float x = translation.x;
		final float y = translation.y;
		final float z = translation.z;
		//draw text
		final PolygonSpriteBatch batch = sceneManager.batch2D;
		final BitmapFont font = sceneManager.getAtlasManager().modelFont;
		{
			final Matrix4 m = new Matrix4();
			final float fontSize = font.getLineHeight();
			final float scaling = size / fontSize;
			final GlyphLayout layout = new GlyphLayout();
			layout.setText(font, text);
			final float width = layout.width;// contains the width of the current set text
			final float height = layout.height; // contains the height of the current set text
			//on top
			{
				final Vector3 xVector = new Vector3(1, 0, 0);
				final Vector3 yVector = new Vector3(0, 1, 0);
				m.setToTranslation(x - height * scaling / 2.0f - dy, y + TRADER_Y_SIZE / 2.0f + 0.2f, z + width * scaling / 2.0f - dx);
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

	@Override
	public void update(final float px, final float py, final Render3DMaster renderMaster, final long currentTime, final float timeOfDay, final int index, final boolean selected) throws Exception {
		update(renderMaster, currentTime, index, selected);
	}

	private void update(final Render3DMaster renderMaster, final long currentTime, final int index, final boolean selected) throws Exception {
		updateTrader(renderMaster, index, selected);
		updateLights(currentTime);
		if (lastTransaction != trader.lastTransaction) {
			createGoods(renderMaster);
			updateLightColor(renderMaster);
			lastTransaction = trader.lastTransaction;
		}
		updateGoods(renderMaster);
	}

	private void updateGoods(final Render3DMaster renderMaster) {

		for (int i = 0; i < goodInstances.size(); i++) {
			final GameObject go = goodInstances.get(i);
			final int xEdgeSize = (int) (TRADER_X_SIZE / Good3DRenderer.GOOD_X);
			final int zEdgeSize = (int) (TRADER_Z_SIZE / Good3DRenderer.GOOD_Y);
			final int xContainer = i % xEdgeSize;
			final int zContainer = (int) Math.floor(i / xEdgeSize) % zEdgeSize;
			final int yContainer = (int) Math.floor(i / (xEdgeSize * zEdgeSize));
			final float x = translation.x - TRADER_X_SIZE / 2 + Good3DRenderer.GOOD_X / 2 + xContainer * (Good3DRenderer.GOOD_X);
			final float z = translation.z - TRADER_Z_SIZE / 2 + Good3DRenderer.GOOD_Y / 2 + zContainer * (Good3DRenderer.GOOD_Z);
			final float y = translation.y - TRADER_Y_SIZE / 2 - Good3DRenderer.GOOD_Z / 2 - yContainer * (Good3DRenderer.GOOD_Y);
			go.instance.transform.setToTranslationAndScaling(x, y, z, Good3DRenderer.GOOD_X - Good3DRenderer.SPACE_BETWEEN_GOOD, Good3DRenderer.GOOD_Y - Good3DRenderer.SPACE_BETWEEN_GOOD, Good3DRenderer.GOOD_Z - Good3DRenderer.SPACE_BETWEEN_GOOD);
			go.update();

			//			goodTranslation.z -= TRADER_Z_SIZE / 2 + Good3DRenderer.GOOD_Z / 2;
			//			goodTranslation.y -= TRADER_Y_SIZE / 2 + Good3DRenderer.GOOD_Y / 2;

		}
	}

	private void updateLightColor(final Render3DMaster renderMaster) {
		//TODO reuse instances
		final ColorAttribute emissiveAttribute = ColorAttribute.createEmissive(Good3DRenderer.getColor(getColorIndex()));
		for (final GameObject go : lightGameObjects) {
			go.instance.materials.get(0).set(emissiveAttribute);
		}
	}

	private void updateLights(final long currentTime) {
		final float[] dx = { translation.x + TRADER_X_SIZE / 2 + LIGHT_DISTANCE, translation.x - TRADER_X_SIZE / 2 - LIGHT_DISTANCE, translation.x + TRADER_X_SIZE / 2 + LIGHT_DISTANCE, translation.x - TRADER_X_SIZE / 2 - LIGHT_DISTANCE };
		final float[] dz = { translation.z + TRADER_Z_SIZE / 2 + LIGHT_DISTANCE, translation.z + TRADER_Z_SIZE / 2 + LIGHT_DISTANCE, translation.z - TRADER_Z_SIZE / 2 - LIGHT_DISTANCE, translation.z - TRADER_Z_SIZE / 2 - LIGHT_DISTANCE };
		lightTranslation.y = translation.y - TRADER_Y_SIZE / 2 + Screen3D.SPACE_BETWEEN_OBJECTS * 10;
		//		lightTranslation.set(translation);
		if (trader.destinationWaypointPlanet != null) {
			final float intensitiy = LIGHT_INTENSITY;
			for (int i = 0; i < NUMBER_OF_LIGHTS; i++) {
				lightTranslation.x = dx[i];
				lightTranslation.z = dz[i];
				lightGameObjects.get(i).instance.transform.setToTranslationAndScaling(lightTranslation, lightScaling);
				pointLights.get(i).set(Good3DRenderer.getColor(getColorIndex()), lightTranslation, intensitiy);
			}
		} else {
			// in port
			final float intensitiy = (float) Math.abs(Math.sin((currentTime) / (2000f)) * 500f);
			//			lightTranslation.y = translation.y - TRADER_Y_SIZE / 2 + Screen3D.SPACE_BETWEEN_OBJECTS * 10;
			for (int i = 0; i < NUMBER_OF_LIGHTS; i++) {
				lightTranslation.x = dx[i];
				lightTranslation.z = dz[i];
				//				lightTranslation.y = lightTranslation.y - TRADER_Y_SIZE / 2 + Trader3DRenderer.TRADER_Z_SIZE + Screen3D.SPACE_BETWEEN_OBJECTS;
				pointLights.get(i).set(Color.RED, lightTranslation, intensitiy);
			}

		}
	}

	private void updateTrader(final Render3DMaster renderMaster, final int index, final boolean selected) throws Exception {
		if (trader.destinationWaypointPlanet != null) {

			position[0] = translation.x;
			position[1] = translation.y;
			position[2] = translation.z;
			trader.calcualteEngineSpeed();
			speed.set(trader.destinationWaypointPlanet.x - trader.planet.x, 0, trader.destinationWaypointPlanet.y - trader.planet.y);
			speed.nor();
			speed.scl(trader.getMaxEngineSpeed());
			velocity[0] = speed.x;
			velocity[1] = 0;
			velocity[2] = speed.z;
			synth.setPositionAndVelocity(position, velocity);
			synth.play();
			// ---Traveling
			translation.y = Good3DRenderer.GOOD_HEIGHT * 8 + Screen3D.SPACE_BETWEEN_OBJECTS;
			if (trader.destinationWaypointDistance != 0) {
				final float scalex = (trader.destinationWaypointPlanet.x - trader.planet.x);
				final float scalez = (trader.destinationWaypointPlanet.y - trader.planet.y);
				direction.set(scalex, 0, scalez);
				shift.set(-direction.z, direction.y, direction.x);
				shift.nor();
				shift.scl(JumpGate3DRenderer.JUMP_GATE_SIZE);
				//				Matrix4 m = new Matrix4();
				//				final Vector3 targetVector = new Vector3(trader.destinationWaypointPlanet.x- trader.planet.x, 0, trader.destinationWaypointPlanet.y - trader.planet.y);
				//				m.rotateTowardTarget(targetVector, Vector3.Y);
				//				shift.rot(m);
				translation.x = (trader.planet.x + (trader.destinationWaypointPlanet.x - trader.planet.x) * trader.destinationWaypointDistanceProgress / trader.destinationWaypointDistance) + shift.x;
				translation.z = (trader.planet.y + (trader.destinationWaypointPlanet.y - trader.planet.y) * trader.destinationWaypointDistanceProgress / trader.destinationWaypointDistance) + shift.z;
			} else {
				translation.x = trader.planet.x - Planet3DRenderer.PLANET_ATMOSPHARE_SIZE / 2;
				translation.z = trader.planet.y - Planet3DRenderer.PLANET_ATMOSPHARE_SIZE / 2 + index * TRADER_Z_SIZE;
			}
		} else {
			synth.pause();
			// in port
			translation.y = Planet3DRenderer.PLANET_HIGHT + Screen3D.SPACE_BETWEEN_OBJECTS;
			translation.x = trader.planet.x - Planet3DRenderer.PLANET_ATMOSPHARE_SIZE / 2;
			translation.z = trader.planet.y - Planet3DRenderer.PLANET_ATMOSPHARE_SIZE / 2 + index * TRADER_Z_SIZE;
		}
		translation.add(TRADER_X_SIZE / 2, TRADER_Y_SIZE / 2, 0);
		scaling.set(TRADER_X_SIZE, TRADER_Y_SIZE, TRADER_Z_SIZE);
		//		float factor = Math.max(trader.getGoodList().queryAmount() / trader.goodSpace, 0.2f);
		//		scaling.scl(100);
		instance.instance.transform.setToTranslationAndScaling(translation, scaling);

		//		if (trader.getName().equals("T-50")) {
		//						System.out.println("x=" + position[0] + " y=" + position[1] + " z=" + position[2]);
		//		}

		trader.x = translation.x;
		trader.z = translation.z;
		instance.update();
		if (selected != lastSelected) {
			if (selected) {
				instance.instance.materials.get(0).set(new PBRColorAttribute(ColorAttribute.Emissive, Color.YELLOW));
				instance.instance.materials.get(0).remove(PBRColorAttribute.BaseColorFactor);
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