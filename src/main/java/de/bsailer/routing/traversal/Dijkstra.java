package de.bsailer.routing.traversal;

import de.bsailer.routing.model.Edge;
import de.bsailer.routing.model.EdgeIdentifier;
import de.bsailer.routing.model.Graph;
import de.bsailer.routing.model.Route;

import java.util.*;
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
 *
 * @param <E> concrete type of {@code Edge}
 * @param <I> concrete type of {@code EdgeIdentifier}
 */
public class Dijkstra<E extends Edge<I>, I extends EdgeIdentifier<I>> {

	private final DijkstraEdge<E> INFINITY_EDGE = new DijkstraEdge<>(Double.POSITIVE_INFINITY) {
		@Override
		E edge() {
			throw new UnsupportedOperationException("the infinity DijkstraEdge has no underlying graph edge");
		}
	};
	private final Graph<E> graph;
	private final PriorityQueue<DijkstraEdge<E>> queue = new PriorityQueue<>();
	private final Map<I, DijkstraEdge<E>> visited = new HashMap<>();
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
	public final Map<I, Optional<Route<E>>> routes(final E... targets) {
		final Map<I, Optional<Route<E>>> result = new HashMap<>();
		for (final E target : targets) {
			result.put(target.id(), createRoute(target));
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
	public final Map<I, Double> costs(final E... targets) {
		final Map<I, Double> result = new HashMap<>();
		for (final E target : targets) {
			result.put(target.id(), visited.getOrDefault(target.id(), INFINITY_EDGE).reachCost);
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
		return routes(target).get(target.id());
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
		return costs(target).get(target.id());
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

	private void enqueue(final DijkstraEdge<E> dijkstraEdge) {
		queue.offer(dijkstraEdge);
		visited.put(dijkstraEdge.edge().id(), dijkstraEdge);
	}

	private void relax(final DijkstraEdge<E> dijkstraEdge) {
		final var adjacentDijkstraEdges = dijkstraAdjacents(dijkstraEdge);
		for (final var adjacent : adjacentDijkstraEdges) {
			if (notYetLazyInitialized(adjacent)) {
				enqueue(adjacent);
			} else {
				update(adjacent);
			}
		}
	}

	private boolean notYetLazyInitialized(final DijkstraEdge<E> dijkstraEdge) {
		return !isCreated(dijkstraEdge.edge());
	}

	private boolean isCreated(final E edge) {
		return visited.containsKey(edge.id());
	}

	private List<E> backtrack(final E targetEdge) {
		final List<E> result = new ArrayList<>();
		var currentEdge = targetEdge;
		do {
			result.add(currentEdge);
			final var dijkstraEdge = visited.get(currentEdge.id());
			currentEdge = dijkstraEdge.predecessor;
		} while (!(currentEdge == null));
		Collections.reverse(result);
		return result;
	}

	private DijkstraEdge<E> poll() {
		return queue.poll();
	}

	private List<DijkstraEdge<E>> dijkstraAdjacents(final DijkstraEdge<E> dijkstraEdge) {
		return graph.adjacents(dijkstraEdge.edge).stream().map((e) -> new DijkstraEdge<>(e, dijkstraEdge))
				.collect(Collectors.toList());
	}

	private void update(final DijkstraEdge<E> adjacent) {
		final DijkstraEdge<E> original = visited.get(adjacent.edge().id());
		if (original.reachCost > adjacent.reachCost) {
			remove(original);
			enqueue(adjacent);
		}
	}

	private void remove(final DijkstraEdge<E> dijkstraEdge) {
		queue.remove(dijkstraEdge);
		visited.remove(dijkstraEdge.edge().id());
	}

	private boolean queueIsEmpty() {
		return queue.isEmpty();
	}

	/**
	 * This class encapsulates an {@code Edge} for Dijkstra routing.  Note that for routing purposes
	 * @param <E>
	 */
	private static class DijkstraEdge<E extends Edge<?>> implements Comparable<DijkstraEdge<E>> {

		private final E edge;
		private final E predecessor;
		private final double reachCost;

		private DijkstraEdge(final E edge) {
			this.edge = edge;
			this.predecessor = null;
			this.reachCost = 0.0D;
		}

		private DijkstraEdge(final E edge, final DijkstraEdge<E> predecessor) {
			this.edge = edge;
			this.predecessor = predecessor.edge;
			this.reachCost = predecessor.reachCost + predecessor.edge.weight();
		}

		private DijkstraEdge(final double cost) {
			this.edge = null;
			this.predecessor = null;
			this.reachCost = cost;
		}

		E edge() {
			return edge;
		}

		@Override
		public int compareTo(final DijkstraEdge<E> o) {
			return Double.compare(this.reachCost, o.reachCost);
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + " " + Arrays.asList(edge, predecessor, reachCost);
		}
	}

	private static class DefaultDijkstraAborter<E extends Edge<?>> implements DijkstraAborter<E> {

		@Override
		public boolean abort(final E target) {
			return false;
		}

	}

}
