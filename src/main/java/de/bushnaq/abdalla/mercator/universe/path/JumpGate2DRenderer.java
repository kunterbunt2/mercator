package de.bushnaq.abdalla.mercator.universe.path;

import de.bushnaq.abdalla.engine.ObjectRenderer;
import de.bushnaq.abdalla.engine.RenderEngine2D;
import de.bushnaq.abdalla.engine.RenderEngine3D;
import de.bushnaq.abdalla.mercator.renderer.Screen2D;
import de.bushnaq.abdalla.mercator.universe.planet.Planet2DRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class JumpGate2DRenderer extends ObjectRenderer<Screen2D> {
	static final Color JUMPGATE_COLOR = new Color(0.275f, 0.314f, 0.314f, 1.0f);
	private static final float MIN_RADIUS = Planet2DRenderer.PLANET_SIZE * 4.0f + 3;
	// static final Color SELECTED_JUMPGATE_COLOR = Color.ORANGE;
	Path jumpGate;

	public JumpGate2DRenderer(final Path jumpGate) {
		this.jumpGate = jumpGate;
	}

	private void drawJumpGate(final float x, final float z, final Path jumpGate, final RenderEngine2D<Screen2D> renderEngine) {
		final Vector2 target = new Vector2(jumpGate.target.x, jumpGate.target.z);
		final Vector2 start = new Vector2(jumpGate.source.x, jumpGate.source.z);
//		final Vector2 line = target.sub(start);
//		final float length = line.len();
//		final float tx = jumpGate.target.x;
//		final float tz = jumpGate.target.z;
//		final float x1 = x + (tx - x) * MIN_RADIUS / length;
//		final float z1 = z + (tz - z) * MIN_RADIUS / length;
//		final float tx2 = x + (tx - x) / 2;
//		final float tz2 = z + (tz - z) / 2;
		Color color;
		// float thickness = jumpGate.usage;
		float thickness = 1.3f * renderEngine.getGameEngine().renderEngine.camera.zoom;
		if (jumpGate.closed) {
			color = Color.RED;
		} else if (jumpGate.selected) {
			color = Screen2D.SELECTED_COLOR;
			thickness = 3.3f * renderEngine.getGameEngine().renderEngine.camera.zoom;
			//		} else if ( jumpGate.planet.sector == jumpGate.targetPlanet.sector) {
			//			color = renderMaster.distinctiveTransparentColorlist.get(jumpGate.planet.sector.type);
		} else {
			color = JUMPGATE_COLOR;
		}
		final Color c = new Color(color);
		c.a = 0.45f;
		renderEngine.getGameEngine().renderEngine.line(renderEngine.getGameEngine().atlasManager.dottedLineTextureRegion, start.x, start.y, target.x, target.y, c, thickness);

	}

	@Override
	public void render(final float x, final float z, final RenderEngine2D<Screen2D> renderEngine, final int index, final boolean selected) {
		drawJumpGate(x, z, jumpGate, renderEngine);
	}

	@Override
	public boolean withinBounds(final float x, final float y) {
		// TODO Auto-generated method stub
		return false;
	}
}
