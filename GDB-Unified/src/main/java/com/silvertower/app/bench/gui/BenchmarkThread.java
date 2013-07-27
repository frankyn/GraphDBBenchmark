package com.silvertower.app.bench.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.utils.IP;
import com.silvertower.app.bench.workload.Workload;

public class BenchmarkThread extends SwingWorker<Void, Void> {
	private List<DBInitializer> dbsInit;
	private List<Workload> workloads;
	private int totalNumberOfTasks;
	private List<IP> ips;
	
	public BenchmarkThread(final JProgressBar progressBar, List<DBInitializer> dbsInit, List<Workload> workloads, List<IP> ips) {
		this.dbsInit = dbsInit;
		this.workloads = workloads;
		this.ips = ips;
		this.totalNumberOfTasks = dbsInit.size() * (workloads.size() + 1); //+1 for loading the dbs
		this.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getPropertyName().equals("progress")) {
					progressBar.setValue((Integer) e.getNewValue());
				}
			}
		});
	}
	
	protected Void doInBackground() throws Exception {
		startActors(args);
		int step = 0;
		for (DBInitializer dbInit: dbsInit) {
			for (Workload workload: workloads) {
				
				this.setProgress(++step);
			}
		}
		return null;
	}
	
	public void done() {
		this.setProgress(totalNumberOfTasks);
	}
	
	public int getTaskLength() {
		return this.totalNumberOfTasks;
	}
}
