package com.silvertower.app.bench.workload;

import java.util.Iterator;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.main.Globals;
import com.silvertower.app.bench.utils.Logger;

public class VerticesExplorationWorkload implements Workload {
	
	public void work(GraphDescriptor gDesc, Logger log) {
		Graph g = gDesc.getGraph();
		long beforeTs = System.nanoTime();
		Iterator <Vertex> iter = g.getVertices().iterator();
		Vertex current = null;
		while (iter.hasNext()) {
			current = iter.next();
		}
		((TransactionalGraph)g).stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
		long afterTs = System.nanoTime();
		log.log("Database vertices exploration", (afterTs - beforeTs) / (Globals.nanosToSFactor * 1.0));
	}
}
