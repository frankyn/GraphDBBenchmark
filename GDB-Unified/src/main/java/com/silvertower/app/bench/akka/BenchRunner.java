package com.silvertower.app.bench.akka;


import com.silvertower.app.bench.akka.Messages.*;
import com.silvertower.app.bench.datasets.Dataset;
import com.silvertower.app.bench.dbinitializers.*;
import com.silvertower.app.bench.main.Benchmark;
import com.silvertower.app.bench.main.ClientProperties;
import com.silvertower.app.bench.utils.Logger;
import com.silvertower.app.bench.workload.TraversalWorkload;
import com.silvertower.app.bench.workload.IntensiveWorkload;

import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.gracefulStop;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.dispatch.Await;
import akka.dispatch.Future;
import akka.util.Duration;
import akka.util.Timeout;

public class BenchRunner extends UntypedActor {
	private ActorRef masterClient;
	private ActorRef server;
	private Timeout timeout;
	private Logger log;
	private Benchmark b;
	private String serverAdd;
	public BenchRunner(ActorRef mc, ActorRef server, Benchmark b, String serverAdd) {
		this.masterClient = mc;
		this.server = server;
		this.timeout = new Timeout(Duration.create(3600, "seconds"));
		this.log = new Logger();
		this.b = b;
		this.serverAdd = serverAdd;
	}
	
	public void preStart() {
		b.start(this);
	}
	
	public void assignInitializer(DBInitializer i) {
		server.tell(i, getSelf());
		log.logDB(i.getName());
	}
	
	public AggregateResult startLoadBench(Dataset d) {
		AggregateResult r = new AggregateResult();
		if (loadDB(d)) {
			log.logMessage(String.format("Dataset %s loaded", d.getDatasetName()));
			Future<Object> answer = ask(server, new GetResult(), timeout);
			try {
				r = (AggregateResult) Await.result(answer, timeout.duration());
			} catch (Exception e) {
				e.printStackTrace();
			}
			log.logResult(r);
		}
		else {
			log.logMessage("Error while loading DB");
			shutdownSystem();
		}
		return r;
	}
	
	public AggregateResult startWorkBench(IntensiveWorkload w, int nOps, int nClients) {
		IntensiveWork work = new IntensiveWork(w, nOps, nClients);
		AggregateResult aggregate = new AggregateResult();
		for (int i = 0; i < ClientProperties.intensiveMeanTimes; i++) {
			Object answer = sendWorkAndWaitAnswer(work);
			if (answer == null) {
				log.logMessage(String.format("Error while executing the workload %s with %d operations " +
						"and %d clients", w.getName(), nOps, nClients));
			}
			else {
				TimeResult r = (TimeResult) answer;
				log.logOp(String.format("Workload %s with %d operations and %d clients", w.getName(), nOps, nClients));
				log.logResult(r);
				aggregate.addTime(r);
			}
		}
		return aggregate;
	}
	
	public AggregateResult startWorkBench(TraversalWorkload w) {
		Object answer = sendWorkAndWaitAnswer(new TraversalWork(w));
		if (answer == null) {
			log.logMessage(String.format("Error while executing the workload %s", w.getName()));
			return null;
		}
		else {
			AggregateResult r = (AggregateResult) answer;
			log.logOp(String.format("Workload %s", w.getName()));
			log.logResult(r);
			return r;
		}
	}
	
	public void shutdownSystem() {
		Duration d = Duration.create(10, "seconds");
		Future<Boolean> sStopped = gracefulStop(server, d, getContext().system());
		Future<Boolean> mStopped = gracefulStop(masterClient, d, getContext().system());
		try {
			Await.result(sStopped, d);
			Await.result(mStopped, d);
		} catch (Exception e) {
			System.err.println("Error while stopping the actor system");
		}
		getContext().system().shutdown();
		System.exit(-1);
	}
	
	private boolean loadDB(Dataset d) {
		Future<Object> answer = ask(server, new Load(d), timeout);
		GraphDescriptor gDesc = null;
		try {
			gDesc = (GraphDescriptor) Await.result(answer, timeout.duration());
			gDesc.setServerAdd(serverAdd);
			gDesc.setServerPort(8182);
			masterClient.tell(gDesc, getSelf());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void onReceive(Object message) throws Exception {
		if (message instanceof Messages.Error) {
			System.err.println(((Messages.Error) message).getMessage() + " from " + getSender());
		}
		else unhandled(message);
	}
	
	public void vanishDB() {
		server.tell(new StopCurrentDB(), getSelf());
	}
	
	private Object sendWorkAndWaitAnswer(Object workMessage) {
		Future<Object> result = ask(masterClient, workMessage, timeout);
		try {
			Object answer = Await.result(result, timeout.duration());
			if (answer instanceof Messages.Error) {
				System.err.println(((Messages.Error) answer).getMessage());
				return null;
			}
			else {
				return answer;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}