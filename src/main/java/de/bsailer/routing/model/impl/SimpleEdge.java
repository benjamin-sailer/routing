package de.bsailer.routing.model.impl;

import de.bsailer.routing.model.Edge;

public class SimpleEdge implements Edge<SimpleEdgeIdentifier> {

	private final SimpleEdgeIdentifier id;
	private double weight;
	private double length;

	public SimpleEdge(final SimpleEdgeIdentifier id) {
		this.id = id;
	}

	@Override
	public SimpleEdgeIdentifier id() {
		return id;
	}

	@Override
	public double weight() {
		return weight;
	}

	public double length() {
		return length;
	}

	public SimpleEdge setWeight(final double weight) {
		this.weight = weight;
		return this;
	}

	public SimpleEdge setLength(final double length) {
		this.length = length;
		return this;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{id=" + id + ", weight=" + weight + ", length=" + length + "}";
	}
}
