package de.bsailer.routing.traversal;

import de.bsailer.routing.model.Edge;
import de.bsailer.routing.model.impl.SimpleEdge;
import de.bsailer.routing.model.impl.SimpleEdgeIdentifier;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TargetDijkstraAborterTest {

	private final Edge<SimpleEdgeIdentifier> current = new SimpleEdge(new SimpleEdgeIdentifier(1));

	private final Edge<SimpleEdgeIdentifier> target = new SimpleEdge(new SimpleEdgeIdentifier(2));

	@Test
	public void abortsOnTarget() {
		final var sut = new TargetDijkstraAborter<>(target);
		assertTrue(sut.abort(target));
	}

	@Test
	public void continuesElse() {
		final var sut = new TargetDijkstraAborter<>(target);
		assertFalse(sut.abort(current));
	}
}
