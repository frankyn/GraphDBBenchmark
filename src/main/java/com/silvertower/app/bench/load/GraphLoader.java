package com.silvertower.app.bench.load;

import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.tinkerpop.blueprints.Graph;

public interface GraphLoader {
	public Graph load(DBInitializer graphInitializer, DBInitializer batchGraphInitializer, String dbDir);
}
