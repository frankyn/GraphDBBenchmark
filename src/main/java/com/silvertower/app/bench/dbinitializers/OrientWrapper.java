package com.silvertower.app.bench.dbinitializers;

import com.silvertower.app.bench.main.BenchmarkProperties;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.orient.OrientBatchGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.util.wrappers.id.IdGraph;

public class OrientWrapper implements DBInitializer {
	private boolean remote;

	public OrientWrapper(boolean remote) {
		this.remote = remote;
	}
	
	public Graph initialize(String dbPath, boolean batchLoading) {
		if (batchLoading) {
			if (remote) {
				return new OrientBatchGraph("remote:" + dbPath);
			}
			else {
				return new OrientBatchGraph("local:" + dbPath);
			}
		}
		
		else {
			if (remote) {
				return new IdGraph(new OrientGraph("remote:" + dbPath));
			}
			else {
				return new IdGraph(new OrientGraph("local:" + dbPath));
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
