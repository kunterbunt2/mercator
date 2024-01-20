package de.bushnaq.abdalla.mercator.renderer;

public abstract class ObjectRenderer {
	public void create(final float x, final float y, final Render3DMaster renderMaster) {
	};

	/**
	 * Rendrs the object using the batch renderer
	 *
	 * @param selected true if this object is selected and needs to be represented
	 *                 in a different way.
	 */
	public void render(final float x, final float y, final Render2DMaster renderMaster, final int index, final boolean selected) {
	};

	public void renderText(final float aX, final float aY, final SceneManager sceneManager, final int index) {
	};

	public void update(final float x, final float y, final Render3DMaster renderMaster, final long currentTime, final float timeOfDay, final int index, final boolean selected) throws Exception {
	}

	public boolean withinBounds(final float x, final float y) {
		return false;
	};
}
