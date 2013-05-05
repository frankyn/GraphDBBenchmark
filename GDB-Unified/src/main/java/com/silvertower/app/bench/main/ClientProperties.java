package com.silvertower.app.bench.main;

import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;

public class ClientProperties {
	public static int intensiveMeanTimes = 100;
	public static int traversalMeanTimes = 100;
	public static String tempDir;
	
	public static void initializeProperties() {
		CodeSource codeSource = BenchmarkLauncher.class.getProtectionDomain().getCodeSource();
		File jarFile = null;
		try {
			jarFile = new File(codeSource.getLocation().toURI().getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		tempDir = jarFile.getParentFile().getPath() + "//..//temp//";
		File f = new File(tempDir);
		f.mkdir();
	}
}
