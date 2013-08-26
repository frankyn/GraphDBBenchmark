package com.silvertower.app.bench.akka;


import com.silvertower.app.bench.akka.Messages.*;
import com.silvertower.app.bench.dbinitializers.*;
import com.silvertower.app.bench.main.BenchmarkConfiguration;
import com.silvertower.app.bench.main.BenchmarkExecutor;
import com.silvertower.app.bench.resultsprocessing.Logger;
import com.silvertower.app.bench.resultsprocessing.Statistics;
import com.silvertower.app.bench.workload.LoadWorkload;
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

public class BenchmarkRunner extends UntypedActor {
	private ActorRef masterClient;
	private ActorRef server;
	private Timeout timeout;
	public Logger logger;
	private BenchmarkExecutor executor;
	private String serverAdd;
	private String currentDBName;
	private BenchmarkConfiguration config;
	public BenchmarkRunner(ActorRef mc, ActorRef server, BenchmarkExecutor executor, String serverAdd) {
		this.masterClient = mc;
		this.server = server;
		this.timeout = new Timeout(Duration.create(Integer.MAX_VALUE, "seconds"));
		this.logger = new Logger();
		this.executor = executor;
		this.serverAdd = serverAdd;
	}
	
	public void preStart() {
		executor.startBenchmark(this, logger);
	}
	
	public void assignInitializer(DBInitializer i) {
		server.tell(i, getSelf());
		logger.logDB(i.toString());
		currentDBName = i.toString();
	}
	
	public LoadResults startLoadBench(LoadWorkload w) {
		LoadResults r = new LoadResults();
		if (loadDB(w)) {
			logger.logOp(w.toString());
			Object answer = sendMessageAndWaitAnswer(new GetResult(), server);
			if (answer == null) {
				logger.logMessage("Error when asking db load result");
			}
			else {
				r = (LoadResults) answer;
				logger.logResult(r);
			}
		}
		else {
			logger.logMessage("Error while loading DB");
			shutdownSystem();
			return null;
		}
		return r;
	}
	
	public AggregateResult startWorkBench(IntensiveWorkload w) {
		AggregateResult aggregate = new AggregateResult();
		long timeBefore = System.nanoTime();
		int count = 0;
		while (System.nanoTime() - timeBefore < config.workloadExTime 
				&& count < config.intensiveRepeatTimes) {
			Object answer = sendMessageAndWaitAnswer(w, masterClient);
			if (answer != null) {
				TimeResult r = (TimeResult) answer;
				logger.logOp(w.toString());
				logger.logResult(r);
				aggregate.addTime(r);
			}
			else {
				logger.logMessage("Error while executing: " + w);
				shutdownSystem();
				return null;
			}
			count ++;
		}
		
		logger.logMessage(Statistics.addStatEntry(aggregate, currentDBName, w).toString());
		return aggregate;
	}
	
	public AggregateResult startWorkBench(TraversalWorkload w) {
		Object answer = sendMessageAndWaitAnswer(w, masterClient);
		AggregateResult aggregate = new AggregateResult();
		if (answer != null) {
			aggregate = (AggregateResult) answer;
			logger.logOp(String.format("Workload %s", w.toString()));
			logger.logResult(aggregate);
		}
		else {
			logger.logMessage(String.format("Error while executing the workload %s", w.toString()));
			shutdownSystem();
			return null;
		}
		logger.logMessage(Statistics.addStatEntry(aggregate, currentDBName, w).toString());
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
	
	private boolean loadDB(LoadWorkload w) {
		Object answer = sendMessageAndWaitAnswer(w, server);
		if (answer == null) {
			logger.logMessage(String.format("Error while executing the workload %s", w.toString()));
			return false;
		}
		else {
			GraphDescriptor gDesc = (GraphDescriptor) answer;
			gDesc.setServerAdd(serverAdd);
			gDesc.setServerPort(8182);
			if (sendMessageAndWaitAnswer(gDesc, masterClient) == null) return false;
			else return true;
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