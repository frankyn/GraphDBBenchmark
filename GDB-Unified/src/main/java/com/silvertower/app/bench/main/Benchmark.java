package com.silvertower.app.bench.main;

import java.util.List;

import com.silvertower.app.bench.datasets.SocialNetworkDataset;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.dbinitializers.DexWrapper;
import com.silvertower.app.bench.dbinitializers.Neo4jWrapper;
import com.silvertower.app.bench.dbinitializers.OrientWrapper;
import com.silvertower.app.bench.dbinitializers.TitanBerkeleyDBWrapper;
import com.silvertower.app.bench.dbinitializers.TitanCassandraWrapper;
import com.silvertower.app.bench.resultsprocessing.PointSeries;
import com.silvertower.app.bench.workload.AddEdgesIntensiveWorkload;
import com.silvertower.app.bench.workload.GetVerticesByIDIntensiveWorkload;
import com.silvertower.app.bench.workload.GetVerticesByPropIntensiveWorkload;
import com.silvertower.app.bench.workload.LoadWorkload;
import com.silvertower.app.bench.workload.UpdateVerticesIntensiveWorkload;

public class Benchmark {
	BenchmarkCommandLineExecutor executor;
	
	public Benchmark(BenchmarkCommandLineExecutor executor) {
		this.executor = executor;
	}
	
	public void addInitializers(List<DBInitializer> initializers) {
		initializers.add(new Neo4jWrapper());
		initializers.add(new DexWrapper());
		initializers.add(new TitanBerkeleyDBWrapper());
		initializers.add(new TitanCassandraWrapper());
		initializers.add(new OrientWrapper());
	}
	
	public void benchmark(String currentDBName) {
		SocialNetworkDataset d = new SocialNetworkDataset(250000);
		PointSeries bps0 = new PointSeries(String.format("Loading DB with a %s dataset", d.getDatasetType()), currentDBName, "Time (seconds)");
		bps0.addResult("Load", executor.loadBench(new LoadWorkload(20000, d)));
		executor.plotter.addXYPointsSeries(bps0);
		
//		PointSeries bps = new PointSeries("Shortest paths search", currentDBName, "Time (seconds)");
//		bps.addResult("2 hops limit", executor.workBench(new ShortestPathWorkload(2)));
//		bps.addResult("3 hops limit", executor.workBench(new ShortestPathWorkload(3)));
//		bps.addResult("4 hops limit", executor.workBench(new ShortestPathWorkload(4)));
//		bps.addResult("5 hops limit", executor.workBench(new ShortestPathWorkload(5)));
//		executor.plotter.addBPSeries(bps);
//		
//		PointSeries bps1 = new PointSeries("Neighborhood breadth-first exploration", currentDBName, "Time (seconds)");
//		bps1.addResult("1 hop limit", executor.workBench(new NeighborhoodWorkload(1)));
//		bps1.addResult("2 hops limit", executor.workBench(new NeighborhoodWorkload(2)));
//		bps1.addResult("3 hops limit", executor.workBench(new NeighborhoodWorkload(3)));
//		bps1.addResult("5 hops limit", executor.workBench(new NeighborhoodWorkload(5)));
//		executor.plotter.addBPSeries(bps1);
//		
		PointSeries bps31 = new PointSeries("Read by ID Intensive 200 ops", currentDBName, "Number of clients", "Time (seconds)");
		bps31.addResult("1 client", 1, executor.workBench(new GetVerticesByIDIntensiveWorkload(200, 1, false)));
		bps31.addResult("2 clients", 2, executor.workBench(new GetVerticesByIDIntensiveWorkload(200, 2, false)));
		bps31.addResult("3 clients", 3, executor.workBench(new GetVerticesByIDIntensiveWorkload(200, 3, false)));
		bps31.addResult("4 clients", 4, executor.workBench(new GetVerticesByIDIntensiveWorkload(200, 4, false)));
		executor.plotter.addBPSeries(bps31);
		executor.plotter.addXYPointsSeries(bps31);
			
		PointSeries bps51 = new PointSeries("Read by property Intensive 200 ops", currentDBName, "Number of clients", "Time (seconds)");
		bps51.addResult("1 client", 1, executor.workBench(new GetVerticesByPropIntensiveWorkload(200, 1, false)));
		bps51.addResult("2 clients", 2, executor.workBench(new GetVerticesByPropIntensiveWorkload(200, 2, false)));
		bps51.addResult("3 clients", 3, executor.workBench(new GetVerticesByPropIntensiveWorkload(200, 3, false)));
		bps51.addResult("4 clients", 4, executor.workBench(new GetVerticesByPropIntensiveWorkload(200, 4, false)));
		executor.plotter.addBPSeries(bps51);
		executor.plotter.addXYPointsSeries(bps51);
		
		PointSeries bps71 = new PointSeries("Read Write Intensive 200 ops", currentDBName, "Number of clients", "Time (seconds)");
		bps71.addResult("1 client", 1, executor.workBench(new UpdateVerticesIntensiveWorkload(200, 1, false)));
		bps71.addResult("2 clients", 2, executor.workBench(new UpdateVerticesIntensiveWorkload(200, 2, false)));
		bps71.addResult("3 clients", 3, executor.workBench(new UpdateVerticesIntensiveWorkload(200, 3, false)));
		bps71.addResult("4 clients", 4, executor.workBench(new UpdateVerticesIntensiveWorkload(200, 4, false)));
		executor.plotter.addBPSeries(bps71);
		executor.plotter.addXYPointsSeries(bps71);
		
		PointSeries bps8 = new PointSeries("Add edges Intensive 200 ops", currentDBName, "Number of clients", "Time (seconds)");
		bps8.addResult("1 client", 1, executor.workBench(new AddEdgesIntensiveWorkload(200, 1, false)));
		bps8.addResult("2 clients", 2, executor.workBench(new AddEdgesIntensiveWorkload(200, 2, false)));
		bps8.addResult("3 clients", 3, executor.workBench(new AddEdgesIntensiveWorkload(200, 3, false)));
		bps8.addResult("4 clients", 4, executor.workBench(new AddEdgesIntensiveWorkload(200, 4, false)));
		executor.plotter.addBPSeries(bps8);
		executor.plotter.addXYPointsSeries(bps8);
	}
}
