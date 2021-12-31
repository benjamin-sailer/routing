package de.bsailer.routing.model.impl;

import de.bsailer.routing.model.ConstructableGraph;
import de.bsailer.routing.model.Edge;
import de.bsailer.routing.model.EdgeIdentifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simple {@code Map}-based implementation of {@code Graph}.
 *
 * @param <E> concrete type of {@code Edge}s within the graph.
 * @param <I> concrete type of {@code IndexProvidingEdgeIdentifier} of these edges.
 */
public class SimpleGraph<E extends Edge<I>, I extends EdgeIdentifier<I>> implements ConstructableGraph<E, I> {

	private final Map<I, E> edges = new HashMap<>();

	private final Map<I,List<I>> adjacents = new HashMap<>();

	@Override
	public List<E> adjacents(final I id) {
		final var edgeAdjacents = adjacents.get(id);
		return edgeAdjacents.stream().map(edges::get).collect(Collectors.toList());
	}

	@Override
	public E edge(final I id) {
		return edges.get(id);
	}

	@Override
	public void addEdge(final E edge) {
		edges.put(edge.id(), edge);
	}

	@Override
	public void connectEdges(final I edgeId, final I adjacentId) {
		if (!adjacents.containsKey(edgeId)) {
			adjacents.put(edgeId, new ArrayList<>());
		}
		adjacents.get(edgeId).add(adjacentId);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ", edges=" + edges + ", adjacents=" + adjacents;
	}

}
