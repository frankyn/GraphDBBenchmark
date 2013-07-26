package com.silvertower.app.bench.main;

import java.util.ArrayList;
import java.util.List;

import com.silvertower.app.bench.akka.BenchRunner;
import com.silvertower.app.bench.akka.Messages.AggregateResult;
import com.silvertower.app.bench.akka.Messages.LoadResults;
import com.silvertower.app.bench.datasets.Dataset;
import com.silvertower.app.bench.datasets.SocialNetworkDataset;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.dbinitializers.DexWrapper;
import com.silvertower.app.bench.dbinitializers.Neo4jWrapper;
import com.silvertower.app.bench.dbinitializers.OrientWrapper;
import com.silvertower.app.bench.dbinitializers.TitanWrapper;
import com.silvertower.app.bench.utils.PointSeries;
import com.silvertower.app.bench.utils.Plotter;
import com.silvertower.app.bench.utils.Logger;
import com.silvertower.app.bench.utils.Statistics;
import com.silvertower.app.bench.workload.AddEdgesIntensiveWorkload;
import com.silvertower.app.bench.workload.TraversalWorkload;
import com.silvertower.app.bench.workload.ShortestPathWorkload;
import com.silvertower.app.bench.workload.IntensiveWorkload;
import com.silvertower.app.bench.workload.NeighborhoodWorkload;
import com.silvertower.app.bench.workload.GetVerticesByIDIntensiveWorkload;
import com.silvertower.app.bench.workload.GetVerticesByPropIntensiveWorkload;
import com.silvertower.app.bench.workload.UpdateVerticesIntensiveWorkload;

public class Benchmark {
	private Plotter plotter = new Plotter();
	private BenchRunner b;
	private String currentDBName;
	private BenchmarkConfiguration config;
	public void start(BenchRunner b, Logger log) {
		this.b = b;
		this.config = new BenchmarkConfiguration();
		config.intensiveRepeatTimes = 10;
		setConfig();
		List<DBInitializer> initializers = new ArrayList<DBInitializer>();
		addInitializers(initializers);
		
		for (DBInitializer i: initializers) {
			b.assignInitializer(i);
			this.currentDBName = i.getName();
			benchmark();
			b.vanishDB();
		}
		
		plotter.plotResults();
		log.logMessage(Statistics.computeReport().toString());
		
		b.shutdownSystem();
	}
	
	private void addInitializers(List<DBInitializer> initializers) {
		initializers.add(new Neo4jWrapper());
//		initializers.add(new DexWrapper());
//		initializers.add(new TitanWrapper("local"));
//		initializers.add(new TitanWrapper("cassandra"));
//		initializers.add(new OrientWrapper());
	}
	
	private void benchmark() {
		SocialNetworkDataset d = new SocialNetworkDataset(10000);
		PointSeries bps0 = new PointSeries(String.format("Loading DB with a %s dataset", d.getDatasetType()), currentDBName, "Time");
		bps0.addResult("Load", loadBench(d, 5000));
		plotter.addXYPointsSeries(bps0);
		
//		PointSeries bps = new PointSeries("Shortest paths search", currentDBName, "Time");
//		bps.addResult("2 hops limit", workBench(new ShortestPathWorkload(2)));
//		bps.addResult("3 hops limit", workBench(new ShortestPathWorkload(3)));
//		bps.addResult("4 hops limit", workBench(new ShortestPathWorkload(4)));
//		bps.addResult("5 hops limit", workBench(new ShortestPathWorkload(5)));
//		plotter.addBPSeries(bps);
//		
//		PointSeries bps1 = new PointSeries("Neighborhood breadth-first exploration", currentDBName, "Time");
//		bps1.addResult("3 hops limit", workBench(new NeighborhoodWorkload(3)));
//		bps1.addResult("4 hops limit", workBench(new NeighborhoodWorkload(4)));
//		bps1.addResult("5 hops limit", workBench(new NeighborhoodWorkload(5)));
//		bps1.addResult("6 hops limit", workBench(new NeighborhoodWorkload(6)));
//		plotter.addBPSeries(bps1);
//		
//		IntensiveWorkload w1 = new GetVerticesByIDIntensiveWorkload();
		
//		PointSeries bps2 = new PointSeries("Read ID Intensive 1 client: batchMode", currentDBName, "Time");
//		bps2.addResult("100 reads", 100, workBench(new GetVerticesByIDIntensiveWorkload(100, 1, true)));
//		bps2.addResult("200 reads", 200, workBench(w1, 200, 1, true));
//		bps2.addResult("300 reads", 300, workBench(w1, 300, 1, true));
//		bps2.addResult("400 reads", 400, workBench(w1, 400, 1, true));
//		plotter.addBPSeries(bps2);
//		plotter.addXYPointsSeries(bps2);
//		
		PointSeries bps21 = new PointSeries("Read ID Intensive 1 client", currentDBName, "Time");
		bps21.addResult("100 reads", 100, workBench(new GetVerticesByIDIntensiveWorkload(100, 1, false)));
		bps21.addResult("200 reads", 200, workBench(new GetVerticesByIDIntensiveWorkload(200, 2, false)));
		bps21.addResult("300 reads", 300, workBench(new GetVerticesByIDIntensiveWorkload(300, 3, false)));
		bps21.addResult("400 reads", 400, workBench(new GetVerticesByIDIntensiveWorkload(400, 4, false)));
		plotter.addBPSeries(bps21);
		plotter.addXYPointsSeries(bps21);
//		
//		PointSeries bps3 = new PointSeries("Read by ID Intensive 200 ops: rexpro", currentDBName, "Clients", "Time");
//		bps3.addResult("1 client", 1, workBench(w1, 200, 1, true));
//		bps3.addResult("2 clients", 2, workBench(w1, 200, 2, true));
//		bps3.addResult("3 clients", 3, workBench(w1, 200, 3, true));
//		bps3.addResult("4 clients", 4, workBench(w1, 200, 4, true));
//		plotter.addBPSeries(bps3);
//		plotter.addXYPointsSeries(bps3);
//		
//		PointSeries bps31 = new PointSeries("Read by ID Intensive 200 ops", currentDBName, "Clients", "Time");
//		bps31.addResult("1 client", 1, workBench(w1, 200, 1, false));
//		bps31.addResult("2 clients", 2, workBench(w1, 200, 2, false));
//		bps31.addResult("3 clients", 3, workBench(w1, 200, 3, false));
//		bps31.addResult("4 clients", 4, workBench(w1, 200, 4, false));
//		plotter.addBPSeries(bps31);
//		plotter.addXYPointsSeries(bps31);
//		
//		IntensiveWorkload w2 = new GetVerticesByPropIntensiveWorkload();
//		
//		PointSeries bps4 = new PointSeries("Read by property Intensive 1 client: rexpro", currentDBName, "Time");
//		bps4.addResult("100 reads", 100, workBench(w2, 100, 1, true));
//		bps4.addResult("200 reads", 200, workBench(w2, 200, 1, true));
//		bps4.addResult("300 reads", 300, workBench(w2, 300, 1, true));
//		bps4.addResult("400 reads", 400, workBench(w2, 400, 1, true));
//		plotter.addBPSeries(bps4);
//		plotter.addXYPointsSeries(bps4);
//		
//		PointSeries bps41 = new PointSeries("Read by property Intensive 1 client", currentDBName, "Time");
//		bps41.addResult("100 reads", 100, workBench(w2, 100, 1, false));
//		bps41.addResult("200 reads", 200, workBench(w2, 200, 1, false));
//		bps41.addResult("300 reads", 300, workBench(w2, 300, 1, false));
//		bps41.addResult("400 reads", 400, workBench(w2, 400, 1, false));
//		plotter.addBPSeries(bps41);
//		plotter.addXYPointsSeries(bps41);
//		
//		PointSeries bps5 = new PointSeries("Read by property Intensive 200 ops: rexpro", currentDBName, "Clients", "Time");
//		bps5.addResult("1 client", 1, workBench(w2, 200, 1, true));
//		bps5.addResult("2 clients", 2, workBench(w2, 200, 2, true));
//		bps5.addResult("3 clients", 3, workBench(w2, 200, 3, true));
//		bps5.addResult("4 clients", 4, workBench(w2, 200, 4, true));
//		plotter.addBPSeries(bps5);
//		plotter.addXYPointsSeries(bps5);
//		
//		PointSeries bps51 = new PointSeries("Read by property Intensive 200 ops", currentDBName, "Clients", "Time");
//		bps51.addResult("1 client", 1, workBench(w2, 200, 1, false));
//		bps51.addResult("2 clients", 2, workBench(w2, 200, 2, false));
//		bps51.addResult("3 clients", 3, workBench(w2, 200, 3, false));
//		bps51.addResult("4 clients", 4, workBench(w2, 200, 4, false));
//		plotter.addBPSeries(bps51);
//		plotter.addXYPointsSeries(bps51);
//		
//		IntensiveWorkload w3 = new UpdateVerticesIntensiveWorkload();
//		
//		PointSeries bps6 = new PointSeries("Read Write Intensive 1 client: rexpro", currentDBName, "Time");
//		bps6.addResult("100 reads", 100, workBench(w3, 100, 1, true));
//		bps6.addResult("200 reads", 200, workBench(w3, 200, 1, true));
//		bps6.addResult("300 reads", 300, workBench(w3, 300, 1, true));
//		bps6.addResult("400 reads", 400, workBench(w3, 400, 1, true));
//		plotter.addBPSeries(bps6);
//		plotter.addXYPointsSeries(bps6);
//		
//		PointSeries bps61 = new PointSeries("Read Write Intensive 1 client", currentDBName, "Time");
//		bps61.addResult("100 reads", 100, workBench(w3, 100, 1, false));
//		bps61.addResult("200 reads", 200, workBench(w3, 200, 1, false));
//		bps61.addResult("300 reads", 300, workBench(w3, 300, 1, false));
//		bps61.addResult("400 reads", 400, workBench(w3, 400, 1, false));
//		plotter.addBPSeries(bps61);
//		plotter.addXYPointsSeries(bps61);
//		
//		PointSeries bps7 = new PointSeries("Read Write Intensive 200 ops: rexpro", currentDBName, "Clients", "Time");
//		bps7.addResult("1 client", 1, workBench(w3, 200, 1, true));
//		bps7.addResult("2 clients", 2, workBench(w3, 200, 2, true));
//		bps7.addResult("3 clients", 3, workBench(w3, 200, 3, true));
//		bps7.addResult("4 clients", 4, workBench(w3, 200, 4, true));
//		plotter.addBPSeries(bps7);
//		plotter.addXYPointsSeries(bps7);
//		
//		PointSeries bps71 = new PointSeries("Read Write Intensive 200 ops", currentDBName, "Clients", "Time");
//		bps71.addResult("1 client", 1, workBench(w3, 200, 1, false));
//		bps71.addResult("2 clients", 2, workBench(w3, 200, 2, false));
//		bps71.addResult("3 clients", 3, workBench(w3, 200, 3, false));
//		bps71.addResult("4 clients", 4, workBench(w3, 200, 4, false));
//		plotter.addBPSeries(bps71);
//		plotter.addXYPointsSeries(bps71);
//		
//		IntensiveWorkload w4 = new AddEdgesIntensiveWorkload();
//		
//		PointSeries bps8 = new PointSeries("Add edges Intensive 200 ops", currentDBName, "Clients", "Time");
//		bps8.addResult("1 client", 1, workBench(w4, 200, 1, false));
//		bps8.addResult("2 clients", 2, workBench(w4, 200, 2, false));
//		bps8.addResult("3 clients", 3, workBench(w4, 200, 3, false));
//		bps8.addResult("4 clients", 4, workBench(w4, 200, 4, false));
//		plotter.addBPSeries(bps8);
//		plotter.addXYPointsSeries(bps8);
	}
	
	private void setConfig() {
		b.shareConfig(config);
	}
	
	private LoadResults loadBench(Dataset d, int bufferSize) {
		return b.startLoadBench(d, bufferSize);
	}
	
	private AggregateResult workBench(TraversalWorkload w) {
		return b.startWorkBench(w);
	}
	
	private AggregateResult workBench(IntensiveWorkload w) {
		return b.startWorkBench(w);
	}
}
