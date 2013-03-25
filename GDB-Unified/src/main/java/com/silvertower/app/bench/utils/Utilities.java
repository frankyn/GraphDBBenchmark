package com.silvertower.app.bench.utils;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.apache.commons.io.FileUtils;

import com.silvertower.app.bench.main.ClientProperties;

import bb.util.Benchmark;


public class Utilities {
	static double nanoToSFactor = 1000000000.0;
	static int numberOfMeasurements = 5;
	
	public static void deleteDirectory(String place) {
		File f = new File(place);
		if (f.isDirectory()) {
			try {
				FileUtils.deleteDirectory(f);
			} catch (IOException e) {
				System.err.println("Unable to delete directory: " + place);
			}
		}
		else {
			f.delete();
		}
	}
	
	// Inspired from:
	// http://stackoverflow.com/questions/7768071/java-delete-a-folder-content
	public static void deleteDirectoryContent(File dir) {
		File[] files = dir.listFiles();
	    if (files != null) { 
	        for (File f: files) {
	            if (f.isDirectory()) {
	            	deleteDirectoryContent(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	}
	
	public static double benchTask(Runnable task, boolean preciseBenchmarking) {
		if (preciseBenchmarking) {
			Benchmark b = null;
			Benchmark.Params p = new Benchmark.Params();
			double wallTimeMean = 0;
			try {
				p.setManyExecutions(true);
				p.setNumberMeasurements(numberOfMeasurements);
				b = new Benchmark(task, p);
				wallTimeMean = b.getMean();
				
			} catch (IllegalArgumentException | IllegalStateException e) { 
				System.err.println("Error while benchmarking");
				e.printStackTrace(); 
			} catch (Exception e) {
				System.err.println("Error while benchmarking");
				e.printStackTrace(); 
			}
			return wallTimeMean;
		}
		
		else {
			long wall1 = System.nanoTime();
			ThreadMXBean mxbean = ManagementFactory.getThreadMXBean();
			for (int i = 0; i < ClientProperties.traversalMeanTimes; i++) {
				task.run();
			}
			long wall2 = System.nanoTime();
			double wallSpentMean = (wall2-wall1)/ClientProperties.traversalMeanTimes/nanoToSFactor;
			return wallSpentMean;
		}
		
	}
}
