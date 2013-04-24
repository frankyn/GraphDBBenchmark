package com.silvertower.app.bench.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

import com.silvertower.app.bench.akka.Messages.AggregateResult;
import com.silvertower.app.bench.akka.Messages.TimeResult;
import com.silvertower.app.bench.main.BenchRunnerProperties;
import com.silvertower.app.bench.utils.PointSeries.PlotResult;

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
                for (BoxAndWhiskerCategoryDataset dataset: createBoxPlotDatasets(currentSerieName)) {
                	CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);
                    JFreeChart chart = new JFreeChart(currentSerieName, plot);
                    saveAsPng(currentSerieName, chart);
                }
        		bpNames.add(currentSerieName);
        	}
        }
	}

	private List<DefaultBoxAndWhiskerCategoryDataset> createBoxPlotDatasets(String currentSerieName) {
        List<DefaultBoxAndWhiskerCategoryDataset> datasets = new ArrayList<DefaultBoxAndWhiskerCategoryDataset>();
        DefaultBoxAndWhiskerCategoryDataset combinedDataset = new DefaultBoxAndWhiskerCategoryDataset();
		List<String> dbNames = new ArrayList<String>();
		for (PointSeries s1: boxPlotSeriesCollection) {
			if (s1.getName().equals(currentSerieName) && dbNames.indexOf(s1.getDbName()) < 0) {
				DefaultBoxAndWhiskerCategoryDataset isolatedDataset = new DefaultBoxAndWhiskerCategoryDataset();
				for (PlotResult r: s1.getResultsCollection()) {
					isolatedDataset.add(r.getResults().getAllResultsAsDouble(), s1.getDbName(), r.getLabel());
					combinedDataset.add(r.getResults().getAllResultsAsDouble(), s1.getDbName(), r.getLabel());
				}
				datasets.add(isolatedDataset);
				dbNames.add(s1.getDbName());
			}
		}
		datasets.add(0, combinedDataset);
		System.out.println(datasets.size());
		return datasets;
    }
	
	private void plotXYPlot() {
		List<String> bpNames = new ArrayList<String>();
		for (PointSeries s: xyPlotSeriesCollection) {
			String currentSerieName = s.getName();
        	if (bpNames.indexOf(currentSerieName) < 0) {
        		boolean[] possibilities = {true, false};
        		for (boolean b: possibilities) {
        			XYSeries[] series = createXYPlotDataset(currentSerieName, b);
            		XYSeriesCollection dataset = new XYSeriesCollection();
            		for (XYSeries serie: series) {
            			dataset.addSeries(serie);
            			XYSeriesCollection datasetSingle = new XYSeriesCollection();
            			datasetSingle.addSeries(serie);
            			String chartName = b ? currentSerieName + " corrected" : currentSerieName;
            			createXYChart(chartName, s.getXAxis(), s.getYAxis(), datasetSingle);
            		}
            		String chartName = b ? currentSerieName + " corrected" : currentSerieName;
            		createXYChart(chartName, s.getXAxis(), s.getYAxis(), dataset);
        		}
        		
        		bpNames.add(currentSerieName);
        	}
		}
	}
	
	private void createXYChart(String chartName, String xAxis, String yAxis, XYSeriesCollection c) {
		JFreeChart chart = ChartFactory.createXYLineChart(chartName, xAxis, yAxis, c,
				PlotOrientation.VERTICAL, true, true, true);
		chart.getXYPlot().setRenderer(new XYSplineRenderer());
        saveAsPng(chartName, chart);
	}
    
	private XYSeries[] createXYPlotDataset(String currentSerieName, boolean correctedMeanDesired) {
		List<XYSeries> series = new ArrayList<XYSeries>();
		List<String> dbNames = new ArrayList<String>();
		for (PointSeries s1: xyPlotSeriesCollection) {
			if (s1.getName().equals(currentSerieName) && dbNames.indexOf(s1.getDbName()) < 0) {
				XYSeries s = new XYSeries(s1.getDbName());
				for (PlotResult r: s1.getResultsCollection()) {
					if (correctedMeanDesired) s.add(r.getXValue(), getCorrectedMean(r.getResults()));
					else s.add(r.getXValue(), r.getResults().getMean().getTime());
				}
				series.add(s);
				dbNames.add(s1.getDbName());
			}
		}
		return series.toArray(new XYSeries[series.size()]);
	}

	private void saveAsPng(String fileName, JFreeChart chart) {
		try {
			ChartUtilities.saveChartAsPNG(new File(BenchRunnerProperties.plotsDir + fileName + 
					counter + ".png"), chart, 500, 500);
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
	
	private double getCorrectedMean(AggregateResult r) {
		double mean = 0;
		List<Double> values = filterAggregate(r).getAllResultsAsDouble();
		
		for (double v: values) {
			mean += v;
		}
		return mean / values.size();
	}
	
	private AggregateResult filterAggregate(AggregateResult r) {
		List<Double> values = r.getAllResultsAsDouble();
		Collections.sort(values);
		int nbr = values.size();
		double q1 = nbr%4==0 ? values.get((nbr/4)-1) : (values.get(nbr/4) + values.get(nbr/4)+1)/2;
		double q2 = nbr*3%4==0 ? values.get(nbr*3/4) : (values.get(nbr*3/4) + values.get((nbr*3/4)+1))/2;
		double iq = q2 - q1;
		double multipliedIq = iq * 1.5;
		double lowLimit = q1 - multipliedIq;
		double highLimit = q2 + multipliedIq;
		
		AggregateResult modified = new AggregateResult();
		for (Double v: values) {
			System.out.println("value:" + v);
			if (v >= lowLimit && v <= highLimit) modified.addTime(new TimeResult(v));
			else System.out.println("ejected");
		}
		
		return modified;
	}
}
