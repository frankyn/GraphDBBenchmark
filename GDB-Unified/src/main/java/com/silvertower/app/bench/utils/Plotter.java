package com.silvertower.app.bench.utils;

import java.io.File;
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

import com.silvertower.app.bench.main.CentralNodeProperties;

public class Plotter {
	private List<PlotResult> resultsVector;
	private String dbName;
	private String operation;
	private String xAxis;
	private String yAxis;
	public Plotter(String dbName, String operation, String xAxis, String yAxis) {
		resultsVector = new ArrayList<PlotResult>(); 
		this.dbName = dbName;
		this.operation = operation;
		this.xAxis = xAxis;
		this.yAxis = yAxis;
	}
	
	public void addResult(PlotResult r) {
		resultsVector.add(r);
	}
	
	public void plotResults() {
		assert resultsVector.size() != 0: "Nothing to plot!";
		XYSeries s = new XYSeries("Wall Time");
		
		for (PlotResult r: resultsVector) {
			s.add(r.getX(), r.getTime().getWallTime());
		}
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(s);
		JFreeChart graphChart = ChartFactory.createXYLineChart(operation, xAxis, yAxis, dataset, 
				PlotOrientation.VERTICAL, true, true, true);
		graphChart.getXYPlot().setRenderer(new XYSplineRenderer());

		try {
			ChartUtilities.saveChartAsPNG(new File(CentralNodeProperties.plotsDir + dbName + "-" + operation + "-" + xAxis + "-" + yAxis + ".png"), graphChart, 500, 500);
			System.out.println("plotted");
		} catch (IOException e) {
			System.err.println("Unable to plot the chart " + operation + "for database " + dbName);
		}
		
		this.resultsVector = new ArrayList<PlotResult>();
	}
}
