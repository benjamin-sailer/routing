package de.bsailer.routing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleGraph implements Graph {

	private final List<Edge> edges = new ArrayList<>();

	private final List<List<Integer>> adjacents = new ArrayList<>();

	public int addEdge(final Edge edge) {
		edges.add(edge);
		return edges.size() - 1;
	}

	public void connectEdges(final Edge edge, final Edge adjacent) {
		final int edgeIndex = edges.indexOf(edge);
		final int adjacentIndex = edges.indexOf(adjacent);
		connectEdges(edgeIndex, adjacentIndex);
	}

	public void connectEdges(final int edgeIndex, final int adjacentIndex) {
		initializeUpTo(edgeIndex);
		adjacents.get(edgeIndex).add(adjacentIndex);
	}

	private void initializeUpTo(final int maxIndex) {
		while (adjacents.size() <= maxIndex) {
			adjacents.add(new ArrayList<>());
		}
	}

	@Override
	public List<Edge> adjacents(final Edge edge) {
		final var edgeAdjacents = adjacents.get(edges.indexOf(edge));
		return edgeAdjacents.stream().map((index) -> edges.get(index)).collect(Collectors.toList());
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof SimpleGraph) {
			final SimpleGraph other = (SimpleGraph) o;
			return edges.equals(other.edges) && adjacents.equals(other.adjacents);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 31 * edges.hashCode() + adjacents.hashCode();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ", edges=" + edges.toString() + ", adjacents=" + adjacents.toString();
	}
}
