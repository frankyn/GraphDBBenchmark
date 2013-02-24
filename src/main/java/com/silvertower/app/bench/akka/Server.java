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
	public void onReceive(Object message) throws Exception {
		if (message instanceof InitializeDB) {
			this.currentInitializer = ((InitializeDB) message).getInitializer();
		}
		
		else if (message instanceof FillDB) {
			if (currentInitializer == null) {
				getSender().tell(new Messages.Error("Error: the database initializer is not set yet!"));
			}
			else {
				Dataset d = ((FillDB) message).getDataset();
				GraphDescriptor gDesc = null;
				double[] times = DBLoader.normalLoadingBenchmark(d, currentInitializer, gDesc);
				TimeResult r = new TimeResult(times[0], times[1]);
				lastResult = r;
				getSender().tell(new GDesc(gDesc));
			}
		}
		
		else if (message instanceof FillDBBatch) {
			if (currentInitializer == null) {
				getSender().tell(new Messages.Error("Error: the database initializer is not set yet!"));
			}
			else {
				Dataset d = ((FillDB) message).getDataset();
				GraphDescriptor gDesc = null;
				double[] times = DBLoader.batchLoadingBenchmark(d, currentInitializer, gDesc);
				TimeResult r = new TimeResult(times[0], times[1]);
				lastResult = r;
				getSender().tell(new GDesc(gDesc));
			}
		}
		
		else if (message instanceof VanishDB) {
			if (currentInitializer == null) {
				getSender().tell(new Messages.Error("Error: the database initializer is not set yet!"));
			}
			else {
				currentInitializer.getLastGraphInitialized().shutdown();
				Utilities.deleteDirectory(currentInitializer.getLastDBPath());
			}
		}
		
		else if (message instanceof GetResult) {
			getSender().tell(lastResult);
		}
		
		else {
			unhandled(message);
		}
	}

}
