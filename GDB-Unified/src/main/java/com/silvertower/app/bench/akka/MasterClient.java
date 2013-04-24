package com.silvertower.app.bench.akka;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Address;
import akka.actor.Deploy;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dispatch.Await;
import akka.dispatch.Future;
import akka.remote.RemoteScope;
import akka.util.Duration;
import akka.util.Timeout;
import static akka.pattern.Patterns.ask;

import com.silvertower.app.bench.akka.Messages.*;
import com.silvertower.app.bench.main.ClientProperties;
import com.silvertower.app.bench.workload.IntensiveWorkload;
import com.silvertower.app.bench.workload.TraversalWorkload;
import com.tinkerpop.blueprints.Vertex;

public class MasterClient extends UntypedActor {
	private int slavesAvailable;
	private GraphDescriptor currentGDesc;
	private List<SlaveReference> slaves;
	private int coresAvailable;
	private int numberOfWorkingSlaves;
	private enum State {WAITING_FOR_INFOS, READY_FOR_WORK, WORKING};
	private State state;
	private ActorRef resultsListener;
	private Timeout t = new Timeout(Duration.create(Integer.MAX_VALUE, "seconds"));
	private List<Ack> ackBuffer;
	private long intensiveWorkloadStartTs;
	public MasterClient(String[] slavesInfos) {
		this.slavesAvailable = slavesInfos.length/2;
		this.slaves = new ArrayList<SlaveReference>(slavesAvailable);
		this.ackBuffer = new ArrayList<Ack>();
		// Create the slave (workers)
		for (int i = 1; i <= slavesAvailable; i++) {
			String ipAddress = slavesInfos[(i-1)*2];
			int port = Integer.parseInt(slavesInfos[((i-1)*2) + 1]);
			Address add = new Address("akka", "SCNode", ipAddress, port);
			int coresAdded = createNewSlave(coresAvailable + 1, add);
			coresAvailable += coresAdded;
		}
		state = State.WAITING_FOR_INFOS;
		ClientProperties.initializeProperties();
	}
	
	public void onReceive(Object message) throws Exception {
		System.out.println("Master:" + message);
		if (message instanceof GraphDescriptor) {
			currentGDesc = (GraphDescriptor) message;
			currentGDesc.setNbConcurrentThreads(coresAvailable);
			assignGDesc();
			currentGDesc.fetchGraph();
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
			intensiveWorkloadStartTs = System.nanoTime();
			startWork();
			System.out.println("Starting intensive work");
		}
		
		else if (message instanceof TraversalWork) {
			if (state != State.READY_FOR_WORK) {
				forwardError("Error: the master client is not ready for work!");
				return;
			}
			resultsListener = getSender();
			
			state = State.WORKING;
			System.out.println("Starting traversal work");
			resultsListener.tell(benchWorkload(((TraversalWork) message).getWorkload()), getSelf());
			System.out.println("Traversal work complete");
			state = State.READY_FOR_WORK;
		}
		
		else if (message instanceof Ack) {
			ackBuffer.add((Ack) message);
			if (ackBuffer.size() == numberOfWorkingSlaves) {
				long intensiveWorkloadEndTs = System.nanoTime();
				TimeResult r = new TimeResult( (intensiveWorkloadEndTs - intensiveWorkloadStartTs) / 1000000000.0);
				resultsListener.tell(r, getSelf());
				System.out.println("Intensive work complete");
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
	
	private AggregateResult benchWorkload(TraversalWorkload w) {
		File inputFile = new File(ClientProperties.tempDir + w.getName() + currentGDesc.getNbVertices());
		if (!inputFile.exists()) {
			generateInputFile(inputFile, w);
		}
		
		AggregateResult r = new AggregateResult();
		List<String> props = readPairs(inputFile);
		if (props.isEmpty()) {
			System.err.println("Unable to perform traversal workload");
		}
		
		for (int i = 0; i < props.size(); i+=2) {
			Vertex from = findVertexWithProps(props.get(i).split(" "));
			Vertex to = findVertexWithProps(props.get(i+1).split(" "));
			if (from == null || to == null) {
				System.err.println("Unable to perform traversal workload");
				break;
			}
			long wall1 = System.nanoTime();
			w.operation(from, to);
			long wall2 = System.nanoTime();
			double timeSpent = (wall2-wall1) / 1000000000.0;
			System.out.println("Time:" + timeSpent);
			r.addTime(new TimeResult(timeSpent));
		}
		
		return r;
	}
	
	private Vertex findVertexWithProps(String[] props) {
		Iterator<Vertex> iter = currentGDesc.getGraph().getVertices(props[0], props[1]).iterator();
		while (iter.hasNext()) {
			Vertex v = iter.next();
			boolean hasAllProps = true;
			for (int i = 2; i < props.length; i+=2) {
				if (!v.getProperty(props[i]).equals(props[i+1])) hasAllProps = false;
			}
			if (hasAllProps) return v;
		}
		return null;
	}

	private void generateInputFile(File f, TraversalWorkload w) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			for (int i = 0; i < 2*ClientProperties.traversalMeanTimes; i++) {
				Vertex v = currentGDesc.getGraph().getVertex(currentGDesc.getRandomVertexId());
				for (String key: v.getPropertyKeys()) {
					bw.write(key);
					bw.write(32);
					bw.write(v.getProperty(key).toString());
					bw.write(32);
				}
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			System.err.println("Error while generating input file for traversal workload");
			e.printStackTrace();
		}
	}
	
	private List<String> readPairs(File f) {
		List<String> pairs = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			while ((line = br.readLine()) != null && !line.equals("\n")) {
				pairs.add(line);
			}
			br.close();
		} catch (IOException e) {
			System.err.println("Error while reading input file for traversal workload");
			e.printStackTrace();
		}
		return pairs;
	}

	private int createNewSlave(final int id, final Address add) {
	    ActorRef slave = getContext().actorOf(new Props(SlaveClient.class).withDeploy(new Deploy(new RemoteScope(add))));
		// We ask the slave for how many cores it has
		int nCores = 0;
		Object answer = askAndWait(slave, new SlaveInitialization(id));
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
		
		numberOfWorkingSlaves = nbrSlavesNeeded;
		
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
		}
		ackBuffer = new ArrayList<Ack>();
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
	
	public void postStop() {
		System.exit(-1);
	}
}