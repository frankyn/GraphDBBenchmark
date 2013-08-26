package com.silvertower.app.bench.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import com.silvertower.app.bench.akka.BenchmarkRunner;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.main.BenchmarkConfiguration;
import com.silvertower.app.bench.main.BenchmarkExecutor;
import com.silvertower.app.bench.resultsprocessing.Logger;
import com.silvertower.app.bench.resultsprocessing.Statistics;
import com.silvertower.app.bench.workload.IntensiveWorkload;
import com.silvertower.app.bench.workload.LoadWorkload;
import com.silvertower.app.bench.workload.TraversalWorkload;
import com.silvertower.app.bench.workload.Workload;

public class BenchmarkGUIExecutor extends SwingWorker<Void, Void> implements BenchmarkExecutor{
	private List<DBInitializer> dbsInit;
	private List<Workload> workloads;
	private int totalNumberOfTasks;
	private BenchmarkRunner benchRunner;
	private Logger log;
	private CountDownLatch startLatch;
	
	public BenchmarkGUIExecutor(final JProgressBar progressBar, List<DBInitializer> dbsInit, List<Workload> workloads) {
		this.dbsInit = dbsInit;
		this.workloads = workloads;
		this.totalNumberOfTasks = dbsInit.size() * (workloads.size() + 1); //+1 for loading the dbs
		this.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getPropertyName().equals("progress")) {
					progressBar.setValue((Integer) e.getNewValue());
				}
			}
		});
		this.startLatch = new CountDownLatch(1);
	}
	
	protected Void doInBackground() throws Exception {
		try {
			startLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		BenchmarkConfiguration config = new BenchmarkConfiguration();
		setConfig(config);
		
		int step = 0;
		for (DBInitializer dbInit: dbsInit) {
			benchRunner.assignInitializer(dbInit);
			for (Workload workload: workloads) {
				bench(workload);
				this.setProgress(++step);
			}
			benchRunner.vanishDB();
		}
		
		log.logMessage(Statistics.computeReport().toString());
		benchRunner.shutdownSystem();
		
		return null;
	}
	
	public void done() {
		this.setProgress(totalNumberOfTasks);
	}
	
	public int getTaskLength() {
		return this.totalNumberOfTasks;
	}

	public void startBenchmark(BenchmarkRunner b, Logger log) {
		this.benchRunner = b;
		this.log = log;
		startLatch.countDown();
	}
	
	private void setConfig(BenchmarkConfiguration config) {
		benchRunner.shareConfig(config);
	}
	
	private void bench(Workload w) {
		if (w instanceof LoadWorkload) bench((LoadWorkload) w);
		else if (w instanceof TraversalWorkload) bench((TraversalWorkload) w);
		else if (w instanceof IntensiveWorkload) bench((IntensiveWorkload) w);
	}
	
	private void bench(LoadWorkload w) {
		benchRunner.startLoadBench(w);
	}
	
	private void bench(TraversalWorkload w) {
		benchRunner.startWorkBench(w);
	}
	
	private void bench(IntensiveWorkload w) {
		benchRunner.startWorkBench(w);
	}
}
