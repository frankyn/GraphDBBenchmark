package com.silvertower.app.bench.main;

import java.util.ArrayList;
import java.util.List;

import com.silvertower.app.bench.akka.BenchRunner;
import com.silvertower.app.bench.akka.Messages.AggregateResult;
import com.silvertower.app.bench.datasets.Dataset;
import com.silvertower.app.bench.datasets.SocialNetworkDataset;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.dbinitializers.DexWrapper;
import com.silvertower.app.bench.dbinitializers.Neo4jWrapper;
import com.silvertower.app.bench.dbinitializers.OrientWrapper;
import com.silvertower.app.bench.dbinitializers.TitanWrapper;
import com.silvertower.app.bench.utils.PointSeries;
import com.silvertower.app.bench.utils.Plotter;
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
	public void start(BenchRunner b) {
		this.b = b;
		List<DBInitializer> initializers = new ArrayList<DBInitializer>();
		addInitializers(initializers);
		
		for (DBInitializer i: initializers) {
			b.assignInitializer(i);
			this.currentDBName = i.getName();
			benchmark();
			b.vanishDB();
		}
		
		plotter.plotResults();
		
		b.shutdownSystem();
	}
	
	private void addInitializers(List<DBInitializer> initializers) {
		initializers.add(new Neo4jWrapper());
		initializers.add(new DexWrapper());
		initializers.add(new TitanWrapper("local"));
		initializers.add(new TitanWrapper("cassandra"));
		initializers.add(new OrientWrapper());
	}
	
	private void benchmark() {
		SocialNetworkDataset d = new SocialNetworkDataset(300000);
		PointSeries bps0 = new PointSeries(String.format("Loading DB with a %s dataset", d.getDatasetType()), currentDBName, "Time");
		bps0.addResult("300000 vertices", loadBench(d));
		plotter.addXYPointsSeries(bps0);
		
//		PointSeries bps = new PointSeries("Shortest paths search", currentDBName, "Time");
//		bps.addResult("2 hops limit", workBench(new ShortestPathWorkload(2)));
//		bps.addResult("3 hops limit", workBench(new ShortestPathWorkload(3)));
//		bps.addResult("4 hops limit", workBench(new ShortestPathWorkload(4)));
//		plotter.addBPSeries(bps);
//		
//		PointSeries bps1 = new PointSeries("Neighborhood breadth-first exploration", currentDBName, "Time");
//		bps1.addResult("2 hops limit", workBench(new NeighborhoodWorkload(2)));
//		bps1.addResult("3 hops limit", workBench(new NeighborhoodWorkload(3)));
//		bps1.addResult("4 hops limit", workBench(new NeighborhoodWorkload(4)));
//		plotter.addBPSeries(bps1);
//		
		IntensiveWorkload w1 = new GetVerticesByIDIntensiveWorkload();
		
		PointSeries bps2 = new PointSeries("Read ID Intensive 1 client", currentDBName, "Time");
		bps2.addResult("100 reads", workBench(w1, 100, 1));
		bps2.addResult("200 reads", workBench(w1, 200, 1));
		bps2.addResult("300 reads", workBench(w1, 300, 1));
		bps2.addResult("400 reads", workBench(w1, 400, 1));
		plotter.addBPSeries(bps2);
		
		PointSeries bps3 = new PointSeries("Read by ID Intensive 200 ops", currentDBName, "Clients", "Time");
		bps3.addResult("1 client", 1, workBench(w1, 200, 1));
		bps3.addResult("2 clients", 2, workBench(w1, 200, 2));
		bps3.addResult("3 clients", 3, workBench(w1, 200, 3));
		bps3.addResult("4 clients", 4, workBench(w1, 200, 4));
		plotter.addBPSeries(bps3);
		plotter.addXYPointsSeries(bps3);
		
		IntensiveWorkload w2 = new GetVerticesByPropIntensiveWorkload();
		
		PointSeries bps4 = new PointSeries("Read by property Intensive 1 client", currentDBName, "Time");
		bps4.addResult("100 reads", workBench(w2, 100, 1));
		bps4.addResult("200 reads", workBench(w2, 200, 1));
		bps4.addResult("300 reads", workBench(w2, 300, 1));
		bps4.addResult("400 reads", workBench(w2, 400, 1));
		plotter.addBPSeries(bps4);
		
		PointSeries bps5 = new PointSeries("Read by property Intensive 200 ops", currentDBName, "Clients", "Time");
		bps5.addResult("1 client", 1, workBench(w2, 200, 1));
		bps5.addResult("2 clients", 2, workBench(w2, 200, 2));
		bps5.addResult("3 clients", 3, workBench(w2, 200, 3));
		bps5.addResult("4 clients", 4, workBench(w2, 200, 4));
		plotter.addBPSeries(bps5);
		plotter.addXYPointsSeries(bps5);
		
		IntensiveWorkload w3 = new UpdateVerticesIntensiveWorkload();
		
		PointSeries bps6 = new PointSeries("Read Write Intensive 1 client", currentDBName, "Time");
		bps6.addResult("100 reads", workBench(w3, 100, 1));
		bps6.addResult("200 reads", workBench(w3, 200, 1));
		bps6.addResult("300 reads", workBench(w3, 300, 1));
		bps6.addResult("400 reads", workBench(w3, 400, 1));
		plotter.addBPSeries(bps6);
		
		PointSeries bps7 = new PointSeries("Read Write Intensive 200 ops", currentDBName, "Clients", "Time");
		bps7.addResult("1 client", 1, workBench(w3, 200, 1));
		bps7.addResult("2 clients", 2, workBench(w3, 200, 2));
		bps7.addResult("3 clients", 3, workBench(w3, 200, 3));
		bps7.addResult("4 clients", 4, workBench(w3, 200, 4));
		plotter.addBPSeries(bps7);
		plotter.addXYPointsSeries(bps7);
	}
	
	private AggregateResult loadBench(Dataset d) {
		return b.startLoadBench(d);
	}
	
	private AggregateResult workBench(TraversalWorkload w) {
		return b.startWorkBench(w);
	}
	
	private AggregateResult workBench(IntensiveWorkload w, int nOps, int nClients) {
		return b.startWorkBench(w, nOps, nClients);
	}
}
