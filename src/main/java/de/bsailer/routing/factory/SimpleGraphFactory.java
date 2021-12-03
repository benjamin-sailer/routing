package de.bsailer.routing.factory;

import de.bsailer.routing.model.impl.SimpleEdge;
import de.bsailer.routing.model.impl.SimpleEdgeIdentifier;
import de.bsailer.routing.model.impl.SimpleGraph;
import de.bsailer.routing.model.Graph;

import java.util.stream.Stream;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

/**
 * This factory constructs a {@code Graph<SimpleEdge>} from a Stream of String representations.
 */
public class SimpleGraphFactory {

	public Graph<SimpleEdge> createSimpleGraph(final Stream<String> edges) {
		final SimpleGraph<SimpleEdge, SimpleEdgeIdentifier> result = new SimpleGraph<>();
		edges.forEach(e -> fromDescriptor(result, e));
		return result;
	}

	private void fromDescriptor(final SimpleGraph<SimpleEdge, SimpleEdgeIdentifier> graph, final String edgeDescriptor) {
		final String[] edgeProperties = edgeDescriptor.split(";");
		final var id = new SimpleEdgeIdentifier(parseInt(edgeProperties[0]));
		final var weight = parseDouble(edgeProperties[1]);
		graph.addEdge(new SimpleEdge(id).setWeight(weight));
		Stream.of(edgeProperties[2].split(","))
				.map(Integer::parseInt)
				.map(SimpleEdgeIdentifier::new)
				.forEach(a -> graph.connectEdges(id, a));
	}

}
