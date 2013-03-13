package com.silvertower.app.bench.workload;


import java.io.Serializable;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;

public abstract class IntensiveWorkload implements Serializable {
	private static final long serialVersionUID = 8599937655942079452L;
	private String name;
	public IntensiveWorkload(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract void operation(GraphDescriptor gDesc, int threadId);
}