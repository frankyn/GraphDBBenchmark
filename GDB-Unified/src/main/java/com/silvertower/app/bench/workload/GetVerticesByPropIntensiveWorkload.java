package com.silvertower.app.bench.workload;

import java.io.Serializable;
import java.util.Iterator;

import com.silvertower.app.bench.akka.GraphDescriptor;
import com.silvertower.app.bench.annotations.Custom;
import com.tinkerpop.blueprints.Vertex;

@Custom
public class GetVerticesByPropIntensiveWorkload extends IntensiveWorkload implements Serializable {
	private static final long serialVersionUID = -6914516778970375060L;

	public GetVerticesByPropIntensiveWorkload(int nOps, int nClients, boolean rexPro) {
		super("Get vertices by properties intensive", nOps, nClients, rexPro);
	}
	
	public String generateRequest(GraphDescriptor gDesc, int threadId, int number) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < number; i++) {
			Object [] property = gDesc.getVerticesRandomPropertyCouple();
			String keyRepr = "\"" + (String)property[0] + "\"";
			String valueRepr = "\"" + (String)property[1] + "\"";
			b.append(String.format("g.V(%s,%s);", keyRepr, valueRepr));
		}
		return b.toString();
	}

	public void operation(GraphDescriptor gDesc, int threadId) {
		Object[] possibleProp = gDesc.getVerticesRandomPropertyCouple();
		Iterator<Vertex> iter = gDesc.getRexsterGraph().query().has((String)possibleProp[0], possibleProp[1]).vertices().iterator();
		if (iter.hasNext()) {
			iter.next().getId();
		}
	}
}
