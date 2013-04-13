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
			DexGraph g = new DexGraph(dir + name, ServerProperties.resourcesDirPath + "dex.cfg");
			g.label.set("default");
			return (new BatchGraph<TransactionalGraph>(g));
		}
		else {
			DexGraph g = new DexGraph(dir + name, ServerProperties.resourcesDirPath + "dex.cfg");
			g.label.set("default");
			return g;
		}
	}

	public Graph initialize(boolean batchLoading) {
		String dir = getWorkDirPath();
		if (batchLoading) {
			DexGraph g = new DexGraph(dir + "dex", ServerProperties.resourcesDirPath + "dex.cfg");
			g.label.set("default");
			return (new BatchGraph<TransactionalGraph>(g));
		}
		else {
			DexGraph g = new DexGraph(dir + "dex", ServerProperties.resourcesDirPath + "dex.cfg");
			g.label.set("default");
			return g;
		}
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
