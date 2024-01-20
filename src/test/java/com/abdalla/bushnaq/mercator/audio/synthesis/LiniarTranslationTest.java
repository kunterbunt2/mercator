package com.abdalla.bushnaq.mercator.audio.synthesis;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abdalla.bushnaq.mercator.audio.synthesis.util.LiniarTranslation;
import com.abdalla.bushnaq.mercator.audio.synthesis.util.TranslationUtil;
import com.abdalla.bushnaq.mercator.universe.sim.trader.Trader;

public class LiniarTranslationTest extends TranslationUtil<LiniarTranslation> {
	private static final float CUBE_SIZE = 64;
	private static final float MAX_GRID_SIZE = 1000f;
	private static final int MAX_LINIAR_TRANSLATION = 1000;
	protected static final int NUMBER_OF_SOURCES = 10;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void create() {
		super.create(NUMBER_OF_SOURCES);
		try {
			for (int l = 0; l < NUMBER_OF_SOURCES; l++) {
				final float grid = MAX_GRID_SIZE;
				final float x = rg.nextInt(grid) - grid / 2;
				final float y = CUBE_SIZE / 2;
				final float z = rg.nextInt(grid) - grid / 2;
				final LiniarTranslation t = new LiniarTranslation();
				t.origin.set(x, y, z);
				t.position.set(x, y, z);
				final float speed = Trader.MIN_ENGINE_SPEED + rg.nextInt(Trader.MAX_ENGINE_SPEED - Trader.MIN_ENGINE_SPEED);
				switch (rg.nextInt(2)) {
				case 0:
					t.velocity.set(speed, 0, 0);
					break;
				case 1:
					t.velocity.set(0, 0, speed);
					break;
				}
				translation.add(t);
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Test
	public void liniarTranslatingSources() throws Exception {
		runFor = 20000;
		startLwjgl();
	}

	@Override
	protected void updateTranslation() {
		for (int i = 0; i < gameObjects.size(); i++) {
			final LiniarTranslation t = translation.get(i);
			if (universe.isEnableTime()) {
				t.position.x += t.velocity.x;
				t.position.z += t.velocity.z;
				if (Math.abs(t.position.x - t.origin.x) > MAX_LINIAR_TRANSLATION)
					t.velocity.x = -t.velocity.x;
				if (Math.abs(t.position.z - t.origin.z) > MAX_LINIAR_TRANSLATION)
					t.velocity.z = -t.velocity.z;
			}
		}
	}

}