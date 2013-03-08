package com.silvertower.app.bench.workload;


import bb.util.Benchmark;

import com.silvertower.app.bench.dbinitializers.*;
import com.silvertower.app.bench.utils.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class MasterThread extends Thread {
	private GraphDescriptor gDesc;
	private IntensiveWorkload w;	
	private static final int initialNbOperations = 50000;
	private static final int addNbOperations = 50000;
	private static final int maxNbOperations = 1000000;
	
	private ArrayList<Result> results;
	private int nbThreads;
	
	
	public MasterThread(int nbThreads, GraphDescriptor gDesc, IntensiveWorkload w) {
		this.gDesc = gDesc;
		this.w = w;
		this.results = new ArrayList<Result>();
		this.nbThreads = nbThreads;
	}

	public void run() {
		measureConcurrency();
	}

	private void measureConcurrency() {
		gDesc.setNbConcurrentThreads(nbThreads);
		for (int i = initialNbOperations; i <= maxNbOperations; i += addNbOperations) {
			List<Callable<Void>> slaves = new ArrayList<Callable<Void>>();
			
			for (int j = 1; j <= nbThreads; j++) {
				Callable<Void> slave = new SlaveThread(gDesc, j, w, i / j);
				slaves.add(slave);
			}
			double [] times = Utilities.benchTask(new InnerThread(slaves));			
			results.add(new Result(i, times[0], times[1]));
		}
	}
	
	public List<Result> getResults() {
		return results;
	}
	
	class InnerThread implements Runnable {
		private ExecutorService executor;
		private List<Callable<Void>> slaves;
		public InnerThread(List<Callable<Void>> slaves) {
			this.executor = Executors.newFixedThreadPool(nbThreads);
			this.slaves = slaves;
		}
		public void run() {
			try {
				executor.invokeAll(slaves);
				executor.shutdown();
				executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
				executor = Executors.newFixedThreadPool(nbThreads);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} 
	};
}
