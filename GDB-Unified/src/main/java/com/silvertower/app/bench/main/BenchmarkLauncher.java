package com.silvertower.app.bench.main;

import java.util.Arrays;

import com.silvertower.app.bench.akka.BenchRunner;
import com.silvertower.app.bench.akka.MasterClient;
import com.silvertower.app.bench.akka.Server;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Deploy;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.remote.RemoteScope;

public class BenchmarkLauncher {
	private static ActorSystem actorsSystem = ActorSystem.create("LocalNodeSystem", 
			ConfigFactory.load().getConfig("LocalSys"));
	
	public static void startActors(final String[] actorsInfos) {
		final String serverAdd = actorsInfos[0];
		int serverPort = Integer.parseInt(actorsInfos[1]);
		String masterClientAdd = actorsInfos[2];
		int masterClientPort = Integer.parseInt(actorsInfos[3]);
		
		Address mcAddr = new Address("akka", "MCNode", masterClientAdd, masterClientPort);
		Props mcProps = new Props(new UntypedActorFactory() {
			 public UntypedActor create() {
				 return new MasterClient(Arrays.copyOfRange(actorsInfos, 4, actorsInfos.length));
			 }
		});
		final ActorRef masterClient = actorsSystem.actorOf(mcProps.withDeploy(new Deploy(new RemoteScope(mcAddr))));
		
		Address servAddr = new Address("akka", "ServerNode", serverAdd, serverPort);
		Props servProps = new Props(Server.class).withDeploy(new Deploy(new RemoteScope(servAddr)));
		final ActorRef server = actorsSystem.actorOf(servProps);
		
		final Benchmark b = new Benchmark();
		ActorRef listener = actorsSystem.actorOf(new Props(new UntypedActorFactory() {
			 public UntypedActor create() {
				 return new BenchRunner(masterClient, server, b, serverAdd);
			 }
		}), "ResultListener");
	}
	
	public static void main (String[] args) {
		if (args.length < 6) {
			usage();
			System.exit(-1);
		}
		for (int i = 0; i < args.length; i++) {
			if (i % 2 == 0) {
				if (!checkIp(args[i])) {
					System.out.println("Only IPv4 address allowed!");
					usage();
					System.exit(-1);
				}
			}
			else {
				if (!checkPort(args[i])) {
					usage();
					System.exit(-1);
				}
			}
		}
		initiateBenchmark();
		startActors(args);
	}
	
	private static void usage() {
		String usage = 
				"Usage: java -jar bench.jar serverAdd serverPort masterClientAdd masterClientPort " +
				"slaveClient1Add slaveClient1Port ... slaveClientXAdd slaveClientXPort";
		System.out.println(usage);
	}
	
	public static void initiateBenchmark() {
		BenchRunnerProperties.initializeProperties();
	}
	
	private static boolean checkIp (String ip) {
		String[] parts = ip.split("\\.");
		if (parts.length != 4) return false;
		for (String partS: parts) {
			int part = 0;
			try {
				part = Integer.parseInt(partS);
			} catch (NumberFormatException e) {
				return false;
			}
			if (!(part >= 0 && part <= 255)) return false;
		}
		return true;
    }
	
	private static boolean checkPort (String port) {
		int portN = 0;
		try {
			portN = Integer.parseInt(port);
		} catch (NumberFormatException e) {
			return false;
		}
		if (!(portN >= 0 && portN <= 65535)) return false;
		else return true;
	}
}
