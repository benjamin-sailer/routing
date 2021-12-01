package de.bsailer.routing;

import java.util.List;

public interface Graph<T extends Edge<?>> {

	List<T> adjacents(T edge);

}