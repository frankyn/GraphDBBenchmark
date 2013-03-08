package com.silvertower.app.bench.datasets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.silvertower.app.bench.dbinitializers.GraphProperty;
import com.silvertower.app.bench.main.BenchmarkProperties;

public class SocialNetworkDataset extends Dataset {
	private static final String pyScriptPath = BenchmarkProperties.pythonDir + "social-graph-creator.py";
	private static final String firstNamesFilePath = BenchmarkProperties.pythonDir + "first_names2011.txt";
	private static final String lastNamesFilePath = BenchmarkProperties.pythonDir + "last_names1990.txt";

	public SocialNetworkDataset(int nVertices) {
		super(nVertices, BenchmarkProperties.datasetsDir + "social" + nVertices, "Social");
	}
	
	public void generate() {
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
            fillInfos();
        }
        catch (IOException | InterruptedException e) {
        	e.printStackTrace();
        	System.err.println("Error while generating the dataset: " + datasetName);
        	System.exit(-1);
        }
	}
	
	public void fillInfos() {
        ArrayList<Object> firstNames = new ArrayList<Object>();
    	ArrayList<Object> lastNames = new ArrayList<Object>();
	    fillFirstNamesList(firstNames);
	    fillLastNamesList(lastNames);
        properties.add(new GraphProperty("Firstname", firstNames));
        properties.add(new GraphProperty("Lastname", lastNames));
	}
	
	public boolean isDirected() {
		return true;
	}
	
	private void fillFirstNamesList(ArrayList<Object> firstNames) {
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
	
	private void fillLastNamesList(ArrayList<Object> lastNames) {
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
