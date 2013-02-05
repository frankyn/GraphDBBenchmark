package com.silvertower.app.bench.workload;

import com.silvertower.app.bench.dbinitializers.*;
import com.silvertower.app.bench.main.Globals;
import com.silvertower.app.bench.utils.Utilities;
import com.silvertower.app.bench.workload.SlaveThread.Type;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import com.tinkerpop.blueprints.Graph;

public class MasterThread extends Thread {
	private Graph g;
	private GraphDescriptor gDesc;
	private Class slaveThreadsClass;
	private ArrayList<SlaveThread> slaves;
	private static final int initialNbThreads = 100;
	private static final int additionalNbThreadsPerStep = 1;
	private static final int maxNbThreads = 200;
	
	public MasterThread(Graph g, GraphDescriptor gDesc, Class slaveThreadsClass) {
		this.g = g;
		this.gDesc = gDesc;
		this.slaveThreadsClass = slaveThreadsClass;
		this.slaves = new ArrayList<SlaveThread>(initialNbThreads);
	}

	public void run() {
		measureConcurrency();
	}

	private void measureConcurrency() {
		long roundTime = 1 * Globals.nanosToSFactor;
		long roundTimePerThread;
		
		for (int i = initialNbThreads; i <= maxNbThreads; i += additionalNbThreadsPerStep) {
			roundTimePerThread = roundTime / i;
			createNewSlaves(i, Type.CONCURRENCY, roundTimePerThread);
			runSlaves();
			waitRoundEnd();
			
			long currentTotalOpCount = getTotalOpCount(); 
			double averageLatency = getAverageLatency();
			
			Utilities.log("For " + i + " concurrent thread, throughput", currentTotalOpCount);
			Utilities.log("For " + i + " concurrent thread, latency (op/s)", averageLatency);
		}
	}

	private double getAverageLatency() {
		double totalLatency = 0;
		for (int i = 0; i < slaves.size(); i++) {
			totalLatency += slaves.get(i).getLatency();
		}
		return totalLatency * Globals.nanosToSFactor / slaves.size();
	}

	private void waitRoundEnd() {
		for (int i = 0; i < slaves.size(); i++) {
			try {
				slaves.get(i).join(5000);
				if (slaves.get(i).isAlive()) {
					System.out.println("Killed mechantely");
					slaves.get(i).interrupt();
				}
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
	
	private void createNewSlaves(int nbThreads, Type type, long roundTimePerThread) {
		slaves = new ArrayList<SlaveThread>(nbThreads);
		Constructor workersConstructor = null;
		for (int i = 0; i < nbThreads; i++) {
			try {
				workersConstructor = slaveThreadsClass.getConstructor(Graph.class, GraphDescriptor.class, Type.class, Long.TYPE);
				SlaveThread t = (SlaveThread) workersConstructor.newInstance(g, gDesc, type, roundTimePerThread);
				slaves.add(t);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
