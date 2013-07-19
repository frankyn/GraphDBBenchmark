package com.silvertower.app.bench.akka;


import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import com.silvertower.app.bench.workload.IntensiveWorkload;
import com.tinkerpop.rexster.client.RexProException;
import com.tinkerpop.rexster.client.RexsterClient;

public class SlaveThread extends Thread {
	private IntensiveWorkload w;
	private GraphDescriptor gDesc;
	private int id;
	private int maxOpCount;
	private CountDownLatch startLatch;
	private CountDownLatch stopLatch;
	private int rexProNOpsAtATime;
	public SlaveThread(GraphDescriptor gDesc, int id, IntensiveWorkload w, int maxOpCount, 
			CountDownLatch startLatch, CountDownLatch stopLatch, int rexProNOpsAtATime) {
		this.w = w;
		this.gDesc = gDesc;
		this.id = id;
		this.maxOpCount = maxOpCount;
		this.startLatch = startLatch;
		this.stopLatch = stopLatch;
		this.rexProNOpsAtATime = rexProNOpsAtATime;
	}
	
	public void run() {
		try {
			startLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Rexpro communication mode
		if (rexProNOpsAtATime > 0) {
			int opRemaining = maxOpCount;
			while (opRemaining > 0) {
				int opToRealize = opRemaining > rexProNOpsAtATime ? rexProNOpsAtATime : opRemaining;
				String request = w.generateRequest(gDesc, id, opToRealize);
				try {
					((RexsterClient) gDesc.getRexsterClient()).execute(request);
					opRemaining -= opToRealize;
				} catch (RexProException | IOException e) {
					System.err.println("Error while executing the request: " + request);
				}
			}
		}
		
		// REST communication mode
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
