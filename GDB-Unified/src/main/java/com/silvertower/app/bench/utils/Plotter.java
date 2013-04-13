package com.silvertower.app.bench.utils;

import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.silvertower.app.bench.main.BenchRunnerProperties;
import com.silvertower.app.bench.main.BoxPlotSeries;
import com.silvertower.app.bench.main.BoxPlotSeries.BoxPlotResult;
import com.silvertower.app.bench.main.GraphSerie;

public class Plotter {
	private List<PlotResult> resultsVector;
	private String dbName;
	private String operation;
	private String xAxis;
	private String yAxis;
	private int nVertices;
	private ArrayList<BoxPlotSeries> bpSeriesCollection;
	private static int counter = 0;
	public Plotter(String dbName, int nVertices, String operation, String xAxis, String yAxis) {
		resultsVector = new ArrayList<PlotResult>(); 
		this.dbName = dbName;
		this.operation = operation;
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		this.nVertices = nVertices;
		this.bpSeriesCollection = new ArrayList<BoxPlotSeries>();
	}
	
	public Plotter() {
		this.bpSeriesCollection = new ArrayList<BoxPlotSeries>();
	}

	public void addResult(PlotResult r) {
		resultsVector.add(r);
	}
	
//	public void plotResults() {
//		assert resultsVector.size() != 0: "Nothing to plot!";
//		XYSeries s = new XYSeries("Time");
//		
//		for (PlotResult r: resultsVector) {
//			s.add(r.getX(), r.getTime().getTime());
//		}
//		
//		XYSeriesCollection dataset = new XYSeriesCollection();
//		dataset.addSeries(s);
//		JFreeChart graphChart = ChartFactory.createXYLineChart(operation, xAxis, yAxis, dataset, 
//				PlotOrientation.VERTICAL, true, true, true);
//		graphChart.setTitle(dbName + "-" + nVertices + " - " + operation);
//		XYSplineRenderer r = new XYSplineRenderer();
//		r.setBaseShape(new Ellipse2D.Double(0,0,3,3));
//		graphChart.getXYPlot().setRenderer(r);
//
//		try {
//			ChartUtilities.saveChartAsPNG(new File(BenchRunnerProperties.plotsDir + dbName + counter + ".png"), graphChart, 500, 500);
//			counter++;
//		} catch (IOException e) {
//			System.err.println("Unable to plot the chart " + operation + "for database " + dbName);
//		}
//		
//		this.resultsVector = new ArrayList<PlotResult>();
//	}
	
	public void plotResults() {
		List<String> bpNames = new ArrayList<String>();
        for (BoxPlotSeries s: bpSeriesCollection) {
        	String currentSerieName = s.getName();
        	if (bpNames.indexOf(currentSerieName) < 0) {
        		
        		CategoryAxis xAxis = new CategoryAxis(currentSerieName);
                NumberAxis yAxis = new NumberAxis("Time");
                yAxis.setAutoRangeIncludesZero(false);
                BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
                renderer.setFillBox(true);
                BoxAndWhiskerCategoryDataset dataset = createDataset(currentSerieName);
                CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);
                JFreeChart chart = new JFreeChart(currentSerieName, plot);
        		//chart.getXYPlot().getRangeAxis().setRange(0, getMeanFromDataset(dataset));
                
                saveAsPng(currentSerieName, chart);
                
        		bpNames.add(currentSerieName);
        	}
        }
	}
	
	private double getMeanFromDataset(BoxAndWhiskerCategoryDataset dataset) {
		return 0;
	}

	private void saveAsPng(String fileName, JFreeChart chart) {
		try {
			ChartUtilities.saveChartAsPNG(new File(BenchRunnerProperties.plotsDir + fileName + counter + ".png"), chart, 500, 500);
			counter++;
		} catch (IOException e) {
			System.err.println("Unable to plot the chart");
		}
	}
	
	private BoxAndWhiskerCategoryDataset createDataset(String currentSerieName) {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
		List<String> dbNames = new ArrayList<String>();
		for (BoxPlotSeries s1: bpSeriesCollection) {
			if (s1.getName().equals(currentSerieName) && dbNames.indexOf(s1.getDbName()) < 0) {
				for (BoxPlotResult r: s1.getResultsCollection()) {
					dataset.add(r.getResults().getAllResultsAsDouble(), s1.getDbName(), r.getLabel());
				}
				dbNames.add(s1.getDbName());
			}
		}
		return dataset;
    }
       
	public void addBPSeries(BoxPlotSeries s) {
		bpSeriesCollection.add(s);
	}
}
