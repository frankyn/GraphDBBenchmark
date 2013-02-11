package com.silvertower.app.bench.dbinitializers;

import com.silvertower.app.bench.main.BenchmarkProperties;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.impls.dex.DexGraph;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;

public class DexWrapper implements DBInitializer {
	
	public Graph initialize(String dbPath, boolean batchLoading) {
		if (batchLoading) {
			return new BatchGraph((TransactionalGraph) new DexGraph(dbPath));
		}
		else {
			return new DexGraph(dbPath);
		}
	}

	public String getName() {
		return "DEX";
	}

	public String getPath() {
		return BenchmarkProperties.dbDirDex;
	}
}
