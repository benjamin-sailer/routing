package de.bsailer.routing.model;

import de.bsailer.routing.model.impl.SimpleEdge;
import de.bsailer.routing.model.impl.SimpleEdgeIdentifier;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class RouteTest {

	@Test
	public void givenRouteHasNoEdgesEdgesReturnsEmptyList() {
		assertEquals(Collections.emptyList(), new Route<>(Collections.emptyList()).edges());
	}

	@Test
	public void givenRouteEdgesReturnsThem() {
		var edge1 = new SimpleEdge(new SimpleEdgeIdentifier(1));
		assertEquals(Collections.singletonList(edge1), new Route<>(Collections.singletonList(edge1)).edges());
	}

	@Test
	public void givenRoutesHaveEdgesEqualsIsBasedOnEdgeIdentifier() {
		var edge1 = new SimpleEdge(new SimpleEdgeIdentifier(1));
		var edge2 = new SimpleEdge(new SimpleEdgeIdentifier(1));
		assertEquals(new Route<>(Collections.singletonList(edge1)), new Route<>(Collections.singletonList(edge2)));
	}

    @Test
    public void givenRoutesHaveDifferentEdgeIdentifiersEqualsEqualsReturnsFalse() {
		var edge1 = new SimpleEdge(new SimpleEdgeIdentifier(1));
		var edge2 = new SimpleEdge(new SimpleEdgeIdentifier(2));
		assertNotEquals(new Route<>(Collections.singletonList(edge1)), new Route<>(Collections.singletonList(edge2)));
    }

    @Test
    public void givenRoutesHaveEdgesHashCodeIsBasedOnEdgeIdentifier() {
		var edge1 = new SimpleEdge(new SimpleEdgeIdentifier(1));
		var edge2 = new SimpleEdge(new SimpleEdgeIdentifier(1));
		assertEquals(new Route<>(Collections.singletonList(edge1)).hashCode(), new Route<>(Collections.singletonList(edge2)).hashCode());
    }

	@Test
	public void givenNoRouteEqualsBehavesSafely() {
		assertFalse(new Route<>(Collections.emptyList()).equals(new Object()));
	}

	@Test
	public void toStringShouldPrintSomethingNonDefault() {
		assertEquals("Route []", new Route<>(Collections.emptyList()).toString());
	}

}
