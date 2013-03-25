package com.silvertower.app.bench.workload;

import java.io.Serializable;
import java.util.List;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.PipeFunction;
import com.tinkerpop.pipes.branch.LoopPipe.LoopBundle;


public class NeighborhoodWorkload extends TraversalWorkload implements Serializable {
	private static final long serialVersionUID = -3153736507890597883L;
	private int k;
	
	public NeighborhoodWorkload(int k) {
		super(String.format("Neighborhood %d", k));
		this.k = k;
	}
	
	public NeighborhoodWorkload() {
		super(String.format("Neighborhood %d", 7));
		this.k = 7;
	}
	
	public void operation(GraphDescriptor gDesc) {
		Graph g = gDesc.getGraph();
		Vertex entry = null;
		while (entry == null) {
			entry = gDesc.getGraph().getVertex(gDesc.getRandomVertexId());
		}
		GremlinPipeline p = new GremlinPipeline();
		p.start(g.getVertex(entry.getId())).out().gather().loop(1, new PipeFunction<LoopBundle,Boolean>() {
			public Boolean compute(LoopBundle argument) {
				    return argument.getLoops() <= k;
			}
		});
		List l = p.toList();
		for (Object o: l) {
			// Evaluate each element inside the pipe
			o.toString();
		}
	}

	public boolean preciseBenchmarkingNeeded() {
		return true;
	}
}
