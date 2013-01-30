package com.silvertower.app.bench.workload;

import com.tinkerpop.blueprints.Graph;
import com.silvertower.app.bench.dbinitializers.*;

public class ReadIDsSlaveThread extends SlaveThread {
	
	public ReadIDsSlaveThread(Graph g, GraphDescriptor gDesc) {
		super(g, gDesc);
	}

	protected void operation() {
		Object rId = gDesc.getRandomVertexId();
		g.getVertex(rId);
	}
}
