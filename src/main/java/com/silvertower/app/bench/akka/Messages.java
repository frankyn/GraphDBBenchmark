package com.silvertower.app.bench.akka;


import java.util.ArrayList;
import java.util.List;

import com.silvertower.app.bench.datasetsgeneration.Dataset;
import com.silvertower.app.bench.dbinitializers.DBInitializer;
import com.silvertower.app.bench.dbinitializers.GraphDescriptor;
import com.silvertower.app.bench.workload.Workload;

public class Messages {
	static class InitializeDB {
		private final DBInitializer i;
		public InitializeDB(DBInitializer i) {
			this.i = i;
		}
		public DBInitializer getInitializer() {
			return i;
		}
	}
	
	static class FillDB {
		private final Dataset d;
		public FillDB(Dataset d) {
			this.d = d;
		}
		public Dataset getDataset() {
			return d;
		}
	}
	
	static class FillDBBatch {
		private final Dataset d;
		public FillDBBatch(Dataset d) {
			this.d = d;
		}
		public Dataset getDataset() {
			return d;
		}
	}
	
	static class AggregateResult {
		private List<TimeResult> times;
		public AggregateResult() {
			this.times = new ArrayList<TimeResult>();
		}
		
		public void addTime (TimeResult t) {
			times.add(t);
		}
		
		public TimeResult getMean () {
			double cpuMeanTime = 0;
			double wallMeanTime = 0;
			for (TimeResult t: times) {
				cpuMeanTime += t.getCpuTime();
				wallMeanTime += t.getWallTime();
			}
			cpuMeanTime /= times.size();
			wallMeanTime /= times.size();
			return new TimeResult(cpuMeanTime, wallMeanTime);
		}
	}
	
	static class TimeResult {
		private double wallTime;
		private double cpuTime;
		private Workload w;
	    public TimeResult(double wallTime, double cpuTime) {
	    	this.wallTime = wallTime;
	        this.cpuTime = cpuTime;
	        this.w = w;
	    }
	    
		public double getWallTime() {
			return wallTime;
		}
		
		public double getCpuTime() {
			return cpuTime;
		}
		
		public Workload getWorkload() {
			return w;
		}
	}
	
	static class GDesc {
		private final GraphDescriptor gDesc;
		public GDesc(GraphDescriptor gDesc) {
			this.gDesc = gDesc;
		}
		
		public GraphDescriptor getGraphDesc() {
			return gDesc;
		}
	}
	
	static class Work {
		private final Workload w;
		private final int howManyOp;
		private final int howManyClients;
		public Work(Workload w, int howManyOp, int howManyClients) {
			this.w = w;
			this.howManyOp = howManyOp;
			this.howManyClients = howManyClients;
		}
		
		public Workload getWork() {
			return w;
		}
		
		public int getHowManyOp() {
			return howManyOp;
		}

		public int getHowManyClients() {
			return howManyClients;
		}
	}
	
	static class NumberOfClients {
		private final int howManyClients;
		public NumberOfClients(int n) {
			this.howManyClients = n;
		}
		public int getHowManyClients() {
			return howManyClients;
		}
	}
	
	static class GetResult {
	}
	
	static class VanishDB {
	}
	
	static class GetNbCores {
	}
	
	static class StartWork {
	}
	
	static class Error {
		private final String s;
		public Error(String s) {
			this.s = s;
		}
		public String getMessage() {
			return s;
		}
	}
}
