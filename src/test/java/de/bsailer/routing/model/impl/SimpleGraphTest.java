package de.bsailer.routing;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collections;

import de.bsailer.routing.model.Edge;
import de.bsailer.routing.model.impl.SimpleEdgeIdentifier;
import de.bsailer.routing.model.impl.SimpleGraph;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SimpleGraphTest {

	@Mock
	private Edge<SimpleEdgeIdentifier> edge1;

	@Mock
	private Edge<SimpleEdgeIdentifier> edge2;

	private final SimpleGraph<Edge<SimpleEdgeIdentifier>, SimpleEdgeIdentifier> sut = new SimpleGraph<>();

	@Test
	public void givenGraphDeliversAdjacents() {
		when(edge1.id()).thenReturn(new SimpleEdgeIdentifier(1));
		when(edge2.id()).thenReturn(new SimpleEdgeIdentifier(2));
		sut.addEdge(edge1);
		sut.addEdge(edge2);
		sut.connectEdges(edge1.id(), edge2.id());
		assertEquals(Collections.singletonList(edge2), sut.adjacents(edge1));
	}

}
