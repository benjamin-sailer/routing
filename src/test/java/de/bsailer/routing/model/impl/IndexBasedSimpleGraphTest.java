package de.bsailer.routing.model.impl;

import de.bsailer.routing.model.Edge;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IndexBasedSimpleGraphTest {

	@Mock
	private Edge<SimpleEdgeIdentifier> edge1;

	@Mock
	private Edge<SimpleEdgeIdentifier> edge2;

	private final IndexBasedSimpleGraph<Edge<SimpleEdgeIdentifier>, SimpleEdgeIdentifier> sut = new IndexBasedSimpleGraph<>();

	@Test
	public void givenGraphDeliversAdjacents() {
		when(edge1.id()).thenReturn(new SimpleEdgeIdentifier(1));
		when(edge2.id()).thenReturn(new SimpleEdgeIdentifier(2));
		sut.addEdge(edge1);
		sut.addEdge(edge2);
		sut.connectEdges(edge1.id(), edge2.id());
		assertEquals(Collections.singletonList(edge2), sut.adjacents(edge1.id()));
	}

    @Test
    public void givenEdgeIdGraphDeliversEdge() {
		when(edge1.id()).thenReturn(new SimpleEdgeIdentifier(1));
		sut.addEdge(edge1);
		assertEquals(edge1, sut.edge(new SimpleEdgeIdentifier(1)));
    }

}
