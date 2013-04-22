package com.silvertower.app.bench.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;


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
}
