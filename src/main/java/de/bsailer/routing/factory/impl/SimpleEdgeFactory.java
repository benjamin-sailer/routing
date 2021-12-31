package de.bsailer.routing.factory.impl;

import de.bsailer.routing.factory.EdgeFactory;
import de.bsailer.routing.factory.EdgeIdentifierSupplier;
import de.bsailer.routing.model.impl.SimpleEdge;
import de.bsailer.routing.model.impl.SimpleEdgeIdentifier;

public class SimpleEdgeFactory implements EdgeFactory<SimpleEdge, SimpleEdgeIdentifier>,
        EdgeIdentifierSupplier<SimpleEdgeIdentifier> {

    private int latestAdditionalId = -1;

    @Override
    public SimpleEdgeIdentifier edgeIdentifierFromDescriptor(final String edgeIdentifierDescriptor) {
        return new SimpleEdgeIdentifier(Integer.parseInt(edgeIdentifierDescriptor));
    }

    @Override
    public SimpleEdge edgeFromDescriptor(final SimpleEdgeIdentifier id, final String edgeDescriptor) {
        return new SimpleEdge(id).setWeight(Double.parseDouble(edgeDescriptor));
    }

    @Override
    public SimpleEdge edgeWithWeight(final EdgeIdentifierSupplier<SimpleEdgeIdentifier> idSupplier, final double weight) {
        return new SimpleEdge(idSupplier.newId()).setWeight(weight);
    }

    @Override
    public SimpleEdgeIdentifier newId() {
        return new SimpleEdgeIdentifier(latestAdditionalId--);
    }

}
