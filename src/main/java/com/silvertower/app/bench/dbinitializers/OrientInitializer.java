package com.silvertower.app.bench.dbinitializers;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class OrientInitializer implements DBInitializer {
	private boolean remote;
	
	public OrientInitializer(boolean remote) {
		this.remote = remote;
	}
	
	public OrientGraph initialize(String dbPath) {
		if (remote) {
			return new OrientGraph("remote:" + dbPath);
		}
		else {
			return new OrientGraph("local:" + dbPath);
		}
	}
}
