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
 	
	private static GraphDescriptor initializeGraphDescriptor(Graph g, List<Object> vIds, List<Object> eIds, 
			Dataset d, DBInitializer i) {
		String rexsterServerIp = ServerProperties.rexsterServerIp;
		int rexsterServerPort = ServerProperties.rexsterServerPort;
		GraphConnectionInformations gi = new GraphConnectionInformations(rexsterServerIp, rexsterServerPort, i.getName());
		return new GraphDescriptor(vIds, eIds, d, gi);
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
		if (d.getNumberVertices() < 6000) time = Utilities.benchTask(t, 5).getMean().getTime();
		else time = Utilities.benchTask(t, 1).getMean().getTime();
		t.deleteUnusedDBs();
		return time;
	}
	
	public static GraphDescriptor loadDB(Dataset d, DBInitializer initializer) {
		Graph g = initializer.initialize(true);
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
		
		loadGraphML(g, initializeIS(d));
		initializer.shutdownGraph(g);
		g = initializer.initialize(false);
		List<Object> vIds = scanVertices(g);
		List<Object> eIds = scanEdges(g);
		initializer.shutdownGraph(g);
		return initializeGraphDescriptor(g, vIds, eIds, d, initializer);
	}
	
	private static List<Object> scanVertices(Graph g) {
		// As OrientDB does not use the same id for RexsterGraph and the raw graph, we need to send
		// to tbe client the string representation of the vertices ids.
		boolean needStringRep = g instanceof OrientGraph;
		Iterator <Vertex> iter = g.getVertices().iterator();
		ArrayList<Object> ids = new ArrayList<Object>();
		int count = 0;
		while (iter.hasNext()) {
			Object id = iter.next().getId();
			if (needStringRep) ids.add(id.toString());
			else ids.add(id);
			count++;
			if (count == 500000) break; // We limit the scan to 500000 vertices
		}
		((TransactionalGraph) g).commit();
		return ids;
	}
	
	private static List<Object> scanEdges(Graph g) {
		// As OrientDB does not use the same id for RexsterGraph and the raw graph, we need to send
		// to tbe client the string representation of the vertices ids.
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
