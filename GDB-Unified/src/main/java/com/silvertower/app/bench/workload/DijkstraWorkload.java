package com.silvertower.app.bench.workload;

import java.io.Serializable;
import java.util.List;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.PipeFunction;
import com.tinkerpop.pipes.branch.LoopPipe.LoopBundle;


public class DijkstraWorkload extends TraversalWorkload implements Serializable {
	private static final long serialVersionUID = -2273059932710550257L;
	
	public DijkstraWorkload() {
		super("Dijkstra");
	}

	public void operation(GraphDescriptor gDesc) {
		Graph g = gDesc.getGraph();
		Object sourceId = gDesc.getRandomVertexId();
		Vertex sourceVertex = null;
		while (sourceVertex == null) {
			System.out.println("1:" + sourceVertex);
			sourceVertex = g.getVertex(sourceId);
		}
		
		Object destinationId = gDesc.getRandomVertexId();
		final Vertex destinationVertex = g.getVertex(destinationId);
		System.out.println("2:" + destinationVertex);
		
		GremlinPipeline p = new GremlinPipeline();
		p.start(g.getVertex(sourceVertex.getId())).out().loop(1, new PipeFunction<LoopBundle,Boolean>() {
			public Boolean compute(LoopBundle bundle) {
				return !((Vertex)bundle.getObject()).getId().equals(destinationVertex.getId()) && bundle.getLoops() < 7;
			}
		}).path();
		List l = p.toList();
		for (Object o: l) {
			// Evaluate each element inside the pipe
			o.toString();
		}
	}

	public boolean preciseBenchmarkingNeeded() {
		return false;
	}
}
