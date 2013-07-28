package com.silvertower.app.bench.dbinitializers;


import com.silvertower.app.bench.annotations.Custom;
import com.silvertower.app.bench.main.ServerProperties;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.impls.dex.DexGraph;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;

@Custom
public class DexWrapper extends DBInitializer {

	private static final long serialVersionUID = 4694503791969544756L;

	public Graph initialize(String storageDir, boolean batchLoading) {
		if (batchLoading) {
			DexGraph g = new DexGraph(storageDir + "dex", ServerProperties.resourcesDirPath + "dex.cfg");
			g.label.set("default");
			return (new BatchGraph<TransactionalGraph>(g));
		}
		else {
			DexGraph g = new DexGraph(storageDir + "dex", ServerProperties.resourcesDirPath + "dex.cfg");
			g.label.set("default");
			return g;
		}
	}

	public String toString() {
		return "dex";
	}

	public String getTempDirPath() {
		return ServerProperties.dbDirDexTemp;
	}

	public String getWorkDirPath() {
		return ServerProperties.dbDirDexWork;
	}
}
