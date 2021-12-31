package de.bsailer.routing.factory.impl;

import de.bsailer.routing.model.impl.SimpleEdgeIdentifier;
import org.junit.Test;

import static de.bsailer.test.ExtendedAssert.assertEqualsDouble;
import static org.junit.Assert.assertEquals;

public class SimpleEdgeFactoryTest {

    private final SimpleEdgeFactory sut = new SimpleEdgeFactory();

    @Test
    public void edgeIdentifierFromDescriptor() {
        final var id = sut.edgeIdentifierFromDescriptor("1");
        assertEquals(SimpleEdgeIdentifier.class, id.getClass());
        assertEquals(1, id.index());
    }

    @Test
    public void edgeFromDescriptor() {
        final var id = new SimpleEdgeIdentifier(1);
        assertEqualsDouble(1.0D, sut.edgeFromDescriptor(id, "1.0").weight());
    }

    @Test
    public void edgeWithWeight() {
        final var id = new SimpleEdgeIdentifier(1);
        final var result = sut.edgeWithWeight(() -> id, 1.0D);
        assertEqualsDouble(1.0D, result.weight());
        assertEquals(result.id(), id);
    }

    @Test
    public void newId() {
        assertEquals(new SimpleEdgeIdentifier(-1), sut.newId());
    }

}