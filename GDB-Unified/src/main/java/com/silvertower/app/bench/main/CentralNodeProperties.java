package com.silvertower.app.bench.main;

import java.io.File;

import com.silvertower.app.bench.utils.Utilities;

public class CentralNodeProperties {
	public static String tempDirPath;
	public static String logDir;
	public static String plotsDir;
	
	public static void initializeProperties() {
		tempDirPath = System.getProperty("user.dir");;

		// === Log directory initialization
		logDir = tempDirPath + "\\log\\";
		File logDir = new File(CentralNodeProperties.logDir);
		if (!logDir.mkdir()) {
			Utilities.deleteDirectory(CentralNodeProperties.logDir);
			logDir.mkdir();
		}
		
		// === Plots directory initialization
		plotsDir = tempDirPath + "\\plots\\";
		File plotsDir = new File(CentralNodeProperties.plotsDir);
		if (!plotsDir.mkdir()) {
			Utilities.deleteDirectory(CentralNodeProperties.plotsDir);
			plotsDir.mkdir();
		}
	}
}
