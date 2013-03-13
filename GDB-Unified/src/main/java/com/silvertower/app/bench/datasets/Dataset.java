package com.silvertower.app.bench.datasets;

import java.io.Serializable;
import java.util.ArrayList;

import com.silvertower.app.bench.dbinitializers.GraphProperty;

/**
 * A dataset represents a set of data that will be used to fill a Graph instance. This dataset
 * is stored as a GraphML file.
 * @author Vansteenberghe Valentin
 *
 */
public abstract class Dataset implements Serializable {
	private static final long serialVersionUID = 4308905129972298707L;
	protected int nVertices;
	protected String datasetFP;
	protected String datasetType;
	protected String datasetName;
	protected ArrayList<GraphProperty> properties;
	
	public Dataset(int nVertices, String datasetType) {
		this.nVertices = nVertices;
		this.datasetType = datasetType;
		this.datasetName = datasetType + nVertices;
		this.properties = new ArrayList<GraphProperty>();
	}

	/**
	 * Generate a GraphML file that represents a graph. The path of this file is datasetFP.
	 * 
	 */
	public abstract void generate();
	
	/**
	 * 
	 * @return True if the graph contained in the GraphML file represents a directed graph.
	 */
	public abstract boolean isDirected();

	/**
	 * 
	 * @return The complete file path where this dataset is located.
	 */
	public String getDatasetFP() {
		return datasetFP;
	}
	
	/**
	 * 
	 * @return The name of this dataset (in one word without any space character).
	 */
	public String getDatasetName() {
		return datasetName;
	}

	/**
	 * 
	 * @return The number of vertices contained in this dataset.
	 */
	public int getNumberVertices() {
		return nVertices;
	}

	/**
	 * 
	 * @return A list of all the properties that are present in the graph. A property represents
	 * a pair (Field, {Possible values for this field})
	 */
	public ArrayList<GraphProperty> getProperties() {
		return properties;
	}
	
	/**
	 * 
	 * @return True if this dataset contains at least one property.
	 */
	public boolean isPropertyGraph() {
		return properties.size()!=0;
	}
	
	/**
	 * 
	 * @return The nature of this dataset as a string.
	 */
	public String getDatasetType() {
		return datasetType;
	}
}
