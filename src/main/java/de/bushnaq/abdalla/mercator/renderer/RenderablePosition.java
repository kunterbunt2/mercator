package de.bushnaq.abdalla.mercator.renderer;

import de.bushnaq.abdalla.engine.ObjectRenderer;
import de.bushnaq.abdalla.mercator.util.Location3D;

public class RenderablePosition extends Location3D {

	public ObjectRenderer<Screen2D> renderer2D;
	public ObjectRenderer<Screen3D> renderer3D;

	public RenderablePosition(final float x, final float y, final float z) {
		super(x, y, z);
	}

	public ObjectRenderer<Screen2D> get2DRenderer() {
		return renderer2D;
	}

	public ObjectRenderer<Screen3D> get3DRenderer() {
		return renderer3D;
	}

	public void set2DRenderer(final ObjectRenderer<Screen2D> renderer2D) {
		this.renderer2D = renderer2D;
	}

	public void set3DRenderer(final ObjectRenderer<Screen3D> renderer3D) {
		this.renderer3D = renderer3D;
	}
}
