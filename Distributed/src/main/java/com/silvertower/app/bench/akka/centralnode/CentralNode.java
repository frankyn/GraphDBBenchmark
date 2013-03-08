package com.silvertower.app.bench.akka.centralnode;


import com.silvertower.app.bench.akka.messages.Messages;
import com.silvertower.app.bench.akka.messages.Messages.*;
import com.silvertower.app.bench.datasetsgeneration.Dataset;
import com.silvertower.app.bench.datasetsgeneration.SocialNetworkDataset;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.dbinitializers.Neo4jWrapper;
import com.silvertower.app.bench.dbinitializers.TitanWrapper;
import com.silvertower.app.bench.utils.Logger;
import com.silvertower.app.bench.utils.Plotter;
import com.silvertower.app.bench.utils.PlotResult;
import com.silvertower.app.bench.workload.ReadIDIntensiveWorkload;
import com.silvertower.app.bench.workload.Workload;

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
	public CentralNode(ActorRef masterClient, ActorRef server) {
		this.masterClient = masterClient;
		this.server = server;
	}
	
	public void preStart() {
		benchmark1();
		getContext().system().shutdown();
	}
	
	private void benchmark1() {
		Logger log = new Logger("Benchmark2");
		DBInitializer titan = new Neo4jWrapper();
		Dataset d100000 = new SocialNetworkDataset(100000);
		//Dataset d250000 = new SocialNetworkDataset(250000);
		Workload w = new ReadIDIntensiveWorkload();
		Plotter plot1 = new Plotter("Titan", "Loading", "Number of vertices", "Load time");
		Plotter plot2 = new Plotter("Titan", w.getName(), "Number of operations", "Time");
		
		try {
			initializeDB(titan, log);
			plot1.addResult(new PlotResult(fillDB(d100000, true, log), d100000.getNumberVertices()));
			//plot1.addResult(new PlotResult(fillDB(d250000, true, log), d250000.getNumberVertices()));
			plot2.addResult(new PlotResult(work(w, 10000000, 2, log), 10000000));
			plot2.addResult(new PlotResult(work(w, 20000000, 2, log), 20000000));
			plot2.addResult(new PlotResult(work(w, 30000000, 2, log), 30000000));
			plot2.addResult(new PlotResult(work(w, 40000000, 2, log), 40000000));
			plot2.addResult(new PlotResult(work(w, 50000000, 2, log), 50000000));
			plot2.addResult(new PlotResult(work(w, 60000000, 2, log), 60000000));
			plot2.addResult(new PlotResult(work(w, 70000000, 2, log), 70000000));
			plot1.plotResults();
			plot2.plotResults();
		} catch(Exception e){}
	}

	private void initializeDB(DBInitializer initializer, Logger log) {
		server.tell(new InitializeDB(initializer), getSelf());
		log.logDB(initializer.getName());
	}
	
	private TimeResult fillDB(Dataset d, boolean batchLoading, Logger log) throws Exception {
		Future<Object> gDescAnswer = ask(server, new FillDB(d, batchLoading), timeout);
		Future<Object> timeAnswer = ask(server, new GetResult(), timeout);
		GDesc gDesc = (GDesc) Await.result(gDescAnswer, timeout.duration());
		TimeResult t = (TimeResult) Await.result(timeAnswer, timeout.duration());
		masterClient.tell(gDesc, getSelf());
		log.logOp(String.format("Loading time for the dataset %s [batchloading=%b]", d.getDatasetName(), batchLoading));
		log.logResult(t);
		return t;
	}
	
	private TimeResult work(Workload workload, int ops, int clients, Logger log) throws Exception {
		Work work = new Work(workload, ops, clients);
		Future<Object> result = ask(masterClient, work, timeout);
		Object answer = Await.result(result, timeout.duration());
		if (answer instanceof Messages.Error) {
			System.err.println(((Messages.Error) answer).getMessage());
			return null;
		}
		if (work.getWorkload().isMT()) {
			AggregateResult r = (AggregateResult) Await.result(result, timeout.duration());
			log.logOp(String.format("Time for %s with %d operations and %d clients", workload.getName(), ops, clients));
			log.logResult(r.getMean());
			return r.getMean();
		}
		else {
			TimeResult r = (TimeResult) Await.result(result, timeout.duration());
			log.logOp(String.format("Time for %s", workload.getName()));
			log.logResult(r);
			return r;
		}
	}

	public void onReceive(Object message) throws Exception {
		unhandled(message);
	}
}
