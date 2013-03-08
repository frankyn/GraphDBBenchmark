package com.silvertower.app.bench.workload;


import java.lang.management.ManagementFactory;
import java.util.concurrent.CountDownLatch;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.silvertower.app.bench.dbinitializers.*;

public class SlaveThread extends Thread {
	private Workload w;
	private GraphDescriptor gDesc;
	private int id;
	private int maxOpCount;
	private long timeSpentCPU;
	private CountDownLatch startLatch;
	private CountDownLatch stopLatch;
	public SlaveThread(GraphDescriptor gDesc, int id, Workload w, int maxOpCount, 
			CountDownLatch startLatch, CountDownLatch stopLatch) {
		this.w = w;
		this.gDesc = gDesc;
		this.id = id;
		this.maxOpCount = maxOpCount;
		this.startLatch = startLatch;
		this.stopLatch = stopLatch;
	}
	
	public void run() {
		try {
			startLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long before = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
		long opCount = 0;
		while (opCount < maxOpCount) {
			w.operation(gDesc, id);
			opCount ++;
		}
		((TransactionalGraph)gDesc.getGraph()).stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
		long after = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();	
		timeSpentCPU = after - before;
		stopLatch.countDown();
	}
	
	public long getTimeSpentCPU() {
		return timeSpentCPU;
	}
}
