package de.bsailer.routing.model.impl;

import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleEdgeIdentifierTest {

    @Test
    public void givenSameIdEdgeIdentifierEqualsReturnsTrue() {
        final SimpleEdgeIdentifier id1 = new SimpleEdgeIdentifier(1);
        final SimpleEdgeIdentifier id2 = new SimpleEdgeIdentifier(1);
        assertTrue(id1.equals(id2));
    }

    @Test
    public void givenDifferentIdEdgeIdentifierEqualsReturnsFalse() {
        final SimpleEdgeIdentifier id1 = new SimpleEdgeIdentifier(1);
        final SimpleEdgeIdentifier id2 = new SimpleEdgeIdentifier(2);
        assertFalse(id1.equals(id2));
    }

    @Test
    public void givenSameIdObjectEqualsReturnsTrue() {
        final SimpleEdgeIdentifier id1 = new SimpleEdgeIdentifier(1);
        final SimpleEdgeIdentifier id2 = new SimpleEdgeIdentifier(1);
        assertTrue(id1.equals((Object) id2));
    }

    @Test
    public void givenDifferentIdObjectEqualsReturnsFalse() {
        final SimpleEdgeIdentifier id1 = new SimpleEdgeIdentifier(1);
        final SimpleEdgeIdentifier id2 = new SimpleEdgeIdentifier(2);
        assertFalse(id1.equals((Object) id2));
    }

    @Test
    public void givenOtherObjectObjectEqualsReturnsFalse() {
        final SimpleEdgeIdentifier id1 = new SimpleEdgeIdentifier(1);
        assertFalse(id1.equals(new Object()));
    }

    @Test
    public void givenSimpleEdgeIdentifierIndexReturnsConstructorArgument() {
        assertEquals(1, new SimpleEdgeIdentifier(1).index());
    }

    @Test
    public void givenSameIdObjectHashCodeReturnsSameValue() {
        final SimpleEdgeIdentifier id1 = new SimpleEdgeIdentifier(1);
        final SimpleEdgeIdentifier id2 = new SimpleEdgeIdentifier(1);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    public void toStringShouldReturnSomethingMeaningful() {
        assertEquals("SimpleEdgeIdentifier{1}", new SimpleEdgeIdentifier(1).toString());
    }
}