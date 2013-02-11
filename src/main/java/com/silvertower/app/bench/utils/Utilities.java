package com.silvertower.app.bench.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;


public class Utilities {
	public static void deleteDirectory(String place) {
		File f = new File(place);
		if (f.isDirectory()) {
			try {
				FileUtils.deleteDirectory(f);
			} catch (IOException e) {
				System.err.println("Unable to delete directory: " + place);
				System.exit(-1);
			}
		}
		else {
			f.delete();
		}
	}
}
