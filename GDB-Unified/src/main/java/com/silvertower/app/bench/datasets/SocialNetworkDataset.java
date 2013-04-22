package com.silvertower.app.bench.datasets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.silvertower.app.bench.dbinitializers.GraphProperty;
import com.silvertower.app.bench.main.ServerProperties;

public class SocialNetworkDataset extends Dataset {
	private static final long serialVersionUID = 7857644860840840667L;
	private String firstNamesFilePath;
	private String lastNamesFilePath;
	private String citiesFilePath;

	public SocialNetworkDataset(int nVertices) {
		super(nVertices, "Social");
	}
	
	public File generate() {
		this.firstNamesFilePath = ServerProperties.pythonDir + "first_names2011.txt";
		this.lastNamesFilePath = ServerProperties.pythonDir + "last_names1990.txt";
		this.citiesFilePath = ServerProperties.pythonDir + "cities.txt";
		String pyScriptPath = ServerProperties.pythonDir + "social-graph-creator.py";
		String datasetFP = ServerProperties.datasetsDir + "social" + nVertices + ".graphml";
		File f = new File(datasetFP);
		if (!f.exists()) {
			Runtime r = Runtime.getRuntime();
			try {
				String command = "python" + " " 
						+ pyScriptPath + " "
						+ nVertices + " " 
						+ firstNamesFilePath + " " 
						+ lastNamesFilePath + " "
						+ citiesFilePath + " "
						+ datasetFP;
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
	
	public void fillProperties() {
        ArrayList<Object> firstNames = new ArrayList<Object>();
    	ArrayList<Object> lastNames = new ArrayList<Object>();
    	ArrayList<Object> citiesNames = new ArrayList<Object>();
    	fillPropertyList(firstNames, firstNamesFilePath);
    	fillPropertyList(lastNames, lastNamesFilePath);
    	fillPropertyList(citiesNames, citiesFilePath);
        vertexProperties.add(new GraphProperty("Firstname", firstNames));
        vertexProperties.add(new GraphProperty("Lastname", lastNames));
        edgesProperties.add(new GraphProperty("In city", citiesNames));
	}
	
	public boolean isDirected() {
		return true;
	}
	
	private void fillPropertyList(ArrayList<Object> list, String fp) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fp));
			String current = null;
			while ((current = reader.readLine()) != null) {
				list.add(current);
			}
		} catch (IOException e) {
			e.printStackTrace();
        	System.err.println("Error while generating the dataset: " + datasetName);
        	System.exit(-1);
		}
	}
}
