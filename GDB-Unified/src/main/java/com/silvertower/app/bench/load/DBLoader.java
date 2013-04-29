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
import com.silvertower.app.bench.akka.GraphDescriptor;
import com.silvertower.app.bench.datasets.Dataset;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.dbinitializers.GraphProperty;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;
import com.tinkerpop.blueprints.util.wrappers.WrapperGraph;

public class DBLoader {
 	public static List<Double> loadTimes;
 	
	private static GraphDescriptor initializeGraphDescriptor(Graph g, List<Object> vIds, List<Object> eIds, 
			Dataset d, DBInitializer i) {
		return new GraphDescriptor(vIds, eIds, i.getName(), d);
	}
	
	public static void batchLoadingBenchmark(Dataset d, File datasetFile, DBInitializer initializer) {
		String suffix = d.getDatasetName() + "_batch";
		loadTimes = loadBenchmark(datasetFile, initializer, suffix, true);
	}
	
	public static void normalLoadingBenchmark(Dataset d, File datasetFile, DBInitializer initializer) {
		String suffix = d.getDatasetName();
		loadTimes =  loadBenchmark(datasetFile, initializer, suffix, false);
	}
	
	public static GraphDescriptor loadDB(File f, Dataset d, DBInitializer initializer) {
		// Load the dataset multiple times in order to have the average load timeif the number of 
		// vertices exceeds 50000.
		loadTimes = new ArrayList<Double>();
		if (d.getNumberVertices() <= ServerProperties.maxNVerticesMultiLoad) {
			batchLoadingBenchmark(d, f, initializer);
		}
		Graph g = initializer.initialize(initializer.getWorkDirPath(), true);
		// Create vertices and edges indices:
		if (g.getFeatures().supportsVertexKeyIndex) { 
			for (GraphProperty p: d.getVertexProperties()) {
				Graph rawGraph;
				if (g instanceof WrapperGraph) rawGraph = ((WrapperGraph) g).getBaseGraph();
				else rawGraph = g;
					((KeyIndexableGraph) rawGraph).createKeyIndex(p.getFieldName(), Vertex.class);
			}
		}
		
//		if (g.getFeatures().supportsEdgeKeyIndex) {
//			for (GraphProperty p: d.getEdgesProperties()) {
//				Graph rawGraph = ((BatchGraph) g).getBaseGraph();
//				((KeyIndexableGraph) rawGraph).createKeyIndex(p.getFieldName(), Edge.class);
//			}
//		}
		long before = System.nanoTime();
		loadGraphML(g, initializeIS(f));
		long after = System.nanoTime();
		loadTimes.add((after - before) / 1000000000.0);
		
		initializer.shutdownGraph(g);
		g = initializer.initialize(initializer.getWorkDirPath(), false);
		
		List<Object> vIds = scanVertices(g);
		List<Object> eIds = scanEdges(g);
		initializer.shutdownGraph(g);
		return initializeGraphDescriptor(g, vIds, eIds, d, initializer);
	}
	
	private static List<Object> scanVertices(Graph g) {
		// As OrientDB does not use the same id for RexsterGraph and the raw graph, we need to send
		// to the client the string representation of the vertices ids.
		boolean needStringRep = g instanceof OrientGraph;
		Iterator <Vertex> iter = g.getVertices().iterator();
		ArrayList<Object> ids = new ArrayList<Object>();
		int count = 0;
		while (iter.hasNext()) {
			Object id = iter.next().getId();
			if (needStringRep) ids.add(id.toString());
			else ids.add(id);
			count++;
			if (count == 50000) break; // We limit the scan to 500000 vertices
		}
		((TransactionalGraph) g).commit();
		return ids;
	}
	
	private static List<Object> scanEdges(Graph g) {
		// As OrientDB does not use the same id for RexsterGraph and the raw graph, we need to send
		// to the client the string representation of the vertices ids.
		boolean needStringRep = g instanceof OrientGraph || g instanceof TitanGraph;
		Iterator <Edge> iter = g.getEdges().iterator();
		ArrayList<Object> ids = new ArrayList<Object>();
		int count = 0;
		while (iter.hasNext()) {
			Object id = iter.next().getId();
			if (needStringRep) ids.add(id.toString());
			else ids.add(id);
			count++;
			if (count == 500000) break; // We limit the scan to 500000 edges
		}
		((TransactionalGraph) g).commit();
		return ids;
	}
	
	private static List<Double> loadBenchmark(File datasetFile, DBInitializer initializer, String suffix, boolean batchLoading) {
		long before = System.nanoTime();
		int counter = 0;
		List<Double> times = new ArrayList<Double>();
		while (System.nanoTime() - before < ServerProperties.maxLoadTimeInNS) {
	    	Graph g = initializer.initialize(initializer.getTempDirPath() + suffix + counter, batchLoading);
	    	InputStream is = initializeIS(datasetFile);
	    	long beforeLoading = System.nanoTime();
			DBLoader.loadGraphML(g, is);
			long afterLoading = System.nanoTime();
			times.add((afterLoading - beforeLoading) / 1000000000.0);
			initializer.shutdownGraph(g);
			counter++;
		}
		
		deleteTempDBs(counter, initializer, suffix);
		return times;
	}
	
	public static void deleteTempDBs(int counter, DBInitializer initializer, String suffix) {
    	for (int i = 0; i < counter; i++) {
    		Utilities.deleteDirectory(initializer.getTempDirPath() + suffix + i);
    	}
	}

	public static InputStream initializeIS(File datasetFile) {
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
