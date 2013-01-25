package com.silvertower.app.bench.workload;

import com.tinkerpop.blueprints.Graph;

public abstract class SlaveThread extends Thread {
	protected boolean active;
	protected int opCount;
	protected Graph g;
	
	protected SlaveThread(Graph g) {
		this.g = g;
	}
	
	public void stopWork() {
		active = false;
	}

	public void resumeWork() {
		active = true;
	}

	public void resetOpCount() {
		opCount = 0;
	}
	
	public int getOpCount() {
		return opCount;
	}
}
