package com.silvertower.app.bench.workload;

import com.silvertower.app.bench.akka.GraphDescriptor;
import com.tinkerpop.blueprints.Vertex;

public class AddEdgesIntensiveWorkload extends IntensiveWorkload {

	public AddEdgesIntensiveWorkload() {
		super("Add edges intensive");
	}

	public String generateRequest(GraphDescriptor gDesc, int threadId, int number) {
		return null;
	}

	public void operation(GraphDescriptor gDesc, int threadId) {
		Object ids = gDesc.getRandomVertexId(threadId);
		Object idd = gDesc.getRandomVertexId(threadId);
		Vertex s = gDesc.getRexsterGraph().getVertex(ids);
		Vertex d = gDesc.getRexsterGraph().getVertex(idd);
		try {
			System.out.println(s);
			System.out.println(d);
			gDesc.getRexsterGraph().addEdge("test", s, d, "test");
		} catch (Exception e) {
			System.out.println("zboub");
		}
	}
}
