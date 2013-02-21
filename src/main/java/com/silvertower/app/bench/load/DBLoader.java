package com.silvertower.app.bench.load;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bb.util.Benchmark;

import com.silvertower.app.bench.datasetsgeneration.Dataset;
import com.silvertower.app.bench.utils.Logger;
import com.silvertower.app.bench.utils.Utilities;
import com.silvertower.app.bench.workload.TimeResult;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;

public class DBLoader{
 	public static GraphDescriptor load(Dataset ds, DBInitializer graphInitializer) {
		File datasetFile = new File(ds.getDatasetFP());
		InputStream is;
		Graph g = graphInitializer.initialize(ds.getDatasetName(), true);
		try {
			is = new FileInputStream(datasetFile);
			GraphMLReader.inputGraph(g, is);
		} catch (IOException e) {
			System.err.println("Error while filling a database with a dataset");
			System.exit(-1);
		}
		Graph g1 = graphInitializer.initialize(ds.getDatasetName(), false);
		GraphDescriptor gDesc = new GraphDescriptor(g1, ds);
		fillGraphDescriptor(gDesc);
		return gDesc;
	}
	
	public static List<GraphDescriptor> normalLoadingBenchmark(List<Dataset> datasets, DBInitializer initializer, Logger log) {
		List<GraphDescriptor> gDescs = new ArrayList<GraphDescriptor>();
		log.logOperation("Load time for a " + datasets.get(0).getDatasetType() + " dataset without batchloading");
		for (final Dataset ds: datasets) {
			String suffix = ds.getDatasetName();
			LoadBenchThread t = new LoadBenchThread(suffix, false, initializer, ds);		
			double [] time = Utilities.benchTask(t);
			t.deleteUnusedDBs();
			log.logResult(new TimeResult(ds.getNumberVertices(), time[0], time[1]));
			GraphDescriptor gDesc = new GraphDescriptor(t.getFinalGraph(), ds);
			fillGraphDescriptor(gDesc);
			gDescs.add(gDesc);
			System.out.println("Ended normal loading");
		}
		
		log.plotResults("Number of vertices", "Time", "Wall time", "Cpu time");
		return gDescs;
	}
	
	public static void batchLoadingBenchmark(List<Dataset> datasets, DBInitializer initializer, Logger log) {
		log.logOperation("Load time for a " + datasets.get(0).getDatasetType() + " dataset using batchloading");
		
		for (final Dataset ds: datasets) {
			String suffix = ds.getDatasetName() + "_batch";
			LoadBenchThread t = new LoadBenchThread(suffix, true, initializer, ds);
			double [] time = Utilities.benchTask(t);
			t.deleteUnusedDBs();
			log.logResult(new TimeResult(ds.getNumberVertices(), time[0], time[1]));
			System.out.println("Ended batch loading");
		}
		
		log.plotResults("Number of vertices", "Time", "Wall time", "Cpu time");
	}
	
	private static void fillGraphDescriptor(GraphDescriptor gDesc) {
		Iterator <Vertex> iter = gDesc.getGraph().getVertices().iterator();
		Vertex current = iter.next();
		Object firstVertexID = current.getId();
		current = iter.next();
		
		if (firstVertexID.getClass().equals(String.class)) {
			Long stepBetweenVertexID = Long.parseLong(current.getId() + "") - Long.parseLong(firstVertexID + "");
			gDesc.setFirstVertexId(Long.parseLong(firstVertexID + ""));
			gDesc.setStepBetweenId(stepBetweenVertexID);
			gDesc.setVerticesIdClass(String.class);
		}
		
		else if (firstVertexID.getClass().equals(Long.class)) {
			Long stepBetweenVertexID = (Long)current.getId() - (Long)firstVertexID;
			gDesc.setFirstVertexId((Long)firstVertexID);
			gDesc.setStepBetweenId(stepBetweenVertexID);
			gDesc.setVerticesIdClass(Long.class);
		}
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
		gDesc = new GraphDescriptor(t.getFinalGraph(), d);
		fillGraphDescriptor(gDesc);
		return time;
	}
}
