package com.silvertower.app.bench.workload;

import java.io.Serializable;

import com.tinkerpop.blueprints.Vertex;

public abstract class TraversalWorkload implements Serializable {
	private static final long serialVersionUID = -4387044308237183728L;
	private String name;
	protected int numberOfElementsInThePipe;
	public TraversalWorkload(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public int getNumberOfElementsInThePipe() {
		return numberOfElementsInThePipe;
	}
	
	public abstract void operation(Vertex from, Vertex to);
}
