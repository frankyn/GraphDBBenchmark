package com.silvertower.app.bench.akka;


import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.kernel.Bootable;

public class ServerBootstrap implements Bootable {
	final ActorSystem system = ActorSystem.create("RemoteServerSys", ConfigFactory.load().getConfig("RemoteSys"));
	
	public void shutdown() {
		system.shutdown();
	}

	public void startup() {
		system.actorOf(new Props(Server.class), "server");
	}

}
