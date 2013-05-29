package com.silvertower.app.bench.workload;

import java.io.Serializable;
import java.util.Iterator;

import com.silvertower.app.bench.akka.GraphDescriptor;
import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.Vertex;


public class GetVerticesByPropIntensiveWorkload extends IntensiveWorkload implements Serializable {
	private static final long serialVersionUID = -6914516778970375060L;

	public GetVerticesByPropIntensiveWorkload() {
		super("Get vertices by properties intensive");
	}
	
	public String generateRequest(GraphDescriptor gDesc, int threadId, int number) {
//		StringBuilder b = new StringBuilder();
//		for (int i = 0; i < number; i++) {
//			Object[] possibleProp = gDesc.getVerticesRandomPropertyCouples();
//			if (i == 0) b.append("g.V.or(_()");
//			else {
//				b.deleteCharAt(b.length()-1);
//				b.append(",_()");
//			}
//			for (int j = 0; j < possibleProp.length; j+=2) {
//				String keyRepr = "\"" + (String)possibleProp[j] + "\"";
//				String valueRepr = "\"" + (String)possibleProp[j+1] + "\"";
//				b.append(String.format(".has(%s,%s)", keyRepr, valueRepr));
//			}
//			b.append(")");
//		}
//		return b.toString();
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < number; i++) {
			Object [] property = gDesc.getVerticesRandomPropertyCouple();
			String keyRepr = "\"" + (String)property[0] + "\"";
			String valueRepr = "\"" + (String)property[1] + "\"";
			if (i == 0) b.append(String.format("g.V(%s,%s)", keyRepr, valueRepr));
			else {
				//b.deleteCharAt(b.length()-1);
				b.append(String.format(";g.V(%s,%s)", keyRepr, valueRepr));
			}
		}
		return b.toString();
	}

	public void operation(GraphDescriptor gDesc, int threadId) {
//		Object[] possibleProp = gDesc.getVerticesRandomPropertyCouple();
////		Iterator<Vertex> iter = gDesc.getGraph().getVertices((String)possibleProp[0], possibleProp[1]).iterator();
//		GraphQuery query = gDesc.getGraph().query();
//		for (int i = 0; i < possibleProp.length; i+=2) {
//			query.has((String)possibleProp[i], possibleProp[i+1]);
//		}
//		Iterator<Vertex> iter = query.vertices().iterator();
//		if (iter.hasNext()) {
//			iter.next().getId();
//		}
		Object[] possibleProp = gDesc.getVerticesRandomPropertyCouple();
		// Iterator<Vertex> iter = gDesc.getGraph().getVertices((String)possibleProp[0], possibleProp[1]).iterator();
		Iterator<Vertex> iter = gDesc.getRexsterGraph().query().has((String)possibleProp[0], possibleProp[1]).vertices().iterator();
		if (iter.hasNext()) {
			iter.next().getId();
		}
	}
}
