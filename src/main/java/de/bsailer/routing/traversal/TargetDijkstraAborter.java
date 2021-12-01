package de.bsailer.routing;

import java.util.Objects;

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
