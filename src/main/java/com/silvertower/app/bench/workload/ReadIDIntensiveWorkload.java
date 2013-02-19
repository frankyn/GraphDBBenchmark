package com.silvertower.app.bench.workload;


public class ReadIDIntensiveWorkload extends IntensiveWorkload {

	public ReadIDIntensiveWorkload() {
		super("Read ID intensive");
	}

	public void operation(int threadId) {
		Object rId = gDesc.getRandomVertexId(threadId);
		gDesc.getGraph().getVertex(rId);
	}
}
