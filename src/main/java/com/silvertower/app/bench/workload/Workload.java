package com.silvertower.app.bench.workload;


import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.utils.Logger;

public abstract class Workload {
	private String name;
	private boolean multiThread;
	public Workload(String name, boolean multiThread) {
		this.name = name;
		this.multiThread = multiThread;
	}
	
	public boolean isMT() {
		return multiThread;
	}
	
	public abstract void operation(GraphDescriptor gDesc, int ... threadId);
	
	public double[][] work(final GraphDescriptor gDesc, Logger log) {
		if (multiThread) {
			/*
			int numberProcessors = Runtime.getRuntime().availableProcessors();
			double [][] allTimes = new double[numberProcessors][];
			this.gDesc = gDesc;
			for (int i = 1; i <= numberProcessors; i++) {
				MasterThread master = new MasterThread(i, gDesc, this);
				master.start();
				try {
					master.join();
				} catch (InterruptedException e) {}
				
				List<TimeResult> resultsList = master.getResults();
				double[] results = ArrayUtils.toPrimitive(resultsList.toArray(new Double[resultsList.size()]));
				allTimes[i-1] = results;
				
				//log.logOperation(name + " " + i + " clients");
				//log.logResults(master.getResults());
				//log.plotResults("Number of operations", "Time", "Wall time", "Cpu time");
			}
			return allTimes;*/
		}
		
		else {
			/*double [][] times = new double[1][2];
			//log.logOperation(name);
			Runnable task = 
					new Runnable() { public void run() { operation(gDesc); } };
			times[0] = Utilities.benchTask(task);
			//log.logResult(new Result(gDesc.getNbVertices(), times[0], times[1]));
			return times;*/
		}
		
		return null;
	}
}