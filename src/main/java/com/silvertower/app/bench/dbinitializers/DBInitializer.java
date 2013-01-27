package com.silvertower.app.bench.dbinitializers;

import com.tinkerpop.blueprints.Graph;

public interface DBInitializer {
	public Graph initialize(String dbPath);
}
