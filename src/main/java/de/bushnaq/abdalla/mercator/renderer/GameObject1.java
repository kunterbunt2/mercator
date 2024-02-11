package de.bushnaq.abdalla.mercator.renderer;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import net.mgsx.gltf.scene3d.animation.AnimationControllerHack;
import net.mgsx.gltf.scene3d.model.ModelInstanceHack;

public class GameObject1 {

	public BoundingBox boundingBox = new BoundingBox();
	public final Vector3 center = new Vector3();
	public AnimationControllerHack controller;
	public ModelInstanceHack instance;
	public Object interactive;
	public BoundingBox transformedBoundingBox = new BoundingBox();

	public GameObject1(final ModelInstanceHack instance, final Object interactive) {
		this.instance = instance;
		this.interactive = interactive;
		boundingBox = new BoundingBox();
		instance.calculateBoundingBox(boundingBox);
		boundingBox.getCenter(center);
	}

	public void update() {
		transformedBoundingBox.set(boundingBox).mul(instance.transform);
	}
}
