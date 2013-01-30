package com.silvertower.app.bench.main;


import com.silvertower.app.bench.datasetsgeneration.SocialNetworkDataset;
import com.silvertower.app.bench.load.SocialNetworkDBLoader;
import com.silvertower.app.bench.dbinitializers.*;
import com.silvertower.app.bench.utils.*;
import com.silvertower.app.bench.workload.*;
import com.tinkerpop.blueprints.Graph;

public class BenchLauncher {	
	private static void socialNetworkBenchmark() {
		Utilities.deleteDatabase(BenchmarkProperties.dbsDir);
		
		SocialNetworkDataset socialDataset = new SocialNetworkDataset(100);
		SocialNetworkDBLoader sl = new SocialNetworkDBLoader(socialDataset);
		VerticesExplorationWorkload vew = new VerticesExplorationWorkload();
		EdgesExplorationWorkload eew = new EdgesExplorationWorkload();
		ReadIntensiveWorkload riw = new ReadIntensiveWorkload();
		
		/*Utilities.log("DEX");
		Graph dexGraph = sl.load(new DexWrapper(), null, BenchmarkProperties.dbDirDex);
		vew.work(dexGraph, sl.getGraphDescriptor());
		eew.work(dexGraph, sl.getGraphDescriptor());
		riw.work(dexGraph, sl.getGraphDescriptor());*/
		
		/*Utilities.log("Neo4j");
		Graph neo4jGraph = sl.load(new Neo4jWrapper(), new Neo4jBatchWrapper(), BenchmarkProperties.dbDirNeo4j);
		vew.work(neo4jGraph, sl.getGraphDescriptor());
		eew.work(neo4jGraph, sl.getGraphDescriptor());
		riw.work(neo4jGraph, sl.getGraphDescriptor());*/
		
		Utilities.log("Orient");
		Graph orientGraph = sl.load(new OrientWrapper(false), new OrientBatchWrapper(false), BenchmarkProperties.dbDirOrient);
		vew.work(orientGraph, sl.getGraphDescriptor());
		eew.work(orientGraph, sl.getGraphDescriptor());
		riw.work(orientGraph, sl.getGraphDescriptor());
		
		/*Utilities.log("Titan");
		Graph titanGraph = sl.load(new TitanWrapper(false, null), null, BenchmarkProperties.dbDirTitan);
		vew.work(titanGraph, sl.getGraphDescriptor());
		eew.work(titanGraph, sl.getGraphDescriptor());
		riw.work(titanGraph, sl.getGraphDescriptor());*/
	}
	
	public static void main(String [] args) {		
		socialNetworkBenchmark();
	}
}
