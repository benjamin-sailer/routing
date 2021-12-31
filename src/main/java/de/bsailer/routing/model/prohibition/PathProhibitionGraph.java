package de.bsailer.routing.model.prohibition;

import de.bsailer.routing.factory.EdgeFactory;
import de.bsailer.routing.factory.EdgeIdentifierSupplier;
import de.bsailer.routing.model.Edge;
import de.bsailer.routing.model.EdgeIdentifier;
import de.bsailer.routing.model.Graph;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@code Graph} implementation that transforms the delegate {@code Graph<E, I>} in combination with given path prohibitions
 * to a new {@code Graph<ShadowEdge<E, I>, ShadowEdgeIdentifier<I>>}.  The new transformed graph implicitly respects the
 * path prohibitions (hopefully according to Schmid_Diss.pdf given in the docs).
 *
 * @param <E> concrete type of delegate graph {@code Edge}s.
 * @param <I> concrete type of delegate graph {@code EdgeIdentifier}s.
 */
public class PathProhibitionGraph<E extends Edge<I>, I extends EdgeIdentifier<I>>
        implements Graph<ShadowEdge<E, I>, ShadowEdgeIdentifier<I>> {

    private final Prohibitions<I> prohibitions = new Prohibitions<>();

    private final GraphOverlay<E, I> graphOverlay;

    private final Graph<E, I> delegateGraph;

    public PathProhibitionGraph(final Graph<E, I> delegateGraph,
                                final EdgeFactory<E, I> edgeFactory,
                                final EdgeIdentifierSupplier<I> idSupplier,
                                final double prohibitionViolationPenalty) {
        this.delegateGraph = delegateGraph;
        this.graphOverlay = new GraphOverlay<>(delegateGraph, edgeFactory, idSupplier, prohibitionViolationPenalty);
    }

    public void addProhibitedPath(final List<I> prohibitedPath) {
        prohibitions.addProhibitedPath(prohibitedPath);
    }

    @Override
    public List<ShadowEdge<E, I>> adjacents(final ShadowEdgeIdentifier<I> id) {
        assureLocalGraphTransformation(id);
        return doListAdjacents(id);
    }

    @Override
    public ShadowEdge<E, I> edge(final ShadowEdgeIdentifier<I> id) {
        return new ShadowEdge<>(delegateGraph.edge(id.delegateId()), id.copy());
    }

    private void assureLocalGraphTransformation(final ShadowEdgeIdentifier<I> id) {
        final var idsToCheck = Stream.concat(Stream.of(id.delegateId()),
                delegateGraph.adjacents(id.delegateId()).stream().map(Edge::id)).collect(Collectors.toSet());
        idsToCheck.forEach(i -> prohibitions.touchingEquivalentClasses(i).forEach(this::transformProhibition));
    }

    private void transformProhibition(final ProhibitionEquivalentClass<I> prohibitionEquivalentClass) {
        graphOverlay.addProhibitionEquivalentClass(prohibitionEquivalentClass);
        prohibitions.removeProhibitionEquivalentClass(prohibitionEquivalentClass.prohibitionClassId());
    }

    private List<ShadowEdge<E, I>> doListAdjacents(final ShadowEdgeIdentifier<I> id) {
        final var delegateAdjacents = delegateGraph.adjacents(id.delegateId());
        return graphOverlay.listAdjacents(id, delegateAdjacents);
    }

    record ProhibitedPath<I extends EdgeIdentifier<I>>(List<I> edgeIds) { }

    record ProhibitionEquivalentClass<I extends EdgeIdentifier<I>>(I prohibitionClassId, List<ProhibitedPath<I>> prohibitedPaths) { }

    static class Prohibitions<I extends EdgeIdentifier<I>> {

        private final Map<I, ProhibitionEquivalentClass<I>> prohibitionEquivalentClasses = new HashMap<>();

        private final Map<I, Set<I>> edgeIdToProhibitionEquivalentClassKey = new HashMap<>();

        void addProhibitedPath(final List<I> prohibitedPath) {
            final var initialEdgeId = prohibitedPath.get(0);
            final var prohibitionEquivalentClass =
                    getOrNewContainer(this.prohibitionEquivalentClasses, initialEdgeId, () -> new ProhibitionEquivalentClass<>(initialEdgeId, new ArrayList<>()));
            prohibitionEquivalentClass.prohibitedPaths().add(new ProhibitedPath<>(prohibitedPath));
            prohibitedPath.forEach(id -> getOrNewContainer(edgeIdToProhibitionEquivalentClassKey, id, HashSet::new).add(initialEdgeId));
        }

        List<ProhibitionEquivalentClass<I>> touchingEquivalentClasses(final I delegateId) {
            return edgeIdToProhibitionEquivalentClassKey.getOrDefault(delegateId, Collections.emptySet())
                    .stream()
                    .map(prohibitionEquivalentClasses::get)
                    .filter(Objects::nonNull)
                    .toList();
        }

        void removeProhibitionEquivalentClass(final I initialEdgeId) {
            prohibitionEquivalentClasses.remove(initialEdgeId);
            edgeIdToProhibitionEquivalentClassKey.values().forEach(s -> s.remove(initialEdgeId));
        }

        private <T> T getOrNewContainer(final Map<I, T> prohibitionMap,
                                        final I id,
                                        final Supplier<T> defaultValueSupplier) {
            prohibitionMap.computeIfAbsent(id, d -> defaultValueSupplier.get());
            return prohibitionMap.get(id);
        }

    }

    static class GraphOverlay<E extends Edge<I>, I extends EdgeIdentifier<I>> {

        private final Graph<E, I> delegateGraph;

        private final EdgeFactory<E, I> edgeFactory;

        private final EdgeIdentifierSupplier<I> idSupplier;

        private final double prohibitionViolationPenalty;

        private final Map<I, List<ShadowEdge<E, I>>> overlayEdges = new HashMap<>();

        private final Map<ShadowEdgeIdentifier<I>, Set<ShadowEdgeIdentifier<I>>> overlayAdjacents = new HashMap<>();

        private final Map<ShadowEdgeIdentifier<I>, Set<ShadowEdgeIdentifier<I>>> removedAdjacents = new HashMap<>();

        private final Map<ShadowEdgeIdentifier<I>, Set<I>> forbiddenPredecessors = new HashMap<>();

        GraphOverlay(final Graph<E, I> delegateGraph,
                     final EdgeFactory<E, I> edgeFactory,
                     final EdgeIdentifierSupplier<I> idSupplier,
                     final double prohibitionViolationPenalty) {
            this.delegateGraph = delegateGraph;
            this.edgeFactory = edgeFactory;
            this.idSupplier = idSupplier;
            this.prohibitionViolationPenalty = prohibitionViolationPenalty;
        }

        void addProhibitionEquivalentClass(final ProhibitionEquivalentClass<I> prohibitionEquivalentClass) {
            final Map<I, ShadowEdge<E, I>> virtualIntermediateEdges = new HashMap<>();
            for (final var prohibition : prohibitionEquivalentClass.prohibitedPaths()) {
                final var edgeIds = prohibition.edgeIds();
                createVirtualIntermediateEdges(virtualIntermediateEdges, edgeIds);
                connectVirtualIntermediateEdges(virtualIntermediateEdges, edgeIds);
                connectLastVirtualIntermediateToTerminalProhibitionEdges(virtualIntermediateEdges, edgeIds);
                addPenaltyEdgesToProhibitedTerminalPath(edgeIds);
            }
        }

        private void createVirtualIntermediateEdges(final Map<I, ShadowEdge<E, I>> virtualIntermediateEdges,
                                                    final List<I> edgeIds) {
            // first edge replaced with point edge
            final var virtualPointEdge = newPointEdge();
            virtualIntermediateEdges.put(edgeIds.get(0), virtualPointEdge);
            // leave out first and last edge
            for (final var edgeId : edgeIds.subList(1, edgeIds.size() - 1)) {
                if (!virtualIntermediateEdges.containsKey(edgeId)) {
                    final ShadowEdge<E, I> virtualEdge = newShadowEdge(edgeId);
                    virtualIntermediateEdges.put(edgeId, virtualEdge);
                }
            }
        }

        private void connectVirtualIntermediateEdges(final Map<I, ShadowEdge<E, I>> virtualIntermediateEdges,
                                                     final List<I> edgeIds) {
            for (int i = 0; i < edgeIds.size() - 2; i++) {
                var fromShadowEdgeId = toLocalVirtual(virtualIntermediateEdges, edgeIds.get(i));
                var toShadowEdgeId = toLocalVirtual(virtualIntermediateEdges, edgeIds.get(i + 1));
                addVirtualAdjacent(fromShadowEdgeId, toShadowEdgeId);
                if (i == 1) {
                    addForbiddenPredecessor(edgeIds.get(0), fromShadowEdgeId);
                }
            }
        }

        private void connectLastVirtualIntermediateToTerminalProhibitionEdges(final Map<I, ShadowEdge<E, I>> virtualIntermediateEdges,
                                                                              final List<I> edgeIds) {
            final var lastIntermediateEdgeId = toLocalVirtual(virtualIntermediateEdges, edgeIds.get(edgeIds.size() - 2));
            final var lastEdgeId = edgeIds.get(edgeIds.size() - 1);
            connectShadowAdjacents(lastIntermediateEdgeId, lastEdgeId);
        }

        private void addPenaltyEdgesToProhibitedTerminalPath(final List<I> edgeIds) {
            final var lastIntermediateEdgeId = edgeIds.get(edgeIds.size() - 2);
            // TODO: probably this has to be done across all shadows of this edge from passes of different prohibition classes
            final var lastIntermediateVirtualEdgeId = new ShadowEdgeIdentifier<>(lastIntermediateEdgeId);
            final var lastEdgeId = edgeIds.get(edgeIds.size() - 1);
            final var penaltyEdge = newPenaltyEdge();
            final var penaltyEdgeId = penaltyEdge.id();
            final var terminalEdge = newShadowEdge(lastEdgeId);
            final var terminalEdgeId = terminalEdge.id();
            addVirtualAdjacent(lastIntermediateVirtualEdgeId, penaltyEdgeId);
            addVirtualAdjacent(penaltyEdgeId, terminalEdgeId);
            addForbiddenPredecessor(lastIntermediateEdgeId, terminalEdgeId);
            delegateGraph.adjacents(terminalEdgeId.delegateId())
                    .stream()
                    .map(Edge::id)
                    .forEach(adjacentId -> connectShadowAdjacents(terminalEdgeId, adjacentId));
            removeAdjacent(encapsulate(lastIntermediateEdgeId), encapsulate(lastEdgeId));
        }

        private ShadowEdgeIdentifier<I> toLocalVirtual(final Map<I, ShadowEdge<E, I>> virtualIntermediateEdges, final I edgeId) {
            return virtualIntermediateEdges.get(edgeId).id();
        }

        private void addVirtualAdjacent(final ShadowEdgeIdentifier<I> from, final ShadowEdgeIdentifier<I> to) {
            overlayAdjacents.computeIfAbsent(from, f -> new HashSet<>());
            overlayAdjacents.get(from).add(to);
        }

        private void removeAdjacent(final ShadowEdgeIdentifier<I> from, final ShadowEdgeIdentifier<I> to) {
            removedAdjacents.computeIfAbsent(from, f -> new HashSet<>());
            removedAdjacents.get(from).add(to);
        }

        private void addForbiddenPredecessor(final I fromEdgeId, final ShadowEdgeIdentifier<I> toShadowEdgeId) {
            forbiddenPredecessors.computeIfAbsent(toShadowEdgeId, id -> new HashSet<>());
            forbiddenPredecessors.get(toShadowEdgeId).add(fromEdgeId);
        }

        private void connectShadowAdjacents(final ShadowEdgeIdentifier<I> from, final I to) {
            shadows(to).forEach(shadowAdjacent -> addVirtualAdjacent(from, shadowAdjacent));
        }

        private Set<ShadowEdgeIdentifier<I>> shadows(final I edgeId) {
            return Stream.concat(Stream.of(encapsulate(delegateGraph.edge(edgeId))),
                            overlayEdges.getOrDefault(edgeId, Collections.emptyList())
                    .stream())
                    .map(ShadowEdge::id)
                    .collect(Collectors.toSet());
        }

        /*
         * Do not call without incorporating result.  This method will have side effects recording the number of copies.
         */
        private ShadowEdge<E, I> newShadowEdge(final I edgeId) {
            final E edge = delegateGraph.edge(edgeId);
            return newShadowEdge(edgeId, edge);
        }

        private ShadowEdge<E,I> newPointEdge() {
            final var pointEdge = edgeFactory.edgeWithWeight(idSupplier, 0.0D);
            return newShadowEdge(pointEdge.id(), pointEdge);
        }

        private ShadowEdge<E, I> newPenaltyEdge() {
            final var penaltyEdge = edgeFactory.edgeWithWeight(idSupplier, prohibitionViolationPenalty);
            return newShadowEdge(penaltyEdge.id(), penaltyEdge);
        }

        private ShadowEdge<E, I> newShadowEdge(final I edgeId, final E edge) {
            final var shadowEdges = overlayEdges.computeIfAbsent(edgeId, e -> new ArrayList<>());
            final var result = new ShadowEdge<>(edge, shadowEdges.size());
            shadowEdges.add(result);
            return result;
        }

        private List<ShadowEdge<E, I>> listAdjacents(final ShadowEdgeIdentifier<I> id, final List<E> delegateAdjacents) {
            final var rootAdjacents = delegatesForRootEdgeId(id, delegateAdjacents)
                    .stream()
                    .map(Edge::id)
                    .flatMap(i -> filterPredecessors(id, shadows(i)).stream())
                    .map(this::toEdge);
            final var overlayAdjacents = overlayAdjacents(id);
            final var result = Stream.concat(rootAdjacents, overlayAdjacents.stream());
            return result
                    .filter(a -> !isRemovedAdjacent(id, a.id()))
                    .toList();
        }

        private Set<ShadowEdgeIdentifier<I>> filterPredecessors(final ShadowEdgeIdentifier<I> id,
                                                                final Set<ShadowEdgeIdentifier<I>> adjacents) {
            if (id.isRootCopy()) {
                return adjacents.stream()
                        .filter(i -> !forbiddenPredecessors.getOrDefault(i, Collections.emptySet()).contains(id.delegateId()))
                        .collect(Collectors.toSet());
            }
            return adjacents;
        }

        private List<E> delegatesForRootEdgeId(final ShadowEdgeIdentifier<I> id, final List<E> delegateAdjacents) {
            if (id.isRootCopy()) {
                return delegateAdjacents;
            }
            return Collections.emptyList();
        }

        private boolean isRemovedAdjacent(final ShadowEdgeIdentifier<I> edge, final ShadowEdgeIdentifier<I> adjacent) {
            return removedAdjacents.getOrDefault(edge, Collections.emptySet()).contains(adjacent);
        }

        private List<ShadowEdge<E, I>> overlayAdjacents(final ShadowEdgeIdentifier<I> id) {
            final Set<ShadowEdgeIdentifier<I>> result = overlayAdjacents.getOrDefault(id, Collections.emptySet());
            return result.stream().map(this::toEdge).toList();
        }

        private ShadowEdge<E, I> toEdge(final ShadowEdgeIdentifier<I> shadowEdgeId) {
            if (shadowEdgeId.isRootCopy()) {
                return encapsulate(delegateGraph.edge(shadowEdgeId.delegateId()));
            }
            return overlayEdges.get(shadowEdgeId.delegateId()).get(shadowEdgeId.copy());
        }

        private ShadowEdgeIdentifier<I> encapsulate(final I edgeId) {
            return new ShadowEdgeIdentifier<>(edgeId);
        }

        private ShadowEdge<E, I> encapsulate(final E edge) {
            return new ShadowEdge<>(edge);
        }

    }

}
