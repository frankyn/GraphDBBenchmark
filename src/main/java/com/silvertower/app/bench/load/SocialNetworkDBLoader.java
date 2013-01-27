package com.silvertower.app.bench.load;

import java.io.File;
import java.io.IOException;

import com.silvertower.app.bench.datasetsgeneration.SocialNetworkDatasetGenerator;
import com.silvertower.app.bench.utils.Utilities;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;

public class SocialNetworkDBLoader extends GraphMLLoader implements GraphLoader {
	private SocialNetworkDatasetGenerator sg;
	
	public SocialNetworkDBLoader(SocialNetworkDatasetGenerator sg) {
		this.sg = sg;
	}
	
	public Graph load(DBInitializer graphInitializer, DBInitializer batchGraphInitializer, String dbDir) {
		Graph g1 = null;
		File datasetFile = new File(sg.getDatasetFP());
		try {
			String standardSocialSuffix = "social\\" + sg.getNumberVertices() + "_1";
			String batchSocialSuffix = "social\\" + sg.getNumberVertices() + "_2";
			
			Utilities.log("Load time for dataset " + sg.getDatasetFP());
				
			g1 = graphInitializer.initialize(dbDir + standardSocialSuffix);
			loadAndLogGraphML(g1, datasetFile, false);
			g1 = graphInitializer.initialize(dbDir + standardSocialSuffix);
			

			if (batchGraphInitializer != null) {
				Graph g2 = batchGraphInitializer.initialize(dbDir + batchSocialSuffix);
				loadAndLogGraphML(g2, datasetFile, true);
			}
			
			else {
				Graph g2 = new BatchGraph((TransactionalGraph) graphInitializer.initialize(dbDir + batchSocialSuffix));
				loadAndLogGraphML(g2, datasetFile, true);
			}			
			
		} catch (IOException e) {
			System.err.println("Error while filling a database with a social dataset");
			System.exit(-1);
		}
		
		return g1;
	}
}
