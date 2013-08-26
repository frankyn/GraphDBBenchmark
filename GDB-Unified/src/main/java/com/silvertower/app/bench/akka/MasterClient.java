package com.silvertower.app.bench.akka;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
import com.silvertower.app.bench.main.BenchmarkConfiguration;
import com.silvertower.app.bench.main.MasterClientProperties;
import com.silvertower.app.bench.utils.IP;
import com.silvertower.app.bench.utils.Port;
import com.silvertower.app.bench.workload.IntensiveWorkload;
import com.silvertower.app.bench.workload.TraversalWorkload;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.rexster.client.RexProException;

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
	private BenchmarkConfiguration config;
	public MasterClient(IP[] slaveIps, Port[] slavePorts) {
		this.slavesAvailable = slaveIps.length;
		this.slaves = new ArrayList<SlaveReference>(slavesAvailable);
		this.ackBuffer = new ArrayList<Ack>();
		// Initialize the slaves (workers)
		for (int i = 1; i <= slavesAvailable; i++) {
			String ipAddress = slaveIps[i-1].toString();
			int port = slavePorts[i-1].toInt();
			Address add = new Address("akka", "SCNode", ipAddress, port);
			int coresAdded = createNewSlave(coresAvailable + 1, add);
			coresAvailable += coresAdded;
		}
		state = State.WAITING_FOR_INFOS;
	}
	
	public void onReceive(Object message) throws Exception {
		System.out.println("Master:" + message);
		if (message instanceof BenchmarkConfiguration) {
			this.config = (BenchmarkConfiguration) message;
		}
		
		else if (message instanceof GraphDescriptor) {
			currentGDesc = (GraphDescriptor) message;
			currentGDesc.setNbConcurrentThreads(coresAvailable);
			
			shareGDesc();
			shareConfig();
			
			try {
				currentGDesc.fetchGraph();
			} catch (Exception e) {
				System.err.println("Error while fetching rexster graph");
				return;
			}
			
			state = State.READY_FOR_WORK;
			
			getSender().tell(new Ack(), getSelf());
		}
		
		else if (message instanceof IntensiveWorkload) {
			if (state != State.READY_FOR_WORK) {
				forwardError("Error: the master client is not ready for work!");
				return;
			}
			
			resultsListener = getSender();
			
			IntensiveWorkload w = (IntensiveWorkload) message;
			int nCores = w.getnClients();
			if (nCores > coresAvailable) {
				forwardError("Error: not enough cores/slaves available!");
				return;
			}
			
			state = State.WORKING;
			assignWork(w);
			intensiveWorkloadStartTs = System.nanoTime();
			startWork();
			System.out.println("Starting intensive work");
		}
		
		else if (message instanceof TraversalWorkload) {
			if (state != State.READY_FOR_WORK) {
				forwardError("Error: the master client is not ready for work!");
				return;
			}
			resultsListener = getSender();
			
			state = State.WORKING;
			System.out.println("Starting traversal work");
			resultsListener.tell(benchWorkload((TraversalWorkload) message), getSelf());
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
		File inputFile = new File(MasterClientProperties.tempDir + w.toString() + currentGDesc.getNbVertices());
		if (!inputFile.exists()) {
			generateInputFile(inputFile, w);
		}
		
		AggregateResult r = new AggregateResult();
		List<String> pairs = readPairs(inputFile);
		if (pairs.isEmpty()) {
			System.err.println("Unable to perform traversal workload");
		}
		
		for (String pair: pairs) {
			String[] cids = pair.split(" ");
			Vertex from = currentGDesc.getRexsterGraph().getVertices("cid", cids[0]).iterator().next();
			Vertex to = currentGDesc.getRexsterGraph().getVertices("cid", cids[1]).iterator().next();
			if (from == null || to == null) {
				System.err.println("Unable to perform traversal workload");
				break;
			}
			long wall1 = System.nanoTime();
			
			String request = w.generateRequest(from, to);
			try {
				currentGDesc.getRexsterClient().execute(request);
			} catch (RexProException e) {
				System.err.println("Error while executing the request: " + request);
			} catch (IOException e) {
				System.err.println("Error while executing the request: " + request);
			}
			
			long wall2 = System.nanoTime();
			double timeSpent = (wall2-wall1) / 1000000000.0;
			System.out.println("Time:" + timeSpent);
			r.addTime(new TimeResult(timeSpent));
		}
		
		return r;
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

	private void generateInputFile(File f, TraversalWorkload w) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			for (int i = 0; i < config.traversalRepeatTimes; i++) {
				Vertex v = currentGDesc.getRexsterGraph().getVertex(currentGDesc.getRandomVertexId());
				bw.write(v.getProperty("cid").toString());
				bw.write(32);
				Vertex v1 = null;
				while (v1 == null || v.equals(v1)) {
					v1 = currentGDesc.getRexsterGraph().getVertex(currentGDesc.getRandomVertexId());
				}
				bw.write(v1.getProperty("cid").toString());
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			System.err.println("Error while generating input file for traversal workload");
			e.printStackTrace();
		}
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
	
	private void shareGDesc() {
		// Share the graph descriptor with all the slaves
		for (SlaveReference s: slaves) {
			// We wait until this slave ack the reception of the graph descriptor
			askAndWait(s.getSlaveRef(), currentGDesc);
		}
	}
	
	private void shareConfig() {
		for (SlaveReference s: slaves) {
			askAndWait(s.getSlaveRef(), config);
		}
	}

	private void assignWork(IntensiveWorkload workload) {
		int nbrSlavesNeeded = 0;
		int nOps = workload.getnOps();
		int nCores = workload.getnClients();
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
			IntensiveWorkload slaveWorkload = workload.reduceWorkload(nbrOpPerSlave, coresUsed);
			askAndWait(slave.getSlaveRef(), slaveWorkload);
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
		System.out.println(errorMessage);
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