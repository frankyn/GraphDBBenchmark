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

import com.silvertower.app.bench.akka.Messages.TimeResult;
import com.silvertower.app.bench.akka.Messages.*;
import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.workload.Workload;

public class MasterClient extends UntypedActor {
	private final int slavesAvailable = 4; // TODO: change this
	private GraphDescriptor currentGDesc;
	private List<SlaveReference> slaves;
	private int coresAvailable;
	private enum State {WAITING_FOR_INFOS, READY_FOR_WORK, WORKING};
	private State state;
	private ActorRef resultsListener;
	
	public MasterClient(ActorRef listener) {
		resultsListener = listener;
	}
	
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
			if (state == State.WORKING) {
				resultsListener.tell(new Messages.Error("Error: the clients are still working!"));
			}
			else {
				currentGDesc = ((GDesc) message).getGraphDesc();
				state = State.READY_FOR_WORK;
			}
		}
		
		else if (message instanceof Work) {
			if (state != State.READY_FOR_WORK) {
				resultsListener.tell(new Messages.Error("Error: the master client is not ready for work!"));
			}
			
			else {
				int nCores = ((Work) message).getHowManyClients();
				int nOp = ((Work) message).getHowManyOp();
				if (nCores > coresAvailable) {
					resultsListener.tell(new Messages.Error("Error: not enough cores/slaves available!"));
					return;
				}
				state = State.WORKING;
				Workload w = ((Work) message).getWork();
				
				if (w.isMT()) {
					// If the workload is multi threaded, the master client sends the workload to 
					// slaves clients if necessary.
					// OPTIMIZATION: the master client also does the work.
					assignWork(w, nOp, nCores);
				}
				
				else {
					// If the workload is single threaded, the work is directly done by a single thread from
					// the master client.
					double[] t = w.work(currentGDesc);
					resultsListener.tell(new TimeResult(t[0], t[1]));
					state = State.READY_FOR_WORK;
				}
			}
		}
		
		else if (message instanceof TimeResult) {
			ActorRef sender = getSender();
			for (SlaveReference s: slaves) {
				if (s.getSlaveRef().equals(sender)) {
					s.setResultReceived((TimeResult) message);
				}
			}
			AggregateResult r = aggregateResult();
			if (r != null) {
				resultsListener.tell(r);
				resetState();
				state = State.READY_FOR_WORK;
			}
		}
		
		else {
			unhandled(message);
		}
	}
	
	private AggregateResult aggregateResult() {
		AggregateResult r = new AggregateResult();
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

	private void assignWork(Workload workload, int totalNbrOp, int nCores) {
		int remainingCoresNeeded = nCores;
		int nbrSlavesNeeded = 0;
		for (SlaveReference s: slaves) {
			if (remainingCoresNeeded <= 0) break;
			else {
				remainingCoresNeeded -= s.getNbCoresAvailable();
				nbrSlavesNeeded++;
			}
		}
		
		int nbrOpPerSlave = totalNbrOp / nbrSlavesNeeded;
		for (int i = 0; i < nbrSlavesNeeded; i++) {
			SlaveReference s = slaves.get(i);
			int coresAvailable = s.getNbCoresAvailable();
			int coresUsed = 0;
			if (coresAvailable > nCores) coresUsed = nCores;
			else coresUsed = coresAvailable;
			Work w = new Work(workload, nbrOpPerSlave, coresUsed);
			s.getSlaveRef().tell(w);
			s.setWorking();
			nCores -= coresUsed;
		}
	}

	private int createNewSlave(final int id) {
		// TODO: check if we have enough vms remaining
		Props p = new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				return new SlaveClient(id);
				}
			}
		);
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
	
	private void resetState() {
		for (SlaveReference s: slaves) {
			s.unsetWorking();
			s.setResultReceived(null);
		}
	}
}
