package com.silvertower.app.bench.load;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import com.silvertower.app.bench.datasetsgeneration.Dataset;
import com.silvertower.app.bench.main.Globals;
import com.silvertower.app.bench.utils.Logger;
import com.silvertower.app.bench.workload.ResultPair;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;

public class DBLoader{

	public static GraphDescriptor load(Dataset ds, DBInitializer graphInitializer, String dbDir, Logger log, boolean batchLoading) {
		File datasetFile = new File(ds.getDatasetFP());
		String suffix;
		Graph g;
		GraphDescriptor gDesc = null;
		
		if (batchLoading) {
			suffix = ds.getDatasetName() + ds.getNumberVertices() + "_batch";
			g = graphInitializer.initialize(dbDir + suffix, true);
			double t = loadGraphML(g, datasetFile, true, log);
			log.logResult(new ResultPair(ds.getNumberVertices(), t));
			System.out.println("Ended batch loading");
		}
		
		else {
			suffix = ds.getDatasetName() + ds.getNumberVertices();
			g = graphInitializer.initialize(dbDir + suffix, false);
			double t = loadGraphML(g, datasetFile, false, log);
			log.logResult(new ResultPair(ds.getNumberVertices(), t));
			System.out.println("Ended normal loading");
			gDesc = new GraphDescriptor(g, ds);
			fillGraphDescriptor(gDesc);
		}
		
		return gDesc;
		
		/*String standardSuffix = ds.getDatasetName() + ds.getNumberVertices() + "_1";
		String batchSuffix = ds.getDatasetName() + ds.getNumberVertices() + "_2";
		
		Graph g1 = graphInitializer.initialize(dbDir + standardSuffix, false);
		double t1Seconds = loadGraphML(g1, datasetFile, false, log);
		log.log("Load time for dataset " + ds.getDatasetFP() + " without batchloading", t1Seconds);
		System.out.println("Ended normal loading");
		GraphDescriptor gDesc = new GraphDescriptor(g1, ds);
		fillGraphDescriptor(gDesc);

		Graph g2 = graphInitializer.initialize(dbDir + batchSuffix, true);
		double t2Seconds = loadGraphML(g2, datasetFile, true, log);
		log.log("Load time for dataset " + ds.getDatasetFP() + " using batchloading", t2Seconds);
		System.out.println("Ended batch loading");
		
		return gDesc;*/
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
	
	private static double loadGraphML(Graph g, File datasetPath, boolean isBatchLoad, Logger log) {
		InputStream is;
		long beforeTs = System.nanoTime();
		
		try {
			is = new FileInputStream(datasetPath);
			GraphMLReader.inputGraph(g, is);
		} catch (IOException e) {
			System.err.println("Error while filling a database with a dataset");
			System.exit(-1);
		}

		// As batch graphs do not handle transactions, we use shutdown() to end all the pending
		// operations.
		if (isBatchLoad) {
			g.shutdown();
			long afterTs = System.nanoTime();
			return (afterTs - beforeTs) / (Globals.nanosToSFactor * 1.0);
		}
		else {
			((TransactionalGraph)g).stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
			long afterTs = System.nanoTime();
			return (afterTs - beforeTs) / (Globals.nanosToSFactor * 1.0);
		}
	}
}
