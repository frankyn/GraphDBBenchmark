package com.silvertower.app.bench.utils;

import com.silvertower.app.bench.akka.Messages.TimeResult;

public class PlotResult {
	private int x;
	private TimeResult t;
	public PlotResult(int x, TimeResult t) {
		this.x = x;
		this.t = t;
	}
	
	public TimeResult getTime() {
		return t;
	}
	
	public int getX() {
		return x;
	}
}
