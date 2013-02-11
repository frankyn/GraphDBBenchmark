package com.silvertower.app.bench.workload;

import java.util.ArrayList;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.utils.Logger;
import com.tinkerpop.blueprints.Graph;

public class UpdateIntensiveWorkload implements Workload {
	
	public void work(GraphDescriptor gDesc, Logger log) {
		Graph g = gDesc.getGraph();
		MasterThread master = new MasterThread(g, gDesc, UpdateVertexSlaveThread.class);
		master.start();
		try {
			master.join();
		} catch (InterruptedException e) {}
		ArrayList<ResultPair> latencyResults = master.getLatencyResults();
		ArrayList<ResultPair> throughputResults = master.getThroughputResults();
		log.logOperation("Update_intensive_workload_latency");
		log.logResults(latencyResults);
		log.plotResults("Number of threads", "Latency");
		
		log.logOperation("Update_intensive_workload_throughput");
		log.logResults(throughputResults);
		log.plotResults("Number of threads", "Throughput");
	}
}
