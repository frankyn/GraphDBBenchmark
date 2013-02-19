package com.silvertower.app.bench.dbinitializers;

import com.silvertower.app.bench.main.BenchmarkProperties;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.impls.dex.DexGraph;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;

public class DexWrapper extends DBInitializer {
	
	public Graph initialize(String name, boolean batchLoading) {
		createDirectory(name);
		if (batchLoading) {
			return new BatchGraph((TransactionalGraph) new DexGraph(getPath() + name));
		}
		else {
			return new DexGraph(getPath() + name);
		}
	}

	public String getName() {
		return "DEX";
	}

	public String getPath() {
		return BenchmarkProperties.dbDirDex;
	}
}
