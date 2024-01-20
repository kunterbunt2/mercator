package com.abdalla.bushnaq.mercator.audio.synthesis;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abdalla.bushnaq.mercator.audio.synthesis.util.LiniarTranslation;
import com.abdalla.bushnaq.mercator.audio.synthesis.util.TranslationUtil;
import com.abdalla.bushnaq.mercator.universe.sim.trader.Trader;
import com.badlogic.gdx.math.Vector3;

public class MercatorSoundTest extends TranslationUtil<LiniarTranslation> {
	private static final float CUBE_SIZE = 64;
	private static final int MAX_LINIAR_TRANSLATION = 2000;
	protected static final int NUMBER_OF_SOURCES = 1;
	protected static final int START_SPEED = 2;
	private float factor = 2;
	private float factorIncrement = 1;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	Vector3 minSpeed = new Vector3(Trader.MIN_ENGINE_SPEED, 0, 0);
	private int switchedDirection;

	@Override
	public void create() {
		//		simulateBassBoost = true;
		super.create(NUMBER_OF_SOURCES);
		try {
			for (int l = 0; l < NUMBER_OF_SOURCES; l++) {
				final float x = 0;
				final float y = CUBE_SIZE / 2;
				final float z = 0;
				final LiniarTranslation t = new LiniarTranslation();
				t.origin.set(x, y, z);
				t.position.set(x - MAX_LINIAR_TRANSLATION, y, z);
				t.velocity.set(new Vector3(START_SPEED, 0, 0));
				translation.add(t);
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Test
	public void liniarTranslatingSources() throws Exception {
		runFor = 120000;
		startLwjgl();
	}

	@Override
	protected void updateTranslation() {
		for (int i = 0; i < gameObjects.size(); i++) {
			final LiniarTranslation t = translation.get(i);
			if (universe.isEnableTime()) {
				if (Math.abs(t.position.x + t.velocity.x - t.origin.x) > MAX_LINIAR_TRANSLATION) {
					t.velocity.x = -t.velocity.x;
					switchedDirection++;
					logger.info(String.format("on the x edge = %f", Math.abs(t.position.x - t.origin.x)));
				} else
					t.position.x += t.velocity.x;
				if (Math.abs(t.position.z + t.velocity.z - t.origin.z) > MAX_LINIAR_TRANSLATION) {
					t.velocity.z = -t.velocity.z;
					switchedDirection++;
					logger.info(String.format("on the z edge = %f", Math.abs(t.position.z - t.origin.z)));
				} else
					t.position.z += t.velocity.z;
				if (switchedDirection > 0) {
					logger.info(String.format("switching direction = %d", switchedDirection));
					final Vector3 newSpeed = t.velocity.cpy().nor().scl(minSpeed).scl(factor);
					//					logger.info("x=" + newSpeed.x + " y=" + newSpeed.y + " z=" + newSpeed.z);
					t.velocity.set(newSpeed);
					switchedDirection = 0;
					if (t.velocity.len() >= Trader.MAX_ENGINE_SPEED || t.velocity.len() <= Trader.MIN_ENGINE_SPEED) {
						factorIncrement = -factorIncrement;
						factor += factorIncrement;
						logger.info(String.format("factor = %f factorIncrement = %f", factor, factorIncrement));
					} else {
						factor += factorIncrement;
						logger.info(String.format("factor = %f factorIncrement = %f", factor, factorIncrement));
					}
				}
			}
		}
	}
}