package com.silvertower.app.bench.load;

import java.io.InputStream;

import com.silvertower.app.bench.datasets.Dataset;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.utils.Utilities;
import com.tinkerpop.blueprints.Graph;

class LoadBenchThread implements Runnable {
	private int counter = 0;
	private String suffix;
	private boolean batchLoading;
	private InputStream currentIs;
	private Dataset d;
	private DBInitializer i;
	
	public LoadBenchThread(String suffix, boolean batchLoading, DBInitializer i, Dataset d) {
		this.suffix = suffix;
		this.batchLoading = batchLoading;
		this.currentIs = DBLoader.initializeIS(d);
		this.i = i;
		this.d = d;
	}
	
	public void run() {
    	counter++;
    	Graph g = i.initialize(suffix + counter, batchLoading);
		DBLoader.loadGraphML(g, currentIs);
		i.shutdownGraph(g);
		currentIs = DBLoader.initializeIS(d);
	}
	
	public void deleteUnusedDBs() {
    	for (int j = 1; j < counter; j++) {
    		Utilities.deleteDirectory(i.getTempDirPath() + suffix + j);
    	}
	}
}