package de.bushnaq.abdalla.mercator.renderer;

public class Renderable {

	public ObjectRenderer renderer2D;
	public ObjectRenderer renderer3D;

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
