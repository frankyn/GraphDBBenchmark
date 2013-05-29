package com.silvertower.app.bench.workload;


import java.io.Serializable;

import com.silvertower.app.bench.akka.GraphDescriptor;


public class GetVerticesByIDIntensiveWorkload extends IntensiveWorkload implements Serializable {
	private static final long serialVersionUID = -8388681706759862131L;

	public GetVerticesByIDIntensiveWorkload() {
		super("Get vertices by ID intensive");
	}

	public String generateRequest(GraphDescriptor gDesc, int threadId, int number) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < number; i++) {
			Object id = gDesc.getRandomVertexId(threadId);
			String idRepr = id instanceof String ? "\"" + id + "\"" : id.toString();
			if (i == 0) b.append(String.format("g.v(%s)", idRepr));
			else {
				b.deleteCharAt(b.length()-1);
				b.append(String.format(",%s)", idRepr));
			}
		}
		
		return b.toString();
	}
	
	public void operation(GraphDescriptor gDesc, int threadId) {
		Object id = gDesc.getRandomVertexId(threadId);
		gDesc.getRexsterGraph().getVertex(id);
	}
}
