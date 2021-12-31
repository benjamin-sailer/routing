package de.bsailer.routing.model;

public interface ConstructableGraph<E extends Edge<I>, I extends EdgeIdentifier<I>> extends Graph<E, I> {

    void addEdge(E edge);

    void connectEdges(I edgeId, I adjacentId);
}
