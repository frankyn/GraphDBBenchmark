package com.silvertower.app.bench.workload;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.tinkerpop.blueprints.Graph;

public interface Workload {
	public void work(Graph g, GraphDescriptor gDesc);
}
