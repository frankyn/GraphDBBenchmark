package com.silvertower.app.bench.akka;

import akka.actor.UntypedActor;
import com.silvertower.app.bench.akka.Messages.*;

public class SlaveClient extends UntypedActor {

	public void onReceive(Object message) throws Exception {
		if (message instanceof Work) {
			// do the work, return (wall time, cpu time)
			// this task must be asynchronous, so must send a result message
		}
		
		else if (message instanceof GetResult) {
			// send result
		}
		
		else {
			unhandled(message);
		}
	}

}
