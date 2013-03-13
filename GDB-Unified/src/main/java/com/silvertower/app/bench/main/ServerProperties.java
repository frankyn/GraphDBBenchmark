package com.silvertower.app.bench.main;

import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;

import org.apache.log4j.PropertyConfigurator;

import com.silvertower.app.bench.utils.Utilities;


public class ServerProperties {
	public static final String rexsterServerIp = "127.0.0.1";
	public static final int rexsterServerPort = 8182;
	public static String tempDirPath;
	public static String datasetsDir;
	public static String rexsterDirPath;
	public static String resourcesDirPath;
	public static String dbsDirTemp;
	public static String dbDirDexTemp;
	public static String dbDirNeo4jTemp;
	public static String dbDirTitanTemp;
	public static String dbDirOrientTemp;
	public static String dbsDirWork;
	public static String dbDirDexWork;
	public static String dbDirNeo4jWork;
	public static String dbDirTitanWork;
	public static String dbDirOrientWork;
	public static String pythonDir;
	public static int meanTimes = 10;
	public static double threshold = 5;
	public static boolean cpuTimeRequired = true;
	
	public static void initializeProperies() {
		CodeSource codeSource = BenchmarkLauncher.class.getProtectionDomain().getCodeSource();
		File jarFile = null;
		try {
			jarFile = new File(codeSource.getLocation().toURI().getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		resourcesDirPath = jarFile.getParentFile().getPath() + "\\..\\resources\\";
		PropertyConfigurator.configure(resourcesDirPath + "log4j.properties");
		
		tempDirPath = jarFile.getParentFile().getPath() + "\\..\\temp\\";
		File workDir = new File(tempDirPath);
		if (!workDir.mkdir()) {
			Utilities.deleteDirectory(ServerProperties.tempDirPath);
			workDir.mkdir();
		}
		
		// === Datasets directory initialization
		datasetsDir = tempDirPath + "datasets\\";
		File datasetsDir = new File(ServerProperties.datasetsDir);
		if (!datasetsDir.mkdir()) {
			Utilities.deleteDirectory(ServerProperties.datasetsDir);
			datasetsDir.mkdir();
		}
		
		// === Python scripts directory
		pythonDir = resourcesDirPath + "python\\";
		
		// === Directory where the temporary graphs (used during load benchmarking) will be stored
		dbsDirTemp = tempDirPath + "dbs\\";
		File dbsDir = new File(ServerProperties.dbsDirTemp);
		if (!dbsDir.mkdir()) {
			Utilities.deleteDirectory(ServerProperties.dbsDirTemp);
			dbsDir.mkdir();
		}
		
		// === Directory where the DEX temporary graphs will be stored
		dbDirDexTemp = tempDirPath + "dbs\\dex\\";
		File dbDirDex = new File(ServerProperties.dbDirDexTemp);
		if (!dbDirDex.mkdir()) {
			Utilities.deleteDirectory(ServerProperties.dbDirDexTemp);
			dbDirDex.mkdir();
		}
		
		// === Directory where the Neo4j temporary graphs will be stored
		dbDirNeo4jTemp = tempDirPath + "dbs\\neo4j\\";
		File dbDirNeo4j = new File(ServerProperties.dbDirNeo4jTemp);
		if (!dbDirNeo4j.mkdir()) {
			Utilities.deleteDirectory(ServerProperties.dbDirNeo4jTemp);
			dbDirNeo4j.mkdir();
		}
		
		// === Directory where the Titan temporary graphs will be stored
		dbDirTitanTemp = tempDirPath + "dbs\\titan\\";
		File dbDirTitan = new File(ServerProperties.dbDirTitanTemp);
		if (!dbDirTitan.mkdir()) {
			Utilities.deleteDirectory(ServerProperties.dbDirTitanTemp);
			dbDirTitan.mkdir();
		}
		
		// === Directory where the Orient temporary graphs will be stored
		dbDirOrientTemp = tempDirPath + "dbs\\orient\\";
		File dbDirOrient = new File(ServerProperties.dbDirOrientTemp);
		if (!dbDirOrient.mkdir()) {
			Utilities.deleteDirectory(ServerProperties.dbDirOrientTemp);
			dbDirOrient.mkdir();
		}
		
		// === Rexster server directory
		rexsterDirPath = jarFile.getParentFile().getPath() + "\\..\\rexster\\bin\\";
		
		// === Directory where the working graphs will be stored
		dbsDirWork = rexsterDirPath + "..\\data\\";
		dbsDir = new File(ServerProperties.dbsDirWork);
		if (!dbsDir.mkdir()) {
			Utilities.deleteDirectory(ServerProperties.dbsDirWork);
			dbsDir.mkdir();
		}
		
		// === Directory where the working DEX graph will be stored
		dbDirDexWork = dbsDirWork + "dex\\";
		dbDirDex = new File(ServerProperties.dbDirDexWork);
		if (!dbDirDex.mkdir()) {
			Utilities.deleteDirectory(ServerProperties.dbDirDexWork);
			dbDirDex.mkdir();
		}

		// === Directory where the working Neo4j graph will be stored
		dbDirNeo4jWork = dbsDirWork + "neo4j\\";
		dbDirNeo4j = new File(ServerProperties.dbDirNeo4jWork);
		if (!dbDirNeo4j.mkdir()) {
			Utilities.deleteDirectory(ServerProperties.dbDirNeo4jWork);
			dbDirNeo4j.mkdir();
		}
		
		// === Directory where the working Titan graph will be stored
		dbDirTitanWork = dbsDirWork + "titan\\";
		dbDirTitan = new File(ServerProperties.dbDirTitanWork);
		if (!dbDirTitan.mkdir()) {
			Utilities.deleteDirectory(ServerProperties.dbDirTitanWork);
			dbDirTitan.mkdir();
		}
		
		// === Directory where the working Orient graph will be stored
		dbDirOrientWork = dbsDirWork + "orient\\";
		dbDirOrient = new File(ServerProperties.dbDirOrientWork);
		if (!dbDirOrient.mkdir()) {
			Utilities.deleteDirectory(ServerProperties.dbDirOrientWork);
			dbDirOrient.mkdir();
		}
	}
}