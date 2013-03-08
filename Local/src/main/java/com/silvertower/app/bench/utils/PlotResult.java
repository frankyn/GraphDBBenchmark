package com.silvertower.app.bench.utils;

import com.silvertower.app.bench.akka.messages.Messages.TimeResult;

public class PlotResult {
	private TimeResult t;
	private int x;
	public PlotResult(TimeResult t, int x) {
		this.t = t;
		this.x = x;
	}
	
	public TimeResult getTime() {
		return t;
	}
	
	public int getX() {
		return x;
	}
}
