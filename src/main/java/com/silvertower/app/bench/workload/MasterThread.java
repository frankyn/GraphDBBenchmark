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
	private static final int initialNbThreads = 1;
	private static final int sleepTime = 3000;
	
	public MasterThread(Graph g, GraphDescriptor gDesc, Class slaveThreadsClass) {
		this.g = g;
		this.gDesc = gDesc;
		this.slaveThreadsClass = slaveThreadsClass;
		this.slaves = new ArrayList<SlaveThread>(initialNbThreads);
	}

	public void run() {
		for (int i = 0; i < initialNbThreads; i++) {
			createNewSlave();
		}
		
		boolean thresholdReached = false;
		int previousTotalOpCount = 0;
		
		runSlaves();
		
		while (!thresholdReached) {
			try {
				sleep(sleepTime);
			} catch (InterruptedException e) {}
			stopSlaves();
			
			int currentTotalOpCount = getTotalOpCount();
			System.out.println(currentTotalOpCount);
			
			if (currentTotalOpCount <= previousTotalOpCount) {
				thresholdReached = true;
			}
			else {
				previousTotalOpCount = currentTotalOpCount;
				createNewSlave();
			}
			
			resumeSlaves();
		}
	}
	
	private void runSlaves() {
		for (int i = 0; i < slaves.size(); i++) {
			slaves.get(i).activate();
		}
	}
	
	private void stopSlaves() {
		for (int i = 0; i < slaves.size(); i++) {
			slaves.get(i).stopWork();
		}
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
				slaves.get(i).activate();
			}
		}
	}
	
	private int getTotalOpCount() {
		int total = 0;
		for (int i = 0; i < slaves.size(); i++) {
			total += slaves.get(i).getOpCount();
		}
		return total;
	}
	
	private SlaveThread createNewSlave() {
		Constructor workersConstructor = null;
		
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
		
		return t;
	}

}
