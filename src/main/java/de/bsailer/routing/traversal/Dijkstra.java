package de.bsailer.routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

/**
 * This class implements a Dijkstra algorithm for finding the shortest paths.
 *
 * The workflow consists of the following steps:
 * <ol>
 * <li>construct with graph</li>
 * <li>(optionally) define non-default abort criteria</li>
 * <li>run from array of start edges</li>
 * <li>retrieve routes or cost for array of target edges</li>
 * </ol>
 *
 * Convenience methods {@code pathFromTo} and {@code costFromTo} combine the
 * above workflow for exactly one start and one target (except the construction
 * of the object itself).
 *
 * @author bsailer
 */
public class Dijkstra<E extends Edge<I>, I extends EdgeIdentifier<I>> {

	private final DijkstraEdge<E> INFINITY_EDGE = new DijkstraEdge<>(Double.POSITIVE_INFINITY);
	private final Graph<E> graph;
	private final PriorityQueue<DijkstraEdge<E>> queue = new PriorityQueue<>();
	private final Map<E, DijkstraEdge<E>> visited = new IdentityHashMap<>();
	private DijkstraAborter<E> aborter = new DefaultDijkstraAborter<>();

	public Dijkstra(final Graph<E> graph) {
		this.graph = graph;
	}

	/**
	 * Allows to supply a new {@code DijkstraAborter}.
	 *
	 * @param aborter new {@code DijkstraAborter}, to set.
	 */
	public void setAborter(final DijkstraAborter<E> aborter) {
		this.aborter = aborter;
	}

	/**
	 * This method actually runs the Dijkstra.
	 *
	 * @param starts varargs of start {@code Edge}s.
	 */
	@SafeVarargs
	public final void run(final E... starts) {
		setStartEdges(starts);
		E currentEdge;
		do {
			final DijkstraEdge<E> dijkstraEdge = poll();
			currentEdge = dijkstraEdge.edge;
			relax(dijkstraEdge);
		} while (!(queueIsEmpty() || aborter.abort(currentEdge)));
	}

	/**
	 * This method gives a mapping between given targets and
	 * ({@code Optional<Route>} (which is empty in case no path has been found).
	 *
	 * It is guaranteed to have an entry in the result map for each given target.
	 *
	 * @param targets varargs of target {@code Edge}s.
	 * @return {@code Map<Edge, Optional<Route>>}.
	 */
	@SafeVarargs
	public final Map<E, Optional<Route<E>>> routes(final E... targets) {
		final Map<E, Optional<Route<E>>> result = new HashMap<>();
		for (final E target : targets) {
			result.put(target, createRoute(target));
		}
		return result;
	}

	/**
	 * This method gives a mapping between given targets and reaching cost (which is
	 * {@code Double.POSITIVE_INFINITY} in case no path has been found).
	 *
	 * It is guaranteed to have an entry in the result map for each given target.
	 *
	 * @param targets varargs of target {@code Edge}s.
	 * @return {@code Map<Edge, Double>}.
	 */
	@SafeVarargs
	public final Map<E, Double> costs(final E... targets) {
		final Map<E, Double> result = new HashMap<>();
		for (final E target : targets) {
			result.put(target, visited.getOrDefault(target, INFINITY_EDGE).reachCost);
		}
		return result;
	}

	/**
	 * Convenience method to retrieve the {@code Route} from start to target.
	 *
	 * @param start  {@code Edge}
	 * @param target {@code Edge}
	 * @return {@code Optional<Route>}, which is empty in case no path has been
	 *         found.
	 */
	public Optional<Route<E>> pathFromTo(final E start, final E target) {
		setAborter(new TargetDijkstraAborter<>(target));
		run(start);
		return routes(target).get(target);
	}

	/**
	 * Convenience method to retrieve the cost from start to target.
	 *
	 * @param start  {@code Edge}
	 * @param target {@code Edge}
	 * @return cost, which is {@code Double.POSITIVE_INFINITY} in case no path has
	 *         been found.
	 */
	public Double costFromTo(final E start, final E target) {
		setAborter(new TargetDijkstraAborter<>(target));
		run(start);
		return costs(target).get(target);
	}

	private Optional<Route<E>> createRoute(final E target) {
		if (isCreated(target)) {
			return Optional.of(new Route<>(backtrack(target)));
		} else {
			return Optional.empty();
		}
	}

	@SafeVarargs
	private void setStartEdges(final E... start) {
		for (final E edge : start) {
			enqueue(new DijkstraEdge<>(edge));
		}
	}

	private void enqueue(final DijkstraEdge<E, I> dijkstraEdge) {
		queue.offer(dijkstraEdge);
		visited.put(dijkstraEdge.edge, dijkstraEdge);
	}

	private void relax(final DijkstraEdge<E, I> dijkstraEdge) {
		final var adjacentDijkstraEdges = dijkstraAdjacents(dijkstraEdge);
		for (final var adjacent : adjacentDijkstraEdges) {
			if (notYetLazyInitialized(adjacent)) {
				enqueue(adjacent);
			} else {
				update(adjacent);
			}
		}
	}

	private boolean notYetLazyInitialized(final DijkstraEdge<E, I> dijkstraEdge) {
		return !isCreated(dijkstraEdge.edge);
	}

	private boolean isCreated(final E edge) {
		return visited.containsKey(edge);
	}

	private List<E> backtrack(final E targetEdge) {
		final List<E> result = new ArrayList<>();
		var currentEdge = targetEdge;
		do {
			result.add(currentEdge);
			final var dijkstraEdge = visited.get(currentEdge);
			currentEdge = dijkstraEdge.predecessor;
		} while (!(currentEdge == null));
		Collections.reverse(result);
		return result;
	}

	private DijkstraEdge<E, I> poll() {
		return queue.poll();
	}

	private List<DijkstraEdge<E, I>> dijkstraAdjacents(final DijkstraEdge<E, I> dijkstraEdge) {
		return graph.adjacents(dijkstraEdge.edge).stream().map((e) -> new DijkstraEdge<>(e, dijkstraEdge))
				.collect(Collectors.toList());
	}

	private void update(final DijkstraEdge<E, I> adjacent) {
		final DijkstraEdge<E, I> original = visited.get(adjacent.edge);
		if (original.reachCost > adjacent.reachCost) {
			remove(original);
			enqueue(adjacent);
		}
	}

	private void remove(final DijkstraEdge<E, I> dijkstraEdge) {
		queue.remove(dijkstraEdge);
		visited.remove(dijkstraEdge.edge);
	}

	private boolean queueIsEmpty() {
		return queue.isEmpty();
	}

	private static class DijkstraEdge<E extends Edge<?>> implements Comparable<DijkstraEdge<E>> {

		private final E edge;
		private final E predecessor;
		private final double reachCost;

		private DijkstraEdge(final E edge) {
			this.edge = edge;
			this.predecessor = null;
			this.reachCost = 0.0D;
		}

		private DijkstraEdge(final E edge, final DijkstraEdge<E, I> predecessor) {
			this.edge = edge;
			this.predecessor = predecessor.edge;
			this.reachCost = predecessor.reachCost + predecessor.edge.weight();
		}

		private DijkstraEdge(final double cost) {
			this.edge = null;
			this.predecessor = null;
			this.reachCost = cost;
		}

		@Override
		public int compareTo(final DijkstraEdge<E, I> o) {
			return Double.compare(this.reachCost, o.reachCost);
		}

		@Override
		public boolean equals(final Object o) {
			if (o instanceof final DijkstraEdge other) {
				return edge.id().equals(other.edge.id());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(edge);
		}

		@Override
		public String toString() {
			return "DijkstraEdge " + Arrays.asList(edge, predecessor, reachCost);
		}
	}

	private static class DefaultDijkstraAborter<E extends Edge<?>> implements DijkstraAborter<E> {

		@Override
		public boolean abort(final E target) {
			return false;
		}

	}

}
