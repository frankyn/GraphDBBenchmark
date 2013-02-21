package com.silvertower.app.bench.workload;


import java.util.concurrent.Callable;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.silvertower.app.bench.dbinitializers.*;

public class SlaveThread implements Callable<Void> {
	private IntensiveWorkload w;
	private GraphDescriptor gDesc;
	private int id;
	private long maxOpCount;
	
	protected SlaveThread(GraphDescriptor gDesc, int id, IntensiveWorkload w, long maxOpCount) {
		this.w = w;
		this.gDesc = gDesc;
		this.id = id;
		this.maxOpCount = maxOpCount;
	}
	
	public Void call() throws Exception {
		long opCount = 0;
		while (opCount < maxOpCount) {
			w.operation(gDesc, id);
			opCount ++;
		}
		((TransactionalGraph)gDesc.getGraph()).stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
		return null;
	}
}
