package de.bsailer.routing.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class represents an ordered sequence of {@code Edge}s - a possible result of a routing.
 *
 */
@SuppressWarnings("ClassCanBeRecord")
public final class Route<E extends Edge<?>> {
	private final List<E> edges;


	public Route(final List<E> edges) {
		this.edges = Objects.requireNonNull(edges);
	}

	public List<E> edges() {
		return Collections.unmodifiableList(edges);
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof final Route other) {
			return Objects.equals(edgeIds(), other.edgeIds());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(edgeIds());
	}

	private List<?> edgeIds() {
		return edges.stream().map(Edge::id).collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + edges;
	}
}
