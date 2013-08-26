package com.silvertower.app.bench.resultsprocessing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.silvertower.app.bench.akka.Messages.AggregateResult;
import com.silvertower.app.bench.akka.Messages.LoadResults;
import com.silvertower.app.bench.akka.Messages.TimeResult;
import com.silvertower.app.bench.main.BenchRunnerProperties;


public class Logger {
	private BufferedWriter logBuffer;
	public Logger() {
		try {
			logBuffer = new BufferedWriter(new FileWriter(BenchRunnerProperties.logDir + "log.txt", true));
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
	
	public void logResult(AggregateResult r) {
		writeAndFlush(r.toString());
	}
	
	public void logResult(LoadResults r) {
		writeAndFlush(r.toString());
	}
	
	public void logMessage(String message) {
		writeAndFlush(message);
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
