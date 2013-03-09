package com.silvertower.app.bench.load;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.silvertower.app.bench.datasets.Dataset;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.utils.Utilities;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;

class LoadBenchThread implements Runnable {
	private int counter = 0;
	private Graph g;
	private String suffix;
	private boolean batchLoading;
	private InputStream currentIs;
	private Dataset d;
	private DBInitializer i;
	
	public LoadBenchThread(String suffix, boolean batchLoading, DBInitializer i, Dataset d) {
		this.suffix = suffix;
		this.batchLoading = batchLoading;
		this.currentIs = initializeIS(d);
		this.i = i;
		this.d = d;
	}
	
	public void run() {
    	counter++;
		loadGraphML((g = i.initialize(suffix + counter, batchLoading)), currentIs);
		i.shutdownLastGraphInitialized();
		currentIs = initializeIS(d);
	}
	
	public Graph getFinalGraph() {
		return i.initialize(suffix + counter, false);
	}
	
	private void loadGraphML(Graph g, InputStream is) {
		try {
			GraphMLReader.inputGraph(g, is);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error while filling a database with a dataset");
			System.exit(-1);
		}
	}
	
	private InputStream initializeIS(Dataset ds) {
		File datasetFile = new File(ds.getDatasetFP());
		InputStream is = null;
		try {
			is = new FileInputStream(datasetFile);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return is;
	}
	
	public void deleteUnusedDBs() {
    	for (int j = 1; j < counter; j++) {
    		Utilities.deleteDirectory(i.getDirPath() + suffix + j);
    	}
	}
}