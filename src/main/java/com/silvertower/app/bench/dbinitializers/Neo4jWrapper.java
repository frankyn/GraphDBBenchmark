package com.silvertower.app.bench.dbinitializers;

import com.silvertower.app.bench.main.BenchmarkProperties;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.impls.neo4jbatch.Neo4jBatchGraph;
import com.tinkerpop.blueprints.util.wrappers.id.IdGraph;

public class Neo4jWrapper implements DBInitializer {

	public Graph initialize(String dbPath, boolean batchLoading) {
		if (batchLoading) 
			return new Neo4jBatchGraph(dbPath);
		else 
			return new IdGraph(new Neo4jGraph(dbPath));
	}

	public String getName() {
		return "Neo4j";
	}

	public String getPath() {
		return BenchmarkProperties.dbDirNeo4j;
	}
}
