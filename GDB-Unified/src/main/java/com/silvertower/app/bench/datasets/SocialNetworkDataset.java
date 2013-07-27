package com.silvertower.app.bench.datasets;

import java.io.File;
import java.io.IOException;

import com.silvertower.app.bench.annotations.Custom;
import com.silvertower.app.bench.main.ServerProperties;

@Custom
public class SocialNetworkDataset extends Dataset {
	private static final long serialVersionUID = 7857644860840840667L;

	public SocialNetworkDataset(int nVertices) {
		super(nVertices, "Social");
	}
	
	public File generate() {
		String pyScriptPath = ServerProperties.pythonDir + "social-graph-creator.py";
		String datasetFP = ServerProperties.datasetsDir + "social" + nVertices + ".graphml";
		File f = new File(datasetFP);
		if (!f.exists()) {
			Runtime r = Runtime.getRuntime();
			try {
				String command = "python" + " " 
						+ pyScriptPath + " "
						+ nVertices + " " 
						+ datasetFP;
				System.out.println(command);
				Process p = r.exec(command);
	            p.waitFor();
	            fillProperties();
	        }
	        catch (IOException | InterruptedException e) {
	        	e.printStackTrace();
	        	System.err.println("Error while generating the dataset: " + datasetName);
	        	System.exit(-1);
	        }
		}
		else fillProperties();
		
		return f;
	}
	
	public boolean isDirected() {
		return true;
	}
}
