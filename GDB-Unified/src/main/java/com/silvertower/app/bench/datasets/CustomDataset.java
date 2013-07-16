package com.silvertower.app.bench.datasets;

import java.io.File;
import java.io.IOException;

import com.silvertower.app.bench.main.ServerProperties;

public class CustomDataset extends Dataset {
	private static final long serialVersionUID = 4032580734430757608L;
	private String edgeListFP;
	public CustomDataset(String edgeListFP) {
		super("Custom");
	}

	public File generate() {
		String pyScriptPath = ServerProperties.pythonDir + "custom-graph-creator.py";
		String datasetFP = ServerProperties.datasetsDir + "custom-dataset" + ".graphml";
		File f = new File(datasetFP);
		if (!f.exists()) {
			Runtime r = Runtime.getRuntime();
			try {
				String command = "python" + " " 
						+ pyScriptPath + " "
						+ edgeListFP + " " 
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
}
