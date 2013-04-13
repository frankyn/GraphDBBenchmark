package com.silvertower.app.bench.workload;

import java.io.Serializable;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;

public class GetEdgesByPropIntensiveWorkload extends IntensiveWorkload implements Serializable {

	private static final long serialVersionUID = -301287645267730066L;

	public GetEdgesByPropIntensiveWorkload(String name) {
		super("Get edges by ID intensive");
	}

	public void operation(GraphDescriptor gDesc, int threadId) {
		Object[] possibleProp = gDesc.getEdgesRandomPropertyCouple();
		gDesc.getGraph().getEdges((String)possibleProp[0], possibleProp[1]);
	}

}
