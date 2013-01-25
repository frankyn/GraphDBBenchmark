package com.silvertower.app.bench.datasetsgeneration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.silvertower.app.bench.main.BenchmarkProperties;

public class SocialNetworkDatasetsGenerator implements DatasetsGenerator {
	private int b;
	private int e;
	private float s;
	private File socialFolder;
	
	public SocialNetworkDatasetsGenerator(int beginning, int end, float step) {
		this.b = beginning;
		this.e = end;
		this.s = step;
		this.socialFolder = new File(BenchmarkProperties.datasetsDir + "social\\");
	}

	public void generate() {
		String pythonDir = BenchmarkProperties.pythonDir;
		String datasetsDir = BenchmarkProperties.datasetsDir;
		Runtime r = Runtime.getRuntime();
		for (int step = b; step <= e; step *= s) {
			try {
				String command = "python" + " " 
						+ pythonDir + "social-graph-creator.py" + " "
						+ step + " " 
						+ pythonDir + "first_names2011.txt" + " " 
						+ pythonDir + "last_names1990.txt" + " "
						+ datasetsDir + "social\\social" + step + ".graphml";
				Process p = r.exec(command);
	            p.waitFor();
	            replaceBadIds(datasetsDir + "social\\social" + step);
	        }
	        catch (Exception e) {
	        	System.err.println("Error while generating a social network dataset");
	        }
		}
	}
	
	private static void replaceBadIds(String fPath) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fPath + ".graphml"));
			PrintWriter writer = new PrintWriter(fPath + "new" + ".graphml");  
		    String candidate = null;  
		    String matchRegex = "n([0-9]+)";  
		    String replacement = "1$1";  
		    while ((candidate = reader.readLine()) != null){  
		      candidate = candidate.replaceAll(matchRegex, replacement);  
		      writer.println(candidate);  
		    }  
		    reader.close();  
		    writer.close();  
		    
		    File oldF = new File(fPath + ".graphml");
		    File newF = new File(fPath + "new" + ".graphml");
		    oldF.delete();
		    newF.renameTo(oldF);
		    
		} catch (IOException e) {
			System.err.println("Unable to replace bad ids!");
		}  
	}
	
	public File getLocation() {
		return socialFolder;
	}
}
