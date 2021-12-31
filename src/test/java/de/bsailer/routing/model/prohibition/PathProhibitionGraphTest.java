package de.bsailer.routing.model.prohibition;

import de.bsailer.routing.factory.EdgeFactory;
import de.bsailer.routing.factory.EdgeIdentifierSupplier;
import de.bsailer.routing.model.Graph;
import de.bsailer.routing.model.impl.SimpleEdge;
import de.bsailer.routing.model.impl.SimpleEdgeIdentifier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.*;

import static de.bsailer.test.ExtendedAssert.assertEqualsDouble;
import static java.lang.Math.abs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class PathProhibitionGraphTest {

    private static final double DEFAULT_WEIGHT = 1.0D;
    private static final double PENALTY_WEIGHT = 10.0D;

    private static final SimpleEdgeIdentifier ID0 = new SimpleEdgeIdentifier(0);
    private static final SimpleEdge EDGE0 = new SimpleEdge(ID0).setWeight(DEFAULT_WEIGHT);

    private static final SimpleEdgeIdentifier ID1 = new SimpleEdgeIdentifier(1);
    private static final SimpleEdge EDGE1 = new SimpleEdge(ID1).setWeight(DEFAULT_WEIGHT);

    private static final SimpleEdgeIdentifier ID2 = new SimpleEdgeIdentifier(2);
    private static final SimpleEdge EDGE2 = new SimpleEdge(ID2).setWeight(DEFAULT_WEIGHT);

    private static final SimpleEdgeIdentifier ID3 = new SimpleEdgeIdentifier(3);
    private static final SimpleEdge EDGE3 = new SimpleEdge(ID3).setWeight(DEFAULT_WEIGHT);

    private static final SimpleEdgeIdentifier ID4 = new SimpleEdgeIdentifier(4);

    private PathProhibitionGraph<SimpleEdge, SimpleEdgeIdentifier> sut;

    @Mock
    private Graph<SimpleEdge, SimpleEdgeIdentifier> delegateGraph;

    @Mock
    private EdgeFactory<SimpleEdge, SimpleEdgeIdentifier> edgeFactory;

    @Mock
    private EdgeIdentifierSupplier<SimpleEdgeIdentifier> edgeIdentifierSupplier;

    @Before
    public void setUp() {
        sut = new PathProhibitionGraph<>(delegateGraph, edgeFactory, edgeIdentifierSupplier, PENALTY_WEIGHT);
        @SuppressWarnings("unchecked")
        final Answer<SimpleEdge> newEdgeAnswer = inv ->
                new SimpleEdge(((EdgeIdentifierSupplier<SimpleEdgeIdentifier>) inv.getArgument(0, EdgeIdentifierSupplier.class)).newId())
                        .setWeight(inv.getArgument(1));
        when(edgeFactory.edgeWithWeight(any(), anyDouble())).thenAnswer(newEdgeAnswer);
        when(edgeIdentifierSupplier.newId()).thenReturn(new SimpleEdgeIdentifier(-1),
                new SimpleEdgeIdentifier(-2),
                new SimpleEdgeIdentifier(-3),
                new SimpleEdgeIdentifier(-4),
                new SimpleEdgeIdentifier(-5));
    }

    @Test
    public void givenUndecoratedGraphEdgeGivesDelegateWeight() {
        when(delegateGraph.edge(ID0)).thenReturn(EDGE0);
        assertEqualsDouble(DEFAULT_WEIGHT, sut.edge(new ShadowEdgeIdentifier<>(ID0)).weight());
    }

    @Test
    public void givenUndecoratedGraphAdjacentsGivesDelegateAdjacents() {
        when(delegateGraph.adjacents(ID0)).thenReturn(Collections.singletonList(EDGE1));
        when(delegateGraph.edge(ID1)).thenReturn(EDGE1);
        assertEquals(Collections.singletonList(ID1), sut.adjacents(new ShadowEdgeIdentifier<>(ID0)).stream().map(ShadowEdge::id).map(ShadowEdgeIdentifier::delegateId).toList());
    }

    /**
     * Check transformation:
     *
     * e_0(-1, 1) -> e_1(-1, 1)
     * with path prohibition e_0,e_1
     * should become
     * e_0(-1, 1) -> e_penalty(0, inf) -> e_1(0, 1) <===
     *
     * (internal structure:
     * - overlayEdges e_point(0, 0), e_penalty(0, inf)
     * - overlayAdjacents e_0(-1, 1) -> e_penalty(0, inf),
     *                    e_penalty(0, inf) -> e_1(0, 1)
     * - removedAdjacents e_0(-1, 1) -> e_1(-1, 1)
     */
    @Test
    public void givenTurnProhibitionGraphAdjacentsAddsPenalty() {
        when(delegateGraph.adjacents(ID0)).thenReturn(Collections.singletonList(EDGE1));
        when(delegateGraph.edge(ID1)).thenReturn(EDGE1);
        sut.addProhibitedPath(Arrays.asList(ID0, ID1));
        checkEdgeIsReachableViaPenalty(new ShadowEdgeIdentifier<>(ID0), 0, ID1);
    }

    /**
     * Check transformation:
     *
     * e_0(-1, 1) -> e_1(-1, 1) -> e_2(-1, 1)
     * with path prohibition e_0,e_1,e_2
     * should become
     * e_0(-1, 1) -> e_1(-1, 1) -> e_penalty(0, inf) -> e_2(0, 1) <===
     *
     * (internal structure:
     * - overlayEdges e_point(0, 0), e_1(0, 1), e_penalty(0, inf)
     * - overlayAdjacents e_1(-1, 1) -> e_penalty(0, inf),
     *                    e_penalty(0, inf) -> e_2(0, 1)
     * - removedAdjacents e_1(-1, 1) -> e_2(-1, 1)
     */
    @Test
    public void givenPathProhibitionGraphAdjacentsAddPenaltyBeforeLastEdge() {
        when(delegateGraph.adjacents(ID0)).thenReturn(Collections.singletonList(EDGE1));
        when(delegateGraph.adjacents(ID1)).thenReturn(Collections.singletonList(EDGE2));
        when(delegateGraph.edge(ID1)).thenReturn(EDGE1);
        when(delegateGraph.edge(ID2)).thenReturn(EDGE2);
        sut.addProhibitedPath(Arrays.asList(ID0, ID1, ID2));
        var idBeforePenalty = checkPathExists(new ShadowEdgeIdentifier<>(ID0), new ShadowEdgeIdentifier<>(ID1));
        checkEdgeIsReachableViaPenalty(idBeforePenalty.get(idBeforePenalty.size() - 1), 0, ID2);
    }

    /**
     * Check transformation:
     *
     * e_0(-1, 1) -> e_1(-1, 1) -> e_2(-1, 1)
     *                          -> e_3(-1, 1)
     * with path prohibition e_0,e_1,e_2
     * should become
     * e_0(-1, 1) -> e_1(-1, 1) -> e_penalty(0, inf) -> e_2(0, 1)
     *                          -> e_3(-1, 1) <===
     *
     * (internal structure:
     * - overlayEdges e_point(0, 0), e_1(0, 1), e_penalty(0, inf)
     * - overlayAdjacents e_1(-1, 1) -> e_penalty(0, inf),
     *                    e_penalty(0, inf) -> e_2(0, 1)
     * - removedAdjacents e_1(-1, 1) -> e_2(-1, 1)
     */
    @Test
    public void givenPathProhibitionGraphTraversalAwayFromShouldBePossibleNormally() {
        when(delegateGraph.adjacents(ID0)).thenReturn(Collections.singletonList(EDGE1));
        when(delegateGraph.adjacents(ID1)).thenReturn(Arrays.asList(EDGE2, EDGE3));
        when(delegateGraph.edge(ID1)).thenReturn(EDGE1);
        when(delegateGraph.edge(ID2)).thenReturn(EDGE2);
        when(delegateGraph.edge(ID3)).thenReturn(EDGE3);
        sut.addProhibitedPath(Arrays.asList(ID0, ID1, ID2));
        System.out.println(checkPathExists(new ShadowEdgeIdentifier<>(ID0), new ShadowEdgeIdentifier<>(ID1), new ShadowEdgeIdentifier<>(ID3)));
    }

    /**
     * Check transformation:
     *
     * e_0(-1, 1) -> e_1(-1, 1) -> e_2(-1, 1)
     *                          -> e_3(-1, 1)
     * with path prohibition e_0,e_1,e_2
     * should become
     * e_0(-1, 1) -> e_1(-1, 1) -> e_penalty(0, inf) -> e_2(0, 1)
     *
     *               e_1(0, 1) -> e_2(1, 1) <===
     * (internal structure:
     * - overlayEdges e_point(0, 0), e_1(0, 1), e_penalty(0, inf)
     * - overlayAdjacents e_1(-1, 1) -> e_penalty(0, inf),
     *                    e_penalty(0, inf) -> e_2(0, 1)
     * - removedAdjacents e_1(-1, 1) -> e_2(-1, 1)
     */
    @Test
    public void givenPathProhibitionStartingFromIntermediateShadowEdgeShouldBePossibleIncludingLastEdge() {
        when(delegateGraph.adjacents(ID1)).thenReturn(Collections.singletonList(EDGE2));
        when(delegateGraph.edge(ID1)).thenReturn(EDGE1);
        when(delegateGraph.edge(ID2)).thenReturn(EDGE2);
        sut.addProhibitedPath(Arrays.asList(ID0, ID1, ID2));
        checkPathExists(new ShadowEdgeIdentifier<>(ID1, 0), new ShadowEdgeIdentifier<>(ID2));
    }

    /**
     * Check transformation:
     *
     * e_0(-1, 1) -> e_1(-1, 1) -> e_2(-1, 1) -> e_3(-1, 1)
     * with path prohibition e_0,e_1,e_2
     * should become
     * e_0(-1, 1) -> e_1(-1, 1) -> e_penalty(0, inf) -> e_2(0, 1)
     *
     *               e_1(0, 1) -> e_2(1, 1) -> e_3(-1, 1) <===
     */
    @Test
    public void givenPathProhibitionStartingFromIntermediateShadowEdgeShouldBePossibleBeyondLastEdge() {
        when(delegateGraph.adjacents(ID1)).thenReturn(Collections.singletonList(EDGE2));
        when(delegateGraph.adjacents(ID2)).thenReturn(Collections.singletonList(EDGE3));
        when(delegateGraph.edge(ID1)).thenReturn(EDGE1);
        when(delegateGraph.edge(ID2)).thenReturn(EDGE2);
        when(delegateGraph.edge(ID3)).thenReturn(EDGE3);
        sut.addProhibitedPath(Arrays.asList(ID0, ID1, ID2));
        checkPathExists(new ShadowEdgeIdentifier<>(ID1, 0), new ShadowEdgeIdentifier<>(ID2), new ShadowEdgeIdentifier<>(ID3));
    }

    /**
     * Check transformation:
     *
     * e_0(-1, 1) -> e_1(-1, 1) -> e_2(-1, 1) -> e_3(-1, 1)
     * with path prohibition e_0,e_1,e_2
     * should become
     * e_0(-1, 1) -> e_1(-1, 1) -> e_penalty(0, inf) -> e_2(0, 1) -> e_3(-1, 1) <===
     *
     *               e_1(0, 1) -> e_2(1, 1) -> e_3(-1, 1)
     */
    @Test
    public void givenPathProhibitionStartingFromTerminalShadowEdgeTraversalShouldBePossibleBeyondLastEdge() {
        when(delegateGraph.adjacents(ID2)).thenReturn(Collections.singletonList(EDGE3));
        when(delegateGraph.edge(ID1)).thenReturn(EDGE1);
        when(delegateGraph.edge(ID2)).thenReturn(EDGE2);
        when(delegateGraph.edge(ID3)).thenReturn(EDGE3);
        sut.addProhibitedPath(Arrays.asList(ID0, ID1, ID2));
        checkPathExists(new ShadowEdgeIdentifier<>(ID2, 0), new ShadowEdgeIdentifier<>(ID3));
    }

    /**
     * Check transformation:
     *
     * e_0(-1, 1) -> e_1(-1, 1) -> e_2(-1, 1)
     * e_3(-1, 1) -> e_1(-1, 1)
     *
     * with path prohibition e_0,e_1
     * should become
     * e_0(-1, 1) -> e_penalty(0, inf) -> e_1(0, 1) -> e_2(-1, 1)
     *               e_3(-1, 1) -> e_1(-1, 1) -> e_2(-1, 1) <===
     */
    @Test
    public void givenTurnProhibitionJoiningPathAfterFirstTraversalShouldBePossibleBeyondLastEdge() {
        when(delegateGraph.adjacents(ID1)).thenReturn(Collections.singletonList(EDGE2));
        when(delegateGraph.adjacents(ID3)).thenReturn(Collections.singletonList(EDGE1));
        when(delegateGraph.edge(ID1)).thenReturn(EDGE1);
        when(delegateGraph.edge(ID2)).thenReturn(EDGE2);
        sut.addProhibitedPath(Arrays.asList(ID0, ID1));
        checkPathExists(new ShadowEdgeIdentifier<>(ID3), new ShadowEdgeIdentifier<>(ID1), new ShadowEdgeIdentifier<>(ID2));
    }

    /**
     * Check transformation:
     *
     * e_0(-1, 1) -> e_1(-1, 1) -> e_2(-1, 1) -> e3_(-1, 1)
     * e_4(-1, 1) -> e_1(-1, 1)
     *
     * with path prohibition e_0,e_1,e_2
     * should become
     * e_0(-1, 1) -> e_1(0, 1) -> e_penalty(0, inf) -> e_2(0, 1) -> e_3(-1, 1)
     *               e_4(-1, 1) -> e_1(0, 1) -> e_2(0, 1) -> e_3(-1, 1) <===
     */
    @Test
    public void givenPathProhibitionJoiningPathAfterFirstTraversalShouldBePossibleBeyondLastEdge() {
        when(delegateGraph.adjacents(ID1)).thenReturn(Collections.singletonList(EDGE2));
        when(delegateGraph.adjacents(ID2)).thenReturn(Collections.singletonList(EDGE3));
        when(delegateGraph.adjacents(ID4)).thenReturn(Collections.singletonList(EDGE1));
        when(delegateGraph.edge(ID1)).thenReturn(EDGE1);
        when(delegateGraph.edge(ID2)).thenReturn(EDGE2);
        when(delegateGraph.edge(ID3)).thenReturn(EDGE3);
        sut.addProhibitedPath(Arrays.asList(ID0, ID1, ID2));
        checkPathExists(new ShadowEdgeIdentifier<>(ID4), new ShadowEdgeIdentifier<>(ID1, 0), new ShadowEdgeIdentifier<>(ID2, 0), new ShadowEdgeIdentifier<>(ID3));
    }

    private List<ShadowEdgeIdentifier<SimpleEdgeIdentifier>> checkPathExists(final ShadowEdgeIdentifier<SimpleEdgeIdentifier> startEdgeId,
                                 final ShadowEdgeIdentifier<SimpleEdgeIdentifier>... edgeIds) {
        final List<ShadowEdgeIdentifier<SimpleEdgeIdentifier>> shadowEdgeIdentifiers = new ArrayList<>();
        var currentEdgeId = startEdgeId;
        shadowEdgeIdentifiers.add(currentEdgeId);
        for (var edgeId : edgeIds) {
            final var nextEdgeOptional = sut.adjacents(currentEdgeId)
                    .stream()
                    .filter(e -> edgeId.delegateId().equals(e.id().delegateId()))
                    .min(Comparator.comparingInt(e -> abs(edgeId.copy() - e.id().copy())));
            assertTrue(nextEdgeOptional.isPresent());
            final var nextEdge = nextEdgeOptional.get();
            assertEqualsDouble(DEFAULT_WEIGHT, nextEdge.weight());
            currentEdgeId = nextEdge.id();
            shadowEdgeIdentifiers.add(currentEdgeId);
        }
        return shadowEdgeIdentifiers;
    }

    @SuppressWarnings("SameParameterValue")
    private void checkEdgeIsReachableViaPenalty(final ShadowEdgeIdentifier<SimpleEdgeIdentifier> idBeforePenalty,
                                                final int numberOfNonPenaltyAdjacents,
                                                final SimpleEdgeIdentifier idAfterPenalty) {
        var adjacents = sut.adjacents(idBeforePenalty);
        assertEquals(numberOfNonPenaltyAdjacents + 1, adjacents.size());
        final var optionalPenaltyEdge = adjacents.stream().filter(e -> PENALTY_WEIGHT == e.weight()).findAny();
        assertTrue(optionalPenaltyEdge.isPresent());
        final var penaltyEdge = optionalPenaltyEdge.get();
        var optionalEdgeAfterPenalty = sut.adjacents(penaltyEdge.id())
                .stream()
                .filter(e -> idAfterPenalty.equals(e.id().delegateId()))
                .findAny();
        assertTrue(optionalEdgeAfterPenalty.isPresent());
        assertEqualsDouble(DEFAULT_WEIGHT, optionalEdgeAfterPenalty.get().weight());
    }

}