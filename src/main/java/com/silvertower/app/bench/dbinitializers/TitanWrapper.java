package com.silvertower.app.bench.dbinitializers;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import com.silvertower.app.bench.main.BenchmarkProperties;
import com.thinkaurelius.titan.core.TitanFactory;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;

public class TitanWrapper extends DBInitializer {
	private Configuration config;
	private boolean inMemory;
	
	public TitanWrapper(boolean inMemory, Configuration config) {
		this.config = config;
		this.inMemory = inMemory;
	}
	
	public Graph initialize(String name, boolean batchLoading) {
		createDirectory(name);
		if (batchLoading) {
			Configuration config = new BaseConfiguration();
			config.setProperty("storage.batch-loading", "true");
			config.setProperty("storage.directory", getPath() + name);
			return new BatchGraph((TransactionalGraph) TitanFactory.open(config));
		}
		
		else {
			if (inMemory) {
				return TitanFactory.openInMemoryGraph();
			}
			else if (config != null) {
				return TitanFactory.open(config);
			}
			else {
				return TitanFactory.open(getPath() + name);
			}
		}
		
	}

	public String getName() {
		return "Titan";
	}

	public String getPath() {
		return BenchmarkProperties.dbDirTitan;
	}
}
