package com.silvertower.app.bench.workload;

import java.io.Serializable;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

public abstract class TraversalWorkload implements Serializable {
	private static final long serialVersionUID = -4387044308237183728L;
	private String name;
	protected int numberOfElementsInThePipe;
	public TraversalWorkload(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
	
	public int getNumberOfElementsInThePipe() {
		return numberOfElementsInThePipe;
	}
	
	public void evaluatePipe(GremlinPipeline p) {
		while (p.hasNext() && numberOfElementsInThePipe < 3000) {
			p.next();
			numberOfElementsInThePipe++;
		}
		System.out.println(numberOfElementsInThePipe);
	}
	
	public abstract String generateRequest(Vertex from, Vertex to);
	
	public abstract void operation(Vertex from, Vertex to);
}
