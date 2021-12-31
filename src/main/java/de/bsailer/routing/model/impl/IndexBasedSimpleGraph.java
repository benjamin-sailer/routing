package de.bsailer.routing.model.impl;

import de.bsailer.routing.model.ConstructableGraph;
import de.bsailer.routing.model.Edge;
import de.bsailer.routing.model.IndexProvidingEdgeIdentifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple {@code Array}-based implementation of {@code Graph}.
 *
 * @param <E> concrete type of {@code Edge}s within the graph.
 * @param <I> concrete type of {@code IndexProvidingEdgeIdentifier} of these edges.
 */
public class IndexBasedSimpleGraph<E extends Edge<I>, I extends IndexProvidingEdgeIdentifier<I>> implements ConstructableGraph<E, I> {

	private final List<E> edges = new ArrayList<>();

	private final List<List<Integer>> adjacents = new ArrayList<>();

	@Override
	public List<E> adjacents(final I id) {
		final var edgeAdjacents = adjacents.get(id.index());
		return edgeAdjacents.stream().map(edges::get).toList();
	}

	@Override
	public E edge(final I id) {
		return edges.get(id.index());
	}

	@Override
	public void addEdge(final E edge) {
		final int index = edge.id().index();
		extendArrayIfNeeded((ArrayList<E>) edges, index);
		edges.set(index, edge);
	}

	@Override
	public void connectEdges(final I edgeId, final I adjacentId) {
		final int index = edgeId.index();
		extendArrayIfNeeded((ArrayList<List<Integer>>) adjacents, index);
		if (adjacents.get(index) == null) {
			adjacents.set(index, new ArrayList<>());
		}
		adjacents.get(index).add(adjacentId.index());
	}

	private void extendArrayIfNeeded(final ArrayList<?> array, final int index) {
		final int capacity = index + 1;
		array.ensureCapacity(capacity);
		for (int i = array.size(); i < capacity; i++) {
			array.add(null);
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ", edges=" + edges + ", adjacents=" + adjacents;
	}
}
