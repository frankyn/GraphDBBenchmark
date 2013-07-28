package com.silvertower.app.bench.main;

import com.silvertower.app.bench.akka.BenchmarkRunner;
import com.silvertower.app.bench.utils.Logger;

public interface BenchmarkExecutor {
	public void startBenchmark(BenchmarkRunner b, Logger log);
}
