package com.silvertower.app.bench.akka;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import akka.actor.ActorRef;
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
	private Work currentWork;
	public SlaveClient(int id) {
		this.id = id;
		this.individualClients = new ArrayList<SlaveThread>();
		this.nbrCores = Runtime.getRuntime().availableProcessors();
		this.state = State.WAITING_FOR_INFOS;
	}
	
	public void onReceive(Object message) throws Exception {
		ActorRef master = getSender();
		
		if (message instanceof GetNbCores) {
			master.tell(new Integer(nbrCores), getSelf());
		}
		
		else if (message instanceof GDesc) {
			currentGDesc = ((GDesc) message).getGraphDesc();
			state = State.READY_FOR_WORK;
		}
		
		else if (message instanceof Work) {
			if (state != State.READY_FOR_WORK) {
				master.tell(new Messages.Error("Error: client is not ready yet!"), getSelf());
				return;
			}
			currentWork = (Work) message;
			createClientThreads();
			state = State.WORK_RECEIVED;
		}
		
		else if (message instanceof StartWork) {
			if (state != State.WORK_RECEIVED) {
				master.tell(new Messages.Error("Error: client first needs work!"), getSelf());
				return;
			}
			state = State.WORKING;
			long before = System.nanoTime();
			startLatch.countDown();
			stopLatch.await();
			long after = System.nanoTime();
			double meanCPUTime = getMeanCPUTime();
			master.tell(new TimeResult((after - before) / 1000000000.0, meanCPUTime), getSelf());
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
		return (total * 1.0) / individualClients.size() / 1000000000;
	}

	public void createClientThreads() {
		int nbrCoresWanted = currentWork.getHowManyClients();
		int nbrOpWanted = currentWork.getHowManyOp();
		Workload w = currentWork.getWorkload(); 
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
