package com.silvertower.app.bench.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.silvertower.app.bench.akka.Messages.TimeResult;
import com.silvertower.app.bench.main.CentralNodeProperties;


public class Logger {
	private BufferedWriter logBuffer;
	public Logger(String benchmarkName) {
		try {
			logBuffer = new BufferedWriter(new FileWriter(CentralNodeProperties.logDir + benchmarkName + ".txt", true));
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Unable to initialize the log buffer");
			System.exit(-1);
		}
	}
	
	public void logDB(String dbName) {
		writeAndFlush(String.format("DB: %s", dbName));
	}
	
	public void logOp(String op) {
		writeAndFlush(String.format("Operation: %s:", op));
	}
	
	public void logResult(TimeResult r) {
		writeAndFlush(r.toString());
	}
	
	private void writeAndFlush(String message) {
		try {
			logBuffer.write(message + "\n");
			logBuffer.flush();
		} catch (IOException e) {
			System.err.println("Unable to flush to the log buffer");
		}
	}
	
	public void closeLogger() {
		try {
			logBuffer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
