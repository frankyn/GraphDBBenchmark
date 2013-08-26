package com.silvertower.app.bench.main;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import com.silvertower.app.bench.akka.BenchmarkRunner;
import com.silvertower.app.bench.akka.MasterClient;
import com.silvertower.app.bench.akka.Server;
import com.silvertower.app.bench.akka.SlaveClient;
import com.silvertower.app.bench.gui.MainGui;
import com.silvertower.app.bench.utils.IP;
import com.silvertower.app.bench.utils.Port;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Deploy;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.remote.RemoteScope;

public class GDBMain {
	public static void startActors(final List<IP> ips, final List<Port> ports, final BenchmarkExecutor executor) {
		ActorSystem actorsSystem = ActorSystem.create("LocalNodeSystem", 
				ConfigFactory.load().getConfig("LocalSys"));
		final String serverAdd = ips.get(0).toString();
		int serverPort = ports.get(0).toInt();
		String masterClientAdd = ips.get(1).toString();
		int masterClientPort = ports.get(1).toInt();
		
		Address mcAddr = new Address("akka", "MCNode", masterClientAdd, masterClientPort);
		Props mcProps = new Props(new UntypedActorFactory() {
			 public UntypedActor create() {
				 IP[] slaveIps = ips.subList(2, ips.size()).toArray(new IP[ips.size()-2]);
				 Port[] slavePorts = ports.subList(2, ports.size()).toArray(new Port[ports.size()-2]);
				 return new MasterClient(slaveIps, slavePorts);
			 }
		});
		final ActorRef masterClient = actorsSystem.actorOf(mcProps.withDeploy(new Deploy(new RemoteScope(mcAddr))), "MasterClient");
		System.out.println(masterClient);
		
		Address servAddr = new Address("akka", "ServerNode", serverAdd, serverPort);
		Props servProps = new Props(Server.class).withDeploy(new Deploy(new RemoteScope(servAddr)));
		final ActorRef server = actorsSystem.actorOf(servProps, "Server");
		System.out.println(server);
		
		ActorRef listener = actorsSystem.actorOf(new Props(new UntypedActorFactory() {
			 public UntypedActor create() {
				 return new BenchmarkRunner(masterClient, server, executor, serverAdd);
			 }
		}), "ResultListener");
		
		System.out.println(listener);
	}
	
	public static void main (String[] args) {
		if (args.length == 0) {
			usage();
			System.exit(-1);
		}
		
		BenchRunnerProperties.initializeProperties();
		
		if (args[0].equals("commandline")) {
			List<IP> ips = new ArrayList<IP>();
			List<Port> ports = new ArrayList<Port>();
			for (int i = 1; i < args.length; i++) {
				if (i % 2 != 0) {
					try {
						ips.add(new IP(args[i]));
					} catch (NumberFormatException e) {
						System.err.println("Only IPv4 address allowed!");
						usage();
						System.exit(-1);
						return;
					}
				}
				else {
					try {
						ports.add(new Port(args[i]));
					} catch (NumberFormatException e) {
						System.err.println("Incorrect port number!");
						usage();
						System.exit(-1);
					}
				}
			}
			startActors(ips, ports, new BenchmarkCommandLineExecutor());
		}
		
		else if (args[0].equals("gui")) {
			BenchRunnerProperties.initializeProperties();
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						MainGui window = new MainGui();
						window.frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		
		else {
			usage();
			System.exit(-1);
		}
	}
	
	private static void usage() {
		String usage = 
				"Usage:\n" + 
				"java -jar bench.jar commandline serverAdd serverPort masterClientAdd masterClientPort " +
				"[slaveClientAdd...] [slaveClientPort...]\n" +
				"java -jar bench.jar gui";
		System.out.println(usage);
	}
}
