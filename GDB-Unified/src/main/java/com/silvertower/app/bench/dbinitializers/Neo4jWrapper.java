package com.silvertower.app.bench.dbinitializers;


import com.silvertower.app.bench.annotations.Custom;
import com.silvertower.app.bench.main.ServerProperties;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.impls.neo4j.batch.Neo4jBatchGraph;

@Custom
public class Neo4jWrapper extends DBInitializer {

	private static final long serialVersionUID = -7001472988542034752L;
	
	public Graph initialize(String storageDir, boolean batchLoading) {
		createDirectory(storageDir);
		if (batchLoading) return new Neo4jBatchGraph(storageDir);
		else return new Neo4jGraph(storageDir);
	}

	public String toString() {
		return "neo4j";
	}

	public String getTempDirPath() {
		return ServerProperties.dbDirNeo4jTemp;
	}

	public String getWorkDirPath() {
		return ServerProperties.dbDirNeo4jWork;
	}

}
