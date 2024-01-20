package com.abdalla.bushnaq.mercator.util;

public class AnnulusSegment {
	float maxAngle;
	float maxRadius;
	float minAngle;
	float minRadius;
	float x;
	float y;

	public AnnulusSegment(final float x, final float y, final float minRadius, final float maxRadius, final float minAngle, final float maxAngle) {
		this.x = x;
		this.y = y;
		this.minRadius = minRadius;
		this.maxRadius = maxRadius;
		this.minAngle = minAngle;
		this.maxAngle = maxAngle;
	}

	public boolean contains(final float x, final float y) {
		final float h = y - this.y;
		final float w = x - this.x;
		final float r = (float) Math.sqrt(Math.pow(w, 2) + Math.pow(h, 2));
		float angle = (float) Math.asin(w / r);
		if (h > 0) {
			angle = (float) Math.PI - angle;
		}
		if (w < 0) {
			angle += Math.PI;
		}
		if (angle >= minAngle && angle <= maxAngle && r > minRadius && r <= maxRadius)
			return true;
		return false;
	}

	public void setPosition(final float x, final float y) {
		this.x = x;
		this.y = y;
	}
}
