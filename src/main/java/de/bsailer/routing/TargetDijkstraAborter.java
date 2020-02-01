package de.bsailer.routing;

import java.util.Objects;

public class TargetDijkstraAborter implements DijkstraAborter {

	private final Edge target;

	public TargetDijkstraAborter(final Edge target) {
		this.target = Objects.requireNonNull(target);
	}

	@Override
	public boolean abort(final Edge current) {
		return target.equals(current);
	}

}
