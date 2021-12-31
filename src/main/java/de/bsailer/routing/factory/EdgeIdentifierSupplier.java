package de.bsailer.routing.factory;

import de.bsailer.routing.model.EdgeIdentifier;

public interface EdgeIdentifierSupplier<I extends EdgeIdentifier<I>> {

    I newId();

}
