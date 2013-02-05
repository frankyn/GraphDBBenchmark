package com.silvertower.app.bench.workload;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public class UpdateVertexSlaveThread extends SlaveThread {

	public UpdateVertexSlaveThread(Graph g, GraphDescriptor gDesc, Type t, long executionTime) {
		super(g, gDesc, t, executionTime);
	}
	
	protected void operation() {
		Object [] property = gDesc.getRandomPropertyCouple();
		Vertex v = g.getVertex(gDesc.getRandomVertexId());
		v.setProperty((String) property[0], property[1]);
	}

}
