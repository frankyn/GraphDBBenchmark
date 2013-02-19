package com.silvertower.app.bench.workload;

import com.tinkerpop.blueprints.Vertex;

public class UpdateIntensiveWorkload extends IntensiveWorkload {

	public UpdateIntensiveWorkload() {
		super("Update intensive");
	}

	public void operation(int threadId) {
		Object [] property = gDesc.getRandomPropertyCouple();
		Vertex v = gDesc.getGraph().getVertex(gDesc.getRandomVertexId(threadId));
		if (v != null) {
			v.setProperty((String) property[0], property[1]);
		}
	}
}
