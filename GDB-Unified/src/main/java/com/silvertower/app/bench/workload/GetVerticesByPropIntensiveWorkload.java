package com.silvertower.app.bench.workload;

import java.io.Serializable;
import java.util.Iterator;

import com.silvertower.app.bench.akka.GraphDescriptor;
import com.tinkerpop.blueprints.Vertex;


public class GetVerticesByPropIntensiveWorkload extends IntensiveWorkload implements Serializable {
	private static final long serialVersionUID = -6914516778970375060L;

	public GetVerticesByPropIntensiveWorkload() {
		super("Get vertices by properties intensive");
	}
	
	public String generateRequest(GraphDescriptor gDesc, int threadId, int number) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < number; i++) {
			Object [] property = gDesc.getVerticesRandomPropertyCouple();
			String keyRepr = "\"" + (String)property[0] + "\"";
			String valueRepr = "\"" + (String)property[1] + "\"";
			if (i == 0) b.append(String.format("g.V.or(_().has(%s,%s))", keyRepr, valueRepr));
			else {
				b.deleteCharAt(b.length()-1);
				b.append(String.format(",_().has(%s,%s))", keyRepr, valueRepr));
			}
		}
		return b.toString();
	}

	public void operation(GraphDescriptor gDesc, int threadId) {
		Object[] possibleProp = gDesc.getVerticesRandomPropertyCouple();
//		Iterator<Vertex> iter = gDesc.getGraph().getVertices((String)possibleProp[0], possibleProp[1]).iterator();
		Iterator<Vertex> iter = gDesc.getGraph().query().has((String)possibleProp[0], possibleProp[1]).vertices().iterator();
		if (iter.hasNext()) {
			iter.next().getId();
		}
	}
}
