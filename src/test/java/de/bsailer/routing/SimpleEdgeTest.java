package de.bsailer.routing;

import static de.bsailer.test.ExtendedAssert.assertEqualsDouble;

import org.junit.Test;

public class SimpleEdgeTest {

	private static final double DEFAULT_LENGTH = 1.0D;
	private static final double DEFAULT_WEIGHT = 1.0D;

	@Test
	public void givenEdgeHasWeightOne() {
		final double weight = new SimpleEdge().setWeight(DEFAULT_WEIGHT).weight();
		assertEqualsDouble(DEFAULT_WEIGHT, weight);
	}

	@Test
	public void givenEdgeHasLengthOne() {
		final double length = new SimpleEdge().setLength(DEFAULT_LENGTH).length();
		assertEqualsDouble(DEFAULT_LENGTH, length);
	}
}
