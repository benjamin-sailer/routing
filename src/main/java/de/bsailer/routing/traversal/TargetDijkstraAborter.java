package de.bsailer.routing.traversal;

import de.bsailer.routing.model.Edge;

import java.util.Objects;

/**
 * Simple implementation of {@code DijkstraAborter} that aborts when a target {@code Edge} is reached.
 *
 * @param <E> concrete type of the {@code Edge}
 */
@SuppressWarnings("ClassCanBeRecord")
public class TargetDijkstraAborter<E extends Edge<?>> implements DijkstraAborter<E> {

	private final E target;

	public TargetDijkstraAborter(final E target) {
		this.target = Objects.requireNonNull(target);
	}

	@Override
	public boolean abort(final E current) {
		return target.id().equals(current.id());
	}

}
