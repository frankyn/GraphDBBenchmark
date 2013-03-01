package com.silvertower.app.bench.akka.masterclient;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.kernel.Bootable;

public class MasterClientBootstrap implements Bootable {
	final ActorSystem system = ActorSystem.create("RemoteMasterClientSys", ConfigFactory.load().getConfig("RemoteMasterClientSys"));
	
	public void shutdown() {
		system.shutdown();
	}

	public void startup() {
		system.actorOf(new Props(MasterClient.class), "masterClient");
	}
}
