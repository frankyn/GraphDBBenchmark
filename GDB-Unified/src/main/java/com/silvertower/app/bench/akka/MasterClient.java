package com.silvertower.app.bench.akka;


import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Address;
import akka.actor.Deploy;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.dispatch.Await;
import akka.dispatch.Future;
import akka.remote.RemoteScope;
import akka.util.Duration;
import akka.util.Timeout;
import static akka.pattern.Patterns.ask;

import com.silvertower.app.bench.akka.Messages.*;
import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.utils.Utilities;
import com.silvertower.app.bench.workload.IntensiveWorkload;
import com.silvertower.app.bench.workload.TraversalWorkload;

public class MasterClient extends UntypedActor {
	private int slavesAvailable;
	private GraphDescriptor currentGDesc;
	private List<SlaveReference> slaves;
	private int coresAvailable;
	private enum State {WAITING_FOR_INFOS, READY_FOR_WORK, WORKING};
	private State state;
	private ActorRef resultsListener;
	private Timeout t = new Timeout(Duration.create(3600, "seconds"));
	public MasterClient(String[] slavesInfos) {
		this.slavesAvailable = slavesInfos.length/2;
		this.slaves = new ArrayList<SlaveReference>(slavesAvailable);
		// Create the slave (workers)
		for (int i = 1; i <= slavesAvailable; i++) {
			String ipAddress = slavesInfos[(i-1)*2];
			int port = Integer.parseInt(slavesInfos[((i-1)*2) + 1]);
			Address add = new Address("akka", "SCNode", ipAddress, port);
			int coresAdded = createNewSlave(coresAvailable + 1, add);
			coresAvailable += coresAdded;
		}
		state = State.WAITING_FOR_INFOS;
	}
	
	public void onReceive(Object message) throws Exception {
		System.out.println("Master:" + message);
		if (message instanceof GraphDescriptor) {
			currentGDesc = (GraphDescriptor) message;
			currentGDesc.setNbConcurrentThreads(coresAvailable);
			assignGDesc();
			currentGDesc.fetchGraph();
			currentGDesc.scanDB();
			state = State.READY_FOR_WORK;
		}
		
		else if (message instanceof IntensiveWork) {
			if (state != State.READY_FOR_WORK) {
				forwardError("Error: the master client is not ready for work!");
				return;
			}
			
			resultsListener = getSender();
			
			IntensiveWork work = (IntensiveWork) message;
			int nCores = work.getHowManyClients();
			int nOps = work.getHowManyOp();
			IntensiveWorkload workload = work.getWorkload();
			
			if (nCores > coresAvailable) {
				forwardError("Error: not enough cores/slaves available!");
				return;
			}
			
			state = State.WORKING;
			assignWork(nCores, nOps, workload);
			startWork();
		}
		
		else if (message instanceof TraversalWork) {
			System.out.println("work:"+getSender());
			if (state != State.READY_FOR_WORK) {
				forwardError("Error: the master client is not ready for work!");
				return;
			}
			resultsListener = getSender();
			
			state = State.WORKING;
			benchWorkload(((TraversalWork) message).getWorkload());
			state = State.READY_FOR_WORK;
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
		
		else if (message instanceof Messages.Error) {
			forwardError((Messages.Error) message);
		}
		
		else {
			unhandled(message);
		}
	}
	
	private void benchWorkload(final TraversalWorkload w) {
		Runnable task = 
				new Runnable() { 
					public void run() { 
						w.operation(currentGDesc); 
					} 
				};
		double[] times = Utilities.benchTask(task);
		resultsListener.tell(new TimeResult(times[0], times[1]), getSelf());
	}

	private int createNewSlave(final int id, final Address add) {
		// TODO: check if we have enough vms remaining
		@SuppressWarnings("serial")
		Props prop = new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				return new SlaveClient(id);
			}
		});
		ActorRef slave = getContext().actorOf(prop.withDeploy(new Deploy(new RemoteScope(add))));
		
		// We ask the slave for how many cores it has
		int nCores = 0;
		Object answer = askAndWait(slave, new GetNbCores());
		if (!(answer == null)) {
			nCores = (Integer) answer;
			slaves.add(new SlaveReference(slave, nCores));
		}
		return nCores;
	}
	
	private void assignGDesc() {
		// Share the graph descriptor with all the slaves
		for (SlaveReference s: slaves) {
			// We wait until this slave ack the reception of the graph descriptor
			askAndWait(s.getSlaveRef(), currentGDesc);
		}
	}

	private void assignWork(int nCores, int nOps, IntensiveWorkload workload) {
		int nbrSlavesNeeded = 0;
		int remainingCoresNeeded = nCores;
		for (SlaveReference s: slaves) {
			if (remainingCoresNeeded <= 0) break;
			else {
				remainingCoresNeeded -= s.getNbCoresAvailable();
				nbrSlavesNeeded++;
			}
		}
		
		int nbrOpPerSlave = nOps / nbrSlavesNeeded;
		
		// Assign the work to as much slaves as necessary
		for (int i = 0; i < nbrSlavesNeeded; i++) {
			SlaveReference slave = slaves.get(i);
			int coresAvailable = slave.getNbCoresAvailable();
			int coresUsed = coresAvailable > nCores ? nCores : coresAvailable;
			IntensiveWork work = new IntensiveWork(workload, nbrOpPerSlave, coresUsed);
			askAndWait(slave.getSlaveRef(), work);
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
		AggregateResult r = new AggregateResult();
		for (SlaveReference s: slaves) {
			if (s.isWorking()) {
				if (s.getResultReceived() != null) r.addTime(s.getResultReceived());
				else return null;
			}
		}
		return r;
	}
	
	private void forwardError(String errorMessage) {
		resultsListener.tell(new Messages.Error(errorMessage), getSelf());
	}
	
	private void forwardError(Messages.Error error) {
		resultsListener.tell(error, getSelf());
	}
	
	private Object askAndWait(ActorRef dest, Object message) {
		try {
			// We wait for the answer
			Future<Object> future = ask(dest, message, t);
			Object answer = Await.result(future, t.duration());
			if (answer instanceof Messages.Error) {
				forwardError((Messages.Error) answer);
				return null;
			}
			else return answer;
		} catch(Exception e) {
			// We normally never reach this point
			e.printStackTrace();
			return null;
		}
	}
}