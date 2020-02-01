package de.bsailer.routing;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SimpleGraphFactoryTest {

	@Test
	public void givenEmptyStreamShouldCreateEmptyGraph() throws Exception {
		assertEquals(new SimpleGraph(), new SimpleGraphFactory().createGraph(Collections.<String>emptyList().stream()));
	}

	@Test
	public void givenOneLineShouldCreateGraphWithOneEdge() throws Exception {
		final var expected = new SimpleGraph();
		expected.addEdge(new SimpleEdge().setWeight(1.0D));
		assertEquals(expected,
				new SimpleGraphFactory().createGraph(Collections.<String>singletonList("1.0D;").stream()));
	}

}
