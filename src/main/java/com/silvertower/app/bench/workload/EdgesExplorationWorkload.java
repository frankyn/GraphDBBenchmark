package com.silvertower.app.bench.workload;

import java.util.Iterator;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;

public class EdgesExplorationWorkload extends TraversalWorkload {
	public EdgesExplorationWorkload() {
		super("Edges exploration");
	}

	public void operation(GraphDescriptor gDesc) {
		Graph g = gDesc.getGraph();
		Iterator <Edge> iter = g.getEdges().iterator();
		Edge current = null;
		while (iter.hasNext()) {
			current = iter.next();
		}
		((TransactionalGraph)g).stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
	}
}
