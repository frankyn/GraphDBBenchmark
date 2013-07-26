package com.silvertower.app.bench.dbinitializers;

import java.io.File;
import java.io.Serializable;

import com.tinkerpop.blueprints.Graph;

public abstract class DBInitializer implements Serializable {

	private static final long serialVersionUID = 7252735542119557794L;

	/**
	 * 
	 * Initialize a temporary Graph instance in a particular way depending on the nature of the graph.
	 * 
	 * @param path The directory path where the Graph(database) will be stored
	 * @return a graph instance
	 */
	public abstract Graph initialize(String path, boolean batchLoading);
	
	/**
	 * 
	 * @return the name of the type of the graph implementation this initializer initializes.
	 */
	public abstract String toString();
	
	/**
	 * 
	 * @return the directory that will be used by the initializer to store temporary graphs.
	 */
	public abstract String getTempDirPath();
	
	/**
	 * 
	 * @return the directory that will be used by the initializer to store working graphs.
	 */
	public abstract String getWorkDirPath();
	
	/**
	 * Create a directory that will be used to store a graph
	 * @param fullPath the path where the graph will be stored
	 */
	public void createDirectory(String fullPath) {
		File f = new File(fullPath);
		f.mkdirs();
	}
	
	/**
	 * Shutdown a graph instance initialized by this initializer
	 * @param g the graph to be shutdown
	 */
	public void shutdownGraph(Graph g) {
		g.shutdown();
	}
}
