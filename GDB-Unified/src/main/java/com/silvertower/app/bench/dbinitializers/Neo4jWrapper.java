package com.silvertower.app.bench.dbinitializers;

import com.silvertower.app.bench.main.ServerProperties;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.impls.neo4j.batch.Neo4jBatchGraph;

public class Neo4jWrapper extends DBInitializer {

	private static final long serialVersionUID = -7001472988542034752L;

	public Graph initialize(String name, boolean batchLoading) {
		String dir = getTempDirPath();
		createDirectory(dir + name);
		if (batchLoading) 
			return new Neo4jBatchGraph(dir + name);
		else 
			return new Neo4jGraph(dir + name);
	}

	public Graph initialize(boolean batchLoading) {
		String dir = getWorkDirPath();
		createDirectory(dir);
		if (batchLoading) return new Neo4jBatchGraph(dir);
		else return new Neo4jGraph(dir);
	}

	public String getName() {
		return "neo4j";
	}

	public String getTempDirPath() {
		return ServerProperties.dbDirNeo4jTemp;
	}

	public String getWorkDirPath() {
		return ServerProperties.dbDirNeo4jWork;
	}

}
