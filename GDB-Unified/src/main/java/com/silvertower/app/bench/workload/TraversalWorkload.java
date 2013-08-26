package com.silvertower.app.bench.workload;

import java.io.Serializable;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

public abstract class TraversalWorkload implements Serializable, Workload {
	private static final long serialVersionUID = -4387044308237183728L;
	private String name;
	protected int nHops;
	protected int numberOfElementsInThePipe;
	public TraversalWorkload(String name, int nHops) {
		this.name = name;
		this.nHops = nHops;
	}
	
	public String toString() {
		return String.format("%s with %d hops", name, nHops);
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
	
	public Type getType() {
		return Type.TRAVERSAL;
	}
}
