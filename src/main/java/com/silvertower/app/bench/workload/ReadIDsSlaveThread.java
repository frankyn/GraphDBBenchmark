package com.silvertower.app.bench.workload;

import com.tinkerpop.blueprints.Graph;
import com.silvertower.app.bench.dbinitializers.*;

public class ReadIDsSlaveThread extends SlaveThread {
	
	public ReadIDsSlaveThread(Graph g, GraphDescriptor gDesc) {
		super(g, gDesc);
	}

	public void run() {
		while(true) {
			while(!active) {
				synchronized(this) {
					try {
						System.out.println("Thread: " + Thread.currentThread().getName() + " waiting");
						wait();
					} catch (InterruptedException e) {}
				}
			}
			readRandomVertexId();
			opCount++;
		}
	}

	private void readRandomVertexId() {
		Object rId = gDesc.getRandomVertexId();
		g.getVertex(rId);
	}
}
