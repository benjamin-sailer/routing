package de.bsailer.routing.factory;

import de.bsailer.routing.model.impl.SimpleEdge;
import de.bsailer.routing.model.impl.SimpleEdgeIdentifier;
import de.bsailer.routing.model.impl.SimpleGraph;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class SimpleGraphFactoryTest {

	@Test
	public void givenEmptyStreamCreateSimpleGraphShouldCreateEmptySimpleGraph() {
		final var result = new SimpleGraphFactory().createSimpleGraph(Stream.empty());
		assertEquals(SimpleGraph.class, result.getClass());
	}

	@Test
	public void givenOneLineShouldCreateGraphWithOneEdge() {
		final var result = new SimpleGraphFactory().createSimpleGraph(Stream.of("0;1.0;1", "1;3.0;1"));
		final var fromEdge = new SimpleEdge(new SimpleEdgeIdentifier(0));
		assertEquals(Collections.singletonList(new SimpleEdgeIdentifier(1)),
				result.adjacents(fromEdge)
					.stream()
					.map(SimpleEdge::id)
					.toList());
	}

}
