package com.silvertower.app.bench.workload;

import java.io.Serializable;

import com.silvertower.app.bench.akka.GraphDescriptor;
import com.tinkerpop.blueprints.Vertex;

public class UpdateVerticesIntensiveWorkload extends IntensiveWorkload implements Serializable {
	private static final long serialVersionUID = -8421933447224966555L;

	public UpdateVerticesIntensiveWorkload() {
		super("Update vertices intensive");
	}

	public void operation(GraphDescriptor gDesc, int threadId) {
		Object [] property = gDesc.getVerticesRandomPropertyCouple();
		Object id = gDesc.getRandomVertexId(threadId);
		Vertex v = gDesc.getGraph().getVertex(id);
		if (v != null) {
			v.setProperty((String) property[0], property[1]);
		}
	}
}
