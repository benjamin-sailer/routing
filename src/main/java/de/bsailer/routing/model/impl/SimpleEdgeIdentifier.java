package de.bsailer.routing.model.impl;

import de.bsailer.routing.model.IndexProvidingEdgeIdentifier;

/**
 * This class is a minimal int-based implementation of {@code EdgeIdentifier}
 * and even {@code IndexProvidingEdgeIdentifier}.
 */
@SuppressWarnings("ClassCanBeRecord")
public class SimpleEdgeIdentifier implements IndexProvidingEdgeIdentifier<SimpleEdgeIdentifier> {

    private final int id;

    public SimpleEdgeIdentifier(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(final SimpleEdgeIdentifier other) {
        return id == other.id;
    }

    @Override
    public int index() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof SimpleEdgeIdentifier other) {
            return equals(other);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + id + "}";
    }
}
