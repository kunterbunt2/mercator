package com.abdalla.bushnaq.mercator.universe.jumpgate;

import com.abdalla.bushnaq.mercator.renderer.GameObject;
import com.abdalla.bushnaq.mercator.renderer.ObjectRenderer;
import com.abdalla.bushnaq.mercator.renderer.Render3DMaster;
import com.abdalla.bushnaq.mercator.renderer.Screen3D;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.model.ModelInstanceHack;

public class JumpGate3DRenderer extends ObjectRenderer {
	private static final float JUMP_GATE_HIGHT = 16;
	public static final float JUMP_GATE_SIZE = 16;
	//	private static final Color SELECTED_JUMPGATE_COLOR = Color.WHITE;
	//	private static final Color JUMPGATE_COLOR = new Color(0.3f, 0.3f, 0.3f, 1.0f); // 0xff5555cc
	//	static final Color JUMPGATE_COLOR = new Color(0.275f, 0.314f, 0.314f, 1.0f);
	//	private static final float JUMP_GATE_MIN_RADIUS = Planet3DRenderer.PLANET_SIZE * 4.0f + 3;
	// static final Color SELECTED_JUMPGATE_COLOR = Color.ORANGE;
	private GameObject instance;
	private final JumpGate jumpGate;
	//	Matrix4 rotationMatrix = new Matrix4();
	//	protected Quaternion rotation = new Quaternion();
	boolean lastSelected = false;
	float r = 45;

	public JumpGate3DRenderer(final JumpGate jumpGate) {
		this.jumpGate = jumpGate;
	}

	@Override
	public void create(final float x, final float y, final Render3DMaster renderMaster) {
		createJumpGate(x, y, renderMaster);
	}

	private void createJumpGate(final float x, final float y, final Render3DMaster renderMaster) {
		//jump gate
		//			Vector2 target = new Vector2(jumpGate.targetPlanet.x, jumpGate.targetPlanet.y);
		//			Vector2 start = new Vector2(x, y);
		//			Vector2 line = target.sub(start);
		//			float length = line.len();
		final float tx = jumpGate.targetPlanet.x;
		final float tz = jumpGate.targetPlanet.y;
		//			float x1 = x + (tx - x) * JUMP_GATE_MIN_RADIUS / length;
		//			float y1 = y + (ty - y) * JUMP_GATE_MIN_RADIUS / length;
		final float scalex = (tx - x);
		final float scalez = (tz - y);

		//			Color color;
		//			if (jumpGate.closed) {
		//				color = Color.RED;
		//			} else if (jumpGate.selected) {
		//				color = Screen2D.SELECTED_COLOR;
		//			} else if (jumpGate.planet.sector == jumpGate.targetPlanet.sector) {
		//				color = renderMaster.distinctiveTransparentColorlist.get(jumpGate.planet.sector.type);
		//			} else {
		//				color = JUMPGATE_COLOR;
		//			}
		//			Color c = new Color(color);
		//			c.a = 0.45f;

		final Vector3 direction = new Vector3(scalex, 0, scalez);
		instance = new GameObject(new ModelInstanceHack(renderMaster.jumpGate), null);
		//			instance = new ModelInstance(renderMaster.boxModel2);
		//			instance.materials.get(0).set(ColorAttribute.createDiffuse(JUMPGATE_COLOR));
		//		final Color sectorColor = renderMaster.getDistinctiveColor(jumpGate.planet.sector.type);
		//		instance.instance.materials.get(0).set(new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, sectorColor));
		renderMaster.sceneManager.addStatic(instance);
		final float directionLength = direction.len() + Screen3D.SPACE_BETWEEN_OBJECTS;

		final Vector3 shift = new Vector3(-direction.z, direction.y, direction.x);
		shift.nor();
		shift.scl(JUMP_GATE_SIZE * 2);
		instance.instance.transform.setToTranslation(x + shift.x, 0, y + shift.z);
		direction.nor();
		final Vector3 targetVector = new Vector3(tx, 0, tz);
		instance.instance.transform.rotateTowardTarget(targetVector, Vector3.Y);
		instance.instance.transform.translate(0, -JUMP_GATE_HIGHT / 2 - Screen3D.SPACE_BETWEEN_OBJECTS, -directionLength / 2);
		instance.instance.transform.scale(JUMP_GATE_SIZE, JUMP_GATE_HIGHT, directionLength);
		instance.update();
	}

	private void drawJumpGate(final float x, final float y, final Render3DMaster renderMaster, final long currentTime, final boolean selected) {
		if (selected != lastSelected) {
			if (selected) {
				instance.instance.materials.get(0).set(new PBRColorAttribute(ColorAttribute.Emissive, Color.YELLOW));
				instance.instance.materials.get(0).remove(PBRColorAttribute.BaseColorFactor);
			} else {
				instance.instance.materials.get(0).remove(ColorAttribute.Emissive);
				final PBRColorAttribute ca = (PBRColorAttribute) renderMaster.cubeBase1.materials.get(0).get(PBRColorAttribute.BaseColorFactor);
				instance.instance.materials.get(0).set(new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, ca.color));
			}
			lastSelected = selected;
		}
	}

	@Override
	public void update(final float x, final float y, final Render3DMaster renderMaster, final long currentTime, final float timeOfDay, final int index, final boolean selected) {
		drawJumpGate(x, y, renderMaster, currentTime, selected);
	}

	@Override
	public boolean withinBounds(final float x, final float y) {
		return false;
	}
}
