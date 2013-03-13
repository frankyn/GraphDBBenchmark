package com.silvertower.app.bench.workload;

import java.io.Serializable;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;


public class ReadPropIntensiveWorkload extends IntensiveWorkload implements Serializable {
	private static final long serialVersionUID = -6914516778970375060L;

	public ReadPropIntensiveWorkload() {
		super("Read properties intensive");
	}

	public void operation(GraphDescriptor gDesc, int threadId) {
		Object[] possibleProp = gDesc.getRandomPropertyCouple();
		gDesc.getGraph().getVertices((String)possibleProp[0], possibleProp[1]);
	}
}
