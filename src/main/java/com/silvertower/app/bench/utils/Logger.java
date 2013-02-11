package com.silvertower.app.bench.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.silvertower.app.bench.main.BenchmarkProperties;
import com.silvertower.app.bench.workload.ResultPair;

public class Logger {
	private String dbName;
	private String operation;
	private ArrayList<ResultPair> resultsVector;
	private BufferedWriter logBuffer;
	
	public Logger(String dbName, String benchmarkName) {
		this.dbName = dbName;
		try {
			logBuffer = new BufferedWriter(new FileWriter(BenchmarkProperties.logDir + benchmarkName + " - " + dbName + ".txt", false));
		} catch (IOException e) {
			System.err.println("Unable to initialize the log buffer");
			System.exit(-1);
		}
		this.resultsVector = new ArrayList<ResultPair>();
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
	
	public void log(String message) {
		try {
			logBuffer.write(message + ":" + "\n");
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
	
	public void logResult(ResultPair result) {
		resultsVector.add(result);
		try {
			logBuffer.write(result.first + ":" + result.second + "\n");
			logBuffer.flush();
		} catch (IOException e) {
			System.err.println("Unable to flush to the log buffer");
		}
	}
	
	public void logResult(Number n) {
		try {
			logBuffer.write(n + "\n");
			logBuffer.flush();
		} catch (IOException e) {
			System.err.println("Unable to flush to the log buffer");
		}
	}
	
	public void logResults(ArrayList<ResultPair> resultsVector) {
		this.resultsVector = resultsVector;
		try {
			for (int i = 0; i < resultsVector.size(); i++) {
				logBuffer.write(resultsVector.get(i).first + ":" + resultsVector.get(i).second + "\n");
				logBuffer.flush();
			}
		} catch (IOException e) {
			System.err.println("Unable to flush to the log buffer");
		}
	}
	
	public void plotResults(String xAxis, String yAxis) {
		XYSeries series = new XYSeries("GraphSeries");
		
		for (int i = 0; i < resultsVector.size(); i++) {
			ResultPair p = resultsVector.get(i);
			series.add(p.first, p.second);
		}
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
		JFreeChart graphChart = ChartFactory.createXYLineChart(operation, xAxis, yAxis, dataset, 
				PlotOrientation.VERTICAL, false, false, false);

		try {
			ChartUtilities.saveChartAsPNG(new File(BenchmarkProperties.plotsDir + dbName + "-" + operation + ".png"), graphChart, 500, 500);
			System.out.println("plotted");
		} catch (IOException e) {
			System.err.println("Unable to plot the chart " + operation + "for database " + dbName);
		}
	}
	
	public void closeLogger() {
		try {
			logBuffer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
