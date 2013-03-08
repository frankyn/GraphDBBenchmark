package com.silvertower.app.bench.workload;


import com.silvertower.app.bench.dbinitializers.GraphDescriptor;

public abstract class Workload {
	private String name;
	private boolean multiThread;
	public Workload(String name, boolean multiThread) {
		this.name = name;
		this.multiThread = multiThread;
	}
	
	public boolean isMT() {
		return multiThread;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract void operation(GraphDescriptor gDesc, int ... threadId);
}