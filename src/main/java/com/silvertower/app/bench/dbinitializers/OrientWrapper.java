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
		if (batchLoading) {

			OGlobalConfiguration.ENVIRONMENT_CONCURRENT.setValue(false);
			if (remote) {
				/*OrientGraph graph = new OrientGraph("remote:" + getPath() + name);
				graph.getRawGraph().declareIntent(new OIntentMassiveInsert());
				return new IdGraph(graph);*/
				return new IdGraph<OrientBatchGraph>(new OrientBatchGraph("remote:" + getPath() + name));
			}
			else {
				/*OrientGraph graph = new OrientGraph("local:" + getPath() + name);
				graph.getRawGraph().declareIntent(new OIntentMassiveInsert());
				return new IdGraph(graph);*/
				return new IdGraph<OrientBatchGraph>(new OrientBatchGraph("local:" + getPath() + name));
			}
		}
		
		else {
			if (remote) {
				return new IdGraph<OrientGraph>(new OrientGraph("remote:" + getPath() + name));
			}
			else {
				return new IdGraph<OrientGraph>(new OrientGraph("local:" + getPath() + name));
			}
		}
	}

	public String getName() {
		return "Orient";
	}

	public String getPath() {
		return BenchmarkProperties.dbDirOrient;
	}	
}
