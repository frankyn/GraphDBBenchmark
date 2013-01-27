package com.silvertower.app.bench.workload;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import com.tinkerpop.blueprints.Graph;

public class MasterThread extends Thread {
	private Graph g;
	private Class slaveThreadsClass;
	private ArrayList<SlaveThread> slaves;
	private static final int initialNbThreads = 10;
	private static final int sleepTime = 10000;
	
	public MasterThread(Graph g, DatasetsGenerator, Class slaveThreadsClass) {
		this.g = g;
		this.slaveThreadsClass = slaveThreadsClass;
		this.slaves = new ArrayList<SlaveThread>(initialNbThreads);
	}

	public void run() {
		for (int i = 0; i < slaves.size(); i++) {
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
			if (currentTotalOpCount <= previousTotalOpCount) {
				thresholdReached = true;
			}
			else {
				previousTotalOpCount = currentTotalOpCount;
				SlaveThread t = createNewSlave();
				t.stopWork();
				t.start();
			}
			resumeSlaves();
		}
	}
	
	private int getTotalOpCount() {
		int total = 0;
		for (int i = 0; i < slaves.size(); i++) {
			total += slaves.get(i).getOpCount();
		}
		return total;
	}
	
	private void runSlaves() {
		for (int i = 0; i < slaves.size(); i++) {
			slaves.get(i).start();
		}
	}
	
	private void stopSlaves() {
		for (int i = 0; i < slaves.size(); i++) {
			slaves.get(i).stopWork();
		}
	}
	
	private void resumeSlaves() {
		for (int i = 0; i < slaves.size(); i++) {
			slaves.get(i).resumeWork();
			slaves.get(i).notify();
		}
	}
	
	private SlaveThread createNewSlave() {
		Constructor workersConstructor = null;
		
		try {
			workersConstructor = slaveThreadsClass.getConstructor(Integer.class, Graph.class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		SlaveThread t = null;
		try {
			t = (SlaveThread) workersConstructor.newInstance(g);
			slaves.add(t);
			
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return t;
	}

}
