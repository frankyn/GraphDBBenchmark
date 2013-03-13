package com.silvertower.app.bench.load;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.silvertower.app.bench.main.ServerProperties;
import com.silvertower.app.bench.utils.Utilities;
import com.silvertower.app.bench.datasets.Dataset;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.dbinitializers.GraphConnectionInformations;
import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;

public class DBLoader {
 	
	private static GraphDescriptor initializeGraphDescriptor(Graph g, Dataset d, DBInitializer i) {
		String rexsterServerIp = ServerProperties.rexsterServerIp;
		int rexsterServerPort = ServerProperties.rexsterServerPort;
		GraphConnectionInformations gi = new GraphConnectionInformations(rexsterServerIp, rexsterServerPort, i.getName());
		return new GraphDescriptor(d, gi);
	}
	
	public static double[] batchLoadingBenchmark(Dataset d, DBInitializer initializer) {
		String suffix = d.getDatasetName() + "_batch";
		LoadBenchThread t = new LoadBenchThread(suffix, true, initializer, d);
		return loadBenchmark(d, initializer, t);
	}
	
	public static double[] normalLoadingBenchmark(Dataset d, DBInitializer initializer) {
		String suffix = d.getDatasetName();
		LoadBenchThread t = new LoadBenchThread(suffix, false, initializer, d);		
		return loadBenchmark(d, initializer, t);
	}
	
	public static double[] loadBenchmark(Dataset d, DBInitializer initializer, LoadBenchThread t) {
		double [] time = Utilities.benchTask(t);
		t.deleteUnusedDBs();
		return time;
	}
	
	public static GraphDescriptor loadDB(Dataset d, DBInitializer initializer) {
		Graph g = initializer.initialize();
		loadGraphML(g, initializeIS(d));
		System.out.println(g);
		System.out.println(d);
		return initializeGraphDescriptor(g, d, initializer);
	}
	
	public static InputStream initializeIS(Dataset d) {
		File datasetFile = new File(d.getDatasetFP());
		InputStream is = null;
		try {
			is = new FileInputStream(datasetFile);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return is;
	}
	
	public static void loadGraphML(Graph g, InputStream is) {
		try {
			GraphMLReader.inputGraph(g, is);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error while filling a database with a dataset");
			System.exit(-1);
		}
	}
}
