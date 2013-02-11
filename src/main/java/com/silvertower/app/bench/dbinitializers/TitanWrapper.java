package com.silvertower.app.bench.dbinitializers;

import org.apache.commons.configuration.Configuration;

import com.silvertower.app.bench.main.BenchmarkProperties;
import com.thinkaurelius.titan.core.TitanFactory;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;

public class TitanWrapper implements DBInitializer {
	private boolean inMemory;
	private Configuration config;
	
	public TitanWrapper(boolean inMemory, Configuration config) {
		this.inMemory = inMemory;
		this.config = config;
	}
	
	public Graph initialize(String dbPath, boolean batchLoading) {
		if (batchLoading) {
			if (inMemory) {
				return new BatchGraph((TransactionalGraph) TitanFactory.openInMemoryGraph());
			}
			else if (config != null) {
				return new BatchGraph((TransactionalGraph) TitanFactory.open(config));
			}
			else {
				return new BatchGraph((TransactionalGraph) TitanFactory.open(dbPath));
			}
		}
		
		else {
			if (inMemory) {
				return TitanFactory.openInMemoryGraph();
			}
			else if (config != null) {
				return TitanFactory.open(config);
			}
			else {
				return TitanFactory.open(dbPath);
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
