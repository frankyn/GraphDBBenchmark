package com.silvertower.app.bench.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bethecoder.ascii_table.ASCIITable;
import com.bethecoder.ascii_table.ASCIITableHeader;
import com.silvertower.app.bench.akka.Messages.AggregateResult;
import com.silvertower.app.bench.workload.IntensiveWorkload;
import com.silvertower.app.bench.workload.TraversalWorkload;

public class Statistics {
	public static List<StatisticsEntry> stats = new ArrayList<StatisticsEntry>();
	
	public static class StatisticsEntry <T> {
		private AggregateResult result;
		private String dbName;
		private T workload;
		private int nbrOps;
		private int nbrClients;
		private StatisticsReport report;
		
		public StatisticsEntry(AggregateResult result, String dbName, T workload, 
				StatisticsReport report) {
			this.result = result;
			this.dbName = dbName;
			this.workload = workload;
			this.report = report;
		}
		
		public StatisticsEntry(AggregateResult result, String dbName, T workload, int nbrOps, 
				int nbrClients, StatisticsReport report) {
			this.result = result;
			this.dbName = dbName;
			this.workload = workload;
			this.nbrOps = nbrOps;
			this.nbrClients = nbrClients;
			this.report = report;
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
			compReport = new ArrayList();
		}
		
		public void addListStatsEntry(List<StatisticsEntry> entries) {
			compReport.add(entries);
		}
		
		public String toString() {
			StringBuilder report = new StringBuilder();
			for (List<StatisticsEntry> entries: compReport) {
				List<ASCIITableHeader> headerObjs = new ArrayList<ASCIITableHeader>();
				StatisticsEntry firstEntry = entries.get(0);
				if (firstEntry.workload instanceof IntensiveWorkload) {
					String workloadCompleteName = String.format("%s/%s clients/%s ops", 
							firstEntry.workload.toString(), firstEntry.nbrClients, firstEntry.nbrOps);
					headerObjs.add(new ASCIITableHeader(workloadCompleteName));
				}
				else headerObjs.add(new ASCIITableHeader(firstEntry.workload.toString()));
				
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
					
					double comparedMean = r.mean / relativeReport.mean;
					if (comparedMean < 1) relativeMeanLine.add(String.format("-%f%%",(1-comparedMean)*100));
					else relativeMeanLine.add(String.format("+%f%%",(comparedMean-1)*100));
					
					double comparedMedian = r.median / relativeReport.median;
					if (comparedMedian < 1) relativeMedianLine.add(String.format("-%f%%",(1-comparedMedian)*100));
					else relativeMedianLine.add(String.format("+%f%%",(comparedMedian-1)*100));
					
					double comparedStdDev = r.stdDeviation / relativeReport.stdDeviation;
					if (comparedStdDev < 1) relativeStdDevLine.add(String.format("-%f%%",(1-comparedStdDev)*100));
					else relativeStdDevLine.add(String.format("+%f%%",(comparedStdDev-1)*100));
					
					double comparedMin = r.min / relativeReport.min;
					if (comparedMin < 1) relativeMinLine.add(String.format("-%f%%",(1-comparedMin)*100));
					else relativeMinLine.add(String.format("+%f%%",(comparedMin-1)*100));
					
					double comparedMax = r.max / relativeReport.max;
					if (comparedMax < 1) relativeMaxLine.add(String.format("-%f%%",(1-comparedMax)*100));
					else relativeMaxLine.add(String.format("+%f%%",(comparedMax-1)*100));
				}
				
				int firstDim = data.size();
				int secondDim = data.get(0).size();
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
			System.out.println(e.workload);
			System.out.println(e.nbrClients);
			System.out.println(e.nbrOps);
			String workloadFullName = e.workload.toString() + e.nbrClients + e.nbrOps;
			if (!alreadyAddedToReport.contains(workloadFullName)) {
				List<StatisticsEntry> relatedEntries = new ArrayList<StatisticsEntry>();
				for (StatisticsEntry e1: stats) {
					if ((e1.workload.toString() + e1.nbrClients + e1.nbrOps).equals(workloadFullName)) {
						relatedEntries.add(e1);
					}
				}
				r.addListStatsEntry(relatedEntries);
				alreadyAddedToReport.add(workloadFullName);
			}
		}
		return r;
	}
	
	public static StatisticsReport addStatEntry(AggregateResult r, String dbName, 
			IntensiveWorkload workload) {
		double mean = computeMean(r);
		double median = computeMedian(r);
		double stdDeviation = computeStdDeviation(r);
		double min = computeMin(r);
		double max = computeMax(r);
		int nOps = workload.getnOps();
		int nClients = workload.getnClients();
		StatisticsReport report = new StatisticsReport(mean, median, stdDeviation, min, max);
		StatisticsEntry<IntensiveWorkload> statEntry = new StatisticsEntry<IntensiveWorkload>(r, 
				dbName, workload, nOps, nClients, report);
		stats.add(statEntry);
		return report;
	}
	
	public static StatisticsReport addStatEntry(AggregateResult r, String dbName, 
			TraversalWorkload workload) {
		double mean = computeMean(r);
		double median = computeMedian(r);
		double stdDeviation = computeStdDeviation(r);
		double min = computeMin(r);
		double max = computeMax(r);
		StatisticsReport report = new StatisticsReport(mean, median, stdDeviation, min, max);
		StatisticsEntry<TraversalWorkload> statEntry = new StatisticsEntry<TraversalWorkload>(r, 
				dbName, workload, report);
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

	private static double computeStdDeviation(AggregateResult r) {
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
