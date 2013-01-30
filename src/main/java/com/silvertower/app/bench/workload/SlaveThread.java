package com.silvertower.app.bench.workload;

import com.tinkerpop.blueprints.Graph;
import com.silvertower.app.bench.dbinitializers.*;

public abstract class SlaveThread extends Thread {
	protected boolean active = false;
	protected int opCount = 0;
	protected Graph g;
	protected GraphDescriptor gDesc;
	
	protected SlaveThread(Graph g, GraphDescriptor gDesc) {
		this.g = g;
		this.gDesc = gDesc;
	}
	
	public void activate() {
		active = true;
		start();
	}

	public int stopWorkGetAndResetOpCount() {
		active = false;
		int previousOpCount= opCount;
		opCount = 0;
		return previousOpCount;
	}

	public void resumeWork() {
		active = true;
	}
	
	public void stopWork() {
		active = false;
	}
	
	public int getOpCount() {
		return opCount;
	}
}
