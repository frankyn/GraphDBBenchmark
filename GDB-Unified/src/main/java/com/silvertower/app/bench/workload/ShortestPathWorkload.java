package com.silvertower.app.bench.workload;

import java.io.Serializable;
import java.util.List;

import com.silvertower.app.bench.akka.GraphDescriptor;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.PipeFunction;
import com.tinkerpop.pipes.branch.LoopPipe.LoopBundle;


public class ShortestPathWorkload extends TraversalWorkload implements Serializable {
	private static final long serialVersionUID = -2273059932710550257L;
	private int hopLimit;
	
	public ShortestPathWorkload(int hopLimit) {
		super(String.format("Dijkstra %d hop limit", hopLimit));
		this.hopLimit = hopLimit;
	}

	public void operation(final Vertex from, final Vertex to) {
		numberOfElementsInThePipe = 0;
		GremlinPipeline p = new GremlinPipeline();
		p = p.start(from).out().loop(1, new PipeFunction<LoopBundle,Boolean>() {
			public Boolean compute(LoopBundle bundle) {
				return !((Vertex)bundle.getObject()).getId().equals(to.getId()) && bundle.getLoops() < hopLimit;
			}
		}).path();
		while (p.hasNext()) {
			p.next();
			numberOfElementsInThePipe++;
		}
		System.out.println(numberOfElementsInThePipe);
	}
}
