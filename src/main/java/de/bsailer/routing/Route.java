package de.bsailer.routing;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Route {

	private final List<Edge> edges;

	public Route(final List<Edge> edges) {
		this.edges = Objects.requireNonNull(edges);
	}

	public List<Edge> edges() {
		return Collections.unmodifiableList(edges);
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof Route) {
			final Route other = (Route) o;
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
		return getClass().getSimpleName() + " " + edges.toString();
	}
}
