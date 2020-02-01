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
 * This class implements a Dijkstra algorithm for finding shortest paths.
 *
 * The workflow consists of the following steps:
 * <ol>
 * <li>construct with graph</li>
 * <li>(optionally) define non-default abort criterium</li>
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
public class Dijkstra {

	private static final DijkstraEdge INFINITY_EDGE = new DijkstraEdge(Double.POSITIVE_INFINITY);
	private final Graph graph;
	private final PriorityQueue<DijkstraEdge> queue = new PriorityQueue<>();
	private final Map<Edge, DijkstraEdge> visited = new IdentityHashMap<>();
	private DijkstraAborter aborter = new DefaultDijkstraAborter();

	public Dijkstra(final Graph graph) {
		this.graph = graph;
	}

	/**
	 * Allows to supply a new {@code DijkstraAborter}.
	 *
	 * @param aborter new {@DijkstraAborter}, to set.
	 */
	public void setAborter(final DijkstraAborter aborter) {
		this.aborter = aborter;
	}

	/**
	 * This method actually runs the Dijkstra.
	 *
	 * @param starts varargs of start {@code Edge}s.
	 */
	public void run(final Edge... starts) {
		setStartEdges(starts);
		Edge currentEdge = null;
		do {
			final DijkstraEdge dijkstraEdge = poll();
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
	public Map<Edge, Optional<Route>> routes(final Edge... targets) {
		final Map<Edge, Optional<Route>> result = new HashMap<>();
		for (final Edge target : targets) {
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
	public Map<Edge, Double> costs(final Edge... targets) {
		final Map<Edge, Double> result = new HashMap<>();
		for (final Edge target : targets) {
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
	public Optional<Route> pathFromTo(final Edge start, final Edge target) {
		setAborter(new TargetDijkstraAborter(target));
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
	public Double costFromTo(final Edge start, final Edge target) {
		setAborter(new TargetDijkstraAborter(target));
		run(start);
		return costs(target).get(target);
	}

	private Optional<Route> createRoute(final Edge target) {
		if (isCreated(target)) {
			return Optional.of(new Route(backtrack(target)));
		} else {
			return Optional.empty();
		}
	}

	private void setStartEdges(final Edge... start) {
		for (final Edge edge : start) {
			enqueue(new DijkstraEdge(edge));
		}
	}

	private void enqueue(final DijkstraEdge dijkstraEdge) {
		queue.offer(dijkstraEdge);
		visited.put(dijkstraEdge.edge, dijkstraEdge);
	}

	private void relax(final DijkstraEdge dijkstraEdge) {
		final var adjacentDijkstraEdges = dijkstraAdjacents(dijkstraEdge);
		for (final var adjacent : adjacentDijkstraEdges) {
			if (notYetLazyInitialized(adjacent)) {
				enqueue(adjacent);
			} else {
				update(adjacent);
			}
		}
	}

	private boolean notYetLazyInitialized(final DijkstraEdge dijkstraEdge) {
		return !isCreated(dijkstraEdge.edge);
	}

	private boolean isCreated(final Edge edge) {
		return visited.containsKey(edge);
	}

	private List<Edge> backtrack(final Edge targetEdge) {
		final List<Edge> result = new ArrayList<>();
		var currentEdge = targetEdge;
		do {
			result.add(currentEdge);
			final var dijkstraEdge = visited.get(currentEdge);
			currentEdge = dijkstraEdge.predecessor;
		} while (!(currentEdge == null));
		Collections.reverse(result);
		return result;
	}

	private DijkstraEdge poll() {
		return queue.poll();
	}

	private List<DijkstraEdge> dijkstraAdjacents(final DijkstraEdge dijkstraEdge) {
		return graph.adjacents(dijkstraEdge.edge).stream().map((e) -> new DijkstraEdge(e, dijkstraEdge))
				.collect(Collectors.toList());
	}

	private void update(final DijkstraEdge adjacent) {
		final DijkstraEdge original = visited.get(adjacent.edge);
		if (original.reachCost > adjacent.reachCost) {
			remove(original);
			enqueue(adjacent);
		}
	}

	private void remove(final DijkstraEdge dijkstraEdge) {
		queue.remove(dijkstraEdge);
		visited.remove(dijkstraEdge.edge);
	}

	private boolean queueIsEmpty() {
		return queue.isEmpty();
	}

	private static class DijkstraEdge implements Comparable<DijkstraEdge> {

		private final Edge edge;
		private final Edge predecessor;
		private final double reachCost;

		private DijkstraEdge(final Edge edge) {
			this.edge = edge;
			this.predecessor = null;
			this.reachCost = 0.0D;
		}

		private DijkstraEdge(final Edge edge, final DijkstraEdge predecessor) {
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
		public int compareTo(final DijkstraEdge o) {
			return Double.compare(this.reachCost, o.reachCost);
		}

		@Override
		public boolean equals(final Object o) {
			if (o instanceof DijkstraEdge) {
				final DijkstraEdge other = (DijkstraEdge) o;
				return edge.equals(other.edge);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(edge);
		}

		@Override
		public String toString() {
			return "DijkstraEdge " + Arrays.asList(edge, predecessor, reachCost).toString();
		}
	}

	private static class DefaultDijkstraAborter implements DijkstraAborter {

		@Override
		public boolean abort(final Edge target) {
			return false;
		}

	}

}
