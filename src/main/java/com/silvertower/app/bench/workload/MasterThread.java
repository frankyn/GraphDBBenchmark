package com.silvertower.app.bench.workload;

import com.silvertower.app.bench.dbinitializers.*;
import com.silvertower.app.bench.main.Globals;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import com.tinkerpop.blueprints.Graph;

public class MasterThread extends Thread {
	private Graph g;
	private GraphDescriptor gDesc;
	private Class slaveThreadsClass;
	private ArrayList<SlaveThread> slaves;
	private ArrayList<ResultPair> throughputs;
	private ArrayList<ResultPair> latencies;
	private static final int initialNbThreads = 1;
	private static final int additionalNbThreadsPerStep = 1;
	private static final int maxNbThreads = 100;
	private static final int numberOfSeconds = 10;
	private static final long roundTime = numberOfSeconds * Globals.nanosToSFactor;
	
	public MasterThread(Graph g, GraphDescriptor gDesc, Class slaveThreadsClass) {
		this.g = g;
		this.gDesc = gDesc;
		this.slaveThreadsClass = slaveThreadsClass;
		this.slaves = new ArrayList<SlaveThread>(initialNbThreads);
		this.throughputs = new ArrayList<ResultPair>();
		this.latencies = new ArrayList<ResultPair>();
	}

	public void run() {
		measureConcurrency();
	}

	private void measureConcurrency() {
		long roundTimePerThread;
		for (int i = initialNbThreads; i <= maxNbThreads; i += additionalNbThreadsPerStep) {
			System.out.println(i);
			long before = System.currentTimeMillis();
			roundTimePerThread = roundTime / i;
			createNewSlaves(i, roundTimePerThread);
			runSlaves();
			waitRoundEnd();
			System.out.println("After:" + (System.currentTimeMillis() - before) / (1000 * 1.0));
			System.out.println("Throughput: " + getTotalOpCount() / numberOfSeconds);
			System.out.println("Latency: " + getAverageLatency());
			throughputs.add(new ResultPair(i, getTotalOpCount() / numberOfSeconds));
			latencies.add(new ResultPair(i, getAverageLatency()));
		}
	}

	private double getAverageLatency() {
		double totalLatency = 0;
		for (int i = 0; i < slaves.size(); i++) {
			totalLatency += slaves.get(i).getLatency();
		}
		return totalLatency / slaves.size();
	}

	private void waitRoundEnd() {
		for (int i = 0; i < slaves.size(); i++) {
			try {
				System.out.println("Blocked");
				slaves.get(i).join();
				System.out.println("Unblocked");
			} catch (InterruptedException e) {}
		}
	}

	private long getTotalOpCount() {
		long totalOpCount = 0;
		for (int i = 0; i < slaves.size(); i++) {
			totalOpCount += slaves.get(i).getOpCount();
		}
		return totalOpCount;
	}
	
	private void runSlaves() {
		for (int i = 0; i < slaves.size(); i++) {
			slaves.get(i).start();
		}
	}
	
	private void createNewSlaves(int nbThreads, long roundTimePerThread) {
		slaves = new ArrayList<SlaveThread>(nbThreads);
		Constructor workersConstructor = null;
		for (int i = 1; i <= nbThreads; i++) {
			try {
				workersConstructor = slaveThreadsClass.getConstructor(Graph.class, GraphDescriptor.class, Long.TYPE, Integer.TYPE);
				SlaveThread t = (SlaveThread) workersConstructor.newInstance(g, gDesc, roundTimePerThread, i);
				slaves.add(t);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		gDesc.setNbConcurrentThreads(nbThreads);
	}
	
	public ArrayList<ResultPair> getLatencyResults() {
		return latencies;
	}

	public ArrayList<ResultPair> getThroughputResults() {
		return throughputs;
	}
}


/*
	private static int initialNbOperationsAssigned = 10000;
	private static int additionalNbOperationsAssigned = 100000;
	
	private void assignOperations(int totalNbOperation, int nbThreads) {
		int nbOpPerThread = (int) Math.floor(totalNbOperation*1.0/nbThreads);
		for (int i = 0; i < slaves.size(); i++) {
			slaves.get(i).setOperationsPerRound(nbOpPerThread);
		}
	}

	private void stopSlaves() {
		for (int i = 0; i < slaves.size(); i++) {
			try {
				slaves.get(i).stopThread();
				synchronized(slaves.get(i)) {
					slaves.get(i).notify();
				}
				slaves.get(i).join();
			} catch (InterruptedException e) {}
		}
	}
	
	private void resetTotalOpCount() {
		for (int i = 0; i < slaves.size(); i++) {
			slaves.get(i).resetOpCount();
		}
	}
*/	
