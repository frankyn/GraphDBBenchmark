package com.silvertower.app.bench.akka;


import com.silvertower.app.bench.akka.Messages.InitializeDB;
import com.silvertower.app.bench.akka.Messages.*;
import com.silvertower.app.bench.datasetsgeneration.Dataset;
import com.silvertower.app.bench.datasetsgeneration.SocialNetworkDataset;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.dbinitializers.TitanWrapper;
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
	}
	
	private void benchmark1() {
		DBInitializer titan = new TitanWrapper(false, null);
		Dataset d10000 = new SocialNetworkDataset(10000);
		Workload dw = new ReadIDIntensiveWorkload();
		try {
			initializeDB(titan);
			fillDB(d10000, true);
			work(dw, 10000, 3);
		} catch(Exception e){}
	}

	private void initializeDB(DBInitializer initializer) {
		server.tell(new InitializeDB(initializer), getSelf());
	}
	
	private void fillDB(Dataset d, boolean batchLoading) throws Exception {
		Future<Object> gDescAnswer = ask(server, new FillDB(d, batchLoading), timeout);
		Future<Object> timeAnswer = ask(server, new GetResult(), timeout);
		GDesc gDesc = (GDesc) Await.result(gDescAnswer, timeout.duration());
		TimeResult t = (TimeResult) Await.result(timeAnswer, timeout.duration());
		masterClient.tell(gDesc, getSelf());
		System.out.println(gDesc.getGraphDesc().getDescription());
		System.out.println(t);
	}
	
	private void work(Workload workload, int ops, int clients) throws Exception {
		Work work = new Work(workload, ops, clients);
		Future<Object> result = ask(masterClient, work, timeout);
		Object answer = Await.result(result, timeout.duration());
		if (answer instanceof Messages.Error) {
			System.err.println(((Messages.Error) answer).getMessage());
			return;
		}
		System.out.println(work.getDescription());
		if (work.getWorkload().isMT()) {
			AggregateResult r = (AggregateResult) Await.result(result, timeout.duration());
			System.out.println(r.getMean());
		}
		else {
			TimeResult r = (TimeResult) Await.result(result, timeout.duration());
			System.out.println(r);
		}
	}

	public void onReceive(Object message) throws Exception {
		unhandled(message);
	}
}
