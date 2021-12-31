package de.bsailer.routing.model.prohibition;

import de.bsailer.routing.model.EdgeIdentifier;

import java.util.Objects;

/**
 * This {@code EdgeIdentifier} implementation holds a delegate thus allowing several copies of that
 * delegate.  This 1:n-relation is needed for non-bijective graph transformations.
 *
 * One object of this class (per delegate object) can implicitly be constructed as the "root copy".
 * {@code copy}-values greater or equal 0 are available for copies apart from the "root copy".
 *
 * @param <I> concrete type of delegate {@code EdgeIdentifier}s.
 */
public class ShadowEdgeIdentifier<I extends EdgeIdentifier<I>>
        implements EdgeIdentifier<ShadowEdgeIdentifier<I>> {

    private static final int ROOT_COPY = -1;

    private final I delegateId;

    private final int copy;

    public ShadowEdgeIdentifier(final I id, final int copy) {
        this.delegateId = id;
        this.copy = copy;
    }

    public ShadowEdgeIdentifier(final I id) {
        this.delegateId = id;
        this.copy = ROOT_COPY;
    }

    @Override
    public boolean equals(final ShadowEdgeIdentifier<I> other) {
        return delegateId.equals(other.delegateId) && copy == other.copy;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof ShadowEdgeIdentifier<?> other) {
            @SuppressWarnings("unchecked")
            final ShadowEdgeIdentifier<I> otherI = (ShadowEdgeIdentifier<I>) other;
            return equals(otherI);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegateId, copy);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{delegateId=" + delegateId + ", copy=" + copy + "}";
    }

    I delegateId() {
        return delegateId;
    }

    int copy() {
        return copy;
    }

    boolean isRootCopy() {
        return copy == ROOT_COPY;
    }

}
