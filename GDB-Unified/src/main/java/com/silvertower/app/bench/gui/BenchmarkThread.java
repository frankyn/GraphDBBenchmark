package com.silvertower.app.bench.gui;

import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.workload.Workload;

public class BenchmarkThread extends SwingWorker<Void, Void> {
	private JProgressBar progressBar;
	private List<DBInitializer> dbsInit;
	private List<Workload> workloads;
	private int totalNumberOfTasks;
	
	public BenchmarkThread(JProgressBar progressBar, List<DBInitializer> dbsInit, List<Workload> workloads) {
		this.progressBar = progressBar;
		this.dbsInit = dbsInit;
		this.workloads = workloads;
		this.totalNumberOfTasks = dbsInit.size() * (workloads.size() + 1); //+1 for loading the dbs
	}
	
	protected Void doInBackground() throws Exception {
		for (DBInitializer dbInit: dbsInit) {
			for (Workload workload: workloads) {
				
			}
		}
		return null;
	}

}
