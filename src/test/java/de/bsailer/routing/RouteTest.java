package de.bsailer.routing;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;

public class RouteTest {

	@Test
	public void givenRouteHasEdges() {
		assertEquals(Collections.emptyList(), new Route(Collections.emptyList()).edges());
	}

}
