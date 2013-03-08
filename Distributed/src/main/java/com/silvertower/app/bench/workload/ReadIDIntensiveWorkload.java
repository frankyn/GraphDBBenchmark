package com.silvertower.app.bench.workload;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;


public class ReadIDIntensiveWorkload extends Workload {

	public ReadIDIntensiveWorkload() {
		super("Read ID intensive", true);
	}

	public void operation(GraphDescriptor gDesc, int ... threadIds) {
		Object rId = gDesc.getRandomVertexId(threadIds[0]);
		gDesc.getGraph().getVertex(rId);
	}
}
