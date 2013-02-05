package com.silvertower.app.bench.load;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.silvertower.app.bench.main.Globals;
import com.silvertower.app.bench.utils.Utilities;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;

public class GraphMLLoader {
	protected static void loadAndLogGraphML(Graph g, File datasetPath, boolean isBatchLoad) throws IOException {
		InputStream is = new FileInputStream(datasetPath);
		long beforeTs = System.nanoTime();
		GraphMLReader.inputGraph(g, is);

		if (isBatchLoad) {
			g.shutdown();
			long afterTs = System.nanoTime();
			Utilities.log("With batch loading", (afterTs - beforeTs) / (Globals.nanosToSFactor * 1.0));
		}
		else {
			((TransactionalGraph)g).stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
			long afterTs = System.nanoTime();
			Utilities.log("Without batch loading", (afterTs - beforeTs) / (Globals.nanosToSFactor * 1.0));
		}
	}
}
