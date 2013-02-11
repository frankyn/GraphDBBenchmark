package com.silvertower.app.bench.workload;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.silvertower.app.bench.dbinitializers.*;
import com.silvertower.app.bench.main.Globals;

public abstract class SlaveThread extends Thread {
	protected boolean activated;
	protected long opCount;
	protected Graph g;
	protected GraphDescriptor gDesc;
	protected long maxExecutionTimeInNano;
	private long effectiveExecutionTime;
	protected int id;
	
	protected SlaveThread(Graph g, GraphDescriptor gDesc, long maxExecutionTimeInNano, int id) {
		this.opCount = 0;
		this.g = g;
		this.gDesc = gDesc;
		this.maxExecutionTimeInNano = maxExecutionTimeInNano;
		this.id = id;
	}
	
	public void startThread() {
		activated = true;
		start();
	}
	
	public void stopThread() {
		activated = false;
	}
	
	public long getOpCount() {
		return opCount;
	}
	
	public void resetOpCount() {
		opCount = 0;
	}
	
	public void run() {
		ThreadMXBean beanThread = ManagementFactory.getThreadMXBean();
		while (true) {
			if (beanThread.getCurrentThreadCpuTime() > maxExecutionTimeInNano) {
				effectiveExecutionTime = beanThread.getCurrentThreadCpuTime();
				((TransactionalGraph)g).stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
				return;
			}
			
			else {
				operation();
				opCount ++;
			}
		}
	}
	
	protected abstract void operation();

	public double getLatency() {
		double scale = Globals.nanosToSFactor / (effectiveExecutionTime * 1.0);
		double opCountScaled = opCount * scale;
		return 1 / opCountScaled;
		//return opCount / (effectiveExecutionTime * 1.0);
	}
}
