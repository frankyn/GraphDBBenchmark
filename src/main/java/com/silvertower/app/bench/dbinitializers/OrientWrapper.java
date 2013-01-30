package com.silvertower.app.bench.dbinitializers;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.util.wrappers.id.IdGraph;

public class OrientWrapper extends GraphDescriptor implements DBInitializer {
	private boolean remote;

	public OrientWrapper(boolean remote) {
		this.remote = remote;
	}
	
	public IdGraph initialize(String dbPath) {
		if (remote) {
			return new IdGraph(new OrientGraph("remote:" + dbPath));
		}
		else {
			return new IdGraph(new OrientGraph("local:" + dbPath));
		}
	}
}
