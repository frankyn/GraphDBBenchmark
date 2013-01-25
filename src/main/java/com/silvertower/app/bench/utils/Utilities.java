package com.silvertower.app.bench.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.silvertower.app.bench.main.BenchmarkProperties;
import com.silvertower.app.bench.main.Globals;

public class Utilities {
	public static void deleteDatabase(String place) {
		File f = new File(place);
		if (f.isDirectory()) {
			try {
				FileUtils.deleteDirectory(f);
			} catch (IOException e) {
				System.err.println("Unable to remove databases");
				System.err.println(e.getClass());
			}
		}
		else {
			f.delete();
		}
	}
	
	public static void log(String dbName, long result) {
		try {
			Globals.logBuffer = new BufferedWriter(new FileWriter(BenchmarkProperties.logFilePath, true));
			Globals.logBuffer.write(dbName + ": " + String.valueOf((double)result/Globals.msToSFactor) + "\n");
			Globals.logBuffer.close();
		} catch (IOException e) {}
	}
	
	public static void log(String category) {
		try {
			Globals.logBuffer = new BufferedWriter(new FileWriter(BenchmarkProperties.logFilePath, true));
			Globals.logBuffer.write("\n" + category + "\n");
			Globals.logBuffer.close();
		} catch (IOException e) {}
	}
}
