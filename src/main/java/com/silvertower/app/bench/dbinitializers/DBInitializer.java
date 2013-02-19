package com.silvertower.app.bench.dbinitializers;

import java.io.File;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;

public abstract class DBInitializer {
	/**
	 * 
	 * Initialize a Graph instance in a particular way depending on the nature of the graph.
	 * 
	 * @param name The (directory) name where the Graph(database) will be stored
	 * @return a graph instance
	 */
	public abstract Graph initialize(String name, boolean batchLoading);
	
	/**
	 * 
	 * @return the name of this graph database implementation.
	 */
	public abstract String getName();
	
	/**
	 * 
	 * @return the absolute path where this graph database will be stored
	 */
	public abstract String getPath();
	
	/**
	 * Create the directory where that will be used to store this graph(database)
	 * @param name
	 */
	public void createDirectory(String name) {
		File f = new File(getPath() + name);
		f.mkdirs();
	}

}
