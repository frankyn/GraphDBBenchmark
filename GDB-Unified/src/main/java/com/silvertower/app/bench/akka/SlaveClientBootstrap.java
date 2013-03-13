package com.silvertower.app.bench.akka;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.kernel.Bootable;

public class SlaveClientBootstrap implements Bootable {
	final ActorSystem system = ActorSystem.create("SCNode", ConfigFactory.load().getConfig("SCSys"));
	
	public void shutdown() {
		system.shutdown();
	}

	public void startup() {
	}
}
