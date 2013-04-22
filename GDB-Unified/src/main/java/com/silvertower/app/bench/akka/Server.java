package com.silvertower.app.bench.akka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;

import com.silvertower.app.bench.datasets.Dataset;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.load.DBLoader;
import com.silvertower.app.bench.main.ServerProperties;
import com.silvertower.app.bench.utils.Utilities;
import com.silvertower.app.bench.akka.Messages.*;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Server extends UntypedActor {
	private DBInitializer currentInitializer;
	private enum State {DB_HOSTED, NO_DB_HOSTED};
	private State state;
	private final int rexsterWaitTimeLimit = 3600000;
	
	public Server() {
		this.state = State.NO_DB_HOSTED;
	}
	
	public void onReceive(Object message) throws Exception {
		System.out.println("Server:" + message);
		if (message instanceof DBInitializer) {
			currentInitializer = (DBInitializer) message;
		}
		
//		else if (message instanceof Dataset) {
//			Dataset d = (Dataset) message;
//			System.out.println(String.format("Received dataset: %s", d.getDatasetName()));
//			System.out.println(String.format("Generating dataset: %s", d.getDatasetName()));
//			File f = d.generate();
//			System.out.println(String.format("Generating dataset: %s completed", d.getDatasetName()));
//			getSender().tell(d, getSelf());
//		}
		
//		else if (message instanceof LoadBench) {
//			if (state != State.READY_TO_WORK) {
//				forwardError(getSender(), "Error: the database initializer is not set yet!");
//				return;
//			}
//			Dataset d = ((LoadBench) message).getDataset();
//			double wallTime;
//			if(((LoadBench) message).isBatchLoading()) {
//				wallTime = DBLoader.batchLoadingBenchmark(d, currentInitializer);
//			}
//			else {
//				wallTime = DBLoader.normalLoadingBenchmark(d, currentInitializer);
//			}
//			startRexsterServer();
//			TimeResult r = new TimeResult(wallTime);
//			getSender().tell(r, getSelf());
//		}
//		
//		else if (message instanceof LoadBench) {
//			if (state != State.READY_TO_WORK) {
//				forwardError(getSender(), "Error: the database initializer is not set yet!");
//				return;
//			}
//		}
		
		else if (message instanceof Load) {
			if (state == State.DB_HOSTED) {
				stopRexsterServer();
				deleteDBDirectory();
			}
			
			Load loadMessage = (Load) message;
			Dataset d = loadMessage.getDataset();
			String dbName = currentInitializer.getName();
			System.out.println(String.format("Received dataset: %s", d.getDatasetName()));
			System.out.println(String.format("Generating dataset: %s", d.getDatasetName()));
			File f = d.generate();
			System.out.println(String.format("Loading the DB %s with dataset: %s", dbName, d.getDatasetName()));
			GraphDescriptor gDesc = DBLoader.loadDB(f, d, currentInitializer);
			startRexsterServer();
			System.out.println("Loading finished, sending confirmation to the master node...");
			getSender().tell(gDesc, getSelf());
		}
		
		else if (message instanceof GetResult) {
			getSender().tell(new AggregateResult(DBLoader.loadTimes), getSelf());
		}
		
		else if (message instanceof StopCurrentDB) {
			if (currentInitializer == null) {
				forwardError(getSender(), "Error: the database initializer is not set yet!");
			}
			else {
				// Stop the rexster server and clean the working directory for this graph
				// db implementation
				stopRexsterServer();
				deleteDBDirectory();
			}
		}
		
		else {
			unhandled(message);
		}
	}
	
	private void deleteDBDirectory() {
		File dir = new File(currentInitializer.getWorkDirPath());
		Utilities.deleteDirectoryContent(dir);
	}
	
	private void startRexsterServer() {
		String command = "--start -c " + currentInitializer.getName() + ".xml";
		String endIndicator = "Starting listener thread for shutdown requests";
	    File f = new File(ServerProperties.rexsterDirPath + "..//rexsteroutput.txt");
	    if (f.exists()) f.delete();
	    try {
			f.createNewFile();
		} catch (IOException e) {}
		rexsterCommand(command, "rexster", f, endIndicator);
	}

	private void stopRexsterServer() {
		String command = "--stop";
		String endIndicator = "Rexster Server shutdown complete";
		File f = new File(ServerProperties.rexsterDirPath + "..//rexsterkilloutput.txt");
	    if (f.exists()) f.delete();
	    try {
			f.createNewFile();
		} catch (IOException e) {}
		rexsterCommand(command, "rexster-kill", f, endIndicator);
	}
	
	/* 
	 * This method was only tested on Linux and Windows
	 */
	private void rexsterCommand(String command, String scriptPrefixName, File outputFile, String endIndicator) {
		String os = System.getProperty("os.name").toLowerCase();
		boolean isWindows;
		if (os.indexOf("win") >= 0) isWindows = true;
		else isWindows = false;
		try {
			DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
			DefaultExecutor exec = new DefaultExecutor();
			exec.setWorkingDirectory(new File(ServerProperties.rexsterDirPath));
			CommandLine commandline;
			
			if (isWindows) {
				commandline = CommandLine.parse("cmd");
				commandline.addArgument("/c");
				commandline.addArgument("start");
				commandline.addArgument(scriptPrefixName + ".bat");
			}
			else {
				commandline = CommandLine.parse("sh");
				commandline.addArgument(scriptPrefixName + ".sh");
			}
			
			for (String arg: command.split("\\s+")) {
				commandline.addArgument(arg);
			}
		    
		    exec.execute(commandline, resultHandler);
		    
		    BufferedReader b = new BufferedReader(new FileReader(outputFile));
		    StringBuilder content = new StringBuilder();
		    String currentLine;
		    long beforeTs = System.currentTimeMillis();
		    System.out.println("Waiting for an indication from the rexster server ...");
		    while (true) {
		    	currentLine = b.readLine();
		    	if (b != null) {
			    	content.append(currentLine);
			    	if (content.toString().contains(endIndicator)) break;
			    	// We exit the loop if we have waited for one hour
			    	else if ((System.currentTimeMillis() - beforeTs) > rexsterWaitTimeLimit) {
			    		// Handle this
			    		System.err.println("Problem while starting the rexster daemon!");
			    		return;
			    	}
		    	}
		    	try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
		    }
		    b.close();
		}
	    catch (IOException e) {
        	e.printStackTrace();
        	System.err.println("Rexster command error");
        	System.exit(-1);
        }
	}
	
	private void forwardError(ActorRef dest, String errorMessage) {
		dest.tell(new Messages.Error(errorMessage), getSelf());
	}
	
	public void postStop() {
		System.exit(-1);
	}
}