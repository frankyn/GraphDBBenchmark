package com.silvertower.app.bench.main;


import com.silvertower.app.bench.datasetsgeneration.SocialNetworkDatasetGenerator;
import com.silvertower.app.bench.load.SocialNetworkDBLoader;
import com.silvertower.app.bench.dbinitializers.*;
import com.silvertower.app.bench.utils.*;
import com.tinkerpop.blueprints.Graph;

public class BenchLauncher {	
	private static void socialNetworkBenchmark() {
		SocialNetworkDatasetGenerator sg = new SocialNetworkDatasetGenerator(100000);
		sg.generate();
		SocialNetworkDBLoader sl = new SocialNetworkDBLoader(sg);
		
		Utilities.log("DEX");
		Graph dexGraph = sl.load(new DexInitializer(), null, BenchmarkProperties.dbDirDex);
		
		Utilities.log("Neo4j");
		Graph neo4jGraph = sl.load(new Neo4jInitializer(), new Neo4jBatchInitializer(), BenchmarkProperties.dbDirNeo4j);
		
		Utilities.log("Orient");
		Graph orientGraph = sl.load(new OrientInitializer(false), new OrientBatchInitializer(false), BenchmarkProperties.dbDirOrient);
		
		Utilities.log("Titan");
		Graph titanGraph = sl.load(new TitanInitializer(false, null), null, BenchmarkProperties.dbDirTitan);
	}
	
	public static void main(String [] args) {		
		socialNetworkBenchmark();
	}
}
