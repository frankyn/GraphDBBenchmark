package com.silvertower.app.bench.load;

import java.io.IOException;
import java.io.InputStream;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;

public class GraphMLLoader {
	protected static long loadAndLogGraphML(Graph g, InputStream is) throws IOException {
		long beforeTs = System.nanoTime();
		GraphMLReader.inputGraph(g, is);
		g.shutdown();
		long afterTs = System.nanoTime();
		return afterTs - beforeTs;
	}
}
