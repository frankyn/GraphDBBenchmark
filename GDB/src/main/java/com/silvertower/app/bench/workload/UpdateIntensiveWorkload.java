package com.silvertower.app.bench.workload;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.tinkerpop.blueprints.Vertex;

public class UpdateIntensiveWorkload extends IntensiveWorkload {

	public UpdateIntensiveWorkload() {
		super("Update intensive");
	}

	public void operation(GraphDescriptor gDesc, int threadId) {
		Object [] property = gDesc.getRandomPropertyCouple();
		Object id = gDesc.getRandomVertexId(threadId);
		Vertex v = gDesc.getGraph().getVertex(id);
		if (v != null) {
			v.setProperty((String) property[0], property[1]);
		}
	}
}
