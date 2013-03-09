package com.silvertower.app.bench.workload;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;

public abstract class TraversalWorkload {
	private String name;
	public TraversalWorkload(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract void operation(GraphDescriptor gDesc);
}
