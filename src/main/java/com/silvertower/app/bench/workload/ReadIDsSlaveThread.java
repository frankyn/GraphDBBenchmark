package com.silvertower.app.bench.workload;

import com.tinkerpop.blueprints.Graph;

public class ReadIDsSlaveThread extends SlaveThread {
	private boolean active = true;
	private Graph g;
	
	public ReadIDsSlaveThread(Graph g) {
		super(g);
	}

	public void run() {
		while(true) {
			while(!active) {
				synchronized(this) {
					try {
						wait();
					} catch (InterruptedException e) {}
				}
				resetOpCount();
			}
			readRandomVertexId();
			opCount++;
		}
	}

	private void readRandomVertexId() {
		
	}
}
