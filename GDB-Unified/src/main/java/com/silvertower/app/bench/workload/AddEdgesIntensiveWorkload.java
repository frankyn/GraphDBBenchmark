package com.silvertower.app.bench.workload;

import com.silvertower.app.bench.akka.GraphDescriptor;
import com.silvertower.app.bench.annotations.Custom;
import com.tinkerpop.blueprints.Vertex;

@Custom
public class AddEdgesIntensiveWorkload extends IntensiveWorkload {
	private static final long serialVersionUID = -4641257810241575418L;

	public AddEdgesIntensiveWorkload(int nOps, int nClients, boolean rexPro) {
		super("Add edges intensive", nOps, nClients, rexPro);
	}

	public String generateRequest(GraphDescriptor gDesc, int threadId, int number) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < number; i++) {
			Object id1 = gDesc.getRandomVertexId(threadId);
			String id1Repr = id1 instanceof String ? "\"" + id1 + "\"" : id1.toString();
			Object id2 = gDesc.getRandomVertexId(threadId);
			String id2Repr = id2 instanceof String ? "\"" + id2 + "\"" : id2.toString();
			b.append(String.format("g.addEdge(g.v(%s), g.v(%s), \"test\", [:]);", id1Repr, id2Repr));
		}
		return b.toString();
	}

	public void operation(GraphDescriptor gDesc, int threadId) {
		Object ids = gDesc.getRandomVertexId(threadId);
		Object idd = gDesc.getRandomVertexId(threadId);
		Vertex s = gDesc.getRexsterGraph().getVertex(ids);
		Vertex d = gDesc.getRexsterGraph().getVertex(idd);
		try {
			gDesc.getRexsterGraph().addEdge("test", s, d, "test");
		} catch (Exception e) {
			System.out.println("Error while executing " + this.toString());
		}
	}
}
