package de.bsailer.routing;

import java.util.stream.Stream;

public class SimpleGraphFactory {

	public Graph createGraph(final Stream<String> edges) {
		final var result = new SimpleGraph();
		edges.map(this::fromDescriptor).forEach(result::addEdge);
		return result;
	}

	private Edge fromDescriptor(final String edgeDescriptor) {
		final String[] edgeProperties = edgeDescriptor.split(";");
		return new SimpleEdge(new SimpleEdgeIdentifier(Integer.parseInt(edgeProperties[0])))
				.setWeight(Double.parseDouble(edgeProperties[1]));
	}

}
