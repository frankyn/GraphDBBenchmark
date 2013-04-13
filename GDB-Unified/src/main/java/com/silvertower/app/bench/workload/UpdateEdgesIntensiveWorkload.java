package com.silvertower.app.bench.workload;

import java.io.Serializable;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.tinkerpop.blueprints.Edge;

public class UpdateEdgesIntensiveWorkload extends IntensiveWorkload implements Serializable {
	
	private static final long serialVersionUID = 9089701656082730745L;

	public UpdateEdgesIntensiveWorkload(String name) {
		super("Update edges properties intensive");
	}

	public void operation(GraphDescriptor gDesc, int threadId) {
		Object [] property = gDesc.getEdgesRandomPropertyCouple();
		Object id = gDesc.getRandomEdgeId(threadId);
		Edge e = gDesc.getGraph().getEdge(id);
		if (e != null) {
			e.setProperty((String) property[0], property[1]);
		}
	}
}