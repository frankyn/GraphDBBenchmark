package com.silvertower.app.bench.workload;

import java.io.IOException;
import java.io.Serializable;

import com.silvertower.app.bench.akka.GraphDescriptor;
import com.tinkerpop.blueprints.Vertex;

public class UpdateVerticesIntensiveWorkload extends IntensiveWorkload implements Serializable {
	private static final long serialVersionUID = -8421933447224966555L;

	public UpdateVerticesIntensiveWorkload() {
		super("Update vertices intensive");
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
		Vertex v = gDesc.getGraph().getVertex(id);
		System.out.println("thread:" + Thread.currentThread().getName() + " id:"+id + " prop0:"+(String) property[0] + " prop1:"+(String) property[1]);
		if (v != null) {
			try {
				v.setProperty((String) property[0], property[1]);
			} catch (Exception e) {System.out.println("zboub");}
		}
	}
}
