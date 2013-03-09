package com.silvertower.app.bench.dbinitializers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.silvertower.app.bench.datasets.Dataset;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;


public class GraphDescriptor {
	private Dataset d;
	private int nbConcurrentThread;
	private Random r = new Random();
	private Graph g;
	private List<Object> ids;
		
	public Graph getGraph() {
		return g;
	}
	
	public int getNbVertices() {
		return d.getNumberVertices();
	}
	
	public int getNbConcurrentThreads() {
		return nbConcurrentThread;
	}
	
	public void setNbConcurrentThreads(int nb) {
		nbConcurrentThread = nb;
	}
	
	public void setGraph(Graph g) {
		this.g = g;
	}
	
	public void setDataset(Dataset d) {
		this.d = d;
	}
	
	public String getDescription() {
		return String.format("Graph: %s for DB: %s", d.getDatasetName(), g.getClass().getName());
	}
	
	public Object[] getRandomPropertyCouple() {
		ArrayList<GraphProperty> graphProperties = d.getProperties();
		GraphProperty property = graphProperties.get(r.nextInt(graphProperties.size()));
		Object fieldName = property.getFieldName();
		Object value = property.getFieldPossibleValues().get(r.nextInt(property.getFieldPossibleValues().size()));
		return new Object[]{fieldName, value};
	}
	
	public void scanDB() {
		Iterator <Vertex> iter = g.getVertices().iterator();
		ids = new ArrayList<Object>();
		while (iter.hasNext()) {
			Object id = iter.next().getId();
			ids.add(id);
		}
	}
	
	public Object getRandomVertexId(int threadId) {
		int nbVertices = ids.size();
		int nbConcurrentThreads = getNbConcurrentThreads();
		int firstPossible = (threadId - 1) * (nbVertices / nbConcurrentThreads);
		int lastPossible = firstPossible + (nbVertices / nbConcurrentThreads - 1);
		int chosenValue = firstPossible + (int)(Math.random() * ((lastPossible - firstPossible) + 1));
		return ids.get(chosenValue);
	}
	
	public Object getRandomVertexId() {
		int chosenValue = r.nextInt(ids.size());
		return ids.get(chosenValue);
	}
}
