package com.silvertower.app.bench.workload;

import java.io.Serializable;

import com.silvertower.app.bench.akka.GraphDescriptor;
import com.tinkerpop.blueprints.Edge;

public class UpdateEdgesIntensiveWorkload extends IntensiveWorkload implements Serializable {
	
	private static final long serialVersionUID = 9089701656082730745L;

	public UpdateEdgesIntensiveWorkload(int nOps, int nClients, boolean rexPro) {
		super("Update edges properties intensive", nOps, nClients, rexPro);
	}

	public void operation(GraphDescriptor gDesc, int threadId) {
		Object [] property = gDesc.getEdgesRandomPropertyCouple();
		Object id = gDesc.getRandomEdgeId(threadId);
		Edge e = gDesc.getRexsterGraph().getEdge(id);
		if (e != null) {
			e.setProperty((String) property[0], property[1]);
		}
	}

	@Override
	public String generateRequest(GraphDescriptor gDesc, int threadId,
			int number) {
		// TODO
		return null;
	}
}
