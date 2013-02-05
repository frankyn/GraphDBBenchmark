package com.silvertower.app.bench.dbinitializers;

import org.apache.commons.configuration.Configuration;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;

public class TitanWrapper extends GraphDescriptor implements DBInitializer {
	private boolean inMemory;
	private Configuration config;
	
	public TitanWrapper(boolean inMemory, Configuration config) {
		this.inMemory = inMemory;
		this.config = config;
	}
	
	public TitanGraph initialize(String dbPath) {
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
