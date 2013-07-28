package com.silvertower.app.bench.akka;

import com.silvertower.app.bench.main.MasterClientProperties;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.kernel.Bootable;

public class MasterClientBootstrap implements Bootable {
	final ActorSystem system = ActorSystem.create("MCNode", ConfigFactory.load().getConfig("MCSys"));
	
	public void shutdown() {
		system.shutdown();
	}

	public void startup() {
		MasterClientProperties.initializeProperties();
	}
}
