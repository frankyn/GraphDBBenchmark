package com.silvertower.app.bench.workload;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;


public class NeighborhoodWorkload extends TraversalWorkload {
	private static final int times = 100;
	private int k;
	
	public NeighborhoodWorkload(int k) {
		super(String.format("Neighborhood %d %d times", k, times));
		this.k = k;
	}
	
	public NeighborhoodWorkload() {
		super(String.format("Neighborhood %d %d times", 7, times));
		this.k = 5;
	}
	
	public void operation(GraphDescriptor gDesc) {
		Vertex entry = null;
		while (entry == null) {
			entry = gDesc.getGraph().getVertex(gDesc.getRandomVertexId());
		}
		for (int i = 0; i < times; i++) {
			computeNeighborhood(entry, 0);
		}
	}

	private void computeNeighborhood(Vertex v, int current) {
		if (current == k) return;
		else {
			current = current + 1;
			for (Vertex adjacent: v.getVertices(Direction.OUT)) {
				computeNeighborhood(adjacent, current);
			}
		}
	}
}
