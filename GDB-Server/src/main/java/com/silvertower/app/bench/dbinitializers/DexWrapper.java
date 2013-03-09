package com.silvertower.app.bench.dbinitializers;


import com.silvertower.app.bench.main.BenchmarkProperties;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.impls.dex.DexGraph;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;

public class DexWrapper extends DBInitializer {
	public Graph initialize(String name, boolean batchLoading) {
		lastDBPath = getDirPath() + name;
		if (batchLoading) {
			System.out.println(getDirPath() + name);
			return (lastGraphInitialized = new BatchGraph<TransactionalGraph>((TransactionalGraph) new DexGraph(getDirPath() + name, "dex.cfg")));
		}
		else {
			return (lastGraphInitialized = new DexGraph(getDirPath() + name, "dex.cfg"));
		}
	}

	public String getName() {
		return "DEX";
	}

	public String getDirPath() {
		return BenchmarkProperties.dbDirDex;
	}
}
