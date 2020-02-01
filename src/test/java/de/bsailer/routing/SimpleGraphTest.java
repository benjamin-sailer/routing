package de.bsailer.routing;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SimpleGraphTest {

	@Mock
	private Edge edge1;

	@Mock
	private Edge edge2;

	private final SimpleGraph sut = new SimpleGraph();

	@Test
	public void givenGraphDeliversAdjacents() {
		final var index1 = sut.addEdge(edge1);
		final var index2 = sut.addEdge(edge2);
		sut.connectEdges(index1, index2);
		assertEquals(Collections.singletonList(edge2), sut.adjacents(edge1));
	}

	@Test
	public void givenGraphDeliversAdjacentsConstructedByEdge() {
		sut.addEdge(edge1);
		sut.addEdge(edge2);
		sut.connectEdges(edge1, edge2);
		assertEquals(Collections.singletonList(edge2), sut.adjacents(edge1));
	}

}
