package com.silvertower.app.bench.main;

import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;

import com.silvertower.app.bench.utils.Utilities;

public class ClientProperties {
	public static String tempDir;
	
	public static void initializeProperties() {
		CodeSource codeSource = BenchmarkLauncher.class.getProtectionDomain().getCodeSource();
		File jarFile = null;
		try {
			jarFile = new File(codeSource.getLocation().toURI().getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		tempDir = jarFile.getParentFile().getPath() + "//..//clienttemp//";
		File f = new File(tempDir);
		if (!f.mkdir()) {
			Utilities.deleteDirectory(tempDir);
			f.mkdir();
		}
	}
}