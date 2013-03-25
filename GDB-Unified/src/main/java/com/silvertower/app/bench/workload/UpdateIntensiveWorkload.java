package com.silvertower.app.bench.workload;

import java.io.Serializable;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.tinkerpop.blueprints.Vertex;

public class UpdateIntensiveWorkload extends IntensiveWorkload implements Serializable {
	private static final long serialVersionUID = -8421933447224966555L;

	public UpdateIntensiveWorkload() {
		super("Update intensive");
	}

	public void operation(GraphDescriptor gDesc, int threadId) {
		Object [] property = gDesc.getRandomPropertyCouple();
		Object id = gDesc.getRandomVertexId(threadId);
		System.out.println(id);
		Vertex v = gDesc.getGraph().getVertex(id);
		if (v != null) {
			v.setProperty((String) property[0], property[1]);
		}
	}
}
