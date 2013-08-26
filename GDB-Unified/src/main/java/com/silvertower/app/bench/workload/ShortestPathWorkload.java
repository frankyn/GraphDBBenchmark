package com.silvertower.app.bench.workload;

import java.io.Serializable;

import com.silvertower.app.bench.annotations.Custom;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.PipeFunction;
import com.tinkerpop.pipes.branch.LoopPipe.LoopBundle;

@Custom
public class ShortestPathWorkload extends TraversalWorkload implements Serializable {
	private static final long serialVersionUID = -2273059932710550257L;
	
	public ShortestPathWorkload(int hopLimit) {
		super("Dijkstra workload", hopLimit);
	}
	
	public String generateRequest(Vertex from, Vertex to) {
		Object fromId = from.getId();
		String fromIdRepr = fromId instanceof String ? "\"" + fromId + "\"" : fromId.toString();
		Object toId = from.getId();
		String toIdRepr = toId instanceof String ? "\"" + toId + "\"" : toId.toString();
		return String.format("g.v(%s).out.loop(1){it.object.id != %s & it.loops <= %d}.path[0..2999]", fromIdRepr, toIdRepr, nHops);
	}

	public void operation(final Vertex from, final Vertex to) {
		numberOfElementsInThePipe = 0;
		GremlinPipeline p = new GremlinPipeline();
		p = p.start(from).out().loop(1, new PipeFunction<LoopBundle,Boolean>() {
			public Boolean compute(LoopBundle bundle) {
				return !((Vertex)bundle.getObject()).getId().equals(to.getId()) && bundle.getLoops() <= nHops;
			}
		}).path();
		
		evaluatePipe(p);
	}
}
