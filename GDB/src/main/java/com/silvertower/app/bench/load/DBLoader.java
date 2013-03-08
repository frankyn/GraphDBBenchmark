package com.silvertower.app.bench.load;


import com.silvertower.app.bench.utils.Utilities;
import com.silvertower.app.bench.datasets.Dataset;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.tinkerpop.blueprints.Graph;

public class DBLoader {
 	
	private static void fillGraphDescriptor(GraphDescriptor gDesc, Graph g, Dataset d) {
		gDesc.setGraph(g);
		gDesc.setDataset(d);
	}
	
	public static double[] batchLoadingBenchmark(Dataset d, DBInitializer initializer, GraphDescriptor gDesc) {
		String suffix = d.getDatasetName() + "_batch";
		LoadBenchThread t = new LoadBenchThread(suffix, true, initializer, d);
		return loadBenchmark(d, initializer, gDesc, t);
	}
	
	public static double[] normalLoadingBenchmark(Dataset d, DBInitializer initializer, GraphDescriptor gDesc) {
		String suffix = d.getDatasetName();
		LoadBenchThread t = new LoadBenchThread(suffix, false, initializer, d);		
		return loadBenchmark(d, initializer, gDesc, t);
	}
	
	public static double[] loadBenchmark(Dataset d, DBInitializer initializer, GraphDescriptor gDesc, LoadBenchThread t) {
		double [] time = Utilities.benchTask(t);
		t.deleteUnusedDBs();
		fillGraphDescriptor(gDesc, t.getFinalGraph(), d);
		return time;
	}
}
