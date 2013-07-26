package com.silvertower.app.bench.akka;


import com.silvertower.app.bench.akka.Messages.*;
import com.silvertower.app.bench.datasets.Dataset;
import com.silvertower.app.bench.dbinitializers.*;
import com.silvertower.app.bench.main.Benchmark;
import com.silvertower.app.bench.main.BenchmarkConfiguration;
import com.silvertower.app.bench.utils.Logger;
import com.silvertower.app.bench.utils.Statistics;
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
	public Logger log;
	private Benchmark b;
	private String serverAdd;
	private String currentDBName;
	private BenchmarkConfiguration config;
	public BenchRunner(ActorRef mc, ActorRef server, Benchmark b, String serverAdd) {
		this.masterClient = mc;
		this.server = server;
		this.timeout = new Timeout(Duration.create(Integer.MAX_VALUE, "seconds"));
		this.log = new Logger();
		this.b = b;
		this.serverAdd = serverAdd;
	}
	
	public void preStart() {
		b.start(this, log);
	}
	
	public void assignInitializer(DBInitializer i) {
		server.tell(i, getSelf());
		log.logDB(i.toString());
		currentDBName = i.toString();
	}
	
	public LoadResults startLoadBench(Dataset d, int bufferSize) {
		LoadResults r = new LoadResults();
		if (loadDB(d, bufferSize)) {
			log.logMessage(String.format("Load benchmark dataset %s with buffer size %d", d.getDatasetName(), bufferSize));
			Future<Object> answer = ask(server, new GetResult(), timeout);
			try {
				r = (LoadResults) Await.result(answer, timeout.duration());
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
	
	public AggregateResult startWorkBench(IntensiveWorkload w) {
		AggregateResult aggregate = new AggregateResult();
		long timeBefore = System.nanoTime();
		int count = 0;
		while (System.nanoTime() - timeBefore < config.workloadExecutionTimeThresholdInNS && count < config.intensiveRepeatTimes) {
			Object answer = sendMessageAndWaitAnswer(w, masterClient);
			if (answer == null) {
				log.logMessage("Error while executing: " + w);
			}
			else {
				TimeResult r = (TimeResult) answer;
				log.logOp(w.toString());
				log.logResult(r);
				aggregate.addTime(r);
			}
			count ++;
		}
		
		log.logMessage(Statistics.addStatEntry(aggregate, currentDBName, w).toString());
		return aggregate;
	}
	
	public AggregateResult startWorkBench(TraversalWorkload w) {
		Object answer = sendMessageAndWaitAnswer(new TraversalWork(w), masterClient);
		AggregateResult aggregate = new AggregateResult();
		if (answer == null) {
			log.logMessage(String.format("Error while executing the workload %s", w.toString()));
			return null;
		}
		else {
			aggregate = (AggregateResult) answer;
			log.logOp(String.format("Workload %s", w.toString()));
			log.logResult(aggregate);
		}
		log.logMessage(Statistics.addStatEntry(aggregate, currentDBName, w).toString());
		return aggregate;
	}
	
	public void shutdownSystem() {
		Duration d = Duration.create(2, "seconds");
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
	
	private boolean loadDB(Dataset d, int bufferSize) {
		Future<Object> answer = ask(server, new Load(d, bufferSize), timeout);
		GraphDescriptor gDesc = null;
		try {
			gDesc = (GraphDescriptor) Await.result(answer, timeout.duration());
			gDesc.setServerAdd(serverAdd);
			gDesc.setServerPort(8182);
			if (sendMessageAndWaitAnswer(gDesc, masterClient) == null) return false;
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
	
	private Object sendMessageAndWaitAnswer(Object message, ActorRef dest) {
		System.out.println("Sending and waits for answer:" + message);
		Future<Object> result = ask(dest, message, timeout);
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

	public void shareConfig(BenchmarkConfiguration config) {
		this.config = config;
		server.tell(config, getSelf());
		masterClient.tell(config, getSelf());
	}
}