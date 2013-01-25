package com.silvertower.app.bench.main;

import com.silvertower.app.bench.datasetsgeneration.DatasetsGenerator;
import com.silvertower.app.bench.datasetsgeneration.SocialNetworkDatasetsGenerator;
import com.silvertower.app.bench.load.SocialNetworkDBLoader;
import com.tinkerpop.blueprints.Graph;

public class BenchLauncher {	
	private static void socialNetworkBenchmark() {
		DatasetsGenerator sg = new SocialNetworkDatasetsGenerator(10000, 100000, 2);
		sg.generate();
		SocialNetworkDBLoader sl = new SocialNetworkDBLoader();
		Graph [] graphs = sl.load(sg);
	}
	
	public static void main(String [] args) {		
		socialNetworkBenchmark();
	}
}
