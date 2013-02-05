package com.silvertower.app.bench.workload;

import java.util.Iterator;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.main.Globals;
import com.silvertower.app.bench.utils.Utilities;

public class VerticesExplorationWorkload implements Workload{
	private Vertex firstVertex;
	private Vertex lastVertex;
	
	public void work(Graph g, GraphDescriptor graphDescriptor) {
		long beforeTs = System.nanoTime();
		Iterator <Vertex> iter = g.getVertices().iterator();
		Vertex current = null;
		while (iter.hasNext()) {
			current = iter.next();
			if (firstVertex == null) {
				firstVertex = current;
			}
		}
		lastVertex = current;
		((TransactionalGraph)g).stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
		long afterTs = System.nanoTime();
		Utilities.log("Database vertices exploration", (afterTs - beforeTs) / (Globals.nanosToSFactor * 1.0));
	}
	
	public Vertex getFirstVertex() {
		return firstVertex;
	}

	public Vertex getLastVertex() {
		return lastVertex;
	}
}
