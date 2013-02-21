package com.silvertower.app.bench.workload;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.tinkerpop.blueprints.Vertex;

public class UpdateIntensiveWorkload extends Workload {

	public UpdateIntensiveWorkload() {
		super("Update intensive", true);
	}

	public void operation(GraphDescriptor gDesc, int ... threadId) {
		Object [] property = gDesc.getRandomPropertyCouple();
		Vertex v = gDesc.getGraph().getVertex(gDesc.getRandomVertexId(threadId[0]));
		if (v != null) {
			v.setProperty((String) property[0], property[1]);
		}
	}
}
