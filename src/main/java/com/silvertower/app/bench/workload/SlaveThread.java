package com.silvertower.app.bench.workload;

import com.tinkerpop.blueprints.Graph;
import com.silvertower.app.bench.dbinitializers.*;

public abstract class SlaveThread extends Thread {
	protected boolean activated;
	protected boolean sleeping;
	protected int opCount = 0;
	private final int opLimit = 200;
	protected Graph g;
	protected GraphDescriptor gDesc;
	
	protected SlaveThread(Graph g, GraphDescriptor gDesc) {
		this.g = g;
		this.gDesc = gDesc;
	}
	
	public void startThread() {
		activated = true;
		sleeping = false;
		start();
	}
	
	public void stopThread() {
		activated = false;
	}
	
	public void sleepThread() {
		sleeping = true;
	}
	
	public int getAndResetOpCount() {
		int previousOpCount= opCount;
		opCount = 0;
		return previousOpCount;
	}

	public void resumeWork() {
		sleeping = false;
	}
	
	public int getOpCount() {
		return opCount;
	}
	
	public void run() {
		while (activated) {
			while (sleeping || opCount >= opLimit) {
				synchronized(this) {
					try {
						wait();
					} catch (InterruptedException e) {}
				}
			}
			operation();
			opCount++;
		}
		System.out.println("Thread killed:" + Thread.currentThread().getId());
	}
	
	protected abstract void operation();
}
