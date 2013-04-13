package com.silvertower.app.bench.main;

import java.util.ArrayList;

import com.silvertower.app.bench.akka.Messages.AggregateResult;

public class BoxPlotSeries {
	private String name;
	private String dbName;
	private String yAxis;
	private ArrayList<BoxPlotResult> resultsCollection;

	public BoxPlotSeries(String name, String dbName, String yAxis) {
		this.name = name;
		this.dbName = dbName;
		this.yAxis = yAxis;
		this.resultsCollection = new ArrayList<BoxPlotResult>();
	}

	public void addResult(String label, AggregateResult results) {
		resultsCollection.add(new BoxPlotResult(label, results));
	}
	
	public String getName() {
		return name;
	}

	public String getDbName() {
		return dbName;
	}

	public String getyAxis() {
		return yAxis;
	}

	public ArrayList<BoxPlotResult> getResultsCollection() {
		return resultsCollection;
	}
	
	public class BoxPlotResult {
		private String label;
		private AggregateResult results;

		public BoxPlotResult(String label, AggregateResult results) {
			this.label = label;
			this.results = results;
		}
		
		public String getLabel() {
			return label;
		}

		public AggregateResult getResults() {
			return results;
		}
	}
}
