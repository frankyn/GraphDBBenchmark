package com.silvertower.app.bench.workload;

import java.util.Iterator;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.main.Globals;
import com.silvertower.app.bench.utils.Utilities;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;

public class EdgesExplorationWorkload implements Workload {
	private Edge firstEdge;
	private Edge lastEdge;
	
	public void work(Graph g, GraphDescriptor graphDescriptor) {
		long beforeTs = System.nanoTime();
		Iterator <Edge> iter = g.getEdges().iterator();
		Edge current = null;
		while (iter.hasNext()) {
			current = iter.next();
			if (firstEdge == null) {
				firstEdge = current;
			}
		}
		firstEdge = current;
		((TransactionalGraph)g).stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
		long afterTs = System.nanoTime();
		Utilities.log("Database edges exploration", (afterTs - beforeTs) / (Globals.nanosToSFactor * 1.0));
	}
	
	public Edge getFirstVertex() {
		return firstEdge;
	}

	public Edge getLastVertex() {
		return lastEdge;
	}
}
