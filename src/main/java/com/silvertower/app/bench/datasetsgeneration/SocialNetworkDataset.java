package com.silvertower.app.bench.datasetsgeneration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.silvertower.app.bench.dbinitializers.GraphProperty;
import com.silvertower.app.bench.main.BenchmarkProperties;

public class SocialNetworkDataset implements Dataset {
	private int nVertices;
	private String datasetFP;
	private final String pyScriptPath = BenchmarkProperties.pythonDir + "social-graph-creator.py";
	private final String firstNamesFilePath = BenchmarkProperties.pythonDir + "first_names2011.txt";
	private final String lastNamesFilePath = BenchmarkProperties.pythonDir + "last_names1990.txt";
	private ArrayList<GraphProperty> properties;

	public SocialNetworkDataset(int nVertices) {
		this.nVertices = nVertices;
		this.datasetFP = BenchmarkProperties.datasetsDir + "social\\" + nVertices + ".graphml";
		new File(datasetFP);
		properties = generate();
	}
	
	public ArrayList<GraphProperty> generate() {
		Runtime r = Runtime.getRuntime();
		ArrayList<GraphProperty> properties = new ArrayList<GraphProperty>();
		try {
			String command = "python" + " " 
					+ pyScriptPath + " "
					+ nVertices + " " 
					+ firstNamesFilePath + " " 
					+ lastNamesFilePath + " "
					+ datasetFP;
			Process p = r.exec(command);
            p.waitFor();
            
            ArrayList<Object> firstNames = new ArrayList<Object>();
        	ArrayList<Object> lastNames = new ArrayList<Object>();
            fillFirstNamesList(firstNames);
            fillLastNamesList(lastNames);
            properties.add(new GraphProperty("Firstname", firstNames));
            properties.add(new GraphProperty("Lastname", lastNames));
        }
        catch (Exception e) {
        	e.printStackTrace();
        	System.err.println("Error while generating a social network dataset");
        	System.exit(-1);
        }
        return properties;
	}
	
	private void fillFirstNamesList(ArrayList<Object> firstNames) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(firstNamesFilePath));
		String current = null;
		while ((current = reader.readLine()) != null) {
			firstNames.add(current);
		}
	}
	
	private void fillLastNamesList(ArrayList<Object> lastNames) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(lastNamesFilePath));
		String current = null;
		while ((current = reader.readLine()) != null) {
			lastNames.add(current);
		}
	}
	
	public String getDatasetFP() {
		return datasetFP;
	}
	
	public int getNumberVertices() {
		return nVertices;
	}

	public ArrayList<GraphProperty> getProperties() {
		return properties;
	}
}
