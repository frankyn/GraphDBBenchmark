package com.silvertower.app.bench.akka;


import java.lang.management.ManagementFactory;
import java.util.concurrent.CountDownLatch;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.silvertower.app.bench.dbinitializers.*;
import com.silvertower.app.bench.main.ServerProperties;
import com.silvertower.app.bench.workload.IntensiveWorkload;

public class SlaveThread extends Thread {
	private IntensiveWorkload w;
	private GraphDescriptor gDesc;
	private int id;
	private int maxOpCount;
	private long timeSpentCPU;
	private CountDownLatch startLatch;
	private CountDownLatch stopLatch;
	public SlaveThread(GraphDescriptor gDesc, int id, IntensiveWorkload w, int maxOpCount, 
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
		long totalTimeSpent = 0;
		for (int i = 0; i < ServerProperties.meanTimes; i++) {
			long before = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
			long opCount = 0;
			while (opCount < maxOpCount) {
				w.operation(gDesc, id);
				System.out.println(opCount);
				opCount ++;
			}
			//((TransactionalGraph) gDesc.getGraph()).stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
			long after = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();	
			totalTimeSpent += after - before;
		}
		timeSpentCPU = totalTimeSpent / ServerProperties.meanTimes;
		stopLatch.countDown();
	}
	
	public long getTimeSpentCPU() {
		return timeSpentCPU;
	}
}
