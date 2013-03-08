package com.silvertower.app.bench.workload;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;


public class ReadPropIntensiveWorkload extends Workload {
	public ReadPropIntensiveWorkload() {
		super("Read properties intensive", true);
	}

	public void operation(GraphDescriptor gDesc, int ... threadId) {
		Object[] possibleProp = gDesc.getRandomPropertyCouple();
		gDesc.getGraph().getVertices((String)possibleProp[0], possibleProp[1]);
	}
}
