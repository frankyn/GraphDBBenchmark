package com.silvertower.app.bench.workload;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.utils.Logger;
import com.tinkerpop.blueprints.Vertex;

public class NeighborhoodWorkload implements Workload {
	private int k;
	private int current;
	
	public NeighborhoodWorkload(int k) {
		this.k = k;
	}
	
	public void work(GraphDescriptor gDesc, Logger log) {
		Vertex entry = null;
		while (entry == null) {
			entry = gDesc.getGraph().getVertex(gDesc.getRandomVertexId());
		}
		computeNeighborhood(entry);
	}

	private void computeNeighborhood(Vertex v) {
		
	}
	
}
