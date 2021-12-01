package de.bsailer.routing;

public interface Edge<I extends EdgeIdentifier<I>> {

	I id();

	double weight();

}