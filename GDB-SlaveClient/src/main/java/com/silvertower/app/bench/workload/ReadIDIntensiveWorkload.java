package com.silvertower.app.bench.workload;


import com.silvertower.app.bench.dbinitializers.GraphDescriptor;


public class ReadIDIntensiveWorkload extends IntensiveWorkload {
	public ReadIDIntensiveWorkload() {
		super("Read ID intensive");
	}

	public void operation(GraphDescriptor gDesc, int threadId) {
		gDesc.getGraph().getVertex(gDesc.getRandomVertexId(threadId));
	}
}
