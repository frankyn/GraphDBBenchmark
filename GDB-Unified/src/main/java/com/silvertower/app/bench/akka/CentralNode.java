package com.silvertower.app.bench.akka;


import com.silvertower.app.bench.akka.Messages.*;
import com.silvertower.app.bench.datasets.Dataset;
import com.silvertower.app.bench.datasets.SocialNetworkDataset;
import com.silvertower.app.bench.dbinitializers.*;
import com.silvertower.app.bench.utils.Logger;
import com.silvertower.app.bench.utils.Plotter;
import com.silvertower.app.bench.utils.PlotResult;
import com.silvertower.app.bench.workload.DegreeInformationWorkload;
import com.silvertower.app.bench.workload.DijkstraWorkload;
import com.silvertower.app.bench.workload.EdgesExplorationWorkload;
import com.silvertower.app.bench.workload.NeighborhoodWorkload;
import com.silvertower.app.bench.workload.ReadIDIntensiveWorkload;
import com.silvertower.app.bench.workload.ReadPropIntensiveWorkload;
import com.silvertower.app.bench.workload.TraversalWorkload;
import com.silvertower.app.bench.workload.UpdateIntensiveWorkload;
import com.silvertower.app.bench.workload.IntensiveWorkload;
import com.silvertower.app.bench.workload.VerticesExplorationWorkload;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

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
	private int fromOps = 10;
	private int toOps = 50;
	private int stepOps = 10;
	private int fromClients = 1;
	private int toClients = 4;
	public CentralNode(ActorRef mc, ActorRef server) {
		this.masterClient = mc;
		this.server = server;
	}
	
	public void preStart() {
		//DBInitializer i = new TitanWrapper();
		//bench(i);
		//vanishDB();
		//DBInitializer j = new Neo4jWrapper();
		//bench(j);
		//vanishDB();
		//DBInitializer k = new OrientWrapper();
		//bench(k);
		//vanishDB();
		DBInitializer l = new DexWrapper();
		bench(l);
		//vanishDB();
		getContext().system().shutdown();
	}
	
	private void bench(DBInitializer i) {
		Logger log = new Logger(String.format("%s benchmark", i.getName()));
		
		load(i, 1000);
		//intensiveBenchmark(i, log, ReadIDIntensiveWorkload.class, fromOps, toOps, stepOps);
		//intensiveBenchmark(i, log, ReadPropIntensiveWorkload.class, fromOps, toOps, stepOps);
		//intensiveBenchmark(i, log, UpdateIntensiveWorkload.class, fromOps, toOps, stepOps);
		
		/*traversalBenchmark(i, log, DijkstraWorkload.class);
		traversalBenchmark(i, log, VerticesExplorationWorkload.class);
		traversalBenchmark(i, log, EdgesExplorationWorkload.class);
		traversalBenchmark(i, log, DegreeInformationWorkload.class);*/
		//traversalBenchmark(i, log, NeighborhoodWorkload.class);
		traversalBenchmark(i, log, DijkstraWorkload.class);
	}
	
	private void load(DBInitializer i, int nbrVertices) {
		Dataset d = assignDataset(new SocialNetworkDataset(nbrVertices));
		server.tell(i, getSelf());
		loadDB(d);
	}
	
	private void loadBenchmark(DBInitializer i, Logger log) {
		Plotter plot = new Plotter(i.getName(), "Loading", "Number of vertices", "Load time");
		server.tell(i, getSelf());
		log.logDB(i.getName());
		try {
			Dataset d10000 = assignDataset(new SocialNetworkDataset(10000));
			plot.addResult(new PlotResult(loadDBBench(d10000, true, log), d10000.getNumberVertices()));
			Dataset d50000 = assignDataset(new SocialNetworkDataset(50000));
			plot.addResult(new PlotResult(loadDBBench(d50000, true, log), d50000.getNumberVertices()));
			Dataset d100000 = assignDataset(new SocialNetworkDataset(100000));
			plot.addResult(new PlotResult(loadDBBench(d100000, true, log), d100000.getNumberVertices()));
		} catch(Exception e) {
			e.printStackTrace();
		}
		plot.plotResults();
	}
	
	private Dataset assignDataset(Dataset d) {
		Dataset dFilled = null;
		try {
			Future<Object> datasetAnswer = ask(server, d, timeout);
			dFilled = (Dataset) Await.result(datasetAnswer, timeout.duration());
		} catch(Exception e) {
			e.printStackTrace();
		}
		return dFilled;
	}
	
	private TimeResult loadDBBench(Dataset d, boolean batchLoading, Logger log) {
		Future<Object> timeAnswer = ask(server, new LoadBench(d, batchLoading), timeout);
		TimeResult t = null;
		try {
			t = (TimeResult) Await.result(timeAnswer, timeout.duration());
			log.logOp(String.format("Loading time for the dataset %s [batchloading=%b]", d.getDatasetName(), batchLoading));
			log.logResult(t);
			return t;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void loadDB(Dataset d) {
		Future<Object> answer = ask(server, new Load(d), timeout);
		GraphDescriptor gDesc = null;
		try {
			gDesc = (GraphDescriptor) Await.result(answer, timeout.duration());
		} catch (Exception e) {
			e.printStackTrace();
		}
		masterClient.tell(gDesc, getSelf());
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

	public void onReceive(Object message) throws Exception {
		if (message instanceof Messages.Error) {
			System.err.println(((Messages.Error) message).getMessage() + " from" + getSender());
		}
		else unhandled(message);
	}
	
	private void vanishDB() {
		server.tell(new StopCurrentDB(), getSelf());
	}
}