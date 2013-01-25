package com.silvertower.app.bench.load;

import com.silvertower.app.bench.datasetsgeneration.DatasetsGenerator;
import com.tinkerpop.blueprints.Graph;

public interface DBLoader {
	public Graph[] load(DatasetsGenerator dg);
}
