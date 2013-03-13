package com.silvertower.app.bench.workload;

import java.io.Serializable;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;

public abstract class TraversalWorkload implements Serializable {
	private static final long serialVersionUID = -4387044308237183728L;
	private String name;
	public TraversalWorkload(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract void operation(GraphDescriptor gDesc);
}
