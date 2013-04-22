package com.silvertower.app.bench.workload;


import java.io.Serializable;

import com.silvertower.app.bench.akka.GraphDescriptor;


public class GetVerticesByIDIntensiveWorkload extends IntensiveWorkload implements Serializable {
	private static final long serialVersionUID = -8388681706759862131L;

	public GetVerticesByIDIntensiveWorkload() {
		super("Get vertices by ID intensive");
	}

	public void operation(GraphDescriptor gDesc, int threadId) {
		gDesc.getGraph().getVertex(gDesc.getRandomVertexId(threadId));
	}
}
