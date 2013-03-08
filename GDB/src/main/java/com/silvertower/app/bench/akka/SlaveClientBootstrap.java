package com.silvertower.app.bench.akka;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.kernel.Bootable;

public class SlaveClientBootstrap implements Bootable {
	final ActorSystem system;
	
	public SlaveClientBootstrap(String sysName) {
		system = ActorSystem.create(sysName, ConfigFactory.load().getConfig(sysName));
	}
	
	public void shutdown() {
		system.shutdown();
	}

	public void startup() {
		system.actorOf(new Props(SlaveClient.class), "slaveCLient");
	}
}
