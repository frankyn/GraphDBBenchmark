package com.silvertower.app.bench.main;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.PropertyConfigurator;

import bb.util.Benchmark;

import com.orientechnologies.orient.core.Orient;
import com.silvertower.app.bench.datasetsgeneration.Dataset;
import com.silvertower.app.bench.datasetsgeneration.SocialNetworkDataset;
import com.silvertower.app.bench.dbinitializers.*;
import com.silvertower.app.bench.load.DBLoader;
import com.silvertower.app.bench.utils.*;
import com.silvertower.app.bench.workload.*;
import com.thinkaurelius.titan.core.TitanFactory;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBatchGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;
import com.tinkerpop.blueprints.util.wrappers.id.IdGraph;

public class BenchLauncher {
	/*private static void load(String name, Dataset d) {
		OrientBatchGraph g = new OrientBatchGraph("local:" + name);
		try {
			InputStream s = new FileInputStream(d.getDatasetFP());
			GraphMLReader.inputGraph(g, s);
		} catch (Exception e) {}
		
		g.shutdown();
	}*/
	
	private static void runBenchmarks() {
		/*Dataset d10000 = new SocialNetworkDataset(10000);
		Dataset d50000 = new SocialNetworkDataset(50000);
		Dataset d100000 = new SocialNetworkDataset(100000);
		Dataset d250000 = new SocialNetworkDataset(250000);
		Dataset[] ds = {d250000};

		for (Dataset d: ds) {
			long before = System.currentTimeMillis();
			load(d.getDatasetName(), d);
			System.out.println("Time:" + (System.currentTimeMillis() - before));
		}
		*/
		
		initiateBenchmark();
		List<DBInitializer> initializers = new ArrayList<DBInitializer>();
		initializers.add(new TitanWrapper(false, null));
		//initializers.add(new Neo4jWrapper());
		//initializers.add(new OrientWrapper(false));
		//initializers.add(new DexWrapper());
		socialBenchmark(initializers);
	}
	
	public static void socialBenchmark(List<DBInitializer> initializers) {
		Dataset d2000 = new SocialNetworkDataset(2000);
		Dataset d4000 = new SocialNetworkDataset(4000);
		Dataset d8000 = new SocialNetworkDataset(8000);
		Dataset d10000 = new SocialNetworkDataset(10000);
		Dataset d50000 = new SocialNetworkDataset(50000);
		Dataset d100000 = new SocialNetworkDataset(100000);
		Dataset d250000 = new SocialNetworkDataset(250000);
		
		TraversalWorkload dw = new DijkstraWorkload();
		TraversalWorkload vew = new VerticesExplorationWorkload();
		TraversalWorkload eew = new EdgesExplorationWorkload();
		TraversalWorkload nw = new NeighborhoodWorkload(3);
		IntensiveWorkload riiw = new ReadIDIntensiveWorkload();
		IntensiveWorkload rpiw = new ReadPropIntensiveWorkload();
		IntensiveWorkload uiw = new UpdateIntensiveWorkload();
		
		for (DBInitializer initializer: initializers) {
			Logger log = new Logger(initializer.getName(), "Social benchmark");
			ArrayList<Dataset> socialDatasets = new ArrayList<Dataset>();
			
			socialDatasets.add(d2000);
			socialDatasets.add(d4000);
			/*socialDatasets.add(d8000);
			socialDatasets.add(d10000);
			socialDatasets.add(d50000);
			socialDatasets.add(d100000);
			socialDatasets.add(d250000);*/
			
			DBLoader.batchLoadingBenchmark(socialDatasets, initializer, log);
			List<GraphDescriptor> gDescs = DBLoader.normalLoadingBenchmark(socialDatasets, initializer, log);
			
			dw.work(gDescs.subList(0, 2), log);
			
			/*vew.work(gDescs, log);
			eew.work(gDescs, log);
			nw.work(gDescs.subList(0, 1), log);
			riiw.work(gDescs.get(gDescs.size()-1), log);
			uiw.work(gDescs.get(gDescs.size()-1), log);
			rpiw.work(gDescs.get(gDescs.size()-1), log);*/
			
			log.closeLogger();
		}
	}
	
	public static void initiateBenchmark() {
		PropertyConfigurator.configure("log4j.properties");
		File logDir = new File(BenchmarkProperties.logDir);
		if (!logDir.mkdir()) {
			Utilities.deleteDirectory(BenchmarkProperties.logDir);
			logDir.mkdir();
		}
		File dbsDir = new File(BenchmarkProperties.dbsDir);
		if (!dbsDir.mkdir()) {
			Utilities.deleteDirectory(BenchmarkProperties.dbsDir);
			dbsDir.mkdir();
		}
		/*File datasetsDir = new File(BenchmarkProperties.datasetsDir);
		if (!datasetsDir.mkdir()) {
			Utilities.deleteDirectory(BenchmarkProperties.datasetsDir);
			datasetsDir.mkdir();
		}*/
		File plotsDir = new File(BenchmarkProperties.plotsDir);
		if (!plotsDir.mkdir()) {
			Utilities.deleteDirectory(BenchmarkProperties.plotsDir);
			plotsDir.mkdir();
		}
	}
	
	public static void main(String [] args) {
		runBenchmarks();
	}
}
