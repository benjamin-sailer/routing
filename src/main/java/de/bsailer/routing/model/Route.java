package de.bsailer.routing;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record Route<E extends Edge<?>>(List<E> edges) {

	public Route(final List<E> edges) {
		this.edges = Objects.requireNonNull(edges);
	}

	@Override
	public List<E> edges() {
		return Collections.unmodifiableList(edges);
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof final Route other) {
			return Objects.equals(edges, other.edges);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(edges);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + edges;
	}
}
