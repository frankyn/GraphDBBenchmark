package com.silvertower.app.bench.datasets;

import java.io.File;
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
	protected String datasetType;
	protected String datasetName;
	protected ArrayList<GraphProperty> vertexProperties;
	protected ArrayList<GraphProperty> edgesProperties;
	
	public Dataset(int nVertices, String datasetType) {
		this.nVertices = nVertices;
		this.datasetType = datasetType;
		this.datasetName = datasetType + nVertices;
		this.vertexProperties = new ArrayList<GraphProperty>();
		this.edgesProperties = new ArrayList<GraphProperty>();
	}
	
	public Dataset(String datasetType) {
		this.datasetType = datasetType;
		this.datasetName = datasetType + nVertices;
		this.vertexProperties = new ArrayList<GraphProperty>();
		this.edgesProperties = new ArrayList<GraphProperty>();
	}

	public void fillProperties() {
        ArrayList<Object> cids = new ArrayList<Object>();
        for (int i = 1; i <= nVertices; i++) {
        	cids.add(String.valueOf(i));
        }
        vertexProperties.add(new GraphProperty("cid", cids));
	}

	/**
	 * Generate a GraphML file that represents a graph.
	 * 
	 */
	public abstract File generate();
	
	/**
	 * 
	 * @return The name of this dataset (in one word without any space character).
	 */
	public String toString() {
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
	 * @return A list of all the vertex properties that are present in the graph. A property represents
	 * a pair (Field, {Possible values for this field})
	 */
	public ArrayList<GraphProperty> getVertexProperties() {
		return vertexProperties;
	}
	
	/**
	 * 
	 * @return A list of all the edges properties that are present in the graph. A property represents
	 * a pair (Field, {Possible values for this field})
	 */
	public ArrayList<GraphProperty> getEdgesProperties() {
		return edgesProperties;
	}
	
	/**
	 * 
	 * @return True if this dataset contains at least one property.
	 */
	public boolean isPropertyGraph() {
		return vertexProperties.size()!=0 || edgesProperties.size()!=0;
	}
	
	/**
	 * 
	 * @return The nature of this dataset as a string.
	 */
	public String getDatasetType() {
		return datasetType;
	}
}
