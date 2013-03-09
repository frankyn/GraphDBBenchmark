package com.silvertower.app.bench.dbinitializers;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import com.silvertower.app.bench.main.BenchmarkProperties;
import com.thinkaurelius.titan.core.TitanFactory;
import com.tinkerpop.blueprints.Graph;

public class TitanWrapper extends DBInitializer {
	private boolean inMemory;
	
	public TitanWrapper(boolean inMemory) {
		this.inMemory = inMemory;
	}
	
	public Graph initialize(String name, boolean batchLoading) {
		createDirectory(name);
		lastDBPath = getDirPath() + name;
		
		if (inMemory) {
			return (lastGraphInitialized = TitanFactory.openInMemoryGraph());
		}
		
		else if (batchLoading) {
			Configuration config = new BaseConfiguration();
			config.setProperty("storage.batch-loading", "true");
			config.setProperty("storage.directory", getDirPath() + name);
			return (lastGraphInitialized = TitanFactory.open(config));
		}
		
		else {
			return (lastGraphInitialized = TitanFactory.open(getDirPath() + name));
		}
		
	}

	public String getName() {
		return "Titan";
	}

	public String getDirPath() {
		return BenchmarkProperties.dbDirTitan;
	}
}
