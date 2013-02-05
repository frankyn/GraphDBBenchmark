package com.silvertower.app.bench.workload;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import com.tinkerpop.blueprints.Graph;
import com.silvertower.app.bench.dbinitializers.*;

public abstract class SlaveThread extends Thread {
	protected boolean activated;
	protected long opCount;
	private int nbOperationsPerRound;
	protected Graph g;
	protected GraphDescriptor gDesc;
	public enum Type {
		CONCURRENCY, THROUGHPUT
	}
	protected Type t;
	protected long maxExecutionTimeInNano;
	private long effectiveExecutionTime;
	
	protected SlaveThread(Graph g, GraphDescriptor gDesc, Type t, long maxExecutionTimeInNano) {
		this.opCount = 0;
		this.g = g;
		this.gDesc = gDesc;
		this.t = t;
		this.maxExecutionTimeInNano = maxExecutionTimeInNano;
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
	
	public void setOperationsPerRound(int nbOperations) {
		nbOperationsPerRound = nbOperations;
	}
	
	public void run() {
		ThreadMXBean beanThread = ManagementFactory.getThreadMXBean();
		switch (t) {
		case CONCURRENCY:
			while (true) {
				if (beanThread.getCurrentThreadCpuTime() > maxExecutionTimeInNano) {
					effectiveExecutionTime = beanThread.getCurrentThreadCpuTime();
					return;
				}
				else {
					operation();
					opCount ++;
				}
			}
		
		case THROUGHPUT:
			while (true) {
				if (beanThread.getCurrentThreadCpuTime() > maxExecutionTimeInNano || opCount >= nbOperationsPerRound) {
					effectiveExecutionTime = beanThread.getCurrentThreadCpuTime();
					return;
				}
				else {
					operation();
					opCount ++;
				}
			}
		}
		
	}
	
	protected abstract void operation();

	public double getLatency() {
		return opCount / (effectiveExecutionTime * 1.0);
	}
}
