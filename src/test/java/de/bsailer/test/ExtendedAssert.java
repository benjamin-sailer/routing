package de.bsailer.test;

import static org.junit.Assert.assertEquals;

public final class ExtendedAssert {

	private static final double DEFAULT_DOUBLE_ASSERT_PRECISION = 0.0001D;

	private ExtendedAssert() {
		throw new IllegalStateException("no instance");
	}
	
	public static void assertEqualsDouble(double expected, double actual) {
		assertEquals(expected, actual, DEFAULT_DOUBLE_ASSERT_PRECISION);
	}

}
