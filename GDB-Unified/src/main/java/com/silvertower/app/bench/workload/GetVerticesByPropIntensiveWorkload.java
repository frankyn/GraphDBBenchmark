package com.silvertower.app.bench.workload;

import java.io.Serializable;
import java.util.Iterator;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.tinkerpop.blueprints.Vertex;


public class GetVerticesByPropIntensiveWorkload extends IntensiveWorkload implements Serializable {
	private static final long serialVersionUID = -6914516778970375060L;

	public GetVerticesByPropIntensiveWorkload() {
		super("Get vertices by properties intensive");
	}

	public void operation(GraphDescriptor gDesc, int threadId) {
		Object[] possibleProp = gDesc.getVerticesRandomPropertyCouple();
		Iterator<Vertex> iter = gDesc.getGraph().getVertices((String)possibleProp[0], possibleProp[1]).iterator();
		if (iter.hasNext()) {
			iter.next();
		}
	}
}
