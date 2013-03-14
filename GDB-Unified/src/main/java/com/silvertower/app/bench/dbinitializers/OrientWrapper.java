package com.silvertower.app.bench.dbinitializers;

import com.orientechnologies.orient.core.Orient;
import com.silvertower.app.bench.main.ServerProperties;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.orient.OrientBatchGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class OrientWrapper extends DBInitializer {
	
	private static final long serialVersionUID = -1546694471943663510L;

	public Graph initialize(String name, boolean batchLoading) {
		String dir = getWorkDirPath();
		createDirectory(dir + name);
		if (batchLoading) {
			return new OrientBatchGraph("local:" + dir + name);
		}
		
		else {
			return new OrientGraph("local:" + dir + name);
		}
	}
	
	public Graph initialize() {
		String dir = getWorkDirPath();
		createDirectory(dir);
		return new OrientBatchGraph("local:" + dir);
	}

	public String getName() {
		return "orient";
	}
	
	public void shutdownLastGraphInitialized(Graph g) {
		g.shutdown();
		Orient.instance().shutdown();
	}

	public String getTempDirPath() {
		return ServerProperties.dbDirOrientTemp;
	}

	public String getWorkDirPath() {
		return ServerProperties.dbDirOrientWork;
	}
}