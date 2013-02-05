package com.silvertower.app.bench.dbinitializers;

import com.tinkerpop.blueprints.impls.orient.OrientBatchGraph;

public class OrientBatchWrapper extends GraphDescriptor implements DBInitializer{
	private boolean remote;
	
	public OrientBatchWrapper(boolean remote) {
		this.remote = remote;
	}
	
	public OrientBatchGraph initialize(String dbPath) {
		if (remote) {
			return new OrientBatchGraph("remote:" + dbPath);
		}
		else {
			return new OrientBatchGraph("local:" + dbPath);
		}
	}
}
