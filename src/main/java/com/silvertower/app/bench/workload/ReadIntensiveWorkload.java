package com.silvertower.app.bench.workload;

import com.silvertower.app.bench.dbinitializers.*;
import com.tinkerpop.blueprints.Graph;

public class ReadIntensiveWorkload implements Workload {
	
	public void work(Graph g, GraphDescriptor gDesc) {
		MasterThread master = new MasterThread(g, gDesc, ReadIDsSlaveThread.class);
		master.start();
	}

}
