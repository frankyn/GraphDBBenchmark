package com.silvertower.app.bench.akka;

import akka.actor.UntypedActor;
import com.silvertower.app.bench.akka.Messages.*;
import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.workload.Workload;

public class MasterClient extends UntypedActor {
	private GraphDescriptor currentGDesc;
	
	public void onReceive(Object message) throws Exception {
		if (message instanceof LoadingEnded) {
			currentGDesc = ((LoadingEnded) message).getGraphDesc();
		}
		
		else if (message instanceof Work) {
			Workload w = ((Work) message).getWork();
			if (w.isMT()) {
				// do the work itself
			}
			else {
				// create SlaveClients that will do the work
			}
		}
		
		else if (message instanceof GetResult) {
			// aggregate result and send to the result listener
		}
		
		else {
			unhandled(message);
		}
	}

}
