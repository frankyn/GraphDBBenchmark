package com.silvertower.app.bench.workload;

import java.lang.management.ManagementFactory;

import com.tinkerpop.blueprints.Graph;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.silvertower.app.bench.dbinitializers.*;
import com.silvertower.app.bench.main.Globals;

public class ReadIDsSlaveThread extends SlaveThread {
	
	public ReadIDsSlaveThread(Graph g, GraphDescriptor gDesc, Type t, long executionTime) {
		super(g, gDesc, t, executionTime);
	}

	protected void operation() {
		Object rId = gDesc.getRandomVertexId();
		g.getVertex(rId);
	}
}
