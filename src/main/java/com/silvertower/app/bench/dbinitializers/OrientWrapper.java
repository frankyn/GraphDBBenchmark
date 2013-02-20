package com.silvertower.app.bench.dbinitializers;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.silvertower.app.bench.main.BenchmarkProperties;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBatchGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.util.wrappers.id.IdGraph;

public class OrientWrapper extends DBInitializer {
	private boolean remote;

	public OrientWrapper(boolean remote) {
		this.remote = remote;
	}
	
	public Graph initialize(String name, boolean batchLoading) {
		createDirectory(name);
		lastDBPath = getDirPath() + name;
		if (batchLoading) {

			OGlobalConfiguration.ENVIRONMENT_CONCURRENT.setValue(false);
			if (remote) {
				/*OrientGraph graph = new OrientGraph("remote:" + getPath() + name);
				graph.getRawGraph().declareIntent(new OIntentMassiveInsert());
				return new IdGraph(graph);*/
				return (lastGraphInitialized = new IdGraph<OrientBatchGraph>(new OrientBatchGraph("remote:" + getDirPath() + name)));
			}
			else {
				/*OrientGraph graph = new OrientGraph("local:" + getPath() + name);
				graph.getRawGraph().declareIntent(new OIntentMassiveInsert());
				return new IdGraph(graph);*/
				return (lastGraphInitialized = new IdGraph<OrientBatchGraph>(new OrientBatchGraph("local:" + getDirPath() + name)));
			}
		}
		
		else {
			if (remote) {
				return (lastGraphInitialized = new IdGraph<OrientGraph>(new OrientGraph("remote:" + getDirPath() + name)));
			}
			else {
				return (lastGraphInitialized = new IdGraph<OrientGraph>(new OrientGraph("local:" + getDirPath() + name)));
			}
		}
	}

	public String getName() {
		return "Orient";
	}

	public String getDirPath() {
		return BenchmarkProperties.dbDirOrient;
	}	
}
