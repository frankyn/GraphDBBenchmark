package com.silvertower.app.bench.dbinitializers;

import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;

public class Neo4jInitializer implements DBInitializer {
	public Neo4jGraph initialize(String dbPath) {
		return new Neo4jGraph(dbPath);
	}
}
