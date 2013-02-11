package com.silvertower.app.bench.workload;


import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.silvertower.app.bench.dbinitializers.*;

public class ReadIDsSlaveThread extends SlaveThread {
	
	public ReadIDsSlaveThread(Graph g, GraphDescriptor gDesc, long executionTime, int id) {
		super(g, gDesc, executionTime, id);
	}

	protected void operation() {
		Object rId = gDesc.getRandomVertexId(id);
		g.getVertex(rId);
	}
}
