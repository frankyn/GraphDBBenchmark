package com.silvertower.app.bench.workload;

import java.io.Serializable;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.PipeFunction;
import com.tinkerpop.pipes.branch.LoopPipe.LoopBundle;


public class NeighborhoodWorkload extends TraversalWorkload implements Serializable {
	private static final long serialVersionUID = -3153736507890597883L;
	private int hopsLimit;
	
	public NeighborhoodWorkload(int hopsLimit) {
		super(String.format("Neighborhood %d", hopsLimit));
		this.hopsLimit = hopsLimit;
	}
	
	public NeighborhoodWorkload() {
		super(String.format("Neighborhood %d", 7));
		this.hopsLimit = 7;
	}
	
	public void operation(Vertex from, Vertex to) {
		numberOfElementsInThePipe = 0;
		GremlinPipeline p = new GremlinPipeline();
		p = p.start(from).out().gather().scatter().loop(1, new PipeFunction<LoopBundle,Boolean>() {
			public Boolean compute(LoopBundle argument) {
				return argument.getLoops() <= hopsLimit;
			}
		});
		
		evaluatePipe(p);
	}

	public String generateRequest(Vertex from, Vertex to) {
		Object fromId = from.getId();
		String fromIdRepr = fromId instanceof String ? "\"" + fromId + "\"" : fromId.toString();
		return String.format("g.v(%s).out.gather.scatter.loop(1){it.loops <= %d}[0..2999]", fromIdRepr, hopsLimit);
	}
}
