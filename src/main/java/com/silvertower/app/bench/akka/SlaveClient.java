package com.silvertower.app.bench.akka;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import akka.actor.UntypedActor;

import com.silvertower.app.bench.akka.Messages.*;
import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.workload.SlaveThread;
import com.silvertower.app.bench.workload.Workload;

public class SlaveClient extends UntypedActor {
	private int id;
	private enum State {WAITING_FOR_INFOS, READY_FOR_WORK, WORK_RECEIVED, WORKING};
	private State state;
	private GraphDescriptor currentGDesc;
	private List<SlaveThread> individualClients;
	private int nbrCores;
	private CountDownLatch startLatch;
	private CountDownLatch stopLatch;
	public SlaveClient(int id) {
		this.id = id;
		this.individualClients = new ArrayList<SlaveThread>();
		this.nbrCores = Runtime.getRuntime().availableProcessors();
		this.state = State.WAITING_FOR_INFOS;
	}
	
	public void onReceive(Object message) throws Exception {
		if (message instanceof GetNbCores) {
			getSender().tell(new Integer(nbrCores));
		}
		
		else if (message instanceof GDesc) {
			if (state == State.WORKING) {
				getSender().tell(new Messages.Error("Error: client still working!"));
			}
			else {
				currentGDesc = ((GDesc) message).getGraphDesc();
				state = State.READY_FOR_WORK;
			}
		}
		
		else if (message instanceof Work) {
			state = State.WORKING;
			if (state == State.WORKING) {
				getSender().tell(new Messages.Error("Error: client still working!"));
			}
			else {
				int nbrCoresWanted = ((Work) message).getHowManyClients();
				int nbrOpWanted = ((Work) message).getHowManyOp();
				Workload w = ((Work) message).getWork();
				createClientThreads(nbrCoresWanted, nbrOpWanted, w);
				state = State.WORK_RECEIVED;
			}
		}
		
		else if (message instanceof StartWork) {
			long before = System.nanoTime();
			startLatch.countDown();
			stopLatch.await();
			long after = System.nanoTime();
			double meanCPUTime = getMeanCPUTime();
			getSender().tell(new TimeResult((after - before / 1000000000.0), meanCPUTime));
			state = State.READY_FOR_WORK;
		}
		
		else {
			unhandled(message);
		}
	}
	
	private double getMeanCPUTime() {
		long total = 0;
		for (SlaveThread t: individualClients) {
			total += t.getTimeSpentCPU();
		}
		return ((total * 1.0) / 1000000000.0) / individualClients.size();
	}

	public void createClientThreads(int nbrCoresWanted, int nbrOpWanted, Workload w) {
		startLatch = new CountDownLatch(1);
		stopLatch = new CountDownLatch(nbrCoresWanted);
		individualClients = new ArrayList<SlaveThread>();
		int nbrOpPerThread = nbrOpWanted / nbrCoresWanted;
		for (int i = 0; i < nbrCoresWanted; i++) {
			SlaveThread t = new SlaveThread(currentGDesc, id + i, w, nbrOpPerThread, startLatch, stopLatch);
			individualClients.add(t);
			t.start();
		}
	}
}
