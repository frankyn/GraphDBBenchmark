package com.silvertower.app.bench.load;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import com.silvertower.app.bench.datasetsgeneration.SocialNetworkDataset;
import com.silvertower.app.bench.utils.Utilities;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.dbinitializers.GraphProperty;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;

public class SocialNetworkDBLoader extends GraphMLLoader implements GraphLoader {
	private SocialNetworkDataset sg;
	private GraphDescriptor gDesc;
	
	public SocialNetworkDBLoader(SocialNetworkDataset sg) {
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
			fillGraphDescriptor(g1);

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

	private void fillGraphDescriptor(Graph g) {
		gDesc = new GraphDescriptor();
		Iterator <Vertex> iter = g.getVertices().iterator();
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

		gDesc.setNbVertices(sg.getNumberVertices());
		for (GraphProperty property: sg.getProperties()) {
			gDesc.addProperty(property);
		}	
	}
	
	public GraphDescriptor getGraphDescriptor() {
		return gDesc;
	}
}
