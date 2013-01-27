package com.silvertower.app.bench.dbinitializers;

import com.tinkerpop.blueprints.impls.dex.DexGraph;

public class DexInitializer implements DBInitializer {
	public DexGraph initialize(String dbPath) {
		return new DexGraph(dbPath);
	}
}
