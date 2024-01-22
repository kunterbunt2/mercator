package de.bushnaq.abdalla.mercator.universe.land;

import de.bushnaq.abdalla.mercator.renderer.GameObject;
import de.bushnaq.abdalla.mercator.renderer.ObjectRenderer;
import de.bushnaq.abdalla.mercator.renderer.Render3DMaster;
import de.bushnaq.abdalla.mercator.universe.planet.Planet;
import de.bushnaq.abdalla.mercator.universe.planet.Planet3DRenderer;

import net.mgsx.gltf.scene3d.model.ModelInstanceHack;

public class Land3DRenderer extends ObjectRenderer {
	private static final float HILL_HIGHT = 16;
	private static final float LAND_HIGHT = Planet3DRenderer.PLANET_HIGHT + HILL_HIGHT;
	private static final float LAND_SIZE = Planet.PLANET_DISTANCE;
	private GameObject instance;
	private final Land land;

	public Land3DRenderer(final Land planet) {
		this.land = planet;
	}

	@Override
	public void create(final Render3DMaster renderMaster) {
		createLand(renderMaster);
	}

	private void createLand(final Render3DMaster renderMaster) {
		instanciateLand(renderMaster, land.x, land.y, land.z);
	}

	private void instanciateLand(final Render3DMaster renderMaster, final float x, final float y, final float z) {
		instance = new GameObject(new ModelInstanceHack(renderMaster.land), null);
		instance.instance.transform.setToTranslationAndScaling(x, y - LAND_HIGHT / 2 + HILL_HIGHT, z, LAND_SIZE, LAND_HIGHT + HILL_HIGHT, LAND_SIZE);
		instance.update();
		renderMaster.sceneManager.addStatic(instance);
	}

}
