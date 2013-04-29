package com.silvertower.app.bench.main;

import java.io.File;

import com.silvertower.app.bench.utils.Utilities;


public class BenchRunnerProperties {
	public static String tempDirPath;
	public static String logDir;
	public static String plotsDir;
	public static long maxWorkTimeInNS = 1000000000L;
	
	public static void initializeProperties() {
		tempDirPath = System.getProperty("user.dir");

		// === Log directory initialization
		logDir = tempDirPath + "/log/";
		File logDir = new File(BenchRunnerProperties.logDir);
		logDir.mkdir();
		if (!logDir.mkdir()) {
			Utilities.deleteDirectory(BenchRunnerProperties.logDir);
			logDir.mkdir();
		}
		
		// === Plots directory initialization
		plotsDir = tempDirPath + "/plots/";
		File plotsDir = new File(BenchRunnerProperties.plotsDir);
		plotsDir.mkdir();
		if (!plotsDir.mkdir()) {
			Utilities.deleteDirectory(BenchRunnerProperties.plotsDir);
			plotsDir.mkdir();
		}
	}
}
