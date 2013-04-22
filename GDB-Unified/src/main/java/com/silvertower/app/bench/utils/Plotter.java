package com.silvertower.app.bench.utils;

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
import com.silvertower.app.bench.utils.PointSeries.BoxPlotResult;

public class Plotter {
	private ArrayList<PointSeries> boxPlotSeriesCollection;
	private ArrayList<PointSeries> xyPlotSeriesCollection;
	private static int counter = 0;
	
	public Plotter() {
		this.boxPlotSeriesCollection = new ArrayList<PointSeries>();
		this.xyPlotSeriesCollection = new ArrayList<PointSeries>();
	}

	public void plotResults() {
		plotBoxPlots();
		plotXYPlot();
	}
	
	private void plotBoxPlots() {
		List<String> bpNames = new ArrayList<String>();
		for (PointSeries s: boxPlotSeriesCollection) {
        	String currentSerieName = s.getName();
        	if (bpNames.indexOf(currentSerieName) < 0) {
        		CategoryAxis xAxis = new CategoryAxis(currentSerieName);
                NumberAxis yAxis = new NumberAxis("Time");
                yAxis.setAutoRangeIncludesZero(false);
                BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
                renderer.setFillBox(true);
                BoxAndWhiskerCategoryDataset dataset = createBoxPlotDataset(currentSerieName);
                CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);
                JFreeChart chart = new JFreeChart(currentSerieName, plot);
                saveAsPng(currentSerieName, chart);
        		bpNames.add(currentSerieName);
        	}
        }
	}

	private BoxAndWhiskerCategoryDataset createBoxPlotDataset(String currentSerieName) {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
		List<String> dbNames = new ArrayList<String>();
		for (PointSeries s1: boxPlotSeriesCollection) {
			if (s1.getName().equals(currentSerieName) && dbNames.indexOf(s1.getDbName()) < 0) {
				for (BoxPlotResult r: s1.getResultsCollection()) {
					dataset.add(r.getResults().getAllResultsAsDouble(), s1.getDbName(), r.getLabel());
				}
				dbNames.add(s1.getDbName());
			}
		}
		return dataset;
    }
	
	private void plotXYPlot() {
		List<String> bpNames = new ArrayList<String>();
		for (PointSeries s: xyPlotSeriesCollection) {
			String currentSerieName = s.getName();
        	if (bpNames.indexOf(currentSerieName) < 0) {
        		XYSeries[] series = createXYPlotDataset(currentSerieName);
        		XYSeriesCollection dataset = new XYSeriesCollection();
        		for (XYSeries serie: series) {
        			dataset.addSeries(serie);
        			XYSeriesCollection datasetSingle = new XYSeriesCollection();
        			datasetSingle.addSeries(serie);
        			JFreeChart chart = ChartFactory.createXYLineChart(currentSerieName, s.getXAxis(), s.getYAxis(), datasetSingle,
            				PlotOrientation.VERTICAL, true, true, true);
            		chart.getXYPlot().setRenderer(new XYSplineRenderer());
                    saveAsPng(currentSerieName, chart);
        		}
    			JFreeChart chart = ChartFactory.createXYLineChart(currentSerieName, s.getXAxis(), s.getYAxis(), dataset,
        				PlotOrientation.VERTICAL, true, true, true);
        		chart.getXYPlot().setRenderer(new XYSplineRenderer());
                saveAsPng(currentSerieName, chart);
        		bpNames.add(currentSerieName);
        	}
		}
	}
    
	private XYSeries[] createXYPlotDataset(String currentSerieName) {
		List<XYSeries> series = new ArrayList<XYSeries>();
		List<String> dbNames = new ArrayList<String>();
		for (PointSeries s1: boxPlotSeriesCollection) {
			if (s1.getName().equals(currentSerieName) && dbNames.indexOf(s1.getDbName()) < 0) {
				XYSeries s = new XYSeries(s1.getDbName());
				for (BoxPlotResult r: s1.getResultsCollection()) {
					s.add(r.getXValue(), r.getResults().getMean().getTime());
				}
				series.add(s);
				dbNames.add(s1.getDbName());
			}
		}
		return series.toArray(new XYSeries[series.size()]);
	}

	private void saveAsPng(String fileName, JFreeChart chart) {
		try {
			ChartUtilities.saveChartAsPNG(new File(BenchRunnerProperties.plotsDir + fileName + counter + ".png"), chart, 500, 500);
			counter++;
		} catch (IOException e) {
			System.err.println("Unable to plot the chart");
		}
	}
	
	public void addBPSeries(PointSeries s) {
		boxPlotSeriesCollection.add(s);
	}
	
	public void addXYPointsSeries(PointSeries s) {
		xyPlotSeriesCollection.add(s);
	}
}
