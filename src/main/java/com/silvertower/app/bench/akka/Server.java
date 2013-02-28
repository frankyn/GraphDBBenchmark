package com.silvertower.app.bench.akka;

import com.silvertower.app.bench.datasetsgeneration.Dataset;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.load.DBLoader;
import com.silvertower.app.bench.utils.Utilities;
import com.silvertower.app.bench.akka.Messages.*;

import akka.actor.UntypedActor;

public class Server extends UntypedActor {
	private DBInitializer currentInitializer;
	private TimeResult lastResult;
	private enum State {WAITING_FOR_INFOS, READY_TO_WORK};
	private State state;
	
	public Server() {
		this.state = State.WAITING_FOR_INFOS;
	}
	public void onReceive(Object message) throws Exception {
		if (message instanceof InitializeDB) {
			state = State.READY_TO_WORK;
			currentInitializer = ((InitializeDB) message).getInitializer();
		}
		
		else if (message instanceof FillDB) {
			if (state != State.READY_TO_WORK) {
				getSender().tell(new Messages.Error("Error: the database initializer is not set yet!"), getSelf());
				return;
			}
			Dataset d = ((FillDB) message).getDataset();
			GraphDescriptor gDesc = new GraphDescriptor();
			double[] times;
			if(((FillDB) message).isBatchLoading()) {
				times = DBLoader.batchLoadingBenchmark(d, currentInitializer, gDesc);
			}
			else {
				times = DBLoader.normalLoadingBenchmark(d, currentInitializer, gDesc);
			}
			TimeResult r = new TimeResult(times[0], times[1]);
			lastResult = r;
			getSender().tell(new GDesc(gDesc), getSelf());
		}
		
		else if (message instanceof VanishDB) {
			if (currentInitializer == null) {
				getSender().tell(new Messages.Error("Error: the database initializer is not set yet!"), getSelf());
			}
			else {
				currentInitializer.getLastGraphInitialized().shutdown();
				Utilities.deleteDirectory(currentInitializer.getLastDBPath());
			}
		}
		
		else if (message instanceof GetResult) {
			getSender().tell(lastResult, getSelf());
		}
		
		else {
			unhandled(message);
		}
	}

}
