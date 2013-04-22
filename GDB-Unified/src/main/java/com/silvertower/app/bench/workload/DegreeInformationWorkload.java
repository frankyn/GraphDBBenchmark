package com.silvertower.app.bench.workload;

import java.util.Iterator;

import com.silvertower.app.bench.akka.GraphDescriptor;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public class DegreeInformationWorkload extends TraversalWorkload {
	private static final long serialVersionUID = 2785969268528966034L;

	public DegreeInformationWorkload() {
		super("Degree information workload");
	}

	public void operation(GraphDescriptor gDesc) {
		Graph g = gDesc.getGraph();
		Iterator <Vertex> iter = g.getVertices().iterator();
		long totalDegree = 0;
		int nVertices = 0;
		int minDegree = 0;
		int maxDegree = 0;
		Vertex current = null;
		while (iter.hasNext()) {
			current = iter.next();
			int nEdges = 0;
			Iterator <Edge> iterEdges = current.getEdges(Direction.OUT).iterator();
			while (iterEdges.hasNext()) {
				iterEdges.next();
				nEdges++;
			}
			if (nEdges < minDegree) minDegree = nEdges;
			else if (nEdges > maxDegree) maxDegree = nEdges;
			totalDegree += nEdges;
			nVertices++;
		}
		double meanDegree = totalDegree / (nVertices * 1.0);
		System.out.println(String.format("Mean: %f - Max: %d - Min: %d", meanDegree, maxDegree, minDegree));
	}

	public boolean preciseBenchmarkingNeeded() {
		return false;
	}

	@Override
	public void operation(Vertex from, Vertex to) {
		// TODO Auto-generated method stub
		
	}
}
