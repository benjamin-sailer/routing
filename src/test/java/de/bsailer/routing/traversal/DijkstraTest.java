package de.bsailer.routing.traversal;

import static de.bsailer.test.ExtendedAssert.assertEqualsDouble;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import de.bsailer.routing.model.Edge;
import de.bsailer.routing.model.Route;
import de.bsailer.routing.model.Graph;
import de.bsailer.routing.model.impl.SimpleEdge;
import de.bsailer.routing.model.impl.SimpleEdgeIdentifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@RunWith(MockitoJUnitRunner.class)
public class DijkstraTest {

	@Mock
	private Graph<Edge<SimpleEdgeIdentifier>, SimpleEdgeIdentifier> graph;

	private final Edge<SimpleEdgeIdentifier> startEdge = new SimpleEdge(new SimpleEdgeIdentifier(1));

	private final Edge<SimpleEdgeIdentifier> veryShortEdge = new SimpleEdge(new SimpleEdgeIdentifier(2)).setWeight(2.0D);

	private final Edge<SimpleEdgeIdentifier> shortEdge = new SimpleEdge(new SimpleEdgeIdentifier(3)).setWeight(3.0D);

	private final Edge<SimpleEdgeIdentifier> mediumEdge = new SimpleEdge(new SimpleEdgeIdentifier(4)).setWeight(4.0D);

	private final Edge<SimpleEdgeIdentifier> longEdge = new SimpleEdge(new SimpleEdgeIdentifier(5)).setWeight(4.0D);

	private final Edge<SimpleEdgeIdentifier> veryLongEdge = new SimpleEdge(new SimpleEdgeIdentifier(6)).setWeight(6.0D);

	private final Edge<SimpleEdgeIdentifier> targetEdge = new SimpleEdge(new SimpleEdgeIdentifier(7));

	private Dijkstra<Edge<SimpleEdgeIdentifier>, SimpleEdgeIdentifier> sut;

	/**
	 * given situation:
	 *
	 * <pre>
	 * start -> short (3.0) -> target
	 *       -> long (4.0)  ->
	 * </pre>
	 *
	 * short should be chosen.
	 */
	@Test
	public void givenTwoEdgesShorterChosenAsRoute() {
		when(graph.adjacents(startEdge.id())).thenReturn(List.of(shortEdge, longEdge));
		when(graph.adjacents(shortEdge.id())).thenReturn(List.of(targetEdge));
		when(graph.adjacents(longEdge.id())).thenReturn(List.of(targetEdge));
		sut = new Dijkstra<>(graph);
		assertEquals(new Route<>(List.of(startEdge, shortEdge, targetEdge)), sut.pathFromTo(startEdge, targetEdge).get());
	}

	/**
	 * given situation:
	 *
	 * <pre>
	 * start -> short (3.0) -> target
	 *       -> long (4.0)  ->
	 * </pre>
	 *
	 * short value should be returned.
	 */
	@Test
	public void givenTwoEdgesShorterCostShouldBeReturned() {
		when(graph.adjacents(startEdge.id())).thenReturn(List.of(shortEdge, longEdge));
		when(graph.adjacents(shortEdge.id())).thenReturn(List.of(targetEdge));
		when(graph.adjacents(longEdge.id())).thenReturn(List.of(targetEdge));
		sut = new Dijkstra<>(graph);
		assertEqualsDouble(3.0D, sut.costFromTo(startEdge, targetEdge));
	}

	/**
	 * given situation:
	 *
	 * <pre>
	 * start -> short (3.0) -> target -> medium
	 *       -> long (4.0)  ->
	 * </pre>
	 *
	 * medium should not at all be evaluated.
	 */
	@Test
	public void givenTargetEdgeReachedButQueueNotEmpty() {
		when(graph.adjacents(startEdge.id())).thenReturn(List.of(shortEdge, longEdge));
		when(graph.adjacents(shortEdge.id())).thenReturn(List.of(targetEdge));
		when(graph.adjacents(longEdge.id())).thenReturn(List.of(targetEdge));
		when(graph.adjacents(targetEdge.id())).thenReturn(List.of(mediumEdge));
		sut = new Dijkstra<>(graph);
		assertEquals(new Route<>(List.of(startEdge, shortEdge, targetEdge)), sut.pathFromTo(startEdge, targetEdge).get());
	}

	/**
	 * given situation:
	 *
	 * <pre>
	 * start -> short (3.0) -> verylong (6.0)  -> target
	 *       -> long (4.0)  -> veryshort (2.0) ->
	 * </pre>
	 *
	 * long, veryshort should be chosen.
	 */
	@Test
	public void givenTwoPathsShorterChoosen() {
		when(graph.adjacents(startEdge.id())).thenReturn(List.of(shortEdge, longEdge));
		when(graph.adjacents(longEdge.id())).thenReturn(List.of(veryShortEdge));
		when(graph.adjacents(shortEdge.id())).thenReturn(List.of(veryLongEdge));
		when(graph.adjacents(veryShortEdge.id())).thenReturn(List.of(targetEdge));
		when(graph.adjacents(veryLongEdge.id())).thenReturn(List.of(targetEdge));
		sut = new Dijkstra<>(graph);
		assertEquals(new Route<>(List.of(startEdge, longEdge, veryShortEdge, targetEdge)),
				sut.pathFromTo(startEdge, targetEdge).get());
	}

	/**
	 * given situation:
	 *
	 * <pre>
	 * start -> short (3.0)    -> veryshort (2.0) -> medium (4.0) -> target
	 *       -> verylong (6.0)                    ->
	 * </pre>
	 *
	 * short, veryshort should be chosen.
	 */
	@Test
	public void givenTwoPathsInitialSolutionReplaced() {
		when(graph.adjacents(startEdge.id())).thenReturn(List.of(shortEdge, veryLongEdge));
		when(graph.adjacents(shortEdge.id())).thenReturn(List.of(veryShortEdge));
		when(graph.adjacents(mediumEdge.id())).thenReturn(List.of(veryShortEdge));
		when(graph.adjacents(veryShortEdge.id())).thenReturn(List.of(mediumEdge));
		when(graph.adjacents(veryLongEdge.id())).thenReturn(List.of(mediumEdge));
		when(graph.adjacents(mediumEdge.id())).thenReturn(List.of(targetEdge));
		sut = new Dijkstra<>(graph);
		assertEquals(new Route<>(List.of(startEdge, shortEdge, veryShortEdge, mediumEdge, targetEdge)),
				sut.pathFromTo(startEdge, targetEdge).get());
	}

	@Test
	public void givenIncompletePathsToTarget() {
		when(graph.adjacents(startEdge.id())).thenReturn(List.of(shortEdge, veryLongEdge));
		sut = new Dijkstra<>(graph);
		assertFalse(sut.pathFromTo(startEdge, targetEdge).isPresent());
	}

	@Test
	public void givenIncompletePathsToTargetShouldGivePositiveInfinityWeight() {
		when(graph.adjacents(startEdge.id())).thenReturn(List.of(shortEdge, veryLongEdge));
		sut = new Dijkstra<>(graph);
		assertEqualsDouble(Double.POSITIVE_INFINITY, sut.costFromTo(startEdge, targetEdge));
	}

	@Test
	public void givenGraphIsTraversedCostsReturnsTheCostMap() {
		when(graph.adjacents(startEdge.id())).thenReturn(List.of(targetEdge));
		sut = new Dijkstra<>(graph);
		sut.run(startEdge);
		assertEquals(Collections.singletonMap(targetEdge.id(), 0.0D), sut.costs(targetEdge));
	}

	@Test
	public void givenGraphIsTraversedCostsReturnsCostFromEndOfStartEdgeToStartOfEndEdge() {
		when(graph.adjacents(startEdge.id())).thenReturn(List.of(veryShortEdge));
		when(graph.adjacents(veryShortEdge.id())).thenReturn(List.of(targetEdge));
		sut = new Dijkstra<>(graph);
		sut.run(startEdge);
		assertEquals(Collections.singletonMap(targetEdge.id(), veryShortEdge.weight()), sut.costs(targetEdge));
	}

}
