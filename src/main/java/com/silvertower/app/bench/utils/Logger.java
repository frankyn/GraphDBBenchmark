package com.silvertower.app.bench.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.silvertower.app.bench.main.BenchmarkProperties;
import com.silvertower.app.bench.workload.Result;

public class Logger {
	private String dbName;
	private String operation;
	private List<Result> resultsVector;
	private BufferedWriter logBuffer;
	
	public Logger(String dbName, String benchmarkName) {
		this.dbName = dbName;
		try {
			logBuffer = new BufferedWriter(new FileWriter(BenchmarkProperties.logDir + benchmarkName + " - " + dbName + ".txt", false));
		} catch (IOException e) {
			System.err.println("Unable to initialize the log buffer");
			System.exit(-1);
		}
		this.resultsVector = new ArrayList<Result>();
	}
	
	public void log(String operation, double result) {
		try {
			logBuffer.write(operation + ": " + String.valueOf(result) + "\n");
			logBuffer.flush();
		} catch (IOException e) {
			System.err.println("Unable to flush to the log buffer");
		}
	}
	
	public void log(String operation, long result) {
		try {
			logBuffer.write(operation + ": " + String.valueOf(result) + "\n");
			logBuffer.flush();
		} catch (IOException e) {
			System.err.println("Unable to flush to the log buffer");
		}
	}
	
	public void logOperation(String operation) {
		this.operation = operation;
		try {
			logBuffer.write(operation + ": " + "\n");
			logBuffer.flush();
		} catch (IOException e) {
			System.err.println("Unable to flush to the log buffer");
		}
	}
	
	public void logResult(Result result) {
		resultsVector.add(result);
		try {
			logBuffer.write(result.first + ":");
			for (Number n: result.associated) {
				 logBuffer.write(n + ":");
			}
			logBuffer.write("\n");
			logBuffer.flush();
		} catch (IOException e) {
			System.err.println("Unable to flush to the log buffer");
		}
	}
	
	public void logResults(List<Result> list) {
		this.resultsVector = list;
		try {
			for (int i = 0; i < list.size(); i++) {
				Result r = list.get(i);
				logBuffer.write(r.first + ":");
				for (Number n: r.associated) {
					 logBuffer.write(n + ":");
				}
				logBuffer.write("\n");
				logBuffer.flush();
			}
		} catch (IOException e) {
			System.err.println("Unable to flush to the log buffer");
		}
	}
	
	public void plotResults(String xAxis, String yAxis, String ... seriesName) {
		assert resultsVector.size() != 0: "Nothing to plot!";
		for (Result r: resultsVector) {
			if (r.associated.length != seriesName.length) {
				System.err.println("Error: one of the results is not the good dimension");
				return;
			}
		}
		
		List <XYSeries> series = new ArrayList<XYSeries>();
		for (String serieName: seriesName) {
			series.add(new XYSeries(serieName));
		}
		
		for (Result r: resultsVector) {
			for (int i = 0; i < r.associated.length; i++) {
				series.get(i).add(r.first, r.associated[i]);
			}
		}
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		for (XYSeries s: series) {
			dataset.addSeries(s);
		}
		JFreeChart graphChart = ChartFactory.createXYLineChart(operation, xAxis, yAxis, dataset, 
				PlotOrientation.VERTICAL, true, true, true);
		graphChart.getXYPlot().setRenderer(new XYSplineRenderer());

		try {
			ChartUtilities.saveChartAsPNG(new File(BenchmarkProperties.plotsDir + dbName + "-" + operation + ".png"), graphChart, 500, 500);
			System.out.println("plotted");
		} catch (IOException e) {
			System.err.println("Unable to plot the chart " + operation + "for database " + dbName);
		}
		
		this.resultsVector = new ArrayList<Result>();
	}
	
	public void closeLogger() {
		try {
			logBuffer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
