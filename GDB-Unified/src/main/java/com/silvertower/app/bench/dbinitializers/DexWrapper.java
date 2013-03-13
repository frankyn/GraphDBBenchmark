package com.silvertower.app.bench.dbinitializers;


import com.silvertower.app.bench.main.ServerProperties;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.impls.dex.DexGraph;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;

public class DexWrapper extends DBInitializer {

	private static final long serialVersionUID = 4694503791969544756L;

	public Graph initialize(String name, boolean batchLoading) {
		String dir = getTempDirPath();
		if (batchLoading) {
			return (new BatchGraph<TransactionalGraph>((TransactionalGraph) new DexGraph(dir + name)));
		}
		else {
			return (new DexGraph(dir + name));
		}
	}

	public Graph initialize() {
		String dir = getWorkDirPath();
		return (new BatchGraph<TransactionalGraph>((TransactionalGraph) new DexGraph(dir + "dex")));
	}

	public String getName() {
		return "dex";
	}

	public String getTempDirPath() {
		return ServerProperties.dbDirDexTemp;
	}

	public String getWorkDirPath() {
		return ServerProperties.dbDirDexWork;
	}
}
