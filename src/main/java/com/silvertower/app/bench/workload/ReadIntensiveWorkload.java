package com.silvertower.app.bench.workload;

import com.silvertower.app.bench.main.Globals;
import com.tinkerpop.blueprints.Graph;

public class ReadIntensiveWorkload {
	private Graph g;
	
	public ReadIntensiveWorkload(Graph g) {
		this.g = g;
	}
	
	public void runWorkload() {
		MasterThread master = new MasterThread(g, ReadIDsSlaveThread.class);
		master.start();
	}

}
