package com.silvertower.app.bench.workload;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.utils.Utilities;
import com.tinkerpop.blueprints.Graph;

public class UpdateIntensiveWorkload implements Workload {

	@Override
	public void work(Graph g, GraphDescriptor gDesc) {
		Utilities.log("Update intensive workload");
		MasterThread master = new MasterThread(g, gDesc, UpdateVertexSlaveThread.class);
		master.start();
	}

}
