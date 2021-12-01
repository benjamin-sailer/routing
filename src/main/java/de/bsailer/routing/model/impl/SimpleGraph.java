package de.bsailer.routing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleGraph<E extends Edge<I>, I extends EdgeIdentifier<I>> implements Graph<E> {

	private final Map<I, E> edges = new HashMap<>();

	private final Map<I,List<I>> adjacents = new HashMap<>();

	@Override
	public List<E> adjacents(final E edge) {
		final var edgeAdjacents = adjacents.get(edge.id());
		return edgeAdjacents.stream().map(edges::get).collect(Collectors.toList());
	}

	public int addEdge(final E edge) {
		edges.put(edge.id(), edge);
		return edges.size() - 1;
	}

	public void connectEdges(final I edge, final I adjacent) {
		adjacents.get(edge).add(adjacent);
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof final SimpleGraph other) {
			return edges.keySet().equals(other.edges.keySet()) && adjacents.equals(other.adjacents);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 31 * edges.hashCode() + adjacents.hashCode();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ", edges=" + edges + ", adjacents=" + adjacents;
	}
}
