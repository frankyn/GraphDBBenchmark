package com.silvertower.app.bench.main;


public class BenchmarkProperties {
	public static final String currentDir = System.getProperty("user.dir");
	public static final String logDir = currentDir + "\\log\\";
	public static final String dbsDir = currentDir + "\\dbs\\";
	public static final String datasetsDir = currentDir + "\\datasets\\";
	public static final String plotsDir = currentDir + "\\plots\\";
	public static final String dbDirDex = currentDir + "\\dbs\\dex\\";
	public static final String dbDirNeo4j = currentDir + "\\dbs\\neo4j\\";
	public static final String dbDirTitan = currentDir + "\\dbs\\titan\\";
	public static final String dbDirOrient = currentDir + "\\dbs\\orient\\";
	public static final String pythonDir = currentDir + "\\src\\python\\";
	public static final int meanTimes = 10;
	public static final double threshold = 5;
	public static boolean cpuTimeRequired = true;
}
