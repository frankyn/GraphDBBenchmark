package com.silvertower.app.bench.datasetsgeneration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.silvertower.app.bench.main.BenchmarkProperties;

public class SocialNetworkDatasetGenerator implements DatasetGenerator {
	private int nVertices;
	private String datasetFP;
	private final String pyScriptPath = BenchmarkProperties.pythonDir + "social-graph-creator.py";
	private final String firstNamesFilePath = BenchmarkProperties.pythonDir + "first_names2011.txt";
	private final String lastNamesFilePath = BenchmarkProperties.pythonDir + "last_names1990.txt";
	private static ArrayList<String> firstNames;
	private static ArrayList<String> lastNames;

	public SocialNetworkDatasetGenerator(int nVertices) {
		this.nVertices = nVertices;
		this.datasetFP = BenchmarkProperties.datasetsDir + "social\\" + nVertices + ".graphml";
		new File(datasetFP);
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
            replaceBadIds(datasetFP);
            fillFirstNamesList();
            fillLastNamesList();
        }
        catch (Exception e) {
        	e.printStackTrace();
        	System.err.println("Error while generating a social network dataset");
        }
	}

	private static void replaceBadIds(String fPath) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fPath));
			PrintWriter writer = new PrintWriter("temp");  
		    String candidate;  
		    String matchRegex = "n([0-9]+)";  
		    String replacement = "1$1";  
		    while ((candidate = reader.readLine()) != null){  
		      candidate = candidate.replaceAll(matchRegex, replacement);  
		      writer.println(candidate);  
		    }  
		    reader.close();  
		    writer.close();  
		    
		    File oldF = new File(fPath);
		    File newF = new File("temp");
		    oldF.delete();
		    newF.renameTo(oldF);
		    
		} catch (IOException e) {
			System.err.println("Unable to replace bad ids!");
		}  
	}
	
	private void fillFirstNamesList() throws IOException {
		if (firstNames == null) {
			firstNames = new ArrayList<String>();
			BufferedReader reader = new BufferedReader(new FileReader(firstNamesFilePath));
			String current = null;
			while ((current = reader.readLine()) != null) {
				firstNames.add(current);
			}
		}
	}
	
	private void fillLastNamesList() throws IOException {
		if (lastNames == null) {
			lastNames = new ArrayList<String>();
			BufferedReader reader = new BufferedReader(new FileReader(lastNamesFilePath));
			String current = null;
			while ((current = reader.readLine()) != null) {
				lastNames.add(current);
			}
		}
	}
	
	public String getDatasetFP() {
		return datasetFP;
	}
	
	public int getNumberVertices() {
		return nVertices;
	}
}
