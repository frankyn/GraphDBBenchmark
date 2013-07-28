package com.silvertower.app.bench.dbinitializers;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import com.silvertower.app.bench.annotations.Custom;
import com.silvertower.app.bench.main.ServerProperties;
import com.thinkaurelius.titan.core.TitanFactory;
import com.tinkerpop.blueprints.Graph;

@Custom
public class TitanCassandraWrapper extends DBInitializer {

	private static final long serialVersionUID = -4666166187789412864L;

	public Graph initialize(String storageDir, boolean batchLoading) {
		createDirectory(storageDir);
		
		Configuration config = new BaseConfiguration();
		if (batchLoading) {
			config.setProperty("storage.batch-loading", "true");
			config.setProperty("ids.block-size", 100000); 
		}
		
		config.setProperty("storage.backend", "embeddedcassandra");
		config.setProperty("storage.cassandra-config-dir", "cassandra.yaml");
		
		return TitanFactory.open(config);	
	}

	public String toString() {
		return "titancassandra";
	}

	public String getTempDirPath() {
		return ServerProperties.dbDirTitanTemp + "/titancassandra" + "/";
	}

	public String getWorkDirPath() {
		return ServerProperties.dbDirTitanWork + "/titancassandra" + "/";
	}
}
