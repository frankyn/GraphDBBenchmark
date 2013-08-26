package com.silvertower.app.bench.main;

import java.io.Serializable;

public class BenchmarkConfiguration implements Serializable {
	private static final long serialVersionUID = 1975820055723903615L;
	public int traversalRepeatTimes = 100;
	public int intensiveRepeatTimes = 100;
	public int maxOpsAtATimeRexPro = 1;
	public long workloadExTime = 120000000000L;
	public long maxLoadExecutionTimeInNS = 60000000000L;
	public int maxNVerticesMultiLoad = 5000;
}
