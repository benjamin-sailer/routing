package de.bsailer.routing.model;

import java.util.List;

/**
 * Implementors of this interface represent a source of {@code Edge}s and can thus be used for traversal.
 *
 * @param <T> concrete type of the {@code Edge}s provided.
 */
public interface Graph<T extends Edge<?>> {

	/**
	 * returns the adjacents of a given edge.
	 */
	List<T> adjacents(T edge);

}