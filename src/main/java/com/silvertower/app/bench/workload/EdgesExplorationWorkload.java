package com.silvertower.app.bench.workload;

import java.util.Iterator;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.main.Globals;
import com.silvertower.app.bench.utils.Logger;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;

public class EdgesExplorationWorkload implements Workload {
	public void work(GraphDescriptor gDesc, Logger log) {
		Graph g = gDesc.getGraph();
		long beforeTs = System.nanoTime();
		Iterator <Edge> iter = g.getEdges().iterator();
		Edge current = null;
		while (iter.hasNext()) {
			current = iter.next();
		}
		((TransactionalGraph)g).stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
		long afterTs = System.nanoTime();
		log.log("Database edges exploration", (afterTs - beforeTs) / (Globals.nanosToSFactor * 1.0));
	}
}
