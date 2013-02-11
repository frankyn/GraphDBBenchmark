package com.silvertower.app.bench.dbinitializers;

import com.tinkerpop.blueprints.Graph;

public interface DBInitializer {
	/**
	 * 
	 * Initialize a Graph instance in a particular way depending on the nature of the graph.
	 * 
	 * @param dbPath The place where the Graph(database) will be stored
	 * @param batchLoading true if we want to create the Batch version of this database.
	 * @return a graph instance
	 */
	public Graph initialize(String dbPath, boolean batchLoading);
	
	/**
	 * 
	 * @return the name of this graph database implementation.
	 */
	public String getName();
	
	/**
	 * 
	 * @return the absolute path where this graph database will be stored
	 */
	public String getPath();
}
