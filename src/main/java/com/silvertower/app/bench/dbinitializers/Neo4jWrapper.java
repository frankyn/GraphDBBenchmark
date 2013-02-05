package com.silvertower.app.bench.dbinitializers;

import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.util.wrappers.id.IdGraph;

public class Neo4jWrapper extends GraphDescriptor implements DBInitializer {

	public IdGraph initialize(String dbPath) {
		return new IdGraph(new Neo4jGraph(dbPath));
	}
}
