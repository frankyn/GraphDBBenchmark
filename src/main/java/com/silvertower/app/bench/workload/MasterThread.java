package com.silvertower.app.bench.workload;

import com.silvertower.app.bench.dbinitializers.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import com.tinkerpop.blueprints.Graph;

public class MasterThread extends Thread {
	private Graph g;
	private GraphDescriptor gDesc;
	private Class slaveThreadsClass;
	private ArrayList<SlaveThread> slaves;
	private static final int initialNbThreads = 10;
	private static final int additionalNbThreadsPerStep = 5;
	private static final int sleepTime = 1000;
	
	public MasterThread(Graph g, GraphDescriptor gDesc, Class slaveThreadsClass) {
		this.g = g;
		this.gDesc = gDesc;
		this.slaveThreadsClass = slaveThreadsClass;
		this.slaves = new ArrayList<SlaveThread>(initialNbThreads);
	}

	public void run() {
		createNewSlaves(initialNbThreads);
		
		boolean thresholdReached = false;
		int previousTotalOpCount = 0;
		
		runSlaves();
		
		while (!thresholdReached) {
			try {
				sleep(sleepTime);
			} catch (InterruptedException e) {}
			
			sleepSlaves();
			
			int currentTotalOpCount = getTotalOpCount(); 
			
			System.out.println(currentTotalOpCount);
			
			if (currentTotalOpCount <= previousTotalOpCount) {
				thresholdReached = true;
				stopSlaves();
			}
			else {
				previousTotalOpCount = currentTotalOpCount;
				createNewSlaves(additionalNbThreadsPerStep);
				resumeSlaves();
			}
		}
	}
	
	private void stopSlaves() {
		resumeSlaves();
		for (int i = 0; i < slaves.size(); i++) {
			try {
				slaves.get(i).stopThread();
				slaves.get(i).join();
			} catch (InterruptedException e) {}
		}
	}

	private void runSlaves() {
		for (int i = 0; i < slaves.size(); i++) {
			slaves.get(i).startThread();
		}
	}
	
	private void sleepSlaves() {
		for (int i = 0; i < slaves.size(); i++) {
			slaves.get(i).sleepThread();
		}
	}
	
	private int getTotalOpCount() {
		int totalOpCount = 0;
		for (int i = 0; i < slaves.size(); i++) {
			totalOpCount += slaves.get(i).getAndResetOpCount();
		}
		return totalOpCount;
	}
	
	private void resumeSlaves() {
		for (int i = 0; i < slaves.size(); i++) {
			if (slaves.get(i).isAlive()) {
				slaves.get(i).resumeWork();
				synchronized(slaves.get(i)) {
					slaves.get(i).notify();
				}
			}
			else {
				slaves.get(i).startThread();
			}
		}
	}
	
	private void createNewSlaves(int number) {
		Constructor workersConstructor = null;
		
		for (int i = 0; i < number; i++) {
			try {
				workersConstructor = slaveThreadsClass.getConstructor(Graph.class, GraphDescriptor.class);
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			
			SlaveThread t = null;
			try {
				t = (SlaveThread) workersConstructor.newInstance(g, gDesc);
				slaves.add(t);
				
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
}
