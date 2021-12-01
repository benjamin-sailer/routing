package de.bsailer.routing;

public interface DijkstraAborter<E extends Edge<?>> {

	boolean abort(E current);
}
