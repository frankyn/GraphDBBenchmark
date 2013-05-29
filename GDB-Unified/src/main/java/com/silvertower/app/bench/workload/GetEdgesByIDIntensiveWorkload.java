package com.silvertower.app.bench.workload;

import java.io.Serializable;

import com.silvertower.app.bench.akka.GraphDescriptor;

public class GetEdgesByIDIntensiveWorkload extends IntensiveWorkload implements Serializable {

	private static final long serialVersionUID = -8452747067849644111L;

	public GetEdgesByIDIntensiveWorkload(String name) {
		super("Get edges by ID intensive");
	}

	public void operation(GraphDescriptor gDesc, int threadId) {
		gDesc.getRexsterGraph().getEdge(gDesc.getRandomEdgeId(threadId));
	}

	@Override
	public String generateRequest(GraphDescriptor gDesc, int threadId,
			int number) {
		// TODO Auto-generated method stub
		return null;
	}
}
