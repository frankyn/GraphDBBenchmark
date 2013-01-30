package com.silvertower.app.bench.dbinitializers;

import java.util.ArrayList;
import java.util.Random;


public class GraphDescriptor {
	protected boolean isPropertyGraph = false;
	protected Long firstVertexId;
	protected Long stepBetweenId;
	protected Class verticesIdClass;
	protected int nbVertices;
	protected ArrayList<GraphProperty> graphProperties = new ArrayList<GraphProperty>();
	private Random r = new Random();
	
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
		return nbVertices;
	}

	public void setNbVertices(int nbVertices) {
		this.nbVertices = nbVertices;
	}
	
	public void addProperty(GraphProperty property) {
		isPropertyGraph = true;
		graphProperties.add(property);
	}
	
	public void setVerticesIdClass(Class verticesIdClass) {
		this.verticesIdClass = verticesIdClass;
	}
	
	public Object getRandomVertexId() {
		if (verticesIdClass.equals(String.class)) {
			return firstVertexId + r.nextInt(nbVertices) * stepBetweenId + "";
		}
		else return firstVertexId + r.nextInt(nbVertices) * stepBetweenId;
	}
	
	public Object[] getRandomPropertyCouple() {
		GraphProperty property = graphProperties.get(r.nextInt(graphProperties.size()));
		Object fieldName = property.getFieldName();
		Object value = property.getFieldPossibleValues().get(r.nextInt(property.getFieldPossibleValues().size()));
		return new Object[]{fieldName, value};
	}
}
