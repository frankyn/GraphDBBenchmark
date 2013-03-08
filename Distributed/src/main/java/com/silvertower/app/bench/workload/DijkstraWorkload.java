package com.silvertower.app.bench.workload;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/*
 * Inspired from http://www.algolist.com/code/java/Dijkstra%27s_algorithm
 */
public class DijkstraWorkload extends Workload {
	private List<DijkstraVertex> visitedVertices;
	
	public DijkstraWorkload() {
		super("Dijkstra", false);
	}
	
	class DijkstraVertex implements Comparable<DijkstraVertex> {
		public Vertex v;
		public DijkstraVertex previousVertexInSP;
		public int minDistance = Integer.MAX_VALUE;
		
		public DijkstraVertex(Vertex v) {
			this.v = v;
		}
		
		public int compareTo(DijkstraVertex other) {
			if (this.minDistance < other.minDistance) return -1;
			else if (this.minDistance > other.minDistance) return 1;
			else return 0;
		}
		
		public boolean equals(Object other) {
			DijkstraVertex otherV = (DijkstraVertex) other;
			return v.equals(otherV.v);
		}
	}
	
	public void computeShortestPath(DijkstraVertex s, DijkstraVertex d) {
		visitedVertices = new ArrayList<DijkstraVertex> ();
		s.minDistance = 0;
		PriorityQueue<DijkstraVertex> q = new PriorityQueue<DijkstraVertex>();
		q.add(s);
		
		while (!q.isEmpty()) {
			DijkstraVertex current = q.poll();
			for (Edge e: current.v.getEdges(Direction.IN)) {
				DijkstraVertex tried = new DijkstraVertex(e.getVertex(Direction.OUT));
				int index = visitedVertices.indexOf(tried);
				if (index == -1) visitedVertices.add(tried);
				else tried = visitedVertices.get(index);
				
				if (e.getVertex(Direction.OUT).equals(d.v)) {
					tried = d;
				}
				// Un-weighted graph: all the edges have the same weight = 1
				int triedDistanceFromSource = current.minDistance + 1;
				if (triedDistanceFromSource < tried.minDistance) {
					q.remove(tried); // No effect if the vertex was never visited.
					tried.minDistance = triedDistanceFromSource;
					tried.previousVertexInSP = current;
					q.add(tried);
				}
			}
			
			for (Edge e: current.v.getEdges(Direction.OUT)) {
				DijkstraVertex tried = new DijkstraVertex(e.getVertex(Direction.IN));
				int index = visitedVertices.indexOf(tried);
				if (index == -1) visitedVertices.add(tried);
				else tried = visitedVertices.get(index);
				
				if (e.getVertex(Direction.IN).equals(d.v)) {
					tried = d;
				}
				// Un-weighted graph: all the edges have the same weight = 1
				int triedDistanceFromSource = current.minDistance + 1;
				if (triedDistanceFromSource < tried.minDistance) {
					q.remove(tried); // No effect if the vertex was never visited.
					tried.minDistance = triedDistanceFromSource;
					tried.previousVertexInSP = current;
					q.add(tried);
				}
			}
		}
		System.out.println(d.minDistance);
	}

	public void operation(GraphDescriptor gDesc, int ...nbThreads) {
		Vertex sourceVertex = null;
		while (sourceVertex == null) {
			Object sourceId = gDesc.getRandomVertexId();
			sourceVertex = gDesc.getGraph().getVertex(sourceId);
		}
		
		Vertex destinationVertex = null;
		while (destinationVertex == null) {
			Object destinationId = gDesc.getRandomVertexId();
			destinationVertex = gDesc.getGraph().getVertex(destinationId);
		}
		computeShortestPath(new DijkstraVertex(sourceVertex), new DijkstraVertex(destinationVertex));
	}
}
