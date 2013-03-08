package com.silvertower.app.bench.dbinitializers;

import com.orientechnologies.orient.core.Orient;
import com.silvertower.app.bench.main.BenchmarkProperties;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.orient.OrientBatchGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class OrientWrapper extends DBInitializer {
	private boolean remote;

	public OrientWrapper(boolean remote) {
		this.remote = remote;
	}
	
	public Graph initialize(String name, boolean batchLoading) {
		createDirectory(name);
		lastDBPath = getDirPath() + name;
		if (batchLoading) {
			if (remote) {
				return (lastGraphInitialized = new OrientBatchGraph("remote:" + getDirPath() + name));
			}
			else {
				return (lastGraphInitialized = new OrientBatchGraph("local:" + getDirPath() + name));
			}
		}
		
		else {
			if (remote) {
				return (lastGraphInitialized = new OrientGraph("remote:" + getDirPath() + name));
			}
			else {
				return (lastGraphInitialized = new OrientGraph("local:" + getDirPath() + name));
			}
		}
	}

	public String getName() {
		return "Orient";
	}

	public String getDirPath() {
		return BenchmarkProperties.dbDirOrient;
	}
	
	public void shutdownLastGraphInitialized() {
		getLastGraphInitialized().shutdown();
		Orient.instance().shutdown();
	}
}
