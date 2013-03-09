package com.silvertower.app.bench.workload;


import com.silvertower.app.bench.dbinitializers.GraphDescriptor;

public abstract class IntensiveWorkload {
	private String name;
	public IntensiveWorkload(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract void operation(GraphDescriptor gDesc, int threadId);
}