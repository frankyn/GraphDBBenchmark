package com.silvertower.app.bench.akka;


import com.silvertower.app.bench.akka.Messages.*;
import com.silvertower.app.bench.datasets.Dataset;
import com.silvertower.app.bench.dbinitializers.*;
import com.silvertower.app.bench.main.Benchmark;
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
	public BenchRunner(ActorRef mc, ActorRef server, Benchmark b) {
		this.masterClient = mc;
		this.server = server;
		this.timeout = new Timeout(Duration.create(3600, "seconds"));
		this.log = new Logger();
		this.b = b;
	}
	
	public void preStart() {
		b.start(this);
	}
	
	public void load(DBInitializer i, Dataset givenD) {
		Dataset d = assignDataset(givenD);
		server.tell(i, getSelf());
		if (loadDB(d)) {
			log.logDB(i.getName());
			log.logMessage(String.format("Dataset %s loaded", d.getDatasetName()));
		}
		else {
			log.logMessage(String.format("Error while loading DB", i.getName()));
			shutdownSystem();
		}
	}
	
	
	public AggregateResult startWorkBench(IntensiveWorkload w, int nOps, int nClients) {
		IntensiveWork work = new IntensiveWork(w, nOps, nClients);
		Object answer = sendWorkAndWaitAnswer(work);
		if (answer == null) {
			log.logMessage(String.format("Error while executing the workload %s with %d operations " +
					"and %d clients", w.getName(), nOps, nClients));
			return null;
		}
		else {
			AggregateResult r = (AggregateResult) answer;
			log.logOp(String.format("Workload %s with %d operations and %d clients", w.getName(), nOps, nClients));
			log.logResult(r);
			return r;
		}
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
	
	public TimeResult startLoadBench(DBInitializer i, Dataset d, boolean batchLoading) {
		server.tell(i, getSelf());
		log.logDB(i.getName());
		Dataset d2 = assignDataset(d);
		Future<Object> timeAnswer = ask(server, new LoadBench(d2, batchLoading), timeout);
		TimeResult t = null;
		try {
			t = (TimeResult) Await.result(timeAnswer, timeout.duration());
			log.logOp(String.format("Loading of dataset %s [batchloading=%b]", d.getDatasetName(), batchLoading));
			log.logResult(t);
			return t;
		} catch (Exception e) {
			log.logMessage("Error during load benchmark");
			e.printStackTrace();
			return null;
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
	
	private boolean loadDB(Dataset d) {
		Future<Object> answer = ask(server, new Load(d), timeout);
		GraphDescriptor gDesc = null;
		try {
			gDesc = (GraphDescriptor) Await.result(answer, timeout.duration());
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