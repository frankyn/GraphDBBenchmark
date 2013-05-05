package com.silvertower.app.bench.workload;

import com.tinkerpop.blueprints.Vertex;

public class RandomWalkWorkload extends TraversalWorkload {
	private int pathLength;
	public RandomWalkWorkload(int pathLength) {
		super("Random walk");
		this.pathLength = pathLength;
	}

	public void operation(Vertex from, Vertex to) {
		for (int i = 0; i < pathLength; i++) {
			
		}
	}
}
