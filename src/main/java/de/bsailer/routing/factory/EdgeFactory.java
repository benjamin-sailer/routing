package de.bsailer.routing.factory;

import de.bsailer.routing.model.Edge;
import de.bsailer.routing.model.EdgeIdentifier;

public interface EdgeFactory<E extends Edge<I>, I extends EdgeIdentifier<I>> {

    I edgeIdentifierFromDescriptor(String edgeIdentifierDescriptor);

    E edgeFromDescriptor(I id, String edgeDescriptor);

    E edgeWithWeight(EdgeIdentifierSupplier<I> idSupplier, double weight);
}
