package com.silvertower.app.bench.main;

import java.util.ArrayList;
import java.util.List;

import com.silvertower.app.bench.akka.BenchmarkRunner;
import com.silvertower.app.bench.akka.Messages.AggregateResult;
import com.silvertower.app.bench.akka.Messages.LoadResults;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.resultsprocessing.Logger;
import com.silvertower.app.bench.resultsprocessing.Plotter;
import com.silvertower.app.bench.resultsprocessing.Statistics;
import com.silvertower.app.bench.workload.LoadWorkload;
import com.silvertower.app.bench.workload.TraversalWorkload;
import com.silvertower.app.bench.workload.IntensiveWorkload;

public class BenchmarkCommandLineExecutor implements BenchmarkExecutor {
	public Plotter plotter = new Plotter();
	private BenchmarkRunner benchRunner;
	public void startBenchmark(BenchmarkRunner b, Logger log) {
		this.benchRunner = b;
		BenchmarkConfiguration config = new BenchmarkConfiguration();
		setConfig(config);
		
		Benchmark benchmark = new Benchmark(this);
		List<DBInitializer> initializers = new ArrayList<DBInitializer>();
		benchmark.addInitializers(initializers);
		
		for (DBInitializer i: initializers) {
			b.assignInitializer(i);
			benchmark.benchmark(i.toString());
			b.vanishDB();
		}
		
		plotter.plotResults();
		log.logMessage(Statistics.computeReport().toString());
		
		b.shutdownSystem();
	}
	
	public void setConfig(BenchmarkConfiguration config) {
		benchRunner.shareConfig(config);
	}
	
	public LoadResults loadBench(LoadWorkload w) {
		return benchRunner.startLoadBench(w);
	}
	
	public AggregateResult workBench(TraversalWorkload w) {
		return benchRunner.startWorkBench(w);
	}
	
	public AggregateResult workBench(IntensiveWorkload w) {
		return benchRunner.startWorkBench(w);
	}
}
