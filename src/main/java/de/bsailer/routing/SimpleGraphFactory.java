package de.bsailer.routing;

import java.util.stream.Stream;

public class SimpleGraphFactory {

	public Graph createGraph(final Stream<String> edges) {
		final var result = new SimpleGraph();
		edges.forEach((l) -> {
			final String[] edgeProperties = l.split(";");
			final Edge edge = new SimpleEdge().setWeight(Double.parseDouble(edgeProperties[0]));
			result.addEdge(edge);
		});
		return result;
	}

}
