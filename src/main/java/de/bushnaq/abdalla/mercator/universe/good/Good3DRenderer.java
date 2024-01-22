package de.bushnaq.abdalla.mercator.universe.good;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.bushnaq.abdalla.mercator.universe.planet.Planet3DRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bushnaq.abdalla.mercator.renderer.GameObject;
import de.bushnaq.abdalla.mercator.renderer.ObjectRenderer;
import de.bushnaq.abdalla.mercator.renderer.Render3DMaster;
import de.bushnaq.abdalla.mercator.renderer.SceneManager;
import de.bushnaq.abdalla.mercator.renderer.Screen3D;
import de.bushnaq.abdalla.mercator.universe.Universe;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.model.ModelInstanceHack;

public class Good3DRenderer extends ObjectRenderer {

	public static final int CONTAINER_EDGE_SIZE = 4;
	private static Color DIAMON_BLUE_COLOR = new Color(0x006ab6ff);
	private static final int GOOD_AMOUNT_DRAWING_FACTOR = 5;
	public static final float GOOD_HEIGHT = 8f;
	private static final Color GOOD_NAME_COLOR = new Color(0.596f, 0.08f, 0.247f, 0.8f);
	//	public static final Color GOOD_COLOR = Color.RED; // 0xff000000;
	public static final float GOOD_X = 8f / Universe.WORLD_SCALE;
	public static final float GOOD_Y = 8f / Universe.WORLD_SCALE;
	public static final float GOOD_Z = 8f / Universe.WORLD_SCALE;
	private static Color GRAY_COLOR = new Color(0x404853ff);
	//	private static Color magentaColor = new Color(1.0f, 0.0f, 1.0f, 1f);
	//	private static Color cyanColor = new Color(0.0f, 1.0f, 1.0f, 1f);
	public static final Color NOT_TRADED_GOOD_COLOR = Color.LIGHT_GRAY; // 0xffbbbbbb;
	private static Color POST_GREEN_COLOR = new Color(0x00614eff);
	private static Color SCARLET_COLOR = new Color(0xb00233ff);
	public static final Color SELECTED_GOOD_COLOR = Color.LIGHT_GRAY; // 0xffeeeeee;
	public static final float SPACE_BETWEEN_GOOD = Screen3D.SPACE_BETWEEN_OBJECTS * 2;

	public static Color getColor(final int index) {
		switch (index) {
		case 0:
			return POST_GREEN_COLOR;
		case 1:
			return SCARLET_COLOR;
		case 2:
			return DIAMON_BLUE_COLOR;
		case 3:
			return GRAY_COLOR;
		case -1:
			return Color.WHITE;//we are not transporting any good
		default:
			return Color.WHITE;
		}
	}

	public static GameObject instanciateGoodGameObject(final Good good, final Render3DMaster renderMaster) {
		GameObject scene = null;
		final Material material1 = renderMaster.cubeGood.materials.get(0);
		scene = new GameObject(new ModelInstanceHack(renderMaster.cubeGood), good);
		//TODO reuse instances
		final Material material2 = scene.instance.materials.get(0);
		final Iterator<Attribute> i = material1.iterator();
		material2.clear();
		while (i.hasNext()) {
			final Attribute a = i.next();
			material2.set(a);
		}
		scene.instance.materials.get(0).set(new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, getColor(good.type.ordinal())));
		return scene;
	}

	private final Good good;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final List<GameObject> unusedMls = new ArrayList<>();

	private final List<GameObject> usedMls = new ArrayList<>();

	public Good3DRenderer(final Good good) {
		this.good = good;
	}

	@Override
	public void renderText(final float aX, final float aY, final float aZ, final SceneManager sceneManager, final int index) {
		{
			final float dy = -Planet3DRenderer.PLANET_SIZE / 2 + index * (CONTAINER_EDGE_SIZE + 1) * (GOOD_Y + SPACE_BETWEEN_GOOD);
			final float dx = Planet3DRenderer.PLANET_SIZE / 2 - (CONTAINER_EDGE_SIZE) * (GOOD_X + SPACE_BETWEEN_GOOD);
			renderTextOnTop(aX, aY, aZ, sceneManager, dy, 0, dx, good.type.getName(), GOOD_X);
			//			final float size = 8;
			//			final float x = aX;
			//			final float y = aY;
			//			final float z = aZ;
			//			//draw text
			//			final PolygonSpriteBatch batch = sceneManager.batch2D;
			//			final BitmapFont font = sceneManager.getAtlasManager().modelFont;
			//			{
			//				final Matrix4 m = new Matrix4();
			//				final float fontSize = font.getLineHeight();
			//				final float scaling = size / fontSize;
			//				m.setToTranslation(x - Planet3DRenderer.PLANET_SIZE / 2 + (CONTAINER_EDGE_SIZE) * (GOOD_X + SPACE_BETWEEN_GOOD) + size, y + 1, z + Planet3DRenderer.PLANET_SIZE / 2 - index * (CONTAINER_EDGE_SIZE + 1) * (GOOD_Y + SPACE_BETWEEN_GOOD) - size / 5);
			//				final Vector3 xVector = new Vector3(1, 0, 0);
			//				final Vector3 yVector = new Vector3(0, 1, 0);
			//				m.rotate(yVector, 90);
			//				m.rotate(xVector, -90);
			//				m.scale(scaling, scaling, 1f);
			//				batch.setTransformMatrix(m);
			//				font.setColor(GOOD_NAME_COLOR);
			//				font.draw(batch, good.type.getName(), 0, 0);
			//			}

		}

	}

	private void renderTextOnTop(final float aX, final float aY, final float aZ, final SceneManager sceneManager, final float dx, final float dy, final float dz, final String text, final float size) {
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
				m.setToTranslation(aX - height * scaling / 2.0f + size - dz, aY + 0.2f, aZ + width * scaling / 2.0f - size - dx);
				m.rotate(yVector, 90);
				m.rotate(xVector, -90);
				m.scale(scaling, scaling, 1f);

			}
			batch.setTransformMatrix(m);
			font.setColor(GOOD_NAME_COLOR);
			font.draw(batch, text, 0, 0);
		}
	}

	@Override
	public void update(final float x, final float y, final float z, final Render3DMaster renderMaster, final long currentTime, final float timeOfDay, final int index, final boolean selected) {
		updateGood(x, y, z, renderMaster, currentTime, index, false);
	}

	private void updateGood(final float aX, final float aY, final float aZ, final Render3DMaster renderMaster, final long currentTime, final int index, final boolean selected) {
		//		Color color;
		//		if (selected) {
		//			color = SELECTED_GOOD_COLOR;
		//		} else if (good.isTraded(renderMaster.universe.currentTime)) {
		//			color = GOOD_COLOR;
		//		} else {
		//			color = NOT_TRADED_GOOD_COLOR;
		//		}
		// if ( good.type == GoodType.Food && !good.isTraded(
		// universe.currentTime ) )
		// System.out.printf( "planet %s last time %d now %d\n", planet.name,
		// good.lastBuyInterest, universe.currentTime );
		final int delta = usedMls.size() - good.getAmount() / GOOD_AMOUNT_DRAWING_FACTOR;
		if (delta > 0) {
			for (int i = 0; i < delta; i++) {
				final GameObject scene = usedMls.remove(usedMls.size() - 1);
				unusedMls.add(scene);
				if (!renderMaster.sceneManager.removeStatic(scene))
					logger.error("Game engine logic error: Expected dynamic GameObject to exist.");
			}
		} else if (delta < 0) {
			final int addNr = -delta;
			final int reuseNr = Math.min(addNr, unusedMls.size());// reuse from unused
			final int createNr = addNr - reuseNr;// create the rest
			for (int i = 0; i < reuseNr; i++) {
				final GameObject scene = unusedMls.remove(unusedMls.size() - 1);
				usedMls.add(scene);
				renderMaster.sceneManager.addStatic(scene);
			}
			for (int i = 0; i < createNr; i++) {
				final int edgeSize = CONTAINER_EDGE_SIZE;
				final int xEdgeSize = edgeSize;
				final int yEdgeSize = edgeSize;
				final int xContainer = usedMls.size() % xEdgeSize;
				final int zContainer = (int) Math.floor(usedMls.size() / xEdgeSize) % yEdgeSize;
				final int yContainer = (int) Math.floor(usedMls.size() / (xEdgeSize * yEdgeSize));
				final float x = aX - Planet3DRenderer.PLANET_SIZE / 2 + GOOD_X / 2 + xContainer * (GOOD_X);
				final float z = aZ + Planet3DRenderer.PLANET_SIZE / 2 - GOOD_Z / 2 - zContainer * (GOOD_Z) - index * (edgeSize + 1) * (GOOD_Z);
				final float y = aY + GOOD_Y / 2 + yContainer * (GOOD_Y);
				final GameObject go = instanciateGoodGameObject(good, renderMaster);
				go.instance.transform.setToTranslationAndScaling(x, y, z, GOOD_X - SPACE_BETWEEN_GOOD, GOOD_Y - SPACE_BETWEEN_GOOD, GOOD_Z - SPACE_BETWEEN_GOOD);
				go.update();
				renderMaster.sceneManager.addStatic(go);
				usedMls.add(go);
			}

		} else {
			// everything is good
		}
		// renderMaster.bar( renderMaster.goodTexture, x, y, x + Screen.GOOD_WIDTH - 1 -
		// Screen.SPACE_BETWEEN_OBJECTS, y + Screen.GOOD_HEIGHT - 1 -
		// Screen.SPACE_BETWEEN_OBJECTS, color );
		// updateLight( good, x + GOOD_WIDTH / 2, y, new Color( 0.8f, 0.8f, 0.8f, 0.8f
		// ), GOOD_WIDTH );
		// light = renderMaster.updatePointLight( light, renderMaster, x + (
		// Screen.GOOD_WIDTH - Screen.SPACE_BETWEEN_OBJECTS ) / 2, y + (
		// Screen.GOOD_HEIGHT - Screen.SPACE_BETWEEN_OBJECTS ) / 2, lightColor,
		// Screen.GOOD_WIDTH );
		switch (renderMaster.showGood)

		{
		case Price:
			//				renderMaster.text( x, y, color, Screen.TEXT_COLOR, String.format( "%.0f", good.price ) );
			//				renderMaster.bar( renderMaster.gaugeTexture, x + Screen.GOOD_WIDTH - 1 - 2 - Screen.SPACE_BETWEEN_OBJECTS, y + Screen.GOOD_HEIGHT - 1 -
			//						(int)( ( Screen.GOOD_HEIGHT * good.price ) / good.getMaxPrice() ), x + Screen.GOOD_WIDTH - 1 - Screen.SPACE_BETWEEN_OBJECTS, y +
			//						Screen.GOOD_HEIGHT - 1 - Screen.SPACE_BETWEEN_OBJECTS, renderMaster.priceColor( good ) );
			break;
		case Name:
			//				renderMaster.text( x, y, color, Screen.TEXT_COLOR, good.type.getName() );
			//				renderMaster.bar( renderMaster.gaugeTexture, x + Screen.GOOD_WIDTH - 1 - 2 - Screen.SPACE_BETWEEN_OBJECTS, y + Screen.GOOD_HEIGHT - 1 -
			//						(int)( ( Screen.GOOD_HEIGHT * good.amount ) / good.getMaxAmount() ), x + Screen.GOOD_WIDTH - 1 - Screen.SPACE_BETWEEN_OBJECTS, y +
			//						Screen.GOOD_HEIGHT - 1 - Screen.SPACE_BETWEEN_OBJECTS, renderMaster.amountColor( good ) );
			break;
		case Volume:
			//				renderMaster.text( x, y, color, Screen.TEXT_COLOR, String.format( "%.0f", good.amount ) );
			//				renderMaster.bar( renderMaster.gaugeTexture, x + Screen.GOOD_WIDTH - 1 - 2 - Screen.SPACE_BETWEEN_OBJECTS, y + Screen.GOOD_HEIGHT - 1 -
			//						(int)( ( Screen.GOOD_HEIGHT * good.amount ) / good.getMaxAmount() ), x + Screen.GOOD_WIDTH - 1 - Screen.SPACE_BETWEEN_OBJECTS, y +
			//						Screen.GOOD_HEIGHT - 1 - Screen.SPACE_BETWEEN_OBJECTS, renderMaster.amountColor( good ) );
			break;
		}
	}

}
