package com.silvertower.app.bench.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.silvertower.app.bench.main.BenchmarkProperties;

import bb.util.Benchmark;


public class Utilities {
	static double threshold = BenchmarkProperties.threshold;
	
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
	
	public static double[] benchTask(Runnable task) { 
		Benchmark b = null;
		Benchmark.Params p = new Benchmark.Params();
		double wallTime = 0;
		double cpuTime = 0;
		try {
			if (BenchmarkProperties.cpuTimeRequired) {
				p.setManyExecutions(false);
				p.setMeasureCpuTime(true);
				b = new Benchmark(task, p);
				if (b.getFirst() < threshold) {
					p.setManyExecutions(true);
					p.setNumberMeasurements(1);
					b = new Benchmark(task, p);
					cpuTime = b.getMean();
				}
				else {
					cpuTime = b.getFirst();
				}
			}
			
			p.setManyExecutions(false);
			p.setMeasureCpuTime(false);
			b = new Benchmark(task, p);
			if (b.getFirst() < threshold) {
				p.setManyExecutions(true);
				p.setNumberMeasurements(1);
				b = new Benchmark(task, p);
				wallTime = b.getMean();
			}
			else {
				wallTime = b.getFirst();
			}
			
		} catch (IllegalArgumentException | IllegalStateException e) { 
			System.err.println("Error while benchmarking");
			e.printStackTrace(); 
		} catch (Exception e) {
			System.err.println("Error while benchmarking");
			e.printStackTrace(); 
		}
		
		return new double[]{wallTime, cpuTime};
	}
}
