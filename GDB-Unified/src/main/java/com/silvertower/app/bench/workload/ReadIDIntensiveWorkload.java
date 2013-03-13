package com.silvertower.app.bench.workload;


import java.io.Serializable;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;


public class ReadIDIntensiveWorkload extends IntensiveWorkload implements Serializable {
	private static final long serialVersionUID = -8388681706759862131L;

	public ReadIDIntensiveWorkload() {
		super("Read ID intensive");
	}

	public void operation(GraphDescriptor gDesc, int threadId) {
		gDesc.getGraph().getVertex(gDesc.getRandomVertexId(threadId));
	}
}
