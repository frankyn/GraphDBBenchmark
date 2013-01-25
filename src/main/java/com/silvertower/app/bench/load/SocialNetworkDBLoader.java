package com.silvertower.app.bench.load;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.silvertower.app.bench.datasetsgeneration.DatasetsGenerator;
import com.silvertower.app.bench.datasetsgeneration.SocialNetworkDatasetsGenerator;
import com.silvertower.app.bench.main.BenchmarkProperties;
import com.silvertower.app.bench.utils.Utilities;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.dex.DexGraph;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.impls.neo4jbatch.Neo4jBatchGraph;
import com.tinkerpop.blueprints.impls.orient.OrientBatchGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;

public class SocialNetworkDBLoader extends GraphMLLoader implements DBLoader {
	
	public Graph[] load(DatasetsGenerator sg) {
		File [] datasetsList = ((SocialNetworkDatasetsGenerator) sg).getLocation().listFiles();
		Graph [] graphs = new Graph [4];
		InputStream is;
		for (int i = 0; i < datasetsList.length; i++) {
			String fName = datasetsList[i].getName();
			try {
				Utilities.log("Load time for dataset " + fName + " without batch loading:\n");
				
				String standardSocialSuffix = "social\\" + fName + "_1";
				
				is = new FileInputStream(datasetsList[i]);
				graphs[0] = new DexGraph(BenchmarkProperties.dbDirDex + standardSocialSuffix);
				Utilities.log("Dex", loadAndLogGraphML(graphs[0], is));
				
				is = new FileInputStream(datasetsList[i]);
				graphs[1] = new OrientGraph(BenchmarkProperties.dbDirOrient + standardSocialSuffix);
				Utilities.log("Orient", loadAndLogGraphML(graphs[1], is));
				
				is = new FileInputStream(datasetsList[i]);
				graphs[2] = TitanFactory.open(BenchmarkProperties.dbDirTitan + standardSocialSuffix);
				Utilities.log("Titan", loadAndLogGraphML(graphs[2], is));
				
				is = new FileInputStream(datasetsList[i]);
				graphs[3] = new Neo4jGraph(BenchmarkProperties.dbDirNeo4j + standardSocialSuffix);
				Utilities.log("Neo4j", loadAndLogGraphML(graphs[3], is));
				
				//Utilities.deleteDatabase(BenchmarkProperties.dbsDir);
				
				Utilities.log("Load time for dataset " + fName + " with batch loading:\n");
				
				String batchSocialSuffix = "social\\" + fName + "_2";
				
				is = new FileInputStream(datasetsList[i]);
				graphs[0] = new BatchGraph<DexGraph>(new DexGraph(BenchmarkProperties.dbDirDex + batchSocialSuffix));
				Utilities.log("Dex", loadAndLogGraphML(graphs[0], is));
				
				is = new FileInputStream(datasetsList[i]);
				graphs[1] = new OrientBatchGraph(BenchmarkProperties.dbDirOrient + batchSocialSuffix);
				Utilities.log("Orient", loadAndLogGraphML(graphs[1], is));
				
				is = new FileInputStream(datasetsList[i]);
				graphs[2] = new BatchGraph<TitanGraph>(TitanFactory.open(BenchmarkProperties.dbDirTitan + batchSocialSuffix));
				Utilities.log("Titan", loadAndLogGraphML(graphs[2], is));

				is = new FileInputStream(datasetsList[i]);
				graphs[3] = new Neo4jBatchGraph(BenchmarkProperties.dbDirNeo4j + batchSocialSuffix);
				Utilities.log("Neo4j", loadAndLogGraphML(graphs[3], is));
				
				//if (i != datasetsList.length - 1) 
				//	Utilities.deleteDatabase(BenchmarkProperties.dbsDir);
				
			} catch (IOException e) {e.printStackTrace();System.out.println(e);}
		}
		return graphs;
	}
}
