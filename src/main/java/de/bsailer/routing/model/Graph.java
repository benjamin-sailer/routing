package de.bsailer.routing.model;

import java.util.List;

/**
 * Implementors of this interface represent a source of {@code Edge}s and can thus be used for traversal.
 *
 * @param <E> concrete type of the {@code Edge}s provided.
 */
public interface Graph<E extends Edge<I>, I extends EdgeIdentifier<I>> {

	/**
	 * returns the adjacents of a given edge.
	 */
	List<E> adjacents(I id);

	E edge(I id);
}