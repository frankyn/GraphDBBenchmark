package com.silvertower.app.bench.utils;

import com.silvertower.app.bench.akka.Messages.TimeResult;

public class Result {
	private TimeResult t;
	private int x;
	public Result(TimeResult t, int x) {
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
