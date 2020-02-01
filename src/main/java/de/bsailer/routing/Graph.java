package de.bsailer.routing;

import java.util.List;

public interface Graph {

	List<Edge> adjacents(Edge edge);

}