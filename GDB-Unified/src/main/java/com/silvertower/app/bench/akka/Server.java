package com.silvertower.app.bench.akka;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;

import java.io.ByteArrayOutputStream;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

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
		String command = "start -c " + currentInitializer.getName() + ".xml";
		String endIndicator = "Starting listener thread for shutdown requests";
		rexsterCommand(command, endIndicator);
	}

	private void stopRexsterServer() {
		String command = "stop";
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
			if (isWindows) 
				 command = "rexster.bat --" + command;
			else command = "rexster.sh --" + command;
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		    DefaultExecutor exec = new DefaultExecutor();
		    PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
		    CommandLine commandline2 = CommandLine.parse(command);
		    exec.setStreamHandler(streamHandler);
		    exec.setWorkingDirectory(new File(ServerProperties.rexsterDirPath));
		    exec.execute(commandline2);
		    
		    System.out.println(outputStream.toString());
		}
	    catch (IOException e) {
        	e.printStackTrace();
        	System.err.println("Rexster command error");
        	System.exit(-1);
        }
	}
}