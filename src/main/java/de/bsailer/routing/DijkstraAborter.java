package de.bsailer.routing;

public interface DijkstraAborter {

	boolean abort(Edge current);
}
