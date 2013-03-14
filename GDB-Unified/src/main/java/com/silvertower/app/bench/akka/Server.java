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
import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.load.DBLoader;
import com.silvertower.app.bench.main.ServerProperties;
import com.silvertower.app.bench.utils.Utilities;
import com.silvertower.app.bench.akka.Messages.*;

import akka.actor.UntypedActor;

public class Server extends UntypedActor {
	private DBInitializer currentInitializer;
	private enum State {WAITING_FOR_INFOS, READY_TO_WORK};
	private State state;
	
	public Server() {
		this.state = State.WAITING_FOR_INFOS;
	}
	public void onReceive(Object message) throws Exception {
		if (message instanceof DBInitializer) {
			state = State.READY_TO_WORK;
			currentInitializer = (DBInitializer) message;
		}
		
		else if (message instanceof Dataset) {
			Dataset d = (Dataset) message;
			d.generate();
			getSender().tell(d, getSelf());
		}
		
		else if (message instanceof LoadBench) {
			if (state != State.READY_TO_WORK) {
				getSender().tell(new Messages.Error("Error: the database initializer is not set yet!"), getSelf());
				return;
			}
			Dataset d = ((LoadBench) message).getDataset();
			double[] times;
			if(((LoadBench) message).isBatchLoading()) {
				times = DBLoader.batchLoadingBenchmark(d, currentInitializer);
			}
			else {
				times = DBLoader.normalLoadingBenchmark(d, currentInitializer);
			}
			TimeResult r = new TimeResult(times[0], times[1]);
			getSender().tell(r, getSelf());
		}
		
		else if (message instanceof Load) {
			if (state != State.READY_TO_WORK) {
				getSender().tell(new Messages.Error("Error: the database initializer is not set yet!"), getSelf());
				return;
			}
			GraphDescriptor gDesc = DBLoader.loadDB(((Load) message).getDataset(), currentInitializer);
			getSender().tell(gDesc, getSelf());
			startRexsterServer();
		}
		
		else if (message instanceof StopCurrentDB) {
			if (currentInitializer == null) {
				getSender().tell(new Messages.Error("Error: the database initializer is not set yet!"), getSelf());
			}
			else {
				// Stop the rexster server and clean the working directory for this graph
				// db implementation
				stopRexsterServer();
				File dir = new File(currentInitializer.getWorkDirPath());
				Utilities.deleteDirectoryContent(dir);
			}
		}
		
		else {
			unhandled(message);
		}
	}
	
	private void startRexsterServer() {
		String command = "--start -c " + currentInitializer.getName() + ".xml";
		String endIndicator = "Starting listener thread for shutdown requests";
		rexsterCommand(command, endIndicator);
	}

	private void stopRexsterServer() {
		String command = "--stop";
		String endIndicator = "Rexster Server shutdown complete";
		rexsterCommand(command, endIndicator);
	}
	
	private void rexsterCommand(String command, String endIndicator) {
		Runtime r = Runtime.getRuntime();
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
				commandline.addArgument("rexster.bat");
			}
			else {
				commandline = CommandLine.parse("bash");
				commandline.addArgument("rexster.sh");
			}
			
			for (String arg: command.split("\\s+")) {
				commandline.addArgument(arg);
			}
		    
		    exec.execute(commandline, resultHandler);
		    
		    File f = new File(ServerProperties.rexsterDirPath + "..\\rexsteroutput.txt");
		    BufferedReader b = new BufferedReader(new FileReader(f));
		    StringBuilder content = new StringBuilder();
		    String currentLine;
		    long beforeTs = System.currentTimeMillis();
		    while ((currentLine = b.readLine()) != null) {
		    	content.append(currentLine);
		    	if (content.toString().contains(endIndicator)) break;
		    	else if ((System.currentTimeMillis() - beforeTs) > 100000) {
		    		System.out.println("PROB");
		    		// Handle this
		    		System.exit(-1);
		    	}
		    }
		}
	    catch (IOException e) {
        	e.printStackTrace();
        	System.err.println("Rexster command error");
        	System.exit(-1);
        }
	}
}