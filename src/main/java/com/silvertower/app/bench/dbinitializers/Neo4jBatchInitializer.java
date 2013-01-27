package com.silvertower.app.bench.dbinitializers;

import com.tinkerpop.blueprints.impls.neo4jbatch.Neo4jBatchGraph;

public class Neo4jBatchInitializer implements DBInitializer {
	public Neo4jBatchGraph initialize(String dbPath) {
		return new Neo4jBatchGraph(dbPath);
	}
}
