package com.silvertower.app.bench.akka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;

import com.silvertower.app.bench.datasets.Dataset;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.dbinitializers.GraphProperty;
import com.silvertower.app.bench.loading.CustomGraphMLReader;
import com.silvertower.app.bench.main.BenchmarkConfiguration;
import com.silvertower.app.bench.main.ServerProperties;
import com.silvertower.app.bench.utils.Utilities;
import com.silvertower.app.bench.workload.LoadWorkload;
import com.silvertower.app.bench.akka.Messages.*;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.util.wrappers.WrapperGraph;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Server extends UntypedActor {
	private DBInitializer currentInitializer;
	private enum State {DB_HOSTED, NO_DB_HOSTED};
	private State state;
	private final int rexsterWaitTimeLimit = 60000;
	private LoadResults currentLoadResults;
	private BenchmarkConfiguration config;
	public Server() {
		this.state = State.NO_DB_HOSTED;
	}
	
	public void onReceive(Object message) throws Exception {
		System.out.println("Server:" + message);
		
		if (message instanceof BenchmarkConfiguration) {
			this.config = (BenchmarkConfiguration) message;
		}
		
		else if (message instanceof DBInitializer) {
			currentInitializer = (DBInitializer) message;
		}
		
		else if (message instanceof LoadWorkload) {
			if (state == State.DB_HOSTED) {
				stopRexsterServer();
				deleteDBDirectory();
			}
			
			LoadWorkload workload = (LoadWorkload) message;
			Dataset d = workload.getDataset();
			int bufferSizeWanted = workload.getBufferSize();
			String dbName = currentInitializer.toString();
			System.out.println(String.format("Received dataset: %s", d.toString()));
			System.out.println(String.format("Generating dataset: %s", d.toString()));
			File f = d.generate();
			System.out.println(String.format("Loading the DB %s with dataset: %s", dbName, d.toString()));
			GraphDescriptor gDesc = loadDB(f, d, bufferSizeWanted, currentInitializer);
			startRexsterServer();
			System.out.println("Loading finished, sending confirmation to the master node...");
			getSender().tell(gDesc, getSelf());
			state = State.DB_HOSTED;
		}
		
		else if (message instanceof GetResult) {
			getSender().tell(currentLoadResults, getSelf());
		}
		
		else if (message instanceof StopCurrentDB) {
			if (state == State.DB_HOSTED) {
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
		String command = "--start -c " + currentInitializer.toString() + ".xml";
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
		    while ((System.currentTimeMillis() - beforeTs) < rexsterWaitTimeLimit) {
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
	
	public void postStop() {
		System.exit(-1);
	}
	
	private GraphDescriptor initializeGraphDescriptor(Graph g, List<Object> vIds, List<Object> eIds, 
			Dataset d, DBInitializer i) {
		return new GraphDescriptor(vIds, eIds, i.toString(), d);
	}
	
	public GraphDescriptor loadDB(File f, Dataset d, int bufferSizeWanted, DBInitializer initializer) {	
		// Create a batch loading graph
		Graph g = initializer.initialize(initializer.getWorkDirPath(), true);
		
		// Create vertices and edges indices
		createVerticesIndex(g, d);
//		createEdgesIndex(g, d);
		
		// Measure the loading time
		long before = System.nanoTime();
		loadGraphML(g, bufferSizeWanted, initializeIS(f));		
		
		// Close the previously opened batch graph and create a new standard graph that contains
		// exactly the same vertices.
		initializer.shutdownGraph(g);
		g = initializer.initialize(initializer.getWorkDirPath(), false);
		List<Object> vIds = scanVertices(g);
//		List<Object> eIds = scanEdges(g);
		initializer.shutdownGraph(g);
		return initializeGraphDescriptor(g, vIds, null, d, initializer);
	}
	
	private void createVerticesIndex(Graph g, Dataset d) {
		if (g.getFeatures().supportsVertexKeyIndex) { 
			Graph rawGraph;
			if (g instanceof WrapperGraph) rawGraph = ((WrapperGraph) g).getBaseGraph();
			else rawGraph = g;
			for (GraphProperty p: d.getVertexProperties()) {
				((KeyIndexableGraph) rawGraph).createKeyIndex(p.getFieldName(), Vertex.class);
			}
		}
	}
	
//	private void createEdgesIndex(Graph g, Dataset d) {
//		if (g.getFeatures().supportsEdgeKeyIndex) {
//			Graph rawGraph;
//			if (g instanceof WrapperGraph) rawGraph = ((WrapperGraph) g).getBaseGraph();
//			else rawGraph = g;
//			for (GraphProperty p: d.getEdgesProperties()) {
//				((KeyIndexableGraph) rawGraph).createKeyIndex(p.getFieldName(), Edge.class);
//			}
//		}
//	}
	
	private List<Object> scanVertices(Graph g) {
		// As OrientDB does not use the same id for RexsterGraph and the raw graph, we need to send
		// to the client the string representation of the vertices ids.
		boolean needStringRep = g instanceof OrientGraph;
		Iterator <Vertex> iter = g.getVertices().iterator();
		ArrayList<Object> ids = new ArrayList<Object>();
		int count = 0;
		while (iter.hasNext()) {
			Object id = iter.next().getId();
			if (needStringRep) ids.add(id.toString());
			else ids.add(id);
			count++;
			if (count == 50000) break; // We limit the scan to 50000 vertices
		}
		((TransactionalGraph) g).commit();
		return ids;
	}
	
//	private List<Object> scanEdges(Graph g) {
//		// As OrientDB does not use the same id for RexsterGraph and the raw graph, we need to send
//		// to the client the string representation of the vertices ids.
//		boolean needStringRep = g instanceof OrientGraph || g instanceof TitanGraph;
//		Iterator <Edge> iter = g.getEdges().iterator();
//		ArrayList<Object> ids = new ArrayList<Object>();
//		int count = 0;
//		while (iter.hasNext()) {
//			Object id = iter.next().getId();
//			if (needStringRep) ids.add(id.toString());
//			else ids.add(id);
//			count++;
//			if (count == 50000) break; // We limit the scan to 50000 vertices
//		}
//		((TransactionalGraph) g).commit();
//		return ids;
//	}
	
	public InputStream initializeIS(File datasetFile) {
		InputStream is = null;
		try {
			is = new FileInputStream(datasetFile);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return is;
	}
	
	public void loadGraphML(Graph g, int bufferSizeWanted, InputStream is) {
		try {
			CustomGraphMLReader reader = new CustomGraphMLReader(g);
			reader.inputGraph(is, bufferSizeWanted);
			currentLoadResults = reader.results;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error while filling a database with a dataset");
			System.exit(-1);
		}
	}
}