package com.silvertower.app.bench.resultsprocessing;

import java.util.ArrayList;

import com.silvertower.app.bench.akka.Messages.AggregateResult;
import com.silvertower.app.bench.akka.Messages.LoadResults;
import com.silvertower.app.bench.akka.Messages.Result;

public class PointSeries {
	private String name;
	private String dbName;
	private String xAxis;
	private String yAxis;
	private ArrayList<PlotResult> resultsCollection;

	public PointSeries(String name, String dbName, String yAxis) {
		this.name = name;
		this.dbName = dbName;
		this.yAxis = yAxis;
		this.resultsCollection = new ArrayList<PlotResult>();
	}
	
	public PointSeries(String name, String dbName, String xAxis, String yAxis) {
		this.name = name;
		this.dbName = dbName;
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		this.resultsCollection = new ArrayList<PlotResult>();
	}

	public void addResult(String label, AggregateResult results) {
		resultsCollection.add(new PlotResult(label, results));
	}
	
	public void addResult(String label, double xValue, AggregateResult results) {
		resultsCollection.add(new PlotResult(label, xValue, results));
	}
	
	public void addResult(String label, LoadResults results) {
		for (Result r: results.getResults()) {
			AggregateResult aggregate = new AggregateResult(r.getTime() / 1000000000.0);
			resultsCollection.add(new PlotResult(label, r.getGraphSize(), aggregate));
		}
	}
	
	public String getName() {
		return name;
	}

	public String getDbName() {
		return dbName;
	}
	
	public String getXAxis() {
		return xAxis;
	}

	public String getYAxis() {
		return yAxis;
	}

	public ArrayList<PlotResult> getResultsCollection() {
		return resultsCollection;
	}
	
	public class PlotResult {
		private String label;
		private double xValue;
		private AggregateResult results;

		public PlotResult(String label, AggregateResult results) {
			this.label = label;
			this.results = results;
		}
		
		public PlotResult(String label, double xValue, AggregateResult results) {
			this.label = label;
			this.xValue = xValue;
			this.results = results;
		}
		
		public String getLabel() {
			return label;
		}
		
		public double getXValue() {
			return xValue;
		}

		public AggregateResult getResults() {
			return results;
		}
	}
}
