package com.silvertower.app.bench.workload;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;

public class UpdateVertexSlaveThread extends SlaveThread {

	public UpdateVertexSlaveThread(Graph g, GraphDescriptor gDesc, long executionTime, int id) {
		super(g, gDesc, executionTime, id);
	}
	
	protected void operation() {
		Object [] property = gDesc.getRandomPropertyCouple();
		Vertex v = g.getVertex(gDesc.getRandomVertexId(id));
		if (v != null) {
			v.setProperty((String) property[0], property[1]);
		}
	}

}
