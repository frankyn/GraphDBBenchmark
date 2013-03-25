package com.silvertower.app.bench.workload;

import java.io.Serializable;
import java.util.Iterator;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;

public class EdgesExplorationWorkload extends TraversalWorkload implements Serializable  {
	private static final long serialVersionUID = -5677909460461764635L;

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
	}

	public boolean preciseBenchmarkingNeeded() {
		return false;
	}
}
