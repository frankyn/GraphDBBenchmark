package com.silvertower.app.bench.dbinitializers;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import com.silvertower.app.bench.main.ServerProperties;
import com.thinkaurelius.titan.core.TitanFactory;
import com.tinkerpop.blueprints.Graph;

public class TitanWrapper extends DBInitializer {
	
	private static final long serialVersionUID = 5735385409119575934L;
	private String backend;
	
	public TitanWrapper(String backend) {
		this.backend = backend;
	}
	
	public Graph initialize(String storageDir, boolean batchLoading) {
		createDirectory(storageDir);
		
		Configuration config = new BaseConfiguration();
		if (batchLoading) config.setProperty("storage.batch-loading", "true");
		if (backend.equals("local")) {
			config.setProperty("storage.directory", storageDir);
		}
		config.setProperty("storage.backend", backend);
		return TitanFactory.open(config);
	}

	public String getName() {
		return "titan"+backend;
	}

	public String getTempDirPath() {
		return ServerProperties.dbDirTitanTemp;
	}

	public String getWorkDirPath() {
		return ServerProperties.dbDirTitanWork;
	}
}
