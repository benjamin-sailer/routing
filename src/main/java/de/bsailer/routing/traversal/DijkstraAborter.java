package de.bsailer.routing.traversal;

import de.bsailer.routing.model.Edge;

/**
 * Implementors of this interface provide criterion to abort a {@code Dijkstra} run by visiting the current {@code Edge}.
 *
 * @param <E> concrete type of the edge.
 */
public interface DijkstraAborter<E extends Edge<?>> {

	boolean abort(E current);
}
