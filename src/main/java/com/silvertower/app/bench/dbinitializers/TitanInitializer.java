package com.silvertower.app.bench.dbinitializers;

import org.apache.commons.configuration.Configuration;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;

public class TitanInitializer implements DBInitializer {
	private boolean inMemory;
	private Configuration config;
	
	public TitanInitializer(boolean inMemory, Configuration config) {
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
