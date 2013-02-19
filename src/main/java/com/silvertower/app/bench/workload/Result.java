package com.silvertower.app.bench.workload;

public class Result {
	public Number first;
    public Number [] associated;
    public Result(Number first, Number... associated){
    	this.first = first;
        this.associated = associated;
    }
}