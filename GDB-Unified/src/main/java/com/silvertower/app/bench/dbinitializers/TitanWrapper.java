package com.silvertower.app.bench.dbinitializers;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import com.silvertower.app.bench.main.ServerProperties;
import com.thinkaurelius.titan.core.TitanFactory;
import com.tinkerpop.blueprints.Graph;

public class TitanWrapper extends DBInitializer {
	
	private static final long serialVersionUID = 5735385409119575934L;

	public Graph initialize(String name, boolean batchLoading) {
		String dir = getTempDirPath();
		createDirectory(dir + name);
		
		if (batchLoading) {
			Configuration config = new BaseConfiguration();
			config.setProperty("storage.batch-loading", "true");
			config.setProperty("storage.directory", dir + name);
			return TitanFactory.open(config);
		}
		
		else {
			return TitanFactory.open(dir + name);
		}
	}

	public String getName() {
		return "titan";
	}

	public Graph initialize(boolean batchLoading) {
		String dir = getWorkDirPath();
		createDirectory(dir);
		Configuration config = new BaseConfiguration();
		if (batchLoading) config.setProperty("storage.batch-loading", "true");
		config.setProperty("storage.directory", dir);
		return TitanFactory.open(config);
	}

	public String getTempDirPath() {
		return ServerProperties.dbDirTitanTemp;
	}

	public String getWorkDirPath() {
		return ServerProperties.dbDirTitanWork;
	}
}
