package com.silvertower.app.bench.workload;

import com.silvertower.app.bench.dbinitializers.*;
import com.silvertower.app.bench.utils.Utilities;
import com.tinkerpop.blueprints.Graph;

public class ReadIntensiveWorkload implements Workload {
	
	public void work(Graph g, GraphDescriptor gDesc) {
		Utilities.log("Read intensive workload");
		MasterThread master = new MasterThread(g, gDesc, ReadIDsSlaveThread.class);
		master.start();
	}

}
