package com.silvertower.app.bench.workload;


import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.utils.Logger;

public abstract class IntensiveWorkload {
	private String name;
	protected GraphDescriptor gDesc;
	public IntensiveWorkload(String name) {
		this.name = name;
	}
	
	public abstract void operation(int threadId);
	
	public void work(GraphDescriptor gDesc, Logger log) {
		this.gDesc = gDesc;
		for (int i = 1; i <= Runtime.getRuntime().availableProcessors(); i++) {
			MasterThread master = new MasterThread(i, gDesc, this);
			master.start();
			try {
				master.join();
			} catch (InterruptedException e) {}
			
			log.logOperation(name + " " + i + " clients");
			log.logResults(master.getResults());
			log.plotResults("Number of operations", "Time", "Wall time", "Cpu time");
		}
	}
}
