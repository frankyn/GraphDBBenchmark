package com.silvertower.app.bench.utils;

import java.util.ArrayList;

import com.silvertower.app.bench.akka.Messages.AggregateResult;

public class PointSeries {
	private String name;
	private String dbName;
	private String xAxis;
	private String yAxis;
	private ArrayList<BoxPlotResult> resultsCollection;

	public PointSeries(String name, String dbName, String yAxis) {
		this.name = name;
		this.dbName = dbName;
		this.yAxis = yAxis;
		this.resultsCollection = new ArrayList<BoxPlotResult>();
	}
	
	public PointSeries(String name, String dbName, String xAxis, String yAxis) {
		this.name = name;
		this.dbName = dbName;
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		this.resultsCollection = new ArrayList<BoxPlotResult>();
	}

	public void addResult(String label, AggregateResult results) {
		resultsCollection.add(new BoxPlotResult(label, results));
	}
	
	public void addResult(String label, double xValue, AggregateResult results) {
		resultsCollection.add(new BoxPlotResult(label, xValue, results));
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

	public ArrayList<BoxPlotResult> getResultsCollection() {
		return resultsCollection;
	}
	
	public class BoxPlotResult {
		private String label;
		private double xValue;
		private AggregateResult results;

		public BoxPlotResult(String label, AggregateResult results) {
			this.label = label;
			this.results = results;
		}
		
		public BoxPlotResult(String label, double xValue, AggregateResult results) {
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
