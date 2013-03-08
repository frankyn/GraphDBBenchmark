package com.silvertower.app.bench.main;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;

import com.silvertower.app.bench.akka.CentralNode;
import com.silvertower.app.bench.akka.MasterClient;
import com.silvertower.app.bench.akka.Server;
import com.silvertower.app.bench.utils.Utilities;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;

public class BenchmarkLauncher {
	private static ActorSystem actorsSystem;
	private static ActorRef server;
	private static ActorRef masterclient;
	private static ActorRef listener;
	
	public static void startActors() {
		actorsSystem = ActorSystem.create("GDBBenchSystem");
		server = actorsSystem.actorOf(new Props(Server.class), "Server");
		masterclient = actorsSystem.actorOf(new Props(MasterClient.class), "MasterClient");
		listener = actorsSystem.actorOf(new Props(new UntypedActorFactory() {
			 public UntypedActor create() {
				 return new CentralNode(masterclient, server);
			 }
		}), "ResultListener");
	}
	
	public static void main (String[] args) {
		initiateBenchmark();
		startActors();
	}
	
	
	public static void initiateBenchmark() {
		PropertyConfigurator.configure("log4j.properties");
		/*File logDir = new File(BenchmarkProperties.logDir);
		if (!logDir.mkdir()) {
			Utilities.deleteDirectory(BenchmarkProperties.logDir);
			logDir.mkdir();
		}*/
		File dbsDir = new File(BenchmarkProperties.dbsDir);
		if (!dbsDir.mkdir()) {
			Utilities.deleteDirectory(BenchmarkProperties.dbsDir);
			dbsDir.mkdir();
		}
		/*File datasetsDir = new File(BenchmarkProperties.datasetsDir);
		if (!datasetsDir.mkdir()) {
			Utilities.deleteDirectory(BenchmarkProperties.datasetsDir);
			datasetsDir.mkdir();
		}*/
		/*File plotsDir = new File(BenchmarkProperties.plotsDir);
		if (!plotsDir.mkdir()) {
			Utilities.deleteDirectory(BenchmarkProperties.plotsDir);
			plotsDir.mkdir();
		}*/
	}
}
