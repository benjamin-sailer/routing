package de.bsailer.routing;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SimpleGraphFactoryTest {

	private static final SimpleEdgeIdentifier ID = new SimpleEdgeIdentifier(0);
	@Test
	public void givenEmptyStreamShouldCreateEmptyGraph() {
		assertEquals(new SimpleGraph(), new SimpleGraphFactory().createGraph(Stream.empty()));
	}

	@Test
	public void givenOneLineShouldCreateGraphWithOneEdge() {
		final var expected = new SimpleGraph();
		expected.addEdge(new SimpleEdge(ID).setWeight(1.0D));
		assertEquals(expected,
				new SimpleGraphFactory().createGraph(Stream.of("1.0D;")));
	}

}
