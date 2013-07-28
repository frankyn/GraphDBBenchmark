package com.silvertower.app.bench.akka;

import com.silvertower.app.bench.main.ServerProperties;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.kernel.Bootable;

public class ServerBootstrap implements Bootable {
	final ActorSystem system = ActorSystem.create("ServerNode", ConfigFactory.load().getConfig("SSys"));
	
	public void shutdown() {
		system.shutdown();
	}

	public void startup() {
		ServerProperties.initializeProperies();
	}
}
