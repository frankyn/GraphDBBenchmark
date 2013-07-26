package com.silvertower.app.bench.workload;

import java.io.Serializable;

import com.silvertower.app.bench.akka.GraphDescriptor;
import com.silvertower.app.bench.annotations.Custom;
import com.tinkerpop.blueprints.Vertex;

@Custom
public class UpdateVerticesIntensiveWorkload extends IntensiveWorkload implements Serializable {
	private static final long serialVersionUID = -8421933447224966555L;

	public UpdateVerticesIntensiveWorkload(int nOps, int nClients, boolean rexPro) {
		super("Update vertices intensive", nOps, nClients, rexPro);
	}
	
	public String generateRequest(GraphDescriptor gDesc, int threadId, int number) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < number; i++) {
			Object id = gDesc.getRandomVertexId(threadId);
			Object [] property = gDesc.getVerticesRandomPropertyCouple();
			String idRepr = id instanceof String ? "\"" + id + "\"" : id.toString();
			String propRepr = "\"" + (String)property[1] + "\"";
			b.append(String.format("g.v(%s).%s=%s;", idRepr, (String) property[0], propRepr));
		}
		return b.toString();
	}

	public void operation(final GraphDescriptor gDesc, int threadId) {
		Object [] property = gDesc.getVerticesRandomPropertyCouple();
		Object id = gDesc.getRandomVertexId(threadId);
		Vertex v = gDesc.getRexsterGraph().getVertex(id);
		if (v != null) {
			try {
				v.setProperty((String) property[0], property[1]);
			} catch (Exception e) {System.out.println("zboub");}
		}
	}
}
