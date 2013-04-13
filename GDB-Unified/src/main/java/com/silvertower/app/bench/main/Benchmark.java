package com.silvertower.app.bench.main;

import java.util.ArrayList;
import java.util.List;

import com.silvertower.app.bench.akka.BenchRunner;
import com.silvertower.app.bench.datasets.Dataset;
import com.silvertower.app.bench.datasets.SocialNetworkDataset;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.dbinitializers.DexWrapper;
import com.silvertower.app.bench.dbinitializers.Neo4jWrapper;
import com.silvertower.app.bench.dbinitializers.OrientWrapper;
import com.silvertower.app.bench.dbinitializers.TitanWrapper;
import com.silvertower.app.bench.utils.PlotResult;
import com.silvertower.app.bench.utils.Plotter;
import com.silvertower.app.bench.workload.ShortestPathWorkload;
import com.silvertower.app.bench.workload.IntensiveWorkload;
import com.silvertower.app.bench.workload.NeighborhoodWorkload;
import com.silvertower.app.bench.workload.GetVerticesByIDIntensiveWorkload;
import com.silvertower.app.bench.workload.GetVerticesByPropIntensiveWorkload;
import com.silvertower.app.bench.workload.UpdateVerticesIntensiveWorkload;

public class Benchmark {
	private Plotter plotter = new Plotter();
	public void start(BenchRunner b) {
		List<DBInitializer> initializers = new ArrayList<DBInitializer>();
		initializers.add(new Neo4jWrapper());
		initializers.add(new DexWrapper());
//		initializers.add(new TitanWrapper("local"));
//		initializers.add(new TitanWrapper("cassandra"));
//		initializers.add(new OrientWrapper());
		
		for (DBInitializer i: initializers) {
			benchmark(i, b);
			b.vanishDB();
		}
		
		plotter.plotResults();
		
		b.shutdownSystem();
	}
	
	private void benchmark(DBInitializer i, BenchRunner b) {
		SocialNetworkDataset d = new SocialNetworkDataset(100000);
		b.load(i, d);
		
		BoxPlotSeries bps = new BoxPlotSeries("Shortest paths search", i.getName(), "Time");
		bps.addResult("2 hops limit", b.startWorkBench(new ShortestPathWorkload(2)));
		bps.addResult("3 hops limit", b.startWorkBench(new ShortestPathWorkload(3)));
		bps.addResult("4 hops limit", b.startWorkBench(new ShortestPathWorkload(4)));
		plotter.addBPSeries(bps);
		
		BoxPlotSeries bps1 = new BoxPlotSeries("Neighborhood breadth-first exploration", i.getName(), "Time");
		bps1.addResult("2 hops limit", b.startWorkBench(new NeighborhoodWorkload(2)));
		bps1.addResult("3 hops limit", b.startWorkBench(new NeighborhoodWorkload(3)));
		bps1.addResult("4 hops limit", b.startWorkBench(new NeighborhoodWorkload(4)));
		plotter.addBPSeries(bps1);
		
		IntensiveWorkload w1 = new GetVerticesByIDIntensiveWorkload();
		
		BoxPlotSeries bps2 = new BoxPlotSeries("Read ID Intensive 1 client", i.getName(), "Time");
		bps2.addResult("100 reads", b.startWorkBench(w1, 100, 1));
		bps2.addResult("200 reads", b.startWorkBench(w1, 200, 1));
		bps2.addResult("300 reads", b.startWorkBench(w1, 300, 1));
		bps2.addResult("400 reads", b.startWorkBench(w1, 400, 1));
		plotter.addBPSeries(bps2);
		
		BoxPlotSeries bps3 = new BoxPlotSeries("Read by ID Intensive 200 ops", i.getName(), "Time");
		bps3.addResult("1 client", b.startWorkBench(w1, 100, 1));
		bps3.addResult("2 clients", b.startWorkBench(w1, 100, 2));
		bps3.addResult("3 clients", b.startWorkBench(w1, 100, 3));
		bps3.addResult("4 clients", b.startWorkBench(w1, 100, 4));
		plotter.addBPSeries(bps3);
		
		IntensiveWorkload w2 = new GetVerticesByPropIntensiveWorkload();
		
		BoxPlotSeries bps4 = new BoxPlotSeries("Read by property Intensive 1 client", i.getName(), "Time");
		bps4.addResult("100 reads", b.startWorkBench(w2, 100, 1));
		bps4.addResult("200 reads", b.startWorkBench(w2, 200, 1));
		bps4.addResult("300 reads", b.startWorkBench(w2, 300, 1));
		bps4.addResult("400 reads", b.startWorkBench(w2, 400, 1));
		plotter.addBPSeries(bps4);
		
		BoxPlotSeries bps5 = new BoxPlotSeries("Read by property Intensive 200 ops", i.getName(), "Time");
		bps5.addResult("1 client", b.startWorkBench(w2, 200, 1));
		bps5.addResult("2 clients", b.startWorkBench(w2, 200, 2));
		bps5.addResult("3 clients", b.startWorkBench(w2, 200, 3));
		bps5.addResult("4 clients", b.startWorkBench(w2, 200, 4));
		plotter.addBPSeries(bps5);
		
		IntensiveWorkload w3 = new UpdateVerticesIntensiveWorkload();
		
		BoxPlotSeries bps6 = new BoxPlotSeries("Read Write Intensive 1 client", i.getName(), "Time");
		bps6.addResult("100 reads", b.startWorkBench(w3, 100, 1));
//		bps6.addResult("200 reads", b.startWorkBench(w3, 200, 1));
//		bps6.addResult("300 reads", b.startWorkBench(w3, 300, 1));
//		bps6.addResult("400 reads", b.startWorkBench(w3, 400, 1));
//		plotter.addBPSeries(bps6);
//		
//		BoxPlotSeries bps7 = new BoxPlotSeries("Read Write Intensive 200 ops", i.getName(), "Time");
//		bps7.addResult("1 client", b.startWorkBench(w3, 200, 1));
//		bps7.addResult("2 clients", b.startWorkBench(w3, 200, 2));
//		bps7.addResult("3 clients", b.startWorkBench(w3, 200, 3));
//		bps7.addResult("4 clients", b.startWorkBench(w3, 200, 4));
//		plotter.addBPSeries(bps7);
	}
}
