package com.silvertower.app.bench.akka;


import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.dispatch.Await;
import akka.dispatch.Future;
import akka.util.Duration;
import akka.util.Timeout;
import static akka.pattern.Patterns.ask;

import com.silvertower.app.bench.akka.Messages.*;
import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.utils.Utilities;
import com.silvertower.app.bench.workload.Workload;

public class MasterClient extends UntypedActor {
	private final int slavesAvailable = 1; // TODO: change this
	private GraphDescriptor currentGDesc;
	private List<SlaveReference> slaves;
	private int coresAvailable;
	private enum State {WAITING_FOR_INFOS, READY_FOR_WORK, WORKING};
	private State state;
	private ActorRef resultsListener;
	private Work currentWork;
	
	public void preStart() {
		slaves = new ArrayList<SlaveReference>();
		// Create the slaves actors
		for (int i = 1; i <= slavesAvailable; i++) {
			int coresAdded = createNewSlave(coresAvailable + 1);
			coresAvailable += coresAdded;
		}
		state = State.WAITING_FOR_INFOS;
	}
	
	public void onReceive(Object message) throws Exception {
		
		if (message instanceof GDesc) {
			currentGDesc = ((GDesc) message).getGraphDesc();
			state = State.READY_FOR_WORK;
		}
		
		else if (message instanceof Work) {
			if (state != State.READY_FOR_WORK) {
				resultsListener.tell(new Messages.Error("Error: the master client is not ready for work!"), getSelf());
				return;
			}
			
			if (resultsListener == null) resultsListener = getSender();
			
			int nCores = ((Work) message).getHowManyClients();
			if (nCores > coresAvailable) {
				resultsListener.tell(new Messages.Error("Error: not enough cores/slaves available!"), getSelf());
				return;
			}
			
			currentWork = (Work) message;
			
			state = State.WORKING;
			if (((Work) message).getWorkload().isMT()) {
				assignWork();
				startWork();
			}
			
			else {
				// If the workload is single threaded, the work is directly done by a single thread from
				// the master client.
				benchWorkload(((Work) message).getWorkload());
				state = State.READY_FOR_WORK;
			}
		}
		
		else if (message instanceof TimeResult) {
			for (SlaveReference s: slaves) {
				if (s.getSlaveRef().equals(getSender())) {
					s.setResultReceived((TimeResult) message);
				}
			}
			AggregateResult r = aggregateResult();
			if (r != null) {
				resultsListener.tell(r, getSelf());
				resetState();
				state = State.READY_FOR_WORK;
			}
		}
		
		else {
			unhandled(message);
		}
	}
	
	private void benchWorkload(final Workload w) {
		Runnable task = 
				new Runnable() { public void run() { w.operation(currentGDesc); } };
		double[] times = Utilities.benchTask(task);
		resultsListener.tell(new TimeResult(times[0], times[1], currentWork), getSelf());
	}

	private int createNewSlave(final int id) {
		// TODO: check if we have enough vms remaining
		Props p = new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				return new SlaveClient(id);
			}
		});
		ActorRef slave = this.getContext().actorOf(p, "slave" + id);
		
		// We synchronously ask the slave the number of cores it has.
		Timeout timeout = new Timeout(Duration.create(5, "seconds"));
		Future<Object> answer = ask(slave, new GetNbCores(), timeout);
		int nCores = 0;
		try {
			nCores = (Integer) Await.result(answer, timeout.duration());
		} catch (Exception e) {
			e.printStackTrace();
		}
		slaves.add(new SlaveReference(slave, nCores));
		
		return nCores;
	}

	private void assignWork() {
		int nCores = currentWork.getHowManyClients();
		
		int nbrSlavesNeeded = 0;
		int remainingCoresNeeded = nCores;
		for (SlaveReference s: slaves) {
			if (remainingCoresNeeded <= 0) break;
			else {
				remainingCoresNeeded -= s.getNbCoresAvailable();
				nbrSlavesNeeded++;
			}
		}
		
		int nbrOpPerSlave = currentWork.getHowManyOp() / nbrSlavesNeeded;
		currentGDesc.setNbConcurrentThreads(nCores);
		for (int i = 0; i < nbrSlavesNeeded; i++) {
			SlaveReference slave = slaves.get(i);
			int coresAvailable = slave.getNbCoresAvailable();
			int coresUsed = 0;
			if (coresAvailable > nCores) coresUsed = nCores;
			else coresUsed = coresAvailable;
			Work w = new Work(currentWork.getWorkload(), nbrOpPerSlave, coresUsed);
			slave.getSlaveRef().tell(new GDesc(currentGDesc), getSelf());
			slave.getSlaveRef().tell(w, getSelf());
			slave.setWorking();
			nCores -= coresUsed;
		}
	}
	
	private void startWork() {
		for (SlaveReference s: slaves) {
			if (s.isWorking()) s.getSlaveRef().tell(new StartWork(), getSelf());
		}
	}
	
	private void resetState() {
		for (SlaveReference s: slaves) {
			s.unsetWorking();
			s.setResultReceived(null);
		}
	}

	private AggregateResult aggregateResult() {
		AggregateResult r = new AggregateResult(currentWork);
		for (SlaveReference s: slaves) {
			if (s.isWorking()) {
				if (s.getResultReceived() != null) {
					r.addTime(s.getResultReceived());
				}
				else return null;
			}
		}
		return r;
	}
}