package com.silvertower.app.bench.load;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.silvertower.app.bench.main.ServerProperties;
import com.silvertower.app.bench.utils.Utilities;
import com.silvertower.app.bench.datasets.Dataset;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.dbinitializers.GraphConnectionInformations;
import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;

public class DBLoader {
 	
	private static GraphDescriptor initializeGraphDescriptor(Graph g, List<Object> ids, Dataset d, DBInitializer i) {
		String rexsterServerIp = ServerProperties.rexsterServerIp;
		int rexsterServerPort = ServerProperties.rexsterServerPort;
		GraphConnectionInformations gi = new GraphConnectionInformations(rexsterServerIp, rexsterServerPort, i.getName());
		return new GraphDescriptor(ids, d, gi);
	}
	
	public static double batchLoadingBenchmark(Dataset d, DBInitializer initializer) {
		String suffix = d.getDatasetName() + "_batch";
		LoadBenchThread t = new LoadBenchThread(suffix, true, initializer, d);
		return loadBenchmark(d, initializer, t);
	}
	
	public static double normalLoadingBenchmark(Dataset d, DBInitializer initializer) {
		String suffix = d.getDatasetName();
		LoadBenchThread t = new LoadBenchThread(suffix, false, initializer, d);		
		return loadBenchmark(d, initializer, t);
	}
	
	public static double loadBenchmark(Dataset d, DBInitializer initializer, LoadBenchThread t) {
		double time;
		if (d.getNumberVertices() < 6000) time = Utilities.benchTask(t, true);
		else time = Utilities.benchTask(t, false);
		t.deleteUnusedDBs();
		return time;
	}
	
	public static GraphDescriptor loadDB(Dataset d, DBInitializer initializer) {
		Graph g = initializer.initialize(true);
		loadGraphML(g, initializeIS(d));
		initializer.shutdownGraph(g);
		g = initializer.initialize(false);
		List<Object> ids = scanDB(g);
		initializer.shutdownGraph(g);
		return initializeGraphDescriptor(g, ids, d, initializer);
	}
	
	private static List<Object> scanDB(Graph g) {
		// As OrientDB does not use the same id for RexsterGraph and the raw graph, we need to send
		// to tbe client the string representation of the vertices ids.
		boolean isOrientDB = g instanceof OrientGraph;
		Iterator <Vertex> iter = g.getVertices().iterator();
		ArrayList<Object> ids = new ArrayList<Object>();
		while (iter.hasNext()) {
			Object id = iter.next().getId();
			if (isOrientDB) ids.add(id.toString());
			else ids.add(id);
		}
		((TransactionalGraph) g).commit();
		return ids;
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
