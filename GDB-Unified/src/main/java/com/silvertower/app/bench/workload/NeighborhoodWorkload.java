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
	
	public void operation(Vertex from, Vertex to) {
		numberOfElementsInThePipe = 0;
		GremlinPipeline p = new GremlinPipeline();
		p = p.start(from).out().gather().scatter().loop(1, new PipeFunction<LoopBundle,Boolean>() {
			public Boolean compute(LoopBundle argument) {
				return argument.getLoops() <= k;
			}
		});
		while (p.hasNext()) {
			p.next();
			numberOfElementsInThePipe++;
		}
		System.out.println(numberOfElementsInThePipe);
	}

	public boolean preciseBenchmarkingNeeded() {
		return false;
	}
}
