package de.bsailer.routing.model.impl;

import org.junit.Test;

import static de.bsailer.test.ExtendedAssert.assertEqualsDouble;
import static org.junit.Assert.assertEquals;

public class SimpleEdgeTest {

	private static final double DEFAULT_LENGTH = 1.0D;
	private static final double DEFAULT_WEIGHT = 1.0D;
	private static final SimpleEdgeIdentifier DEFAULT_ID = new SimpleEdgeIdentifier(1);

	@Test
	public void givenEdgeHasConstructorId() {
		final var edge = new SimpleEdge(DEFAULT_ID);
		assertEquals(DEFAULT_ID, edge.id());
	}

	@Test
	public void givenEdgeHasWeightOne() {
		final var edge = new SimpleEdge(DEFAULT_ID).setWeight(DEFAULT_WEIGHT);
		assertEqualsDouble(DEFAULT_WEIGHT, edge.weight());
	}

	@Test
	public void givenEdgeHasLengthOne() {
		final var edge = new SimpleEdge(DEFAULT_ID).setLength(DEFAULT_LENGTH);
		assertEqualsDouble(DEFAULT_LENGTH, edge.length());
	}

	@Test
	public void toStringShouldReturnSomethingMeaningful() {
		final var edge = new SimpleEdge(new SimpleEdgeIdentifier(1))
				.setWeight(1.0)
				.setLength(1.0);
		assertEquals("SimpleEdge{id=SimpleEdgeIdentifier{1}, weight=1.0, length=1.0}", edge.toString());
	}
}
