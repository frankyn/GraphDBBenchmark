package com.silvertower.app.bench.workload;


public class ReadPropIntensiveWorkload extends IntensiveWorkload {
	public ReadPropIntensiveWorkload() {
		super("Read properties intensive");
	}

	public void operation(int threadId) {
		Object[] possibleProp = gDesc.getRandomPropertyCouple();
		gDesc.getGraph().getVertices((String)possibleProp[0], possibleProp[1]);
	}
}
