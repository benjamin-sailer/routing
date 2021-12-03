package de.bsailer.routing.model;

/**
 * Implementations serve as immutable id of an {@code Edge}.
 *
 * Edges are identified as being the same if their {@code EdgeIdentifier}s are equal, so equality checks for edges
 * should be delegated to the {@code EdgeIdentifier} of the edges.
 */
public interface EdgeIdentifier<T extends EdgeIdentifier<T>> {

    /**
     * Checks if the other is the same as the given one.
     *
     * @param other {@code EdgeIdentifier} to check equality against.
     * @return true if the other identifier matches this, false else.
     */
    boolean equals(final T other);

}
