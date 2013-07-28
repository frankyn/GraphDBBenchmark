package com.silvertower.app.bench.dbinitializers;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.silvertower.app.bench.annotations.Custom;
import com.silvertower.app.bench.main.ServerProperties;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.batch.OrientBatchGraph;

@Custom
public class OrientWrapper extends DBInitializer {
	
	private static final long serialVersionUID = -1546694471943663510L;
	
	public Graph initialize(String storageDir, boolean batchLoading) {
		createDirectory(storageDir);
		OGlobalConfiguration.STORAGE_KEEP_OPEN.setValue(0);
		if (batchLoading) {
			return new OrientBatchGraph("local:" + storageDir);
		}
		
		else {
			return new OrientGraph("local:" + storageDir);
		}
	}

	public String toString() {
		return "orient";
	}

	public String getTempDirPath() {
		return ServerProperties.dbDirOrientTemp;
	}

	public String getWorkDirPath() {
		return ServerProperties.dbDirOrientWork;
	}
}
