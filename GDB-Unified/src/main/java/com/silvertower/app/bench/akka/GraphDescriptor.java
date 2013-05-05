package com.silvertower.app.bench.akka;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.silvertower.app.bench.datasets.Dataset;
import com.silvertower.app.bench.dbinitializers.GraphProperty;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.rexster.RexsterGraph;


public class GraphDescriptor implements Serializable {
	private static final long serialVersionUID = 8910914391911335063L;
	private Dataset d;
	private int nbConcurrentThread;
	private Random r;
	public List<Object> vIds;
	public List<Object> eIds;
	private Graph g;
	private int serverPort;
	private String serverAdd;
	private String graphName;
	public GraphDescriptor(List<Object> vIds, List<Object> eIds, String graphName, Dataset d) {
		this.vIds = vIds;
		this.eIds = eIds;
		this.graphName = graphName;
		this.d = d;
		this.r = new Random();
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
	
	public void setListVIds(List<Object> vIds) {
		this.vIds = vIds;
	}
	
	public void setListEIds(List<Object> eIds) {
		this.eIds = eIds;
	}
	
	public void setServerPort(int port) {
		this.serverPort = port;
	}
	
	public void setServerAdd(String add) {
		this.serverAdd = add;
	}
	
	public void setGraphDBName(String graphName) {
		this.graphName = graphName;
	}
	
	public void fetchGraph() {
		String graphAdd = String.format("http://%s:%d/graphs/%s", serverAdd, serverPort, graphName);
		System.out.println("Connection to rexster graph: " + graphAdd);
		Graph g = new RexsterGraph(graphAdd);
		this.g = g;
	}
	
	public Graph getGraph() {
		return g;
	}
	
	public String getDescription() {
		return String.format("Graph: %s for DB: %s", d.getDatasetName(), g.getClass().getName());
	}
	
	public Object[] getVerticesRandomPropertyCouple() {
//		ArrayList<GraphProperty> graphProperties = d.getVertexProperties();
//		Object[] returnedPropertyCouples = new Object[graphProperties.size()*2];
//		for (int i = 0; i < graphProperties.size(); i++) {
//			GraphProperty p = graphProperties.get(i);
//			Object fieldName = p.getFieldName();
//			Object value = p.getFieldPossibleValues().get(r.nextInt(p.getFieldPossibleValues().size()));
//			returnedPropertyCouples[i*2] = fieldName;
//			returnedPropertyCouples[(i*2)+1] = value;
//		}
//		return returnedPropertyCouples;
		
		ArrayList<GraphProperty> graphProperties = d.getVertexProperties();
		GraphProperty property = graphProperties.get(r.nextInt(graphProperties.size()));
		Object fieldName = property.getFieldName();
		Object value = property.getFieldPossibleValues().get(r.nextInt(property.getFieldPossibleValues().size()));
		return new Object[]{fieldName, value};
		
	}
	
	public Object getRandomVertexId(int threadId) {
		int nbVertices = vIds.size();
		int nbConcurrentThreads = getNbConcurrentThreads();
		int firstPossible = (threadId - 1) * (nbVertices / nbConcurrentThreads);
		int lastPossible = firstPossible + (nbVertices / nbConcurrentThreads - 1);
		int chosenValue = firstPossible + (int)(Math.random() * ((lastPossible - firstPossible) + 1));
		return vIds.get(chosenValue);
	}
	
	public Object getRandomVertexId() {
		int chosenValue = r.nextInt(vIds.size());
		return vIds.get(chosenValue);
	}
	
	public Object[] getEdgesRandomPropertyCouple() {
		ArrayList<GraphProperty> graphProperties = d.getEdgesProperties();
		GraphProperty property = graphProperties.get(r.nextInt(graphProperties.size()));
		Object fieldName = property.getFieldName();
		Object value = property.getFieldPossibleValues().get(r.nextInt(property.getFieldPossibleValues().size()));
		return new Object[]{fieldName, value};
	}
	
	public Object getRandomEdgeId(int threadId) {
		int nbEdges = eIds.size();
		int nbConcurrentThreads = getNbConcurrentThreads();
		int firstPossible = (threadId - 1) * (nbEdges / nbConcurrentThreads);
		int lastPossible = firstPossible + (nbEdges / nbConcurrentThreads - 1);
		int chosenValue = firstPossible + (int)(Math.random() * ((lastPossible - firstPossible) + 1));
		return eIds.get(chosenValue);
	}
	
	public Object getRandomEdgeId() {
		int chosenValue = r.nextInt(eIds.size());
		return eIds.get(chosenValue);
	}
}
