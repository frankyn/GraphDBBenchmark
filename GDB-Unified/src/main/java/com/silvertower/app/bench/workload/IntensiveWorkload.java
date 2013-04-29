package com.silvertower.app.bench.workload;


import java.io.Serializable;

import com.silvertower.app.bench.akka.GraphDescriptor;

public abstract class IntensiveWorkload implements Serializable {
	private static final long serialVersionUID = 8599937655942079452L;
	private String name;
	public IntensiveWorkload(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
	
	public abstract String generateRequest(GraphDescriptor gDesc, int threadId, int number);
	
	public abstract void operation(GraphDescriptor gDesc, int threadId);
}