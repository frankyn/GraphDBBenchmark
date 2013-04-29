package com.silvertower.app.bench.akka;



import java.util.concurrent.CountDownLatch;

import com.silvertower.app.bench.workload.IntensiveWorkload;
import com.tinkerpop.blueprints.impls.rexster.RexsterGraph;

public class SlaveThread extends Thread {
	private IntensiveWorkload w;
	private GraphDescriptor gDesc;
	private int id;
	private int maxOpCount;
	private CountDownLatch startLatch;
	private CountDownLatch stopLatch;
	private boolean isBatchMode;
	public SlaveThread(GraphDescriptor gDesc, int id, IntensiveWorkload w, int maxOpCount, 
			CountDownLatch startLatch, CountDownLatch stopLatch, boolean isBatchMode) {
		this.w = w;
		this.gDesc = gDesc;
		this.id = id;
		this.maxOpCount = maxOpCount;
		this.startLatch = startLatch;
		this.stopLatch = stopLatch;
		this.isBatchMode = isBatchMode;
	}
	
	public void run() {
		try {
			startLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (isBatchMode) {
			int opRemaining = maxOpCount;
			while (opRemaining > 0) {
				int opToRealize = opRemaining > 100 ? 100 : opRemaining;
				String request = w.generateRequest(gDesc, id, opToRealize);
				((RexsterGraph) gDesc.getGraph()).execute(request);
				opRemaining -= opToRealize;
			}
		}
		else {
			int opCount = 0;
			while (opCount < maxOpCount) {
				w.operation(gDesc, id);
				opCount ++;
			}
		}
		stopLatch.countDown();
	}
}
