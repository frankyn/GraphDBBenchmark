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

	public SocialNetworkDataset(int nVertices) {
		super(nVertices, "Social");
	}
	
	public void generate() {
		String pyScriptPath = ServerProperties.pythonDir + "social-graph-creator.py";
		String firstNamesFilePath = ServerProperties.pythonDir + "first_names2011.txt";
		String lastNamesFilePath = ServerProperties.pythonDir + "last_names1990.txt";
		datasetFP = ServerProperties.datasetsDir + "social" + nVertices;
		File f = new File(datasetFP);
		if (!f.exists()) {
			Runtime r = Runtime.getRuntime();
			try {
				String command = "python" + " " 
						+ pyScriptPath + " "
						+ nVertices + " " 
						+ firstNamesFilePath + " " 
						+ lastNamesFilePath + " "
						+ datasetFP;
				Process p = r.exec(command);
	            p.waitFor();
	            fillInfos(firstNamesFilePath, lastNamesFilePath);
	        }
	        catch (IOException | InterruptedException e) {
	        	e.printStackTrace();
	        	System.err.println("Error while generating the dataset: " + datasetName);
	        	System.exit(-1);
	        }
		}
		else fillInfos(firstNamesFilePath, lastNamesFilePath);
	}
	
	private void fillInfos(String firstNamesFilePath, String lastNamesFilePath) {
        ArrayList<Object> firstNames = new ArrayList<Object>();
    	ArrayList<Object> lastNames = new ArrayList<Object>();
	    fillFirstNamesList(firstNames, firstNamesFilePath);
	    fillLastNamesList(lastNames, lastNamesFilePath);
        properties.add(new GraphProperty("Firstname", firstNames));
        properties.add(new GraphProperty("Lastname", lastNames));
	}
	
	public boolean isDirected() {
		return true;
	}
	
	private void fillFirstNamesList(ArrayList<Object> firstNames, String firstNamesFilePath) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(firstNamesFilePath));
			String current = null;
			while ((current = reader.readLine()) != null) {
				firstNames.add(current);
			}
		} catch (IOException e) {
			e.printStackTrace();
        	System.err.println("Error while generating the dataset: " + datasetName);
        	System.exit(-1);
		}
	}
	
	private void fillLastNamesList(ArrayList<Object> lastNames, String lastNamesFilePath) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(lastNamesFilePath));
			String current = null;
			while ((current = reader.readLine()) != null) {
				lastNames.add(current);
			}
		} catch (IOException e) {
			e.printStackTrace();
        	System.err.println("Error while generating the dataset: " + datasetName);
        	System.exit(-1);
		}
	}
}
