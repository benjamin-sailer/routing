package de.bsailer.routing;

import static de.bsailer.test.ExtendedAssert.assertEqualsDouble;

import de.bsailer.routing.model.impl.SimpleEdge;
import de.bsailer.routing.model.impl.SimpleEdgeIdentifier;
import org.junit.Test;

public class SimpleEdgeTest {

	private static final double DEFAULT_LENGTH = 1.0D;
	private static final double DEFAULT_WEIGHT = 1.0D;
	private static final SimpleEdgeIdentifier DEFAULT_ID = new SimpleEdgeIdentifier(1);

	@Test
	public void givenEdgeHasWeightOne() {
		final double weight = new SimpleEdge(DEFAULT_ID).setWeight(DEFAULT_WEIGHT).weight();
		assertEqualsDouble(DEFAULT_WEIGHT, weight);
	}

	@Test
	public void givenEdgeHasLengthOne() {
		final double length = new SimpleEdge(DEFAULT_ID).setLength(DEFAULT_LENGTH).length();
		assertEqualsDouble(DEFAULT_LENGTH, length);
	}
}
