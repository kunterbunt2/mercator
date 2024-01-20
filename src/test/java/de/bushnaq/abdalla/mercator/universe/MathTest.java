package de.bushnaq.abdalla.mercator.universe;

import org.junit.jupiter.api.Test;

/**
 * @author abdalla 2015.09.17 in year 58,00 all sims where starving in year
 *         36,00 export has stopped for one year
 */
public class MathTest {
	@Test
	public void testTrading() throws Exception {
		final float a = 3;
		final float b = 4.0f;
		final int result = (int) Math.floor(a / b);
		System.out.println(result);
	}
}
