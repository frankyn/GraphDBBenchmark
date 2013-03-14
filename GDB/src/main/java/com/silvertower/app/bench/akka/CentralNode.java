package com.silvertower.app.bench.akka;


import com.silvertower.app.bench.akka.Messages.*;
import com.silvertower.app.bench.datasets.Dataset;
import com.silvertower.app.bench.datasets.SocialNetworkDataset;
import com.silvertower.app.bench.dbinitializers.*;
import com.silvertower.app.bench.utils.Logger;
import com.silvertower.app.bench.utils.Plotter;
import com.silvertower.app.bench.utils.PlotResult;
import com.silvertower.app.bench.workload.DijkstraWorkload;
import com.silvertower.app.bench.workload.EdgesExplorationWorkload;
import com.silvertower.app.bench.workload.NeighborhoodWorkload;
import com.silvertower.app.bench.workload.ReadIDIntensiveWorkload;
import com.silvertower.app.bench.workload.ReadPropIntensiveWorkload;
import com.silvertower.app.bench.workload.TraversalWorkload;
import com.silvertower.app.bench.workload.UpdateIntensiveWorkload;
import com.silvertower.app.bench.workload.IntensiveWorkload;
import com.silvertower.app.bench.workload.VerticesExplorationWorkload;

import static akka.pattern.Patterns.ask;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.dispatch.Await;
import akka.dispatch.Future;
import akka.util.Duration;
import akka.util.Timeout;

public class CentralNode extends UntypedActor {
	private ActorRef masterClient;
	private ActorRef server;
	private Timeout timeout = new Timeout(Duration.create(3600, "seconds"));
	private int fromOpsRead = 1000000;
	private int toOpsRead = 4000000;
	private int stepOpsRead = 1000000;
	private int fromOpsWrite = 100000;
	private int toOpsWrite = 400000;
	private int stepOpsWrite = 100000;
	private int fromClients = 1;
	private int toClients = 4;
	public CentralNode(ActorRef masterClient, ActorRef server) {
		this.masterClient = masterClient;
		this.server = server;
	}
	
	public void preStart() {
		DBInitializer i = new TitanWrapper(false);
		bench(i);
		//DBInitializer i = new Neo4jWrapper();
		//bench(i);
		//DBInitializer i = new OrientWrapper(false);
		//bench(i);
		//DBInitializer i = new DexWrapper();
		//bench(i);
		getContext().system().shutdown();
	}
	
	private void bench(DBInitializer i) {
		Logger log = new Logger(String.format("%s benchmark", i.getName()));
		
		/*loadBenchmark(i, log);
		intensiveBenchmark(i, log, ReadIDIntensiveWorkload.class, fromOpsWrite, toOpsWrite, stepOpsWrite);
		intensiveBenchmark(i, log, ReadPropIntensiveWorkload.class, fromOpsWrite, toOpsWrite, stepOpsWrite);
		intensiveBenchmark(i, log, UpdateIntensiveWorkload.class, fromOpsWrite, toOpsWrite, stepOpsWrite);
		
		vanishDB();*/
		load(i, 10000, log);
		traversalBenchmark(i, log, DijkstraWorkload.class);
		traversalBenchmark(i, log, VerticesExplorationWorkload.class);
		traversalBenchmark(i, log, EdgesExplorationWorkload.class);
		traversalBenchmark(i, log, NeighborhoodWorkload.class);
	}
	
	private void load(DBInitializer i, int nbrVertices, Logger log) {
		Dataset d = new SocialNetworkDataset(nbrVertices);
		initializeDB(i, log);
		fillDB(d, true, log, true);
	}
	
	private void loadBenchmark(DBInitializer i, Logger log) {
		Dataset d10000 = new SocialNetworkDataset(10000);
		Dataset d50000 = new SocialNetworkDataset(50000);
		Dataset d100000 = new SocialNetworkDataset(100000);
		Plotter plot = new Plotter(i.getName(), "Loading", "Number of vertices", "Load time");
		initializeDB(i, log);
		try {
			plot.addResult(new PlotResult(fillDB(d10000, true, log, false), d10000.getNumberVertices()));
			plot.addResult(new PlotResult(fillDB(d50000, true, log, false), d50000.getNumberVertices()));
			plot.addResult(new PlotResult(fillDB(d100000, true, log, true), d100000.getNumberVertices()));
		} catch(Exception e) {
			e.printStackTrace();
		}
		plot.plotResults();
	}
	
	private void intensiveBenchmark(DBInitializer i, Logger log, Class workloadClass, int fromOps, int toOps, int stepOps) {
		try {
			IntensiveWorkload w = (IntensiveWorkload) workloadClass.newInstance();
			Plotter plot1 = new Plotter(i.getName(), w.getName(), "Number of operations", "Time");
			workInOpsRange(w, fromOps, toOps, toClients, stepOps, plot1, log);
			plot1.plotResults();
			Plotter plot2 = new Plotter(i.getName(), w.getName(), "Number of clients", "Time");
			workInClientsRange(w, fromClients, toClients, fromOps, plot2, log);
			plot2.plotResults();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private void traversalBenchmark(DBInitializer i, Logger log, Class workloadClass) {
		try {
			TraversalWorkload w = (TraversalWorkload) workloadClass.newInstance();
			work(w, log);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private void workInOpsRange(IntensiveWorkload w, int from, int to, int nClients, int step, Plotter plot, Logger log) {
		for (int i = from; i <= to; i+= step) {
			TimeResult t = work(w, i, nClients, log);
			if (t != null) plot.addResult(new PlotResult(t, i));
		}
	}
	
	private void workInClientsRange(IntensiveWorkload w, int from, int to, int nOps, Plotter plot, Logger log) {
		for (int i = from; i <= to; i++) {
			TimeResult t = work(w, nOps, i, log);
			if (t != null) plot.addResult(new PlotResult(t, i));
		}
	}

	private void initializeDB(DBInitializer initializer, Logger log) {
		server.tell(new InitializeDB(initializer), getSelf());
		log.logDB(initializer.getName());
	}
	
	private void vanishDB() {
		server.tell(new VanishDB(), getSelf());
	}
	
	private TimeResult fillDB(Dataset d, boolean batchLoading, Logger log, boolean permanent) {
		Future<Object> gDescAnswer = ask(server, new FillDB(d, batchLoading), timeout);
		Future<Object> timeAnswer = ask(server, new GetResult(), timeout);
		GDesc gDesc = null;
		TimeResult t = null;
		try {
			gDesc = (GDesc) Await.result(gDescAnswer, timeout.duration());
			t = (TimeResult) Await.result(timeAnswer, timeout.duration());
			log.logOp(String.format("Loading time for the dataset %s [batchloading=%b]", d.getDatasetName(), batchLoading));
			log.logResult(t);
			if (permanent) assignGDesc(gDesc.getGraphDesc());
			else vanishDB();
			return t;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	private TimeResult work(IntensiveWorkload workload, int ops, int clients, Logger log) {
		IntensiveWork work = new IntensiveWork(workload, ops, clients);
		Future<Object> result = ask(masterClient, work, timeout);
		try {
			Object answer;
			answer = Await.result(result, timeout.duration());
			if (answer instanceof Messages.Error) {
				System.err.println(((Messages.Error) answer).getMessage());
				return null;
			}
			else {
				AggregateResult r = (AggregateResult) Await.result(result, timeout.duration());
				log.logOp(String.format("Time for %s with %d operations and %d clients", workload.getName(), ops, clients));
				log.logResult(r.getMean());
				return r.getMean();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private TimeResult work(TraversalWorkload workload, Logger log) {
		TraversalWork work = new TraversalWork(workload);
		Future<Object> result = ask(masterClient, work, timeout);
		Object answer;
		try {
			answer = Await.result(result, timeout.duration());
			if (answer instanceof Messages.Error) {
				System.err.println(((Messages.Error) answer).getMessage());
				return null;
			}
			else {
				TimeResult r = (TimeResult) Await.result(result, timeout.duration());
				log.logOp(String.format("Time for %s", workload.getName()));
				log.logResult(r);
				return r;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void assignGDesc(GraphDescriptor gDesc) {
		masterClient.tell(new GDesc(gDesc), getSelf());
	}

	public void onReceive(Object message) throws Exception {
		if (message instanceof Messages.Error) {
			System.err.println(((Messages.Error) message).getMessage() + " from" + getSender());
		}
		else unhandled(message);
	}
}