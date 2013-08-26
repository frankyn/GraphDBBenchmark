package com.silvertower.app.bench.resultsprocessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bethecoder.ascii_table.ASCIITable;
import com.bethecoder.ascii_table.ASCIITableHeader;
import com.silvertower.app.bench.akka.Messages.AggregateResult;
import com.silvertower.app.bench.workload.Workload;

public class Statistics {
	public static List<StatisticsEntry> stats = new ArrayList<StatisticsEntry>();
	
	public static class StatisticsEntry {
		private String dbName;
		private Workload w;
		private StatisticsReport report;
		private boolean isCombined;
		
		public StatisticsEntry(String dbName, Workload w, StatisticsReport report) {
			this.dbName = dbName;
			this.w = w;
			this.report = report;
			this.isCombined = false;
		}
		
		public StatisticsEntry(String dbName, Workload w, StatisticsReport report, boolean isCombined) {
			this.dbName = dbName;
			this.w = w;
			this.report = report;
			this.isCombined = true;
		}
	}
	
	public static class StatisticsReport {
		private double mean;
		private double median;
		private double stdDeviation;
		private double min;
		private double max;
		
		public StatisticsReport (double mean, double median, double stdDeviation, double min, 
				double max) {
			this.mean = mean;
			this.median = median;
			this.stdDeviation = stdDeviation;
			this.min = min;
			this.max = max;
		}
		
		public String toString() {
			return String.format("Mean: %f\nMedian: %f\nStandard deviation:%f\nMin:%f\nMax:%f", 
					mean, median, stdDeviation, min, max);
		}
	}
	
	public static class ComparisonReport {
		private List<List<StatisticsEntry>> compReport;
		
		public ComparisonReport() {
			compReport = new ArrayList<List<StatisticsEntry>>();
		}
		
		public void addListStatsEntry(List<StatisticsEntry> entries) {
			compReport.add(entries);
		}
		
		public String toString() {
			StringBuilder report = new StringBuilder();
			for (List<StatisticsEntry> entries: compReport) {
				List<ASCIITableHeader> headerObjs = new ArrayList<ASCIITableHeader>();
				StatisticsEntry firstEntry = entries.get(0);
				
				if (firstEntry.isCombined) {
					headerObjs.add(new ASCIITableHeader(firstEntry.w.getClass().getSimpleName()));
				}
				else {
					headerObjs.add(new ASCIITableHeader(firstEntry.w.toString()));
				}
				
				for (StatisticsEntry e: entries) {
					headerObjs.add(new ASCIITableHeader(e.dbName));
				}
				
				String relativeDB = entries.get(0).dbName;
				StatisticsReport relativeReport = entries.get(0).report;
				
				List<List<String>> data = new ArrayList<List<String>>();
				
				List<String> meanLine = new ArrayList<String>();
				meanLine.add("Mean");
				data.add(meanLine);
				
				List<String> relativeMeanLine = new ArrayList<String>();
				relativeMeanLine.add(String.format("vs %s", relativeDB));
				data.add(relativeMeanLine);
				
				List<String> medianLine = new ArrayList<String>();
				medianLine.add("Median");
				data.add(medianLine);
				
				List<String> relativeMedianLine = new ArrayList<String>();
				relativeMedianLine.add(String.format("vs %s", relativeDB));
				data.add(relativeMedianLine);
				
				List<String> stdDevLine = new ArrayList<String>();
				stdDevLine.add("Standard deviation");
				data.add(stdDevLine);
				
				List<String> relativeStdDevLine = new ArrayList<String>();
				relativeStdDevLine.add(String.format("vs %s", relativeDB));
				data.add(relativeStdDevLine);
				
				List<String> minLine = new ArrayList<String>();
				minLine.add("Min");
				data.add(minLine);
				
				List<String> relativeMinLine = new ArrayList<String>();
				relativeMinLine.add(String.format("vs %s", relativeDB));
				data.add(relativeMinLine);
				
				List<String> maxLine = new ArrayList<String>();
				maxLine.add("Max");
				data.add(maxLine);
				
				List<String> relativeMaxLine = new ArrayList<String>();
				relativeMaxLine.add(String.format("vs %s", relativeDB));
				data.add(relativeMaxLine);
				
				for (StatisticsEntry e: entries) {
					StatisticsReport r = e.report;
					meanLine.add(String.format("%f", r.mean));
					medianLine.add(String.format("%f", r.median));
					stdDevLine.add(String.format("%f", r.stdDeviation));
					minLine.add(String.format("%f", r.min));
					maxLine.add(String.format("%f", r.max));
					
					double comparedMean = ((r.mean - relativeReport.mean) / relativeReport.mean)*100;
					relativeMeanLine.add(String.format("%f%%", comparedMean));
					
					double comparedMedian = ((r.median - relativeReport.median) / relativeReport.median)*100;
					relativeMedianLine.add(String.format("%f%%", comparedMedian));
					
					double comparedStdDev = ((r.stdDeviation - relativeReport.stdDeviation) / relativeReport.stdDeviation)*100;
					relativeStdDevLine.add(String.format("%f%%", comparedStdDev));
					
					double comparedMin = ((r.min - relativeReport.min) / relativeReport.min)*100;
					relativeMinLine.add(String.format("%f%%", comparedMin));
					
					double comparedMax = ((r.max - relativeReport.max) / relativeReport.max)*100;
					relativeMaxLine.add(String.format("%f%%", comparedMax));
				}
				
				int firstDim = data.size();
				int headerArraySize = headerObjs.size();
				ASCIITableHeader[] headerArray = headerObjs.toArray(new ASCIITableHeader[headerArraySize]);
				String[][] dataArray = new String[firstDim][];
				for (int i = 0; i < dataArray.length; i++) {
					dataArray[i] = data.get(i).toArray(new String[data.get(i).size()]);
				}
				
				report.append(ASCIITable.getInstance().getTable(headerArray, dataArray));
				report.append("\n\n");
			}
			return report.toString();
		}
	}
	
	public static ComparisonReport computeReport() {
		ComparisonReport r = new ComparisonReport();
		List<String> alreadyAddedToReport = new ArrayList<String>();
		for (StatisticsEntry e: stats) {
			if (!alreadyAddedToReport.contains(e.w.toString())) {
				List<StatisticsEntry> relatedEntries = new ArrayList<StatisticsEntry>();
				for (StatisticsEntry e1: stats) {
					if ((e1.w.toString().equals(e.w.toString()))) {
						relatedEntries.add(e1);
					}
				}
				r.addListStatsEntry(relatedEntries);
				alreadyAddedToReport.add(e.toString());
			}
		}
		return r;
	}
	
	public static StatisticsEntry combineSameWorkloadTypeEntries(Workload concernedWorkloadType, 
			String dbNameConcerned, List<StatisticsEntry> entries) {
		double combinedMean = 0;
		double combinedMedian = 0;
		double combinedStdDeviation = 0;
		double combinedMin = 0;
		double combinedMax = 0;
		for (StatisticsEntry entry: entries) {
			combinedMean += entry.report.mean;
			combinedMedian += entry.report.median;
			combinedStdDeviation += entry.report.stdDeviation;
			combinedMin += entry.report.min;
			combinedMax += entry.report.max;
		}
		StatisticsReport combinedReport = new StatisticsReport(combinedMean/entries.size(), 
				combinedMedian/entries.size(), combinedStdDeviation/entries.size(), 
				combinedMin/entries.size(), combinedMax/entries.size());
		StatisticsEntry combinedEntry = new StatisticsEntry(dbNameConcerned, concernedWorkloadType, 
				combinedReport, true);
		return combinedEntry;
	}
	
	public static StatisticsReport addStatEntry(AggregateResult r, String dbName, 
			Workload workload) {
		double mean = computeMean(r);
		double median = computeMedian(r);
		double stdDeviation = computeVariance(r);
		double min = computeMin(r);
		double max = computeMax(r);
		StatisticsReport report = new StatisticsReport(mean, median, stdDeviation, min, max);
		StatisticsEntry statEntry = new StatisticsEntry(dbName, workload, report);
		stats.add(statEntry);
		return report;
	}

	private static double computeMax(AggregateResult r) {
		double max = Double.MIN_VALUE;
		for (Double value: r.getAllResultsAsDouble()) {
			max = value > max ? value : max;
		}
		return max;
	}

	private static double computeMin(AggregateResult r) {
		double min = Double.MAX_VALUE;
		for (Double value: r.getAllResultsAsDouble()) {
			min = value < min ? value : min;
		}
		return min;
	}

	private static double computeVariance(AggregateResult r) {
		List<Double> values = r.getAllResultsAsDouble();
		double mean = computeMean(r);
		double deviationSum = 0;
		for (Double value: values) {
			deviationSum += Math.pow(value - mean, 2);
		}
		return deviationSum / values.size();
	}

	private static double computeMedian(AggregateResult r) {
		List<Double> values = r.getAllResultsAsDouble();
		Collections.sort(values);
		if (values.size()%2!=0) return values.get(values.size()/2);
		else return (values.get((values.size()/2)-1) + values.get(values.size()/2)) / 2;
	}

	private static double computeMean(AggregateResult r) {
		List<Double> values = r.getAllResultsAsDouble();
		double total = 0;
		for (Double value: values) {
			total += value;
		}
		return total / values.size();
	}
}
