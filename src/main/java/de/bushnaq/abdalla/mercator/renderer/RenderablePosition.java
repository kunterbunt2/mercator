package de.bushnaq.abdalla.mercator.renderer;

import de.bushnaq.abdalla.mercator.util.Location3D;

public class RenderablePosition extends Location3D {

	public ObjectRenderer renderer2D;
	public ObjectRenderer renderer3D;

	public RenderablePosition(final float x, final float y, final float z) {
		super(x, y, z);
	}

	public ObjectRenderer get2DRenderer() {
		return renderer2D;
	}

	public ObjectRenderer get3DRenderer() {
		return renderer3D;
	}

	public void set2DRenderer(final ObjectRenderer renderer2D) {
		this.renderer2D = renderer2D;
	}

	public void set3DRenderer(final ObjectRenderer renderer3D) {
		this.renderer3D = renderer3D;
	}
}
