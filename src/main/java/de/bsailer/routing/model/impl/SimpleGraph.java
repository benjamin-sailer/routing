package de.bsailer.routing.model.impl;

import de.bsailer.routing.model.Edge;
import de.bsailer.routing.model.EdgeIdentifier;
import de.bsailer.routing.model.Graph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Simple {@code Map}-based implementation of {@code Graph}.
 *
 * @param <E> concrete type of {@code Edge}s within the graph.
 * @param <I> concrete type of {@code IndexProvidingEdgeIdentifier} of these edges.
 */
public class SimpleGraph<E extends Edge<I>, I extends EdgeIdentifier<I>> implements Graph<E> {

	private final Map<I, E> edges = new HashMap<>();

	private final Map<I,List<I>> adjacents = new HashMap<>();

	@Override
	public List<E> adjacents(final E edge) {
		final var edgeAdjacents = adjacents.get(edge.id());
		return edgeAdjacents.stream().map(edges::get).collect(Collectors.toList());
	}

	public void addEdge(final E edge) {
		edges.put(edge.id(), edge);
	}

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
