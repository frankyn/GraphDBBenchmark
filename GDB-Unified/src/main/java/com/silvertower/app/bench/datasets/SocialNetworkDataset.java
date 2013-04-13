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
		String citiesFilePath = ServerProperties.pythonDir + "cities.txt";
		datasetFP = ServerProperties.datasetsDir + "social" + nVertices + ".graphml";
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
	            fillInfos(firstNamesFilePath, lastNamesFilePath, citiesFilePath);
	        }
	        catch (IOException | InterruptedException e) {
	        	e.printStackTrace();
	        	System.err.println("Error while generating the dataset: " + datasetName);
	        	System.exit(-1);
	        }
		}
		else fillInfos(firstNamesFilePath, lastNamesFilePath, citiesFilePath);
	}
	
	private void fillInfos(String firstNamesFP, String lastNamesFP, String citiesFP) {
        ArrayList<Object> firstNames = new ArrayList<Object>();
    	ArrayList<Object> lastNames = new ArrayList<Object>();
    	ArrayList<Object> citiesNames = new ArrayList<Object>();
    	fillPropertyList(firstNames, firstNamesFP);
    	fillPropertyList(lastNames, lastNamesFP);
    	fillPropertyList(citiesNames, citiesFP);
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
