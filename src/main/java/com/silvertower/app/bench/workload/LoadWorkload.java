package com.silvertower.app.bench.workload;

import java.util.List;

import com.silvertower.app.bench.datasetsgeneration.Dataset;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.load.DBLoader;
import com.silvertower.app.bench.utils.Logger;

public class LoadWorkload {
	public GraphDescriptor work(List<Dataset> datasets, String dbDir, DBInitializer initializer, Logger log) {
		GraphDescriptor gDesc = null;
		if (datasets.size() == 0) {
			System.err.println("Your datasets set is empty");
			System.exit(-1);
		}
		
		log.logOperation("Load time for a " + datasets.get(0).getDatasetType() + " dataset using batchloading");
		for (Dataset ds: datasets) {
			DBLoader.load(ds, initializer, dbDir + ds.getDatasetName(), log, true);
		}
		log.plotResults("Number of vertices", "Time");
		
		log.logOperation("Load time for a " + datasets.get(0).getDatasetType() + " dataset without batchloading");
		for (Dataset ds: datasets) {
			gDesc = DBLoader.load(ds, initializer, dbDir + ds.getDatasetName(), log, false);
		}
		log.plotResults("Number of vertices", "Time");
		
		return gDesc;
	}
}
