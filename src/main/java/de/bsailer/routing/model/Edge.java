package de.bsailer.routing.model;

/**
 * Implementors of this interface represent an Edge inside a Graph.
 *
 * @param <I> type of the identifier.
 */
public interface Edge<I extends EdgeIdentifier<I>> {

	/**
	 * Retrieve the {@code EdgeIdentifier}.
	 */
	I id();

	/**
	 * Retrieve the edges weight.
	 */
	double weight();

}