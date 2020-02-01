package de.bsailer.routing;

import static de.bsailer.test.ExtendedAssert.assertEqualsDouble;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DijkstraTest {

	@Mock
	private Graph graph;

	@Mock
	private Edge startEdge;

	@Mock
	private Edge veryShortEdge;

	@Mock
	private Edge shortEdge;

	@Mock
	private Edge mediumEdge;

	@Mock
	private Edge longEdge;

	@Mock
	private Edge veryLongEdge;

	@Mock
	private Edge targetEdge;

	private Dijkstra sut;

	/**
	 * given situation:
	 *
	 * <pre>
	 * start -> short (1.0) -> target
	 *       -> long (2.0)  ->
	 * </pre>
	 *
	 * short should be chosen.
	 */
	@Test
	public void givenTwoEdgesShorterChoosen() {
		when(graph.adjacents(startEdge)).thenReturn(List.of(shortEdge, longEdge));
		when(graph.adjacents(shortEdge)).thenReturn(List.of(targetEdge));
		when(graph.adjacents(longEdge)).thenReturn(List.of(targetEdge));
		when(shortEdge.weight()).thenReturn(1.0D);
		when(longEdge.weight()).thenReturn(2.0D);
		sut = new Dijkstra(graph);
		assertEquals(new Route(List.of(startEdge, shortEdge, targetEdge)), sut.pathFromTo(startEdge, targetEdge).get());
	}

	/**
	 * given situation:
	 *
	 * <pre>
	 * start -> short (1.0) -> target
	 *       -> long (2.0)  ->
	 * </pre>
	 *
	 * short value should be returned.
	 */
	@Test
	public void givenTwoEdgesShorterCostShouldBeGiven() {
		when(graph.adjacents(startEdge)).thenReturn(List.of(shortEdge, longEdge));
		when(graph.adjacents(shortEdge)).thenReturn(List.of(targetEdge));
		when(graph.adjacents(longEdge)).thenReturn(List.of(targetEdge));
		when(shortEdge.weight()).thenReturn(1.0D);
		when(longEdge.weight()).thenReturn(2.0D);
		sut = new Dijkstra(graph);
		assertEqualsDouble(1.0D, sut.costFromTo(startEdge, targetEdge));
	}

	/**
	 * given situation:
	 *
	 * <pre>
	 * start -> short (1.0) -> target -> medium
	 *       -> long (2.0)  ->
	 * </pre>
	 *
	 * medium should not at all be evaluated.
	 */
	@Test
	public void givenTargetEdgeReachedButQueueNotEmpty() {
		when(graph.adjacents(startEdge)).thenReturn(List.of(shortEdge, longEdge));
		when(graph.adjacents(shortEdge)).thenReturn(List.of(targetEdge));
		when(graph.adjacents(longEdge)).thenReturn(List.of(targetEdge));
		when(graph.adjacents(targetEdge)).thenReturn(List.of(mediumEdge));
		when(shortEdge.weight()).thenReturn(1.0D);
		when(longEdge.weight()).thenReturn(2.0D);
		sut = new Dijkstra(graph);
		assertEquals(new Route(List.of(startEdge, shortEdge, targetEdge)), sut.pathFromTo(startEdge, targetEdge).get());
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
		when(graph.adjacents(startEdge)).thenReturn(List.of(shortEdge, longEdge));
		when(graph.adjacents(longEdge)).thenReturn(List.of(veryShortEdge));
		when(graph.adjacents(shortEdge)).thenReturn(List.of(veryLongEdge));
		when(graph.adjacents(veryShortEdge)).thenReturn(List.of(targetEdge));
		when(graph.adjacents(veryLongEdge)).thenReturn(List.of(targetEdge));
		when(veryShortEdge.weight()).thenReturn(2.0D);
		when(shortEdge.weight()).thenReturn(3.0D);
		when(longEdge.weight()).thenReturn(4.0D);
		when(veryLongEdge.weight()).thenReturn(6.0D);
		sut = new Dijkstra(graph);
		assertEquals(new Route(List.of(startEdge, longEdge, veryShortEdge, targetEdge)),
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
		when(graph.adjacents(startEdge)).thenReturn(List.of(shortEdge, veryLongEdge));
		when(graph.adjacents(shortEdge)).thenReturn(List.of(veryShortEdge));
		when(graph.adjacents(mediumEdge)).thenReturn(List.of(veryShortEdge));
		when(graph.adjacents(veryShortEdge)).thenReturn(List.of(mediumEdge));
		when(graph.adjacents(veryLongEdge)).thenReturn(List.of(mediumEdge));
		when(graph.adjacents(mediumEdge)).thenReturn(List.of(targetEdge));
		when(veryShortEdge.weight()).thenReturn(2.0D);
		when(shortEdge.weight()).thenReturn(3.0D);
		when(mediumEdge.weight()).thenReturn(4.0D);
		when(veryLongEdge.weight()).thenReturn(6.0D);
		sut = new Dijkstra(graph);
		assertEquals(new Route(List.of(startEdge, shortEdge, veryShortEdge, mediumEdge, targetEdge)),
				sut.pathFromTo(startEdge, targetEdge).get());
	}

	@Test
	public void givenIncompletePathsToTarget() {
		when(graph.adjacents(startEdge)).thenReturn(List.of(shortEdge, veryLongEdge));
		sut = new Dijkstra(graph);
		assertFalse(sut.pathFromTo(startEdge, targetEdge).isPresent());
	}

	@Test
	public void givenIncompletePathsToTargetShouldGivePositiveInfinityWeight() {
		when(graph.adjacents(startEdge)).thenReturn(List.of(shortEdge, veryLongEdge));
		sut = new Dijkstra(graph);
		assertEqualsDouble(Double.POSITIVE_INFINITY, sut.costFromTo(startEdge, targetEdge));
	}

}
