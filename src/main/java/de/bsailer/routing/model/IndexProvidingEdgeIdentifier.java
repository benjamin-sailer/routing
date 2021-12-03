package de.bsailer.routing.model;

/**
 * Implementors of this interface provide means for an O(0)-access to the edges, as the interface provides an index
 * that can be used for array access.

 * @param <I> concrete type of {@code IndexProvidingEdgeIdentifier}
 */
public interface IndexProvidingEdgeIdentifier<I extends IndexProvidingEdgeIdentifier<I>> extends EdgeIdentifier<I> {

    int index();

}
