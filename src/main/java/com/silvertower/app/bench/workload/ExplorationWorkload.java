package com.silvertower.app.bench.workload;

import java.util.Iterator;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.silvertower.app.bench.utils.Utilities;

public class ExplorationWorkload {
	private Vertex firstVertex;
	private Vertex lastVertex;
	
	public void work(Graph g) {
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
		g.shutdown();
		long afterTs = System.nanoTime();
		Utilities.log("Database exploration", afterTs - beforeTs);
	}
	
	public Vertex getFirstVertex() {
		return firstVertex;
	}

	public Vertex getLastVertex() {
		return lastVertex;
	}
}
