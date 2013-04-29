package com.silvertower.app.bench.akka;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.silvertower.app.bench.akka.Messages.*;
import com.silvertower.app.bench.workload.IntensiveWorkload;

public class SlaveClient extends UntypedActor {
	private int id;
	private enum State {WAITING_FOR_INFOS, READY_FOR_WORK, WORK_RECEIVED, WORKING};
	private State state;
	private GraphDescriptor currentGDesc;
	private List<SlaveThread> clientThreads;
	private CountDownLatch startLatch;
	private CountDownLatch stopLatch;
	private IntensiveWork currentWork;
	private ActorRef master;
	public SlaveClient() {
		this.clientThreads = new ArrayList<SlaveThread>();
		this.state = State.WAITING_FOR_INFOS;
	}
	
	public void onReceive(Object message) throws Exception {
		System.out.println("Slave:" + message);
		master = getSender();
		if (message instanceof SlaveInitialization) {
			this.id = ((SlaveInitialization) message).getId();
			System.out.println(String.format("I have %d cores available", Runtime.getRuntime().availableProcessors()));
			master.tell(new Integer(Runtime.getRuntime().availableProcessors()), getSelf());
		}
		
		else if (message instanceof GraphDescriptor) {
			currentGDesc = (GraphDescriptor) message;
			currentGDesc.fetchGraph();
			state = State.READY_FOR_WORK;
			master.tell(new Ack());
		}
		
		else if (message instanceof IntensiveWork) {
			if (state != State.READY_FOR_WORK) {
				forwardError("Error: client is not ready yet!");
				return;
			}
			currentWork = (IntensiveWork) message;
			createAndStartClientThreads();
			state = State.WORK_RECEIVED;
			master.tell(new Ack());
		}
		
		else if (message instanceof StartWork) {
			if (state != State.WORK_RECEIVED) {
				forwardError("Error: client first needs work!");
				return;
			}
			state = State.WORKING;
			
			startLatch.countDown();
			stopLatch.await();
			
			master.tell(new Ack(), getSelf());
			state = State.READY_FOR_WORK;
		}
		
		else {
			unhandled(message);
		}
	}
	
	private void createAndStartClientThreads() {
		int nbrCoresWanted = currentWork.getHowManyClients();
		int nbrOpWanted = currentWork.getHowManyOp();
		IntensiveWorkload w = currentWork.getWorkload();
		startLatch = new CountDownLatch(1);
		stopLatch = new CountDownLatch(nbrCoresWanted);
		clientThreads = new ArrayList<SlaveThread>();
		int nbrOpPerThread = nbrOpWanted / nbrCoresWanted;
		for (int i = 0; i < nbrCoresWanted; i++) {
			SlaveThread t = new SlaveThread(currentGDesc, id + i, w, nbrOpPerThread, startLatch, 
					stopLatch, currentWork.isBatchMode());
			clientThreads.add(t);
			t.start();
		}
	}
	
	private void forwardError(String errorMessage) {
		master.tell(new Messages.Error(errorMessage), getSelf());
	}
	
	public void postStop() {
		System.exit(-1);
	}
}
