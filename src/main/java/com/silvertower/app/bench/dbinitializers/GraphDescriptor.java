package com.silvertower.app.bench.dbinitializers;

import java.util.ArrayList;
import java.util.Random;

import com.silvertower.app.bench.datasetsgeneration.Dataset;
import com.tinkerpop.blueprints.Graph;


public class GraphDescriptor {
	private Dataset ds;
	private Graph g;
	private Long firstVertexId;
	private Long stepBetweenId;
	private Class verticesIdClass;
	private Random r = new Random();
	private int nbConcurrentThread;
	
	public GraphDescriptor(Graph g, Dataset ds) {
		this.g = g;
		this.ds = ds;
	}
	
	public Graph getGraph() {
		return g;
	}
	
	public Long getFirstVertexId() {
		return firstVertexId;
	}

	public void setFirstVertexId(Long firstVertexId) {
		this.firstVertexId = firstVertexId;
	}

	public Long getStepBetweenId() {
		return stepBetweenId;
	}

	public void setStepBetweenId(Long stepBetweenId) {
		this.stepBetweenId = stepBetweenId;
	}

	public int getNbVertices() {
		return ds.getNumberVertices();
	}
	
	public void setVerticesIdClass(Class verticesIdClass) {
		this.verticesIdClass = verticesIdClass;
	}
	
	public void setNbConcurrentThreads(int nb) {
		nbConcurrentThread = nb;
	}
	
	public Object getRandomVertexId(int threadId) {
		int nbVertices = getNbVertices();
		int firstPossible = (threadId - 1) * (nbVertices / nbConcurrentThread);
		int lastPossible = firstPossible + (nbVertices / nbConcurrentThread - 1);
		int chosenValue = firstPossible + (int)(Math.random() * ((lastPossible - firstPossible) + 1));
		if (verticesIdClass.equals(String.class)) {
			return firstVertexId + chosenValue * stepBetweenId + "";
		}
		else return firstVertexId + chosenValue * stepBetweenId;
	}
	
	public Object getRandomVertexId() {
		int nbVertices = getNbVertices();
		if (verticesIdClass.equals(String.class)) {
			return firstVertexId + r.nextInt(nbVertices) * stepBetweenId + "";
		}
		else return firstVertexId + r.nextInt(nbVertices) * stepBetweenId;
	}
	
	public Object[] getRandomPropertyCouple() {
		ArrayList<GraphProperty> graphProperties = ds.getProperties();
		GraphProperty property = graphProperties.get(r.nextInt(graphProperties.size()));
		Object fieldName = property.getFieldName();
		Object value = property.getFieldPossibleValues().get(r.nextInt(property.getFieldPossibleValues().size()));
		return new Object[]{fieldName, value};
	}
}
