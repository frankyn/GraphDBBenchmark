package com.silvertower.app.bench.workload;

import java.util.Iterator;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.silvertower.app.bench.dbinitializers.GraphDescriptor;

public class VerticesExplorationWorkload extends Workload {
	
	public VerticesExplorationWorkload() {
		super("Vertices exploration", false);
	}

	public void operation(GraphDescriptor gDesc, int ... threadId) {
		Graph g = gDesc.getGraph();
		Iterator <Vertex> iter = g.getVertices().iterator();
		Vertex current = null;
		while (iter.hasNext()) {
			current = iter.next();
		}
		((TransactionalGraph)g).stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
	}
}
