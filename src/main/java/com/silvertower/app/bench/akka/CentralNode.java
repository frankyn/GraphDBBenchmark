package com.silvertower.app.bench.akka;


import com.silvertower.app.bench.akka.Messages.*;
import com.silvertower.app.bench.datasetsgeneration.Dataset;
import com.silvertower.app.bench.datasetsgeneration.SocialNetworkDataset;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.dbinitializers.TitanWrapper;
import com.silvertower.app.bench.utils.Logger;
import com.silvertower.app.bench.utils.Result;
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
		Logger log = new Logger("Benchmark1");
		log.logDB("Titan on disk not distributed batch loading");
		DBInitializer titan = new TitanWrapper(false, null);
		Dataset d250000 = new SocialNetworkDataset(250000);
		Workload dw = new ReadIDIntensiveWorkload();
		try {
			initializeDB(titan);
			fillDB(d250000, true, log);
			log.plotResults("Nb vertices", "Time");
			work(dw, 10000000, 3, log, 10000000);
			work(dw, 20000000, 3, log, 20000000);
			work(dw, 30000000, 3, log, 30000000);
			work(dw, 40000000, 3, log, 40000000);
			work(dw, 50000000, 3, log, 40000000);
			work(dw, 60000000, 3, log, 40000000);
			work(dw, 70000000, 3, log, 40000000);
			log.plotResults("Operations", "Time");
		} catch(Exception e){}
	}

	private void initializeDB(DBInitializer initializer) {
		server.tell(new InitializeDB(initializer), getSelf());
	}
	
	private void fillDB(Dataset d, boolean batchLoading, Logger log) throws Exception {
		Future<Object> gDescAnswer = ask(server, new FillDB(d, batchLoading), timeout);
		Future<Object> timeAnswer = ask(server, new GetResult(), timeout);
		GDesc gDesc = (GDesc) Await.result(gDescAnswer, timeout.duration());
		TimeResult t = (TimeResult) Await.result(timeAnswer, timeout.duration());
		masterClient.tell(gDesc, getSelf());
		//log.logOp(String.format("Loading time for the dataset %s [batchloading=%b]", d.getDatasetName(), batchLoading));
		//log.logResult(new Result(t, d.getNumberVertices()));
	}
	
	private void work(Workload workload, int ops, int clients, Logger log, int x) throws Exception {
		Work work = new Work(workload, ops, clients);
		Future<Object> result = ask(masterClient, work, timeout);
		Object answer = Await.result(result, timeout.duration());
		if (answer instanceof Messages.Error) {
			System.err.println(((Messages.Error) answer).getMessage());
			return;
		}
		if (work.getWorkload().isMT()) {
			AggregateResult r = (AggregateResult) Await.result(result, timeout.duration());
			log.logOp(String.format("Time for %s with %d operations and %d clients", workload.getName(), ops, clients));
			log.logResult(new Result(r.getMean(), x));
		}
		else {
			TimeResult r = (TimeResult) Await.result(result, timeout.duration());
			log.logOp(String.format("Time for %s", workload.getName()));
			log.logResult(new Result(r, x));
		}
	}

	public void onReceive(Object message) throws Exception {
		unhandled(message);
	}
}
