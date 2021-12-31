package de.bsailer.routing.model.prohibition;

import de.bsailer.routing.model.Edge;
import de.bsailer.routing.model.EdgeIdentifier;

/**
 * This {@code Edge} implementation holds a delegate thus allowing several copies of that
 * delegate.  This 1:n-relation is needed for non-bijective graph transformations.
 *
 * @param <E> concrete type of delegate {@code Edge}s.
 * @param <I> concrete type of delegate {@code EdgeIdentifier}s.
 */
public class ShadowEdge<E extends Edge<I>, I extends EdgeIdentifier<I>>
        implements Edge<ShadowEdgeIdentifier<I>> {

    private final ShadowEdgeIdentifier<I> id;

    private final E delegateEdge;

    ShadowEdge(final E delegateEdge, final int copy) {
        this.delegateEdge = delegateEdge;
        this.id = new ShadowEdgeIdentifier<>(delegateEdge.id(), copy);
    }

    ShadowEdge(final E delegateEdge) {
        this.delegateEdge = delegateEdge;
        this.id = new ShadowEdgeIdentifier<>(delegateEdge.id());
    }

    @Override
    public ShadowEdgeIdentifier<I> id() {
        return id;
    }

    @Override
    public double weight() {
        return delegateEdge.weight();
    }
}
