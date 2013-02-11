package com.silvertower.app.bench.main;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;

import com.silvertower.app.bench.datasetsgeneration.Dataset;
import com.silvertower.app.bench.datasetsgeneration.SocialNetworkDataset;
import com.silvertower.app.bench.dbinitializers.*;
import com.silvertower.app.bench.utils.*;
import com.silvertower.app.bench.workload.*;

public class BenchLauncher {	
	private static void runBenchmarks() {
		initiateBenchmark();
		List<DBInitializer> initializers = new ArrayList<DBInitializer>();
		//initializers.add(new Neo4jWrapper());
		initializers.add(new TitanWrapper(false, null));
		initializers.add(new OrientWrapper(false));
		socialBenchmark(initializers);
	}

	public static void socialBenchmark(List<DBInitializer> initializers) {
		/*Dataset d5000 = new SocialNetworkDataset(5000);
		Dataset d10000 = new SocialNetworkDataset(10000);
		Dataset d20000 = new SocialNetworkDataset(20000);
		Dataset d30000 = new SocialNetworkDataset(30000);
		Dataset d40000 = new SocialNetworkDataset(40000);
		Dataset d50000 = new SocialNetworkDataset(50000);*/
		Dataset d100000 = new SocialNetworkDataset(100000);
		
		LoadWorkload lw = new LoadWorkload();
		DijkstraWorkload dw = new DijkstraWorkload();
		VerticesExplorationWorkload vew = new VerticesExplorationWorkload();
		EdgesExplorationWorkload eew = new EdgesExplorationWorkload();
		ReadIntensiveWorkload riw = new ReadIntensiveWorkload();
		UpdateIntensiveWorkload uiw = new UpdateIntensiveWorkload();
		
		for (DBInitializer initializer: initializers) {
			Logger log = new Logger(initializer.getName(), "Social benchmark");
			ArrayList<Dataset> socialDatasets = new ArrayList<Dataset>();
			//socialDatasets.add(d5000);
			
			//GraphDescriptor gDesc = lw.work(socialDatasets, initializer.getPath(), initializer, log);
			//dw.work(gDesc, log);
			
			/*socialDatasets.remove(d5000);
			socialDatasets.add(d10000);
			socialDatasets.add(d20000);
			socialDatasets.add(d30000);
			socialDatasets.add(d40000);
			socialDatasets.add(d50000);*/
			socialDatasets.add(d100000);
			
			GraphDescriptor gDesc = lw.work(socialDatasets, initializer.getPath(), initializer, log);
			
			/*vew.work(gDesc, log);
			eew.work(gDesc, log);
			riw.work(gDesc, log);*/
			uiw.work(gDesc, log);
			
			log.closeLogger();
		}
	}
	
	public static void initiateBenchmark() {
		PropertyConfigurator.configure("log4j.properties");
		File logDir = new File(BenchmarkProperties.logDir);
		if (!logDir.mkdir()) {
			Utilities.deleteDirectory(BenchmarkProperties.logDir);
			logDir.mkdir();
		}
		File dbsDir = new File(BenchmarkProperties.dbsDir);
		if (!dbsDir.mkdir()) {
			Utilities.deleteDirectory(BenchmarkProperties.dbsDir);
			dbsDir.mkdir();
		}
		File datasetsDir = new File(BenchmarkProperties.datasetsDir);
		if (!datasetsDir.mkdir()) {
			Utilities.deleteDirectory(BenchmarkProperties.datasetsDir);
			datasetsDir.mkdir();
		}
		File plotsDir = new File(BenchmarkProperties.plotsDir);
		if (!plotsDir.mkdir()) {
			Utilities.deleteDirectory(BenchmarkProperties.plotsDir);
			plotsDir.mkdir();
		}
	}
	
	public static void main(String [] args) {		
		runBenchmarks();
	}
}
