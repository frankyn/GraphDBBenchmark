package com.silvertower.app.bench.workload;

import java.util.List;

import bb.util.Benchmark;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.utils.Logger;
import com.silvertower.app.bench.utils.Utilities;

public abstract class TraversalWorkload {
	private String name;
	public TraversalWorkload(String name) {
		this.name = name;
	}
	
	public abstract void operation(GraphDescriptor gDesc);
	
	public void work(List<GraphDescriptor> gDescs, Logger log) {
		double time = 0;
		log.logOperation(name);
		for (final GraphDescriptor gDesc: gDescs) {
			Runnable task = 
			        new Runnable() { public void run() { operation(gDesc); } };
			double [] times = Utilities.benchTask(task);
			log.logResult(new Result(gDesc.getNbVertices(), times[0], times[1]));
		}
		if (gDescs.size() > 1) log.plotResults("Number of vertices", "Time", "Wall time", "Cpu time");
	}
}
