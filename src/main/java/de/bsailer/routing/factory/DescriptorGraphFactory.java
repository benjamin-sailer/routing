package de.bsailer.routing.factory;

import de.bsailer.routing.model.ConstructableGraph;
import de.bsailer.routing.model.Edge;
import de.bsailer.routing.model.EdgeIdentifier;
import de.bsailer.routing.model.Graph;
import de.bsailer.routing.model.impl.SimpleGraph;

import java.util.stream.Stream;

/**
 * This factory constructs a {@code Graph<E, I>} from a Stream of String representations.
 */
@SuppressWarnings("ClassCanBeRecord")
public class DescriptorGraphFactory<E extends Edge<I>, I extends EdgeIdentifier<I>> {

	private final EdgeFactory<E, I> edgeFactory;

	public DescriptorGraphFactory(final EdgeFactory<E, I> edgeFactory) {
		this.edgeFactory = edgeFactory;
	}

	public Graph<E, I> createSimpleGraph(final Stream<String> edgeDescriptors) {
		final SimpleGraph<E,I> result = new SimpleGraph<>();
		edgeDescriptors.forEach(e -> fromDescriptor(result, e));
		return result;
	}

	private void fromDescriptor(final ConstructableGraph<E, I> graph, final String edgeDescriptor) {
		final var edgeProperties = edgeDescriptor.split(";");
		final var id = edgeFactory.edgeIdentifierFromDescriptor(edgeProperties[0]);
		graph.addEdge(edgeFactory.edgeFromDescriptor(id, edgeProperties[1]));
		Stream.of(edgeProperties[2].split(","))
				.map(edgeFactory::edgeIdentifierFromDescriptor)
				.forEach(a -> graph.connectEdges(id, a));
	}

}
