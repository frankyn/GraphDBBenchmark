package com.silvertower.app.bench.workload;

import java.io.Serializable;

import com.silvertower.app.bench.akka.GraphDescriptor;

public class GetEdgesByPropIntensiveWorkload extends IntensiveWorkload implements Serializable {

	private static final long serialVersionUID = -301287645267730066L;

	public GetEdgesByPropIntensiveWorkload(int nOps, int nClients, boolean rexPro) {
		super("Get edges by ID intensive", nOps, nClients, rexPro);
	}

	public void operation(GraphDescriptor gDesc, int threadId) {
		Object[] possibleProp = gDesc.getEdgesRandomPropertyCouple();
		gDesc.getRexsterGraph().getEdges((String)possibleProp[0], possibleProp[1]);
	}

	@Override
	public String generateRequest(GraphDescriptor gDesc, int threadId,
			int number) {
		// TODO
		return null;
	}

}
