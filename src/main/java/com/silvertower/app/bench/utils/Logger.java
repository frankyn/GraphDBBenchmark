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

public class Logger {
	private String dbName;
	private String operation;
	private List<Result> resultsVector;
	private BufferedWriter logBuffer;
	
	public Logger(String benchmarkName) {
		try {
			logBuffer = new BufferedWriter(new FileWriter(BenchmarkProperties.logDir + benchmarkName + ".txt", false));
		} catch (IOException e) {
			System.err.println("Unable to initialize the log buffer");
			System.exit(-1);
		}
	}
	
	public void logDB(String dbName) {
		this.dbName = dbName;
		this.resultsVector = new ArrayList<Result>();
		writeAndFlush(String.format("-------- DB: %s", dbName));
	}
	
	public void logOp(String op) {
		writeAndFlush(op);
	}
	
	public void logResult(Result r) {
		writeAndFlush(r.getTime().toString());
		resultsVector.add(r);
	}
	
	public void plotResults(String xAxis, String yAxis) {
		assert resultsVector.size() != 0: "Nothing to plot!";

		List <XYSeries> series = new ArrayList<XYSeries>();
		series.add(new XYSeries("CPU Time"));
		series.add(new XYSeries("Wall Time"));
		
		for (Result r: resultsVector) {
			series.get(0).add(r.getX(), r.getTime().getCpuTime());
			series.get(1).add(r.getX(), r.getTime().getWallTime());
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
	
	private void writeAndFlush(String message) {
		try {
			logBuffer.write(message + "\n");
			logBuffer.flush();
		} catch (IOException e) {
			System.err.println("Unable to flush to the log buffer");
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
