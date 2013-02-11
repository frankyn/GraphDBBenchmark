package com.silvertower.app.bench.workload;

import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.utils.Logger;

public interface Workload {
	public void work(GraphDescriptor gDesc, Logger log);
}
