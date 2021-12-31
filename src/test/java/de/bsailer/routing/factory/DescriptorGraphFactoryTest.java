package de.bsailer.routing.factory;

import de.bsailer.routing.factory.impl.SimpleEdgeFactory;
import de.bsailer.routing.model.Edge;
import de.bsailer.routing.model.Graph;
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
public class DescriptorGraphFactoryTest {

	@Test
	public void givenEmptyStreamCreateSimpleGraphShouldCreateEmptySimpleGraph() {
		final var result = new DescriptorGraphFactory<>(new SimpleEdgeFactory()).createSimpleGraph(Stream.empty());
		assertEquals(SimpleGraph.class, result.getClass());
	}

	@Test
	public void givenOneLineShouldCreateGraphWithOneEdge() {
		final Graph<SimpleEdge, SimpleEdgeIdentifier> result = new DescriptorGraphFactory<>(new SimpleEdgeFactory()).createSimpleGraph(Stream.of("0;1.0;1", "1;3.0;1"));
		final var fromEdge = new SimpleEdge(new SimpleEdgeIdentifier(0));
		assertEquals(Collections.singletonList(new SimpleEdgeIdentifier(1)),
				result.adjacents(fromEdge.id())
					.stream()
					.map(Edge::id)
					.toList());
	}

}
