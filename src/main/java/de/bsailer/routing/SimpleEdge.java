package de.bsailer.routing;

public class SimpleEdge implements Edge {

	private double weight;
	private double length;

	@Override
	public double weight() {
		return weight;
	}

	public double length() {
		return length;
	}

	public Edge setWeight(final double weight) {
		this.weight = weight;
		return this;
	}

	public SimpleEdge setLength(final double length) {
		this.length = length;
		return this;
	}

}
